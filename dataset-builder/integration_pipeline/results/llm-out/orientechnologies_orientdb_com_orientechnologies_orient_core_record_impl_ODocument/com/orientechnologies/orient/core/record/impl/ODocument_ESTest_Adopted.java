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
public class ODocument_ESTest_Adopted {
    @Test(timeout = 4000)
    public void createDocumentFromClassNameString_throwsNoClassDefFoundError() throws Throwable {
        ODocument document = null;
        try {
            document = new ODocument("z[' J~J");
            fail("Expecting exception: NoClassDefFoundError");
        } catch (NoClassDefFoundError e) {
            // 
            // Could not initialize class com.orientechnologies.orient.core.serialization.serializer.record.ORecordSerializerFactory
            // 
            verifyException("com.orientechnologies.orient.core.db.document.ODatabaseDocumentAbstract", e);
        }
    }

    @Test(timeout = 4000)
    public void validateLinkCollection_withNullProperty_throwsNPE() throws Throwable {
        TreeMap<LinkedHashMap<Object, Object>, Object> treeMap = new TreeMap<LinkedHashMap<Object, Object>, Object>();
        Collection<Object> values = treeMap.values();
        ODocumentEntry entry = new ODocumentEntry();
        ODocumentEntry clonedEntry = entry.clone();
        // Undeclared exception!
        try {
            ODocument.validateLinkCollection(((OProperty) (null)), values, clonedEntry);
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            // 
            // Cannot invoke \"com.orientechnologies.orient.core.metadata.schema.OProperty.getLinkedClass()\" because \"property\" is null
            // 
            verifyException("com.orientechnologies.orient.core.record.impl.ODocument", e);
        }
    }

    @Test(timeout = 4000)
    public void createDocumentFromMap_throwsNoClassDefFoundError() throws Throwable {
        Object unusedObject = new Object();
        TreeMap<ODocument, Object> documentToObjectMap = new TreeMap<ODocument, Object>();
        ODocument document = null;
        try {
            document = new ODocument(documentToObjectMap);
            fail("Expecting exception: NoClassDefFoundError");
        } catch (NoClassDefFoundError e) {
            // 
            // Could not initialize class com.orientechnologies.orient.core.serialization.serializer.record.ORecordSerializerFactory
            // 
            verifyException("com.orientechnologies.orient.core.db.document.ODatabaseDocumentAbstract", e);
        }
    }

