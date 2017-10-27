package io.github.notapresent;

import com.google.appengine.api.urlfetch.HTTPHeader;

import java.io.IOException;
import java.net.CookieManager;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HTTPSessionCookieManager extends CookieManager {
    public HTTPHeader load(URL url) throws IOException {

        try {
            URI uri = url.toURI();
            List<String> cookies = get(uri, new HashMap<>()).get("Cookie");

            if(cookies != null && cookies.size() > 0) {
                return new HTTPHeader("cookie", String.join("; ", cookies));
            }
            else {
                return null;
            }
        }

        catch (URISyntaxException e) {
            throw new RuntimeException("Failed to convert URL " + url + " to URI");
        }
    }

    public void save(URL url, List<HTTPHeader> responseHeaders) throws IOException {
        try {
            URI uri = url.toURI();
            Map<String, List<String>> respHeaders = convertHeaders(responseHeaders);
            put(uri, respHeaders);
        }

        catch (URISyntaxException e) {
            throw new RuntimeException("Failed to convert URL " + url + " to URI");
        }
    }

    public static Map<String, List<String>> convertHeaders(List<HTTPHeader> headers) {
        Map<String, List<String>> rv = new HashMap<>();
        for(HTTPHeader hdr : headers) {
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
