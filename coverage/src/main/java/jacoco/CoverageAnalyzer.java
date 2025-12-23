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

    public List<ClassDelta> perClassDelta(File baselineExec, File candidateExec) throws IOException {
        Map<String, IClassCoverage> base = analyzePerClass(baselineExec);
        Map<String, IClassCoverage> cand = analyzePerClass(candidateExec);

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

    private Map<String, IClassCoverage> analyzePerClass(File execFile) throws IOException {
        ExecFileLoader loader = new ExecFileLoader();
        loader.load(execFile);

        CoverageBuilder builder = new CoverageBuilder();
        Analyzer analyzer = new Analyzer(loader.getExecutionDataStore(), builder);
        analyzer.analyzeAll(classesDir);

        Map<String, IClassCoverage> out = new HashMap<>();
        for (IClassCoverage cc : builder.getClasses()) {
            out.put(cc.getName().replace('/', '.'), cc); // normalize to FQCN
        }
        return out;
    }

    public CoverageSet analyze(File execFile) throws IOException {
        Objects.requireNonNull(execFile, "execFile");

        ExecFileLoader loader = new ExecFileLoader();
        loader.load(execFile);

        CoverageBuilder builder = new CoverageBuilder();
        Analyzer analyzer = new Analyzer(loader.getExecutionDataStore(), builder);
        analyzer.analyzeAll(classesDir);

        Set<String> units = new HashSet<>();

        for (IClassCoverage cc : builder.getClasses()) {
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

        return new CoverageSet(units);
    }
}