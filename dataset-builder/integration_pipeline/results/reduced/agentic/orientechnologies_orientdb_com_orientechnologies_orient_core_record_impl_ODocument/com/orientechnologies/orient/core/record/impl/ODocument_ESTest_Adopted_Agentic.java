package com.orientechnologies.orient.core.record.impl;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentAbstract;
import com.orientechnologies.orient.core.db.record.ridbag.ORidBag;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.ORecordInternal;
import com.orientechnologies.orient.core.serialization.serializer.record.ORecordSerializer;
/**
 *
 * @author Artem Orobets (enisher-at-gmail.com)
 */
public class ODocument_ESTest_Adopted_Agentic {
    // private static final String dbName = ODocumentTest.class.getSimpleName();
    private static final java.lang.String defaultDbAdminCredentials = "admin";

    @org.junit.Test
    public void testCopyToCopiesEmptyFieldsTypesAndOwners() throws java.lang.Exception {
        com.orientechnologies.orient.core.record.impl.ODocument doc1 = new com.orientechnologies.orient.core.record.impl.ODocument();
        com.orientechnologies.orient.core.record.impl.ODocument doc2 = new com.orientechnologies.orient.core.record.impl.ODocument().field("integer2", 123).field("string", "OrientDB").field("a", 123.3).setFieldType("integer", com.orientechnologies.orient.core.metadata.schema.OType.INTEGER).setFieldType("string", com.orientechnologies.orient.core.metadata.schema.OType.STRING).setFieldType("binary", com.orientechnologies.orient.core.metadata.schema.OType.BINARY);
        com.orientechnologies.orient.core.record.impl.ODocumentInternal.addOwner(doc2, new com.orientechnologies.orient.core.record.impl.ODocument());
        org.junit.Assert.assertEquals(doc2.<java.lang.Object>field("integer2"), 123);
        org.junit.Assert.assertEquals(doc2.field("string"), "OrientDB");
        // assertEquals(doc2.field("a"), 123.3);
        org.assertj.core.api.Assertions.assertThat(doc2.<java.lang.Double>field("a")).isEqualTo(123.3);
        org.junit.Assert.assertEquals(doc2.fieldType("integer"), com.orientechnologies.orient.core.metadata.schema.OType.INTEGER);
        org.junit.Assert.assertEquals(doc2.fieldType("string"), com.orientechnologies.orient.core.metadata.schema.OType.STRING);
        org.junit.Assert.assertEquals(doc2.fieldType("binary"), com.orientechnologies.orient.core.metadata.schema.OType.BINARY);
        org.junit.Assert.assertNotNull(doc2.getOwner());
        doc1.copyTo(doc2);
        org.junit.Assert.assertEquals(doc2.<java.lang.Object>field("integer2"), null);
        org.junit.Assert.assertEquals(doc2.<java.lang.Object>field("string"), null);
        org.junit.Assert.assertEquals(doc2.<java.lang.Object>field("a"), null);
        org.junit.Assert.assertEquals(doc2.fieldType("integer"), null);
        org.junit.Assert.assertEquals(doc2.fieldType("string"), null);
        org.junit.Assert.assertEquals(doc2.fieldType("binary"), null);
        org.junit.Assert.assertNull(doc2.getOwner());
    }

    @org.junit.Test
    public void testNullConstructor() {
        new com.orientechnologies.orient.core.record.impl.ODocument(((java.lang.String) (null)));
        new com.orientechnologies.orient.core.record.impl.ODocument(((com.orientechnologies.orient.core.metadata.schema.OClass) (null)));
    }

    @org.junit.Test
    public void testClearResetsFieldTypes() throws java.lang.Exception {
        com.orientechnologies.orient.core.record.impl.ODocument doc = new com.orientechnologies.orient.core.record.impl.ODocument();
        doc.setFieldType("integer", com.orientechnologies.orient.core.metadata.schema.OType.INTEGER);
        doc.setFieldType("string", com.orientechnologies.orient.core.metadata.schema.OType.STRING);
        doc.setFieldType("binary", com.orientechnologies.orient.core.metadata.schema.OType.BINARY);
        org.junit.Assert.assertEquals(doc.fieldType("integer"), com.orientechnologies.orient.core.metadata.schema.OType.INTEGER);
        org.junit.Assert.assertEquals(doc.fieldType("string"), com.orientechnologies.orient.core.metadata.schema.OType.STRING);
        org.junit.Assert.assertEquals(doc.fieldType("binary"), com.orientechnologies.orient.core.metadata.schema.OType.BINARY);
        doc.clear();
        org.junit.Assert.assertEquals(doc.fieldType("integer"), null);
        org.junit.Assert.assertEquals(doc.fieldType("string"), null);
        org.junit.Assert.assertEquals(doc.fieldType("binary"), null);
    }

