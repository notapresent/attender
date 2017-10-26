package io.github.notapresent;

import com.google.appengine.api.urlfetch.*;
import com.sun.javaws.exceptions.InvalidArgumentException;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HTTPSession {
    public static final int MAX_REDIRECTS = 5;
    private URLFetchService service;
    private CookieHandler cookieManager;

    public HTTPSession(URLFetchService service, CookieHandler cookieManager) {
        this.service = service;
        this.cookieManager = cookieManager;
    }

    public HTTPResponse fetch(URL url) throws IOException {
        HTTPSessionRequest req = new HTTPSessionRequest(url, HTTPMethod.GET,
                FetchOptions.Builder.withDefaults());
        return fetch(req);
    }

    public HTTPResponse fetch(HTTPSessionRequest req) throws IOException {
        int numHops = 0;
        boolean followRedirects = req.getFollowRedirects();
        HTTPResponse resp = null;

        while(++numHops < MAX_REDIRECTS) {
            resp = doRequest(req);

            if (!followRedirects || !HTTPUtil.isRedirect(resp.getResponseCode())) {
                break;
            }
            req = HTTPSessionRequest.makeRedirect(req, resp);
        }
        return resp;
    }

    private HTTPResponse doRequest(HTTPSessionRequest req) throws IOException {
        /* Load cookies start */
        try {
            URI uri = req.getURL().toURI();
            List<String> cookies = cookieManager.get(uri, new HashMap<>()).get("Cookie");
            if(cookies != null && cookies.size() > 0) {
                HTTPHeader cookieHeader = new HTTPHeader("cookie", String.join("; ", cookies));
                req.addHeader(cookieHeader);
            }
        }
        catch (URISyntaxException e) {
            throw new RuntimeException("Failed to convert URL " + req.getURL() + " to URI");
        }
        /* Load cookies end */

        HTTPResponse resp = service.fetch(req);

        /* Save cookies start */
        try {
            URI uri = req.getURL().toURI();
            Map<String, List<String>> respHeaders = convertHeaders(resp.getHeadersUncombined());
            cookieManager.put(uri, respHeaders);
        }

        catch (URISyntaxException e) {
            throw new RuntimeException("Failed to convert URL " + req.getURL() + " to URI");
        }
        /* Save cookies end */

        return resp;
    }

    public static Map<String, List<String>> convertHeaders(List<HTTPHeader> headers) {
        Map<String, List<String>> rv = new HashMap<>();
        for(HTTPHeader hdr : headers) {
            String key = hdr.getName().toLowerCase();
            String value = hdr.getValue();
            if(value == null || value.length() == 0) {
                continue;
            }
            rv.putIfAbsent(key, new ArrayList<>());
            rv.get(key).add(value);
        }
        return rv;
    }
}
