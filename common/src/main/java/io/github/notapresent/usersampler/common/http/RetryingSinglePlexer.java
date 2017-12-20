package io.github.notapresent.usersampler.common.http;

import com.google.common.util.concurrent.Futures;
import com.google.inject.Inject;
import java.util.concurrent.Future;

public class RetryingSinglePlexer implements RequestMultiplexer {

  private static final int MAX_RETRIES = 3;
  private final Session session;

  @Inject
  public RetryingSinglePlexer(Session session) {
    this.session = session;
  }

  @Override
  public Future<Response> send(Request request) {
    int tries = 0;
    while (true) {
      try {
        Response response = session.send(request);
        return Futures.immediateFuture(response);
      } catch (HttpError e) {
        if (tries++ > MAX_RETRIES) {
          throw new HttpError("Giving up " + request + " after " + tries + " tries");
        }
      }
    }
  }

}
