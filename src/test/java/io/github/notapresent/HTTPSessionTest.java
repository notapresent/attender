package io.github.notapresent;

import com.google.appengine.api.urlfetch.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;


public class HTTPSessionTest {
    private static URL url;

    private HTTPSessionCookieManager cookieManager;
    private HTTPSession session;
    @Mock
    private URLFetchService mockService;

    @BeforeClass
    public static void setUpBeforeClass() throws MalformedURLException {
        url = new URL("http://fake.url");
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        cookieManager = new HTTPSessionCookieManager();
        session = new HTTPSession(mockService, cookieManager);
    }

    @Test
    public void testFetchForwards() throws IOException {
        HTTPResponse resp = TestUtil.makeResponse(200, "");
        when(mockService.fetch(any(HTTPSessionRequest.class))).thenReturn(resp);

        HTTPResponse realResp = session.fetch(url);

        verify(mockService, times(1)).fetch(any(HTTPSessionRequest.class));
        assertSame(resp, realResp);
    }

    @Test
    public void testRedirectRedirects() throws IOException {
        HTTPResponse redirResp = TestUtil.makeRedirectResponse(302, "/blah");
        HTTPResponse okResp = TestUtil.makeResponse(200, "");
        when(mockService.fetch(any(HTTPSessionRequest.class))).thenReturn(redirResp, okResp);

        HTTPResponse realResp = session.fetch(url);

        assertSame(realResp, okResp);
        verify(mockService, times(2)).fetch(any(HTTPSessionRequest.class));
    }

    @Test
    public void testSessionSavesCookies() throws IOException, URISyntaxException {
        List<HTTPHeader> setCookieHdrs = Collections.singletonList(
                new HTTPHeader("set-cookie", "k1=v1; Path=/"));
        HTTPResponse resp = TestUtil.makeResponse(200, "", setCookieHdrs);
        when(mockService.fetch(any(HTTPSessionRequest.class))).thenReturn(resp);

        session.fetch(url);

        Map<String, List<String>> storedHeaders = cookieManager.get(url.toURI(), new HashMap<>());
        assertThat(storedHeaders).hasSize(1);
        String cookieHeader = String.join("", storedHeaders.values().iterator().next());
        assertThat(cookieHeader).contains("k1");
        assertThat(cookieHeader).contains("v1");
    }

    @Test
    public void testSessionLoadsCookies() throws Exception {
        HTTPRequest req;
        Map<String, List<String>> cookieHeaders = new HashMap<>();
        cookieHeaders.put("set-cookie", Collections.singletonList("k1=v1; Path=/"));
        cookieManager.put(url.toURI(), cookieHeaders);
        ArgumentCaptor<HTTPSessionRequest> requestCaptor = ArgumentCaptor.forClass(HTTPSessionRequest.class);
        when(mockService.fetch(any(HTTPSessionRequest.class))).thenReturn(TestUtil.makeResponse(200, ""));

        session.fetch(url);

        verify(mockService, times(1)).fetch(requestCaptor.capture());
        String cookieHeader = HTTPUtil.getHeader(requestCaptor.getValue().getHeaders(), "cookie");
        assertThat(cookieHeader).contains("k1");
        assertThat(cookieHeader).contains("v1");
    }

    @Test
    public void testSessionRetainsHeaders() throws IOException {
        HTTPResponse redirResp = TestUtil.makeRedirectResponse(302, "/blah");
        HTTPResponse okResp = TestUtil.makeResponse(200, "");
        HTTPHeader testHeader = new HTTPHeader("some-header", "some-value");

        ArgumentCaptor<HTTPSessionRequest> requestCaptor = ArgumentCaptor.forClass(HTTPSessionRequest.class);
        when(mockService.fetch(any(HTTPSessionRequest.class))).thenReturn(redirResp, okResp);
        HTTPSessionRequest req = new HTTPSessionRequest(url, HTTPMethod.GET);
        req.addHeader(testHeader);

        session.fetch(req);

        verify(mockService, times(2)).fetch(requestCaptor.capture());
        HTTPSessionRequest request2 = requestCaptor.getAllValues().get(1);
        List<HTTPHeader> headers = request2.getHeaders();
        assertThat(HTTPUtil.getHeader(request2.getHeaders(), "some-header")).isEqualTo("some-value");
    }


}

