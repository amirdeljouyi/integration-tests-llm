#!/usr/bin/env python3
from __future__ import annotations

import argparse
import sys
from pathlib import Path


def _load_integration_src() -> None:
    integration_root = Path(__file__).resolve().parent / "integration_pipeline"
    sys.path.insert(0, str(integration_root))


def main() -> int:
    _load_integration_src()
    from src.steps.fatjar import FatjarConfig, run_fatjar

    parser = argparse.ArgumentParser()
    parser.add_argument("cut_csv", help="Path to selected_cut_classes.csv")
    parser.add_argument("--mode", choices=["local", "docker"], default="local")
    parser.add_argument("--base-dir", default=".")
    parser.add_argument("--java-home", default="")
    parser.add_argument("--java21-home", default="")
    parser.add_argument("--log-dir", default="")
    parser.add_argument("--repos-csv", default="")
    parser.add_argument("--out-map-csv", default="")
    parser.add_argument("--failures-csv", default="")
    parser.add_argument("--retry-only", action="store_true")
    args = parser.parse_args()

    return run_fatjar(
        FatjarConfig(
            cut_csv=Path(args.cut_csv).resolve(),
            mode=args.mode,
            base_dir=Path(args.base_dir).resolve(),
            java_home=args.java_home,
            java21_home=args.java21_home,
            log_dir=Path(args.log_dir).resolve() if args.log_dir else None,
            repos_csv=Path(args.repos_csv).resolve() if args.repos_csv else None,
            out_map_csv=Path(args.out_map_csv).resolve() if args.out_map_csv else None,
            failures_csv=Path(args.failures_csv).resolve() if args.failures_csv else None,
            retry_only=args.retry_only,
        )
    )


if __name__ == "__main__":
    raise SystemExit(main())
