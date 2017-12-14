package io.github.notapresent.usersampler.common.sampling;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import io.github.notapresent.usersampler.common.site.SiteAdapter;


public class AggregatorTest {
    @Mock
    SiteAdapter mockSite;    

    @Before
    public void setUp() {
        initMocks(this);
        when(mockSite.shortName()).thenReturn("MS");
    }

    @Test
    public void itShouldReturnAggregatedSample() {
        Aggregator aggr = new Aggregator();
        List<Sample> samples = Collections.singletonList(
            new Sample(mockSite, 
            new HashMap<>(),
            SampleStatus.OK,
            "")
        );
        AggregateSample aggrSample = aggr.aggregate(samples);
    }
}
