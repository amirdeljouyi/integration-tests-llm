from __future__ import annotations

import os
import shutil
import subprocess
from pathlib import Path
from typing import Iterable, List, Optional, Tuple, TYPE_CHECKING

from ..core.common import ensure_dir, parse_package_and_class, repo_to_dir
from ..pipeline.helpers import first_test_source_for_fqcn, improved_test_path
from .base import Step
from .llm import _output_class_name_from_path, _rewrite_class_name, write_adopted_output

if TYPE_CHECKING:
    from ..pipeline.pipeline import TargetContext


def _read_text(path: Path) -> str:
    return path.read_text(encoding="utf-8", errors="ignore")




def _collect_repo_context(
    repo_root: Path,
    package_name: str,
    *,
    max_files: int,
    max_chars: int,
) -> List[Tuple[Path, str]]:
    if not repo_root or not repo_root.exists():
        return []

    pkg_path = Path(*package_name.split(".")) if package_name else None
    roots = [
        repo_root / "src" / "main" / "java",
        repo_root / "src" / "test" / "java",
    ]

    candidates: List[Path] = []
    for root in roots:
        if not root.exists():
            continue
        if pkg_path:
            pkg_dir = root / pkg_path
            if pkg_dir.exists():
                candidates.extend(sorted(pkg_dir.rglob("*.java")))
        else:
            candidates.extend(sorted(root.rglob("*.java")))

    seen: set[Path] = set()
    out: List[Tuple[Path, str]] = []
    remaining = max_chars

    for p in candidates:
        if p in seen:
            continue
        seen.add(p)
        if len(out) >= max_files or remaining <= 0:
            break
        text = _read_text(p)
        if not text:
            continue
        if len(text) > remaining:
            text = text[:remaining]
        out.append((p, text))
        remaining -= len(text)

    return out


def _build_prompt(
    *,
    rules_text: str,
    improved_code: str,
    manual_code: str,
    repo_context: Iterable[Tuple[Path, str]],
) -> str:
    lines = [
        rules_text.strip(),
        "",
        "For this automated pipeline, output ONLY the full merged Java test code.",
        "Do not include a patch/diff or summary.",
        "",
    ]
    lines.extend(
        [
            "[MWT]",
            manual_code,
            "[/MWT]",
            "",
            "[IGT]",
            improved_code,
            "[/IGT]",
            "",
        ]
    )

    ctx = list(repo_context)
    if ctx:
        lines.append("Repo context (read-only, may be partial):")
        for path, text in ctx:
            lines.append(f"File: {path}")
            lines.append("```java")
            lines.append(text)
            lines.append("```")
            lines.append("")

    return "\n".join(lines)


def _truncate_text(text: str, max_chars: int) -> str:
    if max_chars <= 0 or len(text) <= max_chars:
        return text
    return text[:max_chars]


def _strip_code_fences(text: str) -> str:
    s = text.strip()
    lines = s.splitlines()
    if lines and lines[0].strip().startswith("```"):
        lines = lines[1:]
    if lines and lines[-1].strip() == "```":
        lines = lines[:-1]
    return "\n".join(lines).strip()


def _load_agent_rules() -> str:
    agents_path = Path(__file__).resolve().parents[1] / "AGENTS.md"
    if agents_path.exists():
        return _read_text(agents_path)
    return "Follow the repo conventions when merging [MWT] and [IGT]."


