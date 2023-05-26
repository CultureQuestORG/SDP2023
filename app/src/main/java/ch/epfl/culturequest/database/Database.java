package ch.epfl.culturequest.database;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import ch.epfl.culturequest.BuildConfig;
import ch.epfl.culturequest.backend.artprocessing.processingobjects.BasicArtDescription;
import ch.epfl.culturequest.backend.tournament.tournamentobjects.ArtQuiz;
import ch.epfl.culturequest.backend.tournament.tournamentobjects.QuizQuestion;
import ch.epfl.culturequest.backend.tournament.tournamentobjects.Tournament;
import ch.epfl.culturequest.notifications.PushNotification;
import ch.epfl.culturequest.social.Follows;
import ch.epfl.culturequest.social.Post;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.social.SightseeingEvent;


/**
 * This class is the implementation of the database using Firebase
 */
public class Database {
    private static final FirebaseDatabase databaseInstance = FirebaseDatabase.getInstance();
    private static boolean isEmulatorOn = false;

    public static void setPersistenceEnabled() {
        // !BuildConfig.IS_TESTING makes the code run only when we are not testing
        if (!BuildConfig.IS_TESTING.get()) {
            databaseInstance.setPersistenceEnabled(true);
            databaseInstance.getReference("users").keepSynced(true);
            databaseInstance.getReference("posts").keepSynced(true);
            databaseInstance.getReference("follows").keepSynced(true);
            databaseInstance.getReference("artworks").keepSynced(true);
            databaseInstance.getReference("notifications").keepSynced(true);
            databaseInstance.getReference("sightseeing_event").keepSynced(true);
            databaseInstance.getReference("tournaments").keepSynced(true);
        }
    }

    public static void setEmulatorOn() {
        if (!isEmulatorOn) {
            databaseInstance.useEmulator("10.0.2.2", 9000);
            isEmulatorOn = true;
        }
    }

