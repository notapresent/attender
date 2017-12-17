package io.github.notapresent.usersampler.common.sampling;

import io.github.notapresent.usersampler.common.site.SiteAdapter;

import java.time.LocalDateTime;

public interface SampleStorage {
    void put(Sample sample);
    Iterable<Sample> getForSiteByDate(SiteAdapter site, LocalDateTime date);
    void deleteFromSiteByDate(SiteAdapter site, LocalDateTime day);
    void putAggregateSample(AggregateSample sample);
}
