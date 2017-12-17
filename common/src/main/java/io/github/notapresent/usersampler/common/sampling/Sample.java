package io.github.notapresent.usersampler.common.sampling;

import io.github.notapresent.usersampler.common.site.SiteAdapter;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

public class Sample {
    private final SiteAdapter site;
    private final LocalDateTime taken;
    private final Map<String, UserStatus> payload;
    private final SampleStatus sampleStatus;

    public SiteAdapter getSite() {
        return site;
    }

    public LocalDateTime getTaken() {
        return taken;
    }

    public Map<String, UserStatus> getPayload() {
        return payload;
    }

    public SampleStatus getSampleStatus() {
        return sampleStatus;
    }

    public Sample(SiteAdapter site, // OK constructor
                  Map<String, UserStatus> payload) {
        this(site, payload, SampleStatus.OK);
    }

    private Sample(SiteAdapter site,
                   Map<String, UserStatus> payload,
                   SampleStatus sampleStatus) {
        this( site,
                LocalDateTime.now(ZoneOffset.UTC),
                payload,
                sampleStatus
        );
    }

    public Sample(SiteAdapter site,
                  LocalDateTime taken,
                  Map<String, UserStatus> payload,
                  SampleStatus sampleStatus) {
        this.site = site;
        this.taken = taken;
        this.payload = payload;
        this.sampleStatus = sampleStatus;
    }
}
