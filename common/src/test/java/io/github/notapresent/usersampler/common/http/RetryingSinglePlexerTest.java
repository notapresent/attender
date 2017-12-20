package io.github.notapresent.usersampler.common.http;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

public class RetryingSinglePlexerTest {

  private final Request request = new Request("http://fake.url");
  private final Response okResponse = new Response(200, "OK".getBytes(), "http://fake.url");
  private RequestMultiplexer plexer;
  @Mock
  private Session mockSession;

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
        .thenThrow(new HttpError("Fake error"))
        .thenReturn(okResponse);
    Response resp = plexer.send(request).get();

    verify(mockSession, times(2)).send(request);
    assertEquals(resp, okResponse);
  }

}