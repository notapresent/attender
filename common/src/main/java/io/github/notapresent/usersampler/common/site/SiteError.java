package io.github.notapresent.usersampler.common.site;

class SiteError extends RuntimeException {

  SiteError(String message, Throwable cause) {
    super(message, cause);
  }

  SiteError(String message) {
    super(message);
  }
}
