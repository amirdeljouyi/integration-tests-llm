#!/usr/bin/env bash
set -euo pipefail

export JDK_JAVA_OPTIONS="-Djdk.attach.allowAttachSelf=true"

# Usage:
#   ./run_evosuite_from_cut_map.sh <cut_to_fatjar_map.csv> [attempt]
#
# Requirements:
# - llmsuite-r.jar in current dir (or adjust path)
# - repos checked out under REPOS_DIR (default: ./repos)
#
# Behavior:
# - First run with fatjar only
# - If error log shows ClassNotFound/ClassNotFoundException -> compute runtime CP and retry

CSV_PATH="${1:-}"
ATTEMPT="${2:-}"

if [[ -z "${CSV_PATH}" || ! -f "${CSV_PATH}" ]]; then
  echo "ERROR: Provide cut_to_fatjar_map.csv as the first argument."
  echo "Example: $0 ./out/cut_to_fatjar_map.csv"
  exit 1
fi

: "${REPOS_DIR:=./repos}"   # override like: REPOS_DIR=/Users/.../dataset-builder/repos ./run...
: "${OUTPUT_DIR:=./result}"

LOG_DIR="${OUTPUT_DIR}/log"
mkdir -p "${LOG_DIR}" "${OUTPUT_DIR}/evosuite-report" "${OUTPUT_DIR}/generated-tests"

# Attempt naming (same as your old behavior)
if [[ -z "${ATTEMPT}" ]]; then
  PREFIX="$(date '+%Y-%m-%d-%H-%M')"
  POSTFIX="/${PREFIX}"
  JUNIT_SUFFIX="_ESTest"
  DATEMPT=""
else
  PREFIX="${ATTEMPT}"
  POSTFIX=""
  JUNIT_SUFFIX="_${ATTEMPT}_ESTest"
  DATEMPT="-Dattempt=${ATTEMPT}"
fi

GLOBAL_OUT="${LOG_DIR}/global_output.log"
GLOBAL_ERR="${LOG_DIR}/global_error.log"
: > "${GLOBAL_OUT}"
: > "${GLOBAL_ERR}"

safe_name() { echo "$1" | sed 's#[^A-Za-z0-9._/-]#_#g'; }
safe_id() { echo "$1" | sed 's#[^A-Za-z0-9._-]#_#g'; }

is_bad_fatjar() {
  local p="$1"
  [[ -z "$p" ]] && return 0
  [[ "$p" == "FAIL" || "$p" == "SKIP" || "$p" == "SKIP-REPO" ]] && return 0
  [[ "$p" == SKIP:* ]] && return 0
  return 1
}

needs_cp_retry() {
  local errlog="$1"
  grep -qE "ClassNotFoundException|Class not found:" "$errlog"
}

# ---- Maven runtime CP (module-aware) ----
maven_runtime_cp() {
  local repo_dir="$1"
  local module_rel="$2"

  # Find nearest root pom above module dir, else fall back to module pom
  local module_dir="${repo_dir}"
  if [[ -n "${module_rel}" && "${module_rel}" != "." && "${module_rel}" != "root" ]]; then
    module_dir="${repo_dir}/${module_rel}"
  fi

  local cur="${module_dir}"
  local root_pom=""
  while [[ "$cur" != "$repo_dir" && "$cur" != "/" ]]; do
    if [[ -f "$cur/pom.xml" ]]; then root_pom="$cur/pom.xml"; break; fi
    cur="$(dirname "$cur")"
  done
  if [[ -z "$root_pom" && -f "$repo_dir/pom.xml" ]]; then
    root_pom="$repo_dir/pom.xml"
  fi

  local outcp
  outcp="$(mktemp)"
  rm -f "$outcp"
  touch "$outcp"

  # Prefer reactor build-classpath if we found a root pom and module_rel looks usable
  if [[ -n "$root_pom" && -n "${module_rel}" && "${module_rel}" != "." && "${module_rel}" != "root" ]]; then
    ( cd "$repo_dir" && \
      mvn -q -f "$root_pom" -pl "$module_rel" -am -DskipTests \
        dependency:build-classpath -Dmdep.pathSeparator=":" -Dmdep.outputFile="$outcp" \
      ) >/dev/null 2>&1 || true
  fi

  # If empty, try module pom directly
  if [[ ! -s "$outcp" && -f "$module_dir/pom.xml" ]]; then
    ( cd "$module_dir" && \
      mvn -q -f "$module_dir/pom.xml" -DskipTests \
        dependency:build-classpath -Dmdep.pathSeparator=":" -Dmdep.outputFile="$outcp" \
      ) >/dev/null 2>&1 || true
  fi

  # Print result (may be empty)
  cat "$outcp" 2>/dev/null || true
  rm -f "$outcp"
}

