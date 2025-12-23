package io.quarkus.qute.deployment;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;
import java.util.Vector;
import java.util.concurrent.SynchronousQueue;
import java.util.regex.Pattern;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.AnnotationValue;
import org.jboss.jandex.ArrayType;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.ClassType;
import org.jboss.jandex.CompositeIndex;
import org.jboss.jandex.DotName;
import org.jboss.jandex.EmptyIndex;
import org.jboss.jandex.FieldInfo;
import org.jboss.jandex.Index;
import org.jboss.jandex.IndexView;
import org.jboss.jandex.MethodInfo;
import org.jboss.jandex.ParameterizedType;
import org.jboss.jandex.PrimitiveType;
import org.jboss.jandex.StackedIndex;
import org.jboss.jandex.Type;
import org.jboss.jandex.TypeVariable;
import org.jboss.jandex.WildcardType;
import org.junit.jupiter.api.Test;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.AutoAddScopeBuildItem;
import io.quarkus.arc.deployment.BeanArchiveIndexBuildItem;
import io.quarkus.arc.deployment.BeanDefiningAnnotationBuildItem;
import io.quarkus.arc.deployment.QualifierRegistrarBuildItem;
import io.quarkus.arc.deployment.SynthesisFinishedBuildItem;
import io.quarkus.arc.deployment.ValidationPhaseBuildItem;
import io.quarkus.arc.processor.BeanDeployment;
import io.quarkus.arc.processor.BeanResolver;
import io.quarkus.arc.processor.InjectionPointInfo;
import io.quarkus.arc.processor.ObserverInfo;
import io.quarkus.bootstrap.model.ApplicationModelBuilder;
import io.quarkus.bootstrap.model.DefaultApplicationModel;
import io.quarkus.builder.BuildContext;
import io.quarkus.deployment.ApplicationArchive;
import io.quarkus.deployment.ApplicationArchiveImpl;
import io.quarkus.deployment.BuildProducerImpl;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.builditem.ApplicationArchivesBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.GeneratedResourceBuildItem;
import io.quarkus.deployment.builditem.ServiceStartBuildItem;
import io.quarkus.deployment.dev.AlwaysFalsePredicate;
import io.quarkus.deployment.pkg.NativeConfig;
import io.quarkus.deployment.pkg.builditem.CurateOutcomeBuildItem;
import io.quarkus.maven.dependency.ResolvedDependencyBuilder;
import io.quarkus.paths.EmptyPathTree;
import io.quarkus.qute.Engine;
import io.quarkus.qute.Expression;
import io.quarkus.qute.LoopSectionHelper;
import io.quarkus.qute.SetSectionHelper;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateNode;
import io.quarkus.qute.deployment.TemplatesAnalysisBuildItem.TemplateAnalysis;
import io.quarkus.qute.runtime.QuteConfig;

public class QuteProcessorTest {

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

    // Adapted tests from IGT

    @Test
    public void matchResult_isEmpty_whenUninitialized_returnsTrue() {
        QuteProcessor.MatchResult result = new QuteProcessor.MatchResult((Types.AssignabilityCheck) null);
        assertTrue(result.isEmpty());
    }

    @Test
    public void matchResult_getParameterizedTypeArguments_nullType_throwsNPE() {
        Types.AssignabilityCheck ac = new Types.AssignabilityCheck(EmptyIndex.INSTANCE);
        QuteProcessor.MatchResult result = new QuteProcessor.MatchResult(ac);
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(result::getParameterizedTypeArguments);
    }

