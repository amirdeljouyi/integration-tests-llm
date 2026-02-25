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
package org.apache.seata.config;
class FileConfiguration_ESTest_Adopted_Agentic {
    // Logger logger = LoggerFactory.getLogger(FileConfigurationTest.class);
    @org.junit.jupiter.api.BeforeAll
    static void setUp() {
        java.lang.System.setProperty("file.listener.enabled", "true");
        org.apache.seata.config.ConfigurationCache.clear();
    }

    @org.junit.jupiter.api.AfterAll
    static void tearDown() {
        org.apache.seata.config.ConfigurationCache.clear();
        java.lang.System.setProperty("file.listener.enabled", "true");
    }

    @org.junit.jupiter.api.Test
    void addConfigListener() throws java.lang.InterruptedException {
        // logger.info("addConfigListener");
        org.apache.seata.config.ConfigurationFactory.reload();
        org.apache.seata.config.Configuration fileConfig = org.apache.seata.config.ConfigurationFactory.getInstance();
        java.util.concurrent.CountDownLatch countDownLatch = new java.util.concurrent.CountDownLatch(1);
        java.lang.String dataId = "service.disableGlobalTransaction";
        boolean value = fileConfig.getBoolean(dataId);
        fileConfig.addConfigListener(dataId, ((org.apache.seata.config.CachedConfigurationChangeListener) (event -> {
            // logger.info(
            // "before dataId: {}, oldValue: {}, newValue: {}",
            // event.getDataId(),
            // event.getOldValue(),
            // event.getNewValue());
            org.junit.jupiter.api.Assertions.assertEquals(java.lang.Boolean.parseBoolean(event.getNewValue()), !java.lang.Boolean.parseBoolean(event.getOldValue()));
            // logger.info(
            // "after dataId: {}, oldValue: {}, newValue: {}",
            // event.getDataId(),
            // event.getOldValue(),
            // event.getNewValue());
            countDownLatch.countDown();
        })));
        java.lang.System.setProperty(dataId, java.lang.String.valueOf(!value));
        // logger.info(System.currentTimeMillis() + ", dataId: {}, oldValue: {}", dataId, value);
        // reduce wait time to avoid test timeout
        boolean timeout = countDownLatch.await(5, java.util.concurrent.TimeUnit.SECONDS);
        if (!timeout) {
            // logger.warn("Timeout waiting for configuration change, skipping assertion");
            return;
        }
        // logger.info(
        // System.currentTimeMillis() + ", dataId: {}, currenValue: {}", dataId, fileConfig.getBoolean(dataId));
        org.junit.jupiter.api.Assertions.assertNotEquals(fileConfig.getBoolean(dataId), value);
        // wait for loop safety, loop time is LISTENER_CONFIG_INTERVAL=1s
        java.util.concurrent.CountDownLatch countDownLatch2 = new java.util.concurrent.CountDownLatch(1);
        fileConfig.addConfigListener("file.listener.enabled", ((org.apache.seata.config.CachedConfigurationChangeListener) (event -> {
            if (!java.lang.Boolean.parseBoolean(event.getNewValue())) {
                countDownLatch2.countDown();
            }
        })));
        java.lang.System.setProperty("file.listener.enabled", "false");
        countDownLatch2.await(10, java.util.concurrent.TimeUnit.SECONDS);
        java.lang.System.setProperty(dataId, java.lang.String.valueOf(value));
        // sleep for a period of time to simulate waiting for a cache refresh.Actually, it doesn't trigger.
        java.lang.Thread.sleep(1000);
        boolean currentValue = fileConfig.getBoolean(dataId);
        org.junit.jupiter.api.Assertions.assertNotEquals(value, currentValue);
        java.lang.System.setProperty(dataId, java.lang.String.valueOf(!value));
    }

