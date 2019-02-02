package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.typesafe.config.Config;
import java.util.concurrent.CompletionStage;
import javax.inject.Inject;
import model.PostResource;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.*;
import handlers.Handler;

/**
 * This controller contains an action to handle HTTP requests to the application's home page.
 */
public class HomeController extends Controller {

  private HttpExecutionContext ec;
  private final Config config;
  private Handler handler;

  @Inject
  public HomeController(HttpExecutionContext ec, Config config, Handler handler) {
    this.ec = ec;
    this.config = config;
    this.handler = handler;
  }

  /**
   * An action that renders an HTML page with a welcome message. The configuration in the
   * <code>routes</code> file means that this method will be called when the application receives a
   * <code>GET</code> request with a path of <code>/</code>.
   */
  public Result index() {

    return ok(views.html.index.render(config.getString("app.config")));
  }

  public Result hello(String string) {

    return ok(views.html.index.render(string));
  }

  public CompletionStage<Result> create() {
    //JsonNode json = request().body().asJson();
    final PostResource resource =new PostResource("id_1", "title_s", "body_x");/// Json.fromJson(json, PostResource.class);
    return handler.create(resource).thenApplyAsync(savedResource -> {
      return created(Json.toJson(savedResource));
    }, ec.current());
  }
}
