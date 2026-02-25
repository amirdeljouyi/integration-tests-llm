from __future__ import annotations

import csv
import html
import re
import shutil
import subprocess
from dataclasses import dataclass
from pathlib import Path
from typing import Dict, List, Optional, Tuple, TYPE_CHECKING

from ..core.common import repo_to_dir
from ..pipeline.helpers import (
    first_test_source_for_fqcn,
    reduced_test_path,
    reduced_variant_test_path,
)
from .base import Step

if TYPE_CHECKING:
    from ..pipeline.pipeline import TargetContext


_METHOD_RE = re.compile(
    r"^(?P<indent>\s*)(?:public|protected|private)?\s*(?:final\s+)?(?:static\s+)?void\s+"
    r"(?P<name>[A-Za-z_][A-Za-z0-9_]*)\s*\("
)


@dataclass
class MethodDelta:
    added_lines: int
    added_methods: int
    added_branches: int
    added_instructions: int
    target_added_lines: int
    line_entries: List["LineEntry"]


@dataclass
class LineEntry:
    class_name: str
    newly_covered_lines: str


def _read_csv_rows(path: Path) -> List[Dict[str, str]]:
    if not path.exists():
        return []
    with path.open("r", encoding="utf-8", newline="") as f:
        return [dict(r) for r in csv.DictReader(f)]


def _selector_method_name(selector: str) -> str:
    raw = (selector or "").strip()
    if "#" not in raw:
        return raw
    return raw.rsplit("#", 1)[1].strip()


def _safe_int(value: str) -> int:
    try:
        return int((value or "").strip())
    except Exception:
        return 0


def _parse_line_spec(spec: str) -> List[int]:
    out: List[int] = []
    for token in [t.strip() for t in (spec or "").split(";") if t.strip()]:
        if "-" in token:
            a, b = token.split("-", 1)
            try:
                start = int(a)
                end = int(b)
            except ValueError:
                continue
            if end < start:
                start, end = end, start
            out.extend(range(start, end + 1))
            continue
        try:
            out.append(int(token))
        except ValueError:
            continue
    return sorted(set(n for n in out if n > 0))


def _parse_spans(spec: str) -> List[Tuple[int, int]]:
    spans: List[Tuple[int, int]] = []
    for token in [t.strip() for t in (spec or "").split(";") if t.strip()]:
        if "-" in token:
            a, b = token.split("-", 1)
            try:
                start = int(a)
                end = int(b)
            except ValueError:
                continue
            if end < start:
                start, end = end, start
            if start > 0:
                spans.append((start, end))
            continue
        try:
            n = int(token)
        except ValueError:
            continue
        if n > 0:
            spans.append((n, n))

    uniq: List[Tuple[int, int]] = []
    seen = set()
    for span in spans:
        if span in seen:
            continue
        seen.add(span)
        uniq.append(span)
    return uniq


def _span_token(span: Tuple[int, int]) -> str:
    a, b = span
    return f"{a}" if a == b else f"{a}-{b}"


def _pick_largest_span(spans: List[Tuple[int, int]]) -> Optional[Tuple[int, int]]:
    if not spans:
        return None
    return max(spans, key=lambda s: (s[1] - s[0] + 1, -s[0]))


def _find_existing_delta_csv(base_dir: Path, names: List[str]) -> Optional[Path]:
    for name in names:
        cand = base_dir / name
        if cand.exists():
            return cand
    return None


def _load_line_total_from_summaries(
    summary_csvs: List[Path],
    *,
    repo: str,
    fqcn: str,
    variant: str,
) -> int:
    repo = (repo or "").strip()
    fqcn = (fqcn or "").strip()
    variant = (variant or "").strip().lower()
    if not repo or not fqcn or not variant:
        return 0

    for csv_path in summary_csvs:
        if not csv_path.exists():
            continue
        for row in _read_csv_rows(csv_path):
            if (row.get("repo", "") or "").strip() != repo:
                continue
            if (row.get("fqcn", "") or "").strip() != fqcn:
                continue
            if (row.get("variant", "") or "").strip().lower() != variant:
                continue
            val = _safe_int(row.get("line_total", "0"))
            if val > 0:
                return val
    return 0


def _is_target_class(class_name: str, target_class: str) -> bool:
    cls = (class_name or "").strip()
    tgt = (target_class or "").strip()
    if not cls or not tgt:
        return False
    return cls == tgt or cls.startswith(tgt + "$")


