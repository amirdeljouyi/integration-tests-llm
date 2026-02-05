package io.dropwizard.client;
import HttpClientBuilder_ESTest_scaffolding;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.NoopMetricRegistry;
import com.codahale.metrics.httpclient5.HttpClientMetricNameStrategy;
import com.codahale.metrics.httpclient5.InstrumentedHttpClientConnectionManager;
import io.dropwizard.client.proxy.AuthConfiguration;
import io.dropwizard.client.proxy.ProxyConfiguration;
import io.dropwizard.client.ssl.TlsConfiguration;
import io.dropwizard.core.setup.Environment;
import io.dropwizard.util.Duration;
import java.net.ProxySelector;
import java.util.List;
import java.util.Vector;
import javax.net.ssl.HostnameVerifier;
import org.apache.hc.client5.http.DnsResolver;
import org.apache.hc.client5.http.HttpRequestRetryStrategy;
import org.apache.hc.client5.http.SystemDefaultDnsResolver;
import org.apache.hc.client5.http.auth.CredentialsStore;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.impl.DefaultHttpRequestRetryStrategy;
import org.apache.hc.client5.http.impl.DefaultRedirectStrategy;
import org.apache.hc.client5.http.impl.DefaultSchemePortResolver;
import org.apache.hc.client5.http.impl.auth.SystemDefaultCredentialsProvider;
import org.apache.hc.client5.http.impl.routing.SystemDefaultRoutePlanner;
import org.apache.hc.client5.http.protocol.RedirectStrategy;
import org.apache.hc.client5.http.routing.HttpRoutePlanner;
import org.apache.hc.client5.http.ssl.DefaultHostnameVerifier;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpRequestInterceptor;
import org.apache.hc.core5.http.impl.io.HttpRequestExecutor;
import org.apache.hc.core5.http.message.BufferedHeader;
import org.apache.hc.core5.http.protocol.DefaultHttpProcessor;
import org.apache.hc.core5.http.protocol.HttpProcessor;
import org.evosuite.runtime.ViolatedAssumptionAnswer;
import org.junit.Test;
import static org.evosuite.runtime.EvoAssertions.verifyException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
public class HttpClientBuilder_ESTest_Top100 extends HttpClientBuilder_ESTest_scaffolding {
    @Test(timeout = 4000)
    public void testBuildWithDefaultRequestConfigurationThrowsTooManyResourcesException() throws Throwable {
        NoopMetricRegistry noopMetricRegistry0 = new NoopMetricRegistry();
        HttpClientBuilder httpClientBuilder0 = new HttpClientBuilder(noopMetricRegistry0);
        // Undeclared exception!
        httpClientBuilder0.buildWithDefaultRequestConfiguration("#QOR$rPT1|#U4`\"<");
    }

    @Test(timeout = 4000)
    public void testCustomizeBuilderReturningNull() throws Throwable {
        NoopMetricRegistry noopMetricRegistry0 = new NoopMetricRegistry();
        HttpClientBuilder httpClientBuilder0 = new HttpClientBuilder(noopMetricRegistry0);
        org.apache.hc.client5.http.impl.classic.HttpClientBuilder httpClientBuilder1 = httpClientBuilder0.customizeBuilder(((org.apache.hc.client5.http.impl.classic.HttpClientBuilder) (null)));
        assertNull(httpClientBuilder1);
    }

    @Test(timeout = 4000)
    public void testCustomizeBuilderReturningNonNull() throws Throwable {
        NoopMetricRegistry noopMetricRegistry0 = new NoopMetricRegistry();
        HttpClientBuilder httpClientBuilder0 = new HttpClientBuilder(noopMetricRegistry0);
        org.apache.hc.client5.http.impl.classic.HttpClientBuilder httpClientBuilder1 = HttpClientBuilder.create();
        org.apache.hc.client5.http.impl.classic.HttpClientBuilder httpClientBuilder2 = httpClientBuilder0.customizeBuilder(httpClientBuilder1);
        assertSame(httpClientBuilder2, httpClientBuilder1);
    }

