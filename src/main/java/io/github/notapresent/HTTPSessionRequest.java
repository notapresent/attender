package io.github.notapresent;

import com.google.appengine.api.urlfetch.FetchOptions;
import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPResponse;
import org.apache.commons.lang3.SerializationUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class HTTPSessionRequest extends com.google.appengine.api.urlfetch.HTTPRequest {
    private final boolean followRedirects;

    public HTTPSessionRequest(URL url) {
        this(url, HTTPMethod.GET);
    }

    public HTTPSessionRequest(URL url, HTTPMethod method) {
        this(url, method, FetchOptions.Builder.withDefaults());
    }

    public HTTPSessionRequest(URL url, HTTPMethod method, FetchOptions opts) {
        super(url, method, SerializationUtils.clone(opts).doNotFollowRedirects());
        followRedirects = opts.getFollowRedirects();
    }

    public static HTTPSessionRequest makeRedirect(HTTPSessionRequest prev, HTTPResponse resp) throws MalformedURLException {
        String location = HTTPUtil.getHeader(resp.getHeaders(), "location");
        if (location == null) {
            throw new IllegalArgumentException("Redirect response without location header");
        }

        return new HTTPSessionRequest(new URL(prev.getURL(), location),
                HTTPMethod.GET, prev.getFetchOptions());
    }

    public void addHeaders(List<HTTPHeader> headers) {
        for(HTTPHeader header : headers) {
            addHeader(header);
        }
    }

    public boolean getFollowRedirects() {
        return followRedirects;
    }

}
