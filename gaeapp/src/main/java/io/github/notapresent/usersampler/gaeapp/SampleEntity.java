package io.github.notapresent.usersampler.gaeapp;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import io.github.notapresent.usersampler.common.sampling.Sample;
import io.github.notapresent.usersampler.common.sampling.SampleStatus;
import io.github.notapresent.usersampler.common.sampling.UserStatus;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.mapping;


@Entity
public class SampleEntity {
    @Id Long id;
    String sn;  // shortName
    SampleStatus st; // SampleStatus

    Date ts;

    Map<String, String> pl = new HashMap<>();    // Payload

    private SampleEntity() {
    }

    public Long getId() {
        return id;
    }

    public SampleEntity(String si, SampleStatus st, LocalDateTime ldt, Map<String, String> pl) {
        this.sn = si;
        this.st = st;
        this.ts = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());;
        this.pl = pl;
    }

    public static SampleEntity fromSample(Sample sample) {
        return new SampleEntity(
                sample.getSiteShortName(),
                sample.getSampleStatus(),
                sample.getTaken()
                ,payloadFromSample(sample.getPayload())
        );
    }

    public Sample toSample() {
        return new Sample(
                sn,
                LocalDateTime.ofInstant(ts.toInstant(), ZoneId.systemDefault()),
                new HashMap<>(), //payloadToSample(this.pl),
                st,
                null
        );
    }


    private static Map<String, String> payloadFromSample(Map<String, UserStatus> orig) {
        return orig.entrySet().stream().collect(
                Collectors.groupingBy(
                        (e) -> Integer.toString(e.getValue().getValue()),
                        mapping(Map.Entry::getKey, Collectors.joining(","))
                )
        );
    }

    private static Map<String, UserStatus> payloadToSample(Map<String, String> stored)  {
        Map<String, UserStatus> rv = new HashMap<>();

        for (Map.Entry<String, String> e: stored.entrySet()) {
            UserStatus st = UserStatus.fromValue(Integer.parseInt(e.getKey()));
            Arrays.stream(e.getValue().split(",")).forEach((name) -> rv.put(name, st));
        }
        return rv;
    }
}
