package io.github.notapresent.usersampler.HTTP;

public class Error extends RuntimeException {
    Error(String message) {
        super(message);
    }

    Error(String message, Throwable cause) {
        super(message, cause);
    }
}
