package io.github.notapresent.usersampler.common.sampling;

import java.time.ZonedDateTime;

public interface SampleStorage {
    void put(Sample sample);
    Iterable<Sample> getForDate(ZonedDateTime date);
    void deleteByIds(Iterable<Long> ids);
    void putAggregateSample(AggregateSample sample);
}
