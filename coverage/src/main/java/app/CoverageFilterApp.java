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

        /* =========================
         * 1) Baseline: manual only
         * ========================= */
        File baselineExec = new File(workDir, "baseline_manual.exec");
        runner.runSelectors(List.of(manualTestClass), baselineExec, false);

        CoverageSet baseline = coverageAnalyzer.analyze(baselineExec);

        /* =========================
         * 2) Discover AGT methods
         * ========================= */
        List<String> methods = runner.runAndCaptureLines(
                        "app.ListJUnit5Tests",
                        List.of(agtTestClass)
                ).stream()
                .filter(s -> s != null && !s.isBlank())
                .filter(s -> !s.startsWith("["))
                .toList();

        System.out.println("[CoverageFilterApp] AGT methods discovered: " + methods.size());

        /* =========================
         * 3) Incremental filtering
         * ========================= */
        CoverageSet current = baseline;
        List<String> keptSelectors = new ArrayList<>();

        for (int i = 0; i < methods.size(); i++) {
            String selector = agtTestClass + "#" + methods.get(i);
            File candExec = new File(workDir, "cand_" + i + ".exec");

            runner.runSelectors(
                    List.of(manualTestClass, selector),
                    candExec,
                    false
            );

            CoverageSet cand = coverageAnalyzer.analyze(candExec);

            if (cand.addsAnythingBeyond(current)) {
                keptSelectors.add(selector);
                current = current.union(cand);
                System.out.println("[KEEP] " + selector);
            } else {
                System.out.println("[DROP] " + selector);
            }
        }

        /* =========================================================
         * 4) FINAL AGGREGATE RUN  ←  PUT YOUR CODE HERE
         * ========================================================= */
        File finalExec = new File(workDir, "final_manual_plus_kept.exec");

        List<String> selectors = new ArrayList<>();
        selectors.add(manualTestClass);
        selectors.addAll(keptSelectors);

        runner.runSelectors(selectors, finalExec, false);

        List<jacoco.ClassDelta> deltas =
                coverageAnalyzer.perClassDelta(baselineExec, finalExec);

        System.out.println("\nTop classes by added covered lines:");
        for (int i = 0; i < Math.min(15, deltas.size()); i++) {
            jacoco.ClassDelta d = deltas.get(i);
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

        /* =========================
         * 5) Optional: persist kept list
         * ========================= */
        File out = new File(workDir, "kept_agt.txt");
        Files.write(out.toPath(), keptSelectors, StandardCharsets.UTF_8);
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