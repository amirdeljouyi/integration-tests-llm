package org.jboss.as.connector.subsystems.datasources;
public class Constants_ESTest_Adopted {
    @org.junit.Test(timeout = 4000)
    public void shouldInstantiateConstantsWithoutException() throws java.lang.Throwable {
        org.jboss.as.connector.subsystems.datasources.Constants constantsInstance = new org.jboss.as.connector.subsystems.datasources.Constants();
    }

    @org.junit.Test(timeout = 4000)
    public void jaspiAuthModuleConstantMatchesIfPresent() throws java.lang.Throwable {
        try {
            java.lang.Class<?> cls = java.lang.Class.forName("org.wwildfly.test.undertow.common.TestConstants");
            java.lang.reflect.Field field = cls.getField("JASPI_AUTH_MODULE");
            java.lang.Object value = field.get(null);
            org.junit.Assert.assertEquals("org.wildfly.extension.undertow.security.jaspi.modules.HTTPSchemeServerAuthModule", value);
        } catch (java.lang.ClassNotFoundException | java.lang.NoSuchFieldException e) {
            org.junit.Assume.assumeTrue("TestConstants class or JASPI_AUTH_MODULE not available; skipping test.", false);
        }
    }
}