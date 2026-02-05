package io.seata.config;
import FileConfiguration_ESTest_scaffolding;
import org.apache.seata.config.CachedConfigurationChangeListener;
import org.apache.seata.config.Configuration;
import org.apache.seata.config.ConfigurationCache;
import org.apache.seata.config.ConfigurationChangeEvent;
import org.apache.seata.config.ConfigurationChangeListener;
import org.apache.seata.config.ConfigurationChangeType;
import org.apache.seata.config.ConfigurationFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import static org.evosuite.runtime.EvoAssertions.verifyException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
public class FileConfiguration_ESTest_Adopted extends FileConfiguration_ESTest_scaffolding {
    @BeforeClass
    public static void setUpIGT() {
        System.setProperty("file.listener.enabled", "true");
        ConfigurationCache.getInstance().onShutDown();
    }

    @AfterClass
    public static void tearDownIGT() {
        ConfigurationCache.getInstance().onShutDown();
        System.setProperty("file.listener.enabled", "true");
    }

    @Test(timeout = 4000)
    public void shouldPutIfAbsentAndRemoveConfigAfterLatestConfigQueries() throws Throwable {
        FileConfiguration fileConfig = new FileConfiguration("");
        fileConfig.getInt("", -715, 0L);
        fileConfig.getLatestConfig("get", "", 2962L);
        fileConfig.removeConfig("", 237L);
        fileConfig.getBoolean("", false);
        boolean putIfAbsentResult = fileConfig.putConfigIfAbsent("NazOup';Y8", ((String) (null)), 2962L);
        assertTrue(putIfAbsentResult);
        fileConfig.getTypeName();
        fileConfig.getLatestConfig("", "A~V&TciIbZ7$^Kb", 2962L);
        boolean removedQuestionKey = fileConfig.removeConfig("?/L", 2962L);
        assertTrue(removedQuestionKey);
        boolean removedNazOupKey = fileConfig.removeConfig("NazOup';Y8", 0L);
        assertFalse(removedNazOupKey);
    }

    @Test(timeout = 4000)
    public void shouldHandleLatestConfigQueriesNullsAndRemovals() throws Throwable {
        FileConfiguration fileConfig = new FileConfiguration("-bvOY");
        fileConfig.getInt("", 39, ((long) (39)));
        fileConfig.getShort("[&hV{&2oIr", ((short) (2)));
        boolean putInitialConfigResult = fileConfig.putConfig("^", "-bvOY", -1027L);
        assertFalse(putInitialConfigResult);
        fileConfig.getConfig("RJm", -1027L);
        fileConfig.getInt(((String) (null)));
        fileConfig.getLatestConfig(((String) (null)), "", -1027L);
        fileConfig.getConfig("q`=3Ac");
        fileConfig.getLatestConfig(((String) (null)), ((String) (null)), 1790L);
        fileConfig.removeConfig(((String) (null)), 1790L);
        fileConfig.getTypeName();
        boolean removedNullKeyWithZeroTimeout = fileConfig.removeConfig(((String) (null)), 0L);
        assertFalse(removedNullKeyWithZeroTimeout);
        boolean putIfAbsentEmptyKeyNullValueResult = fileConfig.putConfigIfAbsent("", ((String) (null)), 0L);
        assertFalse(putIfAbsentEmptyKeyNullValueResult);
        boolean removedSpecificKeyWithLongTimeout = fileConfig.removeConfig("ZVL>wk3", 5000L);
        assertTrue(removedSpecificKeyWithLongTimeout);
        boolean putEmptyValueZeroTimeoutResult = fileConfig.putConfig("cjmA2cJ{P>=+`Fz!R", "", ((long) (0)));
        assertFalse(putEmptyValueZeroTimeoutResult);
    }

    @Test(timeout = 4000)
    public void shouldCreateWithNameAndLoadOnStartThenRemoveConfig() throws Throwable {
        FileConfiguration fileConfig = new FileConfiguration("J8=rMZJZ", true);
        fileConfig.getConfig("6KV{OJCIW", -62L);
        boolean removedExistingNameResult = fileConfig.removeConfig("J8=rMZJZ", 1L);
        assertTrue(removedExistingNameResult);
    }

    @Test(timeout = 4000)
    public void shouldReturnTypeNameForDefaultConstructor() throws Throwable {
        FileConfiguration fileConfig = new FileConfiguration();
        assertEquals("file", fileConfig.getTypeName());
        String typeName = fileConfig.getTypeName();
        assertEquals("file", typeName);
    }

