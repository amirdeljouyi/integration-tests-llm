package runner;

import model.TestId;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class TestRunner {

    private final ClassLoader classLoader;

    public TestRunner(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public void runTests(List<TestId> tests) {
        Map<String, List<TestId>> byClass = tests.stream()
                .collect(Collectors.groupingBy(TestId::getClassName));

        JUnitCore core = new JUnitCore();

        for (Map.Entry<String, List<TestId>> e : byClass.entrySet()) {
            Class<?> testClass = loadClass(e.getKey());
            for (TestId t : e.getValue()) {
                Request req = Request.method(testClass, t.getMethodName());
                core.run(req);
            }
        }
    }

    private Class<?> loadClass(String fqcn) {
        try {
            return Class.forName(fqcn, true, classLoader);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException("Cannot load test class: " + fqcn, ex);
        }
    }
}