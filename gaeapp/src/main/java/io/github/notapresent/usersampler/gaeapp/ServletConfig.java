package io.github.notapresent.usersampler.gaeapp;

import com.google.inject.servlet.ServletModule;

class ServletConfig extends ServletModule {
    @Override
    protected void configureServlets() {
        serve("/sampler").with(SamplerServlet.class);
    }
}