    @Test(timeout = 4000)
    public void testCreateUserAgentReturningNull() throws Throwable {
        HttpClientBuilder httpClientBuilder0 = new HttpClientBuilder(((MetricRegistry) (null)));
        String string0 = httpClientBuilder0.createUserAgent(((String) (null)));
        assertNull(string0);
    }

    @Test(timeout = 4000)
    public void testCreateUserAgentReturningNonEmptyString() throws Throwable {
        NoopMetricRegistry noopMetricRegistry0 = new NoopMetricRegistry();
        HttpClientBuilder httpClientBuilder0 = new HttpClientBuilder(noopMetricRegistry0);
        String string0 = httpClientBuilder0.createUserAgent("ignore");
        assertEquals("ignore", string0);
    }

    @Test(timeout = 4000)
    public void testCreateUserAgentReturningEmptyString() throws Throwable {
        MetricRegistry metricRegistry0 = new MetricRegistry();
        HttpClientBuilder httpClientBuilder0 = new HttpClientBuilder(metricRegistry0);
        String string0 = httpClientBuilder0.createUserAgent("");
        assertEquals("", string0);
    }

    @Test(timeout = 4000)
    public void testCreateRequestExecutor() throws Throwable {
        MetricRegistry metricRegistry0 = new MetricRegistry();
        HttpClientBuilder httpClientBuilder0 = new HttpClientBuilder(metricRegistry0);
        HttpRequestExecutor httpRequestExecutor0 = httpClientBuilder0.createRequestExecutor("");
        assertNotNull(httpRequestExecutor0);
    }

    @Test(timeout = 4000)
    public void testUsingTakingHostnameVerifier() throws Throwable {
        MetricRegistry metricRegistry0 = new MetricRegistry();
        HttpClientBuilder httpClientBuilder0 = new HttpClientBuilder(metricRegistry0);
        DefaultHostnameVerifier defaultHostnameVerifier0 = new DefaultHostnameVerifier();
        httpClientBuilder0.using(((HostnameVerifier) (defaultHostnameVerifier0)));
        // Undeclared exception!
        httpClientBuilder0.build("nt");
    }

    @Test(timeout = 4000)
    public void testConfigureCredentialsThrowsNullPointerException() throws Throwable {
        HttpClientBuilder httpClientBuilder0 = new HttpClientBuilder(((MetricRegistry) (null)));
        AuthConfiguration authConfiguration0 = new AuthConfiguration("relaxed", ((String) (null)), ((String) (null)), "ah6", "1Wj@Eou6~7n:InR ", ((String) (null)), "nt");
        // Undeclared exception!
        try {
            httpClientBuilder0.configureCredentials(authConfiguration0);
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            // 
            // Cannot invoke \"String.toCharArray()\" because the return value of \"io.dropwizard.client.proxy.AuthConfiguration.getPassword()\" is null
            // 
            verifyException("io.dropwizard.client.HttpClientBuilder", e);
        }
    }

    @Test(timeout = 4000)
    public void testConfigureCredentials() throws Throwable {
        HttpClientBuilder httpClientBuilder0 = new HttpClientBuilder(((MetricRegistry) (null)));
        AuthConfiguration authConfiguration0 = new AuthConfiguration("<any port>", "ah6", ";%j$", "ah6", "g", "ae6", "ah6");
        UsernamePasswordCredentials usernamePasswordCredentials0 = ((UsernamePasswordCredentials) (httpClientBuilder0.configureCredentials(authConfiguration0)));
        assertEquals("[principal: <any port>]", usernamePasswordCredentials0.toString());
    }

