package io.github.notapresent.usersampler.HTTP;


import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;

import java.io.IOException;

public class URLFetchSession implements Session<URLFetchRequest> {
    private URLFetchService urlFetch;

    public URLFetchSession(URLFetchService urlFetch) {
        this.urlFetch = urlFetch;
    }

    @Override
    public URLFetchResponse send(URLFetchRequest request) throws IOException {
        HTTPRequest ufRequest = request.toHTTPRequest();
        HTTPResponse ufResp = urlFetch.fetch(ufRequest);
        return URLFetchResponse.fromHTTPResponse(ufResp);
    }

}
