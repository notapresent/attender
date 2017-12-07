package io.github.notapresent.usersampler.common.sampling;

import com.google.inject.Inject;
import io.github.notapresent.usersampler.common.HTTP.Request;
import io.github.notapresent.usersampler.common.HTTP.Response;
import io.github.notapresent.usersampler.common.HTTP.Session;
import io.github.notapresent.usersampler.common.site.SiteAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sampler {
    private Session session;
    private List<Sample> samples;

    @Inject
    public Sampler(Session session) {
        this.session = session;
    }

    public Iterable<Sample> takeSamples(Iterable<SiteAdapter> adapters) {
        Map<SiteAdapter, Request> site2request = new HashMap<>();
        for (SiteAdapter site: adapters
             ) {

        }

        return null;
    }

    protected Iterable<Response> processBatch(Iterable<Request> requests) {
        List<Response> rv = new ArrayList<>();
        for (Request req: requests
             ) {
            rv.add(session.send(req));
        }
        return rv;
    }

    private void foo() {

    }
}
