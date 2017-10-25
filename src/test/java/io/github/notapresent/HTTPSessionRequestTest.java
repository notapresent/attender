package io.github.notapresent;

import com.google.appengine.api.urlfetch.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mortbay.util.SingletonList;

import java.io.IOException;
import java.net.CookieManager;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class HTTPSessionRequestTest {
    private static URL url;

    @BeforeClass
    public static void setUpBeforeClass() throws MalformedURLException {
        url = new URL("http://fake.url");
    }

    @Test
    public void testFollowRedirect() throws IOException {
        HTTPSessionRequest req = new HTTPSessionRequest(url);
        assertTrue(req.getFollowRedirects());
        assertFalse(req.getFetchOptions().getFollowRedirects());
    }

    @Test
    public void testDoNotFollowRedirect() throws IOException {
        HTTPSessionRequest req = new HTTPSessionRequest(url, HTTPMethod.GET, FetchOptions.Builder.doNotFollowRedirects());
        assertFalse(req.getFollowRedirects());
        assertFalse(req.getFetchOptions().getFollowRedirects());
    }

    @Test
    public void testFetchOptionsSteCorrectly(){
        // all non-default options
        FetchOptions src = FetchOptions.Builder.withDefaults()
                .allowTruncate()
                .doNotFollowRedirects()
                .setDeadline(33.33)
                .validateCertificate();
        HTTPSessionRequest req = new HTTPSessionRequest(url , HTTPMethod.GET, src);
        FetchOptions dest = req.getFetchOptions();
        assertEquals(src.getAllowTruncate(), dest.getAllowTruncate());
        assertEquals(src.getDeadline(), dest.getDeadline());
        assertEquals(src.getValidateCertificate(), dest.getValidateCertificate());
    }

    @Test
    public void testMakeRedirectMakes() throws MalformedURLException {
        String location = "/newlocation";
        HTTPSessionRequest prev = new HTTPSessionRequest(url);
        HTTPResponse resp = TestUtil.makeRedirectResponse(302, location);
        HTTPSessionRequest req = HTTPSessionRequest.makeRedirect(prev, resp);

        URL expected = new URL(url, location);
        assertEquals(expected.toString(), req.getURL().toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRedirectThrows() throws IOException {
        HTTPSessionRequest req = new HTTPSessionRequest(url);
        HTTPResponse resp = TestUtil.makeResponse(302, "", Collections.singletonList(
                new HTTPHeader("location", null)));
        HTTPSessionRequest.makeRedirect(req, resp);
    }

}

