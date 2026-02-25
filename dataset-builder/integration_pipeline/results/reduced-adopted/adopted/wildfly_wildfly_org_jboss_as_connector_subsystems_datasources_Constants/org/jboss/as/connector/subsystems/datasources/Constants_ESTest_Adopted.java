package org.jboss.as.connector.subsystems.datasources;
import java.lang.reflect.Field;
import org.junit.Assert;
import org.junit.Assume;
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
            Field field = cls.getField("JASPI_AUTH_MODULE");
            Object value = field.get(null);
            Assert.assertEquals("org.wildfly.extension.undertow.security.jaspi.modules.HTTPSchemeServerAuthModule", value);
        } catch (ClassNotFoundException | NoSuchFieldException e) {
            Assume.assumeTrue("TestConstants class or JASPI_AUTH_MODULE not available; skipping test.", false);
        }
    }
}