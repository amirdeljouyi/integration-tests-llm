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

# Repos that are not Maven/Gradle builds (skip entirely)
SKIP_REPOS = {"openjdk/jdk", "bazelbuild/bazel"}

# ---------- process helpers ----------

class CmdError(RuntimeError):
    def __init__(self, cmd: List[str], rc: int, out: str):
        super().__init__(f"Command failed ({rc}): {' '.join(cmd)}\n{out}")
        self.cmd = cmd
        self.rc = rc
        self.out = out

def run(cmd: List[str], cwd: Optional[Path] = None, env: Optional[dict] = None) -> str:
    """
    Runs command, prints output, returns combined stdout+stderr as string.
    Raises CmdError on non-zero.
    """
    print(f"\n>> {' '.join(cmd)}")
    p = subprocess.run(
        cmd,
        cwd=str(cwd) if cwd else None,
        env=env,
        text=True,
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
    )
    out = p.stdout or ""
    if out:
        print(out, end="" if out.endswith("\n") else "\n")
    if p.returncode != 0:
        raise CmdError(cmd, p.returncode, out)
    return out

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

# ---------- build root detection ----------

def find_build_root(start_dir: Path, repo_root: Path) -> Tuple[str, Path]:
    cur = start_dir
    while True:
        if (cur / "pom.xml").exists():
            return ("maven", cur)
        if (cur / "build.gradle").exists() or (cur / "build.gradle.kts").exists():
            return ("gradle", cur)
        if cur == repo_root or cur.parent == cur:
            break
        cur = cur.parent

    if (repo_root / "pom.xml").exists():
        return ("maven", repo_root)
    if (repo_root / "build.gradle").exists() or (repo_root / "build.gradle.kts").exists():
        return ("gradle", repo_root)
    raise FileNotFoundError(f"Could not find pom.xml or build.gradle(.kts) up from {start_dir}")

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
    # wrapper script exists + wrapper jar exists (otherwise "MavenWrapperMain" missing)
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
        # rare but possible; still check jar in that place
        maybe_root = module_dir
        if mvnw_is_usable(maybe_root):
            mvnw = maybe_root / "mvnw"
            ensure_executable(mvnw)
            return [str(mvnw)]
    which_or_fail("mvn")
    return ["mvn"]

def pick_gradle_cmd(repo_root: Path, module_dir: Path) -> List[str]:
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

# ---------- Gradle shadow init (fallback only) ----------

def write_shadow_init_script(path: Path):
    path.write_text(
        """
initscript {
  repositories { mavenCentral(); gradlePluginPortal() }
  dependencies { classpath "com.github.johnrengelman:shadow:8.1.1" }
}

allprojects { prj ->
  prj.plugins.withId("java") {
    prj.apply plugin: com.github.jengelman.gradle.plugins.shadow.ShadowPlugin
    prj.tasks.matching { it.name == "shadowJar" }.configureEach { t ->
      t.archiveClassifier.set("")
    }
  }
}
""".strip() + "\n"
    )

# ---------- fat jar discovery ----------

FAT_JAR_HINTS = [
    "all", "uber", "with-dependencies", "shadow", "shaded"
]

def pick_best_jar(jar_dir: Path) -> Optional[Path]:
    if not jar_dir.exists():
        return None
    jars = sorted(jar_dir.glob("*.jar"), key=lambda p: p.stat().st_mtime, reverse=True)
    jars = [j for j in jars if not any(x in j.name for x in ["sources", "javadoc", "original-"])]
    if not jars:
        return None

    # Prefer jars that look like fat jars
    fat = [j for j in jars if any(h in j.name.lower() for h in FAT_JAR_HINTS)]
    return (fat[0] if fat else jars[0])

# ---------- Maven build (robust) ----------

