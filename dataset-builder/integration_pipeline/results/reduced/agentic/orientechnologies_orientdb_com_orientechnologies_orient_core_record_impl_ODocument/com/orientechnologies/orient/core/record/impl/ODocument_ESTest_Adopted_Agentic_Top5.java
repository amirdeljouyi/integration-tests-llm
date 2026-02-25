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
     * This test added coverage 59.62% (31/52 added lines among kept tests).
     * Delta details: +2 methods, +5 branches, +117 instructions.
     * Full version of the covered block is here: <a href="https://github.com/orientechnologies/orientdb/blob/main/core/src/main/java/com/orientechnologies/orient/core/record/impl/ODocument.java#L1141-L1141">ODocument.java (lines 1141-1141)</a>
     * Covered Lines:
     * <pre><code>
     * <span style="background-color:#fff3b0;">    checkForFields();</span>
     * </code></pre>
     * Full version of the covered block is here: <a href="https://github.com/orientechnologies/orientdb/blob/main/core/src/main/java/com/orientechnologies/orient/core/record/ORecordAbstract.java#L356-L365">ORecordAbstract.java (lines 356-365)</a>
     * Covered Lines:
     * <pre><code>
     * <span style="background-color:#fff3b0;">    cloned.source = source;</span>
     * <span style="background-color:#fff3b0;">    cloned.size = size;</span>
     * <span style="background-color:#fff3b0;">    cloned.recordId = recordId.copy();</span>
     * <span style="background-color:#fff3b0;">    cloned.recordVersion = recordVersion;</span>
     * <span style="background-color:#fff3b0;">    cloned.status = status;</span>
     * <span style="background-color:#fff3b0;">    cloned.recordFormat = recordFormat;</span>
     * <span style="background-color:#fff3b0;">    cloned.dirty = false;</span>
     * <span style="background-color:#fff3b0;">    cloned.contentChanged = false;</span>
     * <span style="background-color:#fff3b0;">    cloned.dirtyManager = null;</span>
     * <span style="background-color:#fff3b0;">    return cloned;</span>
     * </code></pre>
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
