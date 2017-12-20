package io.github.notapresent.usersampler.common.sampling;

import com.google.inject.Inject;
import com.google.inject.Provider;
import io.github.notapresent.usersampler.common.site.SiteAdapter;
import io.github.notapresent.usersampler.common.site.SiteRegistry;
import io.github.notapresent.usersampler.common.storage.HotStorage;
import io.github.notapresent.usersampler.common.storage.Tube;
import io.github.notapresent.usersampler.common.storage.TubeFactory;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public class Orchestrator {

  private final HotStorage storage;
  private final Sampler sampler;
  private final SiteRegistry siteRegistry;
  private final Provider<Instant> timeProvider;
  private final TubeFactory tubeFactory;

  @Inject
  public Orchestrator(HotStorage storage, Sampler sampler, SiteRegistry registry,
      Provider<Instant> timeProvider, TubeFactory tubeFactory) {
    this.storage = storage;
    this.sampler = sampler;
    this.siteRegistry = registry;
    this.timeProvider = timeProvider;
    this.tubeFactory = tubeFactory;
  }

  public int run() {
    Instant now = timeProvider.get();
    List<SiteAdapter> sites = siteRegistry.getAdapters();
    Map<SiteAdapter, Sample> siteToSamples = sampler.takeSamples(sites);
    for (Map.Entry<SiteAdapter, Sample> e : siteToSamples.entrySet()) {
      Sample sample = e.getValue();
      Tube tube = tubeFactory.create(
          e.getKey().shortName(),
          now,
          sample,
          sample.getSampleStatus()
      );
      storage.put(tube);
    }

    return siteToSamples.size();
  }
}

