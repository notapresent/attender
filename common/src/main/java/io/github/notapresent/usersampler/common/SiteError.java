package io.github.notapresent.usersampler.common;

public class SiteError extends RuntimeException {
    public SiteError(String message, Throwable cause) {
        super(message, cause);
    }

    public SiteError(String message) {
        super(message);
    }
}
