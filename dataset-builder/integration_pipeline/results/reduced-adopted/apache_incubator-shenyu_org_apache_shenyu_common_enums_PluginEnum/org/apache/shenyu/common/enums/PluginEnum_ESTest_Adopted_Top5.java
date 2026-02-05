package org.apache.shenyu.common.enums;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
public final class PluginEnum_ESTest_Adopted_Top5 {
    @Test
    public void testGetPluginEnumByName() {
        Arrays.stream(PluginEnum.values()).forEach(pluginEnum -> assertEquals(pluginEnum, PluginEnum.getPluginEnumByName(pluginEnum.getName())));
    }

    @Test
    public void testGetCode() {
        Arrays.stream(PluginEnum.values()).forEach(pluginEnum -> assertEquals(pluginEnum.getCode(), PluginEnum.getPluginEnumByName(pluginEnum.getName()).getCode()));
    }

    @Test
    public void testGetPluginEnumByNameCasdoorMatchesName() {
        PluginEnum casdoorEnum = PluginEnum.getPluginEnumByName("casdoor");
        assertEquals("casdoor", casdoorEnum.getName());
    }

    @Test
    public void testGetPluginEnumByNameEmptyNameCodeIsMinusOne() {
        PluginEnum pluginEnumForEmptyName = PluginEnum.getPluginEnumByName("");
        int code = pluginEnumForEmptyName.getCode();
        assertEquals(-1, code);
    }

    @Test
    public void testAiTokenLimiterNameMatches() {
        PluginEnum aiTokenLimiterEnum = PluginEnum.AI_TOKEN_LIMITER;
        String name = aiTokenLimiterEnum.getName();
        assertEquals("aiTokenLimiter", name);
    }
}