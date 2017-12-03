package io.github.notapresent.usersampler.HTTP;

public class URLFetchRequestFactory implements RequestFactory {
    @Override
    public Request create(String url, Method method) {
        return new URLFetchRequest(url, method);
    }

    @Override
    public Request GET(String url) {
        return create(url, Method.GET);
    }
}
