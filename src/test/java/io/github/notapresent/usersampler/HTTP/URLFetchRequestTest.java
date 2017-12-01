package io.github.notapresent.usersampler.HTTP;

import com.google.appengine.api.urlfetch.FetchOptions;
import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import org.junit.Before;
import org.junit.Test;

import java.net.URL;

import static org.junit.Assert.*;


public class URLFetchRequestTest {
    private URLFetchRequest request;

    private String url = "http://fake.url/";

    @Before
    public void setUp(){
        request = new URLFetchRequest(url, Method.GET);
    }

    @Test
    public void itShouldConvertToHTTPRequest() {
        HTTPRequest httpRequest = request.toHTTPRequest();
        assertEquals(request.getMethod().toString(), httpRequest.getMethod().toString());
        assertEquals(request.getUrl(), httpRequest.getURL().toString());
    }

    @Test
    public void itShouldSetFollowRedirectsIfDEFAULTPolicy() {
        request.setRedirectHandlingPolicy(Request.RedirectHandlingPolicy.DEFAULT);
        HTTPRequest req = request.toHTTPRequest();
        assertTrue(req.getFetchOptions().getFollowRedirects());
    }

    @Test
    public void itShouldSetDontFollowRedirectsIfNotDEFAULTPolicy() {
        request.setRedirectHandlingPolicy(Request.RedirectHandlingPolicy.FOLLOW);
        assertFalse(request.toHTTPRequest().getFetchOptions().getFollowRedirects());

        request.setRedirectHandlingPolicy(Request.RedirectHandlingPolicy.DO_NOT_FOLLOW);
        assertFalse(request.toHTTPRequest().getFetchOptions().getFollowRedirects());
    }

    @Test
    public void itShouldSetDeadlineToTimeout() {
        request.setTimeout(42.0);
        assertEquals((Double)request.getTimeout(), request.toHTTPRequest().getFetchOptions().getDeadline());
    }

    @Test
    public void itShouldBuildGetRequestWithGetMethod() {
        request = URLFetchRequest.GET(url);
        assertEquals(Method.GET, request.getMethod());
    }

    @Test
    public void itShouldUseDEFAULTRedirectPolicyByDefault() {
        request = URLFetchRequest.GET(url);
        assertEquals(request.getRedirectHandlingPolicy(), Request.RedirectHandlingPolicy.DEFAULT);
    }

    @Test
    public void itShouldRetainHeadersWhenConvertingToHTTPRequest() {
        request.getHeaders().put("foo", "bar");
        HTTPRequest httpRequest = request.toHTTPRequest();
        HTTPHeader header = httpRequest.getHeaders().get(0);
        assertEquals(header.getName(), "foo");
        assertEquals(header.getValue(), "bar");

    }
}
