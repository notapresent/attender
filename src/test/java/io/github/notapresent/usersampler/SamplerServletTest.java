package io.github.notapresent.usersampler;

import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import io.github.notapresent.usersampler.HTTP.Request;
import io.github.notapresent.usersampler.HTTP.RequestFactory;
import io.github.notapresent.usersampler.HTTP.Response;
import io.github.notapresent.usersampler.HTTP.Session;
import io.github.notapresent.usersampler.SamplerServlet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link SamplerServlet}.
 */

@RunWith(JUnit4.class)
public class SamplerServletTest {
    private static final String FAKE_URL = "http://fake.fk/hello";
    private static final String FAKE_SESSION_RESPONSE = "Fake session response";

    // Set up a helper so that the ApiProxy returns a valid environment for local testing.
    private final LocalServiceTestHelper helper = new LocalServiceTestHelper();

    // Servlet stuff
    @Mock private HttpServletRequest mockRequest;
    @Mock private HttpServletResponse mockResponse;


    @Mock private Session mockSession;
    @Mock private RequestFactory mockRequestFactory;
    @Mock private Request mockSessionRequest;
    @Mock private Response mockSessionResponse;



    private StringWriter responseWriter;

    private SamplerServlet servletUnderTest;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        helper.setUp();

        //  Set up some fake HTTP requests
        when(mockRequest.getRequestURI()).thenReturn(FAKE_URL); // FIXME: delete

        // Set up a fake HTTP response.
        responseWriter = new StringWriter();
        when(mockResponse.getWriter()).thenReturn(new PrintWriter(responseWriter));

        javax.servlet.ServletConfig servletConfig = mock(javax.servlet.ServletConfig.class);

        when(mockRequestFactory.GET(any(String.class))).thenReturn(mockSessionRequest);
        when(mockSessionResponse.getContentBytes()).thenReturn(FAKE_SESSION_RESPONSE.getBytes());
        when(mockSession.send(any(Request.class))).thenReturn(mockSessionResponse);

        servletUnderTest = new SamplerServlet(mockSession, mockRequestFactory, FAKE_URL);
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
