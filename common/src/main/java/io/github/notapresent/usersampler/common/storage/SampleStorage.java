package io.github.notapresent.usersampler.common.storage;

import io.github.notapresent.usersampler.common.sampling.AggregateSample;
import io.github.notapresent.usersampler.common.site.SiteAdapter;
import java.time.LocalDate;

public interface SampleStorage {

  Long put(Tube tube);

  Iterable<? extends Tube> getForSiteByDate(SiteAdapter site, LocalDate date);

  void deleteFromSiteByDate(SiteAdapter site, LocalDate day);

  void putAggregateSample(AggregateSample sample);
}
