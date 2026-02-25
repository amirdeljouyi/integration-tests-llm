package com.alibaba.druid.pool;

import junit.framework.TestCase;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class DruidDataSource_ESTest_Adopted extends TestCase {
    /**
     * 验证将mysql jdbc url中可能出现的密码信息全都掩码的效果，目前会出现的密码key名有password,password1,password2,password3,trustCertificateKeyStorePassword,clientCertificateKeyStorePassword
     * @see  <a href="https://dev.mysql.com/doc/connector-j/en/connector-j-connp-props-authentication.html">...</a>
     * @see <a href="https://dev.mysql.com/doc/connector-j/en/connector-j-connp-props-security.html">...</a>
     */
    public void test_sanitizedUrl() {
        String url = "jdbc:mysql://127.0.0.1:3306/druid?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowMultiQueries=true";
        String expectedUrl = url;
        String urlNew = DruidDataSource.sanitizedUrl(url);
        System.out.println("原始url=" + url);
        System.out.println("掩码url=" + urlNew);
        assertEquals(expectedUrl, urlNew);

        url = "jdbc:mysql://127.0.0.1:3306/druid?useUnicode=true&user=root&password";
        expectedUrl = url;
        urlNew = DruidDataSource.sanitizedUrl(url);
        System.out.println("原始url=" + url);
        System.out.println("掩码后url=" + urlNew);
        assertEquals(expectedUrl, urlNew);

        url = "jdbc:mysql://127.0.0.1:3306/druid?useUnicode=true&user=root&password=";
        expectedUrl = "jdbc:mysql://127.0.0.1:3306/druid?useUnicode=true&user=root&password=<masked>";
        urlNew = DruidDataSource.sanitizedUrl(url);
        System.out.println("原始url=" + url);
        System.out.println("掩码后url=" + urlNew);
        assertEquals(expectedUrl, urlNew);

        url = "jdbc:mysql://127.0.0.1:3306/druid?useUnicode=true&user=root&password=12345678";
        expectedUrl = "jdbc:mysql://127.0.0.1:3306/druid?useUnicode=true&user=root&password=<masked>";
        urlNew = DruidDataSource.sanitizedUrl(url);
        System.out.println("原始url=" + url);
        System.out.println("掩码后url=" + urlNew);
        assertEquals(expectedUrl, urlNew);

        url = "jdbc:mysql://127.0.0.1:3306/druid?useUnicode=true&user=root&password=12345678&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowMultiQueries=true";

        expectedUrl = "jdbc:mysql://127.0.0.1:3306/druid?useUnicode=true&user=root&password=<masked>&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowMultiQueries=true";
        urlNew = DruidDataSource.sanitizedUrl(url);
        System.out.println("原始url=" + url);
        System.out.println("掩码后url=" + urlNew);
        assertEquals(expectedUrl, urlNew);

        url = "jdbc:mysql://127.0.0.1:3306/druid?useUnicode=true&user=root&password1=12345678&password=12345678&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowMultiQueries=true";
        expectedUrl = "jdbc:mysql://127.0.0.1:3306/druid?useUnicode=true&user=root&password1=<masked>&password=<masked>&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowMultiQueries=true";

        urlNew = DruidDataSource.sanitizedUrl(url);
        System.out.println("原始url=" + url);
        System.out.println("掩码后url=" + urlNew);
        assertEquals(expectedUrl, urlNew);

        url = "jdbc:mysql://127.0.0.1:3306/druid?useUnicode=true&user=root&password2=12345678&password1=12345678&password=12345678&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowMultiQueries=true";
        expectedUrl = "jdbc:mysql://127.0.0.1:3306/druid?useUnicode=true&user=root&password2=<masked>&password1=<masked>&password=<masked>&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowMultiQueries=true";
        urlNew = DruidDataSource.sanitizedUrl(url);
        System.out.println("原始url=" + url);
        System.out.println("掩码后url=" + urlNew);
        assertEquals(expectedUrl, urlNew);

        url = "jdbc:mysql://127.0.0.1:3306/druid?useUnicode=true&user=root&password3=12345678&password2=12345678&password1=12345678&password=12345678&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowMultiQueries=true";
        expectedUrl = "jdbc:mysql://127.0.0.1:3306/druid?useUnicode=true&user=root&password3=<masked>&password2=<masked>&password1=<masked>&password=<masked>&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowMultiQueries=true";
        urlNew = DruidDataSource.sanitizedUrl(url);
        System.out.println("原始url=" + url);
        System.out.println("掩码后url=" + urlNew);
        assertEquals(expectedUrl, urlNew);

        url = "jdbc:mysql://127.0.0.1:3306/druid?clientCertificateKeyStorePassword=12345678&useUnicode=true&user=root&password3=12345678&password2=12345678&password1=12345678&password=12345678&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowMultiQueries=true";
        expectedUrl = "jdbc:mysql://127.0.0.1:3306/druid?clientCertificateKeyStorePassword=<masked>&useUnicode=true&user=root&password3=<masked>&password2=<masked>&password1=<masked>&password=<masked>&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowMultiQueries=true";
        urlNew = DruidDataSource.sanitizedUrl(url);
        System.out.println("原始url=" + url);
        System.out.println("掩码后url=" + urlNew);
        assertEquals(expectedUrl, urlNew);

        url = "jdbc:mysql://127.0.0.1:3306/druid?trustCertificateKeyStorePassword=12345678&useUnicode=true&user=root&password3=12345678&password2=12345678&password1=12345678&password=12345678&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowMultiQueries=true";
        expectedUrl = "jdbc:mysql://127.0.0.1:3306/druid?trustCertificateKeyStorePassword=<masked>&useUnicode=true&user=root&password3=<masked>&password2=<masked>&password1=<masked>&password=<masked>&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowMultiQueries=true";
        urlNew = DruidDataSource.sanitizedUrl(url);
        System.out.println("原始url=" + url);
        System.out.println("掩码后url=" + urlNew);
        assertEquals(expectedUrl, urlNew);
    }

    public void testResolveDriver_withoutUrl_throwsSQLException() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource(false);
        try {
            dataSource.resolveDriver();
            fail("Expecting exception: SQLException");
        } catch (SQLException e) {
            // url not set
        }
    }

    public void testHandleConnectionException_withNullPooledConnection_throwsNPE() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource();
        SQLWarning sqlWarning = new SQLWarning("tSVE<.", "tSVE<.", 1433);
        try {
            dataSource.handleConnectionException(((DruidPooledConnection) (null)), ((Throwable) (sqlWarning)), "tSVE<.");
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            // Cannot invoke "com.alibaba.druid.pool.DruidPooledConnection.getConnectionHolder()" because "pooledConnection" is null
        }
    }

    public void testGetRawDriverMajorVersion_whenNoDriver_returnsMinusOne() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource();
        int majorVersion = dataSource.getRawDriverMajorVersion();
        assertTrue(dataSource.isResetStatEnable());
        assertTrue(dataSource.isEnable());
        assertEquals(-1, majorVersion);
        assertTrue(dataSource.isLogDifferentThread());
    }

    public void testGetProperties_default_returnsEmptyJsonObjectString() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource(false);
        String propertiesJson = dataSource.getProperties();
        assertTrue(dataSource.isEnable());
        assertTrue(dataSource.isLogDifferentThread());
        assertTrue(dataSource.isResetStatEnable());
        assertEquals("{}", propertiesJson);
    }

    public void testGetDataSourceStat_doesNotThrow() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.getDataSourceStat();
        assertTrue(dataSource.isLogDifferentThread());
        assertTrue(dataSource.isEnable());
        assertTrue(dataSource.isResetStatEnable());
    }

    public void testGetCreateCount_initiallyZero() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource();
        long createCount = dataSource.getCreateCount();
        assertTrue(dataSource.isLogDifferentThread());
        assertTrue(dataSource.isResetStatEnable());
        assertEquals(0L, createCount);
    }

    public void testGetConnectCount_initiallyZero() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource();
        long connectCount = dataSource.getConnectCount();
        assertEquals(0L, connectCount);
        assertTrue(dataSource.isLogDifferentThread());
        assertTrue(dataSource.isEnable());
        assertTrue(dataSource.isResetStatEnable());
    }

    public void testGetCloseCount_initiallyZero() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource(true);
        long closeCount = dataSource.getCloseCount();
        assertTrue(dataSource.isResetStatEnable());
        assertTrue(dataSource.isEnable());
        assertEquals(0L, closeCount);
        assertTrue(dataSource.isLogDifferentThread());
    }

    public void testGetActiveCount_initiallyZero() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource();
        int activeCount = dataSource.getActiveCount();
        assertTrue(dataSource.isResetStatEnable());
        assertTrue(dataSource.isEnable());
        assertEquals(0, activeCount);
        assertTrue(dataSource.isLogDifferentThread());
    }

    public void testCloneDruidDataSource_preservesTestOnBorrowWhenTrue() throws Throwable {
        DruidDataSource originalDataSource = new DruidDataSource();
        originalDataSource.setTestOnBorrow(true);
        DruidDataSource clonedDataSource = originalDataSource.cloneDruidDataSource();
        assertNotSame(clonedDataSource, originalDataSource);
        assertTrue(clonedDataSource.isResetStatEnable());
        assertTrue(clonedDataSource.isLogDifferentThread());
        assertTrue(clonedDataSource.isEnable());
        assertTrue(clonedDataSource.isTestOnBorrow());
    }

    public void testCloneDruidDataSource_preservesDupCloseLogEnableTrue() throws Throwable {
        DruidDataSource originalDataSource = new DruidDataSource();
        originalDataSource.setDupCloseLogEnable(true);
        DruidDataSource clonedDataSource = originalDataSource.cloneDruidDataSource();
        assertTrue(clonedDataSource.isLogDifferentThread());
        assertTrue(clonedDataSource.isResetStatEnable());
        assertTrue(clonedDataSource.isDupCloseLogEnable());
        assertNotSame(clonedDataSource, originalDataSource);
        assertTrue(clonedDataSource.isEnable());
    }

    public void testGetWallStatValue_doesNotThrow() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.getWallStatValue(true);
        assertTrue(dataSource.isLogDifferentThread());
        assertTrue(dataSource.isResetStatEnable());
        assertTrue(dataSource.isEnable());
    }

    public void testGetActivePeakTime_initiallyNull() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource();
        Date activePeakTime = dataSource.getActivePeakTime();
        assertTrue(dataSource.isEnable());
        assertTrue(dataSource.isResetStatEnable());
        assertTrue(dataSource.isLogDifferentThread());
        assertNull(activePeakTime);
    }

    public void testShrink_withFlags_doesNotThrow() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.shrink(true, true);
        assertTrue(dataSource.isResetStatEnable());
        assertTrue(dataSource.isEnable());
    }

    public void testGetFilterClassNames_default_notNull() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource(false);
        List<String> filterClassNames = dataSource.getFilterClassNames();
        assertTrue(dataSource.isEnable());
        assertNotNull(filterClassNames);
        assertTrue(dataSource.isResetStatEnable());
        assertTrue(dataSource.isLogDifferentThread());
    }

    public void testGetPoolingPeakTime_initiallyNull() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource();
        Date poolingPeakTime = dataSource.getPoolingPeakTime();
        assertNull(poolingPeakTime);
    }

    public void testClose_doesNotDisable() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.close();
        assertTrue(dataSource.isEnable());
    }

    public void testGetConnection_withTimeout_doesNotThrow() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.getConnection(((long) (0)));
        assertTrue(dataSource.isEnable());
        assertTrue(dataSource.isResetStatEnable());
    }

    public void testInit_withoutUrl_throwsSQLException() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource(false);
        try {
            dataSource.init();
            fail("Expecting exception: SQLException");
        } catch (SQLException e) {
            // url not set
        }
    }

    public void testResetStat_incrementsResetCount() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource(true);
        dataSource.resetStat();
        assertEquals(1L, dataSource.getResetCount());
    }

    public void testGetErrorCount_initiallyZero() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource(false);
        long errorCount = dataSource.getErrorCount();
        assertTrue(dataSource.isEnable());
        assertTrue(dataSource.isLogDifferentThread());
        assertEquals(0L, errorCount);
        assertTrue(dataSource.isResetStatEnable());
    }

    public void testGetPoolingCount_initiallyZero() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource();
        int poolingCount = dataSource.getPoolingCount();
        assertTrue(dataSource.isLogDifferentThread());
        assertTrue(dataSource.isEnable());
        assertEquals(0, poolingCount);
    }

    public void testGetConnection_noArgs_doesNotThrow() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.getConnection();
        assertTrue(dataSource.isEnable());
        assertTrue(dataSource.isResetStatEnable());
    }

    public void testIsResetStatEnable_defaultTrue() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource();
        boolean resetStatEnabled = dataSource.isResetStatEnable();
        assertTrue(dataSource.isEnable());
        assertTrue(resetStatEnabled);
        assertTrue(dataSource.isLogDifferentThread());
    }

    public void testGetConnectErrorCount_initiallyZero() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource();
        long connectErrorCount = dataSource.getConnectErrorCount();
        assertEquals(0L, connectErrorCount);
        assertTrue(dataSource.isEnable());
        assertTrue(dataSource.isResetStatEnable());
        assertTrue(dataSource.isLogDifferentThread());
    }

    public void testPreDeregister_doesNotThrow() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.preDeregister();
        assertTrue(dataSource.isResetStatEnable());
        assertTrue(dataSource.isEnable());
        assertTrue(dataSource.isLogDifferentThread());
    }

    public void testTryGetConnection_doesNotThrow() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource(true);
        dataSource.tryGetConnection();
        assertTrue(dataSource.isLogDifferentThread());
        assertTrue(dataSource.isEnable());
    }

    public void testUnwrap_withUnrelatedType_doesNotThrow() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource(true);
        Class<ScheduledThreadPoolExecutor> scheduledThreadPoolExecutorClass = ScheduledThreadPoolExecutor.class;
        dataSource.unwrap(scheduledThreadPoolExecutorClass);
        assertTrue(dataSource.isResetStatEnable());
        assertTrue(dataSource.isLogDifferentThread());
        assertTrue(dataSource.isEnable());
    }

    public void testIsWrapperFor_unrelatedType_returnsFalse() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource();
        Class<ScheduledThreadPoolExecutor> scheduledThreadPoolExecutorClass = ScheduledThreadPoolExecutor.class;
        boolean isWrapper = dataSource.isWrapperFor(scheduledThreadPoolExecutorClass);
        assertTrue(dataSource.isEnable());
        assertTrue(dataSource.isResetStatEnable());
        assertFalse(isWrapper);
        assertTrue(dataSource.isLogDifferentThread());
    }

    public void testGetWallStatMap_doesNotThrow() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource(false);
        dataSource.getWallStatMap();
        assertTrue(dataSource.isLogDifferentThread());
        assertTrue(dataSource.isResetStatEnable());
        assertTrue(dataSource.isEnable());
    }

    public void testGetPoolingConnectionInfo_doesNotThrow() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.getPoolingConnectionInfo();
        assertTrue(dataSource.isEnable());
        assertTrue(dataSource.isResetStatEnable());
    }

    public void testRemoveAbandoned_returnsZero() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource(false);
        int removed = dataSource.removeAbandoned();
        assertTrue(dataSource.isEnable());
        assertTrue(dataSource.isResetStatEnable());
        assertEquals(0, removed);
    }

    public void testDestroyTask_run_doesNotThrow() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource();
        DruidDataSource.DestroyTask destroyTask = dataSource.new DestroyTask();
        destroyTask.run();
        assertTrue(dataSource.isResetStatEnable());
        assertTrue(dataSource.isLogDifferentThread());
        assertTrue(dataSource.isEnable());
    }

    public void testDestroyConnectionThread_run_throwsNPE() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource(true);
        DruidDataSource.DestroyConnectionThread destroyConnectionThread = dataSource.new DestroyConnectionThread("ZdIS^gE2H");
        try {
            destroyConnectionThread.run();
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            // Cannot invoke "com.alibaba.druid.pool.DruidDataSource$DestroyTask.run()" because the return value of "com.alibaba.druid.pool.DruidDataSource.access$1300(com.alibaba.druid.pool.DruidDataSource)" is null
        }
    }

    public void testGetStatValueAndReset_withoutStat_throwsNPE() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource();
        try {
            dataSource.getStatValueAndReset();
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            // Cannot invoke "com.alibaba.druid.stat.JdbcDataSourceStat.getConnectionHoldHistogram()" because the return value of "com.alibaba.druid.pool.DruidDataSource.getDataSourceStat()" is null
        }
    }

    public void testInitCheck_doesNotThrow() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource(true);
        dataSource.initCheck();
        assertTrue(dataSource.isResetStatEnable());
        assertTrue(dataSource.isEnable());
    }

    public void testFill_withSizeWithoutUrl_throwsSQLException() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource();
        try {
            dataSource.fill(72);
            fail("Expecting exception: SQLException");
        } catch (SQLException e) {
            // url not set
        }
    }

    public void testSetConnectProperties_withExistingProperties_doesNotThrow() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource(true);
        Properties connectProperties = dataSource.getConnectProperties();
        dataSource.setConnectProperties(connectProperties);
        assertTrue(dataSource.isLogDifferentThread());
        assertTrue(dataSource.isEnable());
        assertTrue(dataSource.isResetStatEnable());
    }

    public void testSetEnable_true_setsFlag() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setEnable(true);
        assertTrue(dataSource.isResetStatEnable());
        assertEquals(0L, dataSource.getNotEmptySignalCount());
        assertTrue(dataSource.isEnable());
    }

    public void testCreateConnectionThread_constructor_doesNotThrow() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource();
        DruidDataSource.CreateConnectionThread createConnectionThread = dataSource.new CreateConnectionThread("PoolingPeak");
    }

    public void testCreateConnectionTask_run_doesNotThrow() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource(false);
        DruidDataSource.CreateConnectionTask createConnectionTask = dataSource.new CreateConnectionTask();
        createConnectionTask.run();
        assertTrue(dataSource.isEnable());
        assertTrue(dataSource.isLogDifferentThread());
        assertTrue(dataSource.isResetStatEnable());
    }

    public void testCreateConnectionTask_constructorWithBoolean_doesNotThrow() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource();
        DruidDataSource.CreateConnectionTask createConnectionTask = dataSource.new CreateConnectionTask(true);
        assertTrue(dataSource.isLogDifferentThread());
        assertTrue(dataSource.isEnable());
        assertTrue(dataSource.isResetStatEnable());
    }

    public void testSetCheckExecuteTime_false_disablesFlag() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setCheckExecuteTime(false);
        assertTrue(dataSource.isResetStatEnable());
        assertTrue(dataSource.isLogDifferentThread());
        assertFalse(dataSource.isCheckExecuteTime());
        assertTrue(dataSource.isEnable());
    }

    public void testGetSqlStatMap_withoutStat_throwsNPE() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource(true);
        try {
            dataSource.getSqlStatMap();
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            // Cannot invoke "com.alibaba.druid.stat.JdbcDataSourceStat.getSqlStatMap()" because the return value of "com.alibaba.druid.pool.DruidDataSource.getDataSourceStat()" is null
        }
    }

    public void testGetPooledConnection_withCredentials_throwsUnsupportedOperationException() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource();
        try {
            dataSource.getPooledConnection("", "keepAliveErr");
            fail("Expecting exception: UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // Not supported by DruidDataSource
        }
    }

    public void testGetNotEmptySignalCount_initiallyZero() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource(true);
        long notEmptySignalCount = dataSource.getNotEmptySignalCount();
        assertEquals(0L, notEmptySignalCount);
        assertTrue(dataSource.isEnable());
        assertTrue(dataSource.isLogDifferentThread());
    }

    public void testIsUseGlobalDataSourceStat_defaultFalse() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource();
        boolean useGlobalStat = dataSource.isUseGlobalDataSourceStat();
        assertTrue(dataSource.isEnable());
        assertFalse(useGlobalStat);
        assertTrue(dataSource.isResetStatEnable());
        assertTrue(dataSource.isLogDifferentThread());
    }

    public void testGetSqlStat_byIndex_withoutStat_throwsNPE() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource();
        try {
            dataSource.getSqlStat(0);
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            // Cannot invoke "com.alibaba.druid.stat.JdbcDataSourceStat.getSqlStat(int)" because the return value of "com.alibaba.druid.pool.DruidDataSource.getDataSourceStat()" is null
        }
    }

    public void testGetPoolingPeak_initiallyZero() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource();
        int poolingPeak = dataSource.getPoolingPeak();
        assertTrue(dataSource.isLogDifferentThread());
        assertTrue(dataSource.isResetStatEnable());
        assertEquals(0, poolingPeak);
        assertTrue(dataSource.isEnable());
    }

    public void testIsLoadSpifilterSkip_defaultFalse() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource();
        boolean loadSpiFilterSkip = dataSource.isLoadSpifilterSkip();
        assertTrue(dataSource.isResetStatEnable());
        assertFalse(loadSpiFilterSkip);
        assertTrue(dataSource.isEnable());
        assertTrue(dataSource.isLogDifferentThread());
    }

    public void testGetConnection_withUsernamePassword_setsPassword() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource(true);
        dataSource.getConnection("zL|z", "QueryTimeout");
        assertEquals("QueryTimeout", dataSource.getPassword());
    }

    public void testFill_withoutUrl_throwsSQLException() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setLoadSpifilterSkip(true);
        try {
            dataSource.fill();
            fail("Expecting exception: SQLException");
        } catch (SQLException e) {
            // url not set
        }
    }

    public void testSetLogDifferentThread_false_updatesFlag() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource();
        assertTrue(dataSource.isLogDifferentThread());
        dataSource.setLogDifferentThread(false);
        assertFalse(dataSource.isLogDifferentThread());
    }

    public void testShrink_noArgs_doesNotThrow() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.shrink();
        assertTrue(dataSource.isEnable());
        assertTrue(dataSource.isLogDifferentThread());
        assertTrue(dataSource.isResetStatEnable());
    }

    public void testGetRecycleErrorCount_initiallyZero() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource();
        long recycleErrorCount = dataSource.getRecycleErrorCount();
        assertTrue(dataSource.isResetStatEnable());
        assertEquals(0L, recycleErrorCount);
    }

    public void testRegisterMbean_marksAsRegistered() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.registerMbean();
        assertTrue(dataSource.isMbeanRegistered());
    }

    public void testSetKeepAlive_true_setsFlag() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setKeepAlive(true);
        assertTrue(dataSource.isKeepAlive());
    }

    public void testGetLockQueueLength_doesNotThrow() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.getLockQueueLength();
        assertTrue(dataSource.isEnable());
        assertTrue(dataSource.isResetStatEnable());
        assertTrue(dataSource.isLogDifferentThread());
    }

    public void testPostDeregister_doesNotThrow() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.postDeregister();
        assertTrue(dataSource.isResetStatEnable());
        assertTrue(dataSource.isLogDifferentThread());
        assertTrue(dataSource.isEnable());
    }

    public void testGetRemoveAbandonedCount_initiallyZero() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource();
        long removeAbandonedCount = dataSource.getRemoveAbandonedCount();
        assertTrue(dataSource.isEnable());
        assertTrue(dataSource.isResetStatEnable());
        assertEquals(0L, removeAbandonedCount);
        assertTrue(dataSource.isLogDifferentThread());
    }

    public void testGetVersion_doesNotThrow() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource(true);
        dataSource.getVersion();
        assertTrue(dataSource.isEnable());
        assertTrue(dataSource.isLogDifferentThread());
        assertTrue(dataSource.isResetStatEnable());
    }

    public void testGetReference_doesNotThrow() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.getReference();
        assertTrue(dataSource.isResetStatEnable());
        assertTrue(dataSource.isLogDifferentThread());
        assertTrue(dataSource.isEnable());
    }

    public void testGetInitStackTrace_doesNotThrow() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.getInitStackTrace();
        assertTrue(dataSource.isEnable());
        assertTrue(dataSource.isResetStatEnable());
        assertTrue(dataSource.isLogDifferentThread());
    }

    public void testDump_returnsFormattedStateString() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource();
        String dump = dataSource.dump();
        assertEquals("{\n\tCreateTime:\"2014-02-14 20:21:21\",\n\tActiveCount:0,\n\tPoolingCount:0,\n\tCreateCount:0,\n\tDestroyCount:0,\n\tCloseCount:0,\n\tConnectCount:0,\n\tConnections:[\n\t]\n}", dump);
        assertTrue(dataSource.isEnable());
    }

    public void testGetSqlStat_byId_withoutStat_throwsNPE() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource(true);
        try {
            dataSource.getSqlStat(0L);
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            // Cannot invoke "com.alibaba.druid.stat.JdbcDataSourceStat.getSqlStat(long)" because the return value of "com.alibaba.druid.pool.DruidDataSource.getDataSourceStat()" is null
        }
    }

    public void testClone_createsDistinctCopyWithSameFlags() throws Throwable {
        DruidDataSource originalDataSource = new DruidDataSource(true);
        DruidDataSource clonedDataSource = ((DruidDataSource) (originalDataSource.clone()));
        assertTrue(clonedDataSource.isEnable());
        assertTrue(clonedDataSource.isLogDifferentThread());
        assertTrue(clonedDataSource.isResetStatEnable());
        assertNotSame(clonedDataSource, originalDataSource);
    }

    public void testGetNotEmptyWaitThreadPeak_initiallyZero() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource(true);
        int notEmptyWaitThreadPeak = dataSource.getNotEmptyWaitThreadPeak();
        assertTrue(dataSource.isEnable());
        assertTrue(dataSource.isResetStatEnable());
        assertEquals(0, notEmptyWaitThreadPeak);
        assertTrue(dataSource.isLogDifferentThread());
    }

    public void testSetPoolPreparedStatements_true_setsFlag() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setPoolPreparedStatements(true);
        assertTrue(dataSource.isPoolPreparedStatements());
    }

    public void testSetAsyncInit_true_setsFlag() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setAsyncInit(true);
        assertTrue(dataSource.isAsyncInit());
    }

    public void testGetStatData_withoutStat_throwsNPE() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource(false);
        try {
            dataSource.getStatData();
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            // Cannot invoke "com.alibaba.druid.stat.JdbcDataSourceStat.getConnectionHoldHistogram()" because the return value of "com.alibaba.druid.pool.DruidDataSource.getDataSourceStat()" is null
        }
    }

    public void testRestart_incrementsResetCount() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource(false);
        dataSource.restart();
        assertEquals(1L, dataSource.getResetCount());
    }

    public void testGetCompositeData_withoutStat_throwsNPE() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource();
        try {
            dataSource.getCompositeData();
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            // Cannot invoke "com.alibaba.druid.stat.JdbcDataSourceStat.getConnectionStat()" because "stat" is null
        }
    }

    public void testCreateAndStartDestroyThread_doesNotThrow() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.createAndStartDestroyThread();
        assertTrue(dataSource.isLogDifferentThread());
        assertTrue(dataSource.isResetStatEnable());
        assertTrue(dataSource.isEnable());
    }
}