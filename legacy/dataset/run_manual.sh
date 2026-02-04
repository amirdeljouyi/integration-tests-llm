#!/usr/bin/env bash
set -euo pipefail

CSV="${1:-projects.csv}"

SUT_JAR="tmp/quarkus_classes"
JACOCO_AGENT="jacoco-deps/org.jacoco.agent-run-0.8.14.jar"
LIBS_SRC="libs/*"

TEST_SRC_DIR="manual-tests/"
TEST_CLASSES_DIR="build/test-classes"

./coverage_compile.sh $TEST_SRC_DIR $TEST_CLASSES_DIR

OUT_DIR="jacoco-out/manual"
mkdir -p "$OUT_DIR"

# Read CSV, skip header
tail -n +2 "$CSV" | while IFS=, read -r project class manual_test agt_test; do
  # Trim whitespace (safe)
  project="$(echo "$project" | xargs)"
  class="$(echo "$class" | xargs)"
  manual_test="$(echo "$manual_test" | xargs)"

  if [[ -z "$manual_test" || "$manual_test" == "null" ]]; then
    echo "[manual] Skip (no manual_test): project=$project class=$class"
    continue
  fi

  # If your CSV uses simple class names, qualify it using the 'class' package
  # Example: class=io.quarkus.qute.deployment.QuteProcessor
  # package=io.quarkus.qute.deployment
  pkg="${class%.*}"
  if [[ "$manual_test" != *.* ]]; then
    manual_fqcn="${pkg}.${manual_test}"
  else
    manual_fqcn="$manual_test"
  fi

  exec_file="${OUT_DIR}/${project}_${manual_test}.exec"
  rm -f "$exec_file"

  echo "[manual] Running: $manual_fqcn -> $exec_file"

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
    "${manual_fqcn}"
done