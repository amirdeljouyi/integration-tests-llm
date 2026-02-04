from __future__ import annotations
import os
from pathlib import Path
from typing import List, Optional

from .command_runner import CommandRunner
from .exceptions import CmdError, SkipBuild
from .wrappers import WrapperSelector
from .build_root import BuildRootDetector
from .jars import JarPicker


class GradleFatJarBuilder:
    def __init__(self, runner: CommandRunner, wrappers: WrapperSelector, detector: BuildRootDetector, jar_picker: JarPicker):
        self.runner = runner
        self.wrappers = wrappers
        self.detector = detector
        self.jar_picker = jar_picker

    def build(self, cache_dir: Path, repo_root: Path, module_dir: Path) -> Path:
        env = os.environ.copy()
        env["GRADLE_USER_HOME"] = str(cache_dir / "gradle-home")

        gradle_root = self.detector.pick_gradle_root(repo_root, scope_dir=module_dir) or module_dir
        gradle = self.wrappers.pick_gradle_cmd(repo_root, gradle_root)

        libs_candidates = [module_dir / "build" / "libs", gradle_root / "build" / "libs"]

        def pick_any_jar() -> Optional[Path]:
            for d in libs_candidates:
                j = self.jar_picker.pick_best_jar(d)
                if j:
                    return j
            return None

        def run_gradle(args: List[str]) -> None:
            self.runner.run(gradle + ["--no-daemon"] + args, cwd=gradle_root, env=env)

        def looks_like_jdk25(out: str) -> bool:
            return ("requires at least JDK 25" in out) or ("at least JDK 25" in out)

        def is_dep_verification(out: str) -> bool:
            return ("Dependency verification failed" in out) or ("verification-metadata.xml" in out)

        def is_missing_artifact(out: str) -> bool:
            return ("Could not find " in out) and ("Searched in the following locations" in out)

        # jar
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

        jar = pick_any_jar()
        if jar:
            return jar

        # shadowJar
        try:
            run_gradle(["shadowJar", "-x", "test"])
        except CmdError as e2:
            if looks_like_jdk25(e2.out):
                raise SkipBuild("Requires at least JDK 25 (skipping)")
            if is_dep_verification(e2.out):
                run_gradle(["--dependency-verification=off", "shadowJar", "-x", "test"])
            elif is_missing_artifact(e2.out):
                try:
                    run_gradle(["--refresh-dependencies", "jar", "-x", "test"])
                except CmdError:
                    raise SkipBuild("Missing dependency artifact (cannot resolve)")
            else:
                run_gradle(["jar", "-x", "test"])

        jar = pick_any_jar()
        if not jar:
            raise FileNotFoundError(
                f"No jars produced under any of: {', '.join(str(p) for p in libs_candidates)}"
            )
        return jar