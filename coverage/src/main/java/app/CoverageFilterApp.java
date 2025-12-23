package app;

import jacoco.CoverageAnalyzer;
import model.CoverageSet;

import java.io.File;
import java.net.URISyntaxException;
import java.security.CodeSource;
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
        if (args.length < 7) {
            throw new IllegalArgumentException(
                    "Usage:\n" +
                            "  <classesDirRel> <workDirRel> <manualTestClass> <agtTestClass>\n" +
                            "  <jacocoAgentRel> <sutClassesRel> <libsRelOrGlob>\n\n" +
                            "Example:\n" +
                            "  tmp/quarkus_classes tmp/covfilter \\\n" +
                            "  io.quarkus.qute.deployment.QuteProcessorTest \\\n" +
                            "  io.quarkus.qute.deployment.QuteProcessor_1_ESTest \\\n" +
                            "  jacoco-deps/org.jacoco.agent-run-0.8.14.jar \\\n" +
                            "  tmp/quarkus_classes libs/*"
            );
        }

        File jarDir = resolveJarDir(CoverageFilterApp.class);

        File classesDir = new File(jarDir, args[0]).getCanonicalFile();
        File workDir    = new File(jarDir, args[1]).getCanonicalFile();

        String manualTestClass = args[2];
        String agtTestClass    = args[3];

        String jacocoAgentJar  = new File(jarDir, args[4]).getCanonicalPath();
        String sutClassesPath  = new File(jarDir, args[5]).getCanonicalPath();

        // dataset convention
        String testClassesPath = new File(jarDir, "build/test-classes").getCanonicalPath();
        String toolJarPath     = new File(jarDir, "coverage-filter-1.0-SNAPSHOT.jar").getCanonicalPath();

        File libsDir = new File(jarDir, args[6]).getCanonicalFile(); // args[6] should be "libs"
        ForkedJacocoRunner runner = new ForkedJacocoRunner(
                jacocoAgentJar,
                libsDir,
                sutClassesPath,
                testClassesPath,
                toolJarPath,
                "app.RunOne"
        );

        CoverageFilterApp app = new CoverageFilterApp(
                new CoverageAnalyzer(classesDir),
                runner
        );

        app.run(workDir, manualTestClass, agtTestClass);
    }

    /* =========================
     * Orchestration
     * ========================= */
    public void run(File workDir,
                    String manualTestClass,
                    String agtTestClass) throws Exception {

        workDir.mkdirs();

        File manualExec = new File(workDir, "manual.exec");
        File agtExec    = new File(workDir, "agt.exec");

        runner.runJUnit5Class(manualTestClass, manualExec, false);
        System.out.println("[CoverageFilterApp] wrote " +
                manualExec.getPath() + " size=" + manualExec.length());

        runner.runJUnit5Class(agtTestClass, agtExec, false);
        System.out.println("[CoverageFilterApp] wrote " +
                agtExec.getPath() + " size=" + agtExec.length());

        CoverageSet manualCoverage = coverageAnalyzer.analyze(manualExec);
        CoverageSet agtCoverage    = coverageAnalyzer.analyze(agtExec);

        System.out.println("Manual covered units: " +
                manualCoverage.getCoveredUnits().size());
        System.out.println("AGT covered units:    " +
                agtCoverage.getCoveredUnits().size());
        System.out.println("AGT-only coverage:    " +
                agtCoverage.subtract(manualCoverage).getCoveredUnits().size());
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