package io.dropwizard.client;
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
public class HttpClientBuilder_ESTest_Improved {
    @Test(timeout = 4000)
    public void buildWithDefaultRequestConfiguration_withArbitraryName_throwsException() throws Throwable {
        NoopMetricRegistry noopMetricRegistry = new NoopMetricRegistry();
        HttpClientBuilder builder = new HttpClientBuilder(noopMetricRegistry);
        // Undeclared exception!
        builder.buildWithDefaultRequestConfiguration("#QOR$rPT1|#U4`\"<");
    }

    @Test(timeout = 4000)
    public void customizeBuilder_withNullBuilder_returnsNull() throws Throwable {
        NoopMetricRegistry noopMetricRegistry = new NoopMetricRegistry();
        HttpClientBuilder builder = new HttpClientBuilder(noopMetricRegistry);
        org.apache.hc.client5.http.impl.classic.HttpClientBuilder result = builder.customizeBuilder(((org.apache.hc.client5.http.impl.classic.HttpClientBuilder) (null)));
        assertNull(result);
    }

    @Test(timeout = 4000)
    public void customizeBuilder_withProvidedBuilder_returnsSameInstance() throws Throwable {
        NoopMetricRegistry noopMetricRegistry = new NoopMetricRegistry();
        HttpClientBuilder builder = new HttpClientBuilder(noopMetricRegistry);
        org.apache.hc.client5.http.impl.classic.HttpClientBuilder providedBuilder = HttpClientBuilder.create();
        org.apache.hc.client5.http.impl.classic.HttpClientBuilder customized = builder.customizeBuilder(providedBuilder);
        assertSame(customized, providedBuilder);
    }

    @Test(timeout = 4000)
    public void createUserAgent_withNullUserAgent_returnsNull() throws Throwable {
        HttpClientBuilder builder = new HttpClientBuilder(((MetricRegistry) (null)));
        String userAgent = builder.createUserAgent(((String) (null)));
        assertNull(userAgent);
    }

    @Test(timeout = 4000)
    public void createUserAgent_withNonEmptyString_returnsSameValue() throws Throwable {
        NoopMetricRegistry noopMetricRegistry = new NoopMetricRegistry();
        HttpClientBuilder builder = new HttpClientBuilder(noopMetricRegistry);
        String userAgent = builder.createUserAgent("ignore");
        assertEquals("ignore", userAgent);
    }

    @Test(timeout = 4000)
    public void createUserAgent_withEmptyString_returnsEmptyString() throws Throwable {
        MetricRegistry metricRegistry = new MetricRegistry();
        HttpClientBuilder builder = new HttpClientBuilder(metricRegistry);
        String userAgent = builder.createUserAgent("");
        assertEquals("", userAgent);
    }

    @Test(timeout = 4000)
    public void createRequestExecutor_withName_returnsExecutor() throws Throwable {
        MetricRegistry metricRegistry = new MetricRegistry();
        HttpClientBuilder builder = new HttpClientBuilder(metricRegistry);
        HttpRequestExecutor executor = builder.createRequestExecutor("");
        assertNotNull(executor);
    }

    @Test(timeout = 4000)
    public void using_withHostnameVerifier_thenBuild_throwsException() throws Throwable {
        MetricRegistry metricRegistry = new MetricRegistry();
        HttpClientBuilder builder = new HttpClientBuilder(metricRegistry);
        DefaultHostnameVerifier hostnameVerifier = new DefaultHostnameVerifier();
        builder.using(((HostnameVerifier) (hostnameVerifier)));
        // Undeclared exception!
        builder.build("nt");
    }

    @Test(timeout = 4000)
    public void configureCredentials_withNullPassword_throwsNullPointerException() throws Throwable {
        HttpClientBuilder builder = new HttpClientBuilder(((MetricRegistry) (null)));
        AuthConfiguration authConfig = new AuthConfiguration("relaxed", ((String) (null)), ((String) (null)), "ah6", "1Wj@Eou6~7n:InR ", ((String) (null)), "nt");
        // Undeclared exception!
        try {
            builder.configureCredentials(authConfig);
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            // 
            // Cannot invoke \"String.toCharArray()\" because the return value of \"io.dropwizard.client.proxy.AuthConfiguration.getPassword()\" is null
            // 
            verifyException("io.dropwizard.client.HttpClientBuilder", e);
        }
    }

