package ch.epfl.culturequest.social;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Future;

import ch.epfl.culturequest.database.Database;

public final class Post {
    private final String postId;
    private final String uid;
    private final String imageUrl;
    private final String artworkName;
    private final Date date;

    public Post(String postid, String uid, String imageUrl, String artworkName, Date date) {
        this.postId = postid;
        this.uid = uid;
        this.imageUrl = imageUrl;
        this.artworkName = artworkName;
        this.date = date;
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

    public static Future<ArrayList<Post>> getPosts() {
//        return Database.getPosts();
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public String toString() {
        return "Post [artworkName=" + artworkName + ", date=" + date + ", imageUrl=" + imageUrl + ", postid=" + postId
                + ", uid=" + uid + "]";
    }

}
