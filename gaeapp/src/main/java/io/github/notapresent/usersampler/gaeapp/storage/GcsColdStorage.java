package io.github.notapresent.usersampler.gaeapp.storage;

import static com.google.common.io.ByteStreams.copy;

import com.google.appengine.api.appidentity.AppIdentityService;
import com.google.appengine.api.appidentity.AppIdentityServiceFactory;
import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;
import com.google.common.io.ByteSource;
import io.github.notapresent.usersampler.common.storage.ColdStorage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;


public class GcsColdStorage implements ColdStorage {
  private final GcsService gcsService = GcsServiceFactory.createGcsService(new RetryParams.Builder()
      .initialRetryDelayMillis(10)
      .retryMaxAttempts(10)
      .totalRetryPeriodMillis(15000)
      .build());

  @Override
  public void put(String path, byte[] content) throws IOException {
    AppIdentityService appIdentity = AppIdentityServiceFactory.getAppIdentityService();
    String defaultBucketName = appIdentity.getDefaultGcsBucketName();
    put(path, content, defaultBucketName);
  }

  public void put(String path, byte[] content, String bucketName) throws IOException {
    GcsFileOptions instance = GcsFileOptions.getDefaultInstance();
    GcsFilename fileName = new GcsFilename(bucketName, path);
    GcsOutputChannel outputChannel;
    outputChannel = gcsService.createOrReplace(fileName, instance);
    copyAndClose(ByteSource.wrap(content).openStream(), Channels.newOutputStream(outputChannel));
  }

  private static long copyAndClose(InputStream from, OutputStream to) throws IOException {
    try {
      return copy(from, to);
    }
    finally {
      from.close();
      to.close();
    }
  }
}
