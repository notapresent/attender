package io.github.notapresent.usersampler.gaeapp;

import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalURLFetchServiceTestConfig;
import com.google.inject.Provider;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.Closeable;
import io.github.notapresent.usersampler.common.HTTP.RequestFactory;
import io.github.notapresent.usersampler.common.HTTP.RequestMultiplexer;
import io.github.notapresent.usersampler.common.HTTP.RetryingSinglePlexer;
import io.github.notapresent.usersampler.common.IntegrationTest;
import io.github.notapresent.usersampler.common.sampling.*;
import io.github.notapresent.usersampler.common.site.SiteAdapter;
import io.github.notapresent.usersampler.common.site.SiteRegistry;
import io.github.notapresent.usersampler.common.storage.SampleStorage;
import io.github.notapresent.usersampler.gaeapp.HTTP.URLFetchSession;
import io.github.notapresent.usersampler.gaeapp.storage.OfyStorage;
import io.github.notapresent.usersampler.gaeapp.storage.OfyTubeFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static java.time.ZoneOffset.UTC;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@Category(IntegrationTest.class)
public class OrchestratorIntegrationTest {
    private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
            new LocalDatastoreServiceTestConfig(),
            new LocalURLFetchServiceTestConfig()
    );

    private final Instant now = Instant.now();

    private Closeable closeable;

    @Before
    public void setUp() {
        helper.setUp();
        closeable = ObjectifyService.begin();
        OfyStorage.registerEntities();
    }

    @After
    public void tearDown() {
        closeable.close();
        helper.tearDown();
    }

    @Test
    public void itShouldCreateOneSamplePerAdapter() {
        SiteRegistry registry = new SiteRegistry();
        SampleStorage storage = new OfyStorage();
        Instant now = Instant.now();

        Orchestrator orchestrator = makeOrchestrator(registry, storage);
        orchestrator.run();
        for (SiteAdapter site: registry.getAdapters() ) {
            Sample sample = storage.getForSiteByDate(
                    site,
                    LocalDateTime.ofInstant(now, UTC).toLocalDate())
                    .iterator().next().getSample();
            assertEquals(SampleStatus.OK, sample.getSampleStatus());
            assertNotEquals(0, sample.getPayload().size());
        }
    }

    private static Orchestrator makeOrchestrator(
            SiteRegistry registry,
            SampleStorage storage) {
        RequestMultiplexer muxer = new RetryingSinglePlexer(
                new URLFetchSession(URLFetchServiceFactory.getURLFetchService())
        );

        Sampler sampler = new Sampler(muxer, new RequestFactory());

        return new Orchestrator(
                storage,
                sampler,
                registry,
                Instant::now,
                new OfyTubeFactory()
        );
    }
}
