from __future__ import annotations

import os
from pathlib import Path
from typing import Dict, List, Optional, Set, Tuple

from ..core.common import is_probably_test_filename, looks_like_scaffolding, parse_package_and_class


def find_tests_in_bucket(bucket_root: Path, filenames: List[str]) -> List[Path]:
    """
    Resolve basenames by searching within a repo bucket directory.
    Works for both:
      - collected layout: ../manual/<repo_dir>/<fqcn>/SomeTest.java
      - repos layout:     ../repos/<repo_dir>/.../SomeTest.java (not recommended unless inventory is tight)
    """
    want = [f.strip() for f in filenames if f and f.strip() and f.strip().lower() != "null"]
    if not want or not bucket_root.exists():
        return []

    index: Dict[str, List[Path]] = {}
    for p in bucket_root.rglob("*.java"):
        index.setdefault(p.name, []).append(p)

    out: List[Path] = []
    for f in want:
        hits = index.get(f, [])
        if hits:
            out.append(sorted(hits)[0])
    return out


def expand_manual_sources(manual_test_files: List[Path]) -> List[Path]:
    """
    Keep scope tight:
      - Always include selected manual test file(s)
      - Include same-folder NON-test helpers (*.java that are not *Test.java / *IT.java etc)
    """
    out: List[Path] = []
    seen: Set[Path] = set()

    for tf in manual_test_files:
        if tf not in seen:
            seen.add(tf)
            out.append(tf)

        pkg_dir = tf.parent
        if not pkg_dir.exists():
            continue

        for p in pkg_dir.glob("*.java"):
            if p == tf:
                continue
            if looks_like_scaffolding(p.name):
                continue
            if is_probably_test_filename(p.name):
                continue
            if p not in seen:
                seen.add(p)
                out.append(p)

    return out


def looks_like_test_source(java_file: Path) -> bool:
    if is_probably_test_filename(java_file.name):
        return True

    try:
        text = java_file.read_text(encoding="utf-8", errors="ignore")
    except OSError:
        return False

    markers = (
        "@Test",
        "@ParameterizedTest",
        "@RepeatedTest",
        "@TestFactory",
        "@TestTemplate",
        "@RunWith(",
        "@ExtendWith(",
        " extends TestCase",
        "org.junit.Test",
        "org.junit.jupiter.api.Test",
    )
    return any(marker in text for marker in markers)


def generated_source_matches_target(java_file: Path, target_fqcn: str) -> bool:
    target_fqcn = (target_fqcn or "").strip()
    if not target_fqcn or "." not in target_fqcn:
        return True

    target_pkg, target_cls = target_fqcn.rsplit(".", 1)
    pkg, cls = parse_package_and_class(java_file)
    if not cls:
        return False

    normalized_cls = cls
    if normalized_cls.endswith("_ESTest_scaffolding"):
        normalized_cls = normalized_cls[: -len("_ESTest_scaffolding")]
    elif normalized_cls.endswith("_ESTest"):
        normalized_cls = normalized_cls[: -len("_ESTest")]

    if normalized_cls != target_cls:
        return False
    if pkg and pkg != target_pkg:
        return False
    return True


def first_test_fqcn_from_sources(sources: List[Path], *, prefer_estest: bool) -> Optional[str]:
    """
    Pick a single "representative" test fqcn from compiled sources.
    - prefer_estest=True: prefer *_ESTest.java
    - prefer_estest=False: prefer non-scaffolding and not *_ESTest_scaffolding.java
    """
    if not sources:
        return None

    def score(p: Path) -> Tuple[int, str]:
        name = p.name
        s = 100
        if looks_like_scaffolding(name):
            s += 1000
        if not looks_like_test_source(p):
            s += 500
        if prefer_estest:
            if name.endswith("_ESTest.java"):
                s -= 50
            if name.endswith("_ESTest_scaffolding.java"):
                s += 500
        else:
            if name.endswith("_ESTest.java"):
                s += 200
        return (s, name)

    for p in sorted(sources, key=score):
        pkg, cls = parse_package_and_class(p)
        if not cls:
            continue
        if "scaffolding" in cls.lower():
            continue
        return f"{pkg}.{cls}" if pkg else cls

    return None


