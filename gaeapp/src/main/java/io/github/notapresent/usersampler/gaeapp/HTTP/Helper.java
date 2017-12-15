package io.github.notapresent.usersampler.gaeapp.HTTP;

import com.google.appengine.api.urlfetch.*;
import io.github.notapresent.usersampler.common.HTTP.HTTPError;
import io.github.notapresent.usersampler.common.HTTP.Request;
import io.github.notapresent.usersampler.common.HTTP.Response;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static String getHeaderValue(List<HTTPHeader> headers, String name) {
        for (HTTPHeader hdr : headers) {
            if (hdr.getName().equalsIgnoreCase(name)) {
                return hdr.getValue();
            }
        }
        return null;
    }

    protected static Map<String, String> headersListToMap(List<HTTPHeader> headersList) {
        Map<String, String> headersMap = new HashMap<>();
        for (HTTPHeader header : headersList) {
            headersMap.merge(
                    header.getName(),
                    header.getValue(),
                    (oldVal, val) -> oldVal == null ? val : oldVal + ", " + val
            );
        }
        return headersMap;
    }

    public static Response createResponse(HTTPResponse httpResponse, String finalUrl) {

        return new Response(
                httpResponse.getResponseCode(),
                headersListToMap(httpResponse.getHeadersUncombined()),
                httpResponse.getContent(),
                httpResponse.getFinalUrl() == null ? finalUrl : httpResponse.getFinalUrl().toString()
        );
    }

    protected static URI URL2URI(URL url) {
        try {
            return url.toURI();
        } catch (URISyntaxException e) {
            throw new HTTPError("Failed to convert URL " + url + " to URI", e);
        }
    }

    public static boolean isCookieHeader(String name) {
        return (name.equalsIgnoreCase("set-cookie") ||
                name.equalsIgnoreCase("set-cookie2"));
    }
}
