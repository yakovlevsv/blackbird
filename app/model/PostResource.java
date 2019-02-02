package model;

/**
 * Resource for the API.  This is a presentation class for frontend work.
 */
public class PostResource {
    private String id;
    private String title;
    private String body;

    public PostResource() {
    }

    public PostResource(String id, String title, String body) {
        this.id = id;
        this.title = title;
        this.body = body;
    }

    public PostResource(PostData data) {
        this.id = data.id.toString();
        this.title = data.title;
        this.body = data.body;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

}
