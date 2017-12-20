package io.github.notapresent.usersampler.gaeapp.servlets;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import io.github.notapresent.usersampler.common.sampling.Orchestrator;
import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Singleton
public class SamplerServlet extends HttpServlet {

  private final Provider<Orchestrator> orchestratorProvider;

  @Inject
  public SamplerServlet(Provider<Orchestrator> orchestratorProvider) {
    this.orchestratorProvider = orchestratorProvider;
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException {

    int sitesProcessed = orchestratorProvider.get().run();
    String message = sitesProcessed + " sites processed\n";
    response.setContentType("text/plain; charset=utf-8");
    response.getWriter().print(message);
  }
}