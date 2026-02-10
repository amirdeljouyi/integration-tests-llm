from __future__ import annotations

import fnmatch
from dataclasses import dataclass
from pathlib import Path
from typing import Dict, List, Optional, Set, Tuple


from ..core.common import (
    ensure_dir,
    load_cut_to_fatjar_map,
    looks_like_scaffolding,
    read_csv_rows,
    repo_to_dir,
    split_list_field,
)
from .helpers import (
    _load_dotenv_if_present,
    expand_manual_sources,
    find_tests_in_bucket,
    first_test_fqcn_from_sources,
)
from ..steps import (
    AdoptedCommentStep,
    AdoptedFilterStep,
    AdoptedFixStep,
    AdoptedReduceStep,
    AdoptedRunStep,
    AgentStep,
    CompareStep,
    CompileStep,
    CoverageComparisonReducedStep,
    CoverageComparisonStep,
    CovfilterStep,
    PullRequestMakerStep,
    ReduceStep,
    RunStep,
    SendStep,
    Step,
)

STEP_CHOICES = [
    "compile",
    "filter",
    "reduce",
    "send",
    "llm-all",
    "llm-agt-improvement",
    "llm-integration",
    "llm-integration-step-by-step",
    "agent",
    "compare",
    "run",
    "adopted-fix",
    "adopted-comment",
    "adopted-filter",
    "adopted-reduce",
    "adopted-run",
    "pull-request-maker",
    "coverage-comparison",
    "coverage-comparison-reduced",
    "all",
]


@dataclass(frozen=True)
class PipelineArgs:
    tests_inventory_csv: str
    cut_to_fatjar_map_csv: str
    generated_dir: str = "../generated"
    manual_dir: str = "../manual"
    repos_dir: str = "../repos"
    libs_cp: str = "libs/*"
    jacoco_agent: str = "jacoco-deps/org.jacoco.agent-run-0.8.14.jar"
    out_dir: str = "tmp"
    build_dir: str = "build/agt"
    mode: str = "both"
    includes: str = "*"
    dep_rounds: int = 3
    tool_jar: str = "coverage-filter-1.0-SNAPSHOT.jar"
    step: str = "all"
    timeout_ms: int = 240_000
    filter_only_agt_covered: bool = False
    coverage_summary: str = ""
    reduced_out: str = "results/reduced-agt"
    reduce_max_tests: int = 100
    send_script: str = ""
    send_api_url: str = "http://localhost:8001/graphql"
    send_sleep_seconds: int = 30
    adopted_dir: str = "results/llm-out"
    agent_model: str = ""
    agent_max_context_files: int = 12
    agent_max_context_chars: int = 40_000
    agent_max_prompt_chars: int = 60_000
    compare_root: str = "src/metrics"
    compare_out: str = "results/compare"
    compare_min_tokens: int = 50
    coverage_compare_top_n: int = 5
    do_covfilter: bool = False
    covfilter_jar: str = ""
    covfilter_out: str = "results/covfilter"
    sut_classes_dir: str = ""
    adopted_covfilter_out: str = "results/covfilter-adopted"
    adopted_reduced_out: str = "results/reduced-adopted"
    adopted_reduce_max_tests: int = 5


@dataclass
class TargetContext:
    repo: str
    fqcn: str
    target_id: str
    sut_jar: Path
    target_build: Path
    sources: List[Path]
    manual_sources: List[Path]
    final_sources: List[Path]
    repo_root_for_deps: Path
    manual_test_fqcn: Optional[str] = None
    generated_test_fqcn: Optional[str] = None


