#!/usr/bin/env python3

import argparse
import os
import re
import sys
import subprocess
from typing import TYPE_CHECKING

from .base import Step

if TYPE_CHECKING:
    from ..pipeline.pipeline import TargetContext


def iter_target_files(root):
    for dirpath, _, filenames in os.walk(root):
        for name in filenames:
            if name.endswith(".java"):
                yield os.path.join(dirpath, name)


def strip_import(path, pattern, dry_run):
    try:
        with open(path, "r", encoding="utf-8") as handle:
            lines = handle.readlines()
    except OSError as exc:
        print(f"error: failed to read {path}: {exc}", file=sys.stderr)
        return False

    new_lines = [line for line in lines if not pattern.match(line)]
    if new_lines == lines:
        return False

    if dry_run:
        return True

    try:
        with open(path, "w", encoding="utf-8") as handle:
            handle.writelines(new_lines)
    except OSError as exc:
        print(f"error: failed to write {path}: {exc}", file=sys.stderr)
        return False

    return True


def replace_extends(path, pattern, dry_run):
    try:
        with open(path, "r", encoding="utf-8") as handle:
            lines = handle.readlines()
    except OSError as exc:
        print(f"error: failed to read {path}: {exc}", file=sys.stderr)
        return False

    changed = False
    new_lines = []
    for line in lines:
        updated = pattern.sub("", line)
        if updated != line:
            changed = True
        new_lines.append(updated)

    if not changed:
        return False

    if dry_run:
        return True

    try:
        with open(path, "w", encoding="utf-8") as handle:
            handle.writelines(new_lines)
    except OSError as exc:
        print(f"error: failed to write {path}: {exc}", file=sys.stderr)
        return False

    return True


def add_throws_exception_to_tests(path, dry_run):
    try:
        with open(path, "r", encoding="utf-8") as handle:
            lines = handle.readlines()
    except OSError as exc:
        print(f"error: failed to read {path}: {exc}", file=sys.stderr)
        return False

    changed = False
    new_lines = []
    pending_test = False
    test_annotation = re.compile(
        r"^\s*@(?:Test|ParameterizedTest|RepeatedTest|TestFactory|TestTemplate)\b"
    )

    for line in lines:
        stripped = line.strip()
        if test_annotation.match(stripped):
            pending_test = True
            new_lines.append(line)
            continue

        if pending_test:
            if stripped.startswith("@"):
                new_lines.append(line)
                continue

            if "throws" not in line and ")" in line:
                updated = re.sub(r"\)\s*\{", ") throws Exception {", line)
                if updated == line:
                    updated = re.sub(r"\)\s*$", ") throws Exception", line)
                if updated != line:
                    changed = True
                    new_lines.append(updated)
                    pending_test = False
                    continue

            pending_test = False

        new_lines.append(line)

    if not changed:
        return False

    if dry_run:
        return True

    try:
        with open(path, "w", encoding="utf-8") as handle:
            handle.writelines(new_lines)
    except OSError as exc:
        print(f"error: failed to write {path}: {exc}", file=sys.stderr)
        return False

    return True


def main():
    parser = argparse.ArgumentParser(
        description=(
            "Remove import lines that include _scaffolding and extends *_scaffolding { "
            "from Java files."
        )
    )
    parser.add_argument(
        "path",
        nargs="?",
        default="results/llm-out",
        help="Root directory to scan (default: results/llm-out).",
    )
    parser.add_argument(
        "--dry-run",
        action="store_true",
        help="Report files that would change without editing.",
    )
    args = parser.parse_args()

    import_pattern = re.compile(r"^\s*import\s+.*_scaffolding.*;\s*$")
    extends_pattern = re.compile(r"\s+extends\s+\S*_scaffolding\b")

    if not os.path.isdir(args.path):
        print(f"error: directory not found: {args.path}", file=sys.stderr)
        return 2

    changed = 0
    for path in iter_target_files(args.path):
        modified = strip_import(path, import_pattern, args.dry_run)
        modified = replace_extends(path, extends_pattern, args.dry_run) or modified
        modified = add_throws_exception_to_tests(path, args.dry_run) or modified
        if modified:
            changed += 1
            print(path)

    print(f"files updated: {changed}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())


class AdoptedFixStep(Step):
    step_names = ("adopted-fix",)

    def run(self, ctx: "TargetContext") -> bool:
        if not self.should_run():
            return True
        if not self.pipeline.adopted_root.exists():
            print(f'[agt] adopted-fix: Skip (missing adopted dir): {self.pipeline.adopted_root}')
            return True
        if not self.pipeline.adopted_fix_script.exists():
            print(f'[agt] adopted-fix: Skip (missing script): {self.pipeline.adopted_fix_script}')
            return True

        fix_log = self.pipeline.logs_dir / f"{ctx.target_id}.adopted.fix.log"
        cmd = ["python3", str(self.pipeline.adopted_fix_script), str(self.pipeline.adopted_root)]
        print(f'[agt] adopted-fix: {ctx.target_id}')
        proc = subprocess.run(cmd, stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
        fix_log.write_text(proc.stdout or "", encoding="utf-8", errors="ignore")
        if proc.returncode != 0:
            print(f'[agt] adopted-fix: FAIL (see {fix_log}) repo="{ctx.repo}" fqcn="{ctx.fqcn}"')
        return True
