package io.github.notapresent.usersampler.common.site;

abstract public class BaseSiteAdapter {

  protected boolean done = false;

  public boolean isDone() {
    return done;
  }

  protected void reset() {
    done = false;
  }
}
