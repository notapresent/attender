package io.github.notapresent;

import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;

import java.util.Properties;

public class ServicesModule extends AbstractModule {
    private final Properties serviceConfig;

    public ServicesModule(Properties props) {
        serviceConfig = props;
    }


    @Override
    protected void configure() {
        Names.bindProperties(binder(), serviceConfig);
    }

    @Provides
    URLFetchService provideURLFetchService() {
        return URLFetchServiceFactory.getURLFetchService();
    }
}
