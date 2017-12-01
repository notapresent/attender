package io.github.notapresent.usersampler.HTTP;

public interface RequestFactory {
    Request create(String url, Method method);
    Request GET(String url);
}
