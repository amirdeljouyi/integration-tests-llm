from __future__ import annotations

import csv
import shutil
from pathlib import Path
from typing import List, Optional, Sequence, Set, Tuple, TYPE_CHECKING

from ..core.common import (
    candidate_repo_class_dirs,
    ensure_dir,
    parse_package_and_class,
)
from ..core.java import (
    add_throws_exception_to_tests,
    add_throws_exception_to_error_methods,
    categorize_compile_problem,
    compile_test_set_smart,
    expand_same_package_support_sources,
    extract_compile_problem_detail,
    looks_like_fixable_test_source,
    prefer_repo_manual_sources,
    read_compile_log,
    remove_unused_imports,
)
from .base import Step

if TYPE_CHECKING:
    from ..pipeline.pipeline import TargetContext


def _source_labels(java_files: Sequence[Path]) -> str:
    labels: List[str] = []
    seen: Set[str] = set()
    for java_file in java_files:
        pkg, cls = parse_package_and_class(java_file)
        label = f"{pkg}.{cls}" if pkg and cls else (cls or java_file.stem)
        if label in seen:
            continue
        seen.add(label)
        labels.append(label)
    return "|".join(labels)


def _compile_summary_row(*, ctx: "TargetContext", variant: str, requested_sources: Sequence[Path], status: str) -> dict[str, str]:
    return {
        "repo": ctx.repo,
        "fqcn": ctx.fqcn,
        "variant": variant,
        "status": status,
        "selected": "false",
        "requested_test_count": str(len(requested_sources)),
        "requested_test_cases": _source_labels(requested_sources),
        "final_source_count": "0",
        "resolved_support_source_count": "0",
        "class_origin": "",
        "class_origin_detail": "",
        "problem_category": "",
        "problem_detail": "",
        "log_file": "",
    }


def _update_compile_summary_row(
    row: dict[str, str],
    *,
    ok: bool,
    requested_sources: Sequence[Path],
    final_sources: Sequence[Path],
    build_dir: Path,
    log_file: Path,
) -> None:
    row["status"] = "compiled" if ok else "failed"
    row["final_source_count"] = str(len(final_sources))
    requested_set = set(requested_sources)
    support_count = sum(1 for source in final_sources if source not in requested_set)
    row["resolved_support_source_count"] = str(support_count)
    row["log_file"] = str(log_file)
    if ok:
        row["class_origin"] = "fresh_javac"
        row["class_origin_detail"] = str(build_dir)
        row["problem_category"] = ""
        row["problem_detail"] = ""
        return

    log_text = read_compile_log(log_file)
    row["class_origin"] = ""
    row["class_origin_detail"] = ""
    row["problem_category"] = categorize_compile_problem(log_text)
    row["problem_detail"] = extract_compile_problem_detail(log_text)


def _append_compile_summary_rows(summary_csv: Path, rows: Sequence[dict[str, str]]) -> None:
    if not rows:
        return

    ensure_dir(summary_csv.parent)
    fieldnames = [
        "repo",
        "fqcn",
        "variant",
        "status",
        "selected",
        "requested_test_count",
        "requested_test_cases",
        "final_source_count",
        "resolved_support_source_count",
        "class_origin",
        "class_origin_detail",
        "problem_category",
        "problem_detail",
        "log_file",
    ]
    with summary_csv.open("a", newline="", encoding="utf-8") as handle:
        writer = csv.DictWriter(handle, fieldnames=fieldnames)
        writer.writerows(rows)

def _attempt_source_remediation(
    *,
    java_files: Sequence[Path],
    build_dir: Path,
    libs_glob_cp: str,
    sut_jar: Path,
    log_file: Path,
    repo_root_for_deps: Optional[Path],
    module_rel: str,
    build_tool: str,
    max_rounds: int,
) -> Tuple[bool, List[Path]]:
    candidates = [p for p in java_files if looks_like_fixable_test_source(p)]
    if not candidates:
        return False, list(java_files)

    compile_output = read_compile_log(log_file)
    for candidate in candidates:
        remove_unused_imports(str(candidate), False)
        add_throws_exception_to_tests(str(candidate), False)
        add_throws_exception_to_error_methods(str(candidate), compile_output, False)

    if build_dir.exists():
        shutil.rmtree(build_dir, ignore_errors=True)
    ensure_dir(build_dir)

    ok, _tail, compiled_sources = compile_test_set_smart(
        java_files=java_files,
        build_dir=build_dir,
        libs_glob_cp=libs_glob_cp,
        sut_jar=sut_jar,
        log_file=log_file,
        repo_root_for_deps=repo_root_for_deps,
        module_rel=module_rel,
        build_tool=build_tool,
        max_rounds=max_rounds,
    )
    return ok, compiled_sources


