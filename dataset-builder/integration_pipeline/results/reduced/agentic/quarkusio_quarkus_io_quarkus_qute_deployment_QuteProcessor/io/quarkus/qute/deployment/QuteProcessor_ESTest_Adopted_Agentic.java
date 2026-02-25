package io.quarkus.qute.deployment;
import io.quarkus.arc.deployment.AutoAddScopeBuildItem;
import io.quarkus.arc.deployment.BeanArchiveIndexBuildItem;
import io.quarkus.arc.deployment.SynthesisFinishedBuildItem;
import io.quarkus.arc.deployment.SyntheticBeanBuildItem;
import io.quarkus.arc.processor.BeanDeployment;
import io.quarkus.arc.processor.BeanResolver;
import io.quarkus.arc.processor.InjectionPointInfo;
import io.quarkus.builder.BuildContext;
import io.quarkus.deployment.BuildProducerImpl;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.builditem.ServiceStartBuildItem;
import io.quarkus.deployment.dev.AlwaysFalsePredicate;
import io.quarkus.deployment.pkg.NativeConfig;
import io.quarkus.qute.Engine;
import io.quarkus.qute.Expression;
import io.quarkus.qute.Template;
import io.quarkus.qute.deployment.TemplatesAnalysisBuildItem.TemplateAnalysis;
import io.quarkus.qute.runtime.QuteConfig;
import io.quarkus.qute.runtime.QuteRecorder;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.ClassType;
import org.jboss.jandex.DotName;
import org.jboss.jandex.EmptyIndex;
import org.jboss.jandex.Index;
import org.jboss.jandex.IndexView;
import org.jboss.jandex.MethodInfo;
import org.jboss.jandex.PrimitiveType;
import org.jboss.jandex.StackedIndex;
import org.jboss.jandex.Type;
import org.jboss.jandex.TypeVariable;
import org.jboss.jandex.WildcardType;
public class QuteProcessor_ESTest_Adopted_Agentic {
    @org.junit.jupiter.api.Test
    public void testTemplateDataIgnorePattern() {
        java.util.List<java.lang.String> names = java.util.List.of("foo", "bar");
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(io.quarkus.qute.deployment.QuteProcessor.buildIgnorePattern(names));
        // Ignore "baz" and "getFoo"
        org.junit.jupiter.api.Assertions.assertTrue(p.matcher("baz").matches());
        org.junit.jupiter.api.Assertions.assertTrue(p.matcher("getFoo").matches());
        // Do not ignore "foo" and "bar"
        for (java.lang.String name : names) {
            org.junit.jupiter.api.Assertions.assertFalse(p.matcher(name).matches());
        }
        org.assertj.core.api.Assertions.assertThatExceptionOfType(java.lang.IllegalArgumentException.class).isThrownBy(() -> io.quarkus.qute.deployment.QuteProcessor.buildIgnorePattern(java.util.List.of()));
    }

    @org.junit.jupiter.api.Test
    public void testCollectNamespaceExpressions() {
        io.quarkus.qute.Template template = io.quarkus.qute.Engine.builder().build().parse("{msg:hello} {msg2:hello_alpha} {foo:baz.get(foo:bar)}");
        io.quarkus.qute.deployment.TemplatesAnalysisBuildItem.TemplateAnalysis analysis = new io.quarkus.qute.deployment.TemplatesAnalysisBuildItem.TemplateAnalysis("foo", template, null);
        java.util.Set<io.quarkus.qute.Expression> msg = io.quarkus.qute.deployment.QuteProcessor.collectNamespaceExpressions(analysis, "msg");
        org.junit.jupiter.api.Assertions.assertEquals(1, msg.size());
        org.junit.jupiter.api.Assertions.assertEquals("msg:hello", msg.iterator().next().toOriginalString());
        java.util.Set<io.quarkus.qute.Expression> msg2 = io.quarkus.qute.deployment.QuteProcessor.collectNamespaceExpressions(analysis, "msg2");
        org.junit.jupiter.api.Assertions.assertEquals(1, msg2.size());
        org.junit.jupiter.api.Assertions.assertEquals("msg2:hello_alpha", msg2.iterator().next().toOriginalString());
        java.util.Set<io.quarkus.qute.Expression> foo = io.quarkus.qute.deployment.QuteProcessor.collectNamespaceExpressions(analysis, "foo");
        org.junit.jupiter.api.Assertions.assertEquals(2, foo.size());
        for (io.quarkus.qute.Expression fooExpr : foo) {
            org.junit.jupiter.api.Assertions.assertTrue(fooExpr.toOriginalString().equals("foo:bar") || fooExpr.toOriginalString().equals("foo:baz.get(foo:bar)"));
        }
    }

    @org.junit.jupiter.api.Test
    public void isEmptyReturnsTrueForNewMatchResult() {
        org.jboss.jandex.EmptyIndex emptyIndex = org.jboss.jandex.EmptyIndex.INSTANCE;
        io.quarkus.qute.deployment.Types.AssignabilityCheck assignabilityCheck = new io.quarkus.qute.deployment.Types.AssignabilityCheck(emptyIndex);
        io.quarkus.qute.deployment.QuteProcessor.MatchResult matchResult = new io.quarkus.qute.deployment.QuteProcessor.MatchResult(assignabilityCheck);
        org.junit.jupiter.api.Assertions.assertTrue(matchResult.isEmpty());
    }