def _load_method_deltas(
    test_deltas_csv: Path,
    line_deltas_csv: Path,
    *,
    target_class: str,
) -> Tuple[Dict[str, MethodDelta], int]:
    by_method: Dict[str, MethodDelta] = {}

    for row in _read_csv_rows(test_deltas_csv):
        method = _selector_method_name(row.get("test_selector", ""))
        if not method:
            continue
        by_method[method] = MethodDelta(
            added_lines=_safe_int(row.get("added_lines", "0")),
            added_methods=_safe_int(row.get("added_methods", "0")),
            added_branches=_safe_int(row.get("added_branches", "0")),
            added_instructions=_safe_int(row.get("added_instructions", "0")),
            target_added_lines=0,
            line_entries=[],
        )

    for row in _read_csv_rows(line_deltas_csv):
        method = _selector_method_name(row.get("test_selector", ""))
        if not method or method not in by_method:
            continue
        cls = (row.get("class_name", "") or "").strip()
        newly = (row.get("newly_covered_lines", "") or "").strip()
        if not cls or not newly:
            continue
        if not _is_target_class(cls, target_class):
            continue
        by_method[method].target_added_lines += len(_parse_line_spec(newly))
        by_method[method].line_entries.append(LineEntry(class_name=cls, newly_covered_lines=newly))

    total_target_added_lines = sum(max(0, d.target_added_lines) for d in by_method.values())
    return by_method, total_target_added_lines


def _resolve_source_for_class(
    repo_root: Path,
    class_name: str,
    cache: Dict[str, Optional[Path]],
) -> Optional[Path]:
    if class_name in cache:
        return cache[class_name]

    primary_name = class_name.split("$", 1)[0]
    rel = Path(*primary_name.split(".")).with_suffix(".java")
    direct = repo_root / rel
    if direct.exists():
        cache[class_name] = direct
        return direct

    matches = list(repo_root.rglob(rel.name))
    if matches:
        for cand in matches:
            if str(cand).endswith(str(rel)):
                cache[class_name] = cand
                return cand
        cache[class_name] = matches[0]
        return matches[0]

    cache[class_name] = None
    return None


def _git_output(repo_root: Path, *args: str) -> Optional[str]:
    try:
        proc = subprocess.run(
            ["git", "-C", str(repo_root), *args],
            stdout=subprocess.PIPE,
            stderr=subprocess.DEVNULL,
            text=True,
            check=False,
        )
    except Exception:
        return None
    out = (proc.stdout or "").strip()
    if proc.returncode != 0 or not out:
        return None
    return out


def _github_ref(repo_root: Path) -> str:
    # Prefer immutable commit links so line anchors stay stable.
    head_sha = _git_output(repo_root, "rev-parse", "HEAD")
    if head_sha:
        return head_sha
    remote_head = _git_output(repo_root, "symbolic-ref", "--short", "refs/remotes/origin/HEAD")
    if remote_head and "/" in remote_head:
        return remote_head.split("/", 1)[1]
    for cand in ("main", "master"):
        if (repo_root / ".git").exists() and _git_output(repo_root, "rev-parse", "--verify", cand):
            return cand
    return "main"


def _github_url(repo: str, repo_root: Path, src: Path, span: Tuple[int, int], ref: str) -> Optional[str]:
    try:
        rel = src.resolve().relative_to(repo_root.resolve())
    except Exception:
        return None
    a, b = span
    return f"https://github.com/{repo}/blob/{ref}/{rel.as_posix()}#L{a}-L{b}"


