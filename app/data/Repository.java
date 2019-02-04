package data;


import akka.actor.ActorSystem;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import model.PostData;
import model.PostResource;
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
    return CompletableFuture.supplyAsync(() -> wrap(em -> em.merge(postData)), ec);
  }

  private <T> T wrap(Function<EntityManager, T> function) {
    return jpaApi.withTransaction(function);
  }

  public CompletionStage<PostData> get(long id) {
    return CompletableFuture.supplyAsync(() -> wrap(em -> em.find(PostData.class, id))); // TODO: 05.02.2019 make it OPTIONAL
  }

  public CompletionStage<Boolean> delete(long id) {
    return CompletableFuture.supplyAsync(() -> wrap(em -> {
      // TODO: 03.02.2019 exception handling
      em.remove(em.find(PostData.class, id));
      return true;
    }));
  }

  public CompletionStage<Stream<PostData>> find() {
    return CompletableFuture.supplyAsync(() -> wrap(
        em -> em.createQuery("SELECT p FROM PostData p").getResultList().stream()), ec);
  }
}
