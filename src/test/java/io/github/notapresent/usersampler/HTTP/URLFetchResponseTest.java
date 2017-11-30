package io.github.notapresent.usersampler.HTTP;

import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPResponse;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;


public class URLFetchResponseTest {
    private URLFetchResponse response;

    private static String url = "http://fake.url/";
    private static List<HTTPHeader> headers =  Arrays.asList(
            new HTTPHeader("foo", "bar")
    );
    private static HTTPResponse okHTTPResponse;

    @BeforeClass
    public static void setUpClass() throws IOException {
        okHTTPResponse = new HTTPResponse(200, new byte[0], new URL(url), headers);
    }

    @Test
    public void itShouldBuildFromHTTPResponse() {
        response = URLFetchResponse.fromHTTPResponse(okHTTPResponse);
        assertEquals(okHTTPResponse.getResponseCode(), response.getStatus());
        assertEquals(okHTTPResponse.getFinalUrl().toString(), response.getFinalUrl());
        assertTrue(response.getHeaders().containsKey(headers.get(0).getName()));
        assertTrue(response.getHeaders().containsValue(headers.get(0).getValue()));
    }
}
