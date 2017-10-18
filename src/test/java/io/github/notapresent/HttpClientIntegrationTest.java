package io.github.notapresent;

import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalURLFetchServiceTestConfig;
import com.google.common.base.Charsets;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


public class HttpClientIntegrationTest {
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
        String html = new String(doRequest("http://httpbin.org/").getContent(), Charsets.UTF_8);

        assertThat(html)
                .named("Contents of http://httpbin.org/")
                .contains("httpbin(1): HTTP Request &amp; Response Service");
    }

    @Test(expected = HttpException.class)
    public void testRequestThrowsOnInvalidURL() throws HttpException {
        client.request("this is not a valid url");
    }

    @Test(expected = HttpException.class)
    public void testRequestThrowsOnHttpError() throws HttpException {
        try {
            client.request("https://httpbin.org/status/404");
        }
        catch(HttpException e) {
            assertEquals(404, e.getResponseCode());
            throw e;
        }

    }

    @Test
    public void testRedirectOff() {
        client.setFollowRedirects(false);
        HTTPResponse resp = client.request("http://httpbin.org/redirect/1");
        assertEquals(302, resp.getResponseCode());
    }

    @Test
    public void testRedirectOn() {
        HTTPResponse resp = client.request("http://httpbin.org/redirect/1");
        assertEquals(200, resp.getResponseCode());
    }

    @Test
    public void testMaxRedirects() {
        HTTPResponse resp = client.request("http://httpbin.org/redirect/6");
        assertEquals(302, resp.getResponseCode());
    }

    private HTTPResponse doRequest(String urlStr) {
        try {
            return client.request(urlStr);
        }
        catch (HttpException e) {
            fail(e.getMessage());
        }
        return null;
    }

}