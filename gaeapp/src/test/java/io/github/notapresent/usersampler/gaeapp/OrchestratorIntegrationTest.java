package io.github.notapresent.usersampler.gaeapp;

import static java.time.ZoneOffset.UTC;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalURLFetchServiceTestConfig;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.Closeable;
import io.github.notapresent.usersampler.common.http.RequestFactory;
import io.github.notapresent.usersampler.common.http.RequestMultiplexer;
import io.github.notapresent.usersampler.common.http.RetryingSinglePlexer;
import io.github.notapresent.usersampler.common.IntegrationTest;
import io.github.notapresent.usersampler.common.sampling.Orchestrator;
import io.github.notapresent.usersampler.common.sampling.Sample;
import io.github.notapresent.usersampler.common.sampling.SampleStatus;
import io.github.notapresent.usersampler.common.sampling.Sampler;
import io.github.notapresent.usersampler.common.site.SiteAdapter;
import io.github.notapresent.usersampler.common.site.SiteRegistry;
import io.github.notapresent.usersampler.common.storage.HotStorage;
import io.github.notapresent.usersampler.gaeapp.http.UrlFetchSession;
import io.github.notapresent.usersampler.gaeapp.storage.OfyStorage;
import io.github.notapresent.usersampler.gaeapp.storage.OfyTubeFactory;
import java.time.Instant;
import java.time.LocalDateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class OrchestratorIntegrationTest {

  private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
      new LocalDatastoreServiceTestConfig(),
      new LocalURLFetchServiceTestConfig()
  );

  private final Instant now = Instant.now();

  private Closeable closeable;

  private static Orchestrator makeOrchestrator(
      SiteRegistry registry,
      HotStorage storage) {
    RequestMultiplexer muxer = new RetryingSinglePlexer(
        new UrlFetchSession(URLFetchServiceFactory.getURLFetchService())
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
    HotStorage storage = new OfyStorage();
    Instant now = Instant.now();

    Orchestrator orchestrator = makeOrchestrator(registry, storage);
    orchestrator.run();
    for (SiteAdapter site : registry.getAdapters()) {
      Sample sample = storage.getForSiteByDate(
          site,
          LocalDateTime.ofInstant(now, UTC).toLocalDate())
          .iterator().next().getSample();
      assertEquals(SampleStatus.OK, sample.getSampleStatus());
      assertNotEquals(0, sample.getPayload().size());
    }
  }
}
