package handlers;

import com.typesafe.config.Config;
import data.Repository;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;
import javax.inject.Inject;
import model.PostData;
import model.PostResource;
import play.Logger;
import play.libs.concurrent.HttpExecutionContext;

public class Handler {

  private Repository repository;
  private HttpExecutionContext ec;
  private Config config;

  @Inject
  public Handler(Repository repository, Config config, HttpExecutionContext ec) {
    this.repository = repository;
    this.config = config;
    this.ec = ec;
  }

  public CompletionStage<PostResource> create(PostResource resource) {
    final PostData data = new PostData(resource.getTitle(), resource.getBody());
    return repository.create(data)
        .thenApplyAsync(PostResource::new, ec.current());
  }

  public CompletionStage<PostResource> lookup(String id) {
    return repository.get(Long.parseLong(id))
        .thenApplyAsync(r -> new PostResource(r.get()), ec.current()).exceptionally(t -> {
          Logger.warn("no post with id: {}", id);
          throw new RuntimeException("No such post " + id);
        });
  }

  public CompletionStage<Boolean> remove(String id) {
    return repository.delete(Long.parseLong(id)).exceptionally(t -> {
      Logger.warn("exception while deleting Post", t);
      return false;
    });
  }

  public CompletionStage<PostResource> update(PostResource resource) {
    return repository.create(
        new PostData(Long.parseLong(resource.getId()), resource.getTitle(), resource.getBody()))
        .thenApplyAsync(PostResource::new, ec.current());
  }

  public CompletionStage<List<PostResource>> preparePage(int number) {
    int size = config.getInt("app.postsPerPage");
    return repository.find(size * number < 1 ? 0 : (number - 1), size)
        .thenApplyAsync(r -> r.map(PostResource::new).collect(Collectors.toList()), ec.current());
  }

  public CompletionStage<Integer> getPageCount() {
    return repository.getPostCount().thenApply(r -> r / config.getInt("app.postsPerPage"));
  }
}
