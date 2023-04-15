package ch.epfl.culturequest.database;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import ch.epfl.culturequest.social.Follows;
import ch.epfl.culturequest.social.Image;
import ch.epfl.culturequest.social.Post;
import ch.epfl.culturequest.social.Profile;

/**
 * This class is the implementation of the database using Firebase
 */
public class FireDatabase implements DatabaseInterface {
    private final FirebaseDatabase database;

    public FireDatabase() {
        this.database = FirebaseDatabase.getInstance();
    }

    public FireDatabase(FirebaseDatabase database) {
        this.database = database;
    }

    @Override
    public void clearDatabase() {
        database.getReference().setValue(null);
    }

    @Override
    public CompletableFuture<AtomicBoolean> set(String key, Object value) {
        CompletableFuture<AtomicBoolean> future = new CompletableFuture<>();
        database.getReference(key).setValue(value).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                future.complete(new AtomicBoolean(true));
            } else {
                future.complete(new AtomicBoolean(false));
            }
        });
        return future;
    }

    @Override
    public CompletableFuture<Object> get(String key) {
        CompletableFuture<Object> future = new CompletableFuture<>();
        database.getReference(key).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                future.complete(task.getResult().getValue());
            } else {
                future.completeExceptionally(task.getException());
            }
        });
        return future;
    }


    @Override
    public CompletableFuture<List<Profile>> getAllProfiles() {
        return getNumberOfProfiles().thenCompose(this::getTopNProfiles);
    }

    @Override
    public CompletableFuture<AtomicBoolean> setProfile(Profile profile) {
        CompletableFuture<AtomicBoolean> future = new CompletableFuture<>();
        database.getReference("users").child(profile.getUid()).setValue(profile).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                future.complete(new AtomicBoolean(true));
            } else {
                future.complete(new AtomicBoolean(false));
            }
        });
        return future;
    }

    @Override
    public CompletableFuture<AtomicBoolean> deleteProfile(String uid) {
        CompletableFuture<AtomicBoolean> future = new CompletableFuture<>();
        DatabaseReference ref = database.getReference("users").child(uid);
        ref.removeValue((error, ref1) -> {
            if (error == null) {
                removeAllPosts(uid, future);
            } else {
                future.complete(new AtomicBoolean(false));
            }
        });
        return future;
    }

    private void removeAllPosts(String uid, CompletableFuture<AtomicBoolean> future) {
        DatabaseReference ref = database.getReference("posts").child(uid);
        ref.removeValue((error, ref1) -> {
            if (error == null) {
                future.complete(new AtomicBoolean(true));
            } else {
                future.complete(new AtomicBoolean(false));
            }
        });
    }

    @Override
    public CompletableFuture<Profile> getProfile(String UId) {
        DatabaseReference usersRef = database.getReference("users").child(UId);
        return getValue(usersRef, Profile.class);
    }

    @Override
    public CompletableFuture<Image> getImage(String UId) {
        DatabaseReference imagesRef = database.getReference("images").child(UId);
        return getValue(imagesRef, Image.class);
    }

    private <T> CompletableFuture<T> getValue(DatabaseReference ref, Class<T> valueType) {
        CompletableFuture<T> future = new CompletableFuture<>();
        ref.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                T value = task.getResult().getValue(valueType);
                future.complete(value);
            } else {
                future.completeExceptionally(task.getException());
            }
        });
        return future;
    }

    @Override
    public CompletableFuture<AtomicBoolean> setImage(Image image) {
        CompletableFuture<AtomicBoolean> future = new CompletableFuture<>();
        database.getReference("images").child(image.getUid()).setValue(image).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                future.complete(new AtomicBoolean(true));
            } else {
                future.complete(new AtomicBoolean(false));
            }
        });
        return future;
    }

    /**
     * @param UId the user's id
     * @return the rank of the user in the database with respect to their score
     */
    @Override
    public CompletableFuture<Integer> getRank(String UId) {

        CompletableFuture<Integer> future = new CompletableFuture<>();
        getAllProfiles().whenComplete((allProfiles, e) -> {
            int rank = findRank(UId,allProfiles);
            if (rank != -1) {
                future.complete(rank);
            } else {
                future.completeExceptionally(new RuntimeException("User not found"));
            }
        });
        return future;
    }


    /**
     * @param UId the user's id
     * @return the rank of the user in the database with respect to their score among his friends
     */
    @Override
    public CompletableFuture<Integer> getRankFriends(String UId) {
        CompletableFuture<Integer> future = new CompletableFuture<>();
        getProfile(UId).whenComplete((profile, e) -> {
            if (profile == null) {
                future.completeExceptionally(new RuntimeException("User not found"));
                return;
            }

            getTopNFriendsProfiles(profile.getFriends().size()+1).whenComplete((friendsProfiles, e2) -> {
                if (friendsProfiles == null) {
                    future.completeExceptionally(new RuntimeException("User not found"));
                    return;
                }

                int rank = findRank(UId,friendsProfiles);
                if (rank != -1) {
                    future.complete(rank);
                } else {
                    future.completeExceptionally(new RuntimeException("User not found"));
                }

            });
        });

        return future;
    }


    private int findRank(String UId,List<Profile> profiles){
        int rank = 1;
        for (Profile p :profiles){
            if (Objects.equals(p.getUid(), UId)) {
                return rank;
            }
            rank++;
        }
        return -1;
    }

    /**
     * @return the number of profiles in the database
     */
    @Override
    public CompletableFuture<Integer> getNumberOfProfiles() {
        DatabaseReference usersRef = database.getReference("users");
        CompletableFuture<Integer> future = new CompletableFuture<>();
        usersRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                future.complete((int) task.getResult().getChildrenCount());
            } else {
                future.completeExceptionally(task.getException());
            }
        });
        return future;
    }

    /**
     * @param n the number of profiles to get
     * @return the top n profiles in the database with respect to their score
     */
    @Override
    public CompletableFuture<List<Profile>> getTopNProfiles(int n) {
        DatabaseReference usersRef = database.getReference("users");
        CompletableFuture<List<Profile>> future = new CompletableFuture<>();
        usersRef.orderByChild("score").limitToLast(n).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Profile> profilesList = new ArrayList<>();
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    Profile profile = snapshot.getValue(Profile.class);
                    profilesList.add(profile);
                }
                profilesList.sort((p1,p2)-> p2.getScore().compareTo(p1.getScore()));
                future.complete(profilesList);
            } else {
                future.completeExceptionally(task.getException());
            }
        });
        return future;
    }

    /**
     * @param n the number of profiles to get
     * @return the top n profiles in the database with respect to their score among the active user friends
     */
    @Override
    public CompletableFuture<List<Profile>> getTopNFriendsProfiles(int n) {
        DatabaseReference usersRef = database.getReference("users");
        CompletableFuture<List<Profile>> future = new CompletableFuture<>();
        List<String> friends = Profile.getActiveProfile().getFriends();
        List<Profile> profilesList = new ArrayList<>();

        //if the user does not have any friends, no need to fetch the database
        if (friends == null || friends.isEmpty()) {
            profilesList.add(Profile.getActiveProfile());
            future.complete(profilesList);
            return future;
        }

        usersRef.orderByChild("score").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    Profile profile = snapshot.getValue(Profile.class);
                    if (profile != null && (friends.contains(profile.getUid()) || profile.getUid().equals(Profile.getActiveProfile().getUid()))) {
                        profilesList.add(profile);
                    }
                }
                List<Profile> topNProfiles = profilesList.subList(0, Math.min(n, profilesList.size()));
                topNProfiles.sort((p1,p2)-> p2.getScore().compareTo(p1.getScore()));
                future.complete(topNProfiles);
            } else {
                future.completeExceptionally(task.getException());
            }
        });
        return future;
    }

    @Override
    public CompletableFuture<AtomicBoolean> uploadPost(Post post) {
        CompletableFuture<AtomicBoolean> future = new CompletableFuture<>();
        DatabaseReference usersRef = database.getReference("posts").child(post.getUid()).child(String.valueOf(post.getPostid()));
        usersRef.setValue(post).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                future.complete(new AtomicBoolean(true));
            } else {
                future.complete(new AtomicBoolean(false));
            }
        });
        return future;
    }

    @Override
    public CompletableFuture<AtomicBoolean> removePost(Post post) {
        CompletableFuture<AtomicBoolean> future = new CompletableFuture<>();
        DatabaseReference usersRef = database.getReference("posts").child(post.getUid()).child(post.getPostid());
        usersRef.removeValue((error, ref1) -> {
            if (error == null) {
                future.complete(new AtomicBoolean(true));
            } else {
                future.complete(new AtomicBoolean(false));
            }
        });
        return future;
    }

    @Override
    public CompletableFuture<List<Post>> getPosts(String UId, int limit, int offset) {
        CompletableFuture<List<Post>> future = new CompletableFuture<>();
        DatabaseReference usersRef = database.getReference("posts").child(UId);
        usersRef.orderByChild("date").limitToLast(limit + offset).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Post> posts = new ArrayList<>();
                int i = 0;
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    posts.add(post);
                    if (++i == limit) break;
                }
                future.complete(posts);
            } else {
                future.complete(List.of());
            }
        });
        return future;
    }

    @Override
    public CompletableFuture<List<Post>> getPostsFeed(List<String> UIds, int limit, int offset) {
        //List of posts to retrieve from each user
        CompletableFuture<List<Post>>[] futures = UIds.stream().map((uid) -> getPosts(uid, limit, 0)).toArray(CompletableFuture[]::new);

        //Future to return
        CompletableFuture<List<Post>> result = new CompletableFuture<>();

        //Wait for all futures to complete
        CompletableFuture.allOf(futures).whenComplete((v, e) -> {

            //Merge all posts into one list
            List<Post> posts = new ArrayList<>();
            for (CompletableFuture<List<Post>> future : futures) {
                posts.addAll(future.join());
            }

            posts.sort(Comparator.comparing(Post::getDate).reversed());
            posts = posts.subList(offset, Math.min(posts.size(), limit + offset));
            result.complete(posts);

        }).exceptionally(e -> {
            result.complete(List.of());
            return null;
        });

        return result;
    }

    @Override
    public CompletableFuture<List<Post>> getPostsFeed(List<String> UIds, int limit) {
        return getPostsFeed(UIds, limit, 0);
    }

    @Override
    public CompletableFuture<List<Post>> getPostsFeed(List<String> UIds) {
        return getPostsFeed(UIds, 100, 0);
    }

    @Override
    public CompletableFuture<Post> addLike(Post post, String UId) {
        return changeLike(post, UId, true);
    }

    /**
     * @param post the post to remove the like from
     * @param UId  the id of the user who liked the post
     * @return a future that will return true if the like was removed successfully, false otherwise
     */
    @Override
    public CompletableFuture<Post> removeLike(Post post, String UId) {
        return changeLike(post, UId, false);
    }

    private CompletableFuture<Post> changeLike(Post post, String UId, boolean add) {
        CompletableFuture<Post> future = new CompletableFuture<>();
        DatabaseReference usersRef = database.getReference("posts").child(post.getUid()).child(post.getPostid());

        usersRef.runTransaction(handler(future, UId, add));

        return future;
    }

    private Transaction.Handler handler(CompletableFuture<Post> future, String UId, boolean add) {
        return new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                Post dbPost = mutableData.getValue(Post.class);

                if (dbPost == null) {
                    future.completeExceptionally(new RuntimeException("Post not found"));
                    return Transaction.abort();
                }

                if (add) {
                    dbPost.addLike(UId);
                } else {
                    dbPost.removeLike(UId);
                }

                mutableData.setValue(dbPost);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                if (databaseError != null || dataSnapshot == null) {
                    future.complete(null);
                } else {
                    future.complete(dataSnapshot.getValue(Post.class));
                }
            }
        };
    }

    @Override
    public CompletableFuture<Follows> addFollow(String follower, String followed) {
        return changeFollow(follower, followed, true);
    }

    @Override
    public CompletableFuture<Follows> removeFollow(String follower, String followed) {
        return changeFollow(follower, followed, false);
    }

    private CompletableFuture<Follows> changeFollow(String follower, String followed, boolean follow) {
        CompletableFuture<Follows> future = new CompletableFuture<>();
        DatabaseReference followsRef = database.getReference("follows").child(follower);

        followsRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData currentData) {
                Follows follows = currentData.getValue(Follows.class);
                if (follows == null) {
                    follows = new Follows(List.of());
                }
                if (follow) {
                    follows.addFollowed(followed);
                } else {
                    follows.removeFollowed(followed);
                }
                currentData.setValue(follows);
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(DatabaseError error, boolean committed, DataSnapshot currentData) {
                if (error != null) {
                    future.completeExceptionally(error.toException());
                } else {
                    future.complete(currentData.getValue(Follows.class));
                }
            }
        });

        return future;
    }

}