def _render_line_block(
    indent: str,
    *,
    repo: str,
    repo_root: Path,
    repo_ref: str,
    line_entry: LineEntry,
    source_cache: Dict[str, Optional[Path]],
) -> List[str]:
    def _format_code_line(raw: str) -> str:
        # Preserve indentation in rendered HTML blocks: expand tabs and keep spaces explicit.
        expanded = raw.expandtabs(4)
        return html.escape(expanded, quote=False)

    spans = _parse_spans(line_entry.newly_covered_lines)
    span = _pick_largest_span(spans)
    if not span:
        return []

    src = _resolve_source_for_class(repo_root, line_entry.class_name, source_cache)
    simple_name = line_entry.class_name.split(".")[-1]
    label_name = simple_name.split("$", 1)[0] + ".java"

    block: List[str] = []
    if src and src.exists():
        url = _github_url(repo, repo_root, src, span, repo_ref)
        if url:
            block.append(
                f'{indent} * Full version of the covered block is here: '
                f'<a href="{url}">{label_name} (lines {span[0]}-{span[1]})</a>'
            )
        else:
            block.append(
                f"{indent} * Full version of the covered block is here: {line_entry.class_name} "
                f"(lines {span[0]}-{span[1]})"
            )

        try:
            src_lines = src.read_text(encoding="utf-8", errors="ignore").splitlines()
        except Exception:
            src_lines = []

        if src_lines:
            covered = set(range(span[0], span[1] + 1))
            start, end = span
            end = min(end, start + 24)
            block.append(f"{indent} * Covered Lines:")
            block.append(f"{indent} * <pre><code>")
            for n in range(start, min(end, len(src_lines)) + 1):
                code = _format_code_line(src_lines[n - 1])
                block.append(f"{indent} * {code}")
            block.append(f"{indent} * </code></pre>")
            other_spans = [s for s in spans if s != span]
            if other_spans:
                others = ";".join(_span_token(s) for s in other_spans)
                block.append(f"{indent} * Other newly covered ranges to check: {others}")
            return block

    block.append(
        f"{indent} * Full version of the covered block is here: {line_entry.class_name} "
        f"(lines {span[0]}-{span[1]})"
    )
    other_spans = [s for s in spans if s != span]
    if other_spans:
        others = ";".join(_span_token(s) for s in other_spans)
        block.append(f"{indent} * Other newly covered ranges to check: {others}")
    return block


def _build_annotation_comment(
    indent: str,
    *,
    repo: str,
    repo_root: Path,
    repo_ref: str,
    target_class: str,
    method_delta: MethodDelta,
    denominator_line_total: int,
    source_cache: Dict[str, Optional[Path]],
) -> List[str]:
    pct = (100.0 * method_delta.target_added_lines / denominator_line_total) if denominator_line_total > 0 else 0.0
    out = [f"{indent}/**"]
    out.append(
        f"{indent} * This test added target-class coverage {pct:.2f}% for {target_class} "
        f"({method_delta.target_added_lines}/{denominator_line_total} lines)."
    )
    out.append(
        f"{indent} * Delta details: +{method_delta.added_methods} methods, "
        f"+{method_delta.added_branches} branches, +{method_delta.added_instructions} instructions."
    )

    ranked_entries = sorted(
        method_delta.line_entries,
        key=lambda e: (
            0 if _resolve_source_for_class(repo_root, e.class_name, source_cache) is not None else 1,
            -len(_parse_line_spec(e.newly_covered_lines)),
        ),
    )

    for line_entry in ranked_entries[:2]:
        out.extend(
            _render_line_block(
                indent,
                repo=repo,
                repo_root=repo_root,
                repo_ref=repo_ref,
                line_entry=line_entry,
                source_cache=source_cache,
            )
        )

    if len(method_delta.line_entries) > 2:
        out.append(f"{indent} * Additional covered classes omitted: {len(method_delta.line_entries) - 2}")

    out.append(f"{indent} */")
    return out


