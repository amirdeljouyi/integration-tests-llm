package jacoco;

import model.CoverageSet;
import org.jacoco.core.analysis.*;
import org.jacoco.core.tools.ExecFileLoader;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Produces a CoverageSet of "covered units" at method granularity:
 *   pkg/Foo::bar|LINE
 *   pkg/Foo::bar|BRANCH
 */
public final class CoverageAnalyzer {

    private final File classesDir;

    public CoverageAnalyzer(File classesDir) {
        this.classesDir = Objects.requireNonNull(classesDir, "classesDir");
    }

    public static final class AnalysisResult {
        private final Map<String, IClassCoverage> perClass;
        private final CoverageSet coverageSet;

        private AnalysisResult(Map<String, IClassCoverage> perClass, CoverageSet coverageSet) {
            this.perClass = perClass;
            this.coverageSet = coverageSet;
        }

        public Map<String, IClassCoverage> getPerClass() {
            return perClass;
        }

        public CoverageSet getCoverageSet() {
            return coverageSet;
        }
    }

    public static final class LineDelta {
        public final List<Integer> newlyCovered = new ArrayList<>();
        public final List<Integer> upgradedToFull = new ArrayList<>();

        public boolean isEmpty() {
            return newlyCovered.isEmpty() && upgradedToFull.isEmpty();
        }
    }

    /**
     * Computes line-level coverage added by candidateExec compared to baselineExec.
     *
     * Key:
     *  - newlyCovered: NOT_COVERED -> PARTLY/FULLY
     *  - upgradedToFull: PARTLY -> FULLY
     */
    public Map<String, LineDelta> newlyCoveredLines(
            File baselineExec,
            File candidateExec) throws IOException {
        return newlyCoveredLines(analyzeExec(baselineExec), analyzeExec(candidateExec));
    }

    public Map<String, LineDelta> newlyCoveredLines(
            AnalysisResult baseline,
            AnalysisResult candidate) {

        Map<String, IClassCoverage> base = baseline.getPerClass();
        Map<String, IClassCoverage> cand = candidate.getPerClass();

        Map<String, LineDelta> result = new LinkedHashMap<>();

        for (Map.Entry<String, IClassCoverage> e : cand.entrySet()) {
            String fqcn = e.getKey();
            IClassCoverage c = e.getValue();
            IClassCoverage b = base.get(fqcn);

            int first = c.getFirstLine();
            int last  = c.getLastLine();
            if (first == -1 || last == -1) continue;

            LineDelta delta = new LineDelta();

            for (int line = first; line <= last; line++) {
                int candStatus = c.getLine(line).getStatus();
                int baseStatus = (b == null)
                        ? ICounter.NOT_COVERED
                        : b.getLine(line).getStatus();

                boolean candCovered = isCovered(candStatus);
                boolean baseCovered = isCovered(baseStatus);

                // NOT_COVERED -> PARTLY/FULLY
                if (candCovered && !baseCovered) {
                    delta.newlyCovered.add(line);
                }

                // PARTLY -> FULLY
                if (baseStatus == ICounter.PARTLY_COVERED
                        && candStatus == ICounter.FULLY_COVERED) {
                    delta.upgradedToFull.add(line);
                }
            }

            if (!delta.isEmpty()) {
                result.put(fqcn, delta);
            }
        }

        return result;
    }

    private boolean isCovered(int status) {
        return status == ICounter.PARTLY_COVERED
                || status == ICounter.FULLY_COVERED;
    }

    private boolean isFullyCovered(int status) {
        return status == ICounter.FULLY_COVERED;
    }

    public jacoco.TestDelta testDeltaTotals(File baselineExec, File candidateExec, String testSelector) throws IOException {
        return testDeltaTotals(analyzeExec(baselineExec), analyzeExec(candidateExec), testSelector);
    }

