package io.github.notapresent.usersampler.common.HTTP;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.MalformedURLException;

abstract public class Session {

  private static final int DEFAULT_MAX_REDIRECTS = 5;
  private int maxRedirects = DEFAULT_MAX_REDIRECTS;
  private CookieHandler cookieManager;

  public CookieHandler getCookieManager() {
    return cookieManager;
  }

  protected void setCookieManager(CookieHandler cookieManager) {
    this.cookieManager = cookieManager;
  }

  public int getMaxRedirects() {
    return maxRedirects;
  }

  public void setMaxRedirects(int maxRedirects) {
    this.maxRedirects = maxRedirects;
  }

  public Response send(Request request) throws HTTPError {
    Response response;
    try {
      if (request.getRedirectHandlingPolicy() == Request.RedirectPolicy.FOLLOW) {
        response = sendWithRedirects(request);
      } else {
        response = doSend(request);
      }
    } catch (MalformedURLException e) {
      throw new HTTPError("Malformed URL: " + request.getUrl(), e);
    } catch (IOException e) {
      throw new HTTPError(e);
    }

    return response;
  }

  private Response sendWithRedirects(Request request) throws IOException {
    Request origRequest = request.clone();
    Response resp = null;
    int tries = 0;

    while (tries++ < maxRedirects) {
      resp = doSend(request);

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

  abstract protected Response doSend(Request request) throws IOException;
}
