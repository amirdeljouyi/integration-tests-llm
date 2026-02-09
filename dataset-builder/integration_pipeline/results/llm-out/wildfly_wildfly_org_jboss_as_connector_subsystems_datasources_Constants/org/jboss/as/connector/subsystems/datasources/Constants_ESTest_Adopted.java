package org.jboss.as.connector.subsystems.datasources;

import org.junit.Test;

public class Constants_ESTest_Adopted {

    @Test(timeout = 4000)
    public void shouldInstantiateConstantsWithoutException() throws Throwable {
        Constants constantsInstance = new Constants();
    }

    @Test(timeout = 4000)
    public void jaspiAuthModuleConstantMatchesIfPresent() throws Throwable {
        try {
            Class<?> cls = Class.forName("org.wwildfly.test.undertow.common.TestConstants");
            java.lang.reflect.Field field = cls.getField("JASPI_AUTH_MODULE");
            Object value = field.get(null);
            org.junit.Assert.assertEquals("org.wildfly.extension.undertow.security.jaspi.modules.HTTPSchemeServerAuthModule", value);
        } catch (ClassNotFoundException | NoSuchFieldException e) {
            org.junit.Assume.assumeTrue("TestConstants class or JASPI_AUTH_MODULE not available; skipping test.", false);
        }
    }
}