package io.github.notapresent.usersampler.gaeapp.http;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertEquals;

import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalURLFetchServiceTestConfig;
import io.github.notapresent.usersampler.common.http.Request;
import io.github.notapresent.usersampler.common.http.Response;
import io.github.notapresent.usersampler.common.IntegrationTest;
import java.net.CookieManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class UrlFetchSessionIntegrationTest {

  private static final String HTTPBIN = "http://httpbin.org";
  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalURLFetchServiceTestConfig());
  private final CookieManager cookieManager = new CookieManager();
  private UrlFetchSession session;
  private Request request;
  private Response response;

  @Before
  public void setUp() {
    helper.setUp();
    session = new UrlFetchSession(
        URLFetchServiceFactory.getURLFetchService());
  }

  @After
  public void tearDown() {
    session = null;
    helper.tearDown();
  }

  @Test
  public void itShouldFetchDocument() {
    request = new Request(HTTPBIN + "/ip");
    response = session.send(request);
    assertThat(response.getContentString()).contains("origin");
  }


  @Test
  public void ifShouldNotFollowRedirectsIfPolicySaysSo() {
    request = new Request(HTTPBIN + "/redirect/2");
    request.setRedirectHandlingPolicy(Request.RedirectPolicy.DO_NOT_FOLLOW);
    response = session.send(request);
    assertEquals(302, response.getStatus());
  }

  @Test
  public void itShouldFollowRedirectsIfPolisySaysSo() {
    request = new Request(HTTPBIN + "/redirect/2");
    request.setRedirectHandlingPolicy(Request.RedirectPolicy.FOLLOW);
    response = session.send(request);
    assertEquals(200, response.getStatus());
  }

  @Test
  public void itShouldRetainCookiesAfterRedirect() {
    request = new Request(HTTPBIN + "/cookies/set?k2=v2&k1=v1");
    request.setRedirectHandlingPolicy(Request.RedirectPolicy.FOLLOW);
    session.setCookieManager(cookieManager);
    response = session.send(request);
    String body = response.getContentString();
    assertThat(body).containsMatch("\"k1\":\\s+\"v1\"");
    assertThat(body).containsMatch("\"k2\":\\s+\"v2\"");
  }

  @Test
  public void itShouldRetainsHeadersOnRedirect() {
    request = new Request(HTTPBIN + "/redirect-to?url=/headers");
    request.getHeaders().put("foo", "bar");
    response = session.send(request);
    String body = response.getContentString();
    assertThat(body).containsMatch("(?i)\"foo\":\\s+\"bar\"");
  }
}
