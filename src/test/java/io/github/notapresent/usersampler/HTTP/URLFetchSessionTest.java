package io.github.notapresent.usersampler.HTTP;

import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.any;


public class URLFetchSessionTest {
    private URLFetchSession session;

    @Mock private URLFetchService mockURLFetch;

    // Canned requests and responses
    private static URL url;
    private static List<HTTPHeader> emptyHeaders =  new ArrayList<HTTPHeader>();
    private static HTTPResponse okHTTPResponse;


    @BeforeClass
    public static void setUpClass() throws IOException {
        url = new URL("http://fake.url");
        okHTTPResponse = new HTTPResponse(200, new byte[0], url, emptyHeaders);
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void itShouldRespondToRequestWithResponse() throws IOException {
        when(mockURLFetch.fetch(any(HTTPRequest.class))).thenReturn(okHTTPResponse);
        session = new URLFetchSession(mockURLFetch);

        URLFetchRequest req = URLFetchRequest.GET(url.toString());
        URLFetchResponse resp = session.send(req);
        assertEquals(resp.getFinalUrl(), url.toString());
    }
}
