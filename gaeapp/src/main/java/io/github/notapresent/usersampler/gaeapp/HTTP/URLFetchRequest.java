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
import java.util.HashMap;
import java.util.Map;

public class URLFetchRequest implements Request {
    private final String url;
    private final Method method;
    private RedirectPolicy redirectHandlingPolicy = RedirectPolicy.DEFAULT;
    private double timeout = 5.0;
    private Map<String, String> headers = new HashMap<>();

    public URLFetchRequest(String url, Method method) {
        this.url = url;
        this.method = method;
    }

    public static URLFetchRequest GET(String url) {
        return new URLFetchRequest(url, Method.GET);
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public double getTimeout() {
        return timeout;
    }

    @Override
    public void setTimeout(double timeout) {
        this.timeout = timeout;
    }

    @Override
    public Map<String, String> getHeaders() {
        return headers;
    }

    public RedirectPolicy getRedirectHandlingPolicy() {
        return redirectHandlingPolicy;
    }

    @Override
    public void setRedirectHandlingPolicy(RedirectPolicy policy) {
        this.redirectHandlingPolicy = policy;
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
