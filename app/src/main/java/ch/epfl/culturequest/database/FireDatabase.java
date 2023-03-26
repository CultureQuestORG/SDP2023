package ch.epfl.culturequest.database;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import ch.epfl.culturequest.social.Image;
import ch.epfl.culturequest.social.Profile;

public class FireDatabase implements DatabaseInterface {
    private final FirebaseDatabase database;

    public FireDatabase() {
        database = FirebaseDatabase.getInstance();
    }

    public FireDatabase(FirebaseDatabase database) {
        this.database = database;
    }

    @Override
    public void set(String key, Object value) {
        database.getReference(key).setValue(value);
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
    public void setProfile(Profile profile) {
        database.getReference("users").child(profile.getUid()).setValue(profile);
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
    public void setImage(Image image) {
        database.getReference("images").child(image.getUid()).setValue(image);
    }

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
