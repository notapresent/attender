package io.github.notapresent.usersampler.gaeapp;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

public class GuiceServletConfig extends GuiceServletContextListener {
    private ServletContext servletContext;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        servletContext = servletContextEvent.getServletContext();
        super.contextInitialized(servletContextEvent);
    }

    @Override
    protected Injector getInjector() {
        return Guice.createInjector(
                new ServletModule(),
                new ServicesModule()
        );
    }
}