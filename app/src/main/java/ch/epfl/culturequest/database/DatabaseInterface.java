package ch.epfl.culturequest.database;

import ch.epfl.culturequest.social.Image;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import ch.epfl.culturequest.social.Post;
import ch.epfl.culturequest.social.Profile;

/**
 * This interface is used to abstract the database
 */
public interface DatabaseInterface {
    CompletableFuture<AtomicBoolean> set(String key, Object value);

    CompletableFuture<Object> get(String key);

    CompletableFuture<Profile> getProfile(String UId);

    CompletableFuture<List<Profile>> getAllProfiles();

    CompletableFuture<AtomicBoolean> setProfile(Profile profile);

    CompletableFuture<Image> getImage(String UId);

    CompletableFuture<AtomicBoolean> deleteProfile(String uid);

    CompletableFuture<AtomicBoolean> setImage(Image picture);

    CompletableFuture<Integer> getRank(String UId);

    CompletableFuture<Integer> getNumberOfProfiles();

    CompletableFuture<List<Profile>> getTopNProfiles(int n);


    /////////////////////////// POSTS ///////////////////////////

    // Upload a post
    CompletableFuture<AtomicBoolean> uploadPost(Post post);

    // Get the posts of a user with a limit and an offset
    CompletableFuture<List<Post>> getPosts(String UId, int limit, int offset);

    // Get the posts of a user followings with a limit and an offset
    CompletableFuture<List<Post>> getPostsFeed(List<String> UIds, int limit, int offset);

    // Get the posts of a user followings with a limit
    CompletableFuture<List<Post>> getPostsFeed(List<String> UIds, int limit);

    // Get the posts of a user followings
    CompletableFuture<List<Post>> getPostsFeed(List<String> UIds);

    // Add a like to a post
    CompletableFuture<Post> addLike(Post post, String UId);

    // Remove a like from a post
    CompletableFuture<Post> removeLike(Post post, String UId);
}
