package io.github.notapresent.usersampler.gaeapp.http;

import com.google.appengine.api.urlfetch.FetchOptions;
import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import io.github.notapresent.usersampler.common.http.HttpError;
import io.github.notapresent.usersampler.common.http.Request;
import io.github.notapresent.usersampler.common.http.Response;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Helper {

  private static FetchOptions buildFetchOptions(Request request) {
    FetchOptions opts = FetchOptions.Builder.withDeadline(request.getTimeout());

    if (request.getRedirectHandlingPolicy() == Request.RedirectPolicy.DEFAULT) {
      opts.followRedirects();
    } else {
      opts.doNotFollowRedirects();
    }
    return opts;
  }

  public static HTTPRequest createUrlFetchRequest(Request request) {
    try {
      HTTPRequest httpRequest = new HTTPRequest(
          new URL(request.getUrl()),
          HTTPMethod.valueOf(request.getMethod().name()),
          buildFetchOptions(request)
      );

      request.getHeaders().forEach((name, value) ->
          httpRequest.addHeader(new HTTPHeader(name, value)));

      return httpRequest;

    } catch (MalformedURLException e) {
      throw new HttpError("Malformed URL: " + request.getUrl(), e);
    }
  }

  public static String getHeaderValue(List<HTTPHeader> headers, String name) {
    for (HTTPHeader hdr : headers) {
      if (hdr.getName().equalsIgnoreCase(name)) {
        return hdr.getValue();
      }
    }
    return null;
  }

  private static Map<String, String> headersListToMap(List<HTTPHeader> headersList) {
    Map<String, String> headersMap = new HashMap<>();
    for (HTTPHeader header : headersList) {
      headersMap.merge(
          header.getName(),
          header.getValue(),
          (oldVal, val) -> oldVal == null ? val : oldVal + ", " + val
      );
    }
    return headersMap;
  }

  public static Response createResponse(HTTPResponse httpResponse, String finalUrl) {

    return new Response(
        httpResponse.getResponseCode(),
        headersListToMap(httpResponse.getHeadersUncombined()),
        httpResponse.getContent(),
        httpResponse.getFinalUrl() == null ? finalUrl : httpResponse.getFinalUrl().toString()
    );
  }

  static URI UrlToUri(URL url) {
    try {
      return url.toURI();
    } catch (URISyntaxException e) {
      throw new HttpError("Failed to convert URL " + url + " to URI", e);
    }
  }

  public static boolean isCookieHeader(String name) {
    return (name.equalsIgnoreCase("set-cookie")
        || name.equalsIgnoreCase("set-cookie2"));
  }
}
