#!/usr/bin/env bash
set -euo pipefail

# ---- config you may edit ----
CUT_CSV="${1:-selected_cut_classes.csv}"     # first arg (optional)
MODE="${MODE:-local}"                        # local | docker
BASE_DIR="${BASE_DIR:-.}"                    # where repos/out/.cache live in local mode
PY_SCRIPT="${PY_SCRIPT:-build_fatjars.py}"   # your python build script name
UPDATE_EXISTING="${UPDATE_EXISTING:-0}"      # 1 to git fetch/reset existing repos
LOG_DIR="${LOG_DIR:-}"                       # optional override, e.g., ./out/logs
# -----------------------------

if [[ ! -f "$CUT_CSV" ]]; then
  echo "[ERROR] CUT_CSV not found: $CUT_CSV" >&2
  exit 1
fi

if [[ ! -f "$PY_SCRIPT" ]]; then
  echo "[ERROR] Python script not found: $PY_SCRIPT" >&2
  echo "Set PY_SCRIPT env var or rename it accordingly." >&2
  exit 1
fi

# ---- find JDK 21 from SDKMAN without any interactive shell switching ----
if [[ -z "${SDKMAN_CANDIDATES_DIR:-}" ]]; then
  echo "[ERROR] SDKMAN_CANDIDATES_DIR is not set. Did you install SDKMAN?" >&2
  echo "If you don't want SDKMAN, set JAVA21_HOME manually, e.g.:" >&2
  echo "  JAVA21_HOME=/path/to/jdk21 ./run_fat_build.sh" >&2
  exit 1
fi

if [[ -z "${JAVA21_HOME:-}" ]]; then
  # pick latest installed 21.x
  JAVA21_HOME="$SDKMAN_CANDIDATES_DIR/java/$(ls -1 "$SDKMAN_CANDIDATES_DIR/java" \
    | grep -E '^21\.' \
    | sort -V \
    | tail -n 1)"
fi

if [[ ! -d "$JAVA21_HOME" ]]; then
  echo "[ERROR] JAVA21_HOME does not exist: $JAVA21_HOME" >&2
  echo "Installed SDKMAN Javas:" >&2
  ls -1 "$SDKMAN_CANDIDATES_DIR/java" || true
  exit 1
fi

echo "[INFO] Using JAVA21_HOME=$JAVA21_HOME"

# Build command
cmd=(python3 "$PY_SCRIPT" "$CUT_CSV" --mode "$MODE" --base-dir "$BASE_DIR" --java21-home "$JAVA21_HOME")

if [[ "$UPDATE_EXISTING" == "1" ]]; then
  cmd+=(--update-existing)
fi

if [[ -n "$LOG_DIR" ]]; then
  cmd+=(--log-dir "$LOG_DIR")
fi

# Optional: force all builds to run under JDK 21 (not only retries)
export JAVA_HOME="$JAVA21_HOME"
export PATH="$JAVA21_HOME/bin:$PATH"

echo "[INFO] Running: ${cmd[*]}"
"${cmd[@]}"

echo "[DONE] Fat-jar build finished."