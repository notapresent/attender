package io.github.notapresent.usersampler.common.HTTP;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class RetryingSinglePlexerTest {
    private RequestMultiplexer plexer;

    @Mock
    private Session mockSession;
    private final Request request = new Request("http://fake.url");
    private final Response okResponse = new Response(200, "OK".getBytes(), "http://fake.url");

    @Before
    public void setUp() {
        initMocks(this);
         plexer = new RetryingSinglePlexer(mockSession);
    }

    @Test
    public void itShouldReturnFutureWithResponse() throws Exception {
        when(mockSession.send(any())).thenReturn(okResponse);

        Response resp = plexer.send(new Request("http://fake.url")).get();

        assertEquals(resp, okResponse);

    }

    @Test
    public void itShouldRetryFailedRequests() throws Exception {
        when(mockSession.send(any()))
                .thenThrow(new HTTPError("Fake error"))
                .thenReturn(okResponse);
        Response resp = plexer.send(request).get();

        verify(mockSession, times(2)).send(request);
        assertEquals(resp, okResponse);
    }

}