    public jacoco.TestDelta testDeltaTotals(AnalysisResult baseline, AnalysisResult candidate, String testSelector) {
        java.util.List<jacoco.ClassDelta> perClass = perClassDelta(baseline, candidate);

        int lines = 0, methods = 0, branches = 0, instr = 0;
        for (jacoco.ClassDelta d : perClass) {
            lines += d.getAddedLines();
            methods += d.getAddedMethods();
            branches += d.getAddedBranches();
            instr += d.getAddedInstructions();
        }
        return new jacoco.TestDelta(testSelector, lines, methods, branches, instr);
    }

    public List<ClassDelta> perClassDelta(File baselineExec, File candidateExec) throws IOException {
        return perClassDelta(analyzeExec(baselineExec), analyzeExec(candidateExec));
    }

    public List<ClassDelta> perClassDelta(AnalysisResult baseline, AnalysisResult candidate) {
        Map<String, IClassCoverage> base = baseline.getPerClass();
        Map<String, IClassCoverage> cand = candidate.getPerClass();

        List<ClassDelta> deltas = new ArrayList<>();
        for (Map.Entry<String, IClassCoverage> e : cand.entrySet()) {
            String cls = e.getKey();
            IClassCoverage c = e.getValue();
            IClassCoverage b = base.get(cls);

            int addedLines = diffCovered(c.getLineCounter(), b == null ? null : b.getLineCounter());
            int addedInstr = diffCovered(c.getInstructionCounter(), b == null ? null : b.getInstructionCounter());
            int addedBranches = diffCovered(c.getBranchCounter(), b == null ? null : b.getBranchCounter());
            int addedMethods = diffCovered(c.getMethodCounter(), b == null ? null : b.getMethodCounter());

            if (addedLines != 0 || addedInstr != 0 || addedBranches != 0 || addedMethods != 0) {
                deltas.add(new ClassDelta(cls, addedLines, addedInstr, addedBranches, addedMethods));
            }
        }

        // Sort by added lines desc (you can change to instructions, branches, etc.)
        deltas.sort(Comparator.comparingInt(ClassDelta::getAddedLines).reversed());
        return deltas;
    }

    private int diffCovered(ICounter cand, ICounter base) {
        int b = (base == null) ? 0 : base.getCoveredCount();
        return cand.getCoveredCount() - b;
    }

    public AnalysisResult analyzeExec(File execFile) throws IOException {
        Objects.requireNonNull(execFile, "execFile");
        ExecFileLoader loader = new ExecFileLoader();
        loader.load(execFile);
        return analyzeFromLoader(loader);
    }

    public AnalysisResult analyzeMergedExecs(File... execFiles) throws IOException {
        Objects.requireNonNull(execFiles, "execFiles");
        if (execFiles.length == 0) {
            throw new IllegalArgumentException("execFiles is empty");
        }
        ExecFileLoader loader = new ExecFileLoader();
        for (File execFile : execFiles) {
            Objects.requireNonNull(execFile, "execFile");
            loader.load(execFile);
        }
        return analyzeFromLoader(loader);
    }

    private AnalysisResult analyzeFromLoader(ExecFileLoader loader) throws IOException {
        CoverageBuilder builder = new CoverageBuilder();
        Analyzer analyzer = new Analyzer(loader.getExecutionDataStore(), builder);
        analyzer.analyzeAll(classesDir);

        Map<String, IClassCoverage> out = new HashMap<>();
        Set<String> units = new HashSet<>();
        for (IClassCoverage cc : builder.getClasses()) {
            out.put(cc.getName().replace('/', '.'), cc);

            String className = cc.getName(); // internal name: pkg/Foo
            for (IMethodCoverage mc : cc.getMethods()) {
                String methodName = mc.getName();

                if (mc.getLineCounter().getCoveredCount() > 0) {
                    units.add(className + "::" + methodName + "|LINE");
                }
                if (mc.getBranchCounter().getCoveredCount() > 0) {
                    units.add(className + "::" + methodName + "|BRANCH");
                }
            }
        }
        return new AnalysisResult(out, new CoverageSet(units));
    }

    public CoverageSet analyze(File execFile) throws IOException {
        return analyzeExec(execFile).getCoverageSet();
    }
}
