package io.github.notapresent;

import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.Future;


public class URLFetchSession implements URLFetchService {
    private URLFetchService service;

    public URLFetchSession(URLFetchService service) {
        this.service = service;
    }

    public HTTPResponse fetch(URL url) throws IOException {
        return service.fetch(url);
    }

    public HTTPResponse fetch(HTTPRequest req) throws IOException {
        return service.fetch(req);
    }

    public Future<HTTPResponse> fetchAsync(URL url) {
        return service.fetchAsync(url);
    }

    public Future<HTTPResponse> fetchAsync(HTTPRequest req) {
        return service.fetchAsync(req);
    }
}
