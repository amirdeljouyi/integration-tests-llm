package io.quarkus.qute.deployment;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;
import java.util.Vector;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

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
import org.junit.jupiter.api.Test;

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

public class QuteProcessor_ESTest_Adopted_Agentic_Top5 {
    @Test

    /**
     * This test added target-class coverage 1.09% for io.quarkus.qute.deployment.QuteProcessor (20/1836 lines).
     * Delta details: +45 methods, +54 branches, +1193 instructions.
     * Full version of the covered block is here: <a href="https://github.com/quarkusio/quarkus/blob/5b8508beac194241cbe9a4cbf2520c6ca8340e58/extensions/qute/deployment/src/main/java/io/quarkus/qute/deployment/QuteProcessor.java#L3063-L3066">QuteProcessor.java (lines 3063-3066)</a>
     * Covered Lines:
     * <pre><code>
     *             this.clazz = clazz;
     *             this.type = type;
     *             autoExtractType();
     *         }
     * </code></pre>
     * Other newly covered ranges to check: 3025-3027;3043;3051;3055;3059;3075;3079;3094
     * Full version of the covered block is here: <a href="https://github.com/quarkusio/quarkus/blob/5b8508beac194241cbe9a4cbf2520c6ca8340e58/extensions/qute/deployment/src/main/java/io/quarkus/qute/deployment/QuteProcessor.java#L2951-L2953">QuteProcessor.java (lines 2951-2953)</a>
     * Covered Lines:
     * <pre><code>
     *         if (match.isEmpty()
     *                 || match.type().name().equals(DotNames.INTEGER)
     *                 || match.type().equals(PrimitiveType.INT)) {
     * </code></pre>
     * Other newly covered ranges to check: 2956-2957;2959
     */
    public void processLoopElementHintWithNullExpressionThrowsNPE() {
        io.quarkus.qute.deployment.QuteProcessor.MatchResult matchResult = new io.quarkus.qute.deployment.QuteProcessor.MatchResult(((io.quarkus.qute.deployment.Types.AssignabilityCheck) (null)));
        TypeVariable variable = TypeVariable.create("8LiOQgPN+)^-");
        matchResult.setValues(((ClassInfo) (null)), variable);
        Class<io.quarkus.qute.deployment.IncorrectExpressionBuildItem> incorrectClass = io.quarkus.qute.deployment.IncorrectExpressionBuildItem.class;
        BuildProducerImpl<io.quarkus.qute.deployment.IncorrectExpressionBuildItem> incorrectProducer = new BuildProducerImpl<>(incorrectClass, ((BuildContext) (null)));
        NullPointerException exception = assertThrows(NullPointerException.class, () -> io.quarkus.qute.deployment.QuteProcessor.processLoopElementHint(matchResult, ((IndexView) (null)), ((Expression) (null)), incorrectProducer));
        boolean found = false;
        for (StackTraceElement element : exception.getStackTrace()) {
            if (element.getClassName().equals("io.quarkus.qute.deployment.QuteProcessor")) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    /**
     * This test added target-class coverage 0.16% for io.quarkus.qute.deployment.QuteProcessor (3/1836 lines).
     * Delta details: +28 methods, +39 branches, +808 instructions.
     * Full version of the covered block is here: <a href="https://github.com/quarkusio/quarkus/blob/5b8508beac194241cbe9a4cbf2520c6ca8340e58/extensions/qute/deployment/src/main/java/io/quarkus/qute/deployment/QuteProcessor.java#L3643-L3643">QuteProcessor.java (lines 3643-3643)</a>
     * Covered Lines:
     * <pre><code>
     *         if (injectionPoint.isField()) {
     * </code></pre>
     * Other newly covered ranges to check: 3645;3649
     */
    @Test
    public void getNameOnInjectionPointThrowsIAEForInvalidType() {
        ClassType characterType = ClassType.CHARACTER_CLASS;
        LinkedHashSet<AnnotationInstance> qualifiers = new LinkedHashSet<>();
        InjectionPointInfo.TypeAndQualifiers tq = new InjectionPointInfo.TypeAndQualifiers(characterType, qualifiers);
        InjectionPointInfo ip = InjectionPointInfo.fromSyntheticInjectionPoint(tq);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> io.quarkus.qute.deployment.QuteProcessor.getName(ip));
        boolean found = false;
        for (StackTraceElement element : exception.getStackTrace()) {
            if (element.getClassName().equals("io.quarkus.qute.deployment.QuteProcessor")) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    /**
     * This test added target-class coverage 0.82% for io.quarkus.qute.deployment.QuteProcessor (15/1836 lines).
     * Delta details: +50 methods, +29 branches, +834 instructions.
     * Full version of the covered block is here: <a href="https://github.com/quarkusio/quarkus/blob/5b8508beac194241cbe9a4cbf2520c6ca8340e58/extensions/qute/deployment/src/main/java/io/quarkus/qute/deployment/QuteProcessor.java#L2674-L2677">QuteProcessor.java (lines 2674-2677)</a>
     * Covered Lines:
     * <pre><code>
     *         List&lt;String&gt; excludePatterns = new ArrayList&lt;&gt;(templatePathExcludes.size());
     *         for (TemplatePathExcludeBuildItem exclude : templatePathExcludes) {
     *             excludePatterns.add(exclude.getRegexPattern());
     *         }
     * </code></pre>
     * Other newly covered ranges to check: 172;214-215;218;2660-2661;2668-2669;2679-2680;2682
     */
    @Test
    public void initializeWithValidParamsButNonProxyRecorderThrowsIAE() {
        io.quarkus.qute.deployment.QuteProcessor processor = new io.quarkus.qute.deployment.QuteProcessor();
        Class<SyntheticBeanBuildItem> syntheticClass = SyntheticBeanBuildItem.class;
        BuildContext buildContext = mock(BuildContext.class);
        BuildProducerImpl<SyntheticBeanBuildItem> syntheticProducer = new BuildProducerImpl<>(syntheticClass, buildContext);
        QuteRecorder recorder = new QuteRecorder();
        Stack<io.quarkus.qute.deployment.TemplatePathBuildItem> templatePaths = new Stack<>();
        io.quarkus.qute.deployment.EffectiveTemplatePathsBuildItem effectivePaths = new io.quarkus.qute.deployment.EffectiveTemplatePathsBuildItem(templatePaths);
        HashMap<String, List<String>> variants = new HashMap<>();
        io.quarkus.qute.deployment.TemplateVariantsBuildItem templateVariants = new io.quarkus.qute.deployment.TemplateVariantsBuildItem(variants);
        Optional<io.quarkus.qute.deployment.TemplateVariantsBuildItem> optionalVariants = Optional.of(templateVariants);
        LinkedList<io.quarkus.qute.deployment.TemplateRootBuildItem> roots = new LinkedList<>();
        io.quarkus.qute.deployment.TemplateRootsBuildItem templateRoots = processor.collectTemplateRoots(roots);
        Stack<io.quarkus.qute.deployment.TemplatePathExcludeBuildItem> excludes = new Stack<>();
        io.quarkus.qute.deployment.TemplatePathExcludeBuildItem exclude = new io.quarkus.qute.deployment.TemplatePathExcludeBuildItem(((String) (null)));
        excludes.add(exclude);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> processor.initialize(syntheticProducer, recorder, effectivePaths, optionalVariants, templateRoots, excludes));
        boolean found = false;
        for (StackTraceElement element : exception.getStackTrace()) {
            if (element.getClassName().equals("SyntheticBeanBuildItem$ExtendedBeanConfigurator")) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    /**
     * This test added target-class coverage 0.27% for io.quarkus.qute.deployment.QuteProcessor (5/1836 lines).
     * Delta details: +17 methods, +20 branches, +574 instructions.
     * Full version of the covered block is here: <a href="https://github.com/quarkusio/quarkus/blob/5b8508beac194241cbe9a4cbf2520c6ca8340e58/extensions/qute/deployment/src/main/java/io/quarkus/qute/deployment/QuteProcessor.java#L3009-L3011">QuteProcessor.java (lines 3009-3011)</a>
     * Covered Lines:
     * <pre><code>
     *         Type type = null;
     *         for (Type t : closure) {
     *             if (t.name().equals(matchName)) {
     * </code></pre>
     * Other newly covered ranges to check: 3014-3015
     */
    @Test
    public void extractMatchTypeReturnsNullWhenNoMatch() {
        LinkedHashSet<Type> candidates = new LinkedHashSet<>();
        DotName targetName = DotName.FLOAT_CLASS_NAME;
        PrimitiveType doublePrimitive = PrimitiveType.DOUBLE;
        candidates.add(doublePrimitive);
        Function<Type, Type> extractor = QuteProcessor.MAP_ENTRY_EXTRACT_FUN;
        Type result = io.quarkus.qute.deployment.QuteProcessor.extractMatchType(candidates, targetName, extractor);
        assertNull(result);
    }

    /**
     * This test added target-class coverage 0.27% for io.quarkus.qute.deployment.QuteProcessor (5/1836 lines).
     * Delta details: +28 methods, +8 branches, +354 instructions.
     * Full version of the covered block is here: <a href="https://github.com/quarkusio/quarkus/blob/5b8508beac194241cbe9a4cbf2520c6ca8340e58/extensions/qute/deployment/src/main/java/io/quarkus/qute/deployment/QuteProcessor.java#L1019-L1020">QuteProcessor.java (lines 1019-1020)</a>
     * Covered Lines:
     * <pre><code>
     *         IndexView index = beanArchiveIndex.getIndex();
     *         Function&lt;String, String&gt; templateIdToPathFun = new Function&lt;String, String&gt;() {
     * </code></pre>
     * Other newly covered ranges to check: 172;1014
     * Full version of the covered block is here: <a href="https://github.com/quarkusio/quarkus/blob/5b8508beac194241cbe9a4cbf2520c6ca8340e58/extensions/qute/deployment/src/main/java/io/quarkus/qute/deployment/QuteProcessor.java#L1020-L1020">QuteProcessor.java (lines 1020-1020)</a>
     * Covered Lines:
     * <pre><code>
     *         Function&lt;String, String&gt; templateIdToPathFun = new Function&lt;String, String&gt;() {
     * </code></pre>
     */
    @Test
    public void validateExpressionsWithNullBeanDeploymentCollectionsThrowsNPE() {
        io.quarkus.qute.deployment.QuteProcessor processor = new io.quarkus.qute.deployment.QuteProcessor();
        AlwaysFalsePredicate<io.quarkus.qute.deployment.TypeCheckExcludeBuildItem.TypeCheck> alwaysFalse = new AlwaysFalsePredicate<>();
        io.quarkus.qute.deployment.TypeCheckExcludeBuildItem excludeItem = new io.quarkus.qute.deployment.TypeCheckExcludeBuildItem(alwaysFalse, true);
        List<io.quarkus.qute.deployment.TypeCheckExcludeBuildItem> excludes = List.of(excludeItem, excludeItem, excludeItem, excludeItem, excludeItem);
        Class<io.quarkus.qute.deployment.IncorrectExpressionBuildItem> incorrectClass = io.quarkus.qute.deployment.IncorrectExpressionBuildItem.class;
        BuildContext buildContext = mock(BuildContext.class);
        BuildProducerImpl<io.quarkus.qute.deployment.IncorrectExpressionBuildItem> incorrectProducer = new BuildProducerImpl<>(incorrectClass, buildContext);
        BeanDeployment beanDeployment = mock(BeanDeployment.class);
        doReturn(((BeanResolver) (null))).when(beanDeployment).getBeanResolver();
        doReturn(((Collection<?>) (null))).when(beanDeployment).getBeans();
        doReturn(((Collection<?>) (null))).when(beanDeployment).getInjectionPoints();
        doReturn(((Collection<?>) (null))).when(beanDeployment).getObservers();
        SynthesisFinishedBuildItem synthesisFinished = new SynthesisFinishedBuildItem(beanDeployment);
        ArrayList<io.quarkus.qute.deployment.CheckedTemplateBuildItem> checkedTemplates = new ArrayList<>();
        Stack<io.quarkus.qute.deployment.TemplateDataBuildItem> templateDataItems = new Stack<>();
        io.quarkus.qute.deployment.TemplatesAnalysisBuildItem analyses = new io.quarkus.qute.deployment.TemplatesAnalysisBuildItem(((List<TemplateAnalysis>) (null)));
        LinkedHashSet<DotName> beanExclusions = new LinkedHashSet<>();
        BeanArchiveIndexBuildItem beanArchiveIndex = new BeanArchiveIndexBuildItem(((IndexView) (null)), ((IndexView) (null)), beanExclusions);
        ArrayList<io.quarkus.qute.deployment.TemplateExtensionMethodBuildItem> extensionMethods = new ArrayList<>();
        Class<io.quarkus.qute.deployment.ImplicitValueResolverBuildItem> implicitClass = io.quarkus.qute.deployment.ImplicitValueResolverBuildItem.class;
        BuildContext buildContext1 = mock(BuildContext.class);
        BuildProducerImpl<io.quarkus.qute.deployment.ImplicitValueResolverBuildItem> implicitProducer = new BuildProducerImpl<>(implicitClass, buildContext1);
        NativeConfig nativeConfig = mock(NativeConfig.class);
        NullPointerException exception = assertThrows(NullPointerException.class, () -> processor.validateExpressions(analyses, beanArchiveIndex, extensionMethods, excludes, incorrectProducer, implicitProducer, ((BuildProducer<io.quarkus.qute.deployment.TemplateExpressionMatchesBuildItem>) (null)), synthesisFinished, checkedTemplates, templateDataItems, ((QuteConfig) (null)), nativeConfig, ((List<io.quarkus.qute.deployment.TemplateGlobalBuildItem>) (null))));
        boolean found = false;
        for (StackTraceElement element : exception.getStackTrace()) {
            if (element.getClassName().equals("java.util.Objects")) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }
}
