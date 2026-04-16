from __future__ import annotations

from dataclasses import MISSING, dataclass, fields
from pathlib import Path
from typing import Dict, List, Optional, Set, Tuple

from ..core.common import CutToFatjarRow, ensure_dir, load_cut_to_fatjar_map, read_csv_rows
from .sanitize import sanitize_compare_summary_csv, sanitize_summary_csv
from .helpers import _load_dotenv_if_present


def includes_filter_active(includes: str) -> bool:
    normalized = (includes or "").strip()
    return bool(normalized) and normalized != "*"


def includes_csv_path(path: Path, includes: str) -> Path:
    if not includes_filter_active(includes) or path.suffix.lower() != ".csv":
        return path
    return path.with_name(f"{path.stem}.includes{path.suffix}")


def is_auto_generated_variant(variant: str) -> bool:
    return variant in {"auto", "auto-original"}


def covfilter_variant_root(covfilter_out_root: Path, adopted_covfilter_out_root: Path, variant: str) -> Path:
    base_root = covfilter_out_root if is_auto_generated_variant(variant) else adopted_covfilter_out_root
    return base_root / variant


def covfilter_variant_out_dir(
    covfilter_out_root: Path, adopted_covfilter_out_root: Path, variant: str, target_id: str
) -> Path:
    return covfilter_variant_root(covfilter_out_root, adopted_covfilter_out_root, variant) / target_id


def covfilter_variant_summary_csv(
    covfilter_out_root: Path,
    adopted_covfilter_out_root: Path,
    variant: str,
    includes: str = "*",
) -> Path:
    return includes_csv_path(
        covfilter_variant_root(covfilter_out_root, adopted_covfilter_out_root, variant) / "covfilter_summary.csv",
        includes,
    )


def reduce_variant_summary_csv(reduced_out_root: Path, variant: str, includes: str = "*") -> Path:
    return includes_csv_path(reduced_out_root / variant / "reduce_summary.csv", includes)


def covfilter_candidate_out_dirs(
    covfilter_out_root: Path, adopted_covfilter_out_root: Path, variant: str, target_id: str
) -> List[Path]:
    candidates: List[Path] = [
        covfilter_variant_out_dir(covfilter_out_root, adopted_covfilter_out_root, variant, target_id)
    ]
    if variant == "auto":
        candidates.append(covfilter_out_root / target_id)
    else:
        if variant == "adopted":
            candidates.append(adopted_covfilter_out_root / target_id)
        if adopted_covfilter_out_root == covfilter_out_root:
            legacy_adopted_root = Path("results/covfilter-adopted")
            candidates.append(legacy_adopted_root / variant / target_id)
            if variant == "adopted":
                candidates.append(legacy_adopted_root / target_id)

    deduped: List[Path] = []
    seen = set()
    for candidate in candidates:
        if candidate in seen:
            continue
        seen.add(candidate)
        deduped.append(candidate)
    return deduped


@dataclass(frozen=True)
class PipelineArgs:
    tests_inventory_csv: str = "../collected-tests/_logs/tests_inventory.csv"
    cut_to_fatjar_map_csv: str = "../out/cut_to_fatjar_map.csv"
    generate_auto_script: str = "../run-agt/run-agt.sh"
    generate_auto_output_dir: str = "../run-agt/result"
    collect_tests_script: str = "../collect_tests.sh"
    generate_auto_skip_existing: bool = True
    generate_auto_java_home: str = ""
    sync_variants: str = "auto"
    sync_force: bool = False
    generated_dir: str = "../collected-tests/generated"
    manual_dir: str = "../collected-tests/manual"
    repos_dir: str = "../repos"
    libs_cp: str = "libs/*"
    jacoco_agent: str = "jacoco-deps/org.jacoco.agent-run-0.8.14.jar"
    out_dir: str = "tmp"
    build_dir: str = "build/agt"
    includes: str = "*"
    dep_rounds: int = 20
    tool_jar: str = "coverage-filter-1.0-SNAPSHOT.jar"
    step: str = "all"
    timeout_ms: int = 240_000
    filter_only_agt_covered: bool = False
    coverage_summary: str = ""
    reduced_out: str = "results/reduced"
    reduce_max_tests: int = 100
    send_script: str = ""
    send_api_url: str = "http://localhost:8001/graphql"
    send_sleep_seconds: int = 30
    adopted_dir: str = "results/llm-out"
    skip_exists: bool = False
    skip_passed: bool = False
    skip_passed_by_status: bool = False
    skip_empty_tests: bool = True
    agent_model: str = ""
    agent_max_context_files: int = 12
    agent_max_context_chars: int = 40_000
    agent_max_prompt_chars: int = 60_000
    compare_root: str = "src/metrics"
    compare_out: str = "results/compare"
    compare_min_tokens: int = 50
    coverage_compare_top_n: int = 5
    auto_variant: str = "auto"
    do_covfilter: bool = False
    covfilter_jar: str = "coverage-filter-1.0-SNAPSHOT.jar"
    covfilter_out: str = "results/covfilter"
    sanitized_es_dir: str = "results/sanitized-es"
    sanitize_compare: bool = False
    sanitize_compare_out: str = "results/covfilter-compare"
    sut_classes_dir: str = ""
    adopted_covfilter_out: str = "results/covfilter"
    adopted_reduced_out: str = "results/reduced"
    adopted_reduce_max_tests: int = 5
    annotation_variants: str = "auto,adopted,agentic"
    annotation_out: str = "results/annotation"


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
    compile_summary_csv: Path
    summary_csv: Path
    adopted_summary_csv: Path
    coverage_errors_csv: Path
    coverage_zero_hit_csv: Path
    coverage_report_issues_csv: Path
    coverage_compare_csv: Path
    coverage_compare_reduced_csv: Path
    covfilter_summary_csv: Path
    adopted_covfilter_summary_csv: Path
    reduce_summary_csv: Path
    adopted_reduce_summary_csv: Path
    agentic_reduce_summary_csv: Path
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


