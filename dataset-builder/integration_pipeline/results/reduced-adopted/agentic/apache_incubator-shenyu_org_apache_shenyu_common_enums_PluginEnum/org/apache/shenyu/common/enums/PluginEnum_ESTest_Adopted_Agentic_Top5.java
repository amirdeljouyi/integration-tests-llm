package org.apache.shenyu.common.enums;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
public final class PluginEnum_ESTest_Adopted_Agentic_Top5 {
    @Test
    public void testGetCode() {
        Arrays.stream(PluginEnum.values()).forEach(pluginEnum -> assertEquals(pluginEnum.getCode(), PluginEnum.getPluginEnumByName(pluginEnum.getName()).getCode()));
    }

    @Test
    public void testGetCodeForSofaIsPositive() {
        PluginEnum sofaPlugin = PluginEnum.SOFA;
        int sofaCode = sofaPlugin.getCode();
        assertEquals(310, sofaCode);
    }

    @Test
    public void testGetCodeForUnknownNameIsNegative() {
        PluginEnum unknownPlugin = PluginEnum.getPluginEnumByName("");
        int unknownCode = unknownPlugin.getCode();
        assertEquals(-1, unknownCode);
    }

    @Test
    public void testGetNameForAiTokenLimiter() {
        PluginEnum aiTokenLimiter = PluginEnum.AI_TOKEN_LIMITER;
        String pluginName = aiTokenLimiter.getName();
        assertEquals("aiTokenLimiter", pluginName);
    }

    @Test
    public void testGetCodeForMqttIsZero() {
        PluginEnum mqttPlugin = PluginEnum.MQTT;
        int mqttCode = mqttPlugin.getCode();
        assertEquals(0, mqttCode);
    }
}