    @Test(timeout = 4000)
    public void createDocumentFromDataInputStream_throwsNoClassDefFoundError() throws Throwable {
        byte[] buffer = new byte[8];
        buffer[7] = ((byte) (59));
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buffer);
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
        ODocument document = null;
        try {
            document = new ODocument(dataInputStream);
            fail("Expecting exception: NoClassDefFoundError");
        } catch (NoClassDefFoundError e) {
            // 
            // Could not initialize class com.orientechnologies.orient.core.serialization.serializer.record.ORecordSerializerFactory
            // 
            verifyException("com.orientechnologies.orient.core.db.document.ODatabaseDocumentAbstract", e);
        }
    }

    @Test(timeout = 4000)
    public void createDocumentFromObjectArray_throwsNoClassDefFoundError() throws Throwable {
        TreeMap<ODocument, Object> documentMap = new TreeMap<ODocument, Object>();
        Object[] values = new Object[6];
        values[5] = ((Object) (documentMap));
        ODocument document = null;
        try {
            document = new ODocument(values);
            fail("Expecting exception: NoClassDefFoundError");
        } catch (NoClassDefFoundError e) {
            // 
            // Could not initialize class com.orientechnologies.orient.core.serialization.serializer.record.ORecordSerializerFactory
            // 
            verifyException("com.orientechnologies.orient.core.db.document.ODatabaseDocumentAbstract", e);
        }
    }

    @Test(timeout = 4000)
    public void validateLink_withNullPropertyAndNullValueAndAllowNullFalse_throwsNPE() throws Throwable {
        // Undeclared exception!
        try {
            ODocument.validateLink(((OProperty) (null)), ((Object) (null)), false);
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            // 
            // Cannot invoke \"com.orientechnologies.orient.core.metadata.schema.OProperty.getFullName()\" because \"p\" is null
            // 
            verifyException("com.orientechnologies.orient.core.record.impl.ODocument", e);
        }
    }

    @Test(timeout = 4000)
    public void createDocumentWithDatabaseAndClassName_throwsNoClassDefFoundError() throws Throwable {
        ODocument document = null;
        try {
            document = new ODocument(((ODatabaseSession) (null)), "d2/-oe@Metq|J}");
            fail("Expecting exception: NoClassDefFoundError");
        } catch (NoClassDefFoundError e) {
            // 
            // Could not initialize class com.orientechnologies.orient.core.serialization.serializer.record.ORecordSerializerFactory
            // 
            verifyException("com.orientechnologies.orient.core.db.document.ODatabaseDocumentAbstract", e);
        }
    }

    @Test(timeout = 4000)
    public void createDocumentFromByteArray_throwsNoClassDefFoundError() throws Throwable {
        byte[] content = new byte[2];
        ODocument document = null;
        try {
            document = new ODocument(content);
            fail("Expecting exception: NoClassDefFoundError");
        } catch (NoClassDefFoundError e) {
            // 
            // Could not initialize class com.orientechnologies.orient.core.serialization.serializer.record.ORecordSerializerFactory
            // 
            verifyException("com.orientechnologies.orient.core.db.document.ODatabaseDocumentAbstract", e);
        }
    }

    @Test(timeout = 4000)
    public void createDocumentWithDatabaseOnly_throwsNoClassDefFoundError() throws Throwable {
        Comparator.reverseOrder();
        ODocument document = null;
        try {
            document = new ODocument(((ODatabaseSession) (null)));
            fail("Expecting exception: NoClassDefFoundError");
        } catch (NoClassDefFoundError e) {
            // 
            // Could not initialize class com.orientechnologies.orient.core.serialization.serializer.record.ORecordSerializerFactory
            // 
            verifyException("com.orientechnologies.orient.core.db.document.ODatabaseDocumentAbstract", e);
        }
    }

    @Test(timeout = 4000)
    public void validateEmbedded_withNullPropertyAndNullValue_isNoOp() throws Throwable {
        ODocument.validateEmbedded(((OProperty) (null)), ((Object) (null)));
    }

    @Test(timeout = 4000)
    public void validateEmbedded_withNullPropertyAndNonNullValue_throwsNPE() throws Throwable {
        Object nonNullValue = new Object();
        // Undeclared exception!
        try {
            ODocument.validateEmbedded(((OProperty) (null)), nonNullValue);
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            // 
            // Cannot invoke \"com.orientechnologies.orient.core.metadata.schema.OProperty.getFullName()\" because \"p\" is null
            // 
            verifyException("com.orientechnologies.orient.core.record.impl.ODocument", e);
        }
    }

    @Test(timeout = 4000)
    public void validateLink_withNullPropertyAndNullValueAndAllowNullTrue_isNoOp() throws Throwable {
        ODocument.validateLink(((OProperty) (null)), ((Object) (null)), true);
    }

    @Test(timeout = 4000)
    public void validateLink_withNullPropertyAndNonNullValue_throwsNPE() throws Throwable {
        Object nonNullValue = new Object();
        // Undeclared exception!
        try {
            ODocument.validateLink(((OProperty) (null)), nonNullValue, false);
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            // 
            // Cannot invoke \"com.orientechnologies.orient.core.metadata.schema.OProperty.getFullName()\" because \"p\" is null
            // 
            verifyException("com.orientechnologies.orient.core.record.impl.ODocument", e);
        }
    }

    @Test(timeout = 4000)
    public void createDocumentWithThreeArguments_throwsNoClassDefFoundError() throws Throwable {
        Object[] fields = new Object[0];
        ODocument document = null;
        try {
            document = new ODocument("+M;?", ((Object) (null)), fields);
            fail("Expecting exception: NoClassDefFoundError");
        } catch (NoClassDefFoundError e) {
            // 
            // Could not initialize class com.orientechnologies.orient.core.serialization.serializer.record.ORecordSerializerFactory
            // 
            verifyException("com.orientechnologies.orient.core.db.document.ODatabaseDocumentAbstract", e);
        }
    }

    @Test(timeout = 4000)
    public void createDocumentWithOClass_throwsNoClassDefFoundError() throws Throwable {
        ODocument document = null;
        try {
            document = new ODocument(((OClass) (null)));
            fail("Expecting exception: NoClassDefFoundError");
        } catch (NoClassDefFoundError e) {
            // 
            // Could not initialize class com.orientechnologies.orient.core.serialization.serializer.record.ORecordSerializerFactory
            // 
            verifyException("com.orientechnologies.orient.core.db.document.ODatabaseDocumentAbstract", e);
        }
    }

    @Test(timeout = 4000)
    public void createEmptyDocument_throwsNoClassDefFoundError() throws Throwable {
        ODocument document = null;
        try {
            document = new ODocument();
            fail("Expecting exception: NoClassDefFoundError");
        } catch (NoClassDefFoundError e) {
            // 
            // Could not initialize class com.orientechnologies.orient.core.serialization.serializer.record.ORecordSerializerFactory
            // 
            verifyException("com.orientechnologies.orient.core.db.document.ODatabaseDocumentAbstract", e);
        }
    }

    @Test(timeout = 4000)
    public void createDocumentWithRecordId_throwsNoClassDefFoundError() throws Throwable {
        ORecordId emptyRecordId = ORecordId.EMPTY_RECORD_ID;
        ODocument document = null;
        try {
            document = new ODocument(emptyRecordId);
            fail("Expecting exception: NoClassDefFoundError");
        } catch (NoClassDefFoundError e) {
            // 
            // Could not initialize class com.orientechnologies.orient.core.serialization.serializer.record.ORecordSerializerFactory
            // 
            verifyException("com.orientechnologies.orient.core.db.document.ODatabaseDocumentAbstract", e);
        }
    }

    // Adapted IGT tests to match MWT environment and style

    @Test(timeout = 4000)
    public void createDocumentWithNullStringConstructor_throwsNoClassDefFoundError() throws Throwable {
        try {
            new ODocument(((String) (null)));
            fail("Expecting exception: NoClassDefFoundError");
        } catch (NoClassDefFoundError e) {
            // Could not initialize serializer factory via ODatabaseDocumentAbstract
            verifyException("com.orientechnologies.orient.core.db.document.ODatabaseDocumentAbstract", e);
        }
    }

    @Test(timeout = 4000)
    public void createDocumentWithNullOClassConstructor_throwsNoClassDefFoundError() throws Throwable {
        try {
            new ODocument(((OClass) (null)));
            fail("Expecting exception: NoClassDefFoundError");
        } catch (NoClassDefFoundError e) {
            // Could not initialize serializer factory via ODatabaseDocumentAbstract
            verifyException("com.orientechnologies.orient.core.db.document.ODatabaseDocumentAbstract", e);
        }
    }

    @Test(timeout = 4000)
    public void getDefaultSerializer_throwsNoClassDefFoundError() throws Throwable {
        try {
            ODatabaseDocumentAbstract.getDefaultSerializer();
            fail("Expecting exception: NoClassDefFoundError");
        } catch (NoClassDefFoundError e) {
            // Could not initialize class com.orientechnologies.orient.core.serialization.serializer.record.ORecordSerializerFactory
            verifyException("com.orientechnologies.orient.core.db.document.ODatabaseDocumentAbstract", e);
        }
    }
}