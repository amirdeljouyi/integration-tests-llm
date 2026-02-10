from __future__ import annotations

from pathlib import Path
from typing import Optional, Tuple

from .common import detect_junit_version, parse_package_and_class
from .compile import compile_test_set_smart
from .coverage import write_coverage_row
from .run import run_one_test_with_jacoco, get_coverage_stats


def run_coverage_for_test(
    *,
    test_src: Path,
    variant: str,
    repo: str,
    fqcn: str,
    build_dir: Path,
    log_file: Path,
    exec_file: Path,
    libs_glob_cp: str,
    sut_jar: Path,
    jacoco_agent: Path,
    tool_jar: Path,
    timeout_ms: int,
    repo_root_for_deps: Optional[Path],
    summary_csv: Path,
    max_rounds: int = 3,
) -> None:
    ok_compile, _tail, _final_sources = compile_test_set_smart(
        java_files=[test_src],
        build_dir=build_dir,
        libs_glob_cp=libs_glob_cp,
        sut_jar=sut_jar,
        log_file=log_file,
        repo_root_for_deps=repo_root_for_deps,
        max_rounds=max_rounds,
    )
    if not ok_compile:
        write_coverage_row(
            csv_path=summary_csv,
            repo=repo,
            fqcn=fqcn,
            variant=variant,
            line_covered=0,
            line_total=0,
            branch_covered=0,
            branch_total=0,
            status="failed",
        )
        return

    pkg, cls = parse_package_and_class(test_src)
    if not cls:
        write_coverage_row(
            csv_path=summary_csv,
            repo=repo,
            fqcn=fqcn,
            variant=variant,
            line_covered=0,
            line_total=0,
            branch_covered=0,
            branch_total=0,
            status="skipped",
        )
        return

    test_fqcn = f"{pkg}.{cls}" if pkg else cls
    junit_ver = detect_junit_version(test_src)
    status = run_one_test_with_jacoco(
        junit_version=junit_ver,
        test_fqcn=test_fqcn,
        sut_jar=sut_jar,
        libs_glob_cp=libs_glob_cp,
        compiled_tests_dir=build_dir,
        jacoco_agent_jar=jacoco_agent,
        jacoco_exec_file=exec_file,
        log_file=log_file,
        tool_jar=tool_jar,
        timeout_ms=timeout_ms,
    )

    stats: Tuple[int, int, int, int] = (0, 0, 0, 0)
    jacoco_cli = jacoco_agent.parent / "org.jacoco.cli-run-0.8.14.jar"
    if status == "passed" and jacoco_cli.exists():
        stats = get_coverage_stats(jacoco_cli, exec_file, sut_jar, fqcn, build_dir)

    write_coverage_row(
        csv_path=summary_csv,
        repo=repo,
        fqcn=fqcn,
        variant=variant,
        line_covered=stats[0],
        line_total=stats[1],
        branch_covered=stats[2],
        branch_total=stats[3],
        status=status,
    )
