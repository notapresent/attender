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
import io.github.notapresent.usersampler.common.HTTP.RetryingSinglePlexer;
import io.github.notapresent.usersampler.common.IntegrationTest;
import io.github.notapresent.usersampler.common.sampling.*;
import io.github.notapresent.usersampler.common.site.SiteAdapter;
import io.github.notapresent.usersampler.common.site.SiteRegistry;
import io.github.notapresent.usersampler.gaeapp.HTTP.URLFetchSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@Category(IntegrationTest.class)
public class OrchestratorIntegrationTest {
    private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
            new LocalDatastoreServiceTestConfig(),
            new LocalURLFetchServiceTestConfig()
    );

    private Closeable closeable;

    @Before
    public void setUp() {
        helper.setUp();
        closeable = ObjectifyService.begin();
        GAESampleStorage.registerEntities();
    }

    @After
    public void tearDown() {
        closeable.close();
        helper.tearDown();
    }

    @Test
    public void itShouldCreateOneSamplePerAdapter() {
        Provider<Objectify> ofyProvider = ObjectifyService::ofy;
        SiteRegistry registry = new SiteRegistry();
        SampleStorage storage = new GAESampleStorage(
                registry
        );
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        Orchestrator orchestrator = makeOrchestrator(registry, storage, now);
        orchestrator.run();
        for (SiteAdapter site: registry.getAdapters() ) {
            Sample sample = storage.getForSiteByDate(site, now).iterator().next();
            assertEquals(SampleStatus.OK, sample.getSampleStatus());
            assertNotEquals(0, sample.getPayload().size());
        }
    }


    private static Orchestrator makeOrchestrator(
            SiteRegistry registry,
            SampleStorage storage,
            LocalDateTime startTime) {

        Sampler sampler = new Sampler(
                new RetryingSinglePlexer(
                        new URLFetchSession(
                                URLFetchServiceFactory.getURLFetchService()
                        )
                ),
                new RequestFactory(),
                startTime
        );

        return new Orchestrator(storage, sampler, registry);
    }
}

