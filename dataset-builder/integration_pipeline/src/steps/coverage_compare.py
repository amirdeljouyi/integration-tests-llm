from __future__ import annotations

from pathlib import Path
from typing import Optional, Tuple, TYPE_CHECKING

from ..core.common import detect_junit_version, parse_package_and_class
from .compile import compile_test_set_smart
from ..core.coverage import write_coverage_row
from .run import run_one_test_with_jacoco, get_coverage_stats
from ..pipeline.helpers import (
    adopted_variants,
    first_test_source_for_fqcn,
    reduced_test_path,
    reduced_variant_test_path,
)
from .base import Step

if TYPE_CHECKING:
    from ..pipeline.pipeline import TargetContext


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


class CoverageComparisonStep(Step):
    step_names = ("coverage-comparison",)

    def run(self, ctx: "TargetContext") -> bool:
        if not self.should_run():
            return True
        if self.pipeline.covfilter_allow is not None and (ctx.repo, ctx.fqcn) not in self.pipeline.covfilter_allow:
            print(f'[agt] coverage-comparison: Skip (agt_line_covered=0): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
            return True

        manual_test_src = first_test_source_for_fqcn(ctx.manual_sources or ctx.final_sources, ctx.manual_test_fqcn)
        generated_test_src = first_test_source_for_fqcn(ctx.final_sources, ctx.generated_test_fqcn)
        variants = adopted_variants(self.pipeline.adopted_root, ctx.target_id)
        tool_jar = Path(self.pipeline.args.tool_jar)

        if manual_test_src and manual_test_src.exists():
            run_coverage_for_test(
                test_src=manual_test_src,
                variant="manual",
                repo=ctx.repo,
                fqcn=ctx.fqcn,
                build_dir=self.pipeline.build_dir / "coverage-compare" / "manual" / ctx.target_id,
                log_file=self.pipeline.logs_dir / f"{ctx.target_id}.coverage.manual.log",
                exec_file=self.pipeline.out_dir / f"{ctx.target_id}__manual.exec",
                libs_glob_cp=self.pipeline.args.libs_cp,
                sut_jar=ctx.sut_jar,
                jacoco_agent=self.pipeline.jacoco_agent,
                tool_jar=tool_jar,
                timeout_ms=self.pipeline.args.timeout_ms,
                repo_root_for_deps=ctx.repo_root_for_deps,
                summary_csv=self.pipeline.coverage_compare_csv,
            )

        if generated_test_src and generated_test_src.exists():
            run_coverage_for_test(
                test_src=generated_test_src,
                variant="auto",
                repo=ctx.repo,
                fqcn=ctx.fqcn,
                build_dir=self.pipeline.build_dir / "coverage-compare" / "auto" / ctx.target_id,
                log_file=self.pipeline.logs_dir / f"{ctx.target_id}.coverage.auto.log",
                exec_file=self.pipeline.out_dir / f"{ctx.target_id}__auto.exec",
                libs_glob_cp=self.pipeline.args.libs_cp,
                sut_jar=ctx.sut_jar,
                jacoco_agent=self.pipeline.jacoco_agent,
                tool_jar=tool_jar,
                timeout_ms=self.pipeline.args.timeout_ms,
                repo_root_for_deps=ctx.repo_root_for_deps,
                summary_csv=self.pipeline.coverage_compare_csv,
            )

        for variant, adopted_src in variants:
            if not adopted_src.exists():
                continue
            run_coverage_for_test(
                test_src=adopted_src,
                variant=variant,
                repo=ctx.repo,
                fqcn=ctx.fqcn,
                build_dir=self.pipeline.build_dir / "coverage-compare" / variant / ctx.target_id,
                log_file=self.pipeline.logs_dir / f"{ctx.target_id}.coverage.{variant}.log",
                exec_file=self.pipeline.out_dir / f"{ctx.target_id}__{variant}.exec",
                libs_glob_cp=self.pipeline.args.libs_cp,
                sut_jar=ctx.sut_jar,
                jacoco_agent=self.pipeline.jacoco_agent,
                tool_jar=tool_jar,
                timeout_ms=self.pipeline.args.timeout_ms,
                repo_root_for_deps=ctx.repo_root_for_deps,
                summary_csv=self.pipeline.coverage_compare_csv,
            )
        return True


class CoverageComparisonReducedStep(Step):
    step_names = ("coverage-comparison-reduced",)

    def run(self, ctx: "TargetContext") -> bool:
        if not self.should_run():
            return True
        if self.pipeline.covfilter_allow is not None and (ctx.repo, ctx.fqcn) not in self.pipeline.covfilter_allow:
            print(
                f'[agt] coverage-comparison-reduced: Skip (agt_line_covered=0): repo="{ctx.repo}" fqcn="{ctx.fqcn}"'
            )
            return True

        reduced_root = Path(self.pipeline.args.reduced_out)
        top_n = max(1, min(self.pipeline.args.coverage_compare_top_n, 100))
        generated_test_src = first_test_source_for_fqcn(ctx.final_sources, ctx.generated_test_fqcn)
        auto_reduced = reduced_test_path(reduced_root, ctx.target_id, generated_test_src, top_n) if generated_test_src else None
        tool_jar = Path(self.pipeline.args.tool_jar)

        if auto_reduced and auto_reduced.exists():
            run_coverage_for_test(
                test_src=auto_reduced,
                variant="auto-reduced",
                repo=ctx.repo,
                fqcn=ctx.fqcn,
                build_dir=self.pipeline.build_dir / "coverage-compare-reduced" / "auto" / ctx.target_id,
                log_file=self.pipeline.logs_dir / f"{ctx.target_id}.coverage.auto-reduced.log",
                exec_file=self.pipeline.out_dir / f"{ctx.target_id}__auto_reduced.exec",
                libs_glob_cp=self.pipeline.args.libs_cp,
                sut_jar=ctx.sut_jar,
                jacoco_agent=self.pipeline.jacoco_agent,
                tool_jar=tool_jar,
                timeout_ms=self.pipeline.args.timeout_ms,
                repo_root_for_deps=ctx.repo_root_for_deps,
                summary_csv=self.pipeline.coverage_compare_reduced_csv,
            )

        for variant in ("adopted", "agentic"):
            reduced_src = reduced_variant_test_path(self.pipeline.adopted_reduced_out_root, variant, ctx.target_id, top_n)
            if not reduced_src or not reduced_src.exists():
                continue
            run_coverage_for_test(
                test_src=reduced_src,
                variant=f"{variant}-reduced",
                repo=ctx.repo,
                fqcn=ctx.fqcn,
                build_dir=self.pipeline.build_dir / "coverage-compare-reduced" / variant / ctx.target_id,
                log_file=self.pipeline.logs_dir / f"{ctx.target_id}.coverage.{variant}-reduced.log",
                exec_file=self.pipeline.out_dir / f"{ctx.target_id}__{variant}_reduced.exec",
                libs_glob_cp=self.pipeline.args.libs_cp,
                sut_jar=ctx.sut_jar,
                jacoco_agent=self.pipeline.jacoco_agent,
                tool_jar=tool_jar,
                timeout_ms=self.pipeline.args.timeout_ms,
                repo_root_for_deps=ctx.repo_root_for_deps,
                summary_csv=self.pipeline.coverage_compare_reduced_csv,
            )
        return True
