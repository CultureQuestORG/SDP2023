package ch.epfl.culturequest.database;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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


}
