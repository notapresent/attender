package io.github.notapresent.usersampler.common.http;

import java.net.MalformedURLException;
import java.net.URL;

class Util {

  public static boolean isRedirect(int code) {
    return code >= 301 && code <= 303;
  }

  public static String absoluteUrl(String base, String url) {
    try {
      return new URL(new URL(base), url).toString();
    } catch (MalformedURLException e) {
      throw new HttpError("Failed to construct url from " + base + " and " + url, e);
    }
  }
}
