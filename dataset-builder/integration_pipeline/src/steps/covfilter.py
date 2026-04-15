from __future__ import annotations

import csv
import hashlib
import os
import re
import shlex
import shutil
import subprocess
import zipfile
from dataclasses import dataclass
from pathlib import Path
from typing import List, Optional, Tuple, TYPE_CHECKING

from ..core.common import ensure_dir, parse_package_and_class, shlex_join, write_text
from ..core.java import (
    compile_test_set_smart,
    resolve_external_runtime_classpath_from_output,
    resolve_repo_runtime_classpath,
)
from ..pipeline.config import covfilter_candidate_out_dirs, covfilter_variant_out_dir, covfilter_variant_summary_csv
from ..pipeline.sanitize import (
    EvoSuitePair,
    clear_pair_root,
    materialize_sanitized_pair,
    sanitize_compare_summary_csv,
    sanitize_compare_target_dir,
    variant_test_fqcn,
    variant_pair,
)
from ..pipeline.helpers import adopted_variants, first_test_source_for_fqcn, libs_dir_from_glob, test_fqcn_from_source
from .llm import namespace_non_primary_type_name_file, normalize_primary_class_name_file
from .base import Step

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
    "-Dnet.bytebuddy.experimental=true",
]

_COVFILTER_JAVA_FEATURE_VERSION: Optional[int] = None
_COVFILTER_RUNTIME_RETRY_LIMIT = 12
_COVFILTER_SUBPROCESS_TIMEOUT_MS = 600_000


def _covfilter_java_feature_version() -> int:
    global _COVFILTER_JAVA_FEATURE_VERSION
    if _COVFILTER_JAVA_FEATURE_VERSION is not None:
        return _COVFILTER_JAVA_FEATURE_VERSION

    feature_version = 17
    try:
        proc = subprocess.run(
            ["java", "-XshowSettings:properties", "-version"],
            stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT,
            text=True,
            env=_covfilter_java_env(),
        )
        for line in (proc.stdout or "").splitlines():
            marker = "java.specification.version ="
            if marker not in line:
                continue
            feature_version = int(line.split("=", 1)[1].strip().split(".", 1)[0])
            break
    except (OSError, ValueError, subprocess.SubprocessError):
        pass

    _COVFILTER_JAVA_FEATURE_VERSION = feature_version
    return feature_version


def _effective_covfilter_timeout_ms(timeout_ms: Optional[int]) -> Optional[int]:
    if timeout_ms is None or timeout_ms <= 0:
        return _COVFILTER_SUBPROCESS_TIMEOUT_MS
    return max(timeout_ms, _COVFILTER_SUBPROCESS_TIMEOUT_MS)


def _covfilter_java_wrapper_dir(build_dir: Path) -> Path:
    return build_dir / "covfilter-java-bin"


def _ensure_covfilter_java_wrapper(build_dir: Path) -> Optional[Path]:
    real_java = shutil.which("java")
    if not real_java:
        return None

    wrapper_dir = _covfilter_java_wrapper_dir(build_dir)
    ensure_dir(wrapper_dir)
    wrapper_path = wrapper_dir / "java"
    script = (
        "#!/bin/sh\n"
        f"exec {shlex.quote(real_java)} -Dnet.bytebuddy.experimental=true \"$@\"\n"
    )
    current = ""
    if wrapper_path.exists():
        try:
            current = wrapper_path.read_text(encoding="utf-8", errors="ignore")
        except OSError:
            current = ""
    if current != script:
        write_text(wrapper_path, script)
        wrapper_path.chmod(0o755)
    return wrapper_dir


def _covfilter_java_env(build_dir: Optional[Path] = None) -> dict[str, str]:
    env = os.environ.copy()
    if build_dir is not None:
        wrapper_dir = _ensure_covfilter_java_wrapper(build_dir)
        if wrapper_dir is not None:
            env["PATH"] = f"{wrapper_dir}{os.pathsep}{env.get('PATH', '')}".rstrip(os.pathsep)
            env.pop("JDK_JAVA_OPTIONS", None)
            return env

    required_flag = "-Dnet.bytebuddy.experimental=true"
    existing = (env.get("JDK_JAVA_OPTIONS") or "").strip()
    if required_flag not in existing.split():
        env["JDK_JAVA_OPTIONS"] = f"{existing} {required_flag}".strip()
    return env


