package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.typesafe.config.Config;
import handlers.Handler;
import java.util.concurrent.CompletionStage;
import javax.inject.Inject;
import model.PostResource;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.post;

/**
 * This controller contains an action to handle HTTP requests to the application's home page.
 */
public class HomeController extends Controller {

  private HttpExecutionContext ec;
  private final Config config;
  private Handler handler;


  // TODO: 03.02.2019 exception handling for request without content
  // TODO: 03.02.2019 500 when request for nonexistent post
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

  // TODO: 03.02.2019 change it, do it really need to return a new page or just updated content?
  public CompletionStage<Result> create() {
    JsonNode json = request().body().asJson();
    final PostResource resource = Json.fromJson(json, PostResource.class);
    return handler.create(resource).thenApplyAsync(p -> created(post.render(p)), ec.current());
  }

  public CompletionStage<Result> show(String id) {
    return handler.lookup(id).thenApplyAsync(p -> ok(post.render(p)), ec.current());
  }

  public CompletionStage<Result> remove(String id) {
    return handler.remove(id)
        .thenApplyAsync(result -> result ? ok(id + " removed") : badRequest(id), ec.current());
  }

  // TODO: 03.02.2019  the same question
  public CompletionStage<Result> update() {
    JsonNode json = request().body().asJson();
    final PostResource resource = Json.fromJson(json, PostResource.class);
    return handler.update(resource).thenApplyAsync(p -> ok(post.render(p)), ec.current());
  }
}
