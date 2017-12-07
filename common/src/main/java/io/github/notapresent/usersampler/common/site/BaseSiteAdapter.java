package io.github.notapresent.usersampler.common.site;

import com.google.inject.Inject;
import io.github.notapresent.usersampler.common.HTTP.RequestFactory;

abstract public class BaseSiteAdapter {
    protected RequestFactory requestFactory;
    protected boolean done = false;

    @Inject
    public void setRequestFactory(RequestFactory requestFactory) {
        this.requestFactory = requestFactory;
    }

    public boolean isDone() {
        return done;
    }
}