class Pipeline:
    def __init__(self, args: PipelineArgs) -> None:
        self.args = args
        self.ran = 0
        self.skipped = 0
        self.covfilter_allow: Optional[Set[Tuple[str, str]]] = None
        self.steps: List[Step] = []

    def run_configuration(self) -> None:
        _load_dotenv_if_present()

        self.inventory_csv = Path(self.args.tests_inventory_csv)
        self.map_csv = Path(self.args.cut_to_fatjar_map_csv)

        self.generated_dir = Path(self.args.generated_dir)
        self.manual_dir = Path(self.args.manual_dir)
        self.repos_dir = Path(self.args.repos_dir)

        self.out_dir = Path(self.args.out_dir)
        self.build_dir = Path(self.args.build_dir)
        self.logs_dir = self.out_dir / "logs"

        ensure_dir(self.out_dir)
        ensure_dir(self.logs_dir)
        ensure_dir(self.build_dir)

        self.jacoco_agent = Path(self.args.jacoco_agent)

        self.mapping = load_cut_to_fatjar_map(self.map_csv)
        self.inv_rows = read_csv_rows(self.inventory_csv)

        # covfilter config
        self.do_covfilter = bool(self.args.do_covfilter)
        self.covfilter_jar = Path(self.args.covfilter_jar) if self.args.covfilter_jar else None
        self.covfilter_out_root = Path(self.args.covfilter_out)
        self.sut_classes_dir = Path(self.args.sut_classes_dir) if self.args.sut_classes_dir else None
        self.adopted_covfilter_out_root = Path(self.args.adopted_covfilter_out)
        self.adopted_reduced_out_root = Path(self.args.adopted_reduced_out)
        self.adopted_root = Path(self.args.adopted_dir)
        self.adopted_fix_script = Path("src/steps/fix.py")
        self.pr_template = Path("docs/PR_TEMPLATE.md")
        self.pr_out_root = Path("results/pr")

        # Global coverage report CSV
        self.summary_csv = Path("results/coverage/coverage_summary.csv")
        header = (
            "repo,fqcn,variant,"
            "line_percentage_coverage,line_covered,line_total,"
            "branch_percentage_coverage,branch_covered,branch_total,"
            "failed,timeout,skipped\n"
        )
        if self.args.step in ("run", "all"):
            # Clear it if it already exists to avoid appending to old results in the same run
            self.summary_csv.parent.mkdir(parents=True, exist_ok=True)
            self.summary_csv.write_text(header, encoding="utf-8")
        elif not self.summary_csv.exists():
            self.summary_csv.parent.mkdir(parents=True, exist_ok=True)
            self.summary_csv.write_text(header, encoding="utf-8")

        self.adopted_summary_csv = Path("results/coverage/adopted_coverage_summary.csv")
        adopted_header = (
            "repo,fqcn,variant,"
            "line_percentage_coverage,line_covered,line_total,"
            "branch_percentage_coverage,branch_covered,branch_total,"
            "failed,timeout,skipped\n"
        )
        if self.args.step in ("adopted-run", "all"):
            self.adopted_summary_csv.parent.mkdir(parents=True, exist_ok=True)
            self.adopted_summary_csv.write_text(adopted_header, encoding="utf-8")
        elif not self.adopted_summary_csv.exists():
            self.adopted_summary_csv.parent.mkdir(parents=True, exist_ok=True)
            self.adopted_summary_csv.write_text(adopted_header, encoding="utf-8")

        self.coverage_compare_csv = Path("results/coverage/coverage_compare.csv")
        self.coverage_compare_reduced_csv = Path("results/coverage/coverage_compare_reduced.csv")
        coverage_compare_header = adopted_header
        if self.args.step in ("coverage-comparison", "all"):
            self.coverage_compare_csv.parent.mkdir(parents=True, exist_ok=True)
            self.coverage_compare_csv.write_text(coverage_compare_header, encoding="utf-8")
        elif not self.coverage_compare_csv.exists():
            self.coverage_compare_csv.parent.mkdir(parents=True, exist_ok=True)
            self.coverage_compare_csv.write_text(coverage_compare_header, encoding="utf-8")

        if self.args.step in ("coverage-comparison-reduced", "all"):
            self.coverage_compare_reduced_csv.parent.mkdir(parents=True, exist_ok=True)
            self.coverage_compare_reduced_csv.write_text(coverage_compare_header, encoding="utf-8")
        elif not self.coverage_compare_reduced_csv.exists():
            self.coverage_compare_reduced_csv.parent.mkdir(parents=True, exist_ok=True)
            self.coverage_compare_reduced_csv.write_text(coverage_compare_header, encoding="utf-8")

        self.covfilter_allow = None
        if self.args.filter_only_agt_covered and self.args.step in (
            "filter",
            "all",
            "reduce",
            "send",
            "llm-all",
            "llm-agt-improvement",
            "llm-integration",
            "llm-integration-step-by-step",
            "agent",
            "compare",
            "adopted-filter",
            "adopted-reduce",
            "adopted-run",
            "adopted-comment",
            "adopted-fix",
            "pull-request-maker",
            "coverage-comparison",
            "coverage-comparison-reduced",
        ):
            summary_path = Path(self.args.coverage_summary) if self.args.coverage_summary else self.summary_csv
            if summary_path.exists():
                cov_rows = read_csv_rows(summary_path)
                allow: Set[Tuple[str, str]] = set()
                for row in cov_rows:
                    repo_row = (row.get("repo", "") or "").strip()
                    fqcn_row = (row.get("fqcn", "") or "").strip()
                    variant = (row.get("variant", "") or "").strip()
                    raw = (row.get("line_covered", "") or "").strip()
                    try:
                        val = int(raw)
                    except ValueError:
                        val = 0
                    if repo_row and fqcn_row and variant == "auto" and val > 0:
                        allow.add((repo_row, fqcn_row))
                self.covfilter_allow = allow

    def run(self) -> int:
        self.run_configuration()
        self.steps = self.build_steps()
        for r in self.inv_rows:
            ctx = self.build_target_context(r)
            if not ctx:
                continue
            ok = self.process_target(ctx)
            if not ok:
                continue

        print("[agt] Done.")
        print(f"[agt] Ran:     {self.ran}")
        print(f"[agt] Skipped: {self.skipped}")
        print(f"[agt] Exec files: {self.out_dir}")
        print(f"[agt] Logs:      {self.logs_dir}")
        return 0

    def build_target_context(self, r: Dict[str, str]) -> Optional[TargetContext]:
        repo = (r.get("repo", "") or "").strip().strip('"')
        fqcn = (r.get("fqcn", "") or "").strip().strip('"')
        if not repo or not fqcn:
            return None

        # Optional wildcard filtering
        if self.args.includes and self.args.includes != "*":
            if not (fnmatch.fnmatch(repo, self.args.includes) or fnmatch.fnmatch(fqcn, self.args.includes)):
                return None

        gen_files = split_list_field(r.get("generated_files", ""))
        man_files = split_list_field(r.get("manual_files", ""))

        if self.args.mode == "generated" and not gen_files:
            self.skipped += 1
            return None
        if self.args.mode == "manual" and not man_files:
            self.skipped += 1
            return None
        if self.args.mode == "both" and (not gen_files and not man_files):
            self.skipped += 1
            return None

        m = self.mapping.get((repo, fqcn))
        if not m:
            print(f'[agt] Skip (no fatjar mapping): repo="{repo}" fqcn="{fqcn}"')
            self.skipped += 1
            return None

        fatjar_path = (m.fatjar_path or "").strip()
        if not fatjar_path or fatjar_path in ("FAIL", "SKIP-REPO"):
            print(f'[agt] Skip (fatjar missing): repo="{repo}" fqcn="{fqcn}" fatjar="{fatjar_path or "EMPTY"}"')
            self.skipped += 1
            return None

        sut_jar = Path(fatjar_path).resolve(strict=True)
        if not sut_jar.exists():
            print(f'[agt] Skip (fatjar path not found): repo="{repo}" fqcn="{fqcn}" fatjar="{sut_jar}"')
            self.skipped += 1
            return None

        target_id = f"{repo_to_dir(repo)}_{fqcn.replace('.', '_')}"
        target_build = self.build_dir / "test-classes" / target_id
        ensure_dir(target_build)

        sources: List[Path] = []
        manual_sources: List[Path] = []
        repo_bucket_gen = self.generated_dir / repo_to_dir(repo)
        repo_bucket_man = self.manual_dir / repo_to_dir(repo)

        # ---------- GENERATED ----------
        if self.args.mode in ("generated", "both") and gen_files:
            expanded: List[str] = []
            seen_names: Set[str] = set()

            for f in gen_files:
                if not f or f.lower() == "null":
                    continue
                if f not in seen_names:
                    seen_names.add(f)
                    expanded.append(f)

                if f.endswith("_ESTest.java"):
                    scaf = f.replace("_ESTest.java", "_ESTest_scaffolding.java")
                    if scaf not in seen_names:
                        seen_names.add(scaf)
                        expanded.append(scaf)

            gen_scaf = [f for f in expanded if looks_like_scaffolding(f)]
            gen_non_scaf = [f for f in expanded if not looks_like_scaffolding(f)]
            sources.extend(find_tests_in_bucket(repo_bucket_gen, gen_scaf))
            sources.extend(find_tests_in_bucket(repo_bucket_gen, gen_non_scaf))

        # ---------- MANUAL ----------
        if self.args.mode in ("manual", "both") and man_files:
            manual_primary = find_tests_in_bucket(repo_bucket_man, man_files)
            manual_all = expand_manual_sources(manual_primary)
            manual_sources = list(manual_all)
            sources.extend(manual_all)

        # de-dupe
        uniq: List[Path] = []
        seen = set()
        for s in sources:
            if s not in seen:
                seen.add(s)
                uniq.append(s)
        sources = uniq

        if not sources:
            print(f'[agt] Skip (no existing .java files): repo="{repo}" fqcn="{fqcn}"')
            self.skipped += 1
            return None

        # repo root for dependency search (ONLY used when javac asks)
        repo_root_for_deps = self.repos_dir / repo_to_dir(repo)

        return TargetContext(
            repo=repo,
            fqcn=fqcn,
            target_id=target_id,
            sut_jar=sut_jar,
            target_build=target_build,
            sources=sources,
            manual_sources=manual_sources,
            final_sources=list(sources),
            repo_root_for_deps=repo_root_for_deps,
        )

    def process_target(self, ctx: TargetContext) -> bool:
        for step in self.steps:
            if isinstance(step, CompileStep):
                if not step.run(ctx):
                    self.skipped += 1
                    return False
                ctx.manual_test_fqcn = first_test_fqcn_from_sources(
                    ctx.manual_sources or ctx.final_sources,
                    prefer_estest=False,
                )
                ctx.generated_test_fqcn = first_test_fqcn_from_sources(
                    ctx.final_sources,
                    prefer_estest=True,
                )
                continue

            if not step.run(ctx):
                self.skipped += 1
                return False
        return True

    def build_steps(self) -> List[Step]:
        return [
            CompileStep(self),
            AdoptedFixStep(self),
            AdoptedCommentStep(self),
            CovfilterStep(self),
            AdoptedFilterStep(self),
            ReduceStep(self),
            AdoptedReduceStep(self),
            SendStep(self),
            AgentStep(self),
            CompareStep(self),
            PullRequestMakerStep(self),
            CoverageComparisonStep(self),
            CoverageComparisonReducedStep(self),
            RunStep(self),
            AdoptedRunStep(self),
        ]


def run_pipeline(args: PipelineArgs) -> int:
    pipeline = Pipeline(args)
    return pipeline.run()
