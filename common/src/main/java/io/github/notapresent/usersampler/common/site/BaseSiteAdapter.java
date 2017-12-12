package io.github.notapresent.usersampler.common.site;

import com.google.inject.Inject;
import io.github.notapresent.usersampler.common.HTTP.RequestFactory;

abstract public class BaseSiteAdapter {

    protected boolean done = false;

    public boolean isDone() {
        return done;
    }

    protected void reset() {
        done = false;
    }
}
