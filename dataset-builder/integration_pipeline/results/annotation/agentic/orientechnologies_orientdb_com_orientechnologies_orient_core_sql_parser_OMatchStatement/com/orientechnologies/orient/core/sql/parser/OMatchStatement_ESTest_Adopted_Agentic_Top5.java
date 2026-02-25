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
     * This test added target-class coverage 2.49% for com.orientechnologies.orient.core.sql.parser.OMatchStatement (8/321 lines).
     * Delta details: +120 methods, +64 branches, +4069 instructions.
     * Full version of the covered block is here: <a href="https://github.com/orientechnologies/orientdb/blob/20ac246eb62a89d78d784974de0fd2a36752b2d8/core/src/main/java/com/orientechnologies/orient/core/sql/parser/OMatchStatement.java#L77-L79">OMatchStatement.java (lines 77-79)</a>
     * Covered Lines:
     * <pre><code>
     *     Map&lt;String, Iterable&gt; candidates = new LinkedHashMap&lt;String, Iterable&gt;();
     *     Map&lt;String, OIdentifiable&gt; matched = new LinkedHashMap&lt;String, OIdentifiable&gt;();
     *     Map&lt;PatternEdge, Boolean&gt; matchedEdges = new IdentityHashMap&lt;PatternEdge, Boolean&gt;();
     * </code></pre>
     * Other newly covered ranges to check: 74-75;96
     * Full version of the covered block is here: <a href="https://github.com/orientechnologies/orientdb/blob/20ac246eb62a89d78d784974de0fd2a36752b2d8/core/src/main/java/com/orientechnologies/orient/core/sql/parser/OMatchStatement.java#L101-L102">OMatchStatement.java (lines 101-102)</a>
     * Covered Lines:
     * <pre><code>
     *     super(-1);
     *   }
     * </code></pre>
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

    /**
     * This test added target-class coverage 21.50% for com.orientechnologies.orient.core.sql.parser.OMatchStatement (69/321 lines).
     * Delta details: +22 methods, +97 branches, +674 instructions.
     * Full version of the covered block is here: <a href="https://github.com/orientechnologies/orientdb/blob/20ac246eb62a89d78d784974de0fd2a36752b2d8/core/src/main/java/com/orientechnologies/orient/core/sql/parser/OMatchStatement.java#L438-L448">OMatchStatement.java (lines 438-448)</a>
     * Covered Lines:
     * <pre><code>
     *             : returnNestedProjections.stream()
     *                 .map(x -&gt; x == null ? null : x.copy())
     *                 .collect(Collectors.toList());
     *     result.groupBy = groupBy == null ? null : groupBy.copy();
     *     result.orderBy = orderBy == null ? null : orderBy.copy();
     *     result.unwind = unwind == null ? null : unwind.copy();
     *     result.skip = skip == null ? null : skip.copy();
     *     result.limit = limit == null ? null : limit.copy();
     *     result.returnDistinct = this.returnDistinct;
     *     result.buildPatterns();
     *     return result;
     * </code></pre>
     * Other newly covered ranges to check: 101-102;121-123;127-129;133-134;142;151;159-160;174;410-412;414-418;420-424;426-430;432-436;453-454;456;458-459;461-462;464;466-467;469-470;472-476;478;480;540
     */
    @Test(timeout = 4000)
    public void copy_preservesReturnItems() {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement();
        com.orientechnologies.orient.core.sql.parser.OExpression expression = new com.orientechnologies.orient.core.sql.parser.OExpression(((com.orientechnologies.orient.core.sql.parser.OIdentifier) (null)));
        matchStatement.addReturnItem(expression);
        com.orientechnologies.orient.core.sql.parser.OMatchStatement copiedStatement = matchStatement.copy();
        assertFalse(copiedStatement.isReturnDistinct());
        assertTrue(copiedStatement.equals(((Object) (matchStatement))));
    }

    /**
     * This test added target-class coverage 6.85% for com.orientechnologies.orient.core.sql.parser.OMatchStatement (22/321 lines).
     * Delta details: +27 methods, +16 branches, +426 instructions.
     * Full version of the covered block is here: <a href="https://github.com/orientechnologies/orientdb/blob/20ac246eb62a89d78d784974de0fd2a36752b2d8/core/src/main/java/com/orientechnologies/orient/core/sql/parser/OMatchStatement.java#L101-L102">OMatchStatement.java (lines 101-102)</a>
     * Covered Lines:
     * <pre><code>
     *     super(-1);
     *   }
     * </code></pre>
     * Other newly covered ranges to check: 47;113;177;182;186;191;195;203;207;212;500;508;516;524;532;540;548;556;564;572
     */
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

    /**
     * This test added target-class coverage 22.12% for com.orientechnologies.orient.core.sql.parser.OMatchStatement (71/321 lines).
     * Delta details: +12 methods, +52 branches, +405 instructions.
     * Full version of the covered block is here: <a href="https://github.com/orientechnologies/orientdb/blob/20ac246eb62a89d78d784974de0fd2a36752b2d8/core/src/main/java/com/orientechnologies/orient/core/sql/parser/OMatchStatement.java#L438-L448">OMatchStatement.java (lines 438-448)</a>
     * Covered Lines:
     * <pre><code>
     *             : returnNestedProjections.stream()
     *                 .map(x -&gt; x == null ? null : x.copy())
     *                 .collect(Collectors.toList());
     *     result.groupBy = groupBy == null ? null : groupBy.copy();
     *     result.orderBy = orderBy == null ? null : orderBy.copy();
     *     result.unwind = unwind == null ? null : unwind.copy();
     *     result.skip = skip == null ? null : skip.copy();
     *     result.limit = limit == null ? null : limit.copy();
     *     result.returnDistinct = this.returnDistinct;
     *     result.buildPatterns();
     *     return result;
     * </code></pre>
     * Other newly covered ranges to check: 51-52;101-102;121-123;127-129;133-134;142;151;159-160;174;410-412;414-418;420-424;426-430;432-436;453-454;456;458-459;461-462;464;466-467;469-470;472-476;478;480;540
     */
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

    /**
     * This test added target-class coverage 22.12% for com.orientechnologies.orient.core.sql.parser.OMatchStatement (71/321 lines).
     * Delta details: +13 methods, +43 branches, +375 instructions.
     * Full version of the covered block is here: <a href="https://github.com/orientechnologies/orientdb/blob/20ac246eb62a89d78d784974de0fd2a36752b2d8/core/src/main/java/com/orientechnologies/orient/core/sql/parser/OMatchStatement.java#L438-L448">OMatchStatement.java (lines 438-448)</a>
     * Covered Lines:
     * <pre><code>
     *             : returnNestedProjections.stream()
     *                 .map(x -&gt; x == null ? null : x.copy())
     *                 .collect(Collectors.toList());
     *     result.groupBy = groupBy == null ? null : groupBy.copy();
     *     result.orderBy = orderBy == null ? null : orderBy.copy();
     *     result.unwind = unwind == null ? null : unwind.copy();
     *     result.skip = skip == null ? null : skip.copy();
     *     result.limit = limit == null ? null : limit.copy();
     *     result.returnDistinct = this.returnDistinct;
     *     result.buildPatterns();
     *     return result;
     * </code></pre>
     * Other newly covered ranges to check: 101-102;121-123;127-129;133-134;142;151;159-160;174;410-412;414-418;420-424;426-430;432-436;453-454;456;458-459;461-462;464;466-467;469-470;472-476;478;480;540;568-569
     */
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
