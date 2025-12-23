rm -f jacoco.exec

SUT_JAR="tmp/quarkus_classes"
JACOCO_AGENT="jacoco-deps/org.jacoco.agent-run-0.8.14.jar"
LIBS_SRC="libs/*"

java \
  --add-opens java.base/java.lang=ALL-UNNAMED \
  --add-opens java.base/java.lang.reflect=ALL-UNNAMED \
  --add-opens java.base/java.util=ALL-UNNAMED \
  --add-opens java.base/java.net=ALL-UNNAMED \
  --add-opens java.desktop/java.awt=ALL-UNNAMED \
  -Dcustomrunner.fullStackTraces=true \
  -javaagent:${JACOCO_AGENT}=destfile=jacoco.exec,append=false,includes=io.quarkus.* \
  -cp "${SUT_JAR}:build/test-classes:${LIBS_SRC}:." \
  CustomRunner \
  io.quarkus.qute.deployment.QuteProcessor_1_ESTest