package io.github.notapresent.usersampler.gaeapp;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class SysinfoServletTest {

  private static final String FAKE_URL = "http://fake.fk/hello";

  // Set up a helper so that the ApiProxy returns a valid environment for local testing.
  private final LocalServiceTestHelper helper = new LocalServiceTestHelper();

  // Servlet stuff
  @Mock
  private HttpServletRequest mockRequest;
  @Mock
  private HttpServletResponse mockResponse;


  private StringWriter responseWriter;

  private SysinfoServlet servletUnderTest;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    helper.setUp();

    // Set up a fake HTTP response.
    responseWriter = new StringWriter();
    when(mockResponse.getWriter()).thenReturn(new PrintWriter(responseWriter));

    javax.servlet.ServletConfig servletConfig = mock(javax.servlet.ServletConfig.class);

    servletUnderTest = new SysinfoServlet();
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

    assertThat(strResponse)
        .named("SamplerServlet response")
        .contains("Java System");
  }
}
