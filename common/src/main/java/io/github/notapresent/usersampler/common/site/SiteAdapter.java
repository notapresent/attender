package io.github.notapresent.usersampler.common.site;

import io.github.notapresent.usersampler.common.http.Request;
import io.github.notapresent.usersampler.common.http.RequestFactory;
import io.github.notapresent.usersampler.common.http.Response;
import io.github.notapresent.usersampler.common.sampling.UserStatus;
import java.util.List;
import java.util.Map;

public interface SiteAdapter {

  String shortName();

  boolean isDone();

  List<Request> getRequests(RequestFactory requestFactory);

  void registerResponse(Response resp);

  void reset();

  Map<String, UserStatus> getResult();
}