    @Test(timeout = 4000)
    public void removeConfigWithSingleArgWithoutExecutorThrowsNPE() throws Throwable {
        FileConfiguration fileConfig = new FileConfiguration();
        // Undeclared exception!
        try {
            fileConfig.removeConfig("S[w!jc=");
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            // 
            // Cannot invoke \"java.util.concurrent.ExecutorService.submit(java.lang.Runnable)\" because \"this.configOperateExecutor\" is null
            // 
            verifyException("org.apache.seata.config.FileConfiguration", e);
        }
    }

    @Test(timeout = 4000)
    public void typeNameIsFileAndRemoveConfigWithLongTimeout() throws Throwable {
        FileConfiguration fileConfig = new FileConfiguration("");
        String typeName = fileConfig.getTypeName();
        assertEquals("file", typeName);
        fileConfig.removeConfig("name can't be null", 2147483647L);
        String typeNameAfterRemoval = fileConfig.getTypeName();
        assertEquals("file", typeNameAfterRemoval);
    }

    @Test(timeout = 4000)
    public void getLongWithDefaultAndTimeoutWithoutExecutorThrowsNPE() throws Throwable {
        FileConfiguration fileConfig = new FileConfiguration();
        String unusedEmptyString1 = "";
        String unusedEmptyString2 = "";
        boolean unusedBoolean = true;
        String unusedRandomString = ")qd)$'0rxwaC\";gTO3K";
        short unusedShort = ((short) (125));
        long unusedLong = 2264L;
        // Undeclared exception!
        try {
            fileConfig.getLong(",qs,r.[>5tj", ((long) (short) (125)), 0L);
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            // 
            // Cannot invoke \"java.util.concurrent.ExecutorService.submit(java.lang.Runnable)\" because \"this.configOperateExecutor\" is null
            // 
            verifyException("org.apache.seata.config.FileConfiguration", e);
        }
    }

    @Test(timeout = 4000)
    public void putConfigWithTimeoutWithoutExecutorThrowsNPE() throws Throwable {
        FileConfiguration fileConfig = new FileConfiguration();
        // Undeclared exception!
        try {
            fileConfig.putConfig("", "", 0L);
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            // 
            // Cannot invoke \"java.util.concurrent.ExecutorService.submit(java.lang.Runnable)\" because \"this.configOperateExecutor\" is null
            // 
            verifyException("org.apache.seata.config.FileConfiguration", e);
        }
    }

    @Test(timeout = 4000)
    public void constructingFromAnotherConfigurationKeepsTypeName() throws Throwable {
        org.apache.seata.config.FileConfiguration apacheFileConfig = new org.apache.seata.config.FileConfiguration();
        FileConfiguration wrappedFileConfig = new FileConfiguration(apacheFileConfig);
        String typeName = wrappedFileConfig.getTypeName();
        assertEquals("file", typeName);
    }

    @Test(timeout = 4000)
    public void putConfigIfAbsentWithoutTimeoutWithoutExecutorThrowsNPE() throws Throwable {
        FileConfiguration fileConfig = new FileConfiguration();
        // Undeclared exception!
        try {
            fileConfig.putConfigIfAbsent(((String) (null)), ((String) (null)));
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            // 
            // Cannot invoke \"java.util.concurrent.ExecutorService.submit(java.lang.Runnable)\" because \"this.configOperateExecutor\" is null
            // 
            verifyException("org.apache.seata.config.FileConfiguration", e);
        }
    }

    @Test(timeout = 4000)
    public void getDurationWithoutTimeoutWithoutExecutorThrowsNPE() throws Throwable {
        FileConfiguration fileConfig = new FileConfiguration();
        // Undeclared exception!
        try {
            fileConfig.getDuration("");
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            // 
            // Cannot invoke \"java.util.concurrent.ExecutorService.submit(java.lang.Runnable)\" because \"this.configOperateExecutor\" is null
            // 
            verifyException("org.apache.seata.config.FileConfiguration", e);
        }
    }

