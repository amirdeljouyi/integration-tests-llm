package com.orientechnologies.orient.core.record.impl;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentAbstract;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
public class ODocument_ESTest_Adopted {
    @org.junit.Test(timeout = 4000)
    public void createDocumentFromClassNameString_throwsNoClassDefFoundError() throws java.lang.Throwable {
        com.orientechnologies.orient.core.record.impl.ODocument document = null;
        try {
            document = new com.orientechnologies.orient.core.record.impl.ODocument("z[' J~J");
            org.junit.Assert.fail("Expecting exception: NoClassDefFoundError");
        } catch (java.lang.NoClassDefFoundError e) {
            // 
            // Could not initialize class com.orientechnologies.orient.core.serialization.serializer.record.ORecordSerializerFactory
            // 
            org.evosuite.runtime.EvoAssertions.verifyException("com.orientechnologies.orient.core.db.document.ODatabaseDocumentAbstract", e);
        }
    }

    @org.junit.Test(timeout = 4000)
    public void validateLinkCollection_withNullProperty_throwsNPE() throws java.lang.Throwable {
        java.util.TreeMap<java.util.LinkedHashMap<java.lang.Object, java.lang.Object>, java.lang.Object> treeMap = new java.util.TreeMap<java.util.LinkedHashMap<java.lang.Object, java.lang.Object>, java.lang.Object>();
        java.util.Collection<java.lang.Object> values = treeMap.values();
        com.orientechnologies.orient.core.record.impl.ODocumentEntry entry = new com.orientechnologies.orient.core.record.impl.ODocumentEntry();
        com.orientechnologies.orient.core.record.impl.ODocumentEntry clonedEntry = entry.clone();
        // Undeclared exception!
        try {
            com.orientechnologies.orient.core.record.impl.ODocument.validateLinkCollection(((com.orientechnologies.orient.core.metadata.schema.OProperty) (null)), values, clonedEntry);
            org.junit.Assert.fail("Expecting exception: NullPointerException");
        } catch (java.lang.NullPointerException e) {
            // 
            // Cannot invoke \"com.orientechnologies.orient.core.metadata.schema.OProperty.getLinkedClass()\" because \"property\" is null
            // 
            org.evosuite.runtime.EvoAssertions.verifyException("com.orientechnologies.orient.core.record.impl.ODocument", e);
        }
    }

    @org.junit.Test(timeout = 4000)
    public void createDocumentFromMap_throwsNoClassDefFoundError() throws java.lang.Throwable {
        java.lang.Object unusedObject = new java.lang.Object();
        java.util.TreeMap<com.orientechnologies.orient.core.record.impl.ODocument, java.lang.Object> documentToObjectMap = new java.util.TreeMap<com.orientechnologies.orient.core.record.impl.ODocument, java.lang.Object>();
        com.orientechnologies.orient.core.record.impl.ODocument document = null;
        try {
            document = new com.orientechnologies.orient.core.record.impl.ODocument(documentToObjectMap);
            org.junit.Assert.fail("Expecting exception: NoClassDefFoundError");
        } catch (java.lang.NoClassDefFoundError e) {
            // 
            // Could not initialize class com.orientechnologies.orient.core.serialization.serializer.record.ORecordSerializerFactory
            // 
            org.evosuite.runtime.EvoAssertions.verifyException("com.orientechnologies.orient.core.db.document.ODatabaseDocumentAbstract", e);
        }
    }

    @org.junit.Test(timeout = 4000)
    public void createDocumentFromDataInputStream_throwsNoClassDefFoundError() throws java.lang.Throwable {
        byte[] buffer = new byte[8];
        buffer[7] = ((byte) (59));
        java.io.ByteArrayInputStream byteArrayInputStream = new java.io.ByteArrayInputStream(buffer);
        java.io.DataInputStream dataInputStream = new java.io.DataInputStream(byteArrayInputStream);
        com.orientechnologies.orient.core.record.impl.ODocument document = null;
        try {
            document = new com.orientechnologies.orient.core.record.impl.ODocument(dataInputStream);
            org.junit.Assert.fail("Expecting exception: NoClassDefFoundError");
        } catch (java.lang.NoClassDefFoundError e) {
            // 
            // Could not initialize class com.orientechnologies.orient.core.serialization.serializer.record.ORecordSerializerFactory
            // 
            org.evosuite.runtime.EvoAssertions.verifyException("com.orientechnologies.orient.core.db.document.ODatabaseDocumentAbstract", e);
        }
    }

