package com.orientechnologies.orient.core.sql.parser;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;
import java.util.Vector;
import org.junit.Test;
import com.orientechnologies.orient.core.command.OCommandContext;
import com.orientechnologies.orient.core.id.ORecordId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
public class OMatchStatement_ESTest_Adopted {
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

    @Test
    public void testWrongFilterKey() {
        checkWrongSyntax("MATCH {clasx: 'V'} RETURN foo");
    }

    @Test
    public void testBasicMatch() {
        checkRightSyntax("MATCH {class: 'V', as: foo} RETURN foo");
    }

    @Test
    public void testNoReturn() {
        checkWrongSyntax("MATCH {class: 'V', as: foo}");
    }

    @Test
    public void testSingleMethod() {
        checkRightSyntax("MATCH {class: 'V', as: foo}.out() RETURN foo");
    }

    @Test
    public void testArrowsNoBrackets() {
        checkWrongSyntax("MATCH {}-->-->{as:foo} RETURN foo");
    }

    @Test
    public void testSingleMethodAndFilter() {
        checkRightSyntax("MATCH {class: 'V', as: foo}.out(){class: 'V', as: bar} RETURN foo");
        checkRightSyntax("MATCH {class: 'V', as: foo}-E->{class: 'V', as: bar} RETURN foo");
        checkRightSyntax("MATCH {class: 'V', as: foo}-->{class: 'V', as: bar} RETURN foo");
    }

    @Test
    public void testLongPath() {
        checkRightSyntax("MATCH {class: 'V', as: foo}.out().in('foo').both('bar').out(){as: bar} RETURN foo");
        checkRightSyntax("MATCH {class: 'V', as: foo}-->{}<-foo-{}-bar-{}-->{as: bar} RETURN foo");
    }

    @Test
    public void testLongPath2() {
        checkRightSyntax("MATCH {class: 'V', as: foo}.out().in('foo'){}.both('bar'){CLASS: 'bar'}.out(){as: bar}" + " RETURN foo");
    }

    @Test
    public void testFilterTypes() {
        StringBuilder query = new StringBuilder();
        query.append("MATCH {");
        query.append("   class: 'v', ");
        query.append("   as: foo, ");
        query.append("   where: (name = 'foo' and surname = 'bar' or aaa in [1,2,3]), ");
        query.append("   maxDepth: 10 ");
        query.append("} return foo");
        checkRightSyntax(query.toString());
    }

    @Test
    public void testFilterTypes2() {
        StringBuilder query = new StringBuilder();
        query.append("MATCH {");
        query.append("   classes: ['V', 'E'], ");
        query.append("   as: foo, ");
        query.append("   where: (name = 'foo' and surname = 'bar' or aaa in [1,2,3]), ");
        query.append("   maxDepth: 10 ");
        query.append("} return foo");
        checkRightSyntax(query.toString());
    }

    @Test
    public void testMultiPath() {
        StringBuilder query = new StringBuilder();
        query.append("MATCH {}");
        query.append("  .(out().in(){class:'v'}.both('Foo')){maxDepth: 3}.out() return foo");
        checkRightSyntax(query.toString());
    }

    @Test
    public void testMultiPathArrows() {
        StringBuilder query = new StringBuilder();
        query.append("MATCH {}");
        query.append("  .(-->{}<--{class:'v'}--){maxDepth: 3}-->{} return foo");
        checkRightSyntax(query.toString());
    }

    @Test
    public void testMultipleMatches() {
        String query = "MATCH {class: 'V', as: foo}.out(){class: 'V', as: bar}, ";
        query += " {class: 'V', as: foo}.out(){class: 'V', as: bar},";
        query += " {class: 'V', as: foo}.out(){class: 'V', as: bar} RETURN foo";
        checkRightSyntax(query);
    }

    @Test
    public void testMultipleMatchesArrow() {
        String query = "MATCH {class: 'V', as: foo}-->{class: 'V', as: bar}, ";
        query += " {class: 'V', as: foo}-->{class: 'V', as: bar},";
        query += " {class: 'V', as: foo}-->{class: 'V', as: bar} RETURN foo";
        checkRightSyntax(query);
    }

