package io.github.notapresent.usersampler.HTTP;

import com.google.common.base.Charsets;

import java.nio.charset.Charset;
import java.util.Map;

public interface Response {
    int getStatus();

    byte[] getContentBytes();

    String getFinalUrl();

    Map<String, String> getHeaders();

    default String getContentString() {
        return new String(getContentBytes(), Charsets.UTF_8);
    }

    default String getContentString(Charset charSet) {
        return new String(getContentBytes(), charSet);
    }
}