    @Test(timeout = 4000)
    public void testBuildThrowsTooManyResourcesException() throws Throwable {
        NoopMetricRegistry noopMetricRegistry0 = new NoopMetricRegistry();
        HttpClientBuilder httpClientBuilder0 = new HttpClientBuilder(noopMetricRegistry0);
        HttpClientConfiguration httpClientConfiguration0 = new HttpClientConfiguration();
        TlsConfiguration tlsConfiguration0 = new TlsConfiguration();
        httpClientConfiguration0.setTlsConfiguration(tlsConfiguration0);
        httpClientBuilder0.using(httpClientConfiguration0);
        // Undeclared exception!
        httpClientBuilder0.build("ACCESS_CONTROL_ALLOW_HEADERS");
    }

    @Test(timeout = 4000)
    public void testName() throws Throwable {
        NoopMetricRegistry noopMetricRegistry0 = new NoopMetricRegistry();
        HttpClientBuilder httpClientBuilder0 = new HttpClientBuilder(noopMetricRegistry0);
        HttpClientBuilder httpClientBuilder1 = httpClientBuilder0.name("Zh\u007f\".7{_F+");
        InstrumentedHttpClientConnectionManager instrumentedHttpClientConnectionManager0 = mock(InstrumentedHttpClientConnectionManager.class, new ViolatedAssumptionAnswer());
        doReturn(((String) (null))).when(instrumentedHttpClientConnectionManager0).toString();
        InstrumentedHttpClientConnectionManager instrumentedHttpClientConnectionManager1 = httpClientBuilder1.configureConnectionManager(instrumentedHttpClientConnectionManager0);
        org.apache.hc.client5.http.impl.classic.HttpClientBuilder httpClientBuilder2 = httpClientBuilder0.createBuilder();
        // Undeclared exception!
        httpClientBuilder0.createClient(httpClientBuilder2, instrumentedHttpClientConnectionManager1, ":metnd");
    }

    @Test(timeout = 4000)
    public void testUsingTakingList() throws Throwable {
        NoopMetricRegistry noopMetricRegistry0 = new NoopMetricRegistry();
        HttpClientBuilder httpClientBuilder0 = new HttpClientBuilder(noopMetricRegistry0);
        org.apache.hc.client5.http.impl.classic.HttpClientBuilder httpClientBuilder1 = httpClientBuilder0.createBuilder();
        Vector<BufferedHeader> vector0 = new Vector<BufferedHeader>();
        HttpClientBuilder httpClientBuilder2 = httpClientBuilder0.using(((List<? extends Header>) (vector0)));
        InstrumentedHttpClientConnectionManager instrumentedHttpClientConnectionManager0 = mock(InstrumentedHttpClientConnectionManager.class, new ViolatedAssumptionAnswer());
        doReturn(((String) (null))).when(instrumentedHttpClientConnectionManager0).toString();
        InstrumentedHttpClientConnectionManager instrumentedHttpClientConnectionManager1 = httpClientBuilder2.configureConnectionManager(instrumentedHttpClientConnectionManager0);
        // Undeclared exception!
        httpClientBuilder0.createClient(httpClientBuilder1, instrumentedHttpClientConnectionManager1, "%~S &RV");
    }

    @Test(timeout = 4000)
    public void testDisableContentCompression() throws Throwable {
        MetricRegistry metricRegistry0 = new MetricRegistry();
        HttpClientBuilder httpClientBuilder0 = new HttpClientBuilder(metricRegistry0);
        HttpClientBuilder httpClientBuilder1 = httpClientBuilder0.disableContentCompression(true);
        org.apache.hc.client5.http.impl.classic.HttpClientBuilder httpClientBuilder2 = HttpClientBuilder.create();
        InstrumentedHttpClientConnectionManager instrumentedHttpClientConnectionManager0 = mock(InstrumentedHttpClientConnectionManager.class, new ViolatedAssumptionAnswer());
        doReturn(((String) (null))).when(instrumentedHttpClientConnectionManager0).toString();
        InstrumentedHttpClientConnectionManager instrumentedHttpClientConnectionManager1 = httpClientBuilder0.configureConnectionManager(instrumentedHttpClientConnectionManager0);
        // Undeclared exception!
        httpClientBuilder1.createClient(httpClientBuilder2, instrumentedHttpClientConnectionManager1, "|6`]JWyo,R}mN");
    }

