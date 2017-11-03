package io.github.notapresent;

import com.google.inject.servlet.ServletModule;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class AttenderGuiceServletContextListener extends GuiceServletContextListener {

  @Override protected Injector getInjector() {
    return Guice.createInjector(
        new AttenderServletModule(),
        new SamplerModule()
      );
  }
}