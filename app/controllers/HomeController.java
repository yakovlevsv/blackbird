package controllers;

import com.typesafe.config.Config;
import handlers.Handler;
import java.util.concurrent.CompletionStage;
import javax.inject.Inject;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * This controller contains an action to handle HTTP requests to the application's home page.
 */
public class HomeController extends Controller {

  private Config config;
  private Handler handler;

  @Inject
  public HomeController(Config config, Handler handler) {
    this.config = config;
    this.handler = handler;
  }

  public CompletionStage<Result> index() {
    return handler.find(0, config.getInt("app.postsPerPage"))
        .thenApplyAsync(r -> ok(views.html.index.render(r)));
  }


}