    @Test
    public void testWhile() {
        checkRightSyntax("MATCH {class: 'V', as: foo}.out(){while:($depth<4), as:bar} RETURN bar ");
    }

    @Test
    public void testWhileArrow() {
        checkRightSyntax("MATCH {class: 'V', as: foo}-->{while:($depth<4), as:bar} RETURN bar ");
    }

    @Test
    public void testLimit() {
        checkRightSyntax("MATCH {class: 'V'} RETURN foo limit 10");
    }

    @Test
    public void testReturnJson() {
        checkRightSyntax("MATCH {class: 'V'} RETURN {'name':'foo', 'value': bar}");
    }

    @Test
    public void testOptional() {
        checkRightSyntax("MATCH {class: 'V', as: foo}-->{}<-foo-{}-bar-{}-->{as: bar, optional:true} RETURN foo");
        checkRightSyntax("MATCH {class: 'V', as: foo}-->{}<-foo-{}-bar-{}-->{as: bar, optional:false} RETURN foo");
    }

    @Test
    public void testOrderBy() {
        checkRightSyntax("MATCH {class: 'V', as: foo}-->{} RETURN foo ORDER BY foo");
        checkRightSyntax("MATCH {class: 'V', as: foo}-->{} RETURN foo ORDER BY foo limit 10");
    }

    @Test
    public void testNestedProjections() {
        checkRightSyntax("MATCH {class: 'V', as: foo}-->{} RETURN foo:{name, surname}");
        checkRightSyntax("MATCH {class: 'V', as: foo}-->{as:bar} RETURN foo:{name, surname} as bloo, bar:{*}");
    }

    @Test
    public void testUnwind() {
        checkRightSyntax("MATCH {class: 'V', as: foo}-->{as:bar} RETURN foo.name, bar.name as x unwind x");
    }

    @Test
    public void testDepthAlias() {
        checkRightSyntax("MATCH {class: 'V', as: foo}-->{as:bar, while:($depth < 2), depthAlias: depth} RETURN" + " depth");
    }

    @Test
    public void testPathAlias() {
        checkRightSyntax("MATCH {class: 'V', as: foo}-->{as:bar, while:($depth < 2), pathAlias: barPath} RETURN" + " barPath");
    }

    @Test
    public void testClusterTarget() {
        checkRightSyntax("MATCH {cluster:v, as: foo} RETURN $elements");
        checkRightSyntax("MATCH {cluster:12, as: foo} RETURN $elements");
        checkRightSyntax("MATCH {cluster: v, as: foo} RETURN $elements");
        checkRightSyntax("MATCH {cluster: `v`, as: foo} RETURN $elements");
        checkRightSyntax("MATCH {cluster:`v`, as: foo} RETURN $elements");
        checkRightSyntax("MATCH {cluster: 12, as: foo} RETURN $elements");
        checkWrongSyntax("MATCH {cluster: 12.1, as: foo} RETURN $elements");
    }

    @Test
    public void testNot() {
        checkRightSyntax("MATCH {cluster:v, as: foo}, NOT {as:foo}-->{as:bar} RETURN $elements");
    }

    @Test
    public void testSkip() {
        checkRightSyntax("MATCH {class: 'V', as: foo}-->{as:bar} RETURN foo.name, bar.name skip 10 limit 10");
    }

    @Test
    public void testFieldTraversal() {
        checkRightSyntax("MATCH {class: 'V', as: foo}.toBar{as:bar} RETURN foo.name, bar.name skip 10 limit 10");
        checkRightSyntax("MATCH {class: 'V', as: foo}.toBar{as:bar}.out(){as:c} RETURN foo.name, bar.name skip 10" + " limit 10");
        checkRightSyntax("MATCH {class: 'V', as: foo}.toBar.baz{as:bar} RETURN foo.name, bar.name skip 10 limit 10");
        checkRightSyntax("MATCH {class: 'V', as: foo}.toBar.out(){as:bar} RETURN foo.name, bar.name skip 10 limit" + " 10");
    }