    public static CompletableFuture<AtomicBoolean> clearDatabase() {
        CompletableFuture<AtomicBoolean> future = new CompletableFuture<>();
        if (isEmulatorOn) {
            databaseInstance.getReference().setValue(null).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    future.complete(new AtomicBoolean(true));
                } else {
                    future.complete(new AtomicBoolean(false));
                }
            });

            return future;
        }
        return CompletableFuture.completedFuture(new AtomicBoolean(false));
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
        ref.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                future.complete(new AtomicBoolean(true));
            } else {
                future.complete(new AtomicBoolean(false));
            }
        });

        return future;
    }

    public static CompletableFuture<AtomicBoolean> removeAllPosts(String uid) {
        CompletableFuture<AtomicBoolean> future = new CompletableFuture<>();
        DatabaseReference ref = databaseInstance.getReference("posts").child(uid);
        ref.removeValue().addOnCompleteListener(task -> {
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
        getFollowed(UId).whenComplete((followed, e) -> {
            if (followed == null || followed.getFollowed().isEmpty()) {
                future.complete(1);
                return;
            }

            System.out.println("Following " + followed.getFollowed().size());

            getTopNFriendsProfiles(followed.getFollowed().size() + 1).whenComplete((friendsProfiles, e2) -> {
                if (friendsProfiles == null) {
                    System.out.println("Error 1");
                    future.completeExceptionally(new RuntimeException("User not found"));
                    return;
                }

                int rank = findRank(UId, friendsProfiles);
                if (rank != -1) {
                    future.complete(rank);
                } else {
                    System.out.println("Error 2");
                    future.completeExceptionally(new RuntimeException("User not found in ranking"));
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
        Profile.getActiveProfile().retrieveFriends().thenAccept(friends -> {
            //the list of profiles to return (the top n profiles)
            List<Profile> profilesList = new ArrayList<>();

            //if the user does not have any friends, no need to fetch the database
            if (friends == null || friends.isEmpty()) {
                profilesList.add(Profile.getActiveProfile());
                future.complete(profilesList);
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
        }).exceptionally(e -> {
            future.completeExceptionally(e);
            return null;
        });

        return future;
    }

    public static CompletableFuture<AtomicBoolean> updateScore(String uid, int newScore) {
        CompletableFuture<AtomicBoolean> future = new CompletableFuture<>();
        DatabaseReference scoreRef = databaseInstance.getReference("users/" + uid + "/score");
        scoreRef.setValue(newScore, (error, ref) -> {
            future.complete(new AtomicBoolean(error == null));
        });
        return future;
    }


    public static CompletableFuture<HashMap<String, Integer>> updateBadges(String uid, List<String> newbadges) {
        CompletableFuture<HashMap<String, Integer>> future = new CompletableFuture<>();
        DatabaseReference badgesRef = databaseInstance.getReference("users/" + uid + "/badges");
        badgesRef.runTransaction(
                new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        HashMap<String, Integer> badges = mutableData.getValue(new GenericTypeIndicator<HashMap<String, Integer>>() {
                        });
                        if (badges == null) {
                            badges = new HashMap<>();
                        }
                        for (String badge : newbadges) {
                            if (badges.containsKey(badge)) {
                                badges.put(badge, badges.get(badge) + 1);
                            } else {
                                badges.put(badge, 1);
                            }
                        }
                        mutableData.setValue(badges);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                        if (committed) {
                            HashMap<String, Integer> badges = dataSnapshot.getValue(new GenericTypeIndicator<HashMap<String, Integer>>() {
                            });
                            future.complete(badges);
                        } else {
                            future.completeExceptionally(databaseError.toException());
                        }
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
                    follows = new Follows(new ArrayList<>());
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


    public static CompletableFuture<Follows> getFollowed(String UId) {
        CompletableFuture<Follows> future = new CompletableFuture<>();
        DatabaseReference followsRef = databaseInstance.getReference("follows").child(UId);
        followsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Follows follows = task.getResult().getValue(Follows.class);
                if (follows == null) {
                    follows = new Follows(new ArrayList<>());
                }
                future.complete(follows);
            } else {
                future.complete(new Follows(new ArrayList<>()));
            }
        });
        return future;
    }

    /**
     * This method is used to retrieve an artwork when scanning. It is intentionally not completing
     * the future if the artwork is not found, so that the api fetch can succeed before this one fails.
     *
     * @param artName the name of the artwork to be retrieved
     * @return a CompletableFuture that will be completed when the artwork is retrieved
     */
    public static CompletableFuture<BasicArtDescription> getArtworkScan(String artName) {
        CompletableFuture<BasicArtDescription> future = new CompletableFuture<>();
        DatabaseReference artRef = databaseInstance.getReference("artworks").child(artName);
        artRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                BasicArtDescription art = task.getResult().getValue(BasicArtDescription.class);
                if (art != null) {
                    future.complete(art);
                }
            }
        });
        return future;
    }

    /**
     * This method is used to retrieve an artwork.
     *
     * @param artName the name of the artwork to be retrieved
     * @return a CompletableFuture that will be completed when the artwork is retrieved, or will fail
     * if the artwork is not found.
     */
    public static CompletableFuture<BasicArtDescription> getArtwork(String artName) {
        CompletableFuture<BasicArtDescription> future = new CompletableFuture<>();
        DatabaseReference artRef = databaseInstance.getReference("artworks").child(artName);
        artRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                BasicArtDescription art = task.getResult().getValue(BasicArtDescription.class);
                if (art == null) {
                    future.completeExceptionally(new Exception("Artwork not found"));
                } else {
                    future.complete(art);
                }
            } else {
                future.completeExceptionally(new Exception("Artwork not found"));
            }
        });
        return future;
    }

    public static CompletableFuture<AtomicBoolean> setArtwork(BasicArtDescription artworks) {
        System.out.println("Setting artwork: " + artworks.getName());
        CompletableFuture<AtomicBoolean> future = new CompletableFuture<>();
        DatabaseReference artRef = databaseInstance.getReference("artworks").child(artworks.getName());
        artRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                if (currentData.getValue() == null) {
                    currentData.setValue(artworks);
                }
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(DatabaseError error, boolean committed, DataSnapshot currentData) {
                if (error != null) {
                    future.completeExceptionally(error.toException());
                } else {
                    future.complete(new AtomicBoolean(committed));
                }
            }
        });
        return future;
    }

    /**
     * This method is used to set the device tokens of a user. It is used to update the list of
     * devices to which the notifications of a user should be sent.
     *
     * @param UId          of the user for which the device tokens are going to be set
     * @param deviceTokens the list of device tokens to be set
     * @return a CompletableFuture that will be completed with an AtomicBoolean when the device tokens are set
     */
    public static CompletableFuture<AtomicBoolean> setDeviceTokens(String UId, List<String> deviceTokens) {
        CompletableFuture<AtomicBoolean> future = new CompletableFuture<>();
        databaseInstance.getReference("users").child(UId).child("deviceTokens").setValue(deviceTokens).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                future.complete(new AtomicBoolean(true));
            } else {
                future.completeExceptionally(task.getException());
            }
        });
        return future;
    }

    /**
     * This method is used to get the device tokens of a user. It is used to know to which devices
     * of a user a notification should be sent.
     *
     * @param UId of the user for which the device tokens are going to be retrieved
     * @return a CompletableFuture that will be completed with a list of device tokens when the device tokens are retrieved
     */
    public static CompletableFuture<List<String>> getDeviceTokens(String UId) {
        CompletableFuture<List<String>> future = new CompletableFuture<>();
        databaseInstance.getReference("users").child(UId).child("deviceTokens").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<String> deviceTokens = new ArrayList<>();
                for (DataSnapshot token : task.getResult().getChildren()) {
                    deviceTokens.add(token.getValue(String.class));
                }
                future.complete(deviceTokens);
            } else {
                future.completeExceptionally(task.getException());
            }
        });
        return future;
    }

    /**
     * This method is used to get the notifications of a user, ordered in reverse chronological order (i.e
     * the most recent notification is the first one in the list). It is used to display the notifications
     * in the app.
     *
     * @param UId of the user for which the notifications are going to be retrieved
     * @return a CompletableFuture that will be completed with a list of notifications when the notifications are retrieved
     */
    public static CompletableFuture<List<PushNotification>> getNotifications(String UId) {
        CompletableFuture<List<PushNotification>> future = new CompletableFuture<>();
        databaseInstance.getReference("notifications").child(UId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<PushNotification> notificationsList = new ArrayList<>();
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    notificationsList.add(snapshot.getValue(PushNotification.class));
                }

                // sort by time such that we get the latest notification first
                notificationsList.sort((p1, p2) -> Long.compare(p2.getTime(), p1.getTime()));
                future.complete(notificationsList);
            } else {
                future.completeExceptionally(task.getException());
            }
        });
        return future;
    }

    /**
     * This method is used to add a notification to a user. It is used when someone sends a
     * notification to a user.
     *
     * @param UId          of the user for which the notification is going to be added
     * @param notification the notification to be added
     * @return a CompletableFuture that will be completed with an AtomicBoolean when the notification is added
     */
    public static CompletableFuture<AtomicBoolean> addNotification(String UId, PushNotification notification) {
        CompletableFuture<AtomicBoolean> future = new CompletableFuture<>();
        DatabaseReference notificationRef = databaseInstance.getReference("notifications").child(UId).child(notification.getNotificationId());
        notificationRef.setValue(notification).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                future.complete(new AtomicBoolean(true));
            } else {
                future.completeExceptionally(task.getException());
            }
        });
        return future;
    }

    /**
     * This method is used to delete a notification from a user. It is used to delete old notifications.
     *
     * @param UId          of the user for which the notification is going to be deleted
     * @param notification the notification to be deleted
     * @return a CompletableFuture that will be completed with an AtomicBoolean when the notification is deleted
     */
    public static CompletableFuture<AtomicBoolean> deleteNotification(String UId, PushNotification notification) {
        CompletableFuture<AtomicBoolean> future = new CompletableFuture<>();
        DatabaseReference notificationRef = databaseInstance.getReference("notifications").child(UId).child(notification.getNotificationId());
        notificationRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                future.complete(new AtomicBoolean(true));
            } else {
                future.complete(new AtomicBoolean(false));
            }
        });
        return future;
    }

    public static CompletableFuture<AtomicBoolean> setSightseeingEvent(SightseeingEvent event) {
        CompletableFuture<AtomicBoolean> future = new CompletableFuture<>();
        DatabaseReference usersRef = databaseInstance.getReference("sightseeing_event");
        //// we need two separate set operations. if we merge both the owner also receives a notification...
        usersRef.child(event.getOwner().getUid()).child(String.valueOf(event.getEventId())).setValue(event).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                future.complete(new AtomicBoolean(true));
            } else {
                future.complete(new AtomicBoolean(false));
            }
        });

        event.getInvited().forEach(profile -> {
            usersRef.child(profile.getUid()).child(String.valueOf(event.getEventId())).setValue(event).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    future.complete(new AtomicBoolean(true));
                } else {
                    future.complete(new AtomicBoolean(false));
                }
            });
        });
        return future;
    }

    /**
     * Returns the event or events user with Uid is invited to/organizes
     *
     * @param Uid user id
     * @return the event or events organized by someone
     */
    public static CompletableFuture<List<SightseeingEvent>> getSightseeingEvents(String Uid) {
        CompletableFuture<List<SightseeingEvent>> future = new CompletableFuture<>();
        DatabaseReference usersRef = databaseInstance.getReference("sightseeing_event").child(Uid);
        usersRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<SightseeingEvent> events = new ArrayList<>();
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    SightseeingEvent event = snapshot.getValue(SightseeingEvent.class);
                    events.add(event);
                }
                future.complete(events);
            } else {
                future.completeExceptionally(task.getException());
            }
        });
        return future;
    }

    public static CompletableFuture<ArtQuiz> getQuiz(String tournament, String artName) {
        CompletableFuture<ArtQuiz> future = new CompletableFuture<>();

        DatabaseReference quizRef = databaseInstance.getReference("tournaments").child(tournament).child(artName);
        quizRef.get().addOnCompleteListener(task -> {
            //returns a list of questions
            if (task.isSuccessful()) {
                // there is a child questions and a child scores

                ArrayList<QuizQuestion> questions = new ArrayList<>();
                for (DataSnapshot child : task.getResult().child("questions").getChildren()) {
                    QuizQuestion question = child.getValue(QuizQuestion.class);
                    questions.add(question);
                }
                HashMap<String, Integer> scores = new HashMap<>();
                for (DataSnapshot child : task.getResult().child("scores").getChildren()) {
                    scores.put(child.getKey(), child.getValue(Integer.class));
                }
                future.complete(new ArtQuiz(artName, questions,scores));
            } else {
                future.completeExceptionally(new Exception("Quiz not found"));
            }
        });
        return future;
    }

    public static CompletableFuture<AtomicBoolean> addQuiz(ArtQuiz quiz,String tournament) {
        CompletableFuture<AtomicBoolean> future = new CompletableFuture<>();
        DatabaseReference quizRef = databaseInstance.getReference("tournaments").child(tournament).child(quiz.getArtName()).child("questions");
        quizRef.setValue(quiz.getQuestions()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                future.complete(new AtomicBoolean(true));
            } else {
                future.complete(new AtomicBoolean(false));
            }
        });
        return future;
    }

    public static void startQuiz(String tournament, String artName, String uid) {
        setScoreQuiz(tournament, artName, uid, 0);
    }

    public static void setScoreQuiz(String tournament, String artName, String uid, int score) {
        DatabaseReference quizRef = databaseInstance.getReference("tournaments").child(tournament).child("artQuizzes").child(artName).child("scores").child(uid);
        quizRef.setValue(score);
    }

    public static CompletableFuture<Integer> getScoreQuiz(String tournament, String artName, String uid) {
        DatabaseReference quizRef = databaseInstance.getReference("tournaments").child(tournament).child("artQuizzes").child(artName).child("scores").child(uid);
        CompletableFuture<Integer> future = new CompletableFuture<>();
        quizRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                future.complete(task.getResult().getValue(Integer.class));
            } else {
                future.completeExceptionally(task.getException());
            }
        });
        return future;
    }

    public static CompletableFuture<String> getImageForArt(String artwork){
        CompletableFuture<String> future = new CompletableFuture<>();
        // get the first post with "artworkName" = artwork
        DatabaseReference postsRef = databaseInstance.getReference("posts");
        postsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                //iterate over the users
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    //iterate over the posts
                    for (DataSnapshot post : snapshot.getChildren()) {
                        if (post.child("artworkName").getValue().equals(artwork)) {
                            future.complete(post.child("imageUrl").getValue().toString());
                            return;
                        }

                    }
                }
                future.completeExceptionally(new Exception("No post found for artwork " + artwork));
            } else {
                future.completeExceptionally(task.getException());
            }
        });
        return future;
    }

    ///// TOURNAMENTS //////


    // Indicate other users that the tournament is currently being generated
    public static CompletableFuture<Boolean> lockTournamentGeneration() {

        DatabaseReference pathToGenerationLock = getDeviceSynchronizationRef().child("generationLocked");
        return setBoolAsync(pathToGenerationLock, true);
    }


    // To unlock when the tournament is over or if one of the device fails to generate the tournament
    public static CompletableFuture<Boolean> unlockTournamentGeneration() {

        DatabaseReference pathToGenerationLock = getDeviceSynchronizationRef().child("generationLocked");
        return setBoolAsync(pathToGenerationLock, false);
    }


    // Allow of form of synchronization to prevent other devices from generating the tournament if one of the device has already been charged to do so
    public static CompletableFuture<Boolean> isTournamentGenerationLocked() {

        DatabaseReference pathToGenerationLock = getDeviceSynchronizationRef().child("generationLocked");
        return isEqualAsync(pathToGenerationLock, true);
    }

    // Indicate other devices that the tournament can now be fetched from Firebase
    public static CompletableFuture<Boolean> indicateTournamentGenerated() {

        DatabaseReference pathToGenerated = getDeviceSynchronizationRef().child("generated");
        return setBoolAsync(pathToGenerated, true);
    }

    // Reset the generation state of the tournament to allow upcoming generation in the next week

    public static CompletableFuture<Boolean> indicateTournamentNotGenerated() {

        DatabaseReference pathToGenerated = getDeviceSynchronizationRef().child("generated");
        return setBoolAsync(pathToGenerated, false);
    }

    // Slot where the variables used to handle the android apps synchronization are stored
    public static DatabaseReference getDeviceSynchronizationRef() {
        return databaseInstance.getReference("tournaments").child("device-synchronization");
    }


    // Put boolean in database reference and returns a future boolean indicating whether the operation was successful or not
    // true -> setValue succeeded; null -> setValue failed
    @SuppressLint("NewApi")
    public static CompletableFuture<Boolean> setBoolAsync(DatabaseReference databaseReference, Boolean bool) {


        CompletableFuture<Boolean> future = new CompletableFuture<>();
        databaseReference.setValue(bool, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {

                if (error != null) {
                    future.complete(null);
                } else {
                    future.complete(true);
                }
            }
        });

        handleFutureTimeout(future, 120);

        return future;
    }

    @SuppressLint("NewApi")
    public static CompletableFuture<Boolean> isEqualAsync(DatabaseReference databaseReference, Boolean expectedBool) {

        CompletableFuture<Boolean> future = new CompletableFuture<>();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean value = snapshot.getValue(Boolean.class);

                if (value == null) {
                    future.complete(null);
                } else {
                    future.complete(value.equals(expectedBool));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                future.complete(null);
            }
        });

        handleFutureTimeout(future, 120);

        return future;
    }

    public static <T> void handleFutureTimeout(CompletableFuture<T> future, int timeoutSeconds) {

        // create scheduled executor service
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.schedule(() -> {
            if (!future.isDone()) {
                future.completeExceptionally(new Exception("Timeout exception"));
            }
        }, timeoutSeconds, java.util.concurrent.TimeUnit.SECONDS);
    }

    public static CompletableFuture<Void> uploadTournamentToDatabase(Tournament tournament) {

        CompletableFuture<Void> voidFuture = new CompletableFuture<>();

        DatabaseReference tournamentRef = databaseInstance.getReference().child("tournaments").child(tournament.getTournamentId());

        auxiliaryUploadTournamentToDatabase(tournament, tournamentRef, 0, voidFuture);

        return voidFuture;
    }

    private static void auxiliaryUploadTournamentToDatabase(Tournament tournament, DatabaseReference tournamentRef, int tentativeNumber, CompletableFuture<Void> voidFuture) {

        tournamentRef.setValue(tournament, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                if (error != null) {

                    if (tentativeNumber == 2) {
                        // If the upload failed 2 times, we unlock the tournament generation so that another device can try to generate it
                        unlockTournamentGeneration();
                        voidFuture.completeExceptionally(new RuntimeException("Failed to upload tournament to database: " + error.getMessage()));
                        return;
                    }

                    // If the upload failed and tentative didn't reach max, try again
                    auxiliaryUploadTournamentToDatabase(tournament, tournamentRef, tentativeNumber + 1, voidFuture);

                } else {
                    // If the upload is successful, we tell the other devices that the tournament can be fetched from the database
                    indicateTournamentGenerated();
                    voidFuture.complete(null);
                }
            }
        });
    }

    public static CompletableFuture<Tournament> waitForTournamentGenerationAndFetchIt(AtomicReference<Tournament> fetchedTournament, CompletableFuture<Tournament> future, String tournamentId){

        DatabaseReference dbRef = databaseInstance.getReference();
        DatabaseReference tournamentRef = dbRef.child("tournaments").child(tournamentId);
        DatabaseReference generatedRef = getDeviceSynchronizationRef().child("generated");
        generatedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean isGenerated = dataSnapshot.exists() ? dataSnapshot.getValue(Boolean.class) : false;
                if (isGenerated) {
                    tournamentRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            fetchedTournament.set(dataSnapshot.getValue(Tournament.class));
                            future.complete(fetchedTournament.get());
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // todo: handle it better
                            future.completeExceptionally(new RuntimeException("Failed to read data from Firebase: " + databaseError.getMessage()));
                        }});}}
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // todo: handle it better
                future.completeExceptionally(new RuntimeException("Failed to read data from Firebase: " + databaseError.getMessage()));
            }
        });
        return future;
    }

    public static CompletableFuture<Long> fetchSeedIfAlreadyGenerated() {

        CompletableFuture<Long> seedFuture = new CompletableFuture<>();

        DatabaseReference seedReference = databaseInstance.getReference().child("tournaments").child("device-synchronization").child("seed");

        seedReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long seed = dataSnapshot.getValue(Long.class);
                if (seed == null) {
                    seedFuture.complete(null);
                } else {
                    seedFuture.complete(seed);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                seedFuture.completeExceptionally(new RuntimeException("Failed to read data from Firebase: " + databaseError.getMessage()));
            }
        });

        return seedFuture;
    }

    public static CompletableFuture<Void> uploadSeedToDatabase(Long seed){

        CompletableFuture<Void> voidFuture = new CompletableFuture<>();

        DatabaseReference seedReference = databaseInstance.getReference().child("tournaments").child("device-synchronization").child("seed");
        seedReference.setValue(seed, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                if (error != null) {
                    voidFuture.completeExceptionally(new RuntimeException("Failed to upload seed to database: " + error.getMessage()));
                } else {
                    voidFuture.complete(null);
                }
            }
        });

        return voidFuture;
    }

}