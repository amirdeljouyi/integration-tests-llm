package io.seata.config;
import io.seata.config.FileConfiguration_ESTest_scaffolding;
import org.junit.Test;
import static org.evosuite.runtime.EvoAssertions.verifyException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
public class FileConfiguration_ESTest_Top5 extends FileConfiguration_ESTest_scaffolding {
    @Test(timeout = 4000)
    public void testGetTypeNameAndCreatesFileConfigurationTakingNoArguments() throws Throwable {
        FileConfiguration fileConfiguration0 = new FileConfiguration();
        assertEquals("file", fileConfiguration0.getTypeName());
        String string0 = fileConfiguration0.getTypeName();
        assertEquals("file", string0);
    }

    @Test(timeout = 4000)
    public void testCreatesFileConfigurationTakingNoArguments0() throws Throwable {
        FileConfiguration fileConfiguration0 = new FileConfiguration();
        // Undeclared exception!
        try {
            fileConfiguration0.removeConfig("S[w!jc=");
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            // 
            // Cannot invoke \"java.util.concurrent.ExecutorService.submit(java.lang.Runnable)\" because \"this.configOperateExecutor\" is null
            // 
            verifyException("org.apache.seata.config.FileConfiguration", e);
        }
    }

    @Test(timeout = 4000)
    public void testPutConfigThrowsNullPointerException() throws Throwable {
        FileConfiguration fileConfiguration0 = new FileConfiguration();
        // Undeclared exception!
        try {
            fileConfiguration0.putConfig("", "", 0L);
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            // 
            // Cannot invoke \"java.util.concurrent.ExecutorService.submit(java.lang.Runnable)\" because \"this.configOperateExecutor\" is null
            // 
            verifyException("org.apache.seata.config.FileConfiguration", e);
        }
    }

    @Test(timeout = 4000)
    public void testCreatesFileConfigurationTakingConfiguration() throws Throwable {
        org.apache.seata.config.FileConfiguration fileConfiguration0 = new org.apache.seata.config.FileConfiguration();
        FileConfiguration fileConfiguration1 = new FileConfiguration(fileConfiguration0);
        String string0 = fileConfiguration1.getTypeName();
        assertEquals("file", string0);
    }

    @Test(timeout = 4000)
    public void testCreatesFileConfigurationTakingNoArguments2() throws Throwable {
        FileConfiguration fileConfiguration0 = new FileConfiguration();
        // Undeclared exception!
        try {
            fileConfiguration0.putConfigIfAbsent(((String) (null)), ((String) (null)));
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            // 
            // Cannot invoke \"java.util.concurrent.ExecutorService.submit(java.lang.Runnable)\" because \"this.configOperateExecutor\" is null
            // 
            verifyException("org.apache.seata.config.FileConfiguration", e);
        }
    }
}