    protected OrientSql getParserFor(String string) {
        InputStream is = new ByteArrayInputStream(string.getBytes());
        OrientSql osql = new OrientSql(is);
        return osql;
    }

    @Test(timeout = 4000)
    public void buildPatterns_doesNotAffectReturnDistinct() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement(19);
        matchStatement.buildPatterns();
        assertFalse(matchStatement.isReturnDistinct());
    }

    @Test(timeout = 4000)
    public void getReturnNestedProjections_whenSetToNull_returnsNull() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        matchStatement.setReturnNestedProjections(((List<ONestedProjection>) (null)));
        List<ONestedProjection> nestedProjections = matchStatement.getReturnNestedProjections();
        assertNull(nestedProjections);
        assertFalse(matchStatement.isReturnDistinct());
    }

    @Test(timeout = 4000)
    public void getReturnItems_whenOneItemAdded_returnsListWithOneItem() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        OArrayConcatExpressionElement concatElement = new OArrayConcatExpressionElement(900);
        matchStatement.addReturnItem(concatElement);
        List<OExpression> returnItems = matchStatement.getReturnItems();
        assertFalse(matchStatement.isReturnDistinct());
        assertEquals(1, returnItems.size());
    }

    @Test(timeout = 4000)
    public void getReturnAliases_whenAliasAdded_returnsNonEmptyList() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        matchStatement.addReturnAlias(((OIdentifier) (null)));
        List<OIdentifier> aliases = matchStatement.getReturnAliases();
        assertFalse(aliases.isEmpty());
        assertFalse(matchStatement.isReturnDistinct());
    }

    @Test(timeout = 4000)
    public void getNotMatchExpressions_whenExplicitlyNull_returnsNull() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        matchStatement.notMatchExpressions = null;
        List<OMatchExpression> notMatchExpressions = matchStatement.getNotMatchExpressions();
        assertFalse(matchStatement.isReturnDistinct());
        assertNull(notMatchExpressions);
    }

    @Test(timeout = 4000)
    public void getNotMatchExpressions_whenExpressionAdded_returnsNonEmptyList() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        matchStatement.addNotMatchExpression(((OMatchExpression) (null)));
        List<OMatchExpression> notMatchExpressions = matchStatement.getNotMatchExpressions();
        assertFalse(matchStatement.isReturnDistinct());
        assertFalse(notMatchExpressions.isEmpty());
    }

    @Test(timeout = 4000)
    public void getMatchExpressions_whenSetWithOneItem_containsThatItem() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        OMatchExpression matchExpression = new OMatchExpression(1296);
        Stack<OMatchExpression> stack = new Stack<OMatchExpression>();
        stack.push(matchExpression);
        matchStatement.setMatchExpressions(stack);
        List<OMatchExpression> expressionsList = matchStatement.getMatchExpressions();
        assertFalse(matchStatement.isReturnDistinct());
        assertTrue(expressionsList.contains(matchExpression));
    }

    @Test(timeout = 4000)
    public void getGroupBy_afterSettingGroupBy_returnsNonNull() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        OGroupBy groupBy = new OGroupBy(0);
        matchStatement.setGroupBy(groupBy);
        OGroupBy result = matchStatement.getGroupBy();
        assertFalse(matchStatement.isReturnDistinct());
        assertNotNull(result);
    }

    @Test(timeout = 4000)
    public void copy_whenReturnDistinctTrue_returnsDifferentInstance() throws Throwable {
        OMatchStatement original = new OMatchStatement(-1210);
        original.returnDistinct = true;
        OMatchStatement copy = original.copy();
        assertNotSame(copy, original);
    }

    @Test(timeout = 4000)
    public void getSkip_defaultDoesNotAffectReturnDistinct() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        matchStatement.getSkip();
        assertFalse(matchStatement.isReturnDistinct());
    }

    @Test(timeout = 4000)
    public void getOrderBy_defaultDoesNotAffectReturnDistinct() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        matchStatement.getOrderBy();
        assertFalse(matchStatement.isReturnDistinct());
    }

    @Test(timeout = 4000)
    public void getLimit_defaultDoesNotAffectReturnDistinct() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        matchStatement.getLimit();
        assertFalse(matchStatement.isReturnDistinct());
    }

    @Test(timeout = 4000)
    public void getUnwind_defaultDoesNotAffectReturnDistinct() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        matchStatement.getUnwind();
        assertFalse(matchStatement.isReturnDistinct());
    }

    @Test(timeout = 4000)
    public void getReturnNestedProjections_defaultState() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        matchStatement.getReturnNestedProjections();
        assertFalse(matchStatement.isReturnDistinct());
    }

    @Test(timeout = 4000)
    public void getReturnItems_defaultState() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        matchStatement.getReturnItems();
        assertFalse(matchStatement.isReturnDistinct());
    }

    @Test(timeout = 4000)
    public void isReturnDistinct_defaultIsFalse() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        boolean isDistinct = matchStatement.isReturnDistinct();
        assertFalse(isDistinct);
    }

    @Test(timeout = 4000)
    public void hashCode_afterSettingUnwind_onCopiedStatement() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement(-1159);
        OMatchStatement copied = matchStatement.copy();
        OUnwind unwind = new OUnwind(-1159);
        copied.unwind = unwind;
        copied.hashCode();
    }

    @Test(timeout = 4000)
    public void hashCode_afterSettingOrderBy() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement(-2068473975);
        OOrderBy orderBy = new OOrderBy();
        matchStatement.setOrderBy(orderBy);
        matchStatement.hashCode();
        assertFalse(matchStatement.isReturnDistinct());
    }

    @Test(timeout = 4000)
    public void hashCode_afterSettingGroupBy() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        OGroupBy groupBy = new OGroupBy(197);
        matchStatement.setGroupBy(groupBy);
        matchStatement.hashCode();
    }

    @Test(timeout = 4000)
    public void hashCode_withNullReturnNestedProjections() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        matchStatement.returnNestedProjections = null;
        matchStatement.hashCode();
        assertFalse(matchStatement.isReturnDistinct());
    }

    @Test(timeout = 4000)
    public void equals_withUnrelatedObject_returnsFalse() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        Object other = new Object();
        boolean equals = matchStatement.equals(other);
        assertFalse(matchStatement.isReturnDistinct());
        assertFalse(equals);
    }

    @Test(timeout = 4000)
    public void equals_withSameInstance_returnsTrue() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        boolean equals = matchStatement.equals(matchStatement);
        assertTrue(equals);
        assertFalse(matchStatement.isReturnDistinct());
    }

    @Test(timeout = 4000)
    public void equals_withNull_returnsFalse() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        boolean equals = matchStatement.equals(((Object) (null)));
        assertFalse(equals);
        assertFalse(matchStatement.isReturnDistinct());
    }

    @Test(timeout = 4000)
    public void copy_afterSettingSkip_preservesDistinctFlag() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        OSkip skip = new OSkip(-2041052067);
        matchStatement.setSkip(skip);
        OMatchStatement copied = matchStatement.copy();
        assertFalse(copied.isReturnDistinct());
    }

    @Test(timeout = 4000)
    public void copy_afterSettingOrderBy_preservesDistinctFlag() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement(-2068473975);
        OOrderBy orderBy = new OOrderBy();
        matchStatement.setOrderBy(orderBy);
        OMatchStatement copied = matchStatement.copy();
        assertFalse(copied.isReturnDistinct());
    }

    @Test(timeout = 4000)
    public void copy_afterSettingGroupBy_preservesDistinctFlag() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        OGroupBy groupBy = new OGroupBy(197);
        matchStatement.setGroupBy(groupBy);
        OMatchStatement copied = matchStatement.copy();
        assertFalse(copied.isReturnDistinct());
    }

    @Test(timeout = 4000)
    public void copy_withLargeReturnNestedProjections_preservesDistinctFlag() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        Stack<ONestedProjection> largeStack = new Stack<ONestedProjection>();
        largeStack.setSize(996);
        matchStatement.setReturnNestedProjections(largeStack);
        OMatchStatement copied = matchStatement.copy();
        assertFalse(copied.isReturnDistinct());
    }

    @Test(timeout = 4000)
    public void copy_withNullReturnNestedProjections_preservesDistinctFlag() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        matchStatement.returnNestedProjections = null;
        OMatchStatement copied = matchStatement.copy();
        assertFalse(copied.isReturnDistinct());
    }

    @Test(timeout = 4000)
    public void copy_afterAddingReturnAlias_equalsOriginal() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        matchStatement.addReturnAlias(((OIdentifier) (null)));
        OMatchStatement copied = matchStatement.copy();
        assertFalse(copied.isReturnDistinct());
        assertTrue(copied.equals(((Object) (matchStatement))));
    }

    @Test(timeout = 4000)
    public void copy_afterSettingReturnAliasesNull_equalsOriginal() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        matchStatement.setReturnAliases(((List<OIdentifier>) (null)));
        OMatchStatement copied = matchStatement.copy();
        assertFalse(copied.isReturnDistinct());
        assertTrue(copied.equals(((Object) (matchStatement))));
    }

    @Test(timeout = 4000)
    public void copy_afterAddingNullReturnItem_equalsOriginal() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        matchStatement.addReturnItem(((OExpression) (null)));
        OMatchStatement copied = matchStatement.copy();
        assertFalse(copied.isReturnDistinct());
        assertTrue(copied.equals(((Object) (matchStatement))));
    }

    @Test(timeout = 4000)
    public void copy_afterAddingReturnItem_equalsOriginal() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        OExpression expression = new OExpression(((OIdentifier) (null)));
        matchStatement.addReturnItem(expression);
        OMatchStatement copied = matchStatement.copy();
        assertFalse(copied.isReturnDistinct());
        assertTrue(copied.equals(((Object) (matchStatement))));
    }

    @Test(timeout = 4000)
    public void copy_afterSettingReturnItemsNull_equalsOriginal() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement(0);
        matchStatement.setReturnItems(((List<OExpression>) (null)));
        OMatchStatement copied = matchStatement.copy();
        assertTrue(copied.equals(((Object) (matchStatement))));
        assertFalse(copied.isReturnDistinct());
    }

    @Test(timeout = 4000)
    public void copy_afterAddingNotMatchExpression_equalsOriginal() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        OMatchExpression notMatchExpr = new OMatchExpression(-3607);
        matchStatement.addNotMatchExpression(notMatchExpr);
        OMatchStatement copied = matchStatement.copy();
        assertFalse(copied.isReturnDistinct());
        assertTrue(copied.equals(((Object) (matchStatement))));
    }

    @Test(timeout = 4000)
    public void copy_withNullNotMatchExpressions_equalsOriginal() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        matchStatement.notMatchExpressions = null;
        OMatchStatement copied = matchStatement.copy();
        assertTrue(copied.equals(((Object) (matchStatement))));
        assertFalse(copied.isReturnDistinct());
    }

    @Test(timeout = 4000)
    public void copy_afterAddingMatchExpressionWithoutOrigin_throwsNPE() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        OMatchExpression matchExpression = new OMatchExpression(-1452647753);
        matchStatement.addMatchExpression(matchExpression);
        try {
            matchStatement.copy();
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
    }

    @Test(timeout = 4000)
    public void copy_afterSettingMatchExpressionsNull_throwsNPE() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        matchStatement.setMatchExpressions(((List<OMatchExpression>) (null)));
        try {
            matchStatement.copy();
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
    }

    @Test(timeout = 4000)
    public void toGenericStatement_withTwoReturnItems_outputsParameterizedPlaceholders() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        OArrayConcatExpressionElement concatElement = new OArrayConcatExpressionElement(900);
        matchStatement.addReturnItem(concatElement);
        matchStatement.addReturnItem(concatElement);
        String generic = matchStatement.toGenericStatement();
        assertEquals("MATCH  RETURN ?, ?", generic);
    }

    @Test(timeout = 4000)
    public void toGenericStatement_afterSettingReturnDistinctTrue_setsFlag() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        assertFalse(matchStatement.isReturnDistinct());
        matchStatement.setReturnDistinct(true);
        matchStatement.toGenericStatement();
        assertTrue(matchStatement.isReturnDistinct());
    }

    @Test(timeout = 4000)
    public void toGenericStatement_withNullMatchExpression_throwsNPE() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        matchStatement.addMatchExpression(((OMatchExpression) (null)));
        try {
            matchStatement.toGenericStatement();
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
    }

    @Test(timeout = 4000)
    public void toGenericStatement_appendsToBuilder() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        StringBuilder builder = new StringBuilder("$matches");
        matchStatement.toGenericStatement(builder);
        assertEquals("$matchesMATCH  RETURN ", builder.toString());
    }

    @Test(timeout = 4000)
    public void toString_withBaseIdentifierExpression_andAlias() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        AggregateProjectionSplit splitter = new AggregateProjectionSplit();
        OIdentifier alias = splitter.getNextAlias();
        matchStatement.addReturnAlias(alias);
        OExpression expression = new OExpression(alias);
        matchStatement.addReturnItem(expression);
        String result = matchStatement.toString();
        assertEquals("MATCH  RETURN _$$$OALIAS$$_0 AS _$$$OALIAS$$_0", result);
    }

    @Test(timeout = 4000)
    public void toString_withNullNestedProjection_returnsNullLiteral() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        matchStatement.addReturnNestedProjection(((ONestedProjection) (null)));
        OArrayConcatExpressionElement concatElement = new OArrayConcatExpressionElement(1);
        matchStatement.addReturnItem(concatElement);
        String result = matchStatement.toString();
        assertEquals("MATCH  RETURN null", result);
    }

    @Test(timeout = 4000)
    public void toString_withNullReturnNestedProjectionsAndReturnItem_returnsNullLiteral() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        matchStatement.returnNestedProjections = null;
        OArrayConcatExpressionElement concatElement = new OArrayConcatExpressionElement(1400);
        matchStatement.addReturnItem(concatElement);
        String result = matchStatement.toString();
        assertEquals("MATCH  RETURN null", result);
    }

    @Test(timeout = 4000)
    public void toString_withTwoReturnItems_outputsTwoNulls() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        OArrayConcatExpressionElement concatElement = new OArrayConcatExpressionElement(404);
        matchStatement.addReturnItem(concatElement);
        matchStatement.addReturnItem(concatElement);
        String result = matchStatement.toString();
        assertEquals("MATCH  RETURN null, null", result);
    }

    @Test(timeout = 4000)
    public void toString_withNullMatchExpression_throwsNPE() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        matchStatement.addMatchExpression(((OMatchExpression) (null)));
        try {
            matchStatement.toString();
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
    }

    @Test(timeout = 4000)
    public void toString_withMapAndBuilder_appendsPrefix() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement(-2068473975);
        TreeMap<Object, Object> params = new TreeMap<Object, Object>();
        StringBuilder builder = new StringBuilder("EVKgf ?\"\";vS[x[");
        matchStatement.toString(((Map<Object, Object>) (params)), builder);
        assertEquals("EVKgf ?\"\";vS[x[MATCH  RETURN ", builder.toString());
    }

    @Test(timeout = 4000)
    public void returnsPaths_whenOnlyArrayConcatItemAdded_isFalse() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        OArrayConcatExpressionElement concatElement = new OArrayConcatExpressionElement(900);
        matchStatement.addReturnItem(concatElement);
        boolean result = matchStatement.returnsPaths();
        assertFalse(result);
        assertFalse(matchStatement.isReturnDistinct());
    }

    @Test(timeout = 4000)
    public void returnsPatterns_whenOnlyArrayConcatItemAdded_isFalse() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        OArrayConcatExpressionElement concatElement = new OArrayConcatExpressionElement(900);
        matchStatement.addReturnItem(concatElement);
        boolean result = matchStatement.returnsPatterns();
        assertFalse(result);
        assertFalse(matchStatement.isReturnDistinct());
    }

    @Test(timeout = 4000)
    public void returnsElements_whenOnlyArrayConcatItemAdded_isFalse() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        OArrayConcatExpressionElement concatElement = new OArrayConcatExpressionElement(31);
        matchStatement.addReturnItem(concatElement);
        boolean result = matchStatement.returnsElements();
        assertFalse(result);
        assertFalse(matchStatement.isReturnDistinct());
    }

    @Test(timeout = 4000)
    public void returnsPathElements_whenOnlyArrayConcatItemAdded_isFalse() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        OArrayConcatExpressionElement concatElement = new OArrayConcatExpressionElement(900);
        matchStatement.addReturnItem(concatElement);
        boolean result = matchStatement.returnsPathElements();
        assertFalse(matchStatement.isReturnDistinct());
        assertFalse(result);
    }

    @Test(timeout = 4000)
    public void matchContext_copy_returnsNewInstance() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        OMatchStatement.MatchContext context = matchStatement.new com.orientechnologies.orient.core.sql.parser.MatchContext();
        ORecordId rid = new ORecordId(1613, 1613);
        OMatchStatement.MatchContext copied = context.copy("@,l>", rid);
        assertFalse(matchStatement.isReturnDistinct());
        assertNotSame(copied, context);
    }

    @Test(timeout = 4000)
    public void matchContext_toDoc_throwsNoClassDefFoundError() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        OMatchStatement.MatchContext context = matchStatement.new com.orientechnologies.orient.core.sql.parser.MatchContext();
        try {
            context.toDoc();
            fail("Expecting exception: NoClassDefFoundError");
        } catch (NoClassDefFoundError e) {
            // expected
        }
    }

    @Test(timeout = 4000)
    public void copy_afterAddingNullNotMatchExpression_equalsOriginal() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        matchStatement.addNotMatchExpression(((OMatchExpression) (null)));
        OMatchStatement copied = matchStatement.copy();
        assertTrue(copied.equals(((Object) (matchStatement))));
        assertFalse(copied.isReturnDistinct());
    }

    @Test(timeout = 4000)
    public void toGenericStatement_withGroupBy_appendsGroupBy() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        OGroupBy groupBy = new OGroupBy(963);
        matchStatement.setGroupBy(groupBy);
        StringBuilder builder = new StringBuilder("$matches");
        matchStatement.toGenericStatement(builder);
        assertEquals("$matchesMATCH  RETURN  GROUP BY ", builder.toString());
    }

    @Test(timeout = 4000)
    public void toString_afterSettingReturnDistinctTrue_setsFlag() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        assertFalse(matchStatement.isReturnDistinct());
        matchStatement.setReturnDistinct(true);
        matchStatement.toString();
        assertTrue(matchStatement.isReturnDistinct());
    }

    @Test(timeout = 4000)
    public void copy_afterSettingUnwind_equalsOriginal() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        OUnwind unwind = new OUnwind(((OrientSql) (null)), -1556);
        matchStatement.setUnwind(unwind);
        OMatchStatement copied = matchStatement.copy();
        assertTrue(copied.equals(((Object) (matchStatement))));
        assertFalse(copied.isReturnDistinct());
    }

    @Test(timeout = 4000)
    public void isIdempotent_defaultIsTrue() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        boolean idempotent = matchStatement.isIdempotent();
        assertTrue(idempotent);
        assertFalse(matchStatement.isReturnDistinct());
    }

    @Test(timeout = 4000)
    public void toString_afterSettingSkip_containsSpaces() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        OSkip skip = new OSkip(-4162);
        matchStatement.setSkip(skip);
        String result = matchStatement.toString();
        assertEquals("MATCH  RETURN  ", result);
    }

    @Test(timeout = 4000)
    public void toString_withNullAliasAndBaseIdentifierExpression_isEmptyReturn() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        matchStatement.addReturnAlias(((OIdentifier) (null)));
        OExpression expression = new OExpression(((OIdentifier) (null)));
        matchStatement.addReturnItem(expression);
        String result = matchStatement.toString();
        assertEquals("MATCH  RETURN ", result);
    }

    @Test(timeout = 4000)
    public void toString_withNestedProjection_outputsNullObject() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        ONestedProjection nestedProjection = new ONestedProjection(130);
        matchStatement.addReturnNestedProjection(nestedProjection);
        OArrayConcatExpressionElement concatElement = new OArrayConcatExpressionElement(130);
        matchStatement.addReturnItem(concatElement);
        String result = matchStatement.toString();
        assertEquals("MATCH  RETURN null:{}", result);
    }

    @Test(timeout = 4000)
    public void refersToParent_defaultIsFalse() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        boolean refers = matchStatement.refersToParent();
        assertFalse(matchStatement.isReturnDistinct());
        assertFalse(refers);
    }

    @Test(timeout = 4000)
    public void constructor_withNullOrientSql_andInt_doesNotSetDistinct() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement(((OrientSql) (null)), 1);
        assertFalse(matchStatement.isReturnDistinct());
    }

    @Test(timeout = 4000)
    public void setNotMatchExpressions_withEmptyVector_doesNotSetDistinct() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        Vector<OMatchExpression> vector = new Vector<OMatchExpression>();
        matchStatement.setNotMatchExpressions(vector);
        assertFalse(matchStatement.isReturnDistinct());
    }

    @Test(timeout = 4000)
    public void toString_withOrderBy_appendsReturnAndSpaces() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement(-2068473975);
        OOrderBy orderBy = new OOrderBy();
        matchStatement.setOrderBy(orderBy);
        TreeMap<Object, Object> params = new TreeMap<Object, Object>();
        StringBuilder builder = new StringBuilder("EVKgf ?\"\";vS[x[");
        matchStatement.toString(((Map<Object, Object>) (params)), builder);
        assertEquals("EVKgf ?\"\";vS[x[MATCH  RETURN  ", builder.toString());
        assertFalse(matchStatement.isReturnDistinct());
    }

    @Test(timeout = 4000)
    public void setLimit_withNull_doesNotSetDistinct() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        matchStatement.setLimit(((OLimit) (null)));
        assertFalse(matchStatement.isReturnDistinct());
    }

    @Test(timeout = 4000)
    public void copy_afterAddingNullMatchExpression_throwsNullPointerException() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        matchStatement.addMatchExpression(((OMatchExpression) (null)));
        try {
            matchStatement.copy();
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
    }

    @Test(timeout = 4000)
    public void copy_withReturnNestedProjectionsList_equalsOriginal() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        ONestedProjection nestedProjection = new ONestedProjection(-1725222855);
        List<ONestedProjection> nestedProjections = new ArrayList<>();
        nestedProjections.add(nestedProjection);
        matchStatement.setReturnNestedProjections(nestedProjections);
        OMatchStatement copied = matchStatement.copy();
        assertFalse(copied.isReturnDistinct());
        assertTrue(copied.equals(((Object) (matchStatement))));
    }

    @Test(timeout = 4000)
    public void createExecutionPlan_withNullContext_throwsNPE() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        try {
            matchStatement.createExecutionPlan(((OCommandContext) (null)));
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
    }

    @Test(timeout = 4000)
    public void getNotMatchExpressions_defaultState_returnsEmptyOrNullConsistent() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement(-2068473975);
        matchStatement.getNotMatchExpressions();
        assertFalse(matchStatement.isReturnDistinct());
    }

    @Test(timeout = 4000)
    public void getMatchExpressions_defaultState_returnsEmptyOrNullConsistent() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement(-2068473975);
        matchStatement.getMatchExpressions();
        assertFalse(matchStatement.isReturnDistinct());
    }

    @Test(timeout = 4000)
    public void getGroupBy_defaultState_returnsNull() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement(-2068473975);
        matchStatement.getGroupBy();
        assertFalse(matchStatement.isReturnDistinct());
    }

    @Test(timeout = 4000)
    public void getReturnAliases_defaultState_thenSetSameList_doesNotSetDistinct() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement(-2068473975);
        List<OIdentifier> aliases = matchStatement.getReturnAliases();
        matchStatement.setReturnAliases(aliases);
        assertFalse(matchStatement.isReturnDistinct());
    }
}