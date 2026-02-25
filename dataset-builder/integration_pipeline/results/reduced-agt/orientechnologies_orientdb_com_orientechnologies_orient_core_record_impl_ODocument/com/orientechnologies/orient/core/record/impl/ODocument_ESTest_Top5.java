package com.orientechnologies.orient.core.record.impl;
import com.orientechnologies.orient.core.record.impl.ODocument_ESTest_scaffolding;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import java.util.Comparator;
import java.util.TreeMap;
import org.junit.Test;
import static org.evosuite.runtime.EvoAssertions.verifyException;
import static org.junit.Assert.fail;
public class ODocument_ESTest_Top5 extends ODocument_ESTest_scaffolding {
    @Test(timeout = 4000)
    public void testFailsToCreateODocumentTakingStringThrowsNoClassDefFoundError() throws Throwable {
        ODocument oDocument0 = null;
        try {
            oDocument0 = new ODocument("z[' J~J");
            fail("Expecting exception: NoClassDefFoundError");
        } catch (NoClassDefFoundError e) {
            // 
            // Could not initialize class com.orientechnologies.orient.core.serialization.serializer.record.ORecordSerializerFactory
            // 
            verifyException("com.orientechnologies.orient.core.db.document.ODatabaseDocumentAbstract", e);
        }
    }

    @Test(timeout = 4000)
    public void testFailsToCreateODocumentTakingMapThrowsNoClassDefFoundError() throws Throwable {
        Object object0 = new Object();
        TreeMap<ODocument, Object> treeMap0 = new TreeMap<ODocument, Object>();
        ODocument oDocument0 = null;
        try {
            oDocument0 = new ODocument(treeMap0);
            fail("Expecting exception: NoClassDefFoundError");
        } catch (NoClassDefFoundError e) {
            // 
            // Could not initialize class com.orientechnologies.orient.core.serialization.serializer.record.ORecordSerializerFactory
            // 
            verifyException("com.orientechnologies.orient.core.db.document.ODatabaseDocumentAbstract", e);
        }
    }

    @Test(timeout = 4000)
    public void testFailsToCreateODocumentTakingODatabaseSessionThrowsNoClassDefFoundError() throws Throwable {
        Comparator.reverseOrder();
        ODocument oDocument0 = null;
        try {
            oDocument0 = new ODocument(((ODatabaseSession) (null)));
            fail("Expecting exception: NoClassDefFoundError");
        } catch (NoClassDefFoundError e) {
            // 
            // Could not initialize class com.orientechnologies.orient.core.serialization.serializer.record.ORecordSerializerFactory
            // 
            verifyException("com.orientechnologies.orient.core.db.document.ODatabaseDocumentAbstract", e);
        }
    }

    @Test(timeout = 4000)
    public void testFailsToCreateODocumentTakingOClassThrowsNoClassDefFoundError() throws Throwable {
        ODocument oDocument0 = null;
        try {
            oDocument0 = new ODocument(((OClass) (null)));
            fail("Expecting exception: NoClassDefFoundError");
        } catch (NoClassDefFoundError e) {
            // 
            // Could not initialize class com.orientechnologies.orient.core.serialization.serializer.record.ORecordSerializerFactory
            // 
            verifyException("com.orientechnologies.orient.core.db.document.ODatabaseDocumentAbstract", e);
        }
    }

    @Test(timeout = 4000)
    public void testFailsToCreateODocumentTakingORIDThrowsNoClassDefFoundError() throws Throwable {
        ORecordId oRecordId0 = ORecordId.EMPTY_RECORD_ID;
        ODocument oDocument0 = null;
        try {
            oDocument0 = new ODocument(oRecordId0);
            fail("Expecting exception: NoClassDefFoundError");
        } catch (NoClassDefFoundError e) {
            // 
            // Could not initialize class com.orientechnologies.orient.core.serialization.serializer.record.ORecordSerializerFactory
            // 
            verifyException("com.orientechnologies.orient.core.db.document.ODatabaseDocumentAbstract", e);
        }
    }
}
