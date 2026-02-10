from __future__ import annotations

import csv
import fnmatch
import os
import re
import shlex
from dataclasses import dataclass
from pathlib import Path
from typing import Dict, Iterable, Iterator, List, Optional, Sequence, Set, Tuple


def write_text(p: Path, s: str) -> None:
    ensure_dir(p.parent)
    p.write_text(s, encoding="utf-8", errors="replace")

def shlex_join(args: Sequence[str]) -> str:
    """
    Python 3.8-compatible shlex.join().
    """
    return " ".join(shlex.quote(a) for a in args)


def ensure_dir(p: Path) -> None:
    p.mkdir(parents=True, exist_ok=True)


def repo_to_dir(repo: str) -> str:
    return repo.replace("/", "_").strip()


def split_list_field(s: str) -> List[str]:
    """
    Parse list-like fields that may be:
      - "A.java;B.java"
      - "A.java|B.java"
      - CSV-escaped with quotes
    """
    if s is None:
        return []
    s = str(s).strip().strip('"').strip()
    if not s or s.lower() == "null":
        return []
    parts = re.split(r"[;|]", s)
    return [p.strip() for p in parts if p and p.strip()]


def looks_like_scaffolding(name: str) -> bool:
    n = name.lower()
    return "scaffolding" in n


def is_probably_test_filename(name: str) -> bool:
    """
    Conservative filter: treat these as tests
    """
    base = name.lower()
    if base.endswith("test.java") or base.endswith("tests.java"):
        return True
    if base.endswith("it.java") or base.endswith("its.java"):
        return True
    if base.endswith("testcase.java") or base.endswith("testcases.java"):
        return True
    if base.endswith("_test.java") or base.endswith("_tests.java"):
        return True
    if base.endswith("_it.java") or base.endswith("_its.java"):
        return True
    return False


def read_csv_rows(path: Path) -> List[Dict[str, str]]:
    with path.open("r", encoding="utf-8", newline="") as f:
        reader = csv.DictReader(f)
        return [dict(r) for r in reader]


def parse_package_and_class(java_file: Path) -> Tuple[str, str]:
    """
    Very small parser: reads "package x;" and "public class Y" etc.
    Works for typical JUnit tests.
    """
    pkg = ""
    cls = ""
    try:
        text = java_file.read_text(encoding="utf-8", errors="ignore")
    except Exception:
        return "", ""

    m = re.search(r"^\s*package\s+([a-zA-Z0-9_.]+)\s*;", text, flags=re.MULTILINE)
    if m:
        pkg = m.group(1).strip()

    # class name = file basename minus .java is often fine,
    # but prefer an actual class declaration if possible.
    m2 = re.search(
        r"^\s*(public\s+)?(final\s+)?(class|interface|enum)\s+([A-Za-z_][A-Za-z0-9_]*)\b",
        text,
        flags=re.MULTILINE,
    )
    if m2:
        cls = m2.group(4).strip()
    else:
        cls = java_file.stem

    return pkg, cls


def detect_junit_version(java_file: Path) -> int:
    """
    Return 5 if JUnit5, else 4 (default).
    """
    try:
        text = java_file.read_text(encoding="utf-8", errors="ignore")
    except Exception:
        return 4

    if "org.junit.jupiter.api" in text or "org.junit.jupiter" in text:
        return 5
    if "org.junit.Test" in text or "org.junit.Assert" in text:
        return 4
    # fallback: JUnit4
    return 4


@dataclass(frozen=True)
class CutToFatjarRow:
    repo: str
    fqcn: str
    fatjar_path: str
    module_rel: str = ""


def load_cut_to_fatjar_map(csv_path: Path) -> Dict[Tuple[str, str], CutToFatjarRow]:
    rows = read_csv_rows(csv_path)
    out: Dict[Tuple[str, str], CutToFatjarRow] = {}

    for r in rows:
        repo = (r.get("repo", "") or "").strip().strip('"')
        fqcn = (r.get("fqcn", "") or "").strip().strip('"')
        fatjar = (r.get("fatjar", "") or r.get("fatjar_path", "") or "").strip().strip('"')
        module_rel = (r.get("module_rel", "") or "").strip().strip('"')
        if not repo or not fqcn:
            continue
        out[(repo, fqcn)] = CutToFatjarRow(repo=repo, fqcn=fqcn, fatjar_path=fatjar, module_rel=module_rel)

    return out


def find_test_source_root(repo_root: Path) -> Optional[Path]:
    """
    Best-effort: locate likely test root in a repo module.
    We do NOT use this to compile everything. Only for targeted dependency search.
    """
    # Common layouts:
    # - src/test/java
    # - <module>/src/test/java
    candidates = list(repo_root.rglob("src/test/java"))
    if not candidates:
        return None
    # choose shortest path deterministically
    candidates = sorted(candidates, key=lambda p: (len(str(p)), str(p)))
    return candidates[0]


def extract_missing_symbols_from_javac_log(log_text: str) -> Set[str]:
    """
    Extract class names from common javac errors like:
      cannot find symbol
        symbol:   class Foo
    """
    missing: Set[str] = set()
    # class symbols
    for m in re.finditer(r"symbol:\s+class\s+([A-Za-z_][A-Za-z0-9_]*)", log_text):
        missing.add(m.group(1))
    # sometimes: symbol:   variable Foo   (could be a class used as static holder)
    for m in re.finditer(r"symbol:\s+variable\s+([A-Za-z_][A-Za-z0-9_]*)", log_text):
        missing.add(m.group(1))
    return missing


def file_declares_type(java_path: Path, type_name: str) -> bool:
    """
    Check if a java file declares 'class/interface/enum type_name'.
    Lightweight text scan.
    """
    try:
        txt = java_path.read_text(encoding="utf-8", errors="ignore")
    except Exception:
        return False
    pat = rf"^\s*(public\s+)?(final\s+)?(class|interface|enum)\s+{re.escape(type_name)}\b"
    return re.search(pat, txt, flags=re.MULTILINE) is not None
