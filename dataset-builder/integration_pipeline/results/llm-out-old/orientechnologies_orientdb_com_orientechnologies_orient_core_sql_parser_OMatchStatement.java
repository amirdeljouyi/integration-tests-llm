package com.orientechnologies.orient.core.sql.parser;

import static org.junit.Assert.*;

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
    checkRightSyntax(
        "MATCH {class: 'V', as: foo}.out().in('foo').both('bar').out(){as: bar} RETURN foo");

    checkRightSyntax("MATCH {class: 'V', as: foo}-->{}<-foo-{}-bar-{}-->{as: bar} RETURN foo");
  }

  @Test
  public void testLongPath2() {
    checkRightSyntax(
        "MATCH {class: 'V', as: foo}.out().in('foo'){}.both('bar'){CLASS: 'bar'}.out(){as: bar}"
            + " RETURN foo");
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
    checkRightSyntax(
        "MATCH {class: 'V', as: foo}-->{}<-foo-{}-bar-{}-->{as: bar, optional:true} RETURN foo");
    checkRightSyntax(
        "MATCH {class: 'V', as: foo}-->{}<-foo-{}-bar-{}-->{as: bar, optional:false} RETURN foo");
  }

  @Test
  public void testOrderBy() {
    checkRightSyntax("MATCH {class: 'V', as: foo}-->{} RETURN foo ORDER BY foo");
    checkRightSyntax("MATCH {class: 'V', as: foo}-->{} RETURN foo ORDER BY foo limit 10");
  }

  @Test
  public void testNestedProjections() {
    checkRightSyntax("MATCH {class: 'V', as: foo}-->{} RETURN foo:{name, surname}");
    checkRightSyntax(
        "MATCH {class: 'V', as: foo}-->{as:bar} RETURN foo:{name, surname} as bloo, bar:{*}");
  }

  @Test
  public void testUnwind() {
    checkRightSyntax(
        "MATCH {class: 'V', as: foo}-->{as:bar} RETURN foo.name, bar.name as x unwind x");
  }

  @Test
  public void testDepthAlias() {
    checkRightSyntax(
        "MATCH {class: 'V', as: foo}-->{as:bar, while:($depth < 2), depthAlias: depth} RETURN"
            + " depth");
  }

  @Test
  public void testPathAlias() {
    checkRightSyntax(
        "MATCH {class: 'V', as: foo}-->{as:bar, while:($depth < 2), pathAlias: barPath} RETURN"
            + " barPath");
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
    checkRightSyntax(
        "MATCH {class: 'V', as: foo}-->{as:bar} RETURN foo.name, bar.name skip 10 limit 10");
  }

  @Test
  public void testFieldTraversal() {
    checkRightSyntax(
        "MATCH {class: 'V', as: foo}.toBar{as:bar} RETURN foo.name, bar.name skip 10 limit 10");
    checkRightSyntax(
        "MATCH {class: 'V', as: foo}.toBar{as:bar}.out(){as:c} RETURN foo.name, bar.name skip 10"
            + " limit 10");
    checkRightSyntax(
        "MATCH {class: 'V', as: foo}.toBar.baz{as:bar} RETURN foo.name, bar.name skip 10 limit 10");
    checkRightSyntax(
        "MATCH {class: 'V', as: foo}.toBar.out(){as:bar} RETURN foo.name, bar.name skip 10 limit"
            + " 10");
  }

  protected OrientSql getParserFor(String string) {
    InputStream is = new ByteArrayInputStream(string.getBytes());
    OrientSql osql = new OrientSql(is);
    return osql;
  }

  // Adapted Improved Generated Tests

  @Test(timeout = 4000)
  public void buildPatternsDoesNotAffectReturnDistinct() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement(19);
    matchStatement.buildPatterns();
    assertFalse(matchStatement.isReturnDistinct());
  }

  @Test(timeout = 4000)
  public void getReturnNestedProjectionsReturnsNullWhenExplicitlySet() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement();
    matchStatement.setReturnNestedProjections(((List<ONestedProjection>) (null)));
    List<ONestedProjection> nestedProjections = matchStatement.getReturnNestedProjections();
    assertNull(nestedProjections);
    assertFalse(matchStatement.isReturnDistinct());
  }

  @Test(timeout = 4000)
  public void getReturnItemsReturnsNonEmptyListAfterAdd() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement();
    OArrayConcatExpressionElement arrayConcatElement = new OArrayConcatExpressionElement(900);
    matchStatement.addReturnItem(arrayConcatElement);
    List<OExpression> returnItems = matchStatement.getReturnItems();
    assertFalse(matchStatement.isReturnDistinct());
    assertEquals(1, returnItems.size());
  }

  @Test(timeout = 4000)
  public void getReturnAliasesReturnsNonEmptyListAfterAddNullAlias() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement();
    matchStatement.addReturnAlias(((OIdentifier) (null)));
    List<OIdentifier> returnAliases = matchStatement.getReturnAliases();
    assertFalse(returnAliases.isEmpty());
    assertFalse(matchStatement.isReturnDistinct());
  }

  @Test(timeout = 4000)
  public void getNotMatchExpressionsReturnsNullWhenFieldIsNull() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement();
    matchStatement.notMatchExpressions = null;
    List<OMatchExpression> notMatchExpressions = matchStatement.getNotMatchExpressions();
    assertFalse(matchStatement.isReturnDistinct());
    assertNull(notMatchExpressions);
  }

  @Test(timeout = 4000)
  public void getNotMatchExpressionsReturnsNonEmptyListAfterAddNull() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement();
    matchStatement.addNotMatchExpression(((OMatchExpression) (null)));
    List<OMatchExpression> notMatchExpressions = matchStatement.getNotMatchExpressions();
    assertFalse(matchStatement.isReturnDistinct());
    assertFalse(notMatchExpressions.isEmpty());
  }

  @Test(timeout = 4000)
  public void getMatchExpressionsReturnsListContainingAddedExpression() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement();
    OMatchExpression matchExpression = new OMatchExpression(1296);
    Stack<OMatchExpression> matchExpressionStack = new Stack<OMatchExpression>();
    matchExpressionStack.push(matchExpression);
    matchStatement.setMatchExpressions(matchExpressionStack);
    List<OMatchExpression> matchExpressions = matchStatement.getMatchExpressions();
    assertFalse(matchStatement.isReturnDistinct());
    assertTrue(matchExpressions.contains(matchExpression));
  }

  @Test(timeout = 4000)
  public void getGroupByReturnsAssignedInstance() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement();
    OGroupBy groupBy = new OGroupBy(0);
    matchStatement.setGroupBy(groupBy);
    OGroupBy retrievedGroupBy = matchStatement.getGroupBy();
    assertFalse(matchStatement.isReturnDistinct());
    assertNotNull(retrievedGroupBy);
  }

  @Test(timeout = 4000)
  public void copyCreatesDistinctInstanceWhenReturnDistinctTrue() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement(-1210);
    matchStatement.returnDistinct = true;
    OMatchStatement copiedMatchStatement = matchStatement.copy();
    assertNotSame(copiedMatchStatement, matchStatement);
  }

  @Test(timeout = 4000)
  public void getSkipDoesNotChangeReturnDistinct() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement();
    matchStatement.getSkip();
    assertFalse(matchStatement.isReturnDistinct());
  }

  @Test(timeout = 4000)
  public void getOrderByDoesNotChangeReturnDistinct() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement();
    matchStatement.getOrderBy();
    assertFalse(matchStatement.isReturnDistinct());
  }

  @Test(timeout = 4000)
  public void getLimitDoesNotChangeReturnDistinct() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement();
    matchStatement.getLimit();
    assertFalse(matchStatement.isReturnDistinct());
  }

  @Test(timeout = 4000)
  public void getUnwindDoesNotChangeReturnDistinct() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement();
    matchStatement.getUnwind();
    assertFalse(matchStatement.isReturnDistinct());
  }

  @Test(timeout = 4000)
  public void getReturnNestedProjectionsDefaultIsEmpty() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement();
    matchStatement.getReturnNestedProjections();
    assertFalse(matchStatement.isReturnDistinct());
  }

  @Test(timeout = 4000)
  public void getReturnItemsDefaultIsEmpty() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement();
    matchStatement.getReturnItems();
    assertFalse(matchStatement.isReturnDistinct());
  }

  @Test(timeout = 4000)
  public void isReturnDistinctDefaultsToFalse() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement();
    boolean isDistinct = matchStatement.isReturnDistinct();
    assertFalse(isDistinct);
  }

  @Test(timeout = 4000)
  public void hashCodeAfterCopyAndSetUnwind() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement(-1159);
    OMatchStatement copiedMatchStatement = matchStatement.copy();
    OUnwind unwind = new OUnwind(-1159);
    copiedMatchStatement.unwind = unwind;
    copiedMatchStatement.hashCode();
  }

  @Test(timeout = 4000)
  public void hashCodeWithOrderBySet() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement(-2068473975);
    OOrderBy orderBy = new OOrderBy();
    matchStatement.setOrderBy(orderBy);
    matchStatement.hashCode();
    assertFalse(matchStatement.isReturnDistinct());
  }

  @Test(timeout = 4000)
  public void hashCodeWithGroupBySet() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement();
    OGroupBy groupBy = new OGroupBy(197);
    matchStatement.setGroupBy(groupBy);
    matchStatement.hashCode();
  }

  @Test(timeout = 4000)
  public void hashCodeWithNullReturnNestedProjectionsField() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement();
    matchStatement.returnNestedProjections = null;
    matchStatement.hashCode();
    assertFalse(matchStatement.isReturnDistinct());
  }

  @Test(timeout = 4000)
  public void equalsReturnsFalseForDifferentType() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement();
    Object otherObject = new Object();
    boolean equalsResult = matchStatement.equals(otherObject);
    assertFalse(matchStatement.isReturnDistinct());
    assertFalse(equalsResult);
  }

  @Test(timeout = 4000)
  public void equalsIsReflexive() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement();
    boolean equalsResult = matchStatement.equals(matchStatement);
    assertTrue(equalsResult);
    assertFalse(matchStatement.isReturnDistinct());
  }

  @Test(timeout = 4000)
  public void equalsReturnsFalseForNull() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement();
    boolean equalsResult = matchStatement.equals(((Object) (null)));
    assertFalse(equalsResult);
    assertFalse(matchStatement.isReturnDistinct());
  }

  @Test(timeout = 4000)
  public void copyAfterSettingSkip() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement();
    OSkip skip = new OSkip(-2041052067);
    matchStatement.setSkip(skip);
    OMatchStatement copiedMatchStatement = matchStatement.copy();
    assertFalse(copiedMatchStatement.isReturnDistinct());
  }

  @Test(timeout = 4000)
  public void copyAfterSettingOrderBy() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement(-2068473975);
    OOrderBy orderBy = new OOrderBy();
    matchStatement.setOrderBy(orderBy);
    OMatchStatement copiedMatchStatement = matchStatement.copy();
    assertFalse(copiedMatchStatement.isReturnDistinct());
  }

  @Test(timeout = 4000)
  public void copyAfterSettingGroupBy() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement();
    OGroupBy groupBy = new OGroupBy(197);
    matchStatement.setGroupBy(groupBy);
    OMatchStatement copiedMatchStatement = matchStatement.copy();
    assertFalse(copiedMatchStatement.isReturnDistinct());
  }

  @Test(timeout = 4000)
  public void copyAfterSettingLargeReturnNestedProjectionsStack() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement();
    Stack<ONestedProjection> nestedProjections = new Stack<ONestedProjection>();
    nestedProjections.setSize(996);
    matchStatement.setReturnNestedProjections(nestedProjections);
    OMatchStatement copiedMatchStatement = matchStatement.copy();
    assertFalse(copiedMatchStatement.isReturnDistinct());
  }

  @Test(timeout = 4000)
  public void copyWithNullReturnNestedProjectionsField() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement();
    matchStatement.returnNestedProjections = null;
    OMatchStatement copiedMatchStatement = matchStatement.copy();
    assertFalse(copiedMatchStatement.isReturnDistinct());
  }

  @Test(timeout = 4000)
  public void copyAfterAddingNullReturnAlias() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement();
    matchStatement.addReturnAlias(((OIdentifier) (null)));
    OMatchStatement copiedMatchStatement = matchStatement.copy();
    assertFalse(copiedMatchStatement.isReturnDistinct());
    assertTrue(copiedMatchStatement.equals(((Object) (matchStatement))));
  }

  @Test(timeout = 4000)
  public void copyAfterSettingReturnAliasesToNull() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement();
    matchStatement.setReturnAliases(((List<OIdentifier>) (null)));
    OMatchStatement copiedMatchStatement = matchStatement.copy();
    assertFalse(copiedMatchStatement.isReturnDistinct());
    assertTrue(copiedMatchStatement.equals(((Object) (matchStatement))));
  }

  @Test(timeout = 4000)
  public void copyAfterAddingNullReturnItem() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement();
    matchStatement.addReturnItem(((OExpression) (null)));
    OMatchStatement copiedMatchStatement = matchStatement.copy();
    assertFalse(copiedMatchStatement.isReturnDistinct());
    assertTrue(copiedMatchStatement.equals(((Object) (matchStatement))));
  }

  @Test(timeout = 4000)
  public void copyAfterAddingConcreteReturnItem() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement();
    OExpression expression = new OExpression(((OIdentifier) (null)));
    matchStatement.addReturnItem(expression);
    OMatchStatement copiedMatchStatement = matchStatement.copy();
    assertFalse(copiedMatchStatement.isReturnDistinct());
    assertTrue(copiedMatchStatement.equals(((Object) (matchStatement))));
  }

  @Test(timeout = 4000)
  public void copyAfterSettingReturnItemsToNull() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement(0);
    matchStatement.setReturnItems(((List<OExpression>) (null)));
    OMatchStatement copiedMatchStatement = matchStatement.copy();
    assertTrue(copiedMatchStatement.equals(((Object) (matchStatement))));
    assertFalse(copiedMatchStatement.isReturnDistinct());
  }

  @Test(timeout = 4000)
  public void copyAfterAddingMatchExpressionToNotMatch() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement();
    OMatchExpression matchExpression = new OMatchExpression(-3607);
    matchStatement.addNotMatchExpression(matchExpression);
    OMatchStatement copiedMatchStatement = matchStatement.copy();
    assertFalse(copiedMatchStatement.isReturnDistinct());
    assertTrue(copiedMatchStatement.equals(((Object) (matchStatement))));
  }

  @Test(timeout = 4000)
  public void copyWithNullNotMatchExpressionsField() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement();
    matchStatement.notMatchExpressions = null;
    OMatchStatement copiedMatchStatement = matchStatement.copy();
    assertTrue(copiedMatchStatement.equals(((Object) (matchStatement))));
    assertFalse(copiedMatchStatement.isReturnDistinct());
  }

  @Test(timeout = 4000)
  public void copyThrowsNPEWhenMatchExpressionOriginNull() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement();
    OMatchExpression matchExpression = new OMatchExpression(-1452647753);
    matchStatement.addMatchExpression(matchExpression);
    try {
      matchStatement.copy();
      fail("Expecting exception: NullPointerException");
    } catch (NullPointerException e) {
      // Cannot invoke "com.orientechnologies.orient.core.sql.parser.OMatchFilter.getAlias()"
      // because "expression.origin" is null
      assertTrue(e instanceof NullPointerException);
    }
  }

  @Test(timeout = 4000)
  public void copyThrowsNPEWhenMatchExpressionsIsNull() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement();
    matchStatement.setMatchExpressions(((List<OMatchExpression>) (null)));
    try {
      matchStatement.copy();
      fail("Expecting exception: NullPointerException");
    } catch (NullPointerException e) {
      // Cannot invoke "java.util.List.iterator()"
      assertTrue(e instanceof NullPointerException);
    }
  }

  @Test(timeout = 4000)
  public void toGenericStatementWithTwoReturnItems() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement();
    OArrayConcatExpressionElement arrayConcatElement = new OArrayConcatExpressionElement(900);
    matchStatement.addReturnItem(arrayConcatElement);
    matchStatement.addReturnItem(arrayConcatElement);
    String generic = matchStatement.toGenericStatement();
    assertEquals("MATCH  RETURN ?, ?", generic);
  }

  @Test(timeout = 4000)
  public void toGenericStatementAfterSettingReturnDistinctTrue() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement();
    assertFalse(matchStatement.isReturnDistinct());
    matchStatement.setReturnDistinct(true);
    matchStatement.toGenericStatement();
    assertTrue(matchStatement.isReturnDistinct());
  }

  @Test(timeout = 4000)
  public void toGenericStatementThrowsNPEWhenNullMatchExpressionAdded() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement();
    matchStatement.addMatchExpression(((OMatchExpression) (null)));
    try {
      matchStatement.toGenericStatement();
      fail("Expecting exception: NullPointerException");
    } catch (NullPointerException e) {
      // Cannot invoke "com.orientechnologies.orient.core.sql.parser.OMatchExpression.toGenericStatement(StringBuilder)"
      // because "expr" is null
      assertTrue(e instanceof NullPointerException);
    }
  }

  @Test(timeout = 4000)
  public void toGenericStatementAppendsToStringBuilder() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement();
    StringBuilder sb = new StringBuilder("$matches");
    matchStatement.toGenericStatement(sb);
    assertEquals("$matchesMATCH  RETURN ", sb.toString());
  }

  @Test(timeout = 4000)
  public void toStringWithReturnAliasAndExpressionIdentifier() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement();
    AggregateProjectionSplit split = new AggregateProjectionSplit();
    OIdentifier identifier = split.getNextAlias();
    matchStatement.addReturnAlias(identifier);
    OExpression expression = new OExpression(identifier);
    matchStatement.addReturnItem(expression);
    String resultString = matchStatement.toString();
    assertEquals("MATCH  RETURN _$$$OALIAS$$_0 AS _$$$OALIAS$$_0", resultString);
  }

  @Test(timeout = 4000)
  public void toStringWithNullNestedProjectionAndReturnItem() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement();
    matchStatement.addReturnNestedProjection(((ONestedProjection) (null)));
    OArrayConcatExpressionElement arrayConcatElement = new OArrayConcatExpressionElement(1);
    matchStatement.addReturnItem(arrayConcatElement);
    String resultString = matchStatement.toString();
    assertEquals("MATCH  RETURN null", resultString);
  }

  @Test(timeout = 4000)
  public void toStringWithNullReturnNestedProjectionsFieldAndReturnItem() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement();
    matchStatement.returnNestedProjections = null;
    OArrayConcatExpressionElement arrayConcatElement = new OArrayConcatExpressionElement(1400);
    matchStatement.addReturnItem(arrayConcatElement);
    String resultString = matchStatement.toString();
    assertEquals("MATCH  RETURN null", resultString);
  }

  @Test(timeout = 4000)
  public void toStringWithDuplicateReturnItems() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement();
    OArrayConcatExpressionElement arrayConcatElement = new OArrayConcatExpressionElement(404);
    matchStatement.addReturnItem(arrayConcatElement);
    matchStatement.addReturnItem(arrayConcatElement);
    String resultString = matchStatement.toString();
    assertEquals("MATCH  RETURN null, null", resultString);
  }

  @Test(timeout = 4000)
  public void toStringThrowsNPEWhenNullMatchExpressionAdded() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement();
    matchStatement.addMatchExpression(((OMatchExpression) (null)));
    try {
      matchStatement.toString();
      fail("Expecting exception: NullPointerException");
    } catch (NullPointerException e) {
      // Cannot invoke "com.orientechnologies.orient.core.sql.parser.OMatchExpression.toString(java.util.Map, StringBuilder)"
      // because "expr" is null
      assertTrue(e instanceof NullPointerException);
    }
  }

  @Test(timeout = 4000)
  public void toStringAppendsToStringBuilderWithMap() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement(-2068473975);
    TreeMap<Object, Object> map = new TreeMap<Object, Object>();
    StringBuilder sb = new StringBuilder("EVKgf ?\"\";vS[x[");
    matchStatement.toString(((Map<Object, Object>) (map)), sb);
    assertEquals("EVKgf ?\"\";vS[x[MATCH  RETURN ", sb.toString());
  }

  @Test(timeout = 4000)
  public void returnsPathsIsFalseWithArrayConcatItem() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement();
    OArrayConcatExpressionElement arrayConcatElement = new OArrayConcatExpressionElement(900);
    matchStatement.addReturnItem(arrayConcatElement);
    boolean result = matchStatement.returnsPaths();
    assertFalse(result);
    assertFalse(matchStatement.isReturnDistinct());
  }

  @Test(timeout = 4000)
  public void returnsPatternsIsFalseWithArrayConcatItem() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement();
    OArrayConcatExpressionElement arrayConcatElement = new OArrayConcatExpressionElement(900);
    matchStatement.addReturnItem(arrayConcatElement);
    boolean result = matchStatement.returnsPatterns();
    assertFalse(result);
    assertFalse(matchStatement.isReturnDistinct());
  }

  @Test(timeout = 4000)
  public void returnsElementsIsFalseWithArrayConcatItem() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement();
    OArrayConcatExpressionElement arrayConcatElement = new OArrayConcatExpressionElement(31);
    matchStatement.addReturnItem(arrayConcatElement);
    boolean result = matchStatement.returnsElements();
    assertFalse(result);
    assertFalse(matchStatement.isReturnDistinct());
  }

  @Test(timeout = 4000)
  public void returnsPathElementsIsFalseWithArrayConcatItem() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement();
    OArrayConcatExpressionElement arrayConcatElement = new OArrayConcatExpressionElement(900);
    matchStatement.addReturnItem(arrayConcatElement);
    boolean result = matchStatement.returnsPathElements();
    assertFalse(matchStatement.isReturnDistinct());
    assertFalse(result);
  }

  @Test(timeout = 4000)
  public void matchContextCopyCreatesNewInstance() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement();
    OMatchStatement.MatchContext matchContext = matchStatement.new MatchContext();
    ORecordId recordId = new ORecordId(1613, 1613);
    OMatchStatement.MatchContext copiedMatchContext = matchContext.copy("@,l>", recordId);
    assertFalse(matchStatement.isReturnDistinct());
    assertNotSame(copiedMatchContext, matchContext);
  }

  @Test(timeout = 4000)
  public void matchContextToDocThrowsNoClassDefFoundError() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement();
    OMatchStatement.MatchContext matchContext = matchStatement.new MatchContext();
    try {
      matchContext.toDoc();
      fail("Expecting exception: NoClassDefFoundError");
    } catch (NoClassDefFoundError e) {
      // Could not initialize class
      // com.orientechnologies.orient.core.serialization.serializer.record.ORecordSerializerFactory
      assertTrue(e instanceof NoClassDefFoundError);
    }
  }

  @Test(timeout = 4000)
  public void copyAfterAddingNullNotMatchExpression() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement();
    matchStatement.addNotMatchExpression(((OMatchExpression) (null)));
    OMatchStatement copiedMatchStatement = matchStatement.copy();
    assertTrue(copiedMatchStatement.equals(((Object) (matchStatement))));
    assertFalse(copiedMatchStatement.isReturnDistinct());
  }

  @Test(timeout = 4000)
  public void toGenericStatementIncludesGroupBySection() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement();
    OGroupBy groupBy = new OGroupBy(963);
    matchStatement.setGroupBy(groupBy);
    StringBuilder sb = new StringBuilder("$matches");
    matchStatement.toGenericStatement(sb);
    assertEquals("$matchesMATCH  RETURN  GROUP BY ", sb.toString());
  }

  @Test(timeout = 4000)
  public void toStringAfterSettingReturnDistinctTrue() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement();
    assertFalse(matchStatement.isReturnDistinct());
    matchStatement.setReturnDistinct(true);
    matchStatement.toString();
    assertTrue(matchStatement.isReturnDistinct());
  }

  @Test(timeout = 4000)
  public void copyAfterSettingUnwind() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement();
    OUnwind unwind = new OUnwind(((OrientSql) (null)), -1556);
    matchStatement.setUnwind(unwind);
    OMatchStatement copiedMatchStatement = matchStatement.copy();
    assertTrue(copiedMatchStatement.equals(((Object) (matchStatement))));
    assertFalse(copiedMatchStatement.isReturnDistinct());
  }

  @Test(timeout = 4000)
  public void isIdempotentReturnsTrue() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement();
    boolean idempotent = matchStatement.isIdempotent();
    assertTrue(idempotent);
    assertFalse(matchStatement.isReturnDistinct());
  }

  @Test(timeout = 4000)
  public void toStringIncludesTrailingSpaceWithSkip() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement();
    OSkip skip = new OSkip(-4162);
    matchStatement.setSkip(skip);
    String resultString = matchStatement.toString();
    assertEquals("MATCH  RETURN  ", resultString);
  }

  @Test(timeout = 4000)
  public void toStringWithNullAliasAndExpressionIdentifier() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement();
    matchStatement.addReturnAlias(((OIdentifier) (null)));
    OExpression expression = new OExpression(((OIdentifier) (null)));
    matchStatement.addReturnItem(expression);
    String resultString = matchStatement.toString();
    assertEquals("MATCH  RETURN ", resultString);
  }

  @Test(timeout = 4000)
  public void toStringWithNestedProjectionAndArrayConcatItem() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement();
    ONestedProjection nestedProjection = new ONestedProjection(130);
    matchStatement.addReturnNestedProjection(nestedProjection);
    OArrayConcatExpressionElement arrayConcatElement = new OArrayConcatExpressionElement(130);
    matchStatement.addReturnItem(arrayConcatElement);
    String resultString = matchStatement.toString();
    assertEquals("MATCH  RETURN null:{}", resultString);
  }

  @Test(timeout = 4000)
  public void refersToParentReturnsFalse() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement();
    boolean refersToParent = matchStatement.refersToParent();
    assertFalse(matchStatement.isReturnDistinct());
    assertFalse(refersToParent);
  }

  @Test(timeout = 4000)
  public void constructorWithOrientSqlAndIntDoesNotSetReturnDistinct() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement(((OrientSql) (null)), 1);
    assertFalse(matchStatement.isReturnDistinct());
  }

  @Test(timeout = 4000)
  public void setNotMatchExpressionsWithEmptyVector() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement();
    Vector<OMatchExpression> vector = new Vector<OMatchExpression>();
    matchStatement.setNotMatchExpressions(vector);
    assertFalse(matchStatement.isReturnDistinct());
  }

  @Test(timeout = 4000)
  public void toStringWithOrderByAppendsSpace() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement(-2068473975);
    OOrderBy orderBy = new OOrderBy();
    matchStatement.setOrderBy(orderBy);
    TreeMap<Object, Object> map = new TreeMap<Object, Object>();
    StringBuilder sb = new StringBuilder("EVKgf ?\"\";vS[x[");
    matchStatement.toString(((Map<Object, Object>) (map)), sb);
    assertEquals("EVKgf ?\"\";vS[x[MATCH  RETURN  ", sb.toString());
    assertFalse(matchStatement.isReturnDistinct());
  }

  @Test(timeout = 4000)
  public void setLimitWithNullDoesNotChangeReturnDistinct() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement();
    matchStatement.setLimit(((OLimit) (null)));
    assertFalse(matchStatement.isReturnDistinct());
  }

  @Test(timeout = 4000)
  public void copyThrowsNPEWithNullMatchExpression() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement();
    matchStatement.addMatchExpression(((OMatchExpression) (null)));
    try {
      matchStatement.copy();
      fail("Expecting exception: NullPointerException");
    } catch (NullPointerException e) {
      // Cannot read field "origin" because "expression" is null
      assertTrue(e instanceof NullPointerException);
    }
  }

  @Test(timeout = 4000)
  public void copyWithListReturnNestedProjections() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement();
    ONestedProjection nestedProjection = new ONestedProjection(-1725222855);
    List<ONestedProjection> nestedProjections = List.of(nestedProjection);
    matchStatement.setReturnNestedProjections(nestedProjections);
    OMatchStatement copiedMatchStatement = matchStatement.copy();
    assertFalse(copiedMatchStatement.isReturnDistinct());
    assertTrue(copiedMatchStatement.equals(((Object) (matchStatement))));
  }

  @Test(timeout = 4000)
  public void createExecutionPlanThrowsNPEWhenContextNull() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement();
    try {
      matchStatement.createExecutionPlan(((OCommandContext) (null)));
      fail("Expecting exception: NullPointerException");
    } catch (NullPointerException e) {
      // Cannot invoke "com.orientechnologies.orient.core.command.OCommandContext.getDatabase()"
      // because "ctx" is null
      assertTrue(e instanceof NullPointerException);
    }
  }

  @Test(timeout = 4000)
  public void getNotMatchExpressionsDefaultIsEmpty() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement(-2068473975);
    matchStatement.getNotMatchExpressions();
    assertFalse(matchStatement.isReturnDistinct());
  }

  @Test(timeout = 4000)
  public void getMatchExpressionsDefaultIsEmpty() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement(-2068473975);
    matchStatement.getMatchExpressions();
    assertFalse(matchStatement.isReturnDistinct());
  }

  @Test(timeout = 4000)
  public void getGroupByDefaultIsNull() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement(-2068473975);
    matchStatement.getGroupBy();
    assertFalse(matchStatement.isReturnDistinct());
  }

  @Test(timeout = 4000)
  public void getAndSetReturnAliasesWithEmptyList() throws Throwable {
    OMatchStatement matchStatement = new OMatchStatement(-2068473975);
    List<OIdentifier> returnAliases = matchStatement.getReturnAliases();
    matchStatement.setReturnAliases(returnAliases);
    assertFalse(matchStatement.isReturnDistinct());
  }
}