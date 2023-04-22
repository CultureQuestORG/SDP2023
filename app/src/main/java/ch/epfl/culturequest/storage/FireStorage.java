package ch.epfl.culturequest.storage;

import android.net.Uri;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.utils.ProfileUtils;

public class FireStorage {
    private static final FirebaseStorage storage = FirebaseStorage.getInstance();
    private static boolean isEmulatorOn = false;

    public static void setEmulatorOn() {
        if (!isEmulatorOn) {
            storage.useEmulator("10.0.2.2", 9199);
            isEmulatorOn = true;
        }
    }

    public static CompletableFuture<Profile> storeNewProfilePictureInStorage(Profile profile, String profilePicUri) {
        CompletableFuture<Profile> future = new CompletableFuture<>();
        //upload image to firebase storage
        UploadTask task = storage.getReference().child("profilePictures").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).putFile(Uri.parse(profilePicUri));
        // on failure, return profile with default profile picture path as profile picture
        task.addOnCompleteListener(taskSnapshot -> {
            if (taskSnapshot.isSuccessful()) {
                storage.getReference().child("profilePictures").child(profile.getUid()).getDownloadUrl().addOnCompleteListener(taskSnapshot1 -> {
                    if (taskSnapshot1.isSuccessful()) {
                        profile.setProfilePicture(taskSnapshot1.getResult().toString());
                        future.complete(profile);
                    } else {
                        profile.setProfilePicture(ProfileUtils.DEFAULT_PROFILE_PATH);
                        future.complete(profile);
                    }
                });
            } else {
                profile.setProfilePicture(ProfileUtils.DEFAULT_PROFILE_PATH);
                future.complete(profile);
            }
        });

        return future;
    }

}
