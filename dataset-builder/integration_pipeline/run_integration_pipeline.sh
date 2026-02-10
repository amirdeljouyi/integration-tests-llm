#!/usr/bin/env bash
set -euo pipefail

INV="${1:-../_logs/tests_inventory.csv}"
MAP="${2:-../../out/cut_to_fatjar_map.csv}"
STEP="${3:-all}"

echo "[integration-pipeline] Starting (step: ${STEP})..."

python -m src.cli \
  "${INV}" \
  "${MAP}" \
  --generated-dir ../collected-tests/generated \
  --manual-dir ../collected-tests/manual \
  --repos-dir ../repos \
  --libs-cp "libs/*" \
  --build-dir build/agt \
  --out-dir tmp \
  --mode both \
  --do-covfilter \
  --filter-only-agt-covered \
  --covfilter-jar coverage-filter-1.0-SNAPSHOT.jar \
  --covfilter-out result/covfilter \
  --adopted-dir results/llm-out \
  --step "${STEP}"

echo "[integration-pipeline] Done."
