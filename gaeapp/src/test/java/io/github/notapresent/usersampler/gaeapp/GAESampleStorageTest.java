package io.github.notapresent.usersampler.gaeapp;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.ObjectifyService;
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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class GAESampleStorageTest {
    private SampleStorage storage;
    private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
            new LocalDatastoreServiceTestConfig()
    );
    private Closeable closeable;

    @Mock
    private SiteAdapter site;
    private final LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
    private Sample sample;

    @Before
    public void setUp() {
        helper.setUp();
        closeable = ObjectifyService.begin();
        initMocks(this);
        GAESampleStorage.registerEntities();
        when(site.shortName()).thenReturn("T");
        storage = new GAESampleStorage(ObjectifyService::ofy, new SiteRegistry());
        sample = new Sample(site, now, new HashMap<>(), SampleStatus.OK, "msg");
    }

    @After
    public void tearDown() {
        closeable.close();
        helper.tearDown();
    }

    @Test
    public void itShouldPersistSampleEntry() {
        storage.put(sample);
        Sample persisted = storage.getForSiteDate(site, now).iterator().next();
        assertEquals(now, persisted.getTaken());
        assertEquals(SampleStatus.OK, persisted.getSampleStatus());
//        assertEquals("msg", persisted.getMessage());
    }

    @Test
    public void itShouldReturnPersistedEntitiesForSiteAndDate() {
        // TODO
    }
}