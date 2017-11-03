package io.github.notapresent;

import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class SamplerModule extends AbstractModule {
    @Override 
    protected void configure() {
    
    }
    
    @Provides
    URLFetchService provideURLFetchService() {
      return URLFetchServiceFactory.getURLFetchService();
    }
  }