    @org.junit.jupiter.api.Test
    public void autoExtractTypeDoesNotThrowOnNullAssignabilityCheck() {
        io.quarkus.qute.deployment.QuteProcessor.MatchResult matchResult = new io.quarkus.qute.deployment.QuteProcessor.MatchResult(((io.quarkus.qute.deployment.Types.AssignabilityCheck) (null)));
        matchResult.autoExtractType();
    }

    @org.junit.jupiter.api.Test
    public void collectNamespaceExpressionsWithNullAnalysisThrowsNPE() {
        java.lang.NullPointerException exception = org.junit.jupiter.api.Assertions.assertThrows(java.lang.NullPointerException.class, () -> io.quarkus.qute.deployment.QuteProcessor.collectNamespaceExpressions(((io.quarkus.qute.deployment.TemplatesAnalysisBuildItem.TemplateAnalysis) (null)), ((java.lang.String) (null))));
        boolean found = false;
        for (java.lang.StackTraceElement element : exception.getStackTrace()) {
            if (element.getClassName().equals("io.quarkus.qute.deployment.QuteProcessor")) {
                found = true;
                break;
            }
        }
        org.junit.jupiter.api.Assertions.assertTrue(found);
    }

    @org.junit.jupiter.api.Test
    public void validateExpressionsWithNullBeanDeploymentCollectionsThrowsNPE() {
        io.quarkus.qute.deployment.QuteProcessor processor = new io.quarkus.qute.deployment.QuteProcessor();
        io.quarkus.deployment.dev.AlwaysFalsePredicate<io.quarkus.qute.deployment.TypeCheckExcludeBuildItem.TypeCheck> alwaysFalse = new io.quarkus.deployment.dev.AlwaysFalsePredicate<>();
        io.quarkus.qute.deployment.TypeCheckExcludeBuildItem excludeItem = new io.quarkus.qute.deployment.TypeCheckExcludeBuildItem(alwaysFalse, true);
        java.util.List<io.quarkus.qute.deployment.TypeCheckExcludeBuildItem> excludes = java.util.List.of(excludeItem, excludeItem, excludeItem, excludeItem, excludeItem);
        java.lang.Class<io.quarkus.qute.deployment.IncorrectExpressionBuildItem> incorrectClass = io.quarkus.qute.deployment.IncorrectExpressionBuildItem.class;
        io.quarkus.builder.BuildContext buildContext = org.mockito.Mockito.mock(io.quarkus.builder.BuildContext.class);
        io.quarkus.deployment.BuildProducerImpl<io.quarkus.qute.deployment.IncorrectExpressionBuildItem> incorrectProducer = new io.quarkus.deployment.BuildProducerImpl<>(incorrectClass, buildContext);
        io.quarkus.arc.processor.BeanDeployment beanDeployment = org.mockito.Mockito.mock(io.quarkus.arc.processor.BeanDeployment.class);
        org.mockito.Mockito.doReturn(((io.quarkus.arc.processor.BeanResolver) (null))).when(beanDeployment).getBeanResolver();
        org.mockito.Mockito.doReturn(((java.util.Collection<?>) (null))).when(beanDeployment).getBeans();
        org.mockito.Mockito.doReturn(((java.util.Collection<?>) (null))).when(beanDeployment).getInjectionPoints();
        org.mockito.Mockito.doReturn(((java.util.Collection<?>) (null))).when(beanDeployment).getObservers();
        io.quarkus.arc.deployment.SynthesisFinishedBuildItem synthesisFinished = new io.quarkus.arc.deployment.SynthesisFinishedBuildItem(beanDeployment);
        java.util.ArrayList<io.quarkus.qute.deployment.CheckedTemplateBuildItem> checkedTemplates = new java.util.ArrayList<>();
        java.util.Stack<io.quarkus.qute.deployment.TemplateDataBuildItem> templateDataItems = new java.util.Stack<>();
        io.quarkus.qute.deployment.TemplatesAnalysisBuildItem analyses = new io.quarkus.qute.deployment.TemplatesAnalysisBuildItem(((java.util.List<io.quarkus.qute.deployment.TemplatesAnalysisBuildItem.TemplateAnalysis>) (null)));
        java.util.LinkedHashSet<org.jboss.jandex.DotName> beanExclusions = new java.util.LinkedHashSet<>();
        io.quarkus.arc.deployment.BeanArchiveIndexBuildItem beanArchiveIndex = new io.quarkus.arc.deployment.BeanArchiveIndexBuildItem(((org.jboss.jandex.IndexView) (null)), ((org.jboss.jandex.IndexView) (null)), beanExclusions);
        java.util.ArrayList<io.quarkus.qute.deployment.TemplateExtensionMethodBuildItem> extensionMethods = new java.util.ArrayList<>();
        java.lang.Class<io.quarkus.qute.deployment.ImplicitValueResolverBuildItem> implicitClass = io.quarkus.qute.deployment.ImplicitValueResolverBuildItem.class;
        io.quarkus.builder.BuildContext buildContext1 = org.mockito.Mockito.mock(io.quarkus.builder.BuildContext.class);
        io.quarkus.deployment.BuildProducerImpl<io.quarkus.qute.deployment.ImplicitValueResolverBuildItem> implicitProducer = new io.quarkus.deployment.BuildProducerImpl<>(implicitClass, buildContext1);
        io.quarkus.deployment.pkg.NativeConfig nativeConfig = org.mockito.Mockito.mock(io.quarkus.deployment.pkg.NativeConfig.class);
        java.lang.NullPointerException exception = org.junit.jupiter.api.Assertions.assertThrows(java.lang.NullPointerException.class, () -> processor.validateExpressions(analyses, beanArchiveIndex, extensionMethods, excludes, incorrectProducer, implicitProducer, ((io.quarkus.deployment.annotations.BuildProducer<io.quarkus.qute.deployment.TemplateExpressionMatchesBuildItem>) (null)), synthesisFinished, checkedTemplates, templateDataItems, ((io.quarkus.qute.runtime.QuteConfig) (null)), nativeConfig, ((java.util.List<io.quarkus.qute.deployment.TemplateGlobalBuildItem>) (null))));
        boolean found = false;
        for (java.lang.StackTraceElement element : exception.getStackTrace()) {
            if (element.getClassName().equals("java.util.Objects")) {
                found = true;
                break;
            }
        }
        org.junit.jupiter.api.Assertions.assertTrue(found);
    }

