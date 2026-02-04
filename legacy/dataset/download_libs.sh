set -euo pipefail

# ---- versions (keep aligned) ----
JUPITER=5.10.2
PLATFORM=1.10.2
JUNIT4=4.13.2
HAMCREST=1.3

MOCKITO=5.11.0
BYTEBUDDY=1.14.12
OBJENESIS=3.3

mkdir -p libs
cd libs

# ---- JUnit Jupiter ----
curl -LO https://repo1.maven.org/maven2/org/junit/jupiter/junit-jupiter-api/${JUPITER}/junit-jupiter-api-${JUPITER}.jar
curl -LO https://repo1.maven.org/maven2/org/junit/jupiter/junit-jupiter-engine/${JUPITER}/junit-jupiter-engine-${JUPITER}.jar
curl -LO https://repo1.maven.org/maven2/org/junit/jupiter/junit-jupiter-params/${JUPITER}/junit-jupiter-params-${JUPITER}.jar
curl -LO https://repo1.maven.org/maven2/org/junit/jupiter/junit-jupiter-migrationsupport/${JUPITER}/junit-jupiter-migrationsupport-${JUPITER}.jar

# ---- JUnit Platform ----
curl -LO https://repo1.maven.org/maven2/org/junit/platform/junit-platform-commons/${PLATFORM}/junit-platform-commons-${PLATFORM}.jar
curl -LO https://repo1.maven.org/maven2/org/junit/platform/junit-platform-engine/${PLATFORM}/junit-platform-engine-${PLATFORM}.jar
curl -LO https://repo1.maven.org/maven2/org/junit/platform/junit-platform-launcher/${PLATFORM}/junit-platform-launcher-${PLATFORM}.jar

# ---- Dependencies used by JUnit Jupiter ----
curl -LO https://repo1.maven.org/maven2/org/apiguardian/apiguardian-api/1.1.2/apiguardian-api-1.1.2.jar
curl -LO https://repo1.maven.org/maven2/org/opentest4j/opentest4j/1.3.0/opentest4j-1.3.0.jar

# ---- JUnit4 (for @Rule annotation in scaffolding) ----
curl -LO https://repo1.maven.org/maven2/junit/junit/${JUNIT4}/junit-${JUNIT4}.jar
curl -LO https://repo1.maven.org/maven2/org/hamcrest/hamcrest-core/${HAMCREST}/hamcrest-core-${HAMCREST}.jar

# ---- Mockito + deps ----
curl -LO https://repo1.maven.org/maven2/org/mockito/mockito-core/${MOCKITO}/mockito-core-${MOCKITO}.jar
curl -LO https://repo1.maven.org/maven2/net/bytebuddy/byte-buddy/${BYTEBUDDY}/byte-buddy-${BYTEBUDDY}.jar
curl -LO https://repo1.maven.org/maven2/net/bytebuddy/byte-buddy-agent/${BYTEBUDDY}/byte-buddy-agent-${BYTEBUDDY}.jar
curl -LO https://repo1.maven.org/maven2/org/objenesis/objenesis/${OBJENESIS}/objenesis-${OBJENESIS}.jar

cd ..
echo "Downloaded jars:"
ls -1 libs