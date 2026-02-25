package com.orientechnologies.orient.core.sql.parser;
import com.orientechnologies.orient.core.command.OCommandContext;
import com.orientechnologies.orient.core.id.ORecordId;
public class OMatchStatement_ESTest_Adopted {
    @org.junit.Test(timeout = 4000)
    public void buildPatterns_doesNotAffectDistinctFlag() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement(19);
        matchStatement.buildPatterns();
        org.junit.Assert.assertFalse(matchStatement.isReturnDistinct());
    }

    @org.junit.Test(timeout = 4000)
    public void getReturnNestedProjections_returnsNullWhenUnset() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement();
        matchStatement.setReturnNestedProjections(((java.util.List<com.orientechnologies.orient.core.sql.parser.ONestedProjection>) (null)));
        java.util.List<com.orientechnologies.orient.core.sql.parser.ONestedProjection> nestedProjections = matchStatement.getReturnNestedProjections();
        org.junit.Assert.assertNull(nestedProjections);
        org.junit.Assert.assertFalse(matchStatement.isReturnDistinct());
    }

    @org.junit.Test(timeout = 4000)
    public void getReturnItems_returnsNonEmptyAfterAdd() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement();
        com.orientechnologies.orient.core.sql.parser.OArrayConcatExpressionElement arrayConcatElement = new com.orientechnologies.orient.core.sql.parser.OArrayConcatExpressionElement(900);
        matchStatement.addReturnItem(arrayConcatElement);
        java.util.List<com.orientechnologies.orient.core.sql.parser.OExpression> returnItems = matchStatement.getReturnItems();
        org.junit.Assert.assertFalse(matchStatement.isReturnDistinct());
        org.junit.Assert.assertEquals(1, returnItems.size());
    }

    @org.junit.Test(timeout = 4000)
    public void getReturnAliases_returnsNonEmptyAfterAdd() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement();
        matchStatement.addReturnAlias(((com.orientechnologies.orient.core.sql.parser.OIdentifier) (null)));
        java.util.List<com.orientechnologies.orient.core.sql.parser.OIdentifier> returnAliases = matchStatement.getReturnAliases();
        org.junit.Assert.assertFalse(returnAliases.isEmpty());
        org.junit.Assert.assertFalse(matchStatement.isReturnDistinct());
    }

    @org.junit.Test(timeout = 4000)
    public void getNotMatchExpressions_returnsNullWhenFieldIsNull() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement();
        matchStatement.notMatchExpressions = null;
        java.util.List<com.orientechnologies.orient.core.sql.parser.OMatchExpression> notMatchExpressions = matchStatement.getNotMatchExpressions();
        org.junit.Assert.assertFalse(matchStatement.isReturnDistinct());
        org.junit.Assert.assertNull(notMatchExpressions);
    }

    @org.junit.Test(timeout = 4000)
    public void getNotMatchExpressions_returnsNonEmptyAfterAdd() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement();
        matchStatement.addNotMatchExpression(((com.orientechnologies.orient.core.sql.parser.OMatchExpression) (null)));
        java.util.List<com.orientechnologies.orient.core.sql.parser.OMatchExpression> notMatchExpressions = matchStatement.getNotMatchExpressions();
        org.junit.Assert.assertFalse(matchStatement.isReturnDistinct());
        org.junit.Assert.assertFalse(notMatchExpressions.isEmpty());
    }

    @org.junit.Test(timeout = 4000)
    public void getMatchExpressions_containsAddedExpression() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement();
        com.orientechnologies.orient.core.sql.parser.OMatchExpression matchExpression = new com.orientechnologies.orient.core.sql.parser.OMatchExpression(1296);
        java.util.Stack<com.orientechnologies.orient.core.sql.parser.OMatchExpression> matchExpressionStack = new java.util.Stack<com.orientechnologies.orient.core.sql.parser.OMatchExpression>();
        matchExpressionStack.push(matchExpression);
        matchStatement.setMatchExpressions(matchExpressionStack);
        java.util.List<com.orientechnologies.orient.core.sql.parser.OMatchExpression> matchExpressions = matchStatement.getMatchExpressions();
        org.junit.Assert.assertFalse(matchStatement.isReturnDistinct());
        org.junit.Assert.assertTrue(matchExpressions.contains(matchExpression));
    }

    @org.junit.Test(timeout = 4000)
    public void getGroupBy_returnsSetValue() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement();
        com.orientechnologies.orient.core.sql.parser.OGroupBy groupBy = new com.orientechnologies.orient.core.sql.parser.OGroupBy(0);
        matchStatement.setGroupBy(groupBy);
        com.orientechnologies.orient.core.sql.parser.OGroupBy returnedGroupBy = matchStatement.getGroupBy();
        org.junit.Assert.assertFalse(matchStatement.isReturnDistinct());
        org.junit.Assert.assertNotNull(returnedGroupBy);
    }

    @org.junit.Test(timeout = 4000)
    public void copy_createsNewInstance_whenDistinctTrue() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement(-1210);
        matchStatement.returnDistinct = true;
        com.orientechnologies.orient.core.sql.parser.OMatchStatement copiedStatement = matchStatement.copy();
        org.junit.Assert.assertNotSame(copiedStatement, matchStatement);
    }

    @org.junit.Test(timeout = 4000)
    public void getSkip_default() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement();
        matchStatement.getSkip();
        org.junit.Assert.assertFalse(matchStatement.isReturnDistinct());
    }

    @org.junit.Test(timeout = 4000)
    public void getOrderBy_default() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement();
        matchStatement.getOrderBy();
        org.junit.Assert.assertFalse(matchStatement.isReturnDistinct());
    }

    @org.junit.Test(timeout = 4000)
    public void getLimit_default() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement();
        matchStatement.getLimit();
        org.junit.Assert.assertFalse(matchStatement.isReturnDistinct());
    }

    @org.junit.Test(timeout = 4000)
    public void getUnwind_default() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement();
        matchStatement.getUnwind();
        org.junit.Assert.assertFalse(matchStatement.isReturnDistinct());
    }

    @org.junit.Test(timeout = 4000)
    public void getReturnNestedProjections_returnsEmptyByDefault() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement();
        matchStatement.getReturnNestedProjections();
        org.junit.Assert.assertFalse(matchStatement.isReturnDistinct());
    }

    @org.junit.Test(timeout = 4000)
    public void getReturnItems_returnsEmptyByDefault() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement();
        matchStatement.getReturnItems();
        org.junit.Assert.assertFalse(matchStatement.isReturnDistinct());
    }

    @org.junit.Test(timeout = 4000)
    public void isReturnDistinct_defaultsToFalse() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement();
        boolean distinct = matchStatement.isReturnDistinct();
        org.junit.Assert.assertFalse(distinct);
    }

    @org.junit.Test(timeout = 4000)
    public void hashCode_withUnwindAfterCopy() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement(-1159);
        com.orientechnologies.orient.core.sql.parser.OMatchStatement copiedStatement = matchStatement.copy();
        com.orientechnologies.orient.core.sql.parser.OUnwind unwind = new com.orientechnologies.orient.core.sql.parser.OUnwind(-1159);
        copiedStatement.unwind = unwind;
        copiedStatement.hashCode();
    }

    @org.junit.Test(timeout = 4000)
    public void hashCode_afterSettingOrderBy() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement(-2068473975);
        com.orientechnologies.orient.core.sql.parser.OOrderBy orderBy = new com.orientechnologies.orient.core.sql.parser.OOrderBy();
        matchStatement.setOrderBy(orderBy);
        matchStatement.hashCode();
        org.junit.Assert.assertFalse(matchStatement.isReturnDistinct());
    }

    @org.junit.Test(timeout = 4000)
    public void hashCode_afterSettingGroupBy() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement();
        com.orientechnologies.orient.core.sql.parser.OGroupBy groupBy = new com.orientechnologies.orient.core.sql.parser.OGroupBy(197);
        matchStatement.setGroupBy(groupBy);
        matchStatement.hashCode();
    }

    @org.junit.Test(timeout = 4000)
    public void hashCode_handlesNullReturnNestedProjections() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement();
        matchStatement.returnNestedProjections = null;
        matchStatement.hashCode();
        org.junit.Assert.assertFalse(matchStatement.isReturnDistinct());
    }

    @org.junit.Test(timeout = 4000)
    public void equals_withUnrelatedObject_returnsFalse() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement();
        java.lang.Object unrelated = new java.lang.Object();
        boolean equalsResult = matchStatement.equals(unrelated);
        org.junit.Assert.assertFalse(matchStatement.isReturnDistinct());
        org.junit.Assert.assertFalse(equalsResult);
    }

    @org.junit.Test(timeout = 4000)
    public void equals_withSelf_returnsTrue() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement();
        boolean equalsResult = matchStatement.equals(matchStatement);
        org.junit.Assert.assertTrue(equalsResult);
        org.junit.Assert.assertFalse(matchStatement.isReturnDistinct());
    }

    @org.junit.Test(timeout = 4000)
    public void equals_withNull_returnsFalse() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement();
        boolean equalsResult = matchStatement.equals(((java.lang.Object) (null)));
        org.junit.Assert.assertFalse(equalsResult);
        org.junit.Assert.assertFalse(matchStatement.isReturnDistinct());
    }

    @org.junit.Test(timeout = 4000)
    public void copy_preservesSkip() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement();
        com.orientechnologies.orient.core.sql.parser.OSkip skip = new com.orientechnologies.orient.core.sql.parser.OSkip(-2041052067);
        matchStatement.setSkip(skip);
        com.orientechnologies.orient.core.sql.parser.OMatchStatement copiedStatement = matchStatement.copy();
        org.junit.Assert.assertFalse(copiedStatement.isReturnDistinct());
    }

    @org.junit.Test(timeout = 4000)
    public void copy_preservesOrderBy() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement(-2068473975);
        com.orientechnologies.orient.core.sql.parser.OOrderBy orderBy = new com.orientechnologies.orient.core.sql.parser.OOrderBy();
        matchStatement.setOrderBy(orderBy);
        com.orientechnologies.orient.core.sql.parser.OMatchStatement copiedStatement = matchStatement.copy();
        org.junit.Assert.assertFalse(copiedStatement.isReturnDistinct());
    }

    @org.junit.Test(timeout = 4000)
    public void copy_preservesGroupBy() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement();
        com.orientechnologies.orient.core.sql.parser.OGroupBy groupBy = new com.orientechnologies.orient.core.sql.parser.OGroupBy(197);
        matchStatement.setGroupBy(groupBy);
        com.orientechnologies.orient.core.sql.parser.OMatchStatement copiedStatement = matchStatement.copy();
        org.junit.Assert.assertFalse(copiedStatement.isReturnDistinct());
    }

    @org.junit.Test(timeout = 4000)
    public void copy_preservesReturnNestedProjections() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement();
        java.util.Stack<com.orientechnologies.orient.core.sql.parser.ONestedProjection> nestedProjectionStack = new java.util.Stack<com.orientechnologies.orient.core.sql.parser.ONestedProjection>();
        nestedProjectionStack.setSize(996);
        matchStatement.setReturnNestedProjections(nestedProjectionStack);
        com.orientechnologies.orient.core.sql.parser.OMatchStatement copiedStatement = matchStatement.copy();
        org.junit.Assert.assertFalse(copiedStatement.isReturnDistinct());
    }

    @org.junit.Test(timeout = 4000)
    public void copy_handlesNullReturnNestedProjections() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement();
        matchStatement.returnNestedProjections = null;
        com.orientechnologies.orient.core.sql.parser.OMatchStatement copiedStatement = matchStatement.copy();
        org.junit.Assert.assertFalse(copiedStatement.isReturnDistinct());
    }

    @org.junit.Test(timeout = 4000)
    public void copy_preservesReturnAliases() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement();
        matchStatement.addReturnAlias(((com.orientechnologies.orient.core.sql.parser.OIdentifier) (null)));
        com.orientechnologies.orient.core.sql.parser.OMatchStatement copiedStatement = matchStatement.copy();
        org.junit.Assert.assertFalse(copiedStatement.isReturnDistinct());
        org.junit.Assert.assertTrue(copiedStatement.equals(((java.lang.Object) (matchStatement))));
    }

    @org.junit.Test(timeout = 4000)
    public void copy_preservesNullReturnAliases() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement();
        matchStatement.setReturnAliases(((java.util.List<com.orientechnologies.orient.core.sql.parser.OIdentifier>) (null)));
        com.orientechnologies.orient.core.sql.parser.OMatchStatement copiedStatement = matchStatement.copy();
        org.junit.Assert.assertFalse(copiedStatement.isReturnDistinct());
        org.junit.Assert.assertTrue(copiedStatement.equals(((java.lang.Object) (matchStatement))));
    }

    @org.junit.Test(timeout = 4000)
    public void copy_preservesNullReturnItems() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement();
        matchStatement.addReturnItem(((com.orientechnologies.orient.core.sql.parser.OExpression) (null)));
        com.orientechnologies.orient.core.sql.parser.OMatchStatement copiedStatement = matchStatement.copy();
        org.junit.Assert.assertFalse(copiedStatement.isReturnDistinct());
        org.junit.Assert.assertTrue(copiedStatement.equals(((java.lang.Object) (matchStatement))));
    }

    @org.junit.Test(timeout = 4000)
    public void copy_preservesReturnItems() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement();
        com.orientechnologies.orient.core.sql.parser.OExpression expression = new com.orientechnologies.orient.core.sql.parser.OExpression(((com.orientechnologies.orient.core.sql.parser.OIdentifier) (null)));
        matchStatement.addReturnItem(expression);
        com.orientechnologies.orient.core.sql.parser.OMatchStatement copiedStatement = matchStatement.copy();
        org.junit.Assert.assertFalse(copiedStatement.isReturnDistinct());
        org.junit.Assert.assertTrue(copiedStatement.equals(((java.lang.Object) (matchStatement))));
    }

    @org.junit.Test(timeout = 4000)
    public void copy_preservesNullReturnItems_whenSet() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement(0);
        matchStatement.setReturnItems(((java.util.List<com.orientechnologies.orient.core.sql.parser.OExpression>) (null)));
        com.orientechnologies.orient.core.sql.parser.OMatchStatement copiedStatement = matchStatement.copy();
        org.junit.Assert.assertTrue(copiedStatement.equals(((java.lang.Object) (matchStatement))));
        org.junit.Assert.assertFalse(copiedStatement.isReturnDistinct());
    }

    @org.junit.Test(timeout = 4000)
    public void copy_preservesNotMatchExpressions() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement();
        com.orientechnologies.orient.core.sql.parser.OMatchExpression matchExpression = new com.orientechnologies.orient.core.sql.parser.OMatchExpression(-3607);
        matchStatement.addNotMatchExpression(matchExpression);
        com.orientechnologies.orient.core.sql.parser.OMatchStatement copiedStatement = matchStatement.copy();
        org.junit.Assert.assertFalse(copiedStatement.isReturnDistinct());
        org.junit.Assert.assertTrue(copiedStatement.equals(((java.lang.Object) (matchStatement))));
    }

    @org.junit.Test(timeout = 4000)
    public void copy_handlesNullNotMatchExpressions() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement();
        matchStatement.notMatchExpressions = null;
        com.orientechnologies.orient.core.sql.parser.OMatchStatement copiedStatement = matchStatement.copy();
        org.junit.Assert.assertTrue(copiedStatement.equals(((java.lang.Object) (matchStatement))));
        org.junit.Assert.assertFalse(copiedStatement.isReturnDistinct());
    }

    @org.junit.Test(timeout = 4000)
    public void copy_throwsNPE_whenMatchExpressionHasNullOrigin() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement();
        com.orientechnologies.orient.core.sql.parser.OMatchExpression matchExpression = new com.orientechnologies.orient.core.sql.parser.OMatchExpression(-1452647753);
        matchStatement.addMatchExpression(matchExpression);
        try {
            matchStatement.copy();
            org.junit.Assert.fail("Expecting exception: NullPointerException");
        } catch (java.lang.NullPointerException e) {
            org.evosuite.runtime.EvoAssertions.verifyException("com.orientechnologies.orient.core.sql.parser.OMatchStatement", e);
        }
    }

    @org.junit.Test(timeout = 4000)
    public void copy_throwsNPE_whenMatchExpressionsListNull() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement();
        matchStatement.setMatchExpressions(((java.util.List<com.orientechnologies.orient.core.sql.parser.OMatchExpression>) (null)));
        try {
            matchStatement.copy();
            org.junit.Assert.fail("Expecting exception: NullPointerException");
        } catch (java.lang.NullPointerException e) {
            org.evosuite.runtime.EvoAssertions.verifyException("com.orientechnologies.orient.core.sql.parser.OMatchStatement", e);
        }
    }

    @org.junit.Test(timeout = 4000)
    public void toGenericStatement_withTwoReturnItems_usesPlaceholders() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement();
        com.orientechnologies.orient.core.sql.parser.OArrayConcatExpressionElement arrayConcatElement = new com.orientechnologies.orient.core.sql.parser.OArrayConcatExpressionElement(900);
        matchStatement.addReturnItem(arrayConcatElement);
        matchStatement.addReturnItem(arrayConcatElement);
        java.lang.String generic = matchStatement.toGenericStatement();
        org.junit.Assert.assertEquals("MATCH  RETURN ?, ?", generic);
    }

    @org.junit.Test(timeout = 4000)
    public void toGenericStatement_reflectsDistinctFlag() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement();
        org.junit.Assert.assertFalse(matchStatement.isReturnDistinct());
        matchStatement.setReturnDistinct(true);
        matchStatement.toGenericStatement();
        org.junit.Assert.assertTrue(matchStatement.isReturnDistinct());
    }

    @org.junit.Test(timeout = 4000)
    public void toGenericStatement_throwsNPE_whenMatchExpressionNull() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement();
        matchStatement.addMatchExpression(((com.orientechnologies.orient.core.sql.parser.OMatchExpression) (null)));
        try {
            matchStatement.toGenericStatement();
            org.junit.Assert.fail("Expecting exception: NullPointerException");
        } catch (java.lang.NullPointerException e) {
            org.evosuite.runtime.EvoAssertions.verifyException("com.orientechnologies.orient.core.sql.parser.OMatchStatement", e);
        }
    }

    @org.junit.Test(timeout = 4000)
    public void toGenericStatement_appendsToProvidedBuilder() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement();
        java.lang.StringBuilder builder = new java.lang.StringBuilder("$matches");
        matchStatement.toGenericStatement(builder);
        org.junit.Assert.assertEquals("$matchesMATCH  RETURN ", builder.toString());
    }

    @org.junit.Test(timeout = 4000)
    public void toString_withAliasProjection() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement();
        com.orientechnologies.orient.core.sql.parser.AggregateProjectionSplit split = new com.orientechnologies.orient.core.sql.parser.AggregateProjectionSplit();
        com.orientechnologies.orient.core.sql.parser.OIdentifier aliasIdentifier = split.getNextAlias();
        matchStatement.addReturnAlias(aliasIdentifier);
        com.orientechnologies.orient.core.sql.parser.OExpression expression = new com.orientechnologies.orient.core.sql.parser.OExpression(aliasIdentifier);
        matchStatement.addReturnItem(expression);
        java.lang.String output = matchStatement.toString();
        org.junit.Assert.assertEquals("MATCH  RETURN _$$$OALIAS$$_0 AS _$$$OALIAS$$_0", output);
    }

    @org.junit.Test(timeout = 4000)
    public void toString_withNullNestedProjection() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement();
        matchStatement.addReturnNestedProjection(((com.orientechnologies.orient.core.sql.parser.ONestedProjection) (null)));
        com.orientechnologies.orient.core.sql.parser.OArrayConcatExpressionElement arrayConcatElement = new com.orientechnologies.orient.core.sql.parser.OArrayConcatExpressionElement(1);
        matchStatement.addReturnItem(arrayConcatElement);
        java.lang.String output = matchStatement.toString();
        org.junit.Assert.assertEquals("MATCH  RETURN null", output);
    }

    @org.junit.Test(timeout = 4000)
    public void toString_returnsNullWhenReturnNestedProjectionsNull() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement();
        matchStatement.returnNestedProjections = null;
        com.orientechnologies.orient.core.sql.parser.OArrayConcatExpressionElement arrayConcatElement = new com.orientechnologies.orient.core.sql.parser.OArrayConcatExpressionElement(1400);
        matchStatement.addReturnItem(arrayConcatElement);
        java.lang.String output = matchStatement.toString();
        org.junit.Assert.assertEquals("MATCH  RETURN null", output);
    }

    @org.junit.Test(timeout = 4000)
    public void toString_withTwoNullReturnItems() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement();
        com.orientechnologies.orient.core.sql.parser.OArrayConcatExpressionElement arrayConcatElement = new com.orientechnologies.orient.core.sql.parser.OArrayConcatExpressionElement(404);
        matchStatement.addReturnItem(arrayConcatElement);
        matchStatement.addReturnItem(arrayConcatElement);
        java.lang.String output = matchStatement.toString();
        org.junit.Assert.assertEquals("MATCH  RETURN null, null", output);
    }

    @org.junit.Test(timeout = 4000)
    public void toString_throwsNPE_whenMatchExpressionNull() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement();
        matchStatement.addMatchExpression(((com.orientechnologies.orient.core.sql.parser.OMatchExpression) (null)));
        try {
            matchStatement.toString();
            org.junit.Assert.fail("Expecting exception: NullPointerException");
        } catch (java.lang.NullPointerException e) {
            org.evosuite.runtime.EvoAssertions.verifyException("com.orientechnologies.orient.core.sql.parser.OMatchStatement", e);
        }
    }

    @org.junit.Test(timeout = 4000)
    public void toString_appendsToProvidedBuilder_withMap() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement(-2068473975);
        java.util.TreeMap<java.lang.Object, java.lang.Object> map = new java.util.TreeMap<java.lang.Object, java.lang.Object>();
        java.lang.StringBuilder builder = new java.lang.StringBuilder("EVKgf ?\"\";vS[x[");
        matchStatement.toString(((java.util.Map<java.lang.Object, java.lang.Object>) (map)), builder);
        org.junit.Assert.assertEquals("EVKgf ?\"\";vS[x[MATCH  RETURN ", builder.toString());
    }

    @org.junit.Test(timeout = 4000)
    public void returnsPaths_falseWithArrayConcatElement() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement();
        com.orientechnologies.orient.core.sql.parser.OArrayConcatExpressionElement arrayConcatElement = new com.orientechnologies.orient.core.sql.parser.OArrayConcatExpressionElement(900);
        matchStatement.addReturnItem(arrayConcatElement);
        boolean returnsPaths = matchStatement.returnsPaths();
        org.junit.Assert.assertFalse(returnsPaths);
        org.junit.Assert.assertFalse(matchStatement.isReturnDistinct());
    }

    @org.junit.Test(timeout = 4000)
    public void returnsPatterns_falseWithArrayConcatElement() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement();
        com.orientechnologies.orient.core.sql.parser.OArrayConcatExpressionElement arrayConcatElement = new com.orientechnologies.orient.core.sql.parser.OArrayConcatExpressionElement(900);
        matchStatement.addReturnItem(arrayConcatElement);
        boolean returnsPatterns = matchStatement.returnsPatterns();
        org.junit.Assert.assertFalse(returnsPatterns);
        org.junit.Assert.assertFalse(matchStatement.isReturnDistinct());
    }

    @org.junit.Test(timeout = 4000)
    public void returnsElements_falseWithArrayConcatElement() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement();
        com.orientechnologies.orient.core.sql.parser.OArrayConcatExpressionElement arrayConcatElement = new com.orientechnologies.orient.core.sql.parser.OArrayConcatExpressionElement(31);
        matchStatement.addReturnItem(arrayConcatElement);
        boolean returnsElements = matchStatement.returnsElements();
        org.junit.Assert.assertFalse(returnsElements);
        org.junit.Assert.assertFalse(matchStatement.isReturnDistinct());
    }

    @org.junit.Test(timeout = 4000)
    public void returnsPathElements_falseWithArrayConcatElement() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement();
        com.orientechnologies.orient.core.sql.parser.OArrayConcatExpressionElement arrayConcatElement = new com.orientechnologies.orient.core.sql.parser.OArrayConcatExpressionElement(900);
        matchStatement.addReturnItem(arrayConcatElement);
        boolean returnsPathElements = matchStatement.returnsPathElements();
        org.junit.Assert.assertFalse(matchStatement.isReturnDistinct());
        org.junit.Assert.assertFalse(returnsPathElements);
    }

    @org.junit.Test(timeout = 4000)
    public void matchContext_copy_createsNewInstance() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement();
        com.orientechnologies.orient.core.sql.parser.OMatchStatement.MatchContext matchContext = matchStatement.new com.orientechnologies.orient.core.sql.parser.MatchContext();
        com.orientechnologies.orient.core.id.ORecordId recordId = new com.orientechnologies.orient.core.id.ORecordId(1613, 1613);
        com.orientechnologies.orient.core.sql.parser.OMatchStatement.MatchContext matchContextCopy = matchContext.copy("@,l>", recordId);
        org.junit.Assert.assertFalse(matchStatement.isReturnDistinct());
        org.junit.Assert.assertNotSame(matchContextCopy, matchContext);
    }

    @org.junit.Test(timeout = 4000)
    public void matchContext_toDoc_throwsNoClassDefFoundError() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement();
        com.orientechnologies.orient.core.sql.parser.OMatchStatement.MatchContext matchContext = matchStatement.new com.orientechnologies.orient.core.sql.parser.MatchContext();
        try {
            matchContext.toDoc();
            org.junit.Assert.fail("Expecting exception: NoClassDefFoundError");
        } catch (java.lang.NoClassDefFoundError e) {
            org.evosuite.runtime.EvoAssertions.verifyException("com.orientechnologies.orient.core.db.document.ODatabaseDocumentAbstract", e);
        }
    }

    @org.junit.Test(timeout = 4000)
    public void copy_preservesNullNotMatchExpression() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement();
        matchStatement.addNotMatchExpression(((com.orientechnologies.orient.core.sql.parser.OMatchExpression) (null)));
        com.orientechnologies.orient.core.sql.parser.OMatchStatement copiedStatement = matchStatement.copy();
        org.junit.Assert.assertTrue(copiedStatement.equals(((java.lang.Object) (matchStatement))));
        org.junit.Assert.assertFalse(copiedStatement.isReturnDistinct());
    }

    @org.junit.Test(timeout = 4000)
    public void toGenericStatement_includesGroupBy() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement();
        com.orientechnologies.orient.core.sql.parser.OGroupBy groupBy = new com.orientechnologies.orient.core.sql.parser.OGroupBy(963);
        matchStatement.setGroupBy(groupBy);
        java.lang.StringBuilder builder = new java.lang.StringBuilder("$matches");
        matchStatement.toGenericStatement(builder);
        org.junit.Assert.assertEquals("$matchesMATCH  RETURN  GROUP BY ", builder.toString());
    }

    @org.junit.Test(timeout = 4000)
    public void toString_reflectsDistinctFlag() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement();
        org.junit.Assert.assertFalse(matchStatement.isReturnDistinct());
        matchStatement.setReturnDistinct(true);
        matchStatement.toString();
        org.junit.Assert.assertTrue(matchStatement.isReturnDistinct());
    }

    @org.junit.Test(timeout = 4000)
    public void copy_preservesUnwind() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement();
        com.orientechnologies.orient.core.sql.parser.OUnwind unwind = new com.orientechnologies.orient.core.sql.parser.OUnwind(((com.orientechnologies.orient.core.sql.parser.OrientSql) (null)), -1556);
        matchStatement.setUnwind(unwind);
        com.orientechnologies.orient.core.sql.parser.OMatchStatement copiedStatement = matchStatement.copy();
        org.junit.Assert.assertTrue(copiedStatement.equals(((java.lang.Object) (matchStatement))));
        org.junit.Assert.assertFalse(copiedStatement.isReturnDistinct());
    }

    @org.junit.Test(timeout = 4000)
    public void isIdempotent_returnsTrue() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement();
        boolean idempotent = matchStatement.isIdempotent();
        org.junit.Assert.assertTrue(idempotent);
        org.junit.Assert.assertFalse(matchStatement.isReturnDistinct());
    }

    @org.junit.Test(timeout = 4000)
    public void toString_includesSkip() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement();
        com.orientechnologies.orient.core.sql.parser.OSkip skip = new com.orientechnologies.orient.core.sql.parser.OSkip(-4162);
        matchStatement.setSkip(skip);
        java.lang.String output = matchStatement.toString();
        org.junit.Assert.assertEquals("MATCH  RETURN  ", output);
    }

    @org.junit.Test(timeout = 4000)
    public void toString_withNullAliasAndExpression() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement();
        matchStatement.addReturnAlias(((com.orientechnologies.orient.core.sql.parser.OIdentifier) (null)));
        com.orientechnologies.orient.core.sql.parser.OExpression expression = new com.orientechnologies.orient.core.sql.parser.OExpression(((com.orientechnologies.orient.core.sql.parser.OIdentifier) (null)));
        matchStatement.addReturnItem(expression);
        java.lang.String output = matchStatement.toString();
        org.junit.Assert.assertEquals("MATCH  RETURN ", output);
    }

    @org.junit.Test(timeout = 4000)
    public void toString_withNestedProjectionAndArrayConcat() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement();
        com.orientechnologies.orient.core.sql.parser.ONestedProjection nestedProjection = new com.orientechnologies.orient.core.sql.parser.ONestedProjection(130);
        matchStatement.addReturnNestedProjection(nestedProjection);
        com.orientechnologies.orient.core.sql.parser.OArrayConcatExpressionElement arrayConcatElement = new com.orientechnologies.orient.core.sql.parser.OArrayConcatExpressionElement(130);
        matchStatement.addReturnItem(arrayConcatElement);
        java.lang.String output = matchStatement.toString();
        org.junit.Assert.assertEquals("MATCH  RETURN null:{}", output);
    }

    @org.junit.Test(timeout = 4000)
    public void refersToParent_returnsFalse() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement();
        boolean refers = matchStatement.refersToParent();
        org.junit.Assert.assertFalse(matchStatement.isReturnDistinct());
        org.junit.Assert.assertFalse(refers);
    }

    @org.junit.Test(timeout = 4000)
    public void constructor_withOrientSqlAndId() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement(((com.orientechnologies.orient.core.sql.parser.OrientSql) (null)), 1);
        org.junit.Assert.assertFalse(matchStatement.isReturnDistinct());
    }

    @org.junit.Test(timeout = 4000)
    public void setNotMatchExpressions_acceptsVector() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement();
        java.util.Vector<com.orientechnologies.orient.core.sql.parser.OMatchExpression> vector = new java.util.Vector<com.orientechnologies.orient.core.sql.parser.OMatchExpression>();
        matchStatement.setNotMatchExpressions(vector);
        org.junit.Assert.assertFalse(matchStatement.isReturnDistinct());
    }

    @org.junit.Test(timeout = 4000)
    public void toString_appendsToProvidedBuilder_withOrderBy() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement(-2068473975);
        com.orientechnologies.orient.core.sql.parser.OOrderBy orderBy = new com.orientechnologies.orient.core.sql.parser.OOrderBy();
        matchStatement.setOrderBy(orderBy);
        java.util.TreeMap<java.lang.Object, java.lang.Object> map = new java.util.TreeMap<java.lang.Object, java.lang.Object>();
        java.lang.StringBuilder builder = new java.lang.StringBuilder("EVKgf ?\"\";vS[x[");
        matchStatement.toString(((java.util.Map<java.lang.Object, java.lang.Object>) (map)), builder);
        org.junit.Assert.assertEquals("EVKgf ?\"\";vS[x[MATCH  RETURN  ", builder.toString());
        org.junit.Assert.assertFalse(matchStatement.isReturnDistinct());
    }

    @org.junit.Test(timeout = 4000)
    public void setLimit_acceptsNull() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement();
        matchStatement.setLimit(((com.orientechnologies.orient.core.sql.parser.OLimit) (null)));
        org.junit.Assert.assertFalse(matchStatement.isReturnDistinct());
    }

    @org.junit.Test(timeout = 4000)
    public void copy_throwsNPE_whenMatchExpressionNull() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement();
        matchStatement.addMatchExpression(((com.orientechnologies.orient.core.sql.parser.OMatchExpression) (null)));
        try {
            matchStatement.copy();
            org.junit.Assert.fail("Expecting exception: NullPointerException");
        } catch (java.lang.NullPointerException e) {
            org.evosuite.runtime.EvoAssertions.verifyException("com.orientechnologies.orient.core.sql.parser.OMatchStatement", e);
        }
    }

    @org.junit.Test(timeout = 4000)
    public void copy_preservesReturnNestedProjections_fromImmutableList() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement();
        com.orientechnologies.orient.core.sql.parser.ONestedProjection nestedProjection = new com.orientechnologies.orient.core.sql.parser.ONestedProjection(-1725222855);
        java.util.List<com.orientechnologies.orient.core.sql.parser.ONestedProjection> nestedProjections = java.util.List.of(nestedProjection);
        matchStatement.setReturnNestedProjections(nestedProjections);
        com.orientechnologies.orient.core.sql.parser.OMatchStatement copiedStatement = matchStatement.copy();
        org.junit.Assert.assertFalse(copiedStatement.isReturnDistinct());
        org.junit.Assert.assertTrue(copiedStatement.equals(((java.lang.Object) (matchStatement))));
    }

    @org.junit.Test(timeout = 4000)
    public void createExecutionPlan_throwsNPE_whenContextNull() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement();
        try {
            matchStatement.createExecutionPlan(((com.orientechnologies.orient.core.command.OCommandContext) (null)));
            org.junit.Assert.fail("Expecting exception: NullPointerException");
        } catch (java.lang.NullPointerException e) {
            org.evosuite.runtime.EvoAssertions.verifyException("com.orientechnologies.orient.core.sql.executor.OMatchExecutionPlanner", e);
        }
    }

    @org.junit.Test(timeout = 4000)
    public void getNotMatchExpressions_returnsEmptyForNewInstance() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement(-2068473975);
        matchStatement.getNotMatchExpressions();
        org.junit.Assert.assertFalse(matchStatement.isReturnDistinct());
    }

    @org.junit.Test(timeout = 4000)
    public void getMatchExpressions_returnsEmptyForNewInstance() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement(-2068473975);
        matchStatement.getMatchExpressions();
        org.junit.Assert.assertFalse(matchStatement.isReturnDistinct());
    }

    @org.junit.Test(timeout = 4000)
    public void getGroupBy_returnsNullByDefault() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement(-2068473975);
        matchStatement.getGroupBy();
        org.junit.Assert.assertFalse(matchStatement.isReturnDistinct());
    }

    @org.junit.Test(timeout = 4000)
    public void getAndSetReturnAliases_withEmptyList() throws java.lang.Throwable {
        com.orientechnologies.orient.core.sql.parser.OMatchStatement matchStatement = new com.orientechnologies.orient.core.sql.parser.OMatchStatement(-2068473975);
        java.util.List<com.orientechnologies.orient.core.sql.parser.OIdentifier> aliases = matchStatement.getReturnAliases();
        matchStatement.setReturnAliases(aliases);
        org.junit.Assert.assertFalse(matchStatement.isReturnDistinct());
    }

    // Adapted and merged parser syntax tests from IGT
    private com.orientechnologies.orient.core.sql.parser.SimpleNode checkRightSyntax(java.lang.String query) {
        com.orientechnologies.orient.core.sql.parser.SimpleNode result = checkSyntax(query, true);
        java.lang.StringBuilder builder = new java.lang.StringBuilder();
        result.toString(null, builder);
        return checkSyntax(builder.toString(), true);
    }

    private com.orientechnologies.orient.core.sql.parser.SimpleNode checkWrongSyntax(java.lang.String query) {
        return checkSyntax(query, false);
    }

    private com.orientechnologies.orient.core.sql.parser.SimpleNode checkSyntax(java.lang.String query, boolean isCorrect) {
        com.orientechnologies.orient.core.sql.parser.OrientSql osql = getParserFor(query);
        try {
            com.orientechnologies.orient.core.sql.parser.SimpleNode result = osql.parse();
            if (!isCorrect) {
                org.junit.Assert.fail();
            }
            return result;
        } catch (java.lang.Exception e) {
            if (isCorrect) {
                e.printStackTrace();
                org.junit.Assert.fail();
            }
        }
        return null;
    }

    private com.orientechnologies.orient.core.sql.parser.OrientSql getParserFor(java.lang.String string) {
        java.io.InputStream is = new java.io.ByteArrayInputStream(string.getBytes());
        return new com.orientechnologies.orient.core.sql.parser.OrientSql(is);
    }

    @org.junit.Test(timeout = 4000)
    public void testWrongFilterKey() {
        checkWrongSyntax("MATCH {clasx: 'V'} RETURN foo");
    }

    @org.junit.Test(timeout = 4000)
    public void testBasicMatch() {
        checkRightSyntax("MATCH {class: 'V', as: foo} RETURN foo");
    }

    @org.junit.Test(timeout = 4000)
    public void testNoReturn() {
        checkWrongSyntax("MATCH {class: 'V', as: foo}");
    }

    @org.junit.Test(timeout = 4000)
    public void testSingleMethod() {
        checkRightSyntax("MATCH {class: 'V', as: foo}.out() RETURN foo");
    }

    @org.junit.Test(timeout = 4000)
    public void testArrowsNoBrackets() {
        checkWrongSyntax("MATCH {}-->-->{as:foo} RETURN foo");
    }

    @org.junit.Test(timeout = 4000)
    public void testSingleMethodAndFilter() {
        checkRightSyntax("MATCH {class: 'V', as: foo}.out(){class: 'V', as: bar} RETURN foo");
        checkRightSyntax("MATCH {class: 'V', as: foo}-E->{class: 'V', as: bar} RETURN foo");
        checkRightSyntax("MATCH {class: 'V', as: foo}-->{class: 'V', as: bar} RETURN foo");
    }

    @org.junit.Test(timeout = 4000)
    public void testLongPath() {
        checkRightSyntax("MATCH {class: 'V', as: foo}.out().in('foo').both('bar').out(){as: bar} RETURN foo");
        checkRightSyntax("MATCH {class: 'V', as: foo}-->{}<-foo-{}-bar-{}-->{as: bar} RETURN foo");
    }

    @org.junit.Test(timeout = 4000)
    public void testLongPath2() {
        checkRightSyntax("MATCH {class: 'V', as: foo}.out().in('foo'){}.both('bar'){CLASS: 'bar'}.out(){as: bar} RETURN foo");
    }

    @org.junit.Test(timeout = 4000)
    public void testFilterTypes() {
        java.lang.StringBuilder query = new java.lang.StringBuilder();
        query.append("MATCH {");
        query.append("   class: 'v', ");
        query.append("   as: foo, ");
        query.append("   where: (name = 'foo' and surname = 'bar' or aaa in [1,2,3]), ");
        query.append("   maxDepth: 10 ");
        query.append("} return foo");
        checkRightSyntax(query.toString());
    }

    @org.junit.Test(timeout = 4000)
    public void testFilterTypes2() {
        java.lang.StringBuilder query = new java.lang.StringBuilder();
        query.append("MATCH {");
        query.append("   classes: ['V', 'E'], ");
        query.append("   as: foo, ");
        query.append("   where: (name = 'foo' and surname = 'bar' or aaa in [1,2,3]), ");
        query.append("   maxDepth: 10 ");
        query.append("} return foo");
        checkRightSyntax(query.toString());
    }

    @org.junit.Test(timeout = 4000)
    public void testMultiPath() {
        java.lang.StringBuilder query = new java.lang.StringBuilder();
        query.append("MATCH {}");
        query.append("  .(out().in(){class:'v'}.both('Foo')){maxDepth: 3}.out() return foo");
        checkRightSyntax(query.toString());
    }

    @org.junit.Test(timeout = 4000)
    public void testMultiPathArrows() {
        java.lang.StringBuilder query = new java.lang.StringBuilder();
        query.append("MATCH {}");
        query.append("  .(-->{}<--{class:'v'}--){maxDepth: 3}-->{} return foo");
        checkRightSyntax(query.toString());
    }

    @org.junit.Test(timeout = 4000)
    public void testMultipleMatches() {
        java.lang.String query = "MATCH {class: 'V', as: foo}.out(){class: 'V', as: bar}, ";
        query += " {class: 'V', as: foo}.out(){class: 'V', as: bar},";
        query += " {class: 'V', as: foo}.out(){class: 'V', as: bar} RETURN foo";
        checkRightSyntax(query);
    }

    @org.junit.Test(timeout = 4000)
    public void testMultipleMatchesArrow() {
        java.lang.String query = "MATCH {class: 'V', as: foo}-->{class: 'V', as: bar}, ";
        query += " {class: 'V', as: foo}-->{class: 'V', as: bar},";
        query += " {class: 'V', as: foo}-->{class: 'V', as: bar} RETURN foo";
        checkRightSyntax(query);
    }

    @org.junit.Test(timeout = 4000)
    public void testWhile() {
        checkRightSyntax("MATCH {class: 'V', as: foo}.out(){while:($depth<4), as:bar} RETURN bar ");
    }

    @org.junit.Test(timeout = 4000)
    public void testWhileArrow() {
        checkRightSyntax("MATCH {class: 'V', as: foo}-->{while:($depth<4), as:bar} RETURN bar ");
    }

    @org.junit.Test(timeout = 4000)
    public void testLimit() {
        checkRightSyntax("MATCH {class: 'V'} RETURN foo limit 10");
    }

    @org.junit.Test(timeout = 4000)
    public void testReturnJson() {
        checkRightSyntax("MATCH {class: 'V'} RETURN {'name':'foo', 'value': bar}");
    }

    @org.junit.Test(timeout = 4000)
    public void testOptional() {
        checkRightSyntax("MATCH {class: 'V', as: foo}-->{}<-foo-{}-bar-{}-->{as: bar, optional:true} RETURN foo");
        checkRightSyntax("MATCH {class: 'V', as: foo}-->{}<-foo-{}-bar-{}-->{as: bar, optional:false} RETURN foo");
    }

    @org.junit.Test(timeout = 4000)
    public void testOrderBy() {
        checkRightSyntax("MATCH {class: 'V', as: foo}-->{} RETURN foo ORDER BY foo");
        checkRightSyntax("MATCH {class: 'V', as: foo}-->{} RETURN foo ORDER BY foo limit 10");
    }

    @org.junit.Test(timeout = 4000)
    public void testNestedProjections() {
        checkRightSyntax("MATCH {class: 'V', as: foo}-->{} RETURN foo:{name, surname}");
        checkRightSyntax("MATCH {class: 'V', as: foo}-->{as:bar} RETURN foo:{name, surname} as bloo, bar:{*}");
    }

    @org.junit.Test(timeout = 4000)
    public void testUnwind() {
        checkRightSyntax("MATCH {class: 'V', as: foo}-->{as:bar} RETURN foo.name, bar.name as x unwind x");
    }

    @org.junit.Test(timeout = 4000)
    public void testDepthAlias() {
        checkRightSyntax("MATCH {class: 'V', as: foo}-->{as:bar, while:($depth < 2), depthAlias: depth} RETURN depth");
    }

    @org.junit.Test(timeout = 4000)
    public void testPathAlias() {
        checkRightSyntax("MATCH {class: 'V', as: foo}-->{as:bar, while:($depth < 2), pathAlias: barPath} RETURN barPath");
    }

    @org.junit.Test(timeout = 4000)
    public void testClusterTarget() {
        checkRightSyntax("MATCH {cluster:v, as: foo} RETURN $elements");
        checkRightSyntax("MATCH {cluster:12, as: foo} RETURN $elements");
        checkRightSyntax("MATCH {cluster: v, as: foo} RETURN $elements");
        checkRightSyntax("MATCH {cluster: `v`, as: foo} RETURN $elements");
        checkRightSyntax("MATCH {cluster:`v`, as: foo} RETURN $elements");
        checkRightSyntax("MATCH {cluster: 12, as: foo} RETURN $elements");
        checkWrongSyntax("MATCH {cluster: 12.1, as: foo} RETURN $elements");
    }

    @org.junit.Test(timeout = 4000)
    public void testNot() {
        checkRightSyntax("MATCH {cluster:v, as: foo}, NOT {as:foo}-->{as:bar} RETURN $elements");
    }

    @org.junit.Test(timeout = 4000)
    public void testSkip() {
        checkRightSyntax("MATCH {class: 'V', as: foo}-->{as:bar} RETURN foo.name, bar.name skip 10 limit 10");
    }

    @org.junit.Test(timeout = 4000)
    public void testFieldTraversal() {
        checkRightSyntax("MATCH {class: 'V', as: foo}.toBar{as:bar} RETURN foo.name, bar.name skip 10 limit 10");
        checkRightSyntax("MATCH {class: 'V', as: foo}.toBar{as:bar}.out(){as:c} RETURN foo.name, bar.name skip 10 limit 10");
        checkRightSyntax("MATCH {class: 'V', as: foo}.toBar.baz{as:bar} RETURN foo.name, bar.name skip 10 limit 10");
        checkRightSyntax("MATCH {class: 'V', as: foo}.toBar.out(){as:bar} RETURN foo.name, bar.name skip 10 limit 10");
    }
}