def _repo_class_rel_dir(pkg: str) -> Path:
    if not pkg:
        return Path()
    return Path(*pkg.split("."))


def _copy_matching_repo_classes(
    *,
    java_files: Sequence[Path],
    build_dir: Path,
    repo_root_for_deps: Optional[Path],
    module_rel: str,
    log_file: Path,
) -> Tuple[bool, str]:
    candidate_dirs = candidate_repo_class_dirs(repo_root_for_deps, module_rel)
    if not candidate_dirs:
        return False, ""

    copied: List[str] = []
    missing: List[str] = []
    used_roots: List[str] = []

    for java_file in java_files:
        pkg, cls = parse_package_and_class(java_file)
        if not cls:
            missing.append(str(java_file))
            continue

        rel_dir = _repo_class_rel_dir(pkg)
        matched = False
        for class_root in candidate_dirs:
            source_dir = class_root / rel_dir
            if not source_dir.exists():
                continue
            matches = sorted(source_dir.glob(f"{cls}*.class"))
            if not matches:
                continue

            dest_dir = build_dir / rel_dir
            ensure_dir(dest_dir)
            for class_file in matches:
                shutil.copy2(class_file, dest_dir / class_file.name)
            copied.append(f"{pkg}.{cls}" if pkg else cls)
            root_str = str(class_root)
            if root_str not in used_roots:
                used_roots.append(root_str)
            matched = True
            break

        if not matched:
            missing.append(f"{pkg}.{cls}" if pkg else cls)

    if missing:
        with log_file.open("a", encoding="utf-8", errors="ignore") as handle:
            handle.write("\n[agt] repo class fallback missing:\n")
            for item in missing:
                handle.write(f"{item}\n")
        return False, ""

    with log_file.open("a", encoding="utf-8", errors="ignore") as handle:
        handle.write("\n[agt] repo class fallback copied classes for:\n")
        for item in copied:
            handle.write(f"{item}\n")
        handle.write("[agt] repo class fallback roots:\n")
        for item in used_roots:
            handle.write(f"{item}\n")
    return True, "|".join(used_roots)


