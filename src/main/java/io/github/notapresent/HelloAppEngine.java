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

@WebServlet(name = "HelloAppEngine", value = "/hello")
public class HelloAppEngine extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        Properties properties = System.getProperties();
//        Properties config = getConfig("/WEB-INF/config.properties");

        response.setContentType("text/plain");
        response.getWriter().println("Hello App Engine - Standard using "
                + SystemProperty.version.get() + " Java " + properties.get("java.specification.version")
//                + "\nUser list URL: " + config.getProperty("urls.index")
//                + "\nProfile URL template: " + config.getProperty("urls.profile")
        );
    }

    public Properties getConfig(String path) throws IOException {
        ServletContext ctx = getServletConfig().getServletContext();
        InputStream input = ctx.getResourceAsStream(path);
        Properties prop = new Properties();

        try {
            prop.load(input);
        }
        catch (IOException e) {
            System.out.println("config.properties is missing");
            throw e;
        }

        return prop;
    }


    public static String getInfo() {
        return "Version: " + System.getProperty("java.version")
                + " OS: " + System.getProperty("os.name")
                + " User: " + System.getProperty("user.name");
    }

}
