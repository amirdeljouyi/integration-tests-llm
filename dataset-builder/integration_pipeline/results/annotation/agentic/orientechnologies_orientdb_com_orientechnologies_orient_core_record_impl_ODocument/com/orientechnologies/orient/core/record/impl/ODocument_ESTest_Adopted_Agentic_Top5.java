package com.orientechnologies.orient.core.record.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ODocument_ESTest_Adopted_Agentic_Top5 {
    // private static final String dbName = ODocumentTest.class.getSimpleName();
    private static final String defaultDbAdminCredentials = "admin";

    @org.junit.Test

    /**
     * This test added target-class coverage 1.24% for com.orientechnologies.orient.core.record.impl.ODocument (21/1700 lines).
     * Delta details: +2 methods, +5 branches, +117 instructions.
     * Full version of the covered block is here: <a href="https://github.com/orientechnologies/orientdb/blob/20ac246eb62a89d78d784974de0fd2a36752b2d8/core/src/main/java/com/orientechnologies/orient/core/record/impl/ODocument.java#L1156-L1159">ODocument.java (lines 1156-1159)</a>
     * Covered Lines:
     * <pre><code>
     *     if (fields != null) {
     *       destination.fields =
     *           fields instanceof LinkedHashMap ? new LinkedHashMap&lt;&gt;() : new HashMap&lt;&gt;();
     *       for (Entry&lt;String, ODocumentEntry&gt; entry : fields.entrySet()) {
     * </code></pre>
     * Other newly covered ranges to check: 1141;1143;1145;1147;1149-1151;1153-1154;1163;1165-1166;1168-1169;1171;3596;3599
     */
    public void testCopyToCopiesEmptyFieldsTypesAndOwners() throws Exception {
        com.orientechnologies.orient.core.record.impl.ODocument doc1 = new com.orientechnologies.orient.core.record.impl.ODocument();
        com.orientechnologies.orient.core.record.impl.ODocument doc2 = new com.orientechnologies.orient.core.record.impl.ODocument().field("integer2", 123).field("string", "OrientDB").field("a", 123.3).setFieldType("integer", com.orientechnologies.orient.core.metadata.schema.OType.INTEGER).setFieldType("string", com.orientechnologies.orient.core.metadata.schema.OType.STRING).setFieldType("binary", com.orientechnologies.orient.core.metadata.schema.OType.BINARY);
        com.orientechnologies.orient.core.record.impl.ODocumentInternal.addOwner(doc2, new com.orientechnologies.orient.core.record.impl.ODocument());
        assertEquals(doc2.<Object>field("integer2"), 123);
        assertEquals(doc2.field("string"), "OrientDB");
        // assertEquals(doc2.field("a"), 123.3);
        org.assertj.core.api.Assertions.assertThat(doc2.<Double>field("a")).isEqualTo(123.3);
        assertEquals(doc2.fieldType("integer"), com.orientechnologies.orient.core.metadata.schema.OType.INTEGER);
        assertEquals(doc2.fieldType("string"), com.orientechnologies.orient.core.metadata.schema.OType.STRING);
        assertEquals(doc2.fieldType("binary"), com.orientechnologies.orient.core.metadata.schema.OType.BINARY);
        assertNotNull(doc2.getOwner());
        doc1.copyTo(doc2);
        assertEquals(doc2.<Object>field("integer2"), null);
        assertEquals(doc2.<Object>field("string"), null);
        assertEquals(doc2.<Object>field("a"), null);
        assertEquals(doc2.fieldType("integer"), null);
        assertEquals(doc2.fieldType("string"), null);
        assertEquals(doc2.fieldType("binary"), null);
        assertNull(doc2.getOwner());
    }

    /**
     * This test added target-class coverage 0.00% for com.orientechnologies.orient.core.record.impl.ODocument (0/1700 lines).
     * Delta details: +1 methods, +0 branches, +42 instructions.
     */
    @org.junit.Test
    public void validateLinkCollection_withNullProperty_throwsNPE() {
        java.util.TreeMap<java.util.LinkedHashMap<Object, Object>, Object> treeMap = new java.util.TreeMap<java.util.LinkedHashMap<Object, Object>, Object>();
        java.util.Collection<Object> values = treeMap.values();
        com.orientechnologies.orient.core.record.impl.ODocumentEntry entry = new com.orientechnologies.orient.core.record.impl.ODocumentEntry();
        com.orientechnologies.orient.core.record.impl.ODocumentEntry clonedEntry = entry.clone();
        try {
            com.orientechnologies.orient.core.record.impl.ODocument.validateLinkCollection(((com.orientechnologies.orient.core.metadata.schema.OProperty) (null)), values, clonedEntry);
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            boolean found = false;
            for (StackTraceElement element : e.getStackTrace()) {
                if ("com.orientechnologies.orient.core.record.impl.ODocument".equals(element.getClassName())) {
                    found = true;
                    break;
                }
            }
            assertTrue(found);
        }
    }

    /**
     * This test added target-class coverage 0.24% for com.orientechnologies.orient.core.record.impl.ODocument (4/1700 lines).
     * Delta details: +2 methods, +1 branches, +30 instructions.
     * Full version of the covered block is here: <a href="https://github.com/orientechnologies/orientdb/blob/20ac246eb62a89d78d784974de0fd2a36752b2d8/core/src/main/java/com/orientechnologies/orient/core/record/impl/ODocument.java#L168-L171">ODocument.java (lines 168-171)</a>
     * Covered Lines:
     * <pre><code>
     *   public ODocument(final byte[] iSource) {
     *     source = iSource;
     *     setup(ODatabaseRecordThreadLocal.instance().getIfDefined());
     *   }
     * </code></pre>
     */
    @org.junit.Test
    public void createDocumentFromByteArray_throwsNoClassDefFoundError() {
        byte[] content = new byte[2];
        try {
            new com.orientechnologies.orient.core.record.impl.ODocument(content);
            fail("Expecting exception: NoClassDefFoundError");
        } catch (NoClassDefFoundError e) {
            boolean found = false;
            for (StackTraceElement element : e.getStackTrace()) {
                if ("com.orientechnologies.orient.core.db.document.ODatabaseDocumentAbstract".equals(element.getClassName())) {
                    found = true;
                    break;
                }
            }
            assertTrue(found);
        }
    }

    /**
     * This test added target-class coverage 0.24% for com.orientechnologies.orient.core.record.impl.ODocument (4/1700 lines).
     * Delta details: +2 methods, +3 branches, +9 instructions.
     * Full version of the covered block is here: <a href="https://github.com/orientechnologies/orientdb/blob/20ac246eb62a89d78d784974de0fd2a36752b2d8/core/src/main/java/com/orientechnologies/orient/core/record/impl/ODocument.java#L1028-L1029">ODocument.java (lines 1028-1029)</a>
     * Covered Lines:
     * <pre><code>
     *     if (fieldValue == null) return;
     *     if (fieldValue instanceof ORecordId)
     * </code></pre>
     * Other newly covered ranges to check: 1037;1119
     */
    @org.junit.Test
    public void validateEmbedded_withNullPropertyAndNonNullValue_throwsNPE() {
        Object nonNullValue = new Object();
        try {
            com.orientechnologies.orient.core.record.impl.ODocument.validateEmbedded(((com.orientechnologies.orient.core.metadata.schema.OProperty) (null)), nonNullValue);
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            boolean found = false;
            for (StackTraceElement element : e.getStackTrace()) {
                if ("com.orientechnologies.orient.core.record.impl.ODocument".equals(element.getClassName())) {
                    found = true;
                    break;
                }
            }
            assertTrue(found);
        }
    }
}
