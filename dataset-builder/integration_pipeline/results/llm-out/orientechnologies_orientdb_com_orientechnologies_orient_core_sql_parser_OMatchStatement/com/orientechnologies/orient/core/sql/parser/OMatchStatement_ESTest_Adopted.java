package com.orientechnologies.orient.core.sql.parser;
import OMatchStatement_ESTest_scaffolding;
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
public class OMatchStatement_ESTest_Adopted extends OMatchStatement_ESTest_scaffolding {
    @Test(timeout = 4000)
    public void buildPatterns_doesNotAffectDistinctFlag() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement(19);
        matchStatement.buildPatterns();
        assertFalse(matchStatement.isReturnDistinct());
    }

    @Test(timeout = 4000)
    public void getReturnNestedProjections_returnsNullWhenUnset() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        matchStatement.setReturnNestedProjections(((List<ONestedProjection>) (null)));
        List<ONestedProjection> nestedProjections = matchStatement.getReturnNestedProjections();
        assertNull(nestedProjections);
        assertFalse(matchStatement.isReturnDistinct());
    }

    @Test(timeout = 4000)
    public void getReturnItems_returnsNonEmptyAfterAdd() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        OArrayConcatExpressionElement arrayConcatElement = new OArrayConcatExpressionElement(900);
        matchStatement.addReturnItem(arrayConcatElement);
        List<OExpression> returnItems = matchStatement.getReturnItems();
        assertFalse(matchStatement.isReturnDistinct());
        assertEquals(1, returnItems.size());
    }

    @Test(timeout = 4000)
    public void getReturnAliases_returnsNonEmptyAfterAdd() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        matchStatement.addReturnAlias(((OIdentifier) (null)));
        List<OIdentifier> returnAliases = matchStatement.getReturnAliases();
        assertFalse(returnAliases.isEmpty());
        assertFalse(matchStatement.isReturnDistinct());
    }

    @Test(timeout = 4000)
    public void getNotMatchExpressions_returnsNullWhenFieldIsNull() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        matchStatement.notMatchExpressions = null;
        List<OMatchExpression> notMatchExpressions = matchStatement.getNotMatchExpressions();
        assertFalse(matchStatement.isReturnDistinct());
        assertNull(notMatchExpressions);
    }

    @Test(timeout = 4000)
    public void getNotMatchExpressions_returnsNonEmptyAfterAdd() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        matchStatement.addNotMatchExpression(((OMatchExpression) (null)));
        List<OMatchExpression> notMatchExpressions = matchStatement.getNotMatchExpressions();
        assertFalse(matchStatement.isReturnDistinct());
        assertFalse(notMatchExpressions.isEmpty());
    }

    @Test(timeout = 4000)
    public void getMatchExpressions_containsAddedExpression() throws Throwable {
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
    public void getGroupBy_returnsSetValue() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        OGroupBy groupBy = new OGroupBy(0);
        matchStatement.setGroupBy(groupBy);
        OGroupBy returnedGroupBy = matchStatement.getGroupBy();
        assertFalse(matchStatement.isReturnDistinct());
        assertNotNull(returnedGroupBy);
    }

    @Test(timeout = 4000)
    public void copy_createsNewInstance_whenDistinctTrue() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement(-1210);
        matchStatement.returnDistinct = true;
        OMatchStatement copiedStatement = matchStatement.copy();
        assertNotSame(copiedStatement, matchStatement);
    }

    @Test(timeout = 4000)
    public void getSkip_default() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        matchStatement.getSkip();
        assertFalse(matchStatement.isReturnDistinct());
    }

    @Test(timeout = 4000)
    public void getOrderBy_default() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        matchStatement.getOrderBy();
        assertFalse(matchStatement.isReturnDistinct());
    }

    @Test(timeout = 4000)
    public void getLimit_default() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        matchStatement.getLimit();
        assertFalse(matchStatement.isReturnDistinct());
    }

    @Test(timeout = 4000)
    public void getUnwind_default() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        matchStatement.getUnwind();
        assertFalse(matchStatement.isReturnDistinct());
    }

    @Test(timeout = 4000)
    public void getReturnNestedProjections_returnsEmptyByDefault() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        matchStatement.getReturnNestedProjections();
        assertFalse(matchStatement.isReturnDistinct());
    }

    @Test(timeout = 4000)
    public void getReturnItems_returnsEmptyByDefault() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        matchStatement.getReturnItems();
        assertFalse(matchStatement.isReturnDistinct());
    }

    @Test(timeout = 4000)
    public void isReturnDistinct_defaultsToFalse() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        boolean distinct = matchStatement.isReturnDistinct();
        assertFalse(distinct);
    }

    @Test(timeout = 4000)
    public void hashCode_withUnwindAfterCopy() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement(-1159);
        OMatchStatement copiedStatement = matchStatement.copy();
        OUnwind unwind = new OUnwind(-1159);
        copiedStatement.unwind = unwind;
        copiedStatement.hashCode();
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
    public void hashCode_handlesNullReturnNestedProjections() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        matchStatement.returnNestedProjections = null;
        matchStatement.hashCode();
        assertFalse(matchStatement.isReturnDistinct());
    }

    @Test(timeout = 4000)
    public void equals_withUnrelatedObject_returnsFalse() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        Object unrelated = new Object();
        boolean equalsResult = matchStatement.equals(unrelated);
        assertFalse(matchStatement.isReturnDistinct());
        assertFalse(equalsResult);
    }

    @Test(timeout = 4000)
    public void equals_withSelf_returnsTrue() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        boolean equalsResult = matchStatement.equals(matchStatement);
        assertTrue(equalsResult);
        assertFalse(matchStatement.isReturnDistinct());
    }

    @Test(timeout = 4000)
    public void equals_withNull_returnsFalse() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        boolean equalsResult = matchStatement.equals(((Object) (null)));
        assertFalse(equalsResult);
        assertFalse(matchStatement.isReturnDistinct());
    }

    @Test(timeout = 4000)
    public void copy_preservesSkip() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        OSkip skip = new OSkip(-2041052067);
        matchStatement.setSkip(skip);
        OMatchStatement copiedStatement = matchStatement.copy();
        assertFalse(copiedStatement.isReturnDistinct());
    }

    @Test(timeout = 4000)
    public void copy_preservesOrderBy() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement(-2068473975);
        OOrderBy orderBy = new OOrderBy();
        matchStatement.setOrderBy(orderBy);
        OMatchStatement copiedStatement = matchStatement.copy();
        assertFalse(copiedStatement.isReturnDistinct());
    }

    @Test(timeout = 4000)
    public void copy_preservesGroupBy() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        OGroupBy groupBy = new OGroupBy(197);
        matchStatement.setGroupBy(groupBy);
        OMatchStatement copiedStatement = matchStatement.copy();
        assertFalse(copiedStatement.isReturnDistinct());
    }

    @Test(timeout = 4000)
    public void copy_preservesReturnNestedProjections() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        Stack<ONestedProjection> nestedProjectionStack = new Stack<ONestedProjection>();
        nestedProjectionStack.setSize(996);
        matchStatement.setReturnNestedProjections(nestedProjectionStack);
        OMatchStatement copiedStatement = matchStatement.copy();
        assertFalse(copiedStatement.isReturnDistinct());
    }

    @Test(timeout = 4000)
    public void copy_handlesNullReturnNestedProjections() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        matchStatement.returnNestedProjections = null;
        OMatchStatement copiedStatement = matchStatement.copy();
        assertFalse(copiedStatement.isReturnDistinct());
    }

    @Test(timeout = 4000)
    public void copy_preservesReturnAliases() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        matchStatement.addReturnAlias(((OIdentifier) (null)));
        OMatchStatement copiedStatement = matchStatement.copy();
        assertFalse(copiedStatement.isReturnDistinct());
        assertTrue(copiedStatement.equals(((Object) (matchStatement))));
    }

    @Test(timeout = 4000)
    public void copy_preservesNullReturnAliases() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        matchStatement.setReturnAliases(((List<OIdentifier>) (null)));
        OMatchStatement copiedStatement = matchStatement.copy();
        assertFalse(copiedStatement.isReturnDistinct());
        assertTrue(copiedStatement.equals(((Object) (matchStatement))));
    }

    @Test(timeout = 4000)
    public void copy_preservesNullReturnItems() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        matchStatement.addReturnItem(((OExpression) (null)));
        OMatchStatement copiedStatement = matchStatement.copy();
        assertFalse(copiedStatement.isReturnDistinct());
        assertTrue(copiedStatement.equals(((Object) (matchStatement))));
    }

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
    public void copy_preservesNullReturnItems_whenSet() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement(0);
        matchStatement.setReturnItems(((List<OExpression>) (null)));
        OMatchStatement copiedStatement = matchStatement.copy();
        assertTrue(copiedStatement.equals(((Object) (matchStatement))));
        assertFalse(copiedStatement.isReturnDistinct());
    }

    @Test(timeout = 4000)
    public void copy_preservesNotMatchExpressions() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        OMatchExpression matchExpression = new OMatchExpression(-3607);
        matchStatement.addNotMatchExpression(matchExpression);
        OMatchStatement copiedStatement = matchStatement.copy();
        assertFalse(copiedStatement.isReturnDistinct());
        assertTrue(copiedStatement.equals(((Object) (matchStatement))));
    }

    @Test(timeout = 4000)
    public void copy_handlesNullNotMatchExpressions() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        matchStatement.notMatchExpressions = null;
        OMatchStatement copiedStatement = matchStatement.copy();
        assertTrue(copiedStatement.equals(((Object) (matchStatement))));
        assertFalse(copiedStatement.isReturnDistinct());
    }

    @Test(timeout = 4000)
    public void copy_throwsNPE_whenMatchExpressionHasNullOrigin() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        OMatchExpression matchExpression = new OMatchExpression(-1452647753);
        matchStatement.addMatchExpression(matchExpression);
        try {
            matchStatement.copy();
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            verifyException("com.orientechnologies.orient.core.sql.parser.OMatchStatement", e);
        }
    }

    @Test(timeout = 4000)
    public void copy_throwsNPE_whenMatchExpressionsListNull() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        matchStatement.setMatchExpressions(((List<OMatchExpression>) (null)));
        try {
            matchStatement.copy();
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            verifyException("com.orientechnologies.orient.core.sql.parser.OMatchStatement", e);
        }
    }

    @Test(timeout = 4000)
    public void toGenericStatement_withTwoReturnItems_usesPlaceholders() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        OArrayConcatExpressionElement arrayConcatElement = new OArrayConcatExpressionElement(900);
        matchStatement.addReturnItem(arrayConcatElement);
        matchStatement.addReturnItem(arrayConcatElement);
        String generic = matchStatement.toGenericStatement();
        assertEquals("MATCH  RETURN ?, ?", generic);
    }

    @Test(timeout = 4000)
    public void toGenericStatement_reflectsDistinctFlag() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        assertFalse(matchStatement.isReturnDistinct());
        matchStatement.setReturnDistinct(true);
        matchStatement.toGenericStatement();
        assertTrue(matchStatement.isReturnDistinct());
    }

    @Test(timeout = 4000)
    public void toGenericStatement_throwsNPE_whenMatchExpressionNull() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        matchStatement.addMatchExpression(((OMatchExpression) (null)));
        try {
            matchStatement.toGenericStatement();
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            verifyException("com.orientechnologies.orient.core.sql.parser.OMatchStatement", e);
        }
    }

    @Test(timeout = 4000)
    public void toGenericStatement_appendsToProvidedBuilder() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        StringBuilder builder = new StringBuilder("$matches");
        matchStatement.toGenericStatement(builder);
        assertEquals("$matchesMATCH  RETURN ", builder.toString());
    }

    @Test(timeout = 4000)
    public void toString_withAliasProjection() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        AggregateProjectionSplit split = new AggregateProjectionSplit();
        OIdentifier aliasIdentifier = split.getNextAlias();
        matchStatement.addReturnAlias(aliasIdentifier);
        OExpression expression = new OExpression(aliasIdentifier);
        matchStatement.addReturnItem(expression);
        String output = matchStatement.toString();
        assertEquals("MATCH  RETURN _$$$OALIAS$$_0 AS _$$$OALIAS$$_0", output);
    }

    @Test(timeout = 4000)
    public void toString_withNullNestedProjection() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        matchStatement.addReturnNestedProjection(((ONestedProjection) (null)));
        OArrayConcatExpressionElement arrayConcatElement = new OArrayConcatExpressionElement(1);
        matchStatement.addReturnItem(arrayConcatElement);
        String output = matchStatement.toString();
        assertEquals("MATCH  RETURN null", output);
    }

    @Test(timeout = 4000)
    public void toString_returnsNullWhenReturnNestedProjectionsNull() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        matchStatement.returnNestedProjections = null;
        OArrayConcatExpressionElement arrayConcatElement = new OArrayConcatExpressionElement(1400);
        matchStatement.addReturnItem(arrayConcatElement);
        String output = matchStatement.toString();
        assertEquals("MATCH  RETURN null", output);
    }

    @Test(timeout = 4000)
    public void toString_withTwoNullReturnItems() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        OArrayConcatExpressionElement arrayConcatElement = new OArrayConcatExpressionElement(404);
        matchStatement.addReturnItem(arrayConcatElement);
        matchStatement.addReturnItem(arrayConcatElement);
        String output = matchStatement.toString();
        assertEquals("MATCH  RETURN null, null", output);
    }

    @Test(timeout = 4000)
    public void toString_throwsNPE_whenMatchExpressionNull() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        matchStatement.addMatchExpression(((OMatchExpression) (null)));
        try {
            matchStatement.toString();
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            verifyException("com.orientechnologies.orient.core.sql.parser.OMatchStatement", e);
        }
    }

    @Test(timeout = 4000)
    public void toString_appendsToProvidedBuilder_withMap() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement(-2068473975);
        TreeMap<Object, Object> map = new TreeMap<Object, Object>();
        StringBuilder builder = new StringBuilder("EVKgf ?\"\";vS[x[");
        matchStatement.toString(((Map<Object, Object>) (map)), builder);
        assertEquals("EVKgf ?\"\";vS[x[MATCH  RETURN ", builder.toString());
    }

    @Test(timeout = 4000)
    public void returnsPaths_falseWithArrayConcatElement() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        OArrayConcatExpressionElement arrayConcatElement = new OArrayConcatExpressionElement(900);
        matchStatement.addReturnItem(arrayConcatElement);
        boolean returnsPaths = matchStatement.returnsPaths();
        assertFalse(returnsPaths);
        assertFalse(matchStatement.isReturnDistinct());
    }

    @Test(timeout = 4000)
    public void returnsPatterns_falseWithArrayConcatElement() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        OArrayConcatExpressionElement arrayConcatElement = new OArrayConcatExpressionElement(900);
        matchStatement.addReturnItem(arrayConcatElement);
        boolean returnsPatterns = matchStatement.returnsPatterns();
        assertFalse(returnsPatterns);
        assertFalse(matchStatement.isReturnDistinct());
    }

    @Test(timeout = 4000)
    public void returnsElements_falseWithArrayConcatElement() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        OArrayConcatExpressionElement arrayConcatElement = new OArrayConcatExpressionElement(31);
        matchStatement.addReturnItem(arrayConcatElement);
        boolean returnsElements = matchStatement.returnsElements();
        assertFalse(returnsElements);
        assertFalse(matchStatement.isReturnDistinct());
    }

    @Test(timeout = 4000)
    public void returnsPathElements_falseWithArrayConcatElement() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        OArrayConcatExpressionElement arrayConcatElement = new OArrayConcatExpressionElement(900);
        matchStatement.addReturnItem(arrayConcatElement);
        boolean returnsPathElements = matchStatement.returnsPathElements();
        assertFalse(matchStatement.isReturnDistinct());
        assertFalse(returnsPathElements);
    }

    @Test(timeout = 4000)
    public void matchContext_copy_createsNewInstance() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        OMatchStatement.MatchContext matchContext = matchStatement.new MatchContext();
        ORecordId recordId = new ORecordId(1613, 1613);
        OMatchStatement.MatchContext matchContextCopy = matchContext.copy("@,l>", recordId);
        assertFalse(matchStatement.isReturnDistinct());
        assertNotSame(matchContextCopy, matchContext);
    }

    @Test(timeout = 4000)
    public void matchContext_toDoc_throwsNoClassDefFoundError() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        OMatchStatement.MatchContext matchContext = matchStatement.new MatchContext();
        try {
            matchContext.toDoc();
            fail("Expecting exception: NoClassDefFoundError");
        } catch (NoClassDefFoundError e) {
            verifyException("com.orientechnologies.orient.core.db.document.ODatabaseDocumentAbstract", e);
        }
    }

    @Test(timeout = 4000)
    public void copy_preservesNullNotMatchExpression() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        matchStatement.addNotMatchExpression(((OMatchExpression) (null)));
        OMatchStatement copiedStatement = matchStatement.copy();
        assertTrue(copiedStatement.equals(((Object) (matchStatement))));
        assertFalse(copiedStatement.isReturnDistinct());
    }

    @Test(timeout = 4000)
    public void toGenericStatement_includesGroupBy() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        OGroupBy groupBy = new OGroupBy(963);
        matchStatement.setGroupBy(groupBy);
        StringBuilder builder = new StringBuilder("$matches");
        matchStatement.toGenericStatement(builder);
        assertEquals("$matchesMATCH  RETURN  GROUP BY ", builder.toString());
    }

    @Test(timeout = 4000)
    public void toString_reflectsDistinctFlag() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        assertFalse(matchStatement.isReturnDistinct());
        matchStatement.setReturnDistinct(true);
        matchStatement.toString();
        assertTrue(matchStatement.isReturnDistinct());
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
    public void isIdempotent_returnsTrue() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        boolean idempotent = matchStatement.isIdempotent();
        assertTrue(idempotent);
        assertFalse(matchStatement.isReturnDistinct());
    }

    @Test(timeout = 4000)
    public void toString_includesSkip() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        OSkip skip = new OSkip(-4162);
        matchStatement.setSkip(skip);
        String output = matchStatement.toString();
        assertEquals("MATCH  RETURN  ", output);
    }

    @Test(timeout = 4000)
    public void toString_withNullAliasAndExpression() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        matchStatement.addReturnAlias(((OIdentifier) (null)));
        OExpression expression = new OExpression(((OIdentifier) (null)));
        matchStatement.addReturnItem(expression);
        String output = matchStatement.toString();
        assertEquals("MATCH  RETURN ", output);
    }

    @Test(timeout = 4000)
    public void toString_withNestedProjectionAndArrayConcat() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        ONestedProjection nestedProjection = new ONestedProjection(130);
        matchStatement.addReturnNestedProjection(nestedProjection);
        OArrayConcatExpressionElement arrayConcatElement = new OArrayConcatExpressionElement(130);
        matchStatement.addReturnItem(arrayConcatElement);
        String output = matchStatement.toString();
        assertEquals("MATCH  RETURN null:{}", output);
    }

    @Test(timeout = 4000)
    public void refersToParent_returnsFalse() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        boolean refers = matchStatement.refersToParent();
        assertFalse(matchStatement.isReturnDistinct());
        assertFalse(refers);
    }

    @Test(timeout = 4000)
    public void constructor_withOrientSqlAndId() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement(((OrientSql) (null)), 1);
        assertFalse(matchStatement.isReturnDistinct());
    }

    @Test(timeout = 4000)
    public void setNotMatchExpressions_acceptsVector() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        Vector<OMatchExpression> vector = new Vector<OMatchExpression>();
        matchStatement.setNotMatchExpressions(vector);
        assertFalse(matchStatement.isReturnDistinct());
    }

    @Test(timeout = 4000)
    public void toString_appendsToProvidedBuilder_withOrderBy() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement(-2068473975);
        OOrderBy orderBy = new OOrderBy();
        matchStatement.setOrderBy(orderBy);
        TreeMap<Object, Object> map = new TreeMap<Object, Object>();
        StringBuilder builder = new StringBuilder("EVKgf ?\"\";vS[x[");
        matchStatement.toString(((Map<Object, Object>) (map)), builder);
        assertEquals("EVKgf ?\"\";vS[x[MATCH  RETURN  ", builder.toString());
        assertFalse(matchStatement.isReturnDistinct());
    }

    @Test(timeout = 4000)
    public void setLimit_acceptsNull() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        matchStatement.setLimit(((OLimit) (null)));
        assertFalse(matchStatement.isReturnDistinct());
    }

    @Test(timeout = 4000)
    public void copy_throwsNPE_whenMatchExpressionNull() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement();
        matchStatement.addMatchExpression(((OMatchExpression) (null)));
        try {
            matchStatement.copy();
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            verifyException("com.orientechnologies.orient.core.sql.parser.OMatchStatement", e);
        }
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

    @Test(timeout = 4000)
    public void getNotMatchExpressions_returnsEmptyForNewInstance() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement(-2068473975);
        matchStatement.getNotMatchExpressions();
        assertFalse(matchStatement.isReturnDistinct());
    }

    @Test(timeout = 4000)
    public void getMatchExpressions_returnsEmptyForNewInstance() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement(-2068473975);
        matchStatement.getMatchExpressions();
        assertFalse(matchStatement.isReturnDistinct());
    }

    @Test(timeout = 4000)
    public void getGroupBy_returnsNullByDefault() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement(-2068473975);
        matchStatement.getGroupBy();
        assertFalse(matchStatement.isReturnDistinct());
    }

    @Test(timeout = 4000)
    public void getAndSetReturnAliases_withEmptyList() throws Throwable {
        OMatchStatement matchStatement = new OMatchStatement(-2068473975);
        List<OIdentifier> aliases = matchStatement.getReturnAliases();
        matchStatement.setReturnAliases(aliases);
        assertFalse(matchStatement.isReturnDistinct());
    }

    // Adapted and merged parser syntax tests from IGT

    private SimpleNode checkRightSyntax(String query) {
        SimpleNode result = checkSyntax(query, true);
        StringBuilder builder = new StringBuilder();
        result.toString(null, builder);
        return checkSyntax(builder.toString(), true);
    }

    private SimpleNode checkWrongSyntax(String query) {
        return checkSyntax(query, false);
    }

    private SimpleNode checkSyntax(String query, boolean isCorrect) {
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

    private OrientSql getParserFor(String string) {
        InputStream is = new ByteArrayInputStream(string.getBytes());
        return new OrientSql(is);
    }

    @Test(timeout = 4000)
    public void testWrongFilterKey() {
        checkWrongSyntax("MATCH {clasx: 'V'} RETURN foo");
    }

    @Test(timeout = 4000)
    public void testBasicMatch() {
        checkRightSyntax("MATCH {class: 'V', as: foo} RETURN foo");
    }

    @Test(timeout = 4000)
    public void testNoReturn() {
        checkWrongSyntax("MATCH {class: 'V', as: foo}");
    }

    @Test(timeout = 4000)
    public void testSingleMethod() {
        checkRightSyntax("MATCH {class: 'V', as: foo}.out() RETURN foo");
    }

    @Test(timeout = 4000)
    public void testArrowsNoBrackets() {
        checkWrongSyntax("MATCH {}-->-->{as:foo} RETURN foo");
    }

    @Test(timeout = 4000)
    public void testSingleMethodAndFilter() {
        checkRightSyntax("MATCH {class: 'V', as: foo}.out(){class: 'V', as: bar} RETURN foo");
        checkRightSyntax("MATCH {class: 'V', as: foo}-E->{class: 'V', as: bar} RETURN foo");
        checkRightSyntax("MATCH {class: 'V', as: foo}-->{class: 'V', as: bar} RETURN foo");
    }

    @Test(timeout = 4000)
    public void testLongPath() {
        checkRightSyntax(
            "MATCH {class: 'V', as: foo}.out().in('foo').both('bar').out(){as: bar} RETURN foo");
        checkRightSyntax("MATCH {class: 'V', as: foo}-->{}<-foo-{}-bar-{}-->{as: bar} RETURN foo");
    }

    @Test(timeout = 4000)
    public void testLongPath2() {
        checkRightSyntax(
            "MATCH {class: 'V', as: foo}.out().in('foo'){}.both('bar'){CLASS: 'bar'}.out(){as: bar} RETURN foo");
    }

    @Test(timeout = 4000)
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

    @Test(timeout = 4000)
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

    @Test(timeout = 4000)
    public void testMultiPath() {
        StringBuilder query = new StringBuilder();
        query.append("MATCH {}");
        query.append("  .(out().in(){class:'v'}.both('Foo')){maxDepth: 3}.out() return foo");
        checkRightSyntax(query.toString());
    }

    @Test(timeout = 4000)
    public void testMultiPathArrows() {
        StringBuilder query = new StringBuilder();
        query.append("MATCH {}");
        query.append("  .(-->{}<--{class:'v'}--){maxDepth: 3}-->{} return foo");
        checkRightSyntax(query.toString());
    }

    @Test(timeout = 4000)
    public void testMultipleMatches() {
        String query = "MATCH {class: 'V', as: foo}.out(){class: 'V', as: bar}, ";
        query += " {class: 'V', as: foo}.out(){class: 'V', as: bar},";
        query += " {class: 'V', as: foo}.out(){class: 'V', as: bar} RETURN foo";
        checkRightSyntax(query);
    }

    @Test(timeout = 4000)
    public void testMultipleMatchesArrow() {
        String query = "MATCH {class: 'V', as: foo}-->{class: 'V', as: bar}, ";
        query += " {class: 'V', as: foo}-->{class: 'V', as: bar},";
        query += " {class: 'V', as: foo}-->{class: 'V', as: bar} RETURN foo";
        checkRightSyntax(query);
    }

    @Test(timeout = 4000)
    public void testWhile() {
        checkRightSyntax("MATCH {class: 'V', as: foo}.out(){while:($depth<4), as:bar} RETURN bar ");
    }

    @Test(timeout = 4000)
    public void testWhileArrow() {
        checkRightSyntax("MATCH {class: 'V', as: foo}-->{while:($depth<4), as:bar} RETURN bar ");
    }

    @Test(timeout = 4000)
    public void testLimit() {
        checkRightSyntax("MATCH {class: 'V'} RETURN foo limit 10");
    }

    @Test(timeout = 4000)
    public void testReturnJson() {
        checkRightSyntax("MATCH {class: 'V'} RETURN {'name':'foo', 'value': bar}");
    }

    @Test(timeout = 4000)
    public void testOptional() {
        checkRightSyntax(
            "MATCH {class: 'V', as: foo}-->{}<-foo-{}-bar-{}-->{as: bar, optional:true} RETURN foo");
        checkRightSyntax(
            "MATCH {class: 'V', as: foo}-->{}<-foo-{}-bar-{}-->{as: bar, optional:false} RETURN foo");
    }

    @Test(timeout = 4000)
    public void testOrderBy() {
        checkRightSyntax("MATCH {class: 'V', as: foo}-->{} RETURN foo ORDER BY foo");
        checkRightSyntax("MATCH {class: 'V', as: foo}-->{} RETURN foo ORDER BY foo limit 10");
    }

    @Test(timeout = 4000)
    public void testNestedProjections() {
        checkRightSyntax("MATCH {class: 'V', as: foo}-->{} RETURN foo:{name, surname}");
        checkRightSyntax(
            "MATCH {class: 'V', as: foo}-->{as:bar} RETURN foo:{name, surname} as bloo, bar:{*}");
    }

    @Test(timeout = 4000)
    public void testUnwind() {
        checkRightSyntax(
            "MATCH {class: 'V', as: foo}-->{as:bar} RETURN foo.name, bar.name as x unwind x");
    }

    @Test(timeout = 4000)
    public void testDepthAlias() {
        checkRightSyntax(
            "MATCH {class: 'V', as: foo}-->{as:bar, while:($depth < 2), depthAlias: depth} RETURN depth");
    }

    @Test(timeout = 4000)
    public void testPathAlias() {
        checkRightSyntax(
            "MATCH {class: 'V', as: foo}-->{as:bar, while:($depth < 2), pathAlias: barPath} RETURN barPath");
    }

    @Test(timeout = 4000)
    public void testClusterTarget() {
        checkRightSyntax("MATCH {cluster:v, as: foo} RETURN $elements");
        checkRightSyntax("MATCH {cluster:12, as: foo} RETURN $elements");
        checkRightSyntax("MATCH {cluster: v, as: foo} RETURN $elements");
        checkRightSyntax("MATCH {cluster: `v`, as: foo} RETURN $elements");
        checkRightSyntax("MATCH {cluster:`v`, as: foo} RETURN $elements");
        checkRightSyntax("MATCH {cluster: 12, as: foo} RETURN $elements");
        checkWrongSyntax("MATCH {cluster: 12.1, as: foo} RETURN $elements");
    }

    @Test(timeout = 4000)
    public void testNot() {
        checkRightSyntax("MATCH {cluster:v, as: foo}, NOT {as:foo}-->{as:bar} RETURN $elements");
    }

    @Test(timeout = 4000)
    public void testSkip() {
        checkRightSyntax(
            "MATCH {class: 'V', as: foo}-->{as:bar} RETURN foo.name, bar.name skip 10 limit 10");
    }

    @Test(timeout = 4000)
    public void testFieldTraversal() {
        checkRightSyntax(
            "MATCH {class: 'V', as: foo}.toBar{as:bar} RETURN foo.name, bar.name skip 10 limit 10");
        checkRightSyntax(
            "MATCH {class: 'V', as: foo}.toBar{as:bar}.out(){as:c} RETURN foo.name, bar.name skip 10 limit 10");
        checkRightSyntax(
            "MATCH {class: 'V', as: foo}.toBar.baz{as:bar} RETURN foo.name, bar.name skip 10 limit 10");
        checkRightSyntax(
            "MATCH {class: 'V', as: foo}.toBar.out(){as:bar} RETURN foo.name, bar.name skip 10 limit 10");
    }
}