    @Test(timeout = 4000)
    public void testUsingTakingCredentialsStore() throws Throwable {
        NoopMetricRegistry noopMetricRegistry0 = new NoopMetricRegistry();
        HttpClientBuilder httpClientBuilder0 = new HttpClientBuilder(noopMetricRegistry0);
        HttpClientConfiguration httpClientConfiguration0 = new HttpClientConfiguration();
        HttpClientBuilder httpClientBuilder1 = httpClientBuilder0.using(httpClientConfiguration0);
        AuthConfiguration authConfiguration0 = new AuthConfiguration("[... truncated]", "NT");
        org.apache.hc.client5.http.impl.classic.HttpClientBuilder httpClientBuilder2 = httpClientBuilder1.createBuilder();
        ProxyConfiguration proxyConfiguration0 = new ProxyConfiguration("NT", 1, "NT", authConfiguration0);
        httpClientConfiguration0.setProxyConfiguration(proxyConfiguration0);
        SystemDefaultCredentialsProvider systemDefaultCredentialsProvider0 = new SystemDefaultCredentialsProvider();
        HttpClientBuilder httpClientBuilder3 = httpClientBuilder0.using(((CredentialsStore) (systemDefaultCredentialsProvider0)));
        InstrumentedHttpClientConnectionManager instrumentedHttpClientConnectionManager0 = mock(InstrumentedHttpClientConnectionManager.class, new ViolatedAssumptionAnswer());
        doReturn(((String) (null))).when(instrumentedHttpClientConnectionManager0).toString();
        InstrumentedHttpClientConnectionManager instrumentedHttpClientConnectionManager1 = httpClientBuilder1.configureConnectionManager(instrumentedHttpClientConnectionManager0);
        // Undeclared exception!
        httpClientBuilder3.createClient(httpClientBuilder2, instrumentedHttpClientConnectionManager1, "[... truncated]");
    }

    @Test(timeout = 4000)
    public void testConfigureConnectionManager() throws Throwable {
        MetricRegistry metricRegistry0 = new MetricRegistry();
        HttpClientBuilder httpClientBuilder0 = new HttpClientBuilder(metricRegistry0);
        HttpClientConfiguration httpClientConfiguration0 = new HttpClientConfiguration();
        HttpClientBuilder httpClientBuilder1 = httpClientBuilder0.using(httpClientConfiguration0);
        ProxyConfiguration proxyConfiguration0 = new ProxyConfiguration("g:at`?", 0);
        httpClientConfiguration0.setProxyConfiguration(proxyConfiguration0);
        org.apache.hc.client5.http.impl.classic.HttpClientBuilder httpClientBuilder2 = HttpClientBuilder.create();
        InstrumentedHttpClientConnectionManager instrumentedHttpClientConnectionManager0 = mock(InstrumentedHttpClientConnectionManager.class, new ViolatedAssumptionAnswer());
        doReturn(((String) (null))).when(instrumentedHttpClientConnectionManager0).toString();
        InstrumentedHttpClientConnectionManager instrumentedHttpClientConnectionManager1 = httpClientBuilder1.configureConnectionManager(instrumentedHttpClientConnectionManager0);
        // Undeclared exception!
        httpClientBuilder0.createClient(httpClientBuilder2, instrumentedHttpClientConnectionManager1, "g:at`?");
    }

