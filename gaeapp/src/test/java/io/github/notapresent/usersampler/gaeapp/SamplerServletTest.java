package io.github.notapresent.usersampler.gaeapp;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.inject.Provider;
import io.github.notapresent.usersampler.common.sampling.Orchestrator;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class SamplerServletTest {

  // Servlet stuff
  @Mock
  private HttpServletRequest mockRequest;
  @Mock
  private HttpServletResponse mockResponse;


  @Mock
  private Orchestrator mockOrchestrator;

  private StringWriter responseWriter;

  private SamplerServlet servletUnderTest;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    when(mockOrchestrator.run()).thenReturn(42);

    // Set up a fake HTTP response.
    responseWriter = new StringWriter();
    when(mockResponse.getWriter()).thenReturn(new PrintWriter(responseWriter));

    javax.servlet.ServletConfig servletConfig = mock(javax.servlet.ServletConfig.class);

    Provider<Orchestrator> orchProvider = () -> mockOrchestrator;

    servletUnderTest = new SamplerServlet(orchProvider);
    servletUnderTest.init(servletConfig);
  }

  @Test
  public void itShouldReportNumberOfSites() throws Exception {
    servletUnderTest.doGet(mockRequest, mockResponse);

    String strResponse = responseWriter.toString();
    assertThat(strResponse).contains("42 sites processed");
    verify(mockOrchestrator, times(1)).run();
  }
}
