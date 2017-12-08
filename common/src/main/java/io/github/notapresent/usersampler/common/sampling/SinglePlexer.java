package io.github.notapresent.usersampler.common.sampling;

import com.google.common.util.concurrent.Futures;
import io.github.notapresent.usersampler.common.HTTP.Request;
import io.github.notapresent.usersampler.common.HTTP.Response;
import io.github.notapresent.usersampler.common.HTTP.Session;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

public class SinglePlexer implements RequestMultiplexer {
    private Session session;
    public SinglePlexer(Session session) {
        this.session = session;
    }

    @Override
    public Map<Request, Future<Response>> multiSend(List<Request> batch) {
        Map<Request, Future<Response>> rv = new HashMap<>();
        for (Request req: batch) {
            rv.put(req, Futures.immediateFuture(session.send(req)));
        }
        return rv;
    }
}