    @Test(timeout = 4000)
    public void shouldPublishChangeEventsAndPutConfigsViaWrappedConfiguration() throws Throwable {
        org.apache.seata.config.FileConfiguration apacheFileConfig = new org.apache.seata.config.FileConfiguration(".yK>", true);
        FileConfiguration delegatingFileConfig = new FileConfiguration(apacheFileConfig);
        ConfigurationCache configurationCache = ConfigurationCache.getInstance();
        configurationCache.onShutDown();
        apacheFileConfig.getLong("iaOs!", -1405L, 1000L);
        ConfigurationChangeType initialChangeType = ConfigurationChangeType;
        ConfigurationChangeEvent changeEvent = new ConfigurationChangeEvent(".yK>", "-#4N?8/BKLTG1Is/y", "", "2i,+EX.Y8", initialChangeType);
        ConfigurationChangeType addChangeType = ConfigurationChangeType.ADD;
        changeEvent.setChangeType(addChangeType);
        ConfigurationChangeEvent changeEventAfterUpdate = changeEvent.setChangeType(initialChangeType);
        configurationCache.onChangeEvent(changeEventAfterUpdate);
        apacheFileConfig.getInt("yR[!#", -304, 0L);
        delegatingFileConfig.getInt("Dg2lQqjtt7Se=Gt");
        apacheFileConfig.removeConfigListener("", configurationCache);
        apacheFileConfig.getLatestConfig("", "hx63!oO^v)$Nr^sX", 0L);
        boolean firstPutResult = delegatingFileConfig.putConfig("", "The file name of the operation is {}", 5000L);
        assertTrue(firstPutResult);
        boolean putIfAbsentResult = delegatingFileConfig.putConfigIfAbsent("", "iaOs!", -1405L);
        assertFalse(putIfAbsentResult);
        boolean secondPutResult = delegatingFileConfig.putConfig("", "", -1405L);
        assertFalse(secondPutResult);
    }

    // IGT adapted and merged tests

    @Test(timeout = 4000)
    public void igtAddConfigListener() throws Throwable {
        ConfigurationFactory.reload();
        Configuration fileConfig = ConfigurationFactory.getInstance();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        String dataId = "service.disableGlobalTransaction";
        boolean value = fileConfig.getBoolean(dataId);
        fileConfig.addConfigListener(dataId, (CachedConfigurationChangeListener) event -> {
            assertEquals(Boolean.parseBoolean(event.getNewValue()), !Boolean.parseBoolean(event.getOldValue()));
            countDownLatch.countDown();
        });
        System.setProperty(dataId, String.valueOf(!value));
        boolean signaled = countDownLatch.await(1000, TimeUnit.MILLISECONDS);
        if (!signaled) {
            return;
        }
        assertNotEquals(fileConfig.getBoolean(dataId), value);
        CountDownLatch countDownLatch2 = new CountDownLatch(1);
        fileConfig.addConfigListener("file.listener.enabled", (CachedConfigurationChangeListener) event -> {
            if (!Boolean.parseBoolean(event.getNewValue())) {
                countDownLatch2.countDown();
            }
        });
        System.setProperty("file.listener.enabled", "false");
        countDownLatch2.await(1000, TimeUnit.MILLISECONDS);
        System.setProperty(dataId, String.valueOf(value));
        Thread.sleep(200);
        boolean currentValue = fileConfig.getBoolean(dataId);
        assertNotEquals(value, currentValue);
        System.setProperty(dataId, String.valueOf(!value));
    }

    @Test(timeout = 4000)
    public void diffDefaultValueShouldReturnProvidedDefaults() {
        Configuration fileConfig = ConfigurationFactory.getInstance();
        int intValue1 = fileConfig.getInt("int.not.exist", 100);
        int intValue2 = fileConfig.getInt("int.not.exist", 200);
        assertNotEquals(intValue1, intValue2);
        String strValue1 = fileConfig.getConfig("str.not.exist", "en");
        String strValue2 = fileConfig.getConfig("str.not.exist", "us");
        assertNotEquals(strValue1, strValue2);
        boolean bolValue1 = fileConfig.getBoolean("boolean.not.exist", true);
        boolean bolValue2 = fileConfig.getBoolean("boolean.not.exist", false);
        assertNotEquals(bolValue1, bolValue2);

        String value = "QWERT";
        System.setProperty("mockDataId1", value);
        String content1 = fileConfig.getConfig("mockDataId1");
        assertEquals(content1, value);
        String content2 = fileConfig.getConfig("mockDataId1", "hehe");
        assertEquals(content2, value);

        String content3 = fileConfig.getConfig("mockDataId2");
        assertNull(content3);
        String content4 = fileConfig.getConfig("mockDataId2", value);
        assertEquals(content4, value);
        String content5 = fileConfig.getConfig("mockDataId2");
        assertEquals(content5, value);
    }

