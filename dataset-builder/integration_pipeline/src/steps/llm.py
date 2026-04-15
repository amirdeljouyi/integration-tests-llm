from __future__ import annotations

import re
import subprocess
import time
from pathlib import Path
from typing import Optional, TYPE_CHECKING

import requests
from ..pipeline.helpers import (
    adopted_test_path,
    first_test_source_for_fqcn,
    improved_test_path,
    reduced_test_path,
    step_by_step_test_path,
)
from .base import Step

if TYPE_CHECKING:
    from ..pipeline.pipeline import TargetContext

_TOP_LEVEL_TYPE_RE = re.compile(
    r"^\s*(?:public\s+|protected\s+|private\s+|final\s+|abstract\s+|static\s+|sealed\s+|non-sealed\s+)*"
    r"(class|interface|enum|record|@interface)\s+([A-Za-z_][A-Za-z0-9_]*)\b",
    flags=re.MULTILINE,
)
_TEST_NAME_RE = re.compile(r"(?:_ESTest(?:_[A-Za-z0-9_]+)?|Test(?:s|Case)?|IT(?:s|Case)?)$")
_TEST_MARKERS = (
    "@Test",
    "@ParameterizedTest",
    "@RepeatedTest",
    "@TestFactory",
    "@TestTemplate",
    "@RunWith(",
    "@ExtendWith(",
)


def _output_class_name_from_path(path: Path, suffix: str) -> str:
    stem = path.stem
    if "_Top" in stem:
        stem = stem.split("_Top", 1)[0]
    if stem.endswith("_Improved"):
        stem = stem[: -len("_Improved")]
    if stem.endswith("_Adopted"):
        stem = stem[: -len("_Adopted")]
    return f"{stem}{suffix}"


def _mask_java_comments_and_strings(source: str) -> str:
    chars = list(source)
    i = 0
    in_line_comment = False
    in_block_comment = False
    in_single_quote = False
    in_double_quote = False
    in_text_block = False
    length = len(chars)

    while i < length:
        ch = source[i]
        nxt = source[i + 1] if i + 1 < length else ""
        tri = source[i : i + 3]

        if in_line_comment:
            if ch == "\n":
                in_line_comment = False
            else:
                chars[i] = " "
            i += 1
            continue

        if in_block_comment:
            if ch == "*" and nxt == "/":
                chars[i] = " "
                chars[i + 1] = " "
                in_block_comment = False
                i += 2
            else:
                if ch != "\n":
                    chars[i] = " "
                i += 1
            continue

        if in_text_block:
            if tri == '"""':
                chars[i] = " "
                chars[i + 1] = " "
                chars[i + 2] = " "
                in_text_block = False
                i += 3
            else:
                if ch != "\n":
                    chars[i] = " "
                i += 1
            continue

        if in_double_quote:
            chars[i] = " "
            if ch == "\\" and i + 1 < length:
                chars[i + 1] = " "
                i += 2
                continue
            if ch == '"':
                in_double_quote = False
            i += 1
            continue

        if in_single_quote:
            chars[i] = " "
            if ch == "\\" and i + 1 < length:
                chars[i + 1] = " "
                i += 2
                continue
            if ch == "'":
                in_single_quote = False
            i += 1
            continue

        if ch == "/" and nxt == "/":
            chars[i] = " "
            chars[i + 1] = " "
            in_line_comment = True
            i += 2
            continue
        if ch == "/" and nxt == "*":
            chars[i] = " "
            chars[i + 1] = " "
            in_block_comment = True
            i += 2
            continue
        if tri == '"""':
            chars[i] = " "
            chars[i + 1] = " "
            chars[i + 2] = " "
            in_text_block = True
            i += 3
            continue
        if ch == '"':
            chars[i] = " "
            in_double_quote = True
            i += 1
            continue
        if ch == "'":
            chars[i] = " "
            in_single_quote = True
            i += 1
            continue

        i += 1

    return "".join(chars)


def _top_level_type_declarations(source: str) -> list[tuple[int, int, str]]:
    masked = _mask_java_comments_and_strings(source)
    declarations: list[tuple[int, int, str]] = []
    brace_depth = 0
    offset = 0

    for line in masked.splitlines(keepends=True):
        if brace_depth == 0:
            match = _TOP_LEVEL_TYPE_RE.match(line)
            if match:
                declarations.append((offset + match.start(2), offset + match.end(2), match.group(2)))
        brace_depth += line.count("{") - line.count("}")
        offset += len(line)

    return declarations


