package io.github.notapresent.usersampler.common.sampling;

import io.github.notapresent.usersampler.common.site.SiteAdapter;

import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

public class Sample {
    private final SiteAdapter site;
    private final LocalDateTime taken;
    private final Map<String, UserStatus> payload;
    private final SampleStatus sampleStatus;

    @Nullable
    public String getMessage() {
        return message;
    }

    @Nullable
    private final String message;


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
        this(site, payload, SampleStatus.OK, null);
    }

    public Sample(SiteAdapter site,     // HTTPError constructor
                  String message) {
        this(site, new HashMap<>(), SampleStatus.ERROR, message);
    }

    private Sample(SiteAdapter site,
                   Map<String, UserStatus> payload,
                   SampleStatus sampleStatus, String message) {
        this( site,
                LocalDateTime.now(ZoneOffset.UTC),
                payload,
                sampleStatus,
                message
        );
    }

    public Sample(SiteAdapter site,
                  LocalDateTime taken,
                  Map<String, UserStatus> payload,
                  SampleStatus sampleStatus, @Nullable String message) {
        this.site = site;
        this.taken = taken;
        this.payload = payload;
        this.sampleStatus = sampleStatus;
        this.message = message;
    }
}
