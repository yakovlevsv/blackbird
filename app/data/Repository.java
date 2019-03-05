package data;


import akka.actor.ActorSystem;
import java.text.MessageFormat;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import model.PostData;
import play.Logger;
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

  public CompletionStage<Optional<PostData>> get(long id) {
    return CompletableFuture.supplyAsync(
        () -> wrap(em -> Optional.ofNullable(em.find(PostData.class, id))));
  }

  public CompletionStage<Boolean> delete(long id) {
    return CompletableFuture.supplyAsync(() -> wrap(em -> {
      em.remove(em.find(PostData.class, id));
      return true;
    }));
  }

  public CompletionStage<Stream<PostData>> find(int first, int offset) {
    return CompletableFuture.supplyAsync(() ->
        wrap(em -> em.createNativeQuery(
            MessageFormat.format("SELECT * FROM posts offset {0} limit {1}", first, offset),
            PostData.class).getResultList().stream()), ec);
  }
}
