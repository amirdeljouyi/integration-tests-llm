package com.alibaba.druid.pool;
import com.alibaba.druid.pool.DruidDataSource_ESTest_scaffolding;
import java.sql.SQLWarning;
import org.junit.Test;
import static org.evosuite.runtime.EvoAssertions.verifyException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
public class DruidDataSource_ESTest_Top5 extends DruidDataSource_ESTest_scaffolding {
    @Test(timeout = 4000)
    public void testHandleConnectionExceptionThrowsNullPointerException() throws Throwable {
        DruidDataSource druidDataSource0 = new DruidDataSource();
        SQLWarning sQLWarning0 = new SQLWarning("tSVE<.", "tSVE<.", 1433);
        // Undeclared exception!
        try {
            druidDataSource0.handleConnectionException(((DruidPooledConnection) (null)), ((Throwable) (sQLWarning0)), "tSVE<.");
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            // 
            // Cannot invoke \"com.alibaba.druid.pool.DruidPooledConnection.getConnectionHolder()\" because \"pooledConnection\" is null
            // 
            verifyException("com.alibaba.druid.pool.DruidDataSource", e);
        }
    }

    @Test(timeout = 4000)
    public void testGetSqlStatMapThrowsNullPointerException() throws Throwable {
        DruidDataSource druidDataSource0 = new DruidDataSource(true);
        // Undeclared exception!
        try {
            druidDataSource0.getSqlStatMap();
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            // 
            // Cannot invoke \"com.alibaba.druid.stat.JdbcDataSourceStat.getSqlStatMap()\" because the return value of \"com.alibaba.druid.pool.DruidDataSource.getDataSourceStat()\" is null
            // 
            verifyException("com.alibaba.druid.pool.DruidDataSource", e);
        }
    }

    @Test(timeout = 4000)
    public void testGetPooledConnectionThrowsUnsupportedOperationException() throws Throwable {
        DruidDataSource druidDataSource0 = new DruidDataSource();
        // Undeclared exception!
        try {
            druidDataSource0.getPooledConnection("", "keepAliveErr");
            fail("Expecting exception: UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // 
            // Not supported by DruidDataSource
            // 
            verifyException("com.alibaba.druid.pool.DruidDataSource", e);
        }
    }

    @Test(timeout = 4000)
    public void testGetNotEmptySignalCount() throws Throwable {
        DruidDataSource druidDataSource0 = new DruidDataSource(true);
        long long0 = druidDataSource0.getNotEmptySignalCount();
        assertEquals(0L, long0);
        assertTrue(druidDataSource0.isEnable());
        assertTrue(druidDataSource0.isLogDifferentThread());
    }

    @Test(timeout = 4000)
    public void testGetSqlStatTakingLongThrowsNullPointerException() throws Throwable {
        DruidDataSource druidDataSource0 = new DruidDataSource(true);
        // Undeclared exception!
        try {
            druidDataSource0.getSqlStat(0L);
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            // 
            // Cannot invoke \"com.alibaba.druid.stat.JdbcDataSourceStat.getSqlStat(long)\" because the return value of \"com.alibaba.druid.pool.DruidDataSource.getDataSourceStat()\" is null
            // 
            verifyException("com.alibaba.druid.pool.DruidDataSource", e);
        }
    }
}
