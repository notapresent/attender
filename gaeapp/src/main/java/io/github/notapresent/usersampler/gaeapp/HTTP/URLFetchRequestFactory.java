package io.github.notapresent.usersampler.gaeapp.HTTP;

import io.github.notapresent.usersampler.common.HTTP.Request;
import io.github.notapresent.usersampler.common.HTTP.Method;
import io.github.notapresent.usersampler.common.HTTP.RequestFactory;

public class URLFetchRequestFactory extends RequestFactory {
    @Override
    public Request create(String url, Method method) {
        return new URLFetchRequest(url, method);
    }
}
