SUT_JAR="tmp/quarkus_classes"
TEST_SRC_DIR="result/generated-tests/"
TEST_CLASSES_DIR="build/test-classes"
LIBS_DIR="libs/*"

javac \
  -cp "${LIBS_DIR}:${SUT_JAR}" \
  -d "${TEST_CLASSES_DIR}" \
  $(find "${TEST_SRC_DIR}" -name "*.java")