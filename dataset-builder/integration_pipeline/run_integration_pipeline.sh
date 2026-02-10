#!/usr/bin/env bash
set -euo pipefail

INV="${1:-../_logs/tests_inventory.csv}"
MAP="${2:-../../out/cut_to_fatjar_map.csv}"
STEP="${3:-all}"
SUBSTEP="${4:-}"
TOP_N="${5:-}"

STEP_LABEL="${STEP}"
if [[ -n "${SUBSTEP}" ]]; then
  STEP_LABEL="${STEP} ${SUBSTEP}"
fi
echo "[integration-pipeline] Starting (step: ${STEP_LABEL})..."

CMD_ARGS=(
  "${INV}"
  "${MAP}"
  "${STEP}"
)
if [[ -n "${SUBSTEP}" ]]; then
  CMD_ARGS+=("${SUBSTEP}")
fi

EXTRA_ARGS=()
if [[ "${STEP}" == "reduce" && -n "${TOP_N}" ]]; then
  EXTRA_ARGS+=(--max-tests "${TOP_N}")
elif [[ "${STEP}" == "adopted" && "${SUBSTEP}" == "reduce" && -n "${TOP_N}" ]]; then
  EXTRA_ARGS+=(--max-tests "${TOP_N}")
elif [[ "${STEP}" == "coverage" && "${SUBSTEP}" == "compare-reduced" && -n "${TOP_N}" ]]; then
  EXTRA_ARGS+=(--top-n "${TOP_N}")
fi

python -m src \
  --generated-dir ../collected-tests/generated \
  --manual-dir ../collected-tests/manual \
  --repos-dir ../repos \
  --libs-cp "libs/*" \
  --build-dir build/agt \
  --out-dir tmp \
  --mode both \
  "${CMD_ARGS[@]}" \
  "${EXTRA_ARGS[@]}"

echo "[integration-pipeline] Done."
