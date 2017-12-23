package io.github.notapresent.usersampler.common.http;

import static java.net.HttpURLConnection.HTTP_OK;
import static org.junit.Assert.*;

import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

public class ResponseTest {
  private static final String url = "http://example.com/";
  private static final byte[] body = ("Ok").getBytes();

  @Test
  public void headersShouldBeCaseInsensitive() {
    Map<String, String> headers  = new ImmutableMap.Builder<String, String>()
    .put("Header-name", "header-value")
    .build();
    Response response = new Response(HTTP_OK, headers, body, url);

    assertTrue(response.getHeaders().containsKey("header-name"));
    assertTrue(response.getHeaders().containsKey("HEADER-NAME"));
    assertTrue(response.getHeaders().containsKey("Header-name"));
  }

}