    @org.junit.jupiter.api.Test
    public void initializeWithValidParamsButNonProxyRecorderThrowsIAE() {
        io.quarkus.qute.deployment.QuteProcessor processor = new io.quarkus.qute.deployment.QuteProcessor();
        java.lang.Class<io.quarkus.arc.deployment.SyntheticBeanBuildItem> syntheticClass = io.quarkus.arc.deployment.SyntheticBeanBuildItem.class;
        io.quarkus.builder.BuildContext buildContext = org.mockito.Mockito.mock(io.quarkus.builder.BuildContext.class);
        io.quarkus.deployment.BuildProducerImpl<io.quarkus.arc.deployment.SyntheticBeanBuildItem> syntheticProducer = new io.quarkus.deployment.BuildProducerImpl<>(syntheticClass, buildContext);
        io.quarkus.qute.runtime.QuteRecorder recorder = new io.quarkus.qute.runtime.QuteRecorder();
        java.util.Stack<io.quarkus.qute.deployment.TemplatePathBuildItem> templatePaths = new java.util.Stack<>();
        io.quarkus.qute.deployment.EffectiveTemplatePathsBuildItem effectivePaths = new io.quarkus.qute.deployment.EffectiveTemplatePathsBuildItem(templatePaths);
        java.util.HashMap<java.lang.String, java.util.List<java.lang.String>> variants = new java.util.HashMap<>();
        io.quarkus.qute.deployment.TemplateVariantsBuildItem templateVariants = new io.quarkus.qute.deployment.TemplateVariantsBuildItem(variants);
        java.util.Optional<io.quarkus.qute.deployment.TemplateVariantsBuildItem> optionalVariants = java.util.Optional.of(templateVariants);
        java.util.LinkedList<io.quarkus.qute.deployment.TemplateRootBuildItem> roots = new java.util.LinkedList<>();
        io.quarkus.qute.deployment.TemplateRootsBuildItem templateRoots = processor.collectTemplateRoots(roots);
        java.util.Stack<io.quarkus.qute.deployment.TemplatePathExcludeBuildItem> excludes = new java.util.Stack<>();
        io.quarkus.qute.deployment.TemplatePathExcludeBuildItem exclude = new io.quarkus.qute.deployment.TemplatePathExcludeBuildItem(((java.lang.String) (null)));
        excludes.add(exclude);
        java.lang.IllegalArgumentException exception = org.junit.jupiter.api.Assertions.assertThrows(java.lang.IllegalArgumentException.class, () -> processor.initialize(syntheticProducer, recorder, effectivePaths, optionalVariants, templateRoots, excludes));
        boolean found = false;
        for (java.lang.StackTraceElement element : exception.getStackTrace()) {
            if (element.getClassName().equals("io.quarkus.arc.deployment.SyntheticBeanBuildItem$ExtendedBeanConfigurator")) {
                found = true;
                break;
            }
        }
        org.junit.jupiter.api.Assertions.assertTrue(found);
    }

    @org.junit.jupiter.api.Test
    public void collectTemplateGlobalsDoesNotFailOnEmptyIndex() {
        io.quarkus.qute.deployment.QuteProcessor processor = new io.quarkus.qute.deployment.QuteProcessor();
        java.util.ArrayList<org.jboss.jandex.IndexView> views = new java.util.ArrayList<>();
        org.jboss.jandex.StackedIndex stackedIndex = org.jboss.jandex.StackedIndex.create(views);
        java.util.TreeSet<org.jboss.jandex.DotName> subpackages = new java.util.TreeSet<>();
        io.quarkus.arc.deployment.BeanArchiveIndexBuildItem beanArchiveIndex = new io.quarkus.arc.deployment.BeanArchiveIndexBuildItem(stackedIndex, stackedIndex, subpackages);
        java.lang.Class<io.quarkus.qute.deployment.TemplateGlobalBuildItem> globalClass = io.quarkus.qute.deployment.TemplateGlobalBuildItem.class;
        io.quarkus.builder.BuildContext buildContext = org.mockito.Mockito.mock(io.quarkus.builder.BuildContext.class);
        io.quarkus.deployment.BuildProducerImpl<io.quarkus.qute.deployment.TemplateGlobalBuildItem> globalProducer = new io.quarkus.deployment.BuildProducerImpl<>(globalClass, buildContext);
        processor.collectTemplateGlobals(beanArchiveIndex, globalProducer);
    }