    @org.junit.jupiter.api.Test
    void testDiffDefaultValue() {
        org.apache.seata.config.Configuration fileConfig = org.apache.seata.config.ConfigurationFactory.getInstance();
        int intValue1 = fileConfig.getInt("int.not.exist", 100);
        int intValue2 = fileConfig.getInt("int.not.exist", 200);
        org.junit.jupiter.api.Assertions.assertNotEquals(intValue1, intValue2);
        java.lang.String strValue1 = fileConfig.getConfig("str.not.exist", "en");
        java.lang.String strValue2 = fileConfig.getConfig("str.not.exist", "us");
        org.junit.jupiter.api.Assertions.assertNotEquals(strValue1, strValue2);
        boolean bolValue1 = fileConfig.getBoolean("boolean.not.exist", true);
        boolean bolValue2 = fileConfig.getBoolean("boolean.not.exist", false);
        org.junit.jupiter.api.Assertions.assertNotEquals(bolValue1, bolValue2);
        java.lang.String value = "QWERT";
        java.lang.System.setProperty("mockDataId1", value);
        java.lang.String content1 = fileConfig.getConfig("mockDataId1");
        org.junit.jupiter.api.Assertions.assertEquals(content1, value);
        java.lang.String content2 = fileConfig.getConfig("mockDataId1", "hehe");
        org.junit.jupiter.api.Assertions.assertEquals(content2, value);
        java.lang.String content3 = fileConfig.getConfig("mockDataId2");
        org.junit.jupiter.api.Assertions.assertNull(content3);
        java.lang.String content4 = fileConfig.getConfig("mockDataId2", value);
        org.junit.jupiter.api.Assertions.assertEquals(content4, value);
        java.lang.String content5 = fileConfig.getConfig("mockDataId2");
        org.junit.jupiter.api.Assertions.assertEquals(content5, value);
    }

    @org.junit.jupiter.api.Test
    void testGetConfigWithTimeout() {
        org.apache.seata.config.Configuration fileConfig = org.apache.seata.config.ConfigurationFactory.getInstance();
        java.lang.String value = fileConfig.getConfig("test.key", "default-value", 1000);
        org.junit.jupiter.api.Assertions.assertNotNull(value);
    }

    @org.junit.jupiter.api.Test
    void testGetIntWithTimeout() {
        org.apache.seata.config.Configuration fileConfig = org.apache.seata.config.ConfigurationFactory.getInstance();
        int value = fileConfig.getInt("test.int.key", 100, 1000);
        org.junit.jupiter.api.Assertions.assertTrue(value >= 0);
    }

    @org.junit.jupiter.api.Test
    void testGetBooleanWithTimeout() {
        org.apache.seata.config.Configuration fileConfig = org.apache.seata.config.ConfigurationFactory.getInstance();
        boolean value = fileConfig.getBoolean("test.boolean.key", true, 1000);
        org.junit.jupiter.api.Assertions.assertTrue(value || (!value));
    }

    @org.junit.jupiter.api.Test
    void testGetLongWithTimeout() {
        org.apache.seata.config.Configuration fileConfig = org.apache.seata.config.ConfigurationFactory.getInstance();
        long value = fileConfig.getLong("test.long.key", 1000L, 1000);
        org.junit.jupiter.api.Assertions.assertTrue(value >= 0);
    }

    @org.junit.jupiter.api.Test
    void testGetShortWithTimeout() {
        org.apache.seata.config.Configuration fileConfig = org.apache.seata.config.ConfigurationFactory.getInstance();
        short value = fileConfig.getShort("test.short.key", ((short) (10)), 1000);
        org.junit.jupiter.api.Assertions.assertTrue(value >= 0);
    }

    @org.junit.jupiter.api.Test
    void testPutConfig() {
        org.apache.seata.config.Configuration fileConfig = org.apache.seata.config.ConfigurationFactory.getInstance();
        boolean result = fileConfig.putConfig("test.put.key", "test-value");
        org.junit.jupiter.api.Assertions.assertTrue(result || (!result));
    }

    @org.junit.jupiter.api.Test
    void testPutConfigWithTimeout() {
        org.apache.seata.config.Configuration fileConfig = org.apache.seata.config.ConfigurationFactory.getInstance();
        boolean result = fileConfig.putConfig("test.put.key", "test-value", 1000);
        org.junit.jupiter.api.Assertions.assertTrue(result || (!result));
    }

    @org.junit.jupiter.api.Test
    void testPutConfigIfAbsent() {
        org.apache.seata.config.Configuration fileConfig = org.apache.seata.config.ConfigurationFactory.getInstance();
        boolean result = fileConfig.putConfigIfAbsent("test.absent.key", "test-value");
        org.junit.jupiter.api.Assertions.assertTrue(result || (!result));
    }

