package io.github.notapresent;

import com.google.appengine.api.urlfetch.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.net.CookieManager;
import java.net.URL;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;


public class HTTPSessionTest {
    @Mock
    private URLFetchService mockService;
    private CookieManager cookieManager;
    private HTTPSession session;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        cookieManager = new CookieManager();
        session = new HTTPSession(mockService, cookieManager);
    }

    @Test
    public void testFetchForwards() throws IOException {
        HTTPResponse resp = TestUtil.makeResponse(200, "");
        when(mockService.fetch(any(HTTPSessionRequest.class))).thenReturn(resp);

        HTTPResponse realResp = session.fetch(new URL("http://fake.url"));

        verify(mockService, times(1)).fetch(any(HTTPSessionRequest.class));
        assertSame(resp, realResp);
    }

    @Test
    public void testRedirectRedirects() throws IOException {
        HTTPResponse redirResp = TestUtil.makeRedirectResponse(302, "/blah");
        HTTPResponse okResp = TestUtil.makeResponse(200, "");
        when(mockService.fetch(any(HTTPSessionRequest.class))).thenReturn(redirResp, okResp);

        HTTPResponse realResp = session.fetch(new URL("http://fake.url"));

        assertSame(realResp, okResp);
        verify(mockService, times(2)).fetch(any(HTTPSessionRequest.class));
    }
}
