SUT_JAR="tmp/quarkus_classes"
JACOCO_AGENT="jacoco-deps/org.jacoco.agent-run-0.8.14.jar"
LIBS_SRC="libs/*"
#OUT_DIR="jacoco-out/agt"
OUT_DIR="tmp/covfilter"


project="quarkus"
agt_test="QuteProcessor_1_ESTest"
pkg="io.quarkus.qute.deployment"
agt_fqcn="${pkg}.${agt_test}"
#exec_file="${OUT_DIR}/${project}_${agt_test}.exec"
exec_file="${OUT_DIR}/agt.exec"

#java -cp "libs/*:coverage-filter-1.0-SNAPSHOT.jar:build/test-classes" app.CoverageFilterApp \
#  tmp/quarkus_classes \
#  tmp/covfilter \
#  io.quarkus.qute.deployment.QuteProcessorTest \
#  io.quarkus.qute.deployment.QuteProcessor_1_ESTest \
#  jacoco-deps/org.jacoco.agent-run-0.8.14.jar \
#  tmp/quarkus_classes \
#  libs

java -cp "libs/*:coverage-filter-1.0-SNAPSHOT.jar:build/test-classes" app.CoverageFilterApp \
  filter \
  tmp/quarkus_classes \
  tmp/covfilter \
  io.quarkus.qute.deployment.QuteProcessorTest \
  io.quarkus.qute.deployment.QuteProcessor_1_ESTest \
  jacoco-deps/org.jacoco.agent-run-0.8.14.jar \
  tmp/quarkus_classes \
  libs