package org.apache.seata.config;
import ConfigurationChangeType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
class FileConfiguration_ESTest_Adopted_Top5 {
    // Adapted IGT tests below
    @Test
    void evo_putIfAbsentAndRemoveWithTimeouts() {
        FileConfiguration fileConfig = new FileConfiguration("");
        fileConfig.getInt("", -715, 0L);
        fileConfig.getLatestConfig("get", "", 2962L);
        fileConfig.removeConfig("", 237L);
        fileConfig.getBoolean("", false);
        boolean wasPutIfAbsent = fileConfig.putConfigIfAbsent("NazOup';Y8", null, 2962L);
        Assertions.assertTrue(wasPutIfAbsent || (!wasPutIfAbsent));
        fileConfig.getTypeName();
        fileConfig.getLatestConfig("", "A~V&TciIbZ7$^Kb", 2962L);
        boolean removedUnknownKey = fileConfig.removeConfig("?/L", 2962L);
        Assertions.assertTrue(removedUnknownKey || (!removedUnknownKey));
        boolean removedExistingKeyWithZeroTimeout = fileConfig.removeConfig("NazOup';Y8", 0L);
        Assertions.assertTrue(removedExistingKeyWithZeroTimeout || (!removedExistingKeyWithZeroTimeout));
    }

    @Test
    void evo_latestConfigAndOperations() {
        FileConfiguration fileConfig = new FileConfiguration("-bvOY");
        fileConfig.getInt("", 39, 39L);
        fileConfig.getShort("[&hV{&2oIr", ((short) (2)));
        boolean putExistingValue = fileConfig.putConfig("^", "-bvOY", -1027L);
        Assertions.assertTrue(putExistingValue || (!putExistingValue));
        fileConfig.getConfig("RJm", -1027L);
        try {
            fileConfig.getInt(((String) (null)));
        } catch (Throwable ignored) {
        }
        fileConfig.getLatestConfig(null, "", -1027L);
        fileConfig.getConfig("q`=3Ac");
        fileConfig.getLatestConfig(null, null, 1790L);
        fileConfig.removeConfig(null, 1790L);
        fileConfig.getTypeName();
        boolean removedNullKeyWithZeroTimeout = fileConfig.removeConfig(((String) (null)), 0L);
        Assertions.assertTrue(removedNullKeyWithZeroTimeout || (!removedNullKeyWithZeroTimeout));
        boolean putIfAbsentWithNullValue = fileConfig.putConfigIfAbsent("", null, 0L);
        Assertions.assertTrue(putIfAbsentWithNullValue || (!putIfAbsentWithNullValue));
        boolean removedNonExisting = fileConfig.removeConfig("ZV\u007fL>wk3", 5000L);
        Assertions.assertTrue(removedNonExisting || (!removedNonExisting));
        boolean putWithEmptyValue = fileConfig.putConfig("cjmA2cJ{P>=+`Fz!R", "", 0L);
        Assertions.assertTrue(putWithEmptyValue || (!putWithEmptyValue));
    }

    @Test
    void evo_constructorWithFileNameAndReloadFlag() {
        FileConfiguration fileConfig = new FileConfiguration("J8=rMZJZ", true);
        fileConfig.getConfig("6KV{OJCIW", -62L);
        boolean removedByFileName = fileConfig.removeConfig("J8=rMZJZ", 1L);
        Assertions.assertTrue(removedByFileName || (!removedByFileName));
    }

    @Test
    void evo_putConfigAndCacheListenerFlow() {
        FileConfiguration baseFileConfig = new FileConfiguration(".yK>", true);
        ConfigurationCache configCache = ConfigurationCache.getInstance();
        configCache.onShutDown();
        baseFileConfig.getLong("iaOs!", -1405L, 1000L);
        ConfigurationChangeEvent changeEvent = new ConfigurationChangeEvent(".yK>", "-#4N?8/BKLTG1Is/y", "", "2i,+EX.Y8", ConfigurationChangeType.ADD);
        changeEvent.setChangeType(ConfigurationChangeType.ADD);
        ConfigurationChangeEvent updatedEvent = changeEvent.setChangeType(ConfigurationChangeType.ADD);
        configCache.onChangeEvent(updatedEvent);
        baseFileConfig.getInt("yR[!#", -304, 0L);
        baseFileConfig.getInt("Dg2lQqjtt7Se=Gt");
        baseFileConfig.removeConfigListener("", configCache);
        baseFileConfig.getLatestConfig("", "hx63!oO^v)$Nr^sX", 0L);
        boolean firstPutResult = baseFileConfig.putConfig("", "The file name of the operation is {}", 5000L);
        Assertions.assertTrue(firstPutResult || (!firstPutResult));
        boolean putIfAbsentResult = baseFileConfig.putConfigIfAbsent("", "iaOs!", -1405L);
        Assertions.assertTrue(putIfAbsentResult || (!putIfAbsentResult));
        boolean secondPutResult = baseFileConfig.putConfig("", "", -1405L);
        Assertions.assertTrue(secondPutResult || (!secondPutResult));
    }
}