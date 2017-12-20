package io.github.notapresent.usersampler.gaeapp.storage;

import io.github.notapresent.usersampler.common.sampling.Sample;
import io.github.notapresent.usersampler.common.sampling.SampleStatus;
import io.github.notapresent.usersampler.common.storage.Tube;
import io.github.notapresent.usersampler.common.storage.TubeFactory;

import java.time.Instant;

public class OfyTubeFactory  implements TubeFactory {
    @Override
    public Tube create(String siteId, Instant taken, Sample sample, SampleStatus status) {
        return new OfyTube(siteId, taken, sample, status);
    }
}
