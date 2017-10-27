package io.github.notapresent;

import com.google.appengine.api.urlfetch.HTTPHeader;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class HTTPUtilTest {
    private static URL url;

    @BeforeClass
    public static void setUpBeforeClass() throws MalformedURLException {
        url = new URL("http://fake.url");
    }

    @Test
    public void testIsRedirect() {
        int[] redirectCodes = {301, 302, 303};
        int[] nonRedirectCodes = {200, 404, 500};
        for (int redirectCode : redirectCodes) {
            assertTrue(HTTPUtil.isRedirect(redirectCode));
        }
        for (int nonRedirectCode : nonRedirectCodes) {
            assertFalse(HTTPUtil.isRedirect(nonRedirectCode));
        }
    }

    @Test
    public void testGetHeaderReturnsHeader() {
        List<HTTPHeader> headers = Collections.singletonList(
                new HTTPHeader("Name", "Value"));
        assertEquals("Value", HTTPUtil.getHeader(headers, "Name"));
    }

    @Test
    public void testGetHeaderReturnsNull() {
        List<HTTPHeader> headers = new LinkedList<>();
        assertEquals(null, HTTPUtil.getHeader(headers, "Anything"));
    }

}

