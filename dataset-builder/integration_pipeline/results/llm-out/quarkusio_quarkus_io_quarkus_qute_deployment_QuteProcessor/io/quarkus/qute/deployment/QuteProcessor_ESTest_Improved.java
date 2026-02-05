package io.quarkus.qute.deployment;
import QuteProcessor_ESTest_scaffolding;
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
import io.quarkus.qute.Expression;
import io.quarkus.qute.InsertSectionHelper;
import io.quarkus.qute.TemplateNode;
import io.quarkus.qute.WhenSectionHelper;
import io.quarkus.qute.runtime.QuteConfig;
import io.quarkus.qute.runtime.QuteRecorder;
import io.quarkus.qute.runtime.QuteTestModeConfig;
import io.quarkus.runtime.LaunchMode;
import java.io.File;
import java.io.UncheckedIOException;
import java.lang.reflect.Array;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.time.ZoneId;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import org.aesh.command.impl.invocation.DefaultCommandInvocation;
import org.aesh.command.impl.registry.MutableCommandRegistryImpl;
import org.aesh.readline.alias.AliasManager;
import org.evosuite.runtime.ViolatedAssumptionAnswer;
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
import org.junit.Test;
import static org.evosuite.runtime.EvoAssertions.verifyException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
public class QuteProcessor_ESTest_Improved extends QuteProcessor_ESTest_scaffolding {
    @Test(timeout = 4000)
    public void isEmptyReturnsTrueForNewMatchResult() throws Throwable {
        EmptyIndex emptyIndex = EmptyIndex.INSTANCE;
        Types.AssignabilityCheck assignabilityCheck = new Types.AssignabilityCheck(emptyIndex);
        QuteProcessor.MatchResult matchResult = new QuteProcessor.MatchResult(assignabilityCheck);
        boolean isEmpty = matchResult.isEmpty();
        assertTrue(isEmpty);
    }

    @Test(timeout = 4000)
    public void autoExtractTypeDoesNotThrowOnNullAssignabilityCheck() throws Throwable {
        QuteProcessor.MatchResult matchResult = new QuteProcessor.MatchResult(((Types.AssignabilityCheck) (null)));
        matchResult.autoExtractType();
    }

