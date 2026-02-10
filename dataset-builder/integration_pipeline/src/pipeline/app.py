from __future__ import annotations

import argparse
import fnmatch
import os
import shutil
import subprocess
import time
from pathlib import Path
from typing import Dict, List, Optional, Set, Tuple

from .common import (
    detect_junit_version,
    ensure_dir,
    load_cut_to_fatjar_map,
    looks_like_scaffolding,
    parse_package_and_class,
    read_csv_rows,
    repo_to_dir,
    split_list_field,
    is_probably_test_filename,
)
from .comment import comment_compile_errors
from .coverage import write_coverage_row
from .coverage_compare import run_coverage_for_test
from .pr_maker import write_pr_draft
from .compile import compile_test_set_smart
from .run import run_one_test_with_jacoco, get_coverage_stats
from .covfilter import run_covfilter_app, run_generate_reduced_app
from .llm import send_reduced_test_to_dir
from .agent import run_codex_integration
from .compare import compare_tests, run_tri_compare, run_tri_compare_with_log

STEP_CHOICES = [
    "compile",
    "filter",
    "reduce",
    "send",
    "llm-all",
    "llm-agt-improvement",
    "llm-integration",
    "llm-integration-step-by-step",
    "agent",
    "compare",
    "run",
    "adopted-fix",
    "adopted-comment",
    "adopted-filter",
    "adopted-reduce",
    "adopted-run",
    "pull-request-maker",
    "coverage-comparison",
    "coverage-comparison-reduced",
    "all",
]


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


def reduced_test_path(reduced_root: Path, target_id: str, generated_src: Path, top_n: int) -> Optional[Path]:
    pkg, cls = parse_package_and_class(generated_src)
    if not cls:
        return None
    reduced_name = f"{cls}_Top{top_n}.java"
    base = reduced_root / target_id
    cand = base / reduced_name
    if cand.exists():
        return cand
    matches = list(base.rglob(reduced_name)) if base.exists() else []
    return matches[0] if matches else None


def adopted_test_path(adopted_root: Path, target_id: str) -> Optional[Path]:
    base = adopted_root / target_id
    if not base.exists():
        return None
    matches = list(base.rglob("*_Adopted.java"))
    return matches[0] if matches else None


def improved_test_path(adopted_root: Path, target_id: str) -> Optional[Path]:
    base = adopted_root / target_id
    if not base.exists():
        return None
    matches = list(base.rglob("*_Improved.java"))
    return matches[0] if matches else None


def agentic_test_path(adopted_root: Path, target_id: str) -> Optional[Path]:
    base = adopted_root / target_id
    if not base.exists():
        return None
    matches = list(base.rglob("*_Adopted_Agentic.java"))
    return matches[0] if matches else None


def adopted_variants(adopted_root: Path, target_id: str) -> List[Tuple[str, Path]]:
    variants: List[Tuple[str, Path]] = []
    adopted_src = adopted_test_path(adopted_root, target_id)
    if adopted_src and adopted_src.exists():
        variants.append(("adopted", adopted_src))
    agentic_src = agentic_test_path(adopted_root, target_id)
    if agentic_src and agentic_src.exists():
        variants.append(("agentic", agentic_src))
    return variants


def adopted_variants(adopted_root: Path, target_id: str) -> List[Tuple[str, Path]]:
    variants: List[Tuple[str, Path]] = []
    adopted_src = adopted_test_path(adopted_root, target_id)
    if adopted_src and adopted_src.exists():
        variants.append(("adopted", adopted_src))
    agentic_src = agentic_test_path(adopted_root, target_id)
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





def build_arg_parser() -> argparse.ArgumentParser:
    ap = argparse.ArgumentParser()
    ap.add_argument("tests_inventory_csv", type=str)
    ap.add_argument("cut_to_fatjar_map_csv", type=str)

    ap.add_argument("--generated-dir", type=str, default="../generated")
    ap.add_argument(
        "--manual-dir",
        type=str,
        default="../manual",
        help="Manual tests root. Prefer ../manual (collected) instead of ../repos to avoid pulling unrelated tests.",
    )
    ap.add_argument("--repos-dir", type=str, default="../repos", help="Repo root used only for dependency auto-include.")
    ap.add_argument("--libs-cp", type=str, default="libs/*")
    ap.add_argument("--jacoco-agent", type=str, default="jacoco-deps/org.jacoco.agent-run-0.8.14.jar")
    ap.add_argument("--out-dir", type=str, default="tmp")
    ap.add_argument("--build-dir", type=str, default="build/agt")
    ap.add_argument("--mode", choices=["generated", "manual", "both"], default="both")
    ap.add_argument("--includes", type=str, default="*")
    ap.add_argument("--dep-rounds", type=int, default=3, help="Max smart-compile dependency expansion rounds.")
    ap.add_argument("--tool-jar", type=str, default="coverage-filter-1.0-SNAPSHOT.jar", help="Path to the tool jar (RunOne/RunMany/ListTests)")
    ap.add_argument(
        "--step",
        choices=STEP_CHOICES,
        default="all",
        help="Execution step to run",
    )
    ap.add_argument("--timeout-ms", type=int, default=240_000, help="Per-test hard timeout in ms (also passed to RunOne). Use 0 to disable.")
    ap.add_argument(
        "--filter-only-agt-covered",
        action="store_true",
        help="During filter step, only run covfilter for rows with auto line_covered > 0 in coverage_summary.csv.",
    )
    ap.add_argument(
        "--coverage-summary",
        type=str,
        default="",
        help="Optional path to coverage_summary.csv used by --filter-only-agt-covered (defaults to <out-dir>/coverage_summary.csv).",
    )
    ap.add_argument("--reduced-out", type=str, default="results/reduced-agt", help="Output dir for reduced AGT tests.")
    ap.add_argument("--reduce-max-tests", type=int, default=100, help="Max number of top-priority tests to keep in reduced AGT class.")
    ap.add_argument("--send-script", type=str, default="", help="Optional external sender script. If empty, use pipeline_llm.")
    ap.add_argument("--send-api-url", type=str, default="http://localhost:8001/graphql", help="API URL for send_java_file_to_api.py.")
    ap.add_argument("--send-sleep-seconds", type=int, default=30, help="Sleep between sends.")
    ap.add_argument("--adopted-dir", type=str, default="results/llm-out", help="Root dir for adopted tests (output from send/LLM).")
    ap.add_argument("--agent-model", type=str, default="", help="Codex model name (empty = use CLI default).")
    ap.add_argument("--agent-max-context-files", type=int, default=12, help="Max repo context files to include in agent prompt.")
    ap.add_argument("--agent-max-context-chars", type=int, default=40_000, help="Max total repo context characters for agent prompt.")
    ap.add_argument("--agent-max-prompt-chars", type=int, default=60_000, help="Max total prompt characters for agent request.")
    ap.add_argument("--compare-root", type=str, default="src/comparison", help="Path to comparison folder (contains tools/ and configs/).")
    ap.add_argument("--compare-out", type=str, default="results/compare", help="Output dir for AGT vs adopted comparison CSVs.")
    ap.add_argument("--compare-min-tokens", type=int, default=50, help="CPD minimum tokens.")
    ap.add_argument("--coverage-compare-top-n", type=int, default=5, help="Top-N to use for reduced coverage comparison.")

    # ---- CoverageFilterApp integration ----
    ap.add_argument("--do-covfilter", action="store_true", help="Run CoverageFilterApp 'filter' step per repo/fqcn when possible.")
    ap.add_argument("--covfilter-jar", type=str, default="", help="Path to coverage-filter-*.jar that contains app.CoverageFilterApp")
    ap.add_argument("--covfilter-out", type=str, default="results/covfilter", help="Output dir for CoverageFilterApp results")
    ap.add_argument(
        "--sut-classes-dir",
        type=str,
        default="",
        help=(
            "Optional directory containing compiled SUT classes. If omitted or invalid, the fatjar from the cut-to-fatjar map "
            "will be used directly and unpacked at runtime by CoverageFilterApp."
        ),
    )
    ap.add_argument("--adopted-covfilter-out", type=str, default="results/covfilter-adopted", help="Output dir for adopted CoverageFilterApp results")
    ap.add_argument("--adopted-reduced-out", type=str, default="results/reduced-adopted", help="Output dir for reduced adopted tests.")
    ap.add_argument("--adopted-reduce-max-tests", type=int, default=5, help="Max number of top-priority tests to keep in reduced adopted class.")
    return ap


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