    @Test(timeout = 4000)
    public void configureCredentials_withValidAuth_returnsUsernamePasswordCredentials() throws Throwable {
        HttpClientBuilder builder = new HttpClientBuilder(((MetricRegistry) (null)));
        AuthConfiguration authConfig = new AuthConfiguration("<any port>", "ah6", ";%j$", "ah6", "g", "ae6", "ah6");
        UsernamePasswordCredentials credentials = ((UsernamePasswordCredentials) (builder.configureCredentials(authConfig)));
        assertEquals("[principal: <any port>]", credentials.toString());
    }

    @Test(timeout = 4000)
    public void build_withTlsConfiguration_throwsException() throws Throwable {
        NoopMetricRegistry noopMetricRegistry = new NoopMetricRegistry();
        HttpClientBuilder builder = new HttpClientBuilder(noopMetricRegistry);
        HttpClientConfiguration clientConfig = new HttpClientConfiguration();
        TlsConfiguration tlsConfiguration = new TlsConfiguration();
        clientConfig.setTlsConfiguration(tlsConfiguration);
        builder.using(clientConfig);
        // Undeclared exception!
        builder.build("ACCESS_CONTROL_ALLOW_HEADERS");
    }

    @Test(timeout = 4000)
    public void name_setsClientName_thenCreateClient_throwsException() throws Throwable {
        NoopMetricRegistry noopMetricRegistry = new NoopMetricRegistry();
        HttpClientBuilder builder = new HttpClientBuilder(noopMetricRegistry);
        HttpClientBuilder namedBuilder = builder.name("Zh\u007f\".7{_F+");
        InstrumentedHttpClientConnectionManager connectionManagerMock = mock(InstrumentedHttpClientConnectionManager.class, new ViolatedAssumptionAnswer());
        doReturn(((String) (null))).when(connectionManagerMock).toString();
        InstrumentedHttpClientConnectionManager configuredConnectionManager = namedBuilder.configureConnectionManager(connectionManagerMock);
        org.apache.hc.client5.http.impl.classic.HttpClientBuilder apacheBuilder = builder.createBuilder();
        // Undeclared exception!
        builder.createClient(apacheBuilder, configuredConnectionManager, ":metnd");
    }

    @Test(timeout = 4000)
    public void using_withDefaultHeaders_thenCreateClient_throwsException() throws Throwable {
        NoopMetricRegistry noopMetricRegistry = new NoopMetricRegistry();
        HttpClientBuilder builder = new HttpClientBuilder(noopMetricRegistry);
        org.apache.hc.client5.http.impl.classic.HttpClientBuilder apacheBuilder = builder.createBuilder();
        Vector<BufferedHeader> defaultHeaders = new Vector<BufferedHeader>();
        HttpClientBuilder withHeaders = builder.using(((List<? extends Header>) (defaultHeaders)));
        InstrumentedHttpClientConnectionManager connectionManagerMock = mock(InstrumentedHttpClientConnectionManager.class, new ViolatedAssumptionAnswer());
        doReturn(((String) (null))).when(connectionManagerMock).toString();
        InstrumentedHttpClientConnectionManager configuredConnectionManager = withHeaders.configureConnectionManager(connectionManagerMock);
        // Undeclared exception!
        builder.createClient(apacheBuilder, configuredConnectionManager, "%~S &RV");
    }

    @Test(timeout = 4000)
    public void disableContentCompression_thenCreateClient_throwsException() throws Throwable {
        MetricRegistry metricRegistry = new MetricRegistry();
        HttpClientBuilder builder = new HttpClientBuilder(metricRegistry);
        HttpClientBuilder compressionDisabledBuilder = builder.disableContentCompression(true);
        org.apache.hc.client5.http.impl.classic.HttpClientBuilder apacheBuilder = HttpClientBuilder.create();
        InstrumentedHttpClientConnectionManager connectionManagerMock = mock(InstrumentedHttpClientConnectionManager.class, new ViolatedAssumptionAnswer());
        doReturn(((String) (null))).when(connectionManagerMock).toString();
        InstrumentedHttpClientConnectionManager configuredConnectionManager = builder.configureConnectionManager(connectionManagerMock);
        // Undeclared exception!
        compressionDisabledBuilder.createClient(apacheBuilder, configuredConnectionManager, "|6`]JWyo,R}mN");
    }

