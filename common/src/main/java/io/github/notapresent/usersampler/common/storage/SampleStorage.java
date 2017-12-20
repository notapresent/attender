package io.github.notapresent.usersampler.common.storage;

import io.github.notapresent.usersampler.common.sampling.AggregateSample;
import io.github.notapresent.usersampler.common.sampling.Sample;
import io.github.notapresent.usersampler.common.site.SiteAdapter;
import io.github.notapresent.usersampler.common.storage.Tube;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

public interface SampleStorage {
    Long put(Tube tube);
    Iterable<? extends Tube> getForSiteByDate(SiteAdapter site, LocalDate date);
    void deleteFromSiteByDate(SiteAdapter site, LocalDate day);
    void putAggregateSample(AggregateSample sample);
}
