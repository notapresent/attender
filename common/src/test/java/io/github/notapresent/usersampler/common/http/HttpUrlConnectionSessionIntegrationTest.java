package io.github.notapresent.usersampler.common.http;

import static com.google.common.truth.Truth.assertThat;

import io.github.notapresent.usersampler.common.IntegrationTest;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.util.List;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class HttpUrlConnectionSessionIntegrationTest {

  private static final String HTTPBIN = "http://httpbin.org";
  private final HttpUrlConnectionSession session = new HttpUrlConnectionSession();
  private final CookieManager cookieMan = new CookieManager();
  private Response response;
  private Request request;
  private HttpCookie cookie = new HttpCookie("cookieName", "blah");

  @Test
  public void itShouldRertieveContent() {
    request = new Request(HTTPBIN);

    response = session.send(request);

    assertThat(response.getContentString())
        .contains("httpbin(1): HTTP Client Testing Service");
  }

  @Test
  public void itShouldSendHeaders() {
    request = new Request(HTTPBIN + "/headers");
    request.getHeaders().put("custom-header", "custom-value");

    response = session.send(request);

    assertThat(response.getContentString())
        .containsMatch("(?i)\"custom-header\":\\s+\"custom-value\"");
  }

  @Test
  public void itShouldFollowRedirectsWhenAsked() {
    request = new Request(HTTPBIN + "/redirect/1");
    request.setRedirectHandlingPolicy(Request.RedirectPolicy.FOLLOW);

    response = session.send(request);

    assertThat(response.getStatus()).isEqualTo(200);
  }

  @Test
  public void itShouldntFollowRedirectsWhenAskedNotTo() {
    request = new Request(HTTPBIN + "/redirect/1");
    request.setRedirectHandlingPolicy(Request.RedirectPolicy.DO_NOT_FOLLOW);

    response = session.send(request);

    assertThat(response.getStatus()).isEqualTo(302);
  }

  @Test
  public void itShouldSetResponseHeaders() {
    request = new Request(HTTPBIN + "/response-headers?myheader=teapot");

    response = session.send(request);

    assertThat(response.getHeaders()).containsEntry("myheader", "teapot");
  }

  @Test
  public void itShouldAcceptCookies() {
    request = new Request(HTTPBIN + "/cookies/set?mycookie=myvalue");
    session.setCookieManager(cookieMan);

    response = session.send(request);

    List<HttpCookie> cookies = cookieMan.getCookieStore().getCookies();
    assertThat(cookies.get(0).getName()).isEqualTo("mycookie");
    assertThat(cookies.get(0).getValue()).isEqualTo("myvalue");
  }

  @Test
  public void itShouldSendStoredCookies() {
    session.setCookieManager(cookieMan);
    session.send(new Request(HTTPBIN + "/cookies/set?mycookie=myvalue"));
    request = new Request(HTTPBIN + "/cookies");

    response = session.send(request);

    assertThat(response.getContentString())
        .containsMatch("(?i)\"mycookie\":\\s*\"myvalue\"");
  }

  @Test
  public void itShouldSetResponseStatusCode() {
    request = new Request(HTTPBIN + "/status/201");

    response = session.send(request);

    assertThat(response.getStatus()).isEqualTo(201);
  }

  @Test
  public void itShouldSetFinalUrl() {
    request = new Request(HTTPBIN + "/get");

    response = session.send(request);

    assertThat(response.getFinalUrl()).isEqualTo(HTTPBIN + "/get");
  }

  @Test
  public void itShouldSetFinalUrlOnRedirects() {
    request = new Request(HTTPBIN + "/redirect/1");
    request.setRedirectHandlingPolicy(Request.RedirectPolicy.FOLLOW);

    response = session.send(request);

    assertThat(response.getFinalUrl()).isEqualTo(HTTPBIN + "/get");
  }
}