def run_pipeline(args: Optional[argparse.Namespace] = None) -> int:
    if args is None:
        ap = build_arg_parser()
        args = ap.parse_args()

    _load_dotenv_if_present()

    inventory_csv = Path(args.tests_inventory_csv)
    map_csv = Path(args.cut_to_fatjar_map_csv)

    generated_dir = Path(args.generated_dir)
    manual_dir = Path(args.manual_dir)
    repos_dir = Path(args.repos_dir)

    out_dir = Path(args.out_dir)
    build_dir = Path(args.build_dir)
    logs_dir = out_dir / "logs"

    ensure_dir(out_dir)
    ensure_dir(logs_dir)
    ensure_dir(build_dir)

    jacoco_agent = Path(args.jacoco_agent)

    mapping = load_cut_to_fatjar_map(map_csv)
    inv_rows = read_csv_rows(inventory_csv)

    # covfilter config
    do_covfilter = bool(args.do_covfilter)
    covfilter_jar = Path(args.covfilter_jar) if args.covfilter_jar else None
    covfilter_out_root = Path(args.covfilter_out)
    sut_classes_dir = Path(args.sut_classes_dir) if args.sut_classes_dir else None
    adopted_covfilter_out_root = Path(args.adopted_covfilter_out)
    adopted_reduced_out_root = Path(args.adopted_reduced_out)
    adopted_root = Path(args.adopted_dir)
    adopted_fix_script = Path("src/pipeline/fix.py")
    pr_template = Path("docs/PR_TEMPLATE.md")
    pr_out_root = Path("results/pr")

    # Global coverage report CSV
    summary_csv = Path("results/coverage/coverage_summary.csv")
    header = (
        "repo,fqcn,variant,"
        "line_percentage_coverage,line_covered,line_total,"
        "branch_percentage_coverage,branch_covered,branch_total,"
        "failed,timeout,skipped\n"
    )
    if args.step in ("run", "all"):
        # Clear it if it already exists to avoid appending to old results in the same run
        summary_csv.parent.mkdir(parents=True, exist_ok=True)
        summary_csv.write_text(header, encoding="utf-8")
    elif not summary_csv.exists():
        summary_csv.parent.mkdir(parents=True, exist_ok=True)
        summary_csv.write_text(header, encoding="utf-8")

    adopted_summary_csv = Path("results/coverage/adopted_coverage_summary.csv")
    adopted_header = (
        "repo,fqcn,variant,"
        "line_percentage_coverage,line_covered,line_total,"
        "branch_percentage_coverage,branch_covered,branch_total,"
        "failed,timeout,skipped\n"
    )
    if args.step in ("adopted-run", "all"):
        adopted_summary_csv.parent.mkdir(parents=True, exist_ok=True)
        adopted_summary_csv.write_text(adopted_header, encoding="utf-8")
    elif not adopted_summary_csv.exists():
        adopted_summary_csv.parent.mkdir(parents=True, exist_ok=True)
        adopted_summary_csv.write_text(adopted_header, encoding="utf-8")

    coverage_compare_csv = Path(args.compare_out) / "coverage_compare.csv"
    coverage_compare_reduced_csv = Path(args.compare_out) / "coverage_compare_reduced.csv"
    coverage_compare_header = adopted_header
    if args.step in ("coverage-comparison", "all"):
        coverage_compare_csv.parent.mkdir(parents=True, exist_ok=True)
        coverage_compare_csv.write_text(coverage_compare_header, encoding="utf-8")
    elif not coverage_compare_csv.exists():
        coverage_compare_csv.parent.mkdir(parents=True, exist_ok=True)
        coverage_compare_csv.write_text(coverage_compare_header, encoding="utf-8")

    if args.step in ("coverage-comparison-reduced", "all"):
        coverage_compare_reduced_csv.parent.mkdir(parents=True, exist_ok=True)
        coverage_compare_reduced_csv.write_text(coverage_compare_header, encoding="utf-8")
    elif not coverage_compare_reduced_csv.exists():
        coverage_compare_reduced_csv.parent.mkdir(parents=True, exist_ok=True)
        coverage_compare_reduced_csv.write_text(coverage_compare_header, encoding="utf-8")

    ran = 0
    skipped = 0

    covfilter_allow: Optional[Set[Tuple[str, str]]] = None
    if args.filter_only_agt_covered and args.step in (
        "filter",
        "all",
        "reduce",
        "send",
        "llm-all",
        "llm-agt-improvement",
        "llm-integration",
        "llm-integration-step-by-step",
        "agent",
        "compare",
        "adopted-filter",
        "adopted-reduce",
        "adopted-run",
        "adopted-comment",
        "adopted-fix",
        "pull-request-maker",
        "coverage-comparison",
        "coverage-comparison-reduced",
    ):
        summary_path = Path(args.coverage_summary) if args.coverage_summary else summary_csv
        if summary_path.exists():
            cov_rows = read_csv_rows(summary_path)
            allow: Set[Tuple[str, str]] = set()
            for row in cov_rows:
                repo_row = (row.get("repo", "") or "").strip()
                fqcn_row = (row.get("fqcn", "") or "").strip()
                variant = (row.get("variant", "") or "").strip()
                raw = (row.get("line_covered", "") or "").strip()
                try:
                    val = int(raw)
                except ValueError:
                    val = 0
                if repo_row and fqcn_row and variant == "auto" and val > 0:
                    allow.add((repo_row, fqcn_row))
            covfilter_allow = allow

    for r in inv_rows:
        repo = (r.get("repo", "") or "").strip().strip('"')
        fqcn = (r.get("fqcn", "") or "").strip().strip('"')
        if not repo or not fqcn:
            continue

        # Optional wildcard filtering
        if args.includes and args.includes != "*":
            if not (fnmatch.fnmatch(repo, args.includes) or fnmatch.fnmatch(fqcn, args.includes)):
                continue

        gen_files = split_list_field(r.get("generated_files", ""))
        man_files = split_list_field(r.get("manual_files", ""))

        if args.mode == "generated" and not gen_files:
            skipped += 1
            continue
        if args.mode == "manual" and not man_files:
            skipped += 1
            continue
        if args.mode == "both" and (not gen_files and not man_files):
            skipped += 1
            continue

        m = mapping.get((repo, fqcn))
        if not m:
            print(f'[agt] Skip (no fatjar mapping): repo="{repo}" fqcn="{fqcn}"')
            skipped += 1
            continue

        fatjar_path = (m.fatjar_path or "").strip()
        if not fatjar_path or fatjar_path in ("FAIL", "SKIP-REPO"):
            print(f'[agt] Skip (fatjar missing): repo="{repo}" fqcn="{fqcn}" fatjar="{fatjar_path or "EMPTY"}"')
            skipped += 1
            continue

        sut_jar = Path(fatjar_path).resolve(strict=True)
        if not sut_jar.exists():
            print(f'[agt] Skip (fatjar path not found): repo="{repo}" fqcn="{fqcn}" fatjar="{sut_jar}"')
            skipped += 1
            continue

        target_id = f"{repo_to_dir(repo)}_{fqcn.replace('.', '_')}"
        target_build = build_dir / "test-classes" / target_id
        ensure_dir(target_build)

        sources: List[Path] = []
        manual_sources: List[Path] = []
        repo_bucket_gen = generated_dir / repo_to_dir(repo)
        repo_bucket_man = manual_dir / repo_to_dir(repo)

        # ---------- GENERATED ----------
        if args.mode in ("generated", "both") and gen_files:
            expanded: List[str] = []
            seen_names: Set[str] = set()

            for f in gen_files:
                if not f or f.lower() == "null":
                    continue
                if f not in seen_names:
                    seen_names.add(f)
                    expanded.append(f)

                if f.endswith("_ESTest.java"):
                    scaf = f.replace("_ESTest.java", "_ESTest_scaffolding.java")
                    if scaf not in seen_names:
                        seen_names.add(scaf)
                        expanded.append(scaf)

            gen_scaf = [f for f in expanded if looks_like_scaffolding(f)]
            gen_non_scaf = [f for f in expanded if not looks_like_scaffolding(f)]
            sources.extend(find_tests_in_bucket(repo_bucket_gen, gen_scaf))
            sources.extend(find_tests_in_bucket(repo_bucket_gen, gen_non_scaf))

        # ---------- MANUAL ----------
        if args.mode in ("manual", "both") and man_files:
            manual_primary = find_tests_in_bucket(repo_bucket_man, man_files)
            manual_all = expand_manual_sources(manual_primary)
            manual_sources = list(manual_all)
            sources.extend(manual_all)

        # de-dupe
        uniq: List[Path] = []
        seen = set()
        for s in sources:
            if s not in seen:
                seen.add(s)
                uniq.append(s)
        sources = uniq

        if not sources:
            print(f'[agt] Skip (no existing .java files): repo="{repo}" fqcn="{fqcn}"')
            skipped += 1
            continue

        # repo root for dependency search (ONLY used when javac asks)
        repo_root_for_deps = repos_dir / repo_to_dir(repo)

        compile_log = logs_dir / f"{target_id}.compile.log"
        if args.step in ("compile", "all"):
            ok, tail, final_sources = compile_test_set_smart(
                java_files=sources,
                build_dir=target_build,
                libs_glob_cp=args.libs_cp,
                sut_jar=sut_jar,
                log_file=compile_log,
                repo_root_for_deps=repo_root_for_deps,
                max_rounds=args.dep_rounds,
            )

            if not ok:
                print(f'[agt] Skip (compile failed): repo="{repo}" fqcn="{fqcn}" (see {compile_log})')
                print("[agt][COMPILE-TAIL]\n" + tail)
                skipped += 1
                continue
        else:
            # We need to identify which sources would have been used if we didn't compile now.
            # However, compile_test_set_smart also discovers dependencies.
            # If we are in 'filter' or 'run' step, we assume 'compile' has already run.
            # We still need to know the FQCNs of the tests.
            # For simplicity, we can assume that if we are not in compile step, 
            # we just take the initial sources and hope for the best, 
            # OR we try to see what's in the build dir.
            # Actually, first_test_fqcn_from_sources needs the Path objects.
            # Let's just use the initial `sources` list if we skip compile.
            final_sources = sources

        # -------- CoverageFilterApp step (split before run tests) --------
        manual_test_fqcn = first_test_fqcn_from_sources(manual_sources or final_sources, prefer_estest=False)
        generated_test_fqcn = first_test_fqcn_from_sources(final_sources, prefer_estest=True)

        if args.step in ("adopted-fix", "all"):
            if not adopted_root.exists():
                print(f'[agt] adopted-fix: Skip (missing adopted dir): {adopted_root}')
            elif not adopted_fix_script.exists():
                print(f'[agt] adopted-fix: Skip (missing script): {adopted_fix_script}')
            else:
                fix_log = logs_dir / f"{target_id}.adopted.fix.log"
                cmd = ["python3", str(adopted_fix_script), str(adopted_root)]
                print(f'[agt] adopted-fix: {target_id}')
                proc = subprocess.run(cmd, stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
                fix_log.write_text(proc.stdout or "", encoding="utf-8", errors="ignore")
                if proc.returncode != 0:
                    print(f'[agt] adopted-fix: FAIL (see {fix_log}) repo="{repo}" fqcn="{fqcn}"')

        if args.step in ("adopted-comment", "all"):
            variants = adopted_variants(adopted_root, target_id)
            if not variants:
                print(f'[agt] adopted-comment: Skip (missing adopted tests): repo="{repo}" fqcn="{fqcn}"')
            else:
                for variant, adopted_src in variants:
                    if not adopted_src.exists():
                        print(f'[agt] adopted-comment: Skip (missing {variant} source): repo="{repo}" fqcn="{fqcn}"')
                        continue
                    comment_build = build_dir / "adopted-comment-classes" / variant / target_id
                    comment_log = logs_dir / f"{target_id}.adopted.{variant}.comment.log"
                    print(f'[agt] Commenting compile errors ({variant}): repo="{repo}" fqcn="{fqcn}"')
                    ok = comment_compile_errors(
                        test_file=adopted_src,
                        build_dir=comment_build,
                        log_file=comment_log,
                        libs_glob_cp=args.libs_cp,
                        sut_jar=sut_jar,
                    )
                    if not ok:
                        print(f'[agt] adopted-comment: FAIL (see {comment_log}) repo="{repo}" fqcn="{fqcn}" variant="{variant}"')

        if do_covfilter and args.step in ("filter", "all"):
            if covfilter_allow is not None and (repo, fqcn) not in covfilter_allow:
                print(f'[agt] covfilter: Skip (agt_line_covered=0): repo="{repo}" fqcn="{fqcn}"')
                continue
            if covfilter_jar is None or not covfilter_jar.exists():
                print(f'[agt] covfilter: Skip (missing --covfilter-jar): repo="{repo}" fqcn="{fqcn}"')
            elif not manual_test_fqcn or not generated_test_fqcn:
                print(f'[agt] covfilter: Skip (need both manual+generated test fqcn): repo="{repo}" fqcn="{fqcn}"')
            else:
                if sut_classes_dir is not None and sut_classes_dir.exists():
                    cov_classes_dir = sut_classes_dir
                else:
                    cov_classes_dir = sut_jar

                cov_out = covfilter_out_root / target_id
                cov_log = logs_dir / f"{target_id}.covfilter.log"
                libs_dir_arg = libs_dir_from_glob(args.libs_cp)

                print(f'[agt] Running covfilter: repo="{repo}" fqcn="{fqcn}"')
                ok_cov, cov_tail = run_covfilter_app(
                    coverage_filter_jar=covfilter_jar,
                    libs_glob_cp=args.libs_cp,
                    test_classes_dir=target_build,
                    sut_classes_dir=cov_classes_dir,
                    out_dir=cov_out,
                    manual_test_fqcn=manual_test_fqcn,
                    generated_test_fqcn=generated_test_fqcn,
                    jacoco_agent_jar=jacoco_agent,
                    sut_cp_entry=sut_jar,
                    libs_dir_arg=libs_dir_arg,
                    log_file=cov_log,
                )
                if not ok_cov:
                    print(f'[agt] covfilter: FAIL (see {cov_log})')
                    print("[agt][COVFILTER-TAIL]\n" + cov_tail)

        if do_covfilter and args.step in ("adopted-filter", "all"):
            if covfilter_allow is not None and (repo, fqcn) not in covfilter_allow:
                print(f'[agt] adopted-covfilter: Skip (agt_line_covered=0): repo="{repo}" fqcn="{fqcn}"')
            elif covfilter_jar is None or not covfilter_jar.exists():
                print(f'[agt] adopted-covfilter: Skip (missing --covfilter-jar): repo="{repo}" fqcn="{fqcn}"')
            else:
                if not manual_sources:
                    print(f'[agt] adopted-covfilter: Skip (missing manual test source): repo="{repo}" fqcn="{fqcn}"')
                else:
                    variants = adopted_variants(adopted_root, target_id)
                    if not variants:
                        print(f'[agt] adopted-covfilter: Skip (missing adopted tests): repo="{repo}" fqcn="{fqcn}"')
                    else:
                        for variant, adopted_src in variants:
                            adopted_test_fqcn = test_fqcn_from_source(adopted_src)
                            if not adopted_test_fqcn or not manual_test_fqcn:
                                print(f'[agt] adopted-covfilter: Skip (cannot parse test fqcn): repo="{repo}" fqcn="{fqcn}" variant="{variant}"')
                                continue
                            if sut_classes_dir is not None and sut_classes_dir.exists():
                                cov_classes_dir = sut_classes_dir
                            else:
                                cov_classes_dir = sut_jar

                            adopted_build = build_dir / "adopted-filter-classes" / variant / target_id
                            ensure_dir(adopted_build)
                            adopt_compile_log = logs_dir / f"{target_id}.adopted.{variant}.covfilter.compile.log"
                            ok_adopt, adopt_tail, _ = compile_test_set_smart(
                                java_files=manual_sources + [adopted_src],
                                build_dir=adopted_build,
                                libs_glob_cp=args.libs_cp,
                                sut_jar=sut_jar,
                                log_file=adopt_compile_log,
                                repo_root_for_deps=repo_root_for_deps,
                                max_rounds=args.dep_rounds,
                            )
                            if not ok_adopt:
                                print(f'[agt] adopted-covfilter: Skip (compile failed): repo="{repo}" fqcn="{fqcn}" variant="{variant}" (see {adopt_compile_log})')
                                print("[agt][ADOPTED-COMPILE-TAIL]\n" + adopt_tail)
                                continue

                            cov_out = adopted_covfilter_out_root / variant / target_id
                            cov_log = logs_dir / f"{target_id}.adopted.{variant}.covfilter.log"
                            libs_dir_arg = libs_dir_from_glob(args.libs_cp)

                            print(f'[agt] Running adopted covfilter ({variant}): repo="{repo}" fqcn="{fqcn}"')
                            ok_cov, cov_tail = run_covfilter_app(
                                coverage_filter_jar=covfilter_jar,
                                libs_glob_cp=args.libs_cp,
                                test_classes_dir=adopted_build,
                                sut_classes_dir=cov_classes_dir,
                                out_dir=cov_out,
                                manual_test_fqcn=manual_test_fqcn,
                                generated_test_fqcn=adopted_test_fqcn,
                                jacoco_agent_jar=jacoco_agent,
                                sut_cp_entry=sut_jar,
                                libs_dir_arg=libs_dir_arg,
                                log_file=cov_log,
                            )
                            if not ok_cov:
                                print(f'[agt] adopted-covfilter: FAIL (see {cov_log})')
                                print("[agt][ADOPTED-COVFILTER-TAIL]\n" + cov_tail)

        if args.step in ("reduce", "all"):
            reduced_root = Path(args.reduced_out)
            test_deltas_csv = covfilter_out_root / target_id / "test_deltas_all.csv"
            generated_test_src = first_test_source_for_fqcn(final_sources, generated_test_fqcn)
            if covfilter_allow is not None and (repo, fqcn) not in covfilter_allow:
                print(f'[agt] reduce: Skip (agt_line_covered=0): repo="{repo}" fqcn="{fqcn}"')
            elif covfilter_jar is None or not covfilter_jar.exists():
                print(f'[agt] reduce: Skip (missing --covfilter-jar): repo="{repo}" fqcn="{fqcn}"')
            elif not generated_test_src or not generated_test_src.exists():
                print(f'[agt] reduce: Skip (missing generated test source): repo="{repo}" fqcn="{fqcn}"')
            elif not test_deltas_csv.exists():
                print(f'[agt] reduce: Skip (missing test_deltas_all.csv): repo="{repo}" fqcn="{fqcn}"')
            else:
                reduced_out = reduced_root / target_id
                reduced_log = logs_dir / f"{target_id}.reduce.log"
                top_n = max(1, min(args.reduce_max_tests, 100))
                print(f'[agt] Reducing AGT tests (top {top_n}): repo="{repo}" fqcn="{fqcn}"')
                ok_red, red_tail = run_generate_reduced_app(
                    coverage_filter_jar=covfilter_jar,
                    libs_glob_cp=args.libs_cp,
                    original_test_java=generated_test_src,
                    test_deltas_csv=test_deltas_csv,
                    top_n=top_n,
                    out_dir=reduced_out,
                    log_file=reduced_log,
                )
                if not ok_red:
                    print(f'[agt] reduce: FAIL (see {reduced_log})')
                    print("[agt][REDUCE-TAIL]\n" + red_tail)

        if args.step in ("adopted-reduce", "all"):
            if covfilter_allow is not None and (repo, fqcn) not in covfilter_allow:
                print(f'[agt] adopted-reduce: Skip (agt_line_covered=0): repo="{repo}" fqcn="{fqcn}"')
            elif covfilter_jar is None or not covfilter_jar.exists():
                print(f'[agt] adopted-reduce: Skip (missing --covfilter-jar): repo="{repo}" fqcn="{fqcn}"')
            else:
                variants = adopted_variants(adopted_root, target_id)
                if not variants:
                    print(f'[agt] adopted-reduce: Skip (missing adopted tests): repo="{repo}" fqcn="{fqcn}"')
                else:
                    for variant, adopted_src in variants:
                        test_deltas_csv = adopted_covfilter_out_root / variant / target_id / "test_deltas_all.csv"
                        if not test_deltas_csv.exists():
                            print(f'[agt] adopted-reduce: Skip (missing test_deltas_all.csv): repo="{repo}" fqcn="{fqcn}" variant="{variant}"')
                            continue
                        reduced_out = adopted_reduced_out_root / variant / target_id
                        reduced_log = logs_dir / f"{target_id}.adopted.{variant}.reduce.log"
                        top_n = max(1, min(args.adopted_reduce_max_tests, 100))
                        print(f'[agt] Reducing adopted tests ({variant}, top {top_n}): repo="{repo}" fqcn="{fqcn}"')
                        ok_red, red_tail = run_generate_reduced_app(
                            coverage_filter_jar=covfilter_jar,
                            libs_glob_cp=args.libs_cp,
                            original_test_java=adopted_src,
                            test_deltas_csv=test_deltas_csv,
                            top_n=top_n,
                            out_dir=reduced_out,
                            log_file=reduced_log,
                        )
                        if not ok_red:
                            print(f'[agt] adopted-reduce: FAIL (see {reduced_log})')
                            print("[agt][ADOPTED-REDUCE-TAIL]\n" + red_tail)

        if args.step in ("send", "llm-all", "llm-agt-improvement", "llm-integration", "llm-integration-step-by-step", "all"):
            send_script = Path(args.send_script) if args.send_script else None
            if covfilter_allow is not None and (repo, fqcn) not in covfilter_allow:
                print(f'[agt] send: Skip (agt_line_covered=0): repo="{repo}" fqcn="{fqcn}"')
            else:
                reduced_root = Path(args.reduced_out)
                top_n = max(1, min(args.reduce_max_tests, 100))
                generated_test_src = first_test_source_for_fqcn(final_sources, generated_test_fqcn)
                manual_test_src = first_test_source_for_fqcn(manual_sources or final_sources, manual_test_fqcn)
                reduced_src = reduced_test_path(reduced_root, target_id, generated_test_src, top_n) if generated_test_src else None
                improved_src = improved_test_path(adopted_root, target_id)

                phases = []
                if args.step in ("send", "llm-all", "all"):
                    phases.append(("llm-all", "integration_all", "_Adopted", reduced_src))
                if args.step == "llm-agt-improvement":
                    phases.append(("llm-agt-improvement", "integration_improvement", "_Improved", reduced_src))
                if args.step == "llm-integration":
                    phases.append(("llm-integration", "integration_merge", "_Adopted", improved_src))
                if args.step == "llm-integration-step-by-step":
                    phases.append(("llm-integration-step-by-step", "integration_step_by_step", "_Adopted_StepByStep", improved_src))

                for phase_name, prompt_type, output_suffix, agt_src in phases:
                    if phase_name == "llm-integration" and not improved_src:
                        print(f'[agt] {phase_name}: Skip (missing improved test): repo="{repo}" fqcn="{fqcn}"')
                        continue
                    if not agt_src or not agt_src.exists():
                        print(f'[agt] {phase_name}: Skip (missing AGT test): repo="{repo}" fqcn="{fqcn}"')
                        continue
                    if phase_name != "llm-agt-improvement":
                        if not manual_test_src or not manual_test_src.exists():
                            print(f'[agt] {phase_name}: Skip (missing manual test source): repo="{repo}" fqcn="{fqcn}"')
                            continue

                    mwt_src = None if phase_name == "llm-agt-improvement" else manual_test_src

                    send_log = logs_dir / f"{target_id}.{phase_name}.send.log"
                    llm_out_root = adopted_root
                    print(f'[agt] Sending {phase_name} to LLM: repo="{repo}" fqcn="{fqcn}"')
                    if send_script and send_script.exists():
                        if mwt_src is None:
                            print(f'[agt] {phase_name}: Skip (send_script requires manual test): repo="{repo}" fqcn="{fqcn}"')
                            continue
                        cmd = [
                            "python",
                            str(send_script),
                            "--api-url",
                            args.send_api_url,
                            "--agt_file_path",
                            str(agt_src),
                            "--mwt_file_path",
                            str(mwt_src),
                        ]
                        proc = subprocess.run(cmd, stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
                        send_log.write_text(proc.stdout or "", encoding="utf-8", errors="ignore")
                        if proc.returncode != 0:
                            print(f'[agt] {phase_name}: FAIL (see {send_log})')
                    else:
                        out_path = send_reduced_test_to_dir(
                            agt_file_path=agt_src,
                            mwt_file_path=mwt_src,
                            api_url=args.send_api_url,
                            out_root=llm_out_root,
                            target_id=target_id,
                            prompt_type=prompt_type,
                            output_suffix=output_suffix,
                        )
                        if not out_path:
                            print(f'[agt] {phase_name}: FAIL (see {send_log})')
                    time.sleep(max(0, args.send_sleep_seconds))

        if args.step in ("agent", "all"):
            if covfilter_allow is not None and (repo, fqcn) not in covfilter_allow:
                print(f'[agt] agent: Skip (agt_line_covered=0): repo="{repo}" fqcn="{fqcn}"')
            elif not shutil.which("codex"):
                print(f'[agt] agent: Skip (missing codex CLI): repo="{repo}" fqcn="{fqcn}"')
            else:
                improved_src = improved_test_path(adopted_root, target_id)
                manual_test_src = first_test_source_for_fqcn(manual_sources or final_sources, manual_test_fqcn)
                if not improved_src or not improved_src.exists():
                    print(f'[agt] agent: Skip (missing improved test): repo="{repo}" fqcn="{fqcn}"')
                elif not manual_test_src or not manual_test_src.exists():
                    print(f'[agt] agent: Skip (missing manual test source): repo="{repo}" fqcn="{fqcn}"')
                else:
                    repo_root = repos_dir / repo_to_dir(repo)
                    out_path = run_codex_integration(
                        model=args.agent_model,
                        improved_test_path=improved_src,
                        manual_test_path=manual_test_src,
                        repo_root=repo_root if repo_root.exists() else None,
                        out_root=adopted_root,
                        target_id=target_id,
                        max_context_files=args.agent_max_context_files,
                        max_context_chars=args.agent_max_context_chars,
                        max_prompt_chars=args.agent_max_prompt_chars,
                        log_file=logs_dir / f"{target_id}.agent.log",
                    )
                    if not out_path:
                        print(f'[agt] agent: FAIL (no output): repo="{repo}" fqcn="{fqcn}"')

        if args.step in ("compare", "all"):
            if covfilter_allow is not None and (repo, fqcn) not in covfilter_allow:
                print(f'[agt] compare: Skip (agt_line_covered=0): repo="{repo}" fqcn="{fqcn}"')
            else:
                reduced_root = Path(args.reduced_out)
                top_n = max(1, min(args.reduce_max_tests, 100))
                generated_test_src = first_test_source_for_fqcn(final_sources, generated_test_fqcn)
                reduced_src = reduced_test_path(reduced_root, target_id, generated_test_src, top_n) if generated_test_src else None
                manual_test_src = first_test_source_for_fqcn(final_sources, manual_test_fqcn)
                variants = adopted_variants(adopted_root, target_id)

                if not reduced_src or not reduced_src.exists():
                    print(f'[agt] compare: Skip (missing reduced AGT test): repo="{repo}" fqcn="{fqcn}"')
                elif not manual_test_src or not manual_test_src.exists():
                    print(f'[agt] compare: Skip (missing manual test source): repo="{repo}" fqcn="{fqcn}"')
                elif not variants:
                    print(f'[agt] compare: Skip (missing adopted tests): repo="{repo}" fqcn="{fqcn}"')
                else:
                    compare_root = Path(args.compare_root)
                    tri_script = Path("src/comparison/tri_compare_tests.py")
                    tri_csv = Path("results/compare/tri_compare.csv")

                    compare_out = Path(args.compare_out) / "compare.csv"
                    for idx, (variant, adopted_src) in enumerate(variants):
                        if not adopted_src.exists():
                            print(f'[agt] compare: Skip (missing {variant} test): repo="{repo}" fqcn="{fqcn}"')
                            continue
                        try:
                            compare_tests(
                                comparison_root=compare_root,
                                agt_file=reduced_src,
                                adopted_file=adopted_src,
                                out_csv=compare_out,
                                minimum_tokens=args.compare_min_tokens,
                                candidate_variant=variant,
                                include_auto=idx == 0,
                                repo=repo,
                                fqcn=fqcn,
                            )
                        except Exception as e:
                            print(f'[agt] compare: FAIL ({e}) repo="{repo}" fqcn="{fqcn}" variant="{variant}"')
                        tri_log = logs_dir / f"{target_id}.tri_compare.{variant}.log"
                        try:
                            run_tri_compare_with_log(
                                tri_script=tri_script,
                                group_id=f"{target_id}.{variant}",
                                auto_path=reduced_src,
                                adopted_path=adopted_src,
                                manual_path=manual_test_src,
                                out_csv=tri_csv,
                                log_path=tri_log,
                                adopted_variant=variant,
                                include_auto=idx == 0,
                            )
                            print(f'[agt] compare: tri_compare appended to {tri_csv} ({variant})')
                        except ModuleNotFoundError as e:
                            print(f'[agt] compare: Skip (tri_compare missing dep: {e.name}) repo="{repo}" fqcn="{fqcn}" variant="{variant}"')
                        except Exception as e:
                            print(f'[agt] compare: FAIL (tri_compare: {e}) repo="{repo}" fqcn="{fqcn}" variant="{variant}" (see {tri_log})')

        if args.step in ("pull-request-maker", "all"):
            if not pr_template.exists():
                print(f'[agt] pull-request-maker: Skip (missing template): {pr_template}')
            else:
                manual_test_src = first_test_source_for_fqcn(manual_sources or final_sources, manual_test_fqcn)
                ensure_dir(pr_out_root)
                for variant in ("adopted", "agentic"):
                    out_path = pr_out_root / f"{target_id}.{variant}.md"
                    ok = write_pr_draft(
                        template_path=pr_template,
                        out_path=out_path,
                        repo=repo,
                        fqcn=fqcn,
                        target_id=target_id,
                        variant=variant,
                        manual_test_src=manual_test_src,
                        adopted_reduced_root=adopted_reduced_out_root,
                    )
                    if ok:
                        print(f'[agt] pull-request-maker: wrote {out_path}')
                    else:
                        print(f'[agt] pull-request-maker: Skip (missing {variant} reduced test) for {target_id}')

        if args.step in ("coverage-comparison", "all"):
            if covfilter_allow is not None and (repo, fqcn) not in covfilter_allow:
                print(f'[agt] coverage-comparison: Skip (agt_line_covered=0): repo="{repo}" fqcn="{fqcn}"')
            else:
                manual_test_src = first_test_source_for_fqcn(manual_sources or final_sources, manual_test_fqcn)
                generated_test_src = first_test_source_for_fqcn(final_sources, generated_test_fqcn)
                variants = adopted_variants(adopted_root, target_id)
                tool_jar = Path(args.tool_jar)

                if manual_test_src and manual_test_src.exists():
                    run_coverage_for_test(
                        test_src=manual_test_src,
                        variant="manual",
                        repo=repo,
                        fqcn=fqcn,
                        build_dir=build_dir / "coverage-compare" / "manual" / target_id,
                        log_file=logs_dir / f"{target_id}.coverage.manual.log",
                        exec_file=out_dir / f"{target_id}__manual.exec",
                        libs_glob_cp=args.libs_cp,
                        sut_jar=sut_jar,
                        jacoco_agent=jacoco_agent,
                        tool_jar=tool_jar,
                        timeout_ms=args.timeout_ms,
                        repo_root_for_deps=repo_root_for_deps,
                        summary_csv=coverage_compare_csv,
                    )

                if generated_test_src and generated_test_src.exists():
                    run_coverage_for_test(
                        test_src=generated_test_src,
                        variant="auto",
                        repo=repo,
                        fqcn=fqcn,
                        build_dir=build_dir / "coverage-compare" / "auto" / target_id,
                        log_file=logs_dir / f"{target_id}.coverage.auto.log",
                        exec_file=out_dir / f"{target_id}__auto.exec",
                        libs_glob_cp=args.libs_cp,
                        sut_jar=sut_jar,
                        jacoco_agent=jacoco_agent,
                        tool_jar=tool_jar,
                        timeout_ms=args.timeout_ms,
                        repo_root_for_deps=repo_root_for_deps,
                        summary_csv=coverage_compare_csv,
                    )

                for variant, adopted_src in variants:
                    if not adopted_src.exists():
                        continue
                    run_coverage_for_test(
                        test_src=adopted_src,
                        variant=variant,
                        repo=repo,
                        fqcn=fqcn,
                        build_dir=build_dir / "coverage-compare" / variant / target_id,
                        log_file=logs_dir / f"{target_id}.coverage.{variant}.log",
                        exec_file=out_dir / f"{target_id}__{variant}.exec",
                        libs_glob_cp=args.libs_cp,
                        sut_jar=sut_jar,
                        jacoco_agent=jacoco_agent,
                        tool_jar=tool_jar,
                        timeout_ms=args.timeout_ms,
                        repo_root_for_deps=repo_root_for_deps,
                        summary_csv=coverage_compare_csv,
                    )

        if args.step in ("coverage-comparison-reduced", "all"):
            if covfilter_allow is not None and (repo, fqcn) not in covfilter_allow:
                print(f'[agt] coverage-comparison-reduced: Skip (agt_line_covered=0): repo="{repo}" fqcn="{fqcn}"')
            else:
                reduced_root = Path(args.reduced_out)
                top_n = max(1, min(args.coverage_compare_top_n, 100))
                generated_test_src = first_test_source_for_fqcn(final_sources, generated_test_fqcn)
                auto_reduced = reduced_test_path(reduced_root, target_id, generated_test_src, top_n) if generated_test_src else None
                tool_jar = Path(args.tool_jar)

                if auto_reduced and auto_reduced.exists():
                    run_coverage_for_test(
                        test_src=auto_reduced,
                        variant="auto-reduced",
                        repo=repo,
                        fqcn=fqcn,
                        build_dir=build_dir / "coverage-compare-reduced" / "auto" / target_id,
                        log_file=logs_dir / f"{target_id}.coverage.auto-reduced.log",
                        exec_file=out_dir / f"{target_id}__auto_reduced.exec",
                        libs_glob_cp=args.libs_cp,
                        sut_jar=sut_jar,
                        jacoco_agent=jacoco_agent,
                        tool_jar=tool_jar,
                        timeout_ms=args.timeout_ms,
                        repo_root_for_deps=repo_root_for_deps,
                        summary_csv=coverage_compare_reduced_csv,
                    )

                for variant in ("adopted", "agentic"):
                    reduced_src = reduced_variant_test_path(adopted_reduced_out_root, variant, target_id, top_n)
                    if not reduced_src or not reduced_src.exists():
                        continue
                    run_coverage_for_test(
                        test_src=reduced_src,
                        variant=f"{variant}-reduced",
                        repo=repo,
                        fqcn=fqcn,
                        build_dir=build_dir / "coverage-compare-reduced" / variant / target_id,
                        log_file=logs_dir / f"{target_id}.coverage.{variant}-reduced.log",
                        exec_file=out_dir / f"{target_id}__{variant}_reduced.exec",
                        libs_glob_cp=args.libs_cp,
                        sut_jar=sut_jar,
                        jacoco_agent=jacoco_agent,
                        tool_jar=tool_jar,
                        timeout_ms=args.timeout_ms,
                        repo_root_for_deps=repo_root_for_deps,
                        summary_csv=coverage_compare_reduced_csv,
                    )

        # -------- Run compiled tests normally --------
        if args.step in ("run", "all"):
            jacoco_cli = jacoco_agent.parent / "org.jacoco.cli-run-0.8.14.jar"
            wrote_manual = False
            wrote_auto = False

            for src in final_sources:
                pkg, cls = parse_package_and_class(src)
                if not cls:
                    continue
                if "scaffolding" in cls.lower():
                    continue

                test_fqcn = f"{pkg}.{cls}" if pkg else cls
                junit_ver = detect_junit_version(src)

                exec_file = out_dir / f"{target_id}__{cls}.exec"
                run_log = logs_dir / f"{target_id}__{cls}.run.log"

                print(f"[agt] Running (junit{junit_ver}): {test_fqcn}")
                status = run_one_test_with_jacoco(
                    junit_version=junit_ver,
                    test_fqcn=test_fqcn,
                    sut_jar=sut_jar,
                    libs_glob_cp=args.libs_cp,
                    compiled_tests_dir=target_build,
                    jacoco_agent_jar=jacoco_agent,
                    jacoco_exec_file=exec_file,
                    log_file=run_log,
                    tool_jar=Path(args.tool_jar),
                    timeout_ms=args.timeout_ms,
                )
                if status != "passed":
                    print(f"[agt] Test failed: {test_fqcn} (see {run_log})")
                stats = (0, 0, 0, 0)
                if status == "passed" and jacoco_cli.exists():
                    stats = get_coverage_stats(jacoco_cli, exec_file, sut_jar, fqcn, build_dir)

                if test_fqcn == manual_test_fqcn and not wrote_manual:
                    write_coverage_row(
                        csv_path=summary_csv,
                        repo=repo,
                        fqcn=fqcn,
                        variant="manual",
                        line_covered=stats[0],
                        line_total=stats[1],
                        branch_covered=stats[2],
                        branch_total=stats[3],
                        status=status,
                    )
                    wrote_manual = True

                if test_fqcn == generated_test_fqcn and not wrote_auto:
                    write_coverage_row(
                        csv_path=summary_csv,
                        repo=repo,
                        fqcn=fqcn,
                        variant="auto",
                        line_covered=stats[0],
                        line_total=stats[1],
                        branch_covered=stats[2],
                        branch_total=stats[3],
                        status=status,
                    )
                    wrote_auto = True

                ran += 1

            if not wrote_manual:
                write_coverage_row(
                    csv_path=summary_csv,
                    repo=repo,
                    fqcn=fqcn,
                    variant="manual",
                    line_covered=0,
                    line_total=0,
                    branch_covered=0,
                    branch_total=0,
                    status="skipped",
                )
            if not wrote_auto:
                write_coverage_row(
                    csv_path=summary_csv,
                    repo=repo,
                    fqcn=fqcn,
                    variant="auto",
                    line_covered=0,
                    line_total=0,
                    branch_covered=0,
                    branch_total=0,
                    status="skipped",
                )
        else:
            # If not running, we don't increment 'ran' or write to summary_csv for now.
            # Maybe skip reporting or report what we have.
            pass

        if args.step in ("adopted-run", "all"):
            if covfilter_allow is not None and (repo, fqcn) not in covfilter_allow:
                print(f'[agt] adopted-run: Skip (agt_line_covered=0): repo="{repo}" fqcn="{fqcn}"')
            else:
                variants = adopted_variants(adopted_root, target_id)
                if not variants:
                    print(f'[agt] adopted-run: Skip (missing adopted tests): repo="{repo}" fqcn="{fqcn}"')
                else:
                    for variant, adopted_src in variants:
                        if not adopted_src.exists():
                            print(f'[agt] adopted-run: Skip (missing {variant} test): repo="{repo}" fqcn="{fqcn}"')
                            continue
                        adopted_build = build_dir / "adopted-classes" / variant / target_id
                        ensure_dir(adopted_build)
                        adopt_compile_log = logs_dir / f"{target_id}.adopted.{variant}.compile.log"
                        ok_adopt, tail_adopt, _final_adopt_sources = compile_test_set_smart(
                            java_files=[adopted_src],
                            build_dir=adopted_build,
                            libs_glob_cp=args.libs_cp,
                            sut_jar=sut_jar,
                            log_file=adopt_compile_log,
                            repo_root_for_deps=repo_root_for_deps,
                            max_rounds=args.dep_rounds,
                        )
                        if not ok_adopt:
                            print(f'[agt] adopted-run: Skip (compile failed): repo="{repo}" fqcn="{fqcn}" variant="{variant}" (see {adopt_compile_log})')
                            print("[agt][ADOPTED-COMPILE-TAIL]\n" + tail_adopt)
                            continue

                        adopt_pkg, adopt_cls = parse_package_and_class(adopted_src)
                        if not adopt_cls:
                            print(f'[agt] adopted-run: Skip (cannot parse class): repo="{repo}" fqcn="{fqcn}" variant="{variant}"')
                            continue

                        adopted_fqcn = f"{adopt_pkg}.{adopt_cls}" if adopt_pkg else adopt_cls
                        junit_ver = detect_junit_version(adopted_src)
                        exec_file = out_dir / f"{target_id}__{variant}.exec"
                        run_log = logs_dir / f"{target_id}.adopted.{variant}.run.log"
                        print(f"[agt] Running adopted ({variant}, junit{junit_ver}): {adopted_fqcn}")
                        status = run_one_test_with_jacoco(
                            junit_version=junit_ver,
                            test_fqcn=adopted_fqcn,
                            sut_jar=sut_jar,
                            libs_glob_cp=args.libs_cp,
                            compiled_tests_dir=adopted_build,
                            jacoco_agent_jar=jacoco_agent,
                            jacoco_exec_file=exec_file,
                            log_file=run_log,
                            tool_jar=Path(args.tool_jar),
                            timeout_ms=args.timeout_ms,
                        )
                        if status != "passed":
                            print(f"[agt] adopted test failed: {adopted_fqcn} (see {run_log})")

                        adopted_cov = (0, 0, 0, 0)
                        if status == "passed" and (jacoco_agent.parent / "org.jacoco.cli-run-0.8.14.jar").exists():
                            adopted_cov = get_coverage_stats(
                                jacoco_agent.parent / "org.jacoco.cli-run-0.8.14.jar",
                                exec_file,
                                sut_jar,
                                fqcn,
                                build_dir,
                            )
                        a_lc, a_lt, a_bc, a_bt = adopted_cov
                        write_coverage_row(
                            csv_path=adopted_summary_csv,
                            repo=repo,
                            fqcn=fqcn,
                            variant=variant,
                            line_covered=a_lc,
                            line_total=a_lt,
                            branch_covered=a_bc,
                            branch_total=a_bt,
                            status=status,
                        )

    print("[agt] Done.")
    print(f"[agt] Ran:     {ran}")
    print(f"[agt] Skipped: {skipped}")
    print(f"[agt] Exec files: {out_dir}")
    print(f"[agt] Logs:      {logs_dir}")
    return 0


class AgtPipeline:
    def __init__(self, args: Optional[argparse.Namespace] = None) -> None:
        self.args = args

    def run(self) -> int:
        return run_pipeline(self.args)


def main() -> int:
    return AgtPipeline().run()


if __name__ == "__main__":
    raise SystemExit(main())
