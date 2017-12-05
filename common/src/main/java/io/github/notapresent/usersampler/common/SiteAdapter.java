package io.github.notapresent.usersampler.common;

import io.github.notapresent.usersampler.common.HTTP.Request;
import io.github.notapresent.usersampler.common.HTTP.RequestFactory;
import io.github.notapresent.usersampler.common.HTTP.Response;

import java.util.Map;

public interface SiteAdapter {
    void setRequestFactory(RequestFactory requestFactory);

    String getAlias();

    boolean isDone();

    Iterable<Request> produceRequests();

    void processResponse(Request request, Response response);

    Map<String, Status> getResult();
}
