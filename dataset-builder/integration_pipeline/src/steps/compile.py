from __future__ import annotations

import shutil
import subprocess
import tempfile
from pathlib import Path
from typing import Iterable, List, Optional, Sequence, Set, Tuple, TYPE_CHECKING

from ..core.common import (
    ensure_dir,
    extract_missing_symbols_from_javac_log,
    file_declares_type,
    looks_like_scaffolding,
    shlex_join,
)
from .base import Step

if TYPE_CHECKING:
    from ..pipeline.pipeline import TargetContext


class JavacCompiler:
    def __init__(self, *, libs_glob_cp: str, sut_jar: Path, extra_cp: str = "") -> None:
        self.libs_glob_cp = libs_glob_cp
        self.sut_jar = sut_jar
        self.extra_cp = extra_cp

    def _javac_cmd(self, java_files: Sequence[Path], build_dir: Path) -> List[str]:
        cp_parts = [self.libs_glob_cp, str(self.sut_jar)]
        if self.extra_cp.strip():
            cp_parts.append(self.extra_cp.strip())
        cp = ":".join(cp_parts)
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
        module_rel: str = "",
        build_tool: str = "",
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

        extra_cp = resolve_repo_runtime_classpath(
            repo_root=repo_root_for_deps,
            module_rel=module_rel,
            build_tool=build_tool,
        )
        if extra_cp:
            self.extra_cp = extra_cp
            ok, tail = self.compile_set(java_files=sources, build_dir=build_dir, log_file=log_file)
            if ok:
                return True, tail, sources
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
    module_rel: str = "",
    build_tool: str = "",
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
        module_rel=module_rel,
        build_tool=build_tool,
        max_rounds=max_rounds,
    )


_RUNTIME_CP_CACHE: dict[tuple[str, str, str], str] = {}


def _norm_module_rel(module_rel: str) -> str:
    s = (module_rel or "").strip()
    if not s or s in {".", "root"}:
        return ""
    return s


def _detect_build_tool(repo_root: Path) -> str:
    if (repo_root / "pom.xml").exists():
        return "maven"
    if (repo_root / "build.gradle").exists() or (repo_root / "build.gradle.kts").exists() or (repo_root / "gradlew").exists():
        return "gradle"
    return ""


def _module_dir(repo_root: Path, module_rel: str) -> Path:
    rel = _norm_module_rel(module_rel)
    if not rel:
        return repo_root
    return repo_root / rel


