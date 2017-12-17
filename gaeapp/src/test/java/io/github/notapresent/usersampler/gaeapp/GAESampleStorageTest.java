package io.github.notapresent.usersampler.gaeapp;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalMemcacheServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.common.collect.Lists;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.VoidWork;
import com.googlecode.objectify.Work;
import com.googlecode.objectify.util.Closeable;
import io.github.notapresent.usersampler.common.sampling.Sample;
import io.github.notapresent.usersampler.common.sampling.SampleStatus;
import io.github.notapresent.usersampler.common.sampling.SampleStorage;
import io.github.notapresent.usersampler.common.site.SiteAdapter;
import io.github.notapresent.usersampler.common.site.SiteRegistry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import static com.googlecode.objectify.ObjectifyService.ofy;

public class GAESampleStorageTest {
    private SampleStorage storage;
    private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
            new LocalDatastoreServiceTestConfig().setApplyAllHighRepJobPolicy(),
            new LocalMemcacheServiceTestConfig()
    );
    private Closeable ofySession;

    @Mock
    private SiteAdapter site;
    @Mock
    private SiteAdapter otherSite;


    private final LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
    private Sample sample;

    @Before
    public void setUp() {
        helper.setUp();
        GAESampleStorage.registerEntities();
        ofySession = ObjectifyService.begin();
        initMocks(this);

        when(site.shortName()).thenReturn("T");
        when(otherSite.shortName()).thenReturn("O");

        storage = new GAESampleStorage(new SiteRegistry());
        sample = new Sample(site, now, new HashMap<>(), SampleStatus.OK, "");
    }

    @After
    public void tearDown() {
        ofySession.close();
        helper.tearDown();
    }

    @Test
    public void itShouldPersistSampleEntry() {
        storage.put(sample);

        Sample persisted = storage.getForSiteByDate(site, now).iterator().next();
        //assertEquals(sample, persisted);
        assertEquals(now, persisted.getTaken());
    }

    @Test
    public void gfsdShouldFilterByDate() {
        LocalDateTime yesterday = LocalDateTime.now(ZoneOffset.UTC).minusDays(1);
        Sample oldSample = new Sample(site, yesterday, new HashMap<>(), SampleStatus.OK, "");

        storage.put(sample);
        storage.put(oldSample);

        List<Sample> persisted = Lists.newArrayList(storage.getForSiteByDate(site, now));

        assertEquals(1, persisted.size());
        Sample persistedSample = persisted.get(0);
        assertEquals(now, persistedSample.getTaken());
    }

    @Test
    public void gfsdShouldFilterBySite() {
        Sample otherSample = new Sample(otherSite, now, new HashMap<>(), SampleStatus.OK, "");
        storage.put(sample);
        storage.put(otherSample);

        ofy().clear();
        List<Sample> persisted = Lists.newArrayList(storage.getForSiteByDate(site, now));

        assertEquals(1, persisted.size());
        Sample persistedSample = persisted.get(0);
        assertEquals(now, persistedSample.getTaken());
    }
}