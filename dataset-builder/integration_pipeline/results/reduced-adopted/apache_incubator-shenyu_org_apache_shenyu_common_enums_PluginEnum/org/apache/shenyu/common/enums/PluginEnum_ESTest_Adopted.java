/* Licensed to the Apache Software Foundation (ASF) under one or more
contributor license agreements.  See the NOTICE file distributed with
this work for additional information regarding copyright ownership.
The ASF licenses this file to You under the Apache License, Version 2.0
(the "License"); you may not use this file except in compliance with
the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package org.apache.shenyu.common.enums;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
/**
 * Test Cases for PluginEnum.
 */
public final class PluginEnum_ESTest_Adopted {
    @Test
    public void testGetPluginEnumByName() {
        Arrays.stream(PluginEnum.values()).forEach(pluginEnum -> assertEquals(pluginEnum, PluginEnum.getPluginEnumByName(pluginEnum.getName())));
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
        Arrays.stream(PluginEnum.values()).forEach(pluginEnum -> assertEquals(pluginEnum.getCode(), PluginEnum.getPluginEnumByName(pluginEnum.getName()).getCode()));
    }

    @Test
    public void testGetRole() {
        Arrays.stream(PluginEnum.values()).forEach(pluginEnum -> assertEquals(pluginEnum.getRole(), PluginEnum.getPluginEnumByName(pluginEnum.getName()).getRole()));
    }

    // Adapted IGT tests
    @Test
    public void testValuesCountIs55() {
        PluginEnum[] pluginEnums = PluginEnum.values();
        assertEquals(55, pluginEnums.length);
    }

    @Test
    public void testValueOfReturnsMQTT() {
        PluginEnum mqttEnum = PluginEnum.valueOf("MQTT");
        assertEquals(PluginEnum.MQTT, mqttEnum);
    }

    @Test
    public void testValueOfRequestHasNameRequest() {
        PluginEnum requestEnum = PluginEnum.valueOf("REQUEST");
        assertEquals("request", requestEnum.getName());
    }

    @Test
    public void testValueOfGlobalHasRoleZero() {
        PluginEnum globalEnum = PluginEnum.valueOf("GLOBAL");
        assertEquals(0, globalEnum.getRole());
    }

    @Test
    public void testGetPluginEnumByNameCasdoorMatchesName() {
        PluginEnum casdoorEnum = PluginEnum.getPluginEnumByName("casdoor");
        assertEquals("casdoor", casdoorEnum.getName());
    }

    @Test
    public void testSofaCodeIs310() {
        PluginEnum sofaEnum = PluginEnum.SOFA;
        int code = sofaEnum.getCode();
        assertEquals(310, code);
    }

    @Test
    public void testGetPluginEnumByNameEmptyNameCodeIsMinusOne() {
        PluginEnum pluginEnumForEmptyName = PluginEnum.getPluginEnumByName("");
        int code = pluginEnumForEmptyName.getCode();
        assertEquals(-1, code);
    }

    @Test
    public void testGetPluginEnumByNameTcpCodeIsZero() {
        PluginEnum tcpEnum = PluginEnum.getPluginEnumByName("tcp");
        assertEquals(0, tcpEnum.getCode());
    }

    @Test
    public void testAiTokenLimiterNameMatches() {
        PluginEnum aiTokenLimiterEnum = PluginEnum.AI_TOKEN_LIMITER;
        String name = aiTokenLimiterEnum.getName();
        assertEquals("aiTokenLimiter", name);
    }

    @Test
    public void testMcpServerRoleIsZero() {
        PluginEnum mcpServerEnum = PluginEnum.MCP_SERVER;
        int role = mcpServerEnum.getRole();
        assertEquals(0, role);
    }

    @Test
    public void testMqttCodeIsZero() {
        PluginEnum mqttEnum = PluginEnum.MQTT;
        int code = mqttEnum.getCode();
        assertEquals(0, code);
    }

    @Test
    public void testGetUpstreamNamesSizeIsFive() {
        List<String> upstreamNames = PluginEnum.getUpstreamNames();
        assertEquals(5, upstreamNames.size());
    }
}