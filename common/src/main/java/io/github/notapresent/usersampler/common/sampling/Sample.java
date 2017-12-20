package io.github.notapresent.usersampler.common.sampling;

import java.util.Map;

public class Sample {

  private final Map<String, UserStatus> payload;
  private final SampleStatus sampleStatus;

  public Sample(Map<String, UserStatus> payload) {    // OK constructor
    this(payload, SampleStatus.OK);
  }

  public Sample(Map<String, UserStatus> payload, SampleStatus sampleStatus) {
    this.payload = payload;
    this.sampleStatus = sampleStatus;
  }

  public Map<String, UserStatus> getPayload() {
    return payload;
  }

  public SampleStatus getSampleStatus() {
    return sampleStatus;
  }
}
