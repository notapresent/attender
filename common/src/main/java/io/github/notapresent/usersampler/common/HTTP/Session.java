package io.github.notapresent.usersampler.common.HTTP;

import java.net.CookieHandler;

abstract public class Session {
    public static int DEFAULT_MAX_REDIRECTS = 5;
    private CookieHandler cookieManager;

    public CookieHandler getCookieManager() {
        return cookieManager;
    }

    public void setCookieManager(CookieHandler cookieManager) {
        this.cookieManager = cookieManager;
    }

    abstract public Response send(Request request);
}
