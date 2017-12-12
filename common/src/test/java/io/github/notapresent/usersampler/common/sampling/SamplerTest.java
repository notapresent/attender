package io.github.notapresent.usersampler.common.sampling;

import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.Futures;
import io.github.notapresent.usersampler.common.HTTP.HTTPError;
import io.github.notapresent.usersampler.common.HTTP.Request;
import io.github.notapresent.usersampler.common.HTTP.RequestFactory;
import io.github.notapresent.usersampler.common.HTTP.Response;
import io.github.notapresent.usersampler.common.site.FatalSiteError;
import io.github.notapresent.usersampler.common.site.RetryableSiteError;
import io.github.notapresent.usersampler.common.site.SiteAdapter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;


public class SamplerTest {
    private Sampler sampler;
    private MuxerStub fakeMuxer = new MuxerStub();

    @Mock
    private SiteAdapter mockSite;
    @Mock
    private Request mockRequest;
    @Mock
    private Response mockResponse;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(mockSite.getRequests(any()))
                .thenReturn(Collections.singletonList(mockRequest));
        when(mockSite.isDone()).thenReturn(true);
        when(mockSite.shortName()).thenReturn("blah");
        sampler = new Sampler(fakeMuxer, new RequestFactory());
        fakeMuxer.response = Futures.immediateFuture(mockResponse);
    }

    @Test
    public void itShouldResetAdaptersBeforeUse() {
        InOrder inOrder = inOrder(mockSite);
         sampler.takeSamples(sites());
        inOrder.verify(mockSite).reset();
        inOrder.verify(mockSite).getRequests(any());
        inOrder.verify(mockSite).registerResponse(any());
        inOrder.verify(mockSite).getResult();
        verify(mockSite, times(1)).reset();
    }


    @Test
    public void itShouldSetSiteAndTimeOnSamples() {
        Sample sample = sampler.takeSamples(sites()).get(0);
        assertEquals(mockSite, sample.getSite());
        double now = ZonedDateTime.now(ZoneOffset.UTC).toEpochSecond();
        assertEquals(now, sample.getTaken().toEpochSecond(ZoneOffset.UTC), 1.0);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void itShouldSendAllGeneratedRequests() {
        Request req1 = mock(Request.class), req2 = mock(Request.class);
        when(mockSite.getRequests(any())).thenReturn(
                Collections.singletonList(req1),
                Collections.singletonList(req2)
        );
        when(mockSite.isDone()).thenReturn(false, true);
        sampler.takeSamples(sites());
        assertEquals(req1, fakeMuxer.requestBatches.get(0).get(0));
        assertEquals(req2, fakeMuxer.requestBatches.get(1).get(0));
    }

    @Test
    public void itShouldProcessAllRequestsBeforeAskingForMore() {}  // TODO

    @SuppressWarnings("unchecked")
    @Test
    public void itShouldMarkSampleAsFailedIfAnyRequestFails() throws Exception {
        Future failedFuture = mock(Future.class);
        when(failedFuture.get()).thenThrow(new HTTPError("Fake"));
        fakeMuxer.response = failedFuture;

        Sample sample = sampler.takeSamples(sites()).get(0);

        assertEquals(SampleStatus.ERROR, sample.getSampleStatus());
    }

    @Test
    public void itShouldMarkSampleAsFailedIfAdapterThrowsFatal() {
        doThrow(new FatalSiteError("fake")).when(mockSite).registerResponse(any());

        Sample sample = sampler.takeSamples(sites()).get(0);

        assertEquals(SampleStatus.ERROR, sample.getSampleStatus());
    }

    @Test
    public void itShouldRetryRequestIfAdapterThrowsRetryable() {
        doThrow(new RetryableSiteError("fake")).when(mockSite).registerResponse(any());

        sampler.takeSamples(sites());

        assertEquals(Sampler.MAX_BATCH_RETRIES, fakeMuxer.requestBatches.size());
    }

    @Test
    public void itShouldMarkSampleAsFailedAfterAllRetriesFailed() {
        doThrow(new RetryableSiteError("fake")).when(mockSite).registerResponse(any());

        Sample sample = sampler.takeSamples(sites()).get(0);

        assertEquals(SampleStatus.ERROR, sample.getSampleStatus());
    }

    private List<SiteAdapter> sites() {
        return Collections.singletonList(mockSite);
    }

    class MuxerStub implements RequestMultiplexer {
        Future<Response> response;
        List<List<Request>> requestBatches = new ArrayList<>();

        @Override
        public Map<Request, Future<Response>> multiSend(List<Request> batch) {
            requestBatches.add(batch);
            return ImmutableMap.of(batch.get(0), response);
        }
    }
}
