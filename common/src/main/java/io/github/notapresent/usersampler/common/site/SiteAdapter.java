package io.github.notapresent.usersampler.common.site;

import io.github.notapresent.usersampler.common.HTTP.Request;
import io.github.notapresent.usersampler.common.HTTP.RequestFactory;
import io.github.notapresent.usersampler.common.HTTP.Response;
import io.github.notapresent.usersampler.common.sampling.UserStatus;

import java.util.List;
import java.util.Map;

public interface SiteAdapter {
    default String getAlias() {
        return this.getClass().getCanonicalName();
    }
    String shortName();

    boolean isDone();

    List<Request> getRequests(RequestFactory requestFactory);
    void registerResponse(Response resp);
    void reset();

    Map<String, UserStatus> getResult();

    default UserStatus fromInt(int intStatus) {
        return UserStatus.fromValue(intStatus);
    }
}
