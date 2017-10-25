package io.github.notapresent;

import com.google.appengine.api.urlfetch.FetchOptions;
import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.URL;


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
        return doRequest(req);
    }

    private HTTPResponse doRequest(HTTPSessionRequest req) throws IOException {
        int numHops = 0;
        boolean followRedirects = req.getFollowRedirects();
        HTTPResponse resp = null;

        while(++numHops < MAX_REDIRECTS) {
            resp = service.fetch(req);

            if (!followRedirects || !HTTPUtil.isRedirect(resp.getResponseCode())) {
                break;
            }
            req = HTTPSessionRequest.makeRedirect(req, resp);
        }
        return resp;
    }

}
