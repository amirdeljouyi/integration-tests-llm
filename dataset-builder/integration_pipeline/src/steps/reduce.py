from __future__ import annotations

import csv
from pathlib import Path
from typing import List, Optional, Tuple, TYPE_CHECKING

from ..pipeline.config import covfilter_candidate_out_dirs, reduce_variant_summary_csv
from ..pipeline.helpers import (
    adopted_variants,
    find_scaffolding_source,
    fix_reduced_scaffolding_import,
    first_test_source_for_fqcn,
    reduced_test_path,
    reduced_variant_test_path,
)
from .base import Step
from .covfilter import CovfilterRunner

if TYPE_CHECKING:
    from ..pipeline.pipeline import TargetContext


def run_generate_reduced_app(
    *,
    coverage_filter_jar: Path,
    libs_glob_cp: str,
    original_test_java: Path,
    test_deltas_csv: Path,
    top_n: int,
    out_dir: Path,
    log_file: Path,
    extra_java_opts: Optional[List[str]] = None,
) -> Tuple[bool, str]:
    return CovfilterRunner(java_opts=extra_java_opts).run_generate_reduced(
        coverage_filter_jar=coverage_filter_jar,
        libs_glob_cp=libs_glob_cp,
        original_test_java=original_test_java,
        test_deltas_csv=test_deltas_csv,
        top_n=top_n,
        out_dir=out_dir,
        log_file=log_file,
    )


def _find_existing_delta_csv(base_dir: Path, names: List[str]) -> Optional[Path]:
    for name in names:
        cand = base_dir / name
        if cand.exists():
            return cand
    return None


def _count_csv_rows(csv_path: Optional[Path]) -> int:
    if not csv_path or not csv_path.exists():
        return 0
    try:
        with csv_path.open("r", encoding="utf-8", newline="") as handle:
            reader = csv.reader(handle)
            next(reader, None)
            return sum(1 for _ in reader)
    except OSError:
        return 0


def _append_reduce_summary_row(
    *,
    summary_csv: Path,
    ctx: "TargetContext",
    variant: str,
    status: str,
    problem_category: str,
    problem_detail: str,
    input_test_source: Optional[Path],
    test_deltas_csv: Optional[Path],
    selected_top_n: int,
    selected_test_count: int,
    reduced_test_path: Optional[Path],
    out_dir: Path,
    log_file: Path,
) -> None:
    with summary_csv.open("a", encoding="utf-8", newline="") as handle:
        writer = csv.writer(handle)
        writer.writerow(
            [
                ctx.repo,
                ctx.fqcn,
                variant,
                status,
                problem_category,
                problem_detail,
                ctx.manual_test_fqcn or "",
                ctx.generated_test_fqcn or "",
                str(input_test_source) if input_test_source else "",
                str(test_deltas_csv) if test_deltas_csv else "",
                _count_csv_rows(test_deltas_csv),
                selected_top_n,
                selected_test_count,
                str(reduced_test_path) if reduced_test_path else "",
                str(out_dir),
                str(log_file),
            ]
        )


