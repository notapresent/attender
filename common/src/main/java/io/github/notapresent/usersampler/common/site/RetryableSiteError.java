package io.github.notapresent.usersampler.common.site;

public class RetryableSiteError extends SiteError {

  public RetryableSiteError(String message, Throwable cause) {
    super(message, cause);
  }

  public RetryableSiteError(String message) {
    super(message);
  }
}
