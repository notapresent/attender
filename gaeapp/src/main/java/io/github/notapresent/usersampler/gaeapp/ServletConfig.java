package io.github.notapresent.usersampler.gaeapp;

import com.google.inject.servlet.ServletModule;
import com.googlecode.objectify.ObjectifyFilter;

class ServletConfig extends ServletModule {
    @Override
    protected void configureServlets() {
        filter("/*").through(ObjectifyFilter.class);
        serve("/sampler").with(SamplerServlet.class);
    }
}
