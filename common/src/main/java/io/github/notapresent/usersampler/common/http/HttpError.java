package io.github.notapresent.usersampler.common.http;

public class HttpError extends RuntimeException {

  public HttpError(String message) {
    super(message);
  }

  public HttpError(Throwable cause) {
    super(cause);
  }

  public HttpError(String message, Throwable cause) {
    super(message, cause);
  }
}
