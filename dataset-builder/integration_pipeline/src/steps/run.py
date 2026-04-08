from __future__ import annotations

import csv
import os
import re
import shutil
import signal
import subprocess
import zipfile
from dataclasses import dataclass
from pathlib import Path
from typing import List, Tuple, TYPE_CHECKING, Optional

from ..core.common import candidate_repo_class_dirs, detect_junit_version, ensure_dir, parse_package_and_class, shlex_join
from ..core.coverage import write_coverage_diagnostic_row, write_coverage_error_row, write_coverage_row
from ..core.java import (
    add_throws_exception_to_tests,
    add_throws_exception_to_error_methods,
    categorize_compile_problem,
    compile_test_set_smart,
    extract_compile_problem_detail,
    prefer_repo_manual_sources,
    resolve_repo_runtime_classpath,
)
from ..pipeline.helpers import (
    adopted_variants,
    find_scaffolding_source,
    first_test_fqcn_from_sources,
    first_test_source_for_fqcn,
)
from .base import Step

if TYPE_CHECKING:
    from ..pipeline.pipeline import TargetContext

JAVA_OPENS = [
    "--add-opens=java.desktop/java.awt=ALL-UNNAMED",
    "--add-opens=java.base/java.lang=ALL-UNNAMED",
    "--add-opens=java.base/java.util=ALL-UNNAMED",
    "--add-opens=java.base/java.io=ALL-UNNAMED",
    "--add-opens=java.base/java.net=ALL-UNNAMED",
    "-Djava.awt.headless=true",
]


@dataclass(frozen=True)
class TestExecutionResult:
    status: str
    error_category: str = ""
    error_detail: str = ""
    class_origin: str = ""
    class_origin_detail: str = ""


@dataclass(frozen=True)
class CoverageObservation:
    category: str = ""
    detail: str = ""


def _extract_run_error_detail(output: str) -> str:
    for raw_line in output.splitlines():
        line = raw_line.strip()
        if not line:
            continue
        if any(
            token in line
            for token in (
                "Error opening zip file",
                "agent library failed to init",
                "AssertionError",
                "AssertionFailedError",
                "ComparisonFailure",
                "NoClassDefFoundError",
                "ClassNotFoundException",
                "NoSuchMethodError",
                "NoSuchFieldError",
                "IllegalAccessError",
                "ExceptionInInitializerError",
                "Exception:",
                "Error:",
            )
        ):
            return line
    return ""


def _categorize_run_failure(output: str, test_selector: str) -> tuple[str, str]:
    if re.search(rf"ClassNotFoundException:\s+{re.escape(test_selector)}\b", output):
        return "not_compiled", _extract_run_error_detail(output)
    if "Error opening zip file" in output or "agent library failed to init" in output:
        return "coverage_agent_error", _extract_run_error_detail(output)
    if any(token in output for token in ("AssertionError", "AssertionFailedError", "ComparisonFailure")):
        return "assertion_failure", _extract_run_error_detail(output)
    if any(token in output for token in ("NoClassDefFoundError", "ClassNotFoundException")):
        return "missing_runtime_dependency", _extract_run_error_detail(output)
    if any(
        token in output
        for token in (
            "NoSuchMethodError",
            "NoSuchFieldError",
            "IllegalAccessError",
            "IncompatibleClassChangeError",
            "AbstractMethodError",
        )
    ):
        return "linkage_error", _extract_run_error_detail(output)
    if "ExceptionInInitializerError" in output:
        return "initialization_error", _extract_run_error_detail(output)
    if "Exception:" in output or "Error:" in output:
        return "runtime_error", _extract_run_error_detail(output)
    return "test_process_failed", _extract_run_error_detail(output)


def _merge_sources(existing: List[Path], additions: List[Path]) -> List[Path]:
    merged: List[Path] = []
    seen = set()
    for source in [*existing, *additions]:
        if source in seen:
            continue
        seen.add(source)
        merged.append(source)
    return merged


def _compile_sources_for_variant(ctx: "TargetContext", *, variant: str, test_src: Path) -> List[Path]:
    if variant == "manual":
        manual_sources = list(ctx.manual_sources) if ctx.manual_sources else [test_src]
        sources = prefer_repo_manual_sources(ctx.repo_root_for_deps, manual_sources)
    else:
        sources = [test_src]
        scaffolding = find_scaffolding_source(test_src, list(ctx.sources))
        if scaffolding is not None:
            sources.append(scaffolding)
    return _merge_sources([], sources)


