package io.github.notapresent.usersampler.gaeapp;

import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFilter;
import com.googlecode.objectify.ObjectifyService;
import io.github.notapresent.usersampler.common.HTTP.RequestFactory;
import io.github.notapresent.usersampler.common.HTTP.Session;
import io.github.notapresent.usersampler.common.sampling.*;
import io.github.notapresent.usersampler.common.site.SiteRegistry;
import io.github.notapresent.usersampler.gaeapp.HTTP.URLFetchCookieManager;
import io.github.notapresent.usersampler.gaeapp.HTTP.URLFetchSession;

import java.net.CookieHandler;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class ServicesModule extends AbstractModule {
    @Override
    protected void configure() {
        GAESampleStorage.registerEntities();
        bind(ObjectifyFilter.class).in(Singleton.class);

        bind(RequestFactory.class).in(Singleton.class);
        bind(Sampler.class);
        bind(Session.class).to(URLFetchSession.class);
        bind(LocalDateTime.class).annotatedWith(Names.named("utcNow")).toProvider(this::provideUTCNow);


        bind(RequestMultiplexer.class).to(SinglePlexer.class);
        bind(CookieHandler.class).to(URLFetchCookieManager.class);
        bind(Orchestrator.class);
        bind(SampleStorage.class).to(GAESampleStorage.class);
        bind(SiteRegistry.class);
    }

    @Provides
    Objectify ofyProvider() {
        return ObjectifyService.ofy();
    }

    @Provides
    URLFetchService provideURLFetchService() {
        return URLFetchServiceFactory.getURLFetchService();
    }

    @Provides
    LocalDateTime provideUTCNow() {
        return LocalDateTime.now(ZoneOffset.UTC);
    }
}
