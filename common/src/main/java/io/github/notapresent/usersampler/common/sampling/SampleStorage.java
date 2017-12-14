package io.github.notapresent.usersampler.common.sampling;

import io.github.notapresent.usersampler.common.site.SiteAdapter;

import java.time.LocalDateTime;
import java.util.List;

public interface SampleStorage {
    void put(Sample sample);
    List<Sample> getForSiteDate(SiteAdapter site, LocalDateTime date);  // TODO iterable?
    void deleteByIds(Iterable<Long> ids);
    void putAggregateSample(AggregateSample sample);
}
