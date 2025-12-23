package app;

import runner.JUnit5TestRunner;
import model.TestId;

import java.util.List;

public final class RunOne {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            throw new IllegalArgumentException("Usage: RunOne <testClassFqcn>");
        }

        // inside RunOne.main, before running
        Class.forName(args[0], true, Thread.currentThread().getContextClassLoader());
        System.out.println("[RunOne] Loaded test class: " + args[0]);

        
        JUnit5TestRunner r = new JUnit5TestRunner(Thread.currentThread().getContextClassLoader());
        r.runTests(List.of(new TestId(args[0], null))); // class-level
    }
}