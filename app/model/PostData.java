package model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "posts")
public class PostData {

  public PostData() {
  }

  public PostData(String title, String body) {
    this.title = title;
    this.body = body;
  }

  public PostData(Long id, String title, String body) {
    this.id = id;
    this.title = title;
    this.body = body;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;
  public String title;
  public String body;
}
