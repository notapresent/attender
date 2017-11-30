package io.github.notapresent.usersampler.HTTP;

import com.google.appengine.api.urlfetch.FetchOptions;
import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;

import java.net.MalformedURLException;
import java.net.URL;

public class URLFetchRequest implements Request {
    private final String url;
    private final Method method;
    private RedirectHandlingPolicy redirectHandlingPolicy;
    private double timeout = 5.0;

    public URLFetchRequest(String url, Method method) {
        this.url = url;
        this.method = method;
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
    public void setRedirectHandlingPolicy(RedirectHandlingPolicy policy) {
        this.redirectHandlingPolicy = policy;
    }

    @Override
    public void setTimeout(double timeout) {
        this.timeout = timeout;
    }

    @Override
    public double getTimeout(){
        return timeout;
    }

    public RedirectHandlingPolicy getRedirectHandlingPolicy() {
        return redirectHandlingPolicy;
    }


    public HTTPRequest toHTTPRequest() {
        try {
            HTTPRequest request = new HTTPRequest(
                    new URL(url),
                    HTTPMethod.valueOf(method.toString()),
                    buildFetchOptions()
            );
            return request;

        } catch (MalformedURLException e) {
            throw new Error("Malformed URL: " + url, e);
        }
    }

    protected FetchOptions buildFetchOptions() {
        FetchOptions opts = FetchOptions.Builder.withDeadline(timeout);

        if(redirectHandlingPolicy == RedirectHandlingPolicy.DEFAULT) {
            opts.followRedirects();
        } else {
            opts.doNotFollowRedirects();
        }

        return opts;
    }

    public static URLFetchRequest GET(String url) {
        return new URLFetchRequest(url, Method.GET);
    }

}
