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
public class QuteProcessor_ESTest_Adopted_Top5 {
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
}
