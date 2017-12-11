package io.github.notapresent.usersampler.common.sampling;

import io.github.notapresent.usersampler.common.site.SiteAdapter;

import javax.annotation.Nullable;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Map;

public class Sample {
    private final String siteShortName;
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


    public String getSiteShortName() {
        return siteShortName;
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

    public Sample(String siteShortName, // OK constructor
                  Map<String, UserStatus> payload) {
        this(siteShortName, payload, SampleStatus.OK, null);
    }

    public Sample(String siteShortName,     // HTTPError constructor
                  String message) {
        this(siteShortName, null, SampleStatus.ERROR, message);
    }

    public Sample(String siteShortName,
                  Map<String, UserStatus> payload,
                  SampleStatus sampleStatus, String message) {
        this( siteShortName,
            ZonedDateTime.now(ZoneOffset.UTC),
            payload,
            sampleStatus,
            message
        );
    }

    public Sample(String siteShortName,
                  ZonedDateTime taken,
                  Map<String, UserStatus> payload,
                  SampleStatus sampleStatus, String message) {
        this.siteShortName = siteShortName;
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
