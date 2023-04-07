package ch.epfl.culturequest.database;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import ch.epfl.culturequest.social.Follows;
import ch.epfl.culturequest.social.Image;
import ch.epfl.culturequest.social.Post;
import ch.epfl.culturequest.social.Profile;


/**
 * This class is used to implement the database
 * It is used to make the code more modular and to make it easier to change the database
 */
public class Database {
    private static DatabaseInterface  databaseInstance = new FireDatabase();

    /**
     * This method is used to initialize the database instance as something else than the default (FireDatabase)
     * This is useful for testing purposes
     *
     * @param database the database instance to be used
     */
    public static void init(DatabaseInterface database) {
        Database.databaseInstance = database;
    }


    public static CompletableFuture<AtomicBoolean> deleteProfile(String uid) {
    return databaseInstance.deleteProfile(uid);
    }

    public static CompletableFuture<AtomicBoolean> set(String key, Object value) {
        return databaseInstance.set(key, value);
    }

    public static CompletableFuture<Object> get(String key) {
        return databaseInstance.get(key);
    }

    public static CompletableFuture<Profile> getProfile(String UId) {
        return databaseInstance.getProfile(UId);
    }

    public static CompletableFuture<AtomicBoolean> setProfile(Profile profile) {
        return databaseInstance.setProfile(profile);
    }

    public static CompletableFuture<List<Profile>> getAllProfiles() {
        return databaseInstance.getAllProfiles();
    }


    public static CompletableFuture<Image> getImage(String UId) {
        return databaseInstance.getImage(UId);
    }

    public static CompletableFuture<AtomicBoolean> setImage(Image picture) {
        return databaseInstance.setImage(picture);
    }

    public static CompletableFuture<Integer> getRank(String UId) {
        return databaseInstance.getRank(UId);
    }

    public static CompletableFuture<Integer> getRankFriends(String UId) {
        return databaseInstance.getRankFriends(UId);
    }

    public static CompletableFuture<Integer> getNumberOfProfiles() {
        return databaseInstance.getNumberOfProfiles();
    }

    public static CompletableFuture<List<Profile>> getTopNProfiles(int n) {
        return databaseInstance.getTopNProfiles(n);
    }

    public static CompletableFuture<List<Profile>> getTopNFriendsProfiles(int n) {
        return databaseInstance.getTopNProfiles(n);
    }

    /**
     * This method is used to upload a post to the database
     * @param post the post to be uploaded
     * @return a CompletableFuture that will be completed when the upload is done
     */
    public static CompletableFuture<AtomicBoolean> uploadPost(Post post) {
        return databaseInstance.uploadPost(post);
    }

    /**
     * This method is used to remove a post from the database
     * @param post the post to be removed
     * @return a CompletableFuture that will be completed when the removal is done
     */
    public static CompletableFuture<AtomicBoolean> removePost(Post post) {
        return databaseInstance.removePost(post);
    }

    /**
     * This method is used to get the posts of a user
     * @param UId the user's id
     * @param limit the maximum number of posts to be returned
     * @param offset the number of posts to be skipped
     * @return a CompletableFuture that will be completed when the posts are retrieved
     */
    public static CompletableFuture<List<Post>> getPosts(String UId, int limit, int offset) {
        return databaseInstance.getPosts(UId, limit, offset);
    }

    /**
     * This method is used to get the posts of a user's followings
     * @param UIds the user's id
     * @param limit the maximum number of posts to be returned
     * @param offset the number of posts to be skipped
     * @return a CompletableFuture that will be completed when the posts are retrieved
     */
    public static CompletableFuture<List<Post>> getPostsFeed(List<String> UIds, int limit, int offset) {
        return databaseInstance.getPostsFeed(UIds, limit, offset);
    }

    /**
     * This method is used to get the posts of a user's followings
     * @param UIds the user's id
     * @param limit the maximum number of posts to be returned
     * @return a CompletableFuture that will be completed when the posts are retrieved
     */
    public static CompletableFuture<List<Post>> getPostsFeed(List<String> UIds, int limit) {
        return databaseInstance.getPostsFeed(UIds, limit);
    }

    /**
     * This method is used to get the posts of a user's followings
     * @param UIds the user's id
     * @return a CompletableFuture that will be completed when the posts are retrieved
     */
    public static CompletableFuture<List<Post>> getPostsFeed(List<String> UIds) {
        return databaseInstance.getPostsFeed(UIds);
    }

    /**
     * This method is used to get the posts of a user's followings
     * @param post the post to be liked
     * @param UId the user's id
     * @return a CompletableFuture that will be completed when the posts are retrieved
     */
    public static CompletableFuture<Post> addLike(Post post, String UId) {
        return databaseInstance.addLike(post, UId);
    }

    /**
     * This method is used to get the posts of a user's followings
     * @param post the post to be liked
     * @param uid the user's id
     * @return a CompletableFuture that will be completed when the posts are retrieved
     */
    public static CompletableFuture<Post> removeLike(Post post, String uid) {
        return databaseInstance.removeLike(post, uid);
    }

    public static CompletableFuture<Follows> addFollow(String follower, String followed) {
        return databaseInstance.addFollow(follower, followed);
    }

    public static CompletableFuture<Follows> removeFollow(String follower, String followed) {
        return databaseInstance.removeFollow(follower, followed);
    }
}
