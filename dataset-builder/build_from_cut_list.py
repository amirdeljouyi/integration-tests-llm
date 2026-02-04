#!/usr/bin/env python3
import csv
import os
import re
import shutil
import subprocess
import sys
from dataclasses import dataclass
from pathlib import Path
from typing import Optional, List, Dict, Tuple
import argparse
import logging

# Repos that are not Maven/Gradle builds (skip entirely)
SKIP_REPOS = {
    "openjdk/jdk",
    "bazelbuild/bazel",
    "seleniumhq/selenium",
    "hibernate/hibernate-orm",
}

# ---------- process helpers ----------

class CmdError(RuntimeError):
    def __init__(self, cmd: List[str], rc: int, out: str):
        super().__init__(f"Command failed ({rc}): {' '.join(cmd)}\n{out}")
        self.cmd = cmd
        self.rc = rc
        self.out = out


class SkipBuild(RuntimeError):
    """Signal: skip this module/repo build (unsupported build, requires newer JDK, etc.)."""
    pass


def which_or_fail(name: str):
    if shutil.which(name) is None:
        raise RuntimeError(f"Required tool not found on PATH: {name}")


def safe_name(s: str) -> str:
    return re.sub(r"[^a-zA-Z0-9._-]+", "_", s)


def ensure_executable(p: Path):
    if p.exists():
        try:
            p.chmod(p.stat().st_mode | 0o111)
        except Exception:
            pass


# ---------- logging ----------

LOGGER: Optional[logging.Logger] = None
LOG_BASE: Optional[Path] = None
RUN_SEQ = 0
CTX_REPO = "unknown_repo"
CTX_MODULE = "unknown_module"


def setup_logging(log_dir: Path) -> logging.Logger:
    log_dir.mkdir(parents=True, exist_ok=True)

    logger = logging.getLogger("builder")
    logger.setLevel(logging.DEBUG)

    fmt = logging.Formatter(
        "%(asctime)s %(levelname)s %(name)s: %(message)s",
        datefmt="%Y-%m-%d %H:%M:%S",
    )

    info_fh = logging.FileHandler(log_dir / "info.log", encoding="utf-8")
    info_fh.setLevel(logging.INFO)
    info_fh.setFormatter(fmt)

    err_fh = logging.FileHandler(log_dir / "error.log", encoding="utf-8")
    err_fh.setLevel(logging.ERROR)
    err_fh.setFormatter(fmt)

    ch = logging.StreamHandler()
    ch.setLevel(logging.INFO)
    ch.setFormatter(fmt)

    # avoid duplicates if main() called twice in same process
    if not logger.handlers:
        logger.addHandler(info_fh)
        logger.addHandler(err_fh)
        logger.addHandler(ch)

    return logger


def set_context(repo: str, module_rel: str):
    global CTX_REPO, CTX_MODULE
    CTX_REPO = repo or "unknown_repo"
    CTX_MODULE = module_rel or "root"


def _cmd_log_dir() -> Path:
    assert LOG_BASE is not None
    return LOG_BASE / safe_name(CTX_REPO) / safe_name(CTX_MODULE)


def _write_text(p: Path, s: str):
    p.parent.mkdir(parents=True, exist_ok=True)
    p.write_text(s or "", encoding="utf-8", errors="replace")


