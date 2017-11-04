package io.github.notapresent;

import com.google.common.base.Preconditions;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class GuiceServletConfig extends GuiceServletContextListener {
    private static final String SERVICES_PROPERTIES = "/WEB-INF/services.properties";
    private ServletContext servletContext;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        servletContext = servletContextEvent.getServletContext();
        super.contextInitialized(servletContextEvent);
    }

    private Properties loadProperties(String fileName) {
        Properties properties = new Properties();

        try (InputStream in = servletContext.getResourceAsStream(fileName)) {
            Preconditions.checkNotNull(in, "The configuration file " + fileName + " can not be found");
            properties.load(in);
        } catch (IOException e) {
            throw new RuntimeException("I/O Exception during loading configuration");
        }

        return properties;
    }

    @Override
    protected Injector getInjector() {
        return Guice.createInjector(
                new ServletConfig(),
                new ServicesModule(loadProperties(SERVICES_PROPERTIES))
        );
    }
}