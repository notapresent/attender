package io.github.notapresent.usersampler.common.sampling;

import io.github.notapresent.usersampler.common.site.SiteAdapter;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface SampleStorage {
    void put(Sample sample);
    Iterable<Sample> getForSiteByDate(SiteAdapter site, LocalDate date);
    void deleteFromSiteByDate(SiteAdapter site, LocalDate day);
    void putAggregateSample(AggregateSample sample);
}