    @org.junit.Test
    public void testResetResetsFieldTypes() throws java.lang.Exception {
        com.orientechnologies.orient.core.record.impl.ODocument doc = new com.orientechnologies.orient.core.record.impl.ODocument();
        doc.setFieldType("integer", com.orientechnologies.orient.core.metadata.schema.OType.INTEGER);
        doc.setFieldType("string", com.orientechnologies.orient.core.metadata.schema.OType.STRING);
        doc.setFieldType("binary", com.orientechnologies.orient.core.metadata.schema.OType.BINARY);
        org.junit.Assert.assertEquals(doc.fieldType("integer"), com.orientechnologies.orient.core.metadata.schema.OType.INTEGER);
        org.junit.Assert.assertEquals(doc.fieldType("string"), com.orientechnologies.orient.core.metadata.schema.OType.STRING);
        org.junit.Assert.assertEquals(doc.fieldType("binary"), com.orientechnologies.orient.core.metadata.schema.OType.BINARY);
        doc.reset();
        org.junit.Assert.assertEquals(doc.fieldType("integer"), null);
        org.junit.Assert.assertEquals(doc.fieldType("string"), null);
        org.junit.Assert.assertEquals(doc.fieldType("binary"), null);
    }

    @org.junit.Test
    public void testKeepFieldType() throws java.lang.Exception {
        com.orientechnologies.orient.core.record.impl.ODocument doc = new com.orientechnologies.orient.core.record.impl.ODocument();
        doc.field("integer", 10, com.orientechnologies.orient.core.metadata.schema.OType.INTEGER);
        doc.field("string", 20, com.orientechnologies.orient.core.metadata.schema.OType.STRING);
        doc.field("binary", new byte[]{ 30 }, com.orientechnologies.orient.core.metadata.schema.OType.BINARY);
        org.junit.Assert.assertEquals(doc.fieldType("integer"), com.orientechnologies.orient.core.metadata.schema.OType.INTEGER);
        org.junit.Assert.assertEquals(doc.fieldType("string"), com.orientechnologies.orient.core.metadata.schema.OType.STRING);
        org.junit.Assert.assertEquals(doc.fieldType("binary"), com.orientechnologies.orient.core.metadata.schema.OType.BINARY);
    }

    @org.junit.Test
    public void testKeepFieldTypeSerialization() throws java.lang.Exception {
        com.orientechnologies.orient.core.record.impl.ODocument doc = new com.orientechnologies.orient.core.record.impl.ODocument();
        doc.field("integer", 10, com.orientechnologies.orient.core.metadata.schema.OType.INTEGER);
        doc.field("link", new com.orientechnologies.orient.core.id.ORecordId(1, 2), com.orientechnologies.orient.core.metadata.schema.OType.LINK);
        doc.field("string", 20, com.orientechnologies.orient.core.metadata.schema.OType.STRING);
        doc.field("binary", new byte[]{ 30 }, com.orientechnologies.orient.core.metadata.schema.OType.BINARY);
        org.junit.Assert.assertEquals(doc.fieldType("integer"), com.orientechnologies.orient.core.metadata.schema.OType.INTEGER);
        org.junit.Assert.assertEquals(doc.fieldType("link"), com.orientechnologies.orient.core.metadata.schema.OType.LINK);
        org.junit.Assert.assertEquals(doc.fieldType("string"), com.orientechnologies.orient.core.metadata.schema.OType.STRING);
        org.junit.Assert.assertEquals(doc.fieldType("binary"), com.orientechnologies.orient.core.metadata.schema.OType.BINARY);
        com.orientechnologies.orient.core.serialization.serializer.record.ORecordSerializer ser = com.orientechnologies.orient.core.db.document.ODatabaseDocumentAbstract.getDefaultSerializer();
        byte[] bytes = ser.toStream(doc);
        doc = new com.orientechnologies.orient.core.record.impl.ODocument();
        ser.fromStream(bytes, doc, null);
        org.junit.Assert.assertEquals(doc.fieldType("integer"), com.orientechnologies.orient.core.metadata.schema.OType.INTEGER);
        org.junit.Assert.assertEquals(doc.fieldType("string"), com.orientechnologies.orient.core.metadata.schema.OType.STRING);
        org.junit.Assert.assertEquals(doc.fieldType("binary"), com.orientechnologies.orient.core.metadata.schema.OType.BINARY);
        org.junit.Assert.assertEquals(doc.fieldType("link"), com.orientechnologies.orient.core.metadata.schema.OType.LINK);
    }

