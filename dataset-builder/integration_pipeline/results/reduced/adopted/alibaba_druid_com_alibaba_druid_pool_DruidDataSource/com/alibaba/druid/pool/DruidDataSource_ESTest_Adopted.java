package com.alibaba.druid.pool;
public class DruidDataSource_ESTest_Adopted {
    @org.junit.Test(timeout = 4000)
    public void testResolveDriver_withoutUrl_throwsSQLException() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource(false);
        try {
            dataSource.resolveDriver();
            org.junit.Assert.fail("Expecting exception: SQLException");
        } catch (java.sql.SQLException e) {
            // 
            // url not set
            // 
            org.evosuite.runtime.EvoAssertions.verifyException("com.alibaba.druid.pool.DruidDataSource", e);
        }
    }

    @org.junit.Test(timeout = 4000)
    public void testHandleConnectionException_withNullConnection_throwsNullPointerException() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource();
        java.sql.SQLWarning sqlWarning = new java.sql.SQLWarning("tSVE<.", "tSVE<.", 1433);
        // Undeclared exception!
        try {
            dataSource.handleConnectionException(((com.alibaba.druid.pool.DruidPooledConnection) (null)), ((java.lang.Throwable) (sqlWarning)), "tSVE<.");
            org.junit.Assert.fail("Expecting exception: NullPointerException");
        } catch (java.lang.NullPointerException e) {
            // 
            // Cannot invoke \"com.alibaba.druid.pool.DruidPooledConnection.getConnectionHolder()\" because \"pooledConnection\" is null
            // 
            org.evosuite.runtime.EvoAssertions.verifyException("com.alibaba.druid.pool.DruidDataSource", e);
        }
    }

    @org.junit.Test(timeout = 4000)
    public void testGetRawDriverMajorVersion_defaultIsMinusOne() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource();
        int majorVersion = dataSource.getRawDriverMajorVersion();
        org.junit.Assert.assertTrue(dataSource.isResetStatEnable());
        org.junit.Assert.assertTrue(dataSource.isEnable());
        org.junit.Assert.assertEquals(-1, majorVersion);
        org.junit.Assert.assertTrue(dataSource.isLogDifferentThread());
    }

    @org.junit.Test(timeout = 4000)
    public void testGetProperties_returnsEmptyJsonByDefault() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource(false);
        java.lang.String propertiesJson = dataSource.getProperties();
        org.junit.Assert.assertTrue(dataSource.isEnable());
        org.junit.Assert.assertTrue(dataSource.isLogDifferentThread());
        org.junit.Assert.assertTrue(dataSource.isResetStatEnable());
        org.junit.Assert.assertEquals("{}", propertiesJson);
    }

    @org.junit.Test(timeout = 4000)
    public void testGetDataSourceStat_default() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource();
        dataSource.getDataSourceStat();
        org.junit.Assert.assertTrue(dataSource.isLogDifferentThread());
        org.junit.Assert.assertTrue(dataSource.isEnable());
        org.junit.Assert.assertTrue(dataSource.isResetStatEnable());
    }

    @org.junit.Test(timeout = 4000)
    public void testGetCreateCount_startsAtZero() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource();
        long createCount = dataSource.getCreateCount();
        org.junit.Assert.assertTrue(dataSource.isLogDifferentThread());
        org.junit.Assert.assertTrue(dataSource.isResetStatEnable());
        org.junit.Assert.assertEquals(0L, createCount);
    }

    @org.junit.Test(timeout = 4000)
    public void testGetConnectCount_startsAtZero() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource();
        long connectCount = dataSource.getConnectCount();
        org.junit.Assert.assertEquals(0L, connectCount);
        org.junit.Assert.assertTrue(dataSource.isLogDifferentThread());
        org.junit.Assert.assertTrue(dataSource.isEnable());
        org.junit.Assert.assertTrue(dataSource.isResetStatEnable());
    }

    @org.junit.Test(timeout = 4000)
    public void testGetCloseCount_startsAtZero() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource(true);
        long closeCount = dataSource.getCloseCount();
        org.junit.Assert.assertTrue(dataSource.isResetStatEnable());
        org.junit.Assert.assertTrue(dataSource.isEnable());
        org.junit.Assert.assertEquals(0L, closeCount);
        org.junit.Assert.assertTrue(dataSource.isLogDifferentThread());
    }

    @org.junit.Test(timeout = 4000)
    public void testGetActiveCount_startsAtZero() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource();
        int activeCount = dataSource.getActiveCount();
        org.junit.Assert.assertTrue(dataSource.isResetStatEnable());
        org.junit.Assert.assertTrue(dataSource.isEnable());
        org.junit.Assert.assertEquals(0, activeCount);
        org.junit.Assert.assertTrue(dataSource.isLogDifferentThread());
    }

    @org.junit.Test(timeout = 4000)
    public void testCloneDruidDataSource_copiesTestOnBorrow() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource();
        dataSource.setTestOnBorrow(true);
        com.alibaba.druid.pool.DruidDataSource cloned = dataSource.cloneDruidDataSource();
        org.junit.Assert.assertNotSame(cloned, dataSource);
        org.junit.Assert.assertTrue(cloned.isResetStatEnable());
        org.junit.Assert.assertTrue(cloned.isLogDifferentThread());
        org.junit.Assert.assertTrue(cloned.isEnable());
        org.junit.Assert.assertTrue(cloned.isTestOnBorrow());
    }

    @org.junit.Test(timeout = 4000)
    public void testCloneDruidDataSource_preservesDupCloseLogEnable() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource();
        dataSource.setDupCloseLogEnable(true);
        com.alibaba.druid.pool.DruidDataSource cloned = dataSource.cloneDruidDataSource();
        org.junit.Assert.assertTrue(cloned.isLogDifferentThread());
        org.junit.Assert.assertTrue(cloned.isResetStatEnable());
        org.junit.Assert.assertTrue(cloned.isDupCloseLogEnable());
        org.junit.Assert.assertNotSame(cloned, dataSource);
        org.junit.Assert.assertTrue(cloned.isEnable());
    }

    @org.junit.Test(timeout = 4000)
    public void testGetWallStatValue_defaultInvocation() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource();
        dataSource.getWallStatValue(true);
        org.junit.Assert.assertTrue(dataSource.isLogDifferentThread());
        org.junit.Assert.assertTrue(dataSource.isResetStatEnable());
        org.junit.Assert.assertTrue(dataSource.isEnable());
    }

    @org.junit.Test(timeout = 4000)
    public void testGetActivePeakTime_initiallyNull() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource();
        java.util.Date activePeakTime = dataSource.getActivePeakTime();
        org.junit.Assert.assertTrue(dataSource.isEnable());
        org.junit.Assert.assertTrue(dataSource.isResetStatEnable());
        org.junit.Assert.assertTrue(dataSource.isLogDifferentThread());
        org.junit.Assert.assertNull(activePeakTime);
    }

    @org.junit.Test(timeout = 4000)
    public void testShrink_withFlags_noException() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource();
        dataSource.shrink(true, true);
        org.junit.Assert.assertTrue(dataSource.isResetStatEnable());
        org.junit.Assert.assertTrue(dataSource.isEnable());
    }

    @org.junit.Test(timeout = 4000)
    public void testGetFilterClassNames_defaultNotNull() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource(false);
        java.util.List<java.lang.String> filterClassNames = dataSource.getFilterClassNames();
        org.junit.Assert.assertTrue(dataSource.isEnable());
        org.junit.Assert.assertNotNull(filterClassNames);
        org.junit.Assert.assertTrue(dataSource.isResetStatEnable());
        org.junit.Assert.assertTrue(dataSource.isLogDifferentThread());
    }

    @org.junit.Test(timeout = 4000)
    public void testGetPoolingPeakTime_initiallyNull() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource();
        java.util.Date poolingPeakTime = dataSource.getPoolingPeakTime();
        org.junit.Assert.assertNull(poolingPeakTime);
    }

    @org.junit.Test(timeout = 4000)
    public void testClose_doesNotDisableDataSource() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource();
        dataSource.close();
        org.junit.Assert.assertTrue(dataSource.isEnable());
    }

    @org.junit.Test(timeout = 4000)
    public void testGetConnection_withTimeoutZero() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource();
        dataSource.getConnection(((long) (0)));
        org.junit.Assert.assertTrue(dataSource.isEnable());
        org.junit.Assert.assertTrue(dataSource.isResetStatEnable());
    }

    @org.junit.Test(timeout = 4000)
    public void testInit_withoutUrl_throwsSQLException() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource(false);
        try {
            dataSource.init();
            org.junit.Assert.fail("Expecting exception: SQLException");
        } catch (java.sql.SQLException e) {
            // 
            // url not set
            // 
            org.evosuite.runtime.EvoAssertions.verifyException("com.alibaba.druid.pool.DruidDataSource", e);
        }
    }

    @org.junit.Test(timeout = 4000)
    public void testResetStat_incrementsResetCount() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource(true);
        dataSource.resetStat();
        org.junit.Assert.assertEquals(1L, dataSource.getResetCount());
    }

    @org.junit.Test(timeout = 4000)
    public void testGetErrorCount_startsAtZero() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource(false);
        long errorCount = dataSource.getErrorCount();
        org.junit.Assert.assertTrue(dataSource.isEnable());
        org.junit.Assert.assertTrue(dataSource.isLogDifferentThread());
        org.junit.Assert.assertEquals(0L, errorCount);
        org.junit.Assert.assertTrue(dataSource.isResetStatEnable());
    }

    @org.junit.Test(timeout = 4000)
    public void testGetPoolingCount_startsAtZero() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource();
        int poolingCount = dataSource.getPoolingCount();
        org.junit.Assert.assertTrue(dataSource.isLogDifferentThread());
        org.junit.Assert.assertTrue(dataSource.isEnable());
        org.junit.Assert.assertEquals(0, poolingCount);
    }

    @org.junit.Test(timeout = 4000)
    public void testGetConnection_defaultInvocation() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource();
        dataSource.getConnection();
        org.junit.Assert.assertTrue(dataSource.isEnable());
        org.junit.Assert.assertTrue(dataSource.isResetStatEnable());
    }

    @org.junit.Test(timeout = 4000)
    public void testIsResetStatEnable_defaultTrue() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource();
        boolean resetStatEnabled = dataSource.isResetStatEnable();
        org.junit.Assert.assertTrue(dataSource.isEnable());
        org.junit.Assert.assertTrue(resetStatEnabled);
        org.junit.Assert.assertTrue(dataSource.isLogDifferentThread());
    }

    @org.junit.Test(timeout = 4000)
    public void testGetConnectErrorCount_startsAtZero() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource();
        long connectErrorCount = dataSource.getConnectErrorCount();
        org.junit.Assert.assertEquals(0L, connectErrorCount);
        org.junit.Assert.assertTrue(dataSource.isEnable());
        org.junit.Assert.assertTrue(dataSource.isResetStatEnable());
        org.junit.Assert.assertTrue(dataSource.isLogDifferentThread());
    }

    @org.junit.Test(timeout = 4000)
    public void testPreDeregister_noExceptions() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource();
        dataSource.preDeregister();
        org.junit.Assert.assertTrue(dataSource.isResetStatEnable());
        org.junit.Assert.assertTrue(dataSource.isEnable());
        org.junit.Assert.assertTrue(dataSource.isLogDifferentThread());
    }

    @org.junit.Test(timeout = 4000)
    public void testTryGetConnection_defaultInvocation() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource(true);
        dataSource.tryGetConnection();
        org.junit.Assert.assertTrue(dataSource.isLogDifferentThread());
        org.junit.Assert.assertTrue(dataSource.isEnable());
    }

    @org.junit.Test(timeout = 4000)
    public void testUnwrap_withScheduledThreadPoolExecutorClass() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource(true);
        java.lang.Class<java.util.concurrent.ScheduledThreadPoolExecutor> targetType = java.util.concurrent.ScheduledThreadPoolExecutor.class;
        dataSource.unwrap(targetType);
        org.junit.Assert.assertTrue(dataSource.isResetStatEnable());
        org.junit.Assert.assertTrue(dataSource.isLogDifferentThread());
        org.junit.Assert.assertTrue(dataSource.isEnable());
    }

    @org.junit.Test(timeout = 4000)
    public void testIsWrapperFor_returnsFalseForUnrelatedType() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource();
        java.lang.Class<java.util.concurrent.ScheduledThreadPoolExecutor> targetType = java.util.concurrent.ScheduledThreadPoolExecutor.class;
        boolean isWrapper = dataSource.isWrapperFor(targetType);
        org.junit.Assert.assertTrue(dataSource.isEnable());
        org.junit.Assert.assertTrue(dataSource.isResetStatEnable());
        org.junit.Assert.assertFalse(isWrapper);
        org.junit.Assert.assertTrue(dataSource.isLogDifferentThread());
    }

    @org.junit.Test(timeout = 4000)
    public void testGetWallStatMap_defaultInvocation() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource(false);
        dataSource.getWallStatMap();
        org.junit.Assert.assertTrue(dataSource.isLogDifferentThread());
        org.junit.Assert.assertTrue(dataSource.isResetStatEnable());
        org.junit.Assert.assertTrue(dataSource.isEnable());
    }

    @org.junit.Test(timeout = 4000)
    public void testGetPoolingConnectionInfo_defaultInvocation() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource();
        dataSource.getPoolingConnectionInfo();
        org.junit.Assert.assertTrue(dataSource.isEnable());
        org.junit.Assert.assertTrue(dataSource.isResetStatEnable());
    }

    @org.junit.Test(timeout = 4000)
    public void testRemoveAbandoned_returnsZeroByDefault() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource(false);
        int removedCount = dataSource.removeAbandoned();
        org.junit.Assert.assertTrue(dataSource.isEnable());
        org.junit.Assert.assertTrue(dataSource.isResetStatEnable());
        org.junit.Assert.assertEquals(0, removedCount);
    }

    @org.junit.Test(timeout = 4000)
    public void testDestroyTask_run_noExceptions() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource();
        com.alibaba.druid.pool.DruidDataSource.DestroyTask destroyTask = dataSource.new com.alibaba.druid.pool.DestroyTask();
        destroyTask.run();
        org.junit.Assert.assertTrue(dataSource.isResetStatEnable());
        org.junit.Assert.assertTrue(dataSource.isLogDifferentThread());
        org.junit.Assert.assertTrue(dataSource.isEnable());
    }

    @org.junit.Test(timeout = 4000)
    public void testDestroyConnectionThread_run_throwsNPE() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource(true);
        com.alibaba.druid.pool.DruidDataSource.DestroyConnectionThread destroyConnectionThread = dataSource.new com.alibaba.druid.pool.DestroyConnectionThread("ZdIS^gE2H");
        // Undeclared exception!
        try {
            destroyConnectionThread.run();
            org.junit.Assert.fail("Expecting exception: NullPointerException");
        } catch (java.lang.NullPointerException e) {
            // 
            // Cannot invoke \"com.alibaba.druid.pool.DruidDataSource$DestroyTask.run()\" because the return value of \"com.alibaba.druid.pool.DruidDataSource.access$1300(com.alibaba.druid.pool.DruidDataSource)\" is null
            // 
            org.evosuite.runtime.EvoAssertions.verifyException("com.alibaba.druid.pool.DruidDataSource$DestroyConnectionThread", e);
        }
    }

    @org.junit.Test(timeout = 4000)
    public void testGetStatValueAndReset_withoutStat_throwsNPE() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource();
        // Undeclared exception!
        try {
            dataSource.getStatValueAndReset();
            org.junit.Assert.fail("Expecting exception: NullPointerException");
        } catch (java.lang.NullPointerException e) {
            // 
            // Cannot invoke \"com.alibaba.druid.stat.JdbcDataSourceStat.getConnectionHoldHistogram()\" because the return value of \"com.alibaba.druid.pool.DruidDataSource.getDataSourceStat()\" is null
            // 
            org.evosuite.runtime.EvoAssertions.verifyException("com.alibaba.druid.pool.DruidDataSource", e);
        }
    }

    @org.junit.Test(timeout = 4000)
    public void testInitCheck_defaultInvocation() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource(true);
        dataSource.initCheck();
        org.junit.Assert.assertTrue(dataSource.isResetStatEnable());
        org.junit.Assert.assertTrue(dataSource.isEnable());
    }

    @org.junit.Test(timeout = 4000)
    public void testFill_withCapacity_throwsSQLException() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource();
        try {
            dataSource.fill(72);
            org.junit.Assert.fail("Expecting exception: SQLException");
        } catch (java.sql.SQLException e) {
            // 
            // url not set
            // 
            org.evosuite.runtime.EvoAssertions.verifyException("com.alibaba.druid.pool.DruidDataSource", e);
        }
    }

    @org.junit.Test(timeout = 4000)
    public void testSetConnectProperties_roundTrip() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource(true);
        java.util.Properties connectProperties = dataSource.getConnectProperties();
        dataSource.setConnectProperties(connectProperties);
        org.junit.Assert.assertTrue(dataSource.isLogDifferentThread());
        org.junit.Assert.assertTrue(dataSource.isEnable());
        org.junit.Assert.assertTrue(dataSource.isResetStatEnable());
    }

    @org.junit.Test(timeout = 4000)
    public void testSetEnable_true_updatesFlags() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource();
        dataSource.setEnable(true);
        org.junit.Assert.assertTrue(dataSource.isResetStatEnable());
        org.junit.Assert.assertEquals(0L, dataSource.getNotEmptySignalCount());
        org.junit.Assert.assertTrue(dataSource.isEnable());
    }

    @org.junit.Test(timeout = 4000)
    public void testCreateConnectionThread_constructor_noExceptions() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource();
        com.alibaba.druid.pool.DruidDataSource.CreateConnectionThread createConnectionThread = dataSource.new com.alibaba.druid.pool.CreateConnectionThread("PoolingPeak");
    }

    @org.junit.Test(timeout = 4000)
    public void testCreateConnectionTask_run_noExceptions() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource(false);
        com.alibaba.druid.pool.DruidDataSource.CreateConnectionTask createConnectionTask = dataSource.new com.alibaba.druid.pool.CreateConnectionTask();
        createConnectionTask.run();
        org.junit.Assert.assertTrue(dataSource.isEnable());
        org.junit.Assert.assertTrue(dataSource.isLogDifferentThread());
        org.junit.Assert.assertTrue(dataSource.isResetStatEnable());
    }

    @org.junit.Test(timeout = 4000)
    public void testCreateConnectionTask_constructorWithCreateError() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource();
        com.alibaba.druid.pool.DruidDataSource.CreateConnectionTask createConnectionTask = dataSource.new com.alibaba.druid.pool.CreateConnectionTask(true);
        org.junit.Assert.assertTrue(dataSource.isLogDifferentThread());
        org.junit.Assert.assertTrue(dataSource.isEnable());
        org.junit.Assert.assertTrue(dataSource.isResetStatEnable());
    }

    @org.junit.Test(timeout = 4000)
    public void testSetCheckExecuteTime_toFalse() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource();
        dataSource.setCheckExecuteTime(false);
        org.junit.Assert.assertTrue(dataSource.isResetStatEnable());
        org.junit.Assert.assertTrue(dataSource.isLogDifferentThread());
        org.junit.Assert.assertFalse(dataSource.isCheckExecuteTime());
        org.junit.Assert.assertTrue(dataSource.isEnable());
    }

    @org.junit.Test(timeout = 4000)
    public void testGetSqlStatMap_withoutStat_throwsNPE() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource(true);
        // Undeclared exception!
        try {
            dataSource.getSqlStatMap();
            org.junit.Assert.fail("Expecting exception: NullPointerException");
        } catch (java.lang.NullPointerException e) {
            // 
            // Cannot invoke \"com.alibaba.druid.stat.JdbcDataSourceStat.getSqlStatMap()\" because the return value of \"com.alibaba.druid.pool.DruidDataSource.getDataSourceStat()\" is null
            // 
            org.evosuite.runtime.EvoAssertions.verifyException("com.alibaba.druid.pool.DruidDataSource", e);
        }
    }

    @org.junit.Test(timeout = 4000)
    public void testGetPooledConnection_withCredentials_throwsUnsupported() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource();
        // Undeclared exception!
        try {
            dataSource.getPooledConnection("", "keepAliveErr");
            org.junit.Assert.fail("Expecting exception: UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException e) {
            // 
            // Not supported by DruidDataSource
            // 
            org.evosuite.runtime.EvoAssertions.verifyException("com.alibaba.druid.pool.DruidDataSource", e);
        }
    }

    @org.junit.Test(timeout = 4000)
    public void testGetNotEmptySignalCount_startsAtZero() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource(true);
        long notEmptySignalCount = dataSource.getNotEmptySignalCount();
        org.junit.Assert.assertEquals(0L, notEmptySignalCount);
        org.junit.Assert.assertTrue(dataSource.isEnable());
        org.junit.Assert.assertTrue(dataSource.isLogDifferentThread());
    }

    @org.junit.Test(timeout = 4000)
    public void testIsUseGlobalDataSourceStat_defaultFalse() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource();
        boolean useGlobalStat = dataSource.isUseGlobalDataSourceStat();
        org.junit.Assert.assertTrue(dataSource.isEnable());
        org.junit.Assert.assertFalse(useGlobalStat);
        org.junit.Assert.assertTrue(dataSource.isResetStatEnable());
        org.junit.Assert.assertTrue(dataSource.isLogDifferentThread());
    }

    @org.junit.Test(timeout = 4000)
    public void testGetSqlStat_byIndex_withoutStat_throwsNPE() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource();
        // Undeclared exception!
        try {
            dataSource.getSqlStat(0);
            org.junit.Assert.fail("Expecting exception: NullPointerException");
        } catch (java.lang.NullPointerException e) {
            // 
            // Cannot invoke \"com.alibaba.druid.stat.JdbcDataSourceStat.getSqlStat(int)\" because the return value of \"com.alibaba.druid.pool.DruidDataSource.getDataSourceStat()\" is null
            // 
            org.evosuite.runtime.EvoAssertions.verifyException("com.alibaba.druid.pool.DruidDataSource", e);
        }
    }

    @org.junit.Test(timeout = 4000)
    public void testGetPoolingPeak_startsAtZero() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource();
        int poolingPeak = dataSource.getPoolingPeak();
        org.junit.Assert.assertTrue(dataSource.isLogDifferentThread());
        org.junit.Assert.assertTrue(dataSource.isResetStatEnable());
        org.junit.Assert.assertEquals(0, poolingPeak);
        org.junit.Assert.assertTrue(dataSource.isEnable());
    }

    @org.junit.Test(timeout = 4000)
    public void testIsLoadSpifilterSkip_defaultFalse() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource();
        boolean skip = dataSource.isLoadSpifilterSkip();
        org.junit.Assert.assertTrue(dataSource.isResetStatEnable());
        org.junit.Assert.assertFalse(skip);
        org.junit.Assert.assertTrue(dataSource.isEnable());
        org.junit.Assert.assertTrue(dataSource.isLogDifferentThread());
    }

    @org.junit.Test(timeout = 4000)
    public void testGetConnection_withCredentials_setsPassword() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource(true);
        dataSource.getConnection("zL|z", "QueryTimeout");
        org.junit.Assert.assertEquals("QueryTimeout", dataSource.getPassword());
    }

    @org.junit.Test(timeout = 4000)
    public void testFill_withoutCapacity_throwsSQLException() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource();
        dataSource.setLoadSpifilterSkip(true);
        try {
            dataSource.fill();
            org.junit.Assert.fail("Expecting exception: SQLException");
        } catch (java.sql.SQLException e) {
            // 
            // url not set
            // 
            org.evosuite.runtime.EvoAssertions.verifyException("com.alibaba.druid.pool.DruidDataSource", e);
        }
    }

    @org.junit.Test(timeout = 4000)
    public void testSetLogDifferentThread_toggleFalseThenAssert() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource();
        org.junit.Assert.assertTrue(dataSource.isLogDifferentThread());
        dataSource.setLogDifferentThread(false);
        org.junit.Assert.assertFalse(dataSource.isLogDifferentThread());
    }

    @org.junit.Test(timeout = 4000)
    public void testShrink_withoutArgs_noExceptions() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource();
        dataSource.shrink();
        org.junit.Assert.assertTrue(dataSource.isEnable());
        org.junit.Assert.assertTrue(dataSource.isLogDifferentThread());
        org.junit.Assert.assertTrue(dataSource.isResetStatEnable());
    }

    @org.junit.Test(timeout = 4000)
    public void testGetRecycleErrorCount_startsAtZero() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource();
        long recycleErrorCount = dataSource.getRecycleErrorCount();
        org.junit.Assert.assertTrue(dataSource.isResetStatEnable());
        org.junit.Assert.assertEquals(0L, recycleErrorCount);
    }

    @org.junit.Test(timeout = 4000)
    public void testRegisterMbean_marksRegistered() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource();
        dataSource.registerMbean();
        org.junit.Assert.assertTrue(dataSource.isMbeanRegistered());
    }

    @org.junit.Test(timeout = 4000)
    public void testSetKeepAlive_true() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource();
        dataSource.setKeepAlive(true);
        org.junit.Assert.assertTrue(dataSource.isKeepAlive());
    }

    @org.junit.Test(timeout = 4000)
    public void testGetLockQueueLength_defaultInvocation() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource();
        dataSource.getLockQueueLength();
        org.junit.Assert.assertTrue(dataSource.isEnable());
        org.junit.Assert.assertTrue(dataSource.isResetStatEnable());
        org.junit.Assert.assertTrue(dataSource.isLogDifferentThread());
    }

    @org.junit.Test(timeout = 4000)
    public void testPostDeregister_noExceptions() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource();
        dataSource.postDeregister();
        org.junit.Assert.assertTrue(dataSource.isResetStatEnable());
        org.junit.Assert.assertTrue(dataSource.isLogDifferentThread());
        org.junit.Assert.assertTrue(dataSource.isEnable());
    }

    @org.junit.Test(timeout = 4000)
    public void testGetRemoveAbandonedCount_startsAtZero() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource();
        long removeAbandonedCount = dataSource.getRemoveAbandonedCount();
        org.junit.Assert.assertTrue(dataSource.isEnable());
        org.junit.Assert.assertTrue(dataSource.isResetStatEnable());
        org.junit.Assert.assertEquals(0L, removeAbandonedCount);
        org.junit.Assert.assertTrue(dataSource.isLogDifferentThread());
    }

    @org.junit.Test(timeout = 4000)
    public void testGetVersion_defaultInvocation() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource(true);
        dataSource.getVersion();
        org.junit.Assert.assertTrue(dataSource.isEnable());
        org.junit.Assert.assertTrue(dataSource.isLogDifferentThread());
        org.junit.Assert.assertTrue(dataSource.isResetStatEnable());
    }

    @org.junit.Test(timeout = 4000)
    public void testGetReference_defaultInvocation() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource();
        dataSource.getReference();
        org.junit.Assert.assertTrue(dataSource.isResetStatEnable());
        org.junit.Assert.assertTrue(dataSource.isLogDifferentThread());
        org.junit.Assert.assertTrue(dataSource.isEnable());
    }

    @org.junit.Test(timeout = 4000)
    public void testGetInitStackTrace_defaultInvocation() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource();
        dataSource.getInitStackTrace();
        org.junit.Assert.assertTrue(dataSource.isEnable());
        org.junit.Assert.assertTrue(dataSource.isResetStatEnable());
        org.junit.Assert.assertTrue(dataSource.isLogDifferentThread());
    }

    @org.junit.Test(timeout = 4000)
    public void testDump_defaultFormat() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource();
        java.lang.String dump = dataSource.dump();
        org.junit.Assert.assertEquals("{\n\tCreateTime:\"2014-02-14 20:21:21\",\n\tActiveCount:0,\n\tPoolingCount:0,\n\tCreateCount:0,\n\tDestroyCount:0,\n\tCloseCount:0,\n\tConnectCount:0,\n\tConnections:[\n\t]\n}", dump);
        org.junit.Assert.assertTrue(dataSource.isEnable());
    }

    @org.junit.Test(timeout = 4000)
    public void testGetSqlStat_byId_withoutStat_throwsNPE() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource(true);
        // Undeclared exception!
        try {
            dataSource.getSqlStat(0L);
            org.junit.Assert.fail("Expecting exception: NullPointerException");
        } catch (java.lang.NullPointerException e) {
            // 
            // Cannot invoke \"com.alibaba.druid.stat.JdbcDataSourceStat.getSqlStat(long)\" because the return value of \"com.alibaba.druid.pool.DruidDataSource.getDataSourceStat()\" is null
            // 
            org.evosuite.runtime.EvoAssertions.verifyException("com.alibaba.druid.pool.DruidDataSource", e);
        }
    }

    @org.junit.Test(timeout = 4000)
    public void testClone_returnsDistinctInstance() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource(true);
        com.alibaba.druid.pool.DruidDataSource cloned = ((com.alibaba.druid.pool.DruidDataSource) (dataSource.clone()));
        org.junit.Assert.assertTrue(cloned.isEnable());
        org.junit.Assert.assertTrue(cloned.isLogDifferentThread());
        org.junit.Assert.assertTrue(cloned.isResetStatEnable());
        org.junit.Assert.assertNotSame(cloned, dataSource);
    }

    @org.junit.Test(timeout = 4000)
    public void testGetNotEmptyWaitThreadPeak_startsAtZero() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource(true);
        int notEmptyWaitThreadPeak = dataSource.getNotEmptyWaitThreadPeak();
        org.junit.Assert.assertTrue(dataSource.isEnable());
        org.junit.Assert.assertTrue(dataSource.isResetStatEnable());
        org.junit.Assert.assertEquals(0, notEmptyWaitThreadPeak);
        org.junit.Assert.assertTrue(dataSource.isLogDifferentThread());
    }

    @org.junit.Test(timeout = 4000)
    public void testSetPoolPreparedStatements_true() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource();
        dataSource.setPoolPreparedStatements(true);
        org.junit.Assert.assertTrue(dataSource.isPoolPreparedStatements());
    }

    @org.junit.Test(timeout = 4000)
    public void testSetAsyncInit_true() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource();
        dataSource.setAsyncInit(true);
        org.junit.Assert.assertTrue(dataSource.isAsyncInit());
    }

    @org.junit.Test(timeout = 4000)
    public void testGetStatData_withoutStat_throwsNPE() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource(false);
        // Undeclared exception!
        try {
            dataSource.getStatData();
            org.junit.Assert.fail("Expecting exception: NullPointerException");
        } catch (java.lang.NullPointerException e) {
            // 
            // Cannot invoke \"com.alibaba.druid.stat.JdbcDataSourceStat.getConnectionHoldHistogram()\" because the return value of \"com.alibaba.druid.pool.DruidDataSource.getDataSourceStat()\" is null
            // 
            org.evosuite.runtime.EvoAssertions.verifyException("com.alibaba.druid.pool.DruidDataSource", e);
        }
    }

    @org.junit.Test(timeout = 4000)
    public void testRestart_incrementsResetCount() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource(false);
        dataSource.restart();
        org.junit.Assert.assertEquals(1L, dataSource.getResetCount());
    }

    @org.junit.Test(timeout = 4000)
    public void testGetCompositeData_withoutStat_throwsNPE() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource();
        // Undeclared exception!
        try {
            dataSource.getCompositeData();
            org.junit.Assert.fail("Expecting exception: NullPointerException");
        } catch (java.lang.NullPointerException e) {
            // 
            // Cannot invoke \"com.alibaba.druid.stat.JdbcDataSourceStat.getConnectionStat()\" because \"stat\" is null
            // 
            org.evosuite.runtime.EvoAssertions.verifyException("com.alibaba.druid.pool.DruidAbstractDataSource", e);
        }
    }

    @org.junit.Test(timeout = 4000)
    public void testCreateAndStartDestroyThread_noExceptions() throws java.lang.Throwable {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource();
        dataSource.createAndStartDestroyThread();
        org.junit.Assert.assertTrue(dataSource.isLogDifferentThread());
        org.junit.Assert.assertTrue(dataSource.isResetStatEnable());
        org.junit.Assert.assertTrue(dataSource.isEnable());
    }

    @org.junit.Test(timeout = 4000)
    public void testSanitizedUrl_masksPasswordsAndSensitiveParams() throws java.lang.Throwable {
        java.lang.String url = "jdbc:mysql://127.0.0.1:3306/druid?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowMultiQueries=true";
        java.lang.String expectedUrl = url;
        java.lang.String urlNew = com.alibaba.druid.pool.DruidDataSource.sanitizedUrl(url);
        org.junit.Assert.assertEquals(expectedUrl, urlNew);
        url = "jdbc:mysql://127.0.0.1:3306/druid?useUnicode=true&user=root&password";
        expectedUrl = url;
        urlNew = com.alibaba.druid.pool.DruidDataSource.sanitizedUrl(url);
        org.junit.Assert.assertEquals(expectedUrl, urlNew);
        url = "jdbc:mysql://127.0.0.1:3306/druid?useUnicode=true&user=root&password=";
        expectedUrl = "jdbc:mysql://127.0.0.1:3306/druid?useUnicode=true&user=root&password=<masked>";
        urlNew = com.alibaba.druid.pool.DruidDataSource.sanitizedUrl(url);
        org.junit.Assert.assertEquals(expectedUrl, urlNew);
        url = "jdbc:mysql://127.0.0.1:3306/druid?useUnicode=true&user=root&password=12345678";
        expectedUrl = "jdbc:mysql://127.0.0.1:3306/druid?useUnicode=true&user=root&password=<masked>";
        urlNew = com.alibaba.druid.pool.DruidDataSource.sanitizedUrl(url);
        org.junit.Assert.assertEquals(expectedUrl, urlNew);
        url = "jdbc:mysql://127.0.0.1:3306/druid?useUnicode=true&user=root&password=12345678&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowMultiQueries=true";
        expectedUrl = "jdbc:mysql://127.0.0.1:3306/druid?useUnicode=true&user=root&password=<masked>&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowMultiQueries=true";
        urlNew = com.alibaba.druid.pool.DruidDataSource.sanitizedUrl(url);
        org.junit.Assert.assertEquals(expectedUrl, urlNew);
        url = "jdbc:mysql://127.0.0.1:3306/druid?useUnicode=true&user=root&password1=12345678&password=12345678&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowMultiQueries=true";
        expectedUrl = "jdbc:mysql://127.0.0.1:3306/druid?useUnicode=true&user=root&password1=<masked>&password=<masked>&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowMultiQueries=true";
        urlNew = com.alibaba.druid.pool.DruidDataSource.sanitizedUrl(url);
        org.junit.Assert.assertEquals(expectedUrl, urlNew);
        url = "jdbc:mysql://127.0.0.1:3306/druid?useUnicode=true&user=root&password2=12345678&password1=12345678&password=12345678&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowMultiQueries=true";
        expectedUrl = "jdbc:mysql://127.0.0.1:3306/druid?useUnicode=true&user=root&password2=<masked>&password1=<masked>&password=<masked>&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowMultiQueries=true";
        urlNew = com.alibaba.druid.pool.DruidDataSource.sanitizedUrl(url);
        org.junit.Assert.assertEquals(expectedUrl, urlNew);
        url = "jdbc:mysql://127.0.0.1:3306/druid?useUnicode=true&user=root&password3=12345678&password2=12345678&password1=12345678&password=12345678&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowMultiQueries=true";
        expectedUrl = "jdbc:mysql://127.0.0.1:3306/druid?useUnicode=true&user=root&password3=<masked>&password2=<masked>&password1=<masked>&password=<masked>&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowMultiQueries=true";
        urlNew = com.alibaba.druid.pool.DruidDataSource.sanitizedUrl(url);
        org.junit.Assert.assertEquals(expectedUrl, urlNew);
        url = "jdbc:mysql://127.0.0.1:3306/druid?clientCertificateKeyStorePassword=12345678&useUnicode=true&user=root&password3=12345678&password2=12345678&password1=12345678&password=12345678&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowMultiQueries=true";
        expectedUrl = "jdbc:mysql://127.0.0.1:3306/druid?clientCertificateKeyStorePassword=<masked>&useUnicode=true&user=root&password3=<masked>&password2=<masked>&password1=<masked>&password=<masked>&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowMultiQueries=true";
        urlNew = com.alibaba.druid.pool.DruidDataSource.sanitizedUrl(url);
        org.junit.Assert.assertEquals(expectedUrl, urlNew);
        url = "jdbc:mysql://127.0.0.1:3306/druid?trustCertificateKeyStorePassword=12345678&useUnicode=true&user=root&password3=12345678&password2=12345678&password1=12345678&password=12345678&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowMultiQueries=true";
        expectedUrl = "jdbc:mysql://127.0.0.1:3306/druid?trustCertificateKeyStorePassword=<masked>&useUnicode=true&user=root&password3=<masked>&password2=<masked>&password1=<masked>&password=<masked>&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowMultiQueries=true";
        urlNew = com.alibaba.druid.pool.DruidDataSource.sanitizedUrl(url);
        org.junit.Assert.assertEquals(expectedUrl, urlNew);
    }
}