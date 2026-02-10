from __future__ import annotations

import re
import subprocess
import time
from pathlib import Path
from typing import Optional, TYPE_CHECKING

import requests
from ..pipeline.helpers import first_test_source_for_fqcn, improved_test_path, reduced_test_path
from .base import Step

if TYPE_CHECKING:
    from ..pipeline.pipeline import TargetContext


def _output_class_name_from_path(path: Path, suffix: str) -> str:
    stem = path.stem
    if "_Top" in stem:
        stem = stem.split("_Top", 1)[0]
    if stem.endswith("_Improved"):
        stem = stem[: -len("_Improved")]
    if stem.endswith("_Adopted"):
        stem = stem[: -len("_Adopted")]
    return f"{stem}{suffix}"


def _rewrite_class_name(source: str, new_name: str) -> str:
    m = re.search(r"\bclass\s+([A-Za-z_][A-Za-z0-9_]*)\b", source)
    if not m:
        return source
    old = m.group(1)
    return re.sub(rf"\bclass\s+{re.escape(old)}\b", f"class {new_name}", source, count=1)


def _extract_package_name(source: str) -> str:
    m = re.search(r"^\s*package\s+([a-zA-Z0-9_.]+)\s*;", source, flags=re.MULTILINE)
    return m.group(1).strip() if m else ""


def _extract_class_name(source: str) -> Optional[str]:
    m = re.search(r"\bclass\s+([A-Za-z_][A-Za-z0-9_]*)\b", source)
    return m.group(1) if m else None


def write_adopted_output(*, out_root: Path, target_id: str, source: str) -> Optional[Path]:
    class_name = _extract_class_name(source)
    if not class_name:
        return None
    pkg = _extract_package_name(source)
    pkg_path = Path(*pkg.split(".")) if pkg else Path()
    out_dir = out_root / target_id / pkg_path
    out_dir.mkdir(parents=True, exist_ok=True)
    out_path = out_dir / f"{class_name}.java"
    out_path.write_text(source, encoding="utf-8", errors="ignore")
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
        return write_adopted_output(out_root=out_root, target_id=target_id, source=rewritten)


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
        prompt_type: str,
        output_suffix: str,
) -> Optional[Path]:
    return LlmSender(api_url).send_reduced_test_to_dir(
        agt_file_path=agt_file_path,
        mwt_file_path=mwt_file_path,
        out_root=out_root,
        target_id=target_id,
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
        generated_test_src = first_test_source_for_fqcn(ctx.final_sources, ctx.generated_test_fqcn)
        manual_test_src = first_test_source_for_fqcn(ctx.manual_sources or ctx.final_sources, ctx.manual_test_fqcn)
        reduced_src = reduced_test_path(reduced_root, ctx.target_id, generated_test_src, top_n) if generated_test_src else None
        improved_src = improved_test_path(self.pipeline.adopted_root, ctx.target_id)

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
                    prompt_type=prompt_type,
                    output_suffix=output_suffix,
                )
                if not out_path:
                    print(f'[agt] {phase_name}: FAIL (see {send_log})')
            time.sleep(max(0, self.pipeline.args.send_sleep_seconds))
        return True
