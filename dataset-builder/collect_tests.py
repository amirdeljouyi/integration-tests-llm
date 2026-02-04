#!/usr/bin/env python3
from __future__ import annotations

import argparse
import csv
import os
import re
import shutil
from dataclasses import dataclass
from pathlib import Path
from typing import Iterable, List, Optional, Set, Tuple


# -----------------------------
# Helpers
# -----------------------------
def safe_repo(repo: str) -> str:
    return repo.replace("/", "_").strip()


def fqcn_to_path(fqcn: str) -> str:
    return fqcn.replace(".", "/")


def split_test_paths(s: str) -> List[str]:
    if not s:
        return []
    # Accept ; or | separated
    parts = re.split(r"[;|]", s)
    return [p.strip() for p in parts if p.strip()]


def copy_file(src: Path, dst_dir: Path) -> None:
    dst_dir.mkdir(parents=True, exist_ok=True)
    shutil.copy2(src, dst_dir / src.name)


def copy_tree_filtered(src_dir: Path, dst_dir: Path, exts: Tuple[str, ...]) -> List[Path]:
    """
    Copy files with selected extensions under src_dir to dst_dir, preserving relative structure.
    Returns list of copied destination paths.
    """
    copied: List[Path] = []
    if not src_dir.exists() or not src_dir.is_dir():
        return copied

    for p in src_dir.rglob("*"):
        if not p.is_file():
            continue
        if p.suffix.lower() not in exts:
            continue
        rel = p.relative_to(src_dir)
        out = dst_dir / rel
        out.parent.mkdir(parents=True, exist_ok=True)
        shutil.copy2(p, out)
        copied.append(out)
    return copied


def find_module_root(start: Path) -> Optional[Path]:
    """
    Walk upwards from 'start' until we find a Maven/Gradle module root.
    """
    cur = start.resolve()
    if cur.is_file():
        cur = cur.parent

    while True:
        if (cur / "pom.xml").is_file():
            return cur
        if (cur / "build.gradle").is_file() or (cur / "build.gradle.kts").is_file():
            return cur
        parent = cur.parent
        if parent == cur:
            return None
        cur = parent


# -----------------------------
# Collection: Generated
# -----------------------------
GEN_EXTS = (".java", ".kt", ".groovy")

def collect_generated_tests_for_fqcn(evosuite_root: Path, fqcn: str) -> List[Path]:
    """
    Collect EvoSuite tests for fqcn from evosuite_root/generated-tests*.
    Excludes *_scaffolding.java.
    Returns source file paths.
    """
    simple = fqcn.split(".")[-1]
    pkg_path = Path(fqcn_to_path(fqcn)).parent  # package directory

    sources: List[Path] = []
    roots = sorted([p for p in evosuite_root.glob("generated-tests*") if p.is_dir()])
    for root in roots:
        # First try likely package dir
        direct_dir = root / pkg_path
        candidates: List[Path] = []
        if direct_dir.is_dir():
            for ext in GEN_EXTS:
                candidates += list(direct_dir.glob(f"*{simple}*ESTest*{ext}"))
        # Fallback: global search
        if not candidates:
            for ext in GEN_EXTS:
                candidates += list(root.rglob(f"*{simple}*ESTest*{ext}"))

        for f in candidates:
            if not f.is_file():
                continue
            # Exclude scaffolding from counting/copying
            if f.name.endswith("_scaffolding.java") or f.name.endswith("_ESTest_scaffolding.java"):
                continue
            sources.append(f)

    # Deduplicate by full path
    seen: Set[str] = set()
    uniq: List[Path] = []
    for p in sources:
        key = str(p.resolve())
        if key in seen:
            continue
        seen.add(key)
        uniq.append(p)
    return uniq


# -----------------------------
# Collection: Manual
# -----------------------------
@dataclass
class ManualCollectResult:
    primary_files: List[Path]        # primary manual tests (from test_paths)
    context_files: List[Path]        # extra copied files (helpers/resources), not to be reported as manual_files


