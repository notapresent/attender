package io.github.notapresent.usersampler.common.sampling;

import com.google.inject.Inject;
import io.github.notapresent.usersampler.common.site.SiteAdapter;
import io.github.notapresent.usersampler.common.site.SiteRegistry;

import java.util.List;

public class Orchestrator {
    private final SampleStorage storage;
    private final Sampler sampler;
    private final SiteRegistry siteRegistry;

    @Inject
    public Orchestrator(SampleStorage storage, Sampler sampler, SiteRegistry registry) {
        this.storage = storage;
        this.sampler = sampler;
        this.siteRegistry = registry;
    }

    public int run() {
        List<SiteAdapter> sites = siteRegistry.getAdapters();
        List<Sample> samples = sampler.takeSamples(sites);
        for (Sample smp: samples) {
            storage.put(smp);
        }

        return samples.size();
    }
}

