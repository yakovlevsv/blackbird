package controllers;

import com.typesafe.config.Config;
import handlers.Handler;
import java.util.concurrent.CompletionStage;
import javax.inject.Inject;
import model.PostResource;
import play.data.Form;
import play.data.FormFactory;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * This controller contains an action to handle HTTP requests to the application's home page.
 */
public class HomeController extends Controller {

  private HttpExecutionContext ec;
  private Config config;
  private Handler handler;
  private FormFactory formFactory;


  // TODO: 03.02.2019 exception handling for request without content
  // TODO: 03.02.2019 500 when request for nonexistent post
  @Inject
  public HomeController(HttpExecutionContext ec, Config config, Handler handler,
      FormFactory formFactory) {
    this.ec = ec;
    this.config = config;
    this.handler = handler;
    this.formFactory = formFactory;
  }

  public CompletionStage<Result> index() {
    return handler.find(0, config.getInt("app.postsPerPage"))
        .thenApplyAsync(r -> ok(views.html.index.render(r)));
  }

  public CompletionStage<Result> page(Integer number) {
    int size = config.getInt("app.postsPerPage");
    return handler.find(size * (number > 1 ? (number - 1) : 1), size)
        .thenApplyAsync(r -> ok(views.html.posts.list.render(r)));
  }

  public Result create() {
    Form<PostResource> postForm = formFactory.form(PostResource.class);

    return ok(views.html.posts.create.render(postForm));
  }

  public CompletionStage<Result> edit(final String id) {
    return handler.lookup(id)
        .thenApplyAsync(
            r -> ok(views.html.posts.edit.render(formFactory.form(PostResource.class).fill(r))),
            ec.current());
  }


  public CompletionStage<Result> save() {
    Form<PostResource> postResourceForm = formFactory.form(PostResource.class)
        .bindFromRequest(request());
    return handler.create(postResourceForm.get())
        .thenApplyAsync(p -> redirect(routes.HomeController.show(p.getId())), ec.current());
  }


  public CompletionStage<Result> update(String id) {
    PostResource resource = formFactory.form(PostResource.class).bindFromRequest(request()).get();
    resource.setId(id);
    return handler.update(resource)
        .thenApplyAsync(p -> redirect(routes.HomeController.show(p.getId())), ec.current());
  }

  public CompletionStage<Result> show(String id) {
    return handler.lookup(id)
        .thenApplyAsync(p -> ok(views.html.posts.post.render(p)), ec.current());
  }

  public CompletionStage<Result> remove(String id) {
    return handler.remove(id)
        .thenApplyAsync(result -> result ? ok(id + " removed") : badRequest(id), ec.current());
  }

}
