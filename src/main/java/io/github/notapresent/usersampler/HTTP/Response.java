package io.github.notapresent.usersampler.HTTP;

import java.util.Map;

public interface Response {
    int getStatus();

    byte[] getContentBytes();

    String getFinalUrl();

    Map<String, String> getHeaders();
}
