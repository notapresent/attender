package io.github.notapresent.usersampler.gaeapp;

import com.google.appengine.api.utils.SystemProperty;
import com.google.inject.Singleton;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Properties;

@Singleton
public class SysinfoServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        Properties properties = System.getProperties();

        response.setContentType("text/plain; charset=utf-8");
        String message = "App Engine Standard using %s%n" +
                "Java %s%n";

        response.getWriter().format(
                message,
                SystemProperty.version.get(),
                properties.get("java.specification.version")
        );
    }
}
