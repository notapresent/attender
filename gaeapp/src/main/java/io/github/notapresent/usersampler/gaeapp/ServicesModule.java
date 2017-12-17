package io.github.notapresent.usersampler.gaeapp;

import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFilter;
import com.googlecode.objectify.ObjectifyService;
import io.github.notapresent.usersampler.common.HTTP.RequestFactory;
import io.github.notapresent.usersampler.common.HTTP.RequestMultiplexer;
import io.github.notapresent.usersampler.common.HTTP.RetryingSinglePlexer;
import io.github.notapresent.usersampler.common.HTTP.Session;
import io.github.notapresent.usersampler.common.sampling.Orchestrator;
import io.github.notapresent.usersampler.common.sampling.SampleStorage;
import io.github.notapresent.usersampler.common.sampling.Sampler;
import io.github.notapresent.usersampler.common.site.SiteRegistry;
import io.github.notapresent.usersampler.gaeapp.HTTP.URLFetchCookieManager;
import io.github.notapresent.usersampler.gaeapp.HTTP.URLFetchSession;

import java.net.CookieHandler;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

class ServicesModule extends AbstractModule {
    @Override
    protected void configure() {
        OfySampleStorage.registerEntities();
        bind(ObjectifyFilter.class).in(Singleton.class);

        bind(RequestFactory.class).in(Singleton.class);
        bind(Sampler.class);
        bind(Session.class).to(URLFetchSession.class);
        bind(LocalDateTime.class).annotatedWith(Names.named("utcNow")).toProvider(this::provideUTCNow);


        bind(RequestMultiplexer.class).to(RetryingSinglePlexer.class);
        bind(CookieHandler.class).to(URLFetchCookieManager.class);
        bind(Orchestrator.class);
        bind(SampleStorage.class).to(OfySampleStorage.class);
        bind(SiteRegistry.class).in(Singleton.class);
    }

    @Provides
    URLFetchService provideURLFetchService() {
        return URLFetchServiceFactory.getURLFetchService();
    }

    @Provides
    private LocalDateTime provideUTCNow() {
        return LocalDateTime.now(ZoneOffset.UTC);
    }
}
