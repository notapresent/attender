package io.github.notapresent.usersampler.gaeapp.HTTP;

import com.google.appengine.api.urlfetch.HTTPHeader;

import java.util.List;

public class URLFetchUtil {
    public static boolean isRedirect(int code) {
        return code >= 301 && code <= 303;
    }

    public static String getHeaderValue(List<HTTPHeader> headers, String name) {
        for (HTTPHeader hdr : headers) {
            if (hdr.getName().equalsIgnoreCase(name)) {
                return hdr.getValue();
            }
        }
        return null;
    }
}
