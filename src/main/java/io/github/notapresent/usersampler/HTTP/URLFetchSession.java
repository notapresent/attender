package io.github.notapresent.usersampler.HTTP;


import com.google.appengine.api.urlfetch.*;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class URLFetchSession implements Session<URLFetchRequest> {
    public static int DEFAULT_MAX_REDIRECTS = 5;
    private URLFetchService urlFetch;
    private int maxRedirects = DEFAULT_MAX_REDIRECTS;

    public URLFetchSession(URLFetchService urlFetch) {
        this.urlFetch = urlFetch;
    }

    @Override
    public URLFetchResponse send(URLFetchRequest request) throws IOException {
        HTTPResponse ufResp;
        HTTPRequest urlFetchRequest = request.toHTTPRequest();
        if(request.getRedirectHandlingPolicy() == Request.RedirectHandlingPolicy.FOLLOW) {
            ufResp = handleRedirects(urlFetchRequest);
        } else {
            ufResp = doSend(urlFetchRequest);
        }

        return URLFetchResponse.fromHTTPResponse(ufResp);
    }

    protected HTTPResponse handleRedirects(HTTPRequest req) throws IOException {
        int numRedirects = 0;
        HTTPResponse resp = null;
        List<HTTPHeader> originalHeaders = req.getHeaders();

        while(numRedirects++ < maxRedirects) {
            resp = doSend(req);
            if(!Util.isRedirect(resp.getResponseCode())) {
                break;
            }

            String location = Util.getHeader(resp.getHeaders(), "location");
            URL redirectUrl = new URL(req.getURL(), location);
            req = new HTTPRequest(redirectUrl, HTTPMethod.GET, req.getFetchOptions());
            for(HTTPHeader header : originalHeaders) {
                req.addHeader(header);
            }
            //originalHeaders.stream().map(req::addHeader);
        }

        return resp;
    }

    protected HTTPResponse doSend(HTTPRequest req) throws IOException {
        return urlFetch.fetch(req);
    }

}
