from __future__ import annotations

import subprocess
from pathlib import Path
from typing import List, Optional, Tuple, TYPE_CHECKING

from ..core.common import ensure_dir, shlex_join, write_text
from ..pipeline.helpers import (
    adopted_variants,
    find_scaffolding_source,
    fix_reduced_scaffolding_import,
    first_test_source_for_fqcn,
    libs_dir_from_glob,
    reduced_test_path,
    test_fqcn_from_source,
)
from .base import Step
from .compile import compile_test_set_smart

if TYPE_CHECKING:
    from ..pipeline.pipeline import TargetContext

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


class CovfilterStep(Step):
    step_names = ("filter",)

    def run(self, ctx: "TargetContext") -> bool:
        if not (self.pipeline.do_covfilter and self.should_run()):
            return True
        if self.pipeline.covfilter_allow is not None and (ctx.repo, ctx.fqcn) not in self.pipeline.covfilter_allow:
            print(f'[agt] covfilter: Skip (agt_line_covered=0): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
            return True
        if self.pipeline.covfilter_jar is None or not self.pipeline.covfilter_jar.exists():
            print(f'[agt] covfilter: Skip (missing --covfilter-jar): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
            return True
        if not ctx.manual_test_fqcn or not ctx.generated_test_fqcn:
            print(f'[agt] covfilter: Skip (need both manual+generated test fqcn): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
            return True

        if self.pipeline.sut_classes_dir is not None and self.pipeline.sut_classes_dir.exists():
            cov_classes_dir = self.pipeline.sut_classes_dir
        else:
            cov_classes_dir = ctx.sut_jar

        cov_out = self.pipeline.covfilter_out_root / ctx.target_id
        cov_log = self.pipeline.logs_dir / f"{ctx.target_id}.covfilter.log"
        libs_dir_arg = libs_dir_from_glob(self.pipeline.args.libs_cp)

        print(f'[agt] Running covfilter: repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
        ok_cov, cov_tail = run_covfilter_app(
            coverage_filter_jar=self.pipeline.covfilter_jar,
            libs_glob_cp=self.pipeline.args.libs_cp,
            test_classes_dir=ctx.target_build,
            sut_classes_dir=cov_classes_dir,
            out_dir=cov_out,
            manual_test_fqcn=ctx.manual_test_fqcn,
            generated_test_fqcn=ctx.generated_test_fqcn,
            jacoco_agent_jar=self.pipeline.jacoco_agent,
            sut_cp_entry=ctx.sut_jar,
            libs_dir_arg=libs_dir_arg,
            log_file=cov_log,
        )
        if not ok_cov:
            print(f'[agt] covfilter: FAIL (see {cov_log})')
            print("[agt][COVFILTER-TAIL]\n" + cov_tail)
        return True


class AdoptedFilterStep(Step):
    step_names = ("adopted-filter",)

    def run(self, ctx: "TargetContext") -> bool:
        if not (self.pipeline.do_covfilter and self.should_run()):
            return True
        if self.pipeline.covfilter_allow is not None and (ctx.repo, ctx.fqcn) not in self.pipeline.covfilter_allow:
            print(f'[agt] adopted-covfilter: Skip (agt_line_covered=0): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
            return True
        if self.pipeline.covfilter_jar is None or not self.pipeline.covfilter_jar.exists():
            print(f'[agt] adopted-covfilter: Skip (missing --covfilter-jar): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
            return True
        if not ctx.manual_sources:
            print(f'[agt] adopted-covfilter: Skip (missing manual test source): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
            return True

        variants = adopted_variants(self.pipeline.adopted_root, ctx.target_id)
        if not variants:
            print(f'[agt] adopted-covfilter: Skip (missing adopted tests): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
            return True

        for variant, adopted_src in variants:
            adopted_test_fqcn = test_fqcn_from_source(adopted_src)
            if not adopted_test_fqcn or not ctx.manual_test_fqcn:
                print(
                    f'[agt] adopted-covfilter: Skip (cannot parse test fqcn): repo="{ctx.repo}" fqcn="{ctx.fqcn}" variant="{variant}"'
                )
                continue
            if self.pipeline.sut_classes_dir is not None and self.pipeline.sut_classes_dir.exists():
                cov_classes_dir = self.pipeline.sut_classes_dir
            else:
                cov_classes_dir = ctx.sut_jar

            adopted_build = self.pipeline.build_dir / "adopted-filter-classes" / variant / ctx.target_id
            ensure_dir(adopted_build)
            adopt_compile_log = self.pipeline.logs_dir / f"{ctx.target_id}.adopted.{variant}.covfilter.compile.log"
            ok_adopt, adopt_tail, _ = compile_test_set_smart(
                java_files=ctx.manual_sources + [adopted_src],
                build_dir=adopted_build,
                libs_glob_cp=self.pipeline.args.libs_cp,
                sut_jar=ctx.sut_jar,
                log_file=adopt_compile_log,
                repo_root_for_deps=ctx.repo_root_for_deps,
                max_rounds=self.pipeline.args.dep_rounds,
            )
            if not ok_adopt:
                print(
                    f'[agt] adopted-covfilter: Skip (compile failed): repo="{ctx.repo}" fqcn="{ctx.fqcn}" variant="{variant}" (see {adopt_compile_log})'
                )
                print("[agt][ADOPTED-COMPILE-TAIL]\n" + adopt_tail)
                continue

            cov_out = self.pipeline.adopted_covfilter_out_root / variant / ctx.target_id
            cov_log = self.pipeline.logs_dir / f"{ctx.target_id}.adopted.{variant}.covfilter.log"
            libs_dir_arg = libs_dir_from_glob(self.pipeline.args.libs_cp)

            print(f'[agt] Running adopted covfilter ({variant}): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
            ok_cov, cov_tail = run_covfilter_app(
                coverage_filter_jar=self.pipeline.covfilter_jar,
                libs_glob_cp=self.pipeline.args.libs_cp,
                test_classes_dir=adopted_build,
                sut_classes_dir=cov_classes_dir,
                out_dir=cov_out,
                manual_test_fqcn=ctx.manual_test_fqcn,
                generated_test_fqcn=adopted_test_fqcn,
                jacoco_agent_jar=self.pipeline.jacoco_agent,
                sut_cp_entry=ctx.sut_jar,
                libs_dir_arg=libs_dir_arg,
                log_file=cov_log,
            )
            if not ok_cov:
                print(f'[agt] adopted-covfilter: FAIL (see {cov_log})')
                print("[agt][ADOPTED-COVFILTER-TAIL]\n" + cov_tail)
        return True


class ReduceStep(Step):
    step_names = ("reduce",)

    def run(self, ctx: "TargetContext") -> bool:
        if not self.should_run():
            return True
        reduced_root = Path(self.pipeline.args.reduced_out)
        test_deltas_csv = self.pipeline.covfilter_out_root / ctx.target_id / "test_deltas_all.csv"
        generated_test_src = first_test_source_for_fqcn(ctx.final_sources, ctx.generated_test_fqcn)
        if self.pipeline.covfilter_allow is not None and (ctx.repo, ctx.fqcn) not in self.pipeline.covfilter_allow:
            print(f'[agt] reduce: Skip (agt_line_covered=0): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
            return True
        if self.pipeline.covfilter_jar is None or not self.pipeline.covfilter_jar.exists():
            print(f'[agt] reduce: Skip (missing --covfilter-jar): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
            return True
        if not generated_test_src or not generated_test_src.exists():
            print(f'[agt] reduce: Skip (missing generated test source): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
            return True
        if not test_deltas_csv.exists():
            print(f'[agt] reduce: Skip (missing test_deltas_all.csv): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
            return True

        reduced_out = reduced_root / ctx.target_id
        reduced_log = self.pipeline.logs_dir / f"{ctx.target_id}.reduce.log"
        top_n = max(1, min(self.pipeline.args.reduce_max_tests, 100))
        print(f'[agt] Reducing AGT tests (top {top_n}): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
        ok_red, red_tail = run_generate_reduced_app(
            coverage_filter_jar=self.pipeline.covfilter_jar,
            libs_glob_cp=self.pipeline.args.libs_cp,
            original_test_java=generated_test_src,
            test_deltas_csv=test_deltas_csv,
            top_n=top_n,
            out_dir=reduced_out,
            log_file=reduced_log,
        )
        if not ok_red:
            print(f'[agt] reduce: FAIL (see {reduced_log})')
            print("[agt][REDUCE-TAIL]\n" + red_tail)
            return True

        reduced_src = reduced_test_path(reduced_root, ctx.target_id, generated_test_src, top_n)
        if reduced_src and reduced_src.exists():
            scaffolding_src = find_scaffolding_source(generated_test_src, ctx.final_sources)
            fix_reduced_scaffolding_import(reduced_src, scaffolding_src)
        return True


class AdoptedReduceStep(Step):
    step_names = ("adopted-reduce",)

    def run(self, ctx: "TargetContext") -> bool:
        if not self.should_run():
            return True
        if self.pipeline.covfilter_allow is not None and (ctx.repo, ctx.fqcn) not in self.pipeline.covfilter_allow:
            print(f'[agt] adopted-reduce: Skip (agt_line_covered=0): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
            return True
        if self.pipeline.covfilter_jar is None or not self.pipeline.covfilter_jar.exists():
            print(f'[agt] adopted-reduce: Skip (missing --covfilter-jar): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
            return True

        variants = adopted_variants(self.pipeline.adopted_root, ctx.target_id)
        if not variants:
            print(f'[agt] adopted-reduce: Skip (missing adopted tests): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
            return True

        for variant, adopted_src in variants:
            test_deltas_csv = self.pipeline.adopted_covfilter_out_root / variant / ctx.target_id / "test_deltas_all.csv"
            if not test_deltas_csv.exists():
                print(
                    f'[agt] adopted-reduce: Skip (missing test_deltas_all.csv): repo="{ctx.repo}" fqcn="{ctx.fqcn}" variant="{variant}"'
                )
                continue
            reduced_out = self.pipeline.adopted_reduced_out_root / variant / ctx.target_id
            reduced_log = self.pipeline.logs_dir / f"{ctx.target_id}.adopted.{variant}.reduce.log"
            top_n = max(1, min(self.pipeline.args.adopted_reduce_max_tests, 100))
            print(f'[agt] Reducing adopted tests ({variant}, top {top_n}): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
            ok_red, red_tail = run_generate_reduced_app(
                coverage_filter_jar=self.pipeline.covfilter_jar,
                libs_glob_cp=self.pipeline.args.libs_cp,
                original_test_java=adopted_src,
                test_deltas_csv=test_deltas_csv,
                top_n=top_n,
                out_dir=reduced_out,
                log_file=reduced_log,
            )
            if not ok_red:
                print(f'[agt] adopted-reduce: FAIL (see {reduced_log})')
                print("[agt][ADOPTED-REDUCE-TAIL]\n" + red_tail)
        return True
