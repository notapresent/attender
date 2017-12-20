package io.github.notapresent.usersampler.common.http;

import com.google.common.io.ByteStreams;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.CookieHandler;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUrlConnectionSession extends Session {

  @Override
  public void setCookieManager(CookieHandler cookieManager) {
    super.setCookieManager(cookieManager);
    CookieHandler.setDefault(cookieManager);
  }

  @Override
  protected Response doSend(Request request) throws IOException {
    HttpURLConnection conn;

    URL url = new URL(request.getUrl());
    conn = (HttpURLConnection) url.openConnection();
    request.getHeaders().forEach(conn::setRequestProperty);
    conn.setInstanceFollowRedirects(false);

    InputStream in = new BufferedInputStream(conn.getInputStream());

    Response response = new Response(
        conn.getResponseCode(),
        ByteStreams.toByteArray(in),
        request.getUrl()
    );

    conn.getHeaderFields()
        .entrySet()
        .stream()
        .filter(e -> e.getKey() != null)
        .forEach((e) -> response.setHeader(e.getKey(), e.getValue()));

    return response;
  }


}

