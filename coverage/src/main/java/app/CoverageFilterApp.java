package app;

import jacoco.CoverageAnalyzer;
import model.CoverageSet;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class CoverageFilterApp {

    private final CoverageAnalyzer coverageAnalyzer;
    private final ForkedJacocoRunner runner;

    public CoverageFilterApp(CoverageAnalyzer coverageAnalyzer,
                             ForkedJacocoRunner runner) {
        this.coverageAnalyzer = Objects.requireNonNull(coverageAnalyzer);
        this.runner = Objects.requireNonNull(runner);
    }

    /* =========================
     * Main entrypoint
     * ========================= */
    public static void main(String[] args) throws Exception {
        if (args.length < 8) {
            throw new IllegalArgumentException(
                    "Usage:\n" +
                            "  <mode: class|filter>\n" +
                            "  <classesDirRel> <workDirRel>\n" +
                            "  <manualTestClass> <agtTestClass>\n" +
                            "  <jacocoAgentRel> <sutClassesRel> <libsDir>"
            );
        }

        String mode = args[0];

        File jarDir = resolveJarDir(CoverageFilterApp.class);

        File classesDir = new File(jarDir, args[1]).getCanonicalFile();
        File workDir = new File(jarDir, args[2]).getCanonicalFile();

        String manualTestClass = args[3];
        String agtTestClass = args[4];

        String jacocoAgentJar = new File(jarDir, args[5]).getCanonicalPath();
        String sutClassesPath = new File(jarDir, args[6]).getCanonicalPath();
        File libsDir = new File(jarDir, args[7]).getCanonicalFile();

        String testClassesPath = new File(jarDir, "build/test-classes").getCanonicalPath();
        String toolJarPath = new File(jarDir, "coverage-filter-1.0-SNAPSHOT.jar").getCanonicalPath();

        ForkedJacocoRunner runner = new ForkedJacocoRunner(
                jacocoAgentJar,
                libsDir,
                sutClassesPath,
                testClassesPath,
                toolJarPath,
                "app.RunMany"
        );

        CoverageFilterApp app = new CoverageFilterApp(
                new CoverageAnalyzer(classesDir),
                runner
        );

        app.run(mode, workDir, manualTestClass, agtTestClass);
    }

    public void runClassLevel(File workDir,
                              String manualTestClass,
                              String agtTestClass) throws Exception {

        workDir.mkdirs();

        File manualExec = new File(workDir, "manual.exec");
        File agtExec = new File(workDir, "agt.exec");

        runner.runJUnit5Class(manualTestClass, manualExec, false);
        System.out.println("[CoverageFilterApp] wrote " + manualExec.getPath()
                + " size=" + manualExec.length());

        runner.runJUnit5Class(agtTestClass, agtExec, false);
        System.out.println("[CoverageFilterApp] wrote " + agtExec.getPath()
                + " size=" + agtExec.length());

        CoverageSet manualCoverage = coverageAnalyzer.analyze(manualExec);
        CoverageSet agtCoverage = coverageAnalyzer.analyze(agtExec);

        System.out.println("Manual covered units: " +
                manualCoverage.getCoveredUnits().size());
        System.out.println("AGT covered units:    " +
                agtCoverage.getCoveredUnits().size());
        System.out.println("AGT-only coverage:    " +
                agtCoverage.subtract(manualCoverage).getCoveredUnits().size());
    }

    public void runIncrementalFiltering(File workDir,
                                        String manualTestClass,
                                        String agtTestClass) throws Exception {

        workDir.mkdirs();

        io.CsvReportWriter csvWriter = new io.CsvReportWriter();

        /* =========================
         * 1) Baseline: manual only
         * ========================= */
        File baselineExec = new File(workDir, "baseline_manual.exec");
        runner.runSelectors(java.util.List.of(manualTestClass), baselineExec, false);

        model.CoverageSet baseline = coverageAnalyzer.analyze(baselineExec);

        /* =========================
         * 2) Discover AGT methods (forked)
         * ========================= */
        java.util.List<String> methods = runner.runAndCaptureLines(
                        "app.ListJUnit5Tests",
                        java.util.List.of(agtTestClass)
                ).stream()
                .filter(s -> s != null && !s.isBlank())
                .filter(s -> !s.startsWith("SLF4J("))
                .filter(s -> !s.startsWith("OpenJDK"))
                .filter(s -> !s.startsWith("Dec "))
                .filter(s -> !s.startsWith("["))
                .toList();

        System.out.println("[CoverageFilterApp] AGT methods discovered: " + methods.size());

        /* =========================
         * 3) Incremental filtering
         * ========================= */
        model.CoverageSet current = baseline;
        java.util.List<String> keptSelectors = new java.util.ArrayList<>();

        java.util.List<jacoco.TestDelta> allTestDeltas = new java.util.ArrayList<>();
        java.util.List<jacoco.TestDelta> keptTestDeltas = new java.util.ArrayList<>();

        java.util.List<model.LineDeltaRow> lineDeltaRows = new java.util.ArrayList<>();

        // Console spam control (CSV will still have full detail)
        final boolean PRINT_LINE_DELTAS_FOR_KEPT = false;

        for (int i = 0; i < methods.size(); i++) {
            String selector = agtTestClass + "#" + methods.get(i);
            File candExec = new File(workDir, "cand_" + i + ".exec");

            // Run manual + candidate method in one fork
            runner.runSelectors(java.util.List.of(manualTestClass, selector), candExec, false);

            // CoverageSet for keep/drop decision
            model.CoverageSet cand = coverageAnalyzer.analyze(candExec);

            // High-level totals for ranking (vs manual baseline)
            jacoco.TestDelta td = coverageAnalyzer.testDeltaTotals(baselineExec, candExec, selector);
            allTestDeltas.add(td);

            if (cand.addsAnythingBeyond(current)) {
                keptSelectors.add(selector);
                current = current.union(cand);

                keptTestDeltas.add(td);

                System.out.println("[KEEP] " + selector +
                        "  +lines=" + td.getAddedLines() +
                        " +methods=" + td.getAddedMethods() +
                        " +branches=" + td.getAddedBranches() +
                        " +instr=" + td.getAddedInstructions());

                // Per-test line attribution (store into CSV rows)
                java.util.Map<String, jacoco.CoverageAnalyzer.LineDelta> deltas =
                        coverageAnalyzer.newlyCoveredLines(baselineExec, candExec);

                for (java.util.Map.Entry<String, jacoco.CoverageAnalyzer.LineDelta> e : deltas.entrySet()) {
                    jacoco.CoverageAnalyzer.LineDelta d = e.getValue();
                    if (d == null || (d.newlyCovered.isEmpty() && d.upgradedToFull.isEmpty())) continue;

                    lineDeltaRows.add(new model.LineDeltaRow(
                            selector,
                            e.getKey(),
                            csvWriter.toRanges(d.newlyCovered),
                            csvWriter.toRanges(d.upgradedToFull)
                    ));
                }

                if (PRINT_LINE_DELTAS_FOR_KEPT) {
                    System.out.println("  (line deltas captured to CSV)");
                }

            } else {
                System.out.println("[DROP] " + selector +
                        "  +lines=" + td.getAddedLines() +
                        " +methods=" + td.getAddedMethods() +
                        " +branches=" + td.getAddedBranches() +
                        " +instr=" + td.getAddedInstructions());
            }
        }

        /* =========================
         * 4) Final aggregate run (manual + kept) + per-class ranking
         * ========================= */
        File finalExec = new File(workDir, "final_manual_plus_kept.exec");
        java.util.List<String> finalSelectors = new java.util.ArrayList<>();
        finalSelectors.add(manualTestClass);
        finalSelectors.addAll(keptSelectors);

        runner.runSelectors(finalSelectors, finalExec, false);

        java.util.List<jacoco.ClassDelta> classDeltas =
                coverageAnalyzer.perClassDelta(baselineExec, finalExec);

        /* =========================
         * 5) Sort rankings
         * ========================= */
        java.util.Comparator<jacoco.TestDelta> byImpact =
                java.util.Comparator
                        .comparingInt(jacoco.TestDelta::getAddedLines).reversed()
                        .thenComparingInt(jacoco.TestDelta::getAddedInstructions).reversed()
                        .thenComparingInt(jacoco.TestDelta::getAddedBranches).reversed()
                        .thenComparingInt(jacoco.TestDelta::getAddedMethods).reversed();

        allTestDeltas.sort(byImpact);
        keptTestDeltas.sort(byImpact);

        /* =========================
         * 6) Print brief summary
         * ========================= */
        System.out.println();
        System.out.println("Manual covered units: " + baseline.getCoveredUnits().size());
        System.out.println("Kept AGT methods:     " + keptSelectors.size());
        System.out.println("Final covered units:  " + current.getCoveredUnits().size());

        System.out.println("\nTop classes by added covered lines:");
        for (int i = 0; i < Math.min(15, classDeltas.size()); i++) {
            jacoco.ClassDelta d = classDeltas.get(i);
            System.out.printf(
                    "%2d) %s  +lines=%d +methods=%d +branches=%d +instr=%d%n",
                    i + 1,
                    d.getClassName(),
                    d.getAddedLines(),
                    d.getAddedMethods(),
                    d.getAddedBranches(),
                    d.getAddedInstructions()
            );
        }

        System.out.println("\nTop AGT test methods by added coverage (vs manual baseline) — ALL:");
        for (int i = 0; i < Math.min(10, allTestDeltas.size()); i++) {
            jacoco.TestDelta t = allTestDeltas.get(i);
            System.out.printf(
                    "%2d) %s  +lines=%d +methods=%d +branches=%d +instr=%d%n",
                    i + 1,
                    t.getTestSelector(),
                    t.getAddedLines(),
                    t.getAddedMethods(),
                    t.getAddedBranches(),
                    t.getAddedInstructions()
            );
        }

        System.out.println("\nTop AGT test methods by added coverage (vs manual baseline) — KEPT ONLY:");
        for (int i = 0; i < Math.min(10, keptTestDeltas.size()); i++) {
            jacoco.TestDelta t = keptTestDeltas.get(i);
            System.out.printf(
                    "%2d) %s  +lines=%d +methods=%d +branches=%d +instr=%d%n",
                    i + 1,
                    t.getTestSelector(),
                    t.getAddedLines(),
                    t.getAddedMethods(),
                    t.getAddedBranches(),
                    t.getAddedInstructions()
            );
        }

        /* =========================
         * 7) Write CSV reports
         * ========================= */
        csvWriter.writeKeptSelectors(new File(workDir, "kept_agt.csv"), keptSelectors);
        csvWriter.writeTestDeltas(new File(workDir, "test_deltas_all.csv"), allTestDeltas);
        csvWriter.writeTestDeltas(new File(workDir, "test_deltas_kept.csv"), keptTestDeltas);
        csvWriter.writeClassDeltas(new File(workDir, "class_deltas.csv"), classDeltas);
        csvWriter.writeLineDeltas(new File(workDir, "line_deltas_kept.csv"), lineDeltaRows);

        System.out.println("[CoverageFilterApp] CSVs written to: " + workDir.getPath());
    }

    /**
     * Prints line numbers in a compact way with a cap to avoid enormous logs.
     * Example: [12, 13, 14, ...] (truncated)
     */
    private void printLineList(java.util.List<Integer> lines, int max) {
        if (lines.size() <= max) {
            System.out.print(lines);
            return;
        }
        java.util.List<Integer> head = lines.subList(0, max);
        System.out.print(head);
        System.out.print("... (+" + (lines.size() - max) + ")");
    }

    private void writeLines(File file, List<String> lines) throws IOException {
        Files.createDirectories(file.toPath().getParent());
        Files.write(file.toPath(), lines, StandardCharsets.UTF_8);
    }

    /* =========================
     * Orchestration
     * ========================= */
    public void run(String mode, File workDir, String manualTestClass, String agtTestClass) throws Exception {

        if ("class".equalsIgnoreCase(mode)) {
            runClassLevel(workDir, manualTestClass, agtTestClass);
        } else if ("filter".equalsIgnoreCase(mode)) {
            runIncrementalFiltering(workDir, manualTestClass, agtTestClass);
        } else {
            throw new IllegalArgumentException("Unknown mode: " + mode);
        }
    }


    /* =========================
     * Jar directory resolver
     * ========================= */
    private static File resolveJarDir(Class<?> anchor) {
        try {
            CodeSource cs = anchor.getProtectionDomain().getCodeSource();
            if (cs == null || cs.getLocation() == null) {
                return new File(".").getAbsoluteFile();
            }

            File location = new File(cs.getLocation().toURI());
            return location.isFile()
                    ? Objects.requireNonNull(location.getParentFile())
                    : location;
        } catch (URISyntaxException e) {
            return new File(".").getAbsoluteFile();
        }
    }
}