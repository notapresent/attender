package io.github.notapresent.usersampler.gaeapp;

import com.google.appengine.api.utils.SystemProperty;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.notapresent.usersampler.common.HTTP.RequestFactory;
import io.github.notapresent.usersampler.common.HTTP.Session;
import io.github.notapresent.usersampler.common.SiteService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

@Singleton
public class SamplerServlet extends HttpServlet {
    private final Session session;
    private final RequestFactory requestFactory;

    @Inject
    public SamplerServlet(
            Session sess,
            RequestFactory requestFactory) {
        this.session = sess;
        this.requestFactory = requestFactory;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        SiteService siteService = SiteService.getInstance();
        List<String> classPathURLS = Arrays.stream(
                ((URLClassLoader) (Thread.currentThread().getContextClassLoader())).getURLs())
                .map(URL::toString)
                .collect(Collectors.toList());
        String serviceMessages = "== SERVICE MESSAGES:%n" + String.join("%n",
                siteService.getMessages("<REQUEST>"));

        Properties properties = System.getProperties();

        response.setContentType("text/plain; charset=utf-8");
        String message = "App Engine Standard using %s%n" +
                "Java %s%n";
        message += "%n%n== CLASSPATH: %n" +
                String.join("%n", classPathURLS) + "%n%n";
        message += serviceMessages;

        response.getWriter().format(
                message,
                SystemProperty.version.get(),
                properties.get("java.specification.version")
        );
    }
}