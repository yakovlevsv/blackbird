package handlers;

import data.Repository;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;
import javax.inject.Inject;
import model.PostData;
import model.PostResource;
import model.User;
import play.libs.concurrent.HttpExecutionContext;
import security.SessionRepository;

public class Handler {


  private Repository repository;
  private HttpExecutionContext ec;
  private SessionRepository sessionRepository;
  private static final User admin = new User("admin", "password");

  @Inject
  public Handler(Repository repository, HttpExecutionContext ec,
      SessionRepository sessionRepository) {
    this.repository = repository;
    this.ec = ec;
    this.sessionRepository = sessionRepository;
  }

  public CompletionStage<PostResource> create(PostResource resource) {
    final PostData data = new PostData(resource.getTitle(), resource.getBody());
    return repository.create(data)
        .thenApplyAsync(PostResource::new, ec.current());
  }

  public CompletionStage<PostResource> lookup(String id) {
    return repository.get(Long.parseLong(id))
        .thenApplyAsync(PostResource::new, ec.current());
  }


  public CompletionStage<Boolean> remove(String id) {
    return repository.delete(Long.parseLong(id));
  }

  public CompletionStage<PostResource> update(PostResource resource) {
    return repository.create(
        new PostData(Long.parseLong(resource.getId()), resource.getTitle(), resource.getBody()))
        .thenApplyAsync(PostResource::new, ec.current());
  }

  public CompletionStage<List<PostResource>> find(int first, int offset) {
    return repository.find(first, offset)
        .thenApplyAsync(r -> r.map(PostResource::new).collect(Collectors.toList()), ec.current());
  }

  public CompletionStage<Boolean> login(User user) {
    return CompletableFuture.supplyAsync(
        () -> {
          boolean valid = admin.getName().equals(user.getName()) && admin.getPassword()
              .equals(user.getPassword());
          sessionRepository.login(user);
          return valid;
        });
  }

  public CompletionStage logout(String user) {
    return CompletableFuture.runAsync(() -> {
      sessionRepository.logout(user);
    });
  }
}
