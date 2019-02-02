package handlers;

import data.Repository;
import java.util.concurrent.CompletionStage;
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
    return repository.create(data).thenApplyAsync(savedData -> {
      return new PostResource(savedData);
    }, ec.current());
  }
}
