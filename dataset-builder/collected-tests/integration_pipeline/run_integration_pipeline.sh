#!/usr/bin/env bash
set -euo pipefail

INV="${1:-../_logs/tests_inventory.csv}"
MAP="${2:-../../out/cut_to_fatjar_map.csv}"
STEP="${3:-all}"

echo "[integration-pipeline] Starting (step: ${STEP})..."

python -m integration_pipeline.pipeline_main \
  "${INV}" \
  "${MAP}" \
  --generated-dir ../generated \
  --manual-dir ../manual \
  --repos-dir ../../repos \
  --libs-cp "libs/*" \
  --build-dir build/agt \
  --out-dir jacoco-out/agt \
  --mode both \
  --do-covfilter \
  --filter-only-agt-covered \
  --covfilter-jar coverage-filter-1.0-SNAPSHOT.jar \
  --covfilter-out tmp/covfilter \
  --step "${STEP}"

echo "[integration-pipeline] Done."