def run(cmd: List[str], cwd: Optional[Path] = None, env: Optional[dict] = None) -> str:
    """
    Runs command, prints output, returns combined stdout+stderr as string.
    Also writes per-command stdout/stderr logs when logging is enabled.
    Raises CmdError on non-zero.
    """
    global RUN_SEQ, LOGGER, LOG_BASE

    RUN_SEQ += 1
    cmd_str = " ".join(cmd)
    print(f"\n>> {cmd_str}")

    if LOGGER:
        LOGGER.info("RUN[%04d]: %s (cwd=%s)", RUN_SEQ, cmd_str, str(cwd) if cwd else "")

    p = subprocess.run(
        cmd,
        cwd=str(cwd) if cwd else None,
        env=env,
        text=True,
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
    )

    out = p.stdout or ""
    err = p.stderr or ""
    combined = out + (("\n" if out and not out.endswith("\n") else "") + err if err else "")

    # print to console
    if out:
        print(out, end="" if out.endswith("\n") else "\n")
    if err:
        print(err, end="" if err.endswith("\n") else "\n")

    # write per-command logs
    if LOG_BASE is not None:
        base = _cmd_log_dir()
        first = safe_name(cmd[0]) if cmd else "cmd"
        last = safe_name(cmd[-1]) if cmd else "cmd"
        stem = f"{RUN_SEQ:04d}_{first}_{last}"
        _write_text(base / f"{stem}.stdout.log", out)
        _write_text(base / f"{stem}.stderr.log", err)
        _write_text(base / f"{stem}.combined.log", combined)

    if p.returncode != 0:
        if LOGGER:
            tail = (err or combined)[-4000:]
            LOGGER.error("FAILED[%04d] rc=%d: %s", RUN_SEQ, p.returncode, cmd_str)
            LOGGER.error("---- tail ----\n%s\n------------", tail)
        raise CmdError(cmd, p.returncode, combined)

    if LOGGER:
        LOGGER.info("OK[%04d]: %s", RUN_SEQ, cmd_str)

    return combined


# ---------- git ----------

def git_clone_or_fetch(base_dir: Path, repo: str, update_existing: bool = False) -> Path:
    url = f"https://github.com/{repo}.git"
    local = base_dir / safe_name(repo)

    if local.exists():
        print(f"[OK] Already cloned: {repo} -> {local}")
        if update_existing:
            run(["git", "fetch", "--all", "--tags", "--prune"], cwd=local)
            try:
                run(["git", "reset", "--hard", "origin/HEAD"], cwd=local)
            except CmdError:
                pass
        return local

    run(["git", "clone", "--depth", "1", url, str(local)])
    return local


# ---------- repo scanning helpers ----------

def _is_ignored_path(p: Path) -> bool:
    s = str(p).replace("\\", "/")
    ignored = (
        "/.git/" in s or "/target/" in s or "/build/" in s or "/out/" in s or
        "/node_modules/" in s or "/.gradle/" in s or "/.idea/" in s
    )
    return ignored


def find_all_files(repo_root: Path, names: List[str]) -> List[Path]:
    out: List[Path] = []
    for n in names:
        for p in repo_root.rglob(n):
            if p.is_file() and not _is_ignored_path(p):
                out.append(p)
    return out


# ---------- build root detection ----------

def pom_has_modules(pom: Path) -> bool:
    try:
        txt = pom.read_text(encoding="utf-8", errors="ignore")
    except Exception:
        return False
    return ("<modules>" in txt) and ("<module>" in txt)


def pick_best_pom(repo_root: Path, scope_dir: Optional[Path] = None) -> Optional[Path]:
    """
    Find the best pom.xml.
    If scope_dir is provided, prefer pom.xml in ancestors of scope_dir (up to repo_root).
    Heuristics:
      - prefer aggregator poms with <modules>
      - then prefer shallower path (closer to repo root)
    """
    candidates: List[Path] = []

    if scope_dir:
        cur = scope_dir.resolve()
        rr = repo_root.resolve()
        while True:
            pom = cur / "pom.xml"
            if pom.exists() and pom.is_file():
                candidates.append(pom)
            if cur == rr or cur.parent == cur:
                break
            cur = cur.parent

    if not candidates:
        candidates = find_all_files(repo_root, ["pom.xml"])

    if not candidates:
        return None

    def depth_score(p: Path) -> int:
        rel = p.relative_to(repo_root)
        depth = len(rel.parts)
        bonus = -1000 if pom_has_modules(p) else 0
        return depth + bonus

    candidates = sorted(set(candidates), key=depth_score)
    return candidates[0]


def pick_module_pom(module_dir: Path, repo_root: Path) -> Optional[Path]:
    cur = module_dir.resolve()
    rr = repo_root.resolve()
    while True:
        pom = cur / "pom.xml"
        if pom.exists() and pom.is_file():
            return pom
        if cur == rr or cur.parent == cur:
            break
        cur = cur.parent
    return None


