package io.github.notapresent.usersampler.common.HTTP;

import java.io.IOException;
import java.net.CookieHandler;

public interface Session {
    Response send(Request request) throws Error;

    CookieHandler getCookieManager();

    void setCookieManager(CookieHandler cookieManager);
}
