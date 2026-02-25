package org.apache.shenyu.common.enums;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
public class PluginEnum_ESTest_Adopted_Top5 {
    @Test(timeout = 4000)
    public void testGetCodeForSofaIsPositive() throws Throwable {
        PluginEnum sofaPlugin = PluginEnum.SOFA;
        int sofaCode = sofaPlugin.getCode();
        assertEquals(310, sofaCode);
    }

    @Test(timeout = 4000)
    public void testGetCodeForUnknownNameIsNegative() throws Throwable {
        PluginEnum unknownPlugin = PluginEnum.getPluginEnumByName("");
        int unknownCode = unknownPlugin.getCode();
        assertEquals(-1, unknownCode);
    }

    @Test(timeout = 4000)
    public void testGetNameForAiTokenLimiter() throws Throwable {
        PluginEnum aiTokenLimiter = PluginEnum.AI_TOKEN_LIMITER;
        String pluginName = aiTokenLimiter.getName();
        assertEquals("aiTokenLimiter", pluginName);
    }

    @Test(timeout = 4000)
    public void testGetCodeForMqttIsZero() throws Throwable {
        PluginEnum mqttPlugin = PluginEnum.MQTT;
        int mqttCode = mqttPlugin.getCode();
        assertEquals(0, mqttCode);
    }

    @Test(timeout = 4000)
    public void testGetCodeMatchesLookupByName() throws Throwable {
        for (PluginEnum pluginEnum : PluginEnum.values()) {
            assertEquals(pluginEnum.getCode(), PluginEnum.getPluginEnumByName(pluginEnum.getName()).getCode());
        }
    }
}