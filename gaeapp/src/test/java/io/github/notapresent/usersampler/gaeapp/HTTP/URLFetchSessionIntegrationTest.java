package io.github.notapresent.usersampler.gaeapp.HTTP;

import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalURLFetchServiceTestConfig;
import io.github.notapresent.usersampler.common.HTTP.Request;
import io.github.notapresent.usersampler.common.HTTP.Response;
import io.github.notapresent.usersampler.gaeapp.IntegrationTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertEquals;

@Category(IntegrationTest.class)
public class URLFetchSessionIntegrationTest {
    private static final String HTTPBIN = "http://httpbin.org";
    //private static final URLFetchRequestFactory requestFactory = new URLFetchRequestFactory();
    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalURLFetchServiceTestConfig());
    private URLFetchSession session;
    private URLFetchCookieManager cookieManager = new URLFetchCookieManager();
    private URLFetchRequest request;
    private Response response;

    @Before
    public void setUp() {
        helper.setUp();
        session = new URLFetchSession(
                URLFetchServiceFactory.getURLFetchService(), cookieManager);
    }

    @After
    public void tearDown() {
        session = null;
        helper.tearDown();
    }

    @Test
    public void itShouldFetchDocument() throws IOException {
        request = new URLFetchRequest(HTTPBIN + "/ip");
        response = session.send(request);
        assertThat(response.getContentString()).contains("origin");
    }


    @Test
    public void ifShouldNotFollowRedirectsIfPolicySaysSo() throws IOException {
        request = new URLFetchRequest(HTTPBIN + "/redirect/2");
        request.setRedirectHandlingPolicy(Request.RedirectPolicy.DO_NOT_FOLLOW);
        response = session.send(request);
        assertEquals(302, response.getStatus());
    }

    @Test
    public void itShouldFollowRedirectsIfPolisySaysSo() throws IOException {
        request = new URLFetchRequest(HTTPBIN + "/redirect/2");
        request.setRedirectHandlingPolicy(Request.RedirectPolicy.FOLLOW);
        response = session.send(request);
        assertEquals(200, response.getStatus());
    }

    @Test
    public void itShouldRetainCookiesAfterRedirect() throws IOException {
        request = new URLFetchRequest(HTTPBIN + "/cookies/set?k2=v2&k1=v1");
        request.setRedirectHandlingPolicy(Request.RedirectPolicy.FOLLOW);
        response = session.send(request);
        String body = response.getContentString();
        assertThat(body).containsMatch("\"k1\":\\s+\"v1\"");
        assertThat(body).containsMatch("\"k2\":\\s+\"v2\"");
    }

    @Test
    public void itShouldRetainsHeadersOnRedirect() throws IOException {
        request = new URLFetchRequest(HTTPBIN + "/redirect-to?url=/headers");
        request.getHeaders().put("foo", "bar");
        response = session.send(request);
        String body = response.getContentString();
        assertThat(body).containsMatch("(?i)\"foo\":\\s+\"bar\"");
    }
}
