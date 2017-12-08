package io.github.notapresent.usersampler.common.HTTP;

import java.net.CookieHandler;

abstract public class Session {
    private CookieHandler cookieManager;

    public CookieHandler getCookieManager() {
        return cookieManager;
    }

    public void setCookieManager(CookieHandler cookieManager) {
        this.cookieManager = cookieManager;
    }

    abstract public Response send(Request request);
}