def _annotate_reduced_test(
    *,
    repo: str,
    repo_root: Path,
    repo_ref: str,
    target_class: str,
    denominator_line_total: int,
    reduced_src: Path,
    annotated_out: Path,
    test_deltas_csv: Path,
    line_deltas_csv: Path,
) -> bool:
    try:
        text = reduced_src.read_text(encoding="utf-8", errors="ignore")
    except Exception:
        return False
    original_text = text
    # Rebuild annotation blocks on each run to avoid stale/incorrect links.
    text = re.sub(
        r"/\*\*[\s\S]*?This test added[\s\S]*?\*/\s*",
        "",
        text,
        flags=re.IGNORECASE,
    )
    # Normalize legacy annotations: remove any span wrappers from prior runs.
    text = re.sub(r"</?span[^>]*>", "", text, flags=re.IGNORECASE)
    # Normalize legacy explicit-space entities from prior runs.
    text = text.replace("&#32;", " ")

    by_method, total_target_added_lines = _load_method_deltas(
        test_deltas_csv,
        line_deltas_csv,
        target_class=target_class,
    )
    if not by_method:
        annotated_out.parent.mkdir(parents=True, exist_ok=True)
        shutil.copy2(reduced_src, annotated_out)
        return False

    lines = text.splitlines()
    if not lines:
        annotated_out.parent.mkdir(parents=True, exist_ok=True)
        shutil.copy2(reduced_src, annotated_out)
        return False

    source_cache: Dict[str, Optional[Path]] = {}
    new_lines: List[str] = []
    changed = text != original_text

    def _has_annotation_immediately_before(buf: List[str]) -> bool:
        idx = len(buf) - 1
        while idx >= 0 and not buf[idx].strip():
            idx -= 1
        if idx < 0 or buf[idx].strip() != "*/":
            return False
        start = idx
        while start >= 0 and not buf[start].lstrip().startswith("/**"):
            start -= 1
        if start < 0:
            return False
        block = "\n".join(buf[start : idx + 1]).lower()
        return "added coverage" in block

    def _relocate_existing_annotation_before_method_annotations(buf: List[str]) -> Tuple[List[str], bool]:
        idx = len(buf) - 1
        while idx >= 0 and not buf[idx].strip():
            idx -= 1
        if idx < 0 or buf[idx].strip() != "*/":
            return buf, False

        end = idx
        start = end
        while start >= 0 and not buf[start].lstrip().startswith("/**"):
            start -= 1
        if start < 0:
            return buf, False

        pre = buf[:start]
        comment_block = buf[start : end + 1]
        post = buf[end + 1 :]

        j = len(pre) - 1
        while j >= 0 and not pre[j].strip():
            j -= 1
        if j < 0 or not pre[j].lstrip().startswith("@"):
            return buf, False

        ann_end = j
        while j >= 0 and pre[j].lstrip().startswith("@"):
            j -= 1
        ann_start = j + 1

        before_ann = pre[:ann_start]
        ann_block = pre[ann_start : ann_end + 1]
        reordered = list(before_ann)
        if reordered and reordered[-1].strip() != "":
            reordered.append("")
        reordered.extend(comment_block)
        reordered.extend(ann_block)
        reordered.extend(post)
        return reordered, True

    for i, line in enumerate(lines):
        m = _METHOD_RE.match(line)
        if m:
            method_name = m.group("name")
            indent = m.group("indent")
            delta = by_method.get(method_name)
            if delta:
                relocated, moved = _relocate_existing_annotation_before_method_annotations(new_lines)
                if moved:
                    new_lines = relocated
                    changed = True

                ann_start = len(new_lines)
                while ann_start > 0 and new_lines[ann_start - 1].lstrip().startswith("@"):
                    ann_start -= 1
                prefix = new_lines[:ann_start]
                trailing_annotations = new_lines[ann_start:]

                if not _has_annotation_immediately_before(prefix):
                    comment_lines = _build_annotation_comment(
                        indent,
                        repo=repo,
                        repo_root=repo_root,
                        repo_ref=repo_ref,
                        target_class=target_class,
                        method_delta=delta,
                        denominator_line_total=(
                            denominator_line_total if denominator_line_total > 0 else total_target_added_lines
                        ),
                        source_cache=source_cache,
                    )
                    if prefix and prefix[-1].strip() != "":
                        prefix.append("")
                    prefix.extend(comment_lines)
                    prefix.extend(trailing_annotations)
                    new_lines = prefix
                    changed = True
        new_lines.append(line)

    annotated_out.parent.mkdir(parents=True, exist_ok=True)
    if changed:
        annotated_out.write_text("\n".join(new_lines) + "\n", encoding="utf-8")
        return True

    shutil.copy2(reduced_src, annotated_out)
    return True


def _find_any_reduced_file(base_dir: Path) -> Optional[Path]:
    if not base_dir.exists():
        return None
    matches = sorted(base_dir.rglob("*_Top*.java"))
    return matches[0] if matches else None


def _resolve_rel_path(src: Path, roots: List[Path]) -> Path:
    for root in roots:
        try:
            return src.resolve().relative_to(root.resolve())
        except Exception:
            continue
    return Path(src.name)


