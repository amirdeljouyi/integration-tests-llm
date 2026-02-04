from __future__ import annotations

import subprocess
import xml.etree.ElementTree as ET
from dataclasses import dataclass
from typing import Dict, List


@dataclass
class CheckstyleResult:
    violations: int


@dataclass
class PMDResult:
    violations: int
    by_rule: Dict[str, int]


@dataclass
class CPDResult:
    duplicate_blocks: int
    duplicate_lines_total: int


def run_cmd(cmd: List[str]) -> str:
    """
    Runs a command and returns stdout.
    PMD/CPD sometimes return non-zero when violations/duplications exist,
    so we accept 0 or 1. Everything else is treated as failure.
    """
    p = subprocess.run(cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)

    if p.returncode not in (0, 1):
        raise RuntimeError(
            f"Command failed ({p.returncode}): {' '.join(cmd)}\n"
            f"STDERR:\n{p.stderr}\n"
            f"STDOUT:\n{p.stdout}\n"
        )
    return p.stdout


def run_pmd(pmd_bin: str, ruleset: str, java_file_or_dir: str) -> PMDResult:
    """
    PMD 7 CLI:
      pmd check -d <path> -R <ruleset> -f xml
    """
    xml_out = run_cmd([pmd_bin, "check", "-d", java_file_or_dir, "-R", ruleset, "-f", "xml"])

    # PMD sometimes prints non-XML warnings to stderr, but stdout should be XML.
    root = ET.fromstring(xml_out)

    violations = 0
    by_rule: Dict[str, int] = {}

    for v in root.findall(".//violation"):
        violations += 1
        rule = v.attrib.get("rule", "UNKNOWN")
        by_rule[rule] = by_rule.get(rule, 0) + 1

    return PMDResult(violations=violations, by_rule=by_rule)


def run_cpd_pmd7(
    pmd_bin: str,
    language: str,
    files_dir: str,
    minimum_tokens: int = 50,
) -> CPDResult:
    """
    PMD 7 CPD is a subcommand:
      pmd cpd --language java --minimum-tokens 50 --files <dir> --format xml

    Note: CPD should usually run on a DIRECTORY (so it can find duplicates across files).
    """
    xml_out = run_cmd([
        pmd_bin,
        "cpd",
        "--language", language,
        "--minimum-tokens", str(minimum_tokens),
        "--files", files_dir,
        "--format", "xml",
    ])

    root = ET.fromstring(xml_out)
    dups = root.findall(".//duplication")

    duplicate_blocks = len(dups)
    duplicate_lines_total = 0

    for d in dups:
        duplicate_lines_total += int(d.attrib.get("lines", "0"))

    return CPDResult(
        duplicate_blocks=duplicate_blocks,
        duplicate_lines_total=duplicate_lines_total,
    )

PMD_BIN = "tools/pmd/pmd-bin-7.0.0/bin/pmd"
PMD_RULESET = "configs/pmd-ruleset.xml"

# PMD per file
pmd_auto = run_pmd(PMD_BIN, PMD_RULESET, "tests/generated.java")
pmd_adopted = run_pmd(PMD_BIN, PMD_RULESET, "tests/adopted.java")

# CPD on directory (recommended)
cpd_all = run_cpd_pmd7(PMD_BIN, "java", "tests/", minimum_tokens=50)

print(cpd_all)
print(pmd_auto)
print(pmd_auto)