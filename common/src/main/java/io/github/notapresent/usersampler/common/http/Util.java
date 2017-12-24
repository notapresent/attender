package io.github.notapresent.usersampler.common.http;

import static java.net.HttpURLConnection.HTTP_MOVED_PERM;
import static java.net.HttpURLConnection.HTTP_MOVED_TEMP;
import static java.net.HttpURLConnection.HTTP_SEE_OTHER;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

class Util {

  public static boolean isRedirect(int code) {
    return code == HTTP_MOVED_PERM || code == HTTP_MOVED_TEMP || code == HTTP_SEE_OTHER;
  }

  public static String absoluteUrl(String base, String url) {
    try {
      return new URL(new URL(base), url).toString();
    } catch (MalformedURLException e) {
      throw new HttpError("Failed to construct url from " + base + " and " + url, e);
    }
  }

  public static URI StrToUri(String strUrl) {
    try {
      return new URI(strUrl);
    } catch (URISyntaxException e) {
      throw new HttpError("Invalid URI: " + strUrl, e);
    }
  }
}
