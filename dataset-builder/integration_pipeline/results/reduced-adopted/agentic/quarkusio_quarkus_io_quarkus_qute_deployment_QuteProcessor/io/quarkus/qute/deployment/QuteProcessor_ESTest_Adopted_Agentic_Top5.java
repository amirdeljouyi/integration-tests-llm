package io.quarkus.qute.deployment;
import io.quarkus.arc.deployment.BeanArchiveIndexBuildItem;
import io.quarkus.arc.deployment.SynthesisFinishedBuildItem;
import io.quarkus.arc.deployment.SyntheticBeanBuildItem;
import io.quarkus.arc.processor.BeanDeployment;
import io.quarkus.arc.processor.BeanResolver;
import io.quarkus.arc.processor.InjectionPointInfo;
import io.quarkus.builder.BuildContext;
import io.quarkus.deployment.BuildProducerImpl;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.dev.AlwaysFalsePredicate;
import io.quarkus.deployment.pkg.NativeConfig;
import io.quarkus.qute.Expression;
import io.quarkus.qute.runtime.QuteConfig;
import io.quarkus.qute.runtime.QuteRecorder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.function.Function;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.ClassType;
import org.jboss.jandex.DotName;
import org.jboss.jandex.IndexView;
import org.jboss.jandex.PrimitiveType;
import org.jboss.jandex.Type;
import org.jboss.jandex.TypeVariable;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
public class QuteProcessor_ESTest_Adopted_Agentic_Top5 {
    @Test
    public void validateExpressionsWithNullBeanDeploymentCollectionsThrowsNPE() {
        QuteProcessor processor = new QuteProcessor();
        AlwaysFalsePredicate<TypeCheckExcludeBuildItem.TypeCheck> alwaysFalse = new AlwaysFalsePredicate<>();
        TypeCheckExcludeBuildItem excludeItem = new TypeCheckExcludeBuildItem(alwaysFalse, true);
        List<TypeCheckExcludeBuildItem> excludes = List.of(excludeItem, excludeItem, excludeItem, excludeItem, excludeItem);
        Class<IncorrectExpressionBuildItem> incorrectClass = IncorrectExpressionBuildItem.class;
        BuildContext buildContext = mock(BuildContext.class);
        BuildProducerImpl<IncorrectExpressionBuildItem> incorrectProducer = new BuildProducerImpl<>(incorrectClass, buildContext);
        BeanDeployment beanDeployment = mock(BeanDeployment.class);
        doReturn(((BeanResolver) (null))).when(beanDeployment).getBeanResolver();
        doReturn(((Collection<?>) (null))).when(beanDeployment).getBeans();
        doReturn(((Collection<?>) (null))).when(beanDeployment).getInjectionPoints();
        doReturn(((Collection<?>) (null))).when(beanDeployment).getObservers();
        SynthesisFinishedBuildItem synthesisFinished = new SynthesisFinishedBuildItem(beanDeployment);
        ArrayList<CheckedTemplateBuildItem> checkedTemplates = new ArrayList<>();
        Stack<TemplateDataBuildItem> templateDataItems = new Stack<>();
        TemplatesAnalysisBuildItem analyses = new TemplatesAnalysisBuildItem(((List<TemplatesAnalysisBuildItem.TemplateAnalysis>) (null)));
        LinkedHashSet<DotName> beanExclusions = new LinkedHashSet<>();
        BeanArchiveIndexBuildItem beanArchiveIndex = new BeanArchiveIndexBuildItem(((IndexView) (null)), ((IndexView) (null)), beanExclusions);
        ArrayList<TemplateExtensionMethodBuildItem> extensionMethods = new ArrayList<>();
        Class<ImplicitValueResolverBuildItem> implicitClass = ImplicitValueResolverBuildItem.class;
        BuildContext buildContext1 = mock(BuildContext.class);
        BuildProducerImpl<ImplicitValueResolverBuildItem> implicitProducer = new BuildProducerImpl<>(implicitClass, buildContext1);
        NativeConfig nativeConfig = mock(NativeConfig.class);
        NullPointerException exception = assertThrows(NullPointerException.class, () -> processor.validateExpressions(analyses, beanArchiveIndex, extensionMethods, excludes, incorrectProducer, implicitProducer, ((BuildProducer<TemplateExpressionMatchesBuildItem>) (null)), synthesisFinished, checkedTemplates, templateDataItems, ((QuteConfig) (null)), nativeConfig, ((List<TemplateGlobalBuildItem>) (null))));
        boolean found = false;
        for (StackTraceElement element : exception.getStackTrace()) {
            if (element.getClassName().equals("java.util.Objects")) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    @Test
    public void initializeWithValidParamsButNonProxyRecorderThrowsIAE() {
        QuteProcessor processor = new QuteProcessor();
        Class<SyntheticBeanBuildItem> syntheticClass = SyntheticBeanBuildItem.class;
        BuildContext buildContext = mock(BuildContext.class);
        BuildProducerImpl<SyntheticBeanBuildItem> syntheticProducer = new BuildProducerImpl<>(syntheticClass, buildContext);
        QuteRecorder recorder = new QuteRecorder();
        Stack<TemplatePathBuildItem> templatePaths = new Stack<>();
        EffectiveTemplatePathsBuildItem effectivePaths = new EffectiveTemplatePathsBuildItem(templatePaths);
        HashMap<String, List<String>> variants = new HashMap<>();
        TemplateVariantsBuildItem templateVariants = new TemplateVariantsBuildItem(variants);
        Optional<TemplateVariantsBuildItem> optionalVariants = Optional.of(templateVariants);
        LinkedList<TemplateRootBuildItem> roots = new LinkedList<>();
        TemplateRootsBuildItem templateRoots = processor.collectTemplateRoots(roots);
        Stack<TemplatePathExcludeBuildItem> excludes = new Stack<>();
        TemplatePathExcludeBuildItem exclude = new TemplatePathExcludeBuildItem(((String) (null)));
        excludes.add(exclude);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> processor.initialize(syntheticProducer, recorder, effectivePaths, optionalVariants, templateRoots, excludes));
        boolean found = false;
        for (StackTraceElement element : exception.getStackTrace()) {
            if (element.getClassName().equals("io.quarkus.arc.deployment.SyntheticBeanBuildItem$ExtendedBeanConfigurator")) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    @Test
    public void getNameOnInjectionPointThrowsIAEForInvalidType() {
        ClassType characterType = ClassType.CHARACTER_CLASS;
        LinkedHashSet<AnnotationInstance> qualifiers = new LinkedHashSet<>();
        InjectionPointInfo.TypeAndQualifiers tq = new InjectionPointInfo.TypeAndQualifiers(characterType, qualifiers);
        InjectionPointInfo ip = InjectionPointInfo.fromSyntheticInjectionPoint(tq);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> QuteProcessor.getName(ip));
        boolean found = false;
        for (StackTraceElement element : exception.getStackTrace()) {
            if (element.getClassName().equals("io.quarkus.qute.deployment.QuteProcessor")) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    @Test
    public void extractMatchTypeReturnsNullWhenNoMatch() {
        LinkedHashSet<Type> candidates = new LinkedHashSet<>();
        DotName targetName = DotName.FLOAT_CLASS_NAME;
        PrimitiveType doublePrimitive = PrimitiveType.DOUBLE;
        candidates.add(doublePrimitive);
        Function<Type, Type> extractor = QuteProcessor.MAP_ENTRY_EXTRACT_FUN;
        Type result = QuteProcessor.extractMatchType(candidates, targetName, extractor);
        assertNull(result);
    }

    @Test
    public void processLoopElementHintWithNullExpressionThrowsNPE() {
        QuteProcessor.MatchResult matchResult = new QuteProcessor.MatchResult(((Types.AssignabilityCheck) (null)));
        TypeVariable variable = TypeVariable.create("8LiOQgPN+)^-");
        matchResult.setValues(((ClassInfo) (null)), variable);
        Class<IncorrectExpressionBuildItem> incorrectClass = IncorrectExpressionBuildItem.class;
        BuildProducerImpl<IncorrectExpressionBuildItem> incorrectProducer = new BuildProducerImpl<>(incorrectClass, ((BuildContext) (null)));
        NullPointerException exception = assertThrows(NullPointerException.class, () -> QuteProcessor.processLoopElementHint(matchResult, ((IndexView) (null)), ((Expression) (null)), incorrectProducer));
        boolean found = false;
        for (StackTraceElement element : exception.getStackTrace()) {
            if (element.getClassName().equals("io.quarkus.qute.deployment.QuteProcessor")) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }
}