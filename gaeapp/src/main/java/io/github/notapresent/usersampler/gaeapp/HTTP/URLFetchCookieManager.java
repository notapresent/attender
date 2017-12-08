package io.github.notapresent.usersampler.gaeapp.HTTP;

import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import io.github.notapresent.usersampler.common.HTTP.HTTPError;

import java.io.IOException;
import java.net.CookieManager;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

public class URLFetchCookieManager extends CookieManager {

    protected static URI URL2URI(URL url) {
        try {
            return url.toURI();
        } catch (URISyntaxException e) {
            throw new HTTPError("Failed to convert URL " + url + " to URI", e);
        }
    }

    public static boolean isCookieHeader(String name) {
        return (name.equalsIgnoreCase("set-cookie") ||
                name.equalsIgnoreCase("set-cookie2"));
    }

    protected List<String> cookiesForURL(URL url) {
        try {
            URI uri = URL2URI(url);
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
                .filter((h) -> isCookieHeader(h.getName()))
                .filter((h) -> h.getValue() != null)
                .filter((h) -> !h.getValue().equals(""))
                .forEach((h) -> setCookieHeaders.merge(
                        h.getName(),
                        new ArrayList<>(Arrays.asList(h.getValue())),
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
            put(URL2URI(url), setCookieHeaders);
        } catch (IOException e) {
            throw new HTTPError("Failed to save cookies for " + url, e);
        }
    }
}