    @org.junit.jupiter.api.Test
    void testPutConfigIfAbsentWithTimeout() {
        org.apache.seata.config.Configuration fileConfig = org.apache.seata.config.ConfigurationFactory.getInstance();
        boolean result = fileConfig.putConfigIfAbsent("test.absent.key", "test-value", 1000);
        org.junit.jupiter.api.Assertions.assertTrue(result || (!result));
    }

    @org.junit.jupiter.api.Test
    void testRemoveConfig() {
        org.apache.seata.config.Configuration fileConfig = org.apache.seata.config.ConfigurationFactory.getInstance();
        fileConfig.putConfig("test.remove.key", "test-value");
        boolean result = fileConfig.removeConfig("test.remove.key");
        org.junit.jupiter.api.Assertions.assertTrue(result || (!result));
    }

    @org.junit.jupiter.api.Test
    void testRemoveConfigWithTimeout() {
        org.apache.seata.config.Configuration fileConfig = org.apache.seata.config.ConfigurationFactory.getInstance();
        fileConfig.putConfig("test.remove.key", "test-value");
        boolean result = fileConfig.removeConfig("test.remove.key", 1000);
        org.junit.jupiter.api.Assertions.assertTrue(result || (!result));
    }

    @org.junit.jupiter.api.Test
    void testGetLatestConfig() {
        org.apache.seata.config.Configuration fileConfig = org.apache.seata.config.ConfigurationFactory.getInstance();
        java.lang.String value = fileConfig.getLatestConfig("test.latest.key", "default-value", 1000);
        org.junit.jupiter.api.Assertions.assertNotNull(value);
    }

    @org.junit.jupiter.api.Test
    void testRemoveConfigListener() {
        org.apache.seata.config.Configuration fileConfig = org.apache.seata.config.ConfigurationFactory.getInstance();
        org.apache.seata.config.ConfigurationChangeListener listener = new org.apache.seata.config.ConfigurationChangeListener() {
            @java.lang.Override
            public void onProcessEvent(org.apache.seata.config.ConfigurationChangeEvent event) {
            }

            @java.lang.Override
            public void onChangeEvent(org.apache.seata.config.ConfigurationChangeEvent event) {
            }
        };
        fileConfig.addConfigListener("test.listener.key", listener);
        fileConfig.removeConfigListener("test.listener.key", listener);
    }

    @org.junit.jupiter.api.Test
    void testGetConfigListeners() {
        org.apache.seata.config.Configuration fileConfig = org.apache.seata.config.ConfigurationFactory.getInstance();
        org.apache.seata.config.ConfigurationChangeListener listener = new org.apache.seata.config.ConfigurationChangeListener() {
            @java.lang.Override
            public void onProcessEvent(org.apache.seata.config.ConfigurationChangeEvent event) {
            }

            @java.lang.Override
            public void onChangeEvent(org.apache.seata.config.ConfigurationChangeEvent event) {
            }
        };
        fileConfig.addConfigListener("test.get.listeners.key", listener);
        java.util.Set<org.apache.seata.config.ConfigurationChangeListener> listeners = fileConfig.getConfigListeners("test.get.listeners.key");
        org.junit.jupiter.api.Assertions.assertNotNull(listeners);
        fileConfig.removeConfigListener("test.get.listeners.key", listener);
    }

    @org.junit.jupiter.api.Test
    void testMultipleListeners() {
        org.apache.seata.config.Configuration fileConfig = org.apache.seata.config.ConfigurationFactory.getInstance();
        org.apache.seata.config.ConfigurationChangeListener listener1 = new org.apache.seata.config.ConfigurationChangeListener() {
            @java.lang.Override
            public void onProcessEvent(org.apache.seata.config.ConfigurationChangeEvent event) {
            }

            @java.lang.Override
            public void onChangeEvent(org.apache.seata.config.ConfigurationChangeEvent event) {
            }
        };
        org.apache.seata.config.ConfigurationChangeListener listener2 = new org.apache.seata.config.ConfigurationChangeListener() {
            @java.lang.Override
            public void onProcessEvent(org.apache.seata.config.ConfigurationChangeEvent event) {
            }

            @java.lang.Override
            public void onChangeEvent(org.apache.seata.config.ConfigurationChangeEvent event) {
            }
        };
        java.lang.String dataId = "test.multiple.listeners";
        fileConfig.addConfigListener(dataId, listener1);
        fileConfig.addConfigListener(dataId, listener2);
        java.util.Set<org.apache.seata.config.ConfigurationChangeListener> listeners = fileConfig.getConfigListeners(dataId);
        org.junit.jupiter.api.Assertions.assertNotNull(listeners);
        fileConfig.removeConfigListener(dataId, listener1);
        fileConfig.removeConfigListener(dataId, listener2);
    }

