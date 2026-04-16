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
    from src.steps.clone import CloneConfig, run_clone

    parser = argparse.ArgumentParser()
    parser.add_argument("cut_csv", help="Path to selected_cut_classes.csv")
    parser.add_argument("--mode", choices=["local", "docker"], default="local")
    parser.add_argument("--base-dir", default=".")
    parser.add_argument("--update-existing", action="store_true")
    parser.add_argument("--log-dir", default="")
    parser.add_argument("--out-repos-csv", default="")
    args = parser.parse_args()

    return run_clone(
        CloneConfig(
            cut_csv=Path(args.cut_csv).resolve(),
            mode=args.mode,
            base_dir=Path(args.base_dir).resolve(),
            update_existing=args.update_existing,
            log_dir=Path(args.log_dir).resolve() if args.log_dir else None,
            out_repos_csv=Path(args.out_repos_csv).resolve() if args.out_repos_csv else None,
        )
    )


if __name__ == "__main__":
    raise SystemExit(main())