def _attempt_run_error_fix(
    *,
    ctx: "TargetContext",
    variant: str,
    test_src: Path,
    test_fqcn: str,
    exec_file: Path,
    run_log: Path,
    jacoco_cli: Path,
    logs_dir: Path,
    libs_glob_cp: str,
    jacoco_agent: Path,
    tool_jar: Path,
    timeout_ms: int | None,
    dep_rounds: int,
    coverage_tmp_dir: Path,
    compile_sources: Optional[List[Path]] = None,
) -> Tuple[Optional[TestExecutionResult], Tuple[int, int, int, int], CoverageObservation]:
    sources = compile_sources or _compile_sources_for_variant(ctx, variant=variant, test_src=test_src)

    add_throws_exception_to_tests(str(test_src), False)
    fix_compile_log = logs_dir / f"{ctx.target_id}__{test_src.stem}.{variant}.runfix.compile.log"
    ok_fix, _tail_fix, _compiled_fix = compile_test_set_smart(
        java_files=sources,
        build_dir=ctx.target_build,
        libs_glob_cp=libs_glob_cp,
        sut_jar=ctx.sut_jar,
        log_file=fix_compile_log,
        repo_root_for_deps=ctx.repo_root_for_deps,
        module_rel=ctx.module_rel,
        build_tool=ctx.build_tool,
        max_rounds=dep_rounds,
    )
    if not ok_fix:
        compile_output = fix_compile_log.read_text(encoding="utf-8", errors="ignore")
        changed = False
        for source in sources:
            changed = add_throws_exception_to_error_methods(str(source), compile_output, False) or changed
        if not changed:
            return None, (0, 0, 0, 0), CoverageObservation()
        ok_fix, _tail_fix, _compiled_fix = compile_test_set_smart(
            java_files=sources,
            build_dir=ctx.target_build,
            libs_glob_cp=libs_glob_cp,
            sut_jar=ctx.sut_jar,
            log_file=fix_compile_log,
            repo_root_for_deps=ctx.repo_root_for_deps,
            module_rel=ctx.module_rel,
            build_tool=ctx.build_tool,
            max_rounds=dep_rounds,
        )
    if not ok_fix:
        return None, (0, 0, 0, 0), CoverageObservation()

    ctx.final_sources = _merge_sources(ctx.final_sources, sources)
    result, stats, _, observation = run_test_with_coverage(
        test_src=test_src,
        test_fqcn=test_fqcn,
        compiled_tests_dir=ctx.target_build,
        exec_file=exec_file,
        run_log=run_log,
        sut_jar=ctx.sut_jar,
        libs_glob_cp=libs_glob_cp,
        jacoco_agent=jacoco_agent,
        tool_jar=tool_jar,
        timeout_ms=timeout_ms,
        jacoco_cli=jacoco_cli,
        target_fqcn=ctx.fqcn,
        coverage_tmp_dir=coverage_tmp_dir,
        repo_root_for_deps=ctx.repo_root_for_deps,
        module_rel=ctx.module_rel,
        build_tool=ctx.build_tool,
    )
    return result, stats, observation


def _write_coverage_observation_if_needed(
    *,
    csv_zero_hit: Path,
    csv_report_issues: Path,
    repo: str,
    fqcn: str,
    variant: str,
    test_fqcn: str,
    result: TestExecutionResult,
    stats: Tuple[int, int, int, int],
    observation: CoverageObservation,
    log_file: Path,
) -> None:
    if result.status != "passed" or not observation.category:
        return

    csv_path = csv_zero_hit if observation.category == "no_target_hit" else csv_report_issues
    write_coverage_diagnostic_row(
        csv_path=csv_path,
        repo=repo,
        fqcn=fqcn,
        variant=variant,
        test_fqcn=test_fqcn,
        status=result.status,
        coverage_category=observation.category,
        coverage_detail=observation.detail,
        line_covered=stats[0],
        line_total=stats[1],
        branch_covered=stats[2],
        branch_total=stats[3],
        class_origin=result.class_origin,
        class_origin_detail=result.class_origin_detail,
        log_file=str(log_file),
    )