    @org.junit.Test
    public void testKeepAutoFieldTypeSerialization() throws java.lang.Exception {
        com.orientechnologies.orient.core.record.impl.ODocument doc = new com.orientechnologies.orient.core.record.impl.ODocument();
        doc.field("integer", 10);
        doc.field("link", new com.orientechnologies.orient.core.id.ORecordId(1, 2));
        doc.field("string", "string");
        doc.field("binary", new byte[]{ 30 });
        // this is null because is not set on value set.
        org.junit.Assert.assertNull(doc.fieldType("integer"));
        org.junit.Assert.assertNull(doc.fieldType("link"));
        org.junit.Assert.assertNull(doc.fieldType("string"));
        org.junit.Assert.assertNull(doc.fieldType("binary"));
        com.orientechnologies.orient.core.serialization.serializer.record.ORecordSerializer ser = com.orientechnologies.orient.core.db.document.ODatabaseDocumentAbstract.getDefaultSerializer();
        byte[] bytes = ser.toStream(doc);
        doc = new com.orientechnologies.orient.core.record.impl.ODocument();
        ser.fromStream(bytes, doc, null);
        org.junit.Assert.assertEquals(doc.fieldType("integer"), com.orientechnologies.orient.core.metadata.schema.OType.INTEGER);
        org.junit.Assert.assertEquals(doc.fieldType("string"), com.orientechnologies.orient.core.metadata.schema.OType.STRING);
        org.junit.Assert.assertEquals(doc.fieldType("binary"), com.orientechnologies.orient.core.metadata.schema.OType.BINARY);
        org.junit.Assert.assertEquals(doc.fieldType("link"), com.orientechnologies.orient.core.metadata.schema.OType.LINK);
    }

    @org.junit.Test
    public void testKeepSchemafullFieldTypeSerialization() throws java.lang.Exception {
        com.orientechnologies.orient.core.db.ODatabaseSession db = null;
        com.orientechnologies.orient.core.db.OrientDB odb = null;
        try {
            // odb = OCreateDatabaseUtil.createDatabase(dbName, "memory:", OCreateDatabaseUtil.TYPE_MEMORY);
            // db = odb.open(dbName, defaultDbAdminCredentials, OCreateDatabaseUtil.NEW_ADMIN_PASSWORD);
            com.orientechnologies.orient.core.metadata.schema.OClass clazz = db.getMetadata().getSchema().createClass("Test");
            clazz.createProperty("integer", com.orientechnologies.orient.core.metadata.schema.OType.INTEGER);
            clazz.createProperty("link", com.orientechnologies.orient.core.metadata.schema.OType.LINK);
            clazz.createProperty("string", com.orientechnologies.orient.core.metadata.schema.OType.STRING);
            clazz.createProperty("binary", com.orientechnologies.orient.core.metadata.schema.OType.BINARY);
            com.orientechnologies.orient.core.record.impl.ODocument doc = new com.orientechnologies.orient.core.record.impl.ODocument(clazz);
            doc.field("integer", 10);
            doc.field("link", new com.orientechnologies.orient.core.id.ORecordId(1, 2));
            doc.field("string", "string");
            doc.field("binary", new byte[]{ 30 });
            // the types are from the schema.
            org.junit.Assert.assertEquals(doc.fieldType("integer"), com.orientechnologies.orient.core.metadata.schema.OType.INTEGER);
            org.junit.Assert.assertEquals(doc.fieldType("link"), com.orientechnologies.orient.core.metadata.schema.OType.LINK);
            org.junit.Assert.assertEquals(doc.fieldType("string"), com.orientechnologies.orient.core.metadata.schema.OType.STRING);
            org.junit.Assert.assertEquals(doc.fieldType("binary"), com.orientechnologies.orient.core.metadata.schema.OType.BINARY);
            com.orientechnologies.orient.core.serialization.serializer.record.ORecordSerializer ser = com.orientechnologies.orient.core.db.document.ODatabaseDocumentAbstract.getDefaultSerializer();
            byte[] bytes = ser.toStream(doc);
            doc = new com.orientechnologies.orient.core.record.impl.ODocument();
            ser.fromStream(bytes, doc, null);
            org.junit.Assert.assertEquals(doc.fieldType("integer"), com.orientechnologies.orient.core.metadata.schema.OType.INTEGER);
            org.junit.Assert.assertEquals(doc.fieldType("string"), com.orientechnologies.orient.core.metadata.schema.OType.STRING);
            org.junit.Assert.assertEquals(doc.fieldType("binary"), com.orientechnologies.orient.core.metadata.schema.OType.BINARY);
            org.junit.Assert.assertEquals(doc.fieldType("link"), com.orientechnologies.orient.core.metadata.schema.OType.LINK);
        } finally {
            if (db != null)
                db.close();

            if (odb != null) {
                // odb.drop(dbName);
                odb.close();
            }
        }
    }

