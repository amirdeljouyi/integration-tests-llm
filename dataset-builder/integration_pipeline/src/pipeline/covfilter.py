from __future__ import annotations

import subprocess
from pathlib import Path
from typing import List, Optional, Tuple

from .common import ensure_dir, shlex_join, write_text

# Keep this consistent with how you run tests (your EvoSuite headless/module opens fix)
DEFAULT_JAVA_OPTS: List[str] = [
    "--add-opens=java.desktop/java.awt=ALL-UNNAMED",
    "--add-opens=java.base/java.lang=ALL-UNNAMED",
    "--add-opens=java.base/java.util=ALL-UNNAMED",
    "--add-opens=java.base/java.io=ALL-UNNAMED",
    "--add-opens=java.base/java.net=ALL-UNNAMED",
    "-Djava.awt.headless=true",
]


class CovfilterRunner:
    def __init__(self, *, java_opts: Optional[List[str]] = None) -> None:
        self.java_opts = list(java_opts) if java_opts else list(DEFAULT_JAVA_OPTS)

    def run_covfilter(
            self,
            *,
            coverage_filter_jar: Path,
            libs_glob_cp: str,
            test_classes_dir: Path,
            sut_classes_dir: Path,
            out_dir: Path,
            manual_test_fqcn: str,
            generated_test_fqcn: str,
            jacoco_agent_jar: Path,
            sut_cp_entry: Path,
            libs_dir_arg: Path,
            log_file: Path,
            extra_java_opts: Optional[List[str]] = None,
    ) -> Tuple[bool, str]:
        ensure_dir(out_dir)
        ensure_dir(log_file.parent)

        java_opts = list(self.java_opts)
        if extra_java_opts:
            java_opts.extend(extra_java_opts)

        cp = f"{libs_glob_cp}:{coverage_filter_jar}:{test_classes_dir}"

        cmd = (
                ["java"]
                + java_opts
                + [
                    "-cp",
                    cp,
                    "app.CoverageFilterApp",
                    "filter",
                    str(sut_classes_dir),
                    str(out_dir),
                    manual_test_fqcn,
                    generated_test_fqcn,
                    str(jacoco_agent_jar),
                    str(sut_cp_entry),
                    str(libs_dir_arg),
                    str(test_classes_dir),
                ]
        )

        proc = subprocess.run(
            cmd,
            stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT,
            text=True,
        )
        out = proc.stdout or ""
        write_text(log_file, f"$ {shlex_join(cmd)}\n\n{out}\n")
        tail = "\n".join(out.splitlines()[-80:])

        return (proc.returncode == 0, tail)

    def run_generate_reduced(
            self,
            *,
            coverage_filter_jar: Path,
            libs_glob_cp: str,
            original_test_java: Path,
            test_deltas_csv: Path,
            top_n: int,
            out_dir: Path,
            log_file: Path,
            extra_java_opts: Optional[List[str]] = None,
    ) -> Tuple[bool, str]:
        ensure_dir(out_dir)
        ensure_dir(log_file.parent)

        java_opts = list(self.java_opts)
        if extra_java_opts:
            java_opts.extend(extra_java_opts)

        cp = f"{libs_glob_cp}:{coverage_filter_jar}"

        cmd = (
                ["java"]
                + java_opts
                + [
                    "-cp",
                    cp,
                    "app.GenerateReducedAgtTestApp",
                    str(original_test_java),
                    str(test_deltas_csv),
                    str(top_n),
                    str(out_dir),
                    "true",
                ]
        )

        proc = subprocess.run(
            cmd,
            stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT,
            text=True,
        )
        out = proc.stdout or ""
        write_text(log_file, f"$ {shlex_join(cmd)}\n\n{out}\n")
        tail = "\n".join(out.splitlines()[-80:])

        return (proc.returncode == 0, tail)

def run_covfilter_app(
        *,
        coverage_filter_jar: Path,
        libs_glob_cp: str,
        test_classes_dir: Path,
        sut_classes_dir: Path,
        out_dir: Path,
        manual_test_fqcn: str,
        generated_test_fqcn: str,
        jacoco_agent_jar: Path,
        sut_cp_entry: Path,
        libs_dir_arg: Path,
        log_file: Path,
        extra_java_opts: Optional[List[str]] = None,
) -> Tuple[bool, str]:
    return CovfilterRunner(java_opts=extra_java_opts).run_covfilter(
        coverage_filter_jar=coverage_filter_jar,
        libs_glob_cp=libs_glob_cp,
        test_classes_dir=test_classes_dir,
        sut_classes_dir=sut_classes_dir,
        out_dir=out_dir,
        manual_test_fqcn=manual_test_fqcn,
        generated_test_fqcn=generated_test_fqcn,
        jacoco_agent_jar=jacoco_agent_jar,
        sut_cp_entry=sut_cp_entry,
        libs_dir_arg=libs_dir_arg,
        log_file=log_file,
    )


def run_generate_reduced_app(
        *,
        coverage_filter_jar: Path,
        libs_glob_cp: str,
        original_test_java: Path,
        test_deltas_csv: Path,
        top_n: int,
        out_dir: Path,
        log_file: Path,
        extra_java_opts: Optional[List[str]] = None,
) -> Tuple[bool, str]:
    return CovfilterRunner(java_opts=extra_java_opts).run_generate_reduced(
        coverage_filter_jar=coverage_filter_jar,
        libs_glob_cp=libs_glob_cp,
        original_test_java=original_test_java,
        test_deltas_csv=test_deltas_csv,
        top_n=top_n,
        out_dir=out_dir,
        log_file=log_file,
    )
