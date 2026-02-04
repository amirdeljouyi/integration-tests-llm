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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
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

        long timeoutMs = TestTimeouts.resolveTimeoutMs();
        ExecutorService executor = TestTimeouts.newExecutor("junit5-runner");

        try {
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

                    String selector = t.isClassOnly()
                            ? t.getClassName()
                            : t.getClassName() + "#" + t.getMethodName();
                    TestExecutionSummary s = runWithTimeout(executor, timeoutMs, selector, () -> {
                        launcher.execute(request);
                        return summary.getSummary();
                    });

                    if (s != null) {
                        System.out.println("[JUnit5TestRunner] started=" + s.getTestsStartedCount()
                                + " succeeded=" + s.getTestsSucceededCount()
                                + " failed=" + s.getTestsFailedCount()
                                + " skipped=" + s.getTestsSkippedCount());
                    }
                }
            }
        } finally {
            executor.shutdownNow();
        }
    }

    private TestExecutionSummary runWithTimeout(ExecutorService executor,
                                                long timeoutMs,
                                                String selector,
                                                java.util.concurrent.Callable<TestExecutionSummary> task) {
        if (timeoutMs <= 0) {
            try {
                return task.call();
            } catch (Exception e) {
                throw new RuntimeException("Test failed: " + selector, e);
            }
        }
        Future<TestExecutionSummary> f = executor.submit(task);
        try {
            return f.get(timeoutMs, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            f.cancel(true);
            System.out.println("[JUnit5TestRunner] TIMEOUT after " + timeoutMs + "ms: " + selector);
            return null;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Test interrupted: " + selector, e);
        } catch (Exception e) {
            throw new RuntimeException("Test failed: " + selector, e);
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
