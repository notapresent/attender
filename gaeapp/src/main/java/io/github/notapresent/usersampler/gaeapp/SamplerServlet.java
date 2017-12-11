package io.github.notapresent.usersampler.gaeapp;

import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.google.appengine.api.utils.SystemProperty;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import io.github.notapresent.usersampler.common.HTTP.RequestFactory;
import io.github.notapresent.usersampler.common.sampling.Orchestrator;
import io.github.notapresent.usersampler.common.sampling.SampleStorage;
import io.github.notapresent.usersampler.common.sampling.Sampler;
import io.github.notapresent.usersampler.common.sampling.SinglePlexer;
import io.github.notapresent.usersampler.common.site.SiteRegistry;
import io.github.notapresent.usersampler.gaeapp.HTTP.URLFetchSession;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.ZoneId;
import java.util.Properties;

@Singleton
public class SamplerServlet extends HttpServlet {
    private Orchestrator orca;

    @Inject
    public SamplerServlet() {
        this.orca = null;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        Properties properties = System.getProperties();

        response.setContentType("text/plain; charset=utf-8");
        String message = "App Engine Standard using %s%n" +
                "Java %s%n";
        message += "System default timezone is: " + ZoneId.systemDefault().toString() + "\n";
        if(orca != null) {
            int sitesProcessed = orca.run();
            message += sitesProcessed + " sites processed\n";
        } else {
            orca = OrcheFactory.get();
            int sitesProcessed = orca.run();
            message += sitesProcessed + " sites processed\n";
//            message += "Orchestrator not set, so no sites were processed\n";
        }

        response.getWriter().format(
                message,
                SystemProperty.version.get(),
                properties.get("java.specification.version")
        );
    }
}

class OrcheFactory {
    public static Orchestrator get() {
        Provider<Objectify> ofyProvider = new Provider<Objectify>(){
            @Override
            public Objectify get() {
                return ObjectifyService.ofy();
            }
        };

        SampleStorage storage = new GAESampleStorage(ofyProvider);
        Sampler sampler = new Sampler(
                new SinglePlexer(
                        new URLFetchSession(
                                URLFetchServiceFactory.getURLFetchService()
                        )
                ),
                new RequestFactory()
        );
        SiteRegistry registry = SiteRegistry.getInstance();
        return new Orchestrator(storage, sampler, registry);
    }
}