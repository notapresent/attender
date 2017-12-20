package io.github.notapresent.usersampler.gaeapp;

import com.google.appengine.api.utils.SystemProperty;
import com.google.inject.Singleton;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Singleton
public class SysinfoServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    String divider = String.join("", Collections.nCopies(80, "-")) + "\n";
    Properties properties = System.getProperties();
    Map<String, String> env = System.getenv();

    response.setContentType("text/plain; charset=utf-8");
    StringBuilder sb = new StringBuilder();

    sb.append("Java System properties\n");
    sb.append(divider);
    for (String key : properties.stringPropertyNames()) {
      sb.append(key)
          .append(": ")
          .append(properties.getProperty(key))
          .append("\n");
    }

    sb.append("\n\nAppengine SystemProperties\n");
    sb.append(divider);
    sb.append("SystemProperty.Environment: ")
        .append(SystemProperty.environment.value())
        .append("\napplicationId: ").append(SystemProperty.applicationId.get())
        .append("\napplicationVersion: ").append(SystemProperty.applicationVersion.get())
        .append("\nversion: ").append(SystemProperty.version.get());


    sb.append("\n\nEnvironment variables\n");
    sb.append(divider);
    env.forEach((key, value) -> sb.append(String.format("%s: %s\n", key, value)));

    response.getWriter().print(sb.toString());
  }
}
