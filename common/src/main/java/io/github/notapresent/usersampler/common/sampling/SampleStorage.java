package io.github.notapresent.usersampler.common.sampling;

import io.github.notapresent.usersampler.common.site.SiteAdapter;

import java.time.LocalDateTime;

public interface SampleStorage {
    void put(Sample sample);
    Iterable<Sample> getForSiteDate(SiteAdapter site, LocalDateTime date);
    void deleteByIds(Iterable<Long> ids);
    void putAggregateSample(AggregateSample sample);
}