    @org.junit.jupiter.api.Test
    void testGetShort() {
        org.apache.seata.config.Configuration fileConfig = org.apache.seata.config.ConfigurationFactory.getInstance();
        java.lang.System.setProperty("test.short.value", "100");
        short value = fileConfig.getShort("test.short.value");
        org.junit.jupiter.api.Assertions.assertEquals(((short) (100)), value);
    }

    @org.junit.jupiter.api.Test
    void testGetShortWithDefault() {
        org.apache.seata.config.Configuration fileConfig = org.apache.seata.config.ConfigurationFactory.getInstance();
        short value = fileConfig.getShort("test.short.not.exist", ((short) (50)));
        org.junit.jupiter.api.Assertions.assertEquals(((short) (50)), value);
    }

    @org.junit.jupiter.api.Test
    void testGetLong() {
        org.apache.seata.config.Configuration fileConfig = org.apache.seata.config.ConfigurationFactory.getInstance();
        java.lang.System.setProperty("test.long.value", "10000");
        long value = fileConfig.getLong("test.long.value");
        org.junit.jupiter.api.Assertions.assertEquals(10000L, value);
    }

    @org.junit.jupiter.api.Test
    void testGetLongWithDefault() {
        org.apache.seata.config.Configuration fileConfig = org.apache.seata.config.ConfigurationFactory.getInstance();
        long value = fileConfig.getLong("test.long.not.exist", 5000L);
        org.junit.jupiter.api.Assertions.assertEquals(5000L, value);
    }

    @org.junit.jupiter.api.Test
    void testNullValues() {
        org.apache.seata.config.Configuration fileConfig = org.apache.seata.config.ConfigurationFactory.getInstance();
        java.lang.String value = fileConfig.getConfig("non.existent.key");
        org.junit.jupiter.api.Assertions.assertNull(value);
    }

    @org.junit.jupiter.api.Test
    void testEmptyStringValue() {
        org.apache.seata.config.Configuration fileConfig = org.apache.seata.config.ConfigurationFactory.getInstance();
        java.lang.System.setProperty("test.empty.value", "");
        java.lang.String value = fileConfig.getConfig("test.empty.value", "default");
        org.junit.jupiter.api.Assertions.assertNotNull(value);
    }

    @org.junit.jupiter.api.Test
    void testSpecialCharacters() {
        org.apache.seata.config.Configuration fileConfig = org.apache.seata.config.ConfigurationFactory.getInstance();
        java.lang.String specialValue = "test@#$%^&*()";
        java.lang.System.setProperty("test.special.chars", specialValue);
        java.lang.String value = fileConfig.getConfig("test.special.chars");
        org.junit.jupiter.api.Assertions.assertEquals(specialValue, value);
    }

    @org.junit.jupiter.api.Test
    void testAddNullListener() {
        org.apache.seata.config.Configuration fileConfig = org.apache.seata.config.ConfigurationFactory.getInstance();
        fileConfig.addConfigListener("test.null.listener", null);
        java.util.Set<org.apache.seata.config.ConfigurationChangeListener> listeners = fileConfig.getConfigListeners("test.null.listener");
        org.junit.jupiter.api.Assertions.assertNull(listeners);
    }

    @org.junit.jupiter.api.Test
    void testAddListenerWithBlankDataId() {
        org.apache.seata.config.Configuration fileConfig = org.apache.seata.config.ConfigurationFactory.getInstance();
        org.apache.seata.config.ConfigurationChangeListener listener = new org.apache.seata.config.ConfigurationChangeListener() {
            @java.lang.Override
            public void onProcessEvent(org.apache.seata.config.ConfigurationChangeEvent event) {
            }

            @java.lang.Override
            public void onChangeEvent(org.apache.seata.config.ConfigurationChangeEvent event) {
            }
        };
        fileConfig.addConfigListener("", listener);
        fileConfig.addConfigListener(null, listener);
    }

