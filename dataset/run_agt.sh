#!/usr/bin/env bash
set -euo pipefail

CSV="${1:-projects.csv}"

SUT_JAR="tmp/quarkus_classes"
JACOCO_AGENT="jacoco-deps/org.jacoco.agent-run-0.8.14.jar"
LIBS_SRC="libs/*"

TEST_SRC_DIR="result/generated-tests/"
TEST_CLASSES_DIR="build/test-classes"

./coverage_compile.sh $TEST_SRC_DIR $TEST_CLASSES_DIR

OUT_DIR="jacoco-out/agt"
mkdir -p "$OUT_DIR"

# Read CSV, skip header
tail -n +2 "$CSV" | while IFS=, read -r project class manual_test agt_test; do
  project="$(echo "$project" | xargs)"
  class="$(echo "$class" | xargs)"
  agt_test="$(echo "$agt_test" | xargs)"

  if [[ -z "$agt_test" || "$agt_test" == "null" ]]; then
    echo "[agt] Skip (no agt_test): project=$project class=$class"
    continue
  fi

  pkg="${class%.*}"
  if [[ "$agt_test" != *.* ]]; then
    agt_fqcn="${pkg}.${agt_test}"
  else
    agt_fqcn="$agt_test"
  fi

  exec_file="${OUT_DIR}/${project}_${agt_test}.exec"
  rm -f "$exec_file"

  echo "[agt] Running: $agt_fqcn -> $exec_file"

  java \
    --add-opens java.base/java.lang=ALL-UNNAMED \
    --add-opens java.base/java.lang.reflect=ALL-UNNAMED \
    --add-opens java.base/java.util=ALL-UNNAMED \
    --add-opens java.base/java.net=ALL-UNNAMED \
    --add-opens java.desktop/java.awt=ALL-UNNAMED \
    -Dcustomrunner.fullStackTraces=true \
    "-javaagent:${JACOCO_AGENT}=destfile=${exec_file},append=false,includes=io.quarkus.*" \
    -cp "${LIBS_SRC}:${SUT_JAR}:build/test-classes:coverage-filter-1.0-SNAPSHOT.jar" \
    custom_runner.CustomRunnerJUnit5 \
    "${agt_fqcn}"
done