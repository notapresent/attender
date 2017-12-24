package io.github.notapresent.usersampler.gaeapp;

import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.googlecode.objectify.ObjectifyFilter;
import io.github.notapresent.usersampler.common.http.RequestFactory;
import io.github.notapresent.usersampler.common.http.RequestMultiplexer;
import io.github.notapresent.usersampler.common.http.RetryingSinglePlexer;
import io.github.notapresent.usersampler.common.http.Session;
import io.github.notapresent.usersampler.common.sampling.Orchestrator;
import io.github.notapresent.usersampler.common.sampling.Sampler;
import io.github.notapresent.usersampler.common.site.SiteRegistry;
import io.github.notapresent.usersampler.common.storage.HotStorage;
import io.github.notapresent.usersampler.common.storage.TubeFactory;
import io.github.notapresent.usersampler.gaeapp.http.UrlFetchSession;
import io.github.notapresent.usersampler.gaeapp.storage.OfyStorage;
import io.github.notapresent.usersampler.gaeapp.storage.OfyTubeFactory;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.time.Instant;

class ServicesModule extends AbstractModule {

  @Override
  protected void configure() {
    OfyStorage.registerEntities();
    bind(ObjectifyFilter.class).in(Singleton.class);

    bind(RequestFactory.class).in(Singleton.class);
    bind(Sampler.class);
    bind(Session.class).to(UrlFetchSession.class);
    bind(TubeFactory.class).to(OfyTubeFactory.class);

    bind(RequestMultiplexer.class).to(RetryingSinglePlexer.class);
    bind(CookieHandler.class).to(CookieManager.class);
    bind(Orchestrator.class);
    bind(HotStorage.class).to(OfyStorage.class);
    bind(SiteRegistry.class).in(Singleton.class);
  }

  @Provides
  URLFetchService provideUrlFetchService() {
    return URLFetchServiceFactory.getURLFetchService();
  }

  @Provides private Instant provideNow() {
    return Instant.now();
  }
}
