package com.orientechnologies.orient.core.sql.parser;
import com.orientechnologies.orient.core.command.OCommandContext;
import java.util.List;
import org.junit.Test;
import static org.evosuite.runtime.EvoAssertions.verifyException;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
public class OMatchStatement_ESTest_Adopted_Top5 {
    @Test(timeout = 4000)
    public void copy_preservesReturnItems() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        OExpression expression = new OExpression(((OIdentifier) (null)));
        matchStatement.addReturnItem(expression);
        OMatchStatement copiedStatement = matchStatement.copy();
        assertFalse(copiedStatement.isReturnDistinct());
        assertTrue(copiedStatement.equals(((Object) (matchStatement))));
    }

    @Test(timeout = 4000)
    public void matchContext_toDoc_throwsNoClassDefFoundError() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        OMatchStatement.MatchContext matchContext = matchStatement.new com.orientechnologies.orient.core.sql.parser.MatchContext();
        try {
            matchContext.toDoc();
            fail("Expecting exception: NoClassDefFoundError");
        } catch (NoClassDefFoundError e) {
            verifyException("com.orientechnologies.orient.core.db.document.ODatabaseDocumentAbstract", e);
        }
    }

    @Test(timeout = 4000)
    public void copy_preservesUnwind() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        OUnwind unwind = new OUnwind(((OrientSql) (null)), -1556);
        matchStatement.setUnwind(unwind);
        OMatchStatement copiedStatement = matchStatement.copy();
        assertTrue(copiedStatement.equals(((Object) (matchStatement))));
        assertFalse(copiedStatement.isReturnDistinct());
    }

    @Test(timeout = 4000)
    public void copy_preservesReturnNestedProjections_fromImmutableList() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        ONestedProjection nestedProjection = new ONestedProjection(-1725222855);
        List<ONestedProjection> nestedProjections = List.of(nestedProjection);
        matchStatement.setReturnNestedProjections(nestedProjections);
        OMatchStatement copiedStatement = matchStatement.copy();
        assertFalse(copiedStatement.isReturnDistinct());
        assertTrue(copiedStatement.equals(((Object) (matchStatement))));
    }

    @Test(timeout = 4000)
    public void createExecutionPlan_throwsNPE_whenContextNull() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        try {
            matchStatement.createExecutionPlan(((OCommandContext) (null)));
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            verifyException("com.orientechnologies.orient.core.sql.executor.OMatchExecutionPlanner", e);
        }
    }
}