class JacocoTestRunner:
    def __init__(
            self,
            *,
            libs_glob_cp: str,
            compiled_tests_dir: Path,
            sut_jar: Path,
            jacoco_agent_jar: Path,
            tool_jar: Path,
            timeout_ms: int | None,
            java_opens: List[str] | None = None,
            fallback_test_class_dirs: Optional[List[Path]] = None,
            extra_runtime_cp: str = "",
    ) -> None:
        self.libs_glob_cp = libs_glob_cp
        self.compiled_tests_dir = compiled_tests_dir
        self.sut_jar = sut_jar
        self.jacoco_agent_jar = jacoco_agent_jar
        self.tool_jar = tool_jar
        self.timeout_ms = timeout_ms
        self.java_opens = list(java_opens) if java_opens else list(JAVA_OPENS)
        self.fallback_test_class_dirs = list(fallback_test_class_dirs or [])
        self.extra_runtime_cp = (extra_runtime_cp or "").strip()

    def run_test(self, *, test_selector: str, jacoco_exec_file: Path, log_file: Path) -> TestExecutionResult:
        rel_class = Path(*test_selector.split(".")).with_suffix(".class")
        candidate_test_roots: List[Path] = [self.compiled_tests_dir, *self.fallback_test_class_dirs]
        ordered_roots: List[Path] = []
        seen_roots = set()
        for p in candidate_test_roots:
            if p in seen_roots:
                continue
            seen_roots.add(p)
            ordered_roots.append(p)

        selected_test_root: Optional[Path] = None
        expected_test_class = self.compiled_tests_dir / rel_class
        checked_locations: List[Path] = []
        for root in ordered_roots:
            expected = root / rel_class
            checked_locations.append(expected)
            if expected.exists():
                selected_test_root = root
                expected_test_class = expected
                break

        cp_parts: List[str] = []
        if selected_test_root is not None:
            cp_parts.append(str(selected_test_root))
        for root in ordered_roots:
            if selected_test_root is not None and root == selected_test_root:
                continue
            if root.exists():
                cp_parts.append(str(root))
        cp_parts.extend([self.libs_glob_cp, str(self.sut_jar)])
        if self.extra_runtime_cp:
            cp_parts.append(self.extra_runtime_cp)
        cp_parts.append(str(self.tool_jar))
        cp = ":".join(cp_parts)

        # Some inventory entries point to tests that don't compile for a given target.
        # If the class file isn't present, treat this as skipped rather than a hard failure.
        if selected_test_root is None:
            checked_text = "\n".join(str(p) for p in checked_locations)
            log_file.write_text(
                (
                    f"[agt] run skipped:\n"
                    f'test class "{test_selector}" is not present under compiled test classes.\n'
                    f"expected class file: {expected_test_class}\n"
                    f"checked class files:\n{checked_text}\n"
                    f"classpath would have been:\n{cp}\n"
                ),
                encoding="utf-8",
                errors="ignore",
            )
            return TestExecutionResult(
                status="skipped",
                error_category="not_compiled",
                error_detail='test class is not present under compiled or fallback class dirs',
            )

        class_origin = "build_output"
        class_origin_detail = str(selected_test_root)
        if selected_test_root != self.compiled_tests_dir:
            class_origin = "repo_class_fallback"

        cmd = [
            "java", *self.java_opens,
            f"-javaagent:{self.jacoco_agent_jar}=destfile={jacoco_exec_file}",
            "-cp",
            cp,
            "app.RunOne",
        ]
        if self.timeout_ms and self.timeout_ms > 0:
            cmd.extend(["--timeout-ms", str(self.timeout_ms)])
        cmd.append(test_selector)

        timed_out = False
        proc = subprocess.Popen(
            cmd,
            stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT,
            text=True,
            start_new_session=True,
        )
        try:
            out, _ = proc.communicate(timeout=(self.timeout_ms / 1000) if self.timeout_ms and self.timeout_ms > 0 else None)
        except subprocess.TimeoutExpired:
            timed_out = True
            try:
                os.killpg(proc.pid, signal.SIGKILL)
            except ProcessLookupError:
                pass
            out, _ = proc.communicate()

        out = out or ""
        log_file.write_text(
            f"[agt] run cmd:\n{shlex_join(cmd)}\n\n[agt] timed_out={timed_out}\n\n[agt] output:\n{out}\n",
            encoding="utf-8",
            errors="ignore",
        )
        if timed_out:
            return TestExecutionResult(
                status="timeout",
                error_category="timeout",
                class_origin=class_origin,
                class_origin_detail=class_origin_detail,
            )
        if proc.returncode == 0:
            for line in out.splitlines():
                if line.startswith("[JUnit5TestRunner]"):
                    parts = line.strip().split()
                    started = 0
                    skipped = 0
                    failed = 0
                    for p in parts:
                        if p.startswith("started="):
                            try:
                                started = int(p.split("=", 1)[1])
                            except ValueError:
                                started = 0
                        if p.startswith("skipped="):
                            try:
                                skipped = int(p.split("=", 1)[1])
                            except ValueError:
                                skipped = 0
                        if p.startswith("failed="):
                            try:
                                failed = int(p.split("=", 1)[1])
                            except ValueError:
                                failed = 0
                    if failed > 0:
                        return TestExecutionResult(
                            status="failed",
                            error_category="junit_test_failure",
                            error_detail=line.strip(),
                            class_origin=class_origin,
                            class_origin_detail=class_origin_detail,
                        )
                    if started == 0 and failed == 0 and skipped == 0:
                        return TestExecutionResult(
                            status="skipped",
                            error_category="no_tests_discovered",
                            error_detail=line.strip(),
                            class_origin=class_origin,
                            class_origin_detail=class_origin_detail,
                        )
                    if skipped > 0 and failed == 0:
                        return TestExecutionResult(
                            status="skipped",
                            error_category="junit_skipped",
                            error_detail=line.strip(),
                            class_origin=class_origin,
                            class_origin_detail=class_origin_detail,
                        )
                if line.startswith("[JUnit4TestRunner]"):
                    parts = line.strip().split()
                    run_count = 0
                    failed = 0
                    for p in parts:
                        if p.startswith("run="):
                            try:
                                run_count = int(p.split("=", 1)[1])
                            except ValueError:
                                run_count = 0
                        if p.startswith("failed="):
                            try:
                                failed = int(p.split("=", 1)[1])
                            except ValueError:
                                failed = 0
                    if failed > 0:
                        return TestExecutionResult(
                            status="failed",
                            error_category="junit_test_failure",
                            error_detail=line.strip(),
                            class_origin=class_origin,
                            class_origin_detail=class_origin_detail,
                        )
                    if run_count == 0 and failed == 0:
                        return TestExecutionResult(
                            status="skipped",
                            error_category="no_tests_discovered",
                            error_detail=line.strip(),
                            class_origin=class_origin,
                            class_origin_detail=class_origin_detail,
                        )
            return TestExecutionResult(
                status="passed",
                class_origin=class_origin,
                class_origin_detail=class_origin_detail,
            )
        m = re.search(r"ClassNotFoundException:\s+([A-Za-z0-9_.$]+)", out)
        if m and m.group(1) == test_selector:
            return TestExecutionResult(
                status="skipped",
                error_category="not_compiled",
                error_detail=_extract_run_error_detail(out),
                class_origin=class_origin,
                class_origin_detail=class_origin_detail,
            )
        error_category, error_detail = _categorize_run_failure(out, test_selector)
        return TestExecutionResult(
            status="failed",
            error_category=error_category,
            error_detail=error_detail,
            class_origin=class_origin,
            class_origin_detail=class_origin_detail,
        )