class ReduceStep(Step):
    step_names = ("reduce",)

    def run(self, ctx: "TargetContext") -> bool:
        if not self.should_run():
            return True
        auto_variant = self.pipeline.args.auto_variant
        reduced_root = Path(self.pipeline.args.reduced_out)
        summary_csv = reduce_variant_summary_csv(reduced_root, auto_variant, self.pipeline.args.includes)
        reduced_out = reduced_root / auto_variant / ctx.target_id
        reduced_log = self.pipeline.logs_dir / f"{ctx.target_id}.{auto_variant}.reduce.log"
        top_n = max(1, min(self.pipeline.args.reduce_max_tests, 100))
        test_deltas_csv = None
        for cov_base in covfilter_candidate_out_dirs(
            self.pipeline.covfilter_out_root,
            self.pipeline.adopted_covfilter_out_root,
            auto_variant,
            ctx.target_id,
        ):
            test_deltas_csv = _find_existing_delta_csv(cov_base, ["test_deltas_kept.csv", "tests_deltas_kept.csv"])
            if test_deltas_csv:
                break
        generated_test_src = first_test_source_for_fqcn(ctx.final_sources, ctx.generated_test_fqcn)
        if self.pipeline.covfilter_allow is not None and (ctx.repo, ctx.fqcn) not in self.pipeline.covfilter_allow:
            print(f'[agt] reduce: Skip (agt_line_covered=0): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
            _append_reduce_summary_row(
                summary_csv=summary_csv,
                ctx=ctx,
                variant=auto_variant,
                status="skipped",
                problem_category="excluded_by_agt_coverage",
                problem_detail="Target excluded because AGT coverage summary reports zero covered lines.",
                input_test_source=generated_test_src,
                test_deltas_csv=test_deltas_csv,
                selected_top_n=top_n,
                selected_test_count=0,
                reduced_test_path=None,
                out_dir=reduced_out,
                log_file=reduced_log,
            )
            return True
        if self.pipeline.covfilter_jar is None or not self.pipeline.covfilter_jar.exists():
            print(f'[agt] reduce: Skip (missing --covfilter-jar): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
            _append_reduce_summary_row(
                summary_csv=summary_csv,
                ctx=ctx,
                variant=auto_variant,
                status="skipped",
                problem_category="missing_covfilter_jar",
                problem_detail="Reduce step requires --covfilter-jar.",
                input_test_source=generated_test_src,
                test_deltas_csv=test_deltas_csv,
                selected_top_n=top_n,
                selected_test_count=0,
                reduced_test_path=None,
                out_dir=reduced_out,
                log_file=reduced_log,
            )
            return True
        if not generated_test_src or not generated_test_src.exists():
            print(f'[agt] reduce: Skip (missing generated test source): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
            _append_reduce_summary_row(
                summary_csv=summary_csv,
                ctx=ctx,
                variant=auto_variant,
                status="skipped",
                problem_category="missing_input_test_source",
                problem_detail="Missing generated test source.",
                input_test_source=generated_test_src,
                test_deltas_csv=test_deltas_csv,
                selected_top_n=top_n,
                selected_test_count=0,
                reduced_test_path=None,
                out_dir=reduced_out,
                log_file=reduced_log,
            )
            return True
        if not test_deltas_csv:
            print(f'[agt] reduce: Skip (missing test(s)_deltas_kept.csv): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
            _append_reduce_summary_row(
                summary_csv=summary_csv,
                ctx=ctx,
                variant=auto_variant,
                status="skipped",
                problem_category="missing_test_deltas_csv",
                problem_detail="Missing test_deltas_kept.csv or tests_deltas_kept.csv.",
                input_test_source=generated_test_src,
                test_deltas_csv=test_deltas_csv,
                selected_top_n=top_n,
                selected_test_count=0,
                reduced_test_path=None,
                out_dir=reduced_out,
                log_file=reduced_log,
            )
            return True

        print(f'[agt] Reducing AGT tests (top {top_n}): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
        ok_red, red_tail = run_generate_reduced_app(
            coverage_filter_jar=self.pipeline.covfilter_jar,
            libs_glob_cp=self.pipeline.args.libs_cp,
            original_test_java=generated_test_src,
            test_deltas_csv=test_deltas_csv,
            top_n=top_n,
            out_dir=reduced_out,
            log_file=reduced_log,
        )
        if not ok_red:
            print(f'[agt] reduce: FAIL (see {reduced_log})')
            print("[agt][REDUCE-TAIL]\n" + red_tail)
            _append_reduce_summary_row(
                summary_csv=summary_csv,
                ctx=ctx,
                variant=auto_variant,
                status="failed",
                problem_category="reduce_generation_failed",
                problem_detail=red_tail,
                input_test_source=generated_test_src,
                test_deltas_csv=test_deltas_csv,
                selected_top_n=top_n,
                selected_test_count=0,
                reduced_test_path=None,
                out_dir=reduced_out,
                log_file=reduced_log,
            )
            return True

        reduced_src = reduced_test_path(
            reduced_root,
            ctx.target_id,
            generated_test_src,
            top_n,
            preferred_variants=[auto_variant],
        )
        if reduced_src and reduced_src.exists():
            scaffolding_src = find_scaffolding_source(generated_test_src, list(ctx.final_sources))
            fix_reduced_scaffolding_import(reduced_src, scaffolding_src)
            _append_reduce_summary_row(
                summary_csv=summary_csv,
                ctx=ctx,
                variant=auto_variant,
                status="passed",
                problem_category="",
                problem_detail="",
                input_test_source=generated_test_src,
                test_deltas_csv=test_deltas_csv,
                selected_top_n=top_n,
                selected_test_count=min(top_n, _count_csv_rows(test_deltas_csv)),
                reduced_test_path=reduced_src,
                out_dir=reduced_out,
                log_file=reduced_log,
            )
            return True
        _append_reduce_summary_row(
            summary_csv=summary_csv,
            ctx=ctx,
            variant=auto_variant,
            status="failed",
            problem_category="missing_reduced_output",
            problem_detail="Reduce command completed without writing the expected reduced test file.",
            input_test_source=generated_test_src,
            test_deltas_csv=test_deltas_csv,
            selected_top_n=top_n,
            selected_test_count=0,
            reduced_test_path=None,
            out_dir=reduced_out,
            log_file=reduced_log,
        )
        return True


class AdoptedReduceStep(Step):
    step_names = ("adopted-reduce",)

    def run(self, ctx: "TargetContext") -> bool:
        if not self.should_run():
            return True
        if self.pipeline.covfilter_allow is not None and (ctx.repo, ctx.fqcn) not in self.pipeline.covfilter_allow:
            print(f'[agt] adopted-reduce: Skip (agt_line_covered=0): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
            for variant in ("adopted", "agentic"):
                _append_reduce_summary_row(
                    summary_csv=reduce_variant_summary_csv(
                        self.pipeline.adopted_reduced_out_root, variant, self.pipeline.args.includes
                    ),
                    ctx=ctx,
                    variant=variant,
                    status="skipped",
                    problem_category="excluded_by_agt_coverage",
                    problem_detail="Target excluded because AGT coverage summary reports zero covered lines.",
                    input_test_source=None,
                    test_deltas_csv=None,
                    selected_top_n=max(1, min(self.pipeline.args.adopted_reduce_max_tests, 100)),
                    selected_test_count=0,
                    reduced_test_path=None,
                    out_dir=self.pipeline.adopted_reduced_out_root / variant / ctx.target_id,
                    log_file=self.pipeline.logs_dir / f"{ctx.target_id}.adopted.{variant}.reduce.log",
                )
            return True
        if self.pipeline.covfilter_jar is None or not self.pipeline.covfilter_jar.exists():
            print(f'[agt] adopted-reduce: Skip (missing --covfilter-jar): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
            for variant in ("adopted", "agentic"):
                _append_reduce_summary_row(
                    summary_csv=reduce_variant_summary_csv(
                        self.pipeline.adopted_reduced_out_root, variant, self.pipeline.args.includes
                    ),
                    ctx=ctx,
                    variant=variant,
                    status="skipped",
                    problem_category="missing_covfilter_jar",
                    problem_detail="Reduce step requires --covfilter-jar.",
                    input_test_source=None,
                    test_deltas_csv=None,
                    selected_top_n=max(1, min(self.pipeline.args.adopted_reduce_max_tests, 100)),
                    selected_test_count=0,
                    reduced_test_path=None,
                    out_dir=self.pipeline.adopted_reduced_out_root / variant / ctx.target_id,
                    log_file=self.pipeline.logs_dir / f"{ctx.target_id}.adopted.{variant}.reduce.log",
                )
            return True

        variant_sources = {
            variant: source for variant, source in adopted_variants(self.pipeline.adopted_root, ctx.target_id, ctx.fqcn)
        }
        if not variant_sources:
            print(f'[agt] adopted-reduce: Skip (missing adopted tests): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
        top_n = max(1, min(self.pipeline.args.adopted_reduce_max_tests, 100))
        for variant in ("adopted", "agentic"):
            adopted_src = variant_sources.get(variant)
            summary_csv = reduce_variant_summary_csv(
                self.pipeline.adopted_reduced_out_root, variant, self.pipeline.args.includes
            )
            reduced_out = self.pipeline.adopted_reduced_out_root / variant / ctx.target_id
            reduced_log = self.pipeline.logs_dir / f"{ctx.target_id}.adopted.{variant}.reduce.log"
            if not adopted_src or not adopted_src.exists():
                problem_category = "missing_adopted_tests" if not variant_sources else "missing_input_test_source"
                problem_detail = "Missing adopted tests." if not variant_sources else f'Missing {variant} test source.'
                _append_reduce_summary_row(
                    summary_csv=summary_csv,
                    ctx=ctx,
                    variant=variant,
                    status="skipped",
                    problem_category=problem_category,
                    problem_detail=problem_detail,
                    input_test_source=adopted_src,
                    test_deltas_csv=None,
                    selected_top_n=top_n,
                    selected_test_count=0,
                    reduced_test_path=None,
                    out_dir=reduced_out,
                    log_file=reduced_log,
                )
                continue
            test_deltas_csv = None
            for cov_base in covfilter_candidate_out_dirs(
                self.pipeline.covfilter_out_root,
                self.pipeline.adopted_covfilter_out_root,
                variant,
                ctx.target_id,
            ):
                test_deltas_csv = _find_existing_delta_csv(cov_base, ["test_deltas_kept.csv", "tests_deltas_kept.csv"])
                if test_deltas_csv:
                    break
            if not test_deltas_csv:
                print(
                    f'[agt] adopted-reduce: Skip (missing test(s)_deltas_kept.csv): repo="{ctx.repo}" fqcn="{ctx.fqcn}" variant="{variant}"'
                )
                _append_reduce_summary_row(
                    summary_csv=summary_csv,
                    ctx=ctx,
                    variant=variant,
                    status="skipped",
                    problem_category="missing_test_deltas_csv",
                    problem_detail="Missing test_deltas_kept.csv or tests_deltas_kept.csv.",
                    input_test_source=adopted_src,
                    test_deltas_csv=test_deltas_csv,
                    selected_top_n=top_n,
                    selected_test_count=0,
                    reduced_test_path=None,
                    out_dir=reduced_out,
                    log_file=reduced_log,
                )
                continue
            print(f'[agt] Reducing adopted tests ({variant}, top {top_n}): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
            ok_red, red_tail = run_generate_reduced_app(
                coverage_filter_jar=self.pipeline.covfilter_jar,
                libs_glob_cp=self.pipeline.args.libs_cp,
                original_test_java=adopted_src,
                test_deltas_csv=test_deltas_csv,
                top_n=top_n,
                out_dir=reduced_out,
                log_file=reduced_log,
            )
            if not ok_red:
                print(f'[agt] adopted-reduce: FAIL (see {reduced_log})')
                print("[agt][ADOPTED-REDUCE-TAIL]\n" + red_tail)
                _append_reduce_summary_row(
                    summary_csv=summary_csv,
                    ctx=ctx,
                    variant=variant,
                    status="failed",
                    problem_category="reduce_generation_failed",
                    problem_detail=red_tail,
                    input_test_source=adopted_src,
                    test_deltas_csv=test_deltas_csv,
                    selected_top_n=top_n,
                    selected_test_count=0,
                    reduced_test_path=None,
                    out_dir=reduced_out,
                    log_file=reduced_log,
                )
                continue
            reduced_src = reduced_variant_test_path(self.pipeline.adopted_reduced_out_root, variant, ctx.target_id, top_n)
            if reduced_src and reduced_src.exists():
                _append_reduce_summary_row(
                    summary_csv=summary_csv,
                    ctx=ctx,
                    variant=variant,
                    status="passed",
                    problem_category="",
                    problem_detail="",
                    input_test_source=adopted_src,
                    test_deltas_csv=test_deltas_csv,
                    selected_top_n=top_n,
                    selected_test_count=min(top_n, _count_csv_rows(test_deltas_csv)),
                    reduced_test_path=reduced_src,
                    out_dir=reduced_out,
                    log_file=reduced_log,
                )
                continue
            _append_reduce_summary_row(
                summary_csv=summary_csv,
                ctx=ctx,
                variant=variant,
                status="failed",
                problem_category="missing_reduced_output",
                problem_detail="Reduce command completed without writing the expected reduced test file.",
                input_test_source=adopted_src,
                test_deltas_csv=test_deltas_csv,
                selected_top_n=top_n,
                selected_test_count=0,
                reduced_test_path=None,
                out_dir=reduced_out,
                log_file=reduced_log,
            )
        return True
