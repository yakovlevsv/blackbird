package controllers;

import static org.junit.Assert.assertEquals;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.GET;
import static play.test.Helpers.POST;
import static play.test.Helpers.route;

import java.util.HashMap;
import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Http;
import play.mvc.Http.Status;
import play.mvc.Result;
import play.test.WithApplication;

public class HomeControllerTest extends WithApplication {

  @Override
  protected Application provideApplication() {
    return new GuiceApplicationBuilder().build();
  }

  @Test
  public void testIndex() {
    Http.RequestBuilder request = new Http.RequestBuilder()
        .method(GET)
        .uri("/");

    Result result = route(app, request);
    assertEquals(OK, result.status());
  }

  @Test
  public void testEditPost() {
    Http.RequestBuilder request = new Http.RequestBuilder()
        .method(GET)
        .uri("/post/" + 1 + "/edit");

    Result result = route(app, request);
    assertEquals(Status.FORBIDDEN, result.status());
  }

  @Test
  public void testCreatePost() {
    Http.RequestBuilder login = new Http.RequestBuilder().method(POST).uri("/auth")
        .bodyForm(new HashMap<String, String>() {{
          put("name", "test");
          put("password", "test");
        }});
    Http.RequestBuilder request = new Http.RequestBuilder().method(POST).uri("/save")
        .session(route(app, login).session()).bodyForm(new HashMap<String, String>() {{
          put("title", "test");
          put("body", "test");
        }});
    Result result = route(app, request);
    assertEquals(Status.SEE_OTHER, result.status());
  }
}
