package controllers;

import handlers.Handler;
import java.util.concurrent.CompletionStage;
import javax.inject.Inject;
import model.PostResource;
import play.data.Form;
import play.data.FormFactory;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import security.Secured;

public class PostController extends Controller {

  private HttpExecutionContext ec;
  private FormFactory formFactory;

  private Handler handler;

  @Inject
  public PostController(HttpExecutionContext ec, FormFactory formFactory, Handler handler) {
    this.ec = ec;
    this.formFactory = formFactory;
    this.handler = handler;
  }

  public CompletionStage<Result> page(Integer number) {
    return handler.getPageCount().thenCombineAsync(handler.preparePage(number),
        (p,r) -> ok(views.html.posts.list.render(r, number , p)));
  }

  @Security.Authenticated(Secured.class)
  public Result create() {
    Form<PostResource> postForm = formFactory.form(PostResource.class);
    return ok(views.html.posts.create.render(postForm));
  }

  @Security.Authenticated(Secured.class)
  public CompletionStage<Result> edit(final String id) {
    return handler.lookup(id).thenApplyAsync(
        r -> ok(views.html.posts.edit.render(formFactory.form(PostResource.class).fill(r))),
        ec.current()).exceptionally(t -> noContent());
  }

  @Security.Authenticated(Secured.class)
  public CompletionStage<Result> save() {
    Form<PostResource> postResourceForm = formFactory.form(PostResource.class)
        .bindFromRequest(request());
    return handler.create(postResourceForm.get())
        .thenApplyAsync(p -> redirect(routes.PostController.show(p.getId())), ec.current());
  }

  @Security.Authenticated(Secured.class)
  public CompletionStage<Result> update(String id) {
    PostResource resource = formFactory.form(PostResource.class).bindFromRequest(request()).get();
    resource.setId(id);
    return handler.update(resource)
        .thenApplyAsync(p -> redirect(routes.PostController.show(p.getId())), ec.current());
  }

  public CompletionStage<Result> show(String id) {
    return handler.lookup(id)
        .thenApplyAsync(p -> ok(views.html.posts.post.render(p, session().get("user") != null)),
            ec.current()).exceptionally(t -> noContent());
  }

  @Security.Authenticated(Secured.class)
  public CompletionStage<Result> remove(String id) {
    return handler.remove(id)
        .thenApplyAsync(result -> result ? ok(id + " removed") : badRequest(id), ec.current());
  }
}
