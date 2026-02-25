package com.orientechnologies.orient.core.record.impl;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentAbstract;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.TreeMap;
import org.junit.Test;
import static org.evosuite.runtime.EvoAssertions.verifyException;
import static org.junit.Assert.fail;
public class ODocument_ESTest_Adopted_Top5 {
    @Test(timeout = 4000)
    public void validateLinkCollection_withNullProperty_throwsNPE() throws Throwable {
        TreeMap<LinkedHashMap<Object, Object>, Object> treeMap = new TreeMap<LinkedHashMap<Object, Object>, Object>();
        Collection<Object> values = treeMap.values();
        com.orientechnologies.orient.core.record.impl.ODocumentEntry entry = new com.orientechnologies.orient.core.record.impl.ODocumentEntry();
        com.orientechnologies.orient.core.record.impl.ODocumentEntry clonedEntry = entry.clone();
        // Undeclared exception!
        try {
            com.orientechnologies.orient.core.record.impl.ODocument.validateLinkCollection(((OProperty) (null)), values, clonedEntry);
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            // 
            // Cannot invoke \"OProperty.getLinkedClass()\" because \"property\" is null
            // 
            verifyException("com.orientechnologies.orient.core.record.impl.ODocument", e);
        }
    }

    @Test(timeout = 4000)
    public void createDocumentFromDataInputStream_throwsNoClassDefFoundError() throws Throwable {
        byte[] buffer = new byte[8];
        buffer[7] = ((byte) (59));
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buffer);
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
        com.orientechnologies.orient.core.record.impl.ODocument document = null;
        try {
            document = new com.orientechnologies.orient.core.record.impl.ODocument(dataInputStream);
            fail("Expecting exception: NoClassDefFoundError");
        } catch (NoClassDefFoundError e) {
            // 
            // Could not initialize class com.orientechnologies.orient.core.serialization.serializer.record.ORecordSerializerFactory
            // 
            verifyException("ODatabaseDocumentAbstract", e);
        }
    }

    @Test(timeout = 4000)
    public void createDocumentFromByteArray_throwsNoClassDefFoundError() throws Throwable {
        byte[] content = new byte[2];
        com.orientechnologies.orient.core.record.impl.ODocument document = null;
        try {
            document = new com.orientechnologies.orient.core.record.impl.ODocument(content);
            fail("Expecting exception: NoClassDefFoundError");
        } catch (NoClassDefFoundError e) {
            // 
            // Could not initialize class com.orientechnologies.orient.core.serialization.serializer.record.ORecordSerializerFactory
            // 
            verifyException("ODatabaseDocumentAbstract", e);
        }
    }

    @Test(timeout = 4000)
    public void validateEmbedded_withNullPropertyAndNonNullValue_throwsNPE() throws Throwable {
        Object nonNullValue = new Object();
        // Undeclared exception!
        try {
            com.orientechnologies.orient.core.record.impl.ODocument.validateEmbedded(((OProperty) (null)), nonNullValue);
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            // 
            // Cannot invoke \"OProperty.getFullName()\" because \"p\" is null
            // 
            verifyException("com.orientechnologies.orient.core.record.impl.ODocument", e);
        }
    }
}
