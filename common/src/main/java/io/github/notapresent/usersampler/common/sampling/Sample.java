package io.github.notapresent.usersampler.common.sampling;

import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

public class Sample {
    private final String siteShortName;
    private final LocalDateTime taken;
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

    public LocalDateTime getTaken() {
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
        this(siteShortName, new HashMap<>(), SampleStatus.ERROR, message);
    }

    public Sample(String siteShortName,
                  Map<String, UserStatus> payload,
                  SampleStatus sampleStatus, String message) {
        this( siteShortName,
                LocalDateTime.now(ZoneOffset.UTC),
            payload,
            sampleStatus,
            message
        );
    }

    public Sample(String siteShortName,
                  LocalDateTime taken,
                  Map<String, UserStatus> payload,
                  SampleStatus sampleStatus, String message) {
        this.siteShortName = siteShortName;
        this.taken = taken;
        this.payload = payload;
        this.sampleStatus = sampleStatus;
        this.message = message;
    }
}
