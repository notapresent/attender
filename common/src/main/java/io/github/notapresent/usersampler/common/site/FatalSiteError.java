package io.github.notapresent.usersampler.common.site;

public class FatalSiteError extends SiteError {

  public FatalSiteError(String message, Throwable cause) {
    super(message, cause);
  }

  public FatalSiteError(String message) {
    super(message);
  }
}
