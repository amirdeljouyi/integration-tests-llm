package app;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public final class ForkedJacocoRunner {

    private final String jacocoAgentJar;
    private final File libsDir;
    private final String sutClassesPath;
    private final String testClassesPath;
    private final String toolJarPath;
    private final String runOneMainClass;

    public ForkedJacocoRunner(String jacocoAgentJar,
                              File libsDir,
                              String sutClassesPath,
                              String testClassesPath,
                              String toolJarPath,
                              String runOneMainClass) {
        this.jacocoAgentJar = Objects.requireNonNull(jacocoAgentJar);
        this.libsDir = Objects.requireNonNull(libsDir);
        this.sutClassesPath = Objects.requireNonNull(sutClassesPath);
        this.testClassesPath = Objects.requireNonNull(testClassesPath);
        this.toolJarPath = Objects.requireNonNull(toolJarPath);
        this.runOneMainClass = Objects.requireNonNull(runOneMainClass);
    }

    public void runJUnit5Class(String testClassFqcn, File execFile, boolean append) throws Exception {
        Objects.requireNonNull(testClassFqcn);
        Objects.requireNonNull(execFile);

        if (!append && execFile.exists()) execFile.delete();
        File parent = execFile.getParentFile();
        if (parent != null) parent.mkdirs();

        List<String> cmd = new ArrayList<>();
        cmd.add("java");

        cmd.add("--add-opens"); cmd.add("java.base/java.lang=ALL-UNNAMED");
        cmd.add("--add-opens"); cmd.add("java.base/java.lang.reflect=ALL-UNNAMED");
        cmd.add("--add-opens"); cmd.add("java.base/java.util=ALL-UNNAMED");
        cmd.add("--add-opens"); cmd.add("java.base/java.net=ALL-UNNAMED");
        cmd.add("--add-opens"); cmd.add("java.desktop/java.awt=ALL-UNNAMED");

        cmd.add("-Djava.util.logging.manager=org.jboss.logmanager.LogManager");

        cmd.add("-javaagent:" + jacocoAgentJar
                + "=destfile=" + execFile.getPath()
                + ",append=" + append
                + ",includes=io.quarkus.*");

        cmd.add("-cp");
        cmd.add(buildClasspath());

        cmd.add(runOneMainClass);
        cmd.add(testClassFqcn);

        Process p = new ProcessBuilder(cmd).inheritIO().start();
        int exit = p.waitFor();
        if (exit != 0) throw new RuntimeException("Fork failed (exit=" + exit + "): " + testClassFqcn);
    }

    private String buildClasspath() {
        String sep = System.getProperty("os.name", "").toLowerCase().contains("win") ? ";" : ":";

        List<String> entries = new ArrayList<>();
        entries.addAll(listJars(libsDir));
        entries.add(sutClassesPath);
        entries.add(testClassesPath);
        entries.add(toolJarPath);

        return String.join(sep, entries);
    }

    public void runSelectors(List<String> selectors, File execFile, boolean append) throws Exception {
        Objects.requireNonNull(selectors, "selectors");
        if (selectors.isEmpty()) throw new IllegalArgumentException("selectors is empty");
        Objects.requireNonNull(execFile, "execFile");

        if (!append && execFile.exists()) {
            //noinspection ResultOfMethodCallIgnored
            execFile.delete();
        }
        File parent = execFile.getParentFile();
        if (parent != null) parent.mkdirs();

        List<String> cmd = new ArrayList<>();
        cmd.add("java");

        cmd.add("--add-opens"); cmd.add("java.base/java.lang=ALL-UNNAMED");
        cmd.add("--add-opens"); cmd.add("java.base/java.lang.reflect=ALL-UNNAMED");
        cmd.add("--add-opens"); cmd.add("java.base/java.util=ALL-UNNAMED");
        cmd.add("--add-opens"); cmd.add("java.base/java.net=ALL-UNNAMED");
        cmd.add("--add-opens"); cmd.add("java.desktop/java.awt=ALL-UNNAMED");

        cmd.add("-Djava.util.logging.manager=org.jboss.logmanager.LogManager");

        cmd.add("-javaagent:" + jacocoAgentJar
                + "=destfile=" + execFile.getPath()
                + ",append=" + append
                + ",includes=io.quarkus.*");

        cmd.add("-cp");
        cmd.add(buildClasspath());

        // Use RunMany now
        cmd.add("app.RunMany");
        cmd.addAll(selectors);

        Process p = new ProcessBuilder(cmd).inheritIO().start();
        int exit = p.waitFor();
        if (exit != 0) {
            throw new RuntimeException("Fork failed (exit=" + exit + "): selectors=" + selectors);
        }
    }

    private List<String> listJars(File dir) {
        if (!dir.isDirectory()) {
            throw new IllegalArgumentException("libsDir is not a directory: " + dir.getPath());
        }

        File[] jars = dir.listFiles((d, name) -> name.endsWith(".jar"));
        if (jars == null) return List.of();

        Arrays.sort(jars, Comparator.comparing(File::getName));
        List<String> paths = new ArrayList<>(jars.length);
        for (File f : jars) paths.add(f.getPath());
        return paths;
    }
}