    @org.junit.jupiter.api.Test
    public void collectNamespaceExpressionsOnExpressionNullThrowsNPE() {
        java.util.TreeSet<io.quarkus.qute.Expression> foundExpressions = new java.util.TreeSet<>();
        java.lang.NullPointerException exception = org.junit.jupiter.api.Assertions.assertThrows(java.lang.NullPointerException.class, () -> io.quarkus.qute.deployment.QuteProcessor.collectNamespaceExpressions(((io.quarkus.qute.Expression) (null)), foundExpressions, "kf46"));
        boolean found = false;
        for (java.lang.StackTraceElement element : exception.getStackTrace()) {
            if (element.getClassName().equals("io.quarkus.qute.deployment.QuteProcessor")) {
                found = true;
                break;
            }
        }
        org.junit.jupiter.api.Assertions.assertTrue(found);
    }

    @org.junit.jupiter.api.Test
    public void staticsFilterReturnsFalseForNonStaticMethod() {
        java.lang.Class<java.lang.Object> objectClass = java.lang.Object.class;
        // ClassInfo classInfo = Index.singleClass(objectClass);
        org.jboss.jandex.TypeVariable returnType = org.jboss.jandex.TypeVariable.create("IN78 +@gT{<^b8");
        java.lang.String[] params = new java.lang.String[1];
        params[0] = "IN78 +@gT{<^b8";
        org.jboss.jandex.Type[] paramTypes = new org.jboss.jandex.Type[0];
        org.jboss.jandex.TypeVariable[] typeParams = new org.jboss.jandex.TypeVariable[2];
        // MethodInfo method = MethodInfo.create(classInfo, "IN78 +@gT{<^b8", params, paramTypes, returnType, (short) -6681,
        // typeParams, paramTypes);
        // boolean accept = QuteProcessor.staticsFilter(method);
        // assertFalse(accept);
    }

    @org.junit.jupiter.api.Test
    public void readTemplateContentReturnsEmptyStringForEmptyFile() throws java.lang.Exception {
        java.io.File tempFile = java.io.File.createTempFile("k,rWgE)MEPis", "k,rWgE)MEPis", ((java.io.File) (null)));
        java.nio.file.Path path = tempFile.toPath();
        java.nio.charset.Charset charset = java.nio.charset.Charset.defaultCharset();
        java.lang.String content = io.quarkus.qute.deployment.QuteProcessor.readTemplateContent(path, charset);
        org.junit.jupiter.api.Assertions.assertEquals("", content);
    }

    @org.junit.jupiter.api.Test
    public void findTemplatePathReturnsNullWhenNotPresent() {
        java.util.LinkedList<io.quarkus.qute.deployment.TemplatesAnalysisBuildItem.TemplateAnalysis> analysesList = new java.util.LinkedList<>();
        io.quarkus.qute.deployment.TemplatesAnalysisBuildItem analyses = new io.quarkus.qute.deployment.TemplatesAnalysisBuildItem(analysesList);
        java.lang.String path = io.quarkus.qute.deployment.QuteProcessor.findTemplatePath(analyses, "k@q)HDU<bJ_Kx_");
        org.junit.jupiter.api.Assertions.assertNull(path);
    }

    @org.junit.jupiter.api.Test
    public void extractMatchTypeReturnsMatchingTypeWhenFound() {
        java.util.LinkedHashSet<org.jboss.jandex.Type> candidates = new java.util.LinkedHashSet<>();
        org.jboss.jandex.WildcardType wildcard = org.jboss.jandex.WildcardType.UNBOUNDED;
        candidates.add(wildcard);
        org.jboss.jandex.DotName typeName = wildcard.name();
        java.util.function.Function<org.jboss.jandex.Type, org.jboss.jandex.Type> identity = java.util.function.Function.identity();
        org.jboss.jandex.Type matched = io.quarkus.qute.deployment.QuteProcessor.extractMatchType(candidates, typeName, identity);
        org.junit.jupiter.api.Assertions.assertSame(wildcard, matched);
    }

