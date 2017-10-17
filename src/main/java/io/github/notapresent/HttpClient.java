package io.github.notapresent;


import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.stream.Collectors;


public class HttpClient {
    public static int MAX_REDIRECTS = 5;

    private URLFetchService urlFetch;
    private boolean followRedirects = true;
    private int max_redirects = MAX_REDIRECTS;

    public HttpClient(URLFetchService urlFetch) {
        this.urlFetch = urlFetch;
    }

    public HTTPResponse request(String urlStr) throws HttpException {
        try {
            URL url = new URL(urlStr);
            HTTPResponse resp = urlFetch.fetch(url);
            int responseCode = resp.getResponseCode();
            if (responseCode >= 400) {
                throw new HttpException("HTTP error " + responseCode, responseCode);
            }
            return resp;
        }
        catch (MalformedURLException e) {
                throw new HttpException(e);
        }
        catch (IOException e) {
            throw new HttpException(e);
        }
    }
}