def first_test_source_for_fqcn(sources: List[Path], fqcn: Optional[str]) -> Optional[Path]:
    if not fqcn:
        return None
    for p in sources:
        pkg, cls = parse_package_and_class(p)
        if not cls:
            continue
        cand = f"{pkg}.{cls}" if pkg else cls
        if cand == fqcn:
            return p
    return None


def test_fqcn_from_source(src: Path) -> Optional[str]:
    pkg, cls = parse_package_and_class(src)
    if not cls:
        return None
    return f"{pkg}.{cls}" if pkg else cls


def find_scaffolding_source(test_src: Path, candidate_sources: Optional[List[Path]] = None) -> Optional[Path]:
    stem = test_src.stem
    base = stem.split("_Top", 1)[0]
    if not base.endswith("_ESTest"):
        return None
    want_stem = f"{base}_scaffolding"
    direct = test_src.parent / f"{want_stem}.java"
    if direct.exists():
        return direct
    if candidate_sources:
        for p in candidate_sources:
            if p.stem == want_stem:
                return p
    return None


def fix_reduced_scaffolding_import(reduced_src: Path, scaffolding_src: Optional[Path]) -> bool:
    if not scaffolding_src or not reduced_src.exists():
        return False
    try:
        reduced_text = reduced_src.read_text(encoding="utf-8", errors="ignore")
    except Exception:
        return False

    scaf_pkg, scaf_cls = parse_package_and_class(scaffolding_src)
    if not scaf_cls:
        return False

    import_line = f"import {scaf_cls};"
    replacement = f"import {scaf_pkg}.{scaf_cls};" if scaf_pkg else ""

    lines = reduced_text.splitlines()
    new_lines: List[str] = []
    replaced = False
    for line in lines:
        if line.strip() == import_line:
            if replacement:
                new_lines.append(replacement)
            replaced = True
            continue
        new_lines.append(line)

    if not replaced:
        return False

    try:
        reduced_src.write_text("\n".join(new_lines) + "\n", encoding="utf-8")
    except Exception:
        return False
    return True


def reduced_test_path(
    reduced_root: Path,
    target_id: str,
    generated_src: Path,
    top_n: int,
    preferred_variants: Optional[Sequence[str]] = None,
) -> Optional[Path]:
    pkg, cls = parse_package_and_class(generated_src)
    if not cls:
        return None
    reduced_name = f"{cls}_Top{top_n}.java"
    bases: List[Path] = []
    seen_bases: set[Path] = set()

    for variant in preferred_variants or ():
        base = reduced_root / variant / target_id
        if base not in seen_bases:
            seen_bases.add(base)
            bases.append(base)

    for base in (
        reduced_root / "auto" / target_id,
        reduced_root / "auto-original" / target_id,
        reduced_root / target_id,
    ):
        if base in seen_bases:
            continue
        seen_bases.add(base)
        bases.append(base)

    for base in bases:
        cand = base / reduced_name
        if cand.exists():
            return cand
        matches = list(base.rglob(reduced_name)) if base.exists() else []
        if matches:
            return matches[0]
    return None


def reduced_variant_test_path(reduced_root: Path, variant: str, target_id: str, top_n: int) -> Optional[Path]:
    base = reduced_root / variant / target_id
    if not base.exists():
        return None
    matches = list(base.rglob(f"*_Top{top_n}.java"))
    return matches[0] if matches else None


def _llm_output_candidate_score(path: Path, *, adopted_root: Path, target_id: str, expected_fqcn: Optional[str]) -> Tuple[int, int, str]:
    score = 0
    rel = path
    try:
        rel = path.relative_to(adopted_root)
    except ValueError:
        pass

    top_dir = rel.parts[0] if rel.parts else ""
    if top_dir != target_id:
        score += 1000

    pkg, cls = parse_package_and_class(path)
    if expected_fqcn and "." in expected_fqcn:
        expected_pkg, expected_cls = expected_fqcn.rsplit(".", 1)
        if pkg == expected_pkg:
            score -= 100
        elif pkg:
            score += 25

        expected_test_prefix = f"{expected_cls}_ESTest"
        if cls.startswith(expected_test_prefix):
            score -= 50
        elif cls:
            score += 10

    return (score, len(rel.parts), str(path))


