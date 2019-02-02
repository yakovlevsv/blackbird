package data;

import akka.actor.ActorSystem;
import play.libs.concurrent.CustomExecutionContext;

public class ExecContext extends CustomExecutionContext {

  public ExecContext(ActorSystem actorSystem, String name) {
    super(actorSystem, name);
  }
}