    @Test(timeout = 4000)
    public void using_withCredentialsStore_thenCreateClient_throwsException() throws Throwable {
        NoopMetricRegistry noopMetricRegistry = new NoopMetricRegistry();
        HttpClientBuilder builder = new HttpClientBuilder(noopMetricRegistry);
        HttpClientConfiguration clientConfig = new HttpClientConfiguration();
        HttpClientBuilder withConfig = builder.using(clientConfig);
        AuthConfiguration proxyAuth = new AuthConfiguration("[... truncated]", "NT");
        org.apache.hc.client5.http.impl.classic.HttpClientBuilder apacheBuilder = withConfig.createBuilder();
        ProxyConfiguration proxyConfig = new ProxyConfiguration("NT", 1, "NT", proxyAuth);
        clientConfig.setProxyConfiguration(proxyConfig);
        SystemDefaultCredentialsProvider credentialsProvider = new SystemDefaultCredentialsProvider();
        HttpClientBuilder withCredentialsStore = builder.using(((CredentialsStore) (credentialsProvider)));
        InstrumentedHttpClientConnectionManager connectionManagerMock = mock(InstrumentedHttpClientConnectionManager.class, new ViolatedAssumptionAnswer());
        doReturn(((String) (null))).when(connectionManagerMock).toString();
        InstrumentedHttpClientConnectionManager configuredConnectionManager = withConfig.configureConnectionManager(connectionManagerMock);
        // Undeclared exception!
        withCredentialsStore.createClient(apacheBuilder, configuredConnectionManager, "[... truncated]");
    }

    @Test(timeout = 4000)
    public void configureConnectionManager_thenCreateClient_throwsException() throws Throwable {
        MetricRegistry metricRegistry = new MetricRegistry();
        HttpClientBuilder builder = new HttpClientBuilder(metricRegistry);
        HttpClientConfiguration clientConfig = new HttpClientConfiguration();
        HttpClientBuilder withConfig = builder.using(clientConfig);
        ProxyConfiguration proxyConfig = new ProxyConfiguration("g:at`?", 0);
        clientConfig.setProxyConfiguration(proxyConfig);
        org.apache.hc.client5.http.impl.classic.HttpClientBuilder apacheBuilder = HttpClientBuilder.create();
        InstrumentedHttpClientConnectionManager connectionManagerMock = mock(InstrumentedHttpClientConnectionManager.class, new ViolatedAssumptionAnswer());
        doReturn(((String) (null))).when(connectionManagerMock).toString();
        InstrumentedHttpClientConnectionManager configuredConnectionManager = withConfig.configureConnectionManager(connectionManagerMock);
        // Undeclared exception!
        builder.createClient(apacheBuilder, configuredConnectionManager, "g:at`?");
    }

    @Test(timeout = 4000)
    public void configureConnectionManager_andCreateBuilder_thenCreateClient_throwsException() throws Throwable {
        NoopMetricRegistry noopMetricRegistry = new NoopMetricRegistry();
        HttpClientBuilder builder = new HttpClientBuilder(noopMetricRegistry);
        AuthConfiguration proxyAuth = new AuthConfiguration("NAgSe*a;C*_D", "NAgSe*a;C*_D");
        HttpClientConfiguration clientConfig = new HttpClientConfiguration();
        HttpClientBuilder withConfig = builder.using(clientConfig);
        org.apache.hc.client5.http.impl.classic.HttpClientBuilder apacheBuilder = withConfig.createBuilder();
        ProxyConfiguration proxyConfig = new ProxyConfiguration("NAgSe*a;C*_D", 1, "Xvddr", proxyAuth);
        clientConfig.setProxyConfiguration(proxyConfig);
        InstrumentedHttpClientConnectionManager connectionManagerMock = mock(InstrumentedHttpClientConnectionManager.class, new ViolatedAssumptionAnswer());
        doReturn(((String) (null))).when(connectionManagerMock).toString();
        InstrumentedHttpClientConnectionManager configuredConnectionManager = withConfig.configureConnectionManager(connectionManagerMock);
        // Undeclared exception!
        builder.createClient(apacheBuilder, configuredConnectionManager, "NAgSe*a;C*_D");
    }

