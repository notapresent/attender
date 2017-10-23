package io.github.notapresent;

import com.google.appengine.api.urlfetch.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Future;


public class URLFetchSession implements URLFetchService {
    public static final int MAX_REDIRECTS = 5;
    private URLFetchService service;


    public URLFetchSession(URLFetchService service) {
        this.service = service;
    }

    public HTTPResponse fetch(URL url) throws IOException {
        HTTPRequest req = new HTTPRequest(url, HTTPMethod.GET,
                FetchOptions.Builder.doNotFollowRedirects());
        return doRequest(req, true);
    }

    public HTTPResponse fetch(HTTPRequest req) throws IOException {
        boolean followRedirects = req.getFetchOptions().getFollowRedirects();
        return doRequest(prepRequest(req), followRedirects);
    }

    private HTTPResponse doRequest(HTTPRequest req, boolean followRedirects) throws IOException {
        int numHops = 0;
        HTTPResponse resp = null;
        while(++numHops < MAX_REDIRECTS) {
            resp = service.fetch(req);
            if (!followRedirects || !isRedirect(resp.getResponseCode())) {
                break;
            }
            req = makeRedirectRequest(req.getURL(), resp);
        }
        return resp;
    }

    public Future<HTTPResponse> fetchAsync(URL url) {
        return service.fetchAsync(url);
    }

    public Future<HTTPResponse> fetchAsync(HTTPRequest req) {
        FetchOptions origFetchOpts = req.getFetchOptions();
        FetchOptions newFetchOpts = copyFetchOptions(origFetchOpts);
        return service.fetchAsync(req);
    }

    public static HTTPRequest makeRedirectRequest(URL originalUrl, HTTPResponse resp)
            throws MalformedURLException {
        String location = getHeader(resp.getHeaders(), "location");

        if(location == null) {
            throw new IllegalArgumentException("Redirect response without location header");
        }
        return new HTTPRequest(new URL(originalUrl, location),
                HTTPMethod.GET,
                FetchOptions.Builder.doNotFollowRedirects());
    }

    public static boolean isRedirect(int code) {
        return code >= 301 && code <= 303;
    }

    public static String getHeader(List<HTTPHeader> headers, String  name) {
        for(HTTPHeader hdr: headers) {
            if(hdr.getName().equalsIgnoreCase(name)) {
                return hdr.getValue();
            }
        }
        return null;
    }

    public static FetchOptions copyFetchOptions(FetchOptions src) {
        FetchOptions dest = FetchOptions.Builder.withDefaults();
        if(src.getAllowTruncate()) {
            dest.allowTruncate();
        } else {
            dest.disallowTruncate();
        }
        if(src.getFollowRedirects()) {
            dest.followRedirects();
        } else {
            dest.doNotFollowRedirects();
        }
        dest.setDeadline(src.getDeadline());

        if(src.getValidateCertificate()) {
            dest.validateCertificate();
        } else {
            dest.doNotValidateCertificate();
        }
        return dest;
    }

    public static HTTPRequest copyWithFetchOptions(HTTPRequest original, FetchOptions opts) {
        HTTPRequest rv = new HTTPRequest(original.getURL(), original.getMethod(), opts);
        for(HTTPHeader hdr : original.getHeaders()) {
            rv.setHeader(hdr);
        }
        return rv;
    }

    public static HTTPRequest prepRequest(HTTPRequest req) {
        FetchOptions newFetchOpts = copyFetchOptions(req.getFetchOptions()).doNotFollowRedirects();
        return copyWithFetchOptions(req, newFetchOpts);
    }
}
