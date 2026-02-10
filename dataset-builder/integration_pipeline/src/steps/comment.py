from __future__ import annotations

import re
import subprocess
from pathlib import Path
from typing import List, Set, Tuple, TYPE_CHECKING

from ..core.common import ensure_dir
from ..pipeline.helpers import adopted_variants
from .base import Step

if TYPE_CHECKING:
    from ..pipeline.pipeline import TargetContext


def _strip_string_literals(line: str) -> str:
    return re.sub(r'"(?:\\.|[^"\\])*"', '""', line)


def _brace_counts(line: str) -> Tuple[int, int]:
    sanitized = _strip_string_literals(line)
    for _ in range(2):
        sanitized = re.sub(r"\{[^{}]*\}", "", sanitized)
    return sanitized.count("{"), sanitized.count("}")


def _parse_javac_error_lines(output: str, test_file: Path) -> List[int]:
    path_str = str(test_file)
    pattern = re.compile(rf"^{re.escape(path_str)}:(\d+):")
    lines: List[int] = []
    for line in output.splitlines():
        m = pattern.match(line)
        if m:
            try:
                lines.append(int(m.group(1)))
            except ValueError:
                continue
    return sorted(set(lines))


def _comment_error_lines_in_file(test_file: Path, line_numbers: List[int]) -> bool:
    try:
        raw = test_file.read_text(encoding="utf-8", errors="ignore")
    except Exception:
        return False

    lines = raw.splitlines(keepends=True)
    if not lines:
        return False

    lines_to_comment: Set[int] = set()
    for ln in line_numbers:
        idx = ln - 1
        if idx < 0 or idx >= len(lines):
            continue
        if idx in lines_to_comment:
            continue
        if lines[idx].lstrip().startswith("//"):
            continue

        open_current, close_current = _brace_counts(lines[idx])
        lines_to_comment.add(idx)

        if open_current != close_current:
            open_count = open_current
            close_count = close_current
            i = idx + 1
            while i < len(lines):
                if not lines[i].lstrip().startswith("//"):
                    o_cnt, c_cnt = _brace_counts(lines[i])
                    open_count += o_cnt
                    close_count += c_cnt
                    lines_to_comment.add(i)
                    if open_count > 0 and open_count == close_count:
                        break
                i += 1

    if not lines_to_comment:
        return False

    for i in sorted(lines_to_comment):
        if lines[i].lstrip().startswith("//"):
            continue
        lines[i] = f"// {lines[i]}"

    test_file.write_text("".join(lines), encoding="utf-8", errors="ignore")
    return True


def comment_compile_errors(
    *,
    test_file: Path,
    build_dir: Path,
    log_file: Path,
    libs_glob_cp: str,
    sut_jar: Path,
    max_iterations: int = 50,
) -> bool:
    ensure_dir(build_dir)
    ensure_dir(log_file.parent)
    test_file = test_file.resolve()

    cp = f"{libs_glob_cp}:{sut_jar}"
    for iteration in range(max_iterations):
        cmd = [
            "javac",
            "-Xmaxerrs",
            "0",
            "-cp",
            cp,
            "-d",
            str(build_dir),
            str(test_file),
        ]
        proc = subprocess.run(
            cmd,
            stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT,
            text=True,
        )
        out = proc.stdout or ""
        status = "success" if proc.returncode == 0 else "failure"
        log_file.write_text(
            f"[agt] javac cmd:\n{' '.join(cmd)}\n\n[agt] returncode: {proc.returncode} ({status})\n\n[agt] output:\n{out}\n",
            encoding="utf-8",
            errors="ignore",
        )

        if proc.returncode == 0:
            return True

        error_lines = _parse_javac_error_lines(out, test_file)
        if not error_lines:
            return False

        if not _comment_error_lines_in_file(test_file, error_lines):
            return False

        if iteration == max_iterations - 1:
            return False

    return False


class AdoptedCommentStep(Step):
    step_names = ("adopted-comment",)

    def run(self, ctx: "TargetContext") -> bool:
        if not self.should_run():
            return True

        variants = adopted_variants(self.pipeline.adopted_root, ctx.target_id)
        if not variants:
            print(f'[agt] adopted-comment: Skip (missing adopted tests): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
            return True

        for variant, adopted_src in variants:
            if not adopted_src.exists():
                print(f'[agt] adopted-comment: Skip (missing {variant} source): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
                continue
            comment_build = self.pipeline.build_dir / "adopted-comment-classes" / variant / ctx.target_id
            comment_log = self.pipeline.logs_dir / f"{ctx.target_id}.adopted.{variant}.comment.log"
            print(f'[agt] Commenting compile errors ({variant}): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
            ok = comment_compile_errors(
                test_file=adopted_src,
                build_dir=comment_build,
                log_file=comment_log,
                libs_glob_cp=self.pipeline.args.libs_cp,
                sut_jar=ctx.sut_jar,
            )
            if not ok:
                print(
                    f'[agt] adopted-comment: FAIL (see {comment_log}) repo="{ctx.repo}" fqcn="{ctx.fqcn}" variant="{variant}"'
                )
        return True
