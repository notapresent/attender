package io.github.notapresent.usersampler.common.HTTP;

import java.net.CookieHandler;

public interface Session {
    Response send(Request request) throws HTTPError;

    CookieHandler getCookieManager();

    void setCookieManager(CookieHandler cookieManager);
}
