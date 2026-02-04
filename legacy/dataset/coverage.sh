#!/usr/bin/env bash
set -e

SUT_JAR="binary/quarks/quarkus-qute-deployment-999-SNAPSHOT-jar-with-dependencies.jar"
TEST_SRC_DIR="result/generated-tests/"
TEST_CLASSES_DIR="build/test-classes"
JUNIT_JAR="libs/junit-4.12.jar"
HAMCREST_JAR="libs/hamcrest-core-1.3.jar"
JACOCO_AGENT="libs/org.jacoco.agent-run-0.8.14.jar"
JACOCO_CLI="libs/org.jacoco.cli-run-0.8.14.jar"
UTGEN="libs/utgen.jar"

# 1) compile
mkdir -p "${TEST_CLASSES_DIR}"
javac \
  -cp "${JUNIT_JAR}:${HAMCREST_JAR}:${UTGEN}:${SUT_JAR}" \
  -d "${TEST_CLASSES_DIR}" \
  $(find "${TEST_SRC_DIR}" -name "*.java")

# 2) run tests with JaCoCo
java \
  --add-opens java.base/java.lang=ALL-UNNAMED \
  --add-opens java.base/java.lang.reflect=ALL-UNNAMED \
  --add-opens java.base/java.util=ALL-UNNAMED \
  --add-opens java.base/java.net=ALL-UNNAMED \
  --add-opens java.desktop/java.awt=ALL-UNNAMED \
  -javaagent:${JACOCO_AGENT}=destfile=jacoco.exec,append=false \
  -cp "${SUT_JAR}:${TEST_CLASSES_DIR}:${UTGEN}:${JUNIT_JAR}:${HAMCREST_JAR}" \
  org.junit.runner.JUnitCore \
  io.quarkus.qute.deployment.QuteProcessor_1_ESTest

# 3) generate report
java -jar ${JACOCO_CLI} report jacoco.exec \
  --classfiles "${SUT_JAR}" \
  --html jacoco-report \
  --name "Coverage Report"

echo "Done. Open jacoco-report/index.html"