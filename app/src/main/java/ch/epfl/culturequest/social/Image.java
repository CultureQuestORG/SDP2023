package ch.epfl.culturequest.social;

/*
    * This class represents a picture with a title, a description, a timestamp and a source
 */
public class Image implements Comparable<Image> {
    private String title;
    private String description;
    private String src;

    private String UId;
    private long time;



    public Image(String title, String description, String src, long time) {
        this.title = title;
        this.description = description;
        this.src = src;
        this.time = time;
    }

    public Image(){
        this.title = "";
        this.description = "";
        this.src = "";
        this.time = 0;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getUid() {
        return UId;
    }

    public void setUId(String UId) {
        this.UId = UId;
    }

    @Override
    public int compareTo(Image other) {
        return this.time < other.time ? 1 : -1;
    }

    @Override
    public String toString() {
        return "Image [description=" + description + ", src=" + src + ", time=" + time + ", title=" + title + ", UId="
                + UId + "]";
    }
}

