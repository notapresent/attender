package io.github.notapresent.usersampler.common.storage;

import io.github.notapresent.usersampler.common.sampling.Sample;
import io.github.notapresent.usersampler.common.sampling.SampleStatus;
import java.time.Instant;

public interface Tube {

  Long getId();

  String getSiteId();

  Instant getTaken();

  Sample getSample();

  SampleStatus getStatus();
}
