package io.github.notapresent.usersampler.HTTP;

public interface Request {
    String getUrl();
    Method getMethod();

    void setRedirectHandlingPolicy(RedirectHandlingPolicy policy);
    RedirectHandlingPolicy getRedirectHandlingPolicy();

    void setTimeout(double timeout);
    double getTimeout();

    enum RedirectHandlingPolicy {
        FOLLOW,
        DO_NOT_FOLLOW,
        DEFAULT;    // Default redirect handling policy for underlying implementation

        private RedirectHandlingPolicy() {}
    }
}
