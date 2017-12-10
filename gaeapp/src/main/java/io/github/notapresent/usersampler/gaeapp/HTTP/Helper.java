package io.github.notapresent.usersampler.gaeapp.HTTP;

import com.google.appengine.api.urlfetch.FetchOptions;
import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import io.github.notapresent.usersampler.common.HTTP.HTTPError;
import io.github.notapresent.usersampler.common.HTTP.Request;

import java.net.MalformedURLException;
import java.net.URL;

public class Helper {
    public static FetchOptions buildFetchOptions(Request request) {
        FetchOptions opts = FetchOptions.Builder.withDeadline(request.getTimeout());

        if (request.getRedirectHandlingPolicy() == Request.RedirectPolicy.DEFAULT) {
            opts.followRedirects();
        } else {
            opts.doNotFollowRedirects();
        }
        return opts;
    }

    public static HTTPRequest createHTTPRequest(Request request) {
        try {
            HTTPRequest httpRequest = new HTTPRequest(
                    new URL(request.getUrl()),
                    HTTPMethod.valueOf(request.getMethod().name()),
                    buildFetchOptions(request)
            );

            request.getHeaders().forEach((name, value) ->
                    httpRequest.addHeader(new HTTPHeader(name, value)));

            return httpRequest;

        } catch (MalformedURLException e) {
            throw new HTTPError("Malformed URL: " + request.getUrl(), e);
        }
    }
}