    @org.junit.Test
    public void testChangeTypeOnValueSet() throws java.lang.Exception {
        com.orientechnologies.orient.core.record.impl.ODocument doc = new com.orientechnologies.orient.core.record.impl.ODocument();
        doc.field("link", new com.orientechnologies.orient.core.id.ORecordId(1, 2));
        com.orientechnologies.orient.core.serialization.serializer.record.ORecordSerializer ser = com.orientechnologies.orient.core.db.document.ODatabaseDocumentAbstract.getDefaultSerializer();
        byte[] bytes = ser.toStream(doc);
        doc = new com.orientechnologies.orient.core.record.impl.ODocument();
        ser.fromStream(bytes, doc, null);
        org.junit.Assert.assertEquals(doc.fieldType("link"), com.orientechnologies.orient.core.metadata.schema.OType.LINK);
        doc.field("link", new com.orientechnologies.orient.core.db.record.ridbag.ORidBag());
        org.junit.Assert.assertNotEquals(doc.fieldType("link"), com.orientechnologies.orient.core.metadata.schema.OType.LINK);
    }

    @org.junit.Test
    public void testRemovingReadonlyField() {
        com.orientechnologies.orient.core.db.ODatabaseSession db = null;
        com.orientechnologies.orient.core.db.OrientDB odb = null;
        try {
            // odb = OCreateDatabaseUtil.createDatabase(dbName, "memory:", OCreateDatabaseUtil.TYPE_MEMORY);
            // db = odb.open(dbName, defaultDbAdminCredentials, OCreateDatabaseUtil.NEW_ADMIN_PASSWORD);
            com.orientechnologies.orient.core.metadata.schema.OSchema schema = db.getMetadata().getSchema();
            com.orientechnologies.orient.core.metadata.schema.OClass classA = schema.createClass("TestRemovingField2");
            classA.createProperty("name", com.orientechnologies.orient.core.metadata.schema.OType.STRING);
            com.orientechnologies.orient.core.metadata.schema.OProperty property = classA.createProperty("property", com.orientechnologies.orient.core.metadata.schema.OType.STRING);
            property.setReadonly(true);
            com.orientechnologies.orient.core.record.impl.ODocument doc = new com.orientechnologies.orient.core.record.impl.ODocument(classA);
            doc.field("name", "My Name");
            doc.field("property", "value1");
            db.save(doc);
            doc.field("name", "My Name 2");
            doc.field("property", "value2");
            doc.undo();// we decided undo everything

            doc.field("name", "My Name 3");// change something

            db.save(doc);
            doc.field("name", "My Name 4");
            doc.field("property", "value4");
            doc.undo("property");// we decided undo readonly field

            db.save(doc);
        } finally {
            if (db != null)
                db.close();

            if (odb != null) {
                // odb.drop(dbName);
                odb.close();
            }
        }
    }

    @org.junit.Test
    public void testSetFieldAtListIndex() {
        com.orientechnologies.orient.core.record.impl.ODocument doc = new com.orientechnologies.orient.core.record.impl.ODocument();
        java.util.Map<java.lang.String, java.lang.Object> data = new java.util.HashMap<java.lang.String, java.lang.Object>();
        java.util.List<java.lang.Object> parentArray = new java.util.ArrayList<java.lang.Object>();
        parentArray.add(1);
        parentArray.add(2);
        parentArray.add(3);
        java.util.Map<java.lang.String, java.lang.Object> object4 = new java.util.HashMap<java.lang.String, java.lang.Object>();
        object4.put("prop", "A");
        parentArray.add(object4);
        data.put("array", parentArray);
        doc.field("data", data);
        org.junit.Assert.assertEquals(doc.field("data.array[3].prop"), "A");
        doc.field("data.array[3].prop", "B");
        org.junit.Assert.assertEquals(doc.field("data.array[3].prop"), "B");
        org.junit.Assert.assertEquals(doc.<java.lang.Object>field("data.array[0]"), 1);
        doc.field("data.array[0]", 5);
        org.junit.Assert.assertEquals(doc.<java.lang.Object>field("data.array[0]"), 5);
    }

