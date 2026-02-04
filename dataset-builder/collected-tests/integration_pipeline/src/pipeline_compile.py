from __future__ import annotations

import subprocess
from pathlib import Path
from typing import Iterable, List, Optional, Sequence, Set, Tuple

from .pipeline_common import (
    ensure_dir,
    extract_missing_symbols_from_javac_log,
    file_declares_type,
    is_probably_test_filename,
    looks_like_scaffolding,
    shlex_join,
)


class JavacCompiler:
    def __init__(self, *, libs_glob_cp: str, sut_jar: Path) -> None:
        self.libs_glob_cp = libs_glob_cp
        self.sut_jar = sut_jar

    def _javac_cmd(self, java_files: Sequence[Path], build_dir: Path) -> List[str]:
        cp = f"{self.libs_glob_cp}:{self.sut_jar}"
        cmd = ["javac", "-g", "-cp", cp, "-d", str(build_dir)]
        cmd.extend([str(p) for p in java_files])
        return cmd

    def compile_set(
        self,
        *,
        java_files: Sequence[Path],
        build_dir: Path,
        log_file: Path,
    ) -> Tuple[bool, str]:
        ensure_dir(build_dir.parent if build_dir.name else build_dir)
        ensure_dir(log_file.parent)

        cmd = self._javac_cmd(java_files, build_dir)

        proc = subprocess.run(
            cmd,
            stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT,
            text=True,
        )

        out = proc.stdout or ""
        log_file.write_text(
            f"[agt] javac cmd:\n{shlex_join(cmd)}\n\n[agt] output:\n{out}\n",
            encoding="utf-8",
            errors="ignore",
        )

        tail = "\n".join(out.splitlines()[-60:])
        return (proc.returncode == 0), tail

    def compile_smart(
        self,
        *,
        java_files: Sequence[Path],
        build_dir: Path,
        log_file: Path,
        repo_root_for_deps: Optional[Path] = None,
        max_rounds: int = 3,
    ) -> Tuple[bool, str, List[Path]]:
        sources: List[Path] = list(java_files)
        ok, tail = self.compile_set(java_files=sources, build_dir=build_dir, log_file=log_file)
        if ok:
            return True, tail, sources

        if not repo_root_for_deps or not repo_root_for_deps.exists():
            return False, tail, sources

        full_log = ""
        try:
            full_log = log_file.read_text(encoding="utf-8", errors="ignore")
        except Exception:
            full_log = ""

        candidates = _index_candidate_java_files(repo_root_for_deps)

        for _round in range(max_rounds):
            missing = extract_missing_symbols_from_javac_log(full_log)
            if not missing:
                break

            new_srcs = _find_declaring_sources(candidates, missing)
            if not new_srcs:
                break

            filtered: List[Path] = []
            for p in new_srcs:
                if looks_like_scaffolding(p.name):
                    filtered.append(p)
                    continue
                if is_probably_test_filename(p.name):
                    continue
                filtered.append(p)

            if not filtered:
                break

            add_count = 0
            for p in filtered:
                if p not in sources:
                    sources.append(p)
                    add_count += 1
            if add_count == 0:
                break

            ok, tail = self.compile_set(java_files=sources, build_dir=build_dir, log_file=log_file)
            if ok:
                return True, tail, sources

            try:
                full_log = log_file.read_text(encoding="utf-8", errors="ignore")
            except Exception:
                full_log = ""

        return False, tail, sources


def compile_test_set(
    java_files: Sequence[Path],
    build_dir: Path,
    libs_glob_cp: str,
    sut_jar: Path,
    log_file: Path,
) -> Tuple[bool, str]:
    """
    Compile exactly the provided java_files.
    Returns (ok, tail_of_log).
    """
    return JavacCompiler(libs_glob_cp=libs_glob_cp, sut_jar=sut_jar).compile_set(
        java_files=java_files,
        build_dir=build_dir,
        log_file=log_file,
    )


def _index_candidate_java_files(repo_root: Path) -> List[Path]:
    """
    Candidate pool for dependency auto-inclusion.
    We keep it relatively tight:
      - only *.java under repo_root
      - ignore build dirs
    """
    if not repo_root.exists():
        return []
    bad_parts = {"target", "build", ".gradle", ".mvn", ".git", "out", ".idea"}
    out: List[Path] = []
    for p in repo_root.rglob("*.java"):
        parts = set(p.parts)
        if parts & bad_parts:
            continue
        out.append(p)
    return out


def _find_declaring_sources(
    candidates: List[Path],
    missing: Set[str],
) -> List[Path]:
    found: List[Path] = []
    # quick heuristic: matching basename first
    by_name = {p.stem: [] for p in candidates}
    for p in candidates:
        by_name.setdefault(p.stem, []).append(p)

    for sym in sorted(missing):
        # If sym matches file name, test that first
        for p in sorted(by_name.get(sym, [])):
            if file_declares_type(p, sym):
                found.append(p)
                break
        else:
            # fallback: scan a limited number of candidates
            for p in candidates:
                if file_declares_type(p, sym):
                    found.append(p)
                    break

    # de-dupe preserving order
    uniq: List[Path] = []
    seen: Set[Path] = set()
    for p in found:
        if p not in seen:
            seen.add(p)
            uniq.append(p)
    return uniq


def compile_test_set_smart(
    java_files: Sequence[Path],
    build_dir: Path,
    libs_glob_cp: str,
    sut_jar: Path,
    log_file: Path,
    repo_root_for_deps: Optional[Path] = None,
    max_rounds: int = 3,
) -> Tuple[bool, str, List[Path]]:
    """
    Smart compile:
      Round 1: compile given sources only
      If fails: parse missing symbols, find declaring sources in repo_root_for_deps,
               add them (prefer non-test helpers), retry.

    Returns: (ok, tail, final_sources)
    """
    return JavacCompiler(libs_glob_cp=libs_glob_cp, sut_jar=sut_jar).compile_smart(
        java_files=java_files,
        build_dir=build_dir,
        log_file=log_file,
        repo_root_for_deps=repo_root_for_deps,
        max_rounds=max_rounds,
    )
