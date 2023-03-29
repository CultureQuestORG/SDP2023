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
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import ch.epfl.culturequest.social.Image;
import ch.epfl.culturequest.social.Post;
import ch.epfl.culturequest.social.Profile;

/**
 * This class is the implementation of the database using Firebase
 */
public class FireDatabase implements DatabaseInterface {
    private final FirebaseDatabase database;

    public FireDatabase() {
        database = FirebaseDatabase.getInstance();
    }

    public FireDatabase(FirebaseDatabase database) {
        this.database = database;
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
                future.complete(new AtomicBoolean(true));
            } else {
                future.complete(new AtomicBoolean(false));
            }
        });
        return future;
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
        DatabaseReference usersRef = database.getReference("users");
        CompletableFuture<Integer> future = new CompletableFuture<>();
        getNumberOfProfiles().whenComplete((numberOfProfiles, e) -> {
            usersRef.orderByChild("score").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    int rank = numberOfProfiles;
                    for (DataSnapshot snapshot : task.getResult().getChildren()) {
                        if (Objects.equals(snapshot.getKey(), UId)) {
                            future.complete(rank);
                            return;
                        }
                        rank--;
                    }
                    future.completeExceptionally(new RuntimeException("User not found"));
                } else {
                    future.completeExceptionally(task.getException());
                }
            });
        });
        return future;
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
        getNumberOfProfiles().whenComplete((numberOfUsers, e) -> {
            usersRef.orderByChild("score").limitToLast(n).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    List<Profile> profilesList = new ArrayList<>();
                    for (DataSnapshot snapshot : task.getResult().getChildren()) {
                        Profile profile = snapshot.getValue(Profile.class);
                        profilesList.add(profile);
                    }
                    future.complete(profilesList);
                } else {
                    future.completeExceptionally(task.getException());
                }
            });
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
                    if(++i == limit) break;
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
    public CompletableFuture<AtomicBoolean> addLike(Post post, String UId) {
        CompletableFuture<AtomicBoolean> future = new CompletableFuture<>();
        DatabaseReference usersRef = database.getReference("posts").child(post.getUid()).child(post.getPostid()).child("likes");

        updateLiker(post, UId, true).whenComplete((v, e) -> {
            if (e != null) {
                future.complete(new AtomicBoolean(false));
            } else {
                usersRef.runTransaction(handler(future, post, UId, true));
            }
        });

        return future;
    }

    private Transaction.Handler handler(CompletableFuture<AtomicBoolean> future, Post post, String UId, boolean add) {
        return new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                if (mutableData.getValue() == null) {
                    mutableData.setValue(add ? 1 : 0);
                } else {
                    mutableData.setValue(add ? (Long) mutableData.getValue() + 1 : (Long) mutableData.getValue() - 1);
                }

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                if (databaseError != null) {
                    future.complete(new AtomicBoolean(false));
                } else {
                    future.complete(new AtomicBoolean(true));
                }
            }
        };
    }

    /**
     * @param post   the post to remove the like from
     * @param UId    the id of the user who liked the post
     * @return a future that will return true if the like was removed successfully, false otherwise
     */
    @Override
    public CompletableFuture<AtomicBoolean> removeLike(Post post, String UId) {
        CompletableFuture<AtomicBoolean> future = new CompletableFuture<>();
        DatabaseReference usersRef = database.getReference("posts").child(post.getUid()).child(post.getPostid()).child("likes");

        updateLiker(post, UId, false).whenComplete((v, e) -> {
            if (e != null) {
                future.complete(new AtomicBoolean(false));
            } else {
                usersRef.runTransaction(handler(future, post, UId, false));
            }
        });

        return future;
    }

    /**
     * Updates the likers of a post. * Already implemented in the addLike and removeLike methods *
     * @param post   the post to update the likers of
     * @param UId    the id of the user
     * @param update true if the user liked the post, false otherwise
     * @return true if the user liked the post, false otherwise
     */
    public CompletableFuture<AtomicBoolean> updateLiker(Post post, String UId, boolean update) {
        CompletableFuture<AtomicBoolean> future = new CompletableFuture<>();
        DatabaseReference usersRef = database.getReference("posts").child(post.getUid()).child(post.getPostid()).child("likers");

        if(update) {
            usersRef.child(String.valueOf(post.getLikers().size())).setValue(UId).addOnCompleteListener(task -> {
                System.out.println("task: " + task.isSuccessful());
                if (task.isSuccessful()) {
                    future.complete(new AtomicBoolean(true));
                } else {
                    future.complete(new AtomicBoolean(false));
                }
            });
        } else {
            usersRef.child(String.valueOf(post.getLikers().indexOf(UId))).removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    future.complete(new AtomicBoolean(true));
                } else {
                    future.complete(new AtomicBoolean(false));
                }
            });
        }
        return future;
    }


}