# ---- Gradle runtime CP via init script (no project changes) ----
gradle_runtime_cp() {
  local repo_dir="$1"
  local module_rel="$2"

  local module_dir="${repo_dir}"
  if [[ -n "${module_rel}" && "${module_rel}" != "." && "${module_rel}" != "root" ]]; then
    module_dir="${repo_dir}/${module_rel}"
  fi

  local init
  init="$(mktemp)"
  cat > "$init" <<'GR'
allprojects { p ->
  p.tasks.register("printRuntimeClasspath") {
    doLast {
      def cfg = null
      if (p.configurations.findByName("runtimeClasspath") != null) {
        cfg = p.configurations.getByName("runtimeClasspath")
      } else if (p.configurations.findByName("runtime") != null) {
        cfg = p.configurations.getByName("runtime")
      }
      if (cfg == null) { return }
      cfg.resolve()
      def files = cfg.files.collect { it.absolutePath }.unique()
      print(files.join(":"))
    }
  }
}
GR

  local gradle_cmd="gradle"
  if [[ -x "$repo_dir/gradlew" ]]; then
    gradle_cmd="$repo_dir/gradlew"
  elif [[ -x "$module_dir/gradlew" ]]; then
    gradle_cmd="$module_dir/gradlew"
  fi

  local cp=""
  cp="$(
    ( cd "$module_dir" && "$gradle_cmd" --no-daemon -q -I "$init" printRuntimeClasspath ) 2>/dev/null || true
  )"

  rm -f "$init"
  echo "$cp"
}

# ---- Main CSV reading using Python csv module ----
PY_TSV=$(
python3 - <<'PY' "$CSV_PATH"
import csv, sys
path = sys.argv[1]
with open(path, newline="", encoding="utf-8") as f:
    r = csv.DictReader(f)
    for row in r:
        repo = (row.get("repo") or "").strip()
        fqcn = (row.get("fqcn") or "").strip()
        fatjar = (row.get("fatjar_path") or "").strip()
        class_path = (row.get("class_path") or "").strip()
        build_tool = (row.get("build_tool") or "").strip()
        module_rel = (row.get("module_rel") or "").strip()
        print("\t".join([repo, fqcn, fatjar, class_path, build_tool, module_rel]))
PY
)

run_evosuite_once() {
  local projectcp="$1"
  local fqcn="$2"
  local stat_log="$3"
  local err_log="$4"

  java \
    --add-opens=java.base/java.util=ALL-UNNAMED \
    --add-opens=java.base/sun.util.calendar=ALL-UNNAMED \
    --add-opens=java.base/java.lang=ALL-UNNAMED \
    -jar llmsuite-r.jar \
    -projectCP "$projectcp" \
    -class "$fqcn" \
    -Dcriterion=BRANCH:LINE:OUTPUT:METHOD:CBRANCH \
    -Dtest_naming_strategy=coverage \
    -Dvariable_naming_strategy=TYPE_BASED \
    -Dassertion_timeout=100000 \
    -Dsearch_budget=360 \
    -Dminimize=true \
    -Dcoverage=true \
    -Dwrite_junit_timeout=100000 \
    -Dextra_timeout=10000 \
    -Dalgorithm=DYNAMOSA \
    ${DATEMPT} \
    -Doutput_variables=TARGET_CLASS,criterion,Coverage,Total_Goals,BranchCoverage,LineCoverage,OutputCoverage,CBranchCoverage,MethodCoverage,Covered_Goals,Fitness,Tests_Executed,Total_Time \
    -Dtimeline_interval=5000 \
    -Ddefuse_debug_mode=true \
    -Dtest_format=JUNIT4 \
    -Djunit_check_timeout=10000 \
    -Dcheck_contracts=false \
    -Dllm_static_constant_pool=false \
    -Dsandbox=false \
    -Dvirtual_fs=false \
    -Dvirtual_net=false \
    -Duse_separate_classloader=false \
    -Dno_runtime_dependency=false \
    -Dreset_static_fields=false \
    -Dreport_dir="${OUTPUT_DIR}/evosuite-report" \
    -Dtest_dir="${OUTPUT_DIR}/generated-tests${POSTFIX}" \
    -Djunit_suffix="${JUNIT_SUFFIX}" \
    -Dbytecode_logging_mode=FILE_DUMP \
    > "$stat_log" 2> "$err_log"
}

