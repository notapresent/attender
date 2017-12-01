package io.github.notapresent.usersampler.HTTP;

import java.io.IOException;
import java.net.CookieHandler;

public interface Session <R extends Request>{
    Response send(R request) throws IOException;
    CookieHandler getCookieManager();
    void setCookieManager(CookieHandler cookieManager);
}