    @Test(timeout = 4000)
    public void collectNamespaceExpressionsWithNullAnalysisThrowsNPE() throws Throwable {
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
    public void validateExpressionsWithNullBeanDeploymentCollectionsThrowsNPE() throws Throwable {
        QuteProcessor processor = new QuteProcessor();
        AlwaysFalsePredicate<TypeCheckExcludeBuildItem.TypeCheck> alwaysFalse = new AlwaysFalsePredicate<TypeCheckExcludeBuildItem.TypeCheck>();
        TypeCheckExcludeBuildItem excludeItem = new TypeCheckExcludeBuildItem(alwaysFalse, true);
        List<TypeCheckExcludeBuildItem> excludes = List.of(excludeItem, excludeItem, excludeItem, excludeItem, excludeItem);
        Class<IncorrectExpressionBuildItem> incorrectClass = IncorrectExpressionBuildItem.class;
        BuildContext buildContext = mock(BuildContext.class, new ViolatedAssumptionAnswer());
        BuildProducerImpl<IncorrectExpressionBuildItem> incorrectProducer = new BuildProducerImpl<IncorrectExpressionBuildItem>(incorrectClass, buildContext);
        BeanDeployment beanDeployment = mock(BeanDeployment.class, new ViolatedAssumptionAnswer());
        doReturn(((BeanResolver) (null))).when(beanDeployment).getBeanResolver();
        doReturn(((Collection) (null))).when(beanDeployment).getBeans();
        doReturn(((Collection) (null))).when(beanDeployment).getInjectionPoints();
        doReturn(((Collection) (null))).when(beanDeployment).getObservers();
        SynthesisFinishedBuildItem synthesisFinished = new SynthesisFinishedBuildItem(beanDeployment);
        ArrayList<CheckedTemplateBuildItem> checkedTemplates = new ArrayList<CheckedTemplateBuildItem>();
        Stack<TemplateDataBuildItem> templateDataItems = new Stack<TemplateDataBuildItem>();
        TemplatesAnalysisBuildItem analyses = new TemplatesAnalysisBuildItem(((List<TemplatesAnalysisBuildItem.TemplateAnalysis>) (null)));
        LinkedHashSet<DotName> beanExclusions = new LinkedHashSet<DotName>();
        BeanArchiveIndexBuildItem beanArchiveIndex = new BeanArchiveIndexBuildItem(((IndexView) (null)), ((IndexView) (null)), beanExclusions);
        ArrayList<TemplateExtensionMethodBuildItem> extensionMethods = new ArrayList<TemplateExtensionMethodBuildItem>();
        Class<ImplicitValueResolverBuildItem> implicitClass = ImplicitValueResolverBuildItem.class;
        BuildContext buildContext1 = mock(BuildContext.class, new ViolatedAssumptionAnswer());
        BuildProducerImpl<ImplicitValueResolverBuildItem> implicitProducer = new BuildProducerImpl<ImplicitValueResolverBuildItem>(implicitClass, buildContext1);
        NativeConfig nativeConfig = mock(NativeConfig.class, new ViolatedAssumptionAnswer());
        // Undeclared exception!
        try {
            processor.validateExpressions(analyses, beanArchiveIndex, extensionMethods, excludes, incorrectProducer, implicitProducer, ((BuildProducer<TemplateExpressionMatchesBuildItem>) (null)), synthesisFinished, checkedTemplates, templateDataItems, ((QuteConfig) (null)), nativeConfig, ((List<TemplateGlobalBuildItem>) (null)));
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            // 
            // Beans collection is null
            // 
            verifyException("java.util.Objects", e);
        }
    }

    @Test(timeout = 4000)
    public void initializeWithValidParamsButNonProxyRecorderThrowsIAE() throws Throwable {
        QuteProcessor processor = new QuteProcessor();
        Class<SyntheticBeanBuildItem> syntheticClass = SyntheticBeanBuildItem.class;
        BuildContext buildContext = mock(BuildContext.class, new ViolatedAssumptionAnswer());
        BuildProducerImpl<SyntheticBeanBuildItem> syntheticProducer = new BuildProducerImpl<SyntheticBeanBuildItem>(syntheticClass, buildContext);
        QuteRecorder recorder = new QuteRecorder();
        Stack<TemplatePathBuildItem> templatePaths = new Stack<TemplatePathBuildItem>();
        EffectiveTemplatePathsBuildItem effectivePaths = new EffectiveTemplatePathsBuildItem(templatePaths);
        HashMap<String, List<String>> variants = new HashMap<String, List<String>>();
        TemplateVariantsBuildItem templateVariants = new TemplateVariantsBuildItem(variants);
        Optional<TemplateVariantsBuildItem> optionalVariants = Optional.of(templateVariants);
        LinkedList<TemplateRootBuildItem> roots = new LinkedList<TemplateRootBuildItem>();
        TemplateRootsBuildItem templateRoots = processor.collectTemplateRoots(roots);
        Stack<TemplatePathExcludeBuildItem> excludes = new Stack<TemplatePathExcludeBuildItem>();
        TemplatePathExcludeBuildItem exclude = new TemplatePathExcludeBuildItem(((String) (null)));
        excludes.add(exclude);
        // Undeclared exception!
        try {
            processor.initialize(syntheticProducer, recorder, effectivePaths, optionalVariants, templateRoots, excludes);
            fail("Expecting exception: IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // 
            // The object is not a proxy returned from a recorder method: io.quarkus.qute.runtime.QuteRecorder$1@0000000011
            // 
            verifyException("io.quarkus.arc.deployment.SyntheticBeanBuildItem$ExtendedBeanConfigurator", e);
        }
    }

    @Test(timeout = 4000)
    public void collectTemplateGlobalsDoesNotFailOnEmptyIndex() throws Throwable {
        QuteProcessor processor = new QuteProcessor();
        ArrayList<IndexView> views = new ArrayList<IndexView>();
        StackedIndex stackedIndex = StackedIndex.create(((List<IndexView>) (views)));
        TreeSet<DotName> subpackages = new TreeSet<DotName>();
        BeanArchiveIndexBuildItem beanArchiveIndex = new BeanArchiveIndexBuildItem(stackedIndex, stackedIndex, subpackages);
        Class<TemplateGlobalBuildItem> globalClass = TemplateGlobalBuildItem.class;
        BuildContext buildContext = mock(BuildContext.class, new ViolatedAssumptionAnswer());
        BuildProducerImpl<TemplateGlobalBuildItem> globalProducer = new BuildProducerImpl<TemplateGlobalBuildItem>(globalClass, buildContext);
        processor.collectTemplateGlobals(beanArchiveIndex, globalProducer);
    }

    @Test(timeout = 4000)
    public void collectNamespaceExpressionsOnExpressionNullThrowsNPE() throws Throwable {
        TreeSet<Expression> foundExpressions = new TreeSet<Expression>();
        // Undeclared exception!
        try {
            QuteProcessor.collectNamespaceExpressions(((Expression) (null)), foundExpressions, "kf46");
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            // 
            // Cannot invoke \"io.quarkus.qute.Expression.isLiteral()\" because \"expression\" is null
            // 
            verifyException("io.quarkus.qute.deployment.QuteProcessor", e);
        }
    }

    @Test(timeout = 4000)
    public void staticsFilterReturnsFalseForNonStaticMethod() throws Throwable {
        Class<Object> objectClass = Object.class;
        ClassInfo classInfo = Index.singleClass(objectClass);
        TypeVariable returnType = TypeVariable.create("IN78 +@gT{<^b8");
        String[] params = new String[1];
        params[0] = "IN78 +@gT{<^b8";
        Type[] paramTypes = new Type[0];
        TypeVariable[] typeParams = new TypeVariable[2];
        MethodInfo method = MethodInfo.create(classInfo, "IN78 +@gT{<^b8", params, paramTypes, ((Type) (returnType)), ((short) (-6681)), typeParams, paramTypes);
        boolean accept = QuteProcessor.staticsFilter(method);
        assertFalse(accept);
    }

    @Test(timeout = 4000)
    public void readTemplateContentReturnsEmptyStringForEmptyFile() throws Throwable {
        File tempFile = File.createTempFile("k,rWgE)MEPis", "k,rWgE)MEPis", ((File) (null)));
        Path path = tempFile.toPath();
        Charset charset = Charset.defaultCharset();
        String content = QuteProcessor.readTemplateContent(path, charset);
        assertEquals("", content);
    }

    @Test(timeout = 4000)
    public void findTemplatePathReturnsNullWhenNotPresent() throws Throwable {
        LinkedList<TemplatesAnalysisBuildItem.TemplateAnalysis> analysesList = new LinkedList<TemplatesAnalysisBuildItem.TemplateAnalysis>();
        TemplatesAnalysisBuildItem analyses = new TemplatesAnalysisBuildItem(analysesList);
        String path = QuteProcessor.findTemplatePath(analyses, "k@q)HDU<bJ_Kx_");
        assertNull(path);
    }

    @Test(timeout = 4000)
    public void extractMatchTypeReturnsMatchingTypeWhenFound() throws Throwable {
        LinkedHashSet<Type> candidates = new LinkedHashSet<Type>();
        WildcardType wildcard = WildcardType.UNBOUNDED;
        candidates.add(wildcard);
        DotName typeName = wildcard.name();
        Function<Type, Type> identity = Function.identity();
        Type matched = QuteProcessor.extractMatchType(candidates, typeName, identity);
        assertSame(wildcard, matched);
    }

    @Test(timeout = 4000)
    public void collectTemplateVariantsThrowsSIOOBEOnInvalidSuffixHandling() throws Throwable {
        QuteProcessor processor = new QuteProcessor();
        TemplatePathBuildItem first = mock(TemplatePathBuildItem.class, new ViolatedAssumptionAnswer());
        doReturn("MW", "MW", "4uX5{").when(first).getPath();
        TemplatePathBuildItem second = mock(TemplatePathBuildItem.class, new ViolatedAssumptionAnswer());
        doReturn("NOSECTION_HELPER_FOUNDelse", "MW", "8O", "NOSECTION_HELPER_FOUNDelse").when(second).getPath();
        List<TemplatePathBuildItem> paths = List.of(first, second, second, first, second, first, second);
        EffectiveTemplatePathsBuildItem effectivePaths = new EffectiveTemplatePathsBuildItem(paths);
        LinkedList<String> suffixes = new LinkedList<String>();
        suffixes.add("8O");
        QuteConfig config = mock(QuteConfig.class, new ViolatedAssumptionAnswer());
        doReturn(suffixes).when(config).suffixes();
        // Undeclared exception!
        try {
            processor.collectTemplateVariants(effectivePaths, config);
            fail("Expecting exception: StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException e) {
        }
    }

    @Test(timeout = 4000)
    public void processTemplateErrorsNoIncorrectExpressionsProducesNoServiceStart() throws Throwable {
        Stack<TemplatesAnalysisBuildItem.TemplateAnalysis> analysesList = new Stack<TemplatesAnalysisBuildItem.TemplateAnalysis>();
        TemplatesAnalysisBuildItem analyses = new TemplatesAnalysisBuildItem(analysesList);
        QuteProcessor processor = new QuteProcessor();
        Vector<IncorrectExpressionBuildItem> incorrect = new Vector<IncorrectExpressionBuildItem>();
        processor.processTemplateErrors(analyses, incorrect, ((BuildProducer<ServiceStartBuildItem>) (null)));
        assertEquals(0, incorrect.size());
    }

    @Test(timeout = 4000)
    public void addSingletonToNamedRecordsHasExpectedReason() throws Throwable {
        QuteProcessor processor = new QuteProcessor();
        AutoAddScopeBuildItem item = processor.addSingletonToNamedRecords();
        assertEquals("Found Java record annotated with @Named", item.getReason());
    }

    @Test(timeout = 4000)
    public void firstPassLookupConfigDeclaredMembersOnlyHonorsOverrideTrue() throws Throwable {
        Class<TypeCheckExcludeBuildItem> target = TypeCheckExcludeBuildItem.class;
        BuildContext buildContext = mock(BuildContext.class, new ViolatedAssumptionAnswer());
        BuildProducerImpl<TypeCheckExcludeBuildItem> producer = new BuildProducerImpl<TypeCheckExcludeBuildItem>(target, buildContext);
        Predicate<AnnotationTarget> filter = Predicate.isEqual(producer);
        Boolean declaredOnly = Boolean.TRUE;
        QuteProcessor.FirstPassJavaMemberLookupConfig config = new QuteProcessor.FirstPassJavaMemberLookupConfig(((QuteProcessor.JavaMemberLookupConfig) (null)), filter, declaredOnly);
        boolean result = config.declaredMembersOnly();
        assertTrue(result);
    }

    @Test(timeout = 4000)
    public void firstPassLookupConfigDeclaredMembersOnlyTrueWhenNextTrue() throws Throwable {
        IndexView[] empty = new IndexView[0];
        StackedIndex index = StackedIndex.create(empty);
        Object target = new Object();
        Predicate<AnnotationTarget> filter = Predicate.isEqual(target);
        QuteProcessor.FixedJavaMemberLookupConfig next = new QuteProcessor.FixedJavaMemberLookupConfig(index, filter, true);
        QuteProcessor.FirstPassJavaMemberLookupConfig firstPass = new QuteProcessor.FirstPassJavaMemberLookupConfig(next, filter, ((Boolean) (null)));
        boolean declaredOnly = firstPass.declaredMembersOnly();
        assertTrue(declaredOnly);
    }

    @Test(timeout = 4000)
    public void firstPassLookupConfigFilterDelegatesToNext() throws Throwable {
        Class<Object> objectClass = Object.class;
        ClassInfo classInfo = Index.singleClass(objectClass);
        Map<DotName, List<AnnotationInstance>> annotations = classInfo.annotationsMap();
        HashMap<DotName, List<ClassInfo>> map = new HashMap<DotName, List<ClassInfo>>();
        HashMap<DotName, ClassInfo> classes = new HashMap<DotName, ClassInfo>();
        Index index = Index.create(annotations, ((Map<DotName, List<ClassInfo>>) (map)), ((Map<DotName, List<ClassInfo>>) (map)), ((Map<DotName, ClassInfo>) (classes)));
        Predicate<AnnotationTarget> filter = Predicate.isEqual(annotations);
        QuteProcessor.FixedJavaMemberLookupConfig next = new QuteProcessor.FixedJavaMemberLookupConfig(index, filter, true);
        Boolean declaredOnly = Boolean.FALSE;
        QuteProcessor.FirstPassJavaMemberLookupConfig firstPass = new QuteProcessor.FirstPassJavaMemberLookupConfig(next, filter, declaredOnly);
        firstPass.filter();
        assertTrue(next.declaredMembersOnly());
    }

    @Test(timeout = 4000)
    public void getNameOnInjectionPointThrowsIAEForInvalidType() throws Throwable {
        ClassType characterType = ClassType.CHARACTER_CLASS;
        LinkedHashSet<AnnotationInstance> qualifiers = new LinkedHashSet<AnnotationInstance>();
        InjectionPointInfo.TypeAndQualifiers tq = new InjectionPointInfo.TypeAndQualifiers(characterType, qualifiers);
        InjectionPointInfo ip = InjectionPointInfo.fromSyntheticInjectionPoint(tq);
        // Undeclared exception!
        try {
            QuteProcessor.getName(ip);
            fail("Expecting exception: IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // 
            // no message in exception (getMessage() returned null)
            // 
            verifyException("io.quarkus.qute.deployment.QuteProcessor", e);
        }
    }

    @Test(timeout = 4000)
    public void collectNamespaceExpressionsThrowsNPEWhenTemplateAnalysisIsNullInList() throws Throwable {
        Stack<TemplatesAnalysisBuildItem.TemplateAnalysis> analysesList = new Stack<TemplatesAnalysisBuildItem.TemplateAnalysis>();
        analysesList.setSize(5);
        TemplatesAnalysisBuildItem analyses = new TemplatesAnalysisBuildItem(analysesList);
        // Undeclared exception!
        try {
            QuteProcessor.collectNamespaceExpressions(analyses, "");
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            // 
            // Cannot read field \"expressions\" because \"analysis\" is null
            // 
            verifyException("io.quarkus.qute.deployment.QuteProcessor", e);
        }
    }

    @Test(timeout = 4000)
    public void collectNamespaceExpressionsWithEmptyAnalysesReturnsEmptyMap() throws Throwable {
        Vector<TemplatesAnalysisBuildItem.TemplateAnalysis> analysesList = new Vector<TemplatesAnalysisBuildItem.TemplateAnalysis>();
        TemplatesAnalysisBuildItem analyses = new TemplatesAnalysisBuildItem(analysesList);
        Map<TemplatesAnalysisBuildItem.TemplateAnalysis, Set<Expression>> result = QuteProcessor.collectNamespaceExpressions(analyses, "");
        assertEquals(0, result.size());
    }

    @Test(timeout = 4000)
    public void validateTemplateDataNamespacesOnEmptyInputNoErrors() throws Throwable {
        QuteProcessor processor = new QuteProcessor();
        LinkedList<TemplateDataBuildItem> dataItems = new LinkedList<TemplateDataBuildItem>();
        Class<ServiceStartBuildItem> serviceStartClass = ServiceStartBuildItem.class;
        BuildContext buildContext = mock(BuildContext.class, new ViolatedAssumptionAnswer());
        BuildProducerImpl<ServiceStartBuildItem> serviceStartProducer = new BuildProducerImpl<ServiceStartBuildItem>(serviceStartClass, buildContext);
        processor.validateTemplateDataNamespaces(dataItems, serviceStartProducer);
        assertEquals(0, dataItems.size());
    }

    @Test(timeout = 4000)
    public void collectTemplateDataAnnotationsHandlesEmptyIndex() throws Throwable {
        QuteProcessor processor = new QuteProcessor();
        IndexView[] empty = new IndexView[0];
        StackedIndex stackedIndex = StackedIndex.create(empty);
        HashSet<DotName> additional = new HashSet<DotName>(387);
        BeanArchiveIndexBuildItem beanArchiveIndex = new BeanArchiveIndexBuildItem(stackedIndex, stackedIndex, additional);
        Class<TemplateDataBuildItem> dataClass = TemplateDataBuildItem.class;
        BuildContext buildContext = mock(BuildContext.class, new ViolatedAssumptionAnswer());
        BuildProducerImpl<TemplateDataBuildItem> dataProducer = new BuildProducerImpl<TemplateDataBuildItem>(dataClass, buildContext);
        processor.collectTemplateDataAnnotations(beanArchiveIndex, dataProducer);
    }

    @Test(timeout = 4000)
    public void matchResultIsClassTrueForClassType() throws Throwable {
        QuteProcessor.MatchResult matchResult = new QuteProcessor.MatchResult(((Types.AssignabilityCheck) (null)));
        ClassType classType = ClassType.ANNOTATION_TYPE;
        matchResult.setValues(((ClassInfo) (null)), classType);
        boolean isClass = matchResult.isClass();
        assertTrue(isClass);
    }

    @Test(timeout = 4000)
    public void matchResultIsClassFalseWhenNoTypeSet() throws Throwable {
        EmptyIndex emptyIndex = EmptyIndex.INSTANCE;
        Types.AssignabilityCheck assignability = new Types.AssignabilityCheck(emptyIndex);
        QuteProcessor.MatchResult matchResult = new QuteProcessor.MatchResult(assignability);
        boolean isClass = matchResult.isClass();
        assertFalse(isClass);
    }

    @Test(timeout = 4000)
    public void matchResultIsParameterizedTypeFalseWhenTypeNotSet() throws Throwable {
        HashMap<DotName, List<AnnotationInstance>> annotations = new HashMap<DotName, List<AnnotationInstance>>();
        HashMap<DotName, List<ClassInfo>> map = new HashMap<DotName, List<ClassInfo>>();
        HashMap<DotName, ClassInfo> classes = new HashMap<DotName, ClassInfo>();
        Index index = Index.create(((Map<DotName, List<AnnotationInstance>>) (annotations)), ((Map<DotName, List<ClassInfo>>) (map)), ((Map<DotName, List<ClassInfo>>) (map)), ((Map<DotName, List<ClassInfo>>) (map)), ((Map<DotName, ClassInfo>) (classes)), ((Map<DotName, List<ClassInfo>>) (map)));
        Types.AssignabilityCheck assignability = new Types.AssignabilityCheck(index);
        QuteProcessor.MatchResult matchResult = new QuteProcessor.MatchResult(assignability);
        boolean parameterized = matchResult.isParameterizedType();
        assertFalse(parameterized);
    }

    @Test(timeout = 4000)
    public void matchResultIsArrayFalseByDefault() throws Throwable {
        QuteProcessor.MatchResult matchResult = new QuteProcessor.MatchResult(((Types.AssignabilityCheck) (null)));
        boolean isArray = matchResult.isArray();
        assertFalse(isArray);
    }

    @Test(timeout = 4000)
    public void matchResultIsPrimitiveFalseForTypeVariable() throws Throwable {
        QuteProcessor.MatchResult matchResult = new QuteProcessor.MatchResult(((Types.AssignabilityCheck) (null)));
        TypeVariable variable = TypeVariable.create("8LiOQgPN+)^-");
        matchResult.setValues(((ClassInfo) (null)), variable);
        boolean primitive = matchResult.isPrimitive();
        assertFalse(primitive);
    }

    @Test(timeout = 4000)
    public void matchResultIsPrimitiveFalseByDefault() throws Throwable {
        QuteProcessor.MatchResult matchResult = new QuteProcessor.MatchResult(((Types.AssignabilityCheck) (null)));
        boolean primitive = matchResult.isPrimitive();
        assertFalse(primitive);
    }

    @Test(timeout = 4000)
    public void getParameterizedTypeArgumentsEmptyForClassType() throws Throwable {
        QuteProcessor.MatchResult matchResult = new QuteProcessor.MatchResult(((Types.AssignabilityCheck) (null)));
        ClassType classType = ClassType.ANNOTATION_TYPE;
        matchResult.setValues(((ClassInfo) (null)), classType);
        List<Type> typeArgs = matchResult.getParameterizedTypeArguments();
        assertTrue(typeArgs.isEmpty());
    }

    @Test(timeout = 4000)
    public void extractMatchTypeReturnsNullWhenNoMatch() throws Throwable {
        LinkedHashSet<Type> candidates = new LinkedHashSet<Type>();
        DotName targetName = DotName.FLOAT_CLASS_NAME;
        PrimitiveType doublePrimitive = PrimitiveType.DOUBLE;
        candidates.add(doublePrimitive);
        Function<Type, Type> extractor = QuteProcessor.MAP_ENTRY_EXTRACT_FUN;
        Type result = QuteProcessor.extractMatchType(candidates, targetName, extractor);
        assertNull(result);
    }

    @Test(timeout = 4000)
    public void processLoopElementHintWithNullExpressionThrowsNPE() throws Throwable {
        QuteProcessor.MatchResult matchResult = new QuteProcessor.MatchResult(((Types.AssignabilityCheck) (null)));
        TypeVariable variable = TypeVariable.create("8LiOQgPN+)^-");
        matchResult.setValues(((ClassInfo) (null)), variable);
        Class<IncorrectExpressionBuildItem> incorrectClass = IncorrectExpressionBuildItem.class;
        BuildProducerImpl<IncorrectExpressionBuildItem> incorrectProducer = new BuildProducerImpl<IncorrectExpressionBuildItem>(incorrectClass, ((BuildContext) (null)));
        // Undeclared exception!
        try {
            QuteProcessor.processLoopElementHint(matchResult, ((IndexView) (null)), ((Expression) (null)), incorrectProducer);
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            // 
            // Cannot invoke \"io.quarkus.qute.Expression.toOriginalString()\" because \"expression\" is null
            // 
            verifyException("io.quarkus.qute.deployment.QuteProcessor", e);
        }
    }

    @Test(timeout = 4000)
    public void processLoopElementHintDoesNotThrowOnNullExpressionAndEmptyIndex() throws Throwable {
        IndexView index = IndexView.empty();
        Types.AssignabilityCheck assignability = new Types.AssignabilityCheck(index);
        Class<IncorrectExpressionBuildItem> incorrectClass = IncorrectExpressionBuildItem.class;
        BuildContext buildContext = mock(BuildContext.class, new ViolatedAssumptionAnswer());
        BuildProducerImpl<IncorrectExpressionBuildItem> incorrectProducer = new BuildProducerImpl<IncorrectExpressionBuildItem>(incorrectClass, buildContext);
        QuteProcessor.MatchResult matchResult = new QuteProcessor.MatchResult(assignability);
        QuteProcessor.processLoopElementHint(matchResult, index, ((Expression) (null)), incorrectProducer);
    }

    @Test(timeout = 4000)
    public void processHintsReturnsFalseWithAliasesListProvided() throws Throwable {
        ArrayDeque<IndexView> views = new ArrayDeque<IndexView>();
        CompositeIndex composite = CompositeIndex.create(((Collection<IndexView>) (views)));
        WhenSectionHelper.Factory factory = new WhenSectionHelper.Factory();
        List<String> aliases = factory.getDefaultAliases();
        QuteProcessor.MatchResult matchResult = new QuteProcessor.MatchResult(((Types.AssignabilityCheck) (null)));
        HashMap<Integer, QuteProcessor.MatchResult> matches = new HashMap<Integer, QuteProcessor.MatchResult>();
        Class<IncorrectExpressionBuildItem> incorrectClass = IncorrectExpressionBuildItem.class;
        BuildContext buildContext = mock(BuildContext.class, new ViolatedAssumptionAnswer());
        BuildProducerImpl<IncorrectExpressionBuildItem> incorrectProducer = new BuildProducerImpl<IncorrectExpressionBuildItem>(incorrectClass, buildContext);
        boolean result = QuteProcessor.processHints(((TemplatesAnalysisBuildItem.TemplateAnalysis) (null)), aliases, matchResult, composite, ((Expression) (null)), matches, incorrectProducer);
        assertFalse(result);
    }

    @Test(timeout = 4000)
    public void processHintsReturnsFalseWithEmptyAliases() throws Throwable {
        EmptyIndex emptyIndex = EmptyIndex.INSTANCE;
        AliasManager aliasManager = new AliasManager(((File) (null)), false);
        List<String> aliases = aliasManager.findAllMatchingNames("N1PV%,Fu .[");
        QuteProcessor.MatchResult matchResult = new QuteProcessor.MatchResult(((Types.AssignabilityCheck) (null)));
        HashMap<Integer, QuteProcessor.MatchResult> matches = new HashMap<Integer, QuteProcessor.MatchResult>();
        Class<IncorrectExpressionBuildItem> incorrectClass = IncorrectExpressionBuildItem.class;
        BuildContext buildContext = mock(BuildContext.class, new ViolatedAssumptionAnswer());
        BuildProducerImpl<IncorrectExpressionBuildItem> incorrectProducer = new BuildProducerImpl<IncorrectExpressionBuildItem>(incorrectClass, buildContext);
        boolean result = QuteProcessor.processHints(((TemplatesAnalysisBuildItem.TemplateAnalysis) (null)), aliases, matchResult, emptyIndex, ((Expression) (null)), matches, incorrectProducer);
        assertFalse(result);
    }

    @Test(timeout = 4000)
    public void initializeThrowsNPEWhenExcludeNull() throws Throwable {
        QuteRecorder recorder = new QuteRecorder();
        Stack<TemplatePathBuildItem> templatePaths = new Stack<TemplatePathBuildItem>();
        EffectiveTemplatePathsBuildItem effectivePaths = new EffectiveTemplatePathsBuildItem(templatePaths);
        Optional<TemplateVariantsBuildItem> variants = Optional.empty();
        Stack<TemplateRootBuildItem> roots = new Stack<TemplateRootBuildItem>();
        QuteProcessor processor = new QuteProcessor();
        TemplateRootsBuildItem templateRoots = processor.collectTemplateRoots(roots);
        ArrayList<TemplatePathExcludeBuildItem> excludes = new ArrayList<TemplatePathExcludeBuildItem>();
        excludes.add(((TemplatePathExcludeBuildItem) (null)));
        // Undeclared exception!
        try {
            processor.initialize(((BuildProducer<SyntheticBeanBuildItem>) (null)), recorder, effectivePaths, variants, templateRoots, excludes);
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            // 
            // Cannot invoke \"io.quarkus.qute.deployment.TemplatePathExcludeBuildItem.getRegexPattern()\" because \"exclude\" is null
            // 
            verifyException("io.quarkus.qute.deployment.QuteProcessor", e);
        }
    }

    @Test(timeout = 4000)
    public void initializeThrowsIAEWhenNotUsingRecorderProxyCase0() throws Throwable {
        QuteProcessor processor = new QuteProcessor();
        QuteRecorder recorder = new QuteRecorder();
        Vector<TemplatePathBuildItem> templatePaths = new Vector<TemplatePathBuildItem>();
        URI source = new URI("=btK_4Q");
        TemplatePathBuildItem pathItem = mock(TemplatePathBuildItem.class, new ViolatedAssumptionAnswer());
        doReturn(((String) (null))).when(pathItem).getContent();
        doReturn(((String) (null)), ((String) (null))).when(pathItem).getPath();
        doReturn(((URI) (null))).when(pathItem).getSource();
        doReturn(false).when(pathItem).isFileBased();
        templatePaths.add(pathItem);
        EffectiveTemplatePathsBuildItem effectivePaths = new EffectiveTemplatePathsBuildItem(templatePaths);
        Optional<TemplateVariantsBuildItem> variants = Optional.empty();
        Vector<TemplateRootBuildItem> roots = new Vector<TemplateRootBuildItem>();
        TemplateRootsBuildItem templateRoots = processor.collectTemplateRoots(roots);
        ArrayList<TemplatePathExcludeBuildItem> excludes = new ArrayList<TemplatePathExcludeBuildItem>();
        // Undeclared exception!
        try {
            processor.initialize(((BuildProducer<SyntheticBeanBuildItem>) (null)), recorder, effectivePaths, variants, templateRoots, excludes);
            fail("Expecting exception: IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // 
            // The object is not a proxy returned from a recorder method: io.quarkus.qute.runtime.QuteRecorder$1@0000000009
            // 
            verifyException("io.quarkus.arc.deployment.SyntheticBeanBuildItem$ExtendedBeanConfigurator", e);
        }
    }

    @Test(timeout = 4000)
    public void initializeThrowsIAEWhenNotUsingRecorderProxyCase1() throws Throwable {
        QuteProcessor processor = new QuteProcessor();
        QuteRecorder recorder = new QuteRecorder();
        Vector<TemplatePathBuildItem> templatePaths = new Vector<TemplatePathBuildItem>();
        TemplatePathBuildItem pathItem = mock(TemplatePathBuildItem.class, new ViolatedAssumptionAnswer());
        doReturn(((String) (null))).when(pathItem).getContent();
        doReturn(((String) (null)), ((String) (null))).when(pathItem).getPath();
        doReturn(((URI) (null))).when(pathItem).getSource();
        doReturn(false).when(pathItem).isFileBased();
        templatePaths.add(pathItem);
        EffectiveTemplatePathsBuildItem effectivePaths = new EffectiveTemplatePathsBuildItem(templatePaths);
        Optional<TemplateVariantsBuildItem> variants = Optional.empty();
        Vector<TemplateRootBuildItem> roots = new Vector<TemplateRootBuildItem>();
        TemplateRootsBuildItem templateRoots = processor.collectTemplateRoots(roots);
        ArrayList<TemplatePathExcludeBuildItem> excludes = new ArrayList<TemplatePathExcludeBuildItem>();
        // Undeclared exception!
        try {
            processor.initialize(((BuildProducer<SyntheticBeanBuildItem>) (null)), recorder, effectivePaths, variants, templateRoots, excludes);
            fail("Expecting exception: IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // 
            // The object is not a proxy returned from a recorder method: io.quarkus.qute.runtime.QuteRecorder$1@0000000009
            // 
            verifyException("io.quarkus.arc.deployment.SyntheticBeanBuildItem$ExtendedBeanConfigurator", e);
        }
    }

    @Test(timeout = 4000)
    public void collectTemplateContentsDoesNotProducePathsWhenNoAdapters() throws Throwable {
        QuteProcessor processor = new QuteProcessor();
        IndexView[] empty = new IndexView[0];
        StackedIndex stackedIndex = StackedIndex.create(empty);
        HashSet<DotName> additional = new HashSet<DotName>(387);
        BeanArchiveIndexBuildItem beanArchiveIndex = new BeanArchiveIndexBuildItem(stackedIndex, stackedIndex, additional);
        Stack<CheckedTemplateAdapterBuildItem> adapters = new Stack<CheckedTemplateAdapterBuildItem>();
        Class<TemplatePathBuildItem> pathClass = TemplatePathBuildItem.class;
        BuildContext buildContext = mock(BuildContext.class, new ViolatedAssumptionAnswer());
        BuildProducerImpl<TemplatePathBuildItem> pathProducer = new BuildProducerImpl<TemplatePathBuildItem>(pathClass, buildContext);
        processor.collecTemplateContents(beanArchiveIndex, adapters, pathProducer);
        assertTrue(adapters.isEmpty());
    }

    @Test(timeout = 4000)
    public void excludeTypeChecksThrowsNPEWhenConfigReturnsNullOptional() throws Throwable {
        QuteProcessor processor = new QuteProcessor();
        InsertSectionHelper.Factory factory = new InsertSectionHelper.Factory();
        List<String> aliases = factory.getDefaultAliases();
        Optional.ofNullable(aliases);
        QuteConfig config = mock(QuteConfig.class, new ViolatedAssumptionAnswer());
        doReturn(((Optional) (null))).when(config).typeCheckExcludes();
        Class<TypeCheckExcludeBuildItem> excludeClass = TypeCheckExcludeBuildItem.class;
        BuildContext buildContext = mock(BuildContext.class, new ViolatedAssumptionAnswer());
        BuildProducerImpl<TypeCheckExcludeBuildItem> excludeProducer = new BuildProducerImpl<TypeCheckExcludeBuildItem>(excludeClass, buildContext);
        // Undeclared exception!
        try {
            processor.excludeTypeChecks(config, excludeProducer);
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            // 
            // Cannot invoke \"java.util.Optional.isPresent()\" because the return value of \"io.quarkus.qute.runtime.QuteConfig.typeCheckExcludes()\" is null
            // 
            verifyException("io.quarkus.qute.deployment.QuteProcessor", e);
        }
    }

    @Test(timeout = 4000)
    public void collectTemplateVariantsThrowsNPEWhenSuffixesNullSecondCall() throws Throwable {
        QuteProcessor processor = new QuteProcessor();
        TemplatePathBuildItem first = mock(TemplatePathBuildItem.class, new ViolatedAssumptionAnswer());
        doReturn("MW", "MW", "4uX5{").when(first).getPath();
        TemplatePathBuildItem second = mock(TemplatePathBuildItem.class, new ViolatedAssumptionAnswer());
        doReturn("\n", "MW", "4uX5{", "\n").when(second).getPath();
        List<TemplatePathBuildItem> paths = List.of(first, second, second, first, second, first, second);
        EffectiveTemplatePathsBuildItem effectivePaths = new EffectiveTemplatePathsBuildItem(paths);
        LinkedList<String> suffixes = new LinkedList<String>();
        suffixes.add("4uX5{");
        QuteConfig config = mock(QuteConfig.class, new ViolatedAssumptionAnswer());
        doReturn(suffixes, ((List) (null))).when(config).suffixes();
        // Undeclared exception!
        try {
            processor.collectTemplateVariants(effectivePaths, config);
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            // 
            // Cannot invoke \"java.util.List.iterator()\" because the return value of \"io.quarkus.qute.runtime.QuteConfig.suffixes()\" is null
            // 
            verifyException("io.quarkus.qute.deployment.QuteProcessor", e);
        }
    }

    @Test(timeout = 4000)
    public void collectEngineConfigurationsHandlesEmptyIndex() throws Throwable {
        EmptyIndex emptyIndex = EmptyIndex.INSTANCE;
        Class<EngineConfigurationsBuildItem> configClass = EngineConfigurationsBuildItem.class;
        BuildProducerImpl<EngineConfigurationsBuildItem> configProducer = new BuildProducerImpl<EngineConfigurationsBuildItem>(configClass, ((BuildContext) (null)));
        QuteProcessor processor = new QuteProcessor();
        BeanArchiveIndexBuildItem beanArchiveIndex = new BeanArchiveIndexBuildItem(emptyIndex, emptyIndex, ((Set<DotName>) (null)));
        processor.collectEngineConfigurations(beanArchiveIndex, configProducer, ((BuildProducer<ValidationPhaseBuildItem.ValidationErrorBuildItem>) (null)));
    }

    @Test(timeout = 4000)
    public void validateAndCollectCustomTemplateLocatorLocationsReturnsResult() throws Throwable {
        EmptyIndex emptyIndex = EmptyIndex.INSTANCE;
        BeanArchiveIndexBuildItem beanArchiveIndex = new BeanArchiveIndexBuildItem(emptyIndex, emptyIndex, ((Set<DotName>) (null)));
        Class<ValidationPhaseBuildItem.ValidationErrorBuildItem> errorClass = ValidationPhaseBuildItem.ValidationErrorBuildItem.class;
        BuildProducerImpl<ValidationPhaseBuildItem.ValidationErrorBuildItem> errorProducer = new BuildProducerImpl<ValidationPhaseBuildItem.ValidationErrorBuildItem>(errorClass, ((BuildContext) (null)));
        QuteProcessor processor = new QuteProcessor();
        CustomTemplateLocatorPatternsBuildItem result = processor.validateAndCollectCustomTemplateLocatorLocations(beanArchiveIndex, errorProducer);
        assertNotNull(result);
    }

    @Test(timeout = 4000)
    public void validateTemplateInjectionPointsHandlesPrimitiveInjectionPoints() throws Throwable {
        QuteProcessor processor = new QuteProcessor();
        Vector<TemplatePathBuildItem> paths = new Vector<TemplatePathBuildItem>();
        EffectiveTemplatePathsBuildItem effectivePaths = new EffectiveTemplatePathsBuildItem(paths);
        ClassType boxedByte = ClassType.BYTE_CLASS;
        PrimitiveType primitive = PrimitiveType.unbox(boxedByte);
        HashSet<AnnotationInstance> qualifiers = new HashSet<AnnotationInstance>();
        InjectionPointInfo.TypeAndQualifiers tq = new InjectionPointInfo.TypeAndQualifiers(primitive, qualifiers);
        InjectionPointInfo ip = InjectionPointInfo.fromSyntheticInjectionPoint(tq);
        List<InjectionPointInfo> injectionPoints = List.of(ip, ip, ip, ip, ip);
        BeanDeploymentValidator.ValidationContext validationContext = mock(BeanDeploymentValidator.ValidationContext.class, new ViolatedAssumptionAnswer());
        doReturn(injectionPoints).when(validationContext).getInjectionPoints();
        BeanProcessor beanProcessor = mock(BeanProcessor.class, new ViolatedAssumptionAnswer());
        ValidationPhaseBuildItem validationPhase = new ValidationPhaseBuildItem(validationContext, beanProcessor);
        Class<ValidationPhaseBuildItem.ValidationErrorBuildItem> errorClass = ValidationPhaseBuildItem.ValidationErrorBuildItem.class;
        BuildContext buildContext = mock(BuildContext.class, new ViolatedAssumptionAnswer());
        BuildProducerImpl<ValidationPhaseBuildItem.ValidationErrorBuildItem> errorProducer = new BuildProducerImpl<ValidationPhaseBuildItem.ValidationErrorBuildItem>(errorClass, buildContext);
        CustomTemplateLocatorPatternsBuildItem patterns = new CustomTemplateLocatorPatternsBuildItem(((Collection<Pattern>) (null)));
        processor.validateTemplateInjectionPoints(((TemplateFilePathsBuildItem) (null)), effectivePaths, validationPhase, errorProducer, patterns);
    }

    @Test(timeout = 4000)
    public void collectTemplateFilePathsThrowsSIOOBEOnInvalidPathSuffix() throws Throwable {
        QuteProcessor processor = new QuteProcessor();
        Stack<String> suffixStack = new Stack<String>();
        String[] resourcesArray = new String[5];
        resourcesArray[0] = "=\"4)9\u0001B-PDT'n";
        resourcesArray[1] = "index";
        resourcesArray[2] = "W";
        resourcesArray[3] = "p~sN^/ MOY%_Pxqinsert";
        NativeImageResourceBuildItem nativeResources = new NativeImageResourceBuildItem(resourcesArray);
        List<String> resources = nativeResources.getResources();
        QuteConfig config = mock(QuteConfig.class, new ViolatedAssumptionAnswer());
        doReturn(suffixStack, resources).when(config).suffixes();
        TemplatePathBuildItem pathItem = mock(TemplatePathBuildItem.class, new ViolatedAssumptionAnswer());
        doReturn("_", "p~sN^/ MOY%_Pxqinsert").when(pathItem).getPath();
        List<TemplatePathBuildItem> paths = List.of(pathItem, pathItem);
        EffectiveTemplatePathsBuildItem effectivePaths = new EffectiveTemplatePathsBuildItem(paths);
        // Undeclared exception!
        try {
            processor.collectTemplateFilePaths(config, effectivePaths);
            fail("Expecting exception: StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException e) {
        }
    }

    @Test(timeout = 4000)
    public void collectTemplatesThrowsNPEWhenApplicationModelNull() throws Throwable {
        CurateOutcomeBuildItem curateOutcome = new CurateOutcomeBuildItem(((ApplicationModel) (null)));
        Stack<TemplatePathExcludeBuildItem> excludes = new Stack<TemplatePathExcludeBuildItem>();
        Class<HotDeploymentWatchedFileBuildItem> watchedClass = HotDeploymentWatchedFileBuildItem.class;
        BuildContext buildContext = mock(BuildContext.class, new ViolatedAssumptionAnswer());
        BuildProducerImpl<HotDeploymentWatchedFileBuildItem> watchedProducer = new BuildProducerImpl<HotDeploymentWatchedFileBuildItem>(watchedClass, buildContext);
        MutableCommandRegistryImpl<DefaultCommandInvocation> cmdRegistry = new MutableCommandRegistryImpl<DefaultCommandInvocation>();
        Set<String> commandNames = cmdRegistry.getAllCommandNames();
        TemplateRootsBuildItem templateRoots = new TemplateRootsBuildItem(commandNames);
        LaunchMode mode = LaunchMode.RUN;
        LaunchModeBuildItem launchItem = new LaunchModeBuildItem(mode, ((Optional<DevModeType>) (null)), false, ((Optional<DevModeType>) (null)), true);
        QuteProcessor processor = new QuteProcessor();
        PriorityBlockingQueue<IndexView> views = new PriorityBlockingQueue<IndexView>();
        CompositeIndex compositeIndex = CompositeIndex.create(((Collection<IndexView>) (views)));
        DirectoryPathTree pathTree = new DirectoryPathTree();
        ResolvedDependencyBuilder dep = ResolvedDependencyBuilder.newInstance();
        ApplicationArchiveImpl rootArchive = new ApplicationArchiveImpl(compositeIndex, pathTree, dep);
        Vector<ApplicationArchiveImpl> archives = new Vector<ApplicationArchiveImpl>();
        PriorityQueue<ApplicationArchive> allArchives = new PriorityQueue<ApplicationArchive>(archives);
        ApplicationArchivesBuildItem appArchives = new ApplicationArchivesBuildItem(rootArchive, allArchives);
        Class<TemplatePathBuildItem> pathClass = TemplatePathBuildItem.class;
        BuildProducerImpl<TemplatePathBuildItem> pathProducer = new BuildProducerImpl<TemplatePathBuildItem>(pathClass, ((BuildContext) (null)));
        QuteConfig config = mock(QuteConfig.class, new ViolatedAssumptionAnswer());
        doReturn(((Pattern) (null))).when(config).templatePathExclude();
        // Undeclared exception!
        try {
            processor.collectTemplates(appArchives, curateOutcome, excludes, watchedProducer, pathProducer, ((BuildProducer<NativeImageResourceBuildItem>) (null)), config, templateRoots, launchItem);
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            // 
            // Cannot invoke \"io.quarkus.bootstrap.model.ApplicationModel.getDependencies(int)\" because \"applicationModel\" is null
            // 
            verifyException("io.quarkus.qute.deployment.QuteProcessor", e);
        }
    }

    @Test(timeout = 4000)
    public void collectTemplatesThrowsNPEWhenApplicationArchivesNull() throws Throwable {
        LaunchMode mode = LaunchMode.DEVELOPMENT;
        DevModeType devMode = DevModeType.LOCAL;
        Optional<DevModeType> maybeDev = Optional.ofNullable(devMode);
        LaunchModeBuildItem launchItem = new LaunchModeBuildItem(mode, maybeDev, false, maybeDev, false);
        Vector<TemplatePathExcludeBuildItem> excludes = new Vector<TemplatePathExcludeBuildItem>(3, 1452);
        Class<TemplatePathBuildItem> pathClass = TemplatePathBuildItem.class;
        BuildContext buildContext = mock(BuildContext.class, new ViolatedAssumptionAnswer());
        BuildProducerImpl<TemplatePathBuildItem> pathProducer = new BuildProducerImpl<TemplatePathBuildItem>(pathClass, buildContext);
        QuteProcessor processor = new QuteProcessor();
        CurateOutcomeBuildItem curateOutcome = new CurateOutcomeBuildItem(((ApplicationModel) (null)));
        Class<HotDeploymentWatchedFileBuildItem> watchedClass = HotDeploymentWatchedFileBuildItem.class;
        BuildContext buildContext1 = mock(BuildContext.class, new ViolatedAssumptionAnswer());
        BuildProducerImpl<HotDeploymentWatchedFileBuildItem> watchedProducer = new BuildProducerImpl<HotDeploymentWatchedFileBuildItem>(watchedClass, buildContext1);
        QuteConfig config = mock(QuteConfig.class, new ViolatedAssumptionAnswer());
        doReturn(((Pattern) (null))).when(config).templatePathExclude();
        // Undeclared exception!
        try {
            processor.collectTemplates(((ApplicationArchivesBuildItem) (null)), curateOutcome, excludes, watchedProducer, pathProducer, ((BuildProducer<NativeImageResourceBuildItem>) (null)), config, ((TemplateRootsBuildItem) (null)), launchItem);
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            // 
            // Cannot invoke \"io.quarkus.deployment.builditem.ApplicationArchivesBuildItem.getAllApplicationArchives()\" because \"applicationArchives\" is null
            // 
            verifyException("io.quarkus.qute.deployment.QuteProcessor", e);
        }
    }

    @Test(timeout = 4000)
    public void existingValueResolversAddGlobalRecordsClassName() throws Throwable {
        Types.AssignabilityCheck assignability = new Types.AssignabilityCheck(((IndexView) (null)));
        DotName location = QuteProcessor.LOCATION;
        QuteProcessor.ExistingValueResolvers resolvers = new QuteProcessor.ExistingValueResolvers();
        Predicate<Object> notSame = Predicate.isEqual(assignability);
        Predicate<DotName> predicate = Predicate.not(notSame);
        resolvers.addGlobal(location, "W", predicate);
        assertFalse(location.isInner());
    }

    @Test(timeout = 4000)
    public void existingValueResolversAddGlobalIdempotent() throws Throwable {
        QuteProcessor.ExistingValueResolvers resolvers = new QuteProcessor.ExistingValueResolvers();
        DotName integerName = DotName.INTEGER_CLASS_NAME;
        AlwaysFalsePredicate<DotName> alwaysFalse = new AlwaysFalsePredicate<DotName>();
        resolvers.addGlobal(integerName, "Component type not supported: ", alwaysFalse);
        resolvers.addGlobal(integerName, "Component type not supported: ", alwaysFalse);
        assertTrue(integerName.isComponentized());
    }

    @Test(timeout = 4000)
    public void existingValueResolversAddExtensionMethodRecordsMethod() throws Throwable {
        Class<Object> objectClass = Object.class;
        ClassInfo classInfo = Index.singleClass(objectClass);
        TypeVariable returnType = TypeVariable.create("IN78 +@gT{<^b8");
        String[] params = new String[8];
        params[0] = "IN78 +@gT{<^b8";
        params[1] = "IN78 +@gT{<^b8";
        params[2] = "IN78 +@gT{<^b8";
        params[3] = "IN78 +@gT{<^b8";
        params[4] = "IN78 +@gT{<^b8";
        params[5] = "IN78 +@gT{<^b8";
        params[6] = "IN78 +@gT{<^b8";
        params[7] = "IN78 +@gT{<^b8";
        Type[] paramTypes = new Type[1];
        TypeVariable[] typeParams = new TypeVariable[1];
        MethodInfo method = MethodInfo.create(classInfo, "IN78 +@gT{<^b8", params, paramTypes, ((Type) (returnType)), ((short) (3038)), typeParams, paramTypes);
        QuteProcessor.ExistingValueResolvers resolvers = new QuteProcessor.ExistingValueResolvers();
        AlwaysFalsePredicate<Object> alwaysFalse = new AlwaysFalsePredicate<Object>();
        Predicate<DotName> predicate = Predicate.not(alwaysFalse);
        resolvers.add(method, ",+&w`b@4a)", predicate);
        assertTrue(method.isDeclaration());
    }

    @Test(timeout = 4000)
    public void generateValueResolversHandlesNullConfigAndKeepsIncorrectExpressions() throws Throwable {
        EmptyIndex emptyIndex = EmptyIndex.INSTANCE;
        QuteProcessor processor = new QuteProcessor();
        ArrayList<IndexView> views = new ArrayList<IndexView>();
        CompositeIndex composite = CompositeIndex.create(((Collection<IndexView>) (views)));
        TreeSet<DotName> subpackages = new TreeSet<DotName>();
        BeanArchiveIndexBuildItem beanArchiveIndex = new BeanArchiveIndexBuildItem(composite, emptyIndex, subpackages);
        EmptyPathTree emptyTree = new EmptyPathTree();
        ResolvedDependencyBuilder dep = ResolvedDependencyBuilder.newInstance();
        ApplicationArchiveImpl rootArchive = new ApplicationArchiveImpl(emptyIndex, emptyTree, dep);
        HashSet<ApplicationArchive> archives = new HashSet<ApplicationArchive>();
        ApplicationArchivesBuildItem appArchives = new ApplicationArchivesBuildItem(rootArchive, archives);
        Vector<TemplateExtensionMethodBuildItem> extensionMethods = new Vector<TemplateExtensionMethodBuildItem>();
        LinkedList<ImplicitValueResolverBuildItem> implicitResolvers = new LinkedList<ImplicitValueResolverBuildItem>();
        LinkedList<TemplatesAnalysisBuildItem.TemplateAnalysis> analysesList = new LinkedList<TemplatesAnalysisBuildItem.TemplateAnalysis>();
        TemplatesAnalysisBuildItem analyses = new TemplatesAnalysisBuildItem(analysesList);
        ArrayList<PanacheEntityClassesBuildItem> panache = new ArrayList<PanacheEntityClassesBuildItem>();
        Vector<TemplateGlobalBuildItem> globals = new Vector<TemplateGlobalBuildItem>();
        Vector<IncorrectExpressionBuildItem> incorrect = new Vector<IncorrectExpressionBuildItem>();
        IncorrectExpressionBuildItem incorrectItem = new IncorrectExpressionBuildItem("case", "_4E?!0f8$1>^*AVsC", "case", ((TemplateNode.Origin) (null)));
        incorrect.add(incorrectItem);
        LiveReloadBuildItem liveReload = new LiveReloadBuildItem();
        Class<GeneratedClassBuildItem> generatedClass = GeneratedClassBuildItem.class;
        BuildContext buildContext = mock(BuildContext.class, new ViolatedAssumptionAnswer());
        BuildProducerImpl<GeneratedClassBuildItem> generatedClassProducer = new BuildProducerImpl<GeneratedClassBuildItem>(generatedClass, buildContext);
        BuildProducer<GeneratedResourceBuildItem> generatedResourceProducer = ((BuildProducer<GeneratedResourceBuildItem>) (mock(BuildProducer.class, new ViolatedAssumptionAnswer())));
        ArrayList<TemplateDataBuildItem> templateData = new ArrayList<TemplateDataBuildItem>();
        CompletedApplicationClassPredicateBuildItem completedPredicate = mock(CompletedApplicationClassPredicateBuildItem.class, new ViolatedAssumptionAnswer());
        BuildProducer<GeneratedValueResolverBuildItem> valueResolverProducer = ((BuildProducer<GeneratedValueResolverBuildItem>) (mock(BuildProducer.class, new ViolatedAssumptionAnswer())));
        BuildProducer<ReflectiveClassBuildItem> reflectiveProducer = ((BuildProducer<ReflectiveClassBuildItem>) (mock(BuildProducer.class, new ViolatedAssumptionAnswer())));
        processor.generateValueResolvers(((QuteConfig) (null)), generatedClassProducer, generatedResourceProducer, beanArchiveIndex, appArchives, extensionMethods, implicitResolvers, analyses, panache, templateData, globals, incorrect, liveReload, completedPredicate, valueResolverProducer, reflectiveProducer, ((BuildProducer<TemplateGlobalProviderBuildItem>) (null)));
        assertTrue(incorrect.contains(incorrectItem));
    }

    @Test(timeout = 4000)
    public void processTemplateErrorsThrowsNPEWhenTemplateAnalysisNull() throws Throwable {
        Stack<TemplatesAnalysisBuildItem.TemplateAnalysis> analysesList = new Stack<TemplatesAnalysisBuildItem.TemplateAnalysis>();
        analysesList.setSize(982);
        TemplateNode.Origin origin = mock(TemplateNode.Origin.class, new ViolatedAssumptionAnswer());
        doReturn("name").when(origin).getTemplateGeneratedId();
        IncorrectExpressionBuildItem incorrect = new IncorrectExpressionBuildItem("name", "name", origin);
        List<IncorrectExpressionBuildItem> incorrectList = List.of(incorrect, incorrect, incorrect, incorrect, incorrect, incorrect, incorrect, incorrect, incorrect);
        Class<ServiceStartBuildItem> serviceStartClass = ServiceStartBuildItem.class;
        BuildContext buildContext = mock(BuildContext.class, new ViolatedAssumptionAnswer());
        BuildProducerImpl<ServiceStartBuildItem> serviceStartProducer = new BuildProducerImpl<ServiceStartBuildItem>(serviceStartClass, buildContext);
        TemplatesAnalysisBuildItem analyses = new TemplatesAnalysisBuildItem(analysesList);
        QuteProcessor processor = new QuteProcessor();
        // Undeclared exception!
        try {
            processor.processTemplateErrors(analyses, incorrectList, serviceStartProducer);
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            // 
            // Cannot read field \"generatedId\" because \"templateAnalysis\" is null
            // 
            verifyException("io.quarkus.qute.deployment.QuteProcessor", e);
        }
    }

    @Test(timeout = 4000)
    public void enumConstantFilterReturnsFalseForNonEnum() throws Throwable {
        Class<Object> objectClass = Object.class;
        ClassInfo classInfo = Index.singleClass(objectClass);
        boolean result = QuteProcessor.enumConstantFilter(classInfo);
        assertFalse(result);
    }

    @Test(timeout = 4000)
    public void staticsFilterOnClassInfoThrowsIAE() throws Throwable {
        Class<Object> objectClass = Object.class;
        ClassInfo classInfo = Index.singleClass(objectClass);
        // Undeclared exception!
        try {
            QuteProcessor.staticsFilter(classInfo);
            fail("Expecting exception: IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // 
            // no message in exception (getMessage() returned null)
            // 
            verifyException("io.quarkus.qute.deployment.QuteProcessor", e);
        }
    }

    @Test(timeout = 4000)
    public void staticsFilterReturnsTrueForStaticMethod() throws Throwable {
        Class<Object> objectClass = Object.class;
        ClassInfo classInfo = Index.singleClass(objectClass);
        TypeVariable returnType = TypeVariable.create("kW[?,rWgEiME88{else");
        String[] params = new String[1];
        params[0] = "kW[?,rWgEiME88{else";
        Type[] paramTypes = new Type[0];
        TypeVariable[] typeParams = new TypeVariable[5];
        MethodInfo method = MethodInfo.create(classInfo, "kW[?,rWgEiME88{else", params, paramTypes, ((Type) (returnType)), ((short) (-22438)), typeParams, paramTypes);
        boolean accept = QuteProcessor.staticsFilter(method);
        assertTrue(accept);
    }

    @Test(timeout = 4000)
    public void defaultFilterReturnsFalseForNonMatchingMethod() throws Throwable {
        Class<Object> objectClass = Object.class;
        ClassInfo classInfo = Index.singleClass(objectClass);
        TypeVariable returnType = TypeVariable.create("k,rWgE)MEP8{else");
        String[] params = new String[1];
        params[0] = "k,rWgE)MEP8{else";
        Type[] paramTypes = new Type[0];
        TypeVariable[] typeParams = new TypeVariable[5];
        MethodInfo method = MethodInfo.create(classInfo, "k,rWgE)MEP8{else", params, paramTypes, ((Type) (returnType)), ((short) (-22428)), typeParams, paramTypes);
        boolean accept = QuteProcessor.defaultFilter(method);
        assertFalse(accept);
    }

    @Test(timeout = 4000)
    public void defaultFilterOnClassInfoThrowsIAE() throws Throwable {
        Class<Object> objectClass = Object.class;
        ClassInfo classInfo = Index.singleClass(objectClass);
        // Undeclared exception!
        try {
            QuteProcessor.defaultFilter(classInfo);
            fail("Expecting exception: IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // 
            // no message in exception (getMessage() returned null)
            // 
            verifyException("io.quarkus.qute.deployment.QuteProcessor", e);
        }
    }

    @Test(timeout = 4000)
    public void collectTemplateExtensionMethodsHandlesEmptyIndex() throws Throwable {
        QuteProcessor processor = new QuteProcessor();
        HashSet<DotName> additional = new HashSet<DotName>();
        IndexView[] single = new IndexView[1];
        EmptyIndex emptyIndex = EmptyIndex.INSTANCE;
        single[0] = ((IndexView) (emptyIndex));
        CompositeIndex composite = CompositeIndex.create(single);
        BeanArchiveIndexBuildItem beanArchiveIndex = new BeanArchiveIndexBuildItem(composite, emptyIndex, additional);
        Class<TemplateExtensionMethodBuildItem> extensionClass = TemplateExtensionMethodBuildItem.class;
        BuildProducerImpl<TemplateExtensionMethodBuildItem> extensionProducer = new BuildProducerImpl<TemplateExtensionMethodBuildItem>(extensionClass, ((BuildContext) (null)));
        processor.collectTemplateExtensionMethods(beanArchiveIndex, extensionProducer);
    }

    @Test(timeout = 4000)
    public void buildIgnorePatternThrowsIAEOnEmptyInput() throws Throwable {
        ArrayList<String> empty = new ArrayList<String>();
        // Undeclared exception!
        try {
            QuteProcessor.buildIgnorePattern(empty);
            fail("Expecting exception: IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // 
            // no message in exception (getMessage() returned null)
            // 
            verifyException("io.quarkus.qute.deployment.QuteProcessor", e);
        }
    }

    @Test(timeout = 4000)
    public void buildIgnorePatternReturnsStringNotInInput() throws Throwable {
        Set<String> ids = ZoneId.getAvailableZoneIds();
        String pattern = QuteProcessor.buildIgnorePattern(ids);
        assertFalse(ids.contains(pattern));
    }

    @Test(timeout = 4000)
    public void validateExpressionsWithEmptyBeansNoErrors() throws Throwable {
        QuteProcessor processor = new QuteProcessor();
        Stack<TemplatesAnalysisBuildItem.TemplateAnalysis> analysesList = new Stack<TemplatesAnalysisBuildItem.TemplateAnalysis>();
        TemplatesAnalysisBuildItem analyses = new TemplatesAnalysisBuildItem(analysesList);
        Class<Object>[] classes = ((Class<Object>[]) (Array.newInstance(Class.class, 0)));
        Index index = Index.of(classes);
        HashSet<DotName> additional = new HashSet<DotName>();
        BeanArchiveIndexBuildItem beanArchiveIndex = new BeanArchiveIndexBuildItem(index, index, additional);
        Stack<TemplateExtensionMethodBuildItem> extensionMethods = new Stack<TemplateExtensionMethodBuildItem>();
        Predicate<TypeCheckExcludeBuildItem.TypeCheck> predicate = Predicate.isEqual(extensionMethods);
        TypeCheckExcludeBuildItem exclude = new TypeCheckExcludeBuildItem(predicate);
        List<TypeCheckExcludeBuildItem> excludes = List.of(exclude, exclude, exclude, exclude, exclude);
        Class<IncorrectExpressionBuildItem> incorrectClass = IncorrectExpressionBuildItem.class;
        BuildContext buildContext = mock(BuildContext.class, new ViolatedAssumptionAnswer());
        BuildProducerImpl<IncorrectExpressionBuildItem> incorrectProducer = new BuildProducerImpl<IncorrectExpressionBuildItem>(incorrectClass, buildContext);
        BuildProducer<ImplicitValueResolverBuildItem> implicitProducer = ((BuildProducer<ImplicitValueResolverBuildItem>) (mock(BuildProducer.class, new ViolatedAssumptionAnswer())));
        PriorityQueue<BeanInfo> beans = new PriorityQueue<BeanInfo>();
        LinkedHashSet<InjectionPointInfo> injectionPoints = new LinkedHashSet<InjectionPointInfo>();
        PriorityQueue<ObserverInfo> observers = new PriorityQueue<ObserverInfo>();
        BeanDeployment deployment = mock(BeanDeployment.class, new ViolatedAssumptionAnswer());
        doReturn(((BeanResolver) (null))).when(deployment).getBeanResolver();
        doReturn(beans).when(deployment).getBeans();
        doReturn(injectionPoints).when(deployment).getInjectionPoints();
        doReturn(observers).when(deployment).getObservers();
        SynthesisFinishedBuildItem synthesis = new SynthesisFinishedBuildItem(deployment);
        ArrayList<CheckedTemplateBuildItem> checkedTemplates = new ArrayList<CheckedTemplateBuildItem>();
        Stack<TemplateDataBuildItem> templateData = new Stack<TemplateDataBuildItem>();
        QuteConfig config = mock(QuteConfig.class, new ViolatedAssumptionAnswer());
        NativeConfig nativeConfig = mock(NativeConfig.class, new ViolatedAssumptionAnswer());
        doReturn(false).when(nativeConfig).enabled();
        Vector<TemplateGlobalBuildItem> globals = new Vector<TemplateGlobalBuildItem>();
        processor.validateExpressions(analyses, beanArchiveIndex, extensionMethods, excludes, incorrectProducer, implicitProducer, ((BuildProducer<TemplateExpressionMatchesBuildItem>) (null)), synthesis, checkedTemplates, templateData, config, nativeConfig, globals);
        assertEquals(0, templateData.size());
    }

    @Test(timeout = 4000)
    public void registerRenderedResultsReturnsSyntheticBeanWhenRecordingEnabled() throws Throwable {
        QuteProcessor processor = new QuteProcessor();
        QuteTestModeConfig testMode = mock(QuteTestModeConfig.class, new ViolatedAssumptionAnswer());
        doReturn(true).when(testMode).recordRenderedResults();
        QuteConfig config = mock(QuteConfig.class, new ViolatedAssumptionAnswer());
        doReturn(testMode).when(config).testMode();
        SyntheticBeanBuildItem item = processor.registerRenderedResults(config);
        assertNotNull(item);
    }

    @Test(timeout = 4000)
    public void registerRenderedResultsReturnsNullWhenRecordingDisabled() throws Throwable {
        QuteProcessor processor = new QuteProcessor();
        QuteTestModeConfig testMode = mock(QuteTestModeConfig.class, new ViolatedAssumptionAnswer());
        doReturn(false).when(testMode).recordRenderedResults();
        QuteConfig config = mock(QuteConfig.class, new ViolatedAssumptionAnswer());
        doReturn(testMode).when(config).testMode();
        SyntheticBeanBuildItem item = processor.registerRenderedResults(config);
        assertNull(item);
    }

    @Test(timeout = 4000)
    public void validateCheckedFragmentsThrowsNPEOnNullExpressionInValidation() throws Throwable {
        LinkedList<CheckedFragmentValidationBuildItem> validations = new LinkedList<CheckedFragmentValidationBuildItem>();
        LinkedList<Expression> expressions = new LinkedList<Expression>();
        expressions.add(((Expression) (null)));
        CheckedFragmentValidationBuildItem validation = new CheckedFragmentValidationBuildItem("IN78 +@gT{<^b8", expressions, ((CheckedTemplateBuildItem) (null)));
        validations.add(validation);
        QuteProcessor processor = new QuteProcessor();
        HashMap<Integer, QuteProcessor.MatchResult> matches = new HashMap<Integer, QuteProcessor.MatchResult>();
        TemplateExpressionMatchesBuildItem matchItem = new TemplateExpressionMatchesBuildItem("IN78 +@gT{<^b8", matches);
        LinkedList<TemplateGlobalBuildItem> globals = new LinkedList<TemplateGlobalBuildItem>();
        Class<ValidationPhaseBuildItem.ValidationErrorBuildItem> errorClass = ValidationPhaseBuildItem.ValidationErrorBuildItem.class;
        BuildContext buildContext = mock(BuildContext.class, new ViolatedAssumptionAnswer());
        BuildProducerImpl<ValidationPhaseBuildItem.ValidationErrorBuildItem> errorProducer = new BuildProducerImpl<ValidationPhaseBuildItem.ValidationErrorBuildItem>(errorClass, buildContext);
        List<TemplateExpressionMatchesBuildItem> allMatches = List.of(matchItem, matchItem, matchItem, matchItem, matchItem, matchItem, matchItem);
        HashMap<DotName, List<AnnotationInstance>> annotations = new HashMap<DotName, List<AnnotationInstance>>();
        HashMap<DotName, List<ClassInfo>> map = new HashMap<DotName, List<ClassInfo>>();
        HashMap<DotName, ClassInfo> classes = new HashMap<DotName, ClassInfo>();
        Index index = Index.create(((Map<DotName, List<AnnotationInstance>>) (annotations)), ((Map<DotName, List<ClassInfo>>) (map)), ((Map<DotName, List<ClassInfo>>) (map)), ((Map<DotName, ClassInfo>) (classes)), ((Map<DotName, List<ClassInfo>>) (map)));
        HashSet<DotName> additional = new HashSet<DotName>();
        BeanArchiveIndexBuildItem beanArchiveIndex = new BeanArchiveIndexBuildItem(index, index, additional);
        // Undeclared exception!
        try {
            processor.validateCheckedFragments(validations, allMatches, globals, beanArchiveIndex, errorProducer);
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            // 
            // Cannot invoke \"io.quarkus.qute.Expression.isLiteral()\" because \"expression\" is null
            // 
            verifyException("io.quarkus.qute.deployment.QuteProcessor", e);
        }
    }

    @Test(timeout = 4000)
    public void validateCheckedFragmentsProcessesMatchingTemplateId() throws Throwable {
        LinkedList<CheckedFragmentValidationBuildItem> validations = new LinkedList<CheckedFragmentValidationBuildItem>();
        LinkedList<Expression> expressions = new LinkedList<Expression>();
        CheckedFragmentValidationBuildItem validation = new CheckedFragmentValidationBuildItem("properties", expressions, ((CheckedTemplateBuildItem) (null)));
        validations.add(validation);
        QuteProcessor processor = new QuteProcessor();
        HashMap<Integer, QuteProcessor.MatchResult> matches = new HashMap<Integer, QuteProcessor.MatchResult>();
        TemplateExpressionMatchesBuildItem otherTemplate = new TemplateExpressionMatchesBuildItem("*l-`5-ir<};", matches);
        LinkedList<TemplateGlobalBuildItem> globals = new LinkedList<TemplateGlobalBuildItem>();
        Class<ValidationPhaseBuildItem.ValidationErrorBuildItem> errorClass = ValidationPhaseBuildItem.ValidationErrorBuildItem.class;
        BuildProducerImpl<ValidationPhaseBuildItem.ValidationErrorBuildItem> errorProducer = new BuildProducerImpl<ValidationPhaseBuildItem.ValidationErrorBuildItem>(errorClass, ((BuildContext) (null)));
        TemplateExpressionMatchesBuildItem targetTemplate = new TemplateExpressionMatchesBuildItem("properties", matches);
        List<TemplateExpressionMatchesBuildItem> allMatches = List.of(otherTemplate, otherTemplate, targetTemplate, targetTemplate, targetTemplate, otherTemplate, targetTemplate);
        HashMap<DotName, List<AnnotationInstance>> annotations = new HashMap<DotName, List<AnnotationInstance>>();
        HashMap<DotName, List<ClassInfo>> map = new HashMap<DotName, List<ClassInfo>>();
        HashMap<DotName, ClassInfo> classes = new HashMap<DotName, ClassInfo>();
        Index index = Index.create(((Map<DotName, List<AnnotationInstance>>) (annotations)), ((Map<DotName, List<ClassInfo>>) (map)), ((Map<DotName, List<ClassInfo>>) (map)), ((Map<DotName, ClassInfo>) (classes)), ((Map<DotName, List<ClassInfo>>) (map)));
        HashSet<DotName> additional = new HashSet<DotName>();
        BeanArchiveIndexBuildItem beanArchiveIndex = new BeanArchiveIndexBuildItem(index, index, additional);
        processor.validateCheckedFragments(validations, allMatches, globals, beanArchiveIndex, errorProducer);
        assertEquals(7, allMatches.size());
    }

    @Test(timeout = 4000)
    public void validateCheckedFragmentsWithNoInputsNoErrors() throws Throwable {
        QuteProcessor processor = new QuteProcessor();
        LinkedList<CheckedFragmentValidationBuildItem> validations = new LinkedList<CheckedFragmentValidationBuildItem>();
        Vector<TemplateExpressionMatchesBuildItem> matches = new Vector<TemplateExpressionMatchesBuildItem>();
        LinkedList<TemplateGlobalBuildItem> globals = new LinkedList<TemplateGlobalBuildItem>();
        HashSet<DotName> additional = new HashSet<DotName>();
        BeanArchiveIndexBuildItem beanArchiveIndex = new BeanArchiveIndexBuildItem(((IndexView) (null)), ((IndexView) (null)), additional);
        Class<ValidationPhaseBuildItem.ValidationErrorBuildItem> errorClass = ValidationPhaseBuildItem.ValidationErrorBuildItem.class;
        BuildContext buildContext = mock(BuildContext.class, new ViolatedAssumptionAnswer());
        BuildProducerImpl<ValidationPhaseBuildItem.ValidationErrorBuildItem> errorProducer = new BuildProducerImpl<ValidationPhaseBuildItem.ValidationErrorBuildItem>(errorClass, buildContext);
        processor.validateCheckedFragments(validations, matches, globals, beanArchiveIndex, errorProducer);
        assertTrue(matches.isEmpty());
    }

    @Test(timeout = 4000)
    public void validateCheckedFragmentsThrowsNPEWhenCheckedTemplateNull() throws Throwable {
        LinkedList<CheckedFragmentValidationBuildItem> validations = new LinkedList<CheckedFragmentValidationBuildItem>();
        LinkedList<Expression> expressions = new LinkedList<Expression>();
        CheckedFragmentValidationBuildItem validation = new CheckedFragmentValidationBuildItem("properties", expressions, ((CheckedTemplateBuildItem) (null)));
        validations.add(validation);
        QuteProcessor processor = new QuteProcessor();
        HashMap<Integer, QuteProcessor.MatchResult> matches = new HashMap<Integer, QuteProcessor.MatchResult>();
        TemplateExpressionMatchesBuildItem matchItem = new TemplateExpressionMatchesBuildItem("*l-`5-ir<};", matches);
        LinkedList<TemplateGlobalBuildItem> globals = new LinkedList<TemplateGlobalBuildItem>();
        Class<ValidationPhaseBuildItem.ValidationErrorBuildItem> errorClass = ValidationPhaseBuildItem.ValidationErrorBuildItem.class;
        BuildProducerImpl<ValidationPhaseBuildItem.ValidationErrorBuildItem> errorProducer = new BuildProducerImpl<ValidationPhaseBuildItem.ValidationErrorBuildItem>(errorClass, ((BuildContext) (null)));
        List<TemplateExpressionMatchesBuildItem> allMatches = List.of(matchItem, matchItem, matchItem, matchItem, matchItem, matchItem, matchItem);
        HashMap<DotName, List<AnnotationInstance>> annotations = new HashMap<DotName, List<AnnotationInstance>>();
        HashMap<DotName, List<ClassInfo>> map = new HashMap<DotName, List<ClassInfo>>();
        HashMap<DotName, ClassInfo> classes = new HashMap<DotName, ClassInfo>();
        Index index = Index.create(((Map<DotName, List<AnnotationInstance>>) (annotations)), ((Map<DotName, List<ClassInfo>>) (map)), ((Map<DotName, List<ClassInfo>>) (map)), ((Map<DotName, ClassInfo>) (classes)), ((Map<DotName, List<ClassInfo>>) (map)));
        HashSet<DotName> additional = new HashSet<DotName>();
        BeanArchiveIndexBuildItem beanArchiveIndex = new BeanArchiveIndexBuildItem(index, index, additional);
        // Undeclared exception!
        try {
            processor.validateCheckedFragments(validations, allMatches, globals, beanArchiveIndex, errorProducer);
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            // 
            // Cannot read field \"templateId\" because \"validation.checkedTemplate\" is null
            // 
            verifyException("io.quarkus.qute.deployment.QuteProcessor", e);
        }
    }

    @Test(timeout = 4000)
    public void analyzeTemplatesProducesCheckedFragmentValidationForNonTag() throws Throwable {
        QuteProcessor processor = new QuteProcessor();
        Vector<TemplatePathBuildItem> paths = new Vector<TemplatePathBuildItem>();
        TemplatePathBuildItem pathItem = mock(TemplatePathBuildItem.class, new ViolatedAssumptionAnswer());
        doReturn("Displays version information of the command", "Displays version information of the command", "iHm8X").when(pathItem).getPath();
        doReturn(false).when(pathItem).isTag();
        paths.add(pathItem);
        EffectiveTemplatePathsBuildItem effectivePaths = new EffectiveTemplatePathsBuildItem(paths);
        LinkedList<MessageBundleMethodBuildItem> messageBundles = new LinkedList<MessageBundleMethodBuildItem>();
        LinkedList<ValidationParserHookBuildItem> hooks = new LinkedList<ValidationParserHookBuildItem>();
        Optional<EngineConfigurationsBuildItem> engineConfigs = Optional.empty();
        Class<CheckedFragmentValidationBuildItem> validationClass = CheckedFragmentValidationBuildItem.class;
        BuildContext buildContext = mock(BuildContext.class, new ViolatedAssumptionAnswer());
        BuildProducerImpl<CheckedFragmentValidationBuildItem> validationProducer = new BuildProducerImpl<CheckedFragmentValidationBuildItem>(validationClass, buildContext);
        InsertSectionHelper.Factory factory = new InsertSectionHelper.Factory();
        List<String> suffixes = factory.getDefaultAliases();
        QuteConfig config = mock(QuteConfig.class, new ViolatedAssumptionAnswer());
        doReturn(suffixes).when(config).suffixes();
        TemplateFilePathsBuildItem filePaths = processor.collectTemplateFilePaths(config, effectivePaths);
        ArrayList<CheckedTemplateBuildItem> checkedTemplates = new ArrayList<CheckedTemplateBuildItem>();
        ArrayList<TemplateGlobalBuildItem> globals = new ArrayList<TemplateGlobalBuildItem>();
        QuteConfig anotherConfig = mock(QuteConfig.class, new ViolatedAssumptionAnswer());
        Class<TemplatesAnalysisBuildItem> analysesClass = TemplatesAnalysisBuildItem.class;
        BuildContext buildContext1 = mock(BuildContext.class, new ViolatedAssumptionAnswer());
        BuildProducerImpl<TemplatesAnalysisBuildItem> analysesProducer = new BuildProducerImpl<TemplatesAnalysisBuildItem>(analysesClass, buildContext1);
        processor.analyzeTemplates(effectivePaths, filePaths, checkedTemplates, messageBundles, globals, anotherConfig, hooks, engineConfigs, ((BeanArchiveIndexBuildItem) (null)), validationProducer, analysesProducer);
        assertEquals(0, globals.size());
    }

    @Test(timeout = 4000)
    public void analyzeTemplatesThrowsNPEWhenEngineConfigContainsNullClassInfo() throws Throwable {
        QuteProcessor processor = new QuteProcessor();
        Stack<TemplatePathBuildItem> paths = new Stack<TemplatePathBuildItem>();
        EffectiveTemplatePathsBuildItem effectivePaths = new EffectiveTemplatePathsBuildItem(paths);
        TemplateFilePathsBuildItem filePaths = processor.collectTemplateFilePaths(((QuteConfig) (null)), effectivePaths);
        LinkedList<CheckedTemplateBuildItem> checkedTemplates = new LinkedList<CheckedTemplateBuildItem>();
        Stack<ValidationParserHookBuildItem> hooks = new Stack<ValidationParserHookBuildItem>();
        EmptyIndex emptyIndex = EmptyIndex.INSTANCE;
        LinkedList<ClassInfo> classes = new LinkedList<ClassInfo>();
        EngineConfigurationsBuildItem engineConfigsItem = new EngineConfigurationsBuildItem(classes);
        Optional<EngineConfigurationsBuildItem> engineConfigs = Optional.of(engineConfigsItem);
        classes.add(((ClassInfo) (null)));
        BeanArchiveIndexBuildItem beanArchiveIndex = new BeanArchiveIndexBuildItem(emptyIndex, emptyIndex, ((Set<DotName>) (null)));
        Class<CheckedFragmentValidationBuildItem> validationClass = CheckedFragmentValidationBuildItem.class;
        BuildProducerImpl<CheckedFragmentValidationBuildItem> validationProducer = new BuildProducerImpl<CheckedFragmentValidationBuildItem>(validationClass, ((BuildContext) (null)));
        Stack<MessageBundleMethodBuildItem> messageBundles = new Stack<MessageBundleMethodBuildItem>();
        Class<TemplatesAnalysisBuildItem> analysesClass = TemplatesAnalysisBuildItem.class;
        BuildProducerImpl<TemplatesAnalysisBuildItem> analysesProducer = new BuildProducerImpl<TemplatesAnalysisBuildItem>(analysesClass, ((BuildContext) (null)));
        // Undeclared exception!
        try {
            processor.analyzeTemplates(effectivePaths, filePaths, checkedTemplates, messageBundles, ((List<TemplateGlobalBuildItem>) (null)), ((QuteConfig) (null)), hooks, engineConfigs, beanArchiveIndex, validationProducer, analysesProducer);
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            // 
            // Cannot invoke \"org.jboss.jandex.ClassInfo.interfaceNames()\" because \"target\" is null
            // 
            verifyException("io.quarkus.qute.deployment.Types", e);
        }
    }

    @Test(timeout = 4000)
    public void analyzeTemplatesThrowsNPEWhenTemplatePathNull() throws Throwable {
        QuteProcessor processor = new QuteProcessor();
        Stack<TemplatePathBuildItem> paths = new Stack<TemplatePathBuildItem>();
        EffectiveTemplatePathsBuildItem effectivePaths = new EffectiveTemplatePathsBuildItem(paths);
        TemplateFilePathsBuildItem filePaths = processor.collectTemplateFilePaths(((QuteConfig) (null)), effectivePaths);
        LinkedList<CheckedTemplateBuildItem> checkedTemplates = new LinkedList<CheckedTemplateBuildItem>();
        Stack<ValidationParserHookBuildItem> hooks = new Stack<ValidationParserHookBuildItem>();
        EmptyIndex emptyIndex = EmptyIndex.INSTANCE;
        LinkedList<ClassInfo> classes = new LinkedList<ClassInfo>();
        EngineConfigurationsBuildItem engineConfigsItem = new EngineConfigurationsBuildItem(classes);
        Optional<EngineConfigurationsBuildItem> engineConfigs = Optional.of(engineConfigsItem);
        BeanArchiveIndexBuildItem beanArchiveIndex = new BeanArchiveIndexBuildItem(emptyIndex, emptyIndex, ((Set<DotName>) (null)));
        Class<CheckedFragmentValidationBuildItem> validationClass = CheckedFragmentValidationBuildItem.class;
        TemplatePathBuildItem pathItem = mock(TemplatePathBuildItem.class, new ViolatedAssumptionAnswer());
        doReturn(((String) (null))).when(pathItem).getPath();
        doReturn(false).when(pathItem).isTag();
        paths.addElement(pathItem);
        BuildProducerImpl<CheckedFragmentValidationBuildItem> validationProducer = new BuildProducerImpl<CheckedFragmentValidationBuildItem>(validationClass, ((BuildContext) (null)));
        Stack<MessageBundleMethodBuildItem> messageBundles = new Stack<MessageBundleMethodBuildItem>();
        Class<TemplatesAnalysisBuildItem> analysesClass = TemplatesAnalysisBuildItem.class;
        BuildProducerImpl<TemplatesAnalysisBuildItem> analysesProducer = new BuildProducerImpl<TemplatesAnalysisBuildItem>(analysesClass, ((BuildContext) (null)));
        // Undeclared exception!
        try {
            processor.analyzeTemplates(effectivePaths, filePaths, checkedTemplates, messageBundles, ((List<TemplateGlobalBuildItem>) (null)), ((QuteConfig) (null)), hooks, engineConfigs, beanArchiveIndex, validationProducer, analysesProducer);
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            // 
            // no message in exception (getMessage() returned null)
            // 
            verifyException("java.util.concurrent.ConcurrentHashMap", e);
        }
    }

    @Test(timeout = 4000)
    public void collectEffectiveTemplatePathsReturnsResultForPrioritizeStrategy() throws Throwable {
        QuteProcessor processor = new QuteProcessor();
        QuteConfig.DuplicitTemplatesStrategy strategy = QuteConfig.DuplicitTemplatesStrategy.PRIORITIZE;
        QuteConfig config = mock(QuteConfig.class, new ViolatedAssumptionAnswer());
        doReturn(strategy).when(config).duplicitTemplatesStrategy();
        LinkedList<TemplatePathBuildItem> paths = new LinkedList<TemplatePathBuildItem>();
        EffectiveTemplatePathsBuildItem effectivePaths = processor.collectEffectiveTemplatePaths(config, paths);
        assertNotNull(effectivePaths);
    }

    @Test(timeout = 4000)
    public void collectEffectiveTemplatePathsReturnsResultForFailStrategy() throws Throwable {
        QuteProcessor processor = new QuteProcessor();
        QuteConfig.DuplicitTemplatesStrategy strategy = QuteConfig.DuplicitTemplatesStrategy.FAIL;
        QuteConfig config = mock(QuteConfig.class, new ViolatedAssumptionAnswer());
        doReturn(strategy).when(config).duplicitTemplatesStrategy();
        LinkedList<TemplatePathBuildItem> paths = new LinkedList<TemplatePathBuildItem>();
        EffectiveTemplatePathsBuildItem effectivePaths = processor.collectEffectiveTemplatePaths(config, paths);
        assertNotNull(effectivePaths);
    }

    @Test(timeout = 4000)
    public void collectCheckedTemplatesThrowsISEWhenTemplateInstanceNotIndexed() throws Throwable {
        QuteProcessor processor = new QuteProcessor();
        IndexView emptyIndex = IndexView.empty();
        Locale prc = Locale.PRC;
        Set<String> attributes = prc.getUnicodeLocaleAttributes();
        TemplateFilePathsBuildItem filePaths = new TemplateFilePathsBuildItem(attributes);
        ArrayDeque<Pattern> patterns = new ArrayDeque<Pattern>(-2718);
        CustomTemplateLocatorPatternsBuildItem customPatterns = new CustomTemplateLocatorPatternsBuildItem(patterns);
        LinkedBlockingDeque<IndexView> indices = new LinkedBlockingDeque<IndexView>();
        CompositeIndex composite = CompositeIndex.create(((Collection<IndexView>) (indices)));
        DotName charName = DotName.CHARACTER_CLASS_NAME;
        Set<DotName> subpackages = composite.getSubpackages(charName);
        BeanArchiveIndexBuildItem beanArchiveIndex = new BeanArchiveIndexBuildItem(emptyIndex, emptyIndex, subpackages);
        Class<BytecodeTransformerBuildItem> transformerClass = BytecodeTransformerBuildItem.class;
        BuildContext buildContext = mock(BuildContext.class, new ViolatedAssumptionAnswer());
        BuildProducerImpl<BytecodeTransformerBuildItem> transformerProducer = new BuildProducerImpl<BytecodeTransformerBuildItem>(transformerClass, buildContext);
        LinkedList<CheckedTemplateAdapterBuildItem> adapters = new LinkedList<CheckedTemplateAdapterBuildItem>();
        // Undeclared exception!
        try {
            processor.collectCheckedTemplates(beanArchiveIndex, transformerProducer, adapters, filePaths, customPatterns);
            fail("Expecting exception: IllegalStateException");
        } catch (IllegalStateException e) {
            // 
            // io.quarkus.qute.TemplateInstance not found in the index
            // 
            verifyException("io.quarkus.qute.deployment.QuteProcessor", e);
        }
    }

    @Test(timeout = 4000)
    public void collectCheckedTemplatesThrowsNPEWhenAdapterReturnsNullBinaryName() throws Throwable {
        CheckedTemplateAdapter adapter = mock(CheckedTemplateAdapter.class, new ViolatedAssumptionAnswer());
        doReturn(">ZQgZ", ((String) (null))).when(adapter).templateInstanceBinaryName();
        CheckedTemplateAdapterBuildItem adapterItem = new CheckedTemplateAdapterBuildItem(adapter);
        List<CheckedTemplateAdapterBuildItem> adapters = List.of(adapterItem, adapterItem, adapterItem);
        LinkedHashSet<String> files = new LinkedHashSet<String>();
        TemplateFilePathsBuildItem filePaths = new TemplateFilePathsBuildItem(files);
        ArrayDeque<Pattern> patterns = new ArrayDeque<Pattern>(-254003081);
        CustomTemplateLocatorPatternsBuildItem customPatterns = new CustomTemplateLocatorPatternsBuildItem(patterns);
        QuteProcessor processor = new QuteProcessor();
        // Undeclared exception!
        try {
            processor.collectCheckedTemplates(((BeanArchiveIndexBuildItem) (null)), ((BuildProducer<BytecodeTransformerBuildItem>) (null)), adapters, filePaths, customPatterns);
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            // 
            // Cannot invoke \"String.replace(char, char)\" because the return value of \"io.quarkus.qute.deployment.CheckedTemplateAdapter.templateInstanceBinaryName()\" is null
            // 
            verifyException("io.quarkus.qute.deployment.QuteProcessor", e);
        }
    }

    @Test(timeout = 4000)
    public void processTemplateErrorsThrowsRuntimeExceptionWithDetailedMessagesCase0() throws Throwable {
        QuteProcessor processor = new QuteProcessor();
        LinkedList<TemplatesAnalysisBuildItem.TemplateAnalysis> analysesList = new LinkedList<TemplatesAnalysisBuildItem.TemplateAnalysis>();
        TemplatesAnalysisBuildItem analyses = new TemplatesAnalysisBuildItem(analysesList);
        TemplateNode.Origin origin0 = mock(TemplateNode.Origin.class, new ViolatedAssumptionAnswer());
        doReturn(1239, 1239, 1239, 0, 0).when(origin0).getLine();
        doReturn(1239, 30, -157, 0, 0).when(origin0).getLineCharacterStart();
        doReturn("@TemplateData declared on %s is ignored: target %s it is not available in the index", "z{:}J`ynr\"wS;p", "@TemplateData declared on %s is ignored: target %s it is not available in the index", ((String) (null)), ((String) (null))).when(origin0).getTemplateGeneratedId();
        doReturn("5fZb3BV@gqkIQb%q6+Z", ".V6}", "@TemplateData declared on %s is ignored: target %s it is not available in the index", ((String) (null)), ((String) (null))).when(origin0).getTemplateId();
        IncorrectExpressionBuildItem incorrect0 = new IncorrectExpressionBuildItem("@TemplateData declared on %s is ignored: target %s it is not available in the index", "@TemplateData declared on %s is ignored: target %s it is not available in the index", origin0);
        TemplateNode.Origin origin1 = mock(TemplateNode.Origin.class, new ViolatedAssumptionAnswer());
        doReturn(0).when(origin1).getLine();
        doReturn(0).when(origin1).getLineCharacterStart();
        doReturn(((String) (null))).when(origin1).getTemplateGeneratedId();
        doReturn(((String) (null))).when(origin1).getTemplateId();
        IncorrectExpressionBuildItem incorrect1 = new IncorrectExpressionBuildItem("Zr#75-e~lw:<R", "vn5o^#q5n>;igPe{'GK", "Zr#75-e~lw:<R", origin1);
        List<IncorrectExpressionBuildItem> incorrectList = List.of(incorrect0, incorrect0, incorrect0, incorrect0, incorrect0, incorrect0, incorrect0, incorrect1, incorrect0);
        Class<ServiceStartBuildItem> serviceStartClass = ServiceStartBuildItem.class;
        BuildContext buildContext = mock(BuildContext.class, new ViolatedAssumptionAnswer());
        BuildProducerImpl<ServiceStartBuildItem> serviceStartProducer = new BuildProducerImpl<ServiceStartBuildItem>(serviceStartClass, buildContext);
        // Undeclared exception!
        try {
            processor.processTemplateErrors(analyses, incorrectList, serviceStartProducer);
            fail("Expecting exception: RuntimeException");
        } catch (RuntimeException e) {
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
            verifyException("io.quarkus.qute.deployment.QuteProcessor", e);
        }
    }

    @Test(timeout = 4000)
    public void processTemplateErrorsThrowsRuntimeExceptionWithDetailedMessagesCase1() throws Throwable {
        QuteProcessor processor = new QuteProcessor();
        TemplateNode.Origin origin0 = mock(TemplateNode.Origin.class, new ViolatedAssumptionAnswer());
        doReturn(0, 0, 0, 0, 0).when(origin0).getLine();
        doReturn(0, 0, 0, 0, 0).when(origin0).getLineCharacterStart();
        doReturn(((String) (null)), ((String) (null)), ((String) (null)), ((String) (null)), ((String) (null))).when(origin0).getTemplateGeneratedId();
        doReturn(((String) (null)), ((String) (null)), ((String) (null)), ((String) (null)), ((String) (null))).when(origin0).getTemplateId();
        IncorrectExpressionBuildItem incorrect0 = new IncorrectExpressionBuildItem("ParserHook registered during template analysis: %s", "<}", "ParserHook registered during template analysis: %s", origin0, "ParserHook registered during template analysis: %s");
        TemplateNode.Origin origin1 = mock(TemplateNode.Origin.class, new ViolatedAssumptionAnswer());
        doReturn(0, 0, 0).when(origin1).getLine();
        doReturn(0, 0, 0).when(origin1).getLineCharacterStart();
        doReturn(((String) (null)), ((String) (null)), ((String) (null))).when(origin1).getTemplateGeneratedId();
        doReturn(((String) (null)), ((String) (null)), ((String) (null))).when(origin1).getTemplateId();
        IncorrectExpressionBuildItem incorrect1 = new IncorrectExpressionBuildItem(((String) (null)), ((String) (null)), origin1);
        List<IncorrectExpressionBuildItem> incorrectList = List.of(incorrect0, incorrect0, incorrect0, incorrect1, incorrect1, incorrect0, incorrect0, incorrect1, incorrect0);
        Class<ServiceStartBuildItem> serviceStartClass = ServiceStartBuildItem.class;
        BuildContext buildContext = mock(BuildContext.class, new ViolatedAssumptionAnswer());
        BuildProducerImpl<ServiceStartBuildItem> serviceStartProducer = new BuildProducerImpl<ServiceStartBuildItem>(serviceStartClass, buildContext);
        LinkedList<TemplatesAnalysisBuildItem.TemplateAnalysis> analysesList = new LinkedList<TemplatesAnalysisBuildItem.TemplateAnalysis>();
        TemplatesAnalysisBuildItem analyses = new TemplatesAnalysisBuildItem(analysesList);
        // Undeclared exception!
        try {
            processor.processTemplateErrors(analyses, incorrectList, serviceStartProducer);
            fail("Expecting exception: RuntimeException");
        } catch (RuntimeException e) {
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
            verifyException("io.quarkus.qute.deployment.QuteProcessor", e);
        }
    }

    @Test(timeout = 4000)
    public void firstPassLookupConfigIndexDelegates() throws Throwable {
        ArrayList<IndexView> views = new ArrayList<IndexView>();
        StackedIndex stackedIndex = StackedIndex.create(((List<IndexView>) (views)));
        Object target = new Object();
        Predicate<AnnotationTarget> filter = Predicate.isEqual(target);
        QuteProcessor.FixedJavaMemberLookupConfig next = new QuteProcessor.FixedJavaMemberLookupConfig(stackedIndex, filter, true);
        Boolean declaredOnly = Boolean.valueOf(true);
        QuteProcessor.FirstPassJavaMemberLookupConfig first = new QuteProcessor.FirstPassJavaMemberLookupConfig(next, filter, declaredOnly);
        first.index();
        assertTrue(next.declaredMembersOnly());
    }

    @Test(timeout = 4000)
    public void fixedLookupConfigFilterNoChangeWhenDeclaredFalse() throws Throwable {
        AlwaysFalsePredicate<Object> alwaysFalse = new AlwaysFalsePredicate<Object>();
        Predicate<AnnotationTarget> filter = Predicate.not(alwaysFalse);
        QuteProcessor.FixedJavaMemberLookupConfig config = new QuteProcessor.FixedJavaMemberLookupConfig(((IndexView) (null)), filter, false);
        config.filter();
        assertFalse(config.declaredMembersOnly());
    }

    @Test(timeout = 4000)
    public void fixedLookupConfigDeclaredMembersOnlyReturnsFalseWhenConfigured() throws Throwable {
        AlwaysFalsePredicate<Object> alwaysFalse = new AlwaysFalsePredicate<Object>();
        Predicate<AnnotationTarget> filter = Predicate.not(alwaysFalse);
        QuteProcessor.FixedJavaMemberLookupConfig config = new QuteProcessor.FixedJavaMemberLookupConfig(((IndexView) (null)), filter, false);
        boolean declaredOnly = config.declaredMembersOnly();
        assertFalse(declaredOnly);
    }

    @Test(timeout = 4000)
    public void extractMatchTypeThrowsIAEWhenTypeNotParameterized() throws Throwable {
        LinkedHashSet<Type> candidates = new LinkedHashSet<Type>();
        Class<ResolvedDependencyBuilder> upperBound = ResolvedDependencyBuilder.class;
        WildcardType wildcard = WildcardType.createUpperBound(upperBound);
        candidates.add(wildcard);
        Function<Type, Type> firstParamExtractor = QuteProcessor.FIRST_PARAM_TYPE_EXTRACT_FUN;
        DotName name = wildcard.name();
        // Undeclared exception!
        try {
            QuteProcessor.extractMatchType(candidates, name, firstParamExtractor);
            fail("Expecting exception: IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // 
            // Not a parameterized type!
            // 
            verifyException("org.jboss.jandex.Type", e);
        }
    }

    @Test(timeout = 4000)
    public void mapEntryExtractFunctionReturnsValueType() throws Throwable {
        Function<Type, Type> extractor = QuteProcessor.MAP_ENTRY_EXTRACT_FUN;
        Class<Object> objectClass = Object.class;
        DotName typeName = DotName.createSimple(objectClass);
        Type[] typeArgs = new Type[5];
        ParameterizedType paramType = ParameterizedType.create(typeName, typeArgs);
        Type valueType = extractor.apply(paramType);
        assertNotSame(paramType, valueType);
    }

    @Test(timeout = 4000)
    public void endToEndCollectTemplateVariantsAnalyzeTemplatesNoErrors() throws Throwable {
        QuteProcessor processor = new QuteProcessor();
        Stack<TemplatePathBuildItem> paths = new Stack<TemplatePathBuildItem>();
        TemplatePathBuildItem pathItem = mock(TemplatePathBuildItem.class, new ViolatedAssumptionAnswer());
        doReturn("B<mXn6Grr06pAJ'").when(pathItem).getContent();
        doReturn("NO_SECTION_HELPER_FOUND", "B<mXn6Grr06pAJ'", "NO_SECTION_HELPER_FOUND", "k,rWgE)MEP", "k,rWgE)MEP").when(pathItem).getPath();
        doReturn(true).when(pathItem).isTag();
        paths.add(pathItem);
        EffectiveTemplatePathsBuildItem effectivePaths = new EffectiveTemplatePathsBuildItem(paths);
        EmptyIndex emptyIndex = EmptyIndex.INSTANCE;
        WhenSectionHelper.Factory whenFactory = new WhenSectionHelper.Factory();
        List<String> suffixes = whenFactory.getBlockLabels();
        QuteConfig config = mock(QuteConfig.class, new ViolatedAssumptionAnswer());
        doReturn(suffixes).when(config).suffixes();
        processor.collectTemplateVariants(effectivePaths, config);
        QuteConfig config2 = mock(QuteConfig.class, new ViolatedAssumptionAnswer());
        doReturn(suffixes).when(config2).suffixes();
        TemplateFilePathsBuildItem filePaths = processor.collectTemplateFilePaths(config2, effectivePaths);
        Vector<CheckedTemplateBuildItem> checked = new Vector<CheckedTemplateBuildItem>();
        LinkedList<MessageBundleMethodBuildItem> messageBundles = new LinkedList<MessageBundleMethodBuildItem>();
        QuteConfig config3 = mock(QuteConfig.class, new ViolatedAssumptionAnswer());
        doReturn(suffixes).when(config3).suffixes();
        Vector<ValidationParserHookBuildItem> hooks = new Vector<ValidationParserHookBuildItem>();
        Optional<EngineConfigurationsBuildItem> engineConfigs = Optional.ofNullable(null);
        BeanArchiveIndexBuildItem beanArchiveIndex = new BeanArchiveIndexBuildItem(emptyIndex, emptyIndex, ((Set<DotName>) (null)));
        Class<CheckedFragmentValidationBuildItem> validationClass = CheckedFragmentValidationBuildItem.class;
        BuildContext buildContext = mock(BuildContext.class, new ViolatedAssumptionAnswer());
        BuildProducerImpl<CheckedFragmentValidationBuildItem> validationProducer = new BuildProducerImpl<CheckedFragmentValidationBuildItem>(validationClass, buildContext);
        BuildProducer<TemplatesAnalysisBuildItem> analysesProducer = ((BuildProducer<TemplatesAnalysisBuildItem>) (mock(BuildProducer.class, new ViolatedAssumptionAnswer())));
        processor.analyzeTemplates(effectivePaths, filePaths, checked, messageBundles, ((List<TemplateGlobalBuildItem>) (null)), config3, hooks, engineConfigs, beanArchiveIndex, validationProducer, analysesProducer);
        assertEquals(0, hooks.size());
    }

    @Test(timeout = 4000)
    public void codeGetNameReturnsPrefixedConstant() throws Throwable {
        QuteProcessor.Code code = QuteProcessor.Code.INCORRECT_EXPRESSION;
        String name = code.getName();
        assertEquals("BUILD_INCORRECT_EXPRESSION", name);
    }

    @Test(timeout = 4000)
    public void existingValueResolversGetGeneratedGlobalClassReturnsNullWhenAbsent() throws Throwable {
        QuteProcessor.ExistingValueResolvers resolvers = new QuteProcessor.ExistingValueResolvers();
        DotName key = DotName.createSimple("t2");
        String generated = resolvers.getGeneratedGlobalClass(key);
        assertNull(generated);
    }

    @Test(timeout = 4000)
    public void existingValueResolversGetGeneratedClassThrowsNPEOnNullMethod() throws Throwable {
        QuteProcessor.ExistingValueResolvers resolvers = new QuteProcessor.ExistingValueResolvers();
        // Undeclared exception!
        try {
            resolvers.getGeneratedClass(((MethodInfo) (null)));
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            // 
            // Cannot invoke \"org.jboss.jandex.MethodInfo.declaringClass()\" because \"extensionMethod\" is null
            // 
            verifyException("io.quarkus.qute.deployment.QuteProcessor$ExistingValueResolvers", e);
        }
    }

    @Test(timeout = 4000)
    public void firstPassLookupConfigNextPartNullNextThrowsOnFilter() throws Throwable {
        Predicate<AnnotationTarget> filter = Predicate.isEqual(null);
        Boolean declaredOnly = Boolean.TRUE;
        QuteProcessor.FirstPassJavaMemberLookupConfig first = new QuteProcessor.FirstPassJavaMemberLookupConfig(((QuteProcessor.JavaMemberLookupConfig) (null)), filter, declaredOnly);
        first.nextPart();
        // Undeclared exception!
        try {
            first.filter();
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            // 
            // Cannot invoke \"io.quarkus.qute.deployment.QuteProcessor$JavaMemberLookupConfig.filter()\" because \"this.next\" is null
            // 
            verifyException("io.quarkus.qute.deployment.QuteProcessor$FirstPassJavaMemberLookupConfig", e);
        }
    }

    @Test(timeout = 4000)
    public void collectTemplatesThrowsNPEWhenApplicationArchivesNullCase2() throws Throwable {
        QuteProcessor processor = new QuteProcessor();
        Vector<TemplatePathExcludeBuildItem> excludes = new Vector<TemplatePathExcludeBuildItem>(64711720, 1452);
        Class<TemplatePathBuildItem> pathClass = TemplatePathBuildItem.class;
        BuildContext buildContext = mock(BuildContext.class, new ViolatedAssumptionAnswer());
        BuildProducerImpl<TemplatePathBuildItem> pathProducer = new BuildProducerImpl<TemplatePathBuildItem>(pathClass, buildContext);
        CurateOutcomeBuildItem curateOutcome = new CurateOutcomeBuildItem(((ApplicationModel) (null)));
        Class<HotDeploymentWatchedFileBuildItem> watchedClass = HotDeploymentWatchedFileBuildItem.class;
        BuildContext buildContext1 = mock(BuildContext.class, new ViolatedAssumptionAnswer());
        BuildProducerImpl<HotDeploymentWatchedFileBuildItem> watchedProducer = new BuildProducerImpl<HotDeploymentWatchedFileBuildItem>(watchedClass, buildContext1);
        LaunchMode mode = LaunchMode.DEVELOPMENT;
        DevModeType devMode = DevModeType.REMOTE_SERVER_SIDE;
        Optional<DevModeType> devModeOpt = Optional.of(devMode);
        LaunchModeBuildItem launchItem = new LaunchModeBuildItem(mode, devModeOpt, true, devModeOpt, true);
        QuteConfig config = mock(QuteConfig.class, new ViolatedAssumptionAnswer());
        doReturn(((Pattern) (null))).when(config).templatePathExclude();
        // Undeclared exception!
        try {
            processor.collectTemplates(((ApplicationArchivesBuildItem) (null)), curateOutcome, excludes, watchedProducer, pathProducer, ((BuildProducer<NativeImageResourceBuildItem>) (null)), config, ((TemplateRootsBuildItem) (null)), launchItem);
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            // 
            // Cannot invoke \"io.quarkus.deployment.builditem.ApplicationArchivesBuildItem.getAllApplicationArchives()\" because \"applicationArchives\" is null
            // 
            verifyException("io.quarkus.qute.deployment.QuteProcessor", e);
        }
    }

    @Test(timeout = 4000)
    public void readTemplateContentThrowsUncheckedIOExceptionForInvalidPath() throws Throwable {
        String[] segments = new String[6];
        segments[0] = "i-t";
        segments[1] = "i-t";
        segments[2] = "i-t";
        segments[3] = "i-t";
        segments[4] = "i-t";
        segments[5] = "i-t";
        Path path = Path.of("i-t", segments);
        Charset charset = Charset.defaultCharset();
        // Undeclared exception!
        try {
            QuteProcessor.readTemplateContent(path, charset);
            fail("Expecting exception: UncheckedIOException");
        } catch (UncheckedIOException e) {
            // 
            // Unable to read the template content from path: i-t/i-t/i-t/i-t/i-t/i-t/i-t
            // 
            verifyException("io.quarkus.qute.deployment.QuteProcessor", e);
        }
    }

    @Test(timeout = 4000)
    public void turnLocationIntoQualifierProducesRegistrarBuildItem() throws Throwable {
        QuteProcessor processor = new QuteProcessor();
        QualifierRegistrarBuildItem item = processor.turnLocationIntoQualifier();
        assertNotNull(item);
    }

    @Test(timeout = 4000)
    public void additionalBeansIsNotRemovable() throws Throwable {
        QuteProcessor processor = new QuteProcessor();
        AdditionalBeanBuildItem additional = processor.additionalBeans();
        assertFalse(additional.isRemovable());
    }

    @Test(timeout = 4000)
    public void beanDefiningAnnotationsReturnsThreeItems() throws Throwable {
        QuteProcessor processor = new QuteProcessor();
        List<BeanDefiningAnnotationBuildItem> annotations = processor.beanDefiningAnnotations();
        assertEquals(3, annotations.size());
    }

    @Test(timeout = 4000)
    public void defaultTemplateRootReturnsTemplatesPath() throws Throwable {
        QuteProcessor processor = new QuteProcessor();
        TemplateRootBuildItem defaultRoot = processor.defaultTemplateRoot();
        List<TemplateRootBuildItem> roots = List.of(defaultRoot, defaultRoot, defaultRoot, defaultRoot, defaultRoot, defaultRoot, defaultRoot);
        processor.collectTemplateRoots(roots);
        assertEquals("templates", defaultRoot.getPath());
    }

    @Test(timeout = 4000)
    public void quteDebuggerBeanIsNotRemovable() throws Throwable {
        QuteProcessor processor = new QuteProcessor();
        AdditionalBeanBuildItem debuggerBean = processor.quteDebuggerBean();
        assertFalse(debuggerBean.isRemovable());
    }

    @Test(timeout = 4000)
    public void featureBuildItemHasQuteName() throws Throwable {
        QuteProcessor processor = new QuteProcessor();
        FeatureBuildItem feature = processor.feature();
        assertEquals("qute", feature.getName());
    }

    @Test(timeout = 4000)
    public void initializeGeneratedClassesThrowsNPEWhenArcContainerNull() throws Throwable {
        QuteProcessor processor = new QuteProcessor();
        BeanContainer container = mock(BeanContainer.class, new ViolatedAssumptionAnswer());
        BeanContainerBuildItem containerItem = new BeanContainerBuildItem(container);
        QuteRecorder recorder = new QuteRecorder();
        LinkedList<GeneratedValueResolverBuildItem> resolvers = new LinkedList<GeneratedValueResolverBuildItem>();
        ArrayList<TemplateGlobalProviderBuildItem> providers = new ArrayList<TemplateGlobalProviderBuildItem>();
        // Undeclared exception!
        try {
            processor.initializeGeneratedClasses(containerItem, recorder, resolvers, providers);
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            // 
            // Cannot invoke \"io.quarkus.arc.ArcContainer.instance(java.lang.Class, java.lang.annotation.Annotation[])\" because the return value of \"io.quarkus.arc.Arc.container()\" is null
            // 
            verifyException("io.quarkus.qute.runtime.QuteRecorder", e);
        }
    }

    @Test(timeout = 4000)
    public void generateValueResolversThrowsNPEWhenImplicitClassesNull() throws Throwable {
        QuteProcessor processor = new QuteProcessor();
        QuteConfig config = mock(QuteConfig.class, new ViolatedAssumptionAnswer());
        Class<GeneratedClassBuildItem> generatedClass = GeneratedClassBuildItem.class;
        BuildContext buildContext = mock(BuildContext.class, new ViolatedAssumptionAnswer());
        BuildProducerImpl<GeneratedClassBuildItem> generatedClassProducer = new BuildProducerImpl<GeneratedClassBuildItem>(generatedClass, buildContext);
        BuildProducer<GeneratedResourceBuildItem> generatedResourceProducer = ((BuildProducer<GeneratedResourceBuildItem>) (mock(BuildProducer.class, new ViolatedAssumptionAnswer())));
        Stack<IndexView> views = new Stack<IndexView>();
        StackedIndex stackedIndex = StackedIndex.create(((List<IndexView>) (views)));
        EmptyIndex emptyIndex = EmptyIndex.INSTANCE;
        Set<DotName> subpackages = emptyIndex.getSubpackages("k,rWgE)MEPcase");
        BeanArchiveIndexBuildItem beanArchiveIndex = new BeanArchiveIndexBuildItem(stackedIndex, stackedIndex, subpackages);
        Vector<TemplateExtensionMethodBuildItem> extensionMethods = new Vector<TemplateExtensionMethodBuildItem>();
        LinkedList<TemplatesAnalysisBuildItem.TemplateAnalysis> analysesList = new LinkedList<TemplatesAnalysisBuildItem.TemplateAnalysis>();
        TemplatesAnalysisBuildItem analyses = new TemplatesAnalysisBuildItem(analysesList);
        LinkedList<PanacheEntityClassesBuildItem> panache = new LinkedList<PanacheEntityClassesBuildItem>();
        TreeSet<String> entities = new TreeSet<String>();
        PanacheEntityClassesBuildItem panacheEntities = new PanacheEntityClassesBuildItem(entities);
        panache.add(panacheEntities);
        Stack<TemplateDataBuildItem> templateData = new Stack<TemplateDataBuildItem>();
        LinkedBlockingDeque<TemplateGlobalBuildItem> globalsQueue = new LinkedBlockingDeque<TemplateGlobalBuildItem>();
        ArrayList<TemplateGlobalBuildItem> globals = new ArrayList<TemplateGlobalBuildItem>(globalsQueue);
        Stack<IncorrectExpressionBuildItem> incorrect = new Stack<IncorrectExpressionBuildItem>();
        LinkedList<IncorrectExpressionBuildItem> incorrectList = new LinkedList<IncorrectExpressionBuildItem>(incorrect);
        LiveReloadBuildItem liveReload = new LiveReloadBuildItem();
        CompletedApplicationClassPredicateBuildItem completedPredicate = mock(CompletedApplicationClassPredicateBuildItem.class, new ViolatedAssumptionAnswer());
        BuildProducer<GeneratedValueResolverBuildItem> valueResolverProducer = ((BuildProducer<GeneratedValueResolverBuildItem>) (mock(BuildProducer.class, new ViolatedAssumptionAnswer())));
        BuildProducer<ReflectiveClassBuildItem> reflectiveProducer = ((BuildProducer<ReflectiveClassBuildItem>) (mock(BuildProducer.class, new ViolatedAssumptionAnswer())));
        BuildProducer<TemplateGlobalProviderBuildItem> providerProducer = ((BuildProducer<TemplateGlobalProviderBuildItem>) (mock(BuildProducer.class, new ViolatedAssumptionAnswer())));
        // Undeclared exception!
        try {
            processor.generateValueResolvers(config, generatedClassProducer, generatedResourceProducer, beanArchiveIndex, ((ApplicationArchivesBuildItem) (null)), extensionMethods, ((List<ImplicitValueResolverBuildItem>) (null)), analyses, panache, templateData, globals, incorrectList, liveReload, completedPredicate, valueResolverProducer, reflectiveProducer, providerProducer);
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            // 
            // Cannot invoke \"java.util.List.iterator()\" because \"implicitClasses\" is null
            // 
            verifyException("io.quarkus.qute.deployment.QuteProcessor", e);
        }
    }

    @Test(timeout = 4000)
    public void generateValueResolversWorksWithEmptyInputs() throws Throwable {
        EmptyIndex emptyIndex = EmptyIndex.INSTANCE;
        LinkedHashSet<DotName> additional = new LinkedHashSet<DotName>();
        QuteProcessor processor = new QuteProcessor();
        QuteConfig config = mock(QuteConfig.class, new ViolatedAssumptionAnswer());
        Class<GeneratedClassBuildItem> generatedClass = GeneratedClassBuildItem.class;
        BuildContext buildContext = mock(BuildContext.class, new ViolatedAssumptionAnswer());
        BuildProducerImpl<GeneratedClassBuildItem> generatedClassProducer = new BuildProducerImpl<GeneratedClassBuildItem>(generatedClass, buildContext);
        BuildProducer<GeneratedResourceBuildItem> generatedResourceProducer = ((BuildProducer<GeneratedResourceBuildItem>) (mock(BuildProducer.class, new ViolatedAssumptionAnswer())));
        BeanArchiveIndexBuildItem beanArchiveIndex = new BeanArchiveIndexBuildItem(emptyIndex, emptyIndex, additional);
        Stack<TemplateExtensionMethodBuildItem> extensionMethods = new Stack<TemplateExtensionMethodBuildItem>();
        Stack<ImplicitValueResolverBuildItem> implicitResolvers = new Stack<ImplicitValueResolverBuildItem>();
        LinkedList<TemplatesAnalysisBuildItem.TemplateAnalysis> analysesList = new LinkedList<TemplatesAnalysisBuildItem.TemplateAnalysis>();
        TemplatesAnalysisBuildItem analyses = new TemplatesAnalysisBuildItem(analysesList);
        LinkedList<PanacheEntityClassesBuildItem> panache = new LinkedList<PanacheEntityClassesBuildItem>();
        Vector<TemplateDataBuildItem> templateData = new Vector<TemplateDataBuildItem>();
        ArrayList<TemplateGlobalBuildItem> globals = new ArrayList<TemplateGlobalBuildItem>();
        Stack<IncorrectExpressionBuildItem> incorrect = new Stack<IncorrectExpressionBuildItem>();
        LiveReloadBuildItem liveReload = new LiveReloadBuildItem();
        BuildProducer<GeneratedValueResolverBuildItem> valueResolverProducer = ((BuildProducer<GeneratedValueResolverBuildItem>) (mock(BuildProducer.class, new ViolatedAssumptionAnswer())));
        BuildProducer<ReflectiveClassBuildItem> reflectiveProducer = ((BuildProducer<ReflectiveClassBuildItem>) (mock(BuildProducer.class, new ViolatedAssumptionAnswer())));
        BuildProducer<TemplateGlobalProviderBuildItem> providerProducer = ((BuildProducer<TemplateGlobalProviderBuildItem>) (mock(BuildProducer.class, new ViolatedAssumptionAnswer())));
        processor.generateValueResolvers(config, generatedClassProducer, generatedResourceProducer, beanArchiveIndex, ((ApplicationArchivesBuildItem) (null)), extensionMethods, implicitResolvers, analyses, panache, templateData, globals, incorrect, liveReload, ((CompletedApplicationClassPredicateBuildItem) (null)), valueResolverProducer, reflectiveProducer, providerProducer);
        QuteConfig config2 = mock(QuteConfig.class, new ViolatedAssumptionAnswer());
        Class<GeneratedResourceBuildItem> resourceClass = GeneratedResourceBuildItem.class;
        BuildContext buildContext1 = mock(BuildContext.class, new ViolatedAssumptionAnswer());
        BuildProducerImpl<GeneratedResourceBuildItem> generatedResourceProducer2 = new BuildProducerImpl<GeneratedResourceBuildItem>(resourceClass, buildContext1);
        BuildProducer<GeneratedValueResolverBuildItem> valueResolverProducer2 = ((BuildProducer<GeneratedValueResolverBuildItem>) (mock(BuildProducer.class, new ViolatedAssumptionAnswer())));
        BuildProducer<ReflectiveClassBuildItem> reflectiveProducer2 = ((BuildProducer<ReflectiveClassBuildItem>) (mock(BuildProducer.class, new ViolatedAssumptionAnswer())));
        BuildProducer<TemplateGlobalProviderBuildItem> providerProducer2 = ((BuildProducer<TemplateGlobalProviderBuildItem>) (mock(BuildProducer.class, new ViolatedAssumptionAnswer())));
        processor.generateValueResolvers(config2, generatedClassProducer, generatedResourceProducer2, beanArchiveIndex, ((ApplicationArchivesBuildItem) (null)), extensionMethods, implicitResolvers, analyses, panache, templateData, globals, incorrect, liveReload, ((CompletedApplicationClassPredicateBuildItem) (null)), valueResolverProducer2, reflectiveProducer2, providerProducer2);
        assertEquals(0, incorrect.size());
    }

    @Test(timeout = 4000)
    public void matchResultGetTypeParametersThrowsNPEWhenClassNull() throws Throwable {
        EmptyIndex emptyIndex = EmptyIndex.INSTANCE;
        Types.AssignabilityCheck assignability = new Types.AssignabilityCheck(emptyIndex);
        QuteProcessor.MatchResult matchResult = new QuteProcessor.MatchResult(assignability);
        // Undeclared exception!
        try {
            matchResult.getTypeParameters();
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            // 
            // Cannot invoke \"org.jboss.jandex.ClassInfo.typeParameters()\" because \"this.clazz\" is null
            // 
            verifyException("io.quarkus.qute.deployment.QuteProcessor$MatchResult", e);
        }
    }

    @Test(timeout = 4000)
    public void matchResultTypeReturnsNullByDefault() throws Throwable {
        EmptyIndex emptyIndex = EmptyIndex.INSTANCE;
        Types.AssignabilityCheck assignability = new Types.AssignabilityCheck(emptyIndex);
        QuteProcessor.MatchResult matchResult = new QuteProcessor.MatchResult(assignability);
        Type type = matchResult.type();
        assertNull(type);
    }

    @Test(timeout = 4000)
    public void matchResultClearValuesDoesNotThrow() throws Throwable {
        HashMap<DotName, List<AnnotationInstance>> annotations = new HashMap<DotName, List<AnnotationInstance>>();
        HashMap<DotName, List<ClassInfo>> map = new HashMap<DotName, List<ClassInfo>>();
        HashMap<DotName, ClassInfo> classes = new HashMap<DotName, ClassInfo>();
        Index index = Index.create(((Map<DotName, List<AnnotationInstance>>) (annotations)), ((Map<DotName, List<ClassInfo>>) (map)), ((Map<DotName, List<ClassInfo>>) (map)), ((Map<DotName, List<ClassInfo>>) (map)), ((Map<DotName, ClassInfo>) (classes)), ((Map<DotName, List<ClassInfo>>) (map)));
        Types.AssignabilityCheck assignability = new Types.AssignabilityCheck(index);
        QuteProcessor.MatchResult matchResult = new QuteProcessor.MatchResult(assignability);
        matchResult.clearValues();
    }

    @Test(timeout = 4000)
    public void matchResultClazzReturnsNullByDefault() throws Throwable {
        Types.AssignabilityCheck assignability = new Types.AssignabilityCheck(((IndexView) (null)));
        QuteProcessor.MatchResult matchResult = new QuteProcessor.MatchResult(assignability);
        ClassInfo clazz = matchResult.clazz();
        assertNull(clazz);
    }
}