def pick_gradle_root(repo_root: Path, scope_dir: Optional[Path] = None) -> Optional[Path]:
    """
    Prefer a directory containing settings.gradle(.kts).
    If scope_dir is provided, check ancestors first.
    Else fall back to nearest build.gradle(.kts).
    """
    def has_settings(d: Path) -> bool:
        return (d / "settings.gradle").exists() or (d / "settings.gradle.kts").exists()

    def has_build(d: Path) -> bool:
        return (d / "build.gradle").exists() or (d / "build.gradle.kts").exists()

    rr = repo_root.resolve()

    if scope_dir:
        cur = scope_dir.resolve()
        while True:
            if has_settings(cur):
                return cur
            if cur == rr or cur.parent == cur:
                break
            cur = cur.parent

    settings = find_all_files(repo_root, ["settings.gradle", "settings.gradle.kts"])
    if settings:
        settings_dirs = [p.parent for p in settings]
        settings_dirs.sort(key=lambda d: len(d.relative_to(repo_root).parts))
        return settings_dirs[0]

    if scope_dir:
        cur = scope_dir.resolve()
        while True:
            if has_build(cur):
                return cur
            if cur == rr or cur.parent == cur:
                break
            cur = cur.parent

    builds = find_all_files(repo_root, ["build.gradle", "build.gradle.kts"])
    if builds:
        dirs = [p.parent for p in builds]
        dirs.sort(key=lambda d: len(d.relative_to(repo_root).parts))
        return dirs[0]

    return None


def find_build_root(start_dir: Path, repo_root: Path) -> Tuple[str, Path]:
    """
    Return (tool, module_dir).
    - Maven: module_dir is nearest ancestor containing pom.xml
    - Gradle: module_dir is nearest ancestor containing build.gradle(.kts)
    If nothing found upward, we scan repo for best candidates.
    """
    cur = start_dir.resolve()
    rr = repo_root.resolve()

    while True:
        if (cur / "pom.xml").exists():
            return ("maven", cur)
        if (cur / "build.gradle").exists() or (cur / "build.gradle.kts").exists():
            return ("gradle", cur)
        if cur == rr or cur.parent == cur:
            break
        cur = cur.parent

    pom = pick_best_pom(repo_root)
    if pom:
        return ("maven", pom.parent)

    gradle_root = pick_gradle_root(repo_root)
    if gradle_root:
        return ("gradle", gradle_root)

    raise FileNotFoundError(f"No pom.xml or build.gradle(.kts) found in repo {repo_root}")


def infer_fqcn_from_path(class_path: str) -> Optional[str]:
    p = class_path.replace("\\", "/")
    marker = "/src/main/java/"
    if marker not in p or not p.endswith(".java"):
        return None
    rel = p.split(marker, 1)[1][:-5]
    return rel.replace("/", ".")


# ---------- wrappers selection ----------

def mvnw_is_usable(repo_root: Path) -> bool:
    mvnw = repo_root / "mvnw"
    jar = repo_root / ".mvn" / "wrapper" / "maven-wrapper.jar"
    return mvnw.exists() and jar.exists()


def gradlew_is_usable(repo_root: Path) -> bool:
    gradlew = repo_root / "gradlew"
    jar = repo_root / "gradle" / "wrapper" / "gradle-wrapper.jar"
    return gradlew.exists() and jar.exists()


def pick_mvn_cmd(repo_root: Path, module_dir: Path) -> List[str]:
    if mvnw_is_usable(repo_root):
        mvnw = repo_root / "mvnw"
        ensure_executable(mvnw)
        return [str(mvnw)]
    if (module_dir / "mvnw").exists():
        maybe_root = module_dir
        if mvnw_is_usable(maybe_root):
            mvnw = maybe_root / "mvnw"
            ensure_executable(mvnw)
            return [str(mvnw)]
    which_or_fail("mvn")
    return ["mvn"]


def pick_gradle_cmd(repo_root: Path, module_dir: Path) -> List[str]:
    # prefer wrapper at actual root, but allow wrapper in module_dir
    if gradlew_is_usable(repo_root):
        gradlew = repo_root / "gradlew"
        ensure_executable(gradlew)
        return [str(gradlew)]
    if (module_dir / "gradlew").exists():
        maybe_root = module_dir
        if gradlew_is_usable(maybe_root):
            gradlew = maybe_root / "gradlew"
            ensure_executable(gradlew)
            return [str(gradlew)]
    which_or_fail("gradle")
    return ["gradle"]