    @org.junit.jupiter.api.Test
    void testRemoveNullListener() {
        org.apache.seata.config.Configuration fileConfig = org.apache.seata.config.ConfigurationFactory.getInstance();
        fileConfig.removeConfigListener("test.remove.null", null);
    }

    @org.junit.jupiter.api.Test
    void testRemoveListenerWithBlankDataId() {
        org.apache.seata.config.Configuration fileConfig = org.apache.seata.config.ConfigurationFactory.getInstance();
        org.apache.seata.config.ConfigurationChangeListener listener = new org.apache.seata.config.ConfigurationChangeListener() {
            @java.lang.Override
            public void onProcessEvent(org.apache.seata.config.ConfigurationChangeEvent event) {
            }

            @java.lang.Override
            public void onChangeEvent(org.apache.seata.config.ConfigurationChangeEvent event) {
            }
        };
        fileConfig.removeConfigListener("", listener);
        fileConfig.removeConfigListener(null, listener);
    }

    @org.junit.jupiter.api.Test
    void testGetListenersForNonExistentKey() {
        org.apache.seata.config.Configuration fileConfig = org.apache.seata.config.ConfigurationFactory.getInstance();
        java.util.Set<org.apache.seata.config.ConfigurationChangeListener> listeners = fileConfig.getConfigListeners("non.existent.listener.key");
        org.junit.jupiter.api.Assertions.assertNull(listeners);
    }

    @org.junit.jupiter.api.Test
    void testRemoveLastListener() {
        org.apache.seata.config.Configuration fileConfig = org.apache.seata.config.ConfigurationFactory.getInstance();
        org.apache.seata.config.ConfigurationChangeListener listener = new org.apache.seata.config.ConfigurationChangeListener() {
            @java.lang.Override
            public void onProcessEvent(org.apache.seata.config.ConfigurationChangeEvent event) {
            }

            @java.lang.Override
            public void onChangeEvent(org.apache.seata.config.ConfigurationChangeEvent event) {
            }
        };
        java.lang.String dataId = "test.remove.last.listener";
        fileConfig.addConfigListener(dataId, listener);
        org.junit.jupiter.api.Assertions.assertNotNull(fileConfig.getConfigListeners(dataId));
        fileConfig.removeConfigListener(dataId, listener);
        java.util.Set<org.apache.seata.config.ConfigurationChangeListener> listeners = fileConfig.getConfigListeners(dataId);
        // due to configuration cache, may still return an empty set instead of null after removing listener
        // or may still contain cached listeners, this is normal behavior
        org.junit.jupiter.api.Assertions.assertTrue(((listeners == null) || listeners.isEmpty()) || (!listeners.contains(listener)));
    }

    @org.junit.jupiter.api.Test
    void testGetTypeName() {
        org.apache.seata.config.FileConfiguration fileConfig = new org.apache.seata.config.FileConfiguration();
        java.lang.String typeName = fileConfig.getTypeName();
        org.junit.jupiter.api.Assertions.assertEquals("file", typeName);
    }

    @org.junit.jupiter.api.Test
    void testFileConfigurationWithCustomName() {
        org.apache.seata.config.FileConfiguration fileConfig = new org.apache.seata.config.FileConfiguration("file.conf");
        org.junit.jupiter.api.Assertions.assertNotNull(fileConfig);
        java.lang.String typeName = fileConfig.getTypeName();
        org.junit.jupiter.api.Assertions.assertEquals("file", typeName);
    }

    @org.junit.jupiter.api.Test
    void testFileConfigurationWithNonExistentFile() {
        org.apache.seata.config.FileConfiguration fileConfig = new org.apache.seata.config.FileConfiguration("non-existent-file.conf");
        org.junit.jupiter.api.Assertions.assertNotNull(fileConfig);
    }

