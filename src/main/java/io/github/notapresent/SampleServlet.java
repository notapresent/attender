package io.github.notapresent;

import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.utils.SystemProperty;
import com.google.common.base.Charsets;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

@Singleton
//@WebServlet(name = "SampleServlet", value = "/sample")
public class SampleServlet extends HttpServlet {
    private Injector injector;

    @Inject
    public SampleServlet(Injector injector) {
        this.injector = injector;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        Config config = Config.getInstance(getServletContext());
        String indexUrl = config.getProperty("urls.index");

         HTTPSession sess =  injector.getInstance(HTTPSession.class);
         HTTPResponse resp = sess.fetch(new URL("http://httpbin.org/headers"));

        Properties properties = System.getProperties();
        response.setContentType("text/plain");
        response.getWriter().println(
                "App Engine Standard using " + SystemProperty.version.get()
                        + " Java " + properties.get("java.specification.version")
                        + new String(resp.getContent(), Charsets.UTF_8)

        );
    }
}
