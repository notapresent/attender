package io.github.notapresent;

import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.utils.SystemProperty;
import com.google.common.base.Charsets;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

@Singleton
public class SamplerServlet extends HttpServlet {
    private final HTTPSession session;
    private final String indexUrl;

    @Inject
    public SamplerServlet(HTTPSession sess, @Named("indexUrl") String indexUrl) {
        this.session = sess;
        this.indexUrl = indexUrl;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

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
