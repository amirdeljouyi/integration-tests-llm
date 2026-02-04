package app;

import java.lang.reflect.Method;

public final class TestDetector {

    public enum JUnitVersion {
        JUNIT_4, JUNIT_5, UNKNOWN
    }

    public static JUnitVersion detect(Class<?> clazz) {
        boolean hasJUnit5 = false;
        boolean hasJUnit4 = false;

        for (Method m : clazz.getDeclaredMethods()) {
            if (isJUnit5TestMethod(m)) {
                hasJUnit5 = true;
            }
            if (isJUnit4TestMethod(m)) {
                hasJUnit4 = true;
            }
        }

        // If it has both, we might prefer one or the other. 
        // Usually, if it has JUnit 5 annotations, it should be run with JUnit 5.
        if (hasJUnit5) return JUnitVersion.JUNIT_5;
        if (hasJUnit4) return JUnitVersion.JUNIT_4;

        // Check class level annotations for JUnit 4 runners or JUnit 5 extensions if needed
        if (clazz.isAnnotationPresent(org.junit.runner.RunWith.class)) {
            return JUnitVersion.JUNIT_4;
        }

        return JUnitVersion.UNKNOWN;
    }

    private static boolean isJUnit5TestMethod(Method m) {
        try {
            return m.isAnnotationPresent(org.junit.jupiter.api.Test.class)
                    || m.isAnnotationPresent(org.junit.jupiter.api.RepeatedTest.class)
                    || m.isAnnotationPresent(org.junit.jupiter.params.ParameterizedTest.class)
                    || m.isAnnotationPresent(org.junit.jupiter.api.TestFactory.class)
                    || m.isAnnotationPresent(org.junit.jupiter.api.TestTemplate.class);
        } catch (NoClassDefFoundError e) {
            return false;
        }
    }

    private static boolean isJUnit4TestMethod(Method m) {
        try {
            return m.isAnnotationPresent(org.junit.Test.class);
        } catch (NoClassDefFoundError e) {
            return false;
        }
    }
}
