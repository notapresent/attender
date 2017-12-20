package io.github.notapresent.usersampler.common.storage;

import java.io.IOException;

public interface ColdStorage {
  void put(String path, byte[] content) throws IOException;
}
