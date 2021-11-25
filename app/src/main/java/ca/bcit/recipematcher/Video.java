package ca.bcit.recipematcher;

public class Video {
    private String video_id;
    private String title;

    public Video() {

    }

    public Video(String video_id, String title) {
        this.video_id = video_id;
        this.title = title;
    }

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
