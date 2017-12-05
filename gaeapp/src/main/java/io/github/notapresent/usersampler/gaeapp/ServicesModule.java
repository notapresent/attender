package io.github.notapresent.usersampler.gaeapp;

import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.github.notapresent.usersampler.common.HTTP.RequestFactory;
import io.github.notapresent.usersampler.common.HTTP.Session;
import io.github.notapresent.usersampler.gaeapp.HTTP.URLFetchCookieManager;
import io.github.notapresent.usersampler.gaeapp.HTTP.URLFetchRequestFactory;
import io.github.notapresent.usersampler.gaeapp.HTTP.URLFetchSession;

import java.net.CookieHandler;

public class ServicesModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(RequestFactory.class).to(URLFetchRequestFactory.class);
        bind(Session.class).to(URLFetchSession.class);
        bind(CookieHandler.class).to(URLFetchCookieManager.class);
    }

    @Provides
    URLFetchService provideURLFetchService() {
        return URLFetchServiceFactory.getURLFetchService();
    }
}
