package io.github.notapresent;

import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalURLFetchServiceTestConfig;
import com.google.common.base.Charsets;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookieStore;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


public class HttpClientIntegrationTest {
    public static final String HTTPBIN = "http://eu.httpbin.org/";
    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalURLFetchServiceTestConfig());
    private HttpClient client;

    @Before
    public void setUp() {
        helper.setUp();
        client = new HttpClient(URLFetchServiceFactory.getURLFetchService());
    }

    @After
    public void tearDown() {
        client = null;
        helper.tearDown();
    }

    @Test
    public void testRequestGetsContent() {
        String html = new String(client.request(HTTPBIN).getContent(), Charsets.UTF_8);

        assertThat(html)
                .named("Contents of " + HTTPBIN)
                .contains("httpbin(1): HTTP Request &amp; Response Service");
    }

    @Test(expected = HttpException.class)
    public void testRequestThrowsOnInvalidURL() throws HttpException {
        client.request("this is not a valid url");
    }

    @Test(expected = HttpException.class)
    public void testRequestThrowsOnHttpError() throws HttpException {
        try {
            client.request(HTTPBIN + "status/404");
        }
        catch(HttpException e) {
            assertEquals(404, e.getResponseCode());
            throw e;
        }
    }

    @Test
    public void testRedirectOff() {
        client.setFollowRedirects(false);
        HTTPResponse resp = client.request(HTTPBIN + "/redirect/1");
        assertEquals(302, resp.getResponseCode());
    }

    @Test
    public void testRedirectOn() {
        HTTPResponse resp = client.request(HTTPBIN +"/redirect/1");
        assertEquals(200, resp.getResponseCode());
    }

    @Test
    public void testMaxRedirects() {
        HTTPResponse resp = client.request(HTTPBIN + "redirect/6");
        assertEquals(302, resp.getResponseCode());
    }

    @Test
    public void testCookiePersistence() {
        URLFetchCookieManager cm = new URLFetchCookieManager();
        client.setCookieManager(cm);
        HTTPResponse resp = client.request(HTTPBIN + "cookies/set?k2=v2&k1=v1");
        String html = new String(resp.getContent(), Charsets.UTF_8);

        assertEquals(200, resp.getResponseCode());
        assertThat(html).named("response").contains("\"k1\": \"v1\"");
        assertThat(html).named("response").contains("\"k2\": \"v2\"");
    }
}
