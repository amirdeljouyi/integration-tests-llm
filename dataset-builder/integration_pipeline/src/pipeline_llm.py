from __future__ import annotations

import re
from pathlib import Path
from typing import Optional

import requests


def _adopted_class_name_from_path(path: Path) -> str:
    stem = path.stem
    if "_Top" in stem:
        stem = stem.split("_Top", 1)[0]
    return f"{stem}_Adopted"


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

    def send_to_strawberry_api(self, agt_code: str, mwt_code: str) -> Optional[str]:
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
                "promptType": "integration_improvement",
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
            mwt_file_path: Path,
            out_path: Path,
    ) -> bool:
        agt_code = self.read_java_file(agt_file_path)
        mwt_code = self.read_java_file(mwt_file_path)

        llm_response = self.send_to_strawberry_api(agt_code, mwt_code)
        if not llm_response:
            return False

        adopted_name = _adopted_class_name_from_path(agt_file_path)
        rewritten = _rewrite_class_name(llm_response, adopted_name)

        out_path.parent.mkdir(parents=True, exist_ok=True)
        out_path.write_text(rewritten, encoding="utf-8", errors="ignore")
        return True

    def send_reduced_test_to_dir(
            self,
            *,
            agt_file_path: Path,
            mwt_file_path: Path,
            out_root: Path,
            target_id: str,
    ) -> Optional[Path]:
        agt_code = self.read_java_file(agt_file_path)
        mwt_code = self.read_java_file(mwt_file_path)

        llm_response = self.send_to_strawberry_api(agt_code, mwt_code)
        if not llm_response:
            return None

        adopted_name = _adopted_class_name_from_path(agt_file_path)
        rewritten = _rewrite_class_name(llm_response, adopted_name)
        return write_adopted_output(out_root=out_root, target_id=target_id, source=rewritten)


def read_java_file(file_path: Path) -> str:
    return LlmSender.read_java_file(file_path)


def send_to_strawberry_api(api_url: str, agt_code: str, mwt_code: str) -> Optional[str]:
    return LlmSender(api_url).send_to_strawberry_api(agt_code, mwt_code)


def send_reduced_test(
        *,
        agt_file_path: Path,
        mwt_file_path: Path,
        api_url: str,
        out_path: Path,
) -> bool:
    return LlmSender(api_url).send_reduced_test(
        agt_file_path=agt_file_path,
        mwt_file_path=mwt_file_path,
        out_path=out_path,
    )


def send_reduced_test_to_dir(
        *,
        agt_file_path: Path,
        mwt_file_path: Path,
        api_url: str,
        out_root: Path,
        target_id: str,
) -> Optional[Path]:
    return LlmSender(api_url).send_reduced_test_to_dir(
        agt_file_path=agt_file_path,
        mwt_file_path=mwt_file_path,
        out_root=out_root,
        target_id=target_id,
    )