def _find_matching_type_end(masked: str, start_index: int) -> int:
    open_brace = masked.find("{", start_index)
    if open_brace < 0:
        return len(masked)

    depth = 0
    for index in range(open_brace, len(masked)):
        ch = masked[index]
        if ch == "{":
            depth += 1
        elif ch == "}":
            depth -= 1
            if depth == 0:
                return index + 1
    return len(masked)


def _type_body(source: str, masked: str, declaration: tuple[int, int, str]) -> str:
    _start, end, _name = declaration
    return source[end:_find_matching_type_end(masked, end)]


def _has_test_markers(body: str) -> bool:
    return any(marker in body for marker in _TEST_MARKERS)


def _select_primary_type_index(source: str, declarations: list[tuple[int, int, str]], masked: str) -> int:
    primary_index = 0
    for index, (_start, end, name) in enumerate(declarations):
        body = _type_body(source, masked, declarations[index])
        if _has_test_markers(body):
            return index
        if primary_index == 0 and _TEST_NAME_RE.search(name):
            primary_index = index
    return primary_index


def _infer_helper_type_name(
    *,
    body: str,
    taken_names: set[str],
    fallback_base: str,
) -> str:
    patterns = (
        r"\bstatic\s+([A-Z][A-Za-z0-9_]*)\s+[A-Za-z_][A-Za-z0-9_]*\s*\(",
        r"\breturn\s+new\s+([A-Z][A-Za-z0-9_]*)\s*\(",
    )
    for pattern in patterns:
        for match in re.finditer(pattern, body):
            candidate = match.group(1)
            if candidate not in taken_names:
                return candidate

    candidate = f"{fallback_base}_Support"
    suffix = 2
    while candidate in taken_names:
        candidate = f"{fallback_base}_Support{suffix}"
        suffix += 1
    return candidate


def _namespaced_helper_type_name(primary_name: str, helper_name: str, taken_names: set[str]) -> str:
    candidate = f"{primary_name}_{helper_name}"
    suffix = 2
    while candidate in taken_names:
        candidate = f"{primary_name}_{helper_name}_{suffix}"
        suffix += 1
    return candidate


def _replace_identifier_outside_comments_and_strings(source: str, old_name: str, new_name: str) -> str:
    if old_name == new_name:
        return source

    masked = _mask_java_comments_and_strings(source)
    pattern = re.compile(rf"\b{re.escape(old_name)}\b")
    matches = list(pattern.finditer(masked))
    if not matches:
        return source

    rewritten = source
    for match in reversed(matches):
        start, end = match.span()
        rewritten = f"{rewritten[:start]}{new_name}{rewritten[end:]}"
    return rewritten


def _rewrite_primary_class_name(source: str, new_name: str) -> str:
    declarations = _top_level_type_declarations(source)
    if not declarations:
        match = re.search(r"\bclass\s+([A-Za-z_][A-Za-z0-9_]*)\b", source)
        if not match:
            return source
        old_name = match.group(1)
        return re.sub(rf"\bclass\s+{re.escape(old_name)}\b", f"class {new_name}", source, count=1)

    masked = _mask_java_comments_and_strings(source)
    primary_index = _select_primary_type_index(source, declarations, masked)
    primary_start, primary_end, primary_name = declarations[primary_index]
    primary_was_misaligned = primary_name != new_name

    replacements: list[tuple[int, int, str]] = []
    if primary_name != new_name:
        replacements.append((primary_start, primary_end, new_name))

    helper_reference_rewrites: list[tuple[str, str]] = []
    replacement_names = {new_name}
    for index, (start, end, name) in enumerate(declarations):
        if index == primary_index:
            continue
        body = _type_body(source, masked, declarations[index])
        should_rename_shadow = name == new_name
        should_fix_stale_test_name = primary_name == new_name and _TEST_NAME_RE.search(name) and not _has_test_markers(body)
        if not primary_was_misaligned and not should_rename_shadow and not should_fix_stale_test_name:
            replacement_names.add(name)
            continue
        inferred_name = _infer_helper_type_name(
            body=body,
            taken_names=replacement_names | {decl_name for _, _, decl_name in declarations},
            fallback_base=new_name,
        )
        replacement = _namespaced_helper_type_name(new_name, inferred_name, replacement_names)
        replacements.append((start, end, replacement))
        helper_reference_rewrites.append((name, replacement))
        replacement_names.add(replacement)

    if not replacements:
        return source
    rewritten = source
    for start, end, replacement in sorted(replacements, key=lambda item: item[0], reverse=True):
        rewritten = f"{rewritten[:start]}{replacement}{rewritten[end:]}"
    for old_name, replacement in helper_reference_rewrites:
        rewritten = _replace_identifier_outside_comments_and_strings(rewritten, old_name, replacement)
    return rewritten


