package io.github.notapresent.usersampler.gaeapp;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.notapresent.usersampler.common.sampling.Orchestrator;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Singleton
public class SamplerServlet extends HttpServlet {
    private Orchestrator orca;

    @Inject
    public SamplerServlet(Orchestrator orchestrator) {
        this.orca = orchestrator;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        int sitesProcessed = orca.run();
        String message = sitesProcessed + " sites processed\n";
        response.setContentType("text/plain; charset=utf-8");
        response.getWriter().print(message);
    }
}