    @org.junit.Test
    public void testUndo() {
        com.orientechnologies.orient.core.db.ODatabaseSession db = null;
        com.orientechnologies.orient.core.db.OrientDB odb = null;
        try {
            // odb = OCreateDatabaseUtil.createDatabase(dbName, "memory:", OCreateDatabaseUtil.TYPE_MEMORY);
            // db = odb.open(dbName, defaultDbAdminCredentials, OCreateDatabaseUtil.NEW_ADMIN_PASSWORD);
            com.orientechnologies.orient.core.metadata.schema.OSchema schema = db.getMetadata().getSchema();
            com.orientechnologies.orient.core.metadata.schema.OClass classA = schema.createClass("TestUndo");
            classA.createProperty("name", com.orientechnologies.orient.core.metadata.schema.OType.STRING);
            classA.createProperty("property", com.orientechnologies.orient.core.metadata.schema.OType.STRING);
            com.orientechnologies.orient.core.record.impl.ODocument doc = new com.orientechnologies.orient.core.record.impl.ODocument(classA);
            doc.field("name", "My Name");
            doc.field("property", "value1");
            db.save(doc);
            org.junit.Assert.assertEquals(doc.field("name"), "My Name");
            org.junit.Assert.assertEquals(doc.field("property"), "value1");
            doc.undo();
            org.junit.Assert.assertEquals(doc.field("name"), "My Name");
            org.junit.Assert.assertEquals(doc.field("property"), "value1");
            doc.field("name", "My Name 2");
            doc.field("property", "value2");
            doc.undo();
            doc.field("name", "My Name 3");
            org.junit.Assert.assertEquals(doc.field("name"), "My Name 3");
            org.junit.Assert.assertEquals(doc.field("property"), "value1");
            db.save(doc);
            doc.field("name", "My Name 4");
            doc.field("property", "value4");
            doc.undo("property");
            org.junit.Assert.assertEquals(doc.field("name"), "My Name 4");
            org.junit.Assert.assertEquals(doc.field("property"), "value1");
            db.save(doc);
            doc.undo("property");
            org.junit.Assert.assertEquals(doc.field("name"), "My Name 4");
            org.junit.Assert.assertEquals(doc.field("property"), "value1");
            doc.undo();
            org.junit.Assert.assertEquals(doc.field("name"), "My Name 4");
            org.junit.Assert.assertEquals(doc.field("property"), "value1");
        } finally {
            if (db != null)
                db.close();

            if (odb != null) {
                // odb.drop(dbName);
                odb.close();
            }
        }
    }

    @org.junit.Test
    public void testMergeNull() {
        com.orientechnologies.orient.core.record.impl.ODocument dest = new com.orientechnologies.orient.core.record.impl.ODocument();
        com.orientechnologies.orient.core.record.impl.ODocument source = new com.orientechnologies.orient.core.record.impl.ODocument();
        source.field("key", "value");
        source.field("somenull", ((java.lang.Object) (null)));
        dest.merge(source, true, false);
        org.junit.Assert.assertEquals(dest.field("key"), "value");
        org.junit.Assert.assertTrue(dest.containsField("somenull"));
    }

    @org.junit.Test(expected = java.lang.IllegalArgumentException.class)
    public void testFailNestedSetNull() {
        com.orientechnologies.orient.core.record.impl.ODocument doc = new com.orientechnologies.orient.core.record.impl.ODocument();
        doc.field("test.nested", "value");
    }

    @org.junit.Test(expected = java.lang.IllegalArgumentException.class)
    public void testFailNullMapKey() {
        com.orientechnologies.orient.core.record.impl.ODocument doc = new com.orientechnologies.orient.core.record.impl.ODocument();
        java.util.Map<java.lang.String, java.lang.String> map = new java.util.HashMap<java.lang.String, java.lang.String>();
        map.put(null, "dd");
        doc.field("testMap", map);
        doc.convertAllMultiValuesToTrackedVersions();
    }

    @org.junit.Test
    public void testGetSetProperty() {
        com.orientechnologies.orient.core.record.impl.ODocument doc = new com.orientechnologies.orient.core.record.impl.ODocument();
        java.util.Map<java.lang.String, java.lang.String> map = new java.util.HashMap<java.lang.String, java.lang.String>();
        map.put("foo", "valueInTheMap");
        doc.field("theMap", map);
        doc.setProperty("theMap.foo", "bar");
        org.junit.Assert.assertEquals(doc.getProperty("theMap"), map);
        org.junit.Assert.assertEquals(doc.getProperty("theMap.foo"), "bar");
        org.junit.Assert.assertEquals(doc.eval("theMap.foo"), "valueInTheMap");
        // doc.setProperty("", "foo");
        // assertEquals(doc.getProperty(""), "foo");
        doc.setProperty(",", "comma");
        org.junit.Assert.assertEquals(doc.getProperty(","), "comma");
        doc.setProperty(",.,/;:'\"", "strange");
        org.junit.Assert.assertEquals(doc.getProperty(",.,/;:'\""), "strange");
        doc.setProperty("   ", "spaces");
        org.junit.Assert.assertEquals(doc.getProperty("   "), "spaces");
    }

    @org.junit.Test
    public void testNoDirtySameBytes() {
        com.orientechnologies.orient.core.record.impl.ODocument doc = new com.orientechnologies.orient.core.record.impl.ODocument();
        byte[] bytes = new byte[]{ 0, 1, 2, 3, 4, 5 };
        doc.field("bytes", bytes);
        com.orientechnologies.orient.core.record.impl.ODocumentInternal.clearTrackData(doc);
        com.orientechnologies.orient.core.record.ORecordInternal.unsetDirty(doc);
        org.junit.Assert.assertFalse(doc.isDirty());
        org.junit.Assert.assertNull(doc.getOriginalValue("bytes"));
        doc.field("bytes", bytes.clone());
        org.junit.Assert.assertFalse(doc.isDirty());
        org.junit.Assert.assertNull(doc.getOriginalValue("bytes"));
        doc.setProperty("bytes", bytes.clone());
        org.junit.Assert.assertFalse(doc.isDirty());
        org.junit.Assert.assertNull(doc.getOriginalValue("bytes"));
    }