    @Test(timeout = 4000)
    public void using_withHttpRequestRetryStrategy_thenCreateClientWithNullManager_throwsNullPointerException() throws Throwable {
        MetricRegistry metricRegistry = new MetricRegistry();
        HttpClientBuilder builder = new HttpClientBuilder(metricRegistry);
        HttpClientConfiguration clientConfig = new HttpClientConfiguration();
        HttpClientBuilder withConfig = builder.using(clientConfig);
        clientConfig.setRetries(1166);
        org.apache.hc.client5.http.impl.classic.HttpClientBuilder apacheBuilder = HttpClientBuilder.create();
        DefaultHttpRequestRetryStrategy retryStrategy = DefaultHttpRequestRetryStrategy.INSTANCE;
        builder.using(((HttpRequestRetryStrategy) (retryStrategy)));
        // Undeclared exception!
        try {
            withConfig.createClient(apacheBuilder, ((InstrumentedHttpClientConnectionManager) (null)), "=W");
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            // 
            // Cannot invoke \"com.codahale.metrics.httpclient5.InstrumentedHttpClientConnectionManager.setDefaultSocketConfig(org.apache.hc.core5.http.io.SocketConfig)\" because \"manager\" is null
            // 
            verifyException("io.dropwizard.client.HttpClientBuilder", e);
        }
    }

    @Test(timeout = 4000)
    public void createClient_withNullBuilder_throwsNullPointerException() throws Throwable {
        NoopMetricRegistry noopMetricRegistry = new NoopMetricRegistry();
        HttpClientBuilder builder = new HttpClientBuilder(noopMetricRegistry);
        HttpClientConfiguration clientConfig = new HttpClientConfiguration();
        clientConfig.setCookiesEnabled(true);
        HttpClientBuilder withConfig = builder.using(clientConfig);
        InstrumentedHttpClientConnectionManager connectionManagerMock = mock(InstrumentedHttpClientConnectionManager.class, new ViolatedAssumptionAnswer());
        doReturn(((String) (null))).when(connectionManagerMock).toString();
        InstrumentedHttpClientConnectionManager configuredConnectionManager = builder.configureConnectionManager(connectionManagerMock);
        // Undeclared exception!
        try {
            withConfig.createClient(((org.apache.hc.client5.http.impl.classic.HttpClientBuilder) (null)), configuredConnectionManager, "4");
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            // 
            // Cannot invoke \"org.apache.hc.client5.http.impl.classic.HttpClientBuilder.setRequestExecutor(org.apache.hc.core5.http.impl.io.HttpRequestExecutor)\" because \"builder\" is null
            // 
            verifyException("io.dropwizard.client.HttpClientBuilder", e);
        }
    }

    @Test(timeout = 4000)
    public void createBuilder_thenCreateClient_throwsException() throws Throwable {
        MetricRegistry metricRegistry = new MetricRegistry();
        HttpClientBuilder builder = new HttpClientBuilder(metricRegistry);
        HttpClientConfiguration clientConfig = new HttpClientConfiguration();
        org.apache.hc.client5.http.impl.classic.HttpClientBuilder apacheBuilder = builder.createBuilder();
        builder.using(clientConfig);
        Duration connectionRequestTimeout = clientConfig.getConnectionRequestTimeout();
        clientConfig.setKeepAlive(connectionRequestTimeout);
        InstrumentedHttpClientConnectionManager connectionManagerMock = mock(InstrumentedHttpClientConnectionManager.class, new ViolatedAssumptionAnswer());
        // Undeclared exception!
        builder.createClient(apacheBuilder, connectionManagerMock, "Connection");
    }

    @Test(timeout = 4000)
    public void using_withHttpProcessor_thenCreateClient_throwsException() throws Throwable {
        NoopMetricRegistry noopMetricRegistry = new NoopMetricRegistry();
        HttpClientBuilder builder = new HttpClientBuilder(noopMetricRegistry);
        HttpRequestInterceptor[] interceptors = new HttpRequestInterceptor[4];
        DefaultHttpProcessor httpProcessor = new DefaultHttpProcessor(interceptors);
        builder.using(((HttpProcessor) (httpProcessor)));
        InstrumentedHttpClientConnectionManager connectionManagerMock = mock(InstrumentedHttpClientConnectionManager.class, new ViolatedAssumptionAnswer());
        doReturn(((String) (null))).when(connectionManagerMock).toString();
        InstrumentedHttpClientConnectionManager configuredConnectionManager = builder.configureConnectionManager(connectionManagerMock);
        org.apache.hc.client5.http.impl.classic.HttpClientBuilder apacheBuilder = builder.createBuilder();
        // Undeclared exception!
        builder.createClient(apacheBuilder, configuredConnectionManager, ":metnd");
    }

