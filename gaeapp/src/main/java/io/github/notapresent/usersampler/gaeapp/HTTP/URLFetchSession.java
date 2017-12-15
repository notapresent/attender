package io.github.notapresent.usersampler.gaeapp.HTTP;


import com.google.appengine.api.urlfetch.*;
import com.google.apphosting.api.ApiProxy;
import com.google.inject.Inject;
import io.github.notapresent.usersampler.common.HTTP.*;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static io.github.notapresent.usersampler.gaeapp.HTTP.Helper.createHTTPRequest;
import static io.github.notapresent.usersampler.gaeapp.HTTP.Helper.createResponse;

public class URLFetchSession extends Session {
    private URLFetchService urlFetch;

    private URLFetchCookieManager cookieManager = null;

    public URLFetchSession(URLFetchService urlFetch) {
        this.urlFetch = urlFetch;
    }

    @Inject
    public URLFetchSession(URLFetchService urlFetch, CookieHandler cookieManager) {
        this.urlFetch = urlFetch;
        this.cookieManager = (URLFetchCookieManager) cookieManager;
    }

    protected Response doSend(Request request) throws HTTPError {
        HTTPRequest req = createHTTPRequest(request);

        if (cookieManager != null) {
            cookieManager.loadToRequest(req);
        }

        try {
            HTTPResponse httpResponse = urlFetch.fetch(req);
            if (cookieManager != null) {
                cookieManager.saveFromResponse(req.getURL(), httpResponse);
            }

            return createResponse(httpResponse, request.getUrl());
        }

        catch (IOException|ApiProxy.ApiProxyException e) {
            throw new HTTPError(e);
        }
    }

    @Override
    public void setCookieManager(CookieHandler cookieManager) {
        this.cookieManager = (URLFetchCookieManager) cookieManager;
    }
}
