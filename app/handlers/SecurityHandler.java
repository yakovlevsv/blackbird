package handlers;

import com.typesafe.config.Config;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import javax.inject.Inject;
import javax.inject.Singleton;
import model.User;
import security.SessionRepository;


@Singleton
public class SecurityHandler {

  private final SessionRepository sessionRepository;
  private final User admin;

  @Inject
  public SecurityHandler(SessionRepository sessionRepository, Config config)
      throws IOException {
    this.sessionRepository = sessionRepository;
    Properties properties = new Properties();
    properties.load(new FileInputStream(config.getString("security.config")));
    admin = new User();
    admin.setName(properties.getProperty("admin.username"));
    admin.setPassword(properties.getProperty("admin.password"));
  }

  public CompletionStage<Boolean> login(User user) {
    return CompletableFuture.supplyAsync(() -> {
      if (admin.getName().equals(user.getName()) &&
          admin.getPassword().equals(user.getPassword())) {
        sessionRepository.login(user);
        return true;
      }
      return false;
    });
  }

  public CompletionStage logout(String user) {
    return CompletableFuture.runAsync(() -> {
      sessionRepository.logout(user);
    });
  }
}
