package io.github.notapresent.usersampler.common.HTTP;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public interface RequestMultiplexer {
    default Map<Request, Future<Response>> multiSend(List<Request> batch) {
        return batch.stream().collect(Collectors.toMap(
                req -> req,
                this::send
        ));
    }

    Future<Response> send(Request request);

}