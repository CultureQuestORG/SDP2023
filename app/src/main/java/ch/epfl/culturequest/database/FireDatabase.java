package ch.epfl.culturequest.database;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import ch.epfl.culturequest.social.Image;
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
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        CompletableFuture<List<Profile>> future = new CompletableFuture<>();
        usersRef.orderByChild("username")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Profile> profiles = new ArrayList<>();
                        for (DataSnapshot snapshot : task.getResult().getChildren()) {
                            profiles.add(extractProfile(snapshot));
                        }
                        future.complete(profiles);
                    } else {
                        future.completeExceptionally(task.getException());
                    }
                });
        return future;
    }

    private Profile extractProfile(DataSnapshot snapshot) {
        String uid = getAttr(snapshot, "uid");
        String name = getAttr(snapshot, "name");
        String username = getAttr(snapshot, "username");
        String email = getAttr(snapshot, "email");
        String phoneNumber = null;
        String profilePic = getAttr(snapshot, "profilePic");
        List<Image> images = null; //TODO needs changing
        return new Profile(uid, name, username, email, phoneNumber, profilePic, images, 0);
    }

    private String getAttr(DataSnapshot snapshot, String key) {
        return snapshot.child(key).getValue(String.class);
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

}
