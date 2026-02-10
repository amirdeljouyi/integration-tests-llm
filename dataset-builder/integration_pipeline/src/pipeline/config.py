from __future__ import annotations

from dataclasses import MISSING, dataclass, fields
from pathlib import Path
from typing import Dict, List, Optional, Set, Tuple

from ..core.common import CutToFatjarRow, ensure_dir, load_cut_to_fatjar_map, read_csv_rows
from .helpers import _load_dotenv_if_present


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
    covfilter_jar: str = "coverage-filter-1.0-SNAPSHOT.jar"
    covfilter_out: str = "results/covfilter"
    sut_classes_dir: str = ""
    adopted_covfilter_out: str = "results/covfilter-adopted"
    adopted_reduced_out: str = "results/reduced-adopted"
    adopted_reduce_max_tests: int = 5


@dataclass(frozen=True)
class PipelineConfig:
    inventory_csv: Path
    map_csv: Path
    generated_dir: Path
    manual_dir: Path
    repos_dir: Path
    out_dir: Path
    build_dir: Path
    logs_dir: Path
    jacoco_agent: Path
    mapping: Dict[Tuple[str, str], CutToFatjarRow]
    inv_rows: List[Dict[str, str]]
    do_covfilter: bool
    covfilter_jar: Optional[Path]
    covfilter_out_root: Path
    sut_classes_dir: Optional[Path]
    adopted_covfilter_out_root: Path
    adopted_reduced_out_root: Path
    adopted_root: Path
    adopted_fix_script: Path
    pr_template: Path
    pr_out_root: Path
    summary_csv: Path
    adopted_summary_csv: Path
    coverage_compare_csv: Path
    coverage_compare_reduced_csv: Path
    covfilter_allow: Optional[Set[Tuple[str, str]]]


def pipeline_defaults() -> Dict[str, object]:
    return {f.name: f.default for f in fields(PipelineArgs) if f.default is not MISSING}


def build_pipeline_args(**overrides: object) -> PipelineArgs:
    defaults = pipeline_defaults()
    merged = {**defaults, **overrides}
    return PipelineArgs(**merged)


def _ensure_csv_header(path: Path, header: str, *, reset: bool) -> None:
    if reset:
        path.parent.mkdir(parents=True, exist_ok=True)
        path.write_text(header, encoding="utf-8")
        return
    if not path.exists():
        path.parent.mkdir(parents=True, exist_ok=True)
        path.write_text(header, encoding="utf-8")


def build_pipeline_config(args: PipelineArgs) -> PipelineConfig:
    _load_dotenv_if_present()

    inventory_csv = Path(args.tests_inventory_csv)
    map_csv = Path(args.cut_to_fatjar_map_csv)

    generated_dir = Path(args.generated_dir)
    manual_dir = Path(args.manual_dir)
    repos_dir = Path(args.repos_dir)

    out_dir = Path(args.out_dir)
    build_dir = Path(args.build_dir)
    logs_dir = out_dir / "logs"

    ensure_dir(out_dir)
    ensure_dir(logs_dir)
    ensure_dir(build_dir)

    jacoco_agent = Path(args.jacoco_agent)

    mapping = load_cut_to_fatjar_map(map_csv)
    inv_rows = read_csv_rows(inventory_csv)

    do_covfilter = bool(args.do_covfilter)
    covfilter_jar = Path(args.covfilter_jar) if args.covfilter_jar else None
    covfilter_out_root = Path(args.covfilter_out)
    sut_classes_dir = Path(args.sut_classes_dir) if args.sut_classes_dir else None
    adopted_covfilter_out_root = Path(args.adopted_covfilter_out)
    adopted_reduced_out_root = Path(args.adopted_reduced_out)
    adopted_root = Path(args.adopted_dir)
    adopted_fix_script = Path("src/steps/fix.py")
    pr_template = Path("docs/PR_TEMPLATE.md")
    pr_out_root = Path("results/pr")

    summary_csv = Path("results/coverage/coverage_summary.csv")
    header = (
        "repo,fqcn,variant,"
        "line_percentage_coverage,line_covered,line_total,"
        "branch_percentage_coverage,branch_covered,branch_total,"
        "failed,timeout,skipped\n"
    )
    _ensure_csv_header(summary_csv, header, reset=args.step in ("run", "all"))

    adopted_summary_csv = Path("results/coverage/adopted_coverage_summary.csv")
    _ensure_csv_header(adopted_summary_csv, header, reset=args.step in ("adopted-run", "all"))

    coverage_compare_csv = Path("results/coverage/coverage_compare.csv")
    coverage_compare_reduced_csv = Path("results/coverage/coverage_compare_reduced.csv")
    _ensure_csv_header(coverage_compare_csv, header, reset=args.step in ("coverage-comparison", "all"))
    _ensure_csv_header(
        coverage_compare_reduced_csv, header, reset=args.step in ("coverage-comparison-reduced", "all")
    )

    covfilter_allow: Optional[Set[Tuple[str, str]]] = None
    if args.filter_only_agt_covered and args.step in (
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
        summary_path = Path(args.coverage_summary) if args.coverage_summary else summary_csv
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
            covfilter_allow = allow

    return PipelineConfig(
        inventory_csv=inventory_csv,
        map_csv=map_csv,
        generated_dir=generated_dir,
        manual_dir=manual_dir,
        repos_dir=repos_dir,
        out_dir=out_dir,
        build_dir=build_dir,
        logs_dir=logs_dir,
        jacoco_agent=jacoco_agent,
        mapping=mapping,
        inv_rows=inv_rows,
        do_covfilter=do_covfilter,
        covfilter_jar=covfilter_jar,
        covfilter_out_root=covfilter_out_root,
        sut_classes_dir=sut_classes_dir,
        adopted_covfilter_out_root=adopted_covfilter_out_root,
        adopted_reduced_out_root=adopted_reduced_out_root,
        adopted_root=adopted_root,
        adopted_fix_script=adopted_fix_script,
        pr_template=pr_template,
        pr_out_root=pr_out_root,
        summary_csv=summary_csv,
        adopted_summary_csv=adopted_summary_csv,
        coverage_compare_csv=coverage_compare_csv,
        coverage_compare_reduced_csv=coverage_compare_reduced_csv,
        covfilter_allow=covfilter_allow,
    )
