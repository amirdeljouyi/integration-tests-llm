package org.apache.shenyu.common.enums;
import org.apache.shenyu.common.enums.PluginEnum_ESTest_scaffolding;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
public class PluginEnum_ESTest_Top5 extends PluginEnum_ESTest_scaffolding {
    @Test(timeout = 4000)
    public void testGetPluginEnumByNameReturningPluginEnumWhereGetCodeIsPositive() throws Throwable {
        PluginEnum pluginEnum0 = PluginEnum.getPluginEnumByName("casdoor");
        assertEquals("casdoor", pluginEnum0.getName());
    }

    @Test(timeout = 4000)
    public void testGetCodeReturningPositive() throws Throwable {
        PluginEnum pluginEnum0 = PluginEnum.SOFA;
        int int0 = pluginEnum0.getCode();
        assertEquals(310, int0);
    }

    @Test(timeout = 4000)
    public void testGetCodeReturningNegative() throws Throwable {
        PluginEnum pluginEnum0 = PluginEnum.getPluginEnumByName("");
        int int0 = pluginEnum0.getCode();
        assertEquals(-1, int0);
    }

    @Test(timeout = 4000)
    public void testGetName() throws Throwable {
        PluginEnum pluginEnum0 = PluginEnum.AI_TOKEN_LIMITER;
        String string0 = pluginEnum0.getName();
        assertEquals("aiTokenLimiter", string0);
    }

    @Test(timeout = 4000)
    public void testGetCodeReturningZero() throws Throwable {
        PluginEnum pluginEnum0 = PluginEnum.MQTT;
        int int0 = pluginEnum0.getCode();
        assertEquals(0, int0);
    }
}
