package app;

import model.TestId;
import runner.JUnit5TestRunner;

import java.util.ArrayList;
import java.util.List;

public final class RunMany {

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            throw new IllegalArgumentException("Usage: RunMany <testSelector1> <testSelector2> ... (selector = fqcn or fqcn#method)");
        }

        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        // Build TestId list
        List<TestId> tests = new ArrayList<>();
        for (String s : args) {
            TestId t = TestId.fromString(s); // supports fqcn or fqcn#method (your TestId already does)
            // fail fast if class not found
            Class.forName(t.getClassName(), true, cl);
            tests.add(t);
        }

        JUnit5TestRunner runner = new JUnit5TestRunner(cl);
        runner.runTests(tests);
    }
}