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

package org.apache.seata.config;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

class FileConfiguration_ESTest_Adopted_Agentic_Top5 {
    // Logger logger = LoggerFactory.getLogger(FileConfigurationTest.class);
    @BeforeAll
    static void setUp() {
        System.setProperty("file.listener.enabled", "true");
        org.apache.seata.config.ConfigurationCache.clear();
    }

    @AfterAll
    static void tearDown() {
        org.apache.seata.config.ConfigurationCache.clear();
        System.setProperty("file.listener.enabled", "true");
    }

    @Test

    /**
     * This test added coverage 40.00% (318/795 added lines among kept tests).
     * Delta details: +84 methods, +112 branches, +1353 instructions.
     * Full version of the covered block is here: <a href="https://github.com/seata/seata/blob/main/config/seata-config-core/src/main/java/org/apache/seata/config/ConfigurationChangeEvent.java#L39-L45">ConfigurationChangeEvent.java (lines 39-45)</a>
     * Covered Lines:
     * <pre><code>
     * <span style="background-color:#fff3b0;">            String dataId, String namespace, String oldValue, String newValue, ConfigurationChangeType type) {</span>
     * <span style="background-color:#fff3b0;">        this.dataId = dataId;</span>
     * <span style="background-color:#fff3b0;">        this.namespace = namespace;</span>
     * <span style="background-color:#fff3b0;">        this.oldValue = oldValue;</span>
     * <span style="background-color:#fff3b0;">        this.newValue = newValue;</span>
     * <span style="background-color:#fff3b0;">        this.changeType = type;</span>
     * <span style="background-color:#fff3b0;">    }</span>
     * </code></pre>
     * Full version of the covered block is here: <a href="https://github.com/seata/seata/blob/main/config/seata-config-core/src/main/java/io/seata/config/AbstractConfiguration.java#L28-L28">AbstractConfiguration.java (lines 28-28)</a>
     * Covered Lines:
     * <pre><code>
     * <span style="background-color:#fff3b0;">public abstract class AbstractConfiguration implements Configuration {</span>
     * </code></pre>
     * Additional covered classes omitted: 30
     */
    void testPublishChangeEventsAndPutConfigsViaWrappedConfiguration() {
        org.apache.seata.config.FileConfiguration apacheFileConfig = new org.apache.seata.config.FileConfiguration(".yK>", true);
        io.seata.config.FileConfiguration delegatingFileConfig = new io.seata.config.FileConfiguration(apacheFileConfig);
        org.apache.seata.config.ConfigurationCache configurationCache = org.apache.seata.config.ConfigurationCache.getInstance();
        configurationCache.onShutDown();
        apacheFileConfig.getLong("iaOs!", -1405L, 1000L);
        org.apache.seata.config.ConfigurationChangeType initialChangeType = ConfigurationChangeType.MODIFY;
        org.apache.seata.config.ConfigurationChangeEvent changeEvent = new org.apache.seata.config.ConfigurationChangeEvent(".yK>", "-#4N?8/BKLTG1Is/y", "", "2i,+EX.Y8", initialChangeType);
        org.apache.seata.config.ConfigurationChangeType addChangeType = ConfigurationChangeType.ADD;
        changeEvent.setChangeType(addChangeType);
        org.apache.seata.config.ConfigurationChangeEvent changeEventAfterUpdate = changeEvent.setChangeType(initialChangeType);
        configurationCache.onChangeEvent(changeEventAfterUpdate);
        apacheFileConfig.getInt("yR[!#", -304, 0L);
        delegatingFileConfig.getInt("Dg2lQqjtt7Se=Gt");
        apacheFileConfig.removeConfigListener("", configurationCache);
        apacheFileConfig.getLatestConfig("", "hx63!oO^v)$Nr^sX", 0L);
        boolean firstPutResult = delegatingFileConfig.putConfig("", "The file name of the operation is {}", 5000L);
        Assertions.assertTrue(firstPutResult);
        boolean putIfAbsentResult = delegatingFileConfig.putConfigIfAbsent("", "iaOs!", -1405L);
        Assertions.assertFalse(putIfAbsentResult);
        boolean secondPutResult = delegatingFileConfig.putConfig("", "", -1405L);
        Assertions.assertFalse(secondPutResult);
    }

