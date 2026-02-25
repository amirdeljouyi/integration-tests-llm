package org.apache.seata.config;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
class FileConfiguration_ESTest_Adopted_Agentic_Top5 {
    @Test
    void testPutIfAbsentAndRemoveConfigAfterLatestConfigQueries() {
        FileConfiguration fileConfig = new FileConfiguration("");
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
    void testHandleLatestConfigQueriesNullsAndRemovals() {
        FileConfiguration fileConfig = new FileConfiguration("-bvOY");
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
    void testCreateWithNameAndLoadOnStartThenRemoveConfig() {
        FileConfiguration fileConfig = new FileConfiguration("J8=rMZJZ", true);
        fileConfig.getConfig("6KV{OJCIW", -62L);
        boolean removedExistingNameResult = fileConfig.removeConfig("J8=rMZJZ", 1L);
        Assertions.assertTrue(removedExistingNameResult);
    }

    @Test
    void testConstructingFromAnotherConfigurationKeepsTypeName() {
        FileConfiguration apacheFileConfig = new FileConfiguration();
        io.seata.config.FileConfiguration wrappedFileConfig = new io.seata.config.FileConfiguration(apacheFileConfig);
        String typeName = wrappedFileConfig.getTypeName();
        Assertions.assertEquals("file", typeName);
    }

    @Test
    void testPublishChangeEventsAndPutConfigsViaWrappedConfiguration() {
        FileConfiguration apacheFileConfig = new FileConfiguration(".yK>", true);
        io.seata.config.FileConfiguration delegatingFileConfig = new io.seata.config.FileConfiguration(apacheFileConfig);
        ConfigurationCache configurationCache = ConfigurationCache.getInstance();
        configurationCache.onShutDown();
        apacheFileConfig.getLong("iaOs!", -1405L, 1000L);
        ConfigurationChangeType initialChangeType = ConfigurationChangeType.MODIFY;
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
        Assertions.assertTrue(firstPutResult);
        boolean putIfAbsentResult = delegatingFileConfig.putConfigIfAbsent("", "iaOs!", -1405L);
        Assertions.assertFalse(putIfAbsentResult);
        boolean secondPutResult = delegatingFileConfig.putConfig("", "", -1405L);
        Assertions.assertFalse(secondPutResult);
    }
}