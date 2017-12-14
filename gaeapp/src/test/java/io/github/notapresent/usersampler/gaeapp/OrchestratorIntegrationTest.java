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
import io.github.notapresent.usersampler.common.sampling.*;
import io.github.notapresent.usersampler.common.site.SiteAdapter;
import io.github.notapresent.usersampler.common.site.SiteRegistry;
import io.github.notapresent.usersampler.gaeapp.HTTP.URLFetchSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class OrchestratorIntegrationTest {
    private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
            new LocalDatastoreServiceTestConfig(),
            new LocalURLFetchServiceTestConfig()
    );

    private Closeable closeable;

    private Orchestrator orchestrator;

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

    // @Test    // TODO uncomment when adapters are ready
    public void itShouldCreateOneSamplePerAdapter() {
        Provider<Objectify> ofyProvider = ObjectifyService::ofy;
        SampleStorage storage = new GAESampleStorage(
                ofyProvider,
                SiteRegistry.getInstance()
        );
        SiteRegistry registry = SiteRegistry.getInstance();
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        orchestrator = makeOrchestrator(registry, storage, now);
        orchestrator.run();
        for (SiteAdapter site: registry.getAdapters() ) {
            Sample sample = storage.getForSiteDate(site, now).get(0);
            assertEquals(SampleStatus.OK, sample.getSampleStatus());
            assertNotEquals(0, sample.getPayload().size());
        }
    }


    public static Orchestrator makeOrchestrator(
            SiteRegistry registry,
            SampleStorage storage,
            LocalDateTime startTime) {

        Sampler sampler = new Sampler(
                new SinglePlexer(
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

