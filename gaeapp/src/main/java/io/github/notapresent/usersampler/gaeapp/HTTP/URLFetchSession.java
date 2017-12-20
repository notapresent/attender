package io.github.notapresent.usersampler.gaeapp.HTTP;


import static io.github.notapresent.usersampler.gaeapp.HTTP.Helper.createHTTPRequest;
import static io.github.notapresent.usersampler.gaeapp.HTTP.Helper.createResponse;

import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.apphosting.api.ApiProxy;
import com.google.inject.Inject;
import io.github.notapresent.usersampler.common.HTTP.HTTPError;
import io.github.notapresent.usersampler.common.HTTP.Request;
import io.github.notapresent.usersampler.common.HTTP.Response;
import io.github.notapresent.usersampler.common.HTTP.Session;
import java.io.IOException;
import java.net.CookieHandler;

public class URLFetchSession extends Session {

  private final URLFetchService urlFetch;

  private URLFetchCookieManager cookieManager = null;

  public URLFetchSession(URLFetchService urlFetch) {
    this.urlFetch = urlFetch;
  }

  @Inject
  public URLFetchSession(URLFetchService urlFetch, CookieHandler cookieManager) {
    this.urlFetch = urlFetch;
    this.cookieManager = (URLFetchCookieManager) cookieManager;
  }

  protected Response doSend(Request request) throws HTTPError {
    HTTPRequest req = createHTTPRequest(request);

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
      throw new HTTPError(e);
    }
  }

  @Override
  public void setCookieManager(CookieHandler cookieManager) {
    this.cookieManager = (URLFetchCookieManager) cookieManager;
  }
}
