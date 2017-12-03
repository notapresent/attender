package io.github.notapresent.usersampler.common.HTTP;

public class Error extends RuntimeException {
    public Error(String message) {
        super(message);
    }

    public Error(String message, Throwable cause) {
        super(message, cause);
    }
}
