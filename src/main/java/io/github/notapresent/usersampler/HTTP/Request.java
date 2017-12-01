package io.github.notapresent.usersampler.HTTP;

import java.util.Map;

public interface Request {
    String getUrl();
    Method getMethod();

    void setRedirectHandlingPolicy(RedirectHandlingPolicy policy);
    RedirectHandlingPolicy getRedirectHandlingPolicy();

    void setTimeout(double timeout);
    double getTimeout();

    Map<String, String> getHeaders();

    enum RedirectHandlingPolicy {
        FOLLOW,
        DO_NOT_FOLLOW,
        DEFAULT;    // Default redirect handling policy for underlying implementation

        private RedirectHandlingPolicy() {}
    }
}
