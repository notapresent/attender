package io.github.notapresent.usersampler.common.HTTP;

public class RequestFactory {
    public Request create(String url, Method method) {
        return new Request(url, method);
    }

    public Request create(String url) {
        return create(url, Method.GET);
    }
}
