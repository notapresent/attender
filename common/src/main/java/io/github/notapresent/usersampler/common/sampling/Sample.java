package io.github.notapresent.usersampler.common.sampling;

import io.github.notapresent.usersampler.common.site.SiteAdapter;

import javax.annotation.Nullable;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Map;

public class Sample {
    private final SiteAdapter site;
    private final ZonedDateTime taken;
    @Nullable
    private final Map<String, UserStatus> payload;
    private final SampleStatus sampleStatus;

    @Nullable
    public String getMessage() {
        return message;
    }

    @Nullable
    private String message;


    public SiteAdapter getSite() {
        return site;
    }

    public ZonedDateTime getTaken() {
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
        this(site, payload, SampleStatus.OK, null);
    }

    public Sample(SiteAdapter site,     // HTTPError constructor
                  String message) {
        this(site, null, SampleStatus.ERROR, message);
    }

    public Sample(SiteAdapter site,
                  Map<String, UserStatus> payload,
                  SampleStatus sampleStatus, String message) {
        this.site = site;
        this.taken = ZonedDateTime.now(ZoneOffset.UTC);
        this.payload = payload;
        this.sampleStatus = sampleStatus;
        this.message = message;
    }

    public Sample(SiteAdapter site,
                  ZonedDateTime taken,
                  Map<String, UserStatus> payload,
                  SampleStatus sampleStatus, String message) {
        this.site = site;
        this.taken = taken;
        this.payload = payload;
        this.sampleStatus = sampleStatus;
        this.message = message;
    }
    public enum SampleStatus {
        OK,
        ERROR,
    }
}