{
  while IFS=$'\t' read -r repo fqcn fatjar class_path build_tool module_rel; do
    [[ -z "$repo" || -z "$fqcn" ]] && continue

    if is_bad_fatjar "$fatjar"; then
      echo "[SKIP] ${repo} fqcn=${fqcn} fatjar_path='${fatjar}'"
      continue
    fi
    if [[ ! -f "$fatjar" ]]; then
      echo "[SKIP] ${repo} fqcn=${fqcn} fatjar missing on disk: ${fatjar}"
      continue
    fi

    repo_dir="${REPOS_DIR}/$(safe_name "$repo")"
    if [[ ! -d "$repo_dir" ]]; then
      echo "[WARN] Repo dir not found for fallback CP generation: ${repo_dir}"
      echo "       (Will still try fatjar-only run)"
    fi

    id="$(safe_id "$fqcn")"
    STAT_LOG="${LOG_DIR}/${id}-${PREFIX}-stat.log"
    ERR_LOG="${LOG_DIR}/${id}-${PREFIX}-error.log"

    echo "============================================================"
    echo "Repo: ${repo}"
    echo "Module: ${module_rel}"
    echo "Build tool: ${build_tool}"
    echo "FQCN: ${fqcn}"
    echo "Fat jar: ${fatjar}"
    echo "Attempt: ${ATTEMPT:-<none>}"
    echo

    # 1) fatjar-only
    echo "[RUN] fatjar-only"
    if run_evosuite_once "$fatjar" "$fqcn" "$STAT_LOG" "$ERR_LOG"; then
      echo "[OK] fatjar-only"
      continue
    fi

    # If it failed, see if it looks like missing deps
    if ! needs_cp_retry "$ERR_LOG"; then
      echo "[FAIL] Not a ClassNotFound-style failure (see $ERR_LOG)"
      continue
    fi

    echo "[RETRY] Detected missing class; generating runtime classpath..."

    extra_cp=""
    if [[ -d "$repo_dir" ]]; then
      if [[ "$build_tool" == "maven" ]]; then
        extra_cp="$(maven_runtime_cp "$repo_dir" "$module_rel")"
      elif [[ "$build_tool" == "gradle" ]]; then
        extra_cp="$(gradle_runtime_cp "$repo_dir" "$module_rel")"
      fi
    fi

    # Add common compiled output dirs too (helps when fatjar isn’t actually fat)
    module_dir="$repo_dir"
    if [[ -n "${module_rel}" && "${module_rel}" != "." && "${module_rel}" != "root" ]]; then
      module_dir="$repo_dir/$module_rel"
    fi
    extra_dirs=""
    [[ -d "$module_dir/target/classes" ]] && extra_dirs="${extra_dirs}:$module_dir/target/classes"
    [[ -d "$module_dir/build/classes/java/main" ]] && extra_dirs="${extra_dirs}:$module_dir/build/classes/java/main"
    [[ -d "$module_dir/build/resources/main" ]] && extra_dirs="${extra_dirs}:$module_dir/build/resources/main"

    retry_cp="$fatjar"
    [[ -n "$extra_cp" ]] && retry_cp="${retry_cp}:$extra_cp"
    [[ -n "$extra_dirs" ]] && retry_cp="${retry_cp}${extra_dirs}"

    RETRY_STAT="${LOG_DIR}/${id}-${PREFIX}-retry-stat.log"
    RETRY_ERR="${LOG_DIR}/${id}-${PREFIX}-retry-error.log"

    echo "[RUN] retry with expanded projectCP"
    echo "      (writing retry logs: $RETRY_STAT / $RETRY_ERR)"
    run_evosuite_once "$retry_cp" "$fqcn" "$RETRY_STAT" "$RETRY_ERR" || {
      echo "[FAIL] retry also failed (see $RETRY_ERR)"
      continue
    }
    echo "[OK] retry succeeded"

  done <<< "${PY_TSV}"
} >> "${GLOBAL_OUT}" 2>> "${GLOBAL_ERR}"

echo
echo "DONE"
echo "Global out: ${GLOBAL_OUT}"
echo "Global err: ${GLOBAL_ERR}"
echo "Per-class logs: ${LOG_DIR}/*-${PREFIX}-*.log"