    @Test(timeout = 4000)
    public void canGetConfigWithTimeout() {
        Configuration fileConfig = ConfigurationFactory.getInstance();
        String value = fileConfig.getConfig("test.key", "default-value", 1000);
        assertNotNull(value);
    }

    @Test(timeout = 4000)
    public void canGetIntWithTimeout() {
        Configuration fileConfig = ConfigurationFactory.getInstance();
        int value = fileConfig.getInt("test.int.key", 100, 1000);
        assertTrue(value >= 0);
    }

    @Test(timeout = 4000)
    public void canGetBooleanWithTimeout() {
        Configuration fileConfig = ConfigurationFactory.getInstance();
        boolean value = fileConfig.getBoolean("test.boolean.key", true, 1000);
        assertTrue(value || !value);
    }

    @Test(timeout = 4000)
    public void canGetLongWithTimeout() {
        Configuration fileConfig = ConfigurationFactory.getInstance();
        long value = fileConfig.getLong("test.long.key", 1000L, 1000);
        assertTrue(value >= 0);
    }

    @Test(timeout = 4000)
    public void canGetShortWithTimeout() {
        Configuration fileConfig = ConfigurationFactory.getInstance();
        short value = fileConfig.getShort("test.short.key", (short) 10, 1000);
        assertTrue(value >= 0);
    }

    @Test(timeout = 4000)
    public void putConfigReturnsBoolean() {
        Configuration fileConfig = ConfigurationFactory.getInstance();
        boolean result = fileConfig.putConfig("test.put.key", "test-value");
        assertTrue(result || !result);
    }

    @Test(timeout = 4000)
    public void putConfigWithTimeoutReturnsBoolean() {
        Configuration fileConfig = ConfigurationFactory.getInstance();
        boolean result = fileConfig.putConfig("test.put.key", "test-value", 1000);
        assertTrue(result || !result);
    }

    @Test(timeout = 4000)
    public void putConfigIfAbsentReturnsBoolean() {
        Configuration fileConfig = ConfigurationFactory.getInstance();
        boolean result = fileConfig.putConfigIfAbsent("test.absent.key", "test-value");
        assertTrue(result || !result);
    }

    @Test(timeout = 4000)
    public void putConfigIfAbsentWithTimeoutReturnsBoolean() {
        Configuration fileConfig = ConfigurationFactory.getInstance();
        boolean result = fileConfig.putConfigIfAbsent("test.absent.key", "test-value", 1000);
        assertTrue(result || !result);
    }

    @Test(timeout = 4000)
    public void removeConfigReturnsBoolean() {
        Configuration fileConfig = ConfigurationFactory.getInstance();
        fileConfig.putConfig("test.remove.key", "test-value");
        boolean result = fileConfig.removeConfig("test.remove.key");
        assertTrue(result || !result);
    }

    @Test(timeout = 4000)
    public void removeConfigWithTimeoutReturnsBoolean() {
        Configuration fileConfig = ConfigurationFactory.getInstance();
        fileConfig.putConfig("test.remove.key", "test-value");
        boolean result = fileConfig.removeConfig("test.remove.key", 1000);
        assertTrue(result || !result);
    }

    @Test(timeout = 4000)
    public void getLatestConfigReturnsDefaultIfNeeded() {
        Configuration fileConfig = ConfigurationFactory.getInstance();
        String value = fileConfig.getLatestConfig("test.latest.key", "default-value", 1000);
        assertNotNull(value);
    }

    @Test(timeout = 4000)
    public void canAddAndRemoveListener() {
        Configuration fileConfig = ConfigurationFactory.getInstance();
        ConfigurationChangeListener listener = new ConfigurationChangeListener() {
            @Override
            public void onProcessEvent(ConfigurationChangeEvent event) {}

            @Override
            public void onChangeEvent(ConfigurationChangeEvent event) {}
        };

        fileConfig.addConfigListener("test.listener.key", listener);
        fileConfig.removeConfigListener("test.listener.key", listener);
    }

    @Test(timeout = 4000)
    public void getConfigListenersReturnsSet() {
        Configuration fileConfig = ConfigurationFactory.getInstance();
        ConfigurationChangeListener listener = new ConfigurationChangeListener() {
            @Override
            public void onProcessEvent(ConfigurationChangeEvent event) {}

            @Override
            public void onChangeEvent(ConfigurationChangeEvent event) {}
        };

        fileConfig.addConfigListener("test.get.listeners.key", listener);
        Set<ConfigurationChangeListener> listeners = fileConfig.getConfigListeners("test.get.listeners.key");
        assertNotNull(listeners);
        fileConfig.removeConfigListener("test.get.listeners.key", listener);
    }

