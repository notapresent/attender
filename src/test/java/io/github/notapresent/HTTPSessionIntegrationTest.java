package io.github.notapresent;

import com.google.appengine.api.urlfetch.*;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalURLFetchServiceTestConfig;
import com.google.common.base.Charsets;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.net.CookieManager;
import java.net.MalformedURLException;
import java.net.URL;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertEquals;

@Category(IntegrationTest.class)
public class HTTPSessionIntegrationTest {
    private static final String HTTPBIN = "http://httpbin.org";
    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalURLFetchServiceTestConfig());
    private HTTPSession session;
    private CookieManager cookieManager;

    @Before
    public void setUp() {
        helper.setUp();
        cookieManager = new CookieManager();
        session = new HTTPSession(URLFetchServiceFactory.getURLFetchService(), cookieManager);
    }

    @After
    public void tearDown() {
        session = null;
        helper.tearDown();
    }

    @Test
    public void testSimpleFetch() throws MalformedURLException, IOException {
        String html = new String(session.fetch(new URL(HTTPBIN + "/ip")).getContent(), Charsets.UTF_8);
        assertThat(html).named("httpbin response").contains("origin");
    }

    @Test
    public void testRedirectsOff() throws IOException {
        HTTPSessionRequest req = new HTTPSessionRequest(
                new URL(HTTPBIN + "/redirect/1"), HTTPMethod.GET,
                FetchOptions.Builder.doNotFollowRedirects());
        HTTPResponse resp = session.fetch(req);
        assertEquals(302, resp.getResponseCode());
    }

    @Test
    public void testRedirectsOn() throws IOException {
        HTTPSessionRequest req = new HTTPSessionRequest(
                new URL(HTTPBIN + "/redirect/3"), HTTPMethod.GET,
                FetchOptions.Builder.followRedirects());
        HTTPResponse resp = session.fetch(req);
        assertEquals(200, resp.getResponseCode());
        // assertEquals(HTTPBIN + "/get", resp.getFinalUrl()); // TODO
    }
}
