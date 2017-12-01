package io.github.notapresent.usersampler.HTTP;

import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.any;


public class URLFetchSessionTest {
    private URLFetchSession session;

    @Mock private URLFetchService mockURLFetch;

    // Canned requests and responses
    private static String url = "http://fake.url";
    private static URLFetchRequest request = URLFetchRequest.GET(url);
    private static List<HTTPHeader> emptyHeaders =  new ArrayList<HTTPHeader>();
    private static List<HTTPHeader> redirectHeaders = Arrays.asList(
            new HTTPHeader("location","http://other.url"));
    private static HTTPResponse okResponse, redirectResponse;


    @BeforeClass
    public static void setUpClass() throws IOException {
        URL urlObj = new URL(url);
        okResponse = new HTTPResponse(200, new byte[0], urlObj, emptyHeaders);
        redirectResponse = new HTTPResponse(302, new byte[0], new URL("http://other.url"), redirectHeaders);
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        session = new URLFetchSession(mockURLFetch);
        request.setRedirectHandlingPolicy(Request.RedirectHandlingPolicy.FOLLOW);

    }

    @Test
    public void itShouldRespondToRequestWithResponse() throws IOException {
        when(mockURLFetch.fetch(any(HTTPRequest.class))).thenReturn(okResponse);
        URLFetchRequest req = URLFetchRequest.GET(url);
        URLFetchResponse resp = session.send(req);
        assertEquals(resp.getFinalUrl(), url);
    }

    @Test
    public void itShouldFollowRedirects() throws IOException {
        when(mockURLFetch.fetch(any(HTTPRequest.class))).thenReturn(redirectResponse, okResponse);
        URLFetchResponse resp = session.send(request);
        assertEquals(okResponse.getResponseCode(), resp.getStatus());
    }

    @Test
    public void itShouldSetFinalUrlAfterRedirect() throws IOException {
        when(mockURLFetch.fetch(any(HTTPRequest.class))).thenReturn(redirectResponse, okResponse);
        URLFetchResponse resp = session.send(request);
        assertEquals(resp.getFinalUrl(), okResponse.getFinalUrl().toString());
    }

    @Test
    public void itSholudRetainHeadersOnRedirect() throws IOException {
        ArgumentCaptor<HTTPRequest> requestCaptor = ArgumentCaptor.forClass(HTTPRequest.class);
        when(mockURLFetch.fetch(any(HTTPRequest.class))).thenReturn(redirectResponse, okResponse);
        request.getHeaders().put("foo", "bar");
        URLFetchResponse resp = session.send(request);
        verify(mockURLFetch, times(2)).fetch(requestCaptor.capture());
        for(HTTPRequest req : requestCaptor.getAllValues()) {
            Map<String, String> headerMap = req.getHeaders().stream().collect(
                    Collectors.toMap(HTTPHeader::getName, HTTPHeader::getValue));
            assertTrue(headerMap.get("foo").equals("bar"));
        }
    }

    @Test
    public void itShouldNotFollowMoreThanMaxRedirects() {

    }
}
