#!/usr/bin/env python3
from __future__ import annotations

import re
from pathlib import Path

IMPORT_REPLACEMENTS = [
    (r'^\s*import\s+org\.junit\.Test\s*;\s*$', 'import org.junit.jupiter.api.Test;'),
    (r'^\s*import\s+org\.junit\.Before\s*;\s*$', 'import org.junit.jupiter.api.BeforeEach;'),
    (r'^\s*import\s+org\.junit\.After\s*;\s*$', 'import org.junit.jupiter.api.AfterEach;'),
    (r'^\s*import\s+org\.junit\.BeforeClass\s*;\s*$', 'import org.junit.jupiter.api.BeforeAll;'),
    (r'^\s*import\s+org\.junit\.AfterClass\s*;\s*$', 'import org.junit.jupiter.api.AfterAll;'),
    (r'^\s*import\s+org\.junit\.Ignore\s*;\s*$', 'import org.junit.jupiter.api.Disabled;'),

    (r'^\s*import\s+org\.junit\.Assert\s*;\s*$', 'import org.junit.jupiter.api.Assertions;'),
    (r'^\s*import\s+static\s+org\.junit\.Assert\.\*\s*;\s*$', 'import static org.junit.jupiter.api.Assertions.*;'),

    (r'^\s*import\s+org\.junit\.Assume\s*;\s*$', 'import org.junit.jupiter.api.Assumptions;'),
    (r'^\s*import\s+static\s+org\.junit\.Assume\.\*\s*;\s*$', 'import static org.junit.jupiter.api.Assumptions.*;'),

    # Keep JUnit4 Rule annotation (works with junit-jupiter-migrationsupport)
    (r'^\s*import\s+org\.junit\.Rule\s*;\s*$', 'import org.junit.Rule;'),
]

ANNOTATION_REPLACEMENTS = [
    (r'@Before\b', '@BeforeEach'),
    (r'@After\b', '@AfterEach'),
    (r'@BeforeClass\b', '@BeforeAll'),
    (r'@AfterClass\b', '@AfterAll'),
    (r'@Ignore\b', '@Disabled'),
]

TEST_TIMEOUT_RE = re.compile(r'@Test\s*\(\s*timeout\s*=\s*(\d+)\s*\)')

def add_import(lines: list[str], import_stmt: str) -> list[str]:
    if any(l.strip() == import_stmt for l in lines):
        return lines

    pkg_idx = None
    last_import_idx = None
    for i, l in enumerate(lines):
        if l.startswith("package "):
            pkg_idx = i
        if l.strip().startswith("import "):
            last_import_idx = i

    if last_import_idx is not None:
        insert_at = last_import_idx + 1
    elif pkg_idx is not None:
        insert_at = pkg_idx + 1
        if insert_at < len(lines) and lines[insert_at].strip() != "":
            lines.insert(insert_at, "\n")
            insert_at += 1
    else:
        insert_at = 0

    lines.insert(insert_at, import_stmt + "\n")
    return lines

def replace_imports(text: str) -> str:
    for pat, rep in IMPORT_REPLACEMENTS:
        text = re.sub(pat, rep, text, flags=re.MULTILINE)
    return text

def replace_annotations(text: str) -> str:
    for pat, rep in ANNOTATION_REPLACEMENTS:
        text = re.sub(pat, rep, text)
    return text

def convert_timeout_annotation(text: str) -> tuple[str, bool]:
    used_timeout = False

    def repl(m: re.Match) -> str:
        nonlocal used_timeout
        used_timeout = True
        ms = int(m.group(1))
        if ms % 1000 == 0:
            secs = ms // 1000
            return "@Test\n  @Timeout(value = %d, unit = TimeUnit.SECONDS)" % secs
        return "@Test\n  @Timeout(value = %d, unit = TimeUnit.MILLISECONDS)" % ms

    return TEST_TIMEOUT_RE.sub(repl, text), used_timeout

def ensure_timeout_imports(text: str) -> str:
    lines = text.splitlines(keepends=True)
    lines = add_import(lines, "import org.junit.jupiter.api.Timeout;")
    lines = add_import(lines, "import java.util.concurrent.TimeUnit;")
    return "".join(lines)

def normalize_rule_annotation(text: str) -> tuple[str, bool]:
    rule_present = ("@org.junit.Rule" in text) or ("@Rule" in text)
    text = re.sub(r'@org\.junit\.Rule\b', '@Rule', text)
    rule_present = rule_present or ("@Rule" in text)
    return text, rule_present

def ensure_enable_rule_migration_support(text: str) -> str:
    if "@Rule" not in text:
        return text
    if "EnableRuleMigrationSupport" in text:
        return text

    lines = text.splitlines(keepends=True)
    lines = add_import(lines, "import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport;")
    text = "".join(lines)

    text = re.sub(
        r'(\n\s*)(public\s+class\s+|class\s+)',
        r'\1@EnableRuleMigrationSupport\n\1\2',
        text,
        count=1
    )
    return text

def is_scaffolding_file(path: Path) -> bool:
    return path.name.endswith("_scaffolding.java")

def transform_java_file(path: Path, keep_scaffolding_junit4: bool) -> bool:
    original = path.read_text(encoding="utf-8", errors="replace")

    if keep_scaffolding_junit4 and is_scaffolding_file(path):
        return False  # leave scaffolding untouched

    text = original
    text = replace_imports(text)
    text = replace_annotations(text)

    # Rule support (EvoSuite scaffolding)
    text, rule_present = normalize_rule_annotation(text)
    if rule_present:
        lines = text.splitlines(keepends=True)
        lines = add_import(lines, "import org.junit.Rule;")
        text = "".join(lines)
        text = ensure_enable_rule_migration_support(text)

    # Timeouts
    text, used_timeout = convert_timeout_annotation(text)
    if used_timeout:
        text = ensure_timeout_imports(text)

    changed = (text != original)
    if changed:
        path.write_text(text, encoding="utf-8")
    return changed

def main() -> None:
    import argparse
    ap = argparse.ArgumentParser(description="Bulk convert JUnit4 tests to JUnit5 (incl. EvoSuite scaffolding).")
    ap.add_argument("root", help="Root directory containing .java tests")
    ap.add_argument("--dry-run", action="store_true", help="Don’t write files; only report")
    ap.add_argument("--include", default=None, help="Regex: only process files whose path matches")
    ap.add_argument("--exclude", default=None, help="Regex: skip files whose path matches")
    ap.add_argument("--keep-scaffolding-junit4", action="store_true",
                    help="Do NOT convert *_scaffolding.java (use JUnit Vintage for those)")
    args = ap.parse_args()

    root = Path(args.root)
    if not root.exists():
        raise SystemExit(f"Not found: {root}")

    include_re = re.compile(args.include) if args.include else None
    exclude_re = re.compile(args.exclude) if args.exclude else None

    changed_files: list[str] = []
    total = 0

    for p in root.rglob("*.java"):
        rel = str(p)
        if include_re and not include_re.search(rel):
            continue
        if exclude_re and exclude_re.search(rel):
            continue

        total += 1
        before = p.read_text(encoding="utf-8", errors="replace")
        changed = transform_java_file(p, keep_scaffolding_junit4=args.keep_scaffolding_junit4)

        if args.dry_run and changed:
            p.write_text(before, encoding="utf-8")

        if changed:
            changed_files.append(rel)

    print(f"Processed {total} file(s). Changed {len(changed_files)} file(s).")
    for f in changed_files[:100]:
        print(" -", f)
    if len(changed_files) > 100:
        print(f" ... and {len(changed_files) - 100} more")

if __name__ == "__main__":
    main()