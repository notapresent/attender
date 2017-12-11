package io.github.notapresent.usersampler.gaeapp;

import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.google.appengine.api.utils.SystemProperty;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.notapresent.usersampler.common.HTTP.RequestFactory;
import io.github.notapresent.usersampler.common.HTTP.Session;
import io.github.notapresent.usersampler.common.sampling.Sample;
import io.github.notapresent.usersampler.common.sampling.Sampler;
import io.github.notapresent.usersampler.common.sampling.SinglePlexer;
import io.github.notapresent.usersampler.common.site.SiteRegistry;
import io.github.notapresent.usersampler.gaeapp.HTTP.URLFetchSession;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.ZoneId;
import java.util.List;
import java.util.Properties;

@Singleton
public class SamplerServlet extends HttpServlet {
    @Inject
    public SamplerServlet() {
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        SiteRegistry siteRegistry = SiteRegistry.getInstance();
        int numAdapters = siteRegistry.getAdapters().size();

        Properties properties = System.getProperties();

        response.setContentType("text/plain; charset=utf-8");
        String message = "App Engine Standard using %s%n" +
                "Java %s%n";
        message += "System default timezone is: " + ZoneId.systemDefault().toString() + "\n";
        message += numAdapters + " sn dapters loaded%n";

        response.getWriter().format(
                message,
                SystemProperty.version.get(),
                properties.get("java.specification.version")
        );
    }
}