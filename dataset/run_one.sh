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

java \
  --add-opens java.base/java.lang=ALL-UNNAMED \
  --add-opens java.base/java.lang.reflect=ALL-UNNAMED \
  --add-opens java.base/java.util=ALL-UNNAMED \
  --add-opens java.base/java.net=ALL-UNNAMED \
  --add-opens java.desktop/java.awt=ALL-UNNAMED \
  "-javaagent:${JACOCO_AGENT}=destfile=${exec_file},append=true,includes=io.quarkus.*" \
  -cp "${LIBS_SRC}:${SUT_JAR}:build/test-classes:coverage-filter-1.0-SNAPSHOT.jar" \
  app.RunOne \
  "${agt_fqcn}"