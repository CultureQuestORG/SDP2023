package ch.epfl.culturequest.database;

import android.net.LinkAddress;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import ch.epfl.culturequest.social.Image;
import ch.epfl.culturequest.social.Profile;

/**
 * This class is the implementation of the database using Firebase
 */
public class FireDatabase implements DatabaseInterface {
    FirebaseDatabase database;

    public FireDatabase() {
        database = FirebaseDatabase.getInstance();
    }


    @Override
    public void set(String key, Object value) {
        database.getReference(key).setValue(value);
    }

    @Override
    public CompletableFuture<Object> get(String key) {
        CompletableFuture<Object> future = new CompletableFuture<>();
        FirebaseDatabase.getInstance().getReference(key).get().addOnCompleteListener(task -> {
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
        return new Profile(uid, name, username, email, phoneNumber, profilePic, images);
    }

    private String getAttr(DataSnapshot snapshot, String key) {
        return snapshot.child(key).getValue(String.class);
    }

    @Override
    public void setProfile(Profile profile) {
        database.getReference("users").child(profile.getUid()).setValue(profile);
    }

    @Override
    public CompletableFuture<Profile> getProfile(String UId) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users").child(UId);
        return getValue(usersRef, Profile.class);
    }

    @Override
    public CompletableFuture<Image> getImage(String UId) {
        DatabaseReference imagesRef = FirebaseDatabase.getInstance().getReference("images").child(UId);
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

}
