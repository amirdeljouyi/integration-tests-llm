from __future__ import annotations

from pathlib import Path


def _coverage_percent(covered: int, total: int) -> float:
    if total <= 0:
        return 0.0
    return (covered / total) * 100.0


def write_coverage_row(
    *,
    csv_path: Path,
    repo: str,
    fqcn: str,
    variant: str,
    line_covered: int,
    line_total: int,
    branch_covered: int,
    branch_total: int,
    status: str,
) -> None:
    line_pct = _coverage_percent(line_covered, line_total)
    branch_pct = _coverage_percent(branch_covered, branch_total)
    failed = 1 if status == "failed" else 0
    timeout = 1 if status == "timeout" else 0
    skipped = 1 if status == "skipped" else 0
    with csv_path.open("a", encoding="utf-8") as f:
        f.write(
            f"{repo},{fqcn},{variant},"
            f"{line_pct:.6f},{line_covered},{line_total},"
            f"{branch_pct:.6f},{branch_covered},{branch_total},"
            f"{failed},{timeout},{skipped}\n"
        )