def run_one_test_with_jacoco(
        *,
        junit_version: int,
        test_fqcn: str,
        sut_jar: Path,
        libs_glob_cp: str,
        compiled_tests_dir: Path,
        jacoco_agent_jar: Path,
        jacoco_exec_file: Path,
        log_file: Path,
        tool_jar: Path,
        timeout_ms: int | None,
        fallback_test_class_dirs: Optional[List[Path]] = None,
        extra_runtime_cp: str = "",
) -> TestExecutionResult:
    """
    Run a single test class with JaCoCo using the Java tool's RunOne.
    junit_version: 4 or 5 (RunOne detects it automatically, but we keep the parameter for compatibility)
    """
    return JacocoTestRunner(
        libs_glob_cp=libs_glob_cp,
        compiled_tests_dir=compiled_tests_dir,
        sut_jar=sut_jar,
        jacoco_agent_jar=jacoco_agent_jar,
        tool_jar=tool_jar,
        timeout_ms=timeout_ms,
        fallback_test_class_dirs=fallback_test_class_dirs,
        extra_runtime_cp=extra_runtime_cp,
    ).run_test(
        test_selector=test_fqcn,
        jacoco_exec_file=jacoco_exec_file,
        log_file=log_file,
    )


def run_test_with_coverage(
    *,
    test_src: Path,
    compiled_tests_dir: Path,
    exec_file: Path,
    run_log: Path,
    sut_jar: Path,
    libs_glob_cp: str,
    jacoco_agent: Path,
    tool_jar: Path,
    timeout_ms: int | None,
    jacoco_cli: Path,
    target_fqcn: str,
    coverage_tmp_dir: Path,
    test_fqcn: str | None = None,
    repo_root_for_deps: Optional[Path] = None,
    module_rel: str = "",
    build_tool: str = "",
) -> Tuple[TestExecutionResult, Tuple[int, int, int, int], str | None, CoverageObservation]:
    if not test_fqcn:
        pkg, cls = parse_package_and_class(test_src)
        if not cls:
            return TestExecutionResult(status="skipped", error_category="parse_error"), (0, 0, 0, 0), None, CoverageObservation()
        test_fqcn = f"{pkg}.{cls}" if pkg else cls

    junit_ver = detect_junit_version(test_src)
    fallback_test_class_dirs = candidate_repo_class_dirs(repo_root_for_deps, module_rel)
    extra_runtime_cp = ""
    if repo_root_for_deps and repo_root_for_deps.exists():
        extra_runtime_cp = resolve_repo_runtime_classpath(
            repo_root_for_deps,
            module_rel,
            build_tool,
            source_files=[test_src],
        )

    result = run_one_test_with_jacoco(
        junit_version=junit_ver,
        test_fqcn=test_fqcn,
        sut_jar=sut_jar,
        libs_glob_cp=libs_glob_cp,
        compiled_tests_dir=compiled_tests_dir,
        jacoco_agent_jar=jacoco_agent,
        jacoco_exec_file=exec_file,
        log_file=run_log,
        tool_jar=tool_jar,
        timeout_ms=timeout_ms,
        fallback_test_class_dirs=fallback_test_class_dirs,
        extra_runtime_cp=extra_runtime_cp,
    )

    stats = (0, 0, 0, 0)
    observation = CoverageObservation()
    if result.status == "passed":
        if jacoco_cli.exists():
            stats, observation = get_coverage_stats(jacoco_cli, exec_file, sut_jar, target_fqcn, coverage_tmp_dir)
            if (
                not observation.category
                and stats[1] > 0
                and stats[0] == 0
            ):
                observation = CoverageObservation(
                    category="no_target_hit",
                    detail=f"JaCoCo found the target class {target_fqcn}, but this test covered 0 of {stats[1]} lines",
                )
        else:
            observation = CoverageObservation(
                category="jacoco_cli_missing",
                detail=f"JaCoCo CLI jar is missing: {jacoco_cli}",
            )

    return result, stats, test_fqcn, observation