class ReducedAnnotationStep(Step):
    step_names = ("annotation",)

    def run(self, ctx: "TargetContext") -> bool:
        if not self.should_run():
            return True
        if self.pipeline.covfilter_allow is not None and (ctx.repo, ctx.fqcn) not in self.pipeline.covfilter_allow:
            print(f'[agt] annotation: Skip (agt_line_covered=0): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
            return True

        enabled = {v.strip().lower() for v in self.pipeline.args.annotation_variants.split(",") if v.strip()}
        if not enabled:
            enabled = {"auto", "adopted", "agentic"}

        repo_root = self.pipeline.repos_dir / repo_to_dir(ctx.repo)
        repo_ref = _github_ref(repo_root)
        annotation_root = Path(self.pipeline.args.annotation_out)
        summary_csvs = [self.pipeline.summary_csv, self.pipeline.adopted_summary_csv]

        if "auto" in enabled:
            generated_test_src = first_test_source_for_fqcn(ctx.final_sources, ctx.generated_test_fqcn)
            reduced_root = Path(self.pipeline.args.reduced_out)
            top_n = max(1, min(self.pipeline.args.reduce_max_tests, 100))
            reduced_src = (
                reduced_test_path(reduced_root, ctx.target_id, generated_test_src, top_n)
                if generated_test_src
                else None
            )
            if reduced_src is None:
                reduced_src = _find_any_reduced_file(reduced_root / "auto" / ctx.target_id)
            if reduced_src and reduced_src.exists():
                rel = _resolve_rel_path(
                    reduced_src,
                    [reduced_root / "auto" / ctx.target_id, reduced_root / ctx.target_id],
                )
                annotated_out = annotation_root / "auto" / ctx.target_id / rel
                cov_base = self.pipeline.covfilter_out_root / ctx.target_id
                test_deltas = _find_existing_delta_csv(cov_base, ["test_deltas_kept.csv", "tests_deltas_kept.csv"])
                line_deltas = _find_existing_delta_csv(cov_base, ["line_deltas_kept.csv", "lines_deltas_kept.csv"])
                if test_deltas and line_deltas:
                    line_total = _load_line_total_from_summaries(
                        summary_csvs,
                        repo=ctx.repo,
                        fqcn=ctx.fqcn,
                        variant="auto",
                    )
                    if _annotate_reduced_test(
                        repo=ctx.repo,
                        repo_root=repo_root,
                        repo_ref=repo_ref,
                        target_class=ctx.fqcn,
                        denominator_line_total=line_total,
                        reduced_src=reduced_src,
                        annotated_out=annotated_out,
                        test_deltas_csv=test_deltas,
                        line_deltas_csv=line_deltas,
                    ):
                        print(
                            f'[agt] annotation: wrote auto annotated test for repo="{ctx.repo}" '
                            f'fqcn="{ctx.fqcn}" -> {annotated_out}'
                        )

        for variant in ("adopted", "agentic"):
            if variant not in enabled:
                continue
            top_n = max(1, min(self.pipeline.args.adopted_reduce_max_tests, 100))
            reduced_src = reduced_variant_test_path(self.pipeline.adopted_reduced_out_root, variant, ctx.target_id, top_n)
            if reduced_src is None:
                reduced_src = _find_any_reduced_file(self.pipeline.adopted_reduced_out_root / variant / ctx.target_id)
            if not reduced_src or not reduced_src.exists():
                continue
            rel = _resolve_rel_path(
                reduced_src,
                [
                    self.pipeline.adopted_reduced_out_root / variant / ctx.target_id,
                    self.pipeline.adopted_reduced_out_root / ctx.target_id,
                ],
            )
            annotated_out = annotation_root / variant / ctx.target_id / rel

            cov_base = self.pipeline.adopted_covfilter_out_root / variant / ctx.target_id
            test_deltas = _find_existing_delta_csv(cov_base, ["test_deltas_kept.csv", "tests_deltas_kept.csv"])
            line_deltas = _find_existing_delta_csv(cov_base, ["line_deltas_kept.csv", "lines_deltas_kept.csv"])
            if not test_deltas or not line_deltas:
                continue
            line_total = _load_line_total_from_summaries(
                summary_csvs,
                repo=ctx.repo,
                fqcn=ctx.fqcn,
                variant=variant,
            )

            if _annotate_reduced_test(
                repo=ctx.repo,
                repo_root=repo_root,
                repo_ref=repo_ref,
                target_class=ctx.fqcn,
                denominator_line_total=line_total,
                reduced_src=reduced_src,
                annotated_out=annotated_out,
                test_deltas_csv=test_deltas,
                line_deltas_csv=line_deltas,
            ):
                print(
                    f'[agt] annotation: wrote {variant} annotated test for repo="{ctx.repo}" '
                    f'fqcn="{ctx.fqcn}" -> {annotated_out}'
                )

        return True