def _maven_runtime_cp(repo_root: Path, module_rel: str) -> str:
    module_dir = _module_dir(repo_root, module_rel)
    if not module_dir.exists():
        return ""

    # Try module-first with test scope; this is usually enough for unit test sources.
    with tempfile.NamedTemporaryFile(prefix="agt-mvn-cp-", delete=False) as f:
        outcp = Path(f.name)
    try:
        cmd = [
            "mvn",
            "-q",
            "-DskipTests",
            "dependency:build-classpath",
            "-Dmdep.pathSeparator=:",
            f"-Dmdep.outputFile={outcp}",
            "-Dmdep.includeScope=test",
        ]
        proc = subprocess.run(cmd, cwd=module_dir, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
        if proc.returncode == 0 and outcp.exists():
            return outcp.read_text(encoding="utf-8", errors="ignore").strip()
        return ""
    finally:
        outcp.unlink(missing_ok=True)


def _gradle_runtime_cp(repo_root: Path, module_rel: str) -> str:
    module_dir = _module_dir(repo_root, module_rel)
    if not module_dir.exists():
        return ""

    with tempfile.NamedTemporaryFile(prefix="agt-gradle-init-", suffix=".gradle", mode="w", delete=False) as f:
        f.write(
            """
allprojects { p ->
  p.tasks.register("printAgtClasspath") {
    doLast {
      def names = [
        "testCompileClasspath",
        "testRuntimeClasspath",
        "compileClasspath",
        "runtimeClasspath",
        "testImplementation",
        "implementation",
        "testRuntimeOnly",
        "runtimeOnly"
      ]
      def files = [] as LinkedHashSet<File>
      names.each { n ->
        def cfg = p.configurations.findByName(n)
        if (cfg != null && cfg.canBeResolved) {
          try {
            cfg.resolve()
            files.addAll(cfg.files)
          } catch (Throwable ignored) {
          }
        }
      }
      print(files.collect { it.absolutePath }.join(":"))
    }
  }
}
"""
        )
        init_script = Path(f.name)

    try:
        gradlew = repo_root / "gradlew"
        gradle_cmd = str(gradlew.resolve()) if gradlew.exists() else "gradle"
        cmd = [gradle_cmd, "--no-daemon", "-q", "-I", str(init_script), "printAgtClasspath"]
        proc = subprocess.run(cmd, cwd=module_dir, stdout=subprocess.PIPE, stderr=subprocess.DEVNULL, text=True)
        if proc.returncode != 0:
            return ""
        return (proc.stdout or "").strip()
    finally:
        init_script.unlink(missing_ok=True)


def resolve_repo_runtime_classpath(repo_root: Path, module_rel: str, build_tool: str) -> str:
    tool = (build_tool or "").strip().lower()
    if tool not in {"maven", "gradle"}:
        tool = _detect_build_tool(repo_root)

    key = (str(repo_root.resolve()), _norm_module_rel(module_rel), tool)
    if key in _RUNTIME_CP_CACHE:
        return _RUNTIME_CP_CACHE[key]

    cp = ""
    if tool == "maven":
        cp = _maven_runtime_cp(repo_root, module_rel)
    elif tool == "gradle":
        cp = _gradle_runtime_cp(repo_root, module_rel)

    _RUNTIME_CP_CACHE[key] = cp
    return cp


class CompileStep(Step):
    step_names = ("compile",)

    @staticmethod
    def _reset_build_dir(build_dir: Path) -> None:
        if build_dir.exists():
            shutil.rmtree(build_dir, ignore_errors=True)
        ensure_dir(build_dir)

    def run(self, ctx: "TargetContext") -> bool:
        if not self.should_run():
            ctx.final_sources = ctx.sources
            return True

        self._reset_build_dir(ctx.target_build)
        compile_log = self.pipeline.logs_dir / f"{ctx.target_id}.compile.log"
        ok, tail, compiled_sources = compile_test_set_smart(
            java_files=ctx.sources,
            build_dir=ctx.target_build,
            libs_glob_cp=self.pipeline.args.libs_cp,
            sut_jar=ctx.sut_jar,
            log_file=compile_log,
            repo_root_for_deps=ctx.repo_root_for_deps,
            module_rel=ctx.module_rel,
            build_tool=ctx.build_tool,
            max_rounds=self.pipeline.args.dep_rounds,
        )

        if ok:
            ctx.final_sources = compiled_sources
            return True

        print(f'[agt] compile failed for full source set: repo="{ctx.repo}" fqcn="{ctx.fqcn}" (see {compile_log})')
        print("[agt][COMPILE-TAIL]\n" + tail)

        generated_sources = [s for s in ctx.sources if s not in set(ctx.manual_sources)]
        if generated_sources:
            self._reset_build_dir(ctx.target_build)
            gen_log = self.pipeline.logs_dir / f"{ctx.target_id}.compile.generated.log"
            ok_gen, tail_gen, compiled_gen = compile_test_set_smart(
                java_files=generated_sources,
                build_dir=ctx.target_build,
                libs_glob_cp=self.pipeline.args.libs_cp,
                sut_jar=ctx.sut_jar,
                log_file=gen_log,
                repo_root_for_deps=ctx.repo_root_for_deps,
                module_rel=ctx.module_rel,
                build_tool=ctx.build_tool,
                max_rounds=self.pipeline.args.dep_rounds,
            )
            if ok_gen:
                print(
                    f'[agt] Proceeding with generated-only compiled sources: repo="{ctx.repo}" fqcn="{ctx.fqcn}"'
                )
                ctx.final_sources = compiled_gen
                return True
            print(f'[agt] generated-only compile failed: repo="{ctx.repo}" fqcn="{ctx.fqcn}" (see {gen_log})')
            print("[agt][COMPILE-GENERATED-TAIL]\n" + tail_gen)

        if ctx.manual_sources:
            self._reset_build_dir(ctx.target_build)
            man_log = self.pipeline.logs_dir / f"{ctx.target_id}.compile.manual.log"
            ok_man, tail_man, compiled_man = compile_test_set_smart(
                java_files=ctx.manual_sources,
                build_dir=ctx.target_build,
                libs_glob_cp=self.pipeline.args.libs_cp,
                sut_jar=ctx.sut_jar,
                log_file=man_log,
                repo_root_for_deps=ctx.repo_root_for_deps,
                module_rel=ctx.module_rel,
                build_tool=ctx.build_tool,
                max_rounds=self.pipeline.args.dep_rounds,
            )
            if ok_man:
                print(
                    f'[agt] Proceeding with manual-only compiled sources: repo="{ctx.repo}" fqcn="{ctx.fqcn}"'
                )
                ctx.final_sources = compiled_man
                return True
            print(f'[agt] manual-only compile failed: repo="{ctx.repo}" fqcn="{ctx.fqcn}" (see {man_log})')
            print("[agt][COMPILE-MANUAL-TAIL]\n" + tail_man)

        print(f'[agt] Skip (compile failed): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
        return False
