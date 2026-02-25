package io.dropwizard.client;
import io.dropwizard.client.HttpClientBuilder_ESTest_scaffolding;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.NoopMetricRegistry;
import io.dropwizard.core.setup.Environment;
import org.apache.hc.client5.http.DnsResolver;
import org.apache.hc.client5.http.SystemDefaultDnsResolver;
import org.junit.Test;
import static org.evosuite.runtime.EvoAssertions.verifyException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
public class HttpClientBuilder_ESTest_Top5 extends HttpClientBuilder_ESTest_scaffolding {
    @Test(timeout = 4000)
    public void testCustomizeBuilderReturningNull() throws Throwable {
        NoopMetricRegistry noopMetricRegistry0 = new NoopMetricRegistry();
        HttpClientBuilder httpClientBuilder0 = new HttpClientBuilder(noopMetricRegistry0);
        org.apache.hc.client5.http.impl.classic.HttpClientBuilder httpClientBuilder1 = httpClientBuilder0.customizeBuilder(((org.apache.hc.client5.http.impl.classic.HttpClientBuilder) (null)));
        assertNull(httpClientBuilder1);
    }

    @Test(timeout = 4000)
    public void testCreateUserAgentReturningNull() throws Throwable {
        HttpClientBuilder httpClientBuilder0 = new HttpClientBuilder(((MetricRegistry) (null)));
        String string0 = httpClientBuilder0.createUserAgent(((String) (null)));
        assertNull(string0);
    }

    @Test(timeout = 4000)
    public void testCreateUserAgentReturningEmptyString() throws Throwable {
        MetricRegistry metricRegistry0 = new MetricRegistry();
        HttpClientBuilder httpClientBuilder0 = new HttpClientBuilder(metricRegistry0);
        String string0 = httpClientBuilder0.createUserAgent("");
        assertEquals("", string0);
    }

    @Test(timeout = 4000)
    public void testFailsToCreateHttpClientBuilderTakingEnvironmentThrowsNullPointerException() throws Throwable {
        HttpClientBuilder httpClientBuilder0 = null;
        try {
            httpClientBuilder0 = new HttpClientBuilder(((Environment) (null)));
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            // 
            // Cannot invoke \"io.dropwizard.core.setup.Environment.metrics()\" because \"environment\" is null
            // 
            verifyException("io.dropwizard.client.HttpClientBuilder", e);
        }
    }

    @Test(timeout = 4000)
    public void testUsingTakingDnsResolver() throws Throwable {
        NoopMetricRegistry noopMetricRegistry0 = new NoopMetricRegistry();
        HttpClientBuilder httpClientBuilder0 = new HttpClientBuilder(noopMetricRegistry0);
        SystemDefaultDnsResolver systemDefaultDnsResolver0 = SystemDefaultDnsResolver.INSTANCE;
        HttpClientBuilder httpClientBuilder1 = httpClientBuilder0.using(((DnsResolver) (systemDefaultDnsResolver0)));
        assertSame(httpClientBuilder1, httpClientBuilder0);
    }
}
