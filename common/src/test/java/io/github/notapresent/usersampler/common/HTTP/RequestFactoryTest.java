package io.github.notapresent.usersampler.common.HTTP;

import static org.junit.Assert.assertEquals;

import java.util.Map;
import org.junit.Test;

public class RequestFactoryTest {

  @Test
  public void itShouldSetDefaultHeaders() {
    RequestFactory factory = new RequestFactory();

    Request request = factory.create("http://fake.url");
    Map<String, String> headers = request.getHeaders();

    assertEquals(
        "Mozilla/5.0 (Windows NT 6.1; Win64; x64) usersampler/1.0",
        headers.get("User-agent")
    );
  }

}