package io.github.notapresent;

import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;

import java.io.IOException;
import java.net.CookieManager;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.github.notapresent.URLFetchHelpers.responseHeaders;


public class URLFetchCookieManager extends CookieManager {
    public void saveFromResponse(URL url, HTTPResponse resp) {
        try {
            URI uri = url.toURI();
            Map<String, List<String>> respHeaders = responseHeaders(resp);
            put(uri, respHeaders);
        }
        catch(URISyntaxException e) {
            // TODO log it
        }
        catch (IOException e) {
            // TODO log it
        }
    }

    public void loadToRequest(HTTPRequest req) {
        try {
            String cookies = cookieForURL(req.getURL().toURI());
            if (cookies != null) {
                req.setHeader(new HTTPHeader("Cookie", cookies));
            }
        } catch(URISyntaxException e) {
            // TODO log it
        }
        catch (IOException e) {
            // TODO log it
        }
    }

    public String cookieForURL(URI uri) throws IOException {
        Map<String, List<String>> requestHeaders = new HashMap<>();
        List<String> storedCookies = get(uri, requestHeaders).get("Cookie");
        if(storedCookies == null || storedCookies.size() == 0) {
            return null;
        }
        return String.join("; ", storedCookies);
    }
}
