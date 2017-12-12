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
import io.github.notapresent.usersampler.common.sampling.Orchestrator;
import io.github.notapresent.usersampler.common.sampling.SampleStorage;
import io.github.notapresent.usersampler.common.sampling.Sampler;
import io.github.notapresent.usersampler.common.sampling.SinglePlexer;
import io.github.notapresent.usersampler.common.site.SiteAdapter;
import io.github.notapresent.usersampler.common.site.SiteRegistry;
import io.github.notapresent.usersampler.gaeapp.HTTP.URLFetchSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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

    @Test
    public void itShouldDoItsThing() {
        Provider<Objectify> ofyProvider = ObjectifyService::ofy;
        SampleStorage storage = new GAESampleStorage(ofyProvider, SiteRegistry.getInstance());
        SiteRegistry registry = SiteRegistry.getInstance();


        orchestrator = makeOrchestrator(registry, storage); // TODO Guice.getInjector().getInstance(Orchestrator.class)
        orchestrator.run();
        for (SiteAdapter site: registry.getAdapters() ) {
            // assert storage has 1 sample for site // TODO
        }
    }


    public static Orchestrator makeOrchestrator(
            SiteRegistry registry,
            SampleStorage storage) {

        Sampler sampler = new Sampler(
                new SinglePlexer(
                        new URLFetchSession(
                                URLFetchServiceFactory.getURLFetchService()
                        )
                ),
                new RequestFactory()
        );

        return new Orchestrator(storage, sampler, registry);
    }
}

