package io.github.notapresent.usersampler.gaeapp.HTTP;

import com.google.appengine.api.urlfetch.FetchOptions;
import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import io.github.notapresent.usersampler.common.HTTP.Error;
import io.github.notapresent.usersampler.common.HTTP.Method;
import io.github.notapresent.usersampler.common.HTTP.Request;

import java.net.MalformedURLException;
import java.net.URL;

public class URLFetchRequest extends Request {

    public URLFetchRequest(String url, Method method) {
        super(url, method);
    }

    public URLFetchRequest(String url) {
        this(url, Method.GET);
    }

    public HTTPRequest toHTTPRequest() {
        try {
            HTTPRequest httpRequest = new HTTPRequest(
                    new URL(url),
                    HTTPMethod.valueOf(method.toString()),
                    buildFetchOptions()
            );
            headers.forEach((name, value) ->
                    httpRequest.addHeader(new HTTPHeader(name, value)));

            return httpRequest;

        } catch (MalformedURLException e) {
            throw new Error("Malformed URL: " + url, e);
        }
    }

    protected FetchOptions buildFetchOptions() {
        FetchOptions opts = FetchOptions.Builder.withDeadline(timeout);

        if (redirectHandlingPolicy == RedirectPolicy.DEFAULT) {
            opts.followRedirects();
        } else {
            opts.doNotFollowRedirects();
        }

        return opts;
    }

}
