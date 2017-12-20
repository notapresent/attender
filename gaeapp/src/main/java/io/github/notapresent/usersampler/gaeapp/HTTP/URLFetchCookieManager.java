package io.github.notapresent.usersampler.gaeapp.HTTP;

import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import io.github.notapresent.usersampler.common.HTTP.HTTPError;
import java.io.IOException;
import java.net.CookieManager;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO move this class functionalito to helpers
public class URLFetchCookieManager extends CookieManager {

  private List<String> cookiesForURL(URL url) {
    try {
      URI uri = Helper.URL2URI(url);
      return get(uri, new HashMap<>()).get("Cookie");
    } catch (IOException e) {
      throw new HTTPError("Failed to load cookies for " + url, e);
    }
  }

  public void loadToRequest(HTTPRequest request) {
    List<String> cookies = cookiesForURL(request.getURL());
    if (cookies != null && cookies.size() > 0) {
      request.addHeader(new HTTPHeader(
          "cookie",
          String.join("; ", cookies)
      ));
    }
  }

  public void saveFromResponse(URL url, HTTPResponse response) {
    Map<String, List<String>> setCookieHeaders = new HashMap<>();
    response.getHeadersUncombined()
        .stream()
        .filter((h) -> Helper.isCookieHeader(h.getName()))
        .filter((h) -> h.getValue() != null)
        .filter((h) -> !h.getValue().equals(""))
        .forEach((h) -> setCookieHeaders.merge(
            h.getName(),
            new ArrayList<>(Collections.singletonList(h.getValue())),
            (oldVal, newVal) -> {
              oldVal.add(newVal.get(0));
              return oldVal;
            }
            )
        );

    if (setCookieHeaders.isEmpty()) {
      return;
    }

    try {
      put(Helper.URL2URI(url), setCookieHeaders);
    } catch (IOException e) {
      throw new HTTPError("Failed to save cookies for " + url, e);
    }
  }
}