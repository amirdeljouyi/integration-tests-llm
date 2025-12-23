package runner;

import model.TestId;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class JUnit5TestRunner {

    private final ClassLoader classLoader;
    private final Launcher launcher;

    public JUnit5TestRunner(ClassLoader classLoader) {
        this.classLoader = classLoader;
        this.launcher = LauncherFactory.create();
    }

    public void runTests(List<TestId> tests) {
        Map<String, List<TestId>> byClass = tests.stream()
                .collect(Collectors.groupingBy(TestId::getClassName));

        for (Map.Entry<String, List<TestId>> e : byClass.entrySet()) {
            Class<?> testClass = loadClass(e.getKey());

            for (TestId t : e.getValue()) {
                LauncherDiscoveryRequest request;

                if (t.isClassOnly()) {
                    request = LauncherDiscoveryRequestBuilder.request()
                            .selectors(DiscoverySelectors.selectClass(testClass))
                            .build();
                } else {
                    request = LauncherDiscoveryRequestBuilder.request()
                            .selectors(DiscoverySelectors.selectMethod(testClass, t.getMethodName()))
                            .build();
                }

                SummaryGeneratingListener summary = new SummaryGeneratingListener();
                launcher.registerTestExecutionListeners(summary);

                launcher.execute(request);

                TestExecutionSummary s = summary.getSummary();
                System.out.println("[JUnit5TestRunner] started=" + s.getTestsStartedCount()
                        + " succeeded=" + s.getTestsSucceededCount()
                        + " failed=" + s.getTestsFailedCount()
                        + " skipped=" + s.getTestsSkippedCount());
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