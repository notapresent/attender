package io.github.notapresent.usersampler.common.sampling;

import io.github.notapresent.usersampler.common.HTTP.Request;
import io.github.notapresent.usersampler.common.HTTP.Response;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

public interface RequestMultiplexer {
    Map<Request, Future<Response>> multiSend(List<Request> batch);
}