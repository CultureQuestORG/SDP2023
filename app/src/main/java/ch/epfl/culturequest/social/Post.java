package ch.epfl.culturequest.social;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import ch.epfl.culturequest.database.Database;

public final class Post {
    private String postId;
    private String uid;
    private String imageUrl;
    private String artworkName;
    private Date date;
    private int likes;
    private final ArrayList<String> likers;

    /**
     * Constructor for a Post
     * @param postid the id of the post
     * @param uid the id of the user who posted the post
     * @param imageUrl  the url of the image
     * @param artworkName   the name of the artwork
     * @param date  the date of the post
     * @param likes the number of likes
     * @param likers    the list of users who liked the post
     */
    public Post(String postid, String uid, String imageUrl, String artworkName, Date date, int likes, List<String> likers) {
        this.postId = postid;
        this.uid = uid;
        this.imageUrl = imageUrl;
        this.artworkName = artworkName;
        this.date = date;
        this.likes = likes;
        this.likers = new ArrayList<>(likers);
    }

    /**
     * Default Constructor for a Post for Firebase
     */
    public Post() {
        this.postId = UUID.randomUUID().toString();
        this.uid = "";
        this.imageUrl = "";
        this.artworkName = "";
        this.date = new Date();
        this.likes = 0;
        this.likers = new ArrayList<>();
    }

    /**
     * Get the post id
     * @return  the post id
     */
    public String getPostid() {
        return postId;
    }

    public void setPostid(String postid) {
        postId = postid;
    }

    /**
     * Get the user id
     * @return  the user id
     */
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * Get the image url
     * @return  the image url
     */
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * Get the artwork name
     * @return  the artwork name
     */
    public String getArtworkName() {
        return artworkName;
    }

    public void setArtworkName(String artworkName) {
        this.artworkName = artworkName;
    }

    /**
     * Get the date
     * @return  the date
     */
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Get the number of likes
     * @return  the number of likes
     */
    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    /**
     * Get the list of users who liked the post
     * @return  the list of users who liked the post
     */
    public ArrayList<String> getLikers() {
        return likers;
    }

    public void setLikers(ArrayList<String> likers) {
        this.likers.clear();
        this.likers.addAll(likers);
    }

    /**
     * Add a like to the post
     * @param uid   the user id of the user who liked the post
     */
    public void addLike(String uid) {
        if (!likers.contains(uid)) {
            likers.add(uid);
            likes++;
        }

    }

    /**
     * Remove a like from the post
     * @param uid   the user id of the user who unliked the post
     */
    public void removeLike(String uid) {
        if (likers.contains(uid)){
            likers.remove(uid);
            likes--;
        }
    }

    /**
     * Check if the post is liked by a user
     * @param uid   the user id of the user
     * @return  true if the post is liked by the user, false otherwise
     */
    public boolean isLikedBy(String uid) {
        return likers.contains(uid);
    }

    @Override
    public String toString() {
        return "Post of artwork " + artworkName + ", at date" + date + ", imageUrl=" + imageUrl + ", postid=" + postId
                + ", from user:" + uid + ".";
    }
}
