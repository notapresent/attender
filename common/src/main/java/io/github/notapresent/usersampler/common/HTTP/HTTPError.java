package io.github.notapresent.usersampler.common.HTTP;

public class HTTPError extends RuntimeException {
    public HTTPError(String message) {
        super(message);
    }

    public HTTPError(Throwable cause) {
        super(cause);
    }

    public HTTPError(String message, Throwable cause) {
        super(message, cause);
    }
}
