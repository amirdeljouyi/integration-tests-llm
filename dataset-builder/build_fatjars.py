#!/usr/bin/env python3
from __future__ import annotations
import argparse
from pathlib import Path

from fatjar_builder.config import BuildConfig
from fatjar_builder.csvio import CsvIO
from fatjar_builder.repo_scan import RepoScanner
from fatjar_builder.build_root import BuildRootDetector
from fatjar_builder.logging_ctx import LoggingContext
from fatjar_builder.command_runner import CommandRunner
from fatjar_builder.wrappers import WrapperSelector
from fatjar_builder.jars import JarPicker
from fatjar_builder.maven_fatjar import MavenFatJarBuilder
from fatjar_builder.gradle_fatjar import GradleFatJarBuilder
from fatjar_builder.orchestrator import BuildOrchestrator


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("cut_csv", help="Path to selected_cut_classes.csv")
    ap.add_argument("--mode", choices=["local", "docker"], default="local")
    ap.add_argument("--base-dir", default=".")
    ap.add_argument("--java-home", default="")
    ap.add_argument("--java21-home", default="")
    ap.add_argument("--log-dir", default="")
    ap.add_argument("--repos-csv", default="")
    ap.add_argument("--out-map-csv", default="")
    args = ap.parse_args()

    cfg = BuildConfig(
        cut_csv=Path(args.cut_csv).resolve(),
        mode=args.mode,
        base_dir=Path(args.base_dir).resolve(),
        java_home=args.java_home,
        java21_home=args.java21_home,
        log_dir=Path(args.log_dir).resolve() if args.log_dir else None,
        repos_csv=Path(args.repos_csv).resolve() if args.repos_csv else None,
        out_map_csv=Path(args.out_map_csv).resolve() if args.out_map_csv else None,
    )

    lc = LoggingContext(cfg.resolved_log_dir())
    logger = lc.setup()
    logger.info("Starting build_fatjars (split OOP)")
    logger.info("Args: %s", vars(args))

    scanner = RepoScanner()
    detector = BuildRootDetector(scanner)
    runner = CommandRunner(lc)
    wrappers = WrapperSelector()
    jar_picker = JarPicker()

    maven = MavenFatJarBuilder(runner, wrappers, detector, jar_picker, logger)
    gradle = GradleFatJarBuilder(runner, wrappers, detector, jar_picker)

    io = CsvIO()
    orch = BuildOrchestrator(cfg, io, detector, maven, gradle, lc)
    orch.run()


if __name__ == "__main__":
    main()