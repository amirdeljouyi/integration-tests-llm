package io.dropwizard.client;
import HttpClientBuilder_ESTest_scaffolding;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.NoopMetricRegistry;
import com.codahale.metrics.httpclient5.HttpClientMetricNameStrategies;
import com.codahale.metrics.httpclient5.HttpClientMetricNameStrategy;
import com.codahale.metrics.httpclient5.InstrumentedHttpClientConnectionManager;
import com.codahale.metrics.httpclient5.InstrumentedHttpRequestExecutor;
import io.dropwizard.client.proxy.AuthConfiguration;
import io.dropwizard.client.proxy.ProxyConfiguration;
import io.dropwizard.client.ssl.TlsConfiguration;
import io.dropwizard.core.setup.Environment;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import io.dropwizard.util.Duration;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import javax.net.ssl.HostnameVerifier;
import org.apache.hc.client5.http.DnsResolver;
import org.apache.hc.client5.http.HttpRequestRetryStrategy;
import org.apache.hc.client5.http.HttpRoute;
import org.apache.hc.client5.http.SystemDefaultDnsResolver;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.Credentials;
import org.apache.hc.client5.http.auth.CredentialsProvider;
import org.apache.hc.client5.http.auth.CredentialsStore;
import org.apache.hc.client5.http.auth.NTCredentials;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.cookie.StandardCookieSpec;
import org.apache.hc.client5.http.impl.DefaultConnectionKeepAliveStrategy;
import org.apache.hc.client5.http.impl.DefaultHttpRequestRetryStrategy;
import org.apache.hc.client5.http.impl.DefaultRedirectStrategy;
import org.apache.hc.client5.http.impl.DefaultSchemePortResolver;
import org.apache.hc.client5.http.impl.auth.SystemDefaultCredentialsProvider;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.routing.DefaultRoutePlanner;
import org.apache.hc.client5.http.impl.routing.SystemDefaultRoutePlanner;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.client5.http.protocol.RedirectStrategy;
import org.apache.hc.client5.http.routing.HttpRoutePlanner;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.DefaultHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.function.Resolver;
import org.apache.hc.core5.http.ConnectionReuseStrategy;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.HttpRequestInterceptor;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.http.impl.io.HttpRequestExecutor;
import org.apache.hc.core5.http.message.BasicHeader;
import org.apache.hc.core5.http.message.BasicHeaderIterator;
import org.apache.hc.core5.http.message.BufferedHeader;
import org.apache.hc.core5.http.protocol.BasicHttpContext;
import org.apache.hc.core5.http.protocol.DefaultHttpProcessor;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.http.protocol.HttpProcessor;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.evosuite.runtime.ViolatedAssumptionAnswer;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import static org.evosuite.runtime.EvoAssertions.verifyException;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
public class HttpClientBuilder_ESTest_Adopted extends HttpClientBuilder_ESTest_scaffolding {
    @Test(timeout = 4000)
    public void buildWithDefaultRequestConfiguration_withArbitraryName_throwsException() throws Throwable {
        NoopMetricRegistry noopMetricRegistry = new NoopMetricRegistry();
        HttpClientBuilder builder = new HttpClientBuilder(noopMetricRegistry);
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
        builder.build("nt");
    }