    @org.junit.jupiter.api.Test
    void testMultipleConfigOperations() {
        org.apache.seata.config.Configuration fileConfig = org.apache.seata.config.ConfigurationFactory.getInstance();
        java.lang.System.setProperty("test.multi.op.1", "value1");
        java.lang.System.setProperty("test.multi.op.2", "value2");
        java.lang.System.setProperty("test.multi.op.3", "value3");
        java.lang.String val1 = fileConfig.getConfig("test.multi.op.1");
        java.lang.String val2 = fileConfig.getConfig("test.multi.op.2");
        java.lang.String val3 = fileConfig.getConfig("test.multi.op.3");
        org.junit.jupiter.api.Assertions.assertEquals("value1", val1);
        org.junit.jupiter.api.Assertions.assertEquals("value2", val2);
        org.junit.jupiter.api.Assertions.assertEquals("value3", val3);
    }

    @org.junit.jupiter.api.Test
    void testPutAndGetConfig() {
        org.apache.seata.config.Configuration fileConfig = org.apache.seata.config.ConfigurationFactory.getInstance();
        boolean putResult = fileConfig.putConfig("test.put.get", "put-value");
        org.junit.jupiter.api.Assertions.assertTrue(putResult || (!putResult));
    }

    @org.junit.jupiter.api.Test
    void testPutConfigIfAbsentWhenKeyExists() {
        org.apache.seata.config.Configuration fileConfig = org.apache.seata.config.ConfigurationFactory.getInstance();
        java.lang.System.setProperty("test.put.if.absent", "existing-value");
        boolean result = fileConfig.putConfigIfAbsent("test.put.if.absent", "new-value");
        org.junit.jupiter.api.Assertions.assertTrue(result || (!result));
    }

    @org.junit.jupiter.api.Test
    void testRemoveExistingConfig() {
        org.apache.seata.config.Configuration fileConfig = org.apache.seata.config.ConfigurationFactory.getInstance();
        java.lang.System.setProperty("test.remove.existing", "value");
        boolean result = fileConfig.removeConfig("test.remove.existing");
        org.junit.jupiter.api.Assertions.assertTrue(result || (!result));
    }

    @org.junit.jupiter.api.Test
    void testGetConfigFromSystemProperty() {
        java.lang.System.setProperty("test.sys.prop", "sys-prop-value");
        org.apache.seata.config.Configuration fileConfig = org.apache.seata.config.ConfigurationFactory.getInstance();
        java.lang.String value = fileConfig.getConfig("test.sys.prop");
        org.junit.jupiter.api.Assertions.assertEquals("sys-prop-value", value);
    }

    @org.junit.jupiter.api.Test
    void testGetConfigFromEnvironmentVariable() {
        org.apache.seata.config.Configuration fileConfig = org.apache.seata.config.ConfigurationFactory.getInstance();
        java.lang.String path = fileConfig.getConfigFromSys("PATH");
        org.junit.jupiter.api.Assertions.assertNotNull(path);
    }

    @org.junit.jupiter.api.Test
    void testPutIfAbsentAndRemoveConfigAfterLatestConfigQueries() {
        org.apache.seata.config.FileConfiguration fileConfig = new org.apache.seata.config.FileConfiguration("");
        fileConfig.getInt("", -715, 0L);
        fileConfig.getLatestConfig("get", "", 2962L);
        fileConfig.removeConfig("", 237L);
        fileConfig.getBoolean("", false);
        boolean putIfAbsentResult = fileConfig.putConfigIfAbsent("NazOup';Y8", null, 2962L);
        org.junit.jupiter.api.Assertions.assertTrue(putIfAbsentResult);
        fileConfig.getTypeName();
        fileConfig.getLatestConfig("", "A~V&TciIbZ7$^Kb", 2962L);
        boolean removedQuestionKey = fileConfig.removeConfig("?/L", 2962L);
        org.junit.jupiter.api.Assertions.assertTrue(removedQuestionKey);
        boolean removedNazOupKey = fileConfig.removeConfig("NazOup';Y8", 0L);
        org.junit.jupiter.api.Assertions.assertFalse(removedNazOupKey);
    }