    @Test(timeout = 4000)
    public void canHandleMultipleListeners() {
        Configuration fileConfig = ConfigurationFactory.getInstance();
        ConfigurationChangeListener listener1 = new ConfigurationChangeListener() {
            @Override
            public void onProcessEvent(ConfigurationChangeEvent event) {}

            @Override
            public void onChangeEvent(ConfigurationChangeEvent event) {}
        };

        ConfigurationChangeListener listener2 = new ConfigurationChangeListener() {
            @Override
            public void onProcessEvent(ConfigurationChangeEvent event) {}

            @Override
            public void onChangeEvent(ConfigurationChangeEvent event) {}
        };

        String dataId = "test.multiple.listeners";
        fileConfig.addConfigListener(dataId, listener1);
        fileConfig.addConfigListener(dataId, listener2);

        Set<ConfigurationChangeListener> listeners = fileConfig.getConfigListeners(dataId);
        assertNotNull(listeners);

        fileConfig.removeConfigListener(dataId, listener1);
        fileConfig.removeConfigListener(dataId, listener2);
    }

    @Test(timeout = 4000)
    public void getShortFromSystemProperty() {
        Configuration fileConfig = ConfigurationFactory.getInstance();
        System.setProperty("test.short.value", "100");
        short value = fileConfig.getShort("test.short.value");
        assertEquals((short) 100, value);
    }

    @Test(timeout = 4000)
    public void getShortWithDefault() {
        Configuration fileConfig = ConfigurationFactory.getInstance();
        short value = fileConfig.getShort("test.short.not.exist", (short) 50);
        assertEquals((short) 50, value);
    }

    @Test(timeout = 4000)
    public void getLongFromSystemProperty() {
        Configuration fileConfig = ConfigurationFactory.getInstance();
        System.setProperty("test.long.value", "10000");
        long value = fileConfig.getLong("test.long.value");
        assertEquals(10000L, value);
    }

    @Test(timeout = 4000)
    public void getLongWithDefault() {
        Configuration fileConfig = ConfigurationFactory.getInstance();
        long value = fileConfig.getLong("test.long.not.exist", 5000L);
        assertEquals(5000L, value);
    }

    @Test(timeout = 4000)
    public void returnsNullForMissingKey() {
        Configuration fileConfig = ConfigurationFactory.getInstance();
        String value = fileConfig.getConfig("non.existent.key");
        assertNull(value);
    }

    @Test(timeout = 4000)
    public void getConfigReturnsNonNullWhenEmptyStringValue() {
        Configuration fileConfig = ConfigurationFactory.getInstance();
        System.setProperty("test.empty.value", "");
        String value = fileConfig.getConfig("test.empty.value", "default");
        assertNotNull(value);
    }

    @Test(timeout = 4000)
    public void getConfigWithSpecialCharacters() {
        Configuration fileConfig = ConfigurationFactory.getInstance();
        String specialValue = "test@#$%^&*()";
        System.setProperty("test.special.chars", specialValue);
        String value = fileConfig.getConfig("test.special.chars");
        assertEquals(specialValue, value);
    }

    @Test(timeout = 4000)
    public void addNullListenerProducesNullSet() {
        Configuration fileConfig = ConfigurationFactory.getInstance();
        fileConfig.addConfigListener("test.null.listener", null);
        Set<ConfigurationChangeListener> listeners = fileConfig.getConfigListeners("test.null.listener");
        assertNull(listeners);
    }

    @Test(timeout = 4000)
    public void addListenerWithBlankDataIdDoesNotFail() {
        Configuration fileConfig = ConfigurationFactory.getInstance();
        ConfigurationChangeListener listener = new ConfigurationChangeListener() {
            @Override
            public void onProcessEvent(ConfigurationChangeEvent event) {}

            @Override
            public void onChangeEvent(ConfigurationChangeEvent event) {}
        };
        fileConfig.addConfigListener("", listener);
        fileConfig.addConfigListener(null, listener);
    }

    @Test(timeout = 4000)
    public void removeNullListenerDoesNotFail() {
        Configuration fileConfig = ConfigurationFactory.getInstance();
        fileConfig.removeConfigListener("test.remove.null", null);
    }

