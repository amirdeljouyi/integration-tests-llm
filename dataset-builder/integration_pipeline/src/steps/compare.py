from __future__ import annotations

from pathlib import Path
from typing import TYPE_CHECKING

from ..metrics.compare import compare_tests, run_tri_compare_with_log
from ..pipeline.helpers import adopted_variants, first_test_source_for_fqcn, reduced_test_path
from .base import Step

if TYPE_CHECKING:
    from ..pipeline.pipeline import TargetContext


class CompareStep(Step):
    step_names = ("compare",)

    def run(self, ctx: TargetContext) -> bool:
        if not self.should_run():
            return True
        if self.pipeline.covfilter_allow is not None and (ctx.repo, ctx.fqcn) not in self.pipeline.covfilter_allow:
            print(f'[agt] compare: Skip (agt_line_covered=0): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
            return True

        reduced_root = Path(self.pipeline.args.reduced_out)
        top_n = max(1, min(self.pipeline.args.reduce_max_tests, 100))
        generated_test_src = first_test_source_for_fqcn(ctx.final_sources, ctx.generated_test_fqcn)
        reduced_src = reduced_test_path(reduced_root, ctx.target_id, generated_test_src, top_n) if generated_test_src else None
        manual_test_src = first_test_source_for_fqcn(ctx.final_sources, ctx.manual_test_fqcn)
        variants = adopted_variants(self.pipeline.adopted_root, ctx.target_id)

        if not reduced_src or not reduced_src.exists():
            print(f'[agt] compare: Skip (missing reduced AGT test): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
            return True
        if not manual_test_src or not manual_test_src.exists():
            print(f'[agt] compare: Skip (missing manual test source): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
            return True
        if not variants:
            print(f'[agt] compare: Skip (missing adopted tests): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
            return True

        compare_root = Path(self.pipeline.args.compare_root)
        tri_script = Path("src/metrics/tri_compare_tests.py")
        tri_csv = Path("results/compare/tri_compare.csv")

        compare_out = Path(self.pipeline.args.compare_out) / "compare.csv"
        for idx, (variant, adopted_src) in enumerate(variants):
            if not adopted_src.exists():
                print(f'[agt] compare: Skip (missing {variant} test): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
                continue
            try:
                compare_tests(
                    comparison_root=compare_root,
                    agt_file=reduced_src,
                    adopted_file=adopted_src,
                    out_csv=compare_out,
                    minimum_tokens=self.pipeline.args.compare_min_tokens,
                    candidate_variant=variant,
                    include_auto=idx == 0,
                    repo=ctx.repo,
                    fqcn=ctx.fqcn,
                )
            except Exception as e:
                print(f'[agt] compare: FAIL ({e}) repo="{ctx.repo}" fqcn="{ctx.fqcn}" variant="{variant}"')
            tri_log = self.pipeline.logs_dir / f"{ctx.target_id}.tri_compare.{variant}.log"
            try:
                run_tri_compare_with_log(
                    tri_script=tri_script,
                    group_id=f"{ctx.target_id}.{variant}",
                    auto_path=reduced_src,
                    adopted_path=adopted_src,
                    manual_path=manual_test_src,
                    out_csv=tri_csv,
                    log_path=tri_log,
                    adopted_variant=variant,
                    include_auto=idx == 0,
                )
                print(f'[agt] compare: tri_compare appended to {tri_csv} ({variant})')
            except ModuleNotFoundError as e:
                print(
                    f'[agt] compare: Skip (tri_compare missing dep: {e.name}) repo="{ctx.repo}" fqcn="{ctx.fqcn}" variant="{variant}"'
                )
            except Exception as e:
                print(
                    f'[agt] compare: FAIL (tri_compare: {e}) repo="{ctx.repo}" fqcn="{ctx.fqcn}" variant="{variant}" (see {tri_log})'
                )
        return True
