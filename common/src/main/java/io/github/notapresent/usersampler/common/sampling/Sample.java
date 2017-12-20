package io.github.notapresent.usersampler.common.sampling;

import io.github.notapresent.usersampler.common.site.SiteAdapter;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

public class Sample {
    private final Map<String, UserStatus> payload;
    private final SampleStatus sampleStatus;

    public Map<String, UserStatus> getPayload() {
        return payload;
    }

    public SampleStatus getSampleStatus() {
        return sampleStatus;
    }

    public Sample(Map<String, UserStatus> payload) {    // OK constructor
        this(payload, SampleStatus.OK);
    }

    public Sample(Map<String, UserStatus> payload, SampleStatus sampleStatus) {
        this.payload = payload;
        this.sampleStatus = sampleStatus;
    }
}
