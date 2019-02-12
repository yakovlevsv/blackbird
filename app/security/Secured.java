package security;

import javax.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

public class Secured extends Security.Authenticator {

  private final SessionRepository repository;

  @Inject
  public Secured(SessionRepository repository) {
    this.repository = repository;
  }

  @Override
  public String getUsername(Http.Context context) {
    final String userName = context.session().get("user");
    if (StringUtils.isNotBlank(userName) && repository.isLoggedIn(userName)) {
      return userName;
    } else {
      return null;
    }
  }

  @Override
  public Result onUnauthorized(Http.Context context) {
    return redirect("/login");
  }
}