    @Test(timeout = 4000)
    public void configureCredentials_withNullPassword_throwsNullPointerException() throws Throwable {
        HttpClientBuilder builder = new HttpClientBuilder(((MetricRegistry) (null)));
        AuthConfiguration authConfig = new AuthConfiguration("relaxed", ((String) (null)), ((String) (null)), "ah6", "1Wj@Eou6~7n:InR ", ((String) (null)), "nt");
        try {
            builder.configureCredentials(authConfig);
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
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
        try {
            withConfig.createClient(apacheBuilder, ((InstrumentedHttpClientConnectionManager) (null)), "=W");
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
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
        try {
            withConfig.createClient(((org.apache.hc.client5.http.impl.classic.HttpClientBuilder) (null)), configuredConnectionManager, "4");
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
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
        try {
            builder.createClient(apacheBuilder, ((InstrumentedHttpClientConnectionManager) (null)), "=W");
            fail("Expecting exception: NullPointerException");
        } catch (NullPointerException e) {
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

    // ===== Adapted and merged tests from IGT below =====

    private static Object getField(Object target, String name) throws Exception {
        Class<?> c = target.getClass();
        while (c != null) {
            try {
                Field f = c.getDeclaredField(name);
                f.setAccessible(true);
                return f.get(target);
            } catch (NoSuchFieldException e) {
                c = c.getSuperclass();
            }
        }
        throw new NoSuchFieldException(name);
    }

    private static Object getNestedField(Object target, String path) throws Exception {
        String[] parts = path.split("\\.");
        Object current = target;
        for (String p : parts) {
            current = getField(current, p);
        }
        return current;
    }

    private static HostnameVerifier extractHostnameVerifier(SSLConnectionSocketFactory sf) throws Exception {
        try {
            Method m = sf.getClass().getDeclaredMethod("getHostnameVerifier");
            m.setAccessible(true);
            Object hv = m.invoke(sf);
            return (HostnameVerifier) hv;
        } catch (NoSuchMethodException ex) {
            return (HostnameVerifier) getField(sf, "hostnameVerifier");
        }
    }

    private static Registry<ConnectionSocketFactory> createDefaultRegistry() {
        return RegistryBuilder.<ConnectionSocketFactory>create()
            .register("http", PlainConnectionSocketFactory.getSocketFactory())
            .register("https", SSLConnectionSocketFactory.getSocketFactory())
            .build();
    }

    private static class AnotherHttpClientBuilder extends org.apache.hc.client5.http.impl.classic.HttpClientBuilder {
        static AnotherHttpClientBuilder create() {
            return new AnotherHttpClientBuilder();
        }
    }

    private static class CustomRequestExecutor extends HttpRequestExecutor {
    }

    private static class CustomBuilder extends HttpClientBuilder {
        boolean customized;
        org.apache.hc.client5.http.impl.classic.HttpClientBuilder apache;

        CustomBuilder(MetricRegistry metricRegistry) {
            this(metricRegistry, org.apache.hc.client5.http.impl.classic.HttpClientBuilder.create());
        }

        CustomBuilder(MetricRegistry metricRegistry, org.apache.hc.client5.http.impl.classic.HttpClientBuilder builder) {
            super(metricRegistry);
            this.customized = false;
            this.apache = builder;
        }

        @Override
        protected org.apache.hc.client5.http.impl.classic.HttpClientBuilder createBuilder() {
            return apache;
        }

        @Override
        protected HttpRequestExecutor createRequestExecutor(String name) {
            return new CustomRequestExecutor();
        }

        @Override
        protected org.apache.hc.client5.http.impl.classic.HttpClientBuilder customizeBuilder(org.apache.hc.client5.http.impl.classic.HttpClientBuilder builder) {
            customized = true;
            return builder;
        }
    }

    @Test(timeout = 4000)
    public void setsTheMaximumConnectionPoolSize() throws Throwable {
        MetricRegistry metrics = new MetricRegistry();
        HttpClientConfiguration configuration = new HttpClientConfiguration();
        configuration.setMaxConnections(412);
        HttpClientBuilder builder = new HttpClientBuilder(metrics);

        Registry<ConnectionSocketFactory> registry = createDefaultRegistry();
        InstrumentedHttpClientConnectionManager manager = spy(InstrumentedHttpClientConnectionManager.builder(metrics).socketFactoryRegistry(registry).build());
        org.apache.hc.client5.http.impl.classic.HttpClientBuilder apacheBuilder = org.apache.hc.client5.http.impl.classic.HttpClientBuilder.create();

        ConfiguredCloseableHttpClient client = builder.using(configuration)
            .createClient(apacheBuilder, builder.configureConnectionManager(manager), "test");

        assertNotNull(client);
        assertSame(manager, getField(apacheBuilder, "connManager"));
        verify(manager).setMaxTotal(412);
    }

    @Test(timeout = 4000)
    public void setsTheMaximumRoutePoolSize() throws Throwable {
        MetricRegistry metrics = new MetricRegistry();
        HttpClientConfiguration configuration = new HttpClientConfiguration();
        configuration.setMaxConnectionsPerRoute(413);
        HttpClientBuilder builder = new HttpClientBuilder(metrics);

        Registry<ConnectionSocketFactory> registry = createDefaultRegistry();
        InstrumentedHttpClientConnectionManager manager = spy(InstrumentedHttpClientConnectionManager.builder(metrics).socketFactoryRegistry(registry).build());
        org.apache.hc.client5.http.impl.classic.HttpClientBuilder apacheBuilder = org.apache.hc.client5.http.impl.classic.HttpClientBuilder.create();

        ConfiguredCloseableHttpClient client = builder.using(configuration)
            .createClient(apacheBuilder, builder.configureConnectionManager(manager), "test");

        assertNotNull(client);
        assertSame(manager, getField(apacheBuilder, "connManager"));
        verify(manager).setDefaultMaxPerRoute(413);
    }

    @Test(timeout = 4000)
    public void setsTheUserAgent() throws Throwable {
        MetricRegistry metrics = new MetricRegistry();
        HttpClientConfiguration configuration = new HttpClientConfiguration();
        configuration.setUserAgent(java.util.Optional.of("qwerty"));
        HttpClientBuilder builder = new HttpClientBuilder(metrics);
        org.apache.hc.client5.http.impl.classic.HttpClientBuilder apacheBuilder = org.apache.hc.client5.http.impl.classic.HttpClientBuilder.create();
        Registry<ConnectionSocketFactory> registry = createDefaultRegistry();
        InstrumentedHttpClientConnectionManager manager = InstrumentedHttpClientConnectionManager.builder(metrics).socketFactoryRegistry(registry).build();

        assertNotNull(builder.using(configuration).createClient(apacheBuilder, manager, "test"));
        assertEquals("qwerty", getField(apacheBuilder, "userAgent"));
    }

    @Test(timeout = 4000)
    public void canUseACustomDnsResolver() throws Throwable {
        MetricRegistry metrics = new MetricRegistry();
        HttpClientBuilder builder = new HttpClientBuilder(metrics);
        Registry<ConnectionSocketFactory> registry = createDefaultRegistry();

        DnsResolver resolver = mock(DnsResolver.class);
        InstrumentedHttpClientConnectionManager manager = builder.using(resolver).createConnectionManager(registry, "test");

        Object connectionOperator = getField(manager, "connectionOperator");
        Object dnsResolver = getField(connectionOperator, "dnsResolver");
        assertSame(resolver, dnsResolver);
    }

    @Test(timeout = 4000)
    public void usesASystemDnsResolverByDefault() throws Throwable {
        MetricRegistry metrics = new MetricRegistry();
        HttpClientBuilder builder = new HttpClientBuilder(metrics);
        Registry<ConnectionSocketFactory> registry = createDefaultRegistry();

        InstrumentedHttpClientConnectionManager manager = builder.createConnectionManager(registry, "test");
        Object connectionOperator = getField(manager, "connectionOperator");
        Object dnsResolver = getField(connectionOperator, "dnsResolver");
        assertTrue(dnsResolver instanceof SystemDefaultDnsResolver);
    }

    @Test(timeout = 4000)
    public void canUseACustomHostnameVerifierWhenTlsConfigurationNotSpecified() throws Throwable {
        MetricRegistry metrics = new MetricRegistry();
        HttpClientBuilder builder = new HttpClientBuilder(metrics);
        HostnameVerifier customVerifier = (s, sslSession) -> false;

        Registry<ConnectionSocketFactory> configured = builder.using(customVerifier).createConfiguredRegistry();
        assertNotNull(configured);

        SSLConnectionSocketFactory sf = (SSLConnectionSocketFactory) configured.lookup("https");
        assertNotNull(sf);
        assertSame(customVerifier, extractHostnameVerifier(sf));
    }

    @Test(timeout = 4000)
    public void canUseACustomHostnameVerifierWhenTlsConfigurationSpecified() throws Throwable {
        MetricRegistry metrics = new MetricRegistry();
        HttpClientConfiguration configuration = new HttpClientConfiguration();
        TlsConfiguration tlsConfiguration = new TlsConfiguration();
        tlsConfiguration.setVerifyHostname(true);
        configuration.setTlsConfiguration(tlsConfiguration);
        HttpClientBuilder builder = new HttpClientBuilder(metrics);

        HostnameVerifier customVerifier = (s, sslSession) -> false;

        Registry<ConnectionSocketFactory> configured = builder.using(configuration).using(customVerifier).createConfiguredRegistry();
        assertNotNull(configured);

        SSLConnectionSocketFactory sf = (SSLConnectionSocketFactory) configured.lookup("https");
        assertNotNull(sf);
        assertSame(customVerifier, extractHostnameVerifier(sf));
    }

    @Test(timeout = 4000)
    public void canUseASystemHostnameVerifierByDefaultWhenTlsConfigurationNotSpecified() throws Throwable {
        MetricRegistry metrics = new MetricRegistry();
        HttpClientBuilder builder = new HttpClientBuilder(metrics);

        Registry<ConnectionSocketFactory> configured = builder.createConfiguredRegistry();
        assertNotNull(configured);

        SSLConnectionSocketFactory sf = (SSLConnectionSocketFactory) configured.lookup("https");
        assertNotNull(sf);
        assertTrue(extractHostnameVerifier(sf) instanceof HostnameVerifier);
    }

    @Test(timeout = 4000)
    public void canUseASystemHostnameVerifierByDefaultWhenTlsConfigurationSpecified() throws Throwable {
        MetricRegistry metrics = new MetricRegistry();
        HttpClientConfiguration configuration = new HttpClientConfiguration();
        TlsConfiguration tlsConfiguration = new TlsConfiguration();
        tlsConfiguration.setVerifyHostname(true);
        configuration.setTlsConfiguration(tlsConfiguration);
        HttpClientBuilder builder = new HttpClientBuilder(metrics);

        Registry<ConnectionSocketFactory> configured = builder.using(configuration).createConfiguredRegistry();
        assertNotNull(configured);

        SSLConnectionSocketFactory sf = (SSLConnectionSocketFactory) configured.lookup("https");
        assertNotNull(sf);
        assertTrue(extractHostnameVerifier(sf) instanceof HostnameVerifier);
    }

    @Test(timeout = 4000)
    public void doesNotReuseConnectionsIfKeepAliveIsZero() throws Throwable {
        MetricRegistry metrics = new MetricRegistry();
        HttpClientConfiguration configuration = new HttpClientConfiguration();
        configuration.setKeepAlive(Duration.seconds(0));
        HttpClientBuilder builder = new HttpClientBuilder(metrics);

        Registry<ConnectionSocketFactory> registry = createDefaultRegistry();
        InstrumentedHttpClientConnectionManager manager = InstrumentedHttpClientConnectionManager.builder(metrics).socketFactoryRegistry(registry).build();
        org.apache.hc.client5.http.impl.classic.HttpClientBuilder apacheBuilder = org.apache.hc.client5.http.impl.classic.HttpClientBuilder.create();

        assertNotNull(builder.using(configuration).createClient(apacheBuilder, manager, "test"));
        Object reuseStrategy = getField(apacheBuilder, "reuseStrategy");
        assertTrue(reuseStrategy instanceof ConnectionReuseStrategy);
    }

    @Test(timeout = 4000)
    public void reusesConnectionsIfKeepAliveIsNonZero() throws Throwable {
        MetricRegistry metrics = new MetricRegistry();
        HttpClientConfiguration configuration = new HttpClientConfiguration();
        configuration.setKeepAlive(Duration.seconds(1));
        HttpClientBuilder builder = new HttpClientBuilder(metrics);

        Registry<ConnectionSocketFactory> registry = createDefaultRegistry();
        InstrumentedHttpClientConnectionManager manager = InstrumentedHttpClientConnectionManager.builder(metrics).socketFactoryRegistry(registry).build();
        org.apache.hc.client5.http.impl.classic.HttpClientBuilder apacheBuilder = org.apache.hc.client5.http.impl.classic.HttpClientBuilder.create();

        assertNotNull(builder.using(configuration).createClient(apacheBuilder, manager, "test"));
        Object reuseStrategy = getField(apacheBuilder, "reuseStrategy");
        assertTrue(reuseStrategy instanceof org.apache.hc.core5.http.impl.DefaultConnectionReuseStrategy);
    }

    @Test(timeout = 4000)
    public void usesKeepAliveForPersistentConnections() throws Throwable {
        MetricRegistry metrics = new MetricRegistry();
        HttpClientConfiguration configuration = new HttpClientConfiguration();
        configuration.setKeepAlive(Duration.seconds(1));
        HttpClientBuilder builder = new HttpClientBuilder(metrics);

        Registry<ConnectionSocketFactory> registry = createDefaultRegistry();
        InstrumentedHttpClientConnectionManager manager = InstrumentedHttpClientConnectionManager.builder(metrics).socketFactoryRegistry(registry).build();
        org.apache.hc.client5.http.impl.classic.HttpClientBuilder apacheBuilder = org.apache.hc.client5.http.impl.classic.HttpClientBuilder.create();

        ConfiguredCloseableHttpClient client = builder.using(configuration).createClient(apacheBuilder, manager, "test");
        assertNotNull(client);

        HttpClientContext context = mock(HttpClientContext.class);
        HttpResponse response = mock(HttpResponse.class);
        when(context.getRequestConfig()).thenReturn(client.getDefaultRequestConfig());
        when(context.getRequestConfigOrDefault()).thenCallRealMethod();
        when(response.headerIterator()).thenReturn(Collections.emptyIterator());
        when(response.headerIterator(any())).thenReturn(Collections.emptyIterator());

        DefaultConnectionKeepAliveStrategy strategy = (DefaultConnectionKeepAliveStrategy) getField(apacheBuilder, "keepAliveStrategy");
        assertEquals(TimeValue.ofSeconds(1), strategy.getKeepAliveDuration(response, context));
    }

    @Test(timeout = 4000)
    public void usesDefaultForNonPersistentConnections() throws Throwable {
        MetricRegistry metrics = new MetricRegistry();
        HttpClientConfiguration configuration = new HttpClientConfiguration();
        configuration.setKeepAlive(Duration.seconds(1));
        HttpClientBuilder builder = new HttpClientBuilder(metrics);

        Registry<ConnectionSocketFactory> registry = createDefaultRegistry();
        InstrumentedHttpClientConnectionManager manager = InstrumentedHttpClientConnectionManager.builder(metrics).socketFactoryRegistry(registry).build();
        org.apache.hc.client5.http.impl.classic.HttpClientBuilder apacheBuilder = org.apache.hc.client5.http.impl.classic.HttpClientBuilder.create();

        assertNotNull(builder.using(configuration).createClient(apacheBuilder, manager, "test"));

        HttpContext context = mock(HttpContext.class);
        Header[] headers = new Header[] { new BasicHeader(HttpHeaders.CONNECTION, "timeout=50") };
        BasicHeaderIterator headerIterator = new BasicHeaderIterator(headers, HttpHeaders.CONNECTION);
        HttpResponse response = mock(HttpResponse.class);
        when(response.headerIterator()).thenReturn(headerIterator);
        when(response.headerIterator(any())).thenReturn(headerIterator);

        DefaultConnectionKeepAliveStrategy strategy = (DefaultConnectionKeepAliveStrategy) getField(apacheBuilder, "keepAliveStrategy");
        assertEquals(TimeValue.ofMilliseconds(50_000L), strategy.getKeepAliveDuration(response, context));
    }

    @Test(timeout = 4000)
    public void ignoresCookiesByDefault() throws Throwable {
        MetricRegistry metrics = new MetricRegistry();
        HttpClientConfiguration configuration = new HttpClientConfiguration();
        HttpClientBuilder builder = new HttpClientBuilder(metrics);
        Registry<ConnectionSocketFactory> registry = createDefaultRegistry();
        InstrumentedHttpClientConnectionManager manager = InstrumentedHttpClientConnectionManager.builder(metrics).socketFactoryRegistry(registry).build();
        org.apache.hc.client5.http.impl.classic.HttpClientBuilder apacheBuilder = org.apache.hc.client5.http.impl.classic.HttpClientBuilder.create();

        assertNotNull(builder.using(configuration).createClient(apacheBuilder, manager, "test"));
        RequestConfig rc = (RequestConfig) getField(apacheBuilder, "defaultRequestConfig");
        assertEquals(StandardCookieSpec.IGNORE, rc.getCookieSpec());
    }

    @Test(timeout = 4000)
    public void usesBestMatchCookiePolicyIfCookiesAreEnabled() throws Throwable {
        MetricRegistry metrics = new MetricRegistry();
        HttpClientConfiguration configuration = new HttpClientConfiguration();
        configuration.setCookiesEnabled(true);
        HttpClientBuilder builder = new HttpClientBuilder(metrics);
        Registry<ConnectionSocketFactory> registry = createDefaultRegistry();
        InstrumentedHttpClientConnectionManager manager = InstrumentedHttpClientConnectionManager.builder(metrics).socketFactoryRegistry(registry).build();
        org.apache.hc.client5.http.impl.classic.HttpClientBuilder apacheBuilder = org.apache.hc.client5.http.impl.classic.HttpClientBuilder.create();

        assertNotNull(builder.using(configuration).createClient(apacheBuilder, manager, "test"));
        RequestConfig rc = (RequestConfig) getField(apacheBuilder, "defaultRequestConfig");
        assertEquals(StandardCookieSpec.RELAXED, rc.getCookieSpec());
    }

    @Test(timeout = 4000)
    public void setsTheSocketTimeout() throws Throwable {
        MetricRegistry metrics = new MetricRegistry();
        HttpClientConfiguration configuration = new HttpClientConfiguration();
        configuration.setTimeout(Duration.milliseconds(500));
        HttpClientBuilder builder = new HttpClientBuilder(metrics);
        Registry<ConnectionSocketFactory> registry = createDefaultRegistry();
        InstrumentedHttpClientConnectionManager manager = InstrumentedHttpClientConnectionManager.builder(metrics).socketFactoryRegistry(registry).build();
        org.apache.hc.client5.http.impl.classic.HttpClientBuilder apacheBuilder = org.apache.hc.client5.http.impl.classic.HttpClientBuilder.create();

        assertNotNull(builder.using(configuration).createClient(apacheBuilder, manager, "test"));
        RequestConfig rc = (RequestConfig) getField(apacheBuilder, "defaultRequestConfig");
        assertEquals(Timeout.ofMilliseconds(500L), rc.getResponseTimeout());
    }

    @Test(timeout = 4000)
    public void setsTheConnectTimeout() throws Throwable {
        MetricRegistry metrics = new MetricRegistry();
        HttpClientConfiguration configuration = new HttpClientConfiguration();
        configuration.setConnectionTimeout(Duration.milliseconds(123));
        HttpClientBuilder builder = new HttpClientBuilder(metrics);

        CloseableHttpClient httpClient = builder.using(configuration).build("test");
        Resolver<HttpRoute, ConnectionConfig> resolver =
            (Resolver<HttpRoute, ConnectionConfig>) getNestedField(httpClient, "connManager.connectionConfigResolver");
        ConnectionConfig cfg = resolver.resolve(new HttpRoute(HttpHost.create(URI.create("https://example.org:443"))));
        assertEquals(Timeout.ofMilliseconds(123L), cfg.getConnectTimeout());
    }

    @Test(timeout = 4000)
    public void setsTheConnectionRequestTimeout() throws Throwable {
        MetricRegistry metrics = new MetricRegistry();
        HttpClientConfiguration configuration = new HttpClientConfiguration();
        configuration.setConnectionRequestTimeout(Duration.milliseconds(123));
        HttpClientBuilder builder = new HttpClientBuilder(metrics);
        Registry<ConnectionSocketFactory> registry = createDefaultRegistry();
        InstrumentedHttpClientConnectionManager manager = InstrumentedHttpClientConnectionManager.builder(metrics).socketFactoryRegistry(registry).build();
        org.apache.hc.client5.http.impl.classic.HttpClientBuilder apacheBuilder = org.apache.hc.client5.http.impl.classic.HttpClientBuilder.create();

        assertNotNull(builder.using(configuration).createClient(apacheBuilder, manager, "test"));
        RequestConfig rc = (RequestConfig) getField(apacheBuilder, "defaultRequestConfig");
        assertEquals(Timeout.ofMilliseconds(123L), rc.getConnectionRequestTimeout());
    }

    @Test(timeout = 4000)
    public void usesTheDefaultRoutePlanner() throws Throwable {
        MetricRegistry metrics = new MetricRegistry();
        HttpClientConfiguration configuration = new HttpClientConfiguration();
        HttpClientBuilder builder = new HttpClientBuilder(metrics);
        Registry<ConnectionSocketFactory> registry = createDefaultRegistry();
        InstrumentedHttpClientConnectionManager manager = InstrumentedHttpClientConnectionManager.builder(metrics).socketFactoryRegistry(registry).build();
        org.apache.hc.client5.http.impl.classic.HttpClientBuilder apacheBuilder = org.apache.hc.client5.http.impl.classic.HttpClientBuilder.create();

        CloseableHttpClient httpClient = builder.using(configuration).createClient(apacheBuilder, manager, "test").getClient();

        assertNull(getField(apacheBuilder, "routePlanner"));
        Object rp = getField(httpClient, "routePlanner");
        assertTrue(rp instanceof DefaultRoutePlanner);
    }

    @Test(timeout = 4000)
    public void usesACustomRoutePlanner() throws Throwable {
        MetricRegistry metrics = new MetricRegistry();
        HttpClientConfiguration configuration = new HttpClientConfiguration();
        HttpClientBuilder builder = new HttpClientBuilder(metrics);
        Registry<ConnectionSocketFactory> registry = createDefaultRegistry();
        InstrumentedHttpClientConnectionManager manager = InstrumentedHttpClientConnectionManager.builder(metrics).socketFactoryRegistry(registry).build();
        org.apache.hc.client5.http.impl.classic.HttpClientBuilder apacheBuilder = org.apache.hc.client5.http.impl.classic.HttpClientBuilder.create();

        HttpRoutePlanner routePlanner = new SystemDefaultRoutePlanner(new ProxySelector() {
            @Override
            public List<Proxy> select(URI uri) {
                return Collections.singletonList(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("192.168.52.1", 8080)));
            }
            @Override
            public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
            }
        });

        CloseableHttpClient httpClient = builder.using(configuration).using(routePlanner)
            .createClient(apacheBuilder, manager, "test").getClient();

        assertSame(routePlanner, getField(apacheBuilder, "routePlanner"));
        assertNotNull(httpClient);
        assertSame(routePlanner, getField(httpClient, "routePlanner"));
    }

    @Test(timeout = 4000)
    public void usesACustomHttpRequestRetryHandler() throws Throwable {
        MetricRegistry metrics = new MetricRegistry();
        HttpClientConfiguration configuration = new HttpClientConfiguration();
        HttpClientBuilder builder = new HttpClientBuilder(metrics);
        HttpRequestRetryStrategy custom = new HttpRequestRetryStrategy() {
            @Override
            public boolean retryRequest(org.apache.hc.core5.http.HttpRequest request, IOException exception, int execCount, HttpContext context) { return false; }
            @Override
            public boolean retryRequest(HttpResponse response, int execCount, HttpContext context) { return false; }
            @Override
            public TimeValue getRetryInterval(HttpResponse response, int execCount, HttpContext context) { return null; }
        };
        configuration.setRetries(1);

        Registry<ConnectionSocketFactory> registry = createDefaultRegistry();
        InstrumentedHttpClientConnectionManager manager = InstrumentedHttpClientConnectionManager.builder(metrics).socketFactoryRegistry(registry).build();
        org.apache.hc.client5.http.impl.classic.HttpClientBuilder apacheBuilder = org.apache.hc.client5.http.impl.classic.HttpClientBuilder.create();

        assertNotNull(builder.using(configuration).using(custom).createClient(apacheBuilder, manager, "test"));
        assertSame(custom, getField(apacheBuilder, "retryStrategy"));
    }

    @Test(timeout = 4000)
    public void usesCredentialsProvider() throws Throwable {
        MetricRegistry metrics = new MetricRegistry();
        HttpClientConfiguration configuration = new HttpClientConfiguration();
        HttpClientBuilder builder = new HttpClientBuilder(metrics);
        CredentialsStore credentialsProvider = new CredentialsStore() {
            @Override
            public void setCredentials(AuthScope authscope, Credentials credentials) {}
            @Override
            public Credentials getCredentials(AuthScope authScope, HttpContext context) { return null; }
            @Override
            public void clear() {}
        };

        Registry<ConnectionSocketFactory> registry = createDefaultRegistry();
        InstrumentedHttpClientConnectionManager manager = InstrumentedHttpClientConnectionManager.builder(metrics).socketFactoryRegistry(registry).build();
        org.apache.hc.client5.http.impl.classic.HttpClientBuilder apacheBuilder = org.apache.hc.client5.http.impl.classic.HttpClientBuilder.create();

        assertNotNull(builder.using(configuration).using(credentialsProvider).createClient(apacheBuilder, manager, "test"));
        assertSame(credentialsProvider, getField(apacheBuilder, "credentialsProvider"));
    }

    @Test(timeout = 4000)
    public void usesProxy() throws Throwable {
        MetricRegistry metrics = new MetricRegistry();
        HttpClientConfiguration config = new HttpClientConfiguration();
        ProxyConfiguration proxy = new ProxyConfiguration("192.168.52.11", 8080);
        config.setProxyConfiguration(proxy);

        CloseableHttpClient httpClient = checkProxy(new HttpClientBuilder(metrics), config, new HttpHost("dropwizard.io", 80), new HttpHost("192.168.52.11", 8080));
        assertNotNull(httpClient);
    }

    @Test(timeout = 4000)
    public void usesProxyWithoutPort() throws Throwable {
        MetricRegistry metrics = new MetricRegistry();
        HttpClientConfiguration config = new HttpClientConfiguration();
        ProxyConfiguration proxy = new ProxyConfiguration("192.168.52.11");
        config.setProxyConfiguration(proxy);

        CloseableHttpClient httpClient = checkProxy(new HttpClientBuilder(metrics), config, new HttpHost("dropwizard.io", 80), new HttpHost("192.168.52.11"));
        assertNotNull(httpClient);
    }

    @Test(timeout = 4000)
    public void usesProxyWithBasicAuth() throws Throwable {
        MetricRegistry metrics = new MetricRegistry();
        HttpClientConfiguration config = new HttpClientConfiguration();
        AuthConfiguration auth = new AuthConfiguration("secret", "stuff");
        ProxyConfiguration proxy = new ProxyConfiguration("192.168.52.11", 8080, "http", auth);
        config.setProxyConfiguration(proxy);

        CloseableHttpClient httpClient = checkProxy(new HttpClientBuilder(metrics), config, new HttpHost("dropwizard.io", 80), new HttpHost("http", "192.168.52.11", 8080));

        AuthScope authScope = new AuthScope("192.168.52.11", 8080);
        HttpContext httpContext = mock(HttpContext.class);
        Credentials creds = new UsernamePasswordCredentials("secret", "stuff".toCharArray());
        CredentialsProvider provider = (CredentialsProvider) getField(httpClient, "credentialsProvider");
        assertEquals(creds, provider.getCredentials(authScope, httpContext));
    }

    @Test(timeout = 4000)
    public void usesProxyWithNtlmAuth() throws Throwable {
        MetricRegistry metrics = new MetricRegistry();
        HttpClientConfiguration config = new HttpClientConfiguration();
        AuthConfiguration auth = new AuthConfiguration("secret", "stuff", "NTLM", "realm", "host", "domain", "NT");
        ProxyConfiguration proxy = new ProxyConfiguration("192.168.52.11", 8080, "http", auth);
        config.setProxyConfiguration(proxy);

        CloseableHttpClient httpClient = checkProxy(new HttpClientBuilder(metrics), config, new HttpHost("dropwizard.io", 80), new HttpHost("http", "192.168.52.11", 8080));

        AuthScope authScope = new AuthScope(null, "192.168.52.11", 8080, "realm", "NTLM");
        CredentialsProvider provider = (CredentialsProvider) getField(httpClient, "credentialsProvider");
        Credentials found = provider.getCredentials(authScope, mock(HttpContext.class));
        assertTrue(found instanceof NTCredentials);
        NTCredentials nt = (NTCredentials) found;
        assertArrayEquals("stuff".toCharArray(), nt.getPassword());
        assertEquals("DOMAIN\\secret", nt.getUserPrincipal().getName());
    }

    @Test(timeout = 4000)
    public void usesProxyWithNonProxyHosts() throws Throwable {
        MetricRegistry metrics = new MetricRegistry();
        HttpClientConfiguration config = new HttpClientConfiguration();
        ProxyConfiguration proxy = new ProxyConfiguration("192.168.52.11", 8080);
        proxy.setNonProxyHosts(Collections.singletonList("*.example.com"));
        config.setProxyConfiguration(proxy);

        CloseableHttpClient httpClient = checkProxy(new HttpClientBuilder(metrics), config, new HttpHost("host.example.com", 80), null);
        assertNotNull(httpClient);
    }

    @Test(timeout = 4000)
    public void usesProxyWithNonProxyHostsAndTargetDoesNotMatch() throws Throwable {
        MetricRegistry metrics = new MetricRegistry();
        HttpClientConfiguration config = new HttpClientConfiguration();
        ProxyConfiguration proxy = new ProxyConfiguration("192.168.52.11");
        proxy.setNonProxyHosts(Collections.singletonList("*.example.com"));
        config.setProxyConfiguration(proxy);

        CloseableHttpClient httpClient = checkProxy(new HttpClientBuilder(metrics), config, new HttpHost("dropwizard.io", 80), new HttpHost("192.168.52.11"));
        assertNotNull(httpClient);
    }

    @Test(timeout = 4000)
    public void usesNoProxy() throws Throwable {
        MetricRegistry metrics = new MetricRegistry();
        CloseableHttpClient httpClient = checkProxy(new HttpClientBuilder(metrics), new HttpClientConfiguration(), new HttpHost("dropwizard.io", 80), null);
        assertNotNull(httpClient);
    }

    private CloseableHttpClient checkProxy(HttpClientBuilder builder, HttpClientConfiguration config, HttpHost target, HttpHost expectedProxy) throws Throwable {
        CloseableHttpClient httpClient = builder.using(config).build("test");
        HttpRoutePlanner planner = (HttpRoutePlanner) getField(httpClient, "routePlanner");
        HttpRoute route;
        try {
            route = planner.determineRoute(target, new BasicHttpContext());
        } catch (HttpException e) {
            throw new RuntimeException(e);
        }
        assertEquals(expectedProxy, route.getProxyHost());
        assertEquals(target, route.getTargetHost());
        assertEquals(expectedProxy != null ? 2 : 1, route.getHopCount());
        return httpClient;
    }

    @Test(timeout = 4000)
    public void setValidateAfterInactivityPeriodFromConfiguration() throws Throwable {
        MetricRegistry metrics = new MetricRegistry();
        HttpClientConfiguration configuration = new HttpClientConfiguration();
        configuration.setValidateAfterInactivityPeriod(Duration.milliseconds(50000));
        HttpClientBuilder builder = new HttpClientBuilder(metrics);

        CloseableHttpClient client = builder.using(configuration).build("test");
        Resolver<HttpRoute, ConnectionConfig> resolver =
            (Resolver<HttpRoute, ConnectionConfig>) getNestedField(client, "connManager.connectionConfigResolver");
        ConnectionConfig cfg = resolver.resolve(new HttpRoute(HttpHost.create(URI.create("https://example.org:443"))));
        assertEquals(TimeValue.ofMilliseconds(50000), cfg.getValidateAfterInactivity());
    }

    @Test(timeout = 4000)
    public void usesACustomHttpClientMetricNameStrategy() throws Throwable {
        MetricRegistry metrics = new MetricRegistry();
        HttpClientBuilder builder = new HttpClientBuilder(metrics);
        Registry<ConnectionSocketFactory> registry = createDefaultRegistry();
        InstrumentedHttpClientConnectionManager manager = InstrumentedHttpClientConnectionManager.builder(metrics).socketFactoryRegistry(registry).build();
        org.apache.hc.client5.http.impl.classic.HttpClientBuilder apacheBuilder = org.apache.hc.client5.http.impl.classic.HttpClientBuilder.create();

        assertNotNull(builder.using(HttpClientMetricNameStrategies.HOST_AND_METHOD).createClient(apacheBuilder, manager, "test"));
        InstrumentedHttpRequestExecutor exec = (InstrumentedHttpRequestExecutor) getField(apacheBuilder, "requestExec");
        Object strategy = getField(exec, "metricNameStrategy");
        assertSame(HttpClientMetricNameStrategies.HOST_AND_METHOD, strategy);
    }

    @Test(timeout = 4000)
    public void usesMethodOnlyHttpClientMetricNameStrategyByDefault() throws Throwable {
        MetricRegistry metrics = new MetricRegistry();
        HttpClientBuilder builder = new HttpClientBuilder(metrics);
        Registry<ConnectionSocketFactory> registry = createDefaultRegistry();
        InstrumentedHttpClientConnectionManager manager = InstrumentedHttpClientConnectionManager.builder(metrics).socketFactoryRegistry(registry).build();
        org.apache.hc.client5.http.impl.classic.HttpClientBuilder apacheBuilder = org.apache.hc.client5.http.impl.classic.HttpClientBuilder.create();

        assertNotNull(builder.createClient(apacheBuilder, manager, "test"));
        InstrumentedHttpRequestExecutor exec = (InstrumentedHttpRequestExecutor) getField(apacheBuilder, "requestExec");
        Object strategy = getField(exec, "metricNameStrategy");
        assertSame(HttpClientMetricNameStrategies.METHOD_ONLY, strategy);
    }

    @Test(timeout = 4000)
    public void exposedConfigIsTheSameAsInternalToTheWrappedHttpClient() throws Throwable {
        MetricRegistry metrics = new MetricRegistry();
        HttpClientBuilder builder = new HttpClientBuilder(metrics);
        Registry<ConnectionSocketFactory> registry = createDefaultRegistry();
        InstrumentedHttpClientConnectionManager manager = InstrumentedHttpClientConnectionManager.builder(metrics).socketFactoryRegistry(registry).build();
        org.apache.hc.client5.http.impl.classic.HttpClientBuilder apacheBuilder = org.apache.hc.client5.http.impl.classic.HttpClientBuilder.create();

        ConfiguredCloseableHttpClient client = builder.createClient(apacheBuilder, manager, "test");
        assertNotNull(client);
        Object defaultConfigInternal = getField(client.getClient(), "defaultConfig");
        assertEquals(defaultConfigInternal, client.getDefaultRequestConfig());
    }

    @Test(timeout = 4000)
    public void disablesContentCompression() throws Throwable {
        MetricRegistry metrics = new MetricRegistry();
        HttpClientBuilder builder = new HttpClientBuilder(metrics);
        Registry<ConnectionSocketFactory> registry = createDefaultRegistry();
        InstrumentedHttpClientConnectionManager manager = InstrumentedHttpClientConnectionManager.builder(metrics).socketFactoryRegistry(registry).build();
        org.apache.hc.client5.http.impl.classic.HttpClientBuilder apacheBuilder = org.apache.hc.client5.http.impl.classic.HttpClientBuilder.create();

        ConfiguredCloseableHttpClient client = builder.disableContentCompression(true).createClient(apacheBuilder, manager, "test");
        assertNotNull(client);
        assertTrue((Boolean) getField(apacheBuilder, "contentCompressionDisabled"));
    }

    @Test(timeout = 4000)
    public void managedByEnvironment() throws Throwable {
        Environment environment = mock(Environment.class);
        when(environment.getName()).thenReturn("test-env");
        when(environment.metrics()).thenReturn(new MetricRegistry());
        LifecycleEnvironment lifecycle = mock(LifecycleEnvironment.class);
        when(environment.lifecycle()).thenReturn(lifecycle);

        CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
        HttpClientBuilder httpClientBuilder = spy(new HttpClientBuilder(environment));
        doReturn(new ConfiguredCloseableHttpClient(httpClient, RequestConfig.DEFAULT))
            .when(httpClientBuilder).buildWithDefaultRequestConfiguration("test-apache-client");

        assertSame(httpClient, httpClientBuilder.build("test-apache-client"));

        ArgumentCaptor<Managed> captor = ArgumentCaptor.forClass(Managed.class);
        verify(lifecycle).manage(captor.capture());
        Managed managed = captor.getValue();
        managed.stop();
        verify(httpClient).close();
    }

    @Test(timeout = 4000)
    public void usesACustomRedirectStrategy() throws Throwable {
        MetricRegistry metrics = new MetricRegistry();
        HttpClientBuilder builder = new HttpClientBuilder(metrics);
        RedirectStrategy never = new RedirectStrategy() {
            @Override
            public boolean isRedirected(org.apache.hc.core5.http.HttpRequest httpRequest, HttpResponse httpResponse, HttpContext httpContext) { return false; }
            @Override
            public URI getLocationURI(org.apache.hc.core5.http.HttpRequest request, HttpResponse response, HttpContext context) { return null; }
        };
        Registry<ConnectionSocketFactory> registry = createDefaultRegistry();
        InstrumentedHttpClientConnectionManager manager = InstrumentedHttpClientConnectionManager.builder(metrics).socketFactoryRegistry(registry).build();
        org.apache.hc.client5.http.impl.classic.HttpClientBuilder apacheBuilder = org.apache.hc.client5.http.impl.classic.HttpClientBuilder.create();

        ConfiguredCloseableHttpClient client = builder.using(never).createClient(apacheBuilder, manager, "test");
        assertNotNull(client);
        assertSame(never, getField(apacheBuilder, "redirectStrategy"));
    }

    @Test(timeout = 4000)
    public void usesDefaultHeaders() throws Throwable {
        MetricRegistry metrics = new MetricRegistry();
        HttpClientBuilder builder = new HttpClientBuilder(metrics);
        Registry<ConnectionSocketFactory> registry = createDefaultRegistry();
        InstrumentedHttpClientConnectionManager manager = InstrumentedHttpClientConnectionManager.builder(metrics).socketFactoryRegistry(registry).build();
        org.apache.hc.client5.http.impl.classic.HttpClientBuilder apacheBuilder = org.apache.hc.client5.http.impl.classic.HttpClientBuilder.create();

        ConfiguredCloseableHttpClient client = builder.using(Collections.singletonList(new BasicHeader(HttpHeaders.ACCEPT_LANGUAGE, "de")))
            .createClient(apacheBuilder, manager, "test");
        assertNotNull(client);

        @SuppressWarnings("unchecked")
        List<Header> headers = (List<Header>) getField(apacheBuilder, "defaultHeaders");
        assertEquals(1, headers.size());
        Header header = headers.get(0);
        assertEquals(HttpHeaders.ACCEPT_LANGUAGE, header.getName());
        assertEquals("de", header.getValue());
    }

    @Test(timeout = 4000)
    public void usesHttpProcessor() throws Throwable {
        MetricRegistry metrics = new MetricRegistry();
        HttpClientBuilder builder = new HttpClientBuilder(metrics);
        HttpProcessor httpProcessor = mock(HttpProcessor.class);
        Registry<ConnectionSocketFactory> registry = createDefaultRegistry();
        InstrumentedHttpClientConnectionManager manager = InstrumentedHttpClientConnectionManager.builder(metrics).socketFactoryRegistry(registry).build();
        org.apache.hc.client5.http.impl.classic.HttpClientBuilder apacheBuilder = org.apache.hc.client5.http.impl.classic.HttpClientBuilder.create();

        ConfiguredCloseableHttpClient client = builder.using(httpProcessor).createClient(apacheBuilder, manager, "test");
        assertNotNull(client);

        List<?> reqInts = (List<?>) getField(apacheBuilder, "requestInterceptors");
        List<?> respInts = (List<?>) getField(apacheBuilder, "responseInterceptors");
        assertEquals(1, reqInts.size());
        assertEquals(1, respInts.size());
    }

    @Test(timeout = 4000)
    public void allowsCustomBuilderConfiguration() throws Throwable {
        MetricRegistry metrics = new MetricRegistry();
        CustomBuilder cb = new CustomBuilder(metrics);
        assertFalse(cb.customized);

        Registry<ConnectionSocketFactory> registry = createDefaultRegistry();
        InstrumentedHttpClientConnectionManager manager = InstrumentedHttpClientConnectionManager.builder(metrics).socketFactoryRegistry(registry).build();
        org.apache.hc.client5.http.impl.classic.HttpClientBuilder apacheBuilder = org.apache.hc.client5.http.impl.classic.HttpClientBuilder.create();

        cb.createClient(apacheBuilder, manager, "test");
        assertTrue(cb.customized);
        assertTrue(getField(apacheBuilder, "requestExec") instanceof CustomRequestExecutor);
    }

    @Test(timeout = 4000)
    public void buildWithAnotherBuilder() throws Throwable {
        MetricRegistry metrics = new MetricRegistry();
        AnotherHttpClientBuilder another = spy(AnotherHttpClientBuilder.create());
        CustomBuilder cb = new CustomBuilder(metrics, another);

        CloseableHttpClient hc = cb.build("test");
        assertNotNull(hc);
        assertTrue(getField(another, "requestExec") instanceof CustomRequestExecutor);
    }

    @Test(timeout = 4000)
    public void configureCredentialReturnsNTCredentialsForNTLMConfig() throws Throwable {
        MetricRegistry metrics = new MetricRegistry();
        HttpClientBuilder builder = new HttpClientBuilder(metrics);

        Credentials creds = builder.configureCredentials(new AuthConfiguration("username", "password", "NTLM", "realm", "hostname", "domain", "NT"));
        assertTrue(creds instanceof NTCredentials);
        NTCredentials nt = (NTCredentials) creds;
        assertArrayEquals("password".toCharArray(), nt.getPassword());
        assertEquals("DOMAIN\\username", nt.getUserPrincipal().getName());
    }

    @Test(timeout = 4000)
    public void configureCredentialReturnsUserNamePasswordCredentialsForBasicConfig() throws Throwable {
        MetricRegistry metrics = new MetricRegistry();
        HttpClientBuilder builder = new HttpClientBuilder(metrics);

        Credentials creds = builder.configureCredentials(new AuthConfiguration("username", "password"));
        assertTrue(creds instanceof UsernamePasswordCredentials);
        UsernamePasswordCredentials up = (UsernamePasswordCredentials) creds;
        assertArrayEquals("password".toCharArray(), up.getUserPassword());
        assertEquals("username", up.getUserPrincipal().getName());
    }
}