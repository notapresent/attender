package io.github.notapresent.usersampler.gaeapp.HTTP;


import com.google.appengine.api.urlfetch.*;
import com.google.inject.Inject;
import io.github.notapresent.usersampler.common.HTTP.Error;
import io.github.notapresent.usersampler.common.HTTP.Request;
import io.github.notapresent.usersampler.common.HTTP.Session;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.URL;
import java.util.List;

public class URLFetchSession implements Session {
    public static int DEFAULT_MAX_REDIRECTS = 5;
    private URLFetchService urlFetch;
    private int maxRedirects = DEFAULT_MAX_REDIRECTS;
    private URLFetchCookieManager cookieManager = null;

    public URLFetchSession(URLFetchService urlFetch) {
        this.urlFetch = urlFetch;
    }

    @Inject
    public URLFetchSession(URLFetchService urlFetch, CookieHandler cookieManager) {
        this.urlFetch = urlFetch;
        this.cookieManager = (URLFetchCookieManager) cookieManager;
    }

    public int getMaxRedirects() {
        return maxRedirects;
    }

    public void setMaxRedirects(int maxRedirects) {
        this.maxRedirects = maxRedirects;
    }

    @Override
    public URLFetchResponse send(Request request) throws IOException {
        HTTPResponse ufResp;
        HTTPRequest urlFetchRequest = ((URLFetchRequest) request).toHTTPRequest();

        if (request.getRedirectHandlingPolicy() == Request.RedirectPolicy.FOLLOW) {
            ufResp = handleRedirects(urlFetchRequest);
        } else {
            ufResp = doSend(urlFetchRequest);
        }

        return URLFetchResponse.fromHTTPResponse(ufResp);
    }

    protected HTTPResponse handleRedirects(HTTPRequest req) throws IOException {
        int timesRedirected = 0;
        HTTPResponse resp = null;
        List<HTTPHeader> originalHeaders = req.getHeaders();

        while (timesRedirected++ < maxRedirects) {
            resp = doSend(req);
            if (!URLFetchUtil.isRedirect(resp.getResponseCode())) {
                break;
            }

            String location = URLFetchUtil.getHeaderValue(resp.getHeaders(), "location");

            if (location == null) {
                throw new Error("Location header missing from redirect response");
            }

            URL redirectUrl = new URL(req.getURL(), location);
            req = new HTTPRequest(redirectUrl, HTTPMethod.GET, req.getFetchOptions());
            for (HTTPHeader header : originalHeaders) {
                req.addHeader(header);
            }
        }

        return resp;
    }

    protected HTTPResponse doSend(HTTPRequest req) throws IOException {
        if (cookieManager == null) {
            return urlFetch.fetch(req);
        }

        cookieManager.loadToRequest(req);

        HTTPResponse response = urlFetch.fetch(req);

        cookieManager.saveFromResponse(req.getURL(), response);

        return response;
    }

    @Override
    public CookieHandler getCookieManager() {
        return cookieManager;
    }

    @Override
    public void setCookieManager(CookieHandler cookieManager) {
        this.cookieManager = (URLFetchCookieManager) cookieManager;
    }
}
