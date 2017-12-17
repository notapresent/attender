package io.github.notapresent.usersampler.gaeapp;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Parent;
import io.github.notapresent.usersampler.common.sampling.Sample;
import io.github.notapresent.usersampler.common.sampling.SampleStatus;
import io.github.notapresent.usersampler.common.sampling.UserStatus;
import io.github.notapresent.usersampler.common.site.SiteAdapter;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.mapping;


@Entity(name="Sample")
public class SampleEntity {
    @Id
    private Long id;

    public Key<SiteEntity> getParent() {
        return parent;
    }

    public SampleStatus getStatus() {
        return st;
    }

    public Date getTaken() {
        return ts;
    }

    public Map<String, String> getPayload() {
        return pl;
    }

    @Parent
    private Key<SiteEntity> parent;
    private SampleStatus st;
    @Index
    private Date ts;        // Date taken
    private Map<String, String> pl = new HashMap<>();    // Payload

    private SampleEntity() {}

    public Long getId() {
        return id;
    }

    public SampleEntity(Key<SiteEntity> parent, SampleStatus sampleStatus,
                        LocalDateTime taken, Map<String, String> payload) {
        this.parent = parent;
        this.st = sampleStatus;
        this.ts = Date.from(taken.atZone(ZoneOffset.UTC).toInstant());
        this.pl = payload;
    }

    public Sample toSample(SiteAdapter site) {
        return new Sample(
                site,
                LocalDateTime.ofInstant(ts.toInstant(), ZoneOffset.UTC),
                payloadToSample(pl), // payload
                st
        );
    }

    private static Map<String, UserStatus> payloadToSample(Map<String, String> stored)  {
        Map<String, UserStatus> rv = new HashMap<>();

        for (Map.Entry<String, String> e: stored.entrySet()) {
            UserStatus st = UserStatus.fromName(e.getKey());
            Arrays.stream(e.getValue().split(",")).forEach((name) -> rv.put(name, st));
        }
        return rv;
    }

    private static Map<String, String> payloadFromSample(Map<String, UserStatus> orig) {
        return orig.entrySet().stream().collect(
                Collectors.groupingBy(
                        (e) -> e.getValue().getQualifiedName(),
                        mapping(Map.Entry::getKey, Collectors.joining(","))
                )
        );
    }

    public static SampleEntity fromSample(Key<SiteEntity> parent, Sample sample) {
        return new SampleEntity(
                parent,
                sample.getSampleStatus(),
                sample.getTaken(),
                payloadFromSample(sample.getPayload())
        );
    }
}
