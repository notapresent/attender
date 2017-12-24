package io.github.notapresent.usersampler.common.http;

import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.net.CookieHandler;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public abstract class Session { // TODO separate cookieProcessor and redirectProcessor

  private static final int DEFAULT_MAX_REDIRECTS = 5;
  private static final String COOKIE_HEADER = "Cookie";
  private int maxRedirects = DEFAULT_MAX_REDIRECTS;
  private CookieHandler cookieManager;

  public CookieHandler getCookieManager() {
    return cookieManager;
  }

  public void setCookieManager(CookieHandler cookieManager) {
    this.cookieManager = cookieManager;
  }

  public int getMaxRedirects() {
    return maxRedirects;
  }

  public void setMaxRedirects(int maxRedirects) {
    this.maxRedirects = maxRedirects;
  }

  public Response send(Request request) throws HttpError {
    Response response;
    try {
      if (request.getRedirectHandlingPolicy() == Request.RedirectPolicy.FOLLOW) {
        response = handleRedirects(request);
      } else {
        response = handleCookies(request);
      }
    } catch (MalformedURLException e) {
      throw new HttpError("Malformed URL: " + request.getUrl(), e);
    } catch (IOException e) {
      throw new HttpError(e);
    }

    return response;
  }

  private Response handleRedirects(Request request) throws IOException {
    Request origRequest = request.clone();
    Response resp = null;
    int tries = 0;

    while (tries++ < maxRedirects) {
      resp = handleCookies(request);

      if (resp.isRedirect()) {
        String location = resp.headers.get("location");
        request = new Request(Util.absoluteUrl(request.getUrl(), location));
        request.getHeaders().putAll(origRequest.getHeaders());
      } else {
        return resp;
      }
    }

    return resp;
  }

  private Response handleCookies(Request request) throws IOException {
    String requestUrl = request.getUrl();
    if(cookieManager != null) {
      // FIXME: This overwrites cookie header if request already has one
      request.getHeaders().putAll(loadCookies(requestUrl));
    }

    Response resp = doSend(request);

    if(cookieManager != null) {
      saveCookies(requestUrl, resp.getHeaders());
    }

    return resp;
  }

  private Map<String, String> loadCookies(String url) throws IOException {
    Map<String, String> rv = new HashMap<>();

    Map<String, List<String>> cookies = cookieManager.get(Util.StrToUri(url), new HashMap<>());
    if(!cookies.isEmpty()) {
      String header = String.join("; ", cookies.get(COOKIE_HEADER));
      rv.put(COOKIE_HEADER, header);
    }
    return rv;
  }

  private void saveCookies(String url, Map<String, String> responseHeaders)
      throws IOException {
    URI uri = Util.StrToUri(url);
    Map<String, List<String>> cookieHeaders = responseHeaders.entrySet().stream()
        .filter((e) -> e.getKey().matches("(?i)^set-cookie2?$"))
        .collect(Collectors.toMap(
            Entry::getKey,
            (e) -> Arrays.asList(e.getValue().split(", "))
        ));

    cookieManager.put(uri, cookieHeaders);
  }

  protected abstract Response doSend(Request request) throws IOException;
}