    @org.junit.jupiter.api.Test
    public void collectTemplateVariantsThrowsSIOOBEOnInvalidSuffixHandling() {
        io.quarkus.qute.deployment.QuteProcessor processor = new io.quarkus.qute.deployment.QuteProcessor();
        io.quarkus.qute.deployment.TemplatePathBuildItem first = org.mockito.Mockito.mock(io.quarkus.qute.deployment.TemplatePathBuildItem.class);
        org.mockito.Mockito.doReturn("MW", "MW", "4uX5{").when(first).getPath();
        io.quarkus.qute.deployment.TemplatePathBuildItem second = org.mockito.Mockito.mock(io.quarkus.qute.deployment.TemplatePathBuildItem.class);
        org.mockito.Mockito.doReturn("NOSECTION_HELPER_FOUNDelse", "MW", "8O", "NOSECTION_HELPER_FOUNDelse").when(second).getPath();
        java.util.List<io.quarkus.qute.deployment.TemplatePathBuildItem> paths = java.util.List.of(first, second, second, first, second, first, second);
        io.quarkus.qute.deployment.EffectiveTemplatePathsBuildItem effectivePaths = new io.quarkus.qute.deployment.EffectiveTemplatePathsBuildItem(paths);
        java.util.LinkedList<java.lang.String> suffixes = new java.util.LinkedList<>();
        suffixes.add("8O");
        io.quarkus.qute.runtime.QuteConfig config = org.mockito.Mockito.mock(io.quarkus.qute.runtime.QuteConfig.class);
        org.mockito.Mockito.doReturn(suffixes).when(config).suffixes();
        org.junit.jupiter.api.Assertions.assertThrows(java.lang.StringIndexOutOfBoundsException.class, () -> processor.collectTemplateVariants(effectivePaths, config));
    }

    @org.junit.jupiter.api.Test
    public void processTemplateErrorsNoIncorrectExpressionsProducesNoServiceStart() {
        java.util.Stack<io.quarkus.qute.deployment.TemplatesAnalysisBuildItem.TemplateAnalysis> analysesList = new java.util.Stack<>();
        io.quarkus.qute.deployment.TemplatesAnalysisBuildItem analyses = new io.quarkus.qute.deployment.TemplatesAnalysisBuildItem(analysesList);
        io.quarkus.qute.deployment.QuteProcessor processor = new io.quarkus.qute.deployment.QuteProcessor();
        java.util.Vector<io.quarkus.qute.deployment.IncorrectExpressionBuildItem> incorrect = new java.util.Vector<>();
        processor.processTemplateErrors(analyses, incorrect, ((io.quarkus.deployment.annotations.BuildProducer<io.quarkus.deployment.builditem.ServiceStartBuildItem>) (null)));
        org.junit.jupiter.api.Assertions.assertEquals(0, incorrect.size());
    }

    @org.junit.jupiter.api.Test
    public void addSingletonToNamedRecordsHasExpectedReason() {
        io.quarkus.qute.deployment.QuteProcessor processor = new io.quarkus.qute.deployment.QuteProcessor();
        io.quarkus.arc.deployment.AutoAddScopeBuildItem item = processor.addSingletonToNamedRecords();
        org.junit.jupiter.api.Assertions.assertEquals("Found Java record annotated with @Named", item.getReason());
    }

    @org.junit.jupiter.api.Test
    public void firstPassLookupConfigDeclaredMembersOnlyHonorsOverrideTrue() {
        java.lang.Class<io.quarkus.qute.deployment.TypeCheckExcludeBuildItem> target = io.quarkus.qute.deployment.TypeCheckExcludeBuildItem.class;
        io.quarkus.builder.BuildContext buildContext = org.mockito.Mockito.mock(io.quarkus.builder.BuildContext.class);
        io.quarkus.deployment.BuildProducerImpl<io.quarkus.qute.deployment.TypeCheckExcludeBuildItem> producer = new io.quarkus.deployment.BuildProducerImpl<>(target, buildContext);
        java.util.function.Predicate<org.jboss.jandex.AnnotationTarget> filter = java.util.function.Predicate.isEqual(producer);
        java.lang.Boolean declaredOnly = java.lang.Boolean.TRUE;
        io.quarkus.qute.deployment.QuteProcessor.FirstPassJavaMemberLookupConfig config = new io.quarkus.qute.deployment.QuteProcessor.FirstPassJavaMemberLookupConfig(((io.quarkus.qute.deployment.QuteProcessor.JavaMemberLookupConfig) (null)), filter, declaredOnly);
        boolean result = config.declaredMembersOnly();
        org.junit.jupiter.api.Assertions.assertTrue(result);
    }

    @org.junit.jupiter.api.Test
    public void firstPassLookupConfigDeclaredMembersOnlyTrueWhenNextTrue() {
        org.jboss.jandex.IndexView[] empty = new org.jboss.jandex.IndexView[0];
        org.jboss.jandex.StackedIndex index = org.jboss.jandex.StackedIndex.create(empty);
        java.lang.Object target = new java.lang.Object();
        java.util.function.Predicate<org.jboss.jandex.AnnotationTarget> filter = java.util.function.Predicate.isEqual(target);
        io.quarkus.qute.deployment.QuteProcessor.FixedJavaMemberLookupConfig next = new io.quarkus.qute.deployment.QuteProcessor.FixedJavaMemberLookupConfig(index, filter, true);
        io.quarkus.qute.deployment.QuteProcessor.FirstPassJavaMemberLookupConfig firstPass = new io.quarkus.qute.deployment.QuteProcessor.FirstPassJavaMemberLookupConfig(next, filter, ((java.lang.Boolean) (null)));
        boolean declaredOnly = firstPass.declaredMembersOnly();
        org.junit.jupiter.api.Assertions.assertTrue(declaredOnly);
    }

