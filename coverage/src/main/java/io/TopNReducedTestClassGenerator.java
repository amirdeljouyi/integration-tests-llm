package io;

import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.*;
import spoon.reflect.reference.CtTypeReference;
import jacoco.TestDelta;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public final class TopNReducedTestClassGenerator {

    public void generateReducedClass(File originalTestSource,
                                     List<TestDelta> rankedTests,
                                     int topN,
                                     File outputRootDir) {

        Launcher launcher = new Launcher();
        launcher.getEnvironment().setComplianceLevel(11);
        launcher.getEnvironment().setAutoImports(true);

        // 🔑 Tell Spoon where to write generated sources
        launcher.setSourceOutputDirectory(outputRootDir);

        launcher.addInputResource(originalTestSource.getAbsolutePath());
        launcher.buildModel();

        CtModel model = launcher.getModel();

        CtClass<?> originalClass = model.getAllTypes().stream()
                .filter(t -> t instanceof CtClass<?>)
                .map(t -> (CtClass<?>) t)
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException("No class found in " + originalTestSource));

        // Top-N method names
        Set<String> selectedMethodNames =
                rankedTests.stream()
                        .limit(topN)
                        .map(TestDelta::getTestSelector)
                        .map(s -> s.substring(s.indexOf('#') + 1))
                        .collect(Collectors.toCollection(LinkedHashSet::new));

        CtPackage pkg = originalClass.getPackage();
        String newClassName = originalClass.getSimpleName() + "_Top" + topN;

        CtClass<?> reduced = launcher.getFactory().Class().create(pkg, newClassName);

        // same modifiers
        reduced.setModifiers(originalClass.getModifiers());

        // same superclass
        CtTypeReference<?> superClass = originalClass.getSuperclass();
        if (superClass != null) {
            reduced.setSuperclass(superClass.clone());
        }

        // copy selected @Test methods
        int copied = 0;
        for (CtMethod<?> m : originalClass.getMethods()) {
            if (!selectedMethodNames.contains(m.getSimpleName())) continue;

            boolean isTest = m.getAnnotations().stream()
                    .anyMatch(a -> a.getAnnotationType().getSimpleName().equals("Test"));

            if (!isTest) continue;

            reduced.addMethod(m.clone());
            copied++;
        }

        if (copied == 0) {
            throw new IllegalStateException("No @Test methods copied — check CSV vs source.");
        }

        // 🔑 This writes the file correctly
        launcher.prettyprint();

        System.out.println("[Spoon] Generated reduced test class: "
                + pkg.getQualifiedName() + "." + newClassName
                + " (methods=" + copied + ")");
    }
}