package app;

import io.TestDeltaCsvReader;
import io.TopNReducedTestClassGenerator;
import jacoco.TestDelta;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public final class GenerateReducedAgtTestApp {

    private final TestDeltaCsvReader reader;
    private final TopNReducedTestClassGenerator generator;

    public GenerateReducedAgtTestApp(TestDeltaCsvReader reader,
                                     TopNReducedTestClassGenerator generator) {
        this.reader = Objects.requireNonNull(reader, "reader");
        this.generator = Objects.requireNonNull(generator, "generator");
    }

    /**
     * Args:
     *  0: path to original AGT test source .java (e.g., .../QuteProcessor_1_ESTest.java)
     *  1: path to test_deltas CSV (e.g., .../test_deltas_all.csv)
     *  2: N (e.g., 10)
     *  3: output dir for generated sources (e.g., .../generated-tests)
     *  4: sort (true|false)  -> if true, sorts by added_lines, then instr, branches, methods
     */
    public void run(String[] args) throws Exception {
        if (args.length < 4) {
            throw new IllegalArgumentException(
                    "Usage: <originalTestJava> <testDeltasCsv> <N> <outDir> [sort=true|false]\n" +
                            "Example: .../QuteProcessor_1_ESTest.java .../test_deltas_all.csv 10 tmp/generated-tests true"
            );
        }

        File originalTestJava = new File(args[0]);
        File testDeltasCsv = new File(args[1]);
        int n = Integer.parseInt(args[2]);
        File outDir = new File(args[3]);
        boolean sort = args.length >= 5 ? Boolean.parseBoolean(args[4]) : true;

        if (!originalTestJava.isFile()) {
            throw new IllegalArgumentException("originalTestJava not found: " + originalTestJava.getPath());
        }
        if (!testDeltasCsv.isFile()) {
            throw new IllegalArgumentException("testDeltasCsv not found: " + testDeltasCsv.getPath());
        }
        if (n <= 0) {
            throw new IllegalArgumentException("N must be > 0, got: " + n);
        }

        List<TestDelta> deltas = reader.read(testDeltasCsv);

        if (sort) {
            deltas.sort(Comparator
                    .comparingInt(TestDelta::getAddedLines).reversed()
                    .thenComparingInt(TestDelta::getAddedInstructions).reversed()
                    .thenComparingInt(TestDelta::getAddedBranches).reversed()
                    .thenComparingInt(TestDelta::getAddedMethods).reversed());
        }

        generator.generateReducedClass(originalTestJava, deltas, n, outDir);

        System.out.println("[GenerateReducedAgtTestApp] Done. Output dir: " + outDir.getPath());
    }

    public static void main(String[] args) throws Exception {
        new GenerateReducedAgtTestApp(
                new TestDeltaCsvReader(),
                new TopNReducedTestClassGenerator()
        ).run(args);
    }
}