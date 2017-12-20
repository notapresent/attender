package io.github.notapresent.usersampler.gaeapp.http;

import static io.github.notapresent.usersampler.gaeapp.http.Helper.createResponse;
import static io.github.notapresent.usersampler.gaeapp.http.Helper.createUrlFetchRequest;

import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.apphosting.api.ApiProxy;
import com.google.inject.Inject;
import io.github.notapresent.usersampler.common.http.HttpError;
import io.github.notapresent.usersampler.common.http.Request;
import io.github.notapresent.usersampler.common.http.Response;
import io.github.notapresent.usersampler.common.http.Session;
import java.io.IOException;
import java.net.CookieHandler;

public class UrlFetchSession extends Session {

  private final URLFetchService urlFetch;

  private UrlFetchCookieManager cookieManager = null;

  public UrlFetchSession(URLFetchService urlFetch) {
    this.urlFetch = urlFetch;
  }

  @Inject
  public UrlFetchSession(URLFetchService urlFetch, CookieHandler cookieManager) {
    this.urlFetch = urlFetch;
    this.cookieManager = (UrlFetchCookieManager) cookieManager;
  }

  protected Response doSend(Request request) throws HttpError {
    HTTPRequest req = createUrlFetchRequest(request);

    if (cookieManager != null) {
      cookieManager.loadToRequest(req);
    }

    try {
      HTTPResponse httpResponse = urlFetch.fetch(req);
      if (cookieManager != null) {
        cookieManager.saveFromResponse(req.getURL(), httpResponse);
      }

      return createResponse(httpResponse, request.getUrl());
    } catch (IOException | ApiProxy.ApiProxyException e) {
      throw new HttpError(e);
    }
  }

  @Override
  public void setCookieManager(CookieHandler cookieManager) {
    this.cookieManager = (UrlFetchCookieManager) cookieManager;
  }
}
