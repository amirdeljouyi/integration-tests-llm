from __future__ import annotations

import csv
import os
import shutil
import signal
import subprocess
import zipfile
from pathlib import Path
from typing import List, Tuple, TYPE_CHECKING

from ..core.common import detect_junit_version, ensure_dir, parse_package_and_class, shlex_join
from ..core.coverage import write_coverage_row
from ..pipeline.helpers import adopted_variants
from .base import Step
from .compile import compile_test_set_smart

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
    ) -> None:
        self.libs_glob_cp = libs_glob_cp
        self.compiled_tests_dir = compiled_tests_dir
        self.sut_jar = sut_jar
        self.jacoco_agent_jar = jacoco_agent_jar
        self.tool_jar = tool_jar
        self.timeout_ms = timeout_ms
        self.java_opens = list(java_opens) if java_opens else list(JAVA_OPENS)

    def run_test(self, *, test_selector: str, jacoco_exec_file: Path, log_file: Path) -> str:
        cp = f"{self.compiled_tests_dir}:{self.libs_glob_cp}:{self.sut_jar}:{self.tool_jar}"

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
            return "timeout"
        if proc.returncode == 0:
            for line in out.splitlines():
                if line.startswith("[JUnit5TestRunner]"):
                    parts = line.strip().split()
                    skipped = 0
                    failed = 0
                    for p in parts:
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
                    if skipped > 0 and failed == 0:
                        return "skipped"
            return "passed"
        return "failed"


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
) -> str:
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
    ).run_test(
        test_selector=test_fqcn,
        jacoco_exec_file=jacoco_exec_file,
        log_file=log_file,
    )


def get_coverage_stats(
    jacoco_cli_jar: Path,
    jacoco_exec_file: Path,
    sut_jar: Path,
    target_fqcn: str,
    temp_dir: Path,
) -> Tuple[int, int, int, int]:
    """
    Returns (line_covered, line_total, branch_covered, branch_total)
    """
    if not jacoco_exec_file.exists():
        return 0, 0, 0, 0

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
                        return 0, 0, 0, 0
                else:
                    return 0, 0, 0, 0
            finally:
                shutil.rmtree(tmp_classes, ignore_errors=True)
        else:
            return 0, 0, 0, 0

    line_cov, line_tot = 0, 0
    branch_cov, branch_tot = 0, 0

    try:
        with csv_report.open("r", encoding="utf-8") as f:
            reader = csv.DictReader(f)
            for row in reader:
                # JaCoCo CSV columns: PACKAGE, CLASS, ... LINE_MISSED, LINE_COVERED, BRANCH_MISSED, BRANCH_COVERED
                pkg = row.get("PACKAGE", "")
                cls = row.get("CLASS", "")
                row_fqcn = f"{pkg}.{cls}" if pkg else cls
                if row_fqcn == target_fqcn:
                    lm = int(row.get("LINE_MISSED", 0))
                    lc = int(row.get("LINE_COVERED", 0))
                    bm = int(row.get("BRANCH_MISSED", 0))
                    bc = int(row.get("BRANCH_COVERED", 0))
                    line_cov += lc
                    line_tot += (lc + lm)
                    branch_cov += bc
                    branch_tot += (bc + bm)
    except Exception:
        pass

    return line_cov, line_tot, branch_cov, branch_tot


class RunStep(Step):
    step_names = ("run",)

    def run(self, ctx: "TargetContext") -> bool:
        if not self.should_run():
            return True
        jacoco_cli = self.pipeline.jacoco_agent.parent / "org.jacoco.cli-run-0.8.14.jar"
        wrote_manual = False
        wrote_auto = False

        for src in ctx.final_sources:
            pkg, cls = parse_package_and_class(src)
            if not cls:
                continue
            if "scaffolding" in cls.lower():
                continue

            test_fqcn = f"{pkg}.{cls}" if pkg else cls
            junit_ver = detect_junit_version(src)

            exec_file = self.pipeline.out_dir / f"{ctx.target_id}__{cls}.exec"
            run_log = self.pipeline.logs_dir / f"{ctx.target_id}__{cls}.run.log"

            print(f"[agt] Running (junit{junit_ver}): {test_fqcn}")
            status = run_one_test_with_jacoco(
                junit_version=junit_ver,
                test_fqcn=test_fqcn,
                sut_jar=ctx.sut_jar,
                libs_glob_cp=self.pipeline.args.libs_cp,
                compiled_tests_dir=ctx.target_build,
                jacoco_agent_jar=self.pipeline.jacoco_agent,
                jacoco_exec_file=exec_file,
                log_file=run_log,
                tool_jar=Path(self.pipeline.args.tool_jar),
                timeout_ms=self.pipeline.args.timeout_ms,
            )
            if status != "passed":
                print(f"[agt] Test failed: {test_fqcn} (see {run_log})")
            stats = (0, 0, 0, 0)
            if status == "passed" and jacoco_cli.exists():
                stats = get_coverage_stats(jacoco_cli, exec_file, ctx.sut_jar, ctx.fqcn, self.pipeline.build_dir)

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
                    status=status,
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
                    status=status,
                )
                wrote_auto = True

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
                max_rounds=self.pipeline.args.dep_rounds,
            )
            if not ok_adopt:
                print(
                    f'[agt] adopted-run: Skip (compile failed): repo="{ctx.repo}" fqcn="{ctx.fqcn}" variant="{variant}" (see {adopt_compile_log})'
                )
                print("[agt][ADOPTED-COMPILE-TAIL]\n" + tail_adopt)
                continue

            adopt_pkg, adopt_cls = parse_package_and_class(adopted_src)
            if not adopt_cls:
                print(f'[agt] adopted-run: Skip (cannot parse class): repo="{ctx.repo}" fqcn="{ctx.fqcn}" variant="{variant}"')
                continue

            adopted_fqcn = f"{adopt_pkg}.{adopt_cls}" if adopt_pkg else adopt_cls
            junit_ver = detect_junit_version(adopted_src)
            exec_file = self.pipeline.out_dir / f"{ctx.target_id}__{variant}.exec"
            run_log = self.pipeline.logs_dir / f"{ctx.target_id}.adopted.{variant}.run.log"
            print(f"[agt] Running adopted ({variant}, junit{junit_ver}): {adopted_fqcn}")
            status = run_one_test_with_jacoco(
                junit_version=junit_ver,
                test_fqcn=adopted_fqcn,
                sut_jar=ctx.sut_jar,
                libs_glob_cp=self.pipeline.args.libs_cp,
                compiled_tests_dir=adopted_build,
                jacoco_agent_jar=self.pipeline.jacoco_agent,
                jacoco_exec_file=exec_file,
                log_file=run_log,
                tool_jar=Path(self.pipeline.args.tool_jar),
                timeout_ms=self.pipeline.args.timeout_ms,
            )
            if status != "passed":
                print(f"[agt] adopted test failed: {adopted_fqcn} (see {run_log})")

            adopted_cov = (0, 0, 0, 0)
            if status == "passed" and (self.pipeline.jacoco_agent.parent / "org.jacoco.cli-run-0.8.14.jar").exists():
                adopted_cov = get_coverage_stats(
                    self.pipeline.jacoco_agent.parent / "org.jacoco.cli-run-0.8.14.jar",
                    exec_file,
                    ctx.sut_jar,
                    ctx.fqcn,
                    self.pipeline.build_dir,
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
                status=status,
            )
        return True