    @org.junit.jupiter.api.Test
    public void firstPassLookupConfigFilterDelegatesToNext() {
        java.lang.Class<java.lang.Object> objectClass = java.lang.Object.class;
        // ClassInfo classInfo = Index.singleClass(objectClass);
        // Map<DotName, List<AnnotationInstance>> annotations = classInfo.annotationsMap();
        java.util.HashMap<org.jboss.jandex.DotName, java.util.List<org.jboss.jandex.ClassInfo>> map = new java.util.HashMap<>();
        java.util.HashMap<org.jboss.jandex.DotName, org.jboss.jandex.ClassInfo> classes = new java.util.HashMap<>();
        // Index index = Index.create(annotations, map, map, classes);
        // Predicate<AnnotationTarget> filter = Predicate.isEqual(annotations);
        // QuteProcessor.FixedJavaMemberLookupConfig next = new QuteProcessor.FixedJavaMemberLookupConfig(index, filter, true);
        java.lang.Boolean declaredOnly = java.lang.Boolean.FALSE;
        // QuteProcessor.FirstPassJavaMemberLookupConfig firstPass = new QuteProcessor.FirstPassJavaMemberLookupConfig(next, filter,
        // declaredOnly);
        // firstPass.filter();
        // assertTrue(next.declaredMembersOnly());
    }

    @org.junit.jupiter.api.Test
    public void getNameOnInjectionPointThrowsIAEForInvalidType() {
        org.jboss.jandex.ClassType characterType = org.jboss.jandex.ClassType.CHARACTER_CLASS;
        java.util.LinkedHashSet<org.jboss.jandex.AnnotationInstance> qualifiers = new java.util.LinkedHashSet<>();
        io.quarkus.arc.processor.InjectionPointInfo.TypeAndQualifiers tq = new io.quarkus.arc.processor.InjectionPointInfo.TypeAndQualifiers(characterType, qualifiers);
        io.quarkus.arc.processor.InjectionPointInfo ip = io.quarkus.arc.processor.InjectionPointInfo.fromSyntheticInjectionPoint(tq);
        java.lang.IllegalArgumentException exception = org.junit.jupiter.api.Assertions.assertThrows(java.lang.IllegalArgumentException.class, () -> io.quarkus.qute.deployment.QuteProcessor.getName(ip));
        boolean found = false;
        for (java.lang.StackTraceElement element : exception.getStackTrace()) {
            if (element.getClassName().equals("io.quarkus.qute.deployment.QuteProcessor")) {
                found = true;
                break;
            }
        }
        org.junit.jupiter.api.Assertions.assertTrue(found);
    }

    @org.junit.jupiter.api.Test
    public void collectNamespaceExpressionsThrowsNPEWhenTemplateAnalysisIsNullInList() {
        java.util.Stack<io.quarkus.qute.deployment.TemplatesAnalysisBuildItem.TemplateAnalysis> analysesList = new java.util.Stack<>();
        analysesList.setSize(5);
        io.quarkus.qute.deployment.TemplatesAnalysisBuildItem analyses = new io.quarkus.qute.deployment.TemplatesAnalysisBuildItem(analysesList);
        java.lang.NullPointerException exception = org.junit.jupiter.api.Assertions.assertThrows(java.lang.NullPointerException.class, () -> io.quarkus.qute.deployment.QuteProcessor.collectNamespaceExpressions(analyses, ""));
        boolean found = false;
        for (java.lang.StackTraceElement element : exception.getStackTrace()) {
            if (element.getClassName().equals("io.quarkus.qute.deployment.QuteProcessor")) {
                found = true;
                break;
            }
        }
        org.junit.jupiter.api.Assertions.assertTrue(found);
    }

    @org.junit.jupiter.api.Test
    public void collectNamespaceExpressionsWithEmptyAnalysesReturnsEmptyMap() {
        java.util.Vector<io.quarkus.qute.deployment.TemplatesAnalysisBuildItem.TemplateAnalysis> analysesList = new java.util.Vector<>();
        io.quarkus.qute.deployment.TemplatesAnalysisBuildItem analyses = new io.quarkus.qute.deployment.TemplatesAnalysisBuildItem(analysesList);
        java.util.Map<io.quarkus.qute.deployment.TemplatesAnalysisBuildItem.TemplateAnalysis, java.util.Set<io.quarkus.qute.Expression>> result = io.quarkus.qute.deployment.QuteProcessor.collectNamespaceExpressions(analyses, "");
        org.junit.jupiter.api.Assertions.assertEquals(0, result.size());
    }

    @org.junit.jupiter.api.Test
    public void validateTemplateDataNamespacesOnEmptyInputNoErrors() {
        io.quarkus.qute.deployment.QuteProcessor processor = new io.quarkus.qute.deployment.QuteProcessor();
        java.util.LinkedList<io.quarkus.qute.deployment.TemplateDataBuildItem> dataItems = new java.util.LinkedList<>();
        java.lang.Class<io.quarkus.deployment.builditem.ServiceStartBuildItem> serviceStartClass = io.quarkus.deployment.builditem.ServiceStartBuildItem.class;
        io.quarkus.builder.BuildContext buildContext = org.mockito.Mockito.mock(io.quarkus.builder.BuildContext.class);
        io.quarkus.deployment.BuildProducerImpl<io.quarkus.deployment.builditem.ServiceStartBuildItem> serviceStartProducer = new io.quarkus.deployment.BuildProducerImpl<>(serviceStartClass, buildContext);
        processor.validateTemplateDataNamespaces(dataItems, serviceStartProducer);
        org.junit.jupiter.api.Assertions.assertEquals(0, dataItems.size());
    }

