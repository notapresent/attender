package io.github.notapresent.usersampler.gaeapp.storage;

import static java.time.ZoneOffset.UTC;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.common.collect.Lists;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cache.AsyncCacheFilter;
import com.googlecode.objectify.util.Closeable;
import io.github.notapresent.usersampler.common.sampling.Sample;
import io.github.notapresent.usersampler.common.sampling.SampleStatus;
import io.github.notapresent.usersampler.common.site.SiteAdapter;
import io.github.notapresent.usersampler.common.storage.HotStorage;
import io.github.notapresent.usersampler.common.storage.Tube;
import io.github.notapresent.usersampler.gaeapp.storage.OfyStorage;
import io.github.notapresent.usersampler.gaeapp.storage.OfyTube;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;

public class OfyStorageTest {

  private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
      new LocalDatastoreServiceTestConfig()
          .setApplyAllHighRepJobPolicy()
  );
  private final Instant now = Instant.now();
  private final LocalDate today = LocalDateTime.ofInstant(now, UTC).toLocalDate();
  private HotStorage storage;
  private Closeable ofySession;
  @Mock
  private SiteAdapter site;
  @Mock
  private SiteAdapter otherSite;
  private Sample sample;
  private OfyTube tube;

  @BeforeClass
  public static void setUpClass() {
    Logger.getLogger("com.google.appengine.api.datastore.dev.LocalDatastoreService")
        .setLevel(Level.WARNING);
    ObjectifyService.setFactory(new ObjectifyFactory());
    OfyStorage.registerEntities();
  }

  @Before
  public void setUp() {
    ofySession = ObjectifyService.begin();
    helper.setUp();

    initMocks(this);

    when(site.shortName()).thenReturn("T");
    when(otherSite.shortName()).thenReturn("O");

    storage = new OfyStorage();
    sample = new Sample(new HashMap<>(), SampleStatus.OK);
    tube = new OfyTube(
        site.shortName(),
        now,
        sample,
        SampleStatus.OK
    );
  }

  @After
  public void tearDown() {
    AsyncCacheFilter.complete();
    ofySession.close();
    helper.tearDown();
  }

  @Test
  public void itShoukdPersistTube() {
    storage.put(tube);
    List<Tube> persisted = Lists.newArrayList(storage.getForSiteByDate(site, today));
    assertEquals(1, persisted.size());
    assertEquals(now, persisted.iterator().next().getTaken());
  }

  @Test
  public void gfsdShouldFilterBySite() {
    Sample otherSample = new Sample(new HashMap<>(), SampleStatus.OK);
    Tube otherTube = new OfyTube(
        otherSite.shortName(),
        now,
        otherSample,
        SampleStatus.OK
    );

    storage.put(tube);
    storage.put(otherTube);

    List<Tube> persisted = Lists.newArrayList(storage.getForSiteByDate(site, today));

    assertEquals(1, persisted.size());
    Tube persistedTube = persisted.get(0);
    assertEquals(now, persistedTube.getTaken());
  }

  @Test
  public void gfsdShouldFilterByDate() {
    Instant yesterday = Instant.now().minusSeconds(60 * 60 * 24);

    Tube oldTube = new OfyTube(
        site.shortName(),
        yesterday,
        sample,
        SampleStatus.OK
    );
    storage.put(tube);
    storage.put(oldTube);

    List<Tube> persisted = Lists.newArrayList(storage.getForSiteByDate(site, today));
    assertEquals(1, persisted.size());
    Tube persistedTube = persisted.get(0);
    assertEquals(now, persistedTube.getTaken());
  }
}