    @org.junit.Test(timeout = 4000)
    public void createDocumentFromObjectArray_throwsNoClassDefFoundError() throws java.lang.Throwable {
        java.util.TreeMap<com.orientechnologies.orient.core.record.impl.ODocument, java.lang.Object> documentMap = new java.util.TreeMap<com.orientechnologies.orient.core.record.impl.ODocument, java.lang.Object>();
        java.lang.Object[] values = new java.lang.Object[6];
        values[5] = ((java.lang.Object) (documentMap));
        com.orientechnologies.orient.core.record.impl.ODocument document = null;
        try {
            document = new com.orientechnologies.orient.core.record.impl.ODocument(values);
            org.junit.Assert.fail("Expecting exception: NoClassDefFoundError");
        } catch (java.lang.NoClassDefFoundError e) {
            // 
            // Could not initialize class com.orientechnologies.orient.core.serialization.serializer.record.ORecordSerializerFactory
            // 
            org.evosuite.runtime.EvoAssertions.verifyException("com.orientechnologies.orient.core.db.document.ODatabaseDocumentAbstract", e);
        }
    }

    @org.junit.Test(timeout = 4000)
    public void validateLink_withNullPropertyAndNullValueAndAllowNullFalse_throwsNPE() throws java.lang.Throwable {
        // Undeclared exception!
        try {
            com.orientechnologies.orient.core.record.impl.ODocument.validateLink(((com.orientechnologies.orient.core.metadata.schema.OProperty) (null)), ((java.lang.Object) (null)), false);
            org.junit.Assert.fail("Expecting exception: NullPointerException");
        } catch (java.lang.NullPointerException e) {
            // 
            // Cannot invoke \"com.orientechnologies.orient.core.metadata.schema.OProperty.getFullName()\" because \"p\" is null
            // 
            org.evosuite.runtime.EvoAssertions.verifyException("com.orientechnologies.orient.core.record.impl.ODocument", e);
        }
    }

    @org.junit.Test(timeout = 4000)
    public void createDocumentWithDatabaseAndClassName_throwsNoClassDefFoundError() throws java.lang.Throwable {
        com.orientechnologies.orient.core.record.impl.ODocument document = null;
        try {
            document = new com.orientechnologies.orient.core.record.impl.ODocument(((com.orientechnologies.orient.core.db.ODatabaseSession) (null)), "d2/-oe@Metq|J}");
            org.junit.Assert.fail("Expecting exception: NoClassDefFoundError");
        } catch (java.lang.NoClassDefFoundError e) {
            // 
            // Could not initialize class com.orientechnologies.orient.core.serialization.serializer.record.ORecordSerializerFactory
            // 
            org.evosuite.runtime.EvoAssertions.verifyException("com.orientechnologies.orient.core.db.document.ODatabaseDocumentAbstract", e);
        }
    }

    @org.junit.Test(timeout = 4000)
    public void createDocumentFromByteArray_throwsNoClassDefFoundError() throws java.lang.Throwable {
        byte[] content = new byte[2];
        com.orientechnologies.orient.core.record.impl.ODocument document = null;
        try {
            document = new com.orientechnologies.orient.core.record.impl.ODocument(content);
            org.junit.Assert.fail("Expecting exception: NoClassDefFoundError");
        } catch (java.lang.NoClassDefFoundError e) {
            // 
            // Could not initialize class com.orientechnologies.orient.core.serialization.serializer.record.ORecordSerializerFactory
            // 
            org.evosuite.runtime.EvoAssertions.verifyException("com.orientechnologies.orient.core.db.document.ODatabaseDocumentAbstract", e);
        }
    }

