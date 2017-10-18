package io.github.notapresent;


import com.google.appengine.api.urlfetch.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.stream.Collectors;

import static com.google.appengine.api.urlfetch.FetchOptions.Builder;


public class HttpClient {
    public static final int MAX_REDIRECTS = 5;
    private boolean followRedirects = true;
    private URLFetchService urlFetch;

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

    public HTTPResponse request(String urlStr) throws HttpException {
        int hops = 0;
        HTTPRequest req;
        HTTPResponse resp = null;
        URL url;

        try {
            url = new URL(urlStr);
            while(hops++ < MAX_REDIRECTS) {
                req = prepareRequest(url);
                resp = doRequest(req);
                if (!isRedirect(resp) || !this.followRedirects) {
                    return resp;
                }
                url = new URL(url, getHeader(resp, "location"));
            }
            return resp;
        }
        catch(MalformedURLException e) {
            throw new HttpException(e);
        }

    }

    protected String getHeader(HTTPResponse resp, String headerName) {
        headerName = headerName.toLowerCase();
        for(HTTPHeader hdr : resp.getHeaders()) {
            if(hdr.getName().equalsIgnoreCase(headerName)) {
                return hdr.getValue();
            }
        }
        return null;
    }

    protected HTTPRequest prepareRequest(URL url) {
            FetchOptions opts = Builder.doNotFollowRedirects()
                    .disallowTruncate()
                    .doNotValidateCertificate();
            HTTPRequest req = new HTTPRequest(url, HTTPMethod.GET, opts);
            return req;
    }

    protected boolean isRedirect(HTTPResponse resp) {
        int responseCode = resp.getResponseCode();
        return (responseCode == 301 || responseCode == 302);
    }
}
