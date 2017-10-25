package io.github.notapresent;


import com.google.appengine.api.urlfetch.*;
import com.google.appengine.repackaged.com.google.common.collect.ImmutableMap;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static com.google.appengine.api.urlfetch.FetchOptions.Builder;
import static io.github.notapresent.URLFetchHelpers.getHeader;
import static io.github.notapresent.URLFetchHelpers.isRedirect;


public class HttpClient {
    public static final int MAX_REDIRECTS = 5;
    public static final Map<String, String> DEFAULT_HEADERS;
    private boolean followRedirects = true;
    private URLFetchService urlFetch;
    private URLFetchCookieManager cookieManager;

    private Map<String, String> headers;

    static {
        DEFAULT_HEADERS = ImmutableMap.of(
                "user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) attender/1.0",
                "accept", "text/html"
        );
    }

    public URLFetchCookieManager getCookieManager() {
        return cookieManager;
    }

    public void setCookieManager(URLFetchCookieManager cookieManager) {
        this.cookieManager = cookieManager;
    }

    public boolean getFollowRedirects() {
        return followRedirects;
    }

    public void setFollowRedirects(boolean followRedirects) {
        this.followRedirects = followRedirects;
    }


    public Map<String, String> getHeaders() {
        return headers;
    }

    public HttpClient(URLFetchService urlFetch) {
        this.urlFetch = urlFetch;
        headers = new HashMap<>();
        headers.putAll(DEFAULT_HEADERS);
    }


    protected HTTPResponse doRequest(HTTPSessionRequest req) throws HttpException {
        try {
            HTTPResponse resp = urlFetch.fetch(req);
            int responseCode = resp.getResponseCode();
            if (responseCode >= 400) {
                throw new HttpException("HTTP error " + responseCode, responseCode);
            }
            return resp;
        }
        catch (IOException e) {
            throw new HttpException(e);
        }
    }

    protected HTTPResponse doRequestWithCookies(HTTPSessionRequest req) throws HttpException {
        cookieManager.loadToRequest(req);
        HTTPResponse resp = doRequest(req);
        cookieManager.saveFromResponse(req.getURL(), resp);
        return resp;
    }

    public HTTPResponse request(String urlStr) throws HttpException {
        int hops = 0;
        HTTPSessionRequest req;
        HTTPResponse resp = null;
        URL url;
        String location;

        try {
            url = new URL(urlStr);
            while(hops++ < MAX_REDIRECTS) {
                req = prepareRequest(url);

                if(cookieManager == null) {
                    resp = doRequest(req);
                } else {
                    resp = doRequestWithCookies(req);
                }

                if (!isRedirect(resp) || !this.followRedirects) {
                    return resp;
                }

                location = getHeader(resp, "location");
                if(location == null) {
                    break;
                }
                url = new URL(url, location);
            }
            throw new HttpException("Too many redirects (" + hops + ")");
        }
        catch(MalformedURLException e) {
            throw new HttpException(e);
        }

    }

    protected HTTPSessionRequest prepareRequest(URL url) {
            FetchOptions opts = Builder.doNotFollowRedirects()
                    .disallowTruncate()
                    .doNotValidateCertificate();
            HTTPSessionRequest req = new HTTPSessionRequest(url, HTTPMethod.GET, opts);

            for(Map.Entry<String, String> hdr : headers.entrySet()) {
                req.setHeader(new HTTPHeader(hdr.getKey(), hdr.getValue()));
            }

            return req;
    }
}