    @Test(timeout = 4000)
    public void createClient_withNullManager_throwsNullPointerException() throws Throwable {
        MetricRegistry metricRegistry = new MetricRegistry();
        HttpClientBuilder builder = new HttpClientBuilder(metricRegistry);
        HttpClientConfiguration clientConfig = new HttpClientConfiguration();
        builder.using(clientConfig);
        clientConfig.setRetries(1166);
        org.apache.hc.client5.http.impl.classic.HttpClientBuilder apacheBuilder = HttpClientBuilder.create();
        // Undeclared exception!
        try {
            builder.createClient(apacheBuilder, ((InstrumentedHttpClientConnectionManager) (null)), "=W");
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            // 
            // Cannot invoke \"com.codahale.metrics.httpclient5.InstrumentedHttpClientConnectionManager.setDefaultSocketConfig(org.apache.hc.core5.http.io.SocketConfig)\" because \"manager\" is null
            // 
            verifyException("io.dropwizard.client.HttpClientBuilder", e);
        }
    }

    @Test(timeout = 4000)
    public void using_withHttpRoutePlanner_thenCreateClient_throwsException() throws Throwable {
        MetricRegistry metricRegistry = new MetricRegistry();
        HttpClientBuilder builder = new HttpClientBuilder(metricRegistry);
        DefaultSchemePortResolver schemePortResolver = new DefaultSchemePortResolver();
        SystemDefaultRoutePlanner routePlanner = new SystemDefaultRoutePlanner(schemePortResolver, ((ProxySelector) (null)));
        HttpClientBuilder withRoutePlanner = builder.using(((HttpRoutePlanner) (routePlanner)));
        org.apache.hc.client5.http.impl.classic.HttpClientBuilder apacheBuilder = builder.createBuilder();
        InstrumentedHttpClientConnectionManager connectionManagerMock = mock(InstrumentedHttpClientConnectionManager.class, new ViolatedAssumptionAnswer());
        doReturn(((String) (null))).when(connectionManagerMock).toString();
        InstrumentedHttpClientConnectionManager configuredConnectionManager = withRoutePlanner.configureConnectionManager(connectionManagerMock);
        // Undeclared exception!
        builder.createClient(apacheBuilder, configuredConnectionManager, "");
    }

    @Test(timeout = 4000)
    public void using_withHttpClientMetricNameStrategy_returnsSameBuilder() throws Throwable {
        NoopMetricRegistry noopMetricRegistry = new NoopMetricRegistry();
        HttpClientBuilder builder = new HttpClientBuilder(noopMetricRegistry);
        HttpClientMetricNameStrategy metricNameStrategy = mock(HttpClientMetricNameStrategy.class, new ViolatedAssumptionAnswer());
        HttpClientBuilder returned = builder.using(metricNameStrategy);
        assertSame(builder, returned);
    }

    @Test(timeout = 4000)
    public void constructor_withNullEnvironment_throwsNullPointerException() throws Throwable {
        HttpClientBuilder builder = null;
        try {
            builder = new HttpClientBuilder(((Environment) (null)));
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
            // 
            // Cannot invoke \"io.dropwizard.core.setup.Environment.metrics()\" because \"environment\" is null
            // 
            verifyException("io.dropwizard.client.HttpClientBuilder", e);
        }
    }

    @Test(timeout = 4000)
    public void using_withRedirectStrategy_thenCreateClient_throwsException() throws Throwable {
        NoopMetricRegistry noopMetricRegistry = new NoopMetricRegistry();
        HttpClientBuilder builder = new HttpClientBuilder(noopMetricRegistry);
        DefaultRedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
        HttpClientBuilder withRedirectStrategy = builder.using(((RedirectStrategy) (redirectStrategy)));
        InstrumentedHttpClientConnectionManager connectionManagerMock = mock(InstrumentedHttpClientConnectionManager.class, new ViolatedAssumptionAnswer());
        doReturn(((String) (null))).when(connectionManagerMock).toString();
        InstrumentedHttpClientConnectionManager configuredConnectionManager = builder.configureConnectionManager(connectionManagerMock);
        org.apache.hc.client5.http.impl.classic.HttpClientBuilder apacheBuilder = builder.createBuilder();
        // Undeclared exception!
        withRedirectStrategy.createClient(apacheBuilder, configuredConnectionManager, ":metnd");
    }

    @Test(timeout = 4000)
    public void using_withDnsResolver_returnsSameBuilder() throws Throwable {
        NoopMetricRegistry noopMetricRegistry = new NoopMetricRegistry();
        HttpClientBuilder builder = new HttpClientBuilder(noopMetricRegistry);
        SystemDefaultDnsResolver dnsResolver = SystemDefaultDnsResolver.INSTANCE;
        HttpClientBuilder returned = builder.using(((DnsResolver) (dnsResolver)));
        assertSame(returned, builder);
    }
}