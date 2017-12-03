package io.github.notapresent.usersampler.common.HTTP;

import java.util.Map;

public interface Request {
    String getUrl();

    Method getMethod();

    RedirectPolicy getRedirectHandlingPolicy();

    void setRedirectHandlingPolicy(RedirectPolicy policy);

    double getTimeout();

    void setTimeout(double timeout);

    Map<String, String> getHeaders();

    enum RedirectPolicy {
        FOLLOW,
        DO_NOT_FOLLOW,
        DEFAULT;    // Default redirect handling policy for underlying implementation

        private RedirectPolicy() {
        }
    }
}
