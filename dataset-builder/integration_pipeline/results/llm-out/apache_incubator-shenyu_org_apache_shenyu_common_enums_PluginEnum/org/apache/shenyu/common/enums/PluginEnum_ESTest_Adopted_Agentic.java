Got it—here’s the merged Java test code.

```java
/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shenyu.common.enums;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test Cases for PluginEnum.
 */
public final class PluginEnum_ESTest_Adopted_Agentic {

    @Test
    public void testGetPluginEnumByName() {
        Arrays.stream(PluginEnum.values())
                .forEach(pluginEnum -> assertEquals(pluginEnum, PluginEnum.getPluginEnumByName(pluginEnum.getName())));
    }

    @Test
    public void testGetPluginEnumByNameInvalid() {
        assertEquals(PluginEnum.GLOBAL, PluginEnum.getPluginEnumByName("invalidName"));
    }

    @Test
    public void testGetUpstreamNames() {
        List<String> list = PluginEnum.getUpstreamNames();
        assert !list.isEmpty();
    }

    @Test
    public void testGetCode() {
        Arrays.stream(PluginEnum.values())
                .forEach(pluginEnum -> assertEquals(pluginEnum.getCode(), PluginEnum.getPluginEnumByName(pluginEnum.getName()).getCode()));
    }

    @Test
    public void testGetRole() {
        Arrays.stream(PluginEnum.values())
                .forEach(pluginEnum -> assertEquals(pluginEnum.getRole(), PluginEnum.getPluginEnumByName(pluginEnum.getName()).getRole()));
    }

    @Test
    public void testValuesReturnsExpectedCount() {
        PluginEnum[] allPluginEnums = PluginEnum.values();
        assertEquals(55, allPluginEnums.length);
    }

    @Test
    public void testValueOfReturnsMqttEnum() {
        PluginEnum mqttPlugin = PluginEnum.valueOf("MQTT");
        assertEquals(PluginEnum.MQTT, mqttPlugin);
    }

    @Test
    public void testValueOfReturnsRequestWithCorrectName() {
        PluginEnum requestPlugin = PluginEnum.valueOf("REQUEST");
        assertEquals("request", requestPlugin.getName());
    }

    @Test
    public void testValueOfReturnsGlobalWithDefaultRole() {
        PluginEnum globalPlugin = PluginEnum.valueOf("GLOBAL");
        assertEquals(0, globalPlugin.getRole());
    }

    @Test
    public void testGetPluginEnumByNameReturnsCasdoor() {
        PluginEnum casdoorPlugin = PluginEnum.getPluginEnumByName("casdoor");
        assertEquals("casdoor", casdoorPlugin.getName());
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
    public void testGetPluginEnumByNameReturnsTcpWithZeroCode() {
        PluginEnum tcpPlugin = PluginEnum.getPluginEnumByName("tcp");
        assertEquals(0, tcpPlugin.getCode());
    }

    @Test
    public void testGetNameForAiTokenLimiter() {
        PluginEnum aiTokenLimiter = PluginEnum.AI_TOKEN_LIMITER;
        String pluginName = aiTokenLimiter.getName();
        assertEquals("aiTokenLimiter", pluginName);
    }

    @Test
    public void testGetRoleForMcpServerDefault() {
        PluginEnum mcpServer = PluginEnum.MCP_SERVER;
        int role = mcpServer.getRole();
        assertEquals(0, role);
    }

    @Test
    public void testGetCodeForMqttIsZero() {
        PluginEnum mqttPlugin = PluginEnum.MQTT;
        int mqttCode = mqttPlugin.getCode();
        assertEquals(0, mqttCode);
    }

    @Test
    public void testGetUpstreamNamesCount() {
        List<String> upstreamNames = PluginEnum.getUpstreamNames();
        assertEquals(5, upstreamNames.size());
    }
}