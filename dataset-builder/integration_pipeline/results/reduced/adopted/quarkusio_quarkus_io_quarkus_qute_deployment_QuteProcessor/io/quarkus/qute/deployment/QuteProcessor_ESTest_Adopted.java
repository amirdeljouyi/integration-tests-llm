package io.quarkus.qute.deployment;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.AutoAddScopeBuildItem;
import io.quarkus.arc.deployment.BeanArchiveIndexBuildItem;
import io.quarkus.arc.deployment.BeanContainerBuildItem;
import io.quarkus.arc.deployment.BeanDefiningAnnotationBuildItem;
import io.quarkus.arc.deployment.CompletedApplicationClassPredicateBuildItem;
import io.quarkus.arc.deployment.QualifierRegistrarBuildItem;
import io.quarkus.arc.deployment.SynthesisFinishedBuildItem;
import io.quarkus.arc.deployment.SyntheticBeanBuildItem;
import io.quarkus.arc.deployment.ValidationPhaseBuildItem;
import io.quarkus.arc.processor.BeanDeployment;
import io.quarkus.arc.processor.BeanDeploymentValidator;
import io.quarkus.arc.processor.BeanInfo;
import io.quarkus.arc.processor.BeanProcessor;
import io.quarkus.arc.processor.BeanResolver;
import io.quarkus.arc.processor.InjectionPointInfo;
import io.quarkus.arc.processor.ObserverInfo;
import io.quarkus.arc.runtime.BeanContainer;
import io.quarkus.bootstrap.model.ApplicationModel;
import io.quarkus.builder.BuildContext;
import io.quarkus.deployment.ApplicationArchive;
import io.quarkus.deployment.ApplicationArchiveImpl;
import io.quarkus.deployment.BuildProducerImpl;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.builditem.ApplicationArchivesBuildItem;
import io.quarkus.deployment.builditem.BytecodeTransformerBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.GeneratedClassBuildItem;
import io.quarkus.deployment.builditem.GeneratedResourceBuildItem;
import io.quarkus.deployment.builditem.HotDeploymentWatchedFileBuildItem;
import io.quarkus.deployment.builditem.LaunchModeBuildItem;
import io.quarkus.deployment.builditem.LiveReloadBuildItem;
import io.quarkus.deployment.builditem.ServiceStartBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageResourceBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import io.quarkus.deployment.dev.AlwaysFalsePredicate;
import io.quarkus.deployment.pkg.NativeConfig;
import io.quarkus.deployment.pkg.builditem.CurateOutcomeBuildItem;
import io.quarkus.dev.spi.DevModeType;
import io.quarkus.maven.dependency.ResolvedDependencyBuilder;
import io.quarkus.panache.common.deployment.PanacheEntityClassesBuildItem;
import io.quarkus.paths.DirectoryPathTree;
import io.quarkus.paths.EmptyPathTree;
import io.quarkus.qute.Engine;
import io.quarkus.qute.Expression;
import io.quarkus.qute.InsertSectionHelper;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateNode;
import io.quarkus.qute.WhenSectionHelper;
import io.quarkus.qute.deployment.TemplatesAnalysisBuildItem.TemplateAnalysis;
import io.quarkus.qute.runtime.QuteConfig;
import io.quarkus.qute.runtime.QuteRecorder;
import io.quarkus.qute.runtime.QuteTestModeConfig;
import io.quarkus.runtime.LaunchMode;
import org.aesh.command.impl.invocation.DefaultCommandInvocation;
import org.aesh.command.impl.registry.MutableCommandRegistryImpl;
import org.aesh.readline.alias.AliasManager;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.ClassType;
import org.jboss.jandex.CompositeIndex;
import org.jboss.jandex.DotName;
import org.jboss.jandex.EmptyIndex;
import org.jboss.jandex.Index;
import org.jboss.jandex.IndexView;
import org.jboss.jandex.MethodInfo;
import org.jboss.jandex.ParameterizedType;
import org.jboss.jandex.PrimitiveType;
import org.jboss.jandex.StackedIndex;
import org.jboss.jandex.Type;
import org.jboss.jandex.TypeVariable;
import org.jboss.jandex.WildcardType;
public class QuteProcessor_ESTest_Adopted {
    @org.junit.Test(timeout = 4000)
    public void isEmptyReturnsTrueForNewMatchResult() throws java.lang.Throwable {
        org.jboss.jandex.EmptyIndex emptyIndex = org.jboss.jandex.EmptyIndex.INSTANCE;
        io.quarkus.qute.deployment.Types.AssignabilityCheck assignabilityCheck = new io.quarkus.qute.deployment.Types.AssignabilityCheck(emptyIndex);
        io.quarkus.qute.deployment.QuteProcessor.MatchResult matchResult = new io.quarkus.qute.deployment.QuteProcessor.MatchResult(assignabilityCheck);
        boolean isEmpty = matchResult.isEmpty();
        org.junit.Assert.assertTrue(isEmpty);
    }

    @org.junit.Test(timeout = 4000)
    public void autoExtractTypeDoesNotThrowOnNullAssignabilityCheck() throws java.lang.Throwable {
        io.quarkus.qute.deployment.QuteProcessor.MatchResult matchResult = new io.quarkus.qute.deployment.QuteProcessor.MatchResult(((io.quarkus.qute.deployment.Types.AssignabilityCheck) (null)));
        matchResult.autoExtractType();
    }

    @org.junit.Test(timeout = 4000)
    public void collectNamespaceExpressionsWithNullAnalysisThrowsNPE() throws java.lang.Throwable {
        // Undeclared exception!
        try {
            io.quarkus.qute.deployment.QuteProcessor.collectNamespaceExpressions(((io.quarkus.qute.deployment.TemplatesAnalysisBuildItem.TemplateAnalysis) (null)), ((java.lang.String) (null)));
            org.junit.Assert.fail("Expecting exception: NullPointerException");
        } catch (java.lang.NullPointerException e) {
            // 
            // Cannot read field \"expressions\" because \"analysis\" is null
            // 
            org.evosuite.runtime.EvoAssertions.verifyException("io.quarkus.qute.deployment.QuteProcessor", e);
        }
    }

