package io.github.notapresent;

import com.google.appengine.api.urlfetch.HTTPHeader;

import java.util.List;

public class HTTPUtil {
    public static boolean isRedirect(int code) {
        return code >= 301 && code <= 303;
    }

    public static String getHeader(List<HTTPHeader> headers, String name) {
        for (HTTPHeader hdr : headers) {
            if (hdr.getName().equalsIgnoreCase(name)) {
                return hdr.getValue();
            }
        }
        return null;
    }
}
