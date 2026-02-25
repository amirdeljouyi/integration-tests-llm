package com.alibaba.druid.pool;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import org.junit.Test;
import static org.evosuite.runtime.EvoAssertions.verifyException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Corresponding manual test: {@link com.alibaba.druid.pool.DruidDataSourceTest}.
 * Manual test source on GitHub: <a href="https://github.com/alibaba/druid/blob/3246166f8b623969c6561eeab1c864f43829399b/core/src/test/java/com/alibaba/druid/pool/DruidDataSourceTest.java">DruidDataSourceTest</a>.
 * @see com.alibaba.druid.pool.DruidDataSourceTest
 */
public class DruidDataSource_ESTest_Adopted_Top5 {

    /**
     * This test added target-class coverage 3.84% for com.alibaba.druid.pool.DruidDataSource (74/1925 lines).
     * Delta details: +44 methods, +84 branches, +1807 instructions.
     * Full version of the covered block is here: <a href="https://github.com/alibaba/druid/blob/3246166f8b623969c6561eeab1c864f43829399b/core/src/main/java/com/alibaba/druid/pool/DruidDataSource.java#L839-L841">DruidDataSource.java (lines 839-841)</a>
     * Covered Lines:
     * <pre><code>
     *         } catch (SQLException e) {
     *             LOG.error("{dataSource-" + this.getID() + "} init error", e);
     *             throw e;
     * </code></pre>
     * Other newly covered ranges to check: 122;128;131;133;140;146;172-173;176;178-179;196-197;204;206;208;210;213;215;660;665;667;669;672;674;676;680;682-683;691;695;697;701-702;705-706;718;722;726;730;734;738;742;746;852-853;855;872;886-887;889;893-894;896;899;1015;1019-1021;1023;1029;1032;1038;1116-1118;1121;1123;1129;1136-1137
     */
    @Test(timeout = 4000)
    public void testFill_withCapacity_throwsSQLException() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource();
        try {
            dataSource.fill(72);
            fail("Expecting exception: SQLException");
        } catch (SQLException e) {
            // 
            // url not set
            // 
            verifyException("com.alibaba.druid.pool.DruidDataSource", e);
        }
    }

    /**
     * This test added target-class coverage 3.64% for com.alibaba.druid.pool.DruidDataSource (70/1925 lines).
     * Delta details: +45 methods, +81 branches, +1788 instructions.
     * Full version of the covered block is here: <a href="https://github.com/alibaba/druid/blob/3246166f8b623969c6561eeab1c864f43829399b/core/src/main/java/com/alibaba/druid/pool/DruidDataSource.java#L839-L841">DruidDataSource.java (lines 839-841)</a>
     * Covered Lines:
     * <pre><code>
     *         } catch (SQLException e) {
     *             LOG.error("{dataSource-" + this.getID() + "} init error", e);
     *             throw e;
     * </code></pre>
     * Other newly covered ranges to check: 122;128;131;133;140;146;172-173;176;178-179;196-197;204;206;208;210;213;215;660;665;667;669;672;674;676;680;682-683;691;695;697;701-702;705-706;718;722;726;730;734;738;742;746;852-853;855;872;886-887;889;893-894;896;899;1015-1016;1116-1118;1121;1123;1129;1136-1137;3979-3980
     */
    @Test(timeout = 4000)
    public void testFill_withoutCapacity_throwsSQLException() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setLoadSpifilterSkip(true);
        try {
            dataSource.fill();
            fail("Expecting exception: SQLException");
        } catch (SQLException e) {
            // 
            // url not set
            // 
            verifyException("com.alibaba.druid.pool.DruidDataSource", e);
        }
    }

    /**
     * This test added target-class coverage 4.10% for com.alibaba.druid.pool.DruidDataSource (79/1925 lines).
     * Delta details: +82 methods, +35 branches, +1335 instructions.
     * Full version of the covered block is here: <a href="https://github.com/alibaba/druid/blob/3246166f8b623969c6561eeab1c864f43829399b/core/src/main/java/com/alibaba/druid/pool/DruidDataSource.java#L2419-L2425">DruidDataSource.java (lines 2419-2425)</a>
     * Covered Lines:
     * <pre><code>
     *             this.poolingPeak = 0;
     *             this.poolingPeakTime = 0;
     *             this.activePeak = 0;
     *             this.activePeakTime = 0;
     *             this.connectCount = 0;
     *             this.closeCount = 0;
     *             this.keepAliveCheckCount = 0;
     * </code></pre>
     * Other newly covered ranges to check: 122;128;131;133;140;146;172-173;176;178-179;196-197;204;206;208;210;213;215;2399;2401;2403-2405;2407-2409;2411-2416;2427-2428;2430;2433-2435;2437-2439;2441-2443;2445-2449;2451-2453;2455;2457;2460;2462;2464-2466;2468-2471;2473-2474;2476-2477;3028-3029;3032;3505
     */
    @Test(timeout = 4000)
    public void testGetStatValueAndReset_withoutStat_throwsNPE() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource();
        // Undeclared exception!
        try {
            dataSource.getStatValueAndReset();
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            // 
            // Cannot invoke \"com.alibaba.druid.stat.JdbcDataSourceStat.getConnectionHoldHistogram()\" because the return value of \"com.alibaba.druid.pool.DruidDataSource.getDataSourceStat()\" is null
            // 
            verifyException("com.alibaba.druid.pool.DruidDataSource", e);
        }
    }

    /**
     * This test added target-class coverage 5.14% for com.alibaba.druid.pool.DruidDataSource (99/1925 lines).
     * Delta details: +61 methods, +36 branches, +1347 instructions.
     * Full version of the covered block is here: <a href="https://github.com/alibaba/druid/blob/3246166f8b623969c6561eeab1c864f43829399b/core/src/main/java/com/alibaba/druid/pool/DruidDataSource.java#L3674-L3680">DruidDataSource.java (lines 3674-3680)</a>
     * Covered Lines:
     * <pre><code>
     *         dataMap.put("ExecuteCount", this.getExecuteCount());
     *         dataMap.put("ExecuteUpdateCount", this.getExecuteUpdateCount());
     *         dataMap.put("ExecuteQueryCount", this.getExecuteQueryCount());
     *         dataMap.put("ExecuteBatchCount", this.getExecuteBatchCount());
     *         dataMap.put("ErrorCount", this.getErrorCount());
     *         dataMap.put("CommitCount", this.getCommitCount());
     *         dataMap.put("RollbackCount", this.getRollbackCount());
     * </code></pre>
     * Other newly covered ranges to check: 122;128;131;133;140;146;176;178-179;196-197;204;206;208;210;213;215;429;2313;2315;2317;2322;2324;2326;2344;2367-2368;3028-3029;3032;3276;3278;3280;3285;3311;3327-3328;3344;3505;3608;3610-3612;3614-3616;3618-3619;3621;3623;3625-3628;3630-3632;3634-3636;3638-3640;3642-3644;3646-3648;3650-3654;3656-3658;3660-3662;3664-3666;3668-3670;3672;3682-3684;3686-3687
     */
    @Test(timeout = 4000)
    public void testGetStatData_withoutStat_throwsNPE() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource(false);
        // Undeclared exception!
        try {
            dataSource.getStatData();
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            // 
            // Cannot invoke \"com.alibaba.druid.stat.JdbcDataSourceStat.getConnectionHoldHistogram()\" because the return value of \"com.alibaba.druid.pool.DruidDataSource.getDataSourceStat()\" is null
            // 
            verifyException("com.alibaba.druid.pool.DruidDataSource", e);
        }
    }

    /**
     * This test added target-class coverage 1.61% for com.alibaba.druid.pool.DruidDataSource (31/1925 lines).
     * Delta details: +25 methods, +52 branches, +1448 instructions.
     * Full version of the covered block is here: <a href="https://github.com/alibaba/druid/blob/3246166f8b623969c6561eeab1c864f43829399b/core/src/main/java/com/alibaba/druid/pool/DruidDataSource.java#L1166-L1172">DruidDataSource.java (lines 1166-1172)</a>
     * Covered Lines:
     * <pre><code>
     *         } else if (dbType == DbType.mysql
     *                 || JdbcUtils.MYSQL_DRIVER.equals(this.driverClass)
     *                 || JdbcUtils.MYSQL_DRIVER_6.equals(this.driverClass)
     *                 || JdbcUtils.MYSQL_DRIVER_603.equals(this.driverClass)
     *                 || JdbcUtils.GOLDENDB_DRIVER.equals(this.driverClass)
     *                 || JdbcUtils.GBASE8S_DRIVER.equals(this.driverClass)
     *                 || JdbcUtils.POLARDBX_DRIVER.equals(this.driverClass)
     * </code></pre>
     * Other newly covered ranges to check: 122;128;131;133;140;146;176;178-179;196-197;204;206;208;210;213;215;418;511;1149;1151;1164;1177;1180
     */
    @Test(timeout = 4000)
    public void testInitCheck_defaultInvocation() throws Throwable {
        DruidDataSource dataSource = new DruidDataSource(true);
        dataSource.initCheck();
        assertTrue(dataSource.isResetStatEnable());
        assertTrue(dataSource.isEnable());
    }
}