    @org.junit.Test
    public void testNoDirtySameString() {
        com.orientechnologies.orient.core.record.impl.ODocument doc = new com.orientechnologies.orient.core.record.impl.ODocument();
        doc.field("string", "value");
        com.orientechnologies.orient.core.record.impl.ODocumentInternal.clearTrackData(doc);
        com.orientechnologies.orient.core.record.ORecordInternal.unsetDirty(doc);
        org.junit.Assert.assertFalse(doc.isDirty());
        org.junit.Assert.assertNull(doc.getOriginalValue("string"));
        doc.field("string", "value");
        org.junit.Assert.assertFalse(doc.isDirty());
        org.junit.Assert.assertNull(doc.getOriginalValue("string"));
        doc.setProperty("string", "value");
        org.junit.Assert.assertFalse(doc.isDirty());
        org.junit.Assert.assertNull(doc.getOriginalValue("string"));
    }

    @org.junit.Test
    public void createDocumentFromClassNameString_throwsNoClassDefFoundError() {
        try {
            new com.orientechnologies.orient.core.record.impl.ODocument("z[' J~J");
            org.junit.Assert.fail("Expecting exception: NoClassDefFoundError");
        } catch (java.lang.NoClassDefFoundError e) {
            boolean found = false;
            for (java.lang.StackTraceElement element : e.getStackTrace()) {
                if ("com.orientechnologies.orient.core.db.document.ODatabaseDocumentAbstract".equals(element.getClassName())) {
                    found = true;
                    break;
                }
            }
            org.junit.Assert.assertTrue(found);
        }
    }

    @org.junit.Test
    public void validateLinkCollection_withNullProperty_throwsNPE() {
        java.util.TreeMap<java.util.LinkedHashMap<java.lang.Object, java.lang.Object>, java.lang.Object> treeMap = new java.util.TreeMap<java.util.LinkedHashMap<java.lang.Object, java.lang.Object>, java.lang.Object>();
        java.util.Collection<java.lang.Object> values = treeMap.values();
        com.orientechnologies.orient.core.record.impl.ODocumentEntry entry = new com.orientechnologies.orient.core.record.impl.ODocumentEntry();
        com.orientechnologies.orient.core.record.impl.ODocumentEntry clonedEntry = entry.clone();
        try {
            com.orientechnologies.orient.core.record.impl.ODocument.validateLinkCollection(((com.orientechnologies.orient.core.metadata.schema.OProperty) (null)), values, clonedEntry);
            org.junit.Assert.fail("Expecting exception: NullPointerException");
        } catch (java.lang.NullPointerException e) {
            boolean found = false;
            for (java.lang.StackTraceElement element : e.getStackTrace()) {
                if ("com.orientechnologies.orient.core.record.impl.ODocument".equals(element.getClassName())) {
                    found = true;
                    break;
                }
            }
            org.junit.Assert.assertTrue(found);
        }
    }

    @org.junit.Test
    public void createDocumentFromMap_throwsNoClassDefFoundError() {
        java.util.TreeMap<com.orientechnologies.orient.core.record.impl.ODocument, java.lang.Object> documentToObjectMap = new java.util.TreeMap<com.orientechnologies.orient.core.record.impl.ODocument, java.lang.Object>();
        try {
            new com.orientechnologies.orient.core.record.impl.ODocument(documentToObjectMap);
            org.junit.Assert.fail("Expecting exception: NoClassDefFoundError");
        } catch (java.lang.NoClassDefFoundError e) {
            boolean found = false;
            for (java.lang.StackTraceElement element : e.getStackTrace()) {
                if ("com.orientechnologies.orient.core.db.document.ODatabaseDocumentAbstract".equals(element.getClassName())) {
                    found = true;
                    break;
                }
            }
            org.junit.Assert.assertTrue(found);
        }
    }

    @org.junit.Test
    public void createDocumentFromDataInputStream_throwsNoClassDefFoundError() {
        byte[] buffer = new byte[8];
        buffer[7] = ((byte) (59));
        java.io.ByteArrayInputStream byteArrayInputStream = new java.io.ByteArrayInputStream(buffer);
        java.io.DataInputStream dataInputStream = new java.io.DataInputStream(byteArrayInputStream);
        try {
            // new ODocument(dataInputStream);
            org.junit.Assert.fail("Expecting exception: NoClassDefFoundError");
        } catch (java.lang.NoClassDefFoundError e) {
            boolean found = false;
            for (java.lang.StackTraceElement element : e.getStackTrace()) {
                if ("com.orientechnologies.orient.core.db.document.ODatabaseDocumentAbstract".equals(element.getClassName())) {
                    found = true;
                    break;
                }
            }
            org.junit.Assert.assertTrue(found);
        }
    }

