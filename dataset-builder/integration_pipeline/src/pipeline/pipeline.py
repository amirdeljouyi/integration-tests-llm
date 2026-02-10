from __future__ import annotations

import fnmatch
from dataclasses import dataclass, fields
from pathlib import Path
from typing import Dict, List, Optional, Set, Tuple


from ..core.common import looks_like_scaffolding, repo_to_dir, split_list_field
from .config import PipelineArgs, PipelineConfig, build_pipeline_config
from .helpers import expand_manual_sources, find_tests_in_bucket, first_test_fqcn_from_sources
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
        self.config: Optional[PipelineConfig] = None
        self.ran = 0
        self.skipped = 0
        self.covfilter_allow: Optional[Set[Tuple[str, str]]] = None
        self.steps: List[Step] = []

    def run_configuration(self) -> None:
        self.config = build_pipeline_config(self.args)
        for f in fields(PipelineConfig):
            setattr(self, f.name, getattr(self.config, f.name))

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
