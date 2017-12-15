package io.github.notapresent.usersampler.common.HTTP;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.MalformedURLException;

abstract public class Session {
    public static int DEFAULT_MAX_REDIRECTS = 5;
    private CookieHandler cookieManager;

    public CookieHandler getCookieManager() {
        return cookieManager;
    }

    public void setCookieManager(CookieHandler cookieManager) {
        this.cookieManager = cookieManager;
    }


    public Response send(Request request) throws HTTPError {
        Response response;
        try {
            if(request.getRedirectHandlingPolicy() == Request.RedirectPolicy.FOLLOW) {
                response = sendWithRedirects(request);
            } else  {
                response = doSend(request);
            }
        } catch (MalformedURLException e) {
            throw new HTTPError("Malformed URL: " + request.getUrl(), e);
        } catch (IOException e) {
            throw new HTTPError(e);
        }

        return response;
    }

    protected Response sendWithRedirects(Request request) throws IOException {
        Request origRequest = request.clone();
        Response resp;
        int tries = 0;

        while(tries++ < DEFAULT_MAX_REDIRECTS) {
            resp = doSend(request);

            if(resp.isRedirect()) {
                String location = resp.headers.get("location");
                request = new Request(Util.absoluteUrl(request.getUrl(), location));
                request.getHeaders().putAll(origRequest.getHeaders());
            }
            else {
                return resp;
            }
        }

        throw new HTTPError("Maxinum of " + tries + " redirects exceeded");
    }

    abstract protected Response doSend(Request request) throws IOException;
}