    @Test(timeout = 4000)
    public void testConfigureConnectionManagerAndCreateBuilder() throws Throwable {
        NoopMetricRegistry noopMetricRegistry0 = new NoopMetricRegistry();
        HttpClientBuilder httpClientBuilder0 = new HttpClientBuilder(noopMetricRegistry0);
        AuthConfiguration authConfiguration0 = new AuthConfiguration("NAgSe*a;C*_D", "NAgSe*a;C*_D");
        HttpClientConfiguration httpClientConfiguration0 = new HttpClientConfiguration();
        HttpClientBuilder httpClientBuilder1 = httpClientBuilder0.using(httpClientConfiguration0);
        org.apache.hc.client5.http.impl.classic.HttpClientBuilder httpClientBuilder2 = httpClientBuilder1.createBuilder();
        ProxyConfiguration proxyConfiguration0 = new ProxyConfiguration("NAgSe*a;C*_D", 1, "Xvddr", authConfiguration0);
        httpClientConfiguration0.setProxyConfiguration(proxyConfiguration0);
        InstrumentedHttpClientConnectionManager instrumentedHttpClientConnectionManager0 = mock(InstrumentedHttpClientConnectionManager.class, new ViolatedAssumptionAnswer());
        doReturn(((String) (null))).when(instrumentedHttpClientConnectionManager0).toString();
        InstrumentedHttpClientConnectionManager instrumentedHttpClientConnectionManager1 = httpClientBuilder1.configureConnectionManager(instrumentedHttpClientConnectionManager0);
        // Undeclared exception!
        httpClientBuilder0.createClient(httpClientBuilder2, instrumentedHttpClientConnectionManager1, "NAgSe*a;C*_D");
    }

    @Test(timeout = 4000)
    public void testUsingTakingHttpRequestRetryStrategy() throws Throwable {
        MetricRegistry metricRegistry0 = new MetricRegistry();
        HttpClientBuilder httpClientBuilder0 = new HttpClientBuilder(metricRegistry0);
        HttpClientConfiguration httpClientConfiguration0 = new HttpClientConfiguration();
        HttpClientBuilder httpClientBuilder1 = httpClientBuilder0.using(httpClientConfiguration0);
        httpClientConfiguration0.setRetries(1166);
        org.apache.hc.client5.http.impl.classic.HttpClientBuilder httpClientBuilder2 = HttpClientBuilder.create();
        DefaultHttpRequestRetryStrategy defaultHttpRequestRetryStrategy0 = DefaultHttpRequestRetryStrategy.INSTANCE;
        httpClientBuilder0.using(((HttpRequestRetryStrategy) (defaultHttpRequestRetryStrategy0)));
        // Undeclared exception!
        try {
            httpClientBuilder1.createClient(httpClientBuilder2, ((InstrumentedHttpClientConnectionManager) (null)), "=W");
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            // 
            // Cannot invoke \"com.codahale.metrics.httpclient5.InstrumentedHttpClientConnectionManager.setDefaultSocketConfig(org.apache.hc.core5.http.io.SocketConfig)\" because \"manager\" is null
            // 
            verifyException("io.dropwizard.client.HttpClientBuilder", e);
        }
    }

    @Test(timeout = 4000)
    public void testCreateClientWithNull() throws Throwable {
        NoopMetricRegistry noopMetricRegistry0 = new NoopMetricRegistry();
        HttpClientBuilder httpClientBuilder0 = new HttpClientBuilder(noopMetricRegistry0);
        HttpClientConfiguration httpClientConfiguration0 = new HttpClientConfiguration();
        httpClientConfiguration0.setCookiesEnabled(true);
        HttpClientBuilder httpClientBuilder1 = httpClientBuilder0.using(httpClientConfiguration0);
        InstrumentedHttpClientConnectionManager instrumentedHttpClientConnectionManager0 = mock(InstrumentedHttpClientConnectionManager.class, new ViolatedAssumptionAnswer());
        doReturn(((String) (null))).when(instrumentedHttpClientConnectionManager0).toString();
        InstrumentedHttpClientConnectionManager instrumentedHttpClientConnectionManager1 = httpClientBuilder0.configureConnectionManager(instrumentedHttpClientConnectionManager0);
        // Undeclared exception!
        try {
            httpClientBuilder1.createClient(((org.apache.hc.client5.http.impl.classic.HttpClientBuilder) (null)), instrumentedHttpClientConnectionManager1, "4");
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            // 
            // Cannot invoke \"org.apache.hc.client5.http.impl.classic.HttpClientBuilder.setRequestExecutor(org.apache.hc.core5.http.impl.io.HttpRequestExecutor)\" because \"builder\" is null
            // 
            verifyException("io.dropwizard.client.HttpClientBuilder", e);
        }
    }

