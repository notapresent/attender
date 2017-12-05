package io.github.notapresent.usersampler.gaeapp.HTTP;

import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPRequest;
import io.github.notapresent.usersampler.common.HTTP.Method;
import io.github.notapresent.usersampler.common.HTTP.Request;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


public class URLFetchRequestTest {
    private URLFetchRequest request;

    private String url = "http://fake.url/";

    @Before
    public void setUp() {
        request = new URLFetchRequest(url, Method.GET);
    }

    @Test
    public void itShouldConvertToHTTPRequest() {
        HTTPRequest httpRequest = request.toHTTPRequest();
        assertEquals(request.getMethod().toString(),
                httpRequest.getMethod().toString());
        assertEquals(request.getUrl(), httpRequest.getURL().toString());
    }

    @Test
    public void itShouldSetFollowRedirectsIfDEFAULTPolicy() {
        request.setRedirectHandlingPolicy(Request.RedirectPolicy.DEFAULT);
        HTTPRequest req = request.toHTTPRequest();
        assertTrue(req.getFetchOptions().getFollowRedirects());
    }

    @Test
    public void itShouldSetDontFollowRedirectsIfNotDEFAULTPolicy() {
        request.setRedirectHandlingPolicy(Request.RedirectPolicy.FOLLOW);
        assertFalse(request.toHTTPRequest().getFetchOptions().getFollowRedirects());

        request.setRedirectHandlingPolicy(Request.RedirectPolicy.DO_NOT_FOLLOW);
        assertFalse(request.toHTTPRequest().getFetchOptions().getFollowRedirects());
    }

    @Test
    public void itShouldSetDeadlineToTimeout() {
        request.setTimeout(42.0);
        assertEquals((Double) request.getTimeout(),
                request.toHTTPRequest().getFetchOptions().getDeadline());
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
