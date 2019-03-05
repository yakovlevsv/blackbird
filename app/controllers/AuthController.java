package controllers;

import handlers.SecurityHandler;
import java.util.concurrent.CompletionStage;
import javax.inject.Inject;
import model.User;
import play.data.FormFactory;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;

public class AuthController extends Controller {

  private HttpExecutionContext ec;
  private FormFactory formFactory;
  private SecurityHandler security;

  @Inject
  public AuthController(HttpExecutionContext ec, FormFactory formFactory,
      SecurityHandler security) {
    this.ec = ec;
    this.formFactory = formFactory;
    this.security = security;
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
        return redirect(routes.AuthController.login());
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