    @Test(timeout = 4000)
    public void testCreateBuilder() throws Throwable {
        MetricRegistry metricRegistry0 = new MetricRegistry();
        HttpClientBuilder httpClientBuilder0 = new HttpClientBuilder(metricRegistry0);
        HttpClientConfiguration httpClientConfiguration0 = new HttpClientConfiguration();
        org.apache.hc.client5.http.impl.classic.HttpClientBuilder httpClientBuilder1 = httpClientBuilder0.createBuilder();
        httpClientBuilder0.using(httpClientConfiguration0);
        Duration duration0 = httpClientConfiguration0.getConnectionRequestTimeout();
        httpClientConfiguration0.setKeepAlive(duration0);
        InstrumentedHttpClientConnectionManager instrumentedHttpClientConnectionManager0 = mock(InstrumentedHttpClientConnectionManager.class, new ViolatedAssumptionAnswer());
        // Undeclared exception!
        httpClientBuilder0.createClient(httpClientBuilder1, instrumentedHttpClientConnectionManager0, "Connection");
    }

    @Test(timeout = 4000)
    public void testUsingTakingHttpProcessor() throws Throwable {
        NoopMetricRegistry noopMetricRegistry0 = new NoopMetricRegistry();
        HttpClientBuilder httpClientBuilder0 = new HttpClientBuilder(noopMetricRegistry0);
        HttpRequestInterceptor[] httpRequestInterceptorArray0 = new HttpRequestInterceptor[4];
        DefaultHttpProcessor defaultHttpProcessor0 = new DefaultHttpProcessor(httpRequestInterceptorArray0);
        httpClientBuilder0.using(((HttpProcessor) (defaultHttpProcessor0)));
        InstrumentedHttpClientConnectionManager instrumentedHttpClientConnectionManager0 = mock(InstrumentedHttpClientConnectionManager.class, new ViolatedAssumptionAnswer());
        doReturn(((String) (null))).when(instrumentedHttpClientConnectionManager0).toString();
        InstrumentedHttpClientConnectionManager instrumentedHttpClientConnectionManager1 = httpClientBuilder0.configureConnectionManager(instrumentedHttpClientConnectionManager0);
        org.apache.hc.client5.http.impl.classic.HttpClientBuilder httpClientBuilder1 = httpClientBuilder0.createBuilder();
        // Undeclared exception!
        httpClientBuilder0.createClient(httpClientBuilder1, instrumentedHttpClientConnectionManager1, ":metnd");
    }

    @Test(timeout = 4000)
    public void testCreateClientThrowsNullPointerException() throws Throwable {
        MetricRegistry metricRegistry0 = new MetricRegistry();
        HttpClientBuilder httpClientBuilder0 = new HttpClientBuilder(metricRegistry0);
        HttpClientConfiguration httpClientConfiguration0 = new HttpClientConfiguration();
        httpClientBuilder0.using(httpClientConfiguration0);
        httpClientConfiguration0.setRetries(1166);
        org.apache.hc.client5.http.impl.classic.HttpClientBuilder httpClientBuilder1 = HttpClientBuilder.create();
        // Undeclared exception!
        try {
            httpClientBuilder0.createClient(httpClientBuilder1, ((InstrumentedHttpClientConnectionManager) (null)), "=W");
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            // 
            // Cannot invoke \"com.codahale.metrics.httpclient5.InstrumentedHttpClientConnectionManager.setDefaultSocketConfig(org.apache.hc.core5.http.io.SocketConfig)\" because \"manager\" is null
            // 
            verifyException("io.dropwizard.client.HttpClientBuilder", e);
        }
    }

