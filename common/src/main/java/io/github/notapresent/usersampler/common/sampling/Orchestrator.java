package io.github.notapresent.usersampler.common.sampling;

import com.google.inject.Inject;
import com.google.inject.Provider;
import io.github.notapresent.usersampler.common.site.SiteAdapter;
import io.github.notapresent.usersampler.common.site.SiteRegistry;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class Orchestrator {
    private final SampleStorage storage;
    private final Sampler sampler;
    private final SiteRegistry siteRegistry;
    private final Provider<LocalDateTime> dateTimeProvider;

    @Inject
    public Orchestrator(SampleStorage storage, Sampler sampler, SiteRegistry registry,
                        Provider<LocalDateTime> dateTimeProvider) {
        this.storage = storage;
        this.sampler = sampler;
        this.siteRegistry = registry;
        this.dateTimeProvider = dateTimeProvider;
    }

    public int run() {
        LocalDateTime now = dateTimeProvider.get();
        List<SiteAdapter> sites = siteRegistry.getAdapters();
        Map<SiteAdapter, Sample> site_to_samples = sampler.takeSamples(sites);
        for (Map.Entry<SiteAdapter, Sample> e: site_to_samples.entrySet()) {
            storage.put(e.getValue(), now);
            //storage.put(smp, now);
        }

        return site_to_samples.size();
    }
}

