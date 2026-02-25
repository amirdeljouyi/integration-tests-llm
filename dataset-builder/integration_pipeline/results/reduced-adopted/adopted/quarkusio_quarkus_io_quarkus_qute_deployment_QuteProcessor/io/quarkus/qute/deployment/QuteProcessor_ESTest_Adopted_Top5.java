package io.quarkus.qute.deployment;
import io.quarkus.deployment.dev.AlwaysFalsePredicate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.jandex.Index;
import org.jboss.jandex.MethodInfo;
import org.jboss.jandex.Type;
import org.jboss.jandex.TypeVariable;
import org.junit.Test;
import static org.evosuite.runtime.EvoAssertions.verifyException;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
public class QuteProcessor_ESTest_Adopted_Top5 {
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
}