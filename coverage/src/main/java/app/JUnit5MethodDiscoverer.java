package app;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestTemplate;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public final class JUnit5MethodDiscoverer {

    private final ClassLoader classLoader;

    public JUnit5MethodDiscoverer(ClassLoader classLoader) {
        this.classLoader = Objects.requireNonNull(classLoader, "classLoader");
    }

    public List<String> discoverTestMethods(String testClassFqcn) {
        Class<?> c = load(testClassFqcn);

        List<String> methods = new ArrayList<>();
        for (Method m : c.getDeclaredMethods()) {
            if (isJUnit5TestMethod(m)) {
                methods.add(m.getName());
            }
        }

        methods.sort(Comparator.naturalOrder());
        return methods;
    }

    private boolean isJUnit5TestMethod(Method m) {
        return m.isAnnotationPresent(Test.class)
                || m.isAnnotationPresent(RepeatedTest.class)
                || m.isAnnotationPresent(ParameterizedTest.class)
                || m.isAnnotationPresent(TestFactory.class)
                || m.isAnnotationPresent(TestTemplate.class);
    }

    private Class<?> load(String fqcn) {
        try {
            return Class.forName(fqcn, true, classLoader);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Cannot load test class: " + fqcn, e);
        }
    }
}