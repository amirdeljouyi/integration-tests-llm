package io.seata.config;
import org.apache.seata.config.ConfigurationCache;
import org.apache.seata.config.ConfigurationChangeEvent;
import org.apache.seata.config.ConfigurationChangeType;
import org.junit.Test;
import static org.evosuite.runtime.EvoAssertions.verifyException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
public class FileConfiguration_ESTest_Improved {
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
}