def _int_field_gt_zero(row: Dict[str, str], key: str) -> bool:
    raw = (row.get(key, "") or "").strip()
    try:
        return int(raw) > 0
    except ValueError:
        return False


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
    if inventory_csv.exists():
        inv_rows = [
            row
            for row in read_csv_rows(inventory_csv)
            if _int_field_gt_zero(row, "generated_count") and _int_field_gt_zero(row, "manual_count")
        ]
    elif args.step in ("generate-auto", "sync"):
        inv_rows = []
    else:
        inv_rows = []

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
    reduced_out_root = Path(args.reduced_out)

    compile_summary_csv = includes_csv_path(Path("results/compile/compile_summary.csv"), args.includes)
    compile_header = (
        "repo,fqcn,variant,status,selected,"
        "requested_test_count,requested_test_cases,"
        "final_source_count,resolved_support_source_count,"
        "class_origin,class_origin_detail,"
        "problem_category,problem_detail,log_file\n"
    )
    _ensure_csv_header(compile_summary_csv, compile_header, reset=args.step in ("compile", "all") and not args.skip_passed)

    summary_csv = includes_csv_path(Path("results/coverage/coverage_summary.csv"), args.includes)
    header = (
        "repo,fqcn,variant,"
        "line_percentage_coverage,line_covered,line_total,"
        "branch_percentage_coverage,branch_covered,branch_total,"
        "failed,timeout,skipped\n"
    )
    _ensure_csv_header(summary_csv, header, reset=args.step in ("run", "all"))

    adopted_summary_csv = includes_csv_path(Path("results/coverage/adopted_coverage_summary.csv"), args.includes)
    _ensure_csv_header(adopted_summary_csv, header, reset=args.step in ("adopted-run", "all"))

    coverage_errors_csv = includes_csv_path(Path("results/coverage/coverage_errors.csv"), args.includes)
    coverage_errors_header = (
        "repo,fqcn,variant,test_fqcn,status,"
        "error_category,error_detail,class_origin,class_origin_detail,log_file\n"
    )
    _ensure_csv_header(coverage_errors_csv, coverage_errors_header, reset=args.step in ("run", "all"))

    coverage_diag_header = (
        "repo,fqcn,variant,test_fqcn,status,"
        "coverage_category,coverage_detail,"
        "line_covered,line_total,branch_covered,branch_total,"
        "class_origin,class_origin_detail,log_file\n"
    )
    coverage_zero_hit_csv = includes_csv_path(Path("results/coverage/coverage_zero_hit.csv"), args.includes)
    _ensure_csv_header(coverage_zero_hit_csv, coverage_diag_header, reset=args.step in ("run", "all"))

    coverage_report_issues_csv = includes_csv_path(Path("results/coverage/coverage_report_issues.csv"), args.includes)
    _ensure_csv_header(coverage_report_issues_csv, coverage_diag_header, reset=args.step in ("run", "all"))

    coverage_compare_csv = includes_csv_path(Path("results/coverage/coverage_compare.csv"), args.includes)
    coverage_compare_reduced_csv = includes_csv_path(Path("results/coverage/coverage_compare_reduced.csv"), args.includes)
    _ensure_csv_header(coverage_compare_csv, header, reset=args.step in ("coverage-comparison", "all"))
    _ensure_csv_header(
        coverage_compare_reduced_csv, header, reset=args.step in ("coverage-comparison-reduced", "all")
    )
    covfilter_summary_header = (
        "repo,fqcn,variant,status,problem_category,problem_detail,"
        "manual_test_fqcn,generated_test_fqcn,"
        "test_deltas_all_count,test_deltas_kept_count,line_deltas_kept_count,"
        "out_dir,log_file\n"
    )
    reset_covfilter_summaries = args.step in ("filter", "adopted-filter", "all") and not args.skip_passed_by_status
    covfilter_summary_csv = covfilter_variant_summary_csv(
        covfilter_out_root, adopted_covfilter_out_root, "auto", args.includes
    )
    _ensure_csv_header(covfilter_summary_csv, covfilter_summary_header, reset=reset_covfilter_summaries and args.step in ("filter", "all"))
    _ensure_csv_header(
        covfilter_variant_summary_csv(covfilter_out_root, adopted_covfilter_out_root, "auto-original", args.includes),
        covfilter_summary_header,
        reset=reset_covfilter_summaries and args.step in ("filter", "all"),
    )

    adopted_covfilter_summary_csv = covfilter_variant_summary_csv(
        covfilter_out_root, adopted_covfilter_out_root, "adopted", args.includes
    )
    _ensure_csv_header(
        adopted_covfilter_summary_csv,
        covfilter_summary_header,
        reset=reset_covfilter_summaries and args.step in ("adopted-filter", "all"),
    )
    _ensure_csv_header(
        covfilter_variant_summary_csv(covfilter_out_root, adopted_covfilter_out_root, "agentic", args.includes),
        covfilter_summary_header,
        reset=reset_covfilter_summaries and args.step in ("adopted-filter", "all"),
    )
    sanitize_summary_header = (
        "repo,fqcn,status,problem_category,problem_detail,"
        "baseline_test_fqcn,sanitized_test_fqcn,"
        "baseline_test_source,sanitized_test_source,sanitized_scaffolding_source\n"
    )
    _ensure_csv_header(
        sanitize_summary_csv(Path(args.sanitized_es_dir), args.includes),
        sanitize_summary_header,
        reset=args.step in ("sanitize-es", "all"),
    )
    sanitize_compare_header = (
        "repo,fqcn,"
        "baseline_status,baseline_problem_category,baseline_problem_detail,baseline_generated_test_fqcn,"
        "baseline_test_deltas_all_count,baseline_test_deltas_kept_count,baseline_line_deltas_kept_count,"
        "baseline_out_dir,baseline_log_file,"
        "sanitized_status,sanitized_problem_category,sanitized_problem_detail,sanitized_generated_test_fqcn,"
        "sanitized_test_deltas_all_count,sanitized_test_deltas_kept_count,sanitized_line_deltas_kept_count,"
        "sanitized_out_dir,sanitized_log_file,"
        "selected_variant,selection_reason,selected_generated_test_fqcn,selected_test_source,selected_out_dir,selected_log_file\n"
    )
    _ensure_csv_header(
        sanitize_compare_summary_csv(Path(args.sanitize_compare_out), args.includes),
        sanitize_compare_header,
        reset=args.sanitize_compare and args.step in ("filter", "all") and not args.skip_passed_by_status,
    )
    reduce_summary_header = (
        "repo,fqcn,variant,status,problem_category,problem_detail,"
        "manual_test_fqcn,generated_test_fqcn,input_test_source,test_deltas_csv,"
        "test_deltas_count,selected_top_n,selected_test_count,reduced_test_path,out_dir,log_file\n"
    )
    reduce_summary_csv = reduce_variant_summary_csv(reduced_out_root, "auto", args.includes)
    _ensure_csv_header(reduce_summary_csv, reduce_summary_header, reset=args.step in ("reduce", "all"))
    _ensure_csv_header(
        reduce_variant_summary_csv(reduced_out_root, "auto-original", args.includes),
        reduce_summary_header,
        reset=args.step in ("reduce", "all"),
    )
    adopted_reduce_summary_csv = reduce_variant_summary_csv(adopted_reduced_out_root, "adopted", args.includes)
    _ensure_csv_header(
        adopted_reduce_summary_csv,
        reduce_summary_header,
        reset=args.step in ("adopted-reduce", "all"),
    )
    agentic_reduce_summary_csv = reduce_variant_summary_csv(adopted_reduced_out_root, "agentic", args.includes)
    _ensure_csv_header(
        agentic_reduce_summary_csv,
        reduce_summary_header,
        reset=args.step in ("adopted-reduce", "all"),
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
        "annotation",
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
                if repo_row and fqcn_row and variant == args.auto_variant and val > 0:
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
        compile_summary_csv=compile_summary_csv,
        summary_csv=summary_csv,
        adopted_summary_csv=adopted_summary_csv,
        coverage_errors_csv=coverage_errors_csv,
        coverage_zero_hit_csv=coverage_zero_hit_csv,
        coverage_report_issues_csv=coverage_report_issues_csv,
        coverage_compare_csv=coverage_compare_csv,
        coverage_compare_reduced_csv=coverage_compare_reduced_csv,
        covfilter_summary_csv=covfilter_summary_csv,
        adopted_covfilter_summary_csv=adopted_covfilter_summary_csv,
        reduce_summary_csv=reduce_summary_csv,
        adopted_reduce_summary_csv=adopted_reduce_summary_csv,
        agentic_reduce_summary_csv=agentic_reduce_summary_csv,
        covfilter_allow=covfilter_allow,
    )
