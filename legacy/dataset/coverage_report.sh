#!/usr/bin/env bash
set -euo pipefail

JACOCO_CLI="jacoco-deps/org.jacoco.cli-run-0.8.14.jar"
EXEC_FILE="jacoco.exec"
CLASS_DIR="tmp/quarkus_classes"
CSV_OUT="jacoco.csv"

CUT_PKG="io.quarkus.qute.deployment"   # package of interest
CUT_PREFIX="QuteProcessor"             # class name prefix (QuteProcessor, QuteProcessor.*, etc.)

echo ">>> Generating JaCoCo CSV report..."
java -jar "${JACOCO_CLI}" report "${EXEC_FILE}" \
  --classfiles "${CLASS_DIR}" \
  --csv "${CSV_OUT}" \
  --name "QuteCoverage"

echo
echo ">>> Aggregating coverage for classes in package '${CUT_PKG}' with name starting with '${CUT_PREFIX}'"
echo

awk -F',' -v pkg="${CUT_PKG}" -v pref="${CUT_PREFIX}" '
  BEGIN {
    total_line_missed  = 0;
    total_line_covered = 0;
    found = 0;
  }

  # header row
  NR == 1 {
    header = $0;
    next;
  }

  # JaCoCo CSV columns:
  # 1:GROUP, 2:PACKAGE, 3:CLASS, 4:INSTR_MISS, 5:INSTR_COV,
  # 6:BR_MISS, 7:BR_COV, 8:LINE_MISS, 9:LINE_COV, ...

  $2 == pkg && index($3, pref) == 1 {
    found = 1;

    lm = $8 + 0;  # line_missed
    lc = $9 + 0;  # line_covered

    total_line_missed  += lm;
    total_line_covered += lc;

    if (!printed_header) {
      print "Matching rows:";
      print header;
      printed_header = 1;
    }
    print $0;
  }

  END {
    print "";
    if (!found) {
      printf "No classes found in package '%s' with name starting with '%s'\n", pkg, pref;
      exit 0;
    }

    total = total_line_missed + total_line_covered;
    if (total > 0) {
      pct = 100.0 * total_line_covered / total;
    } else {
      pct = 0.0;
    }

    printf "==============================\n";
    printf "Aggregated line coverage for %s.%s*\n", pkg, pref;
    printf "Lines covered: %d / %d (%.2f%%)\n",
           total_line_covered, total, pct;
    printf "==============================\n";
  }
' "${CSV_OUT}"