package io.github.notapresent.usersampler.common.sampling;

import com.google.common.collect.ImmutableMap;
import io.github.notapresent.usersampler.common.site.SiteAdapter;
import io.github.notapresent.usersampler.common.site.SiteRegistry;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class OrchestratorTest {
    @Mock private SampleStorage sampleStorage;
    @Mock private Sampler sampler;
    @Mock private SiteRegistry registry;
    @Mock private Sample sample;
    @Mock private SiteAdapter site;
    private LocalDateTime utcNow = LocalDateTime.now(ZoneOffset.UTC);

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void itShouldCallRightMethods() {
        List<SiteAdapter> sites = Collections.singletonList(site);
        Map<SiteAdapter, Sample> samples = new ImmutableMap.Builder<SiteAdapter, Sample>()
                .put(site, sample)
                .build();

        when(registry.getAdapters()).thenReturn(sites);
        when(sampler.takeSamples(any())).thenReturn(samples);

        Orchestrator orchestrator = new Orchestrator(sampleStorage, sampler, registry, () -> utcNow);
        orchestrator.run();

        verify(registry).getAdapters();
        verify(sampler).takeSamples(sites);
        verify(sampleStorage).put(sample, utcNow);
    }

}
