from __future__ import annotations

from pathlib import Path
from typing import Dict, Optional, TYPE_CHECKING

from ..pipeline.helpers import first_test_source_for_fqcn
from .base import Step

if TYPE_CHECKING:
    from ..pipeline.pipeline import TargetContext


def _find_reduced_variant_files(root: Path, variant: str, target_id: str) -> list[Path]:
    base = root / variant / target_id
    if not base.exists():
        return []
    return sorted(base.rglob("*.java"))


def _render_pr_template(template: str, replacements: Dict[str, str]) -> str:
    out = template
    for key, val in replacements.items():
        out = out.replace(key, val)
    return out


def write_pr_draft(
    *,
    template_path: Path,
    out_path: Path,
    repo: str,
    fqcn: str,
    target_id: str,
    variant: str,
    manual_test_src: Optional[Path],
    adopted_reduced_root: Path,
) -> bool:
    if not template_path.exists():
        return False

    try:
        template_text = template_path.read_text(encoding="utf-8")
    except Exception:
        return False

    reduced_files = _find_reduced_variant_files(adopted_reduced_root, variant, target_id)
    if not reduced_files:
        return False

    other_files = [str(path) for path in reduced_files]

    replacements = {
        "CUT: <repo>/<fqcn>": f"CUT: {repo}/{fqcn}",
        "<component/class>": fqcn,
        "Manual test file updated in-place: <path/to/manual/test/...>": (
            f"Manual test file updated in-place: {manual_test_src}"
            if manual_test_src
            else "Manual test file updated in-place: <path/to/manual/test/...>"
        ),
        "Other files: <paths, if any>": "Other files: " + ", ".join(other_files),
    }

    out_path.parent.mkdir(parents=True, exist_ok=True)
    out_path.write_text(_render_pr_template(template_text, replacements), encoding="utf-8")
    return True


class PullRequestMakerStep(Step):
    step_names = ("pull-request-maker",)

    def run(self, ctx: "TargetContext") -> bool:
        if not self.should_run():
            return True
        if not self.pipeline.pr_template.exists():
            print(f'[agt] pull-request-maker: Skip (missing template): {self.pipeline.pr_template}')
            return True

        manual_test_src = first_test_source_for_fqcn(ctx.manual_sources or ctx.final_sources, ctx.manual_test_fqcn)
        self.pipeline.pr_out_root.mkdir(parents=True, exist_ok=True)
        for variant in ("adopted", "agentic"):
            out_path = self.pipeline.pr_out_root / f"{ctx.target_id}.{variant}.md"
            ok = write_pr_draft(
                template_path=self.pipeline.pr_template,
                out_path=out_path,
                repo=ctx.repo,
                fqcn=ctx.fqcn,
                target_id=ctx.target_id,
                variant=variant,
                manual_test_src=manual_test_src,
                adopted_reduced_root=self.pipeline.adopted_reduced_out_root,
            )
            if ok:
                print(f'[agt] pull-request-maker: wrote {out_path}')
            else:
                print(f'[agt] pull-request-maker: Skip (missing {variant} reduced test) for {ctx.target_id}')
        return True
