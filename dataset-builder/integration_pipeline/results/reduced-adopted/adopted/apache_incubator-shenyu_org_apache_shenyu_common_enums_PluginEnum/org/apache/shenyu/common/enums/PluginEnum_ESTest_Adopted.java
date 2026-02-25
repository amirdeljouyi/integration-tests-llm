package org.apache.shenyu.common.enums;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
public class PluginEnum_ESTest_Adopted {
    @Test(timeout = 4000)
    public void testValuesReturnsExpectedCount() throws Throwable {
        PluginEnum[] allPluginEnums = PluginEnum.values();
        assertEquals(55, allPluginEnums.length);
    }

    @Test(timeout = 4000)
    public void testValueOfReturnsMqttEnum() throws Throwable {
        PluginEnum mqttPlugin = PluginEnum.valueOf("MQTT");
        assertEquals(PluginEnum.MQTT, mqttPlugin);
    }

    @Test(timeout = 4000)
    public void testValueOfReturnsRequestWithCorrectName() throws Throwable {
        PluginEnum requestPlugin = PluginEnum.valueOf("REQUEST");
        assertEquals("request", requestPlugin.getName());
    }

    @Test(timeout = 4000)
    public void testValueOfReturnsGlobalWithDefaultRole() throws Throwable {
        PluginEnum globalPlugin = PluginEnum.valueOf("GLOBAL");
        assertEquals(0, globalPlugin.getRole());
    }

    @Test(timeout = 4000)
    public void testGetPluginEnumByNameReturnsCasdoor() throws Throwable {
        PluginEnum casdoorPlugin = PluginEnum.getPluginEnumByName("casdoor");
        assertEquals("casdoor", casdoorPlugin.getName());
    }

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
    public void testGetPluginEnumByNameReturnsTcpWithZeroCode() throws Throwable {
        PluginEnum tcpPlugin = PluginEnum.getPluginEnumByName("tcp");
        assertEquals(0, tcpPlugin.getCode());
    }

    @Test(timeout = 4000)
    public void testGetNameForAiTokenLimiter() throws Throwable {
        PluginEnum aiTokenLimiter = PluginEnum.AI_TOKEN_LIMITER;
        String pluginName = aiTokenLimiter.getName();
        assertEquals("aiTokenLimiter", pluginName);
    }

    @Test(timeout = 4000)
    public void testGetRoleForMcpServerDefault() throws Throwable {
        PluginEnum mcpServer = PluginEnum.MCP_SERVER;
        int role = mcpServer.getRole();
        assertEquals(0, role);
    }

    @Test(timeout = 4000)
    public void testGetCodeForMqttIsZero() throws Throwable {
        PluginEnum mqttPlugin = PluginEnum.MQTT;
        int mqttCode = mqttPlugin.getCode();
        assertEquals(0, mqttCode);
    }

    @Test(timeout = 4000)
    public void testGetUpstreamNamesCount() throws Throwable {
        List<String> upstreamNames = PluginEnum.getUpstreamNames();
        assertEquals(5, upstreamNames.size());
    }

    // Adapted IGT tests
    @Test(timeout = 4000)
    public void testGetPluginEnumByNameMatchesAllValues() throws Throwable {
        for (PluginEnum pluginEnum : PluginEnum.values()) {
            assertEquals(pluginEnum, PluginEnum.getPluginEnumByName(pluginEnum.getName()));
        }
    }

    @Test(timeout = 4000)
    public void testGetPluginEnumByNameWithInvalidReturnsGlobal() throws Throwable {
        assertEquals(PluginEnum.GLOBAL, PluginEnum.getPluginEnumByName("invalidName"));
    }

    @Test(timeout = 4000)
    public void testGetUpstreamNamesNotEmpty() throws Throwable {
        List<String> list = PluginEnum.getUpstreamNames();
        assertTrue(list.size() > 0);
    }

    @Test(timeout = 4000)
    public void testGetCodeMatchesLookupByName() throws Throwable {
        for (PluginEnum pluginEnum : PluginEnum.values()) {
            assertEquals(pluginEnum.getCode(), PluginEnum.getPluginEnumByName(pluginEnum.getName()).getCode());
        }
    }

    @Test(timeout = 4000)
    public void testGetRoleMatchesLookupByName() throws Throwable {
        for (PluginEnum pluginEnum : PluginEnum.values()) {
            assertEquals(pluginEnum.getRole(), PluginEnum.getPluginEnumByName(pluginEnum.getName()).getRole());
        }
    }
}