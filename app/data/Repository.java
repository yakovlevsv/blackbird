package data;

import static java.util.concurrent.CompletableFuture.supplyAsync;

import akka.actor.ActorSystem;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import model.PostData;
import play.db.jpa.JPAApi;
import play.libs.concurrent.CustomExecutionContext;

@Singleton
public class Repository {

  private JPAApi jpaApi;
  private CustomExecutionContext ec;

  @Inject
  public Repository(JPAApi api, ActorSystem actorSystem) {
    this.jpaApi = api;
    this.ec = new CustomExecutionContext(actorSystem, "database.dispatcher") {
    };
  }

  public CompletionStage<PostData> create(PostData postData) {
    return supplyAsync(() -> wrap(em -> insert(em, postData)), ec);
  }

  private <T> T wrap(Function<EntityManager, T> function) {
    return jpaApi.withTransaction(function);
  }

  private PostData insert(EntityManager em, PostData postData) {
    return em.merge(postData);
  }
}
