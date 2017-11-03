package io.github.notapresent;

import com.google.inject.servlet.ServletModule;

class AttenderServletModule extends ServletModule {
  @Override protected void configureServlets() {
    serve("/sample").with(SampleServlet.class);
  }
}