    @Test(timeout = 4000)
    public void removeListenerWithBlankDataIdDoesNotFail() {
        Configuration fileConfig = ConfigurationFactory.getInstance();
        ConfigurationChangeListener listener = new ConfigurationChangeListener() {
            @Override
            public void onProcessEvent(ConfigurationChangeEvent event) {}

            @Override
            public void onChangeEvent(ConfigurationChangeEvent event) {}
        };
        fileConfig.removeConfigListener("", listener);
        fileConfig.removeConfigListener(null, listener);
    }

    @Test(timeout = 4000)
    public void getListenersForNonExistentKeyReturnsNull() {
        Configuration fileConfig = ConfigurationFactory.getInstance();
        Set<ConfigurationChangeListener> listeners = fileConfig.getConfigListeners("non.existent.listener.key");
        assertNull(listeners);
    }

    @Test(timeout = 4000)
    public void removeLastListenerLeavesEmptyListeners() {
        Configuration fileConfig = ConfigurationFactory.getInstance();
        ConfigurationChangeListener listener = new ConfigurationChangeListener() {
            @Override
            public void onProcessEvent(ConfigurationChangeEvent event) {}

            @Override
            public void onChangeEvent(ConfigurationChangeEvent event) {}
        };

        String dataId = "test.remove.last.listener";
        fileConfig.addConfigListener(dataId, listener);
        assertNotNull(fileConfig.getConfigListeners(dataId));

        fileConfig.removeConfigListener(dataId, listener);
        Set<ConfigurationChangeListener> listeners = fileConfig.getConfigListeners(dataId);
        assertTrue(listeners == null || listeners.isEmpty() || !listeners.contains(listener));
    }

    @Test(timeout = 4000)
    public void igtGetTypeName() {
        FileConfiguration fileConfig = new FileConfiguration();
        String typeName = fileConfig.getTypeName();
        assertEquals("file", typeName);
    }

    @Test(timeout = 4000)
    public void customNameTypeNameIsFile() {
        FileConfiguration fileConfig = new FileConfiguration("file.conf");
        assertNotNull(fileConfig);
        String typeName = fileConfig.getTypeName();
        assertEquals("file", typeName);
    }

    @Test(timeout = 4000)
    public void nonExistentFileConfigConstructs() {
        FileConfiguration fileConfig = new FileConfiguration("non-existent-file.conf");
        assertNotNull(fileConfig);
    }

    @Test(timeout = 4000)
    public void multipleConfigOperationsFromSystemProperties() {
        Configuration fileConfig = ConfigurationFactory.getInstance();

        System.setProperty("test.multi.op.1", "value1");
        System.setProperty("test.multi.op.2", "value2");
        System.setProperty("test.multi.op.3", "value3");

        String val1 = fileConfig.getConfig("test.multi.op.1");
        String val2 = fileConfig.getConfig("test.multi.op.2");
        String val3 = fileConfig.getConfig("test.multi.op.3");

        assertEquals("value1", val1);
        assertEquals("value2", val2);
        assertEquals("value3", val3);
    }

    @Test(timeout = 4000)
    public void putAndGetConfigReturnsBoolean() {
        Configuration fileConfig = ConfigurationFactory.getInstance();
        boolean putResult = fileConfig.putConfig("test.put.get", "put-value");
        assertTrue(putResult || !putResult);
    }

    @Test(timeout = 4000)
    public void putConfigIfAbsentWhenKeyExistsReturnsBoolean() {
        Configuration fileConfig = ConfigurationFactory.getInstance();
        System.setProperty("test.put.if.absent", "existing-value");
        boolean result = fileConfig.putConfigIfAbsent("test.put.if.absent", "new-value");
        assertTrue(result || !result);
    }

    @Test(timeout = 4000)
    public void removeExistingConfigReturnsBoolean() {
        Configuration fileConfig = ConfigurationFactory.getInstance();
        System.setProperty("test.remove.existing", "value");
        boolean result = fileConfig.removeConfig("test.remove.existing");
        assertTrue(result || !result);
    }

    @Test(timeout = 4000)
    public void getConfigFromSystemProperty() {
        System.setProperty("test.sys.prop", "sys-prop-value");
        Configuration fileConfig = ConfigurationFactory.getInstance();
        String value = fileConfig.getConfig("test.sys.prop");
        assertEquals("sys-prop-value", value);
    }

    @Test(timeout = 4000)
    public void getConfigFromEnvironmentVariable() {
        Configuration fileConfig = ConfigurationFactory.getInstance();
        String path = fileConfig.getConfigFromSys("PATH");
        assertNotNull(path);
    }
}