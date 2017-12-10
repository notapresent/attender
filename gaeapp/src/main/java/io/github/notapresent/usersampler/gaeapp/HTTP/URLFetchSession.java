package io.github.notapresent.usersampler.gaeapp.HTTP;


import com.google.appengine.api.urlfetch.*;
import com.google.inject.Inject;
import io.github.notapresent.usersampler.common.HTTP.*;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class URLFetchSession extends Session {
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
    public Response send(Request request) throws HTTPError {
        HTTPResponse ufResp;
        HTTPRequest urlFetchRequest = Helper.createHTTPRequest(request);
        URLFetchResponse response;

        if (request.getRedirectHandlingPolicy() == Request.RedirectPolicy.FOLLOW) {
            response = handleRedirects(urlFetchRequest);
        } else {
            ufResp = doSend(urlFetchRequest);
            response = new URLFetchResponse(ufResp, request.getUrl());

        }

        response.setRequest(request);
        return response;
    }

    protected URLFetchResponse handleRedirects(HTTPRequest req) throws HTTPError {
        int timesRedirected = 0;
        HTTPResponse ufResp = null;
        List<HTTPHeader> originalHeaders = req.getHeaders();
        URL redirectUrl = null;

        while (timesRedirected++ < maxRedirects) {
            ufResp = doSend(req);
            if (!Util.isRedirect(ufResp.getResponseCode())) {
                break;
            }

            String location = URLFetchUtil.getHeaderValue(ufResp.getHeaders(), "location");

            if (location == null) {
                throw new HTTPError("Location header missing from redirect response");
            }

            try {
                redirectUrl = new URL(req.getURL(), location);
            } catch (MalformedURLException e) {
                throw new HTTPError("Failed to parse redirect location " + location, e);
            }

            req = new HTTPRequest(redirectUrl, HTTPMethod.GET, req.getFetchOptions());
            for (HTTPHeader header : originalHeaders) {
                req.addHeader(header);
            }
        }

        URLFetchResponse response = new URLFetchResponse(ufResp, req.getURL().toString());

        return response;
    }

    protected HTTPResponse doSend(HTTPRequest req) throws HTTPError {

        if (cookieManager != null) {
            cookieManager.loadToRequest(req);
        }

        try {
            HTTPResponse response = urlFetch.fetch(req);
            if (cookieManager != null) {
                cookieManager.saveFromResponse(req.getURL(), response);
            }

            return response;
        }

        catch (IOException e) {
            throw new HTTPError(e);
        }
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
