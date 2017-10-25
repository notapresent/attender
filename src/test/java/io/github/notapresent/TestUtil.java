package io.github.notapresent;

import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPResponse;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class TestUtil {
    public static HTTPResponse makeResponse(int code, String content, List<HTTPHeader> headers) {
        return new HTTPResponse(code, content.getBytes(), null, headers);
    }

    public static HTTPResponse makeResponse(int code, String content) {
        return makeResponse(code, content, new LinkedList<>());
    }

    public static HTTPResponse makeRedirectResponse(int code, String location) {
        List<HTTPHeader> headers = Collections.singletonList(
                new HTTPHeader("location", location));
        return makeResponse(code, "", headers);
    }
}
