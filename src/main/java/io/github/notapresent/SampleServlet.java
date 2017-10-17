package io.github.notapresent;

import com.google.appengine.api.utils.SystemProperty;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "SampleServlet", value = "/sample")
public class SampleServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        Properties properties = System.getProperties();
        response.setContentType("text/plain");
        response.getWriter().println(
                "App Engine Standard using " + SystemProperty.version.get()
                        + " Java " + properties.get("java.specification.version")
        );
    }
}
