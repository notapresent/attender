package io.github.notapresent.usersampler.gaeapp;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.Closeable;
import io.github.notapresent.usersampler.common.sampling.Sample;
import io.github.notapresent.usersampler.common.sampling.SampleStorage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.googlecode.objectify.ObjectifyService.ofy;
import static org.junit.Assert.assertEquals;

public class GAESampleStorageTest {
    private SampleStorage storage;
    private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
            new LocalDatastoreServiceTestConfig()
    );
    private Closeable closeable;

    @Before
    public void setUp() {
        helper.setUp();
        closeable = ObjectifyService.begin();
        ObjectifyService.register(SampleEntity.class);
        storage = new GAESampleStorage(ObjectifyService::ofy);
    }

    @After
    public void tearDown() {
        closeable.close();
        helper.tearDown();
    }

    @Test
    public void itShouldPersistSampleEntry() {
        Sample badSample = new Sample("fakeSite", "msg");
        storage.put(badSample);
        Sample persisted = ofy().load().type(SampleEntity.class).list().get(0).toSample();
        assertEquals("fakeSite", persisted.getSiteShortName());
    }
}