    @org.junit.jupiter.api.Test
    public void collectTemplateDataAnnotationsHandlesEmptyIndex() {
        io.quarkus.qute.deployment.QuteProcessor processor = new io.quarkus.qute.deployment.QuteProcessor();
        org.jboss.jandex.IndexView[] empty = new org.jboss.jandex.IndexView[0];
        org.jboss.jandex.StackedIndex stackedIndex = org.jboss.jandex.StackedIndex.create(empty);
        java.util.HashSet<org.jboss.jandex.DotName> additional = new java.util.HashSet<>(387);
        io.quarkus.arc.deployment.BeanArchiveIndexBuildItem beanArchiveIndex = new io.quarkus.arc.deployment.BeanArchiveIndexBuildItem(stackedIndex, stackedIndex, additional);
        java.lang.Class<io.quarkus.qute.deployment.TemplateDataBuildItem> dataClass = io.quarkus.qute.deployment.TemplateDataBuildItem.class;
        io.quarkus.builder.BuildContext buildContext = org.mockito.Mockito.mock(io.quarkus.builder.BuildContext.class);
        io.quarkus.deployment.BuildProducerImpl<io.quarkus.qute.deployment.TemplateDataBuildItem> dataProducer = new io.quarkus.deployment.BuildProducerImpl<>(dataClass, buildContext);
        processor.collectTemplateDataAnnotations(beanArchiveIndex, dataProducer);
    }

    @org.junit.jupiter.api.Test
    public void matchResultIsClassTrueForClassType() {
        io.quarkus.qute.deployment.QuteProcessor.MatchResult matchResult = new io.quarkus.qute.deployment.QuteProcessor.MatchResult(((io.quarkus.qute.deployment.Types.AssignabilityCheck) (null)));
        org.jboss.jandex.ClassType classType = org.jboss.jandex.ClassType.ANNOTATION_TYPE;
        matchResult.setValues(((org.jboss.jandex.ClassInfo) (null)), classType);
        boolean isClass = matchResult.isClass();
        org.junit.jupiter.api.Assertions.assertTrue(isClass);
    }

    @org.junit.jupiter.api.Test
    public void matchResultIsClassFalseWhenNoTypeSet() {
        org.jboss.jandex.EmptyIndex emptyIndex = org.jboss.jandex.EmptyIndex.INSTANCE;
        io.quarkus.qute.deployment.Types.AssignabilityCheck assignability = new io.quarkus.qute.deployment.Types.AssignabilityCheck(emptyIndex);
        io.quarkus.qute.deployment.QuteProcessor.MatchResult matchResult = new io.quarkus.qute.deployment.QuteProcessor.MatchResult(assignability);
        boolean isClass = matchResult.isClass();
        org.junit.jupiter.api.Assertions.assertFalse(isClass);
    }

    @org.junit.jupiter.api.Test
    public void matchResultIsParameterizedTypeFalseWhenTypeNotSet() {
        java.util.HashMap<org.jboss.jandex.DotName, java.util.List<org.jboss.jandex.AnnotationInstance>> annotations = new java.util.HashMap<>();
        java.util.HashMap<org.jboss.jandex.DotName, java.util.List<org.jboss.jandex.ClassInfo>> map = new java.util.HashMap<>();
        java.util.HashMap<org.jboss.jandex.DotName, org.jboss.jandex.ClassInfo> classes = new java.util.HashMap<>();
        org.jboss.jandex.Index index = org.jboss.jandex.Index.create(annotations, map, map, map, classes, map);
        io.quarkus.qute.deployment.Types.AssignabilityCheck assignability = new io.quarkus.qute.deployment.Types.AssignabilityCheck(index);
        io.quarkus.qute.deployment.QuteProcessor.MatchResult matchResult = new io.quarkus.qute.deployment.QuteProcessor.MatchResult(assignability);
        boolean parameterized = matchResult.isParameterizedType();
        org.junit.jupiter.api.Assertions.assertFalse(parameterized);
    }

    @org.junit.jupiter.api.Test
    public void matchResultIsArrayFalseByDefault() {
        io.quarkus.qute.deployment.QuteProcessor.MatchResult matchResult = new io.quarkus.qute.deployment.QuteProcessor.MatchResult(((io.quarkus.qute.deployment.Types.AssignabilityCheck) (null)));
        boolean isArray = matchResult.isArray();
        org.junit.jupiter.api.Assertions.assertFalse(isArray);
    }

    @org.junit.jupiter.api.Test
    public void matchResultIsPrimitiveFalseForTypeVariable() {
        io.quarkus.qute.deployment.QuteProcessor.MatchResult matchResult = new io.quarkus.qute.deployment.QuteProcessor.MatchResult(((io.quarkus.qute.deployment.Types.AssignabilityCheck) (null)));
        org.jboss.jandex.TypeVariable variable = org.jboss.jandex.TypeVariable.create("8LiOQgPN+)^-");
        matchResult.setValues(((org.jboss.jandex.ClassInfo) (null)), variable);
        boolean primitive = matchResult.isPrimitive();
        org.junit.jupiter.api.Assertions.assertFalse(primitive);
    }