# ---------- fat jar discovery ----------

FAT_JAR_HINTS = ["all", "uber", "with-dependencies", "shadow", "shaded"]


def pick_best_jar(jar_dir: Path) -> Optional[Path]:
    if not jar_dir.exists():
        return None
    jars = sorted(jar_dir.glob("*.jar"), key=lambda p: p.stat().st_mtime, reverse=True)
    jars = [j for j in jars if not any(x in j.name for x in ["sources", "javadoc", "original-"])]
    if not jars:
        return None
    fat = [j for j in jars if any(h in j.name.lower() for h in FAT_JAR_HINTS)]
    return fat[0] if fat else jars[0]


# ---------- Maven build (robust) ----------

def maven_repo_extras(repo: str) -> List[str]:
    if repo == "quarkusio/quarkus":
        return ["-Dquarkus-extension-verify=false"]
    return []


def build_maven_fatjar(
    repo: str,
    repo_root: Path,
    module_dir: Path,
    cache_dir: Path,
    java21_home: str,
) -> Path:
    mvn = pick_mvn_cmd(repo_root, module_dir)

    common = mvn + ["-q"]
    skips = [
        "-DskipTests",
        "-DskipITs",
        "-Dinvoker.skip=true",
        "-Denforcer.skip=true",
        "-Dformatter.skip=true",
        "-Dlicense.skip=true",
        "-Dspotless.apply.skip=true",
        "-Dspotless.check.skip=true",
    ]
    extras = maven_repo_extras(repo)

    module_pom = pick_module_pom(module_dir, repo_root)
    if not module_pom:
        raise FileNotFoundError(f"No pom.xml found for module_dir={module_dir}")

    best_root_pom = pick_best_pom(repo_root, scope_dir=module_pom.parent) or module_pom
    root_pom = best_root_pom
    reactor_dir = root_pom.parent

    rel = os.path.relpath(module_pom.parent, reactor_dir).replace("\\", "/")

    jar_dir = module_pom.parent / "target"

    def set_java(env: dict, home: str) -> dict:
        if home:
            env = dict(env)
            env["JAVA_HOME"] = home
            env["PATH"] = str(Path(home) / "bin") + os.pathsep + env.get("PATH", "")
        return env

    base_env = os.environ.copy()

    def attempt_reactor_build(env: dict) -> None:
        if root_pom == module_pom:
            raise CmdError(["mvn", "reactor-skip"], 1, "No distinct reactor pom; use standalone build")
        run(common + skips + extras + ["-f", str(root_pom), "-am", "-pl", rel, "package"], cwd=reactor_dir, env=env)

    def attempt_standalone_build(env: dict) -> None:
        run(common + skips + extras + ["-f", str(module_pom), "package"], cwd=module_pom.parent, env=env)

    def attempt_shade(env: dict, use_reactor: bool) -> None:
        shade_goal = "org.apache.maven.plugins:maven-shade-plugin:3.5.0:shade"
        shade_args = [
            shade_goal,
            "-DcreateDependencyReducedPom=false",
            "-DshadedArtifactAttached=false",
        ]
        if use_reactor and root_pom != module_pom:
            run(common + skips + extras + ["-f", str(root_pom), "-am", "-pl", rel] + shade_args, cwd=reactor_dir, env=env)
        else:
            run(common + skips + extras + ["-f", str(module_pom)] + shade_args, cwd=module_pom.parent, env=env)

    def attempt_assembly_jar_with_deps(env: dict, use_reactor: bool) -> None:
        goal = "org.apache.maven.plugins:maven-assembly-plugin:3.6.0:single"
        args = [goal, "-DdescriptorRef=jar-with-dependencies", "-DappendAssemblyId=true"]
        if use_reactor and root_pom != module_pom:
            run(common + skips + extras + ["-f", str(root_pom), "-am", "-pl", rel] + args, cwd=reactor_dir, env=env)
        else:
            run(common + skips + extras + ["-f", str(module_pom)] + args, cwd=module_pom.parent, env=env)

    # 1) Try reactor build, else standalone
    env = base_env
    try:
        attempt_reactor_build(env)
    except CmdError as e:
        if "reactor-skip" in " ".join(e.cmd):
            attempt_standalone_build(env)
        elif "Could not find the selected project in the reactor" in e.out:
            attempt_standalone_build(env)
        elif ("JDK 21" in e.out or "RequireJavaVersion" in e.out) and java21_home:
            env21 = set_java(base_env, java21_home)
            try:
                attempt_reactor_build(env21)
            except CmdError as e2:
                if "reactor-skip" in " ".join(e2.cmd) or "Could not find the selected project in the reactor" in e2.out:
                    attempt_standalone_build(env21)
                else:
                    raise
        else:
            raise

    # 2) If fat jar exists, use it
    jar = pick_best_jar(jar_dir)
    if jar and any(h in jar.name.lower() for h in FAT_JAR_HINTS):
        return jar

    # 3) Try shade plugin
    try:
        use_reactor = (root_pom != module_pom)
        attempt_shade(base_env, use_reactor=use_reactor)
    except CmdError as e:
        if ("JDK 21" in e.out or "RequireJavaVersion" in e.out) and java21_home:
            env21 = set_java(base_env, java21_home)
            attempt_shade(env21, use_reactor=(root_pom != module_pom))
        else:
            # ignore; we will try assembly next
            pass

    jar = pick_best_jar(jar_dir)
    if jar and any(h in jar.name.lower() for h in FAT_JAR_HINTS):
        return jar

    # 4) Final fallback: assembly jar-with-dependencies
    try:
        attempt_assembly_jar_with_deps(base_env, use_reactor=(root_pom != module_pom))
    except CmdError:
        pass

    jar = pick_best_jar(jar_dir)
    if not jar:
        raise FileNotFoundError(f"No jars produced in {jar_dir}")
    return jar


