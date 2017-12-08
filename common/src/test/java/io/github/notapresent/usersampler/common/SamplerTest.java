package io.github.notapresent.usersampler.common;

import io.github.notapresent.usersampler.common.HTTP.Request;
import io.github.notapresent.usersampler.common.HTTP.Session;
import io.github.notapresent.usersampler.common.sampling.Sample;
import io.github.notapresent.usersampler.common.sampling.Sampler;
import io.github.notapresent.usersampler.common.site.SiteAdapter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import java.time.ZonedDateTime;

public class SamplerTest {
    Sampler sampler;

    @Mock
    SiteAdapter mockSite;

    @Mock
    Request mockRequest;

    @Mock
    Session mockSession;

    List<SiteAdapter> sites = new ArrayList<>();


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(mockSite.getAlias()).thenReturn("fakeSite");
        when(mockSite.getRequests()).thenReturn(Collections.singletonList(mockRequest));
        sites.add(mockSite);
        sampler = new Sampler(mockSession);
    }

    @Test
    public void itShouldResetAdaptersBeforeUse() {
        InOrder inOrder = inOrder(mockSite);
         sampler.takeSamples(sites);
        inOrder.verify(mockSite).reset();
        // TODO add getRequests() here
        inOrder.verify(mockSite).getResult();
        verify(mockSite, times(1)).reset();
    }


    @Test
    public void itShouldSetSiteAndTimeOnSamples() {
        Sample sample = sampler.takeSamples(sites).get(0);
        assertEquals(mockSite.getAlias(), sample.getSiteAlias());
        double now = (long) ZonedDateTime.now(ZoneOffset.UTC).toEpochSecond();
        assertEquals(now, sample.getTaken().toEpochSecond(), 1.0);
    }

    @Test
    public void itShouldSendAllGeneratedRequests() {

    }

    @Test
    public void itShouldRegisterAllResponses() {

    }

    @Test
    public void itShouldProcessAllRequestsBeforeAskingForMore() {

    }

    @Test
    public void itShouldMarkSampleAsFailedIfAnyRequestFails() {

    }

    @Test
    public void itShouldMarkSampleAsFailedIfAdapterSaysSo() {

    }

    @Test
    public void itShouldRetryRequestIfAdapterSaysSo() {

    }

}
