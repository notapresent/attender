package io.github.notapresent.usersampler.gaeapp.HTTP;

import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import io.github.notapresent.usersampler.common.HTTP.Request;
import io.github.notapresent.usersampler.common.HTTP.Response;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import static io.github.notapresent.usersampler.gaeapp.HTTP.Helper.URL2URI;
import static io.github.notapresent.usersampler.gaeapp.HTTP.Helper.isCookieHeader;
import static org.junit.Assert.*;

public class HelperTest {
    private String urlStr = "http://fake.url/";
    private Request request = new Request(urlStr);
    private Response response;

    private static List<HTTPHeader> headers = Arrays.asList(
            new HTTPHeader("foo", "bar")
    );

    @Test
    public void itShouldBuildFromHTTPResponse() throws MalformedURLException {
        HTTPResponse okHTTPResponse = new HTTPResponse(
                200,
                new byte[0],
                new URL(urlStr),
                headers
        );

        response = Helper.createResponse(okHTTPResponse, "http://someother.url");
        assertEquals(okHTTPResponse.getResponseCode(), response.getStatus());
        assertEquals(okHTTPResponse.getFinalUrl().toString(), response.getFinalUrl());
        assertTrue(response.getHeaders().containsKey(headers.get(0).getName()));
        assertTrue(response.getHeaders().containsValue(headers.get(0).getValue()));
    }

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

    @Test
    public void URL2URIShouldConvertURLToURI() throws Exception {
        String urlStr = "https://host.com/..path/file?q=e&q1=e1#frag";
        URL url = new URL(urlStr);
        assertEquals(urlStr, URL2URI(url).toString());
    }

    @Test
    public void isCookieHeaderShouldReturnTrueOnSetCookie() {
        assertTrue(isCookieHeader("Set-Cookie"));
        assertTrue(isCookieHeader("Set-Cookie2"));
    }

    @Test
    public void isCookieHeaderShouldIgnoreCase() {
        assertTrue(isCookieHeader("SeT-CoOkiE"));
    }
}