def collect_manual_for_row(
    repos_dir: Path,
    repo: str,
    fqcn: str,
    test_paths: str,
    manual_context: str,
) -> ManualCollectResult:
    """
    Primary = files referred in test_paths.
    Context depends on manual_context:
      - none: no extra
      - package: all *.java/*.kt/*.groovy in the same directory as each primary test
      - module: module's src/test/java + src/test/resources (nearest pom.xml or build.gradle(.kts))
    Returns source paths (not copied yet).
    """
    repo_safe = safe_repo(repo)
    repo_dir = repos_dir / repo_safe
    primary: List[Path] = []
    context: List[Path] = []

    if not repo_dir.is_dir():
        return ManualCollectResult(primary_files=[], context_files=[])

    for rel in split_test_paths(test_paths):
        src = repo_dir / rel
        if src.is_file():
            primary.append(src)
        elif src.is_dir():
            # If they give a directory, treat all files inside as primary
            for p in src.rglob("*"):
                if p.is_file() and p.suffix.lower() in GEN_EXTS:
                    primary.append(p)

    # Dedup primary
    seen: Set[str] = set()
    primary_uniq: List[Path] = []
    for p in primary:
        k = str(p.resolve())
        if k in seen:
            continue
        seen.add(k)
        primary_uniq.append(p)

    if manual_context == "none":
        return ManualCollectResult(primary_files=primary_uniq, context_files=[])

    if manual_context == "package":
        # copy sibling files in same folder as each primary test
        for p in primary_uniq:
            d = p.parent
            for sib in d.iterdir():
                if sib.is_file() and sib.suffix.lower() in GEN_EXTS:
                    if str(sib.resolve()) not in seen:
                        context.append(sib)
                        seen.add(str(sib.resolve()))
        return ManualCollectResult(primary_files=primary_uniq, context_files=context)

    if manual_context == "module":
        # For each primary test, find module root and add src/test/java + src/test/resources
        module_roots: Set[str] = set()
        for p in primary_uniq:
            mr = find_module_root(p)
            if mr:
                module_roots.add(str(mr.resolve()))

        for mr_s in sorted(module_roots):
            mr = Path(mr_s)
            test_java = mr / "src" / "test" / "java"
            test_res = mr / "src" / "test" / "resources"

            for base in (test_java,):
                if base.is_dir():
                    for f in base.rglob("*"):
                        if f.is_file() and f.suffix.lower() in GEN_EXTS:
                            k = str(f.resolve())
                            if k not in seen:
                                context.append(f)
                                seen.add(k)

            # resources: copy everything (not just code)
            if test_res.is_dir():
                for f in test_res.rglob("*"):
                    if f.is_file():
                        k = str(f.resolve())
                        if k not in seen:
                            context.append(f)
                            seen.add(k)

        return ManualCollectResult(primary_files=primary_uniq, context_files=context)

    raise ValueError(f"Unknown manual_context: {manual_context}")


