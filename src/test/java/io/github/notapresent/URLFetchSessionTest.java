package io.github.notapresent;

import com.google.appengine.api.urlfetch.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;


public class URLFetchSessionTest {
    @Mock
    private URLFetchService mockService;

    private URLFetchSession session;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        session = new URLFetchSession(mockService);
    }

    @Test
    public void testFetchForwards() throws IOException {
        HTTPResponse resp = makeResponse(200, "");
        when(mockService.fetch(any(HTTPRequest.class))).thenReturn(resp);

        HTTPResponse realResp = session.fetch(new URL("http://fake.url"));

        verify(mockService, times(1)).fetch(any(HTTPRequest.class));
        assertSame(resp, realResp);
    }

    @Test
    public void testCopyFetchOptions(){
        // all non-default options
        FetchOptions src = FetchOptions.Builder.withDefaults()
                .allowTruncate()
                .doNotFollowRedirects()
                .setDeadline(33.33)
                .validateCertificate();
        FetchOptions dest = URLFetchSession.copyFetchOptions(src);
        assertEquals(src.getFollowRedirects(), dest.getFollowRedirects());
        assertEquals(src.getAllowTruncate(), dest.getAllowTruncate());
        assertEquals(src.getDeadline(), dest.getDeadline());
        assertEquals(src.getValidateCertificate(), dest.getValidateCertificate());
    }

    @Test public void testGetHeaderReturnsHeader() {
        List<HTTPHeader> headers = Collections.singletonList(
                new HTTPHeader("Name", "Value"));
        assertEquals("Value", URLFetchSession.getHeader(headers, "Name"));
    }

    @Test public void testGetHeaderReturnsNull() {
        List<HTTPHeader> headers = new LinkedList<>();
        assertEquals(null, URLFetchSession.getHeader(headers, "Anything"));
    }

    @Test
    public void testRedirectRedirects() throws IOException {
        HTTPResponse redirResp = makeRedirectResponse(302, "");
        HTTPResponse okResp = makeResponse(200, "");
        when(mockService.fetch(any(HTTPRequest.class))).thenReturn(redirResp, okResp);

        HTTPResponse realResp = session.fetch(new URL("http://fake.url"));

        assertSame(realResp, okResp);
        verify(mockService, times(2)).fetch(any(HTTPRequest.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMakeRedirectRequestThrows() throws IOException {
        URL url = new URL("http://fake.url");
        HTTPResponse resp = new HTTPResponse(302,
                new byte[1], url, new LinkedList<HTTPHeader>() );
        URLFetchSession.makeRedirectRequest(url, resp);
    }

    @Test
    public void testMakeRedirectRequestBuildsURL() throws IOException {
        URL url = new URL("http://fake.url");
        List<HTTPHeader> redirHdrs = Collections.singletonList(
                new HTTPHeader("location", "http://redir.url"));
        HTTPResponse resp = new HTTPResponse(302,
                new byte[1], null, redirHdrs);
        HTTPRequest req = URLFetchSession.makeRedirectRequest(url, resp);
        assertEquals("http://redir.url", req.getURL().toString());
    }

    @Test
    public void testIsRedirect() {
        int[] redirectCodes = {301, 302, 303 };
        int[] nonRedirectCodes = {200, 404, 500};
        for(int i=0; i < redirectCodes.length; i++) {
            assertTrue(URLFetchSession.isRedirect(redirectCodes[i]));
        }
        for(int i=0; i < nonRedirectCodes.length; i++) {
            assertFalse(URLFetchSession.isRedirect(nonRedirectCodes[i]));
        }
    }

    private static HTTPResponse makeResponse(int code, String content, List<HTTPHeader> headers) {
        return new HTTPResponse(code, content.getBytes(), null, headers);
    }
    private static HTTPResponse makeResponse(int code, String content) {
        return makeResponse(code, content, new LinkedList<>());
    }
    private static HTTPResponse makeRedirectResponse(int code, String location) {
        List<HTTPHeader> headers = Collections.singletonList(
                new HTTPHeader("location", location));
        return makeResponse(code, "", headers);
    }
}