    @org.junit.Test(timeout = 4000)
    public void createDocumentWithDatabaseOnly_throwsNoClassDefFoundError() throws java.lang.Throwable {
        java.util.Comparator.reverseOrder();
        com.orientechnologies.orient.core.record.impl.ODocument document = null;
        try {
            document = new com.orientechnologies.orient.core.record.impl.ODocument(((com.orientechnologies.orient.core.db.ODatabaseSession) (null)));
            org.junit.Assert.fail("Expecting exception: NoClassDefFoundError");
        } catch (java.lang.NoClassDefFoundError e) {
            // 
            // Could not initialize class com.orientechnologies.orient.core.serialization.serializer.record.ORecordSerializerFactory
            // 
            org.evosuite.runtime.EvoAssertions.verifyException("com.orientechnologies.orient.core.db.document.ODatabaseDocumentAbstract", e);
        }
    }

    @org.junit.Test(timeout = 4000)
    public void validateEmbedded_withNullPropertyAndNullValue_isNoOp() throws java.lang.Throwable {
        com.orientechnologies.orient.core.record.impl.ODocument.validateEmbedded(((com.orientechnologies.orient.core.metadata.schema.OProperty) (null)), ((java.lang.Object) (null)));
    }

    @org.junit.Test(timeout = 4000)
    public void validateEmbedded_withNullPropertyAndNonNullValue_throwsNPE() throws java.lang.Throwable {
        java.lang.Object nonNullValue = new java.lang.Object();
        // Undeclared exception!
        try {
            com.orientechnologies.orient.core.record.impl.ODocument.validateEmbedded(((com.orientechnologies.orient.core.metadata.schema.OProperty) (null)), nonNullValue);
            org.junit.Assert.fail("Expecting exception: NullPointerException");
        } catch (java.lang.NullPointerException e) {
            // 
            // Cannot invoke \"com.orientechnologies.orient.core.metadata.schema.OProperty.getFullName()\" because \"p\" is null
            // 
            org.evosuite.runtime.EvoAssertions.verifyException("com.orientechnologies.orient.core.record.impl.ODocument", e);
        }
    }

    @org.junit.Test(timeout = 4000)
    public void validateLink_withNullPropertyAndNullValueAndAllowNullTrue_isNoOp() throws java.lang.Throwable {
        com.orientechnologies.orient.core.record.impl.ODocument.validateLink(((com.orientechnologies.orient.core.metadata.schema.OProperty) (null)), ((java.lang.Object) (null)), true);
    }

    @org.junit.Test(timeout = 4000)
    public void validateLink_withNullPropertyAndNonNullValue_throwsNPE() throws java.lang.Throwable {
        java.lang.Object nonNullValue = new java.lang.Object();
        // Undeclared exception!
        try {
            com.orientechnologies.orient.core.record.impl.ODocument.validateLink(((com.orientechnologies.orient.core.metadata.schema.OProperty) (null)), nonNullValue, false);
            org.junit.Assert.fail("Expecting exception: NullPointerException");
        } catch (java.lang.NullPointerException e) {
            // 
            // Cannot invoke \"com.orientechnologies.orient.core.metadata.schema.OProperty.getFullName()\" because \"p\" is null
            // 
            org.evosuite.runtime.EvoAssertions.verifyException("com.orientechnologies.orient.core.record.impl.ODocument", e);
        }
    }

    @org.junit.Test(timeout = 4000)
    public void createDocumentWithThreeArguments_throwsNoClassDefFoundError() throws java.lang.Throwable {
        java.lang.Object[] fields = new java.lang.Object[0];
        com.orientechnologies.orient.core.record.impl.ODocument document = null;
        try {
            document = new com.orientechnologies.orient.core.record.impl.ODocument("+M;?", ((java.lang.Object) (null)), fields);
            org.junit.Assert.fail("Expecting exception: NoClassDefFoundError");
        } catch (java.lang.NoClassDefFoundError e) {
            // 
            // Could not initialize class com.orientechnologies.orient.core.serialization.serializer.record.ORecordSerializerFactory
            // 
            org.evosuite.runtime.EvoAssertions.verifyException("com.orientechnologies.orient.core.db.document.ODatabaseDocumentAbstract", e);
        }
    }

