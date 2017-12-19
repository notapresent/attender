package io.github.notapresent.usersampler.gaeapp.storage;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.*;
import io.github.notapresent.usersampler.common.sampling.Sample;
import io.github.notapresent.usersampler.common.sampling.SampleStatus;
import io.github.notapresent.usersampler.common.sampling.UserStatus;
import io.github.notapresent.usersampler.common.site.SiteAdapter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

@Entity(name="Sample")
public class Tube {
    @Id private Long id;
    @Parent private Key<Site> parent;

    private SampleStatus st;

    @Index private Instant ts;        // Date taken

    @Stringify(UserStatusStringifier.class)
    private Map<UserStatus, List<String>> pl;     // Payload

    @Ignore
    private Sample sample;

    private Tube() {} // Required by ofy

    public Tube(SiteAdapter site, SampleStatus status, LocalDateTime taken,
                Sample sample) {
        parent = Key.create(Site.class, site.shortName());
        st = status;
        ts = taken.toInstant(ZoneOffset.UTC);
        this.sample = sample;
    }

    public Long getId() { return id; }

    public Key<Site> getParent() { return parent; }

    public SampleStatus getStatus() { return st; }

    public LocalDateTime getTaken() { return LocalDateTime.ofInstant(ts, ZoneOffset.UTC); }

    public Sample getSample() {
        return sample;
    }

    @OnSave
    void payloadFromSample() {
        pl = SamplePayloadCompactor.deflate(sample.getPayload());
    }

    @OnLoad
    void SampleFromPayLoad() {
        sample = new Sample(null, SamplePayloadCompactor.inflate(pl));
    }
}


