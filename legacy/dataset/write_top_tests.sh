java -cp "libs/*:coverage-filter-1.0-SNAPSHOT.jar" app.GenerateReducedAgtTestApp \
  result/generated-tests/io/quarkus/qute/deployment/QuteProcessor_1_ESTest.java \
  tmp/covfilter/test_deltas_all.csv \
  95 \
  tmp/covfilter/generated-tests \
  true