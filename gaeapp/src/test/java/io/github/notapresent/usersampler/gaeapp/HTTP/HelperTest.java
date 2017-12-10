package io.github.notapresent.usersampler.gaeapp.HTTP;

import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPRequest;
import io.github.notapresent.usersampler.common.HTTP.Request;
import org.junit.Test;

import static org.junit.Assert.*;

public class HelperTest {
    private String urlStr = "http://fake.url/";
    private Request request = new Request(urlStr);

    @Test
    public void itShouldConvertToHTTPRequest() {
        HTTPRequest httpRequest = Helper.createHTTPRequest(request);

        assertEquals(request.getMethod().toString(),
                httpRequest.getMethod().toString());
        assertEquals(request.getUrl(), httpRequest.getURL().toString());
    }

    @Test
    public void itShouldSetFollowRedirectsIfDEFAULTPolicy() {
        request.setRedirectHandlingPolicy(Request.RedirectPolicy.DEFAULT);

        HTTPRequest httpRequest = Helper.createHTTPRequest(request);

        assertTrue(httpRequest.getFetchOptions().getFollowRedirects());
    }

    @Test
    public void itShouldSetDontFollowRedirectsIfNotDEFAULTPolicy() {
        request.setRedirectHandlingPolicy(Request.RedirectPolicy.FOLLOW);
        assertFalse(Helper.createHTTPRequest(request).getFetchOptions().getFollowRedirects());

        request.setRedirectHandlingPolicy(Request.RedirectPolicy.DO_NOT_FOLLOW);
        assertFalse(Helper.createHTTPRequest(request).getFetchOptions().getFollowRedirects());
    }

    @Test
    public void itShouldSetDeadlineToTimeout() {
        request.setTimeout(42.0);

        HTTPRequest httpRequest = Helper.createHTTPRequest(request);

        assertEquals((Double) request.getTimeout(), httpRequest.getFetchOptions().getDeadline());
    }

    @Test
    public void itShouldRetainHeadersWhenConvertingToHTTPRequest() {
        request.getHeaders().put("foo", "bar");

        HTTPRequest httpRequest = Helper.createHTTPRequest(request);

        HTTPHeader header = httpRequest.getHeaders().get(0);
        assertEquals(header.getName(), "foo");
        assertEquals(header.getValue(), "bar");
    }
}