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

/**
 * Corresponding manual test: {@link org.apache.seata.config.FileConfigurationTest}.
 * Manual test source on GitHub: <a href="https://github.com/seata/seata/blob/6160c7dce931c98522a3788ed2bd5440a87fa4e7/config/seata-config-core/src/test/java/org/apache/seata/config/FileConfigurationTest.java">FileConfigurationTest</a>.
 * @see org.apache.seata.config.FileConfigurationTest
 */
class FileConfiguration_ESTest_Adopted_Agentic_Top5 {
    // Logger logger = LoggerFactory.getLogger(FileConfigurationTest.class);
    @BeforeAll
    static void setUp() {
        System.setProperty("file.listener.enabled", "true");
        ConfigurationCache.clear();
    }

    @AfterAll
    static void tearDown() {
        ConfigurationCache.clear();
        System.setProperty("file.listener.enabled", "true");
    }

    /**
     * This test added target-class coverage 31.58% for io.seata.config.FileConfiguration (6/19 lines).
     * Delta details: +84 methods, +112 branches, +1353 instructions.
     * Full version of the covered block is here: <a href="https://github.com/seata/seata/blob/6160c7dce931c98522a3788ed2bd5440a87fa4e7/config/seata-config-core/src/main/java/io/seata/config/FileConfiguration.java#L49-L51">FileConfiguration.java (lines 49-51)</a>
     * Covered Lines:
     * <pre><code>
     *     public FileConfiguration(org.apache.seata.config.Configuration target) {
     *         this.target = (org.apache.seata.config.FileConfiguration) target;
     *     }
     * </code></pre>
     * Other newly covered ranges to check: 81;86;91
     */
    @Test
    void testPublishChangeEventsAndPutConfigsViaWrappedConfiguration() {
        org.apache.seata.config.FileConfiguration apacheFileConfig = new org.apache.seata.config.FileConfiguration(".yK>", true);
        io.seata.config.FileConfiguration delegatingFileConfig = new io.seata.config.FileConfiguration(apacheFileConfig);
        ConfigurationCache configurationCache = ConfigurationCache.getInstance();
        configurationCache.onShutDown();
        apacheFileConfig.getLong("iaOs!", -1405L, 1000L);
        ConfigurationChangeType initialChangeType = ConfigurationChangeType.MODIFY;
        org.apache.seata.config.ConfigurationChangeEvent changeEvent = new org.apache.seata.config.ConfigurationChangeEvent(".yK>", "-#4N?8/BKLTG1Is/y", "", "2i,+EX.Y8", initialChangeType);
        ConfigurationChangeType addChangeType = ConfigurationChangeType.ADD;
        changeEvent.setChangeType(addChangeType);
        ConfigurationChangeEvent changeEventAfterUpdate = changeEvent.setChangeType(initialChangeType);
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

    /**
     * This test added target-class coverage 0.00% for io.seata.config.FileConfiguration (0/19 lines).
     * Delta details: +60 methods, +102 branches, +1171 instructions.
     */
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

    /**
     * This test added target-class coverage 0.00% for io.seata.config.FileConfiguration (0/19 lines).
     * Delta details: +39 methods, +62 branches, +855 instructions.
     */
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

    /**
     * This test added target-class coverage 0.00% for io.seata.config.FileConfiguration (0/19 lines).
     * Delta details: +6 methods, +14 branches, +131 instructions.
     */
    @Test
    void testCreateWithNameAndLoadOnStartThenRemoveConfig() {
        org.apache.seata.config.FileConfiguration fileConfig = new org.apache.seata.config.FileConfiguration("J8=rMZJZ", true);
        fileConfig.getConfig("6KV{OJCIW", -62L);
        boolean removedExistingNameResult = fileConfig.removeConfig("J8=rMZJZ", 1L);
        Assertions.assertTrue(removedExistingNameResult);
    }

    /**
     * This test added target-class coverage 21.05% for io.seata.config.FileConfiguration (4/19 lines).
     * Delta details: +5 methods, +0 branches, +20 instructions.
     * Full version of the covered block is here: <a href="https://github.com/seata/seata/blob/6160c7dce931c98522a3788ed2bd5440a87fa4e7/config/seata-config-core/src/main/java/io/seata/config/FileConfiguration.java#L49-L51">FileConfiguration.java (lines 49-51)</a>
     * Covered Lines:
     * <pre><code>
     *     public FileConfiguration(org.apache.seata.config.Configuration target) {
     *         this.target = (org.apache.seata.config.FileConfiguration) target;
     *     }
     * </code></pre>
     * Other newly covered ranges to check: 76
     */
    @Test
    void testConstructingFromAnotherConfigurationKeepsTypeName() {
        FileConfiguration apacheFileConfig = new FileConfiguration();
        io.seata.config.FileConfiguration wrappedFileConfig = new io.seata.config.FileConfiguration(apacheFileConfig);
        String typeName = wrappedFileConfig.getTypeName();
        Assertions.assertEquals("file", typeName);
    }
}