# ---------- Gradle build (robust) ----------

def build_gradle_fatjar(cache_dir: Path, repo_root: Path, module_dir: Path) -> Path:
    env = os.environ.copy()
    env["GRADLE_USER_HOME"] = str(cache_dir / "gradle-home")

    gradle_root = pick_gradle_root(repo_root, scope_dir=module_dir) or module_dir
    gradle = pick_gradle_cmd(repo_root, gradle_root)

    libs_candidates = [
        module_dir / "build" / "libs",
        gradle_root / "build" / "libs",
    ]

    def pick_any_jar() -> Optional[Path]:
        for d in libs_candidates:
            j = pick_best_jar(d)
            if j:
                return j
        return None

    def run_gradle(args: List[str]) -> None:
        run(gradle + ["--no-daemon"] + args, cwd=gradle_root, env=env)

    def looks_like_jdk25(out: str) -> bool:
        return ("requires at least JDK 25" in out) or ("at least JDK 25" in out)

    def is_shadow_mode_error(out: str) -> bool:
        return ("No such property: mode" in out) or ("ShadowCopyAction" in out)

    def is_dep_verification(out: str) -> bool:
        return ("Dependency verification failed" in out) or ("verification-metadata.xml" in out)

    def is_missing_artifact(out: str) -> bool:
        return ("Could not find " in out) and ("Searched in the following locations" in out)

    # 1) Try normal jar first
    try:
        run_gradle(["jar", "-x", "test"])
    except CmdError as e1:
        if looks_like_jdk25(e1.out):
            raise SkipBuild("Requires at least JDK 25 (skipping)")
        if is_dep_verification(e1.out):
            run_gradle(["--dependency-verification=off", "jar", "-x", "test"])
        elif is_missing_artifact(e1.out):
            try:
                run_gradle(["--refresh-dependencies", "jar", "-x", "test"])
            except CmdError:
                raise SkipBuild("Missing dependency artifact (cannot resolve)")
        else:
            pass

    jar = pick_any_jar()
    if jar:
        return jar

    # 2) Try shadowJar, with guards and fallbacks
    try:
        run_gradle(["shadowJar", "-x", "test"])
    except CmdError as e2:
        if looks_like_jdk25(e2.out):
            raise SkipBuild("Requires at least JDK 25 (skipping)")
        if is_dep_verification(e2.out):
            run_gradle(["--dependency-verification=off", "shadowJar", "-x", "test"])
        elif is_shadow_mode_error(e2.out):
            run_gradle(["jar", "-x", "test"])
        elif is_missing_artifact(e2.out):
            try:
                run_gradle(["--refresh-dependencies", "jar", "-x", "test"])
            except CmdError:
                raise SkipBuild("Missing dependency artifact (cannot resolve)")
        else:
            # last fallback
            try:
                run_gradle(["build", "-x", "test", "-x", "shadowJar"])
            except CmdError:
                run_gradle(["jar", "-x", "test"])

    jar = pick_any_jar()
    if not jar:
        raise FileNotFoundError(
            f"No jars produced under any of: {', '.join(str(p) for p in libs_candidates)}"
        )
    return jar


