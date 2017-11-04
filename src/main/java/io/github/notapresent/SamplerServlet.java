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
public class SamplerServlet extends HttpServlet {
    private HTTPSession session;

    @Inject
    public SamplerServlet(HTTPSession sess) {
        this.session = sess;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        Config config = Config.getInstance(getServletContext());
        String indexUrl = config.getProperty("urls.index");

        HTTPResponse resp = session.fetch(new URL(indexUrl));

        Properties properties = System.getProperties();
        response.setContentType("text/plain");
        response.getWriter().println(
            "App Engine Standard using " + SystemProperty.version.get()
            + " Java " + properties.get("java.specification.version")
            + "\nGot "
            + new String(resp.getContent(), Charsets.UTF_8)
            + " characters"

        );
    }
}