    @org.junit.Test(timeout = 4000)
    public void validateExpressionsWithNullBeanDeploymentCollectionsThrowsNPE() throws java.lang.Throwable {
        io.quarkus.qute.deployment.QuteProcessor processor = new io.quarkus.qute.deployment.QuteProcessor();
        io.quarkus.deployment.dev.AlwaysFalsePredicate<io.quarkus.qute.deployment.TypeCheckExcludeBuildItem.TypeCheck> alwaysFalse = new io.quarkus.deployment.dev.AlwaysFalsePredicate<io.quarkus.qute.deployment.TypeCheckExcludeBuildItem.TypeCheck>();
        io.quarkus.qute.deployment.TypeCheckExcludeBuildItem excludeItem = new io.quarkus.qute.deployment.TypeCheckExcludeBuildItem(alwaysFalse, true);
        java.util.List<io.quarkus.qute.deployment.TypeCheckExcludeBuildItem> excludes = java.util.List.of(excludeItem, excludeItem, excludeItem, excludeItem, excludeItem);
        java.lang.Class<io.quarkus.qute.deployment.IncorrectExpressionBuildItem> incorrectClass = io.quarkus.qute.deployment.IncorrectExpressionBuildItem.class;
        io.quarkus.builder.BuildContext buildContext = org.mockito.Mockito.mock(io.quarkus.builder.BuildContext.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        io.quarkus.deployment.BuildProducerImpl<io.quarkus.qute.deployment.IncorrectExpressionBuildItem> incorrectProducer = new io.quarkus.deployment.BuildProducerImpl<io.quarkus.qute.deployment.IncorrectExpressionBuildItem>(incorrectClass, buildContext);
        io.quarkus.arc.processor.BeanDeployment beanDeployment = org.mockito.Mockito.mock(io.quarkus.arc.processor.BeanDeployment.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        org.mockito.Mockito.doReturn(((io.quarkus.arc.processor.BeanResolver) (null))).when(beanDeployment).getBeanResolver();
        org.mockito.Mockito.doReturn(((java.util.Collection) (null))).when(beanDeployment).getBeans();
        org.mockito.Mockito.doReturn(((java.util.Collection) (null))).when(beanDeployment).getInjectionPoints();
        org.mockito.Mockito.doReturn(((java.util.Collection) (null))).when(beanDeployment).getObservers();
        io.quarkus.arc.deployment.SynthesisFinishedBuildItem synthesisFinished = new io.quarkus.arc.deployment.SynthesisFinishedBuildItem(beanDeployment);
        java.util.ArrayList<io.quarkus.qute.deployment.CheckedTemplateBuildItem> checkedTemplates = new java.util.ArrayList<io.quarkus.qute.deployment.CheckedTemplateBuildItem>();
        java.util.Stack<io.quarkus.qute.deployment.TemplateDataBuildItem> templateDataItems = new java.util.Stack<io.quarkus.qute.deployment.TemplateDataBuildItem>();
        io.quarkus.qute.deployment.TemplatesAnalysisBuildItem analyses = new io.quarkus.qute.deployment.TemplatesAnalysisBuildItem(((java.util.List<io.quarkus.qute.deployment.TemplatesAnalysisBuildItem.TemplateAnalysis>) (null)));
        java.util.LinkedHashSet<org.jboss.jandex.DotName> beanExclusions = new java.util.LinkedHashSet<org.jboss.jandex.DotName>();
        io.quarkus.arc.deployment.BeanArchiveIndexBuildItem beanArchiveIndex = new io.quarkus.arc.deployment.BeanArchiveIndexBuildItem(((org.jboss.jandex.IndexView) (null)), ((org.jboss.jandex.IndexView) (null)), beanExclusions);
        java.util.ArrayList<io.quarkus.qute.deployment.TemplateExtensionMethodBuildItem> extensionMethods = new java.util.ArrayList<io.quarkus.qute.deployment.TemplateExtensionMethodBuildItem>();
        java.lang.Class<io.quarkus.qute.deployment.ImplicitValueResolverBuildItem> implicitClass = io.quarkus.qute.deployment.ImplicitValueResolverBuildItem.class;
        io.quarkus.builder.BuildContext buildContext1 = org.mockito.Mockito.mock(io.quarkus.builder.BuildContext.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        io.quarkus.deployment.BuildProducerImpl<io.quarkus.qute.deployment.ImplicitValueResolverBuildItem> implicitProducer = new io.quarkus.deployment.BuildProducerImpl<io.quarkus.qute.deployment.ImplicitValueResolverBuildItem>(implicitClass, buildContext1);
        io.quarkus.deployment.pkg.NativeConfig nativeConfig = org.mockito.Mockito.mock(io.quarkus.deployment.pkg.NativeConfig.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        // Undeclared exception!
        try {
            processor.validateExpressions(analyses, beanArchiveIndex, extensionMethods, excludes, incorrectProducer, implicitProducer, ((io.quarkus.deployment.annotations.BuildProducer<io.quarkus.qute.deployment.TemplateExpressionMatchesBuildItem>) (null)), synthesisFinished, checkedTemplates, templateDataItems, ((io.quarkus.qute.runtime.QuteConfig) (null)), nativeConfig, ((java.util.List<io.quarkus.qute.deployment.TemplateGlobalBuildItem>) (null)));
            org.junit.Assert.fail("Expecting exception: NullPointerException");
        } catch (java.lang.NullPointerException e) {
            // 
            // Beans collection is null
            // 
            org.evosuite.runtime.EvoAssertions.verifyException("java.util.Objects", e);
        }
    }

    @org.junit.Test(timeout = 4000)
    public void initializeWithValidParamsButNonProxyRecorderThrowsIAE() throws java.lang.Throwable {
        io.quarkus.qute.deployment.QuteProcessor processor = new io.quarkus.qute.deployment.QuteProcessor();
        java.lang.Class<io.quarkus.arc.deployment.SyntheticBeanBuildItem> syntheticClass = io.quarkus.arc.deployment.SyntheticBeanBuildItem.class;
        io.quarkus.builder.BuildContext buildContext = org.mockito.Mockito.mock(io.quarkus.builder.BuildContext.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        io.quarkus.deployment.BuildProducerImpl<io.quarkus.arc.deployment.SyntheticBeanBuildItem> syntheticProducer = new io.quarkus.deployment.BuildProducerImpl<io.quarkus.arc.deployment.SyntheticBeanBuildItem>(syntheticClass, buildContext);
        io.quarkus.qute.runtime.QuteRecorder recorder = new io.quarkus.qute.runtime.QuteRecorder();
        java.util.Stack<io.quarkus.qute.deployment.TemplatePathBuildItem> templatePaths = new java.util.Stack<io.quarkus.qute.deployment.TemplatePathBuildItem>();
        io.quarkus.qute.deployment.EffectiveTemplatePathsBuildItem effectivePaths = new io.quarkus.qute.deployment.EffectiveTemplatePathsBuildItem(templatePaths);
        java.util.HashMap<java.lang.String, java.util.List<java.lang.String>> variants = new java.util.HashMap<java.lang.String, java.util.List<java.lang.String>>();
        io.quarkus.qute.deployment.TemplateVariantsBuildItem templateVariants = new io.quarkus.qute.deployment.TemplateVariantsBuildItem(variants);
        java.util.Optional<io.quarkus.qute.deployment.TemplateVariantsBuildItem> optionalVariants = java.util.Optional.of(templateVariants);
        java.util.LinkedList<io.quarkus.qute.deployment.TemplateRootBuildItem> roots = new java.util.LinkedList<io.quarkus.qute.deployment.TemplateRootBuildItem>();
        io.quarkus.qute.deployment.TemplateRootsBuildItem templateRoots = processor.collectTemplateRoots(roots);
        java.util.Stack<io.quarkus.qute.deployment.TemplatePathExcludeBuildItem> excludes = new java.util.Stack<io.quarkus.qute.deployment.TemplatePathExcludeBuildItem>();
        io.quarkus.qute.deployment.TemplatePathExcludeBuildItem exclude = new io.quarkus.qute.deployment.TemplatePathExcludeBuildItem(((java.lang.String) (null)));
        excludes.add(exclude);
        // Undeclared exception!
        try {
            processor.initialize(syntheticProducer, recorder, effectivePaths, optionalVariants, templateRoots, excludes);
            org.junit.Assert.fail("Expecting exception: IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException e) {
            // 
            // The object is not a proxy returned from a recorder method: io.quarkus.qute.runtime.QuteRecorder$1@0000000011
            // 
            org.evosuite.runtime.EvoAssertions.verifyException("io.quarkus.arc.deployment.SyntheticBeanBuildItem$ExtendedBeanConfigurator", e);
        }
    }

    @org.junit.Test(timeout = 4000)
    public void collectTemplateGlobalsDoesNotFailOnEmptyIndex() throws java.lang.Throwable {
        io.quarkus.qute.deployment.QuteProcessor processor = new io.quarkus.qute.deployment.QuteProcessor();
        java.util.ArrayList<org.jboss.jandex.IndexView> views = new java.util.ArrayList<org.jboss.jandex.IndexView>();
        org.jboss.jandex.StackedIndex stackedIndex = org.jboss.jandex.StackedIndex.create(((java.util.List<org.jboss.jandex.IndexView>) (views)));
        java.util.TreeSet<org.jboss.jandex.DotName> subpackages = new java.util.TreeSet<org.jboss.jandex.DotName>();
        io.quarkus.arc.deployment.BeanArchiveIndexBuildItem beanArchiveIndex = new io.quarkus.arc.deployment.BeanArchiveIndexBuildItem(stackedIndex, stackedIndex, subpackages);
        java.lang.Class<io.quarkus.qute.deployment.TemplateGlobalBuildItem> globalClass = io.quarkus.qute.deployment.TemplateGlobalBuildItem.class;
        io.quarkus.builder.BuildContext buildContext = org.mockito.Mockito.mock(io.quarkus.builder.BuildContext.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        io.quarkus.deployment.BuildProducerImpl<io.quarkus.qute.deployment.TemplateGlobalBuildItem> globalProducer = new io.quarkus.deployment.BuildProducerImpl<io.quarkus.qute.deployment.TemplateGlobalBuildItem>(globalClass, buildContext);
        processor.collectTemplateGlobals(beanArchiveIndex, globalProducer);
    }

    @org.junit.Test(timeout = 4000)
    public void collectNamespaceExpressionsOnExpressionNullThrowsNPE() throws java.lang.Throwable {
        java.util.TreeSet<io.quarkus.qute.Expression> foundExpressions = new java.util.TreeSet<io.quarkus.qute.Expression>();
        // Undeclared exception!
        try {
            io.quarkus.qute.deployment.QuteProcessor.collectNamespaceExpressions(((io.quarkus.qute.Expression) (null)), foundExpressions, "kf46");
            org.junit.Assert.fail("Expecting exception: NullPointerException");
        } catch (java.lang.NullPointerException e) {
            // 
            // Cannot invoke \"io.quarkus.qute.Expression.isLiteral()\" because \"expression\" is null
            // 
            org.evosuite.runtime.EvoAssertions.verifyException("io.quarkus.qute.deployment.QuteProcessor", e);
        }
    }

    @org.junit.Test(timeout = 4000)
    public void staticsFilterReturnsFalseForNonStaticMethod() throws java.lang.Throwable {
        java.lang.Class<java.lang.Object> objectClass = java.lang.Object.class;
        org.jboss.jandex.ClassInfo classInfo = org.jboss.jandex.Index.singleClass(objectClass);
        org.jboss.jandex.TypeVariable returnType = org.jboss.jandex.TypeVariable.create("IN78 +@gT{<^b8");
        java.lang.String[] params = new java.lang.String[1];
        params[0] = "IN78 +@gT{<^b8";
        org.jboss.jandex.Type[] paramTypes = new org.jboss.jandex.Type[0];
        org.jboss.jandex.TypeVariable[] typeParams = new org.jboss.jandex.TypeVariable[2];
        org.jboss.jandex.MethodInfo method = org.jboss.jandex.MethodInfo.create(classInfo, "IN78 +@gT{<^b8", params, paramTypes, ((org.jboss.jandex.Type) (returnType)), ((short) (-6681)), typeParams, paramTypes);
        boolean accept = io.quarkus.qute.deployment.QuteProcessor.staticsFilter(method);
        org.junit.Assert.assertFalse(accept);
    }

    @org.junit.Test(timeout = 4000)
    public void readTemplateContentReturnsEmptyStringForEmptyFile() throws java.lang.Throwable {
        java.io.File tempFile = java.io.File.createTempFile("k,rWgE)MEPis", "k,rWgE)MEPis", ((java.io.File) (null)));
        java.nio.file.Path path = tempFile.toPath();
        java.nio.charset.Charset charset = java.nio.charset.Charset.defaultCharset();
        java.lang.String content = io.quarkus.qute.deployment.QuteProcessor.readTemplateContent(path, charset);
        org.junit.Assert.assertEquals("", content);
    }

    @org.junit.Test(timeout = 4000)
    public void findTemplatePathReturnsNullWhenNotPresent() throws java.lang.Throwable {
        java.util.LinkedList<io.quarkus.qute.deployment.TemplatesAnalysisBuildItem.TemplateAnalysis> analysesList = new java.util.LinkedList<io.quarkus.qute.deployment.TemplatesAnalysisBuildItem.TemplateAnalysis>();
        io.quarkus.qute.deployment.TemplatesAnalysisBuildItem analyses = new io.quarkus.qute.deployment.TemplatesAnalysisBuildItem(analysesList);
        java.lang.String path = io.quarkus.qute.deployment.QuteProcessor.findTemplatePath(analyses, "k@q)HDU<bJ_Kx_");
        org.junit.Assert.assertNull(path);
    }

    @org.junit.Test(timeout = 4000)
    public void extractMatchTypeReturnsMatchingTypeWhenFound() throws java.lang.Throwable {
        java.util.LinkedHashSet<org.jboss.jandex.Type> candidates = new java.util.LinkedHashSet<org.jboss.jandex.Type>();
        org.jboss.jandex.WildcardType wildcard = org.jboss.jandex.WildcardType.UNBOUNDED;
        candidates.add(wildcard);
        org.jboss.jandex.DotName typeName = wildcard.name();
        java.util.function.Function<org.jboss.jandex.Type, org.jboss.jandex.Type> identity = java.util.function.Function.identity();
        org.jboss.jandex.Type matched = io.quarkus.qute.deployment.QuteProcessor.extractMatchType(candidates, typeName, identity);
        org.junit.Assert.assertSame(wildcard, matched);
    }

    @org.junit.Test(timeout = 4000)
    public void collectTemplateVariantsThrowsSIOOBEOnInvalidSuffixHandling() throws java.lang.Throwable {
        io.quarkus.qute.deployment.QuteProcessor processor = new io.quarkus.qute.deployment.QuteProcessor();
        io.quarkus.qute.deployment.TemplatePathBuildItem first = org.mockito.Mockito.mock(io.quarkus.qute.deployment.TemplatePathBuildItem.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        org.mockito.Mockito.doReturn("MW", "MW", "4uX5{").when(first).getPath();
        io.quarkus.qute.deployment.TemplatePathBuildItem second = org.mockito.Mockito.mock(io.quarkus.qute.deployment.TemplatePathBuildItem.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        org.mockito.Mockito.doReturn("NOSECTION_HELPER_FOUNDelse", "MW", "8O", "NOSECTION_HELPER_FOUNDelse").when(second).getPath();
        java.util.List<io.quarkus.qute.deployment.TemplatePathBuildItem> paths = java.util.List.of(first, second, second, first, second, first, second);
        io.quarkus.qute.deployment.EffectiveTemplatePathsBuildItem effectivePaths = new io.quarkus.qute.deployment.EffectiveTemplatePathsBuildItem(paths);
        java.util.LinkedList<java.lang.String> suffixes = new java.util.LinkedList<java.lang.String>();
        suffixes.add("8O");
        io.quarkus.qute.runtime.QuteConfig config = org.mockito.Mockito.mock(io.quarkus.qute.runtime.QuteConfig.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        org.mockito.Mockito.doReturn(suffixes).when(config).suffixes();
        // Undeclared exception!
        try {
            processor.collectTemplateVariants(effectivePaths, config);
            org.junit.Assert.fail("Expecting exception: StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException e) {
        }
    }

    @org.junit.Test(timeout = 4000)
    public void processTemplateErrorsNoIncorrectExpressionsProducesNoServiceStart() throws java.lang.Throwable {
        java.util.Stack<io.quarkus.qute.deployment.TemplatesAnalysisBuildItem.TemplateAnalysis> analysesList = new java.util.Stack<io.quarkus.qute.deployment.TemplatesAnalysisBuildItem.TemplateAnalysis>();
        io.quarkus.qute.deployment.TemplatesAnalysisBuildItem analyses = new io.quarkus.qute.deployment.TemplatesAnalysisBuildItem(analysesList);
        io.quarkus.qute.deployment.QuteProcessor processor = new io.quarkus.qute.deployment.QuteProcessor();
        java.util.Vector<io.quarkus.qute.deployment.IncorrectExpressionBuildItem> incorrect = new java.util.Vector<io.quarkus.qute.deployment.IncorrectExpressionBuildItem>();
        processor.processTemplateErrors(analyses, incorrect, ((io.quarkus.deployment.annotations.BuildProducer<io.quarkus.deployment.builditem.ServiceStartBuildItem>) (null)));
        org.junit.Assert.assertEquals(0, incorrect.size());
    }

    @org.junit.Test(timeout = 4000)
    public void addSingletonToNamedRecordsHasExpectedReason() throws java.lang.Throwable {
        io.quarkus.qute.deployment.QuteProcessor processor = new io.quarkus.qute.deployment.QuteProcessor();
        io.quarkus.arc.deployment.AutoAddScopeBuildItem item = processor.addSingletonToNamedRecords();
        org.junit.Assert.assertEquals("Found Java record annotated with @Named", item.getReason());
    }

    @org.junit.Test(timeout = 4000)
    public void firstPassLookupConfigDeclaredMembersOnlyHonorsOverrideTrue() throws java.lang.Throwable {
        java.lang.Class<io.quarkus.qute.deployment.TypeCheckExcludeBuildItem> target = io.quarkus.qute.deployment.TypeCheckExcludeBuildItem.class;
        io.quarkus.builder.BuildContext buildContext = org.mockito.Mockito.mock(io.quarkus.builder.BuildContext.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        io.quarkus.deployment.BuildProducerImpl<io.quarkus.qute.deployment.TypeCheckExcludeBuildItem> producer = new io.quarkus.deployment.BuildProducerImpl<io.quarkus.qute.deployment.TypeCheckExcludeBuildItem>(target, buildContext);
        java.util.function.Predicate<org.jboss.jandex.AnnotationTarget> filter = java.util.function.Predicate.isEqual(producer);
        java.lang.Boolean declaredOnly = java.lang.Boolean.TRUE;
        io.quarkus.qute.deployment.QuteProcessor.FirstPassJavaMemberLookupConfig config = new io.quarkus.qute.deployment.QuteProcessor.FirstPassJavaMemberLookupConfig(((io.quarkus.qute.deployment.QuteProcessor.JavaMemberLookupConfig) (null)), filter, declaredOnly);
        boolean result = config.declaredMembersOnly();
        org.junit.Assert.assertTrue(result);
    }

    @org.junit.Test(timeout = 4000)
    public void firstPassLookupConfigDeclaredMembersOnlyTrueWhenNextTrue() throws java.lang.Throwable {
        org.jboss.jandex.IndexView[] empty = new org.jboss.jandex.IndexView[0];
        org.jboss.jandex.StackedIndex index = org.jboss.jandex.StackedIndex.create(empty);
        java.lang.Object target = new java.lang.Object();
        java.util.function.Predicate<org.jboss.jandex.AnnotationTarget> filter = java.util.function.Predicate.isEqual(target);
        io.quarkus.qute.deployment.QuteProcessor.FixedJavaMemberLookupConfig next = new io.quarkus.qute.deployment.QuteProcessor.FixedJavaMemberLookupConfig(index, filter, true);
        io.quarkus.qute.deployment.QuteProcessor.FirstPassJavaMemberLookupConfig firstPass = new io.quarkus.qute.deployment.QuteProcessor.FirstPassJavaMemberLookupConfig(next, filter, ((java.lang.Boolean) (null)));
        boolean declaredOnly = firstPass.declaredMembersOnly();
        org.junit.Assert.assertTrue(declaredOnly);
    }

    @org.junit.Test(timeout = 4000)
    public void firstPassLookupConfigFilterDelegatesToNext() throws java.lang.Throwable {
        java.lang.Class<java.lang.Object> objectClass = java.lang.Object.class;
        org.jboss.jandex.ClassInfo classInfo = org.jboss.jandex.Index.singleClass(objectClass);
        java.util.Map<org.jboss.jandex.DotName, java.util.List<org.jboss.jandex.AnnotationInstance>> annotations = classInfo.annotationsMap();
        java.util.HashMap<org.jboss.jandex.DotName, java.util.List<org.jboss.jandex.ClassInfo>> map = new java.util.HashMap<org.jboss.jandex.DotName, java.util.List<org.jboss.jandex.ClassInfo>>();
        java.util.HashMap<org.jboss.jandex.DotName, org.jboss.jandex.ClassInfo> classes = new java.util.HashMap<org.jboss.jandex.DotName, org.jboss.jandex.ClassInfo>();
        org.jboss.jandex.Index index = org.jboss.jandex.Index.create(annotations, ((java.util.Map<org.jboss.jandex.DotName, java.util.List<org.jboss.jandex.ClassInfo>>) (map)), ((java.util.Map<org.jboss.jandex.DotName, java.util.List<org.jboss.jandex.ClassInfo>>) (map)), ((java.util.Map<org.jboss.jandex.DotName, org.jboss.jandex.ClassInfo>) (classes)));
        java.util.function.Predicate<org.jboss.jandex.AnnotationTarget> filter = java.util.function.Predicate.isEqual(annotations);
        io.quarkus.qute.deployment.QuteProcessor.FixedJavaMemberLookupConfig next = new io.quarkus.qute.deployment.QuteProcessor.FixedJavaMemberLookupConfig(index, filter, true);
        java.lang.Boolean declaredOnly = java.lang.Boolean.FALSE;
        io.quarkus.qute.deployment.QuteProcessor.FirstPassJavaMemberLookupConfig firstPass = new io.quarkus.qute.deployment.QuteProcessor.FirstPassJavaMemberLookupConfig(next, filter, declaredOnly);
        firstPass.filter();
        org.junit.Assert.assertTrue(next.declaredMembersOnly());
    }

    @org.junit.Test(timeout = 4000)
    public void getNameOnInjectionPointThrowsIAEForInvalidType() throws java.lang.Throwable {
        org.jboss.jandex.ClassType characterType = org.jboss.jandex.ClassType.CHARACTER_CLASS;
        java.util.LinkedHashSet<org.jboss.jandex.AnnotationInstance> qualifiers = new java.util.LinkedHashSet<org.jboss.jandex.AnnotationInstance>();
        io.quarkus.arc.processor.InjectionPointInfo.TypeAndQualifiers tq = new io.quarkus.arc.processor.InjectionPointInfo.TypeAndQualifiers(characterType, qualifiers);
        io.quarkus.arc.processor.InjectionPointInfo ip = io.quarkus.arc.processor.InjectionPointInfo.fromSyntheticInjectionPoint(tq);
        // Undeclared exception!
        try {
            io.quarkus.qute.deployment.QuteProcessor.getName(ip);
            org.junit.Assert.fail("Expecting exception: IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException e) {
            // 
            // no message in exception (getMessage() returned null)
            // 
            org.evosuite.runtime.EvoAssertions.verifyException("io.quarkus.qute.deployment.QuteProcessor", e);
        }
    }

    @org.junit.Test(timeout = 4000)
    public void collectNamespaceExpressionsThrowsNPEWhenTemplateAnalysisIsNullInList() throws java.lang.Throwable {
        java.util.Stack<io.quarkus.qute.deployment.TemplatesAnalysisBuildItem.TemplateAnalysis> analysesList = new java.util.Stack<io.quarkus.qute.deployment.TemplatesAnalysisBuildItem.TemplateAnalysis>();
        analysesList.setSize(5);
        io.quarkus.qute.deployment.TemplatesAnalysisBuildItem analyses = new io.quarkus.qute.deployment.TemplatesAnalysisBuildItem(analysesList);
        // Undeclared exception!
        try {
            io.quarkus.qute.deployment.QuteProcessor.collectNamespaceExpressions(analyses, "");
            org.junit.Assert.fail("Expecting exception: NullPointerException");
        } catch (java.lang.NullPointerException e) {
            // 
            // Cannot read field \"expressions\" because \"analysis\" is null
            // 
            org.evosuite.runtime.EvoAssertions.verifyException("io.quarkus.qute.deployment.QuteProcessor", e);
        }
    }

    @org.junit.Test(timeout = 4000)
    public void collectNamespaceExpressionsWithEmptyAnalysesReturnsEmptyMap() throws java.lang.Throwable {
        java.util.Vector<io.quarkus.qute.deployment.TemplatesAnalysisBuildItem.TemplateAnalysis> analysesList = new java.util.Vector<io.quarkus.qute.deployment.TemplatesAnalysisBuildItem.TemplateAnalysis>();
        io.quarkus.qute.deployment.TemplatesAnalysisBuildItem analyses = new io.quarkus.qute.deployment.TemplatesAnalysisBuildItem(analysesList);
        java.util.Map<io.quarkus.qute.deployment.TemplatesAnalysisBuildItem.TemplateAnalysis, java.util.Set<io.quarkus.qute.Expression>> result = io.quarkus.qute.deployment.QuteProcessor.collectNamespaceExpressions(analyses, "");
        org.junit.Assert.assertEquals(0, result.size());
    }

    @org.junit.Test(timeout = 4000)
    public void validateTemplateDataNamespacesOnEmptyInputNoErrors() throws java.lang.Throwable {
        io.quarkus.qute.deployment.QuteProcessor processor = new io.quarkus.qute.deployment.QuteProcessor();
        java.util.LinkedList<io.quarkus.qute.deployment.TemplateDataBuildItem> dataItems = new java.util.LinkedList<io.quarkus.qute.deployment.TemplateDataBuildItem>();
        java.lang.Class<io.quarkus.deployment.builditem.ServiceStartBuildItem> serviceStartClass = io.quarkus.deployment.builditem.ServiceStartBuildItem.class;
        io.quarkus.builder.BuildContext buildContext = org.mockito.Mockito.mock(io.quarkus.builder.BuildContext.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        io.quarkus.deployment.BuildProducerImpl<io.quarkus.deployment.builditem.ServiceStartBuildItem> serviceStartProducer = new io.quarkus.deployment.BuildProducerImpl<io.quarkus.deployment.builditem.ServiceStartBuildItem>(serviceStartClass, buildContext);
        processor.validateTemplateDataNamespaces(dataItems, serviceStartProducer);
        org.junit.Assert.assertEquals(0, dataItems.size());
    }

    @org.junit.Test(timeout = 4000)
    public void collectTemplateDataAnnotationsHandlesEmptyIndex() throws java.lang.Throwable {
        io.quarkus.qute.deployment.QuteProcessor processor = new io.quarkus.qute.deployment.QuteProcessor();
        org.jboss.jandex.IndexView[] empty = new org.jboss.jandex.IndexView[0];
        org.jboss.jandex.StackedIndex stackedIndex = org.jboss.jandex.StackedIndex.create(empty);
        java.util.HashSet<org.jboss.jandex.DotName> additional = new java.util.HashSet<org.jboss.jandex.DotName>(387);
        io.quarkus.arc.deployment.BeanArchiveIndexBuildItem beanArchiveIndex = new io.quarkus.arc.deployment.BeanArchiveIndexBuildItem(stackedIndex, stackedIndex, additional);
        java.lang.Class<io.quarkus.qute.deployment.TemplateDataBuildItem> dataClass = io.quarkus.qute.deployment.TemplateDataBuildItem.class;
        io.quarkus.builder.BuildContext buildContext = org.mockito.Mockito.mock(io.quarkus.builder.BuildContext.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        io.quarkus.deployment.BuildProducerImpl<io.quarkus.qute.deployment.TemplateDataBuildItem> dataProducer = new io.quarkus.deployment.BuildProducerImpl<io.quarkus.qute.deployment.TemplateDataBuildItem>(dataClass, buildContext);
        processor.collectTemplateDataAnnotations(beanArchiveIndex, dataProducer);
    }

    @org.junit.Test(timeout = 4000)
    public void matchResultIsClassTrueForClassType() throws java.lang.Throwable {
        io.quarkus.qute.deployment.QuteProcessor.MatchResult matchResult = new io.quarkus.qute.deployment.QuteProcessor.MatchResult(((io.quarkus.qute.deployment.Types.AssignabilityCheck) (null)));
        org.jboss.jandex.ClassType classType = org.jboss.jandex.ClassType.ANNOTATION_TYPE;
        matchResult.setValues(((org.jboss.jandex.ClassInfo) (null)), classType);
        boolean isClass = matchResult.isClass();
        org.junit.Assert.assertTrue(isClass);
    }

    @org.junit.Test(timeout = 4000)
    public void matchResultIsClassFalseWhenNoTypeSet() throws java.lang.Throwable {
        org.jboss.jandex.EmptyIndex emptyIndex = org.jboss.jandex.EmptyIndex.INSTANCE;
        io.quarkus.qute.deployment.Types.AssignabilityCheck assignability = new io.quarkus.qute.deployment.Types.AssignabilityCheck(emptyIndex);
        io.quarkus.qute.deployment.QuteProcessor.MatchResult matchResult = new io.quarkus.qute.deployment.QuteProcessor.MatchResult(assignability);
        boolean isClass = matchResult.isClass();
        org.junit.Assert.assertFalse(isClass);
    }

    @org.junit.Test(timeout = 4000)
    public void matchResultIsParameterizedTypeFalseWhenTypeNotSet() throws java.lang.Throwable {
        java.util.HashMap<org.jboss.jandex.DotName, java.util.List<org.jboss.jandex.AnnotationInstance>> annotations = new java.util.HashMap<org.jboss.jandex.DotName, java.util.List<org.jboss.jandex.AnnotationInstance>>();
        java.util.HashMap<org.jboss.jandex.DotName, java.util.List<org.jboss.jandex.ClassInfo>> map = new java.util.HashMap<org.jboss.jandex.DotName, java.util.List<org.jboss.jandex.ClassInfo>>();
        java.util.HashMap<org.jboss.jandex.DotName, org.jboss.jandex.ClassInfo> classes = new java.util.HashMap<org.jboss.jandex.DotName, org.jboss.jandex.ClassInfo>();
        org.jboss.jandex.Index index = org.jboss.jandex.Index.create(((java.util.Map<org.jboss.jandex.DotName, java.util.List<org.jboss.jandex.AnnotationInstance>>) (annotations)), ((java.util.Map<org.jboss.jandex.DotName, java.util.List<org.jboss.jandex.ClassInfo>>) (map)), ((java.util.Map<org.jboss.jandex.DotName, java.util.List<org.jboss.jandex.ClassInfo>>) (map)), ((java.util.Map<org.jboss.jandex.DotName, java.util.List<org.jboss.jandex.ClassInfo>>) (map)), ((java.util.Map<org.jboss.jandex.DotName, org.jboss.jandex.ClassInfo>) (classes)), ((java.util.Map<org.jboss.jandex.DotName, java.util.List<org.jboss.jandex.ClassInfo>>) (map)));
        io.quarkus.qute.deployment.Types.AssignabilityCheck assignability = new io.quarkus.qute.deployment.Types.AssignabilityCheck(index);
        io.quarkus.qute.deployment.QuteProcessor.MatchResult matchResult = new io.quarkus.qute.deployment.QuteProcessor.MatchResult(assignability);
        boolean parameterized = matchResult.isParameterizedType();
        org.junit.Assert.assertFalse(parameterized);
    }

    @org.junit.Test(timeout = 4000)
    public void matchResultIsArrayFalseByDefault() throws java.lang.Throwable {
        io.quarkus.qute.deployment.QuteProcessor.MatchResult matchResult = new io.quarkus.qute.deployment.QuteProcessor.MatchResult(((io.quarkus.qute.deployment.Types.AssignabilityCheck) (null)));
        boolean isArray = matchResult.isArray();
        org.junit.Assert.assertFalse(isArray);
    }

    @org.junit.Test(timeout = 4000)
    public void matchResultIsPrimitiveFalseForTypeVariable() throws java.lang.Throwable {
        io.quarkus.qute.deployment.QuteProcessor.MatchResult matchResult = new io.quarkus.qute.deployment.QuteProcessor.MatchResult(((io.quarkus.qute.deployment.Types.AssignabilityCheck) (null)));
        org.jboss.jandex.TypeVariable variable = org.jboss.jandex.TypeVariable.create("8LiOQgPN+)^-");
        matchResult.setValues(((org.jboss.jandex.ClassInfo) (null)), variable);
        boolean primitive = matchResult.isPrimitive();
        org.junit.Assert.assertFalse(primitive);
    }

    @org.junit.Test(timeout = 4000)
    public void matchResultIsPrimitiveFalseByDefault() throws java.lang.Throwable {
        io.quarkus.qute.deployment.QuteProcessor.MatchResult matchResult = new io.quarkus.qute.deployment.QuteProcessor.MatchResult(((io.quarkus.qute.deployment.Types.AssignabilityCheck) (null)));
        boolean primitive = matchResult.isPrimitive();
        org.junit.Assert.assertFalse(primitive);
    }

    @org.junit.Test(timeout = 4000)
    public void getParameterizedTypeArgumentsEmptyForClassType() throws java.lang.Throwable {
        io.quarkus.qute.deployment.QuteProcessor.MatchResult matchResult = new io.quarkus.qute.deployment.QuteProcessor.MatchResult(((io.quarkus.qute.deployment.Types.AssignabilityCheck) (null)));
        org.jboss.jandex.ClassType classType = org.jboss.jandex.ClassType.ANNOTATION_TYPE;
        matchResult.setValues(((org.jboss.jandex.ClassInfo) (null)), classType);
        java.util.List<org.jboss.jandex.Type> typeArgs = matchResult.getParameterizedTypeArguments();
        org.junit.Assert.assertTrue(typeArgs.isEmpty());
    }

    @org.junit.Test(timeout = 4000)
    public void extractMatchTypeReturnsNullWhenNoMatch() throws java.lang.Throwable {
        java.util.LinkedHashSet<org.jboss.jandex.Type> candidates = new java.util.LinkedHashSet<org.jboss.jandex.Type>();
        org.jboss.jandex.DotName targetName = org.jboss.jandex.DotName.FLOAT_CLASS_NAME;
        org.jboss.jandex.PrimitiveType doublePrimitive = org.jboss.jandex.PrimitiveType.DOUBLE;
        candidates.add(doublePrimitive);
        java.util.function.Function<org.jboss.jandex.Type, org.jboss.jandex.Type> extractor = QuteProcessor.MAP_ENTRY_EXTRACT_FUN;
        org.jboss.jandex.Type result = io.quarkus.qute.deployment.QuteProcessor.extractMatchType(candidates, targetName, extractor);
        org.junit.Assert.assertNull(result);
    }

    @org.junit.Test(timeout = 4000)
    public void processLoopElementHintWithNullExpressionThrowsNPE() throws java.lang.Throwable {
        io.quarkus.qute.deployment.QuteProcessor.MatchResult matchResult = new io.quarkus.qute.deployment.QuteProcessor.MatchResult(((io.quarkus.qute.deployment.Types.AssignabilityCheck) (null)));
        org.jboss.jandex.TypeVariable variable = org.jboss.jandex.TypeVariable.create("8LiOQgPN+)^-");
        matchResult.setValues(((org.jboss.jandex.ClassInfo) (null)), variable);
        java.lang.Class<io.quarkus.qute.deployment.IncorrectExpressionBuildItem> incorrectClass = io.quarkus.qute.deployment.IncorrectExpressionBuildItem.class;
        io.quarkus.deployment.BuildProducerImpl<io.quarkus.qute.deployment.IncorrectExpressionBuildItem> incorrectProducer = new io.quarkus.deployment.BuildProducerImpl<io.quarkus.qute.deployment.IncorrectExpressionBuildItem>(incorrectClass, ((io.quarkus.builder.BuildContext) (null)));
        // Undeclared exception!
        try {
            io.quarkus.qute.deployment.QuteProcessor.processLoopElementHint(matchResult, ((org.jboss.jandex.IndexView) (null)), ((io.quarkus.qute.Expression) (null)), incorrectProducer);
            org.junit.Assert.fail("Expecting exception: NullPointerException");
        } catch (java.lang.NullPointerException e) {
            // 
            // Cannot invoke \"io.quarkus.qute.Expression.toOriginalString()\" because \"expression\" is null
            // 
            org.evosuite.runtime.EvoAssertions.verifyException("io.quarkus.qute.deployment.QuteProcessor", e);
        }
    }

    @org.junit.Test(timeout = 4000)
    public void processLoopElementHintDoesNotThrowOnNullExpressionAndEmptyIndex() throws java.lang.Throwable {
        org.jboss.jandex.IndexView index = org.jboss.jandex.IndexView.empty();
        io.quarkus.qute.deployment.Types.AssignabilityCheck assignability = new io.quarkus.qute.deployment.Types.AssignabilityCheck(index);
        java.lang.Class<io.quarkus.qute.deployment.IncorrectExpressionBuildItem> incorrectClass = io.quarkus.qute.deployment.IncorrectExpressionBuildItem.class;
        io.quarkus.builder.BuildContext buildContext = org.mockito.Mockito.mock(io.quarkus.builder.BuildContext.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        io.quarkus.deployment.BuildProducerImpl<io.quarkus.qute.deployment.IncorrectExpressionBuildItem> incorrectProducer = new io.quarkus.deployment.BuildProducerImpl<io.quarkus.qute.deployment.IncorrectExpressionBuildItem>(incorrectClass, buildContext);
        io.quarkus.qute.deployment.QuteProcessor.MatchResult matchResult = new io.quarkus.qute.deployment.QuteProcessor.MatchResult(assignability);
        io.quarkus.qute.deployment.QuteProcessor.processLoopElementHint(matchResult, index, ((io.quarkus.qute.Expression) (null)), incorrectProducer);
    }

    @org.junit.Test(timeout = 4000)
    public void processHintsReturnsFalseWithAliasesListProvided() throws java.lang.Throwable {
        java.util.ArrayDeque<org.jboss.jandex.IndexView> views = new java.util.ArrayDeque<org.jboss.jandex.IndexView>();
        org.jboss.jandex.CompositeIndex composite = org.jboss.jandex.CompositeIndex.create(((java.util.Collection<org.jboss.jandex.IndexView>) (views)));
        io.quarkus.qute.WhenSectionHelper.Factory factory = new io.quarkus.qute.WhenSectionHelper.Factory();
        java.util.List<java.lang.String> aliases = factory.getDefaultAliases();
        io.quarkus.qute.deployment.QuteProcessor.MatchResult matchResult = new io.quarkus.qute.deployment.QuteProcessor.MatchResult(((io.quarkus.qute.deployment.Types.AssignabilityCheck) (null)));
        java.util.HashMap<java.lang.Integer, io.quarkus.qute.deployment.QuteProcessor.MatchResult> matches = new java.util.HashMap<java.lang.Integer, io.quarkus.qute.deployment.QuteProcessor.MatchResult>();
        java.lang.Class<io.quarkus.qute.deployment.IncorrectExpressionBuildItem> incorrectClass = io.quarkus.qute.deployment.IncorrectExpressionBuildItem.class;
        io.quarkus.builder.BuildContext buildContext = org.mockito.Mockito.mock(io.quarkus.builder.BuildContext.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        io.quarkus.deployment.BuildProducerImpl<io.quarkus.qute.deployment.IncorrectExpressionBuildItem> incorrectProducer = new io.quarkus.deployment.BuildProducerImpl<io.quarkus.qute.deployment.IncorrectExpressionBuildItem>(incorrectClass, buildContext);
        boolean result = io.quarkus.qute.deployment.QuteProcessor.processHints(((io.quarkus.qute.deployment.TemplatesAnalysisBuildItem.TemplateAnalysis) (null)), aliases, matchResult, composite, ((io.quarkus.qute.Expression) (null)), matches, incorrectProducer);
        org.junit.Assert.assertFalse(result);
    }

    @org.junit.Test(timeout = 4000)
    public void processHintsReturnsFalseWithEmptyAliases() throws java.lang.Throwable {
        org.jboss.jandex.EmptyIndex emptyIndex = org.jboss.jandex.EmptyIndex.INSTANCE;
        org.aesh.readline.alias.AliasManager aliasManager = new org.aesh.readline.alias.AliasManager(((java.io.File) (null)), false);
        java.util.List<java.lang.String> aliases = aliasManager.findAllMatchingNames("N1PV%,Fu .[");
        io.quarkus.qute.deployment.QuteProcessor.MatchResult matchResult = new io.quarkus.qute.deployment.QuteProcessor.MatchResult(((io.quarkus.qute.deployment.Types.AssignabilityCheck) (null)));
        java.util.HashMap<java.lang.Integer, io.quarkus.qute.deployment.QuteProcessor.MatchResult> matches = new java.util.HashMap<java.lang.Integer, io.quarkus.qute.deployment.QuteProcessor.MatchResult>();
        java.lang.Class<io.quarkus.qute.deployment.IncorrectExpressionBuildItem> incorrectClass = io.quarkus.qute.deployment.IncorrectExpressionBuildItem.class;
        io.quarkus.builder.BuildContext buildContext = org.mockito.Mockito.mock(io.quarkus.builder.BuildContext.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        io.quarkus.deployment.BuildProducerImpl<io.quarkus.qute.deployment.IncorrectExpressionBuildItem> incorrectProducer = new io.quarkus.deployment.BuildProducerImpl<io.quarkus.qute.deployment.IncorrectExpressionBuildItem>(incorrectClass, buildContext);
        boolean result = io.quarkus.qute.deployment.QuteProcessor.processHints(((io.quarkus.qute.deployment.TemplatesAnalysisBuildItem.TemplateAnalysis) (null)), aliases, matchResult, emptyIndex, ((io.quarkus.qute.Expression) (null)), matches, incorrectProducer);
        org.junit.Assert.assertFalse(result);
    }

    @org.junit.Test(timeout = 4000)
    public void initializeThrowsNPEWhenExcludeNull() throws java.lang.Throwable {
        io.quarkus.qute.runtime.QuteRecorder recorder = new io.quarkus.qute.runtime.QuteRecorder();
        java.util.Stack<io.quarkus.qute.deployment.TemplatePathBuildItem> templatePaths = new java.util.Stack<io.quarkus.qute.deployment.TemplatePathBuildItem>();
        io.quarkus.qute.deployment.EffectiveTemplatePathsBuildItem effectivePaths = new io.quarkus.qute.deployment.EffectiveTemplatePathsBuildItem(templatePaths);
        java.util.Optional<io.quarkus.qute.deployment.TemplateVariantsBuildItem> variants = java.util.Optional.empty();
        java.util.Stack<io.quarkus.qute.deployment.TemplateRootBuildItem> roots = new java.util.Stack<io.quarkus.qute.deployment.TemplateRootBuildItem>();
        io.quarkus.qute.deployment.QuteProcessor processor = new io.quarkus.qute.deployment.QuteProcessor();
        io.quarkus.qute.deployment.TemplateRootsBuildItem templateRoots = processor.collectTemplateRoots(roots);
        java.util.ArrayList<io.quarkus.qute.deployment.TemplatePathExcludeBuildItem> excludes = new java.util.ArrayList<io.quarkus.qute.deployment.TemplatePathExcludeBuildItem>();
        excludes.add(((io.quarkus.qute.deployment.TemplatePathExcludeBuildItem) (null)));
        // Undeclared exception!
        try {
            processor.initialize(((io.quarkus.deployment.annotations.BuildProducer<io.quarkus.arc.deployment.SyntheticBeanBuildItem>) (null)), recorder, effectivePaths, variants, templateRoots, excludes);
            org.junit.Assert.fail("Expecting exception: NullPointerException");
        } catch (java.lang.NullPointerException e) {
            // 
            // Cannot invoke \"io.quarkus.qute.deployment.TemplatePathExcludeBuildItem.getRegexPattern()\" because \"exclude\" is null
            // 
            org.evosuite.runtime.EvoAssertions.verifyException("io.quarkus.qute.deployment.QuteProcessor", e);
        }
    }

    @org.junit.Test(timeout = 4000)
    public void initializeThrowsIAEWhenNotUsingRecorderProxyCase0() throws java.lang.Throwable {
        io.quarkus.qute.deployment.QuteProcessor processor = new io.quarkus.qute.deployment.QuteProcessor();
        io.quarkus.qute.runtime.QuteRecorder recorder = new io.quarkus.qute.runtime.QuteRecorder();
        java.util.Vector<io.quarkus.qute.deployment.TemplatePathBuildItem> templatePaths = new java.util.Vector<io.quarkus.qute.deployment.TemplatePathBuildItem>();
        java.net.URI source = new java.net.URI("=btK_4Q");
        io.quarkus.qute.deployment.TemplatePathBuildItem pathItem = org.mockito.Mockito.mock(io.quarkus.qute.deployment.TemplatePathBuildItem.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        org.mockito.Mockito.doReturn(((java.lang.String) (null))).when(pathItem).getContent();
        org.mockito.Mockito.doReturn(((java.lang.String) (null)), ((java.lang.String) (null))).when(pathItem).getPath();
        org.mockito.Mockito.doReturn(((java.net.URI) (null))).when(pathItem).getSource();
        org.mockito.Mockito.doReturn(false).when(pathItem).isFileBased();
        templatePaths.add(pathItem);
        io.quarkus.qute.deployment.EffectiveTemplatePathsBuildItem effectivePaths = new io.quarkus.qute.deployment.EffectiveTemplatePathsBuildItem(templatePaths);
        java.util.Optional<io.quarkus.qute.deployment.TemplateVariantsBuildItem> variants = java.util.Optional.empty();
        java.util.Vector<io.quarkus.qute.deployment.TemplateRootBuildItem> roots = new java.util.Vector<io.quarkus.qute.deployment.TemplateRootBuildItem>();
        io.quarkus.qute.deployment.TemplateRootsBuildItem templateRoots = processor.collectTemplateRoots(roots);
        java.util.ArrayList<io.quarkus.qute.deployment.TemplatePathExcludeBuildItem> excludes = new java.util.ArrayList<io.quarkus.qute.deployment.TemplatePathExcludeBuildItem>();
        // Undeclared exception!
        try {
            processor.initialize(((io.quarkus.deployment.annotations.BuildProducer<io.quarkus.arc.deployment.SyntheticBeanBuildItem>) (null)), recorder, effectivePaths, variants, templateRoots, excludes);
            org.junit.Assert.fail("Expecting exception: IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException e) {
            // 
            // The object is not a proxy returned from a recorder method: io.quarkus.qute.runtime.QuteRecorder$1@0000000009
            // 
            org.evosuite.runtime.EvoAssertions.verifyException("io.quarkus.arc.deployment.SyntheticBeanBuildItem$ExtendedBeanConfigurator", e);
        }
    }

    @org.junit.Test(timeout = 4000)
    public void initializeThrowsIAEWhenNotUsingRecorderProxyCase1() throws java.lang.Throwable {
        io.quarkus.qute.deployment.QuteProcessor processor = new io.quarkus.qute.deployment.QuteProcessor();
        io.quarkus.qute.runtime.QuteRecorder recorder = new io.quarkus.qute.runtime.QuteRecorder();
        java.util.Vector<io.quarkus.qute.deployment.TemplatePathBuildItem> templatePaths = new java.util.Vector<io.quarkus.qute.deployment.TemplatePathBuildItem>();
        io.quarkus.qute.deployment.TemplatePathBuildItem pathItem = org.mockito.Mockito.mock(io.quarkus.qute.deployment.TemplatePathBuildItem.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        org.mockito.Mockito.doReturn(((java.lang.String) (null))).when(pathItem).getContent();
        org.mockito.Mockito.doReturn(((java.lang.String) (null)), ((java.lang.String) (null))).when(pathItem).getPath();
        org.mockito.Mockito.doReturn(((java.net.URI) (null))).when(pathItem).getSource();
        org.mockito.Mockito.doReturn(false).when(pathItem).isFileBased();
        templatePaths.add(pathItem);
        io.quarkus.qute.deployment.EffectiveTemplatePathsBuildItem effectivePaths = new io.quarkus.qute.deployment.EffectiveTemplatePathsBuildItem(templatePaths);
        java.util.Optional<io.quarkus.qute.deployment.TemplateVariantsBuildItem> variants = java.util.Optional.empty();
        java.util.Vector<io.quarkus.qute.deployment.TemplateRootBuildItem> roots = new java.util.Vector<io.quarkus.qute.deployment.TemplateRootBuildItem>();
        io.quarkus.qute.deployment.TemplateRootsBuildItem templateRoots = processor.collectTemplateRoots(roots);
        java.util.ArrayList<io.quarkus.qute.deployment.TemplatePathExcludeBuildItem> excludes = new java.util.ArrayList<io.quarkus.qute.deployment.TemplatePathExcludeBuildItem>();
        // Undeclared exception!
        try {
            processor.initialize(((io.quarkus.deployment.annotations.BuildProducer<io.quarkus.arc.deployment.SyntheticBeanBuildItem>) (null)), recorder, effectivePaths, variants, templateRoots, excludes);
            org.junit.Assert.fail("Expecting exception: IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException e) {
            // 
            // The object is not a proxy returned from a recorder method: io.quarkus.qute.runtime.QuteRecorder$1@0000000009
            // 
            org.evosuite.runtime.EvoAssertions.verifyException("io.quarkus.arc.deployment.SyntheticBeanBuildItem$ExtendedBeanConfigurator", e);
        }
    }

    @org.junit.Test(timeout = 4000)
    public void collectTemplateContentsDoesNotProducePathsWhenNoAdapters() throws java.lang.Throwable {
        io.quarkus.qute.deployment.QuteProcessor processor = new io.quarkus.qute.deployment.QuteProcessor();
        org.jboss.jandex.IndexView[] empty = new org.jboss.jandex.IndexView[0];
        org.jboss.jandex.StackedIndex stackedIndex = org.jboss.jandex.StackedIndex.create(empty);
        java.util.HashSet<org.jboss.jandex.DotName> additional = new java.util.HashSet<org.jboss.jandex.DotName>(387);
        io.quarkus.arc.deployment.BeanArchiveIndexBuildItem beanArchiveIndex = new io.quarkus.arc.deployment.BeanArchiveIndexBuildItem(stackedIndex, stackedIndex, additional);
        java.util.Stack<io.quarkus.qute.deployment.CheckedTemplateAdapterBuildItem> adapters = new java.util.Stack<io.quarkus.qute.deployment.CheckedTemplateAdapterBuildItem>();
        java.lang.Class<io.quarkus.qute.deployment.TemplatePathBuildItem> pathClass = io.quarkus.qute.deployment.TemplatePathBuildItem.class;
        io.quarkus.builder.BuildContext buildContext = org.mockito.Mockito.mock(io.quarkus.builder.BuildContext.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        io.quarkus.deployment.BuildProducerImpl<io.quarkus.qute.deployment.TemplatePathBuildItem> pathProducer = new io.quarkus.deployment.BuildProducerImpl<io.quarkus.qute.deployment.TemplatePathBuildItem>(pathClass, buildContext);
        processor.collecTemplateContents(beanArchiveIndex, adapters, pathProducer);
        org.junit.Assert.assertTrue(adapters.isEmpty());
    }

    @org.junit.Test(timeout = 4000)
    public void excludeTypeChecksThrowsNPEWhenConfigReturnsNullOptional() throws java.lang.Throwable {
        io.quarkus.qute.deployment.QuteProcessor processor = new io.quarkus.qute.deployment.QuteProcessor();
        io.quarkus.qute.InsertSectionHelper.Factory factory = new io.quarkus.qute.InsertSectionHelper.Factory();
        java.util.List<java.lang.String> aliases = factory.getDefaultAliases();
        java.util.Optional.ofNullable(aliases);
        io.quarkus.qute.runtime.QuteConfig config = org.mockito.Mockito.mock(io.quarkus.qute.runtime.QuteConfig.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        org.mockito.Mockito.doReturn(((java.util.Optional) (null))).when(config).typeCheckExcludes();
        java.lang.Class<io.quarkus.qute.deployment.TypeCheckExcludeBuildItem> excludeClass = io.quarkus.qute.deployment.TypeCheckExcludeBuildItem.class;
        io.quarkus.builder.BuildContext buildContext = org.mockito.Mockito.mock(io.quarkus.builder.BuildContext.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        io.quarkus.deployment.BuildProducerImpl<io.quarkus.qute.deployment.TypeCheckExcludeBuildItem> excludeProducer = new io.quarkus.deployment.BuildProducerImpl<io.quarkus.qute.deployment.TypeCheckExcludeBuildItem>(excludeClass, buildContext);
        // Undeclared exception!
        try {
            processor.excludeTypeChecks(config, excludeProducer);
            org.junit.Assert.fail("Expecting exception: NullPointerException");
        } catch (java.lang.NullPointerException e) {
            // 
            // Cannot invoke \"java.util.Optional.isPresent()\" because the return value of \"io.quarkus.qute.runtime.QuteConfig.typeCheckExcludes()\" is null
            // 
            org.evosuite.runtime.EvoAssertions.verifyException("io.quarkus.qute.deployment.QuteProcessor", e);
        }
    }

    @org.junit.Test(timeout = 4000)
    public void collectTemplateVariantsThrowsNPEWhenSuffixesNullSecondCall() throws java.lang.Throwable {
        io.quarkus.qute.deployment.QuteProcessor processor = new io.quarkus.qute.deployment.QuteProcessor();
        io.quarkus.qute.deployment.TemplatePathBuildItem first = org.mockito.Mockito.mock(io.quarkus.qute.deployment.TemplatePathBuildItem.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        org.mockito.Mockito.doReturn("MW", "MW", "4uX5{").when(first).getPath();
        io.quarkus.qute.deployment.TemplatePathBuildItem second = org.mockito.Mockito.mock(io.quarkus.qute.deployment.TemplatePathBuildItem.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        org.mockito.Mockito.doReturn("\n", "MW", "4uX5{", "\n").when(second).getPath();
        java.util.List<io.quarkus.qute.deployment.TemplatePathBuildItem> paths = java.util.List.of(first, second, second, first, second, first, second);
        io.quarkus.qute.deployment.EffectiveTemplatePathsBuildItem effectivePaths = new io.quarkus.qute.deployment.EffectiveTemplatePathsBuildItem(paths);
        java.util.LinkedList<java.lang.String> suffixes = new java.util.LinkedList<java.lang.String>();
        suffixes.add("4uX5{");
        io.quarkus.qute.runtime.QuteConfig config = org.mockito.Mockito.mock(io.quarkus.qute.runtime.QuteConfig.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        org.mockito.Mockito.doReturn(suffixes, ((java.util.List) (null))).when(config).suffixes();
        // Undeclared exception!
        try {
            processor.collectTemplateVariants(effectivePaths, config);
            org.junit.Assert.fail("Expecting exception: NullPointerException");
        } catch (java.lang.NullPointerException e) {
            // 
            // Cannot invoke \"java.util.List.iterator()\" because the return value of \"io.quarkus.qute.runtime.QuteConfig.suffixes()\" is null
            // 
            org.evosuite.runtime.EvoAssertions.verifyException("io.quarkus.qute.deployment.QuteProcessor", e);
        }
    }

    @org.junit.Test(timeout = 4000)
    public void collectEngineConfigurationsHandlesEmptyIndex() throws java.lang.Throwable {
        org.jboss.jandex.EmptyIndex emptyIndex = org.jboss.jandex.EmptyIndex.INSTANCE;
        java.lang.Class<io.quarkus.qute.deployment.EngineConfigurationsBuildItem> configClass = io.quarkus.qute.deployment.EngineConfigurationsBuildItem.class;
        io.quarkus.deployment.BuildProducerImpl<io.quarkus.qute.deployment.EngineConfigurationsBuildItem> configProducer = new io.quarkus.deployment.BuildProducerImpl<io.quarkus.qute.deployment.EngineConfigurationsBuildItem>(configClass, ((io.quarkus.builder.BuildContext) (null)));
        io.quarkus.qute.deployment.QuteProcessor processor = new io.quarkus.qute.deployment.QuteProcessor();
        io.quarkus.arc.deployment.BeanArchiveIndexBuildItem beanArchiveIndex = new io.quarkus.arc.deployment.BeanArchiveIndexBuildItem(emptyIndex, emptyIndex, ((java.util.Set<org.jboss.jandex.DotName>) (null)));
        processor.collectEngineConfigurations(beanArchiveIndex, configProducer, ((io.quarkus.deployment.annotations.BuildProducer<io.quarkus.arc.deployment.ValidationPhaseBuildItem.ValidationErrorBuildItem>) (null)));
    }

    @org.junit.Test(timeout = 4000)
    public void validateAndCollectCustomTemplateLocatorLocationsReturnsResult() throws java.lang.Throwable {
        org.jboss.jandex.EmptyIndex emptyIndex = org.jboss.jandex.EmptyIndex.INSTANCE;
        io.quarkus.arc.deployment.BeanArchiveIndexBuildItem beanArchiveIndex = new io.quarkus.arc.deployment.BeanArchiveIndexBuildItem(emptyIndex, emptyIndex, ((java.util.Set<org.jboss.jandex.DotName>) (null)));
        java.lang.Class<io.quarkus.arc.deployment.ValidationPhaseBuildItem.ValidationErrorBuildItem> errorClass = ValidationPhaseBuildItem.ValidationErrorBuildItem.class;
        io.quarkus.deployment.BuildProducerImpl<io.quarkus.arc.deployment.ValidationPhaseBuildItem.ValidationErrorBuildItem> errorProducer = new io.quarkus.deployment.BuildProducerImpl<io.quarkus.arc.deployment.ValidationPhaseBuildItem.ValidationErrorBuildItem>(errorClass, ((io.quarkus.builder.BuildContext) (null)));
        io.quarkus.qute.deployment.QuteProcessor processor = new io.quarkus.qute.deployment.QuteProcessor();
        io.quarkus.qute.deployment.CustomTemplateLocatorPatternsBuildItem result = processor.validateAndCollectCustomTemplateLocatorLocations(beanArchiveIndex, errorProducer);
        org.junit.Assert.assertNotNull(result);
    }

    @org.junit.Test(timeout = 4000)
    public void validateTemplateInjectionPointsHandlesPrimitiveInjectionPoints() throws java.lang.Throwable {
        io.quarkus.qute.deployment.QuteProcessor processor = new io.quarkus.qute.deployment.QuteProcessor();
        java.util.Vector<io.quarkus.qute.deployment.TemplatePathBuildItem> paths = new java.util.Vector<io.quarkus.qute.deployment.TemplatePathBuildItem>();
        io.quarkus.qute.deployment.EffectiveTemplatePathsBuildItem effectivePaths = new io.quarkus.qute.deployment.EffectiveTemplatePathsBuildItem(paths);
        org.jboss.jandex.ClassType boxedByte = org.jboss.jandex.ClassType.BYTE_CLASS;
        org.jboss.jandex.PrimitiveType primitive = org.jboss.jandex.PrimitiveType.unbox(boxedByte);
        java.util.HashSet<org.jboss.jandex.AnnotationInstance> qualifiers = new java.util.HashSet<org.jboss.jandex.AnnotationInstance>();
        io.quarkus.arc.processor.InjectionPointInfo.TypeAndQualifiers tq = new io.quarkus.arc.processor.InjectionPointInfo.TypeAndQualifiers(primitive, qualifiers);
        io.quarkus.arc.processor.InjectionPointInfo ip = io.quarkus.arc.processor.InjectionPointInfo.fromSyntheticInjectionPoint(tq);
        java.util.List<io.quarkus.arc.processor.InjectionPointInfo> injectionPoints = java.util.List.of(ip, ip, ip, ip, ip);
        io.quarkus.arc.processor.BeanDeploymentValidator.ValidationContext validationContext = org.mockito.Mockito.mock(BeanDeploymentValidator.ValidationContext.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        org.mockito.Mockito.doReturn(injectionPoints).when(validationContext).getInjectionPoints();
        io.quarkus.arc.processor.BeanProcessor beanProcessor = org.mockito.Mockito.mock(io.quarkus.arc.processor.BeanProcessor.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        io.quarkus.arc.deployment.ValidationPhaseBuildItem validationPhase = new io.quarkus.arc.deployment.ValidationPhaseBuildItem(validationContext, beanProcessor);
        java.lang.Class<io.quarkus.arc.deployment.ValidationPhaseBuildItem.ValidationErrorBuildItem> errorClass = ValidationPhaseBuildItem.ValidationErrorBuildItem.class;
        io.quarkus.builder.BuildContext buildContext = org.mockito.Mockito.mock(io.quarkus.builder.BuildContext.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        io.quarkus.deployment.BuildProducerImpl<io.quarkus.arc.deployment.ValidationPhaseBuildItem.ValidationErrorBuildItem> errorProducer = new io.quarkus.deployment.BuildProducerImpl<io.quarkus.arc.deployment.ValidationPhaseBuildItem.ValidationErrorBuildItem>(errorClass, buildContext);
        io.quarkus.qute.deployment.CustomTemplateLocatorPatternsBuildItem patterns = new io.quarkus.qute.deployment.CustomTemplateLocatorPatternsBuildItem(((java.util.Collection<java.util.regex.Pattern>) (null)));
        processor.validateTemplateInjectionPoints(((io.quarkus.qute.deployment.TemplateFilePathsBuildItem) (null)), effectivePaths, validationPhase, errorProducer, patterns);
    }

    @org.junit.Test(timeout = 4000)
    public void collectTemplateFilePathsThrowsSIOOBEOnInvalidPathSuffix() throws java.lang.Throwable {
        io.quarkus.qute.deployment.QuteProcessor processor = new io.quarkus.qute.deployment.QuteProcessor();
        java.util.Stack<java.lang.String> suffixStack = new java.util.Stack<java.lang.String>();
        java.lang.String[] resourcesArray = new java.lang.String[5];
        resourcesArray[0] = "=\"4)9\u0001B-PDT'n";
        resourcesArray[1] = "index";
        resourcesArray[2] = "W";
        resourcesArray[3] = "p~sN^/ MOY%_Pxqinsert";
        io.quarkus.deployment.builditem.nativeimage.NativeImageResourceBuildItem nativeResources = new io.quarkus.deployment.builditem.nativeimage.NativeImageResourceBuildItem(resourcesArray);
        java.util.List<java.lang.String> resources = nativeResources.getResources();
        io.quarkus.qute.runtime.QuteConfig config = org.mockito.Mockito.mock(io.quarkus.qute.runtime.QuteConfig.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        org.mockito.Mockito.doReturn(suffixStack, resources).when(config).suffixes();
        io.quarkus.qute.deployment.TemplatePathBuildItem pathItem = org.mockito.Mockito.mock(io.quarkus.qute.deployment.TemplatePathBuildItem.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        org.mockito.Mockito.doReturn("_", "p~sN^/ MOY%_Pxqinsert").when(pathItem).getPath();
        java.util.List<io.quarkus.qute.deployment.TemplatePathBuildItem> paths = java.util.List.of(pathItem, pathItem);
        io.quarkus.qute.deployment.EffectiveTemplatePathsBuildItem effectivePaths = new io.quarkus.qute.deployment.EffectiveTemplatePathsBuildItem(paths);
        // Undeclared exception!
        try {
            processor.collectTemplateFilePaths(config, effectivePaths);
            org.junit.Assert.fail("Expecting exception: StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException e) {
        }
    }

    @org.junit.Test(timeout = 4000)
    public void collectTemplatesThrowsNPEWhenApplicationModelNull() throws java.lang.Throwable {
        io.quarkus.deployment.pkg.builditem.CurateOutcomeBuildItem curateOutcome = new io.quarkus.deployment.pkg.builditem.CurateOutcomeBuildItem(((io.quarkus.bootstrap.model.ApplicationModel) (null)));
        java.util.Stack<io.quarkus.qute.deployment.TemplatePathExcludeBuildItem> excludes = new java.util.Stack<io.quarkus.qute.deployment.TemplatePathExcludeBuildItem>();
        java.lang.Class<io.quarkus.deployment.builditem.HotDeploymentWatchedFileBuildItem> watchedClass = io.quarkus.deployment.builditem.HotDeploymentWatchedFileBuildItem.class;
        io.quarkus.builder.BuildContext buildContext = org.mockito.Mockito.mock(io.quarkus.builder.BuildContext.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        io.quarkus.deployment.BuildProducerImpl<io.quarkus.deployment.builditem.HotDeploymentWatchedFileBuildItem> watchedProducer = new io.quarkus.deployment.BuildProducerImpl<io.quarkus.deployment.builditem.HotDeploymentWatchedFileBuildItem>(watchedClass, buildContext);
        org.aesh.command.impl.registry.MutableCommandRegistryImpl<org.aesh.command.impl.invocation.DefaultCommandInvocation> cmdRegistry = new org.aesh.command.impl.registry.MutableCommandRegistryImpl<org.aesh.command.impl.invocation.DefaultCommandInvocation>();
        java.util.Set<java.lang.String> commandNames = cmdRegistry.getAllCommandNames();
        io.quarkus.qute.deployment.TemplateRootsBuildItem templateRoots = new io.quarkus.qute.deployment.TemplateRootsBuildItem(commandNames);
        io.quarkus.runtime.LaunchMode mode = io.quarkus.runtime.LaunchMode.RUN;
        io.quarkus.deployment.builditem.LaunchModeBuildItem launchItem = new io.quarkus.deployment.builditem.LaunchModeBuildItem(mode, ((java.util.Optional<io.quarkus.dev.spi.DevModeType>) (null)), false, ((java.util.Optional<io.quarkus.dev.spi.DevModeType>) (null)), true);
        io.quarkus.qute.deployment.QuteProcessor processor = new io.quarkus.qute.deployment.QuteProcessor();
        java.util.concurrent.PriorityBlockingQueue<org.jboss.jandex.IndexView> views = new java.util.concurrent.PriorityBlockingQueue<org.jboss.jandex.IndexView>();
        org.jboss.jandex.CompositeIndex compositeIndex = org.jboss.jandex.CompositeIndex.create(((java.util.Collection<org.jboss.jandex.IndexView>) (views)));
        io.quarkus.paths.DirectoryPathTree pathTree = new io.quarkus.paths.DirectoryPathTree();
        io.quarkus.maven.dependency.ResolvedDependencyBuilder dep = io.quarkus.maven.dependency.ResolvedDependencyBuilder.newInstance();
        io.quarkus.deployment.ApplicationArchiveImpl rootArchive = new io.quarkus.deployment.ApplicationArchiveImpl(compositeIndex, pathTree, dep);
        java.util.Vector<io.quarkus.deployment.ApplicationArchiveImpl> archives = new java.util.Vector<io.quarkus.deployment.ApplicationArchiveImpl>();
        java.util.PriorityQueue<io.quarkus.deployment.ApplicationArchive> allArchives = new java.util.PriorityQueue<io.quarkus.deployment.ApplicationArchive>(archives);
        io.quarkus.deployment.builditem.ApplicationArchivesBuildItem appArchives = new io.quarkus.deployment.builditem.ApplicationArchivesBuildItem(rootArchive, allArchives);
        java.lang.Class<io.quarkus.qute.deployment.TemplatePathBuildItem> pathClass = io.quarkus.qute.deployment.TemplatePathBuildItem.class;
        io.quarkus.deployment.BuildProducerImpl<io.quarkus.qute.deployment.TemplatePathBuildItem> pathProducer = new io.quarkus.deployment.BuildProducerImpl<io.quarkus.qute.deployment.TemplatePathBuildItem>(pathClass, ((io.quarkus.builder.BuildContext) (null)));
        io.quarkus.qute.runtime.QuteConfig config = org.mockito.Mockito.mock(io.quarkus.qute.runtime.QuteConfig.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        org.mockito.Mockito.doReturn(((java.util.regex.Pattern) (null))).when(config).templatePathExclude();
        // Undeclared exception!
        try {
            processor.collectTemplates(appArchives, curateOutcome, excludes, watchedProducer, pathProducer, ((io.quarkus.deployment.annotations.BuildProducer<io.quarkus.deployment.builditem.nativeimage.NativeImageResourceBuildItem>) (null)), config, templateRoots, launchItem);
            org.junit.Assert.fail("Expecting exception: NullPointerException");
        } catch (java.lang.NullPointerException e) {
            // 
            // Cannot invoke \"io.quarkus.bootstrap.model.ApplicationModel.getDependencies(int)\" because \"applicationModel\" is null
            // 
            org.evosuite.runtime.EvoAssertions.verifyException("io.quarkus.qute.deployment.QuteProcessor", e);
        }
    }

    @org.junit.Test(timeout = 4000)
    public void collectTemplatesThrowsNPEWhenApplicationArchivesNull() throws java.lang.Throwable {
        io.quarkus.runtime.LaunchMode mode = io.quarkus.runtime.LaunchMode.DEVELOPMENT;
        io.quarkus.dev.spi.DevModeType devMode = io.quarkus.dev.spi.DevModeType.LOCAL;
        java.util.Optional<io.quarkus.dev.spi.DevModeType> maybeDev = java.util.Optional.ofNullable(devMode);
        io.quarkus.deployment.builditem.LaunchModeBuildItem launchItem = new io.quarkus.deployment.builditem.LaunchModeBuildItem(mode, maybeDev, false, maybeDev, false);
        java.util.Vector<io.quarkus.qute.deployment.TemplatePathExcludeBuildItem> excludes = new java.util.Vector<io.quarkus.qute.deployment.TemplatePathExcludeBuildItem>(3, 1452);
        java.lang.Class<io.quarkus.qute.deployment.TemplatePathBuildItem> pathClass = io.quarkus.qute.deployment.TemplatePathBuildItem.class;
        io.quarkus.builder.BuildContext buildContext = org.mockito.Mockito.mock(io.quarkus.builder.BuildContext.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        io.quarkus.deployment.BuildProducerImpl<io.quarkus.qute.deployment.TemplatePathBuildItem> pathProducer = new io.quarkus.deployment.BuildProducerImpl<io.quarkus.qute.deployment.TemplatePathBuildItem>(pathClass, buildContext);
        io.quarkus.qute.deployment.QuteProcessor processor = new io.quarkus.qute.deployment.QuteProcessor();
        io.quarkus.deployment.pkg.builditem.CurateOutcomeBuildItem curateOutcome = new io.quarkus.deployment.pkg.builditem.CurateOutcomeBuildItem(((io.quarkus.bootstrap.model.ApplicationModel) (null)));
        java.lang.Class<io.quarkus.deployment.builditem.HotDeploymentWatchedFileBuildItem> watchedClass = io.quarkus.deployment.builditem.HotDeploymentWatchedFileBuildItem.class;
        io.quarkus.builder.BuildContext buildContext1 = org.mockito.Mockito.mock(io.quarkus.builder.BuildContext.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        io.quarkus.deployment.BuildProducerImpl<io.quarkus.deployment.builditem.HotDeploymentWatchedFileBuildItem> watchedProducer = new io.quarkus.deployment.BuildProducerImpl<io.quarkus.deployment.builditem.HotDeploymentWatchedFileBuildItem>(watchedClass, buildContext1);
        io.quarkus.qute.runtime.QuteConfig config = org.mockito.Mockito.mock(io.quarkus.qute.runtime.QuteConfig.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        org.mockito.Mockito.doReturn(((java.util.regex.Pattern) (null))).when(config).templatePathExclude();
        // Undeclared exception!
        try {
            processor.collectTemplates(((io.quarkus.deployment.builditem.ApplicationArchivesBuildItem) (null)), curateOutcome, excludes, watchedProducer, pathProducer, ((io.quarkus.deployment.annotations.BuildProducer<io.quarkus.deployment.builditem.nativeimage.NativeImageResourceBuildItem>) (null)), config, ((io.quarkus.qute.deployment.TemplateRootsBuildItem) (null)), launchItem);
            org.junit.Assert.fail("Expecting exception: NullPointerException");
        } catch (java.lang.NullPointerException e) {
            // 
            // Cannot invoke \"io.quarkus.deployment.builditem.ApplicationArchivesBuildItem.getAllApplicationArchives()\" because \"applicationArchives\" is null
            // 
            org.evosuite.runtime.EvoAssertions.verifyException("io.quarkus.qute.deployment.QuteProcessor", e);
        }
    }

    @org.junit.Test(timeout = 4000)
    public void existingValueResolversAddGlobalRecordsClassName() throws java.lang.Throwable {
        io.quarkus.qute.deployment.Types.AssignabilityCheck assignability = new io.quarkus.qute.deployment.Types.AssignabilityCheck(((org.jboss.jandex.IndexView) (null)));
        org.jboss.jandex.DotName location = QuteProcessor.LOCATION;
        io.quarkus.qute.deployment.QuteProcessor.ExistingValueResolvers resolvers = new io.quarkus.qute.deployment.QuteProcessor.ExistingValueResolvers();
        java.util.function.Predicate<java.lang.Object> notSame = java.util.function.Predicate.isEqual(assignability);
        java.util.function.Predicate<org.jboss.jandex.DotName> predicate = java.util.function.Predicate.not(notSame);
        resolvers.addGlobal(location, "W", predicate);
        org.junit.Assert.assertFalse(location.isInner());
    }

    @org.junit.Test(timeout = 4000)
    public void existingValueResolversAddGlobalIdempotent() throws java.lang.Throwable {
        io.quarkus.qute.deployment.QuteProcessor.ExistingValueResolvers resolvers = new io.quarkus.qute.deployment.QuteProcessor.ExistingValueResolvers();
        org.jboss.jandex.DotName integerName = org.jboss.jandex.DotName.INTEGER_CLASS_NAME;
        io.quarkus.deployment.dev.AlwaysFalsePredicate<org.jboss.jandex.DotName> alwaysFalse = new io.quarkus.deployment.dev.AlwaysFalsePredicate<org.jboss.jandex.DotName>();
        resolvers.addGlobal(integerName, "Component type not supported: ", alwaysFalse);
        resolvers.addGlobal(integerName, "Component type not supported: ", alwaysFalse);
        org.junit.Assert.assertTrue(integerName.isComponentized());
    }

    @org.junit.Test(timeout = 4000)
    public void existingValueResolversAddExtensionMethodRecordsMethod() throws java.lang.Throwable {
        java.lang.Class<java.lang.Object> objectClass = java.lang.Object.class;
        org.jboss.jandex.ClassInfo classInfo = org.jboss.jandex.Index.singleClass(objectClass);
        org.jboss.jandex.TypeVariable returnType = org.jboss.jandex.TypeVariable.create("IN78 +@gT{<^b8");
        java.lang.String[] params = new java.lang.String[8];
        params[0] = "IN78 +@gT{<^b8";
        params[1] = "IN78 +@gT{<^b8";
        params[2] = "IN78 +@gT{<^b8";
        params[3] = "IN78 +@gT{<^b8";
        params[4] = "IN78 +@gT{<^b8";
        params[5] = "IN78 +@gT{<^b8";
        params[6] = "IN78 +@gT{<^b8";
        params[7] = "IN78 +@gT{<^b8";
        org.jboss.jandex.Type[] paramTypes = new org.jboss.jandex.Type[1];
        org.jboss.jandex.TypeVariable[] typeParams = new org.jboss.jandex.TypeVariable[1];
        org.jboss.jandex.MethodInfo method = org.jboss.jandex.MethodInfo.create(classInfo, "IN78 +@gT{<^b8", params, paramTypes, ((org.jboss.jandex.Type) (returnType)), ((short) (3038)), typeParams, paramTypes);
        io.quarkus.qute.deployment.QuteProcessor.ExistingValueResolvers resolvers = new io.quarkus.qute.deployment.QuteProcessor.ExistingValueResolvers();
        io.quarkus.deployment.dev.AlwaysFalsePredicate<java.lang.Object> alwaysFalse = new io.quarkus.deployment.dev.AlwaysFalsePredicate<java.lang.Object>();
        java.util.function.Predicate<org.jboss.jandex.DotName> predicate = java.util.function.Predicate.not(alwaysFalse);
        resolvers.add(method, ",+&w`b@4a)", predicate);
        org.junit.Assert.assertTrue(method.isDeclaration());
    }

    @org.junit.Test(timeout = 4000)
    public void generateValueResolversHandlesNullConfigAndKeepsIncorrectExpressions() throws java.lang.Throwable {
        org.jboss.jandex.EmptyIndex emptyIndex = org.jboss.jandex.EmptyIndex.INSTANCE;
        io.quarkus.qute.deployment.QuteProcessor processor = new io.quarkus.qute.deployment.QuteProcessor();
        java.util.ArrayList<org.jboss.jandex.IndexView> views = new java.util.ArrayList<org.jboss.jandex.IndexView>();
        org.jboss.jandex.CompositeIndex composite = org.jboss.jandex.CompositeIndex.create(((java.util.Collection<org.jboss.jandex.IndexView>) (views)));
        java.util.TreeSet<org.jboss.jandex.DotName> subpackages = new java.util.TreeSet<org.jboss.jandex.DotName>();
        io.quarkus.arc.deployment.BeanArchiveIndexBuildItem beanArchiveIndex = new io.quarkus.arc.deployment.BeanArchiveIndexBuildItem(composite, emptyIndex, subpackages);
        io.quarkus.paths.EmptyPathTree emptyTree = new io.quarkus.paths.EmptyPathTree();
        io.quarkus.maven.dependency.ResolvedDependencyBuilder dep = io.quarkus.maven.dependency.ResolvedDependencyBuilder.newInstance();
        io.quarkus.deployment.ApplicationArchiveImpl rootArchive = new io.quarkus.deployment.ApplicationArchiveImpl(emptyIndex, emptyTree, dep);
        java.util.HashSet<io.quarkus.deployment.ApplicationArchive> archives = new java.util.HashSet<io.quarkus.deployment.ApplicationArchive>();
        io.quarkus.deployment.builditem.ApplicationArchivesBuildItem appArchives = new io.quarkus.deployment.builditem.ApplicationArchivesBuildItem(rootArchive, archives);
        java.util.Vector<io.quarkus.qute.deployment.TemplateExtensionMethodBuildItem> extensionMethods = new java.util.Vector<io.quarkus.qute.deployment.TemplateExtensionMethodBuildItem>();
        java.util.LinkedList<io.quarkus.qute.deployment.ImplicitValueResolverBuildItem> implicitResolvers = new java.util.LinkedList<io.quarkus.qute.deployment.ImplicitValueResolverBuildItem>();
        java.util.LinkedList<io.quarkus.qute.deployment.TemplatesAnalysisBuildItem.TemplateAnalysis> analysesList = new java.util.LinkedList<io.quarkus.qute.deployment.TemplatesAnalysisBuildItem.TemplateAnalysis>();
        io.quarkus.qute.deployment.TemplatesAnalysisBuildItem analyses = new io.quarkus.qute.deployment.TemplatesAnalysisBuildItem(analysesList);
        java.util.ArrayList<io.quarkus.panache.common.deployment.PanacheEntityClassesBuildItem> panache = new java.util.ArrayList<io.quarkus.panache.common.deployment.PanacheEntityClassesBuildItem>();
        java.util.Vector<io.quarkus.qute.deployment.TemplateGlobalBuildItem> globals = new java.util.Vector<io.quarkus.qute.deployment.TemplateGlobalBuildItem>();
        java.util.Vector<io.quarkus.qute.deployment.IncorrectExpressionBuildItem> incorrect = new java.util.Vector<io.quarkus.qute.deployment.IncorrectExpressionBuildItem>();
        io.quarkus.qute.deployment.IncorrectExpressionBuildItem incorrectItem = new io.quarkus.qute.deployment.IncorrectExpressionBuildItem("case", "_4E?!0f8$1>^*AVsC", "case", ((io.quarkus.qute.TemplateNode.Origin) (null)));
        incorrect.add(incorrectItem);
        io.quarkus.deployment.builditem.LiveReloadBuildItem liveReload = new io.quarkus.deployment.builditem.LiveReloadBuildItem();
        java.lang.Class<io.quarkus.deployment.builditem.GeneratedClassBuildItem> generatedClass = io.quarkus.deployment.builditem.GeneratedClassBuildItem.class;
        io.quarkus.builder.BuildContext buildContext = org.mockito.Mockito.mock(io.quarkus.builder.BuildContext.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        io.quarkus.deployment.BuildProducerImpl<io.quarkus.deployment.builditem.GeneratedClassBuildItem> generatedClassProducer = new io.quarkus.deployment.BuildProducerImpl<io.quarkus.deployment.builditem.GeneratedClassBuildItem>(generatedClass, buildContext);
        io.quarkus.deployment.annotations.BuildProducer<io.quarkus.deployment.builditem.GeneratedResourceBuildItem> generatedResourceProducer = ((io.quarkus.deployment.annotations.BuildProducer<io.quarkus.deployment.builditem.GeneratedResourceBuildItem>) (org.mockito.Mockito.mock(io.quarkus.deployment.annotations.BuildProducer.class, new org.evosuite.runtime.ViolatedAssumptionAnswer())));
        java.util.ArrayList<io.quarkus.qute.deployment.TemplateDataBuildItem> templateData = new java.util.ArrayList<io.quarkus.qute.deployment.TemplateDataBuildItem>();
        io.quarkus.arc.deployment.CompletedApplicationClassPredicateBuildItem completedPredicate = org.mockito.Mockito.mock(io.quarkus.arc.deployment.CompletedApplicationClassPredicateBuildItem.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        io.quarkus.deployment.annotations.BuildProducer<io.quarkus.qute.deployment.GeneratedValueResolverBuildItem> valueResolverProducer = ((io.quarkus.deployment.annotations.BuildProducer<io.quarkus.qute.deployment.GeneratedValueResolverBuildItem>) (org.mockito.Mockito.mock(io.quarkus.deployment.annotations.BuildProducer.class, new org.evosuite.runtime.ViolatedAssumptionAnswer())));
        io.quarkus.deployment.annotations.BuildProducer<io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem> reflectiveProducer = ((io.quarkus.deployment.annotations.BuildProducer<io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem>) (org.mockito.Mockito.mock(io.quarkus.deployment.annotations.BuildProducer.class, new org.evosuite.runtime.ViolatedAssumptionAnswer())));
        processor.generateValueResolvers(((io.quarkus.qute.runtime.QuteConfig) (null)), generatedClassProducer, generatedResourceProducer, beanArchiveIndex, appArchives, extensionMethods, implicitResolvers, analyses, panache, templateData, globals, incorrect, liveReload, completedPredicate, valueResolverProducer, reflectiveProducer, ((io.quarkus.deployment.annotations.BuildProducer<io.quarkus.qute.deployment.TemplateGlobalProviderBuildItem>) (null)));
        org.junit.Assert.assertTrue(incorrect.contains(incorrectItem));
    }

    @org.junit.Test(timeout = 4000)
    public void processTemplateErrorsThrowsNPEWhenTemplateAnalysisNull() throws java.lang.Throwable {
        java.util.Stack<io.quarkus.qute.deployment.TemplatesAnalysisBuildItem.TemplateAnalysis> analysesList = new java.util.Stack<io.quarkus.qute.deployment.TemplatesAnalysisBuildItem.TemplateAnalysis>();
        analysesList.setSize(982);
        io.quarkus.qute.TemplateNode.Origin origin = org.mockito.Mockito.mock(TemplateNode.Origin.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        org.mockito.Mockito.doReturn("name").when(origin).getTemplateGeneratedId();
        io.quarkus.qute.deployment.IncorrectExpressionBuildItem incorrect = new io.quarkus.qute.deployment.IncorrectExpressionBuildItem("name", "name", origin);
        java.util.List<io.quarkus.qute.deployment.IncorrectExpressionBuildItem> incorrectList = java.util.List.of(incorrect, incorrect, incorrect, incorrect, incorrect, incorrect, incorrect, incorrect, incorrect);
        java.lang.Class<io.quarkus.deployment.builditem.ServiceStartBuildItem> serviceStartClass = io.quarkus.deployment.builditem.ServiceStartBuildItem.class;
        io.quarkus.builder.BuildContext buildContext = org.mockito.Mockito.mock(io.quarkus.builder.BuildContext.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        io.quarkus.deployment.BuildProducerImpl<io.quarkus.deployment.builditem.ServiceStartBuildItem> serviceStartProducer = new io.quarkus.deployment.BuildProducerImpl<io.quarkus.deployment.builditem.ServiceStartBuildItem>(serviceStartClass, buildContext);
        io.quarkus.qute.deployment.TemplatesAnalysisBuildItem analyses = new io.quarkus.qute.deployment.TemplatesAnalysisBuildItem(analysesList);
        io.quarkus.qute.deployment.QuteProcessor processor = new io.quarkus.qute.deployment.QuteProcessor();
        // Undeclared exception!
        try {
            processor.processTemplateErrors(analyses, incorrectList, serviceStartProducer);
            org.junit.Assert.fail("Expecting exception: NullPointerException");
        } catch (java.lang.NullPointerException e) {
            // 
            // Cannot read field \"generatedId\" because \"templateAnalysis\" is null
            // 
            org.evosuite.runtime.EvoAssertions.verifyException("io.quarkus.qute.deployment.QuteProcessor", e);
        }
    }

    @org.junit.Test(timeout = 4000)
    public void enumConstantFilterReturnsFalseForNonEnum() throws java.lang.Throwable {
        java.lang.Class<java.lang.Object> objectClass = java.lang.Object.class;
        org.jboss.jandex.ClassInfo classInfo = org.jboss.jandex.Index.singleClass(objectClass);
        boolean result = io.quarkus.qute.deployment.QuteProcessor.enumConstantFilter(classInfo);
        org.junit.Assert.assertFalse(result);
    }

    @org.junit.Test(timeout = 4000)
    public void staticsFilterOnClassInfoThrowsIAE() throws java.lang.Throwable {
        java.lang.Class<java.lang.Object> objectClass = java.lang.Object.class;
        org.jboss.jandex.ClassInfo classInfo = org.jboss.jandex.Index.singleClass(objectClass);
        // Undeclared exception!
        try {
            io.quarkus.qute.deployment.QuteProcessor.staticsFilter(classInfo);
            org.junit.Assert.fail("Expecting exception: IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException e) {
            // 
            // no message in exception (getMessage() returned null)
            // 
            org.evosuite.runtime.EvoAssertions.verifyException("io.quarkus.qute.deployment.QuteProcessor", e);
        }
    }

    @org.junit.Test(timeout = 4000)
    public void staticsFilterReturnsTrueForStaticMethod() throws java.lang.Throwable {
        java.lang.Class<java.lang.Object> objectClass = java.lang.Object.class;
        org.jboss.jandex.ClassInfo classInfo = org.jboss.jandex.Index.singleClass(objectClass);
        org.jboss.jandex.TypeVariable returnType = org.jboss.jandex.TypeVariable.create("kW[?,rWgEiME88{else");
        java.lang.String[] params = new java.lang.String[1];
        params[0] = "kW[?,rWgEiME88{else";
        org.jboss.jandex.Type[] paramTypes = new org.jboss.jandex.Type[0];
        org.jboss.jandex.TypeVariable[] typeParams = new org.jboss.jandex.TypeVariable[5];
        org.jboss.jandex.MethodInfo method = org.jboss.jandex.MethodInfo.create(classInfo, "kW[?,rWgEiME88{else", params, paramTypes, ((org.jboss.jandex.Type) (returnType)), ((short) (-22438)), typeParams, paramTypes);
        boolean accept = io.quarkus.qute.deployment.QuteProcessor.staticsFilter(method);
        org.junit.Assert.assertTrue(accept);
    }

    @org.junit.Test(timeout = 4000)
    public void defaultFilterReturnsFalseForNonMatchingMethod() throws java.lang.Throwable {
        java.lang.Class<java.lang.Object> objectClass = java.lang.Object.class;
        org.jboss.jandex.ClassInfo classInfo = org.jboss.jandex.Index.singleClass(objectClass);
        org.jboss.jandex.TypeVariable returnType = org.jboss.jandex.TypeVariable.create("k,rWgE)MEP8{else");
        java.lang.String[] params = new java.lang.String[1];
        params[0] = "k,rWgE)MEP8{else";
        org.jboss.jandex.Type[] paramTypes = new org.jboss.jandex.Type[0];
        org.jboss.jandex.TypeVariable[] typeParams = new org.jboss.jandex.TypeVariable[5];
        org.jboss.jandex.MethodInfo method = org.jboss.jandex.MethodInfo.create(classInfo, "k,rWgE)MEP8{else", params, paramTypes, ((org.jboss.jandex.Type) (returnType)), ((short) (-22428)), typeParams, paramTypes);
        boolean accept = io.quarkus.qute.deployment.QuteProcessor.defaultFilter(method);
        org.junit.Assert.assertFalse(accept);
    }

    @org.junit.Test(timeout = 4000)
    public void defaultFilterOnClassInfoThrowsIAE() throws java.lang.Throwable {
        java.lang.Class<java.lang.Object> objectClass = java.lang.Object.class;
        org.jboss.jandex.ClassInfo classInfo = org.jboss.jandex.Index.singleClass(objectClass);
        // Undeclared exception!
        try {
            io.quarkus.qute.deployment.QuteProcessor.defaultFilter(classInfo);
            org.junit.Assert.fail("Expecting exception: IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException e) {
            // 
            // no message in exception (getMessage() returned null)
            // 
            org.evosuite.runtime.EvoAssertions.verifyException("io.quarkus.qute.deployment.QuteProcessor", e);
        }
    }

    @org.junit.Test(timeout = 4000)
    public void collectTemplateExtensionMethodsHandlesEmptyIndex() throws java.lang.Throwable {
        io.quarkus.qute.deployment.QuteProcessor processor = new io.quarkus.qute.deployment.QuteProcessor();
        java.util.HashSet<org.jboss.jandex.DotName> additional = new java.util.HashSet<org.jboss.jandex.DotName>();
        org.jboss.jandex.IndexView[] single = new org.jboss.jandex.IndexView[1];
        org.jboss.jandex.EmptyIndex emptyIndex = org.jboss.jandex.EmptyIndex.INSTANCE;
        single[0] = ((org.jboss.jandex.IndexView) (emptyIndex));
        org.jboss.jandex.CompositeIndex composite = org.jboss.jandex.CompositeIndex.create(single);
        io.quarkus.arc.deployment.BeanArchiveIndexBuildItem beanArchiveIndex = new io.quarkus.arc.deployment.BeanArchiveIndexBuildItem(composite, emptyIndex, additional);
        java.lang.Class<io.quarkus.qute.deployment.TemplateExtensionMethodBuildItem> extensionClass = io.quarkus.qute.deployment.TemplateExtensionMethodBuildItem.class;
        io.quarkus.deployment.BuildProducerImpl<io.quarkus.qute.deployment.TemplateExtensionMethodBuildItem> extensionProducer = new io.quarkus.deployment.BuildProducerImpl<io.quarkus.qute.deployment.TemplateExtensionMethodBuildItem>(extensionClass, ((io.quarkus.builder.BuildContext) (null)));
        processor.collectTemplateExtensionMethods(beanArchiveIndex, extensionProducer);
    }

    @org.junit.Test(timeout = 4000)
    public void buildIgnorePatternThrowsIAEOnEmptyInput() throws java.lang.Throwable {
        java.util.ArrayList<java.lang.String> empty = new java.util.ArrayList<java.lang.String>();
        // Undeclared exception!
        try {
            io.quarkus.qute.deployment.QuteProcessor.buildIgnorePattern(empty);
            org.junit.Assert.fail("Expecting exception: IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException e) {
            // 
            // no message in exception (getMessage() returned null)
            // 
            org.evosuite.runtime.EvoAssertions.verifyException("io.quarkus.qute.deployment.QuteProcessor", e);
        }
    }

    @org.junit.Test(timeout = 4000)
    public void buildIgnorePatternReturnsStringNotInInput() throws java.lang.Throwable {
        java.util.Set<java.lang.String> ids = java.time.ZoneId.getAvailableZoneIds();
        java.lang.String pattern = io.quarkus.qute.deployment.QuteProcessor.buildIgnorePattern(ids);
        org.junit.Assert.assertFalse(ids.contains(pattern));
    }

    @org.junit.Test(timeout = 4000)
    public void validateExpressionsWithEmptyBeansNoErrors() throws java.lang.Throwable {
        io.quarkus.qute.deployment.QuteProcessor processor = new io.quarkus.qute.deployment.QuteProcessor();
        java.util.Stack<io.quarkus.qute.deployment.TemplatesAnalysisBuildItem.TemplateAnalysis> analysesList = new java.util.Stack<io.quarkus.qute.deployment.TemplatesAnalysisBuildItem.TemplateAnalysis>();
        io.quarkus.qute.deployment.TemplatesAnalysisBuildItem analyses = new io.quarkus.qute.deployment.TemplatesAnalysisBuildItem(analysesList);
        java.lang.Class<java.lang.Object>[] classes = ((java.lang.Class<java.lang.Object>[]) (java.lang.reflect.Array.newInstance(java.lang.Class.class, 0)));
        org.jboss.jandex.Index index = org.jboss.jandex.Index.of(classes);
        java.util.HashSet<org.jboss.jandex.DotName> additional = new java.util.HashSet<org.jboss.jandex.DotName>();
        io.quarkus.arc.deployment.BeanArchiveIndexBuildItem beanArchiveIndex = new io.quarkus.arc.deployment.BeanArchiveIndexBuildItem(index, index, additional);
        java.util.Stack<io.quarkus.qute.deployment.TemplateExtensionMethodBuildItem> extensionMethods = new java.util.Stack<io.quarkus.qute.deployment.TemplateExtensionMethodBuildItem>();
        java.util.function.Predicate<io.quarkus.qute.deployment.TypeCheckExcludeBuildItem.TypeCheck> predicate = java.util.function.Predicate.isEqual(extensionMethods);
        io.quarkus.qute.deployment.TypeCheckExcludeBuildItem exclude = new io.quarkus.qute.deployment.TypeCheckExcludeBuildItem(predicate);
        java.util.List<io.quarkus.qute.deployment.TypeCheckExcludeBuildItem> excludes = java.util.List.of(exclude, exclude, exclude, exclude, exclude);
        java.lang.Class<io.quarkus.qute.deployment.IncorrectExpressionBuildItem> incorrectClass = io.quarkus.qute.deployment.IncorrectExpressionBuildItem.class;
        io.quarkus.builder.BuildContext buildContext = org.mockito.Mockito.mock(io.quarkus.builder.BuildContext.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        io.quarkus.deployment.BuildProducerImpl<io.quarkus.qute.deployment.IncorrectExpressionBuildItem> incorrectProducer = new io.quarkus.deployment.BuildProducerImpl<io.quarkus.qute.deployment.IncorrectExpressionBuildItem>(incorrectClass, buildContext);
        io.quarkus.deployment.annotations.BuildProducer<io.quarkus.qute.deployment.ImplicitValueResolverBuildItem> implicitProducer = ((io.quarkus.deployment.annotations.BuildProducer<io.quarkus.qute.deployment.ImplicitValueResolverBuildItem>) (org.mockito.Mockito.mock(io.quarkus.deployment.annotations.BuildProducer.class, new org.evosuite.runtime.ViolatedAssumptionAnswer())));
        java.util.PriorityQueue<io.quarkus.arc.processor.BeanInfo> beans = new java.util.PriorityQueue<io.quarkus.arc.processor.BeanInfo>();
        java.util.LinkedHashSet<io.quarkus.arc.processor.InjectionPointInfo> injectionPoints = new java.util.LinkedHashSet<io.quarkus.arc.processor.InjectionPointInfo>();
        java.util.PriorityQueue<io.quarkus.arc.processor.ObserverInfo> observers = new java.util.PriorityQueue<io.quarkus.arc.processor.ObserverInfo>();
        io.quarkus.arc.processor.BeanDeployment deployment = org.mockito.Mockito.mock(io.quarkus.arc.processor.BeanDeployment.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        org.mockito.Mockito.doReturn(((io.quarkus.arc.processor.BeanResolver) (null))).when(deployment).getBeanResolver();
        org.mockito.Mockito.doReturn(beans).when(deployment).getBeans();
        org.mockito.Mockito.doReturn(injectionPoints).when(deployment).getInjectionPoints();
        org.mockito.Mockito.doReturn(observers).when(deployment).getObservers();
        io.quarkus.arc.deployment.SynthesisFinishedBuildItem synthesis = new io.quarkus.arc.deployment.SynthesisFinishedBuildItem(deployment);
        java.util.ArrayList<io.quarkus.qute.deployment.CheckedTemplateBuildItem> checkedTemplates = new java.util.ArrayList<io.quarkus.qute.deployment.CheckedTemplateBuildItem>();
        java.util.Stack<io.quarkus.qute.deployment.TemplateDataBuildItem> templateData = new java.util.Stack<io.quarkus.qute.deployment.TemplateDataBuildItem>();
        io.quarkus.qute.runtime.QuteConfig config = org.mockito.Mockito.mock(io.quarkus.qute.runtime.QuteConfig.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        io.quarkus.deployment.pkg.NativeConfig nativeConfig = org.mockito.Mockito.mock(io.quarkus.deployment.pkg.NativeConfig.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        org.mockito.Mockito.doReturn(false).when(nativeConfig).enabled();
        java.util.Vector<io.quarkus.qute.deployment.TemplateGlobalBuildItem> globals = new java.util.Vector<io.quarkus.qute.deployment.TemplateGlobalBuildItem>();
        processor.validateExpressions(analyses, beanArchiveIndex, extensionMethods, excludes, incorrectProducer, implicitProducer, ((io.quarkus.deployment.annotations.BuildProducer<io.quarkus.qute.deployment.TemplateExpressionMatchesBuildItem>) (null)), synthesis, checkedTemplates, templateData, config, nativeConfig, globals);
        org.junit.Assert.assertEquals(0, templateData.size());
    }

    @org.junit.Test(timeout = 4000)
    public void registerRenderedResultsReturnsSyntheticBeanWhenRecordingEnabled() throws java.lang.Throwable {
        io.quarkus.qute.deployment.QuteProcessor processor = new io.quarkus.qute.deployment.QuteProcessor();
        io.quarkus.qute.runtime.QuteTestModeConfig testMode = org.mockito.Mockito.mock(io.quarkus.qute.runtime.QuteTestModeConfig.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        org.mockito.Mockito.doReturn(true).when(testMode).recordRenderedResults();
        io.quarkus.qute.runtime.QuteConfig config = org.mockito.Mockito.mock(io.quarkus.qute.runtime.QuteConfig.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        org.mockito.Mockito.doReturn(testMode).when(config).testMode();
        io.quarkus.arc.deployment.SyntheticBeanBuildItem item = processor.registerRenderedResults(config);
        org.junit.Assert.assertNotNull(item);
    }

    @org.junit.Test(timeout = 4000)
    public void registerRenderedResultsReturnsNullWhenRecordingDisabled() throws java.lang.Throwable {
        io.quarkus.qute.deployment.QuteProcessor processor = new io.quarkus.qute.deployment.QuteProcessor();
        io.quarkus.qute.runtime.QuteTestModeConfig testMode = org.mockito.Mockito.mock(io.quarkus.qute.runtime.QuteTestModeConfig.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        org.mockito.Mockito.doReturn(false).when(testMode).recordRenderedResults();
        io.quarkus.qute.runtime.QuteConfig config = org.mockito.Mockito.mock(io.quarkus.qute.runtime.QuteConfig.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        org.mockito.Mockito.doReturn(testMode).when(config).testMode();
        io.quarkus.arc.deployment.SyntheticBeanBuildItem item = processor.registerRenderedResults(config);
        org.junit.Assert.assertNull(item);
    }

    @org.junit.Test(timeout = 4000)
    public void validateCheckedFragmentsThrowsNPEOnNullExpressionInValidation() throws java.lang.Throwable {
        java.util.LinkedList<io.quarkus.qute.deployment.CheckedFragmentValidationBuildItem> validations = new java.util.LinkedList<io.quarkus.qute.deployment.CheckedFragmentValidationBuildItem>();
        java.util.LinkedList<io.quarkus.qute.Expression> expressions = new java.util.LinkedList<io.quarkus.qute.Expression>();
        expressions.add(((io.quarkus.qute.Expression) (null)));
        io.quarkus.qute.deployment.CheckedFragmentValidationBuildItem validation = new io.quarkus.qute.deployment.CheckedFragmentValidationBuildItem("IN78 +@gT{<^b8", expressions, ((io.quarkus.qute.deployment.CheckedTemplateBuildItem) (null)));
        validations.add(validation);
        io.quarkus.qute.deployment.QuteProcessor processor = new io.quarkus.qute.deployment.QuteProcessor();
        java.util.HashMap<java.lang.Integer, io.quarkus.qute.deployment.QuteProcessor.MatchResult> matches = new java.util.HashMap<java.lang.Integer, io.quarkus.qute.deployment.QuteProcessor.MatchResult>();
        io.quarkus.qute.deployment.TemplateExpressionMatchesBuildItem matchItem = new io.quarkus.qute.deployment.TemplateExpressionMatchesBuildItem("IN78 +@gT{<^b8", matches);
        java.util.LinkedList<io.quarkus.qute.deployment.TemplateGlobalBuildItem> globals = new java.util.LinkedList<io.quarkus.qute.deployment.TemplateGlobalBuildItem>();
        java.lang.Class<io.quarkus.arc.deployment.ValidationPhaseBuildItem.ValidationErrorBuildItem> errorClass = ValidationPhaseBuildItem.ValidationErrorBuildItem.class;
        io.quarkus.builder.BuildContext buildContext = org.mockito.Mockito.mock(io.quarkus.builder.BuildContext.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        io.quarkus.deployment.BuildProducerImpl<io.quarkus.arc.deployment.ValidationPhaseBuildItem.ValidationErrorBuildItem> errorProducer = new io.quarkus.deployment.BuildProducerImpl<io.quarkus.arc.deployment.ValidationPhaseBuildItem.ValidationErrorBuildItem>(errorClass, buildContext);
        java.util.List<io.quarkus.qute.deployment.TemplateExpressionMatchesBuildItem> allMatches = java.util.List.of(matchItem, matchItem, matchItem, matchItem, matchItem, matchItem, matchItem);
        java.util.HashMap<org.jboss.jandex.DotName, java.util.List<org.jboss.jandex.AnnotationInstance>> annotations = new java.util.HashMap<org.jboss.jandex.DotName, java.util.List<org.jboss.jandex.AnnotationInstance>>();
        java.util.HashMap<org.jboss.jandex.DotName, java.util.List<org.jboss.jandex.ClassInfo>> map = new java.util.HashMap<org.jboss.jandex.DotName, java.util.List<org.jboss.jandex.ClassInfo>>();
        java.util.HashMap<org.jboss.jandex.DotName, org.jboss.jandex.ClassInfo> classes = new java.util.HashMap<org.jboss.jandex.DotName, org.jboss.jandex.ClassInfo>();
        org.jboss.jandex.Index index = org.jboss.jandex.Index.create(((java.util.Map<org.jboss.jandex.DotName, java.util.List<org.jboss.jandex.AnnotationInstance>>) (annotations)), ((java.util.Map<org.jboss.jandex.DotName, java.util.List<org.jboss.jandex.ClassInfo>>) (map)), ((java.util.Map<org.jboss.jandex.DotName, java.util.List<org.jboss.jandex.ClassInfo>>) (map)), ((java.util.Map<org.jboss.jandex.DotName, org.jboss.jandex.ClassInfo>) (classes)), ((java.util.Map<org.jboss.jandex.DotName, java.util.List<org.jboss.jandex.ClassInfo>>) (map)));
        java.util.HashSet<org.jboss.jandex.DotName> additional = new java.util.HashSet<org.jboss.jandex.DotName>();
        io.quarkus.arc.deployment.BeanArchiveIndexBuildItem beanArchiveIndex = new io.quarkus.arc.deployment.BeanArchiveIndexBuildItem(index, index, additional);
        // Undeclared exception!
        try {
            processor.validateCheckedFragments(validations, allMatches, globals, beanArchiveIndex, errorProducer);
            org.junit.Assert.fail("Expecting exception: NullPointerException");
        } catch (java.lang.NullPointerException e) {
            // 
            // Cannot invoke \"io.quarkus.qute.Expression.isLiteral()\" because \"expression\" is null
            // 
            org.evosuite.runtime.EvoAssertions.verifyException("io.quarkus.qute.deployment.QuteProcessor", e);
        }
    }

    @org.junit.Test(timeout = 4000)
    public void validateCheckedFragmentsProcessesMatchingTemplateId() throws java.lang.Throwable {
        java.util.LinkedList<io.quarkus.qute.deployment.CheckedFragmentValidationBuildItem> validations = new java.util.LinkedList<io.quarkus.qute.deployment.CheckedFragmentValidationBuildItem>();
        java.util.LinkedList<io.quarkus.qute.Expression> expressions = new java.util.LinkedList<io.quarkus.qute.Expression>();
        io.quarkus.qute.deployment.CheckedFragmentValidationBuildItem validation = new io.quarkus.qute.deployment.CheckedFragmentValidationBuildItem("properties", expressions, ((io.quarkus.qute.deployment.CheckedTemplateBuildItem) (null)));
        validations.add(validation);
        io.quarkus.qute.deployment.QuteProcessor processor = new io.quarkus.qute.deployment.QuteProcessor();
        java.util.HashMap<java.lang.Integer, io.quarkus.qute.deployment.QuteProcessor.MatchResult> matches = new java.util.HashMap<java.lang.Integer, io.quarkus.qute.deployment.QuteProcessor.MatchResult>();
        io.quarkus.qute.deployment.TemplateExpressionMatchesBuildItem otherTemplate = new io.quarkus.qute.deployment.TemplateExpressionMatchesBuildItem("*l-`5-ir<};", matches);
        java.util.LinkedList<io.quarkus.qute.deployment.TemplateGlobalBuildItem> globals = new java.util.LinkedList<io.quarkus.qute.deployment.TemplateGlobalBuildItem>();
        java.lang.Class<io.quarkus.arc.deployment.ValidationPhaseBuildItem.ValidationErrorBuildItem> errorClass = ValidationPhaseBuildItem.ValidationErrorBuildItem.class;
        io.quarkus.deployment.BuildProducerImpl<io.quarkus.arc.deployment.ValidationPhaseBuildItem.ValidationErrorBuildItem> errorProducer = new io.quarkus.deployment.BuildProducerImpl<io.quarkus.arc.deployment.ValidationPhaseBuildItem.ValidationErrorBuildItem>(errorClass, ((io.quarkus.builder.BuildContext) (null)));
        io.quarkus.qute.deployment.TemplateExpressionMatchesBuildItem targetTemplate = new io.quarkus.qute.deployment.TemplateExpressionMatchesBuildItem("properties", matches);
        java.util.List<io.quarkus.qute.deployment.TemplateExpressionMatchesBuildItem> allMatches = java.util.List.of(otherTemplate, otherTemplate, targetTemplate, targetTemplate, targetTemplate, otherTemplate, targetTemplate);
        java.util.HashMap<org.jboss.jandex.DotName, java.util.List<org.jboss.jandex.AnnotationInstance>> annotations = new java.util.HashMap<org.jboss.jandex.DotName, java.util.List<org.jboss.jandex.AnnotationInstance>>();
        java.util.HashMap<org.jboss.jandex.DotName, java.util.List<org.jboss.jandex.ClassInfo>> map = new java.util.HashMap<org.jboss.jandex.DotName, java.util.List<org.jboss.jandex.ClassInfo>>();
        java.util.HashMap<org.jboss.jandex.DotName, org.jboss.jandex.ClassInfo> classes = new java.util.HashMap<org.jboss.jandex.DotName, org.jboss.jandex.ClassInfo>();
        org.jboss.jandex.Index index = org.jboss.jandex.Index.create(((java.util.Map<org.jboss.jandex.DotName, java.util.List<org.jboss.jandex.AnnotationInstance>>) (annotations)), ((java.util.Map<org.jboss.jandex.DotName, java.util.List<org.jboss.jandex.ClassInfo>>) (map)), ((java.util.Map<org.jboss.jandex.DotName, java.util.List<org.jboss.jandex.ClassInfo>>) (map)), ((java.util.Map<org.jboss.jandex.DotName, org.jboss.jandex.ClassInfo>) (classes)), ((java.util.Map<org.jboss.jandex.DotName, java.util.List<org.jboss.jandex.ClassInfo>>) (map)));
        java.util.HashSet<org.jboss.jandex.DotName> additional = new java.util.HashSet<org.jboss.jandex.DotName>();
        io.quarkus.arc.deployment.BeanArchiveIndexBuildItem beanArchiveIndex = new io.quarkus.arc.deployment.BeanArchiveIndexBuildItem(index, index, additional);
        processor.validateCheckedFragments(validations, allMatches, globals, beanArchiveIndex, errorProducer);
        org.junit.Assert.assertEquals(7, allMatches.size());
    }

    @org.junit.Test(timeout = 4000)
    public void validateCheckedFragmentsWithNoInputsNoErrors() throws java.lang.Throwable {
        io.quarkus.qute.deployment.QuteProcessor processor = new io.quarkus.qute.deployment.QuteProcessor();
        java.util.LinkedList<io.quarkus.qute.deployment.CheckedFragmentValidationBuildItem> validations = new java.util.LinkedList<io.quarkus.qute.deployment.CheckedFragmentValidationBuildItem>();
        java.util.Vector<io.quarkus.qute.deployment.TemplateExpressionMatchesBuildItem> matches = new java.util.Vector<io.quarkus.qute.deployment.TemplateExpressionMatchesBuildItem>();
        java.util.LinkedList<io.quarkus.qute.deployment.TemplateGlobalBuildItem> globals = new java.util.LinkedList<io.quarkus.qute.deployment.TemplateGlobalBuildItem>();
        java.util.HashSet<org.jboss.jandex.DotName> additional = new java.util.HashSet<org.jboss.jandex.DotName>();
        io.quarkus.arc.deployment.BeanArchiveIndexBuildItem beanArchiveIndex = new io.quarkus.arc.deployment.BeanArchiveIndexBuildItem(((org.jboss.jandex.IndexView) (null)), ((org.jboss.jandex.IndexView) (null)), additional);
        java.lang.Class<io.quarkus.arc.deployment.ValidationPhaseBuildItem.ValidationErrorBuildItem> errorClass = ValidationPhaseBuildItem.ValidationErrorBuildItem.class;
        io.quarkus.builder.BuildContext buildContext = org.mockito.Mockito.mock(io.quarkus.builder.BuildContext.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        io.quarkus.deployment.BuildProducerImpl<io.quarkus.arc.deployment.ValidationPhaseBuildItem.ValidationErrorBuildItem> errorProducer = new io.quarkus.deployment.BuildProducerImpl<io.quarkus.arc.deployment.ValidationPhaseBuildItem.ValidationErrorBuildItem>(errorClass, buildContext);
        processor.validateCheckedFragments(validations, matches, globals, beanArchiveIndex, errorProducer);
        org.junit.Assert.assertTrue(matches.isEmpty());
    }

    @org.junit.Test(timeout = 4000)
    public void validateCheckedFragmentsThrowsNPEWhenCheckedTemplateNull() throws java.lang.Throwable {
        java.util.LinkedList<io.quarkus.qute.deployment.CheckedFragmentValidationBuildItem> validations = new java.util.LinkedList<io.quarkus.qute.deployment.CheckedFragmentValidationBuildItem>();
        java.util.LinkedList<io.quarkus.qute.Expression> expressions = new java.util.LinkedList<io.quarkus.qute.Expression>();
        io.quarkus.qute.deployment.CheckedFragmentValidationBuildItem validation = new io.quarkus.qute.deployment.CheckedFragmentValidationBuildItem("properties", expressions, ((io.quarkus.qute.deployment.CheckedTemplateBuildItem) (null)));
        validations.add(validation);
        io.quarkus.qute.deployment.QuteProcessor processor = new io.quarkus.qute.deployment.QuteProcessor();
        java.util.HashMap<java.lang.Integer, io.quarkus.qute.deployment.QuteProcessor.MatchResult> matches = new java.util.HashMap<java.lang.Integer, io.quarkus.qute.deployment.QuteProcessor.MatchResult>();
        io.quarkus.qute.deployment.TemplateExpressionMatchesBuildItem matchItem = new io.quarkus.qute.deployment.TemplateExpressionMatchesBuildItem("*l-`5-ir<};", matches);
        java.util.LinkedList<io.quarkus.qute.deployment.TemplateGlobalBuildItem> globals = new java.util.LinkedList<io.quarkus.qute.deployment.TemplateGlobalBuildItem>();
        java.lang.Class<io.quarkus.arc.deployment.ValidationPhaseBuildItem.ValidationErrorBuildItem> errorClass = ValidationPhaseBuildItem.ValidationErrorBuildItem.class;
        io.quarkus.deployment.BuildProducerImpl<io.quarkus.arc.deployment.ValidationPhaseBuildItem.ValidationErrorBuildItem> errorProducer = new io.quarkus.deployment.BuildProducerImpl<io.quarkus.arc.deployment.ValidationPhaseBuildItem.ValidationErrorBuildItem>(errorClass, ((io.quarkus.builder.BuildContext) (null)));
        java.util.List<io.quarkus.qute.deployment.TemplateExpressionMatchesBuildItem> allMatches = java.util.List.of(matchItem, matchItem, matchItem, matchItem, matchItem, matchItem, matchItem);
        java.util.HashMap<org.jboss.jandex.DotName, java.util.List<org.jboss.jandex.AnnotationInstance>> annotations = new java.util.HashMap<org.jboss.jandex.DotName, java.util.List<org.jboss.jandex.AnnotationInstance>>();
        java.util.HashMap<org.jboss.jandex.DotName, java.util.List<org.jboss.jandex.ClassInfo>> map = new java.util.HashMap<org.jboss.jandex.DotName, java.util.List<org.jboss.jandex.ClassInfo>>();
        java.util.HashMap<org.jboss.jandex.DotName, org.jboss.jandex.ClassInfo> classes = new java.util.HashMap<org.jboss.jandex.DotName, org.jboss.jandex.ClassInfo>();
        org.jboss.jandex.Index index = org.jboss.jandex.Index.create(((java.util.Map<org.jboss.jandex.DotName, java.util.List<org.jboss.jandex.AnnotationInstance>>) (annotations)), ((java.util.Map<org.jboss.jandex.DotName, java.util.List<org.jboss.jandex.ClassInfo>>) (map)), ((java.util.Map<org.jboss.jandex.DotName, java.util.List<org.jboss.jandex.ClassInfo>>) (map)), ((java.util.Map<org.jboss.jandex.DotName, org.jboss.jandex.ClassInfo>) (classes)), ((java.util.Map<org.jboss.jandex.DotName, java.util.List<org.jboss.jandex.ClassInfo>>) (map)));
        java.util.HashSet<org.jboss.jandex.DotName> additional = new java.util.HashSet<org.jboss.jandex.DotName>();
        io.quarkus.arc.deployment.BeanArchiveIndexBuildItem beanArchiveIndex = new io.quarkus.arc.deployment.BeanArchiveIndexBuildItem(index, index, additional);
        // Undeclared exception!
        try {
            processor.validateCheckedFragments(validations, allMatches, globals, beanArchiveIndex, errorProducer);
            org.junit.Assert.fail("Expecting exception: NullPointerException");
        } catch (java.lang.NullPointerException e) {
            // 
            // Cannot read field \"templateId\" because \"validation.checkedTemplate\" is null
            // 
            org.evosuite.runtime.EvoAssertions.verifyException("io.quarkus.qute.deployment.QuteProcessor", e);
        }
    }

    @org.junit.Test(timeout = 4000)
    public void analyzeTemplatesProducesCheckedFragmentValidationForNonTag() throws java.lang.Throwable {
        io.quarkus.qute.deployment.QuteProcessor processor = new io.quarkus.qute.deployment.QuteProcessor();
        java.util.Vector<io.quarkus.qute.deployment.TemplatePathBuildItem> paths = new java.util.Vector<io.quarkus.qute.deployment.TemplatePathBuildItem>();
        io.quarkus.qute.deployment.TemplatePathBuildItem pathItem = org.mockito.Mockito.mock(io.quarkus.qute.deployment.TemplatePathBuildItem.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        org.mockito.Mockito.doReturn("Displays version information of the command", "Displays version information of the command", "iHm8X").when(pathItem).getPath();
        org.mockito.Mockito.doReturn(false).when(pathItem).isTag();
        paths.add(pathItem);
        io.quarkus.qute.deployment.EffectiveTemplatePathsBuildItem effectivePaths = new io.quarkus.qute.deployment.EffectiveTemplatePathsBuildItem(paths);
        java.util.LinkedList<io.quarkus.qute.deployment.MessageBundleMethodBuildItem> messageBundles = new java.util.LinkedList<io.quarkus.qute.deployment.MessageBundleMethodBuildItem>();
        java.util.LinkedList<io.quarkus.qute.deployment.ValidationParserHookBuildItem> hooks = new java.util.LinkedList<io.quarkus.qute.deployment.ValidationParserHookBuildItem>();
        java.util.Optional<io.quarkus.qute.deployment.EngineConfigurationsBuildItem> engineConfigs = java.util.Optional.empty();
        java.lang.Class<io.quarkus.qute.deployment.CheckedFragmentValidationBuildItem> validationClass = io.quarkus.qute.deployment.CheckedFragmentValidationBuildItem.class;
        io.quarkus.builder.BuildContext buildContext = org.mockito.Mockito.mock(io.quarkus.builder.BuildContext.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        io.quarkus.deployment.BuildProducerImpl<io.quarkus.qute.deployment.CheckedFragmentValidationBuildItem> validationProducer = new io.quarkus.deployment.BuildProducerImpl<io.quarkus.qute.deployment.CheckedFragmentValidationBuildItem>(validationClass, buildContext);
        io.quarkus.qute.InsertSectionHelper.Factory factory = new io.quarkus.qute.InsertSectionHelper.Factory();
        java.util.List<java.lang.String> suffixes = factory.getDefaultAliases();
        io.quarkus.qute.runtime.QuteConfig config = org.mockito.Mockito.mock(io.quarkus.qute.runtime.QuteConfig.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        org.mockito.Mockito.doReturn(suffixes).when(config).suffixes();
        io.quarkus.qute.deployment.TemplateFilePathsBuildItem filePaths = processor.collectTemplateFilePaths(config, effectivePaths);
        java.util.ArrayList<io.quarkus.qute.deployment.CheckedTemplateBuildItem> checkedTemplates = new java.util.ArrayList<io.quarkus.qute.deployment.CheckedTemplateBuildItem>();
        java.util.ArrayList<io.quarkus.qute.deployment.TemplateGlobalBuildItem> globals = new java.util.ArrayList<io.quarkus.qute.deployment.TemplateGlobalBuildItem>();
        io.quarkus.qute.runtime.QuteConfig anotherConfig = org.mockito.Mockito.mock(io.quarkus.qute.runtime.QuteConfig.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        java.lang.Class<io.quarkus.qute.deployment.TemplatesAnalysisBuildItem> analysesClass = io.quarkus.qute.deployment.TemplatesAnalysisBuildItem.class;
        io.quarkus.builder.BuildContext buildContext1 = org.mockito.Mockito.mock(io.quarkus.builder.BuildContext.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        io.quarkus.deployment.BuildProducerImpl<io.quarkus.qute.deployment.TemplatesAnalysisBuildItem> analysesProducer = new io.quarkus.deployment.BuildProducerImpl<io.quarkus.qute.deployment.TemplatesAnalysisBuildItem>(analysesClass, buildContext1);
        processor.analyzeTemplates(effectivePaths, filePaths, checkedTemplates, messageBundles, globals, anotherConfig, hooks, engineConfigs, ((io.quarkus.arc.deployment.BeanArchiveIndexBuildItem) (null)), validationProducer, analysesProducer);
        org.junit.Assert.assertEquals(0, globals.size());
    }

    @org.junit.Test(timeout = 4000)
    public void analyzeTemplatesThrowsNPEWhenEngineConfigContainsNullClassInfo() throws java.lang.Throwable {
        io.quarkus.qute.deployment.QuteProcessor processor = new io.quarkus.qute.deployment.QuteProcessor();
        java.util.Stack<io.quarkus.qute.deployment.TemplatePathBuildItem> paths = new java.util.Stack<io.quarkus.qute.deployment.TemplatePathBuildItem>();
        io.quarkus.qute.deployment.EffectiveTemplatePathsBuildItem effectivePaths = new io.quarkus.qute.deployment.EffectiveTemplatePathsBuildItem(paths);
        io.quarkus.qute.deployment.TemplateFilePathsBuildItem filePaths = processor.collectTemplateFilePaths(((io.quarkus.qute.runtime.QuteConfig) (null)), effectivePaths);
        java.util.LinkedList<io.quarkus.qute.deployment.CheckedTemplateBuildItem> checkedTemplates = new java.util.LinkedList<io.quarkus.qute.deployment.CheckedTemplateBuildItem>();
        java.util.Stack<io.quarkus.qute.deployment.ValidationParserHookBuildItem> hooks = new java.util.Stack<io.quarkus.qute.deployment.ValidationParserHookBuildItem>();
        org.jboss.jandex.EmptyIndex emptyIndex = org.jboss.jandex.EmptyIndex.INSTANCE;
        java.util.LinkedList<org.jboss.jandex.ClassInfo> classes = new java.util.LinkedList<org.jboss.jandex.ClassInfo>();
        io.quarkus.qute.deployment.EngineConfigurationsBuildItem engineConfigsItem = new io.quarkus.qute.deployment.EngineConfigurationsBuildItem(classes);
        java.util.Optional<io.quarkus.qute.deployment.EngineConfigurationsBuildItem> engineConfigs = java.util.Optional.of(engineConfigsItem);
        classes.add(((org.jboss.jandex.ClassInfo) (null)));
        io.quarkus.arc.deployment.BeanArchiveIndexBuildItem beanArchiveIndex = new io.quarkus.arc.deployment.BeanArchiveIndexBuildItem(emptyIndex, emptyIndex, ((java.util.Set<org.jboss.jandex.DotName>) (null)));
        java.lang.Class<io.quarkus.qute.deployment.CheckedFragmentValidationBuildItem> validationClass = io.quarkus.qute.deployment.CheckedFragmentValidationBuildItem.class;
        io.quarkus.deployment.BuildProducerImpl<io.quarkus.qute.deployment.CheckedFragmentValidationBuildItem> validationProducer = new io.quarkus.deployment.BuildProducerImpl<io.quarkus.qute.deployment.CheckedFragmentValidationBuildItem>(validationClass, ((io.quarkus.builder.BuildContext) (null)));
        java.util.Stack<io.quarkus.qute.deployment.MessageBundleMethodBuildItem> messageBundles = new java.util.Stack<io.quarkus.qute.deployment.MessageBundleMethodBuildItem>();
        java.lang.Class<io.quarkus.qute.deployment.TemplatesAnalysisBuildItem> analysesClass = io.quarkus.qute.deployment.TemplatesAnalysisBuildItem.class;
        io.quarkus.deployment.BuildProducerImpl<io.quarkus.qute.deployment.TemplatesAnalysisBuildItem> analysesProducer = new io.quarkus.deployment.BuildProducerImpl<io.quarkus.qute.deployment.TemplatesAnalysisBuildItem>(analysesClass, ((io.quarkus.builder.BuildContext) (null)));
        // Undeclared exception!
        try {
            processor.analyzeTemplates(effectivePaths, filePaths, checkedTemplates, messageBundles, ((java.util.List<io.quarkus.qute.deployment.TemplateGlobalBuildItem>) (null)), ((io.quarkus.qute.runtime.QuteConfig) (null)), hooks, engineConfigs, beanArchiveIndex, validationProducer, analysesProducer);
            org.junit.Assert.fail("Expecting exception: NullPointerException");
        } catch (java.lang.NullPointerException e) {
            // 
            // Cannot invoke \"org.jboss.jandex.ClassInfo.interfaceNames()\" because \"target\" is null
            // 
            org.evosuite.runtime.EvoAssertions.verifyException("io.quarkus.qute.deployment.Types", e);
        }
    }

    @org.junit.Test(timeout = 4000)
    public void analyzeTemplatesThrowsNPEWhenTemplatePathNull() throws java.lang.Throwable {
        io.quarkus.qute.deployment.QuteProcessor processor = new io.quarkus.qute.deployment.QuteProcessor();
        java.util.Stack<io.quarkus.qute.deployment.TemplatePathBuildItem> paths = new java.util.Stack<io.quarkus.qute.deployment.TemplatePathBuildItem>();
        io.quarkus.qute.deployment.EffectiveTemplatePathsBuildItem effectivePaths = new io.quarkus.qute.deployment.EffectiveTemplatePathsBuildItem(paths);
        io.quarkus.qute.deployment.TemplateFilePathsBuildItem filePaths = processor.collectTemplateFilePaths(((io.quarkus.qute.runtime.QuteConfig) (null)), effectivePaths);
        java.util.LinkedList<io.quarkus.qute.deployment.CheckedTemplateBuildItem> checkedTemplates = new java.util.LinkedList<io.quarkus.qute.deployment.CheckedTemplateBuildItem>();
        java.util.Stack<io.quarkus.qute.deployment.ValidationParserHookBuildItem> hooks = new java.util.Stack<io.quarkus.qute.deployment.ValidationParserHookBuildItem>();
        org.jboss.jandex.EmptyIndex emptyIndex = org.jboss.jandex.EmptyIndex.INSTANCE;
        java.util.LinkedList<org.jboss.jandex.ClassInfo> classes = new java.util.LinkedList<org.jboss.jandex.ClassInfo>();
        io.quarkus.qute.deployment.EngineConfigurationsBuildItem engineConfigsItem = new io.quarkus.qute.deployment.EngineConfigurationsBuildItem(classes);
        java.util.Optional<io.quarkus.qute.deployment.EngineConfigurationsBuildItem> engineConfigs = java.util.Optional.of(engineConfigsItem);
        io.quarkus.arc.deployment.BeanArchiveIndexBuildItem beanArchiveIndex = new io.quarkus.arc.deployment.BeanArchiveIndexBuildItem(emptyIndex, emptyIndex, ((java.util.Set<org.jboss.jandex.DotName>) (null)));
        java.lang.Class<io.quarkus.qute.deployment.CheckedFragmentValidationBuildItem> validationClass = io.quarkus.qute.deployment.CheckedFragmentValidationBuildItem.class;
        io.quarkus.qute.deployment.TemplatePathBuildItem pathItem = org.mockito.Mockito.mock(io.quarkus.qute.deployment.TemplatePathBuildItem.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        org.mockito.Mockito.doReturn(((java.lang.String) (null))).when(pathItem).getPath();
        org.mockito.Mockito.doReturn(false).when(pathItem).isTag();
        paths.addElement(pathItem);
        io.quarkus.deployment.BuildProducerImpl<io.quarkus.qute.deployment.CheckedFragmentValidationBuildItem> validationProducer = new io.quarkus.deployment.BuildProducerImpl<io.quarkus.qute.deployment.CheckedFragmentValidationBuildItem>(validationClass, ((io.quarkus.builder.BuildContext) (null)));
        java.util.Stack<io.quarkus.qute.deployment.MessageBundleMethodBuildItem> messageBundles = new java.util.Stack<io.quarkus.qute.deployment.MessageBundleMethodBuildItem>();
        java.lang.Class<io.quarkus.qute.deployment.TemplatesAnalysisBuildItem> analysesClass = io.quarkus.qute.deployment.TemplatesAnalysisBuildItem.class;
        io.quarkus.deployment.BuildProducerImpl<io.quarkus.qute.deployment.TemplatesAnalysisBuildItem> analysesProducer = new io.quarkus.deployment.BuildProducerImpl<io.quarkus.qute.deployment.TemplatesAnalysisBuildItem>(analysesClass, ((io.quarkus.builder.BuildContext) (null)));
        // Undeclared exception!
        try {
            processor.analyzeTemplates(effectivePaths, filePaths, checkedTemplates, messageBundles, ((java.util.List<io.quarkus.qute.deployment.TemplateGlobalBuildItem>) (null)), ((io.quarkus.qute.runtime.QuteConfig) (null)), hooks, engineConfigs, beanArchiveIndex, validationProducer, analysesProducer);
            org.junit.Assert.fail("Expecting exception: NullPointerException");
        } catch (java.lang.NullPointerException e) {
            // 
            // no message in exception (getMessage() returned null)
            // 
            org.evosuite.runtime.EvoAssertions.verifyException("java.util.concurrent.ConcurrentHashMap", e);
        }
    }

    @org.junit.Test(timeout = 4000)
    public void collectEffectiveTemplatePathsReturnsResultForPrioritizeStrategy() throws java.lang.Throwable {
        io.quarkus.qute.deployment.QuteProcessor processor = new io.quarkus.qute.deployment.QuteProcessor();
        io.quarkus.qute.runtime.QuteConfig.DuplicitTemplatesStrategy strategy = QuteConfig.DuplicitTemplatesStrategy.PRIORITIZE;
        io.quarkus.qute.runtime.QuteConfig config = org.mockito.Mockito.mock(io.quarkus.qute.runtime.QuteConfig.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        org.mockito.Mockito.doReturn(strategy).when(config).duplicitTemplatesStrategy();
        java.util.LinkedList<io.quarkus.qute.deployment.TemplatePathBuildItem> paths = new java.util.LinkedList<io.quarkus.qute.deployment.TemplatePathBuildItem>();
        io.quarkus.qute.deployment.EffectiveTemplatePathsBuildItem effectivePaths = processor.collectEffectiveTemplatePaths(config, paths);
        org.junit.Assert.assertNotNull(effectivePaths);
    }

    @org.junit.Test(timeout = 4000)
    public void collectEffectiveTemplatePathsReturnsResultForFailStrategy() throws java.lang.Throwable {
        io.quarkus.qute.deployment.QuteProcessor processor = new io.quarkus.qute.deployment.QuteProcessor();
        io.quarkus.qute.runtime.QuteConfig.DuplicitTemplatesStrategy strategy = QuteConfig.DuplicitTemplatesStrategy.FAIL;
        io.quarkus.qute.runtime.QuteConfig config = org.mockito.Mockito.mock(io.quarkus.qute.runtime.QuteConfig.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        org.mockito.Mockito.doReturn(strategy).when(config).duplicitTemplatesStrategy();
        java.util.LinkedList<io.quarkus.qute.deployment.TemplatePathBuildItem> paths = new java.util.LinkedList<io.quarkus.qute.deployment.TemplatePathBuildItem>();
        io.quarkus.qute.deployment.EffectiveTemplatePathsBuildItem effectivePaths = processor.collectEffectiveTemplatePaths(config, paths);
        org.junit.Assert.assertNotNull(effectivePaths);
    }

    @org.junit.Test(timeout = 4000)
    public void collectCheckedTemplatesThrowsISEWhenTemplateInstanceNotIndexed() throws java.lang.Throwable {
        io.quarkus.qute.deployment.QuteProcessor processor = new io.quarkus.qute.deployment.QuteProcessor();
        org.jboss.jandex.IndexView emptyIndex = org.jboss.jandex.IndexView.empty();
        java.util.Locale prc = java.util.Locale.PRC;
        java.util.Set<java.lang.String> attributes = prc.getUnicodeLocaleAttributes();
        io.quarkus.qute.deployment.TemplateFilePathsBuildItem filePaths = new io.quarkus.qute.deployment.TemplateFilePathsBuildItem(attributes);
        java.util.ArrayDeque<java.util.regex.Pattern> patterns = new java.util.ArrayDeque<java.util.regex.Pattern>(-2718);
        io.quarkus.qute.deployment.CustomTemplateLocatorPatternsBuildItem customPatterns = new io.quarkus.qute.deployment.CustomTemplateLocatorPatternsBuildItem(patterns);
        java.util.concurrent.LinkedBlockingDeque<org.jboss.jandex.IndexView> indices = new java.util.concurrent.LinkedBlockingDeque<org.jboss.jandex.IndexView>();
        org.jboss.jandex.CompositeIndex composite = org.jboss.jandex.CompositeIndex.create(((java.util.Collection<org.jboss.jandex.IndexView>) (indices)));
        org.jboss.jandex.DotName charName = org.jboss.jandex.DotName.CHARACTER_CLASS_NAME;
        java.util.Set<org.jboss.jandex.DotName> subpackages = composite.getSubpackages(charName);
        io.quarkus.arc.deployment.BeanArchiveIndexBuildItem beanArchiveIndex = new io.quarkus.arc.deployment.BeanArchiveIndexBuildItem(emptyIndex, emptyIndex, subpackages);
        java.lang.Class<io.quarkus.deployment.builditem.BytecodeTransformerBuildItem> transformerClass = io.quarkus.deployment.builditem.BytecodeTransformerBuildItem.class;
        io.quarkus.builder.BuildContext buildContext = org.mockito.Mockito.mock(io.quarkus.builder.BuildContext.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        io.quarkus.deployment.BuildProducerImpl<io.quarkus.deployment.builditem.BytecodeTransformerBuildItem> transformerProducer = new io.quarkus.deployment.BuildProducerImpl<io.quarkus.deployment.builditem.BytecodeTransformerBuildItem>(transformerClass, buildContext);
        java.util.LinkedList<io.quarkus.qute.deployment.CheckedTemplateAdapterBuildItem> adapters = new java.util.LinkedList<io.quarkus.qute.deployment.CheckedTemplateAdapterBuildItem>();
        // Undeclared exception!
        try {
            processor.collectCheckedTemplates(beanArchiveIndex, transformerProducer, adapters, filePaths, customPatterns);
            org.junit.Assert.fail("Expecting exception: IllegalStateException");
        } catch (java.lang.IllegalStateException e) {
            // 
            // io.quarkus.qute.TemplateInstance not found in the index
            // 
            org.evosuite.runtime.EvoAssertions.verifyException("io.quarkus.qute.deployment.QuteProcessor", e);
        }
    }

    @org.junit.Test(timeout = 4000)
    public void collectCheckedTemplatesThrowsNPEWhenAdapterReturnsNullBinaryName() throws java.lang.Throwable {
        io.quarkus.qute.deployment.CheckedTemplateAdapter adapter = org.mockito.Mockito.mock(io.quarkus.qute.deployment.CheckedTemplateAdapter.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        org.mockito.Mockito.doReturn(">ZQgZ", ((java.lang.String) (null))).when(adapter).templateInstanceBinaryName();
        io.quarkus.qute.deployment.CheckedTemplateAdapterBuildItem adapterItem = new io.quarkus.qute.deployment.CheckedTemplateAdapterBuildItem(adapter);
        java.util.List<io.quarkus.qute.deployment.CheckedTemplateAdapterBuildItem> adapters = java.util.List.of(adapterItem, adapterItem, adapterItem);
        java.util.LinkedHashSet<java.lang.String> files = new java.util.LinkedHashSet<java.lang.String>();
        io.quarkus.qute.deployment.TemplateFilePathsBuildItem filePaths = new io.quarkus.qute.deployment.TemplateFilePathsBuildItem(files);
        java.util.ArrayDeque<java.util.regex.Pattern> patterns = new java.util.ArrayDeque<java.util.regex.Pattern>(-254003081);
        io.quarkus.qute.deployment.CustomTemplateLocatorPatternsBuildItem customPatterns = new io.quarkus.qute.deployment.CustomTemplateLocatorPatternsBuildItem(patterns);
        io.quarkus.qute.deployment.QuteProcessor processor = new io.quarkus.qute.deployment.QuteProcessor();
        // Undeclared exception!
        try {
            processor.collectCheckedTemplates(((io.quarkus.arc.deployment.BeanArchiveIndexBuildItem) (null)), ((io.quarkus.deployment.annotations.BuildProducer<io.quarkus.deployment.builditem.BytecodeTransformerBuildItem>) (null)), adapters, filePaths, customPatterns);
            org.junit.Assert.fail("Expecting exception: NullPointerException");
        } catch (java.lang.NullPointerException e) {
            // 
            // Cannot invoke \"String.replace(char, char)\" because the return value of \"io.quarkus.qute.deployment.CheckedTemplateAdapter.templateInstanceBinaryName()\" is null
            // 
            org.evosuite.runtime.EvoAssertions.verifyException("io.quarkus.qute.deployment.QuteProcessor", e);
        }
    }

    @org.junit.Test(timeout = 4000)
    public void processTemplateErrorsThrowsRuntimeExceptionWithDetailedMessagesCase0() throws java.lang.Throwable {
        io.quarkus.qute.deployment.QuteProcessor processor = new io.quarkus.qute.deployment.QuteProcessor();
        java.util.LinkedList<io.quarkus.qute.deployment.TemplatesAnalysisBuildItem.TemplateAnalysis> analysesList = new java.util.LinkedList<io.quarkus.qute.deployment.TemplatesAnalysisBuildItem.TemplateAnalysis>();
        io.quarkus.qute.deployment.TemplatesAnalysisBuildItem analyses = new io.quarkus.qute.deployment.TemplatesAnalysisBuildItem(analysesList);
        io.quarkus.qute.TemplateNode.Origin origin0 = org.mockito.Mockito.mock(TemplateNode.Origin.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        org.mockito.Mockito.doReturn(1239, 1239, 1239, 0, 0).when(origin0).getLine();
        org.mockito.Mockito.doReturn(1239, 30, -157, 0, 0).when(origin0).getLineCharacterStart();
        org.mockito.Mockito.doReturn("@TemplateData declared on %s is ignored: target %s it is not available in the index", "z{:}J`ynr\"wS;p", "@TemplateData declared on %s is ignored: target %s it is not available in the index", ((java.lang.String) (null)), ((java.lang.String) (null))).when(origin0).getTemplateGeneratedId();
        org.mockito.Mockito.doReturn("5fZb3BV@gqkIQb%q6+Z", ".V6}", "@TemplateData declared on %s is ignored: target %s it is not available in the index", ((java.lang.String) (null)), ((java.lang.String) (null))).when(origin0).getTemplateId();
        io.quarkus.qute.deployment.IncorrectExpressionBuildItem incorrect0 = new io.quarkus.qute.deployment.IncorrectExpressionBuildItem("@TemplateData declared on %s is ignored: target %s it is not available in the index", "@TemplateData declared on %s is ignored: target %s it is not available in the index", origin0);
        io.quarkus.qute.TemplateNode.Origin origin1 = org.mockito.Mockito.mock(TemplateNode.Origin.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        org.mockito.Mockito.doReturn(0).when(origin1).getLine();
        org.mockito.Mockito.doReturn(0).when(origin1).getLineCharacterStart();
        org.mockito.Mockito.doReturn(((java.lang.String) (null))).when(origin1).getTemplateGeneratedId();
        org.mockito.Mockito.doReturn(((java.lang.String) (null))).when(origin1).getTemplateId();
        io.quarkus.qute.deployment.IncorrectExpressionBuildItem incorrect1 = new io.quarkus.qute.deployment.IncorrectExpressionBuildItem("Zr#75-e~lw:<R", "vn5o^#q5n>;igPe{'GK", "Zr#75-e~lw:<R", origin1);
        java.util.List<io.quarkus.qute.deployment.IncorrectExpressionBuildItem> incorrectList = java.util.List.of(incorrect0, incorrect0, incorrect0, incorrect0, incorrect0, incorrect0, incorrect0, incorrect1, incorrect0);
        java.lang.Class<io.quarkus.deployment.builditem.ServiceStartBuildItem> serviceStartClass = io.quarkus.deployment.builditem.ServiceStartBuildItem.class;
        io.quarkus.builder.BuildContext buildContext = org.mockito.Mockito.mock(io.quarkus.builder.BuildContext.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        io.quarkus.deployment.BuildProducerImpl<io.quarkus.deployment.builditem.ServiceStartBuildItem> serviceStartProducer = new io.quarkus.deployment.BuildProducerImpl<io.quarkus.deployment.builditem.ServiceStartBuildItem>(serviceStartClass, buildContext);
        // Undeclared exception!
        try {
            processor.processTemplateErrors(analyses, incorrectList, serviceStartProducer);
            org.junit.Assert.fail("Expecting exception: RuntimeException");
        } catch (java.lang.RuntimeException e) {
            // 
            // Found incorrect expressions (9):
            // \t[1] java.lang.String@0000000126:java.lang.Integer@0000000127:java.lang.Integer@0000000128 - {java.lang.String@0000000129}: java.lang.String@0000000129
            // \t[2] java.lang.String@0000000250:java.lang.Integer@0000000251:java.lang.Integer@0000000252 - {java.lang.String@0000000129}: java.lang.String@0000000129
            // \t[3] java.lang.String@0000000373:java.lang.Integer@0000000374:java.lang.Integer@0000000375 - {java.lang.String@0000000129}: java.lang.String@0000000129
            // \t[4] :java.lang.Integer@0000000493:java.lang.Integer@0000000493 - {java.lang.String@0000000129}: java.lang.String@0000000129
            // \t[5] :java.lang.Integer@0000000493:java.lang.Integer@0000000493 - {java.lang.String@0000000129}: java.lang.String@0000000129
            // \t[6] :java.lang.Integer@0000000493:java.lang.Integer@0000000493 - {java.lang.String@0000000129}: java.lang.String@0000000129
            // \t[7] :java.lang.Integer@0000000493:java.lang.Integer@0000000493 - {java.lang.String@0000000129}: java.lang.String@0000000129
            // \t[8] :java.lang.Integer@0000000493:java.lang.Integer@0000000493 - {java.lang.String@0000000978}: Property/method [java.lang.String@0000000979] not found on class [java.lang.String@0000000978] nor handled by an extension method
            // \t[9] :java.lang.Integer@0000000493:java.lang.Integer@0000000493 - {java.lang.String@0000000129}: java.lang.String@0000000129
            // 
            org.evosuite.runtime.EvoAssertions.verifyException("io.quarkus.qute.deployment.QuteProcessor", e);
        }
    }

    @org.junit.Test(timeout = 4000)
    public void processTemplateErrorsThrowsRuntimeExceptionWithDetailedMessagesCase1() throws java.lang.Throwable {
        io.quarkus.qute.deployment.QuteProcessor processor = new io.quarkus.qute.deployment.QuteProcessor();
        io.quarkus.qute.TemplateNode.Origin origin0 = org.mockito.Mockito.mock(TemplateNode.Origin.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        org.mockito.Mockito.doReturn(0, 0, 0, 0, 0).when(origin0).getLine();
        org.mockito.Mockito.doReturn(0, 0, 0, 0, 0).when(origin0).getLineCharacterStart();
        org.mockito.Mockito.doReturn(((java.lang.String) (null)), ((java.lang.String) (null)), ((java.lang.String) (null)), ((java.lang.String) (null)), ((java.lang.String) (null))).when(origin0).getTemplateGeneratedId();
        org.mockito.Mockito.doReturn(((java.lang.String) (null)), ((java.lang.String) (null)), ((java.lang.String) (null)), ((java.lang.String) (null)), ((java.lang.String) (null))).when(origin0).getTemplateId();
        io.quarkus.qute.deployment.IncorrectExpressionBuildItem incorrect0 = new io.quarkus.qute.deployment.IncorrectExpressionBuildItem("ParserHook registered during template analysis: %s", "<}", "ParserHook registered during template analysis: %s", origin0, "ParserHook registered during template analysis: %s");
        io.quarkus.qute.TemplateNode.Origin origin1 = org.mockito.Mockito.mock(TemplateNode.Origin.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        org.mockito.Mockito.doReturn(0, 0, 0).when(origin1).getLine();
        org.mockito.Mockito.doReturn(0, 0, 0).when(origin1).getLineCharacterStart();
        org.mockito.Mockito.doReturn(((java.lang.String) (null)), ((java.lang.String) (null)), ((java.lang.String) (null))).when(origin1).getTemplateGeneratedId();
        org.mockito.Mockito.doReturn(((java.lang.String) (null)), ((java.lang.String) (null)), ((java.lang.String) (null))).when(origin1).getTemplateId();
        io.quarkus.qute.deployment.IncorrectExpressionBuildItem incorrect1 = new io.quarkus.qute.deployment.IncorrectExpressionBuildItem(((java.lang.String) (null)), ((java.lang.String) (null)), origin1);
        java.util.List<io.quarkus.qute.deployment.IncorrectExpressionBuildItem> incorrectList = java.util.List.of(incorrect0, incorrect0, incorrect0, incorrect1, incorrect1, incorrect0, incorrect0, incorrect1, incorrect0);
        java.lang.Class<io.quarkus.deployment.builditem.ServiceStartBuildItem> serviceStartClass = io.quarkus.deployment.builditem.ServiceStartBuildItem.class;
        io.quarkus.builder.BuildContext buildContext = org.mockito.Mockito.mock(io.quarkus.builder.BuildContext.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        io.quarkus.deployment.BuildProducerImpl<io.quarkus.deployment.builditem.ServiceStartBuildItem> serviceStartProducer = new io.quarkus.deployment.BuildProducerImpl<io.quarkus.deployment.builditem.ServiceStartBuildItem>(serviceStartClass, buildContext);
        java.util.LinkedList<io.quarkus.qute.deployment.TemplatesAnalysisBuildItem.TemplateAnalysis> analysesList = new java.util.LinkedList<io.quarkus.qute.deployment.TemplatesAnalysisBuildItem.TemplateAnalysis>();
        io.quarkus.qute.deployment.TemplatesAnalysisBuildItem analyses = new io.quarkus.qute.deployment.TemplatesAnalysisBuildItem(analysesList);
        // Undeclared exception!
        try {
            processor.processTemplateErrors(analyses, incorrectList, serviceStartProducer);
            org.junit.Assert.fail("Expecting exception: RuntimeException");
        } catch (java.lang.RuntimeException e) {
            // 
            // Found incorrect expressions (9):
            // \t[1] :java.lang.Integer@0000000123:java.lang.Integer@0000000123 - {java.lang.String@0000000124}: java.lang.String@0000000124
            // \t[2] :java.lang.Integer@0000000123:java.lang.Integer@0000000123 - {java.lang.String@0000000124}: java.lang.String@0000000124
            // \t[3] :java.lang.Integer@0000000123:java.lang.Integer@0000000123 - {java.lang.String@0000000124}: java.lang.String@0000000124
            // \t[4] :java.lang.Integer@0000000123:java.lang.Integer@0000000123 - {}: @Named bean not found for []
            // \t[5] :java.lang.Integer@0000000123:java.lang.Integer@0000000123 - {}: @Named bean not found for []
            // \t[6] :java.lang.Integer@0000000123:java.lang.Integer@0000000123 - {java.lang.String@0000000124}: java.lang.String@0000000124
            // \t[7] :java.lang.Integer@0000000123:java.lang.Integer@0000000123 - {java.lang.String@0000000124}: java.lang.String@0000000124
            // \t[8] :java.lang.Integer@0000000123:java.lang.Integer@0000000123 - {}: @Named bean not found for []
            // \t[9] :java.lang.Integer@0000000123:java.lang.Integer@0000000123 - {java.lang.String@0000000124}: java.lang.String@0000000124
            // 
            org.evosuite.runtime.EvoAssertions.verifyException("io.quarkus.qute.deployment.QuteProcessor", e);
        }
    }

    @org.junit.Test(timeout = 4000)
    public void firstPassLookupConfigIndexDelegates() throws java.lang.Throwable {
        java.util.ArrayList<org.jboss.jandex.IndexView> views = new java.util.ArrayList<org.jboss.jandex.IndexView>();
        org.jboss.jandex.StackedIndex stackedIndex = org.jboss.jandex.StackedIndex.create(((java.util.List<org.jboss.jandex.IndexView>) (views)));
        java.lang.Object target = new java.lang.Object();
        java.util.function.Predicate<org.jboss.jandex.AnnotationTarget> filter = java.util.function.Predicate.isEqual(target);
        io.quarkus.qute.deployment.QuteProcessor.FixedJavaMemberLookupConfig next = new io.quarkus.qute.deployment.QuteProcessor.FixedJavaMemberLookupConfig(stackedIndex, filter, true);
        java.lang.Boolean declaredOnly = java.lang.Boolean.valueOf(true);
        io.quarkus.qute.deployment.QuteProcessor.FirstPassJavaMemberLookupConfig first = new io.quarkus.qute.deployment.QuteProcessor.FirstPassJavaMemberLookupConfig(next, filter, declaredOnly);
        first.index();
        org.junit.Assert.assertTrue(next.declaredMembersOnly());
    }

    @org.junit.Test(timeout = 4000)
    public void fixedLookupConfigFilterNoChangeWhenDeclaredFalse() throws java.lang.Throwable {
        io.quarkus.deployment.dev.AlwaysFalsePredicate<java.lang.Object> alwaysFalse = new io.quarkus.deployment.dev.AlwaysFalsePredicate<java.lang.Object>();
        java.util.function.Predicate<org.jboss.jandex.AnnotationTarget> filter = java.util.function.Predicate.not(alwaysFalse);
        io.quarkus.qute.deployment.QuteProcessor.FixedJavaMemberLookupConfig config = new io.quarkus.qute.deployment.QuteProcessor.FixedJavaMemberLookupConfig(((org.jboss.jandex.IndexView) (null)), filter, false);
        config.filter();
        org.junit.Assert.assertFalse(config.declaredMembersOnly());
    }

    @org.junit.Test(timeout = 4000)
    public void fixedLookupConfigDeclaredMembersOnlyReturnsFalseWhenConfigured() throws java.lang.Throwable {
        io.quarkus.deployment.dev.AlwaysFalsePredicate<java.lang.Object> alwaysFalse = new io.quarkus.deployment.dev.AlwaysFalsePredicate<java.lang.Object>();
        java.util.function.Predicate<org.jboss.jandex.AnnotationTarget> filter = java.util.function.Predicate.not(alwaysFalse);
        io.quarkus.qute.deployment.QuteProcessor.FixedJavaMemberLookupConfig config = new io.quarkus.qute.deployment.QuteProcessor.FixedJavaMemberLookupConfig(((org.jboss.jandex.IndexView) (null)), filter, false);
        boolean declaredOnly = config.declaredMembersOnly();
        org.junit.Assert.assertFalse(declaredOnly);
    }

    @org.junit.Test(timeout = 4000)
    public void extractMatchTypeThrowsIAEWhenTypeNotParameterized() throws java.lang.Throwable {
        java.util.LinkedHashSet<org.jboss.jandex.Type> candidates = new java.util.LinkedHashSet<org.jboss.jandex.Type>();
        java.lang.Class<io.quarkus.maven.dependency.ResolvedDependencyBuilder> upperBound = io.quarkus.maven.dependency.ResolvedDependencyBuilder.class;
        org.jboss.jandex.WildcardType wildcard = org.jboss.jandex.WildcardType.createUpperBound(upperBound);
        candidates.add(wildcard);
        java.util.function.Function<org.jboss.jandex.Type, org.jboss.jandex.Type> firstParamExtractor = QuteProcessor.FIRST_PARAM_TYPE_EXTRACT_FUN;
        org.jboss.jandex.DotName name = wildcard.name();
        // Undeclared exception!
        try {
            io.quarkus.qute.deployment.QuteProcessor.extractMatchType(candidates, name, firstParamExtractor);
            org.junit.Assert.fail("Expecting exception: IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException e) {
            // 
            // Not a parameterized type!
            // 
            org.evosuite.runtime.EvoAssertions.verifyException("org.jboss.jandex.Type", e);
        }
    }

    @org.junit.Test(timeout = 4000)
    public void mapEntryExtractFunctionReturnsValueType() throws java.lang.Throwable {
        java.util.function.Function<org.jboss.jandex.Type, org.jboss.jandex.Type> extractor = QuteProcessor.MAP_ENTRY_EXTRACT_FUN;
        java.lang.Class<java.lang.Object> objectClass = java.lang.Object.class;
        org.jboss.jandex.DotName typeName = org.jboss.jandex.DotName.createSimple(objectClass);
        org.jboss.jandex.Type[] typeArgs = new org.jboss.jandex.Type[5];
        org.jboss.jandex.ParameterizedType paramType = org.jboss.jandex.ParameterizedType.create(typeName, typeArgs);
        org.jboss.jandex.Type valueType = extractor.apply(paramType);
        org.junit.Assert.assertNotSame(paramType, valueType);
    }

    @org.junit.Test(timeout = 4000)
    public void endToEndCollectTemplateVariantsAnalyzeTemplatesNoErrors() throws java.lang.Throwable {
        io.quarkus.qute.deployment.QuteProcessor processor = new io.quarkus.qute.deployment.QuteProcessor();
        java.util.Stack<io.quarkus.qute.deployment.TemplatePathBuildItem> paths = new java.util.Stack<io.quarkus.qute.deployment.TemplatePathBuildItem>();
        io.quarkus.qute.deployment.TemplatePathBuildItem pathItem = org.mockito.Mockito.mock(io.quarkus.qute.deployment.TemplatePathBuildItem.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        org.mockito.Mockito.doReturn("B<mXn6Grr06pAJ'").when(pathItem).getContent();
        org.mockito.Mockito.doReturn("NO_SECTION_HELPER_FOUND", "B<mXn6Grr06pAJ'", "NO_SECTION_HELPER_FOUND", "k,rWgE)MEP", "k,rWgE)MEP").when(pathItem).getPath();
        org.mockito.Mockito.doReturn(true).when(pathItem).isTag();
        paths.add(pathItem);
        io.quarkus.qute.deployment.EffectiveTemplatePathsBuildItem effectivePaths = new io.quarkus.qute.deployment.EffectiveTemplatePathsBuildItem(paths);
        org.jboss.jandex.EmptyIndex emptyIndex = org.jboss.jandex.EmptyIndex.INSTANCE;
        io.quarkus.qute.WhenSectionHelper.Factory whenFactory = new io.quarkus.qute.WhenSectionHelper.Factory();
        java.util.List<java.lang.String> suffixes = whenFactory.getBlockLabels();
        io.quarkus.qute.runtime.QuteConfig config = org.mockito.Mockito.mock(io.quarkus.qute.runtime.QuteConfig.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        org.mockito.Mockito.doReturn(suffixes).when(config).suffixes();
        processor.collectTemplateVariants(effectivePaths, config);
        io.quarkus.qute.runtime.QuteConfig config2 = org.mockito.Mockito.mock(io.quarkus.qute.runtime.QuteConfig.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        org.mockito.Mockito.doReturn(suffixes).when(config2).suffixes();
        io.quarkus.qute.deployment.TemplateFilePathsBuildItem filePaths = processor.collectTemplateFilePaths(config2, effectivePaths);
        java.util.Vector<io.quarkus.qute.deployment.CheckedTemplateBuildItem> checked = new java.util.Vector<io.quarkus.qute.deployment.CheckedTemplateBuildItem>();
        java.util.LinkedList<io.quarkus.qute.deployment.MessageBundleMethodBuildItem> messageBundles = new java.util.LinkedList<io.quarkus.qute.deployment.MessageBundleMethodBuildItem>();
        io.quarkus.qute.runtime.QuteConfig config3 = org.mockito.Mockito.mock(io.quarkus.qute.runtime.QuteConfig.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        org.mockito.Mockito.doReturn(suffixes).when(config3).suffixes();
        java.util.Vector<io.quarkus.qute.deployment.ValidationParserHookBuildItem> hooks = new java.util.Vector<io.quarkus.qute.deployment.ValidationParserHookBuildItem>();
        java.util.Optional<io.quarkus.qute.deployment.EngineConfigurationsBuildItem> engineConfigs = java.util.Optional.ofNullable(null);
        io.quarkus.arc.deployment.BeanArchiveIndexBuildItem beanArchiveIndex = new io.quarkus.arc.deployment.BeanArchiveIndexBuildItem(emptyIndex, emptyIndex, ((java.util.Set<org.jboss.jandex.DotName>) (null)));
        java.lang.Class<io.quarkus.qute.deployment.CheckedFragmentValidationBuildItem> validationClass = io.quarkus.qute.deployment.CheckedFragmentValidationBuildItem.class;
        io.quarkus.builder.BuildContext buildContext = org.mockito.Mockito.mock(io.quarkus.builder.BuildContext.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        io.quarkus.deployment.BuildProducerImpl<io.quarkus.qute.deployment.CheckedFragmentValidationBuildItem> validationProducer = new io.quarkus.deployment.BuildProducerImpl<io.quarkus.qute.deployment.CheckedFragmentValidationBuildItem>(validationClass, buildContext);
        io.quarkus.deployment.annotations.BuildProducer<io.quarkus.qute.deployment.TemplatesAnalysisBuildItem> analysesProducer = ((io.quarkus.deployment.annotations.BuildProducer<io.quarkus.qute.deployment.TemplatesAnalysisBuildItem>) (org.mockito.Mockito.mock(io.quarkus.deployment.annotations.BuildProducer.class, new org.evosuite.runtime.ViolatedAssumptionAnswer())));
        processor.analyzeTemplates(effectivePaths, filePaths, checked, messageBundles, ((java.util.List<io.quarkus.qute.deployment.TemplateGlobalBuildItem>) (null)), config3, hooks, engineConfigs, beanArchiveIndex, validationProducer, analysesProducer);
        org.junit.Assert.assertEquals(0, hooks.size());
    }

    @org.junit.Test(timeout = 4000)
    public void codeGetNameReturnsPrefixedConstant() throws java.lang.Throwable {
        io.quarkus.qute.deployment.QuteProcessor.Code code = QuteProcessor.Code.INCORRECT_EXPRESSION;
        java.lang.String name = code.getName();
        org.junit.Assert.assertEquals("BUILD_INCORRECT_EXPRESSION", name);
    }

    @org.junit.Test(timeout = 4000)
    public void existingValueResolversGetGeneratedGlobalClassReturnsNullWhenAbsent() throws java.lang.Throwable {
        io.quarkus.qute.deployment.QuteProcessor.ExistingValueResolvers resolvers = new io.quarkus.qute.deployment.QuteProcessor.ExistingValueResolvers();
        org.jboss.jandex.DotName key = org.jboss.jandex.DotName.createSimple("t2");
        java.lang.String generated = resolvers.getGeneratedGlobalClass(key);
        org.junit.Assert.assertNull(generated);
    }

    @org.junit.Test(timeout = 4000)
    public void existingValueResolversGetGeneratedClassThrowsNPEOnNullMethod() throws java.lang.Throwable {
        io.quarkus.qute.deployment.QuteProcessor.ExistingValueResolvers resolvers = new io.quarkus.qute.deployment.QuteProcessor.ExistingValueResolvers();
        // Undeclared exception!
        try {
            resolvers.getGeneratedClass(((org.jboss.jandex.MethodInfo) (null)));
            org.junit.Assert.fail("Expecting exception: NullPointerException");
        } catch (java.lang.NullPointerException e) {
            // 
            // Cannot invoke \"org.jboss.jandex.MethodInfo.declaringClass()\" because \"extensionMethod\" is null
            // 
            org.evosuite.runtime.EvoAssertions.verifyException("io.quarkus.qute.deployment.QuteProcessor$ExistingValueResolvers", e);
        }
    }

    @org.junit.Test(timeout = 4000)
    public void firstPassLookupConfigNextPartNullNextThrowsOnFilter() throws java.lang.Throwable {
        java.util.function.Predicate<org.jboss.jandex.AnnotationTarget> filter = java.util.function.Predicate.isEqual(null);
        java.lang.Boolean declaredOnly = java.lang.Boolean.TRUE;
        io.quarkus.qute.deployment.QuteProcessor.FirstPassJavaMemberLookupConfig first = new io.quarkus.qute.deployment.QuteProcessor.FirstPassJavaMemberLookupConfig(((io.quarkus.qute.deployment.QuteProcessor.JavaMemberLookupConfig) (null)), filter, declaredOnly);
        first.nextPart();
        // Undeclared exception!
        try {
            first.filter();
            org.junit.Assert.fail("Expecting exception: NullPointerException");
        } catch (java.lang.NullPointerException e) {
            // 
            // Cannot invoke \"io.quarkus.qute.deployment.QuteProcessor$JavaMemberLookupConfig.filter()\" because \"this.next\" is null
            // 
            org.evosuite.runtime.EvoAssertions.verifyException("io.quarkus.qute.deployment.QuteProcessor$FirstPassJavaMemberLookupConfig", e);
        }
    }

    @org.junit.Test(timeout = 4000)
    public void collectTemplatesThrowsNPEWhenApplicationArchivesNullCase2() throws java.lang.Throwable {
        io.quarkus.qute.deployment.QuteProcessor processor = new io.quarkus.qute.deployment.QuteProcessor();
        java.util.Vector<io.quarkus.qute.deployment.TemplatePathExcludeBuildItem> excludes = new java.util.Vector<io.quarkus.qute.deployment.TemplatePathExcludeBuildItem>(64711720, 1452);
        java.lang.Class<io.quarkus.qute.deployment.TemplatePathBuildItem> pathClass = io.quarkus.qute.deployment.TemplatePathBuildItem.class;
        io.quarkus.builder.BuildContext buildContext = org.mockito.Mockito.mock(io.quarkus.builder.BuildContext.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        io.quarkus.deployment.BuildProducerImpl<io.quarkus.qute.deployment.TemplatePathBuildItem> pathProducer = new io.quarkus.deployment.BuildProducerImpl<io.quarkus.qute.deployment.TemplatePathBuildItem>(pathClass, buildContext);
        io.quarkus.deployment.pkg.builditem.CurateOutcomeBuildItem curateOutcome = new io.quarkus.deployment.pkg.builditem.CurateOutcomeBuildItem(((io.quarkus.bootstrap.model.ApplicationModel) (null)));
        java.lang.Class<io.quarkus.deployment.builditem.HotDeploymentWatchedFileBuildItem> watchedClass = io.quarkus.deployment.builditem.HotDeploymentWatchedFileBuildItem.class;
        io.quarkus.builder.BuildContext buildContext1 = org.mockito.Mockito.mock(io.quarkus.builder.BuildContext.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        io.quarkus.deployment.BuildProducerImpl<io.quarkus.deployment.builditem.HotDeploymentWatchedFileBuildItem> watchedProducer = new io.quarkus.deployment.BuildProducerImpl<io.quarkus.deployment.builditem.HotDeploymentWatchedFileBuildItem>(watchedClass, buildContext1);
        io.quarkus.runtime.LaunchMode mode = io.quarkus.runtime.LaunchMode.DEVELOPMENT;
        io.quarkus.dev.spi.DevModeType devMode = io.quarkus.dev.spi.DevModeType.REMOTE_SERVER_SIDE;
        java.util.Optional<io.quarkus.dev.spi.DevModeType> devModeOpt = java.util.Optional.of(devMode);
        io.quarkus.deployment.builditem.LaunchModeBuildItem launchItem = new io.quarkus.deployment.builditem.LaunchModeBuildItem(mode, devModeOpt, true, devModeOpt, true);
        io.quarkus.qute.runtime.QuteConfig config = org.mockito.Mockito.mock(io.quarkus.qute.runtime.QuteConfig.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        org.mockito.Mockito.doReturn(((java.util.regex.Pattern) (null))).when(config).templatePathExclude();
        // Undeclared exception!
        try {
            processor.collectTemplates(((io.quarkus.deployment.builditem.ApplicationArchivesBuildItem) (null)), curateOutcome, excludes, watchedProducer, pathProducer, ((io.quarkus.deployment.annotations.BuildProducer<io.quarkus.deployment.builditem.nativeimage.NativeImageResourceBuildItem>) (null)), config, ((io.quarkus.qute.deployment.TemplateRootsBuildItem) (null)), launchItem);
            org.junit.Assert.fail("Expecting exception: NullPointerException");
        } catch (java.lang.NullPointerException e) {
            // 
            // Cannot invoke \"io.quarkus.deployment.builditem.ApplicationArchivesBuildItem.getAllApplicationArchives()\" because \"applicationArchives\" is null
            // 
            org.evosuite.runtime.EvoAssertions.verifyException("io.quarkus.qute.deployment.QuteProcessor", e);
        }
    }

    @org.junit.Test(timeout = 4000)
    public void readTemplateContentThrowsUncheckedIOExceptionForInvalidPath() throws java.lang.Throwable {
        java.lang.String[] segments = new java.lang.String[6];
        segments[0] = "i-t";
        segments[1] = "i-t";
        segments[2] = "i-t";
        segments[3] = "i-t";
        segments[4] = "i-t";
        segments[5] = "i-t";
        java.nio.file.Path path = java.nio.file.Path.of("i-t", segments);
        java.nio.charset.Charset charset = java.nio.charset.Charset.defaultCharset();
        // Undeclared exception!
        try {
            io.quarkus.qute.deployment.QuteProcessor.readTemplateContent(path, charset);
            org.junit.Assert.fail("Expecting exception: UncheckedIOException");
        } catch (java.io.UncheckedIOException e) {
            // 
            // Unable to read the template content from path: i-t/i-t/i-t/i-t/i-t/i-t/i-t
            // 
            org.evosuite.runtime.EvoAssertions.verifyException("io.quarkus.qute.deployment.QuteProcessor", e);
        }
    }

    @org.junit.Test(timeout = 4000)
    public void turnLocationIntoQualifierProducesRegistrarBuildItem() throws java.lang.Throwable {
        io.quarkus.qute.deployment.QuteProcessor processor = new io.quarkus.qute.deployment.QuteProcessor();
        io.quarkus.arc.deployment.QualifierRegistrarBuildItem item = processor.turnLocationIntoQualifier();
        org.junit.Assert.assertNotNull(item);
    }

    @org.junit.Test(timeout = 4000)
    public void additionalBeansIsNotRemovable() throws java.lang.Throwable {
        io.quarkus.qute.deployment.QuteProcessor processor = new io.quarkus.qute.deployment.QuteProcessor();
        io.quarkus.arc.deployment.AdditionalBeanBuildItem additional = processor.additionalBeans();
        org.junit.Assert.assertFalse(additional.isRemovable());
    }

    @org.junit.Test(timeout = 4000)
    public void beanDefiningAnnotationsReturnsThreeItems() throws java.lang.Throwable {
        io.quarkus.qute.deployment.QuteProcessor processor = new io.quarkus.qute.deployment.QuteProcessor();
        java.util.List<io.quarkus.arc.deployment.BeanDefiningAnnotationBuildItem> annotations = processor.beanDefiningAnnotations();
        org.junit.Assert.assertEquals(3, annotations.size());
    }

    @org.junit.Test(timeout = 4000)
    public void defaultTemplateRootReturnsTemplatesPath() throws java.lang.Throwable {
        io.quarkus.qute.deployment.QuteProcessor processor = new io.quarkus.qute.deployment.QuteProcessor();
        io.quarkus.qute.deployment.TemplateRootBuildItem defaultRoot = processor.defaultTemplateRoot();
        java.util.List<io.quarkus.qute.deployment.TemplateRootBuildItem> roots = java.util.List.of(defaultRoot, defaultRoot, defaultRoot, defaultRoot, defaultRoot, defaultRoot, defaultRoot);
        processor.collectTemplateRoots(roots);
        org.junit.Assert.assertEquals("templates", defaultRoot.getPath());
    }

    @org.junit.Test(timeout = 4000)
    public void quteDebuggerBeanIsNotRemovable() throws java.lang.Throwable {
        io.quarkus.qute.deployment.QuteProcessor processor = new io.quarkus.qute.deployment.QuteProcessor();
        io.quarkus.arc.deployment.AdditionalBeanBuildItem debuggerBean = processor.quteDebuggerBean();
        org.junit.Assert.assertFalse(debuggerBean.isRemovable());
    }

    @org.junit.Test(timeout = 4000)
    public void featureBuildItemHasQuteName() throws java.lang.Throwable {
        io.quarkus.qute.deployment.QuteProcessor processor = new io.quarkus.qute.deployment.QuteProcessor();
        io.quarkus.deployment.builditem.FeatureBuildItem feature = processor.feature();
        org.junit.Assert.assertEquals("qute", feature.getName());
    }

    @org.junit.Test(timeout = 4000)
    public void initializeGeneratedClassesThrowsNPEWhenArcContainerNull() throws java.lang.Throwable {
        io.quarkus.qute.deployment.QuteProcessor processor = new io.quarkus.qute.deployment.QuteProcessor();
        io.quarkus.arc.runtime.BeanContainer container = org.mockito.Mockito.mock(io.quarkus.arc.runtime.BeanContainer.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        io.quarkus.arc.deployment.BeanContainerBuildItem containerItem = new io.quarkus.arc.deployment.BeanContainerBuildItem(container);
        io.quarkus.qute.runtime.QuteRecorder recorder = new io.quarkus.qute.runtime.QuteRecorder();
        java.util.LinkedList<io.quarkus.qute.deployment.GeneratedValueResolverBuildItem> resolvers = new java.util.LinkedList<io.quarkus.qute.deployment.GeneratedValueResolverBuildItem>();
        java.util.ArrayList<io.quarkus.qute.deployment.TemplateGlobalProviderBuildItem> providers = new java.util.ArrayList<io.quarkus.qute.deployment.TemplateGlobalProviderBuildItem>();
        // Undeclared exception!
        try {
            processor.initializeGeneratedClasses(containerItem, recorder, resolvers, providers);
            org.junit.Assert.fail("Expecting exception: NullPointerException");
        } catch (java.lang.NullPointerException e) {
            // 
            // Cannot invoke \"io.quarkus.arc.ArcContainer.instance(java.lang.Class, java.lang.annotation.Annotation[])\" because the return value of \"io.quarkus.arc.Arc.container()\" is null
            // 
            org.evosuite.runtime.EvoAssertions.verifyException("io.quarkus.qute.runtime.QuteRecorder", e);
        }
    }

    @org.junit.Test(timeout = 4000)
    public void generateValueResolversThrowsNPEWhenImplicitClassesNull() throws java.lang.Throwable {
        io.quarkus.qute.deployment.QuteProcessor processor = new io.quarkus.qute.deployment.QuteProcessor();
        io.quarkus.qute.runtime.QuteConfig config = org.mockito.Mockito.mock(io.quarkus.qute.runtime.QuteConfig.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        java.lang.Class<io.quarkus.deployment.builditem.GeneratedClassBuildItem> generatedClass = io.quarkus.deployment.builditem.GeneratedClassBuildItem.class;
        io.quarkus.builder.BuildContext buildContext = org.mockito.Mockito.mock(io.quarkus.builder.BuildContext.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        io.quarkus.deployment.BuildProducerImpl<io.quarkus.deployment.builditem.GeneratedClassBuildItem> generatedClassProducer = new io.quarkus.deployment.BuildProducerImpl<io.quarkus.deployment.builditem.GeneratedClassBuildItem>(generatedClass, buildContext);
        io.quarkus.deployment.annotations.BuildProducer<io.quarkus.deployment.builditem.GeneratedResourceBuildItem> generatedResourceProducer = ((io.quarkus.deployment.annotations.BuildProducer<io.quarkus.deployment.builditem.GeneratedResourceBuildItem>) (org.mockito.Mockito.mock(io.quarkus.deployment.annotations.BuildProducer.class, new org.evosuite.runtime.ViolatedAssumptionAnswer())));
        java.util.Stack<org.jboss.jandex.IndexView> views = new java.util.Stack<org.jboss.jandex.IndexView>();
        org.jboss.jandex.StackedIndex stackedIndex = org.jboss.jandex.StackedIndex.create(((java.util.List<org.jboss.jandex.IndexView>) (views)));
        org.jboss.jandex.EmptyIndex emptyIndex = org.jboss.jandex.EmptyIndex.INSTANCE;
        java.util.Set<org.jboss.jandex.DotName> subpackages = emptyIndex.getSubpackages("k,rWgE)MEPcase");
        io.quarkus.arc.deployment.BeanArchiveIndexBuildItem beanArchiveIndex = new io.quarkus.arc.deployment.BeanArchiveIndexBuildItem(stackedIndex, stackedIndex, subpackages);
        java.util.Vector<io.quarkus.qute.deployment.TemplateExtensionMethodBuildItem> extensionMethods = new java.util.Vector<io.quarkus.qute.deployment.TemplateExtensionMethodBuildItem>();
        java.util.LinkedList<io.quarkus.qute.deployment.TemplatesAnalysisBuildItem.TemplateAnalysis> analysesList = new java.util.LinkedList<io.quarkus.qute.deployment.TemplatesAnalysisBuildItem.TemplateAnalysis>();
        io.quarkus.qute.deployment.TemplatesAnalysisBuildItem analyses = new io.quarkus.qute.deployment.TemplatesAnalysisBuildItem(analysesList);
        java.util.LinkedList<io.quarkus.panache.common.deployment.PanacheEntityClassesBuildItem> panache = new java.util.LinkedList<io.quarkus.panache.common.deployment.PanacheEntityClassesBuildItem>();
        java.util.TreeSet<java.lang.String> entities = new java.util.TreeSet<java.lang.String>();
        io.quarkus.panache.common.deployment.PanacheEntityClassesBuildItem panacheEntities = new io.quarkus.panache.common.deployment.PanacheEntityClassesBuildItem(entities);
        panache.add(panacheEntities);
        java.util.Stack<io.quarkus.qute.deployment.TemplateDataBuildItem> templateData = new java.util.Stack<io.quarkus.qute.deployment.TemplateDataBuildItem>();
        java.util.concurrent.LinkedBlockingDeque<io.quarkus.qute.deployment.TemplateGlobalBuildItem> globalsQueue = new java.util.concurrent.LinkedBlockingDeque<io.quarkus.qute.deployment.TemplateGlobalBuildItem>();
        java.util.ArrayList<io.quarkus.qute.deployment.TemplateGlobalBuildItem> globals = new java.util.ArrayList<io.quarkus.qute.deployment.TemplateGlobalBuildItem>(globalsQueue);
        java.util.Stack<io.quarkus.qute.deployment.IncorrectExpressionBuildItem> incorrect = new java.util.Stack<io.quarkus.qute.deployment.IncorrectExpressionBuildItem>();
        java.util.LinkedList<io.quarkus.qute.deployment.IncorrectExpressionBuildItem> incorrectList = new java.util.LinkedList<io.quarkus.qute.deployment.IncorrectExpressionBuildItem>(incorrect);
        io.quarkus.deployment.builditem.LiveReloadBuildItem liveReload = new io.quarkus.deployment.builditem.LiveReloadBuildItem();
        io.quarkus.arc.deployment.CompletedApplicationClassPredicateBuildItem completedPredicate = org.mockito.Mockito.mock(io.quarkus.arc.deployment.CompletedApplicationClassPredicateBuildItem.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        io.quarkus.deployment.annotations.BuildProducer<io.quarkus.qute.deployment.GeneratedValueResolverBuildItem> valueResolverProducer = ((io.quarkus.deployment.annotations.BuildProducer<io.quarkus.qute.deployment.GeneratedValueResolverBuildItem>) (org.mockito.Mockito.mock(io.quarkus.deployment.annotations.BuildProducer.class, new org.evosuite.runtime.ViolatedAssumptionAnswer())));
        io.quarkus.deployment.annotations.BuildProducer<io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem> reflectiveProducer = ((io.quarkus.deployment.annotations.BuildProducer<io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem>) (org.mockito.Mockito.mock(io.quarkus.deployment.annotations.BuildProducer.class, new org.evosuite.runtime.ViolatedAssumptionAnswer())));
        io.quarkus.deployment.annotations.BuildProducer<io.quarkus.qute.deployment.TemplateGlobalProviderBuildItem> providerProducer = ((io.quarkus.deployment.annotations.BuildProducer<io.quarkus.qute.deployment.TemplateGlobalProviderBuildItem>) (org.mockito.Mockito.mock(io.quarkus.deployment.annotations.BuildProducer.class, new org.evosuite.runtime.ViolatedAssumptionAnswer())));
        // Undeclared exception!
        try {
            processor.generateValueResolvers(config, generatedClassProducer, generatedResourceProducer, beanArchiveIndex, ((io.quarkus.deployment.builditem.ApplicationArchivesBuildItem) (null)), extensionMethods, ((java.util.List<io.quarkus.qute.deployment.ImplicitValueResolverBuildItem>) (null)), analyses, panache, templateData, globals, incorrectList, liveReload, completedPredicate, valueResolverProducer, reflectiveProducer, providerProducer);
            org.junit.Assert.fail("Expecting exception: NullPointerException");
        } catch (java.lang.NullPointerException e) {
            // 
            // Cannot invoke \"java.util.List.iterator()\" because \"implicitClasses\" is null
            // 
            org.evosuite.runtime.EvoAssertions.verifyException("io.quarkus.qute.deployment.QuteProcessor", e);
        }
    }

    @org.junit.Test(timeout = 4000)
    public void generateValueResolversWorksWithEmptyInputs() throws java.lang.Throwable {
        org.jboss.jandex.EmptyIndex emptyIndex = org.jboss.jandex.EmptyIndex.INSTANCE;
        java.util.LinkedHashSet<org.jboss.jandex.DotName> additional = new java.util.LinkedHashSet<org.jboss.jandex.DotName>();
        io.quarkus.qute.deployment.QuteProcessor processor = new io.quarkus.qute.deployment.QuteProcessor();
        io.quarkus.qute.runtime.QuteConfig config = org.mockito.Mockito.mock(io.quarkus.qute.runtime.QuteConfig.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        java.lang.Class<io.quarkus.deployment.builditem.GeneratedClassBuildItem> generatedClass = io.quarkus.deployment.builditem.GeneratedClassBuildItem.class;
        io.quarkus.builder.BuildContext buildContext = org.mockito.Mockito.mock(io.quarkus.builder.BuildContext.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        io.quarkus.deployment.BuildProducerImpl<io.quarkus.deployment.builditem.GeneratedClassBuildItem> generatedClassProducer = new io.quarkus.deployment.BuildProducerImpl<io.quarkus.deployment.builditem.GeneratedClassBuildItem>(generatedClass, buildContext);
        io.quarkus.deployment.annotations.BuildProducer<io.quarkus.deployment.builditem.GeneratedResourceBuildItem> generatedResourceProducer = ((io.quarkus.deployment.annotations.BuildProducer<io.quarkus.deployment.builditem.GeneratedResourceBuildItem>) (org.mockito.Mockito.mock(io.quarkus.deployment.annotations.BuildProducer.class, new org.evosuite.runtime.ViolatedAssumptionAnswer())));
        io.quarkus.arc.deployment.BeanArchiveIndexBuildItem beanArchiveIndex = new io.quarkus.arc.deployment.BeanArchiveIndexBuildItem(emptyIndex, emptyIndex, additional);
        java.util.Stack<io.quarkus.qute.deployment.TemplateExtensionMethodBuildItem> extensionMethods = new java.util.Stack<io.quarkus.qute.deployment.TemplateExtensionMethodBuildItem>();
        java.util.Stack<io.quarkus.qute.deployment.ImplicitValueResolverBuildItem> implicitResolvers = new java.util.Stack<io.quarkus.qute.deployment.ImplicitValueResolverBuildItem>();
        java.util.LinkedList<io.quarkus.qute.deployment.TemplatesAnalysisBuildItem.TemplateAnalysis> analysesList = new java.util.LinkedList<io.quarkus.qute.deployment.TemplatesAnalysisBuildItem.TemplateAnalysis>();
        io.quarkus.qute.deployment.TemplatesAnalysisBuildItem analyses = new io.quarkus.qute.deployment.TemplatesAnalysisBuildItem(analysesList);
        java.util.LinkedList<io.quarkus.panache.common.deployment.PanacheEntityClassesBuildItem> panache = new java.util.LinkedList<io.quarkus.panache.common.deployment.PanacheEntityClassesBuildItem>();
        java.util.Vector<io.quarkus.qute.deployment.TemplateDataBuildItem> templateData = new java.util.Vector<io.quarkus.qute.deployment.TemplateDataBuildItem>();
        java.util.ArrayList<io.quarkus.qute.deployment.TemplateGlobalBuildItem> globals = new java.util.ArrayList<io.quarkus.qute.deployment.TemplateGlobalBuildItem>();
        java.util.Stack<io.quarkus.qute.deployment.IncorrectExpressionBuildItem> incorrect = new java.util.Stack<io.quarkus.qute.deployment.IncorrectExpressionBuildItem>();
        io.quarkus.deployment.builditem.LiveReloadBuildItem liveReload = new io.quarkus.deployment.builditem.LiveReloadBuildItem();
        io.quarkus.deployment.annotations.BuildProducer<io.quarkus.qute.deployment.GeneratedValueResolverBuildItem> valueResolverProducer = ((io.quarkus.deployment.annotations.BuildProducer<io.quarkus.qute.deployment.GeneratedValueResolverBuildItem>) (org.mockito.Mockito.mock(io.quarkus.deployment.annotations.BuildProducer.class, new org.evosuite.runtime.ViolatedAssumptionAnswer())));
        io.quarkus.deployment.annotations.BuildProducer<io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem> reflectiveProducer = ((io.quarkus.deployment.annotations.BuildProducer<io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem>) (org.mockito.Mockito.mock(io.quarkus.deployment.annotations.BuildProducer.class, new org.evosuite.runtime.ViolatedAssumptionAnswer())));
        io.quarkus.deployment.annotations.BuildProducer<io.quarkus.qute.deployment.TemplateGlobalProviderBuildItem> providerProducer = ((io.quarkus.deployment.annotations.BuildProducer<io.quarkus.qute.deployment.TemplateGlobalProviderBuildItem>) (org.mockito.Mockito.mock(io.quarkus.deployment.annotations.BuildProducer.class, new org.evosuite.runtime.ViolatedAssumptionAnswer())));
        processor.generateValueResolvers(config, generatedClassProducer, generatedResourceProducer, beanArchiveIndex, ((io.quarkus.deployment.builditem.ApplicationArchivesBuildItem) (null)), extensionMethods, implicitResolvers, analyses, panache, templateData, globals, incorrect, liveReload, ((io.quarkus.arc.deployment.CompletedApplicationClassPredicateBuildItem) (null)), valueResolverProducer, reflectiveProducer, providerProducer);
        io.quarkus.qute.runtime.QuteConfig config2 = org.mockito.Mockito.mock(io.quarkus.qute.runtime.QuteConfig.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        java.lang.Class<io.quarkus.deployment.builditem.GeneratedResourceBuildItem> resourceClass = io.quarkus.deployment.builditem.GeneratedResourceBuildItem.class;
        io.quarkus.builder.BuildContext buildContext1 = org.mockito.Mockito.mock(io.quarkus.builder.BuildContext.class, new org.evosuite.runtime.ViolatedAssumptionAnswer());
        io.quarkus.deployment.BuildProducerImpl<io.quarkus.deployment.builditem.GeneratedResourceBuildItem> generatedResourceProducer2 = new io.quarkus.deployment.BuildProducerImpl<io.quarkus.deployment.builditem.GeneratedResourceBuildItem>(resourceClass, buildContext1);
        io.quarkus.deployment.annotations.BuildProducer<io.quarkus.qute.deployment.GeneratedValueResolverBuildItem> valueResolverProducer2 = ((io.quarkus.deployment.annotations.BuildProducer<io.quarkus.qute.deployment.GeneratedValueResolverBuildItem>) (org.mockito.Mockito.mock(io.quarkus.deployment.annotations.BuildProducer.class, new org.evosuite.runtime.ViolatedAssumptionAnswer())));
        io.quarkus.deployment.annotations.BuildProducer<io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem> reflectiveProducer2 = ((io.quarkus.deployment.annotations.BuildProducer<io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem>) (org.mockito.Mockito.mock(io.quarkus.deployment.annotations.BuildProducer.class, new org.evosuite.runtime.ViolatedAssumptionAnswer())));
        io.quarkus.deployment.annotations.BuildProducer<io.quarkus.qute.deployment.TemplateGlobalProviderBuildItem> providerProducer2 = ((io.quarkus.deployment.annotations.BuildProducer<io.quarkus.qute.deployment.TemplateGlobalProviderBuildItem>) (org.mockito.Mockito.mock(io.quarkus.deployment.annotations.BuildProducer.class, new org.evosuite.runtime.ViolatedAssumptionAnswer())));
        processor.generateValueResolvers(config2, generatedClassProducer, generatedResourceProducer2, beanArchiveIndex, ((io.quarkus.deployment.builditem.ApplicationArchivesBuildItem) (null)), extensionMethods, implicitResolvers, analyses, panache, templateData, globals, incorrect, liveReload, ((io.quarkus.arc.deployment.CompletedApplicationClassPredicateBuildItem) (null)), valueResolverProducer2, reflectiveProducer2, providerProducer2);
        org.junit.Assert.assertEquals(0, incorrect.size());
    }

    @org.junit.Test(timeout = 4000)
    public void matchResultGetTypeParametersThrowsNPEWhenClassNull() throws java.lang.Throwable {
        org.jboss.jandex.EmptyIndex emptyIndex = org.jboss.jandex.EmptyIndex.INSTANCE;
        io.quarkus.qute.deployment.Types.AssignabilityCheck assignability = new io.quarkus.qute.deployment.Types.AssignabilityCheck(emptyIndex);
        io.quarkus.qute.deployment.QuteProcessor.MatchResult matchResult = new io.quarkus.qute.deployment.QuteProcessor.MatchResult(assignability);
        // Undeclared exception!
        try {
            matchResult.getTypeParameters();
            org.junit.Assert.fail("Expecting exception: NullPointerException");
        } catch (java.lang.NullPointerException e) {
            // 
            // Cannot invoke \"org.jboss.jandex.ClassInfo.typeParameters()\" because \"this.clazz\" is null
            // 
            org.evosuite.runtime.EvoAssertions.verifyException("io.quarkus.qute.deployment.QuteProcessor$MatchResult", e);
        }
    }

    @org.junit.Test(timeout = 4000)
    public void matchResultTypeReturnsNullByDefault() throws java.lang.Throwable {
        org.jboss.jandex.EmptyIndex emptyIndex = org.jboss.jandex.EmptyIndex.INSTANCE;
        io.quarkus.qute.deployment.Types.AssignabilityCheck assignability = new io.quarkus.qute.deployment.Types.AssignabilityCheck(emptyIndex);
        io.quarkus.qute.deployment.QuteProcessor.MatchResult matchResult = new io.quarkus.qute.deployment.QuteProcessor.MatchResult(assignability);
        org.jboss.jandex.Type type = matchResult.type();
        org.junit.Assert.assertNull(type);
    }

    @org.junit.Test(timeout = 4000)
    public void matchResultClearValuesDoesNotThrow() throws java.lang.Throwable {
        java.util.HashMap<org.jboss.jandex.DotName, java.util.List<org.jboss.jandex.AnnotationInstance>> annotations = new java.util.HashMap<org.jboss.jandex.DotName, java.util.List<org.jboss.jandex.AnnotationInstance>>();
        java.util.HashMap<org.jboss.jandex.DotName, java.util.List<org.jboss.jandex.ClassInfo>> map = new java.util.HashMap<org.jboss.jandex.DotName, java.util.List<org.jboss.jandex.ClassInfo>>();
        java.util.HashMap<org.jboss.jandex.DotName, org.jboss.jandex.ClassInfo> classes = new java.util.HashMap<org.jboss.jandex.DotName, org.jboss.jandex.ClassInfo>();
        org.jboss.jandex.Index index = org.jboss.jandex.Index.create(((java.util.Map<org.jboss.jandex.DotName, java.util.List<org.jboss.jandex.AnnotationInstance>>) (annotations)), ((java.util.Map<org.jboss.jandex.DotName, java.util.List<org.jboss.jandex.ClassInfo>>) (map)), ((java.util.Map<org.jboss.jandex.DotName, java.util.List<org.jboss.jandex.ClassInfo>>) (map)), ((java.util.Map<org.jboss.jandex.DotName, java.util.List<org.jboss.jandex.ClassInfo>>) (map)), ((java.util.Map<org.jboss.jandex.DotName, org.jboss.jandex.ClassInfo>) (classes)), ((java.util.Map<org.jboss.jandex.DotName, java.util.List<org.jboss.jandex.ClassInfo>>) (map)));
        io.quarkus.qute.deployment.Types.AssignabilityCheck assignability = new io.quarkus.qute.deployment.Types.AssignabilityCheck(index);
        io.quarkus.qute.deployment.QuteProcessor.MatchResult matchResult = new io.quarkus.qute.deployment.QuteProcessor.MatchResult(assignability);
        matchResult.clearValues();
    }

    @org.junit.Test(timeout = 4000)
    public void matchResultClazzReturnsNullByDefault() throws java.lang.Throwable {
        io.quarkus.qute.deployment.Types.AssignabilityCheck assignability = new io.quarkus.qute.deployment.Types.AssignabilityCheck(((org.jboss.jandex.IndexView) (null)));
        io.quarkus.qute.deployment.QuteProcessor.MatchResult matchResult = new io.quarkus.qute.deployment.QuteProcessor.MatchResult(assignability);
        org.jboss.jandex.ClassInfo clazz = matchResult.clazz();
        org.junit.Assert.assertNull(clazz);
    }

    @org.junit.Test(timeout = 4000)
    public void templateDataIgnorePatternIgnoresBazAndGetters() throws java.lang.Throwable {
        java.util.List<java.lang.String> names = java.util.List.of("foo", "bar");
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(io.quarkus.qute.deployment.QuteProcessor.buildIgnorePattern(names));
        org.junit.Assert.assertTrue(p.matcher("baz").matches());
        org.junit.Assert.assertTrue(p.matcher("getFoo").matches());
        for (java.lang.String name : names) {
            org.junit.Assert.assertFalse(p.matcher(name).matches());
        }
        try {
            io.quarkus.qute.deployment.QuteProcessor.buildIgnorePattern(java.util.List.of());
            org.junit.Assert.fail("Expecting exception: IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException e) {
            org.evosuite.runtime.EvoAssertions.verifyException("io.quarkus.qute.deployment.QuteProcessor", e);
        }
    }

    @org.junit.Test(timeout = 4000)
    public void collectNamespaceExpressionsParsesNamespacesFromTemplate() throws java.lang.Throwable {
        io.quarkus.qute.Engine engine = io.quarkus.qute.Engine.builder().build();
        io.quarkus.qute.Template template = engine.parse("{msg:hello} {msg2:hello_alpha} {foo:baz.get(foo:bar)}");
        io.quarkus.qute.deployment.TemplatesAnalysisBuildItem.TemplateAnalysis analysis = new io.quarkus.qute.deployment.TemplatesAnalysisBuildItem.TemplateAnalysis("foo", template, null);
        java.util.Set<io.quarkus.qute.Expression> msg = io.quarkus.qute.deployment.QuteProcessor.collectNamespaceExpressions(analysis, "msg");
        org.junit.Assert.assertEquals(1, msg.size());
        org.junit.Assert.assertEquals("msg:hello", msg.iterator().next().toOriginalString());
        java.util.Set<io.quarkus.qute.Expression> msg2 = io.quarkus.qute.deployment.QuteProcessor.collectNamespaceExpressions(analysis, "msg2");
        org.junit.Assert.assertEquals(1, msg2.size());
        org.junit.Assert.assertEquals("msg2:hello_alpha", msg2.iterator().next().toOriginalString());
        java.util.Set<io.quarkus.qute.Expression> foo = io.quarkus.qute.deployment.QuteProcessor.collectNamespaceExpressions(analysis, "foo");
        org.junit.Assert.assertEquals(2, foo.size());
        for (io.quarkus.qute.Expression fooExpr : foo) {
            java.lang.String s = fooExpr.toOriginalString();
            org.junit.Assert.assertTrue(s.equals("foo:bar") || s.equals("foo:baz.get(foo:bar)"));
        }
    }
}