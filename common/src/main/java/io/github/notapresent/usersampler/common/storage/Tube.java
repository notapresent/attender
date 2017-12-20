package io.github.notapresent.usersampler.common.storage;

import io.github.notapresent.usersampler.common.sampling.Sample;
import io.github.notapresent.usersampler.common.sampling.SampleStatus;
import io.github.notapresent.usersampler.common.site.SiteAdapter;

import java.time.Instant;

public interface Tube {
    public Long getId();
    public String getSiteId();
    public Instant getTaken();
    public Sample getSample();
    public SampleStatus getStatus();
}
