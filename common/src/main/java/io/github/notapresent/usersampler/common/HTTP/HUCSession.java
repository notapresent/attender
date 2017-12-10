package io.github.notapresent.usersampler.common.HTTP;

import com.google.common.io.ByteStreams;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.CookieHandler;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HUCSession extends Session  {
    @Override
    public void setCookieManager(CookieHandler cookieManager) {
        super.setCookieManager(cookieManager);
        CookieHandler.setDefault(cookieManager);
    }

    @Override
    public Response send(Request request) throws HTTPError {
        Response response;
        try {
            if(request.getRedirectHandlingPolicy() == Request.RedirectPolicy.DO_NOT_FOLLOW) {
                response = doSend(request);
            } else  {
                response = handleRedirects(request);
            }
        } catch (MalformedURLException e) {
            throw new HTTPError("Malformed URL: " + request.getUrl(), e);
        } catch (IOException e) {
            throw new HTTPError(e);
        }

        return response;
    }

    private Response handleRedirects(Request request) throws IOException {
        Request origRequest = request.clone();
        Response resp;
        int tries = 0;

        while(tries++ < DEFAULT_MAX_REDIRECTS) {
            resp = doSend(request);

            if(resp.isRedirect()) {
                String location = resp.headers.get("location");
                request = new Request(Util.absoluteUrl(request.getUrl(), location));
                request.getHeaders().putAll(origRequest.getHeaders());
            }
            else {
                return resp;
            }
        }

        throw new HTTPError("Maxinum number of redirects (" + DEFAULT_MAX_REDIRECTS + ") exceeded");
    }

    private Response doSend(Request request) throws IOException {
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

