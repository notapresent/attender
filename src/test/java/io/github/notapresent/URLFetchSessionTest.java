package io.github.notapresent;

import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class URLFetchSessionTest {
    @Mock
    private URLFetchService mockService;
    @Mock
    private HTTPResponse mockResponse;

    private URLFetchSession session;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        session = new URLFetchSession(mockService);
        when(mockService.fetch(any(URL.class))).thenReturn(mockResponse);
    }

    @Test
    public void testFetchForwards() throws MalformedURLException, IOException {
        HTTPResponse resp = session.fetch(new URL("http://fake.url"));
        // assert service.fetch was called
        verify(mockService, times(1)).fetch(any(URL.class));
        // assert response returned
        assertSame(resp, mockResponse);
    }
}
