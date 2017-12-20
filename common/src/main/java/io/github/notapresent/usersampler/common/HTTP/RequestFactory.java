package io.github.notapresent.usersampler.common.HTTP;

import com.google.common.collect.ImmutableMap;
import java.util.Map;

public class RequestFactory {

  private final Map<String, String> defaultHeaders = ImmutableMap.of(
      "User-agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) usersampler/1.0"
  );

  private Request create(String url, Method method) {
    Request request = new Request(url, method);
    request.getHeaders().putAll(defaultHeaders);
    return request;
  }

  public Request create(String url) {
    return create(url, Method.GET);
  }
}
