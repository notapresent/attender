package io.github.notapresent.usersampler.HTTP;

import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;


public class URLFetchSessionTest {
    // Canned requests and responses
    private static String url = "http://fake.url";
    private static URLFetchRequest request = URLFetchRequest.GET(url);
    private static List<HTTPHeader> emptyHeaders = new ArrayList<HTTPHeader>();
    private static List<HTTPHeader> redirectHeaders = Arrays.asList(
            new HTTPHeader("location", url));
    private static List<HTTPHeader> setCookieHeaders = Arrays.asList(
            new HTTPHeader("set-cookie", "foo=bar"));
    private static HTTPResponse okResponse, redirectResponse,
            redirectResponseWithCookie;
    private URLFetchSession session;
    @Mock
    private URLFetchService mockURLFetch;

    @BeforeClass
    public static void setUpClass() throws IOException {
        URL urlObj = new URL(url);
        okResponse = new HTTPResponse(
                200,
                new byte[0],
                urlObj,
                emptyHeaders
        );
        redirectResponse = new HTTPResponse(
                302,
                new byte[0],
                new URL("http://other.url"),
                redirectHeaders
        );

        redirectResponseWithCookie = new HTTPResponse(
                302,
                new byte[0],
                urlObj,
                Lists.newArrayList(Iterables.concat(
                        redirectHeaders, setCookieHeaders)));
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        session = new URLFetchSession(mockURLFetch);
        request.setRedirectHandlingPolicy(Request.RedirectPolicy.FOLLOW);
    }

    @Test
    public void itShouldRespondToRequestWithResponse() throws IOException {
        when(mockURLFetch.fetch(any(HTTPRequest.class)))
                .thenReturn(okResponse);

        URLFetchResponse resp = session.send(request);

        assertEquals(resp.getFinalUrl(), url);
    }

    @Test
    public void itShouldFollowRedirects() throws IOException {
        when(mockURLFetch.fetch(any(HTTPRequest.class)))
                .thenReturn(redirectResponse, okResponse);

        URLFetchResponse resp = session.send(request);

        assertEquals(okResponse.getResponseCode(), resp.getStatus());
    }

    @Test
    public void itShouldSetFinalUrlAfterRedirect() throws IOException {
        when(mockURLFetch.fetch(any(HTTPRequest.class)))
                .thenReturn(redirectResponse, okResponse);

        URLFetchResponse resp = session.send(request);

        assertEquals(resp.getFinalUrl(), okResponse.getFinalUrl().toString());
    }

    @Test
    public void itSholudRetainHeadersOnRedirect() throws IOException {
        ArgumentCaptor<HTTPRequest> captor = ArgumentCaptor
                .forClass(HTTPRequest.class);
        when(mockURLFetch.fetch(any(HTTPRequest.class)))
                .thenReturn(redirectResponse, okResponse);
        request.getHeaders().put("foo", "bar");

        session.send(request);

        verify(mockURLFetch, times(2))
                .fetch(captor.capture());
        for (HTTPRequest req : captor.getAllValues()) {
            Map<String, String> headerMap = req.getHeaders().stream().collect(
                    Collectors.toMap(HTTPHeader::getName, HTTPHeader::getValue));
            assertTrue(headerMap.get("foo").equals("bar"));
        }
    }

    @Test
    public void itShouldNotFollowMoreThanMaxRedirects() throws IOException {
        when(mockURLFetch.fetch(any(HTTPRequest.class)))
                .thenReturn(redirectResponse, redirectResponse, redirectResponse);
        session.setMaxRedirects(2);

        session.send(request);

        verify(mockURLFetch, times(2))
                .fetch(any(HTTPRequest.class));
    }

    @Test
    public void itShouldRetainCookies() throws IOException {
        CookieHandler cookieManager = new URLFetchCookieManager();
        session.setCookieManager(cookieManager);
        ArgumentCaptor<HTTPRequest> captor = ArgumentCaptor
                .forClass(HTTPRequest.class);
        when(mockURLFetch.fetch(any(HTTPRequest.class)))
                .thenReturn(redirectResponseWithCookie, okResponse);

        session.send(request);

        verify(mockURLFetch, times(2))
                .fetch(captor.capture());
        String cookieHeader = captor
                .getAllValues()
                .get(1)
                .getHeaders()
                .stream()
                .filter((h) -> h.getName().equalsIgnoreCase("cookie"))
                .findFirst()
                .get()
                .getValue();
        assertTrue(cookieHeader.contains(setCookieHeaders.get(0).getValue()));
    }
}
