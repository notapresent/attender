package io.github.notapresent.usersampler.common.sampling;

import io.github.notapresent.usersampler.common.site.SiteAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AggregateSample {
    private Map<String, List<Segment>> username2segments = new HashMap<>();
    private final SiteAdapter site;

    public AggregateSample(SiteAdapter site, Map<String, List<Segment>> payload) {
        this.site = site;
        this.username2segments = payload;
    }
}
