package io.github.notapresent;

import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.utils.SystemProperty;
import com.google.common.base.Charsets;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import io.github.notapresent.usersampler.HTTP.Request;
import io.github.notapresent.usersampler.HTTP.RequestFactory;
import io.github.notapresent.usersampler.HTTP.Response;
import io.github.notapresent.usersampler.HTTP.Session;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Properties;

@Singleton
public class SamplerServlet extends HttpServlet {
    private final Session session;
    private final RequestFactory requestFactory;
    private final String indexUrl;

    @Inject
    public SamplerServlet(
            Session sess,
            RequestFactory requestFactory,
            @Named("indexUrl") String indexUrl) {
        this.session = sess;
        this.indexUrl = indexUrl;
        this.requestFactory = requestFactory;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        Request req = requestFactory.GET(indexUrl);
        req.setRedirectHandlingPolicy(Request.RedirectPolicy.FOLLOW);
        Response resp = session.send(req);
        byte[] respBytes = resp.getContentBytes();
        String respStr = new String(respBytes, Charsets.UTF_8);

        Properties properties = System.getProperties();
        response.setContentType("text/plain; charset=utf-8");
        String message = "App Engine Standard using %s%n" +
                "Java %s%n%nGot %d bytes%nFirst 200 bytes are:%n%s" ;
        response.getWriter().format(
                message,
                SystemProperty.version.get(),
                properties.get("java.specification.version"),
                respBytes.length,
                respStr.substring(0, Math.min(150, respStr.length()))
        );
    }
}