def _rewrite_class_name(source: str, new_name: str) -> str:
    return _rewrite_primary_class_name(source, new_name)


def _extract_package_name(source: str) -> str:
    m = re.search(r"^\s*package\s+([a-zA-Z0-9_.]+)\s*;", source, flags=re.MULTILINE)
    return m.group(1).strip() if m else ""


def _extract_class_name(source: str) -> Optional[str]:
    declarations = _top_level_type_declarations(source)
    if declarations:
        masked = _mask_java_comments_and_strings(source)
        return declarations[_select_primary_type_index(source, declarations, masked)][2]

    match = re.search(r"\bclass\s+([A-Za-z_][A-Za-z0-9_]*)\b", source)
    return match.group(1) if match else None


def _package_from_fqcn(fqcn: Optional[str]) -> str:
    raw = (fqcn or "").strip()
    if "." not in raw:
        return ""
    return raw.rsplit(".", 1)[0]


def _rewrite_package_name(source: str, new_package: str) -> str:
    new_package = (new_package or "").strip()
    match = re.search(r"^(\s*package\s+)([a-zA-Z0-9_.]+)(\s*;)", source, flags=re.MULTILINE)
    if match:
        current_package = match.group(2).strip()
        if current_package == new_package:
            return source
        return f"{source[:match.start(2)]}{new_package}{source[match.end(2):]}"
    if not new_package:
        return source
    return f"package {new_package};\n\n{source.lstrip()}"


def normalize_primary_class_name_file(path: Path) -> bool:
    try:
        source = path.read_text(encoding="utf-8", errors="ignore")
    except OSError:
        return False

    rewritten = _rewrite_class_name(source, path.stem)
    if rewritten == source:
        return False

    path.write_text(rewritten, encoding="utf-8", errors="ignore")
    return True


def namespace_non_primary_type_name_file(path: Path, simple_name: str) -> bool:
    try:
        source = path.read_text(encoding="utf-8", errors="ignore")
    except OSError:
        return False

    declarations = _top_level_type_declarations(source)
    if not declarations:
        return False

    masked = _mask_java_comments_and_strings(source)
    primary_index = _select_primary_type_index(source, declarations, masked)
    replacement_names = {decl_name for _, _, decl_name in declarations}

    target_index = None
    for index, (_start, _end, name) in enumerate(declarations):
        if index == primary_index or name != simple_name:
            continue
        target_index = index
        break
    if target_index is None:
        return False

    start, end, name = declarations[target_index]
    replacement = _namespaced_helper_type_name(path.stem, name, replacement_names)
    rewritten = f"{source[:start]}{replacement}{source[end:]}"
    rewritten = _replace_identifier_outside_comments_and_strings(rewritten, name, replacement)
    if rewritten == source:
        return False

    path.write_text(rewritten, encoding="utf-8", errors="ignore")
    return True


def write_adopted_output(
    *,
    out_root: Path,
    target_id: str,
    source: str,
    target_fqcn: Optional[str] = None,
) -> Optional[Path]:
    class_name = _extract_class_name(source)
    if not class_name:
        return None

    canonical_pkg = _package_from_fqcn(target_fqcn) or _extract_package_name(source)
    rewritten = _rewrite_package_name(source, canonical_pkg) if canonical_pkg else source
    pkg = _extract_package_name(rewritten)
    pkg_path = Path(*pkg.split(".")) if pkg else Path()
    out_dir = out_root / target_id / pkg_path
    out_dir.mkdir(parents=True, exist_ok=True)
    out_path = out_dir / f"{class_name}.java"
    target_root = out_root / target_id
    if target_root.exists():
        for stale_path in target_root.rglob(f"{class_name}.java"):
            if stale_path == out_path:
                continue
            try:
                stale_path.unlink()
            except OSError:
                pass
    out_path.write_text(rewritten, encoding="utf-8", errors="ignore")
    return out_path


