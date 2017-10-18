package io.github.notapresent;

import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class URLFetchHelpers {
    public static boolean isRedirect(HTTPResponse resp) {
        int responseCode = resp.getResponseCode();
        return (responseCode == 301 || responseCode == 302);
    }

    public static String getHeader(HTTPResponse resp, String headerName) {
        headerName = headerName.toLowerCase();
        for(HTTPHeader hdr : resp.getHeaders()) {
            if(hdr.getName().equalsIgnoreCase(headerName)) {
                return hdr.getValue();
            }
        }
        return null;
    }

    public static Map<String, List<String>> responseHeaders(HTTPResponse resp) {
        Map<String, List<String>> rv = new HashMap<>();
        for(HTTPHeader hdr : resp.getHeadersUncombined()) {
            String key = hdr.getName().toLowerCase();
            String value = hdr.getValue();
            if(value == null || value.length() == 0) {
                continue;
            }
            rv.putIfAbsent(key, new ArrayList<>());
            rv.get(key).add(value);
        }
        return rv;
    }
}