def maven_repo_extras(repo: str) -> List[str]:
    # Quarkus often needs this to avoid extension verification errors
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

    common = mvn + ["-q", "-Dstyle.color=always"]
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

    root_pom = repo_root / "pom.xml"
    module_pom = module_dir / "pom.xml"
    rel = os.path.relpath(module_dir, repo_root).replace("\\", "/")

    # We keep output jars in module target dir
    jar_dir = module_dir / "target" if module_dir != repo_root else repo_root / "target"

    def set_java(env: dict, home: str) -> dict:
        if home:
            env = dict(env)
            env["JAVA_HOME"] = home
            env["PATH"] = str(Path(home) / "bin") + os.pathsep + env.get("PATH", "")
        return env

    base_env = os.environ.copy()

    def attempt_reactor_build(env: dict) -> None:
        # Build in reactor: -f root/pom.xml -am -pl rel package
        run(common + skips + extras + ["-f", str(root_pom), "-am", "-pl", rel, "package"], cwd=repo_root, env=env)

    def attempt_standalone_build(env: dict) -> None:
        # Build module as standalone project
        run(common + skips + extras + ["-f", str(module_pom), "package"], cwd=module_dir, env=env)

    def attempt_shade(env: dict, use_reactor: bool) -> None:
        shade_goal = "org.apache.maven.plugins:maven-shade-plugin:3.5.0:shade"
        shade_args = [
            shade_goal,
            "-DcreateDependencyReducedPom=false",
            "-DshadedArtifactAttached=false",
        ]
        if use_reactor:
            run(common + skips + extras + ["-f", str(root_pom), "-am", "-pl", rel] + shade_args, cwd=repo_root, env=env)
        else:
            run(common + skips + extras + ["-f", str(module_pom)] + shade_args, cwd=module_dir, env=env)

    # 1) Try reactor build
    env = base_env
    try:
        attempt_reactor_build(env)
    except CmdError as e:
        # If the module isn't in reactor, retry standalone build
        if "Could not find the selected project in the reactor" in e.out:
            attempt_standalone_build(env)
        # If it explicitly demands JDK 21, retry with java21_home if provided
        elif ("JDK 21" in e.out or "RequireJavaVersion" in e.out) and java21_home:
            print(f"[RETRY-JDK21] {repo}:{rel} with JAVA_HOME={java21_home}")
            env21 = set_java(base_env, java21_home)
            try:
                attempt_reactor_build(env21)
            except CmdError as e2:
                if "Could not find the selected project in the reactor" in e2.out:
                    attempt_standalone_build(env21)
                else:
                    raise
        else:
            raise

    # 2) If a fat jar already exists, use it (many repos already create -all / -shaded etc)
    jar = pick_best_jar(jar_dir)
    if jar and any(h in jar.name.lower() for h in FAT_JAR_HINTS):
        return jar

    # 3) Otherwise try shade plugin (much more reliable than assembly)
    #    Prefer same mode we used for package (reactor vs standalone)
    try:
        # If module was in reactor, rel path is valid in most cases
        use_reactor = True
        # Heuristic: if root_pom exists and module_pom exists; reactor usually ok
        attempt_shade(base_env, use_reactor=use_reactor)
    except CmdError as e:
        if ("JDK 21" in e.out or "RequireJavaVersion" in e.out) and java21_home:
            env21 = set_java(base_env, java21_home)
            attempt_shade(env21, use_reactor=True)
        elif "Could not find the selected project in the reactor" in e.out:
            attempt_shade(base_env, use_reactor=False)
        else:
            # last chance: standalone shade
            try:
                attempt_shade(base_env, use_reactor=False)
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

    gradle = pick_gradle_cmd(repo_root, module_dir)
    libs = module_dir / "build" / "libs"

    # 1) Try native shadowJar first (no init script)
    try:
        run(gradle + ["--no-daemon", "shadowJar", "-x", "test"], cwd=module_dir, env=env)
    except CmdError as e:
        # If shadowJar task doesn't exist, then use init script to add it
        if ("Task 'shadowJar' not found" in e.out) or ("Could not find method shadowJar" in e.out):
            init = cache_dir / "shadow.init.gradle"
            init.parent.mkdir(parents=True, exist_ok=True)
            write_shadow_init_script(init)
            run(gradle + ["--no-daemon", "-I", str(init), "shadowJar", "-x", "test"], cwd=module_dir, env=env)
        # If shadow plugin already exists and our init script caused conflict, retry without init
        elif "extension already registered with that name" in e.out or "Cannot add extension with name 'shadow'" in e.out:
            # try plain jar as fallback
            run(gradle + ["--no-daemon", "jar", "-x", "test"], cwd=module_dir, env=env)
        else:
            # try plain jar as fallback
            run(gradle + ["--no-daemon", "jar", "-x", "test"], cwd=module_dir, env=env)

    jar = pick_best_jar(libs)
    if not jar:
        raise FileNotFoundError(f"No jars produced in {libs}")
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
    args = ap.parse_args()

    base = Path("/work") if args.mode == "docker" else Path(args.base_dir).resolve()

    repos_dir = base / "repos"
    out_dir = base / "out"
    cache_dir = base / ".cache"
    repos_dir.mkdir(parents=True, exist_ok=True)
    out_dir.mkdir(parents=True, exist_ok=True)
    cache_dir.mkdir(parents=True, exist_ok=True)

    if args.mode == "local":
        which_or_fail("git")
        which_or_fail("java")

    if args.java_home:
        os.environ["JAVA_HOME"] = args.java_home

    cut_csv = Path(args.cut_csv).resolve()
    if not cut_csv.exists():
        raise FileNotFoundError(cut_csv)

    # Read rows
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
            print(f"\n==== Cloning/fetching {repo} ====")
            repo_roots[repo] = git_clone_or_fetch(repos_dir, repo, update_existing=args.update_existing)

        root = repo_roots[repo]
        class_abs = root / class_path

        if not class_abs.exists():
            print(f"[WARN] Missing class file: {repo}:{class_path}")

        try:
            tool, module_dir = find_build_root(class_abs.parent, root)
        except FileNotFoundError as e:
            print(f"[SKIP] {repo}:{class_path} -> {e}")
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
        print(f"\n==== BUILD {key.repo} [{key.tool}] module={key.module_rel} ====")
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
        except Exception as e:
            built[key] = "FAIL"
            print(f"[FAIL] {key.repo}:{key.module_rel} -> {e}")

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

if __name__ == "__main__":
    main()

