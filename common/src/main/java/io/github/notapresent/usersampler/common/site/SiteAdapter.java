package io.github.notapresent.usersampler.common.site;

import io.github.notapresent.usersampler.common.HTTP.Request;
import io.github.notapresent.usersampler.common.HTTP.RequestFactory;
import io.github.notapresent.usersampler.common.HTTP.Response;
import io.github.notapresent.usersampler.common.sampling.Status;

import java.util.Map;

public interface SiteAdapter {
    void setRequestFactory(RequestFactory requestFactory);

    default String getAlias() {
        return this.getClass().getCanonicalName();
    }

    boolean isDone();

    Iterable<Request> produceRequests();

    void processResponse(Response response);

    Map<String, Status> getResult();
}
