package io.github.notapresent.usersampler.common.sampling;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.*;

import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import io.github.notapresent.usersampler.common.site.SiteAdapter;


public class AggregatorTest {
    Aggregator aggr = new Aggregator();


    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void itShouldRunLengthEncodeSamples() {
        List<Map<String, UserStatus>> payloads = Arrays.asList(
                ImmutableMap.of("u1", UserStatus.ONLINE),
                ImmutableMap.of("u1", UserStatus.ONLINE)
        );

        Map<String, List<Segment>> result = aggr.aggregate(payloads);
        List<Segment> segments = result.get("u1");
        assertEquals(1, segments.size());
        assertEquals(UserStatus.ONLINE, segments.get(0).getStatus());
        assertEquals(2, segments.get(0).getLength());
    }

    @Test
    public void itShouldLeftPadSequencesWithOFFLINE() {
        List<Map<String, UserStatus>> payloads = Arrays.asList(
                ImmutableMap.of("u1", UserStatus.ONLINE),
                ImmutableMap.of(
                        "u1", UserStatus.ONLINE,
                        "u2", UserStatus.ONLINE)
        );

        Map<String, List<Segment>> result = aggr.aggregate(payloads);
        List<Segment> u2segments = result.get("u2");
        assertEquals(2, u2segments.size());
        Segment segment1 = u2segments.get(0),
                segment2 = u2segments.get(1);

        assertEquals(UserStatus.OFFLINE, segment1.getStatus());
        assertEquals(1, segment1.getLength());
    }

}
