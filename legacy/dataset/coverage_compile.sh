SUT_JAR="tmp/quarkus_classes"
TEST_SRC_DIR="$1"
TEST_CLASSES_DIR="$2"
LIBS_DIR="libs/*"

javac \
  -cp "${LIBS_DIR}:${SUT_JAR}" \
  -d "${TEST_CLASSES_DIR}" \
  $(find "${TEST_SRC_DIR}" -name "*.java")