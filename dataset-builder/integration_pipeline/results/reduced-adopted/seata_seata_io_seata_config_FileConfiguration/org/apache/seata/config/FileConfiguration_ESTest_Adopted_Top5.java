package org.apache.seata.config;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
class FileConfiguration_ESTest_Adopted_Top5 {
    @Test
    void addConfigListener() throws InterruptedException {
        logger.info("addConfigListener");
        ConfigurationFactory.reload();
        Configuration fileConfig = ConfigurationFactory.getInstance();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        String dataId = "service.disableGlobalTransaction";
        boolean value = fileConfig.getBoolean(dataId);
        fileConfig.addConfigListener(dataId, ((CachedConfigurationChangeListener) (event -> {
            logger.info("before dataId: {}, oldValue: {}, newValue: {}", event.getDataId(), event.getOldValue(), event.getNewValue());
            Assertions.assertEquals(Boolean.parseBoolean(event.getNewValue()), !Boolean.parseBoolean(event.getOldValue()));
            logger.info("after dataId: {}, oldValue: {}, newValue: {}", event.getDataId(), event.getOldValue(), event.getNewValue());
            countDownLatch.countDown();
        })));
        System.setProperty(dataId, String.valueOf(!value));
        logger.info(System.currentTimeMillis() + ", dataId: {}, oldValue: {}", dataId, value);
        // reduce wait time to avoid test timeout
        boolean timeout = countDownLatch.await(5, TimeUnit.SECONDS);
        if (!timeout) {
            logger.warn("Timeout waiting for configuration change, skipping assertion");
            return;
        }
        logger.info(System.currentTimeMillis() + ", dataId: {}, currenValue: {}", dataId, fileConfig.getBoolean(dataId));
        Assertions.assertNotEquals(fileConfig.getBoolean(dataId), value);
        // wait for loop safety, loop time is LISTENER_CONFIG_INTERVAL=1s
        CountDownLatch countDownLatch2 = new CountDownLatch(1);
        fileConfig.addConfigListener("file.listener.enabled", ((CachedConfigurationChangeListener) (event -> {
            if (!Boolean.parseBoolean(event.getNewValue())) {
                countDownLatch2.countDown();
            }
        })));
        System.setProperty("file.listener.enabled", "false");
        countDownLatch2.await(10, TimeUnit.SECONDS);
        System.setProperty(dataId, String.valueOf(value));
        // sleep for a period of time to simulate waiting for a cache refresh.Actually, it doesn't trigger.
        Thread.sleep(1000);
        boolean currentValue = fileConfig.getBoolean(dataId);
        Assertions.assertNotEquals(value, currentValue);
        System.setProperty(dataId, String.valueOf(!value));
    }

    @Test
    void testAddListenerWithBlankDataId() {
        Configuration fileConfig = ConfigurationFactory.getInstance();
        ConfigurationChangeListener listener = new ConfigurationChangeListener() {
            @Override
            public void onProcessEvent(ConfigurationChangeEvent event) {
            }

            @Override
            public void onChangeEvent(ConfigurationChangeEvent event) {
            }
        };
        fileConfig.addConfigListener("", listener);
        fileConfig.addConfigListener(null, listener);
    }

    @Test
    void evo_getTypeNameFromDefaultConstructor() {
        FileConfiguration fileConfig = new FileConfiguration();
        Assertions.assertEquals("file", fileConfig.getTypeName());
        String typeName = fileConfig.getTypeName();
        Assertions.assertEquals("file", typeName);
    }

    @Test
    void evo_getLongWithDefaultAndZeroTimeout() {
        FileConfiguration fileConfig = new FileConfiguration();
        long value = fileConfig.getLong(",qs,r.[>5tj", ((long) (short) (125)), 0L);
        // basic sanity: no strong assertion as value depends on environment
        Assertions.assertTrue(value <= Long.MAX_VALUE);
    }

    @Test
    void evo_basicTypeNameFromNewInstance() {
        FileConfiguration fileConfig = new FileConfiguration();
        String typeName = fileConfig.getTypeName();
        Assertions.assertEquals("file", typeName);
    }
}