from __future__ import annotations

from typing import TYPE_CHECKING

from ..core.java import comment_compile_errors, index_candidate_java_files
from ..pipeline.helpers import adopted_variants
from .base import Step

if TYPE_CHECKING:
    from ..pipeline.pipeline import TargetContext


class AdoptedCommentStep(Step):
    step_names = ("adopted-comment",)

    def run(self, ctx: "TargetContext") -> bool:
        if not self.should_run():
            return True

        variants = adopted_variants(self.pipeline.adopted_root, ctx.target_id, ctx.fqcn)
        if not variants:
            print(f'[agt] adopted-comment: Skip (missing adopted tests): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
            return True

        for variant, adopted_src in variants:
            if not adopted_src.exists():
                print(f'[agt] adopted-comment: Skip (missing {variant} source): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
                continue
            comment_build = self.pipeline.build_dir / "adopted-comment-classes" / variant / ctx.target_id
            comment_log = self.pipeline.logs_dir / f"{ctx.target_id}.adopted.{variant}.comment.log"
            candidate_java_files = [adopted_src]
            if ctx.repo_root_for_deps and ctx.repo_root_for_deps.exists():
                candidate_java_files.extend(index_candidate_java_files(ctx.repo_root_for_deps))
            print(f'[agt] Commenting compile errors ({variant}): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
            ok = comment_compile_errors(
                test_file=adopted_src,
                build_dir=comment_build,
                log_file=comment_log,
                libs_glob_cp=self.pipeline.args.libs_cp,
                sut_jar=ctx.sut_jar,
                candidate_java_files=candidate_java_files,
            )
            if not ok:
                print(
                    f'[agt] adopted-comment: FAIL (see {comment_log}) repo="{ctx.repo}" fqcn="{ctx.fqcn}" variant="{variant}"'
                )
        return True
