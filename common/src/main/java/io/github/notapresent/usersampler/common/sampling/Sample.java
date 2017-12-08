package io.github.notapresent.usersampler.common.sampling;

import io.github.notapresent.usersampler.common.site.SiteAdapter;

import javax.annotation.Nullable;
import java.time.ZonedDateTime;
import java.util.Map;

public class Sample {
    private final SiteAdapter site;
    private final ZonedDateTime taken;
    @Nullable
    private final Map<String, Status> payload;
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

    public Map<String, Status> getPayload() {
        return payload;
    }

    public SampleStatus getSampleStatus() {
        return sampleStatus;
    }

    public Sample(SiteAdapter site, // OK constructor
                  ZonedDateTime taken,
                  Map<String, Status> payload) {
        this(site, taken, payload, SampleStatus.OK, null);
    }

    public Sample(SiteAdapter site,     // HTTPError constructor
                  ZonedDateTime taken,
                  String message) {
        this(site, taken, null, SampleStatus.ERROR, message);
    }

    public Sample(SiteAdapter site,
                  ZonedDateTime taken,
                  Map<String, Status> payload,
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
