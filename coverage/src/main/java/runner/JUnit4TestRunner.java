package runner;

import model.TestId;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class JUnit4TestRunner {

    private final ClassLoader classLoader;

    public JUnit4TestRunner(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public void runTests(List<TestId> tests) {
        Map<String, List<TestId>> byClass = tests.stream()
                .collect(Collectors.groupingBy(TestId::getClassName));

        JUnitCore core = new JUnitCore();
        int totalRun = 0;
        int totalFail = 0;

        for (Map.Entry<String, List<TestId>> e : byClass.entrySet()) {
            Class<?> testClass = loadClass(e.getKey());

            for (TestId t : e.getValue()) {
                Result r;
                if (t.isClassOnly()) {
                    r = core.run(testClass);
                } else {
                    r = core.run(Request.method(testClass, t.getMethodName()));
                }
                totalRun += r.getRunCount();
                totalFail += r.getFailureCount();
            }
        }

        System.out.println("[JUnit4TestRunner] run=" + totalRun + " failed=" + totalFail);
    }

    private Class<?> loadClass(String fqcn) {
        try {
            return Class.forName(fqcn, true, classLoader);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException("Cannot load test class: " + fqcn, ex);
        }
    }
}