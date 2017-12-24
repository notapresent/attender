package io.github.notapresent.usersampler.common.http;

import com.google.common.collect.ImmutableMap;
import edu.emory.mathcs.backport.java.util.Arrays;
import io.github.notapresent.usersampler.common.sampling.UserStatus;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.*;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;

import static java.net.HttpURLConnection.HTTP_OK;
import static org.junit.Assert.*;

public class SessionTest {
    private static String url = "http://example.com/";
    private static URI uri;
    private static HttpCookie cookie = new HttpCookie("foo", "bar");
    private static Map<String, List<String>> setCookieHeaders =
        new ImmutableMap.Builder<String, List<String>>()
        .put("set-cookie", Collections.singletonList(cookie.toString()))
        .build();

    private SessionStub session;
    private CookieManager cookieHandler;
    private CookieStore cookieJar;

    private Request request = new Request(url);
    private Response okResponse = new Response(200, ("ok").getBytes(), url);

    private static class SessionStub extends Session {
        public LinkedList<Response> responses = new LinkedList<>();
        public LinkedList<Request> requests = new LinkedList<>();

        @Override
        protected Response doSend(Request request) throws IOException {
            requests.add(request);
            try {
                return responses.pop();
            } catch(NoSuchElementException e) {
                return null;
            }
        }
    }

    @Before
    public void setUp() throws URISyntaxException {
        cookieHandler = new CookieManager();
        cookieJar = cookieHandler.getCookieStore();
        session = new SessionStub();
        session.setCookieManager(cookieHandler);
        uri = new URI(url);
    }


    @Test
    public void itSholudSetCookiesFromHandler() throws IOException {
        cookieHandler.put(uri, setCookieHeaders);
        session.responses.push(okResponse);

        session.send(request);

        Request sent = session.requests.pop();
        Map<String, String> headers = sent.getHeaders();
        assertTrue(headers.containsKey("Cookie"));
        String cookieHeader = headers.get("Cookie");
        assertTrue(cookieHeader.contains(cookie.getName()));
        assertTrue(cookieHeader.contains(cookie.getValue()));
    }

    @Test
    public void itSholudSaveCookiesToHandler() throws IOException {
        Response setCookieResponse = new Response(HTTP_OK, ("Ok").getBytes(), url);
        setCookieResponse.getHeaders().put("set-cookie", cookie.toString());    // ???
        session.responses.push(setCookieResponse);

        session.send(request);

        String saved = String.join(";", cookieHandler.get(uri, new HashMap<>()).get("Cookie"));
        assertTrue(saved.contains(cookie.getName()));
        assertTrue(saved.contains(cookie.getValue()));
    }
}