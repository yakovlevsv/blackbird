package handlers;

import data.Repository;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;
import javax.inject.Inject;
import model.PostData;
import model.PostResource;
import play.libs.concurrent.HttpExecutionContext;

public class Handler {


  Repository repository;
  private HttpExecutionContext ec;

  @Inject
  public Handler(Repository repository, HttpExecutionContext ec) {
    this.repository = repository;
    this.ec = ec;
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

  public CompletionStage<List<PostResource>> find(int first , int offset) {
    return repository.find(first,offset)
        .thenApplyAsync(r -> r.map(PostResource::new).collect(Collectors.toList()), ec.current());
  }
}