# -----------------------------
# Main
# -----------------------------
def main() -> int:
    ap = argparse.ArgumentParser()
    ap.add_argument("--map", required=True, help="cut_to_fatjar_map.csv (must include repo,fqcn,test_paths)")
    ap.add_argument("--repos", default="./repos", help="Directory containing cloned repos by safe repo name")
    ap.add_argument("--evosuite-root", default="./result", help="Root containing generated-tests* folders")
    ap.add_argument("--out", default="./collected-tests", help="Output directory")
    ap.add_argument("--mode", choices=["both", "manual-only", "generated-only"], default="both")
    ap.add_argument("--manual-context", choices=["none", "package", "module"], default="module",
                    help="Extra manual context to copy to help compilation, without polluting manual_files")
    args = ap.parse_args()

    map_path = Path(args.map)
    repos_dir = Path(args.repos)
    evo_root = Path(args.evosuite_root)
    out_dir = Path(args.out)

    out_gen = out_dir / "generated"
    out_man = out_dir / "manual"
    out_logs = out_dir / "_logs"
    out_logs.mkdir(parents=True, exist_ok=True)
    out_gen.mkdir(parents=True, exist_ok=True)
    out_man.mkdir(parents=True, exist_ok=True)

    inv_csv = out_logs / "tests_inventory.csv"
    warn_log = out_logs / "warnings.log"
    warn_log.write_text("", encoding="utf-8")

    required_cols = {"repo", "fqcn", "test_paths"}
    with map_path.open("r", encoding="utf-8", newline="") as f:
        reader = csv.DictReader(f)
        header = reader.fieldnames or []
        missing = [c for c in required_cols if c not in header]
        if missing:
            raise SystemExit(f"[ERROR] map is missing columns {missing}. Header={header}")

        with inv_csv.open("w", encoding="utf-8", newline="") as outf:
            w = csv.writer(outf)
            # manual_files should contain ONLY primary manually-written tests
            w.writerow([
                "repo", "fqcn",
                "generated_count", "manual_count",
                "generated_files", "manual_files",
                "manual_context_count", "manual_context_files",
            ])

            for row in reader:
                repo = (row.get("repo") or "").strip()
                fqcn = (row.get("fqcn") or "").strip()
                test_paths = (row.get("test_paths") or "").strip()

                if not repo or not fqcn:
                    continue

                repo_safe = safe_repo(repo)

                # ---- generated ----
                gen_sources: List[Path] = []
                if args.mode in ("both", "generated-only"):
                    gen_sources = collect_generated_tests_for_fqcn(evo_root, fqcn)
                    dest_dir = out_gen / repo_safe / fqcn
                    for src in gen_sources:
                        copy_file(src, dest_dir)

                # List only basenames
                gen_names = sorted({p.name for p in gen_sources})
                gen_count = len(gen_names)

                if args.mode in ("both", "generated-only") and gen_count == 0:
                    warn_log.write_text(warn_log.read_text(encoding="utf-8") +
                                        f"[WARN] no generated tests found for {repo}:{fqcn}\n", encoding="utf-8")

                # ---- manual ----
                primary_names: List[str] = []
                context_names: List[str] = []

                if args.mode in ("both", "manual-only"):
                    res = collect_manual_for_row(
                        repos_dir=repos_dir,
                        repo=repo,
                        fqcn=fqcn,
                        test_paths=test_paths,
                        manual_context=args.manual_context,
                    )

                    dest_primary = out_man / repo_safe / fqcn / "primary"
                    dest_ctx = out_man / repo_safe / fqcn / "context"

                    # copy primary
                    for src in res.primary_files:
                        copy_file(src, dest_primary)
                    # copy context (may include resources, non-java)
                    for src in res.context_files:
                        if src.is_file():
                            # preserve structure only for module context resources
                            if args.manual_context == "module":
                                # try preserve relative from module root if possible
                                mr = find_module_root(src)
                                if mr:
                                    rel = src.relative_to(mr)
                                    outp = dest_ctx / rel
                                    outp.parent.mkdir(parents=True, exist_ok=True)
                                    shutil.copy2(src, outp)
                                    continue
                            copy_file(src, dest_ctx)

                    # inventory should list ONLY primary tests (basenames)
                    primary_names = sorted({p.name for p in res.primary_files})
                    context_names = sorted({p.name for p in res.context_files})

                    if not repos_dir.joinpath(repo_safe).is_dir():
                        warn_log.write_text(warn_log.read_text(encoding="utf-8") +
                                            f"[WARN] repo dir missing: {repos_dir / repo_safe}\n", encoding="utf-8")

                    if test_paths and not primary_names:
                        warn_log.write_text(warn_log.read_text(encoding="utf-8") +
                                            f"[WARN] no manual primary tests copied for {repo}:{fqcn} (test_paths={test_paths})\n",
                                            encoding="utf-8")
                    if not test_paths:
                        warn_log.write_text(warn_log.read_text(encoding="utf-8") +
                                            f"[WARN] empty test_paths for {repo}:{fqcn}\n", encoding="utf-8")

                w.writerow([
                    repo, fqcn,
                    gen_count, len(primary_names),
                    ";".join(gen_names), ";".join(primary_names),
                    len(context_names), ";".join(context_names),
                ])

    print(f"[DONE] Collected into: {out_dir}")
    print(f"[DONE] Inventory CSV: {inv_csv}")
    if warn_log.stat().st_size > 0:
        print(f"[INFO] Warnings: {warn_log}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())