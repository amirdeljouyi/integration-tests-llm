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
    protected com.orientechnologies.orient.core.sql.parser.SimpleNode checkRightSyntax(String query) {
        com.orientechnologies.orient.core.sql.parser.SimpleNode result = checkSyntax(query, true);
        StringBuilder builder = new StringBuilder();
        result.toString(null, builder);
        return checkSyntax(builder.toString(), true);
    }

    protected com.orientechnologies.orient.core.sql.parser.SimpleNode checkWrongSyntax(String query) {
        return checkSyntax(query, false);
    }

    protected com.orientechnologies.orient.core.sql.parser.SimpleNode checkSyntax(String query, boolean isCorrect) {
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

    protected com.orientechnologies.orient.core.sql.parser.OrientSql getParserFor(String string) {
        InputStream is = new ByteArrayInputStream(string.getBytes());
        com.orientechnologies.orient.core.sql.parser.OrientSql osql = new com.orientechnologies.orient.core.sql.parser.OrientSql(is);
        return osql;
    }

    @Test(timeout = 4000)

    /**
     * This test added coverage 34.79% (838/2409 added lines among kept tests).
     * Delta details: +120 methods, +64 branches, +4069 instructions.
     * Full version of the covered block is here: <a href="https://github.com/orientechnologies/orientdb/blob/main/core/src/main/java/com/orientechnologies/orient/core/config/OGlobalConfiguration.java#L43-L44">OGlobalConfiguration.java (lines 43-44)</a>
     * Covered Lines:
     * <pre><code>
     * <span style="background-color:#fff3b0;">public enum OGlobalConfiguration { // ENVIRONMENT</span>
     * <span style="background-color:#fff3b0;">  ENVIRONMENT_DUMP_CFG_AT_STARTUP(</span>
     * </code></pre>
     * Full version of the covered block is here: <a href="https://github.com/orientechnologies/orientdb/blob/main/core/src/main/java/com/orientechnologies/orient/core/Orient.java#L68-L68">Orient.java (lines 68-68)</a>
     * Covered Lines:
     * <pre><code>
     * <span style="background-color:#fff3b0;">  private static final OLogger logger = OLogManager.instance().logger(Orient.class);</span>
     * </code></pre>
     * Additional covered classes omitted: 50
     */
    public void matchContext_toDoc_throwsNoClassDefFoundError() {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement();
        com.orientechnologies.orient.core.sql.parser.OMatchStatement.MatchContext matchContext = matchStatement.new com.orientechnologies.orient.core.sql.parser.MatchContext();
        try {
            matchContext.toDoc();
            fail("Expecting exception: NoClassDefFoundError");
        } catch (NoClassDefFoundError e) {
            assertEquals("com.orientechnologies.orient.core.db.document.ODatabaseDocumentAbstract", e.getStackTrace()[0].getClassName());
        }
    }

    @Test(timeout = 4000)
    public void copy_preservesReturnItems() {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement();
        com.orientechnologies.orient.core.sql.parser.OExpression expression = new com.orientechnologies.orient.core.sql.parser.OExpression(((com.orientechnologies.orient.core.sql.parser.OIdentifier) (null)));
        matchStatement.addReturnItem(expression);
        com.orientechnologies.orient.core.sql.parser.OMatchStatement copiedStatement = matchStatement.copy();
        assertFalse(copiedStatement.isReturnDistinct());
        assertTrue(copiedStatement.equals(((Object) (matchStatement))));
    }

    @Test(timeout = 4000)
    public void createExecutionPlan_throwsNPE_whenContextNull() {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement();
        try {
            matchStatement.createExecutionPlan(((OCommandContext) (null)));
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            assertEquals("com.orientechnologies.orient.core.sql.executor.OMatchExecutionPlanner", e.getStackTrace()[0].getClassName());
        }
    }

    @Test(timeout = 4000)
    public void copy_preservesReturnNestedProjections_fromImmutableList() {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement();
        com.orientechnologies.orient.core.sql.parser.ONestedProjection nestedProjection = new com.orientechnologies.orient.core.sql.parser.ONestedProjection(-1725222855);
        List<com.orientechnologies.orient.core.sql.parser.ONestedProjection> nestedProjections = Collections.singletonList(nestedProjection);
        matchStatement.setReturnNestedProjections(nestedProjections);
        com.orientechnologies.orient.core.sql.parser.OMatchStatement copiedStatement = matchStatement.copy();
        assertFalse(copiedStatement.isReturnDistinct());
        assertTrue(copiedStatement.equals(((Object) (matchStatement))));
    }

    @Test(timeout = 4000)
    public void copy_preservesUnwind() {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement();
        com.orientechnologies.orient.core.sql.parser.OUnwind unwind = new com.orientechnologies.orient.core.sql.parser.OUnwind(((com.orientechnologies.orient.core.sql.parser.OrientSql) (null)), -1556);
        matchStatement.setUnwind(unwind);
        com.orientechnologies.orient.core.sql.parser.OMatchStatement copiedStatement = matchStatement.copy();
        assertTrue(copiedStatement.equals(((Object) (matchStatement))));
        assertFalse(copiedStatement.isReturnDistinct());
    }
}
