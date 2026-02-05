package com.orientechnologies.orient.core.record.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.orientechnologies.orient.core.OCreateDatabaseUtil;
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
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.assertj.core.api.Assertions;
import org.junit.Test;

/** @author Artem Orobets (enisher-at-gmail.com) */
public class ODocument_ESTest_Adopted_Agentic {
  private static final String dbName = ODocumentTest.class.getSimpleName();
  private static final String defaultDbAdminCredentials = "admin";

  @Test
  public void testCopyToCopiesEmptyFieldsTypesAndOwners() throws Exception {
    ODocument doc1 = new ODocument();

    ODocument doc2 =
        new ODocument()
            .field("integer2", 123)
            .field("string", "OrientDB")
            .field("a", 123.3)
            .setFieldType("integer", OType.INTEGER)
            .setFieldType("string", OType.STRING)
            .setFieldType("binary", OType.BINARY);
    ODocumentInternal.addOwner(doc2, new ODocument());

    assertEquals(doc2.<Object>field("integer2"), 123);
    assertEquals(doc2.field("string"), "OrientDB");
    //    assertEquals(doc2.field("a"), 123.3);

    Assertions.assertThat(doc2.<Double>field("a")).isEqualTo(123.3d);
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
  public void testNullConstructor() {
    new ODocument((String) null);
    new ODocument((OClass) null);
  }

  @Test
  public void testClearResetsFieldTypes() throws Exception {
    ODocument doc = new ODocument();
    doc.setFieldType("integer", OType.INTEGER);
    doc.setFieldType("string", OType.STRING);
    doc.setFieldType("binary", OType.BINARY);

    assertEquals(doc.fieldType("integer"), OType.INTEGER);
    assertEquals(doc.fieldType("string"), OType.STRING);
    assertEquals(doc.fieldType("binary"), OType.BINARY);

    doc.clear();

    assertEquals(doc.fieldType("integer"), null);
    assertEquals(doc.fieldType("string"), null);
    assertEquals(doc.fieldType("binary"), null);
  }

  @Test
  public void testResetResetsFieldTypes() throws Exception {
    ODocument doc = new ODocument();
    doc.setFieldType("integer", OType.INTEGER);
    doc.setFieldType("string", OType.STRING);
    doc.setFieldType("binary", OType.BINARY);

    assertEquals(doc.fieldType("integer"), OType.INTEGER);
    assertEquals(doc.fieldType("string"), OType.STRING);
    assertEquals(doc.fieldType("binary"), OType.BINARY);

    doc.reset();

    assertEquals(doc.fieldType("integer"), null);
    assertEquals(doc.fieldType("string"), null);
    assertEquals(doc.fieldType("binary"), null);
  }

  @Test
  public void testKeepFieldType() throws Exception {
    ODocument doc = new ODocument();
    doc.field("integer", 10, OType.INTEGER);
    doc.field("string", 20, OType.STRING);
    doc.field("binary", new byte[] {30}, OType.BINARY);

    assertEquals(doc.fieldType("integer"), OType.INTEGER);
    assertEquals(doc.fieldType("string"), OType.STRING);
    assertEquals(doc.fieldType("binary"), OType.BINARY);
  }

  @Test
  public void testKeepFieldTypeSerialization() throws Exception {
    ODocument doc = new ODocument();
    doc.field("integer", 10, OType.INTEGER);
    doc.field("link", new ORecordId(1, 2), OType.LINK);
    doc.field("string", 20, OType.STRING);
    doc.field("binary", new byte[] {30}, OType.BINARY);

    assertEquals(doc.fieldType("integer"), OType.INTEGER);
    assertEquals(doc.fieldType("link"), OType.LINK);
    assertEquals(doc.fieldType("string"), OType.STRING);
    assertEquals(doc.fieldType("binary"), OType.BINARY);
    ORecordSerializer ser = ODatabaseDocumentAbstract.getDefaultSerializer();
    byte[] bytes = ser.toStream(doc);
    doc = new ODocument();
    ser.fromStream(bytes, doc, null);
    assertEquals(doc.fieldType("integer"), OType.INTEGER);
    assertEquals(doc.fieldType("string"), OType.STRING);
    assertEquals(doc.fieldType("binary"), OType.BINARY);
    assertEquals(doc.fieldType("link"), OType.LINK);
  }

  @Test
  public void testKeepAutoFieldTypeSerialization() throws Exception {
    ODocument doc = new ODocument();
    doc.field("integer", 10);
    doc.field("link", new ORecordId(1, 2));
    doc.field("string", "string");
    doc.field("binary", new byte[] {30});

    // this is null because is not set on value set.
    assertNull(doc.fieldType("integer"));
    assertNull(doc.fieldType("link"));
    assertNull(doc.fieldType("string"));
    assertNull(doc.fieldType("binary"));
    ORecordSerializer ser = ODatabaseDocumentAbstract.getDefaultSerializer();
    byte[] bytes = ser.toStream(doc);
    doc = new ODocument();
    ser.fromStream(bytes, doc, null);
    assertEquals(doc.fieldType("integer"), OType.INTEGER);
    assertEquals(doc.fieldType("string"), OType.STRING);
    assertEquals(doc.fieldType("binary"), OType.BINARY);
    assertEquals(doc.fieldType("link"), OType.LINK);
  }

  @Test
  public void testKeepSchemafullFieldTypeSerialization() throws Exception {
    ODatabaseSession db = null;
    OrientDB odb = null;
    try {
      odb = OCreateDatabaseUtil.createDatabase(dbName, "memory:", OCreateDatabaseUtil.TYPE_MEMORY);
      db = odb.open(dbName, defaultDbAdminCredentials, OCreateDatabaseUtil.NEW_ADMIN_PASSWORD);

      OClass clazz = db.getMetadata().getSchema().createClass("Test");
      clazz.createProperty("integer", OType.INTEGER);
      clazz.createProperty("link", OType.LINK);
      clazz.createProperty("string", OType.STRING);
      clazz.createProperty("binary", OType.BINARY);
      ODocument doc = new ODocument(clazz);
      doc.field("integer", 10);
      doc.field("link", new ORecordId(1, 2));
      doc.field("string", "string");
      doc.field("binary", new byte[] {30});

      // the types are from the schema.
      assertEquals(doc.fieldType("integer"), OType.INTEGER);
      assertEquals(doc.fieldType("link"), OType.LINK);
      assertEquals(doc.fieldType("string"), OType.STRING);
      assertEquals(doc.fieldType("binary"), OType.BINARY);
      ORecordSerializer ser = ODatabaseDocumentAbstract.getDefaultSerializer();
      byte[] bytes = ser.toStream(doc);
      doc = new ODocument();
      ser.fromStream(bytes, doc, null);
      assertEquals(doc.fieldType("integer"), OType.INTEGER);
      assertEquals(doc.fieldType("string"), OType.STRING);
      assertEquals(doc.fieldType("binary"), OType.BINARY);
      assertEquals(doc.fieldType("link"), OType.LINK);
    } finally {
      if (db != null) db.close();
      if (odb != null) {
        odb.drop(dbName);
        odb.close();
      }
    }
  }

  @Test
  public void testChangeTypeOnValueSet() throws Exception {
    ODocument doc = new ODocument();
    doc.field("link", new ORecordId(1, 2));
    ORecordSerializer ser = ODatabaseDocumentAbstract.getDefaultSerializer();
    byte[] bytes = ser.toStream(doc);
    doc = new ODocument();
    ser.fromStream(bytes, doc, null);
    assertEquals(doc.fieldType("link"), OType.LINK);
    doc.field("link", new ORidBag());
    assertNotEquals(doc.fieldType("link"), OType.LINK);
  }

  @Test
  public void testRemovingReadonlyField() {
    ODatabaseSession db = null;
    OrientDB odb = null;
    try {
      odb = OCreateDatabaseUtil.createDatabase(dbName, "memory:", OCreateDatabaseUtil.TYPE_MEMORY);
      db = odb.open(dbName, defaultDbAdminCredentials, OCreateDatabaseUtil.NEW_ADMIN_PASSWORD);

      OSchema schema = db.getMetadata().getSchema();
      OClass classA = schema.createClass("TestRemovingField2");
      classA.createProperty("name", OType.STRING);
      OProperty property = classA.createProperty("property", OType.STRING);
      property.setReadonly(true);

      ODocument doc = new ODocument(classA);
      doc.field("name", "My Name");
      doc.field("property", "value1");
      db.save(doc);

      doc.field("name", "My Name 2");
      doc.field("property", "value2");
      doc.undo(); // we decided undo everything
      doc.field("name", "My Name 3"); // change something
      db.save(doc);
      doc.field("name", "My Name 4");
      doc.field("property", "value4");
      doc.undo("property"); // we decided undo readonly field
      db.save(doc);
    } finally {
      if (db != null) db.close();
      if (odb != null) {
        odb.drop(dbName);
        odb.close();
      }
    }
  }

  @Test
  public void testSetFieldAtListIndex() {
    ODocument doc = new ODocument();

    Map<String, Object> data = new HashMap<String, Object>();

    List<Object> parentArray = new ArrayList<Object>();
    parentArray.add(1);
    parentArray.add(2);
    parentArray.add(3);

    Map<String, Object> object4 = new HashMap<String, Object>();
    object4.put("prop", "A");
    parentArray.add(object4);

    data.put("array", parentArray);

    doc.field("data", data);

    assertEquals(doc.field("data.array[3].prop"), "A");
    doc.field("data.array[3].prop", "B");

    assertEquals(doc.field("data.array[3].prop"), "B");

    assertEquals(doc.<Object>field("data.array[0]"), 1);
    doc.field("data.array[0]", 5);

    assertEquals(doc.<Object>field("data.array[0]"), 5);
  }

  @Test
  public void testUndo() {
    ODatabaseSession db = null;
    OrientDB odb = null;
    try {
      odb = OCreateDatabaseUtil.createDatabase(dbName, "memory:", OCreateDatabaseUtil.TYPE_MEMORY);
      db = odb.open(dbName, defaultDbAdminCredentials, OCreateDatabaseUtil.NEW_ADMIN_PASSWORD);

      OSchema schema = db.getMetadata().getSchema();
      OClass classA = schema.createClass("TestUndo");
      classA.createProperty("name", OType.STRING);
      classA.createProperty("property", OType.STRING);

      ODocument doc = new ODocument(classA);
      doc.field("name", "My Name");
      doc.field("property", "value1");
      db.save(doc);
      assertEquals(doc.field("name"), "My Name");
      assertEquals(doc.field("property"), "value1");
      doc.undo();
      assertEquals(doc.field("name"), "My Name");
      assertEquals(doc.field("property"), "value1");
      doc.field("name", "My Name 2");
      doc.field("property", "value2");
      doc.undo();
      doc.field("name", "My Name 3");
      assertEquals(doc.field("name"), "My Name 3");
      assertEquals(doc.field("property"), "value1");
      db.save(doc);
      doc.field("name", "My Name 4");
      doc.field("property", "value4");
      doc.undo("property");
      assertEquals(doc.field("name"), "My Name 4");
      assertEquals(doc.field("property"), "value1");
      db.save(doc);
      doc.undo("property");
      assertEquals(doc.field("name"), "My Name 4");
      assertEquals(doc.field("property"), "value1");
      doc.undo();
      assertEquals(doc.field("name"), "My Name 4");
      assertEquals(doc.field("property"), "value1");
    } finally {
      if (db != null) db.close();
      if (odb != null) {
        odb.drop(dbName);
        odb.close();
      }
    }
  }

  @Test
  public void testMergeNull() {
    ODocument dest = new ODocument();

    ODocument source = new ODocument();
    source.field("key", "value");
    source.field("somenull", (Object) null);

    dest.merge(source, true, false);

    assertEquals(dest.field("key"), "value");

    assertTrue(dest.containsField("somenull"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFailNestedSetNull() {
    ODocument doc = new ODocument();
    doc.field("test.nested", "value");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFailNullMapKey() {
    ODocument doc = new ODocument();
    Map<String, String> map = new HashMap<String, String>();
    map.put(null, "dd");
    doc.field("testMap", map);
    doc.convertAllMultiValuesToTrackedVersions();
  }

  @Test
  public void testGetSetProperty() {
    ODocument doc = new ODocument();
    Map<String, String> map = new HashMap<String, String>();
    map.put("foo", "valueInTheMap");
    doc.field("theMap", map);
    doc.setProperty("theMap.foo", "bar");

    assertEquals(doc.getProperty("theMap"), map);
    assertEquals(doc.getProperty("theMap.foo"), "bar");
    assertEquals(doc.eval("theMap.foo"), "valueInTheMap");

    //    doc.setProperty("", "foo");
    //    assertEquals(doc.getProperty(""), "foo");

    doc.setProperty(",", "comma");
    assertEquals(doc.getProperty(","), "comma");

    doc.setProperty(",.,/;:'\"", "strange");
    assertEquals(doc.getProperty(",.,/;:'\""), "strange");

    doc.setProperty("   ", "spaces");
    assertEquals(doc.getProperty("   "), "spaces");
  }

  @Test
  public void testNoDirtySameBytes() {
    ODocument doc = new ODocument();
    byte[] bytes = new byte[] {0, 1, 2, 3, 4, 5};
    doc.field("bytes", bytes);
    ODocumentInternal.clearTrackData(doc);
    ORecordInternal.unsetDirty(doc);
    assertFalse(doc.isDirty());
    assertNull(doc.getOriginalValue("bytes"));
    doc.field("bytes", bytes.clone());
    assertFalse(doc.isDirty());
    assertNull(doc.getOriginalValue("bytes"));

    doc.setProperty("bytes", bytes.clone());
    assertFalse(doc.isDirty());
    assertNull(doc.getOriginalValue("bytes"));
  }

  @Test
  public void testNoDirtySameString() {
    ODocument doc = new ODocument();
    doc.field("string", "value");
    ODocumentInternal.clearTrackData(doc);
    ORecordInternal.unsetDirty(doc);
    assertFalse(doc.isDirty());
    assertNull(doc.getOriginalValue("string"));
    doc.field("string", "value");
    assertFalse(doc.isDirty());
    assertNull(doc.getOriginalValue("string"));
    doc.setProperty("string", "value");
    assertFalse(doc.isDirty());
    assertNull(doc.getOriginalValue("string"));
  }

  @Test
  public void createDocumentFromClassNameString_throwsNoClassDefFoundError() {
    try {
      new ODocument("z[' J~J");
      fail("Expecting exception: NoClassDefFoundError");
    } catch (NoClassDefFoundError e) {
      boolean found = false;
      for (StackTraceElement element : e.getStackTrace()) {
        if ("com.orientechnologies.orient.core.db.document.ODatabaseDocumentAbstract"
            .equals(element.getClassName())) {
          found = true;
          break;
        }
      }
      assertTrue(found);
    }
  }

  @Test
  public void validateLinkCollection_withNullProperty_throwsNPE() {
    TreeMap<LinkedHashMap<Object, Object>, Object> treeMap =
        new TreeMap<LinkedHashMap<Object, Object>, Object>();
    Collection<Object> values = treeMap.values();
    ODocumentEntry entry = new ODocumentEntry();
    ODocumentEntry clonedEntry = entry.clone();
    try {
      ODocument.validateLinkCollection(((OProperty) (null)), values, clonedEntry);
      fail("Expecting exception: NullPointerException");
    } catch (NullPointerException e) {
      boolean found = false;
      for (StackTraceElement element : e.getStackTrace()) {
        if ("com.orientechnologies.orient.core.record.impl.ODocument"
            .equals(element.getClassName())) {
          found = true;
          break;
        }
      }
      assertTrue(found);
    }
  }

  @Test
  public void createDocumentFromMap_throwsNoClassDefFoundError() {
    TreeMap<ODocument, Object> documentToObjectMap = new TreeMap<ODocument, Object>();
    try {
      new ODocument(documentToObjectMap);
      fail("Expecting exception: NoClassDefFoundError");
    } catch (NoClassDefFoundError e) {
      boolean found = false;
      for (StackTraceElement element : e.getStackTrace()) {
        if ("com.orientechnologies.orient.core.db.document.ODatabaseDocumentAbstract"
            .equals(element.getClassName())) {
          found = true;
          break;
        }
      }
      assertTrue(found);
    }
  }

  @Test
  public void createDocumentFromDataInputStream_throwsNoClassDefFoundError() {
    byte[] buffer = new byte[8];
    buffer[7] = ((byte) (59));
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buffer);
    DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
    try {
      new ODocument(dataInputStream);
      fail("Expecting exception: NoClassDefFoundError");
    } catch (NoClassDefFoundError e) {
      boolean found = false;
      for (StackTraceElement element : e.getStackTrace()) {
        if ("com.orientechnologies.orient.core.db.document.ODatabaseDocumentAbstract"
            .equals(element.getClassName())) {
          found = true;
          break;
        }
      }
      assertTrue(found);
    }
  }

  @Test
  public void createDocumentFromObjectArray_throwsNoClassDefFoundError() {
    TreeMap<ODocument, Object> documentMap = new TreeMap<ODocument, Object>();
    Object[] values = new Object[6];
    values[5] = ((Object) (documentMap));
    try {
      new ODocument(values);
      fail("Expecting exception: NoClassDefFoundError");
    } catch (NoClassDefFoundError e) {
      boolean found = false;
      for (StackTraceElement element : e.getStackTrace()) {
        if ("com.orientechnologies.orient.core.db.document.ODatabaseDocumentAbstract"
            .equals(element.getClassName())) {
          found = true;
          break;
        }
      }
      assertTrue(found);
    }
  }

  @Test
  public void validateLink_withNullPropertyAndNullValueAndAllowNullFalse_throwsNPE() {
    try {
      ODocument.validateLink(((OProperty) (null)), ((Object) (null)), false);
      fail("Expecting exception: NullPointerException");
    } catch (NullPointerException e) {
      boolean found = false;
      for (StackTraceElement element : e.getStackTrace()) {
        if ("com.orientechnologies.orient.core.record.impl.ODocument"
            .equals(element.getClassName())) {
          found = true;
          break;
        }
      }
      assertTrue(found);
    }
  }

  @Test
  public void createDocumentWithDatabaseAndClassName_throwsNoClassDefFoundError() {
    try {
      new ODocument(((ODatabaseSession) (null)), "d2/-oe@Metq|J}");
      fail("Expecting exception: NoClassDefFoundError");
    } catch (NoClassDefFoundError e) {
      boolean found = false;
      for (StackTraceElement element : e.getStackTrace()) {
        if ("com.orientechnologies.orient.core.db.document.ODatabaseDocumentAbstract"
            .equals(element.getClassName())) {
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
        if ("com.orientechnologies.orient.core.db.document.ODatabaseDocumentAbstract"
            .equals(element.getClassName())) {
          found = true;
          break;
        }
      }
      assertTrue(found);
    }
  }

  @Test
  public void createDocumentWithDatabaseOnly_throwsNoClassDefFoundError() {
    try {
      new ODocument(((ODatabaseSession) (null)));
      fail("Expecting exception: NoClassDefFoundError");
    } catch (NoClassDefFoundError e) {
      boolean found = false;
      for (StackTraceElement element : e.getStackTrace()) {
        if ("com.orientechnologies.orient.core.db.document.ODatabaseDocumentAbstract"
            .equals(element.getClassName())) {
          found = true;
          break;
        }
      }
      assertTrue(found);
    }
  }

  @Test
  public void validateEmbedded_withNullPropertyAndNullValue_isNoOp() {
    ODocument.validateEmbedded(((OProperty) (null)), ((Object) (null)));
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
        if ("com.orientechnologies.orient.core.record.impl.ODocument"
            .equals(element.getClassName())) {
          found = true;
          break;
        }
      }
      assertTrue(found);
    }
  }

  @Test
  public void validateLink_withNullPropertyAndNullValueAndAllowNullTrue_isNoOp() {
    ODocument.validateLink(((OProperty) (null)), ((Object) (null)), true);
  }

  @Test
  public void validateLink_withNullPropertyAndNonNullValue_throwsNPE() {
    Object nonNullValue = new Object();
    try {
      ODocument.validateLink(((OProperty) (null)), nonNullValue, false);
      fail("Expecting exception: NullPointerException");
    } catch (NullPointerException e) {
      boolean found = false;
      for (StackTraceElement element : e.getStackTrace()) {
        if ("com.orientechnologies.orient.core.record.impl.ODocument"
            .equals(element.getClassName())) {
          found = true;
          break;
        }
      }
      assertTrue(found);
    }
  }

  @Test
  public void createDocumentWithThreeArguments_throwsNoClassDefFoundError() {
    Object[] fields = new Object[0];
    try {
      new ODocument("+M;?", ((Object) (null)), fields);
      fail("Expecting exception: NoClassDefFoundError");
    } catch (NoClassDefFoundError e) {
      boolean found = false;
      for (StackTraceElement element : e.getStackTrace()) {
        if ("com.orientechnologies.orient.core.db.document.ODatabaseDocumentAbstract"
            .equals(element.getClassName())) {
          found = true;
          break;
        }
      }
      assertTrue(found);
    }
  }

  @Test
  public void createDocumentWithOClass_throwsNoClassDefFoundError() {
    try {
      new ODocument(((OClass) (null)));
      fail("Expecting exception: NoClassDefFoundError");
    } catch (NoClassDefFoundError e) {
      boolean found = false;
      for (StackTraceElement element : e.getStackTrace()) {
        if ("com.orientechnologies.orient.core.db.document.ODatabaseDocumentAbstract"
            .equals(element.getClassName())) {
          found = true;
          break;
        }
      }
      assertTrue(found);
    }
  }

  @Test
  public void createEmptyDocument_throwsNoClassDefFoundError() {
    try {
      new ODocument();
      fail("Expecting exception: NoClassDefFoundError");
    } catch (NoClassDefFoundError e) {
      boolean found = false;
      for (StackTraceElement element : e.getStackTrace()) {
        if ("com.orientechnologies.orient.core.db.document.ODatabaseDocumentAbstract"
            .equals(element.getClassName())) {
          found = true;
          break;
        }
      }
      assertTrue(found);
    }
  }

  @Test
  public void createDocumentWithRecordId_throwsNoClassDefFoundError() {
    ORecordId emptyRecordId = ORecordId.EMPTY_RECORD_ID;
    try {
      new ODocument(emptyRecordId);
      fail("Expecting exception: NoClassDefFoundError");
    } catch (NoClassDefFoundError e) {
      boolean found = false;
      for (StackTraceElement element : e.getStackTrace()) {
        if ("com.orientechnologies.orient.core.db.document.ODatabaseDocumentAbstract"
            .equals(element.getClassName())) {
          found = true;
          break;
        }
      }
      assertTrue(found);
    }
  }
}