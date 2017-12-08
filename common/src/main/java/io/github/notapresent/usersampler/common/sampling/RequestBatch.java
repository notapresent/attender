package io.github.notapresent.usersampler.common.sampling;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import io.github.notapresent.usersampler.common.HTTP.Request;
import io.github.notapresent.usersampler.common.site.SiteAdapter;

import java.util.*;
import java.util.stream.Collectors;

public class RequestBatch {
    private Map<Request, SiteAdapter> req2site = new HashMap<>();

    public void put(Request request, SiteAdapter site) {
        req2site.put(request, site);
    }

    public boolean isEmpty() {
        return req2site.isEmpty();
    }

    public List<Request> requests() {
        return ImmutableList.copyOf(req2site.keySet());
    }

    public SiteAdapter siteFor(Request req) {
        return req2site.get(req);
    }

    public Set<SiteAdapter> sites() {
        return ImmutableSet.copyOf(req2site.values());
    }

    public List<Request> requestsForSite(SiteAdapter site) {
        return req2site.entrySet()
                .stream()
                .filter((e) -> e.getValue() == site)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}
