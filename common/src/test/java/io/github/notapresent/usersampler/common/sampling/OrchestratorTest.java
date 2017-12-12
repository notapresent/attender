package io.github.notapresent.usersampler.common.sampling;

import io.github.notapresent.usersampler.common.site.SiteAdapter;
import io.github.notapresent.usersampler.common.site.SiteRegistry;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class OrchestratorTest {
    private Orchestrator orchestrator;
    @Mock private SampleStorage sampleStorage;
    @Mock private Sampler sampler;
    @Mock private SiteRegistry registry;
    @Mock private Sample sample;
    @Mock private SiteAdapter site;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void itShouldCallRightMethods() {
        List<SiteAdapter> sites =  Arrays.asList(site);
        List<Sample> samples = Arrays.asList(sample);

        when(registry.getAdapters()).thenReturn(sites);
        when(sampler.takeSamples(any())).thenReturn(samples);

        orchestrator = new Orchestrator(sampleStorage, sampler, registry);
        orchestrator.run();

        verify(registry).getAdapters();
        verify(sampler).takeSamples(sites);
        verify(sampleStorage).put(sample);
    }

}