def run_codex_integration(
    *,
    model: str,
    improved_test_path: Path,
    manual_test_path: Path,
    repo_root: Optional[Path],
    out_root: Path,
    target_id: str,
    max_context_files: int,
    max_context_chars: int,
    max_prompt_chars: int,
    log_file: Optional[Path] = None,
) -> Optional[Path]:
    def read_log() -> str:
        if not log_file or not log_file.exists():
            return ""
        return log_file.read_text(encoding="utf-8", errors="ignore")

    def write_log(msg: str) -> None:
        if not log_file:
            return
        ensure_dir(log_file.parent)
        log_file.write_text(msg, encoding="utf-8", errors="ignore")

    codex_bin = shutil.which("codex")
    if not codex_bin:
        write_log("Missing codex CLI in PATH.\n")
        return None

    improved_code = _read_text(improved_test_path)
    manual_code = _read_text(manual_test_path)
    if not improved_code or not manual_code:
        write_log("Missing improved or manual test source.\n")
        return None

    pkg, _cls = parse_package_and_class(manual_test_path)
    repo_ctx = _collect_repo_context(
        repo_root if repo_root else Path(),
        pkg,
        max_files=max_context_files,
        max_chars=max_context_chars,
    )

    rules_text = _load_agent_rules()
    prompt = _build_prompt(
        rules_text=rules_text,
        improved_code=improved_code,
        manual_code=manual_code,
        repo_context=repo_ctx,
    )
    if max_prompt_chars > 0 and len(prompt) > max_prompt_chars:
        # Trim rules and inputs to fit the prompt budget.
        keep_rules = max(0, min(len(rules_text), 8000))
        keep_manual = max(0, min(len(manual_code), max_prompt_chars // 2))
        keep_improved = max(0, min(len(improved_code), max_prompt_chars // 2))
        rules_trim = _truncate_text(rules_text, keep_rules)
        manual_trim = _truncate_text(manual_code, keep_manual)
        improved_trim = _truncate_text(improved_code, keep_improved)
        prompt = _build_prompt(
            rules_text=rules_trim,
            improved_code=improved_trim,
            manual_code=manual_trim,
            repo_context=[],
        )
        write_log(
            read_log()
            + f"prompt trimmed: rules={keep_rules} manual={keep_manual} improved={keep_improved}\n"
        )
    write_log(
        "agent request\n"
        f"model={model or '<cli-default>'}\n"
        f"improved_test={improved_test_path}\n"
        f"manual_test={manual_test_path}\n"
        f"repo_root={repo_root}\n"
        f"context_files={len(repo_ctx)}\n"
        f"prompt_chars={len(prompt)}\n"
    )

    output_file = log_file.with_suffix(".codex.out") if log_file else Path("codex_last_message.txt")
    cmd = [codex_bin, "exec"]
    if model:
        cmd.extend(["--model", model])
    cmd.extend(["--output-last-message", str(output_file), "-"])
    if repo_root and repo_root.exists():
        cmd.extend(["-C", str(repo_root)])

    write_log(read_log() + "codex cmd: " + " ".join(cmd) + "\n")
    proc = subprocess.run(
        cmd,
        input=prompt,
        text=True,
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
    )
    write_log(read_log() + f"codex exit={proc.returncode}\nstdout:\n{proc.stdout or ''}\n")
    if proc.returncode != 0:
        return None

    if not output_file.exists():
        write_log(read_log() + "missing codex output file\n")
        return None

    text = output_file.read_text(encoding="utf-8", errors="ignore").strip()
    if not text:
        write_log(read_log() + "codex output file empty\n")
        return None
    text = _strip_code_fences(text)
    if not text:
        write_log(read_log() + "codex output empty after stripping fences\n")
        return None

    out_name = _output_class_name_from_path(improved_test_path, "_Adopted_Agentic")
    rewritten = _rewrite_class_name(text, out_name)
    ensure_dir(out_root)
    return write_adopted_output(out_root=out_root, target_id=target_id, source=rewritten)


class AgentStep(Step):
    step_names = ("agent",)

    def run(self, ctx: "TargetContext") -> bool:
        if not self.should_run():
            return True
        if self.pipeline.covfilter_allow is not None and (ctx.repo, ctx.fqcn) not in self.pipeline.covfilter_allow:
            print(f'[agt] agent: Skip (agt_line_covered=0): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
            return True
        if not shutil.which("codex"):
            print(f'[agt] agent: Skip (missing codex CLI): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
            return True

        improved_src = improved_test_path(self.pipeline.adopted_root, ctx.target_id)
        manual_test_src = first_test_source_for_fqcn(ctx.manual_sources or ctx.final_sources, ctx.manual_test_fqcn)
        if not improved_src or not improved_src.exists():
            print(f'[agt] agent: Skip (missing improved test): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
            return True
        if not manual_test_src or not manual_test_src.exists():
            print(f'[agt] agent: Skip (missing manual test source): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
            return True

        repo_root = self.pipeline.repos_dir / repo_to_dir(ctx.repo)
        out_path = run_codex_integration(
            model=self.pipeline.args.agent_model,
            improved_test_path=improved_src,
            manual_test_path=manual_test_src,
            repo_root=repo_root if repo_root.exists() else None,
            out_root=self.pipeline.adopted_root,
            target_id=ctx.target_id,
            max_context_files=self.pipeline.args.agent_max_context_files,
            max_context_chars=self.pipeline.args.agent_max_context_chars,
            max_prompt_chars=self.pipeline.args.agent_max_prompt_chars,
            log_file=self.pipeline.logs_dir / f"{ctx.target_id}.agent.log",
        )
        if not out_path:
            print(f'[agt] agent: FAIL (no output): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
        return True
