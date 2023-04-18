package ch.epfl.culturequest.database;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import ch.epfl.culturequest.social.Follows;
import ch.epfl.culturequest.social.Image;
import ch.epfl.culturequest.social.Post;
import ch.epfl.culturequest.social.Profile;

/**
 * This class is used to mock the database for testing purposes
 */
public class MockDatabase implements DatabaseInterface {
    HashMap<String, Object> map;

    public MockDatabase(HashMap<String, Object> map) {
        this.map = map;
    }

    public MockDatabase() {
        this(new HashMap<>());
    }

    @Override
    public void clearDatabase() {
        map.clear();
    }

    @Override
    public CompletableFuture<AtomicBoolean> set(String key, Object value) {
        CompletableFuture<AtomicBoolean> future = new CompletableFuture<>();
        map.put(key, value);
        future.complete(new AtomicBoolean(true));
        return future;
    }

    @Override
    public CompletableFuture<Object> get(String key) {
        CompletableFuture<Object> future = new CompletableFuture<>();
        future.complete(map.get(key));
        return future;
    }

    @Override
    public CompletableFuture<Profile> getProfile(String UId) {
        CompletableFuture<Profile> future = new CompletableFuture<>();
        future.complete((Profile) map.get("users/"+UId));
        return future;
    }

    @Override
    public CompletableFuture<List<Profile>> getAllProfiles() {
        CompletableFuture<List<Profile>> profiles = new CompletableFuture<>();
        profiles.complete((List<Profile>) map.get("allProfiles"));
        return profiles;
    }

    @Override
    public CompletableFuture<AtomicBoolean> setProfile(Profile profile) {
        CompletableFuture<AtomicBoolean> future = new CompletableFuture<>();
        map.put("users/"+profile.getUid(), profile);
        future.complete(new AtomicBoolean(true));
        return future;
    }

    @Override
    public CompletableFuture<Image> getImage(String UId) {
        CompletableFuture<Image> future = new CompletableFuture<>();
        future.complete((Image) map.get("pictures/"+UId));
        return future;
    }

    @Override
    public CompletableFuture<AtomicBoolean> deleteProfile(String uid) {
        return null;
    }

    @Override
    public CompletableFuture<AtomicBoolean> setImage(Image image) {
        CompletableFuture<AtomicBoolean> future = new CompletableFuture<>();
        map.put("pictures/"+image.getUid(), image);
        future.complete(new AtomicBoolean(true));
        return future;
    }

    @Override
    public CompletableFuture<Integer> getRank(String UId) {
        CompletableFuture<Integer> future = new CompletableFuture<>();
        future.complete((Integer) map.get("rank/"+UId));
        return future;
    }

    @Override
    public CompletableFuture<Integer> getRankFriends(String UId) {
        CompletableFuture<Integer> future = new CompletableFuture<>();
        future.complete((Integer) map.get("rank/"+UId));
        return future;
    }

    @Override
    public CompletableFuture<Integer> getNumberOfProfiles() {
        CompletableFuture<Integer> future = new CompletableFuture<>();
        future.complete((Integer) map.get("numberOfProfiles"));
        return future;
    }

    @Override
    public CompletableFuture<List<Profile>> getTopNProfiles(int n) {
        CompletableFuture<List<Profile>> future = new CompletableFuture<>();
        future.complete((List<Profile>) map.get("topNProfiles"));
        return future;
    }

    @Override
    public CompletableFuture<List<Profile>> getTopNFriendsProfiles(int n) {
        CompletableFuture<List<Profile>> future = new CompletableFuture<>();
        future.complete((List<Profile>) map.get("topNProfiles"));
        return future;
    }

    @Override
    public CompletableFuture<AtomicBoolean> uploadPost(Post post) {
        HashMap<String, Post> map1 = (HashMap<String, Post>) map.get("posts/"+post.getUid());
        if(map1 == null) {
            map1 = new HashMap<>();
            map.put("posts/"+post.getUid(), map1);
        }
        map1.put(post.getPostId(), post);
        return CompletableFuture.completedFuture(new AtomicBoolean(true));
    }

