package com.orientechnologies.orient.core.sql.parser;
import com.orientechnologies.orient.core.command.OCommandContext;
import com.orientechnologies.orient.core.id.ORecordId;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;
import java.util.Vector;
import org.junit.Test;
import static org.evosuite.runtime.EvoAssertions.verifyException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
public class OMatchStatement_ESTest_Adopted_Top5 {
    // Adapted and merged parser syntax tests from IGT
    private com.orientechnologies.orient.core.sql.parser.SimpleNode checkRightSyntax(String query) {
        com.orientechnologies.orient.core.sql.parser.SimpleNode result = checkSyntax(query, true);
        StringBuilder builder = new StringBuilder();
        result.toString(null, builder);
        return checkSyntax(builder.toString(), true);
    }

    private com.orientechnologies.orient.core.sql.parser.SimpleNode checkWrongSyntax(String query) {
        return checkSyntax(query, false);
    }

    private com.orientechnologies.orient.core.sql.parser.SimpleNode checkSyntax(String query, boolean isCorrect) {
        com.orientechnologies.orient.core.sql.parser.OrientSql osql = getParserFor(query);
        try {
            com.orientechnologies.orient.core.sql.parser.SimpleNode result = osql.parse();
            if (!isCorrect) {
                fail();
            }
            return result;
        } catch (Exception e) {
            if (isCorrect) {
                e.printStackTrace();
                fail();
            }
        }
        return null;
    }

    private com.orientechnologies.orient.core.sql.parser.OrientSql getParserFor(String string) {
        InputStream is = new ByteArrayInputStream(string.getBytes());
        return new com.orientechnologies.orient.core.sql.parser.OrientSql(is);
    }

    @Test(timeout = 4000)
    public void matchContext_toDoc_throwsNoClassDefFoundError() throws Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement();
        com.orientechnologies.orient.core.sql.parser.OMatchStatement.MatchContext matchContext = matchStatement.new com.orientechnologies.orient.core.sql.parser.MatchContext();
        try {
            matchContext.toDoc();
            fail("Expecting exception: NoClassDefFoundError");
        } catch (NoClassDefFoundError e) {
            verifyException("com.orientechnologies.orient.core.db.document.ODatabaseDocumentAbstract", e);
        }
    }

    @Test(timeout = 4000)
    public void copy_preservesReturnItems() throws Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement();
        com.orientechnologies.orient.core.sql.parser.OExpression expression = new com.orientechnologies.orient.core.sql.parser.OExpression(((com.orientechnologies.orient.core.sql.parser.OIdentifier) (null)));
        matchStatement.addReturnItem(expression);
        com.orientechnologies.orient.core.sql.parser.OMatchStatement copiedStatement = matchStatement.copy();
        assertFalse(copiedStatement.isReturnDistinct());
        assertTrue(copiedStatement.equals(((Object) (matchStatement))));
    }

    @Test(timeout = 4000)
    public void createExecutionPlan_throwsNPE_whenContextNull() throws Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement();
        try {
            matchStatement.createExecutionPlan(((OCommandContext) (null)));
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            verifyException("com.orientechnologies.orient.core.sql.executor.OMatchExecutionPlanner", e);
        }
    }

    @Test(timeout = 4000)
    public void copy_preservesReturnNestedProjections_fromImmutableList() throws Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement();
        com.orientechnologies.orient.core.sql.parser.ONestedProjection nestedProjection = new com.orientechnologies.orient.core.sql.parser.ONestedProjection(-1725222855);
        List<com.orientechnologies.orient.core.sql.parser.ONestedProjection> nestedProjections = List.of(nestedProjection);
        matchStatement.setReturnNestedProjections(nestedProjections);
        com.orientechnologies.orient.core.sql.parser.OMatchStatement copiedStatement = matchStatement.copy();
        assertFalse(copiedStatement.isReturnDistinct());
        assertTrue(copiedStatement.equals(((Object) (matchStatement))));
    }

    @Test(timeout = 4000)
    public void copy_preservesUnwind() throws Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement();
        com.orientechnologies.orient.core.sql.parser.OUnwind unwind = new com.orientechnologies.orient.core.sql.parser.OUnwind(((com.orientechnologies.orient.core.sql.parser.OrientSql) (null)), -1556);
        matchStatement.setUnwind(unwind);
        com.orientechnologies.orient.core.sql.parser.OMatchStatement copiedStatement = matchStatement.copy();
        assertTrue(copiedStatement.equals(((Object) (matchStatement))));
        assertFalse(copiedStatement.isReturnDistinct());
    }
}
