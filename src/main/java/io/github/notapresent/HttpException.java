package io.github.notapresent;

public class HttpException extends RuntimeException {
    private int responseCode = 0;

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public HttpException(String message, int code) {
        super(message);
        setResponseCode(code);
    }

    public HttpException(String message) { super(message); }    // Why do i even have to do this?

    public HttpException(String message, Throwable cause) { super(message, cause); }    // ... and this

    public HttpException(Throwable cause) { super(cause); } // ...and this! Thanks, Java!
}