    @org.junit.Test(timeout = 4000)
    public void createDocumentWithOClass_throwsNoClassDefFoundError() throws java.lang.Throwable {
        com.orientechnologies.orient.core.record.impl.ODocument document = null;
        try {
            document = new com.orientechnologies.orient.core.record.impl.ODocument(((com.orientechnologies.orient.core.metadata.schema.OClass) (null)));
            org.junit.Assert.fail("Expecting exception: NoClassDefFoundError");
        } catch (java.lang.NoClassDefFoundError e) {
            // 
            // Could not initialize class com.orientechnologies.orient.core.serialization.serializer.record.ORecordSerializerFactory
            // 
            org.evosuite.runtime.EvoAssertions.verifyException("com.orientechnologies.orient.core.db.document.ODatabaseDocumentAbstract", e);
        }
    }

    @org.junit.Test(timeout = 4000)
    public void createEmptyDocument_throwsNoClassDefFoundError() throws java.lang.Throwable {
        com.orientechnologies.orient.core.record.impl.ODocument document = null;
        try {
            document = new com.orientechnologies.orient.core.record.impl.ODocument();
            org.junit.Assert.fail("Expecting exception: NoClassDefFoundError");
        } catch (java.lang.NoClassDefFoundError e) {
            // 
            // Could not initialize class com.orientechnologies.orient.core.serialization.serializer.record.ORecordSerializerFactory
            // 
            org.evosuite.runtime.EvoAssertions.verifyException("com.orientechnologies.orient.core.db.document.ODatabaseDocumentAbstract", e);
        }
    }

    @org.junit.Test(timeout = 4000)
    public void createDocumentWithRecordId_throwsNoClassDefFoundError() throws java.lang.Throwable {
        com.orientechnologies.orient.core.id.ORecordId emptyRecordId = com.orientechnologies.orient.core.id.ORecordId.EMPTY_RECORD_ID;
        com.orientechnologies.orient.core.record.impl.ODocument document = null;
        try {
            document = new com.orientechnologies.orient.core.record.impl.ODocument(emptyRecordId);
            org.junit.Assert.fail("Expecting exception: NoClassDefFoundError");
        } catch (java.lang.NoClassDefFoundError e) {
            // 
            // Could not initialize class com.orientechnologies.orient.core.serialization.serializer.record.ORecordSerializerFactory
            // 
            org.evosuite.runtime.EvoAssertions.verifyException("com.orientechnologies.orient.core.db.document.ODatabaseDocumentAbstract", e);
        }
    }

    // Adapted IGT tests to match MWT environment and style
    @org.junit.Test(timeout = 4000)
    public void createDocumentWithNullStringConstructor_throwsNoClassDefFoundError() throws java.lang.Throwable {
        try {
            new com.orientechnologies.orient.core.record.impl.ODocument(((java.lang.String) (null)));
            org.junit.Assert.fail("Expecting exception: NoClassDefFoundError");
        } catch (java.lang.NoClassDefFoundError e) {
            // Could not initialize serializer factory via ODatabaseDocumentAbstract
            org.evosuite.runtime.EvoAssertions.verifyException("com.orientechnologies.orient.core.db.document.ODatabaseDocumentAbstract", e);
        }
    }

    @org.junit.Test(timeout = 4000)
    public void createDocumentWithNullOClassConstructor_throwsNoClassDefFoundError() throws java.lang.Throwable {
        try {
            new com.orientechnologies.orient.core.record.impl.ODocument(((com.orientechnologies.orient.core.metadata.schema.OClass) (null)));
            org.junit.Assert.fail("Expecting exception: NoClassDefFoundError");
        } catch (java.lang.NoClassDefFoundError e) {
            // Could not initialize serializer factory via ODatabaseDocumentAbstract
            org.evosuite.runtime.EvoAssertions.verifyException("com.orientechnologies.orient.core.db.document.ODatabaseDocumentAbstract", e);
        }
    }

    @org.junit.Test(timeout = 4000)
    public void getDefaultSerializer_throwsNoClassDefFoundError() throws java.lang.Throwable {
        try {
            com.orientechnologies.orient.core.db.document.ODatabaseDocumentAbstract.getDefaultSerializer();
            org.junit.Assert.fail("Expecting exception: NoClassDefFoundError");
        } catch (java.lang.NoClassDefFoundError e) {
            // Could not initialize class com.orientechnologies.orient.core.serialization.serializer.record.ORecordSerializerFactory
            org.evosuite.runtime.EvoAssertions.verifyException("com.orientechnologies.orient.core.db.document.ODatabaseDocumentAbstract", e);
        }
    }
}