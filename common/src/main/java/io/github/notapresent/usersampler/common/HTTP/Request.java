package io.github.notapresent.usersampler.common.HTTP;

import java.util.HashMap;
import java.util.Map;

public class Request implements Cloneable {
    private final String url;
    private final Method method;
    private RedirectPolicy redirectHandlingPolicy = RedirectPolicy.DEFAULT;
    private double timeout = 5.0;
    private final Map<String, String> headers = new HashMap<>();

    public Request(String url, Method method) {
        this.url = url;
        this.method = method;
    }

    public Request(String url) {
        this(url, Method.GET);
    }

    public String getUrl() {
        return url;
    }

    public Method getMethod() {
        return method;
    }

    public double getTimeout() {
        return timeout;
    }

    public void setTimeout(double timeout) {
        this.timeout = timeout;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public RedirectPolicy getRedirectHandlingPolicy() {
        return redirectHandlingPolicy;
    }

    public void setRedirectHandlingPolicy(RedirectPolicy policy) {
        this.redirectHandlingPolicy = policy;
    }

    public enum RedirectPolicy {
        FOLLOW,
        DO_NOT_FOLLOW,
        DEFAULT;    // Default redirect handling policy for underlying implementation

        RedirectPolicy() {
        }
    }

    @Override
    public String toString() {
        return String.format("<%s@%s %s %s>",
                this.getClass().getName(),
                Integer.toHexString(hashCode()),
                this.method.name(),
                this.url);
    }

    public Request clone() {
        try {
            return  (Request) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
