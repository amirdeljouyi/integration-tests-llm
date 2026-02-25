package com.orientechnologies.orient.core.sql.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.orientechnologies.orient.core.command.OCommandContext;
import com.orientechnologies.orient.core.id.ORecordId;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;
import java.util.Vector;
import org.junit.Test;

public class OMatchStatement_ESTest_Adopted_Agentic_Top5 {
    protected SimpleNode checkRightSyntax(String query) {
        SimpleNode result = checkSyntax(query, true);
        StringBuilder builder = new StringBuilder();
        result.toString(null, builder);
        return checkSyntax(builder.toString(), true);
    }

    protected SimpleNode checkWrongSyntax(String query) {
        return checkSyntax(query, false);
    }

    protected SimpleNode checkSyntax(String query, boolean isCorrect) {
        OrientSql osql = getParserFor(query);
        try {
            SimpleNode result = osql.parse();
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

    protected OrientSql getParserFor(String string) {
        InputStream is = new ByteArrayInputStream(string.getBytes());
        OrientSql osql = new OrientSql(is);
        return osql;
    }

    @Test(timeout = 4000)
    public void matchContext_toDoc_throwsNoClassDefFoundError() {
        OMatchStatement matchStatement = new OMatchStatement();
        OMatchStatement.MatchContext matchContext = matchStatement.new MatchContext();
        try {
            matchContext.toDoc();
            fail("Expecting exception: NoClassDefFoundError");
        } catch (NoClassDefFoundError e) {
            assertEquals("com.orientechnologies.orient.core.db.document.ODatabaseDocumentAbstract", e.getStackTrace()[0].getClassName());
        }
    }

    @Test(timeout = 4000)
    public void copy_preservesReturnItems() {
        OMatchStatement matchStatement = new OMatchStatement();
        OExpression expression = new OExpression(((OIdentifier) (null)));
        matchStatement.addReturnItem(expression);
        OMatchStatement copiedStatement = matchStatement.copy();
        assertFalse(copiedStatement.isReturnDistinct());
        assertTrue(copiedStatement.equals(((Object) (matchStatement))));
    }

    @Test(timeout = 4000)
    public void createExecutionPlan_throwsNPE_whenContextNull() {
        OMatchStatement matchStatement = new OMatchStatement();
        try {
            matchStatement.createExecutionPlan(((OCommandContext) (null)));
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            assertEquals("com.orientechnologies.orient.core.sql.executor.OMatchExecutionPlanner", e.getStackTrace()[0].getClassName());
        }
    }

    @Test(timeout = 4000)
    public void copy_preservesReturnNestedProjections_fromImmutableList() {
        OMatchStatement matchStatement = new OMatchStatement();
        ONestedProjection nestedProjection = new ONestedProjection(-1725222855);
        List<ONestedProjection> nestedProjections = Collections.singletonList(nestedProjection);
        matchStatement.setReturnNestedProjections(nestedProjections);
        OMatchStatement copiedStatement = matchStatement.copy();
        assertFalse(copiedStatement.isReturnDistinct());
        assertTrue(copiedStatement.equals(((Object) (matchStatement))));
    }

    @Test(timeout = 4000)
    public void copy_preservesUnwind() {
        OMatchStatement matchStatement = new OMatchStatement();
        OUnwind unwind = new OUnwind(((OrientSql) (null)), -1556);
        matchStatement.setUnwind(unwind);
        OMatchStatement copiedStatement = matchStatement.copy();
        assertTrue(copiedStatement.equals(((Object) (matchStatement))));
        assertFalse(copiedStatement.isReturnDistinct());
    }
}
