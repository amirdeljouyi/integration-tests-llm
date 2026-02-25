package io.quarkus.qute.deployment;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;

import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.ClassType;
import org.jboss.jandex.DotName;
import org.jboss.jandex.EmptyIndex;
import org.jboss.jandex.Index;
import org.jboss.jandex.IndexView;
import org.jboss.jandex.MethodInfo;
import org.jboss.jandex.ParameterizedType;
import org.jboss.jandex.Type;
import org.jboss.jandex.TypeVariable;
import org.jboss.jandex.WildcardType;
import org.junit.jupiter.api.Test;

import io.quarkus.qute.Engine;
import io.quarkus.qute.Expression;
import io.quarkus.qute.Template;
import io.quarkus.qute.deployment.TemplatesAnalysisBuildItem.TemplateAnalysis;

public class QuteProcessor_ESTest_Adopted {

    @Test
    public void testTemplateDataIgnorePattern() {
        List<String> names = List.of("foo", "bar");
        Pattern p = Pattern.compile(QuteProcessor.buildIgnorePattern(names));
        // Ignore "baz" and "getFoo"
        assertTrue(p.matcher("baz").matches());
        assertTrue(p.matcher("getFoo").matches());
        // Do not ignore "foo" and "bar"
        for (String name : names) {
            assertFalse(p.matcher(name).matches());
        }
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> QuteProcessor.buildIgnorePattern(List.of()));
    }

    @Test
    public void testCollectNamespaceExpressions() {
        Template template = Engine.builder().build().parse("{msg:hello} {msg2:hello_alpha} {foo:baz.get(foo:bar)}");
        TemplateAnalysis analysis = new TemplateAnalysis("foo", template, null);
        Set<Expression> msg = QuteProcessor.collectNamespaceExpressions(analysis, "msg");
        assertEquals(1, msg.size());
        assertEquals("msg:hello", msg.iterator().next().toOriginalString());

        Set<Expression> msg2 = QuteProcessor.collectNamespaceExpressions(analysis, "msg2");
        assertEquals(1, msg2.size());
        assertEquals("msg2:hello_alpha", msg2.iterator().next().toOriginalString());

        Set<Expression> foo = QuteProcessor.collectNamespaceExpressions(analysis, "foo");
        assertEquals(2, foo.size());
        for (Expression fooExpr : foo) {
            assertTrue(
                    fooExpr.toOriginalString().equals("foo:bar") || fooExpr.toOriginalString().equals("foo:baz.get(foo:bar)"));
        }
    }

    // Adapted IGT tests to match the style above

    @Test
    public void matchResultIsEmptyByDefault() {
        EmptyIndex emptyIndex = EmptyIndex.INSTANCE;
        Types.AssignabilityCheck assignabilityCheck = new Types.AssignabilityCheck(emptyIndex);
        QuteProcessor.MatchResult matchResult = new QuteProcessor.MatchResult(assignabilityCheck);
        assertTrue(matchResult.isEmpty());
    }

    @Test
    public void autoExtractTypeWithNullAssignabilityCheck() {
        QuteProcessor.MatchResult matchResult = new QuteProcessor.MatchResult((Types.AssignabilityCheck) null);
        // Should not throw
        matchResult.autoExtractType();
    }

    @Test
    public void collectNamespaceExpressionsWithNullAnalysisThrowsNPE() {
        assertThrows(NullPointerException.class, () -> QuteProcessor.collectNamespaceExpressions((TemplateAnalysis) null, null));
    }

    @Test
    public void staticsFilterReturnsFalseForNonStaticMethod() {
        ClassInfo objectClassInfo = Index.singleClass(Object.class);
        TypeVariable returnType = TypeVariable.create("rType");
        String[] paramNames = new String[] { "p" };
        Type[] params = new Type[0];
        TypeVariable[] typeParams = new TypeVariable[2];
        MethodInfo method = MethodInfo.create(objectClassInfo, "m", paramNames, params, returnType, (short) (-6681), typeParams, params);
        assertFalse(QuteProcessor.staticsFilter(method));
    }

    @Test
    public void readTemplateContentFromEmptyTempFileReturnsEmptyString() throws Exception {
        File tempFile = File.createTempFile("qute", "tmpl");
        String content = QuteProcessor.readTemplateContent(tempFile.toPath(), Charset.defaultCharset());
        assertEquals("", content);
    }

    @Test
    public void readTemplateContentFromNonexistentPathThrowsUncheckedIOException() {
        Path path = Path.of("nonexistent", "dir", "file.tmpl");
        assertThrows(UncheckedIOException.class, () -> QuteProcessor.readTemplateContent(path, Charset.defaultCharset()));
    }

    @Test
    public void findTemplatePathReturnsNullWhenNotFound() {
        LinkedList<TemplateAnalysis> analyses = new LinkedList<>();
        TemplatesAnalysisBuildItem item = new TemplatesAnalysisBuildItem(analyses);
        assertNull(QuteProcessor.findTemplatePath(item, "does_not_exist"));
    }

    @Test
    public void extractMatchTypeReturnsWildcard() {
        LinkedHashSet<Type> candidates = new LinkedHashSet<>();
        WildcardType wildcard = WildcardType.UNBOUNDED;
        candidates.add(wildcard);
        DotName name = wildcard.name();
        Type matched = QuteProcessor.extractMatchType(candidates, name, Function.identity());
        assertSame(wildcard, matched);
    }

    @Test
    public void extractMatchTypeReturnsNullForDifferentType() {
        LinkedHashSet<Type> candidates = new LinkedHashSet<>();
        candidates.add(ParameterizedType.create(DotName.STRING_NAME, new Type[0]));
        Type result = QuteProcessor.extractMatchType(candidates, DotName.FLOAT_CLASS_NAME, QuteProcessor.MAP_ENTRY_EXTRACT_FUN);
        assertNull(result);
    }

    @Test
    public void buildIgnorePatternProducesNonMatchingString() {
        Set<String> names = Set.of("one", "two");
        String pattern = QuteProcessor.buildIgnorePattern(names);
        assertFalse(names.contains(pattern));
    }

    @Test
    public void codeEnumGetNameReturnsExpected() {
        assertEquals("BUILD_INCORRECT_EXPRESSION", QuteProcessor.Code.INCORRECT_EXPRESSION.getName());
    }

    @Test
    public void defaultAndStaticsFiltersBehavior() {
        ClassInfo classInfo = Index.singleClass(Object.class);
        TypeVariable returnType = TypeVariable.create("ret");
        String[] paramNames = new String[] { "p" };
        Type[] params = new Type[0];
        TypeVariable[] tv = new TypeVariable[5];
        // Crafted flags per IGT to exercise filters
        MethodInfo nonPublic = MethodInfo.create(classInfo, "np", paramNames, params, returnType, (short) (-22428), tv, params);
        MethodInfo publicStatic = MethodInfo.create(classInfo, "ps", paramNames, params, returnType, (short) (-22438), tv, params);
        assertFalse(QuteProcessor.defaultFilter(nonPublic));
        assertTrue(QuteProcessor.staticsFilter(publicStatic));
    }

    @Test
    public void matchResultHelpersDefaultStates() {
        EmptyIndex emptyIndex = EmptyIndex.INSTANCE;
        Types.AssignabilityCheck check = new Types.AssignabilityCheck(emptyIndex);
        QuteProcessor.MatchResult result = new QuteProcessor.MatchResult(check);
        assertFalse(result.isClass());
        assertFalse(result.isArray());
        assertFalse(result.isParameterizedType());
        assertNull(result.type());
    }

    @Test
    public void matchResultParameterizedTypeArgumentsForClassTypeAreEmpty() {
        QuteProcessor.MatchResult result = new QuteProcessor.MatchResult(null);
        result.setValues(null, ClassType.ANNOTATION_TYPE);
        assertTrue(result.getParameterizedTypeArguments().isEmpty());
    }
}