class LlmSender:
    def __init__(self, api_url: str) -> None:
        self.api_url = api_url

    @staticmethod
    def read_java_file(file_path: Path) -> str:
        return file_path.read_text(encoding="utf-8", errors="ignore")

    def send_to_strawberry_api(self, agt_code: str, mwt_code: str, *, prompt_type: str) -> Optional[str]:
        query = """
        query GenerateTestClass($prompt: Prompt!) {
            prompt(prompt: $prompt) {
                llmResponse
            }
        }
        """

        variables = {
                "prompt": {
                    "promptText": agt_code,
                "promptType": prompt_type,
                "additionalParam": mwt_code,
            }
        }

        try:
            response = requests.post(
                self.api_url,
                json={"query": query, "variables": variables},
                headers={"Content-Type": "application/json"},
                timeout=5000,
            )
            response.raise_for_status()
        except requests.RequestException:
            return None

        data = response.json()
        if "errors" in data:
            return None
        return data.get("data", {}).get("prompt", {}).get("llmResponse")

    def send_reduced_test(
            self,
            *,
            agt_file_path: Path,
            mwt_file_path: Optional[Path],
            out_path: Path,
            prompt_type: str,
            output_suffix: str,
    ) -> bool:
        agt_code = self.read_java_file(agt_file_path)
        mwt_code = self.read_java_file(mwt_file_path) if mwt_file_path else ""

        llm_response = self.send_to_strawberry_api(agt_code, mwt_code, prompt_type=prompt_type)
        if not llm_response:
            return False

        out_name = _output_class_name_from_path(agt_file_path, output_suffix)
        rewritten = _rewrite_class_name(llm_response, out_name)

        out_path.parent.mkdir(parents=True, exist_ok=True)
        out_path.write_text(rewritten, encoding="utf-8", errors="ignore")
        return True

    def send_reduced_test_to_dir(
            self,
            *,
            agt_file_path: Path,
            mwt_file_path: Optional[Path],
            out_root: Path,
            target_id: str,
            target_fqcn: Optional[str],
            prompt_type: str,
            output_suffix: str,
    ) -> Optional[Path]:
        agt_code = self.read_java_file(agt_file_path)
        mwt_code = self.read_java_file(mwt_file_path) if mwt_file_path else ""

        llm_response = self.send_to_strawberry_api(agt_code, mwt_code, prompt_type=prompt_type)
        if not llm_response:
            return None

        out_name = _output_class_name_from_path(agt_file_path, output_suffix)
        rewritten = _rewrite_class_name(llm_response, out_name)
        return write_adopted_output(out_root=out_root, target_id=target_id, source=rewritten, target_fqcn=target_fqcn)


def read_java_file(file_path: Path) -> str:
    return LlmSender.read_java_file(file_path)


def send_to_strawberry_api(api_url: str, agt_code: str, mwt_code: str, *, prompt_type: str) -> Optional[str]:
    return LlmSender(api_url).send_to_strawberry_api(agt_code, mwt_code, prompt_type=prompt_type)


def send_reduced_test(
        *,
        agt_file_path: Path,
        mwt_file_path: Optional[Path],
        api_url: str,
        out_path: Path,
        prompt_type: str,
        output_suffix: str,
) -> bool:
    return LlmSender(api_url).send_reduced_test(
        agt_file_path=agt_file_path,
        mwt_file_path=mwt_file_path,
        out_path=out_path,
        prompt_type=prompt_type,
        output_suffix=output_suffix,
    )


def send_reduced_test_to_dir(
        *,
        agt_file_path: Path,
        mwt_file_path: Optional[Path],
        api_url: str,
        out_root: Path,
        target_id: str,
        target_fqcn: Optional[str],
        prompt_type: str,
        output_suffix: str,
) -> Optional[Path]:
    return LlmSender(api_url).send_reduced_test_to_dir(
        agt_file_path=agt_file_path,
        mwt_file_path=mwt_file_path,
        out_root=out_root,
        target_id=target_id,
        target_fqcn=target_fqcn,
        prompt_type=prompt_type,
        output_suffix=output_suffix,
    )