    @Override
    public CompletableFuture<AtomicBoolean> removePost(Post post) {
        HashMap<String, Post> map1 = (HashMap<String, Post>) map.get("posts/"+post.getUid());
        if(map1 != null) {
            map1.remove(post.getPostId());
            return CompletableFuture.completedFuture(new AtomicBoolean(true));
        }
        return CompletableFuture.completedFuture(new AtomicBoolean(false));
    }

    @Override
    public CompletableFuture<List<Post>> getPosts(String UId, int limit, int offset) {
        CompletableFuture<List<Post>> future = new CompletableFuture<>();
        List<Post> posts = new ArrayList<>(((HashMap<String, Post>) map.getOrDefault("posts/"+ UId, new HashMap<String, Post>())).values());
        future.complete(posts.subList(offset, Math.min(offset+limit, posts.size())));
        return future;
    }

    @Override
    public CompletableFuture<List<Post>> getPosts(String UId) {
        CompletableFuture<List<Post>> future = new CompletableFuture<>();
        List<Post> posts = new ArrayList<>(((HashMap<String, Post>) map.getOrDefault("posts/"+ UId, new HashMap<String, Post>())).values());
        future.complete(posts);
        return future;
    }

    @Override
    public CompletableFuture<List<Post>> getPostsFeed(List<String> UIds, int limit, int offset) {
        CompletableFuture<List<Post>> future = new CompletableFuture<>();

        List<Post> posts = new ArrayList<>();
        for(String UId : UIds) {
            posts.addAll(((HashMap<String, Post>) map.getOrDefault("posts/"+ UId, new HashMap<String, Post>())).values());
        }

        posts.sort(Comparator.comparing(Post::getTime).reversed());
        posts = posts.subList(offset, Math.min(offset+limit, posts.size()));

        future.complete(posts);
        return future;
    }

    @Override
    public CompletableFuture<List<Post>> getPostsFeed(List<String> UIds, int limit) {
        return getPostsFeed(UIds, limit, 0);
    }

    @Override
    public CompletableFuture<List<Post>> getPostsFeed(List<String> UIds) {
        return getPostsFeed(UIds, 10, 0);
    }

    @Override
    public CompletableFuture<Post> addLike(Post post, String UId) {
        return changeLike(post, UId, true);
    }

    @Override
    public CompletableFuture<Post> removeLike(Post post, String UId) {
        return changeLike(post, UId, false);
    }

    private CompletableFuture<Post> changeLike(Post post, String UId, boolean add) {
        HashMap<String, Post> map1 = (HashMap<String, Post>) map.get("posts/"+post.getUid());

        if(map1 != null) {
            Post post1 = map1.get(post.getPostId());
            if (post1 == null)  return CompletableFuture.completedFuture(null);
            if(add) post1.addLike(UId);
            else post1.removeLike(UId);
            return CompletableFuture.completedFuture(post1);
        }

        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Follows> addFollow(String follower, String followed) {
        return changeFollow(follower, followed, true);
    }

    @Override
    public CompletableFuture<Follows> removeFollow(String follower, String followed) {
        return changeFollow(follower, followed, false);
    }

    private CompletableFuture<Follows> changeFollow(String follower, String followed, boolean add) {
        CompletableFuture<Follows> future = new CompletableFuture<>();
        Follows follow = (Follows) map.get("follows/"+follower);
        if (follow == null) {
            follow = new Follows(List.of());
        }
        List<String> followedList = follow.getFollowed();
        if (add) {
            followedList.add(followed);
        } else {
            followedList.remove(followed);
        }

        follow.setFollowed(followedList);
        map.put("follows/" + follower, follow);
        return future;
    }
}

