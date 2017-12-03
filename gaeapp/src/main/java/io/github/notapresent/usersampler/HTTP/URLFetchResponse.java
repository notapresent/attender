package io.github.notapresent.usersampler.HTTP;

import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPResponse;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.unmodifiableMap;

public class URLFetchResponse implements Response {
    private int status;
    private byte[] content;
    private Map<String, String> headers;
    private String finalUrl;

    public URLFetchResponse(int status, byte[] content, Map<String, String> headers, String finalUrl) {
        this.status = status;
        this.content = content;
        this.headers = headers;
        this.finalUrl = finalUrl;
    }

    public static URLFetchResponse fromHTTPResponse(HTTPResponse response) {
        URL finalUrl = response.getFinalUrl();
        return new URLFetchResponse(
                response.getResponseCode(),
                response.getContent(),
                headersListToMap(response.getHeadersUncombined()),
                 finalUrl == null ? null : finalUrl.toString());
    }

    protected static Map<String, String> headersListToMap(List<HTTPHeader> headersList) {
        Map<String, String> headersMap = new HashMap<>();
        for (HTTPHeader header : headersList) {
            headersMap.merge(header.getName(), header.getValue(), (oldVal, val) -> oldVal == null ? val : oldVal + ", " + val);
        }
        return headersMap;
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public byte[] getContentBytes() {
        return content;
    }

    @Override
    public String getFinalUrl() {
        return finalUrl;
    }

    @Override
    public Map<String, String> getHeaders() {
        return unmodifiableMap(headers);
    }
}
