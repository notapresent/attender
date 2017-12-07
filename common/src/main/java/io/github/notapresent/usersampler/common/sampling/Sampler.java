package io.github.notapresent.usersampler.common.sampling;

import com.google.inject.Inject;
import io.github.notapresent.usersampler.common.HTTP.Request;
import io.github.notapresent.usersampler.common.HTTP.Response;
import io.github.notapresent.usersampler.common.HTTP.Session;
import io.github.notapresent.usersampler.common.site.SiteAdapter;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sampler {
    private Session session;
    private List<Sample> samples;
    private ZonedDateTime startedAt;

    @Inject
    public Sampler(Session session) {
        this.session = session;
    }

    public List<Sample> takeSamples(List<SiteAdapter> adapters) {
        startedAt = ZonedDateTime.now(Clock.systemUTC());

        List<Sample> samples = new ArrayList<>(adapters.size());
        Map<SiteAdapter, Request> site2request = new HashMap<>();

        for (SiteAdapter site: adapters
             ) {
            samples.add(processSite(site));
        }

        return samples;
    }

    private Sample processSite(SiteAdapter site) {
        while (!site.isDone()) {
            for (Request req: site.produceRequests()
                 ) {
                site.processResponse(session.send(req));
            }
        }
        return new Sample(site.getAlias(), startedAt, site.getResult());
    }
}
