# clone_repos.py
#!/usr/bin/env python3
import csv
import os
import re
import shutil
import subprocess
import sys
import argparse
import logging
from pathlib import Path
from typing import Optional, List, Dict, Tuple

# Repos that are not Maven/Gradle builds (skip entirely)
SKIP_REPOS = {
    "openjdk/jdk",
    "bazelbuild/bazel",
    "seleniumhq/selenium",
    "hibernate/hibernate-orm",
}

# ---------- helpers ----------

class CmdError(RuntimeError):
    def __init__(self, cmd: List[str], rc: int, out: str):
        super().__init__(f"Command failed ({rc}): {' '.join(cmd)}\n{out}")
        self.cmd = cmd
        self.rc = rc
        self.out = out

def which_or_fail(name: str):
    if shutil.which(name) is None:
        raise RuntimeError(f"Required tool not found on PATH: {name}")

def safe_name(s: str) -> str:
    return re.sub(r"[^a-zA-Z0-9._-]+", "_", s)

def run(cmd: List[str], cwd: Optional[Path] = None, env: Optional[dict] = None) -> str:
    cmd_str = " ".join(cmd)
    print(f"\n>> {cmd_str}")
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
    if out:
        print(out, end="" if out.endswith("\n") else "\n")
    if err:
        print(err, end="" if err.endswith("\n") else "\n")
    if p.returncode != 0:
        raise CmdError(cmd, p.returncode, combined)
    return combined

def setup_logging(log_dir: Path) -> logging.Logger:
    log_dir.mkdir(parents=True, exist_ok=True)
    logger = logging.getLogger("cloner")
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

    if not logger.handlers:
        logger.addHandler(info_fh)
        logger.addHandler(err_fh)
        logger.addHandler(ch)
    return logger

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

# ---------- main ----------

def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("cut_csv", help="Path to selected_cut_classes.csv")
    ap.add_argument("--mode", choices=["local", "docker"], default="local",
                    help="local: use ./repos ./out; docker: use /work paths")
    ap.add_argument("--base-dir", default=".",
                    help="Base directory to create repos/out under in local mode (default: .)")
    ap.add_argument("--update-existing", action="store_true",
                    help="If set, fetch/reset repos even if already cloned.")
    ap.add_argument("--log-dir", default="",
                    help="Directory to write logs. Default: <out>/logs-clone")
    ap.add_argument("--out-repos-csv", default="",
                    help="Output CSV listing cloned repo roots. Default: <out>/repo_roots.csv")
    args = ap.parse_args()

    base = Path("/work") if args.mode == "docker" else Path(args.base_dir).resolve()
    repos_dir = base / "repos"
    out_dir = base / "out"
    repos_dir.mkdir(parents=True, exist_ok=True)
    out_dir.mkdir(parents=True, exist_ok=True)

    log_dir = Path(args.log_dir).resolve() if args.log_dir else (out_dir / "logs-clone")
    logger = setup_logging(log_dir)
    logger.info("Starting clone_repos.py")
    logger.info("Args: %s", vars(args))

    if args.mode == "local":
        which_or_fail("git")

    cut_csv = Path(args.cut_csv).resolve()
    if not cut_csv.exists():
        raise FileNotFoundError(cut_csv)

    # collect unique repos
    repos: List[str] = []
    seen = set()
    with open(cut_csv, newline="", encoding="utf-8") as f:
        reader = csv.DictReader(f)
        for r in reader:
            repo = (r.get("repo") or "").strip()
            if repo and repo not in seen:
                seen.add(repo)
                repos.append(repo)

    if not repos:
        print("No repos found in cut csv.")
        sys.exit(1)

    repo_roots: Dict[str, str] = {}

    for repo in repos:
        if repo in SKIP_REPOS:
            print(f"[SKIP-REPO] {repo}")
            repo_roots[repo] = "SKIP-REPO"
            continue
        try:
            root = git_clone_or_fetch(repos_dir, repo, update_existing=args.update_existing)
            repo_roots[repo] = str(root)
            logger.info("[OK] %s -> %s", repo, str(root))
        except Exception as e:
            repo_roots[repo] = "FAIL"
            logger.error("[FAIL] %s -> %s", repo, str(e))

    out_csv = Path(args.out_repos_csv).resolve() if args.out_repos_csv else (out_dir / "repo_roots.csv")
    with open(out_csv, "w", newline="", encoding="utf-8") as f:
        w = csv.DictWriter(f, fieldnames=["repo", "repo_root"])
        w.writeheader()
        for repo in repos:
            w.writerow({"repo": repo, "repo_root": repo_roots.get(repo, "FAIL")})

    print("\n===== DONE (CLONE) =====")
    print(f"Repos dir: {repos_dir}")
    print(f"Repo roots CSV: {out_csv}")
    print(f"Logs: {log_dir}")

if __name__ == "__main__":
    main()