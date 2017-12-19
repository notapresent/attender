package io.github.notapresent.usersampler.gaeapp;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.common.collect.Lists;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cache.AsyncCacheFilter;
import com.googlecode.objectify.util.Closeable;
import io.github.notapresent.usersampler.common.sampling.Sample;
import io.github.notapresent.usersampler.common.sampling.SampleStatus;
import io.github.notapresent.usersampler.common.sampling.SampleStorage;
import io.github.notapresent.usersampler.common.site.SiteAdapter;
import io.github.notapresent.usersampler.common.site.SiteRegistry;
import io.github.notapresent.usersampler.gaeapp.storage.OfySampleStorage;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class OfySampleStorageTest {
    private SampleStorage storage;
    private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
            new LocalDatastoreServiceTestConfig()
                    .setApplyAllHighRepJobPolicy()
    );
    private Closeable ofySession;

    @Mock
    private SiteAdapter site;
    @Mock
    private SiteAdapter otherSite;


    private final LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
    private final LocalDate today = now.toLocalDate();
    private Sample sample;

    @BeforeClass
    public static void setUpClass() {
        Logger.getLogger("com.google.appengine.api.datastore.dev.LocalDatastoreService")
                .setLevel(Level.WARNING);
        ObjectifyService.setFactory(new ObjectifyFactory());
        OfySampleStorage.registerEntities();
    }

    @Before
    public void setUp() {
        ofySession = ObjectifyService.begin();
        helper.setUp();

        initMocks(this);

        when(site.shortName()).thenReturn("T");
        when(otherSite.shortName()).thenReturn("O");

        storage = new OfySampleStorage(new SiteRegistry());
        sample = new Sample(site, now, new HashMap<>(), SampleStatus.OK);
    }

    @After
    public void tearDown() {
        AsyncCacheFilter.complete();
        ofySession.close();
        helper.tearDown();
    }

    @Test
    public void itShouldPersistSampleEntry() {
        storage.put(sample);
        List<Sample> persisted = Lists.newArrayList(storage.getForSiteByDate(site, today));
        assertEquals(1, persisted.size());
        assertEquals(now, persisted.iterator().next().getTaken());
    }

    @Test
    public void gfsdShouldFilterBySite() {
        Sample otherSample = new Sample(otherSite, now, new HashMap<>(), SampleStatus.OK);
        storage.put(sample);
        storage.put(otherSample);

        List<Sample> persisted = Lists.newArrayList(storage.getForSiteByDate(site, today));

        assertEquals(1, persisted.size());
        Sample persistedSample = persisted.get(0);
        assertEquals(now, persistedSample.getTaken());
    }

    @Test
    public void gfsdShouldFilterByDate() {
        LocalDateTime yesterday = LocalDateTime.now(ZoneOffset.UTC).minusDays(1);
        Sample oldSample = new Sample(site, yesterday, new HashMap<>(), SampleStatus.OK);
        storage.put(sample);
        storage.put(oldSample);

        List<Sample> persisted = Lists.newArrayList(storage.getForSiteByDate(site, today));
        assertEquals(1, persisted.size());
        Sample persistedSample = persisted.get(0);
        assertEquals(now, persistedSample.getTaken());
    }
}