# ---------- data structures ----------

@dataclass(frozen=True)
class ModuleKey:
    repo: str
    tool: str
    module_rel: str


# ---------- main ----------

def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("cut_csv", help="Path to selected_cut_classes.csv")
    ap.add_argument("--mode", choices=["local", "docker"], default="local",
                    help="local: use ./repos ./out; docker: use /work paths")
    ap.add_argument("--base-dir", default=".",
                    help="Base directory to create repos/out/.cache under in local mode (default: .)")
    ap.add_argument("--java-home", default="",
                    help="Optional JAVA_HOME override (default for all builds)")
    ap.add_argument("--java21-home", default="",
                    help="Optional JAVA_HOME for JDK 21 builds (auto-retry when build demands JDK 21)")
    ap.add_argument("--update-existing", action="store_true",
                    help="If set, fetch/reset repos even if already cloned.")
    ap.add_argument("--log-dir", default="",
                    help="Directory to write logs (info.log/error.log + per-command stdout/stderr). "
                         "Default: <out>/logs")
    args = ap.parse_args()

    base = Path("/work") if args.mode == "docker" else Path(args.base_dir).resolve()

    repos_dir = base / "repos"
    out_dir = base / "out"
    cache_dir = base / ".cache"
    repos_dir.mkdir(parents=True, exist_ok=True)
    out_dir.mkdir(parents=True, exist_ok=True)
    cache_dir.mkdir(parents=True, exist_ok=True)

    global LOGGER, LOG_BASE
    LOG_BASE = Path(args.log_dir).resolve() if args.log_dir else (out_dir / "logs")
    LOGGER = setup_logging(LOG_BASE)
    LOGGER.info("Starting build_from_cut_list.py")
    LOGGER.info("Args: %s", vars(args))

    if args.mode == "local":
        which_or_fail("git")
        which_or_fail("java")

    if args.java_home:
        os.environ["JAVA_HOME"] = args.java_home

    cut_csv = Path(args.cut_csv).resolve()
    if not cut_csv.exists():
        raise FileNotFoundError(cut_csv)

    rows: List[Tuple[str, str, str]] = []
    with open(cut_csv, newline="", encoding="utf-8") as f:
        reader = csv.DictReader(f)
        for r in reader:
            repo = (r.get("repo") or "").strip()
            class_path = (r.get("class_path") or "").strip()
            test_paths = (r.get("test_paths") or "").strip()
            if repo and class_path:
                rows.append((repo, class_path, test_paths))
    if not rows:
        print("No usable rows found.")
        sys.exit(1)

    repo_roots: Dict[str, Path] = {}
    module_dirs: Dict[ModuleKey, Path] = {}
    cut_records: List[dict] = []

    for repo, class_path, test_paths in rows:
        if repo in SKIP_REPOS:
            print(f"[SKIP-REPO] {repo}:{class_path}")
            cut_records.append({
                "repo": repo,
                "class_path": class_path,
                "test_paths": test_paths,
                "build_tool": "SKIP-REPO",
                "module_rel": "",
                "fqcn": infer_fqcn_from_path(class_path) or "",
            })
            continue

        if repo not in repo_roots:
            set_context(repo, "repo-root")
            print(f"\n==== Cloning/fetching {repo} ====")
            repo_roots[repo] = git_clone_or_fetch(repos_dir, repo, update_existing=args.update_existing)

        root = repo_roots[repo]
        class_abs = root / class_path

        if not class_abs.exists():
            print(f"[WARN] Missing class file: {repo}:{class_path}")
            if LOGGER:
                LOGGER.error("Missing class file: %s:%s", repo, class_path)

        try:
            tool, module_dir = find_build_root(class_abs.parent, root)
        except FileNotFoundError as e:
            print(f"[SKIP] {repo}:{class_path} -> {e}")
            if LOGGER:
                LOGGER.error("SKIP (no build root): %s:%s -> %s", repo, class_path, str(e))
            cut_records.append({
                "repo": repo,
                "class_path": class_path,
                "test_paths": test_paths,
                "build_tool": "SKIP",
                "module_rel": "",
                "fqcn": infer_fqcn_from_path(class_path) or "",
            })
            continue

        module_rel = os.path.relpath(module_dir, root).replace("\\", "/")
        key = ModuleKey(repo=repo, tool=tool, module_rel=module_rel)
        module_dirs.setdefault(key, module_dir)

        cut_records.append({
            "repo": repo,
            "class_path": class_path,
            "test_paths": test_paths,
            "build_tool": tool,
            "module_rel": module_rel,
            "fqcn": infer_fqcn_from_path(class_path) or "",
        })

    # Build fat jars per module
    built: Dict[ModuleKey, str] = {}

    for key, module_dir in module_dirs.items():
        root = repo_roots[key.repo]
        set_context(key.repo, key.module_rel)
        print(f"\n==== BUILD {key.repo} [{key.tool}] module={key.module_rel} ====")
        if LOGGER:
            LOGGER.info("==== BUILD %s [%s] module=%s ====", key.repo, key.tool, key.module_rel)

        try:
            if key.tool == "maven":
                jar = build_maven_fatjar(
                    key.repo, root, module_dir, cache_dir,
                    java21_home=args.java21_home
                )
            elif key.tool == "gradle":
                jar = build_gradle_fatjar(cache_dir, root, module_dir)
            else:
                raise ValueError(f"Unknown tool {key.tool}")

            out_sub = out_dir / safe_name(key.repo) / safe_name(key.module_rel or "root")
            out_sub.mkdir(parents=True, exist_ok=True)
            out_path = out_sub / jar.name
            shutil.copy2(jar, out_path)

            built[key] = str(out_path)
            print(f"[OK] {out_path}")
            if LOGGER:
                LOGGER.info("[OK] %s", str(out_path))

        except SkipBuild as e:
            built[key] = f"SKIP: {e}"
            print(f"[SKIP] {key.repo}:{key.module_rel} -> {e}")
            if LOGGER:
                LOGGER.info("[SKIP] %s:%s -> %s", key.repo, key.module_rel, str(e))

        except Exception as e:
            built[key] = "FAIL"
            print(f"[FAIL] {key.repo}:{key.module_rel} -> {e}")
            if LOGGER:
                LOGGER.error("[FAIL] %s:%s -> %s", key.repo, key.module_rel, str(e))

    # Write mapping CSV
    out_csv = out_dir / "cut_to_fatjar_map.csv"
    with open(out_csv, "w", newline="", encoding="utf-8") as f:
        fieldnames = [
            "repo", "build_tool", "module_rel",
            "class_path", "fqcn", "test_paths",
            "fatjar_path"
        ]
        w = csv.DictWriter(f, fieldnames=fieldnames)
        w.writeheader()

        for r in cut_records:
            if r["build_tool"] in ("SKIP", "SKIP-REPO"):
                fatjar_path = r["build_tool"]
            else:
                mk = ModuleKey(repo=r["repo"], tool=r["build_tool"], module_rel=r["module_rel"])
                fatjar_path = built.get(mk, "FAIL")

            w.writerow({
                "repo": r["repo"],
                "build_tool": r["build_tool"],
                "module_rel": r["module_rel"],
                "class_path": r["class_path"],
                "fqcn": r["fqcn"],
                "test_paths": r["test_paths"],
                "fatjar_path": fatjar_path,
            })

    print("\n===== DONE =====")
    print(f"Fat jars in: {out_dir}")
    print(f"Mapping CSV: {out_csv}")
    if LOGGER:
        LOGGER.info("===== DONE =====")
        LOGGER.info("Fat jars in: %s", str(out_dir))
        LOGGER.info("Mapping CSV: %s", str(out_csv))
        LOGGER.info("Logs in: %s", str(LOG_BASE))


if __name__ == "__main__":
    main()