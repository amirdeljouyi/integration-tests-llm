import xml.etree.ElementTree as ET
from dataclasses import dataclass
from typing import Dict, List
import subprocess
import os
import tempfile
import subprocess
from pathlib import Path

PMD_BIN = "tools/pmd/pmd-bin-7.0.0/bin/pmd"

@dataclass
class CPDFileScore:
    dup_lines: int
    total_non_empty_lines: int
    dup_lines_ratio: float
    dup_score: float


def run_cmd(cmd: List[str]) -> str:
    p = subprocess.run(cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)

    # PMD/CPD return codes:
    # 0 = ok/no issues
    # 1 = issues/violations found (common)
    # 4 = CPD duplications found (PMD 7 behavior)
    if p.returncode not in (0, 1, 4):
        raise RuntimeError(
            f"CPD/PMD failed {p.returncode}\nSTDERR:\n{p.stderr}\nSTDOUT:\n{p.stdout}"
        )

    if not p.stdout.strip():
        raise RuntimeError(f"Tool produced no stdout.\nSTDERR:\n{p.stderr}")

    return p.stdout


def count_non_empty_lines(path: str) -> int:
    with open(path, "r", encoding="utf-8") as f:
        return sum(1 for line in f if line.strip())


def cpd_scores_pmd7(
    pmd_bin: str,
    java_files: List[str],
    *,
    language: str = "java",
    minimum_tokens: int = 50,
) -> Dict[str, CPDFileScore]:
    """
    PMD 7 CPD implementation.
    Runs CPD on the given list of Java files and returns per-file scores.
    """
    # 1) Write file-list
    with tempfile.NamedTemporaryFile(mode="w", delete=False) as fl:
        for f in java_files:
            fl.write(str(Path(f).resolve()) + "\n")
        file_list_path = fl.name

    # 2) Run CPD
    xml_out = run_cmd([
        pmd_bin,
        "cpd",
        "--language", language,
        "--minimum-tokens", str(minimum_tokens),
        "--file-list", file_list_path,
        "--format", "xml",
    ])

    root = ET.fromstring(xml_out)

    # 3) Accumulate duplicated lines per file
    dup_lines_by_file: Dict[str, int] = {}
    for dup in root.findall(".//duplication"):
        lines = int(dup.attrib.get("lines", "0"))
        for fe in dup.findall("file"):
            path = fe.attrib.get("path")
            if path:
                dup_lines_by_file[path] = dup_lines_by_file.get(path, 0) + lines

    # 4) Build scores
    scores: Dict[str, CPDFileScore] = {}
    for f in java_files:
        fpath = str(Path(f).resolve())
        total = count_non_empty_lines(fpath)
        dup = dup_lines_by_file.get(fpath, 0)
        ratio = (dup / total) if total > 0 else 0.0
        ratio = min(ratio, 1.0)

        scores[fpath] = CPDFileScore(
            dup_lines=dup,
            total_non_empty_lines=total,
            dup_lines_ratio=ratio,
            dup_score=1.0 - ratio,
        )

    return scores

pmd_auto = cpd_scores_pmd7(
    PMD_BIN,
    java_files=[
        "tests/generated.java",
        "tests/adopted.java",
    ],
    minimum_tokens=50,
)# pmd_adopted = cpd_scores_pmd7(PMD_BIN, "tests/adopted.java")

print(pmd_auto)
print(pmd_auto)