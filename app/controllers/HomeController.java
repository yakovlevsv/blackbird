package controllers;

import com.typesafe.config.Config;
import handlers.Handler;
import handlers.SecurityHandler;
import java.util.concurrent.CompletionStage;
import javax.inject.Inject;
import model.PostResource;
import model.User;
import play.data.Form;
import play.data.FormFactory;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import security.Secured;

/**
 * This controller contains an action to handle HTTP requests to the application's home page.
 */
public class HomeController extends Controller {

  private HttpExecutionContext ec;
  private Config config;
  private Handler handler;
  private FormFactory formFactory;
  private SecurityHandler security;


  // TODO: 03.02.2019 exception handling for request without content
  // TODO: 03.02.2019 500 when request for nonexistent post
  @Inject
  public HomeController(HttpExecutionContext ec, Config config, Handler handler,
      FormFactory formFactory, SecurityHandler security) {
    this.ec = ec;
    this.config = config;
    this.handler = handler;
    this.formFactory = formFactory;
    this.security = security;
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

  @Security.Authenticated(Secured.class)
  public Result create() {
    Form<PostResource> postForm = formFactory.form(PostResource.class);
    return ok(views.html.posts.create.render(postForm));
  }

  @Security.Authenticated(Secured.class)
  public CompletionStage<Result> edit(final String id) {
    return handler.lookup(id)
        .thenApplyAsync(
            r -> ok(views.html.posts.edit.render(formFactory.form(PostResource.class).fill(r))),
            ec.current());
  }

  @Security.Authenticated(Secured.class)
  public CompletionStage<Result> save() {
    Form<PostResource> postResourceForm = formFactory.form(PostResource.class)
        .bindFromRequest(request());
    return handler.create(postResourceForm.get())
        .thenApplyAsync(p -> redirect(routes.HomeController.show(p.getId())), ec.current());
  }

  @Security.Authenticated(Secured.class)
  public CompletionStage<Result> update(String id) {
    PostResource resource = formFactory.form(PostResource.class).bindFromRequest(request()).get();
    resource.setId(id);
    return handler.update(resource)
        .thenApplyAsync(p -> redirect(routes.HomeController.show(p.getId())), ec.current());
  }

  public CompletionStage<Result> show(String id) {
    return handler.lookup(id)
        .thenApplyAsync(p -> ok(views.html.posts.post.render(p, session().get("user")!=null)), ec.current());
  }

  @Security.Authenticated(Secured.class)
  public CompletionStage<Result> remove(String id) {
    return handler.remove(id)
        .thenApplyAsync(result -> result ? ok(id + " removed") : badRequest(id), ec.current());
  }

  public Result login() {
    return ok(views.html.login.render(formFactory.form(User.class)));
  }

  public CompletionStage<Result> authenticate() {
    User user = formFactory.form(User.class).bindFromRequest(request()).get();
    return security.login(user).thenApplyAsync(authenticated -> {
      if (authenticated) {
        session().put("user", user.getName());
        return redirect(routes.HomeController.index());
      } else {
        return redirect(routes.HomeController.login());
      }
    }, ec.current());
  }

  public CompletionStage<Result> logout() {
    return security.logout(session("user")).thenApplyAsync(r -> {
      session().clear();
      return redirect(routes.HomeController.index());
    }, ec.current());
  }
}