def _attempt_repo_class_fallback(
    *,
    requested_sources: Sequence[Path],
    build_dir: Path,
    repo_root_for_deps: Optional[Path],
    module_rel: str,
    log_file: Path,
) -> Tuple[bool, str]:
    if not requested_sources:
        return False, ""

    if build_dir.exists():
        shutil.rmtree(build_dir, ignore_errors=True)
    ensure_dir(build_dir)
    return _copy_matching_repo_classes(
        java_files=requested_sources,
        build_dir=build_dir,
        repo_root_for_deps=repo_root_for_deps,
        module_rel=module_rel,
        log_file=log_file,
    )


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

        manual_sources = prefer_repo_manual_sources(ctx.repo_root_for_deps, ctx.manual_sources)
        generated_sources = [s for s in ctx.sources if s not in set(ctx.manual_sources)]
        full_sources = expand_same_package_support_sources(
            ctx.repo_root_for_deps,
            [s for s in ctx.sources if s not in set(ctx.manual_sources)] + manual_sources,
        )
        manual_compile_sources = expand_same_package_support_sources(ctx.repo_root_for_deps, manual_sources)
        rows = {
            "full": _compile_summary_row(ctx=ctx, variant="full", requested_sources=ctx.sources, status="not_run"),
            "generated_only": _compile_summary_row(
                ctx=ctx,
                variant="generated_only",
                requested_sources=generated_sources,
                status="not_run" if generated_sources else "not_applicable",
            ),
            "manual_only": _compile_summary_row(
                ctx=ctx,
                variant="manual_only",
                requested_sources=ctx.manual_sources,
                status="not_run" if ctx.manual_sources else "not_applicable",
            ),
        }

        self._reset_build_dir(ctx.target_build)
        compile_log = self.pipeline.logs_dir / f"{ctx.target_id}.compile.log"
        ok, tail, compiled_sources = compile_test_set_smart(
            java_files=full_sources,
            build_dir=ctx.target_build,
            libs_glob_cp=self.pipeline.args.libs_cp,
            sut_jar=ctx.sut_jar,
            log_file=compile_log,
            repo_root_for_deps=ctx.repo_root_for_deps,
            module_rel=ctx.module_rel,
            build_tool=ctx.build_tool,
            max_rounds=self.pipeline.args.dep_rounds,
        )
        _update_compile_summary_row(
            rows["full"],
            ok=ok,
            requested_sources=full_sources,
            final_sources=compiled_sources,
            build_dir=ctx.target_build,
            log_file=compile_log,
        )

        if ok:
            ctx.final_sources = compiled_sources
            rows["full"]["selected"] = "true"
            _append_compile_summary_rows(self.pipeline.compile_summary_csv, list(rows.values()))
            return True

        ok_remediate, remediated_sources = _attempt_source_remediation(
            java_files=full_sources,
            build_dir=ctx.target_build,
            libs_glob_cp=self.pipeline.args.libs_cp,
            sut_jar=ctx.sut_jar,
            log_file=compile_log,
            repo_root_for_deps=ctx.repo_root_for_deps,
            module_rel=ctx.module_rel,
            build_tool=ctx.build_tool,
            max_rounds=self.pipeline.args.dep_rounds,
        )
        if ok_remediate:
            ctx.final_sources = remediated_sources
            rows["full"]["status"] = "remediated_compile"
            rows["full"]["selected"] = "true"
            rows["full"]["final_source_count"] = str(len(remediated_sources))
            rows["full"]["resolved_support_source_count"] = str(
                sum(1 for source in remediated_sources if source not in set(full_sources))
            )
            rows["full"]["class_origin"] = "source_remediation"
            rows["full"]["class_origin_detail"] = str(ctx.target_build)
            _append_compile_summary_rows(self.pipeline.compile_summary_csv, list(rows.values()))
            return True

        ok_repo, repo_origin = _attempt_repo_class_fallback(
            requested_sources=full_sources,
            build_dir=ctx.target_build,
            repo_root_for_deps=ctx.repo_root_for_deps,
            module_rel=ctx.module_rel,
            log_file=compile_log,
        )
        if ok_repo:
            ctx.final_sources = list(full_sources)
            rows["full"]["status"] = "repo_fallback"
            rows["full"]["selected"] = "true"
            rows["full"]["final_source_count"] = str(len(full_sources))
            rows["full"]["class_origin"] = "repo_class_fallback"
            rows["full"]["class_origin_detail"] = repo_origin
            _append_compile_summary_rows(self.pipeline.compile_summary_csv, list(rows.values()))
            return True

        print(f'[agt] compile failed for full source set: repo="{ctx.repo}" fqcn="{ctx.fqcn}" (see {compile_log})')
        print("[agt][COMPILE-TAIL]\n" + tail)

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
            _update_compile_summary_row(
                rows["generated_only"],
                ok=ok_gen,
                requested_sources=generated_sources,
                final_sources=compiled_gen,
                build_dir=ctx.target_build,
                log_file=gen_log,
            )
            if ok_gen:
                print(
                    f'[agt] Proceeding with generated-only compiled sources: repo="{ctx.repo}" fqcn="{ctx.fqcn}"'
                )
                ctx.final_sources = compiled_gen
                rows["generated_only"]["selected"] = "true"
                _append_compile_summary_rows(self.pipeline.compile_summary_csv, list(rows.values()))
                return True
            ok_remediate, remediated_gen = _attempt_source_remediation(
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
            if ok_remediate:
                print(
                    f'[agt] Proceeding with generated-only remediated sources: repo="{ctx.repo}" fqcn="{ctx.fqcn}"'
                )
                ctx.final_sources = remediated_gen
                rows["generated_only"]["status"] = "remediated_compile"
                rows["generated_only"]["selected"] = "true"
                rows["generated_only"]["final_source_count"] = str(len(remediated_gen))
                rows["generated_only"]["resolved_support_source_count"] = str(
                    sum(1 for source in remediated_gen if source not in set(generated_sources))
                )
                rows["generated_only"]["class_origin"] = "source_remediation"
                rows["generated_only"]["class_origin_detail"] = str(ctx.target_build)
                _append_compile_summary_rows(self.pipeline.compile_summary_csv, list(rows.values()))
                return True
            ok_repo, repo_origin = _attempt_repo_class_fallback(
                requested_sources=generated_sources,
                build_dir=ctx.target_build,
                repo_root_for_deps=ctx.repo_root_for_deps,
                module_rel=ctx.module_rel,
                log_file=gen_log,
            )
            if ok_repo:
                print(
                    f'[agt] Proceeding with generated-only repo-class fallback: repo="{ctx.repo}" fqcn="{ctx.fqcn}"'
                )
                ctx.final_sources = list(generated_sources)
                rows["generated_only"]["status"] = "repo_fallback"
                rows["generated_only"]["selected"] = "true"
                rows["generated_only"]["final_source_count"] = str(len(generated_sources))
                rows["generated_only"]["class_origin"] = "repo_class_fallback"
                rows["generated_only"]["class_origin_detail"] = repo_origin
                _append_compile_summary_rows(self.pipeline.compile_summary_csv, list(rows.values()))
                return True
            print(f'[agt] generated-only compile failed: repo="{ctx.repo}" fqcn="{ctx.fqcn}" (see {gen_log})')
            print("[agt][COMPILE-GENERATED-TAIL]\n" + tail_gen)

        if ctx.manual_sources:
            self._reset_build_dir(ctx.target_build)
            man_log = self.pipeline.logs_dir / f"{ctx.target_id}.compile.manual.log"
            ok_man, tail_man, compiled_man = compile_test_set_smart(
                java_files=manual_compile_sources,
                build_dir=ctx.target_build,
                libs_glob_cp=self.pipeline.args.libs_cp,
                sut_jar=ctx.sut_jar,
                log_file=man_log,
                repo_root_for_deps=ctx.repo_root_for_deps,
                module_rel=ctx.module_rel,
                build_tool=ctx.build_tool,
                max_rounds=self.pipeline.args.dep_rounds,
            )
            _update_compile_summary_row(
                rows["manual_only"],
                ok=ok_man,
                requested_sources=manual_sources,
                final_sources=compiled_man,
                build_dir=ctx.target_build,
                log_file=man_log,
            )
            if ok_man:
                print(
                    f'[agt] Proceeding with manual-only compiled sources: repo="{ctx.repo}" fqcn="{ctx.fqcn}"'
                )
                ctx.final_sources = compiled_man
                rows["manual_only"]["selected"] = "true"
                _append_compile_summary_rows(self.pipeline.compile_summary_csv, list(rows.values()))
                return True
            ok_remediate, remediated_man = _attempt_source_remediation(
                java_files=manual_compile_sources,
                build_dir=ctx.target_build,
                libs_glob_cp=self.pipeline.args.libs_cp,
                sut_jar=ctx.sut_jar,
                log_file=man_log,
                repo_root_for_deps=ctx.repo_root_for_deps,
                module_rel=ctx.module_rel,
                build_tool=ctx.build_tool,
                max_rounds=self.pipeline.args.dep_rounds,
            )
            if ok_remediate:
                print(
                    f'[agt] Proceeding with manual-only remediated sources: repo="{ctx.repo}" fqcn="{ctx.fqcn}"'
                )
                ctx.final_sources = remediated_man
                rows["manual_only"]["status"] = "remediated_compile"
                rows["manual_only"]["selected"] = "true"
                rows["manual_only"]["final_source_count"] = str(len(remediated_man))
                rows["manual_only"]["resolved_support_source_count"] = str(
                    sum(1 for source in remediated_man if source not in set(manual_sources))
                )
                rows["manual_only"]["class_origin"] = "source_remediation"
                rows["manual_only"]["class_origin_detail"] = str(ctx.target_build)
                _append_compile_summary_rows(self.pipeline.compile_summary_csv, list(rows.values()))
                return True
            ok_repo, repo_origin = _attempt_repo_class_fallback(
                requested_sources=manual_sources,
                build_dir=ctx.target_build,
                repo_root_for_deps=ctx.repo_root_for_deps,
                module_rel=ctx.module_rel,
                log_file=man_log,
            )
            if ok_repo:
                print(
                    f'[agt] Proceeding with manual-only repo-class fallback: repo="{ctx.repo}" fqcn="{ctx.fqcn}"'
                )
                ctx.final_sources = list(manual_sources)
                rows["manual_only"]["status"] = "repo_fallback"
                rows["manual_only"]["selected"] = "true"
                rows["manual_only"]["final_source_count"] = str(len(manual_sources))
                rows["manual_only"]["class_origin"] = "repo_class_fallback"
                rows["manual_only"]["class_origin_detail"] = repo_origin
                _append_compile_summary_rows(self.pipeline.compile_summary_csv, list(rows.values()))
                return True
            print(f'[agt] manual-only compile failed: repo="{ctx.repo}" fqcn="{ctx.fqcn}" (see {man_log})')
            print("[agt][COMPILE-MANUAL-TAIL]\n" + tail_man)

        print(f'[agt] Skip (compile failed): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
        _append_compile_summary_rows(self.pipeline.compile_summary_csv, list(rows.values()))
        return False