def get_coverage_stats(
    jacoco_cli_jar: Path,
    jacoco_exec_file: Path,
    sut_jar: Path,
    target_fqcn: str,
    temp_dir: Path,
) -> Tuple[Tuple[int, int, int, int], CoverageObservation]:
    """
    Returns (line_covered, line_total, branch_covered, branch_total)
    """
    if not jacoco_exec_file.exists():
        return (0, 0, 0, 0), CoverageObservation(
            category="jacoco_exec_missing",
            detail=f"JaCoCo exec file was not produced: {jacoco_exec_file}",
        )

    csv_report = temp_dir / "jacoco_report.csv"

    def run_report(classfiles_path: Path) -> bool:
        if csv_report.exists():
            try:
                csv_report.unlink()
            except Exception:
                pass
        cmd = [
            "java", "-jar", str(jacoco_cli_jar),
            "report", str(jacoco_exec_file),
            "--classfiles", str(classfiles_path),
            "--csv", str(csv_report),
            "--quiet"
        ]
        proc = subprocess.run(cmd, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
        return proc.returncode == 0 and csv_report.exists()

    if not run_report(sut_jar):
        # Fallback: extract only the target class(es) from fatjar to avoid duplicate-class errors.
        if sut_jar.is_file() and sut_jar.suffix == ".jar":
            safe = target_fqcn.replace(".", "_")
            tmp_classes = temp_dir / f"jacoco_classfiles_{safe}"
            if tmp_classes.exists():
                shutil.rmtree(tmp_classes, ignore_errors=True)
            tmp_classes.mkdir(parents=True, exist_ok=True)
            prefix = target_fqcn.replace(".", "/")
            try:
                with zipfile.ZipFile(sut_jar) as z:
                    for info in z.infolist():
                        name = info.filename
                        if not name.endswith(".class"):
                            continue
                        if not name.startswith(prefix):
                            continue
                        z.extract(info, tmp_classes)
                if any(tmp_classes.rglob("*.class")):
                    if not run_report(tmp_classes):
                        return (0, 0, 0, 0), CoverageObservation(
                            category="report_generation_failed",
                            detail=f"JaCoCo report generation failed for extracted classfiles of {target_fqcn}",
                        )
                else:
                    return (0, 0, 0, 0), CoverageObservation(
                        category="target_classfiles_missing",
                        detail=f"Target class {target_fqcn} was not found inside {sut_jar}",
                    )
            finally:
                shutil.rmtree(tmp_classes, ignore_errors=True)
        else:
            return (0, 0, 0, 0), CoverageObservation(
                category="report_generation_failed",
                detail=f"JaCoCo report generation failed for {sut_jar}",
            )

    line_cov, line_tot = 0, 0
    branch_cov, branch_tot = 0, 0
    matched_rows = 0

    try:
        with csv_report.open("r", encoding="utf-8") as f:
            reader = csv.DictReader(f)
            for row in reader:
                # JaCoCo CSV columns: PACKAGE, CLASS, ... LINE_MISSED, LINE_COVERED, BRANCH_MISSED, BRANCH_COVERED
                pkg = row.get("PACKAGE", "")
                cls = row.get("CLASS", "")
                row_fqcn = f"{pkg}.{cls}" if pkg else cls
                if row_fqcn == target_fqcn:
                    matched_rows += 1
                    lm = int(row.get("LINE_MISSED", 0))
                    lc = int(row.get("LINE_COVERED", 0))
                    bm = int(row.get("BRANCH_MISSED", 0))
                    bc = int(row.get("BRANCH_COVERED", 0))
                    line_cov += lc
                    line_tot += (lc + lm)
                    branch_cov += bc
                    branch_tot += (bc + bm)
    except Exception:
        return (0, 0, 0, 0), CoverageObservation(
            category="report_parse_failed",
            detail=f"Failed to parse JaCoCo CSV report {csv_report}",
        )

    if matched_rows == 0:
        return (0, 0, 0, 0), CoverageObservation(
            category="target_not_found_in_report",
            detail=f"JaCoCo report did not contain an entry for target class {target_fqcn}",
        )

    if line_tot == 0 and branch_tot == 0:
        return (line_cov, line_tot, branch_cov, branch_tot), CoverageObservation(
            category="target_has_no_counters",
            detail=f"JaCoCo reported target class {target_fqcn}, but it had no line or branch counters",
        )

    return (line_cov, line_tot, branch_cov, branch_tot), CoverageObservation()


class RunStep(Step):
    step_names = ("run",)

    def run(self, ctx: "TargetContext") -> bool:
        if not self.should_run():
            return True
        jacoco_cli = self.pipeline.jacoco_agent.parent / "org.jacoco.cli-run-0.8.14.jar"
        tool_jar = Path(self.pipeline.args.tool_jar)
        wrote_manual = False
        wrote_auto = False
        selected_tests = {t for t in (ctx.manual_test_fqcn, ctx.generated_test_fqcn) if t}
        generated_sources = [s for s in ctx.sources if s not in set(ctx.manual_sources)]

        for src in ctx.final_sources:
            pkg, cls = parse_package_and_class(src)
            if not cls:
                continue
            if "scaffolding" in cls.lower():
                continue

            test_fqcn = f"{pkg}.{cls}" if pkg else cls
            if selected_tests and test_fqcn not in selected_tests:
                continue
            exec_file = self.pipeline.out_dir / f"{ctx.target_id}__{cls}.exec"
            run_log = self.pipeline.logs_dir / f"{ctx.target_id}__{cls}.run.log"

            print(f"[agt] Running: {test_fqcn}")
            result, stats, _, observation = run_test_with_coverage(
                test_src=src,
                test_fqcn=test_fqcn,
                compiled_tests_dir=ctx.target_build,
                exec_file=exec_file,
                run_log=run_log,
                sut_jar=ctx.sut_jar,
                libs_glob_cp=self.pipeline.args.libs_cp,
                jacoco_agent=self.pipeline.jacoco_agent,
                tool_jar=tool_jar,
                timeout_ms=self.pipeline.args.timeout_ms,
                jacoco_cli=jacoco_cli,
                target_fqcn=ctx.fqcn,
                coverage_tmp_dir=self.pipeline.build_dir,
                repo_root_for_deps=ctx.repo_root_for_deps,
                module_rel=ctx.module_rel,
                build_tool=ctx.build_tool,
            )
            variant = "manual" if test_fqcn == ctx.manual_test_fqcn else "auto"
            if result.error_category == "not_compiled":
                fixed_result, fixed_stats, fixed_observation = _attempt_run_error_fix(
                    ctx=ctx,
                    variant=variant,
                    test_src=src,
                    test_fqcn=test_fqcn,
                    exec_file=exec_file,
                    run_log=run_log,
                    jacoco_cli=jacoco_cli,
                    logs_dir=self.pipeline.logs_dir,
                    libs_glob_cp=self.pipeline.args.libs_cp,
                    jacoco_agent=self.pipeline.jacoco_agent,
                    tool_jar=tool_jar,
                    timeout_ms=self.pipeline.args.timeout_ms,
                    dep_rounds=self.pipeline.args.dep_rounds,
                    coverage_tmp_dir=self.pipeline.build_dir,
                )
                if fixed_result is not None:
                    result, stats = fixed_result, fixed_stats
                    observation = fixed_observation
            if result.status != "passed":
                print(f"[agt] Test failed: {test_fqcn} (see {run_log})")
                write_coverage_error_row(
                    csv_path=self.pipeline.coverage_errors_csv,
                    repo=ctx.repo,
                    fqcn=ctx.fqcn,
                    variant=variant,
                    test_fqcn=test_fqcn,
                    status=result.status,
                    error_category=result.error_category,
                    error_detail=result.error_detail,
                    class_origin=result.class_origin,
                    class_origin_detail=result.class_origin_detail,
                    log_file=str(run_log),
                )
            _write_coverage_observation_if_needed(
                csv_zero_hit=self.pipeline.coverage_zero_hit_csv,
                csv_report_issues=self.pipeline.coverage_report_issues_csv,
                repo=ctx.repo,
                fqcn=ctx.fqcn,
                variant=variant,
                test_fqcn=test_fqcn,
                result=result,
                stats=stats,
                observation=observation,
                log_file=run_log,
            )

            if test_fqcn == ctx.manual_test_fqcn and not wrote_manual:
                write_coverage_row(
                    csv_path=self.pipeline.summary_csv,
                    repo=ctx.repo,
                    fqcn=ctx.fqcn,
                    variant="manual",
                    line_covered=stats[0],
                    line_total=stats[1],
                    branch_covered=stats[2],
                    branch_total=stats[3],
                    status=result.status,
                )
                wrote_manual = True

            if test_fqcn == ctx.generated_test_fqcn and not wrote_auto:
                write_coverage_row(
                    csv_path=self.pipeline.summary_csv,
                    repo=ctx.repo,
                    fqcn=ctx.fqcn,
                    variant="auto",
                    line_covered=stats[0],
                    line_total=stats[1],
                    branch_covered=stats[2],
                    branch_total=stats[3],
                    status=result.status,
                )
                wrote_auto = True

            self.pipeline.ran += 1

        if not wrote_manual:
            manual_src = first_test_source_for_fqcn(ctx.manual_sources or ctx.sources, ctx.manual_test_fqcn)
            if manual_src is None and ctx.manual_sources:
                fallback_manual_fqcn = first_test_fqcn_from_sources(ctx.manual_sources, prefer_estest=False)
                manual_src = first_test_source_for_fqcn(ctx.manual_sources, fallback_manual_fqcn)
            missing_manual = ctx.manual_test_fqcn or first_test_fqcn_from_sources(ctx.manual_sources, prefer_estest=False) or ""
            if manual_src is not None:
                manual_pkg, manual_cls = parse_package_and_class(manual_src)
                manual_fqcn = missing_manual or (f"{manual_pkg}.{manual_cls}" if manual_pkg else manual_cls)
                exec_file = self.pipeline.out_dir / f"{ctx.target_id}__{manual_cls}.exec"
                run_log = self.pipeline.logs_dir / f"{ctx.target_id}__{manual_cls}.run.log"
                fixed_result, fixed_stats, fixed_observation = _attempt_run_error_fix(
                    ctx=ctx,
                    variant="manual",
                    test_src=manual_src,
                    test_fqcn=manual_fqcn,
                    exec_file=exec_file,
                    run_log=run_log,
                    jacoco_cli=jacoco_cli,
                    logs_dir=self.pipeline.logs_dir,
                    libs_glob_cp=self.pipeline.args.libs_cp,
                    jacoco_agent=self.pipeline.jacoco_agent,
                    tool_jar=tool_jar,
                    timeout_ms=self.pipeline.args.timeout_ms,
                    dep_rounds=self.pipeline.args.dep_rounds,
                    coverage_tmp_dir=self.pipeline.build_dir,
                )
                if fixed_result is not None:
                    write_coverage_row(
                        csv_path=self.pipeline.summary_csv,
                        repo=ctx.repo,
                        fqcn=ctx.fqcn,
                        variant="manual",
                        line_covered=fixed_stats[0],
                        line_total=fixed_stats[1],
                        branch_covered=fixed_stats[2],
                        branch_total=fixed_stats[3],
                        status=fixed_result.status,
                    )
                    _write_coverage_observation_if_needed(
                        csv_zero_hit=self.pipeline.coverage_zero_hit_csv,
                        csv_report_issues=self.pipeline.coverage_report_issues_csv,
                        repo=ctx.repo,
                        fqcn=ctx.fqcn,
                        variant="manual",
                        test_fqcn=manual_fqcn,
                        result=fixed_result,
                        stats=fixed_stats,
                        observation=fixed_observation,
                        log_file=run_log,
                    )
                    if fixed_result.status != "passed":
                        write_coverage_error_row(
                            csv_path=self.pipeline.coverage_errors_csv,
                            repo=ctx.repo,
                            fqcn=ctx.fqcn,
                            variant="manual",
                            test_fqcn=manual_fqcn,
                            status=fixed_result.status,
                            error_category=fixed_result.error_category,
                            error_detail=fixed_result.error_detail,
                            class_origin=fixed_result.class_origin,
                            class_origin_detail=fixed_result.class_origin_detail,
                            log_file=str(run_log),
                        )
                    wrote_manual = True
                    self.pipeline.ran += 1
            if not wrote_manual:
                write_coverage_row(
                    csv_path=self.pipeline.summary_csv,
                    repo=ctx.repo,
                    fqcn=ctx.fqcn,
                    variant="manual",
                    line_covered=0,
                    line_total=0,
                    branch_covered=0,
                    branch_total=0,
                    status="skipped",
                )
                write_coverage_error_row(
                    csv_path=self.pipeline.coverage_errors_csv,
                    repo=ctx.repo,
                    fqcn=ctx.fqcn,
                    variant="manual",
                    test_fqcn=missing_manual,
                    status="skipped",
                    error_category="no_compiled_test_class",
                    error_detail="no compiled manual test class available for run",
                    class_origin="",
                    class_origin_detail="",
                    log_file="",
                )
        if not wrote_auto:
            generated_src = first_test_source_for_fqcn(generated_sources, ctx.generated_test_fqcn)
            if generated_src is None and generated_sources:
                fallback_auto_fqcn = first_test_fqcn_from_sources(generated_sources, prefer_estest=True)
                generated_src = first_test_source_for_fqcn(generated_sources, fallback_auto_fqcn)
            missing_auto = ctx.generated_test_fqcn or first_test_fqcn_from_sources(generated_sources, prefer_estest=True) or ""
            if generated_src is not None:
                auto_pkg, auto_cls = parse_package_and_class(generated_src)
                auto_fqcn = missing_auto or (f"{auto_pkg}.{auto_cls}" if auto_pkg else auto_cls)
                exec_file = self.pipeline.out_dir / f"{ctx.target_id}__{auto_cls}.exec"
                run_log = self.pipeline.logs_dir / f"{ctx.target_id}__{auto_cls}.run.log"
                fixed_result, fixed_stats, fixed_observation = _attempt_run_error_fix(
                    ctx=ctx,
                    variant="auto",
                    test_src=generated_src,
                    test_fqcn=auto_fqcn,
                    exec_file=exec_file,
                    run_log=run_log,
                    jacoco_cli=jacoco_cli,
                    logs_dir=self.pipeline.logs_dir,
                    libs_glob_cp=self.pipeline.args.libs_cp,
                    jacoco_agent=self.pipeline.jacoco_agent,
                    tool_jar=tool_jar,
                    timeout_ms=self.pipeline.args.timeout_ms,
                    dep_rounds=self.pipeline.args.dep_rounds,
                    coverage_tmp_dir=self.pipeline.build_dir,
                )
                if fixed_result is not None:
                    write_coverage_row(
                        csv_path=self.pipeline.summary_csv,
                        repo=ctx.repo,
                        fqcn=ctx.fqcn,
                        variant="auto",
                        line_covered=fixed_stats[0],
                        line_total=fixed_stats[1],
                        branch_covered=fixed_stats[2],
                        branch_total=fixed_stats[3],
                        status=fixed_result.status,
                    )
                    _write_coverage_observation_if_needed(
                        csv_zero_hit=self.pipeline.coverage_zero_hit_csv,
                        csv_report_issues=self.pipeline.coverage_report_issues_csv,
                        repo=ctx.repo,
                        fqcn=ctx.fqcn,
                        variant="auto",
                        test_fqcn=auto_fqcn,
                        result=fixed_result,
                        stats=fixed_stats,
                        observation=fixed_observation,
                        log_file=run_log,
                    )
                    if fixed_result.status != "passed":
                        write_coverage_error_row(
                            csv_path=self.pipeline.coverage_errors_csv,
                            repo=ctx.repo,
                            fqcn=ctx.fqcn,
                            variant="auto",
                            test_fqcn=auto_fqcn,
                            status=fixed_result.status,
                            error_category=fixed_result.error_category,
                            error_detail=fixed_result.error_detail,
                            class_origin=fixed_result.class_origin,
                            class_origin_detail=fixed_result.class_origin_detail,
                            log_file=str(run_log),
                        )
                    wrote_auto = True
                    self.pipeline.ran += 1
            if not wrote_auto:
                write_coverage_row(
                    csv_path=self.pipeline.summary_csv,
                    repo=ctx.repo,
                    fqcn=ctx.fqcn,
                    variant="auto",
                    line_covered=0,
                    line_total=0,
                    branch_covered=0,
                    branch_total=0,
                    status="skipped",
                )
                write_coverage_error_row(
                    csv_path=self.pipeline.coverage_errors_csv,
                    repo=ctx.repo,
                    fqcn=ctx.fqcn,
                    variant="auto",
                    test_fqcn=missing_auto,
                    status="skipped",
                    error_category="no_compiled_test_class",
                    error_detail="no compiled generated test class available for run",
                    class_origin="",
                    class_origin_detail="",
                    log_file="",
                )
        return True


class AdoptedRunStep(Step):
    step_names = ("adopted-run",)

    def run(self, ctx: "TargetContext") -> bool:
        if not self.should_run():
            return True
        if self.pipeline.covfilter_allow is not None and (ctx.repo, ctx.fqcn) not in self.pipeline.covfilter_allow:
            print(f'[agt] adopted-run: Skip (agt_line_covered=0): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
            return True

        variants = adopted_variants(self.pipeline.adopted_root, ctx.target_id)
        if not variants:
            print(f'[agt] adopted-run: Skip (missing adopted tests): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
            return True

        for variant, adopted_src in variants:
            if not adopted_src.exists():
                print(f'[agt] adopted-run: Skip (missing {variant} test): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
                continue
            adopted_build = self.pipeline.build_dir / "adopted-classes" / variant / ctx.target_id
            ensure_dir(adopted_build)
            adopt_compile_log = self.pipeline.logs_dir / f"{ctx.target_id}.adopted.{variant}.compile.log"
            ok_adopt, tail_adopt, _final_adopt_sources = compile_test_set_smart(
                java_files=[adopted_src],
                build_dir=adopted_build,
                libs_glob_cp=self.pipeline.args.libs_cp,
                sut_jar=ctx.sut_jar,
                log_file=adopt_compile_log,
                repo_root_for_deps=ctx.repo_root_for_deps,
                module_rel=ctx.module_rel,
                build_tool=ctx.build_tool,
                max_rounds=self.pipeline.args.dep_rounds,
            )
            if not ok_adopt:
                adopt_pkg, adopt_cls = parse_package_and_class(adopted_src)
                adopted_fqcn = f"{adopt_pkg}.{adopt_cls}" if adopt_pkg and adopt_cls else (adopt_cls or adopted_src.stem)
                print(
                    f'[agt] adopted-run: Skip (compile failed): repo="{ctx.repo}" fqcn="{ctx.fqcn}" variant="{variant}" (see {adopt_compile_log})'
                )
                print("[agt][ADOPTED-COMPILE-TAIL]\n" + tail_adopt)
                write_coverage_error_row(
                    csv_path=self.pipeline.coverage_errors_csv,
                    repo=ctx.repo,
                    fqcn=ctx.fqcn,
                    variant=variant,
                    test_fqcn=adopted_fqcn,
                    status="not_compiled",
                    error_category=categorize_compile_problem(adopt_compile_log.read_text(encoding="utf-8", errors="ignore")),
                    error_detail=extract_compile_problem_detail(adopt_compile_log.read_text(encoding="utf-8", errors="ignore")),
                    class_origin="",
                    class_origin_detail="",
                    log_file=str(adopt_compile_log),
                )
                continue

            adopt_pkg, adopt_cls = parse_package_and_class(adopted_src)
            if not adopt_cls:
                print(f'[agt] adopted-run: Skip (cannot parse class): repo="{ctx.repo}" fqcn="{ctx.fqcn}" variant="{variant}"')
                continue

            exec_file = self.pipeline.out_dir / f"{ctx.target_id}__{variant}.exec"
            run_log = self.pipeline.logs_dir / f"{ctx.target_id}.adopted.{variant}.run.log"
            adopted_fqcn = f"{adopt_pkg}.{adopt_cls}" if adopt_pkg else adopt_cls
            print(f"[agt] Running adopted ({variant}): {adopted_fqcn}")
            result, adopted_cov, _, _ = run_test_with_coverage(
                test_src=adopted_src,
                test_fqcn=adopted_fqcn,
                compiled_tests_dir=adopted_build,
                exec_file=exec_file,
                run_log=run_log,
                sut_jar=ctx.sut_jar,
                libs_glob_cp=self.pipeline.args.libs_cp,
                jacoco_agent=self.pipeline.jacoco_agent,
                tool_jar=Path(self.pipeline.args.tool_jar),
                timeout_ms=self.pipeline.args.timeout_ms,
                jacoco_cli=self.pipeline.jacoco_agent.parent / "org.jacoco.cli-run-0.8.14.jar",
                target_fqcn=ctx.fqcn,
                coverage_tmp_dir=self.pipeline.build_dir,
                repo_root_for_deps=ctx.repo_root_for_deps,
                module_rel=ctx.module_rel,
                build_tool=ctx.build_tool,
            )
            if result.status != "passed":
                print(f"[agt] adopted test failed: {adopted_fqcn} (see {run_log})")
                write_coverage_error_row(
                    csv_path=self.pipeline.coverage_errors_csv,
                    repo=ctx.repo,
                    fqcn=ctx.fqcn,
                    variant=variant,
                    test_fqcn=adopted_fqcn,
                    status=result.status,
                    error_category=result.error_category,
                    error_detail=result.error_detail,
                    class_origin=result.class_origin,
                    class_origin_detail=result.class_origin_detail,
                    log_file=str(run_log),
                )

            a_lc, a_lt, a_bc, a_bt = adopted_cov
            write_coverage_row(
                csv_path=self.pipeline.adopted_summary_csv,
                repo=ctx.repo,
                fqcn=ctx.fqcn,
                variant=variant,
                line_covered=a_lc,
                line_total=a_lt,
                branch_covered=a_bc,
                branch_total=a_bt,
                status=result.status,
            )
        return True