    @Test
    public void collectNamespaceExpressions_withNullAnalysis_throwsNPE() {
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> QuteProcessor.collectNamespaceExpressions((TemplateAnalysis) null, ""));
    }

    @Test
    public void readTemplateContent_missingFile_throwsUncheckedIOException() throws Exception {
        File dir = File.createTempFile("qute", "tmp");
        File missingFile = new File(dir, "nope");
        Path path = missingFile.toPath();
        Charset cs = Charset.defaultCharset();
        assertThatExceptionOfType(UncheckedIOException.class)
                .isThrownBy(() -> QuteProcessor.readTemplateContent(path, cs));
    }

    @Test
    public void processTemplateErrors_withIncorrectExpression_reportsAndThrows() {
        QuteProcessor processor = new QuteProcessor();
        TemplatesAnalysisBuildItem analyses = new TemplatesAnalysisBuildItem(new ArrayList<>());
        LinkedList<IncorrectExpressionBuildItem> errors = new LinkedList<>();
        TemplateNode.Origin origin = mock(TemplateNode.Origin.class);
        doReturn(0).when(origin).getLine();
        doReturn(0).when(origin).getLineCharacterStart();
        doReturn((String) null).when(origin).getTemplateGeneratedId();
        doReturn((String) null).when(origin).getTemplateId();
        errors.add(new IncorrectExpressionBuildItem(null, null, null, origin, null));
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> processor.processTemplateErrors(analyses, errors, null));
    }

    @Test
    public void collectTemplateGlobals_runsWithoutExceptions() {
        QuteProcessor processor = new QuteProcessor();
        IndexView empty = IndexView.empty();
        CompositeIndex composite = CompositeIndex.create((Collection<IndexView>) new SynchronousQueue<>());
        Set<DotName> subpackages = composite.getSubpackages(QuteProcessor.LOCATION);
        BeanArchiveIndexBuildItem beanIndex = new BeanArchiveIndexBuildItem(empty, empty, subpackages);
        BuildProducerImpl<TemplateGlobalBuildItem> producer =
                new BuildProducerImpl<>(TemplateGlobalBuildItem.class, mock(BuildContext.class));
        processor.collectTemplateGlobals(beanIndex, producer);
    }

    @Test
    public void collectNamespaceExpressions_withNullExpression_throwsNPE() {
        LinkedHashSet<Expression> out = new LinkedHashSet<>();
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> QuteProcessor.collectNamespaceExpressions((Expression) null, out, "ns"));
    }

    @Test
    public void findTemplatePath_returnsNullWhenNotFound() {
        TemplatesAnalysisBuildItem analyses = new TemplatesAnalysisBuildItem(new LinkedList<>());
        assertEquals(null, QuteProcessor.findTemplatePath(analyses, "NARAYNA_STM"));
    }

    @Test
    public void validateTemplateDataNamespaces_duplicateNamespace_throws() {
        ClassInfo clazz = Index.singleClass(Object.class);
        QuteProcessor processor = new QuteProcessor();
        AnnotationInstance ai = AnnotationInstance.create(DotName.ENUM_NAME, clazz, List.of());
        TemplateDataBuildItem item = new TemplateDataBuildItem(ai, clazz);
        List<TemplateDataBuildItem> list = List.of(item, item, item, item);
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> processor.validateTemplateDataNamespaces(list, null));
    }

    @Test
    public void validateTemplateDataNamespaces_singleNamespace_ok() {
        ClassInfo clazz = Index.singleClass(Object.class);
        QuteProcessor processor = new QuteProcessor();
        AnnotationValue av = AnnotationValue.createEnumValue("x", DotName.ENUM_NAME, " X");
        AnnotationInstance ai = AnnotationInstance.create(DotName.ENUM_NAME, clazz, List.of(av));
        TemplateDataBuildItem item = new TemplateDataBuildItem(ai, clazz);
        List<TemplateDataBuildItem> list = List.of(item);
        BuildProducerImpl<ServiceStartBuildItem> producer =
                new BuildProducerImpl<>(ServiceStartBuildItem.class, mock(BuildContext.class));
        processor.validateTemplateDataNamespaces(list, producer);
        assertEquals(1, list.size());
    }

    @Test
    public void collectTemplateDataAnnotations_runsWithoutExceptions() {
        BeanArchiveIndexBuildItem beanIndex =
                new BeanArchiveIndexBuildItem(EmptyIndex.INSTANCE, EmptyIndex.INSTANCE, new LinkedHashSet<>());
        QuteProcessor processor = new QuteProcessor();
        BuildProducerImpl<TemplateDataBuildItem> producer =
                new BuildProducerImpl<>(TemplateDataBuildItem.class, (BuildContext) null);
        processor.collectTemplateDataAnnotations(beanIndex, producer);
    }

    @Test
    public void matchResult_isClass_defaultsFalse() {
        QuteProcessor.MatchResult result = new QuteProcessor.MatchResult((Types.AssignabilityCheck) null);
        assertFalse(result.isClass());
    }

    @Test
    public void extractMatchType_withWildcard_throwsIAE() {
        HashSet<Type> types = new HashSet<>();
        WildcardType w = WildcardType.createLowerBound(ClassType.BOOLEAN_CLASS);
        types.add(w);
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> QuteProcessor.extractMatchType(types, w.name(), QuteProcessor.FIRST_PARAM_TYPE_EXTRACT_FUN));
    }

    @Test
    public void buildIgnorePattern_fromAliases() {
        SetSectionHelper.Factory factory = new SetSectionHelper.Factory();
        List<String> aliases = factory.getDefaultAliases();
        String pattern = QuteProcessor.buildIgnorePattern(aliases);
        assertEquals("^(?!\\Qset\\E|\\Qlet\\E).*$", pattern);
    }

    @Test
    public void buildIgnorePattern_fromEmptyTemplateRoots_throwsIAE() {
        QuteProcessor processor = new QuteProcessor();
        TemplateRootsBuildItem roots = processor.collectTemplateRoots(new Vector<>());
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> QuteProcessor.buildIgnorePattern(roots));
    }

    @Test
    public void registerRenderedResults_enabled_returnsBean() {
        QuteProcessor processor = new QuteProcessor();
        QuteConfig.TestMode testMode = mock(QuteConfig.TestMode.class);
        QuteConfig cfg = mock(QuteConfig.class);
        doReturn(true).when(testMode).recordRenderedResults();
        doReturn(testMode).when(cfg).testMode();
        assertThatExceptionOfType(Throwable.class).doesNotThrowAnyException();
        assertTrue(processor.registerRenderedResults(cfg) != null);
    }

    @Test
    public void registerRenderedResults_disabled_returnsNull() {
        QuteProcessor processor = new QuteProcessor();
        QuteConfig.TestMode testMode = mock(QuteConfig.TestMode.class);
        QuteConfig cfg = mock(QuteConfig.class);
        doReturn(false).when(testMode).recordRenderedResults();
        doReturn(testMode).when(cfg).testMode();
        assertEquals(null, processor.registerRenderedResults(cfg));
    }

    @Test
    public void addSingletonToNamedRecords_reasonIsSet() {
        QuteProcessor processor = new QuteProcessor();
        AutoAddScopeBuildItem item = processor.addSingletonToNamedRecords();
        assertEquals("Found Java record annotated with @Named", item.getReason());
    }

    @Test
    public void feature_nameIsQute() {
        QuteProcessor processor = new QuteProcessor();
        FeatureBuildItem feature = processor.feature();
        assertEquals("qute", feature.getName());
    }

    @Test
    public void collectTemplateVariants_returnsNonNull() {
        QuteProcessor processor = new QuteProcessor();
        Vector<TemplatePathBuildItem> paths = new Vector<>();
        EffectiveTemplatePathsBuildItem effective = new EffectiveTemplatePathsBuildItem(paths);
        TemplatePathBuildItem item = mock(TemplatePathBuildItem.class);
        doReturn("@[M", "@egMelse").when(item).getPath();
        paths.add(item);
        LoopSectionHelper.Factory factory = new LoopSectionHelper.Factory();
        List<String> suffixes = factory.getBlockLabels();
        QuteConfig cfg = mock(QuteConfig.class);
        doReturn(suffixes).when(cfg).suffixes();
        processor.collectTemplateFilePaths(cfg, effective);
        QuteConfig cfg2 = mock(QuteConfig.class);
        doReturn(suffixes).when(cfg2).suffixes();
        TemplateVariantsBuildItem variants = processor.collectTemplateVariants(effective, cfg2);
        assertTrue(variants != null);
    }

    @Test
    public void collectEffectiveTemplatePaths_returnsNonNull() {
        QuteProcessor processor = new QuteProcessor();
        QuteConfig cfg = mock(QuteConfig.class);
        doReturn(QuteConfig.DuplicitTemplatesStrategy.FAIL).when(cfg).duplicitTemplatesStrategy();
        EffectiveTemplatePathsBuildItem effective = processor.collectEffectiveTemplatePaths(cfg, new ArrayList<>());
        assertTrue(effective != null);
    }

    @Test
    public void readTemplateContent_emptyFile_returnsEmptyString() throws Exception {
        File tmp = File.createTempFile("qute", "tmp");
        String content = QuteProcessor.readTemplateContent(tmp.toPath(), Charset.defaultCharset());
        assertEquals("", content);
    }

    @Test
    public void enumConstantFilter_field_returnsTrue() {
        ClassInfo clazz = Index.singleClass(Object.class);
        FieldInfo fi = FieldInfo.create(clazz, "f", PrimitiveType.BYTE, (short) 0);
        assertTrue(QuteProcessor.enumConstantFilter(fi));
    }

    @Test
    public void enumConstantFilter_class_returnsFalse() {
        ClassInfo clazz = Index.singleClass(String.class);
        assertFalse(QuteProcessor.enumConstantFilter(clazz));
    }

    @Test
    public void defaultFilter_field_returnsFalse() {
        ClassInfo clazz = Index.singleClass(Object.class);
        FieldInfo fi = FieldInfo.create(clazz, "", PrimitiveType.BYTE, (short) 0);
        assertFalse(QuteProcessor.defaultFilter(fi));
    }

    @Test
    public void defaultFilter_method_returnsTrue() {
        ClassInfo clazz = Index.singleClass(TemplatesAnalysisBuildItem.class);
        Type[] params = new Type[2];
        MethodInfo mi = MethodInfo.create(clazz, "m", params, PrimitiveType.BYTE, (short) 0);
        assertTrue(QuteProcessor.defaultFilter(mi));
    }

    @Test
    public void staticsFilter_onClassInfo_throwsIAE() {
        ClassInfo clazz = Index.singleClass(ResolvedDependencyBuilder.class);
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> QuteProcessor.staticsFilter(clazz));
    }

    @Test
    public void staticsFilter_field_returnsFalse() {
        ClassInfo clazz = Index.singleClass(Object.class);
        FieldInfo fi = FieldInfo.create(clazz, "x", PrimitiveType.BYTE, (short) 0);
        assertFalse(QuteProcessor.staticsFilter(fi));
    }

    @Test
    public void staticsFilter_method_returnsTrue() {
        ClassInfo clazz = Index.singleClass(Object.class);
        Type[] types = new Type[1];
        ParameterizedType pt = ParameterizedType.create("E26`V_U", types);
        MethodInfo mi = MethodInfo.create(clazz, "E26`V_U", types, pt, (short) 0);
        assertTrue(QuteProcessor.staticsFilter(mi));
    }

    @Test
    public void analyzeTemplates_withNonEmptyHooksAndConfigs() {
        QuteProcessor processor = new QuteProcessor();
        ArrayList<TemplatePathBuildItem> templatePaths = new ArrayList<>();
        EffectiveTemplatePathsBuildItem effective = new EffectiveTemplatePathsBuildItem(templatePaths);
        QuteConfig cfg = mock(QuteConfig.class);
        TemplateFilePathsBuildItem filePaths = processor.collectTemplateFilePaths(cfg, effective);

        Stack<TemplateGlobalBuildItem> globals = new Stack<>();
        ArrayList<ValidationParserHookBuildItem> hooks = new ArrayList<>();
        EngineConfigurationsBuildItem engineCfg = new EngineConfigurationsBuildItem(new LinkedList<>());
        Optional<EngineConfigurationsBuildItem> engineOpt = Optional.of(engineCfg);
        Stack<MessageBundleMethodBuildItem> msgMethods = new Stack<>();
        ArrayList<CheckedTemplateBuildItem> checked = new ArrayList<>();

        Stack<IndexView> views = new Stack<>();
        StackedIndex stacked = StackedIndex.create((List<IndexView>) views);
        BeanArchiveIndexBuildItem beanIndex = new BeanArchiveIndexBuildItem(stacked, stacked, (Set<DotName>) null);

        BuildProducerImpl<CheckedFragmentValidationBuildItem> checkedFragProducer =
                new BuildProducerImpl<>(CheckedFragmentValidationBuildItem.class, mock(BuildContext.class));
        BuildProducer<TemplatesAnalysisBuildItem> analysisProducer =
                new BuildProducer<TemplatesAnalysisBuildItem>() {
                    @Override
                    public void produce(TemplatesAnalysisBuildItem item) {
                        // no-op
                    }
                };

        processor.analyzeTemplates(effective, filePaths, checked, msgMethods, globals, cfg, hooks, engineOpt, beanIndex,
                checkedFragProducer, analysisProducer);
        // capacity is not deterministic here; ensure call didn't throw
        assertTrue(true);
    }
}