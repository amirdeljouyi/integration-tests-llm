package com.orientechnologies.orient.core.sql.parser;
import com.orientechnologies.orient.core.sql.parser.OMatchStatement_ESTest_scaffolding;
import java.util.Map;
import java.util.TreeMap;
import org.junit.Test;
import static org.evosuite.runtime.EvoAssertions.verifyException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
public class OMatchStatement_ESTest_Top5 extends OMatchStatement_ESTest_scaffolding {
    @Test(timeout = 4000)
    public void testCreatesOMatchStatementTakingNoArgumentsAndCallsAddMatchExpression1() throws Throwable {
        OMatchStatement oMatchStatement0 = new OMatchStatement();
        oMatchStatement0.addMatchExpression(((OMatchExpression) (null)));
        // Undeclared exception!
        try {
            oMatchStatement0.toString();
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            // 
            // Cannot invoke \"com.orientechnologies.orient.core.sql.parser.OMatchExpression.toString(java.util.Map, StringBuilder)\" because \"expr\" is null
            // 
            verifyException("com.orientechnologies.orient.core.sql.parser.OMatchStatement", e);
        }
    }

    @Test(timeout = 4000)
    public void testCreatesOMatchStatementTakingIntAndCallsToString() throws Throwable {
        OMatchStatement oMatchStatement0 = new OMatchStatement(-2068473975);
        TreeMap<Object, Object> treeMap0 = new TreeMap<Object, Object>();
        StringBuilder stringBuilder0 = new StringBuilder("EVKgf ?\"\";vS[x[");
        oMatchStatement0.toString(((Map<Object, Object>) (treeMap0)), stringBuilder0);
        assertEquals("EVKgf ?\"\";vS[x[MATCH  RETURN ", stringBuilder0.toString());
    }

    @Test(timeout = 4000)
    public void testGetNotMatchExpressionsReturningListWhereIsEmptyIsTrue() throws Throwable {
        OMatchStatement oMatchStatement0 = new OMatchStatement(-2068473975);
        oMatchStatement0.getNotMatchExpressions();
        assertFalse(oMatchStatement0.isReturnDistinct());
    }

    @Test(timeout = 4000)
    public void testGetMatchExpressionsReturningListWhereIsEmptyIsTrue() throws Throwable {
        OMatchStatement oMatchStatement0 = new OMatchStatement(-2068473975);
        oMatchStatement0.getMatchExpressions();
        assertFalse(oMatchStatement0.isReturnDistinct());
    }

    @Test(timeout = 4000)
    public void testGetGroupByReturningNull() throws Throwable {
        OMatchStatement oMatchStatement0 = new OMatchStatement(-2068473975);
        oMatchStatement0.getGroupBy();
        assertFalse(oMatchStatement0.isReturnDistinct());
    }
}
