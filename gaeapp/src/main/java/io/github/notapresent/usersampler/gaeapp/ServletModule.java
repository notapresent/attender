package io.github.notapresent.usersampler.gaeapp;

import com.googlecode.objectify.ObjectifyFilter;

class ServletModule extends com.google.inject.servlet.ServletModule {
    @Override
    protected void configureServlets() {
        filter("/*").through(ObjectifyFilter.class);
        serve("/sampler").with(SamplerServlet.class);
        serve("/info").with(SysinfoServlet.class);
    }
}
