package com.orientechnologies.orient.core.record.impl;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.TreeMap;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
public class ODocument_ESTest_Adopted_Agentic_Top5 {
    @Test
    public void testCopyToCopiesEmptyFieldsTypesAndOwners() throws Exception {
        ODocument doc1 = new ODocument();
        ODocument doc2 = new ODocument().field("integer2", 123).field("string", "OrientDB").field("a", 123.3).setFieldType("integer", OType.INTEGER).setFieldType("string", OType.STRING).setFieldType("binary", OType.BINARY);
        ODocumentInternal.addOwner(doc2, new ODocument());
        assertEquals(doc2.<Object>field("integer2"), 123);
        assertEquals(doc2.field("string"), "OrientDB");
        // assertEquals(doc2.field("a"), 123.3);
        Assertions.assertThat(doc2.<Double>field("a")).isEqualTo(123.3);
        assertEquals(doc2.fieldType("integer"), OType.INTEGER);
        assertEquals(doc2.fieldType("string"), OType.STRING);
        assertEquals(doc2.fieldType("binary"), OType.BINARY);
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

    @Test
    public void validateLinkCollection_withNullProperty_throwsNPE() {
        TreeMap<LinkedHashMap<Object, Object>, Object> treeMap = new TreeMap<LinkedHashMap<Object, Object>, Object>();
        Collection<Object> values = treeMap.values();
        ODocumentEntry entry = new ODocumentEntry();
        ODocumentEntry clonedEntry = entry.clone();
        try {
            ODocument.validateLinkCollection(((OProperty) (null)), values, clonedEntry);
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

    @Test
    public void createDocumentFromByteArray_throwsNoClassDefFoundError() {
        byte[] content = new byte[2];
        try {
            new ODocument(content);
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

    @Test
    public void validateEmbedded_withNullPropertyAndNonNullValue_throwsNPE() {
        Object nonNullValue = new Object();
        try {
            ODocument.validateEmbedded(((OProperty) (null)), nonNullValue);
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