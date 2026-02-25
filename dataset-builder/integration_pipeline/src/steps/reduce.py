from __future__ import annotations

from pathlib import Path
from typing import List, Optional, Tuple, TYPE_CHECKING

from ..pipeline.helpers import (
    adopted_variants,
    find_scaffolding_source,
    fix_reduced_scaffolding_import,
    first_test_source_for_fqcn,
    reduced_test_path,
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


class ReduceStep(Step):
    step_names = ("reduce",)

    def run(self, ctx: "TargetContext") -> bool:
        if not self.should_run():
            return True
        reduced_root = Path(self.pipeline.args.reduced_out)
        test_deltas_csv = _find_existing_delta_csv(
            self.pipeline.covfilter_out_root / ctx.target_id,
            ["test_deltas_kept.csv", "tests_deltas_kept.csv"],
        )
        generated_test_src = first_test_source_for_fqcn(ctx.final_sources, ctx.generated_test_fqcn)
        if self.pipeline.covfilter_allow is not None and (ctx.repo, ctx.fqcn) not in self.pipeline.covfilter_allow:
            print(f'[agt] reduce: Skip (agt_line_covered=0): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
            return True
        if self.pipeline.covfilter_jar is None or not self.pipeline.covfilter_jar.exists():
            print(f'[agt] reduce: Skip (missing --covfilter-jar): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
            return True
        if not generated_test_src or not generated_test_src.exists():
            print(f'[agt] reduce: Skip (missing generated test source): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
            return True
        if not test_deltas_csv:
            print(f'[agt] reduce: Skip (missing test(s)_deltas_kept.csv): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
            return True

        reduced_out = reduced_root / "auto" / ctx.target_id
        reduced_log = self.pipeline.logs_dir / f"{ctx.target_id}.reduce.log"
        top_n = max(1, min(self.pipeline.args.reduce_max_tests, 100))
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
            return True

        reduced_src = reduced_test_path(reduced_root, ctx.target_id, generated_test_src, top_n)
        if reduced_src and reduced_src.exists():
            scaffolding_src = find_scaffolding_source(generated_test_src, ctx.final_sources)
            fix_reduced_scaffolding_import(reduced_src, scaffolding_src)
        return True


class AdoptedReduceStep(Step):
    step_names = ("adopted-reduce",)

    def run(self, ctx: "TargetContext") -> bool:
        if not self.should_run():
            return True
        if self.pipeline.covfilter_allow is not None and (ctx.repo, ctx.fqcn) not in self.pipeline.covfilter_allow:
            print(f'[agt] adopted-reduce: Skip (agt_line_covered=0): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
            return True
        if self.pipeline.covfilter_jar is None or not self.pipeline.covfilter_jar.exists():
            print(f'[agt] adopted-reduce: Skip (missing --covfilter-jar): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
            return True

        variants = adopted_variants(self.pipeline.adopted_root, ctx.target_id)
        if not variants:
            print(f'[agt] adopted-reduce: Skip (missing adopted tests): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
            return True

        for variant, adopted_src in variants:
            test_deltas_csv = _find_existing_delta_csv(
                self.pipeline.adopted_covfilter_out_root / variant / ctx.target_id,
                ["test_deltas_kept.csv", "tests_deltas_kept.csv"],
            )
            if not test_deltas_csv:
                print(
                    f'[agt] adopted-reduce: Skip (missing test(s)_deltas_kept.csv): repo="{ctx.repo}" fqcn="{ctx.fqcn}" variant="{variant}"'
                )
                continue
            reduced_out = self.pipeline.adopted_reduced_out_root / variant / ctx.target_id
            reduced_log = self.pipeline.logs_dir / f"{ctx.target_id}.adopted.{variant}.reduce.log"
            top_n = max(1, min(self.pipeline.args.adopted_reduce_max_tests, 100))
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
        return True
