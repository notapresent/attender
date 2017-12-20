package io.github.notapresent.usersampler.gaeapp.storage;

import static org.junit.Assert.*;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalFileServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.annotation.Stringify;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Before;
import org.junit.Test;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GcsColdStorageTest {
  private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
      new LocalFileServiceTestConfig()
  );

  @Before
  public void setUp() {
    Logger.getLogger("com.google.appengine.api.datastore.dev.LocalDatastoreService")
        .setLevel(Level.WARNING);
    helper.setUp();
  }

  @Test
  public void itShouldSaveContentToDefaultBucket() throws IOException {
    String path = "/file.txt";
    byte[] content = ("File content").getBytes();
    GcsColdStorage storage = new GcsColdStorage();
    storage.put(path, content);
  }
}