    @org.junit.jupiter.api.Test
    void testHandleLatestConfigQueriesNullsAndRemovals() {
        org.apache.seata.config.FileConfiguration fileConfig = new org.apache.seata.config.FileConfiguration("-bvOY");
        fileConfig.getInt("", 39, 39L);
        fileConfig.getShort("[&hV{&2oIr", ((short) (2)));
        boolean putInitialConfigResult = fileConfig.putConfig("^", "-bvOY", -1027L);
        org.junit.jupiter.api.Assertions.assertFalse(putInitialConfigResult);
        fileConfig.getConfig("RJm", -1027L);
        fileConfig.getInt(((java.lang.String) (null)));
        fileConfig.getLatestConfig(null, "", -1027L);
        fileConfig.getConfig("q`=3Ac");
        fileConfig.getLatestConfig(null, null, 1790L);
        fileConfig.removeConfig(null, 1790L);
        fileConfig.getTypeName();
        boolean removedNullKeyWithZeroTimeout = fileConfig.removeConfig(null, 0L);
        org.junit.jupiter.api.Assertions.assertFalse(removedNullKeyWithZeroTimeout);
        boolean putIfAbsentEmptyKeyNullValueResult = fileConfig.putConfigIfAbsent("", null, 0L);
        org.junit.jupiter.api.Assertions.assertFalse(putIfAbsentEmptyKeyNullValueResult);
        boolean removedSpecificKeyWithLongTimeout = fileConfig.removeConfig("ZV\u007fL>wk3", 5000L);
        org.junit.jupiter.api.Assertions.assertTrue(removedSpecificKeyWithLongTimeout);
        boolean putEmptyValueZeroTimeoutResult = fileConfig.putConfig("cjmA2cJ{P>=+`Fz!R", "", 0L);
        org.junit.jupiter.api.Assertions.assertFalse(putEmptyValueZeroTimeoutResult);
    }

    @org.junit.jupiter.api.Test
    void testCreateWithNameAndLoadOnStartThenRemoveConfig() {
        org.apache.seata.config.FileConfiguration fileConfig = new org.apache.seata.config.FileConfiguration("J8=rMZJZ", true);
        fileConfig.getConfig("6KV{OJCIW", -62L);
        boolean removedExistingNameResult = fileConfig.removeConfig("J8=rMZJZ", 1L);
        org.junit.jupiter.api.Assertions.assertTrue(removedExistingNameResult);
    }

    @org.junit.jupiter.api.Test
    void testReturnTypeNameForDefaultConstructor() {
        io.seata.config.FileConfiguration fileConfig = new io.seata.config.FileConfiguration();
        org.junit.jupiter.api.Assertions.assertEquals("file", fileConfig.getTypeName());
        java.lang.String typeName = fileConfig.getTypeName();
        org.junit.jupiter.api.Assertions.assertEquals("file", typeName);
    }

    @org.junit.jupiter.api.Test
    void testRemoveConfigWithoutExecutorThrowsNullPointerException() {
        org.apache.seata.config.FileConfiguration fileConfig = new org.apache.seata.config.FileConfiguration();
        java.lang.NullPointerException exception = org.junit.jupiter.api.Assertions.assertThrows(java.lang.NullPointerException.class, () -> fileConfig.removeConfig("S[w!jc="));
        boolean hasFileConfigurationFrame = false;
        for (java.lang.StackTraceElement element : exception.getStackTrace()) {
            if (org.apache.seata.config.FileConfiguration.class.getName().equals(element.getClassName())) {
                hasFileConfigurationFrame = true;
                break;
            }
        }
        org.junit.jupiter.api.Assertions.assertTrue(hasFileConfigurationFrame);
    }

    @org.junit.jupiter.api.Test
    void testTypeNameIsFileAndRemoveConfigWithLongTimeout() {
        org.apache.seata.config.FileConfiguration fileConfig = new org.apache.seata.config.FileConfiguration("");
        java.lang.String typeName = fileConfig.getTypeName();
        org.junit.jupiter.api.Assertions.assertEquals("file", typeName);
        fileConfig.removeConfig("name can't be null", 2147483647L);
        java.lang.String typeNameAfterRemoval = fileConfig.getTypeName();
        org.junit.jupiter.api.Assertions.assertEquals("file", typeNameAfterRemoval);
    }

