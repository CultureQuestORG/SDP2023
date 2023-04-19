package ch.epfl.culturequest.database;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import ch.epfl.culturequest.social.Follows;
import ch.epfl.culturequest.social.Post;
import ch.epfl.culturequest.social.Profile;


/**
 * This class is the implementation of the database using Firebase
 */
public class Database {
    private static final FirebaseDatabase databaseInstance = FirebaseDatabase.getInstance();
    private static boolean isEmulatorOn = false;

    public static void setEmulatorOn() {
        if (!isEmulatorOn) {
            databaseInstance.useEmulator("10.0.2.2", 9000);
            isEmulatorOn = true;
        }
    }

    public static CompletableFuture<AtomicBoolean> clearDatabase() {
        CompletableFuture<AtomicBoolean> future = new CompletableFuture<>();
        databaseInstance.getReference().setValue(null).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                future.complete(new AtomicBoolean(true));
            } else {
                future.complete(new AtomicBoolean(false));
            }
        });
        return future;
    }

    private static <T> CompletableFuture<T> getValue(DatabaseReference ref, Class<T> valueType) {
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

    public static CompletableFuture<Profile> getProfile(String UId) {
        DatabaseReference usersRef = databaseInstance.getReference("users").child(UId);
        return getValue(usersRef, Profile.class);
    }

    public static CompletableFuture<List<Profile>> getAllProfiles() {
        return getNumberOfProfiles().thenCompose(Database::getTopNProfiles);
    }

    public static CompletableFuture<AtomicBoolean> setProfile(Profile profile) {
        CompletableFuture<AtomicBoolean> future = new CompletableFuture<>();
        databaseInstance.getReference("users").child(profile.getUid()).setValue(profile).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                future.complete(new AtomicBoolean(true));
            } else {
                future.complete(new AtomicBoolean(false));
            }
        });
        return future;
    }

    public static CompletableFuture<AtomicBoolean> deleteProfile(String uid) {
        CompletableFuture<AtomicBoolean> future = new CompletableFuture<>();
        DatabaseReference ref = databaseInstance.getReference("users").child(uid);
        ref.removeValue((error, ref1) -> {
            if (error == null) {
                removeAllPosts(uid, future);
            } else {
                future.complete(new AtomicBoolean(false));
            }
        });
        return future;
    }

    private static void removeAllPosts(String uid, CompletableFuture<AtomicBoolean> future) {
        DatabaseReference ref = databaseInstance.getReference("posts").child(uid);
        ref.removeValue((error, ref1) -> {
            if (error == null) {
                future.complete(new AtomicBoolean(true));
            } else {
                future.complete(new AtomicBoolean(false));
            }
        });
    }

    /**
     * @param UId the user's id
     * @return the rank of the user in the database with respect to their score
     */
    public static CompletableFuture<Integer> getRank(String UId) {
        CompletableFuture<Integer> future = new CompletableFuture<>();
        getAllProfiles().whenComplete((allProfiles, e) -> {
            int rank = findRank(UId, allProfiles);
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
    public static CompletableFuture<Integer> getRankFriends(String UId) {
        CompletableFuture<Integer> future = new CompletableFuture<>();
        getProfile(UId).whenComplete((profile, e) -> {
            if (profile == null) {
                future.completeExceptionally(new RuntimeException("User not found"));
                return;
            }

            getTopNFriendsProfiles(profile.getFriends().size() + 1).whenComplete((friendsProfiles, e2) -> {
                if (friendsProfiles == null) {
                    future.completeExceptionally(new RuntimeException("User not found"));
                    return;
                }

                int rank = findRank(UId, friendsProfiles);
                if (rank != -1) {
                    future.complete(rank);
                } else {
                    future.completeExceptionally(new RuntimeException("User not found"));
                }

            });
        });

        return future;
    }

    private static int findRank(String UId, List<Profile> profiles) {
        int rank = 1;
        for (Profile p : profiles) {
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
    public static CompletableFuture<Integer> getNumberOfProfiles() {
        DatabaseReference usersRef = databaseInstance.getReference("users");
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
    public static CompletableFuture<List<Profile>> getTopNProfiles(int n) {
        DatabaseReference usersRef = databaseInstance.getReference("users");
        CompletableFuture<List<Profile>> future = new CompletableFuture<>();
        usersRef.orderByChild("score").limitToLast(n).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Profile> profilesList = new ArrayList<>();
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    Profile profile = snapshot.getValue(Profile.class);
                    profilesList.add(profile);
                }
                profilesList.sort((p1, p2) -> p2.getScore().compareTo(p1.getScore()));
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
    public static CompletableFuture<List<Profile>> getTopNFriendsProfiles(int n) {
        DatabaseReference usersRef = databaseInstance.getReference("users");
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
                topNProfiles.sort((p1, p2) -> p2.getScore().compareTo(p1.getScore()));
                future.complete(topNProfiles);
            } else {
                future.completeExceptionally(task.getException());
            }
        });
        return future;
    }

    /**
     * This method is used to upload a post to the database
     *
     * @param post the post to be uploaded
     * @return a CompletableFuture that will be completed when the upload is done
     */
    public static CompletableFuture<AtomicBoolean> uploadPost(Post post) {
        CompletableFuture<AtomicBoolean> future = new CompletableFuture<>();
        DatabaseReference usersRef = databaseInstance.getReference("posts").child(post.getUid()).child(String.valueOf(post.getPostId()));
        usersRef.setValue(post).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                future.complete(new AtomicBoolean(true));
            } else {
                future.complete(new AtomicBoolean(false));
            }
        });
        return future;
    }

    /**
     * This method is used to remove a post from the database
     *
     * @param post the post to be removed
     * @return a CompletableFuture that will be completed when the removal is done
     */
    public static CompletableFuture<AtomicBoolean> removePost(Post post) {
        CompletableFuture<AtomicBoolean> future = new CompletableFuture<>();
        DatabaseReference usersRef = databaseInstance.getReference("posts").child(post.getUid()).child(post.getPostId());
        usersRef.removeValue((error, ref1) -> {
            if (error == null) {
                future.complete(new AtomicBoolean(true));
            } else {
                future.complete(new AtomicBoolean(false));
            }
        });
        return future;
    }

    /**
     * This method is used to get the posts of a user
     *
     * @param UId    the user's id
     * @param limit  the maximum number of posts to be returned
     * @param offset the number of posts to be skipped
     * @return a CompletableFuture that will be completed when the posts are retrieved
     */
    public static CompletableFuture<List<Post>> getPosts(String UId, int limit, int offset) {
        CompletableFuture<List<Post>> future = new CompletableFuture<>();
        DatabaseReference postsRef = databaseInstance.getReference("posts").child(UId);
        postsRef.orderByChild("time").limitToLast(limit + offset).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Post> posts = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    if (post != null) posts.add(post);
                }
                // Order posts by time descending
                Collections.reverse(posts);
                // Return only the requested number of posts
                if (posts.size() > offset) {
                    posts = posts.subList(offset, posts.size());
                } else {
                    posts = Collections.emptyList();
                }
                future.complete(posts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                future.completeExceptionally(databaseError.toException());
            }
        });
        return future;
    }

    /**
     * This method is used to get the posts of a user
     *
     * @param UId the user's id
     */
    public static CompletableFuture<List<Post>> getPosts(String UId) {
        CompletableFuture<List<Post>> future = new CompletableFuture<>();
        DatabaseReference usersRef = databaseInstance.getReference("posts").child(UId);
        usersRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Post> posts = new ArrayList<>();
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    posts.add(post);
                }
                // sort by time such that we get the latest posts first
                posts.sort((p1, p2) -> Long.compare(p2.getTime(), p1.getTime()));
                future.complete(posts);
            } else {
                future.complete(List.of());
            }
        });
        return future;
    }

    /**
     * This method is used to get the posts of a user's followings
     *
     * @param UIds   the user's id
     * @param limit  the maximum number of posts to be returned
     * @param offset the number of posts to be skipped
     * @return a CompletableFuture that will be completed when the posts are retrieved
     */
    public static CompletableFuture<List<Post>> getPostsFeed(List<String> UIds, int limit, int offset) {
        //List of posts to retrieve from each user
        CompletableFuture<List<Post>>[] futures = UIds.stream().map((uid) -> {
            return getPosts(uid, limit, 0);
        }).toArray(CompletableFuture[]::new);

        //Future to return
        CompletableFuture<List<Post>> result = new CompletableFuture<>();

        //Wait for all futures to complete
        CompletableFuture.allOf(futures).whenComplete((v, e) -> {

            //Merge all posts into one list
            List<Post> posts = new ArrayList<>();
            for (CompletableFuture<List<Post>> future : futures) {
                posts.addAll(future.join());
            }

            posts.sort(Comparator.comparing(Post::getTime).reversed());
            posts = posts.subList(offset, Math.min(posts.size(), limit + offset));
            result.complete(posts);

        }).exceptionally(e -> {
            result.complete(List.of());
            return null;
        });

        return result;
    }

    /**
     * This method is used to get the posts of a user's followings
     *
     * @param UIds  the user's id
     * @param limit the maximum number of posts to be returned
     * @return a CompletableFuture that will be completed when the posts are retrieved
     */
    public static CompletableFuture<List<Post>> getPostsFeed(List<String> UIds, int limit) {
        return getPostsFeed(UIds, limit, 0);
    }

    /**
     * This method is used to get the posts of a user's followings
     *
     * @param UIds the user's id
     * @return a CompletableFuture that will be completed when the posts are retrieved
     */
    public static CompletableFuture<List<Post>> getPostsFeed(List<String> UIds) {
        return getPostsFeed(UIds, 100, 0);
    }

    /**
     * This method is used to get the posts of a user's followings
     *
     * @param post the post to be liked
     * @param UId  the user's id
     * @return a CompletableFuture that will be completed when the posts are retrieved
     */
    public static CompletableFuture<Post> addLike(Post post, String UId) {
        return changeLike(post, UId, true);
    }

    /**
     * @param post the post to remove the like from
     * @param UId  the id of the user who liked the post
     * @return a future that will return true if the like was removed successfully, false otherwise
     */
    public static CompletableFuture<Post> removeLike(Post post, String UId) {
        return changeLike(post, UId, false);
    }

    private static CompletableFuture<Post> changeLike(Post post, String UId, boolean add) {
        CompletableFuture<Post> future = new CompletableFuture<>();
        DatabaseReference usersRef = databaseInstance.getReference("posts").child(post.getUid()).child(post.getPostId());

        usersRef.runTransaction(handler(future, UId, add));

        return future;
    }

    private static Transaction.Handler handler(CompletableFuture<Post> future, String UId, boolean add) {
        return new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                Post dbPost = mutableData.getValue(Post.class);

                if (dbPost == null) {
                    return Transaction.success(mutableData);
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

    public static CompletableFuture<Follows> addFollow(String follower, String followed) {
        return changeFollow(follower, followed, true);
    }

    public static CompletableFuture<Follows> removeFollow(String follower, String followed) {
        return changeFollow(follower, followed, false);
    }

    private static CompletableFuture<Follows> changeFollow(String follower, String followed, boolean follow) {
        CompletableFuture<Follows> future = new CompletableFuture<>();
        DatabaseReference followsRef = databaseInstance.getReference("follows").child(follower);

        followsRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
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