class SendStep(Step):
    step_names = (
        "send",
        "llm-all",
        "llm-agt-improvement",
        "llm-integration",
        "llm-integration-step-by-step",
    )

    def run(self, ctx: "TargetContext") -> bool:
        if not self.should_run():
            return True
        send_script = Path(self.pipeline.args.send_script) if self.pipeline.args.send_script else None
        if self.pipeline.covfilter_allow is not None and (ctx.repo, ctx.fqcn) not in self.pipeline.covfilter_allow:
            print(f'[agt] send: Skip (agt_line_covered=0): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
            return True

        reduced_root = Path(self.pipeline.args.reduced_out)
        top_n = max(1, min(self.pipeline.args.reduce_max_tests, 100))
        auto_variant = self.pipeline.args.auto_variant
        generated_test_src = first_test_source_for_fqcn(ctx.final_sources, ctx.generated_test_fqcn)
        manual_test_src = first_test_source_for_fqcn(ctx.manual_sources or ctx.final_sources, ctx.manual_test_fqcn)
        reduced_src = (
            reduced_test_path(
                reduced_root,
                ctx.target_id,
                generated_test_src,
                top_n,
                preferred_variants=[auto_variant],
            )
            if generated_test_src
            else None
        )
        improved_src = improved_test_path(self.pipeline.adopted_root, ctx.target_id, ctx.fqcn)
        adopted_src = adopted_test_path(self.pipeline.adopted_root, ctx.target_id, ctx.fqcn)
        step_by_step_src = step_by_step_test_path(self.pipeline.adopted_root, ctx.target_id, ctx.fqcn)

        phases = []
        if self.pipeline.args.step in ("send", "llm-all", "all"):
            phases.append(("llm-all", "integration_all", "_Adopted", reduced_src))
        if self.pipeline.args.step == "llm-agt-improvement":
            phases.append(("llm-agt-improvement", "integration_improvement", "_Improved", reduced_src))
        if self.pipeline.args.step == "llm-integration":
            phases.append(("llm-integration", "integration_merge", "_Adopted", improved_src))
        if self.pipeline.args.step == "llm-integration-step-by-step":
            phases.append(("llm-integration-step-by-step", "integration_step_by_step", "_Adopted_StepByStep", improved_src))

        for phase_name, prompt_type, output_suffix, agt_src in phases:
            if phase_name == "llm-agt-improvement" and self.pipeline.args.skip_exists and improved_src and improved_src.exists():
                print(f'[agt] {phase_name}: Skip (existing improved output): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
                continue
            if phase_name == "llm-integration" and self.pipeline.args.skip_exists and adopted_src and adopted_src.exists():
                print(f'[agt] {phase_name}: Skip (existing integrated output): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
                continue
            if (
                phase_name == "llm-integration-step-by-step"
                and self.pipeline.args.skip_exists
                and step_by_step_src
                and step_by_step_src.exists()
            ):
                print(f'[agt] {phase_name}: Skip (existing step-by-step output): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
                continue
            if phase_name == "llm-integration" and not improved_src:
                print(f'[agt] {phase_name}: Skip (missing improved test): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
                continue
            if not agt_src or not agt_src.exists():
                print(f'[agt] {phase_name}: Skip (missing AGT test): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
                continue
            if phase_name != "llm-agt-improvement":
                if not manual_test_src or not manual_test_src.exists():
                    print(f'[agt] {phase_name}: Skip (missing manual test source): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
                    continue

            mwt_src = None if phase_name == "llm-agt-improvement" else manual_test_src

            send_log = self.pipeline.logs_dir / f"{ctx.target_id}.{phase_name}.send.log"
            llm_out_root = self.pipeline.adopted_root
            print(f'[agt] Sending {phase_name} to LLM: repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
            if send_script and send_script.exists():
                if mwt_src is None:
                    print(f'[agt] {phase_name}: Skip (send_script requires manual test): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
                    continue
                cmd = [
                    "python",
                    str(send_script),
                    "--api-url",
                    self.pipeline.args.send_api_url,
                    "--agt_file_path",
                    str(agt_src),
                    "--mwt_file_path",
                    str(mwt_src),
                ]
                proc = subprocess.run(cmd, stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
                send_log.write_text(proc.stdout or "", encoding="utf-8", errors="ignore")
                if proc.returncode != 0:
                    print(f'[agt] {phase_name}: FAIL (see {send_log})')
            else:
                out_path = send_reduced_test_to_dir(
                    agt_file_path=agt_src,
                    mwt_file_path=mwt_src,
                    api_url=self.pipeline.args.send_api_url,
                    out_root=llm_out_root,
                    target_id=ctx.target_id,
                    target_fqcn=ctx.fqcn,
                    prompt_type=prompt_type,
                    output_suffix=output_suffix,
                )
                if not out_path:
                    print(f'[agt] {phase_name}: FAIL (see {send_log})')
            time.sleep(max(0, self.pipeline.args.send_sleep_seconds))
        return True