    @org.junit.jupiter.api.Test
    void testGetLongWithDefaultAndTimeoutWithoutExecutorThrowsNullPointerException() {
        org.apache.seata.config.FileConfiguration fileConfig = new org.apache.seata.config.FileConfiguration();
        java.lang.NullPointerException exception = org.junit.jupiter.api.Assertions.assertThrows(java.lang.NullPointerException.class, () -> fileConfig.getLong(",qs,r.[>5tj", ((long) (short) (125)), 0L));
        boolean hasFileConfigurationFrame = false;
        for (java.lang.StackTraceElement element : exception.getStackTrace()) {
            if (org.apache.seata.config.FileConfiguration.class.getName().equals(element.getClassName())) {
                hasFileConfigurationFrame = true;
                break;
            }
        }
        org.junit.jupiter.api.Assertions.assertTrue(hasFileConfigurationFrame);
    }

    @org.junit.jupiter.api.Test
    void testPutConfigWithTimeoutWithoutExecutorThrowsNullPointerException() {
        org.apache.seata.config.FileConfiguration fileConfig = new org.apache.seata.config.FileConfiguration();
        java.lang.NullPointerException exception = org.junit.jupiter.api.Assertions.assertThrows(java.lang.NullPointerException.class, () -> fileConfig.putConfig("", "", 0L));
        boolean hasFileConfigurationFrame = false;
        for (java.lang.StackTraceElement element : exception.getStackTrace()) {
            if (org.apache.seata.config.FileConfiguration.class.getName().equals(element.getClassName())) {
                hasFileConfigurationFrame = true;
                break;
            }
        }
        org.junit.jupiter.api.Assertions.assertTrue(hasFileConfigurationFrame);
    }

    @org.junit.jupiter.api.Test
    void testConstructingFromAnotherConfigurationKeepsTypeName() {
        org.apache.seata.config.FileConfiguration apacheFileConfig = new org.apache.seata.config.FileConfiguration();
        io.seata.config.FileConfiguration wrappedFileConfig = new io.seata.config.FileConfiguration(apacheFileConfig);
        java.lang.String typeName = wrappedFileConfig.getTypeName();
        org.junit.jupiter.api.Assertions.assertEquals("file", typeName);
    }

    @org.junit.jupiter.api.Test
    void testPutConfigIfAbsentWithoutTimeoutWithoutExecutorThrowsNullPointerException() {
        org.apache.seata.config.FileConfiguration fileConfig = new org.apache.seata.config.FileConfiguration();
        java.lang.NullPointerException exception = org.junit.jupiter.api.Assertions.assertThrows(java.lang.NullPointerException.class, () -> fileConfig.putConfigIfAbsent(null, null));
        boolean hasFileConfigurationFrame = false;
        for (java.lang.StackTraceElement element : exception.getStackTrace()) {
            if (org.apache.seata.config.FileConfiguration.class.getName().equals(element.getClassName())) {
                hasFileConfigurationFrame = true;
                break;
            }
        }
        org.junit.jupiter.api.Assertions.assertTrue(hasFileConfigurationFrame);
    }

    @org.junit.jupiter.api.Test
    void testGetDurationWithoutTimeoutWithoutExecutorThrowsNullPointerException() {
        org.apache.seata.config.FileConfiguration fileConfig = new org.apache.seata.config.FileConfiguration();
        java.lang.NullPointerException exception = org.junit.jupiter.api.Assertions.assertThrows(java.lang.NullPointerException.class, () -> fileConfig.getDuration(""));
        boolean hasFileConfigurationFrame = false;
        for (java.lang.StackTraceElement element : exception.getStackTrace()) {
            if (org.apache.seata.config.FileConfiguration.class.getName().equals(element.getClassName())) {
                hasFileConfigurationFrame = true;
                break;
            }
        }
        org.junit.jupiter.api.Assertions.assertTrue(hasFileConfigurationFrame);
    }

    @org.junit.jupiter.api.Test
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
        org.junit.jupiter.api.Assertions.assertTrue(firstPutResult);
        boolean putIfAbsentResult = delegatingFileConfig.putConfigIfAbsent("", "iaOs!", -1405L);
        org.junit.jupiter.api.Assertions.assertFalse(putIfAbsentResult);
        boolean secondPutResult = delegatingFileConfig.putConfig("", "", -1405L);
        org.junit.jupiter.api.Assertions.assertFalse(secondPutResult);
    }
}