    @org.junit.Test
    public void createDocumentFromObjectArray_throwsNoClassDefFoundError() {
        java.util.TreeMap<com.orientechnologies.orient.core.record.impl.ODocument, java.lang.Object> documentMap = new java.util.TreeMap<com.orientechnologies.orient.core.record.impl.ODocument, java.lang.Object>();
        java.lang.Object[] values = new java.lang.Object[6];
        values[5] = ((java.lang.Object) (documentMap));
        try {
            new com.orientechnologies.orient.core.record.impl.ODocument(values);
            org.junit.Assert.fail("Expecting exception: NoClassDefFoundError");
        } catch (java.lang.NoClassDefFoundError e) {
            boolean found = false;
            for (java.lang.StackTraceElement element : e.getStackTrace()) {
                if ("com.orientechnologies.orient.core.db.document.ODatabaseDocumentAbstract".equals(element.getClassName())) {
                    found = true;
                    break;
                }
            }
            org.junit.Assert.assertTrue(found);
        }
    }

    @org.junit.Test
    public void validateLink_withNullPropertyAndNullValueAndAllowNullFalse_throwsNPE() {
        try {
            com.orientechnologies.orient.core.record.impl.ODocument.validateLink(((com.orientechnologies.orient.core.metadata.schema.OProperty) (null)), ((java.lang.Object) (null)), false);
            org.junit.Assert.fail("Expecting exception: NullPointerException");
        } catch (java.lang.NullPointerException e) {
            boolean found = false;
            for (java.lang.StackTraceElement element : e.getStackTrace()) {
                if ("com.orientechnologies.orient.core.record.impl.ODocument".equals(element.getClassName())) {
                    found = true;
                    break;
                }
            }
            org.junit.Assert.assertTrue(found);
        }
    }

    @org.junit.Test
    public void createDocumentWithDatabaseAndClassName_throwsNoClassDefFoundError() {
        try {
            new com.orientechnologies.orient.core.record.impl.ODocument(((com.orientechnologies.orient.core.db.ODatabaseSession) (null)), "d2/-oe@Metq|J}");
            org.junit.Assert.fail("Expecting exception: NoClassDefFoundError");
        } catch (java.lang.NoClassDefFoundError e) {
            boolean found = false;
            for (java.lang.StackTraceElement element : e.getStackTrace()) {
                if ("com.orientechnologies.orient.core.db.document.ODatabaseDocumentAbstract".equals(element.getClassName())) {
                    found = true;
                    break;
                }
            }
            org.junit.Assert.assertTrue(found);
        }
    }

    @org.junit.Test
    public void createDocumentFromByteArray_throwsNoClassDefFoundError() {
        byte[] content = new byte[2];
        try {
            new com.orientechnologies.orient.core.record.impl.ODocument(content);
            org.junit.Assert.fail("Expecting exception: NoClassDefFoundError");
        } catch (java.lang.NoClassDefFoundError e) {
            boolean found = false;
            for (java.lang.StackTraceElement element : e.getStackTrace()) {
                if ("com.orientechnologies.orient.core.db.document.ODatabaseDocumentAbstract".equals(element.getClassName())) {
                    found = true;
                    break;
                }
            }
            org.junit.Assert.assertTrue(found);
        }
    }

    @org.junit.Test
    public void createDocumentWithDatabaseOnly_throwsNoClassDefFoundError() {
        try {
            new com.orientechnologies.orient.core.record.impl.ODocument(((com.orientechnologies.orient.core.db.ODatabaseSession) (null)));
            org.junit.Assert.fail("Expecting exception: NoClassDefFoundError");
        } catch (java.lang.NoClassDefFoundError e) {
            boolean found = false;
            for (java.lang.StackTraceElement element : e.getStackTrace()) {
                if ("com.orientechnologies.orient.core.db.document.ODatabaseDocumentAbstract".equals(element.getClassName())) {
                    found = true;
                    break;
                }
            }
            org.junit.Assert.assertTrue(found);
        }
    }

    @org.junit.Test
    public void validateEmbedded_withNullPropertyAndNullValue_isNoOp() {
        com.orientechnologies.orient.core.record.impl.ODocument.validateEmbedded(((com.orientechnologies.orient.core.metadata.schema.OProperty) (null)), ((java.lang.Object) (null)));
    }

