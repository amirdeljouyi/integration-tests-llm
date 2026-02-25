package io.quarkus.qute.deployment;
import io.quarkus.qute.deployment.QuteProcessor_ESTest_scaffolding;
import io.quarkus.qute.Expression;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;
import org.junit.Test;
import static org.evosuite.runtime.EvoAssertions.verifyException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
public class QuteProcessor_ESTest_Top5 extends QuteProcessor_ESTest_scaffolding {
    @Test(timeout = 4000)
    public void testCollectNamespaceExpressionsTaking1And1ThrowsNullPointerExceptionAndCollectNamespaceExpressionsTaking1And10() throws Throwable {
        // Undeclared exception!
        try {
            QuteProcessor.collectNamespaceExpressions(((TemplatesAnalysisBuildItem.TemplateAnalysis) (null)), ((String) (null)));
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            // 
            // Cannot read field \"expressions\" because \"analysis\" is null
            // 
            verifyException("io.quarkus.qute.deployment.QuteProcessor", e);
        }
    }

    @Test(timeout = 4000)
    public void testCollectNamespaceExpressionsTaking3ArgumentsThrowsNullPointerException() throws Throwable {
        TreeSet<Expression> treeSet0 = new TreeSet<Expression>();
        // Undeclared exception!
        try {
            QuteProcessor.collectNamespaceExpressions(((Expression) (null)), treeSet0, "kf46");
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            // 
            // Cannot invoke \"io.quarkus.qute.Expression.isLiteral()\" because \"expression\" is null
            // 
            verifyException("io.quarkus.qute.deployment.QuteProcessor", e);
        }
    }

    @Test(timeout = 4000)
    public void testReadTemplateContent() throws Throwable {
        File file0 = File.createTempFile("k,rWgE)MEPis", "k,rWgE)MEPis", ((File) (null)));
        Path path0 = file0.toPath();
        Charset charset0 = Charset.defaultCharset();
        String string0 = QuteProcessor.readTemplateContent(path0, charset0);
        assertEquals("", string0);
    }

    @Test(timeout = 4000)
    public void testBuildIgnorePatternThrowsIllegalArgumentException() throws Throwable {
        ArrayList<String> arrayList0 = new ArrayList<String>();
        // Undeclared exception!
        try {
            QuteProcessor.buildIgnorePattern(arrayList0);
            fail("Expecting exception: IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // 
            // no message in exception (getMessage() returned null)
            // 
            verifyException("io.quarkus.qute.deployment.QuteProcessor", e);
        }
    }

    @Test(timeout = 4000)
    public void testBuildIgnorePattern() throws Throwable {
        Set<String> set0 = ZoneId.getAvailableZoneIds();
        String string0 = QuteProcessor.buildIgnorePattern(set0);
        assertFalse(set0.contains(string0));
    }
}
