package io.github.notapresent;


import com.google.appengine.api.urlfetch.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static io.github.notapresent.URLFetchHelpers.getHeader;
import static io.github.notapresent.URLFetchHelpers.isRedirect;
import static com.google.appengine.api.urlfetch.FetchOptions.Builder;


public class HttpClient {
    public static final int MAX_REDIRECTS = 5;
    static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36 attender/1.0";

    private boolean followRedirects = true;
    private URLFetchService urlFetch;
    private URLFetchCookieManager cookieManager;

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

    public HttpClient(URLFetchService urlFetch) {
        this.urlFetch = urlFetch;
    }

    protected HTTPResponse doRequest(HTTPRequest req) throws HttpException {
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

    protected HTTPResponse doRequestWithCookies(HTTPRequest req) throws HttpException {
        cookieManager.loadToRequest(req);
        HTTPResponse resp = doRequest(req);
        cookieManager.saveFromResponse(req.getURL(), resp);
        return resp;
    }

    public HTTPResponse request(String urlStr) throws HttpException {
        int hops = 0;
        HTTPRequest req;
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
            return resp;
        }
        catch(MalformedURLException e) {
            throw new HttpException(e);
        }

    }

    protected HTTPRequest prepareRequest(URL url) {
            FetchOptions opts = Builder.doNotFollowRedirects()
                    .disallowTruncate()
                    .doNotValidateCertificate();
            HTTPRequest req = new HTTPRequest(url, HTTPMethod.GET, opts);
            return req;
    }
}
