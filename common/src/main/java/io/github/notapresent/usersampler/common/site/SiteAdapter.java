package io.github.notapresent.usersampler.common.site;

import io.github.notapresent.usersampler.common.HTTP.Request;
import io.github.notapresent.usersampler.common.HTTP.RequestFactory;
import io.github.notapresent.usersampler.common.HTTP.Response;
import io.github.notapresent.usersampler.common.sampling.Status;

import java.util.List;
import java.util.Map;

public interface SiteAdapter {
    void setRequestFactory(RequestFactory requestFactory);

    default String getAlias() {
        return this.getClass().getCanonicalName();
    }
    String shortName();

    boolean isDone();

    List<Request> getRequests();
    void registerResponse(Response resp);
    void reset();

    Map<String, Status> getResult();

}