def _find_llm_output_path(
    adopted_root: Path,
    target_id: str,
    pattern: str,
    *,
    expected_fqcn: Optional[str] = None,
) -> Optional[Path]:
    candidates: List[Path] = []
    base = adopted_root / target_id
    if base.exists():
        candidates.extend(sorted(base.rglob(pattern)))
    if not candidates and adopted_root.exists():
        candidates.extend(sorted(adopted_root.rglob(pattern)))
    if not candidates:
        return None
    return min(
        candidates,
        key=lambda p: _llm_output_candidate_score(
            p,
            adopted_root=adopted_root,
            target_id=target_id,
            expected_fqcn=expected_fqcn,
        ),
    )


def adopted_test_path(adopted_root: Path, target_id: str, expected_fqcn: Optional[str] = None) -> Optional[Path]:
    return _find_llm_output_path(adopted_root, target_id, "*_Adopted.java", expected_fqcn=expected_fqcn)


def improved_test_path(adopted_root: Path, target_id: str, expected_fqcn: Optional[str] = None) -> Optional[Path]:
    return _find_llm_output_path(adopted_root, target_id, "*_Improved.java", expected_fqcn=expected_fqcn)


def agentic_test_path(adopted_root: Path, target_id: str, expected_fqcn: Optional[str] = None) -> Optional[Path]:
    return _find_llm_output_path(adopted_root, target_id, "*_Adopted_Agentic.java", expected_fqcn=expected_fqcn)


def step_by_step_test_path(adopted_root: Path, target_id: str, expected_fqcn: Optional[str] = None) -> Optional[Path]:
    return _find_llm_output_path(adopted_root, target_id, "*_Adopted_StepByStep.java", expected_fqcn=expected_fqcn)


def adopted_variants(adopted_root: Path, target_id: str, expected_fqcn: Optional[str] = None) -> List[Tuple[str, Path]]:
    variants: List[Tuple[str, Path]] = []
    adopted_src = adopted_test_path(adopted_root, target_id, expected_fqcn)
    if adopted_src and adopted_src.exists():
        variants.append(("adopted", adopted_src))
    agentic_src = agentic_test_path(adopted_root, target_id, expected_fqcn)
    if agentic_src and agentic_src.exists():
        variants.append(("agentic", agentic_src))
    return variants


def libs_dir_from_glob(libs_glob_cp: str) -> Path:
    """
    CoverageFilterApp wants a libs DIRECTORY arg (e.g., 'libs'), not 'libs/*'.
    We'll infer it from the left-most component if possible.
    """
    s = libs_glob_cp.strip()
    if s.endswith("/*"):
        return Path(s[:-2])
    if s.endswith("*"):
        # e.g. libs*
        return Path(s.rstrip("*").rstrip("/"))
    # if it's a direct dir or classpath string, fall back to 'libs'
    return Path("libs")


def _load_dotenv_manual(path: Path) -> None:
    try:
        lines = path.read_text(encoding="utf-8", errors="ignore").splitlines()
    except Exception:
        return
    for line in lines:
        raw = line.strip()
        if not raw or raw.startswith("#") or "=" not in raw:
            continue
        key, val = raw.split("=", 1)
        key = key.strip()
        val = val.strip().strip('"').strip("'")
        if key and key not in os.environ:
            os.environ[key] = val


def _load_dotenv_if_present() -> None:
    roots = [Path.cwd(), Path(__file__).resolve().parents[1]]
    checked = set()
    for root in roots:
        for parent in [root, *root.parents]:
            for name in ("local.env", "config.env"):
                dotenv_path = parent / name
                if dotenv_path in checked:
                    continue
                checked.add(dotenv_path)
                if not dotenv_path.exists():
                    continue
                try:
                    from dotenv import load_dotenv
                except ImportError:
                    _load_dotenv_manual(dotenv_path)
                    return
                load_dotenv(dotenv_path)
                return
