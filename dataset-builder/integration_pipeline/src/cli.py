from __future__ import annotations

import sys

from .pipeline import app as pipeline_app


def main() -> int:
    argv = sys.argv[1:]
    if argv and argv[0] in pipeline_app.STEP_CHOICES:
        argv = ["--step", argv[0]] + argv[1:]
    parser = pipeline_app.build_arg_parser()
    args = parser.parse_args(argv)
    return pipeline_app.run_pipeline(args)


if __name__ == "__main__":
    raise SystemExit(main())
