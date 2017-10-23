package io.github.notapresent;

import com.google.appengine.api.urlfetch.*;
import com.google.appengine.repackaged.com.google.api.client.http.HttpResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
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
        HTTPResponse mockResp = makeMockResponse();
        when(mockService.fetch(any(HTTPRequest.class))).thenReturn(mockResp);

        HTTPResponse resp = session.fetch(new URL("http://fake.url"));
        verify(mockService, times(1)); // .fetch(any(URL.class));
        assertSame(resp, mockResp);
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

    @Test
    public void testRedirectRedirects() throws IOException {
        List<HTTPHeader> redirHdrs = Collections.singletonList(
                new HTTPHeader("location", "http://redir.url"));
        HTTPResponse redirResp = makeMockResponse("Redirecting", 302, redirHdrs);
        HTTPResponse okResp = makeMockResponse();
        when(mockService.fetch(any(HTTPRequest.class))).thenReturn(redirResp, okResp);

        HTTPResponse resp = session.fetch(new URL("http://fake.url"));
        assertSame(resp, okResp);
        verify(mockService, times(2)).fetch(any(HTTPRequest.class));
    }

    private HTTPResponse makeMockResponse(String content, int statusCode, List<HTTPHeader> headers) {
        HTTPResponse rv = mock(HTTPResponse.class);
        when(rv.getResponseCode()).thenReturn(statusCode);
        when(rv.getContent()).thenReturn(content.getBytes());
        when(rv.getHeaders()).thenReturn(headers == null ? new ArrayList<>() : headers);
        return rv;
    }

    private HTTPResponse makeMockResponse() {
        return makeMockResponse("Dummy vontent", 200, null);
    }
}
