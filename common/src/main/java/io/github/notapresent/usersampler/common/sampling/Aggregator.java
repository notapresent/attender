package io.github.notapresent.usersampler.common.sampling;

import io.github.notapresent.usersampler.common.sampling.AggregateSample;

public class Aggregator {
    public AggregateSample aggregate(Iterable<Sample> samples) {
        return new AggregateSample();
    }
}