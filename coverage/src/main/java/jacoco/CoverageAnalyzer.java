package jacoco;

import model.CoverageSet;
import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.analysis.IClassCoverage;
import org.jacoco.core.analysis.IMethodCoverage;
import org.jacoco.core.tools.ExecFileLoader;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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