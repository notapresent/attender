package io.github.notapresent;

import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.inject.Guice;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link SamplerServlet}.
 */

@RunWith(JUnit4.class)
public class SamplerServletTest {
    private static final String FAKE_URL = "http://fake.fk/hello";
    private static final String FAKE_SESSION_RESPONSE = "Fake session response";

    // Set up a helper so that the ApiProxy returns a valid environment for local testing.
    private final LocalServiceTestHelper helper = new LocalServiceTestHelper();

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private HttpServletResponse mockResponse;

    @Mock
    private HTTPSession mockSesion;
    @Mock
    private HTTPResponse mockSessionResponse;

    private StringWriter responseWriter;

    @Mock
    private HTTPSession mockSession;

    private SamplerServlet servletUnderTest;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        helper.setUp();

        //  Set up some fake HTTP requests
        when(mockRequest.getRequestURI()).thenReturn(FAKE_URL);

        // Set up a fake HTTP response.
        responseWriter = new StringWriter();
        when(mockResponse.getWriter()).thenReturn(new PrintWriter(responseWriter));

        javax.servlet.ServletConfig servletConfig = mock(javax.servlet.ServletConfig.class);

        Config mockConfig = mock(Config.class);
        when(mockConfig.getProperty(anyString())).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return FAKE_URL;
            }
        });
        //when(mockConfig.getProperty(any(String.class))).thenReturn("fake property value");

        ServletContext servletContext = mock(ServletContext.class);
        when(servletContext.getAttribute(any(String.class))).thenReturn(mockConfig);


        when(servletConfig.getServletContext()).thenReturn(servletContext);

        when(mockSessionResponse.getContent()).thenReturn(FAKE_SESSION_RESPONSE.getBytes());
        when(mockSession.fetch(any(URL.class))).thenReturn(mockSessionResponse);

        servletUnderTest = new SamplerServlet(mockSession);
        servletUnderTest.init(servletConfig);
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    public void doGetwritesResponse() throws Exception {
        servletUnderTest.doGet(mockRequest, mockResponse);

        String strResponse = responseWriter.toString();

        // We expect our hello world response.
        assertThat(strResponse)
                .named("SamplerServlet response")
                .contains("App Engine Standard");

        assertThat(strResponse)
                .named("SamplerServlet response")
                .contains(FAKE_SESSION_RESPONSE);
    }
}