    @Test(timeout = 4000)
    public void testUsingTakingHttpRoutePlanner() throws Throwable {
        MetricRegistry metricRegistry0 = new MetricRegistry();
        HttpClientBuilder httpClientBuilder0 = new HttpClientBuilder(metricRegistry0);
        DefaultSchemePortResolver defaultSchemePortResolver0 = new DefaultSchemePortResolver();
        SystemDefaultRoutePlanner systemDefaultRoutePlanner0 = new SystemDefaultRoutePlanner(defaultSchemePortResolver0, ((ProxySelector) (null)));
        HttpClientBuilder httpClientBuilder1 = httpClientBuilder0.using(((HttpRoutePlanner) (systemDefaultRoutePlanner0)));
        org.apache.hc.client5.http.impl.classic.HttpClientBuilder httpClientBuilder2 = httpClientBuilder0.createBuilder();
        InstrumentedHttpClientConnectionManager instrumentedHttpClientConnectionManager0 = mock(InstrumentedHttpClientConnectionManager.class, new ViolatedAssumptionAnswer());
        doReturn(((String) (null))).when(instrumentedHttpClientConnectionManager0).toString();
        InstrumentedHttpClientConnectionManager instrumentedHttpClientConnectionManager1 = httpClientBuilder1.configureConnectionManager(instrumentedHttpClientConnectionManager0);
        // Undeclared exception!
        httpClientBuilder0.createClient(httpClientBuilder2, instrumentedHttpClientConnectionManager1, "");
    }

    @Test(timeout = 4000)
    public void testUsingTakingHttpClientMetricNameStrategy() throws Throwable {
        NoopMetricRegistry noopMetricRegistry0 = new NoopMetricRegistry();
        HttpClientBuilder httpClientBuilder0 = new HttpClientBuilder(noopMetricRegistry0);
        HttpClientMetricNameStrategy httpClientMetricNameStrategy0 = mock(HttpClientMetricNameStrategy.class, new ViolatedAssumptionAnswer());
        HttpClientBuilder httpClientBuilder1 = httpClientBuilder0.using(httpClientMetricNameStrategy0);
        assertSame(httpClientBuilder0, httpClientBuilder1);
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
    public void testUsingTakingRedirectStrategy() throws Throwable {
        NoopMetricRegistry noopMetricRegistry0 = new NoopMetricRegistry();
        HttpClientBuilder httpClientBuilder0 = new HttpClientBuilder(noopMetricRegistry0);
        DefaultRedirectStrategy defaultRedirectStrategy0 = new DefaultRedirectStrategy();
        HttpClientBuilder httpClientBuilder1 = httpClientBuilder0.using(((RedirectStrategy) (defaultRedirectStrategy0)));
        InstrumentedHttpClientConnectionManager instrumentedHttpClientConnectionManager0 = mock(InstrumentedHttpClientConnectionManager.class, new ViolatedAssumptionAnswer());
        doReturn(((String) (null))).when(instrumentedHttpClientConnectionManager0).toString();
        InstrumentedHttpClientConnectionManager instrumentedHttpClientConnectionManager1 = httpClientBuilder0.configureConnectionManager(instrumentedHttpClientConnectionManager0);
        org.apache.hc.client5.http.impl.classic.HttpClientBuilder httpClientBuilder2 = httpClientBuilder0.createBuilder();
        // Undeclared exception!
        httpClientBuilder1.createClient(httpClientBuilder2, instrumentedHttpClientConnectionManager1, ":metnd");
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