    @Test
    void testHandleLatestConfigQueriesNullsAndRemovals() {
        org.apache.seata.config.FileConfiguration fileConfig = new org.apache.seata.config.FileConfiguration("-bvOY");
        fileConfig.getInt("", 39, 39L);
        fileConfig.getShort("[&hV{&2oIr", ((short) (2)));
        boolean putInitialConfigResult = fileConfig.putConfig("^", "-bvOY", -1027L);
        Assertions.assertFalse(putInitialConfigResult);
        fileConfig.getConfig("RJm", -1027L);
        fileConfig.getInt(((String) (null)));
        fileConfig.getLatestConfig(null, "", -1027L);
        fileConfig.getConfig("q`=3Ac");
        fileConfig.getLatestConfig(null, null, 1790L);
        fileConfig.removeConfig(null, 1790L);
        fileConfig.getTypeName();
        boolean removedNullKeyWithZeroTimeout = fileConfig.removeConfig(null, 0L);
        Assertions.assertFalse(removedNullKeyWithZeroTimeout);
        boolean putIfAbsentEmptyKeyNullValueResult = fileConfig.putConfigIfAbsent("", null, 0L);
        Assertions.assertFalse(putIfAbsentEmptyKeyNullValueResult);
        boolean removedSpecificKeyWithLongTimeout = fileConfig.removeConfig("ZV\u007fL>wk3", 5000L);
        Assertions.assertTrue(removedSpecificKeyWithLongTimeout);
        boolean putEmptyValueZeroTimeoutResult = fileConfig.putConfig("cjmA2cJ{P>=+`Fz!R", "", 0L);
        Assertions.assertFalse(putEmptyValueZeroTimeoutResult);
    }

    @Test
    void testPutIfAbsentAndRemoveConfigAfterLatestConfigQueries() {
        org.apache.seata.config.FileConfiguration fileConfig = new org.apache.seata.config.FileConfiguration("");
        fileConfig.getInt("", -715, 0L);
        fileConfig.getLatestConfig("get", "", 2962L);
        fileConfig.removeConfig("", 237L);
        fileConfig.getBoolean("", false);
        boolean putIfAbsentResult = fileConfig.putConfigIfAbsent("NazOup';Y8", null, 2962L);
        Assertions.assertTrue(putIfAbsentResult);
        fileConfig.getTypeName();
        fileConfig.getLatestConfig("", "A~V&TciIbZ7$^Kb", 2962L);
        boolean removedQuestionKey = fileConfig.removeConfig("?/L", 2962L);
        Assertions.assertTrue(removedQuestionKey);
        boolean removedNazOupKey = fileConfig.removeConfig("NazOup';Y8", 0L);
        Assertions.assertFalse(removedNazOupKey);
    }

    @Test
    void testCreateWithNameAndLoadOnStartThenRemoveConfig() {
        org.apache.seata.config.FileConfiguration fileConfig = new org.apache.seata.config.FileConfiguration("J8=rMZJZ", true);
        fileConfig.getConfig("6KV{OJCIW", -62L);
        boolean removedExistingNameResult = fileConfig.removeConfig("J8=rMZJZ", 1L);
        Assertions.assertTrue(removedExistingNameResult);
    }

    @Test
    void testConstructingFromAnotherConfigurationKeepsTypeName() {
        org.apache.seata.config.FileConfiguration apacheFileConfig = new org.apache.seata.config.FileConfiguration();
        io.seata.config.FileConfiguration wrappedFileConfig = new io.seata.config.FileConfiguration(apacheFileConfig);
        String typeName = wrappedFileConfig.getTypeName();
        Assertions.assertEquals("file", typeName);
    }
}
