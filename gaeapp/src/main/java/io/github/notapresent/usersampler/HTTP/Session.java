package io.github.notapresent.usersampler.HTTP;

import java.io.IOException;
import java.net.CookieHandler;

public interface Session {
    Response send(Request request) throws IOException;

    CookieHandler getCookieManager();

    void setCookieManager(CookieHandler cookieManager);
}
