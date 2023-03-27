package ch.epfl.culturequest.social;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.Future;

import ch.epfl.culturequest.database.Database;

public final class Post {
    private final String postId;
    private final String uid;
    private final String imageUrl;
    private final String artworkName;
    private final Date date;
    private final int likes;

    public Post(String postid, String uid, String imageUrl, String artworkName, Date date) {
        this.postId = postid;
        this.uid = uid;
        this.imageUrl = imageUrl;
        this.artworkName = artworkName;
        this.date = date;
        this.likes = 0;
    }

    public Post(String postid, String uid, String imageUrl, String artworkName, Date date, int likes) {
        this.postId = postid;
        this.uid = uid;
        this.imageUrl = imageUrl;
        this.artworkName = artworkName;
        this.date = date;
        this.likes = likes;
    }

    public Post() {
        this.postId = UUID.randomUUID().toString();
        this.uid = "";
        this.imageUrl = "";
        this.artworkName = "";
        this.date = new Date();
        this.likes = 0;
    }

    public String getPostid() {
        return postId;
    }

    public String getUid() {
        return uid;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getArtworkName() {
        return artworkName;
    }

    public Date getDate() {
        return date;
    }

    public int getLikes() {
        return likes;
    }

    @Override
    public String toString() {
        return "Post [artworkName=" + artworkName + ", date=" + date + ", imageUrl=" + imageUrl + ", postid=" + postId
                + ", uid=" + uid + "]";
    }
}
