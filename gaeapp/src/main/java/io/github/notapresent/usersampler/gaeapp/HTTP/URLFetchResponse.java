package io.github.notapresent.usersampler.gaeapp.HTTP;

import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPResponse;
import io.github.notapresent.usersampler.common.HTTP.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class URLFetchResponse extends Response {
    public URLFetchResponse(int status, byte[] content, Map<String, String> headers, String finalUrl) {
        super(status, headers, content, finalUrl);
    }

    public URLFetchResponse(HTTPResponse httpResponse, String finalUrl) {
        this(httpResponse.getResponseCode(),
                httpResponse.getContent(),
                headersListToMap(httpResponse.getHeadersUncombined()),
                httpResponse.getFinalUrl() == null ? finalUrl : httpResponse.getFinalUrl().toString()
        );
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

}
