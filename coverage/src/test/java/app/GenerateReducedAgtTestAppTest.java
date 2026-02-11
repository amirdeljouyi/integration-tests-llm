package app;

import io.TestDeltaCsvReader;
import io.TopNReducedTestClassGenerator;
import org.junit.Test;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GenerateReducedAgtTestAppTest {

    @Test
    public void sortsDeltasDescendingBeforeSelectingTop1() throws Exception {
        Path outputDir = Files.createTempDirectory("agt-reduced-out-");
        Path testFile = resourcePath("fixtures/quarkus-qute/QuteProcessor_ESTest_Adopted_Agentic.java");
        Path csvFile = resourcePath("fixtures/quarkus-qute/test_deltas_kept.csv");

        GenerateReducedAgtTestApp app = new GenerateReducedAgtTestApp(
                new TestDeltaCsvReader(),
                new TopNReducedTestClassGenerator()
        );

        app.run(new String[] {
                testFile.toString(),
                csvFile.toString(),
                "1",
                outputDir.toString(),
                "true"
        });

        Path generated = outputDir
                .resolve("io")
                .resolve("quarkus")
                .resolve("qute")
                .resolve("deployment")
                .resolve("QuteProcessor_ESTest_Adopted_Agentic_Top1.java");

        String generatedSource = Files.readString(generated, StandardCharsets.UTF_8);

        assertTrue(generatedSource.contains("void processLoopElementHintWithNullExpressionThrowsNPE()"));
    }

    @Test
    public void sortsDeltasDescendingBeforeSelectingTop5() throws Exception {
        Path outputDir = Files.createTempDirectory("agt-reduced-out-");
        Path testFile = resourcePath("fixtures/quarkus-qute/QuteProcessor_ESTest_Adopted_Agentic.java");
        Path csvFile = resourcePath("fixtures/quarkus-qute/test_deltas_kept.csv");

        GenerateReducedAgtTestApp app = new GenerateReducedAgtTestApp(
                new TestDeltaCsvReader(),
                new TopNReducedTestClassGenerator()
        );

        app.run(new String[] {
                testFile.toString(),
                csvFile.toString(),
                "5",
                outputDir.toString(),
                "true"
        });

        Path generated = outputDir
                .resolve("io")
                .resolve("quarkus")
                .resolve("qute")
                .resolve("deployment")
                .resolve("QuteProcessor_ESTest_Adopted_Agentic_Top5.java");

        String generatedSource = Files.readString(generated, StandardCharsets.UTF_8);

        assertTrue(generatedSource.contains("void processLoopElementHintWithNullExpressionThrowsNPE()"));
        assertTrue(generatedSource.contains("void getNameOnInjectionPointThrowsIAEForInvalidType()"));
        assertTrue(generatedSource.contains("void initializeWithValidParamsButNonProxyRecorderThrowsIAE()"));
        assertTrue(generatedSource.contains("void extractMatchTypeReturnsNullWhenNoMatch()"));
        assertTrue(generatedSource.contains("void validateExpressionsWithNullBeanDeploymentCollectionsThrowsNPE()"));
        assertEquals(5, countOccurrences(generatedSource, "@Test"));
    }

    private static Path resourcePath(String resource) throws Exception {
        URL url = GenerateReducedAgtTestAppTest.class.getClassLoader().getResource(resource);
        if (url == null) {
            throw new IllegalStateException("Missing test resource: " + resource);
        }
        return Path.of(url.toURI());
    }

    private static int countOccurrences(String text, String needle) {
        int count = 0;
        int idx = 0;
        while ((idx = text.indexOf(needle, idx)) != -1) {
            count++;
            idx += needle.length();
        }
        return count;
    }
}
