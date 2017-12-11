package io.github.notapresent.usersampler.gaeapp;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Stringify;
import io.github.notapresent.usersampler.common.sampling.Sample;
import io.github.notapresent.usersampler.common.sampling.UserStatus;
import io.github.notapresent.usersampler.common.site.SiteRegistry;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;


@Entity
public class SampleEntity {
    @Id Long id;
    String sn;  // shortName
    Integer st; // SampleStatus
    @Stringify(ZonedDateTimeStringifier.class) ZonedDateTime ts;
    Map<Integer, List<String>> pl = new HashMap<>();    // Payload  TODO Map<Integer, String> ?

    private SampleEntity() {
    }

    public SampleEntity(String si, Integer st, ZonedDateTime ts, Map<Integer, List<String>> pl) {
        this.sn = si;
        this.st = st;
        this.ts = ts;
        this.pl = pl;
    }

    public static SampleEntity fromSample(Sample sample) {
        return new SampleEntity(
                sample.getSiteShortName(),
                sample.getSampleStatus().ordinal(),
                sample.getTaken(),
                payloadFromSample(sample.getPayload())
        );
    }

    public Sample toSample() {
        return new Sample(
                this.sn,
                this.ts,
                payloadToSample(this.pl),
                Sample.SampleStatus.values()[this.st],
                null
        );
    }


    private static Map<Integer, List<String>> payloadFromSample(Map<String, UserStatus> orig) {
        return orig.entrySet().stream().collect(
                Collectors.groupingBy(
                        (e) -> e.getValue().getValue(),
                        mapping(Map.Entry::getKey, toList())
                )
        );
    }

    private static Map<String, UserStatus> payloadToSample(
            Map<Integer, List<String>> stored)  {

        Map<String, UserStatus> rv = new HashMap<>();

        for (Map.Entry<Integer, List<String>> e: stored.entrySet()) {
            UserStatus st = UserStatus.fromValue(e.getKey());
            e.getValue().forEach((name) -> rv.put(name, st));
        }
        return rv;
    }
}


//class StatusStringifier implements Stringifier<Integer> {
//    @Override
//    public String toString(Integer value) {
//        return value.toString();
//     }
//
//    @Override
//    public Integer fromString(String str) {
//        return Integer.parseInt(str);
//    }
//}