    @org.junit.jupiter.api.Test
    public void matchResultIsPrimitiveFalseByDefault() {
        io.quarkus.qute.deployment.QuteProcessor.MatchResult matchResult = new io.quarkus.qute.deployment.QuteProcessor.MatchResult(((io.quarkus.qute.deployment.Types.AssignabilityCheck) (null)));
        boolean primitive = matchResult.isPrimitive();
        org.junit.jupiter.api.Assertions.assertFalse(primitive);
    }

    @org.junit.jupiter.api.Test
    public void getParameterizedTypeArgumentsEmptyForClassType() {
        io.quarkus.qute.deployment.QuteProcessor.MatchResult matchResult = new io.quarkus.qute.deployment.QuteProcessor.MatchResult(((io.quarkus.qute.deployment.Types.AssignabilityCheck) (null)));
        org.jboss.jandex.ClassType classType = org.jboss.jandex.ClassType.ANNOTATION_TYPE;
        matchResult.setValues(((org.jboss.jandex.ClassInfo) (null)), classType);
        java.util.List<org.jboss.jandex.Type> typeArgs = matchResult.getParameterizedTypeArguments();
        org.junit.jupiter.api.Assertions.assertTrue(typeArgs.isEmpty());
    }

    @org.junit.jupiter.api.Test
    public void extractMatchTypeReturnsNullWhenNoMatch() {
        java.util.LinkedHashSet<org.jboss.jandex.Type> candidates = new java.util.LinkedHashSet<>();
        org.jboss.jandex.DotName targetName = org.jboss.jandex.DotName.FLOAT_CLASS_NAME;
        org.jboss.jandex.PrimitiveType doublePrimitive = org.jboss.jandex.PrimitiveType.DOUBLE;
        candidates.add(doublePrimitive);
        java.util.function.Function<org.jboss.jandex.Type, org.jboss.jandex.Type> extractor = QuteProcessor.MAP_ENTRY_EXTRACT_FUN;
        org.jboss.jandex.Type result = io.quarkus.qute.deployment.QuteProcessor.extractMatchType(candidates, targetName, extractor);
        org.junit.jupiter.api.Assertions.assertNull(result);
    }

    @org.junit.jupiter.api.Test
    public void processLoopElementHintWithNullExpressionThrowsNPE() {
        io.quarkus.qute.deployment.QuteProcessor.MatchResult matchResult = new io.quarkus.qute.deployment.QuteProcessor.MatchResult(((io.quarkus.qute.deployment.Types.AssignabilityCheck) (null)));
        org.jboss.jandex.TypeVariable variable = org.jboss.jandex.TypeVariable.create("8LiOQgPN+)^-");
        matchResult.setValues(((org.jboss.jandex.ClassInfo) (null)), variable);
        java.lang.Class<io.quarkus.qute.deployment.IncorrectExpressionBuildItem> incorrectClass = io.quarkus.qute.deployment.IncorrectExpressionBuildItem.class;
        io.quarkus.deployment.BuildProducerImpl<io.quarkus.qute.deployment.IncorrectExpressionBuildItem> incorrectProducer = new io.quarkus.deployment.BuildProducerImpl<>(incorrectClass, ((io.quarkus.builder.BuildContext) (null)));
        java.lang.NullPointerException exception = org.junit.jupiter.api.Assertions.assertThrows(java.lang.NullPointerException.class, () -> io.quarkus.qute.deployment.QuteProcessor.processLoopElementHint(matchResult, ((org.jboss.jandex.IndexView) (null)), ((io.quarkus.qute.Expression) (null)), incorrectProducer));
        boolean found = false;
        for (java.lang.StackTraceElement element : exception.getStackTrace()) {
            if (element.getClassName().equals("io.quarkus.qute.deployment.QuteProcessor")) {
                found = true;
                break;
            }
        }
        org.junit.jupiter.api.Assertions.assertTrue(found);
    }

    @org.junit.jupiter.api.Test
    public void processLoopElementHintDoesNotThrowOnNullExpressionAndEmptyIndex() {
        org.jboss.jandex.IndexView index = org.jboss.jandex.IndexView.empty();
        io.quarkus.qute.deployment.Types.AssignabilityCheck assignability = new io.quarkus.qute.deployment.Types.AssignabilityCheck(index);
        java.lang.Class<io.quarkus.qute.deployment.IncorrectExpressionBuildItem> incorrectClass = io.quarkus.qute.deployment.IncorrectExpressionBuildItem.class;
        io.quarkus.builder.BuildContext buildContext = org.mockito.Mockito.mock(io.quarkus.builder.BuildContext.class);
        io.quarkus.deployment.BuildProducerImpl<io.quarkus.qute.deployment.IncorrectExpressionBuildItem> incorrectProducer = new io.quarkus.deployment.BuildProducerImpl<>(incorrectClass, buildContext);
        io.quarkus.qute.deployment.QuteProcessor.MatchResult matchResult = new io.quarkus.qute.deployment.QuteProcessor.MatchResult(assignability);
        io.quarkus.qute.deployment.QuteProcessor.processLoopElementHint(matchResult, index, ((io.quarkus.qute.Expression) (null)), incorrectProducer);
    }
}