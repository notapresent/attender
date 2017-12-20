package io.github.notapresent.usersampler.common.storage;

import io.github.notapresent.usersampler.common.sampling.Sample;
import io.github.notapresent.usersampler.common.sampling.SampleStatus;

import java.time.Instant;

public interface TubeFactory {
    public Tube create(String siteId, Instant taken, Sample sample, SampleStatus status);
}