    @org.junit.Test
    public void validateEmbedded_withNullPropertyAndNonNullValue_throwsNPE() {
        java.lang.Object nonNullValue = new java.lang.Object();
        try {
            com.orientechnologies.orient.core.record.impl.ODocument.validateEmbedded(((com.orientechnologies.orient.core.metadata.schema.OProperty) (null)), nonNullValue);
            org.junit.Assert.fail("Expecting exception: NullPointerException");
        } catch (java.lang.NullPointerException e) {
            boolean found = false;
            for (java.lang.StackTraceElement element : e.getStackTrace()) {
                if ("com.orientechnologies.orient.core.record.impl.ODocument".equals(element.getClassName())) {
                    found = true;
                    break;
                }
            }
            org.junit.Assert.assertTrue(found);
        }
    }

    @org.junit.Test
    public void validateLink_withNullPropertyAndNullValueAndAllowNullTrue_isNoOp() {
        com.orientechnologies.orient.core.record.impl.ODocument.validateLink(((com.orientechnologies.orient.core.metadata.schema.OProperty) (null)), ((java.lang.Object) (null)), true);
    }

    @org.junit.Test
    public void validateLink_withNullPropertyAndNonNullValue_throwsNPE() {
        java.lang.Object nonNullValue = new java.lang.Object();
        try {
            com.orientechnologies.orient.core.record.impl.ODocument.validateLink(((com.orientechnologies.orient.core.metadata.schema.OProperty) (null)), nonNullValue, false);
            org.junit.Assert.fail("Expecting exception: NullPointerException");
        } catch (java.lang.NullPointerException e) {
            boolean found = false;
            for (java.lang.StackTraceElement element : e.getStackTrace()) {
                if ("com.orientechnologies.orient.core.record.impl.ODocument".equals(element.getClassName())) {
                    found = true;
                    break;
                }
            }
            org.junit.Assert.assertTrue(found);
        }
    }

    @org.junit.Test
    public void createDocumentWithThreeArguments_throwsNoClassDefFoundError() {
        java.lang.Object[] fields = new java.lang.Object[0];
        try {
            new com.orientechnologies.orient.core.record.impl.ODocument("+M;?", ((java.lang.Object) (null)), fields);
            org.junit.Assert.fail("Expecting exception: NoClassDefFoundError");
        } catch (java.lang.NoClassDefFoundError e) {
            boolean found = false;
            for (java.lang.StackTraceElement element : e.getStackTrace()) {
                if ("com.orientechnologies.orient.core.db.document.ODatabaseDocumentAbstract".equals(element.getClassName())) {
                    found = true;
                    break;
                }
            }
            org.junit.Assert.assertTrue(found);
        }
    }

    @org.junit.Test
    public void createDocumentWithOClass_throwsNoClassDefFoundError() {
        try {
            new com.orientechnologies.orient.core.record.impl.ODocument(((com.orientechnologies.orient.core.metadata.schema.OClass) (null)));
            org.junit.Assert.fail("Expecting exception: NoClassDefFoundError");
        } catch (java.lang.NoClassDefFoundError e) {
            boolean found = false;
            for (java.lang.StackTraceElement element : e.getStackTrace()) {
                if ("com.orientechnologies.orient.core.db.document.ODatabaseDocumentAbstract".equals(element.getClassName())) {
                    found = true;
                    break;
                }
            }
            org.junit.Assert.assertTrue(found);
        }
    }

    @org.junit.Test
    public void createEmptyDocument_throwsNoClassDefFoundError() {
        try {
            new com.orientechnologies.orient.core.record.impl.ODocument();
            org.junit.Assert.fail("Expecting exception: NoClassDefFoundError");
        } catch (java.lang.NoClassDefFoundError e) {
            boolean found = false;
            for (java.lang.StackTraceElement element : e.getStackTrace()) {
                if ("com.orientechnologies.orient.core.db.document.ODatabaseDocumentAbstract".equals(element.getClassName())) {
                    found = true;
                    break;
                }
            }
            org.junit.Assert.assertTrue(found);
        }
    }

    @org.junit.Test
    public void createDocumentWithRecordId_throwsNoClassDefFoundError() {
        com.orientechnologies.orient.core.id.ORecordId emptyRecordId = com.orientechnologies.orient.core.id.ORecordId.EMPTY_RECORD_ID;
        try {
            new com.orientechnologies.orient.core.record.impl.ODocument(emptyRecordId);
            org.junit.Assert.fail("Expecting exception: NoClassDefFoundError");
        } catch (java.lang.NoClassDefFoundError e) {
            boolean found = false;
            for (java.lang.StackTraceElement element : e.getStackTrace()) {
                if ("com.orientechnologies.orient.core.db.document.ODatabaseDocumentAbstract".equals(element.getClassName())) {
                    found = true;
                    break;
                }
            }
            org.junit.Assert.assertTrue(found);
        }
    }
}