class CovfilterRunner:
    def __init__(
        self,
        *,
        java_opts: Optional[List[str]] = None,
        build_dir: Optional[Path] = None,
        timeout_ms: Optional[int] = None,
    ) -> None:
        self.java_opts = list(java_opts) if java_opts else list(DEFAULT_JAVA_OPTS)
        self.build_dir = build_dir
        self.timeout_ms = timeout_ms

    def run_covfilter(
            self,
            *,
            coverage_filter_jar: Path,
            libs_glob_cp: str,
            extra_runtime_cp: str,
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

        cp_parts = [libs_glob_cp, str(coverage_filter_jar), str(test_classes_dir)]
        if extra_runtime_cp.strip():
            cp_parts.append(extra_runtime_cp.strip())
        cp = ":".join(part for part in cp_parts if part)

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

        timeout_seconds = (self.timeout_ms / 1000) if self.timeout_ms and self.timeout_ms > 0 else None
        try:
            proc = subprocess.run(
                cmd,
                stdout=subprocess.PIPE,
                stderr=subprocess.STDOUT,
                text=True,
                env=_covfilter_java_env(self.build_dir),
                timeout=timeout_seconds,
            )
            out = proc.stdout or ""
            ok = proc.returncode == 0
        except subprocess.TimeoutExpired as exc:
            out = exc.output or ""
            if isinstance(out, bytes):
                out = out.decode("utf-8", errors="replace")
            out = (
                f"{out}\n[agt] covfilter subprocess timed out after {self.timeout_ms} ms.\n"
                if self.timeout_ms and self.timeout_ms > 0
                else f"{out}\n[agt] covfilter subprocess timed out.\n"
            )
            ok = False
        write_text(log_file, f"$ {shlex_join(cmd)}\n\n{out}\n")
        tail = "\n".join(out.splitlines()[-80:])

        return ok, tail

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

        timeout_seconds = (self.timeout_ms / 1000) if self.timeout_ms and self.timeout_ms > 0 else None
        try:
            proc = subprocess.run(
                cmd,
                stdout=subprocess.PIPE,
                stderr=subprocess.STDOUT,
                text=True,
                env=_covfilter_java_env(self.build_dir),
                timeout=timeout_seconds,
            )
            out = proc.stdout or ""
            ok = proc.returncode == 0
        except subprocess.TimeoutExpired as exc:
            out = exc.output or ""
            if isinstance(out, bytes):
                out = out.decode("utf-8", errors="replace")
            out = (
                f"{out}\n[agt] reduced-test subprocess timed out after {self.timeout_ms} ms.\n"
                if self.timeout_ms and self.timeout_ms > 0
                else f"{out}\n[agt] reduced-test subprocess timed out.\n"
            )
            ok = False
        write_text(log_file, f"$ {shlex_join(cmd)}\n\n{out}\n")
        tail = "\n".join(out.splitlines()[-80:])

        return ok, tail

def run_covfilter_app(
        *,
        coverage_filter_jar: Path,
        libs_glob_cp: str,
        extra_runtime_cp: str,
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
    return CovfilterRunner(
        java_opts=extra_java_opts,
        build_dir=Path.cwd() / "build" / "agt",
    ).run_covfilter(
        coverage_filter_jar=coverage_filter_jar,
        libs_glob_cp=libs_glob_cp,
        extra_runtime_cp=extra_runtime_cp,
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


def _probe_covfilter_list_tests_output(
    *,
    coverage_filter_jar: Path,
    libs_dir: Path,
    test_classes_dir: Path,
    sut_classes_dir: Path,
    test_fqcns: List[str],
    timeout_ms: Optional[int],
) -> str:
    cp_entries = [str(path) for path in sorted(libs_dir.glob("*.jar")) if path.is_file()]
    cp_entries.extend([str(sut_classes_dir), str(test_classes_dir), str(coverage_filter_jar)])
    classpath = ":".join(entry for entry in cp_entries if entry)
    if not classpath:
        return ""

    timeout_seconds = (timeout_ms / 1000) if timeout_ms and timeout_ms > 0 else None
    outputs: List[str] = []
    for test_fqcn in test_fqcns:
        if not test_fqcn:
            continue
        cmd = [
            "java",
            *DEFAULT_JAVA_OPTS,
            "-cp",
            classpath,
            "app.ListTests",
            test_fqcn,
        ]
        try:
            proc = subprocess.run(
                cmd,
                stdout=subprocess.PIPE,
                stderr=subprocess.STDOUT,
                text=True,
                env=_covfilter_java_env(Path.cwd() / "build" / "agt"),
                timeout=timeout_seconds,
            )
            outputs.append(f"$ {shlex_join(cmd)}\n{proc.stdout or ''}".rstrip())
        except (OSError, subprocess.SubprocessError) as exc:
            outputs.append(f"$ {shlex_join(cmd)}\n{exc}".rstrip())
    return "\n\n".join(output for output in outputs if output).strip()


def covfilter_output_exists(out_dir: Path) -> bool:
    return (out_dir / "test_deltas_all.csv").exists()


def _covfilter_summary_csv(pipeline, variant: str) -> Path:
    return covfilter_variant_summary_csv(
        pipeline.covfilter_out_root,
        pipeline.adopted_covfilter_out_root,
        variant,
        pipeline.args.includes,
    )


def _covfilter_status_cache(pipeline) -> dict[tuple[str, str, str], str]:
    cache = getattr(pipeline, "_covfilter_status_cache", None)
    if cache is not None:
        return cache

    cache = {}
    for variant in ("auto", "auto-original", "adopted", "agentic"):
        csv_path = _covfilter_summary_csv(pipeline, variant)
        if not csv_path.exists():
            continue
        try:
            with csv_path.open("r", encoding="utf-8", newline="") as handle:
                reader = csv.DictReader(handle)
                for row in reader:
                    repo = (row.get("repo", "") or "").strip()
                    fqcn = (row.get("fqcn", "") or "").strip()
                    row_variant = (row.get("variant", "") or variant).strip() or variant
                    status = (row.get("status", "") or "").strip()
                    if repo and fqcn and row_variant:
                        cache[(repo, fqcn, row_variant)] = status
        except OSError:
            continue

    setattr(pipeline, "_covfilter_status_cache", cache)
    return cache


def _latest_covfilter_status(pipeline, *, repo: str, fqcn: str, variant: str) -> str:
    return _covfilter_status_cache(pipeline).get((repo, fqcn, variant), "")


def _existing_covfilter_out_dir(pipeline, variant: str, target_id: str) -> Optional[Path]:
    for candidate in covfilter_candidate_out_dirs(
        pipeline.covfilter_out_root,
        pipeline.adopted_covfilter_out_root,
        variant,
        target_id,
    ):
        if covfilter_output_exists(candidate):
            return candidate
    return None


def _merge_classpath_strings(*parts: str) -> str:
    merged: List[str] = []
    seen = set()
    artifact_positions: dict[str, int] = {}
    for part in parts:
        if not part:
            continue
        for entry in part.split(":"):
            candidate = entry.strip()
            if not candidate or candidate in seen:
                continue
            artifact_key = _covfilter_runtime_jar_artifact_key(candidate)
            if artifact_key and artifact_key in artifact_positions:
                existing_index = artifact_positions[artifact_key]
                existing_candidate = merged[existing_index]
                if _covfilter_runtime_jar_preference(candidate) > _covfilter_runtime_jar_preference(existing_candidate):
                    merged[existing_index] = candidate
                    seen.add(candidate)
                continue
            seen.add(candidate)
            if artifact_key:
                artifact_positions[artifact_key] = len(merged)
            merged.append(candidate)
    return ":".join(merged)


def _uses_regular_mockito_evosuite_runtime(text: str) -> bool:
    return (
        "org.mockito.Mockito" in text
        and "new ViolatedAssumptionAnswer()" in text
        and "org.evosuite.shaded.org.mockito.stubbing.Answer" not in text
    )


def _filter_evosuite_standalone_runtime(classpath: str) -> str:
    filtered: List[str] = []
    for entry in classpath.split(":"):
        candidate = entry.strip()
        if not candidate:
            continue
        if Path(candidate).name.startswith("evosuite-standalone-runtime-"):
            continue
        filtered.append(candidate)
    return ":".join(filtered)


def _sources_use_regular_mockito_evosuite_runtime(sources: List[Path]) -> bool:
    for source in sources:
        try:
            text = source.read_text(encoding="utf-8", errors="ignore")
        except OSError:
            continue
        if _uses_regular_mockito_evosuite_runtime(text):
            return True
    return False


def _covfilter_runtime_jar_artifact_key(entry: str) -> str:
    path = Path(entry)
    if not path.exists() or not path.is_file() or path.suffix != ".jar":
        return ""

    resolved = path.resolve()
    parts = resolved.parts
    if ".m2" in parts and "repository" in parts and resolved.parent.parent.name:
        return resolved.parent.parent.name
    if ".gradle" in parts and "files-2.1" in parts and resolved.parent.parent.parent.name:
        return resolved.parent.parent.parent.name

    match = re.match(r"^(?P<artifact>.+)-\d", path.stem)
    return (match.group("artifact") if match else path.stem).lower()


def _covfilter_runtime_jar_preference(entry: str) -> tuple[int, tuple]:
    path = Path(entry)
    if not path.exists() or not path.is_file() or path.suffix != ".jar":
        return (0, ())

    resolved = path.resolve()
    parts = resolved.parts
    version = ""
    repository_rank = 0
    if ".m2" in parts and "repository" in parts:
        repository_rank = 2
        version = resolved.parent.name
    elif ".gradle" in parts and "files-2.1" in parts:
        repository_rank = 1
        version = resolved.parent.parent.name
    else:
        match = re.match(r"^.+-(?P<version>\d[^/]*)$", path.stem)
        if match:
            version = match.group("version")

    return repository_rank, _covfilter_runtime_version_sort_key(version)


def _covfilter_runtime_version_sort_key(version: str) -> tuple:
    parts = re.split(r"([0-9]+)", version or "")
    key = []
    for part in parts:
        if not part:
            continue
        if part.isdigit():
            key.append((1, int(part)))
        else:
            key.append((0, part.lower()))
    return tuple(key)


def _test_framework_runtime_cp(test_src: Path) -> str:
    try:
        text = test_src.read_text(encoding="utf-8", errors="ignore")
    except OSError:
        return ""

    jars: List[str] = []
    seen_jars = set()
    fallback_targets: List[str] = []

    def add_jar(group_path: str, artifact_name: str, version: str = "") -> None:
        jar_path = _m2_artifact_jar(group_path, artifact_name, version)
        if not jar_path or jar_path in seen_jars:
            return
        seen_jars.add(jar_path)
        jars.append(jar_path)

    if "org.evosuite.runtime" in text:
        add_jar("org/utgen", "evosuite-standalone-runtime")
        for artifact_name in ("asm", "asm-commons", "asm-tree", "asm-analysis", "asm-util"):
            add_jar("org/ow2/asm", artifact_name)
        add_jar("org/slf4j", "slf4j-api")
    if "org.testng" in text:
        add_jar("org/testng", "testng")
        add_jar("com/beust", "jcommander")
        fallback_targets.append("org.junit.support.testng.engine.TestNGTestEngine")
    if "org.junit.jupiter" in text:
        add_jar("io/kotest", "kotest-common-jvm")
        add_jar("io/kotest", "kotest-framework-discovery-jvm")
    if "MockitoExtension" in text or "org.mockito.Mock" in text:
        add_jar("org/mockito", "mockito-core")
        add_jar("org/mockito", "mockito-junit-jupiter")
    if "org.jboss.arquillian.junit.Arquillian" in text:
        fallback_targets.extend(
            [
                "org.jboss.arquillian.junit.Arquillian",
                "org.jboss.arquillian.container.test.api.Deployment",
                "org.jboss.arquillian.core.impl.loadable.LoadableExtensionLoader",
                "org.jboss.arquillian.test.impl.EventTestRunnerAdaptor",
                "org.jboss.shrinkwrap.api.asset.Asset",
                "org.jboss.shrinkwrap.api.ShrinkWrap",
                "org.jboss.shrinkwrap.api.spec.JavaArchive",
            ]
        )
    fallback_cp = ""
    if fallback_targets:
        fallback_cp = resolve_external_runtime_classpath_from_output(
            "\n".join(f"ClassNotFoundException: {target.replace('.', '/')}" for target in fallback_targets)
        )
    framework_cp = _merge_classpath_strings(":".join(jars), fallback_cp)
    if _uses_regular_mockito_evosuite_runtime(text):
        framework_cp = _filter_evosuite_standalone_runtime(framework_cp)
    return framework_cp


def _framework_runtime_cp_for_sources(sources: List[Path]) -> str:
    merged = ""
    for source in sources:
        merged = _merge_classpath_strings(merged, _test_framework_runtime_cp(source))
    if _sources_use_regular_mockito_evosuite_runtime(sources):
        merged = _filter_evosuite_standalone_runtime(merged)
    return merged


def _effective_compile_libs_cp_for_sources(*, libs_glob_cp: str, sources: List[Path]) -> str:
    effective_cp = _merge_classpath_strings(
        libs_glob_cp,
        _framework_runtime_cp_for_sources(sources),
        _spring_runtime_cp_for_sources(sources),
    )
    if _sources_use_regular_mockito_evosuite_runtime(sources):
        effective_cp = _filter_evosuite_standalone_runtime(effective_cp)
    return effective_cp


def _latest_matching_dependency_jars(group_path: str, artifact_names: List[str]) -> str:
    jars: List[str] = []
    for artifact_name in artifact_names:
        matches = sorted((Path.home() / ".m2" / "repository" / group_path / artifact_name).glob(f"*/{artifact_name}-*.jar"))
        if matches:
            jars.append(str(matches[-1]))
    return ":".join(jars)


def _latest_m2_artifact_version(group_path: str, artifact_name: str) -> str:
    artifact_root = Path.home() / ".m2" / "repository" / group_path / artifact_name
    if not artifact_root.exists():
        return ""
    versions = sorted((path.name for path in artifact_root.iterdir() if path.is_dir()))
    return versions[-1] if versions else ""


def _m2_artifact_jar(group_path: str, artifact_name: str, version: str = "") -> str:
    artifact_root = Path.home() / ".m2" / "repository" / group_path / artifact_name
    if not artifact_root.exists():
        return ""
    if version:
        matches = sorted((artifact_root / version).glob(f"{artifact_name}-*.jar"))
        if matches:
            return str(matches[-1])
    matches = sorted(artifact_root.glob(f"*/{artifact_name}-*.jar"))
    return str(matches[-1]) if matches else ""


def _spring_runtime_cp_for_sources(sources: List[Path]) -> str:
    try:
        if not any("org.springframework" in source.read_text(encoding="utf-8", errors="ignore") for source in sources):
            return ""
    except OSError:
        return ""

    spring_artifacts = [
        "spring-aop",
        "spring-beans",
        "spring-context",
        "spring-core",
        "spring-expression",
        "spring-jcl",
        "spring-test",
        "spring-web",
        "spring-webmvc",
    ]
    anchor_version = (
        _latest_m2_artifact_version("org/springframework", "spring-webmvc")
        or _latest_m2_artifact_version("org/springframework", "spring-test")
        or _latest_m2_artifact_version("org/springframework", "spring-context")
    )

    jars: List[str] = []
    seen = set()

    for artifact_name in spring_artifacts:
        jar_path = _m2_artifact_jar("org/springframework", artifact_name, anchor_version)
        if not jar_path:
            jar_path = _m2_artifact_jar("org/springframework", artifact_name)
        if jar_path and jar_path not in seen:
            seen.add(jar_path)
            jars.append(jar_path)

    for group_path, artifact_name in (
        ("jakarta/annotation", "jakarta.annotation-api"),
        ("jakarta/servlet", "jakarta.servlet-api"),
    ):
        jar_path = _m2_artifact_jar(group_path, artifact_name)
        if jar_path and jar_path not in seen:
            seen.add(jar_path)
            jars.append(jar_path)

    return ":".join(jars)


def _classpath_dir_entries(classpath: str) -> List[Path]:
    entries: List[Path] = []
    seen = set()
    for raw_entry in classpath.split(":"):
        entry = raw_entry.strip()
        if not entry:
            continue
        path = Path(entry)
        if not path.exists() or not path.is_dir():
            continue
        resolved = path.resolve()
        if resolved in seen:
            continue
        seen.add(resolved)
        entries.append(resolved)
    return entries


def _classpath_jar_entries(classpath: str) -> List[Path]:
    entries: List[Path] = []
    seen = set()
    for raw_entry in classpath.split(":"):
        entry = raw_entry.strip()
        if not entry:
            continue
        path = Path(entry)
        if not path.exists() or not path.is_file() or path.suffix != ".jar":
            continue
        resolved = path.resolve()
        if resolved in seen:
            continue
        seen.add(resolved)
        entries.append(resolved)
    return entries


def _covfilter_safe_jar_name(path: Path, *, prefix: str = "extra") -> str:
    stem = "".join(ch if ch.isalnum() else "_" for ch in path.stem).strip("_") or "dir"
    digest = hashlib.sha1(str(path.resolve()).encode("utf-8")).hexdigest()[:12]
    return f"{prefix}_{stem}_{digest}.jar"


def _is_covfilter_main_output_dir(path: Path) -> bool:
    normalized = path.as_posix().rstrip("/")
    return normalized.endswith(
        (
            "/target/classes",
            "/target/generated-classes",
            "/target/generated-classes/jacoco",
            "/build/classes/java/main",
            "/build/classes/kotlin/main",
            "/build/resources/main",
        )
    )


def _should_skip_covfilter_runtime_entry(entry_name: str) -> bool:
    normalized = entry_name.replace("\\", "/").lstrip("/")
    lowered = normalized.lower()
    if not normalized:
        return True
    if lowered.endswith(".scl.lombok") or lowered.startswith("scl.lombok/") or "/scl.lombok/" in lowered:
        return True
    if lowered.startswith("meta-inf/license/") or lowered.startswith("meta-inf/licenses/"):
        return True
    if lowered.startswith("meta-inf/"):
        base = lowered.rsplit("/", 1)[-1]
        if base in {
            "license",
            "license.txt",
            "license.md",
            "license-notice.md",
            "notice",
            "notice.txt",
            "dependencies",
            "index.list",
            "al2.0",
            "lgpl2.1",
        }:
            return True
        if base.endswith((".sf", ".rsa", ".dsa", ".ec")):
            return True
    return False


def _write_sanitized_covfilter_jar(*, source: Path, target: Path) -> None:
    ensure_dir(target.parent)
    seen_entries = set()
    with zipfile.ZipFile(source) as input_jar, zipfile.ZipFile(target, "w", compression=zipfile.ZIP_DEFLATED) as output_jar:
        for entry in input_jar.infolist():
            if entry.is_dir():
                continue
            entry_name = entry.filename.replace("\\", "/").lstrip("/")
            if _should_skip_covfilter_runtime_entry(entry_name) or entry_name in seen_entries:
                continue
            seen_entries.add(entry_name)
            output_jar.writestr(entry_name, input_jar.read(entry))


def _write_directory_as_covfilter_jar(*, source_dir: Path, target: Path) -> None:
    ensure_dir(target.parent)
    seen_entries = set()
    with zipfile.ZipFile(target, "w", compression=zipfile.ZIP_DEFLATED) as output_jar:
        for path in sorted(source_dir.rglob("*")):
            if not path.is_file():
                continue
            rel_name = path.relative_to(source_dir).as_posix()
            if rel_name.startswith(".agt-source-stamps/"):
                continue
            if _should_skip_covfilter_runtime_entry(rel_name) or rel_name in seen_entries:
                continue
            seen_entries.add(rel_name)
            output_jar.write(path, arcname=rel_name)


def _merge_covfilter_class_dirs(*, source_dirs: List[Path], merged_dir: Path) -> Optional[Path]:
    if not source_dirs:
        return None
    if merged_dir.exists():
        shutil.rmtree(merged_dir, ignore_errors=True)
    ensure_dir(merged_dir)
    copied_any = False
    selected_versions: dict[Path, int] = {}
    java_feature_version = _covfilter_java_feature_version()
    for source_dir in source_dirs:
        for path in sorted(source_dir.rglob("*")):
            if not path.is_file() or path.suffix != ".class":
                continue
            rel_name, version = _normalized_covfilter_sut_entry_name(path.relative_to(source_dir).as_posix())
            if version > java_feature_version:
                continue
            target = merged_dir / Path(rel_name)
            previous_version = selected_versions.get(target, -1)
            if previous_version > version:
                continue
            source_is_instrumented = _covfilter_class_is_instrumented(path)
            if target.exists():
                target_is_instrumented = _covfilter_class_is_instrumented(target)
                if version > previous_version or (target_is_instrumented and not source_is_instrumented):
                    shutil.copy2(path, target)
                    selected_versions[target] = version
                    copied_any = True
                continue
            ensure_dir(target.parent)
            shutil.copy2(path, target)
            selected_versions[target] = version
            copied_any = True
    return merged_dir if copied_any else None


def _covfilter_class_is_instrumented(path: Path) -> bool:
    try:
        data = path.read_bytes()
    except OSError:
        return False
    return b"$jacocoData" in data or b"$jacocoInit" in data


def _augment_covfilter_source_dirs(source_dirs: List[Path]) -> List[Path]:
    augmented: List[Path] = []
    seen = set()

    def add(path: Path) -> None:
        resolved = path.resolve()
        if not path.is_dir() or resolved in seen:
            return
        seen.add(resolved)
        augmented.append(path)

    for source_dir in source_dirs:
        add(source_dir)
        if source_dir.name == "classes" and source_dir.parent.name == "target":
            add(source_dir.parent / "generated-classes")
            add(source_dir.parent / "generated-classes" / "jacoco")
    return augmented


def _normalized_covfilter_sut_entry_name(entry_name: str) -> tuple[str, int]:
    normalized = entry_name.replace("\\", "/").lstrip("/")
    for prefix in ("BOOT-INF/classes/", "WEB-INF/classes/", "APP-INF/classes/"):
        if normalized.startswith(prefix):
            normalized = normalized[len(prefix):]
            break
    if normalized.startswith("META-INF/versions/"):
        remainder = normalized[len("META-INF/versions/") :]
        version_text, _, rel_name = remainder.partition("/")
        if version_text.isdigit() and rel_name:
            return rel_name, int(version_text)
    return normalized, 0


def _covfilter_has_multi_release_layout(path: Path) -> bool:
    versions_dir = path / "META-INF" / "versions"
    return versions_dir.is_dir() and any(versions_dir.rglob("*.class"))


def _extract_covfilter_sut_classes(*, sut_jar: Path, target_dir: Path) -> Path:
    if target_dir.exists():
        shutil.rmtree(target_dir, ignore_errors=True)
    ensure_dir(target_dir)
    selected_versions: dict[str, int] = {}
    java_feature_version = _covfilter_java_feature_version()
    with zipfile.ZipFile(sut_jar) as jar_file:
        for entry in jar_file.infolist():
            if entry.is_dir():
                continue
            entry_name, version = _normalized_covfilter_sut_entry_name(entry.filename)
            lowered = entry_name.lower()
            is_class = lowered.endswith(".class")
            is_service_resource = lowered.startswith("meta-inf/services/")
            if (
                not entry_name
                or version > java_feature_version
                or (not is_class and not is_service_resource)
                or (lowered.startswith("meta-inf/") and not is_service_resource)
                or lowered.endswith(".scl.lombok")
                or lowered.startswith("scl.lombok/")
                or "/scl.lombok/" in lowered
            ):
                continue
            previous_version = selected_versions.get(entry_name, -1)
            if previous_version > version:
                continue
            selected_versions[entry_name] = version
            target = target_dir / entry_name
            ensure_dir(target.parent)
            with jar_file.open(entry) as src, target.open("wb") as dst:
                shutil.copyfileobj(src, dst)
    return target_dir


def _prepare_covfilter_test_classes_dir(
    *,
    compiled_test_classes_dir: Path,
    extra_runtime_cp: str,
    merged_dir: Path,
) -> Path:
    return compiled_test_classes_dir


def _covfilter_dir_contains(path: Path, pattern: str) -> bool:
    return path.is_dir() and any(path.rglob(pattern))


def _prepare_covfilter_libs_dir(
    *,
    base_libs_dir: Path,
    extra_runtime_cp: str,
    merged_dir: Path,
) -> Path:
    extra_jars = _classpath_jar_entries(extra_runtime_cp)
    extra_dirs = _classpath_dir_entries(extra_runtime_cp)
    if not extra_jars and not extra_dirs:
        return base_libs_dir

    if merged_dir.exists():
        shutil.rmtree(merged_dir, ignore_errors=True)
    ensure_dir(merged_dir)

    seen_names = set()
    seen_artifacts = set()
    for jar_file in sorted(base_libs_dir.glob("*.jar")):
        target = merged_dir / jar_file.name
        shutil.copy2(jar_file, target)
        seen_names.add(jar_file.name)
        artifact_key = _covfilter_runtime_jar_artifact_key(str(jar_file))
        if artifact_key:
            seen_artifacts.add(artifact_key)

    for extra_dir in extra_dirs:
        target_name = _covfilter_safe_jar_name(extra_dir, prefix="extra_dir")
        while target_name in seen_names:
            target_name = _covfilter_safe_jar_name(extra_dir / target_name, prefix="extra_dir")
        _write_directory_as_covfilter_jar(source_dir=extra_dir, target=merged_dir / target_name)
        seen_names.add(target_name)

    for jar_file in extra_jars:
        artifact_key = _covfilter_runtime_jar_artifact_key(str(jar_file))
        if artifact_key and artifact_key in seen_artifacts:
            continue
        target_name = jar_file.name
        if target_name in seen_names:
            target_name = _covfilter_safe_jar_name(jar_file, prefix="extra")
        _write_sanitized_covfilter_jar(source=jar_file, target=merged_dir / target_name)
        seen_names.add(target_name)
        if artifact_key:
            seen_artifacts.add(artifact_key)

    _prune_covfilter_duplicate_artifacts(merged_dir)
    return merged_dir


def _ensure_covfilter_extra_jars_present(*, libs_dir: Path, extra_runtime_cp: str) -> None:
    if not libs_dir.exists() or not libs_dir.is_dir():
        return
    existing_names = {path.name for path in libs_dir.glob("*.jar")}
    existing_artifacts = {
        artifact_key
        for artifact_key in (_covfilter_runtime_jar_artifact_key(str(path)) for path in libs_dir.glob("*.jar"))
        if artifact_key
    }
    for jar_file in _classpath_jar_entries(extra_runtime_cp):
        artifact_key = _covfilter_runtime_jar_artifact_key(str(jar_file))
        if artifact_key and artifact_key in existing_artifacts:
            continue
        target_name = jar_file.name
        if target_name in existing_names:
            continue
        _write_sanitized_covfilter_jar(source=jar_file, target=libs_dir / target_name)
        existing_names.add(target_name)
        if artifact_key:
            existing_artifacts.add(artifact_key)
    _prune_covfilter_duplicate_artifacts(libs_dir)


def _prune_covfilter_duplicate_artifacts(libs_dir: Path) -> None:
    candidates: dict[str, list[Path]] = {}
    for jar_file in libs_dir.glob("*.jar"):
        artifact_key = _covfilter_runtime_jar_artifact_key(str(jar_file))
        if not artifact_key:
            continue
        candidates.setdefault(artifact_key, []).append(jar_file)

    for paths in candidates.values():
        if len(paths) < 2:
            continue
        winner = max(paths, key=lambda path: _covfilter_runtime_jar_preference(str(path)))
        for jar_file in paths:
            if jar_file == winner:
                continue
            jar_file.unlink(missing_ok=True)


def _prepare_covfilter_sut_classes_dir(
    *,
    sut_classes_input: Path,
    extra_runtime_cp: str,
    merged_dir: Path,
) -> Path:
    if sut_classes_input.is_file():
        return _extract_covfilter_sut_classes(sut_jar=sut_classes_input, target_dir=merged_dir)

    main_output_dirs = _augment_covfilter_source_dirs(
        [path for path in _classpath_dir_entries(extra_runtime_cp) if _is_covfilter_main_output_dir(path)]
    )
    merged_main_dir = _merge_covfilter_class_dirs(source_dirs=main_output_dirs, merged_dir=merged_dir)
    if merged_main_dir is not None:
        return merged_main_dir

    if sut_classes_input.is_dir():
        sanitized_dir = _merge_covfilter_class_dirs(source_dirs=[sut_classes_input], merged_dir=merged_dir)
        return sanitized_dir if sanitized_dir is not None else sut_classes_input
    return _extract_covfilter_sut_classes(sut_jar=sut_classes_input, target_dir=merged_dir)


def _compiled_test_class_exists(classes_dir: Path, fqcn: str) -> bool:
    if not fqcn:
        return False
    class_file = (classes_dir / Path(*fqcn.split("."))).with_suffix(".class")
    return class_file.exists()


def _compiled_sources_are_fresh(classes_dir: Path, source_files: List[Path]) -> bool:
    for source_file in source_files:
        try:
            pkg, cls = parse_package_and_class(source_file)
        except Exception:
            continue
        if not cls:
            continue
        rel_parts = [part for part in pkg.split(".") if part]
        class_file = classes_dir.joinpath(*rel_parts, f"{cls}.class")
        if not class_file.exists():
            return False
        try:
            if class_file.stat().st_mtime_ns < source_file.stat().st_mtime_ns:
                return False
        except OSError:
            return False
    return True


def _count_csv_rows(csv_path: Path) -> int:
    if not csv_path.exists():
        return 0
    try:
        with csv_path.open("r", encoding="utf-8", newline="") as handle:
            reader = csv.reader(handle)
            next(reader, None)
            return sum(1 for _ in reader)
    except OSError:
        return 0


def _covfilter_output_counts(out_dir: Path) -> tuple[int, int, int]:
    return (
        _count_csv_rows(out_dir / "test_deltas_all.csv"),
        _count_csv_rows(out_dir / "test_deltas_kept.csv"),
        _count_csv_rows(out_dir / "line_deltas_kept.csv"),
    )


def _duplicate_class_simple_name(compile_output: str) -> str:
    import re

    match = re.search(r"duplicate class:\s+(?:[A-Za-z_][A-Za-z0-9_$.]*\.)?([A-Za-z_][A-Za-z0-9_]*)", compile_output or "")
    return match.group(1) if match else ""


@dataclass(frozen=True)
class AutoCovfilterCandidate:
    label: str
    pair: Optional[EvoSuitePair]
    source_files: List[Path]
    generated_test_fqcn: str
    build_dir: Path
    out_dir: Path
    log_file: Path
    compile_log: Path


@dataclass(frozen=True)
class AutoCovfilterResult:
    candidate: AutoCovfilterCandidate
    status: str
    problem_category: str
    problem_detail: str

    @property
    def label(self) -> str:
        return self.candidate.label

    @property
    def generated_test_fqcn(self) -> str:
        return self.candidate.generated_test_fqcn

    @property
    def out_dir(self) -> Path:
        return self.candidate.out_dir

    @property
    def log_file(self) -> Path:
        return self.candidate.log_file

    @property
    def compile_log(self) -> Path:
        return self.candidate.compile_log

    @property
    def pair(self) -> Optional[EvoSuitePair]:
        return self.candidate.pair

    def counts(self) -> tuple[int, int, int]:
        return _covfilter_output_counts(self.out_dir)


def _pair_source_files(pair: Optional[EvoSuitePair]) -> List[Path]:
    if pair is None:
        return []
    files = [pair.test_src]
    if pair.scaffolding_src is not None and pair.scaffolding_src.exists():
        files.append(pair.scaffolding_src)
    return files


def _materialize_sanitized_pair_for_ctx(pipeline, ctx: "TargetContext") -> Optional[EvoSuitePair]:
    current_generated_fqcn = (ctx.generated_test_fqcn or "").strip()
    if not current_generated_fqcn:
        return None
    generated_test_fqcn = variant_test_fqcn(current_generated_fqcn, "baseline").strip()
    sanitized_root = Path(pipeline.args.sanitized_es_dir)
    clear_pair_root(sanitized_root, ctx.repo, ctx.fqcn)
    return materialize_sanitized_pair(
        source_root=pipeline.generated_dir,
        sanitized_root=sanitized_root,
        repo=ctx.repo,
        fqcn=ctx.fqcn,
        test_fqcn=generated_test_fqcn,
    )


def _replace_generated_sources(ctx: "TargetContext", pair: EvoSuitePair) -> None:
    generated_sources = _pair_source_files(pair)
    combined = list(ctx.manual_sources) + generated_sources
    deduped: List[Path] = []
    seen = set()
    for source in combined:
        if source in seen:
            continue
        seen.add(source)
        deduped.append(source)
    ctx.sources = list(deduped)
    ctx.final_sources = list(deduped)
    ctx.generated_test_fqcn = pair.test_fqcn


def _copy_covfilter_result_artifacts(result: AutoCovfilterResult, *, target_out_dir: Path, target_log_file: Path, target_compile_log: Path) -> None:
    if target_out_dir.exists():
        shutil.rmtree(target_out_dir, ignore_errors=True)
    if result.out_dir.exists():
        shutil.copytree(result.out_dir, target_out_dir)
    if result.log_file.exists():
        ensure_dir(target_log_file.parent)
        shutil.copy2(result.log_file, target_log_file)
    if result.compile_log.exists():
        ensure_dir(target_compile_log.parent)
        shutil.copy2(result.compile_log, target_compile_log)


def _append_sanitize_compare_row(
    *,
    csv_path: Path,
    ctx: "TargetContext",
    baseline: AutoCovfilterResult,
    sanitized: AutoCovfilterResult,
    selected_variant: str,
    selection_reason: str,
    selected_pair: Optional[EvoSuitePair],
    selected_out_dir: Path,
    selected_log_file: Path,
) -> None:
    baseline_all, baseline_kept, baseline_lines = baseline.counts()
    sanitized_all, sanitized_kept, sanitized_lines = sanitized.counts()
    with csv_path.open("a", encoding="utf-8", newline="") as handle:
        writer = csv.writer(handle)
        writer.writerow(
            [
                ctx.repo,
                ctx.fqcn,
                baseline.status,
                baseline.problem_category,
                baseline.problem_detail,
                baseline.generated_test_fqcn,
                baseline_all,
                baseline_kept,
                baseline_lines,
                str(baseline.out_dir),
                str(baseline.log_file),
                sanitized.status,
                sanitized.problem_category,
                sanitized.problem_detail,
                sanitized.generated_test_fqcn,
                sanitized_all,
                sanitized_kept,
                sanitized_lines,
                str(sanitized.out_dir),
                str(sanitized.log_file),
                selected_variant,
                selection_reason,
                selected_pair.test_fqcn if selected_pair else "",
                str(selected_pair.test_src) if selected_pair else "",
                str(selected_out_dir),
                str(selected_log_file),
            ]
        )


def _select_auto_covfilter_result(
    baseline: AutoCovfilterResult,
    sanitized: AutoCovfilterResult,
) -> tuple[AutoCovfilterResult, str]:
    if sanitized.status == "passed":
        return sanitized, "prefer_sanitized"
    if baseline.status == "passed":
        return baseline, "fallback_to_baseline"
    if sanitized.pair is not None:
        return sanitized, "sanitized_failed_without_baseline_recovery"
    if baseline.pair is not None:
        return baseline, "baseline_failed_last_fallback"
    return sanitized, "missing_generated_variant"


def _run_auto_covfilter_candidate(
    *,
    pipeline,
    ctx: "TargetContext",
    candidate: AutoCovfilterCandidate,
    cov_classes_dir: Path,
) -> AutoCovfilterResult:
    if candidate.build_dir.exists():
        shutil.rmtree(candidate.build_dir, ignore_errors=True)
    ensure_dir(candidate.build_dir)
    if candidate.out_dir.exists():
        shutil.rmtree(candidate.out_dir, ignore_errors=True)

    if not candidate.generated_test_fqcn or candidate.pair is None:
        return AutoCovfilterResult(
            candidate=candidate,
            status="skipped",
            problem_category="missing_generated_test_fqcn",
            problem_detail="Generated test variant is missing.",
        )

    effective_libs_cp = _effective_compile_libs_cp_for_sources(
        libs_glob_cp=pipeline.args.libs_cp,
        sources=candidate.source_files,
    )
    ok_compile, compile_tail, compiled_sources = compile_test_set_smart(
        java_files=candidate.source_files,
        build_dir=candidate.build_dir,
        libs_glob_cp=effective_libs_cp,
        sut_jar=ctx.sut_jar,
        log_file=candidate.compile_log,
        repo_root_for_deps=ctx.repo_root_for_deps,
        module_rel=ctx.module_rel,
        build_tool=ctx.build_tool,
        max_rounds=pipeline.args.dep_rounds,
    )

    if not ok_compile:
        return AutoCovfilterResult(
            candidate=candidate,
            status="skipped",
            problem_category="compile_failed",
            problem_detail=compile_tail,
        )

    ok_cov, cov_tail = _run_covfilter_with_retries(
        pipeline=pipeline,
        ctx=ctx,
        variant=f"compare-{candidate.label}",
        source_files=compiled_sources,
        coverage_filter_jar=pipeline.covfilter_jar,
        libs_glob_cp=pipeline.args.libs_cp,
        test_classes_dir=candidate.build_dir,
        sut_classes_dir=cov_classes_dir,
        out_dir=candidate.out_dir,
        manual_test_fqcn=ctx.manual_test_fqcn or "",
        generated_test_fqcn=candidate.generated_test_fqcn,
        jacoco_agent_jar=pipeline.jacoco_agent,
        sut_cp_entry=ctx.sut_jar,
        log_file=candidate.log_file,
    )
    if not ok_cov:
        return AutoCovfilterResult(
            candidate=candidate,
            status="failed",
            problem_category="covfilter_failed",
            problem_detail=cov_tail,
        )
    if not covfilter_output_exists(candidate.out_dir):
        return AutoCovfilterResult(
            candidate=candidate,
            status="failed",
            problem_category="missing_output",
            problem_detail="Covfilter finished without producing test_deltas_all.csv.",
        )
    return AutoCovfilterResult(
        candidate=candidate,
        status="passed",
        problem_category="",
        problem_detail="",
    )


def _run_auto_sanitize_compare(
    *,
    pipeline,
    ctx: "TargetContext",
    cov_classes_dir: Path,
    cov_out: Path,
    cov_log: Path,
    cov_compile_log: Path,
) -> bool:
    compare_root = Path(pipeline.args.sanitize_compare_out)
    compare_csv = sanitize_compare_summary_csv(compare_root, pipeline.args.includes)
    baseline_fqcn = variant_test_fqcn(ctx.generated_test_fqcn or "", "baseline") if ctx.generated_test_fqcn else ""
    baseline_pair = variant_pair(pipeline.generated_dir, ctx.repo, ctx.fqcn, baseline_fqcn, "baseline")
    sanitized_pair = _materialize_sanitized_pair_for_ctx(pipeline, ctx)

    baseline_candidate = AutoCovfilterCandidate(
        label="baseline",
        pair=baseline_pair,
        source_files=list(ctx.manual_sources) + _pair_source_files(baseline_pair),
        generated_test_fqcn=baseline_pair.test_fqcn if baseline_pair else "",
        build_dir=pipeline.build_dir / "covfilter-compare-classes" / ctx.target_id / "baseline",
        out_dir=sanitize_compare_target_dir(compare_root, ctx.target_id, "baseline"),
        log_file=pipeline.logs_dir / f"{ctx.target_id}.baseline.covfilter.log",
        compile_log=pipeline.logs_dir / f"{ctx.target_id}.baseline.covfilter.compile.log",
    )
    sanitized_candidate = AutoCovfilterCandidate(
        label="sanitized",
        pair=sanitized_pair,
        source_files=list(ctx.manual_sources) + _pair_source_files(sanitized_pair),
        generated_test_fqcn=sanitized_pair.test_fqcn if sanitized_pair else "",
        build_dir=pipeline.build_dir / "covfilter-compare-classes" / ctx.target_id / "sanitized",
        out_dir=sanitize_compare_target_dir(compare_root, ctx.target_id, "sanitized"),
        log_file=pipeline.logs_dir / f"{ctx.target_id}.sanitized.covfilter.log",
        compile_log=pipeline.logs_dir / f"{ctx.target_id}.sanitized.covfilter.compile.log",
    )

    print(f'[agt] Running covfilter compare: repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
    sanitized_result = _run_auto_covfilter_candidate(
        pipeline=pipeline,
        ctx=ctx,
        candidate=sanitized_candidate,
        cov_classes_dir=cov_classes_dir,
    )
    if sanitized_result.status == "passed":
        baseline_result = AutoCovfilterResult(
            candidate=baseline_candidate,
            status="skipped",
            problem_category="not_run_prefer_sanitized",
            problem_detail="Sanitized variant passed; baseline/original variant not run.",
        )
    else:
        baseline_result = _run_auto_covfilter_candidate(
            pipeline=pipeline,
            ctx=ctx,
            candidate=baseline_candidate,
            cov_classes_dir=cov_classes_dir,
        )
    selected_result, selection_reason = _select_auto_covfilter_result(baseline_result, sanitized_result)
    if selected_result.pair is not None:
        _replace_generated_sources(ctx, selected_result.pair)

    _copy_covfilter_result_artifacts(
        selected_result,
        target_out_dir=cov_out,
        target_log_file=cov_log,
        target_compile_log=cov_compile_log,
    )
    selected_log_for_summary = cov_compile_log if selected_result.problem_category == "compile_failed" else cov_log
    _append_sanitize_compare_row(
        csv_path=compare_csv,
        ctx=ctx,
        baseline=baseline_result,
        sanitized=sanitized_result,
        selected_variant=selected_result.label,
        selection_reason=selection_reason,
        selected_pair=selected_result.pair,
        selected_out_dir=cov_out,
        selected_log_file=selected_log_for_summary,
    )
    _append_covfilter_summary_row(
        csv_path=_covfilter_summary_csv(pipeline, "auto"),
        repo=ctx.repo,
        fqcn=ctx.fqcn,
        variant="auto",
        status=selected_result.status,
        problem_category=selected_result.problem_category,
        problem_detail=selected_result.problem_detail,
        manual_test_fqcn=ctx.manual_test_fqcn or "",
        generated_test_fqcn=selected_result.pair.test_fqcn if selected_result.pair else selected_result.generated_test_fqcn,
        out_dir=cov_out,
        log_file=selected_log_for_summary,
    )
    if selected_result.status == "passed":
        pipeline.ran += 1
        return True

    if selected_result.problem_category == "compile_failed":
        print(f'[agt] covfilter: Skip (compile failed): repo="{ctx.repo}" fqcn="{ctx.fqcn}" (see {selected_log_for_summary})')
        print("[agt][COVFILTER-COMPILE-TAIL]\n" + selected_result.problem_detail)
    elif selected_result.status == "failed":
        print(f'[agt] covfilter: FAIL (see {selected_log_for_summary})')
        print("[agt][COVFILTER-TAIL]\n" + selected_result.problem_detail)
    return True


def _append_covfilter_summary_row(
    *,
    csv_path: Path,
    repo: str,
    fqcn: str,
    variant: str,
    status: str,
    problem_category: str,
    problem_detail: str,
    manual_test_fqcn: str,
    generated_test_fqcn: str,
    out_dir: Path,
    log_file: Path,
) -> None:
    test_all_count, test_kept_count, line_kept_count = _covfilter_output_counts(out_dir)
    row = {
        "repo": repo,
        "fqcn": fqcn,
        "variant": variant,
        "status": status,
        "problem_category": problem_category,
        "problem_detail": problem_detail,
        "manual_test_fqcn": manual_test_fqcn,
        "generated_test_fqcn": generated_test_fqcn,
        "test_deltas_all_count": str(test_all_count),
        "test_deltas_kept_count": str(test_kept_count),
        "line_deltas_kept_count": str(line_kept_count),
        "out_dir": str(out_dir),
        "log_file": str(log_file),
    }
    fieldnames = list(row.keys())
    rows: List[dict[str, str]] = []
    replaced = False
    if csv_path.exists():
        with csv_path.open("r", encoding="utf-8", newline="") as handle:
            reader = csv.DictReader(handle)
            existing_fieldnames = reader.fieldnames or []
            if existing_fieldnames:
                fieldnames = existing_fieldnames
            for existing_row in reader:
                if (
                    existing_row.get("repo", "") == repo
                    and existing_row.get("fqcn", "") == fqcn
                    and existing_row.get("variant", "") == variant
                ):
                    if not replaced:
                        rows.append({name: row.get(name, "") for name in fieldnames})
                        replaced = True
                    continue
                rows.append({name: existing_row.get(name, "") for name in fieldnames})
    if not replaced:
        rows.append({name: row.get(name, "") for name in fieldnames})
    with csv_path.open("w", encoding="utf-8", newline="") as handle:
        writer = csv.DictWriter(handle, fieldnames=fieldnames)
        writer.writeheader()
        writer.writerows(rows)


def _run_covfilter_with_retries(
    *,
    pipeline,
    ctx: "TargetContext",
    variant: str,
    source_files: List[Path],
    coverage_filter_jar: Path,
    libs_glob_cp: str,
    test_classes_dir: Path,
    sut_classes_dir: Path,
    out_dir: Path,
    manual_test_fqcn: str,
    generated_test_fqcn: str,
    jacoco_agent_jar: Path,
    sut_cp_entry: Path,
    log_file: Path,
) -> Tuple[bool, str]:
    repo_runtime_cp = ""
    if ctx.repo_root_for_deps and ctx.repo_root_for_deps.exists():
        repo_runtime_cp = resolve_repo_runtime_classpath(
            ctx.repo_root_for_deps,
            ctx.module_rel,
            ctx.build_tool,
            source_files=source_files,
        )
    framework_runtime_cp = _framework_runtime_cp_for_sources(source_files)
    if _sources_use_regular_mockito_evosuite_runtime(source_files):
        framework_runtime_cp = _filter_evosuite_standalone_runtime(framework_runtime_cp)
        repo_runtime_cp = _filter_evosuite_standalone_runtime(repo_runtime_cp)
    extra_runtime_cp = _merge_classpath_strings(
        framework_runtime_cp,
        _spring_runtime_cp_for_sources(source_files),
        repo_runtime_cp,
    )

    for _ in range(_COVFILTER_RUNTIME_RETRY_LIMIT):
        base_libs_dir = libs_dir_from_glob(libs_glob_cp)
        libs_dir_arg = _prepare_covfilter_libs_dir(
            base_libs_dir=base_libs_dir,
            extra_runtime_cp=extra_runtime_cp,
            merged_dir=pipeline.build_dir / f"{variant}-filter-runtime-libs" / ctx.target_id,
        )
        _ensure_covfilter_extra_jars_present(libs_dir=libs_dir_arg, extra_runtime_cp=extra_runtime_cp)
        cov_test_classes_dir = _prepare_covfilter_test_classes_dir(
            compiled_test_classes_dir=test_classes_dir,
            extra_runtime_cp=extra_runtime_cp,
            merged_dir=pipeline.build_dir / f"{variant}-filter-runtime-classes" / ctx.target_id,
        )
        cov_sut_classes_dir = _prepare_covfilter_sut_classes_dir(
            sut_classes_input=sut_classes_dir,
            extra_runtime_cp=extra_runtime_cp,
            merged_dir=pipeline.build_dir / f"{variant}-filter-sut-classes" / ctx.target_id,
        )
        ok_cov, cov_tail = CovfilterRunner(
            build_dir=Path.cwd() / "build" / "agt",
            timeout_ms=_effective_covfilter_timeout_ms(pipeline.args.timeout_ms),
        ).run_covfilter(
            coverage_filter_jar=coverage_filter_jar,
            libs_glob_cp=libs_glob_cp,
            extra_runtime_cp=extra_runtime_cp,
            test_classes_dir=cov_test_classes_dir,
            sut_classes_dir=cov_sut_classes_dir,
            out_dir=out_dir,
            manual_test_fqcn=manual_test_fqcn,
            generated_test_fqcn=generated_test_fqcn,
            jacoco_agent_jar=jacoco_agent_jar,
            sut_cp_entry=cov_sut_classes_dir,
            libs_dir_arg=libs_dir_arg,
            log_file=log_file,
        )
        if ok_cov and covfilter_output_exists(out_dir):
            return ok_cov, cov_tail
        full_output = log_file.read_text(encoding="utf-8", errors="ignore")
        external_runtime_cp = resolve_external_runtime_classpath_from_output(full_output)
        if "Fork failed (exit=1) main=app.ListTests" in full_output:
            list_tests_output = _probe_covfilter_list_tests_output(
                coverage_filter_jar=coverage_filter_jar,
                libs_dir=libs_dir_arg,
                test_classes_dir=cov_test_classes_dir,
                sut_classes_dir=cov_sut_classes_dir,
                test_fqcns=[generated_test_fqcn, manual_test_fqcn],
                timeout_ms=_effective_covfilter_timeout_ms(pipeline.args.timeout_ms),
            )
            if list_tests_output:
                full_output = f"{full_output.rstrip()}\n\n[agt] list-tests probe:\n{list_tests_output}\n"
                write_text(log_file, full_output)
                external_runtime_cp = _merge_classpath_strings(
                    external_runtime_cp,
                    resolve_external_runtime_classpath_from_output(list_tests_output),
                )
        retried_runtime_cp = _merge_classpath_strings(extra_runtime_cp, external_runtime_cp)
        if not retried_runtime_cp or retried_runtime_cp == extra_runtime_cp:
            return ok_cov, cov_tail
        extra_runtime_cp = retried_runtime_cp
    return ok_cov, cov_tail


class CovfilterStep(Step):
    step_names = ("filter",)

    def run(self, ctx: "TargetContext") -> bool:
        if not (self.pipeline.do_covfilter and self.should_run()):
            return True
        auto_variant = self.pipeline.args.auto_variant
        if self.pipeline.covfilter_allow is not None and (ctx.repo, ctx.fqcn) not in self.pipeline.covfilter_allow:
            print(f'[agt] covfilter: Skip (agt_line_covered=0): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
            cov_out = covfilter_variant_out_dir(
                self.pipeline.covfilter_out_root, self.pipeline.adopted_covfilter_out_root, auto_variant, ctx.target_id
            )
            _append_covfilter_summary_row(
                csv_path=_covfilter_summary_csv(self.pipeline, auto_variant),
                repo=ctx.repo,
                fqcn=ctx.fqcn,
                variant=auto_variant,
                status="skipped",
                problem_category="agt_line_covered_zero",
                problem_detail="Target excluded because AGT coverage summary reports zero covered lines.",
                manual_test_fqcn=ctx.manual_test_fqcn or "",
                generated_test_fqcn=ctx.generated_test_fqcn or "",
                out_dir=cov_out,
                log_file=self.pipeline.logs_dir / f"{ctx.target_id}.{auto_variant}.covfilter.log",
            )
            return True
        if self.pipeline.covfilter_jar is None or not self.pipeline.covfilter_jar.exists():
            print(f'[agt] covfilter: Skip (missing --covfilter-jar): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
            cov_out = covfilter_variant_out_dir(
                self.pipeline.covfilter_out_root, self.pipeline.adopted_covfilter_out_root, auto_variant, ctx.target_id
            )
            _append_covfilter_summary_row(
                csv_path=_covfilter_summary_csv(self.pipeline, auto_variant),
                repo=ctx.repo,
                fqcn=ctx.fqcn,
                variant=auto_variant,
                status="skipped",
                problem_category="missing_covfilter_jar",
                problem_detail="Coverage filter jar is missing.",
                manual_test_fqcn=ctx.manual_test_fqcn or "",
                generated_test_fqcn=ctx.generated_test_fqcn or "",
                out_dir=cov_out,
                log_file=self.pipeline.logs_dir / f"{ctx.target_id}.{auto_variant}.covfilter.log",
            )
            return True
        if not ctx.manual_test_fqcn or not ctx.generated_test_fqcn:
            if not ctx.manual_test_fqcn and not ctx.generated_test_fqcn:
                problem_category = "missing_test_fqcn"
                problem_detail = "Need both manual and generated test FQCNs to run covfilter."
            elif not ctx.manual_test_fqcn:
                problem_category = "missing_manual_test_fqcn"
                problem_detail = "Need a manual test FQCN to run covfilter."
            else:
                problem_category = "missing_generated_test_fqcn"
                problem_detail = "Need a generated test FQCN to run covfilter."
            print(f'[agt] covfilter: Skip (need both manual+generated test fqcn): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
            cov_out = covfilter_variant_out_dir(
                self.pipeline.covfilter_out_root, self.pipeline.adopted_covfilter_out_root, auto_variant, ctx.target_id
            )
            _append_covfilter_summary_row(
                csv_path=_covfilter_summary_csv(self.pipeline, auto_variant),
                repo=ctx.repo,
                fqcn=ctx.fqcn,
                variant=auto_variant,
                status="skipped",
                problem_category=problem_category,
                problem_detail=problem_detail,
                manual_test_fqcn=ctx.manual_test_fqcn or "",
                generated_test_fqcn=ctx.generated_test_fqcn or "",
                out_dir=cov_out,
                log_file=self.pipeline.logs_dir / f"{ctx.target_id}.{auto_variant}.covfilter.log",
            )
            return True

        if self.pipeline.sut_classes_dir is not None and self.pipeline.sut_classes_dir.exists():
            cov_classes_dir = self.pipeline.sut_classes_dir
        else:
            cov_classes_dir = ctx.sut_jar

        cov_out = covfilter_variant_out_dir(
            self.pipeline.covfilter_out_root, self.pipeline.adopted_covfilter_out_root, auto_variant, ctx.target_id
        )
        cov_log = self.pipeline.logs_dir / f"{ctx.target_id}.{auto_variant}.covfilter.log"
        cov_compile_log = self.pipeline.logs_dir / f"{ctx.target_id}.{auto_variant}.covfilter.compile.log"
        existing_cov_out = _existing_covfilter_out_dir(self.pipeline, auto_variant, ctx.target_id)
        if self.pipeline.args.skip_passed_by_status and _latest_covfilter_status(
            self.pipeline,
            repo=ctx.repo,
            fqcn=ctx.fqcn,
            variant=auto_variant,
        ) == "passed":
            print(f'[agt] covfilter: Skip (existing passed status): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
            _append_covfilter_summary_row(
                csv_path=_covfilter_summary_csv(self.pipeline, auto_variant),
                repo=ctx.repo,
                fqcn=ctx.fqcn,
                variant=auto_variant,
                status="skipped",
                problem_category="existing_passed_status",
                problem_detail="Latest covfilter summary row already has status=passed.",
                manual_test_fqcn=ctx.manual_test_fqcn or "",
                generated_test_fqcn=ctx.generated_test_fqcn or "",
                out_dir=existing_cov_out or cov_out,
                log_file=cov_log,
            )
            return True
        if self.pipeline.args.skip_passed and existing_cov_out is not None:
            print(f'[agt] covfilter: Skip (existing passed output): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
            _append_covfilter_summary_row(
                csv_path=_covfilter_summary_csv(self.pipeline, auto_variant),
                repo=ctx.repo,
                fqcn=ctx.fqcn,
                variant=auto_variant,
                status="skipped",
                problem_category="existing_passed_output",
                problem_detail="Existing covfilter output found for a previously passed target.",
                manual_test_fqcn=ctx.manual_test_fqcn or "",
                generated_test_fqcn=ctx.generated_test_fqcn or "",
                out_dir=existing_cov_out,
                log_file=cov_log,
            )
            return True
        if self.pipeline.args.skip_exists and existing_cov_out is not None:
            print(f'[agt] covfilter: Skip (existing covfilter output): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
            _append_covfilter_summary_row(
                csv_path=_covfilter_summary_csv(self.pipeline, auto_variant),
                repo=ctx.repo,
                fqcn=ctx.fqcn,
                variant=auto_variant,
                status="skipped",
                problem_category="existing_output",
                problem_detail="Existing covfilter output found.",
                manual_test_fqcn=ctx.manual_test_fqcn or "",
                generated_test_fqcn=ctx.generated_test_fqcn or "",
                out_dir=existing_cov_out,
                log_file=cov_log,
            )
            return True
        if self.pipeline.args.sanitize_compare and self.pipeline.args.auto_variant != "auto-original":
            return _run_auto_sanitize_compare(
                pipeline=self.pipeline,
                ctx=ctx,
                cov_classes_dir=cov_classes_dir,
                cov_out=cov_out,
                cov_log=cov_log,
                cov_compile_log=cov_compile_log,
            )

        covfilter_build = ctx.target_build
        compiled_sources = ctx.final_sources
        has_manual_class = _compiled_test_class_exists(ctx.target_build, ctx.manual_test_fqcn or "")
        has_generated_class = _compiled_test_class_exists(ctx.target_build, ctx.generated_test_fqcn or "")
        classes_are_fresh = _compiled_sources_are_fresh(ctx.target_build, ctx.final_sources)
        if not (has_manual_class and has_generated_class and classes_are_fresh):
            covfilter_build = self.pipeline.build_dir / "covfilter-classes" / ctx.target_id
            if covfilter_build.exists():
                shutil.rmtree(covfilter_build, ignore_errors=True)
            ensure_dir(covfilter_build)
            effective_libs_cp = _effective_compile_libs_cp_for_sources(
                libs_glob_cp=self.pipeline.args.libs_cp,
                sources=ctx.final_sources,
            )
            ok_compile, compile_tail, compiled_sources = compile_test_set_smart(
                java_files=ctx.final_sources,
                build_dir=covfilter_build,
                libs_glob_cp=effective_libs_cp,
                sut_jar=ctx.sut_jar,
                log_file=cov_compile_log,
                repo_root_for_deps=ctx.repo_root_for_deps,
                module_rel=ctx.module_rel,
                build_tool=ctx.build_tool,
                max_rounds=self.pipeline.args.dep_rounds,
            )
            if not ok_compile:
                print(f'[agt] covfilter: Skip (compile failed): repo="{ctx.repo}" fqcn="{ctx.fqcn}" (see {cov_compile_log})')
                print("[agt][COVFILTER-COMPILE-TAIL]\n" + compile_tail)
                _append_covfilter_summary_row(
                    csv_path=_covfilter_summary_csv(self.pipeline, auto_variant),
                    repo=ctx.repo,
                    fqcn=ctx.fqcn,
                    variant=auto_variant,
                    status="skipped",
                    problem_category="compile_failed",
                    problem_detail=compile_tail,
                    manual_test_fqcn=ctx.manual_test_fqcn or "",
                    generated_test_fqcn=ctx.generated_test_fqcn or "",
                    out_dir=cov_out,
                    log_file=cov_compile_log,
                )
                return True

        print(f'[agt] Running covfilter: repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
        ok_cov, cov_tail = _run_covfilter_with_retries(
            pipeline=self.pipeline,
            ctx=ctx,
            variant="covfilter",
            source_files=compiled_sources,
            coverage_filter_jar=self.pipeline.covfilter_jar,
            libs_glob_cp=self.pipeline.args.libs_cp,
            test_classes_dir=covfilter_build,
            sut_classes_dir=cov_classes_dir,
            out_dir=cov_out,
            manual_test_fqcn=ctx.manual_test_fqcn,
            generated_test_fqcn=ctx.generated_test_fqcn,
            jacoco_agent_jar=self.pipeline.jacoco_agent,
            sut_cp_entry=ctx.sut_jar,
            log_file=cov_log,
        )
        if not ok_cov or not covfilter_output_exists(cov_out):
            problem_category = "covfilter_failed" if not ok_cov else "missing_output"
            problem_detail = cov_tail if not ok_cov else "Covfilter finished without producing test_deltas_all.csv."
            _append_covfilter_summary_row(
                csv_path=_covfilter_summary_csv(self.pipeline, auto_variant),
                repo=ctx.repo,
                fqcn=ctx.fqcn,
                variant=auto_variant,
                status="failed",
                problem_category=problem_category,
                problem_detail=problem_detail,
                manual_test_fqcn=ctx.manual_test_fqcn or "",
                generated_test_fqcn=ctx.generated_test_fqcn or "",
                out_dir=cov_out,
                log_file=cov_log,
            )
        else:
            _append_covfilter_summary_row(
                csv_path=_covfilter_summary_csv(self.pipeline, auto_variant),
                repo=ctx.repo,
                fqcn=ctx.fqcn,
                variant=auto_variant,
                status="passed",
                problem_category="",
                problem_detail="",
                manual_test_fqcn=ctx.manual_test_fqcn or "",
                generated_test_fqcn=ctx.generated_test_fqcn or "",
                out_dir=cov_out,
                log_file=cov_log,
            )
            self.pipeline.ran += 1
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
            for variant in ("adopted", "agentic"):
                cov_out = covfilter_variant_out_dir(
                    self.pipeline.covfilter_out_root,
                    self.pipeline.adopted_covfilter_out_root,
                    variant,
                    ctx.target_id,
                )
                _append_covfilter_summary_row(
                    csv_path=_covfilter_summary_csv(self.pipeline, variant),
                    repo=ctx.repo,
                    fqcn=ctx.fqcn,
                    variant=variant,
                    status="skipped",
                    problem_category="agt_line_covered_zero",
                    problem_detail="Target excluded because AGT coverage summary reports zero covered lines.",
                    manual_test_fqcn=ctx.manual_test_fqcn or "",
                    generated_test_fqcn="",
                    out_dir=cov_out,
                    log_file=self.pipeline.logs_dir / f"{ctx.target_id}.adopted.{variant}.covfilter.log",
                )
            return True
        if self.pipeline.covfilter_jar is None or not self.pipeline.covfilter_jar.exists():
            print(f'[agt] adopted-covfilter: Skip (missing --covfilter-jar): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
            for variant in ("adopted", "agentic"):
                cov_out = covfilter_variant_out_dir(
                    self.pipeline.covfilter_out_root,
                    self.pipeline.adopted_covfilter_out_root,
                    variant,
                    ctx.target_id,
                )
                _append_covfilter_summary_row(
                    csv_path=_covfilter_summary_csv(self.pipeline, variant),
                    repo=ctx.repo,
                    fqcn=ctx.fqcn,
                    variant=variant,
                    status="skipped",
                    problem_category="missing_covfilter_jar",
                    problem_detail="Coverage filter jar is missing.",
                    manual_test_fqcn=ctx.manual_test_fqcn or "",
                    generated_test_fqcn="",
                    out_dir=cov_out,
                    log_file=self.pipeline.logs_dir / f"{ctx.target_id}.adopted.{variant}.covfilter.log",
                )
            return True
        if not ctx.manual_sources:
            print(f'[agt] adopted-covfilter: Skip (missing manual test source): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
            for variant in ("adopted", "agentic"):
                cov_out = covfilter_variant_out_dir(
                    self.pipeline.covfilter_out_root,
                    self.pipeline.adopted_covfilter_out_root,
                    variant,
                    ctx.target_id,
                )
                _append_covfilter_summary_row(
                    csv_path=_covfilter_summary_csv(self.pipeline, variant),
                    repo=ctx.repo,
                    fqcn=ctx.fqcn,
                    variant=variant,
                    status="skipped",
                    problem_category="missing_manual_test_source",
                    problem_detail="Missing manual test source.",
                    manual_test_fqcn=ctx.manual_test_fqcn or "",
                    generated_test_fqcn="",
                    out_dir=cov_out,
                    log_file=self.pipeline.logs_dir / f"{ctx.target_id}.adopted.{variant}.covfilter.log",
                )
            return True

        variants = adopted_variants(self.pipeline.adopted_root, ctx.target_id, ctx.fqcn)
        if not variants:
            print(f'[agt] adopted-covfilter: Skip (missing adopted tests): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
            for variant in ("adopted", "agentic"):
                cov_out = covfilter_variant_out_dir(
                    self.pipeline.covfilter_out_root,
                    self.pipeline.adopted_covfilter_out_root,
                    variant,
                    ctx.target_id,
                )
                _append_covfilter_summary_row(
                    csv_path=_covfilter_summary_csv(self.pipeline, variant),
                    repo=ctx.repo,
                    fqcn=ctx.fqcn,
                    variant=variant,
                    status="skipped",
                    problem_category="missing_adopted_tests",
                    problem_detail="Missing adopted test source.",
                    manual_test_fqcn=ctx.manual_test_fqcn or "",
                    generated_test_fqcn="",
                    out_dir=cov_out,
                    log_file=self.pipeline.logs_dir / f"{ctx.target_id}.adopted.{variant}.covfilter.log",
                )
            return True

        for variant, adopted_src in variants:
            normalize_primary_class_name_file(adopted_src)
            adopted_test_fqcn = test_fqcn_from_source(adopted_src)
            cov_out = covfilter_variant_out_dir(
                self.pipeline.covfilter_out_root, self.pipeline.adopted_covfilter_out_root, variant, ctx.target_id
            )
            cov_log = self.pipeline.logs_dir / f"{ctx.target_id}.adopted.{variant}.covfilter.log"
            if not adopted_test_fqcn or not ctx.manual_test_fqcn:
                print(
                    f'[agt] adopted-covfilter: Skip (cannot parse test fqcn): repo="{ctx.repo}" fqcn="{ctx.fqcn}" variant="{variant}"'
                )
                _append_covfilter_summary_row(
                    csv_path=_covfilter_summary_csv(self.pipeline, variant),
                    repo=ctx.repo,
                    fqcn=ctx.fqcn,
                    variant=variant,
                    status="skipped",
                    problem_category="parse_test_fqcn_failed",
                    problem_detail="Could not parse adopted or manual test FQCN.",
                    manual_test_fqcn=ctx.manual_test_fqcn or "",
                    generated_test_fqcn=adopted_test_fqcn or "",
                    out_dir=cov_out,
                    log_file=cov_log,
                )
                continue
            if self.pipeline.sut_classes_dir is not None and self.pipeline.sut_classes_dir.exists():
                cov_classes_dir = self.pipeline.sut_classes_dir
            else:
                cov_classes_dir = ctx.sut_jar

            adopted_build = self.pipeline.build_dir / "adopted-filter-classes" / variant / ctx.target_id
            ensure_dir(adopted_build)
            adopt_compile_log = self.pipeline.logs_dir / f"{ctx.target_id}.adopted.{variant}.covfilter.compile.log"
            ok_adopt = False
            adopt_tail = ""
            adopted_sources = ctx.manual_sources + [adopted_src]
            effective_libs_cp = _effective_compile_libs_cp_for_sources(
                libs_glob_cp=self.pipeline.args.libs_cp,
                sources=adopted_sources,
            )
            for _attempt in range(3):
                ok_adopt, adopt_tail, _ = compile_test_set_smart(
                    java_files=adopted_sources,
                    build_dir=adopted_build,
                    libs_glob_cp=effective_libs_cp,
                    sut_jar=ctx.sut_jar,
                    log_file=adopt_compile_log,
                    repo_root_for_deps=ctx.repo_root_for_deps,
                    module_rel=ctx.module_rel,
                    build_tool=ctx.build_tool,
                    max_rounds=self.pipeline.args.dep_rounds,
                )
                if ok_adopt:
                    break
                duplicate_simple_name = _duplicate_class_simple_name(adopt_tail)
                if not duplicate_simple_name or not namespace_non_primary_type_name_file(adopted_src, duplicate_simple_name):
                    break
            if not ok_adopt:
                print(
                    f'[agt] adopted-covfilter: Skip (compile failed): repo="{ctx.repo}" fqcn="{ctx.fqcn}" variant="{variant}" (see {adopt_compile_log})'
                )
                print("[agt][ADOPTED-COMPILE-TAIL]\n" + adopt_tail)
                _append_covfilter_summary_row(
                    csv_path=_covfilter_summary_csv(self.pipeline, variant),
                    repo=ctx.repo,
                    fqcn=ctx.fqcn,
                    variant=variant,
                    status="skipped",
                    problem_category="compile_failed",
                    problem_detail=adopt_tail,
                    manual_test_fqcn=ctx.manual_test_fqcn or "",
                    generated_test_fqcn=adopted_test_fqcn or "",
                    out_dir=cov_out,
                    log_file=adopt_compile_log,
                )
                continue

            existing_cov_out = _existing_covfilter_out_dir(self.pipeline, variant, ctx.target_id)
            if self.pipeline.args.skip_passed_by_status and _latest_covfilter_status(
                self.pipeline,
                repo=ctx.repo,
                fqcn=ctx.fqcn,
                variant=variant,
            ) == "passed":
                print(
                    f'[agt] adopted-covfilter: Skip (existing passed status): repo="{ctx.repo}" fqcn="{ctx.fqcn}" variant="{variant}"'
                )
                _append_covfilter_summary_row(
                    csv_path=_covfilter_summary_csv(self.pipeline, variant),
                    repo=ctx.repo,
                    fqcn=ctx.fqcn,
                    variant=variant,
                    status="skipped",
                    problem_category="existing_passed_status",
                    problem_detail="Latest covfilter summary row already has status=passed.",
                    manual_test_fqcn=ctx.manual_test_fqcn or "",
                    generated_test_fqcn=adopted_test_fqcn or "",
                    out_dir=existing_cov_out or cov_out,
                    log_file=cov_log,
                )
                continue
            if self.pipeline.args.skip_passed and existing_cov_out is not None:
                print(
                    f'[agt] adopted-covfilter: Skip (existing passed output): repo="{ctx.repo}" fqcn="{ctx.fqcn}" variant="{variant}"'
                )
                _append_covfilter_summary_row(
                    csv_path=_covfilter_summary_csv(self.pipeline, variant),
                    repo=ctx.repo,
                    fqcn=ctx.fqcn,
                    variant=variant,
                    status="skipped",
                    problem_category="existing_passed_output",
                    problem_detail="Existing covfilter output found for a previously passed target.",
                    manual_test_fqcn=ctx.manual_test_fqcn or "",
                    generated_test_fqcn=adopted_test_fqcn or "",
                    out_dir=existing_cov_out,
                    log_file=cov_log,
                )
                continue
            if self.pipeline.args.skip_exists and existing_cov_out is not None:
                print(
                    f'[agt] adopted-covfilter: Skip (existing covfilter output): repo="{ctx.repo}" fqcn="{ctx.fqcn}" variant="{variant}"'
                )
                _append_covfilter_summary_row(
                    csv_path=_covfilter_summary_csv(self.pipeline, variant),
                    repo=ctx.repo,
                    fqcn=ctx.fqcn,
                    variant=variant,
                    status="skipped",
                    problem_category="existing_output",
                    problem_detail="Existing covfilter output found.",
                    manual_test_fqcn=ctx.manual_test_fqcn or "",
                    generated_test_fqcn=adopted_test_fqcn or "",
                    out_dir=existing_cov_out,
                    log_file=cov_log,
                )
                continue

            print(f'[agt] Running adopted covfilter ({variant}): repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
            ok_cov, cov_tail = _run_covfilter_with_retries(
                pipeline=self.pipeline,
                ctx=ctx,
                variant=f"adopted-{variant}",
                source_files=[*ctx.manual_sources, adopted_src],
                coverage_filter_jar=self.pipeline.covfilter_jar,
                libs_glob_cp=self.pipeline.args.libs_cp,
                test_classes_dir=adopted_build,
                sut_classes_dir=cov_classes_dir,
                out_dir=cov_out,
                manual_test_fqcn=ctx.manual_test_fqcn,
                generated_test_fqcn=adopted_test_fqcn,
                jacoco_agent_jar=self.pipeline.jacoco_agent,
                sut_cp_entry=ctx.sut_jar,
                log_file=cov_log,
            )
            if not ok_cov or not covfilter_output_exists(cov_out):
                problem_category = "covfilter_failed" if not ok_cov else "missing_output"
                problem_detail = cov_tail if not ok_cov else "Covfilter finished without producing test_deltas_all.csv."
                _append_covfilter_summary_row(
                    csv_path=_covfilter_summary_csv(self.pipeline, variant),
                    repo=ctx.repo,
                    fqcn=ctx.fqcn,
                    variant=variant,
                    status="failed",
                    problem_category=problem_category,
                    problem_detail=problem_detail,
                    manual_test_fqcn=ctx.manual_test_fqcn or "",
                    generated_test_fqcn=adopted_test_fqcn or "",
                    out_dir=cov_out,
                    log_file=cov_log,
                )
            else:
                _append_covfilter_summary_row(
                    csv_path=_covfilter_summary_csv(self.pipeline, variant),
                    repo=ctx.repo,
                    fqcn=ctx.fqcn,
                    variant=variant,
                    status="passed",
                    problem_category="",
                    problem_detail="",
                    manual_test_fqcn=ctx.manual_test_fqcn or "",
                    generated_test_fqcn=adopted_test_fqcn or "",
                    out_dir=cov_out,
                    log_file=cov_log,
                )
                self.pipeline.ran += 1
            if not ok_cov:
                print(f'[agt] adopted-covfilter: FAIL (see {cov_log})')
                print("[agt][ADOPTED-COVFILTER-TAIL]\n" + cov_tail)
        return True
