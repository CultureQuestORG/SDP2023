package ch.epfl.culturequest.storage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.firebase.FirebaseException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import ch.epfl.culturequest.authentication.Authenticator;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.utils.ProfileUtils;

/**
 * Class that handles the storage of images in Firebase Storage.
 */
public class FireStorage {
    private static final FirebaseStorage storage = FirebaseStorage.getInstance();
    private static boolean isEmulatorOn = false;

    /**
     * Sets the Firebase storage emulator on for the tests.
     */
    public static void setEmulatorOn() {
        if (!isEmulatorOn) {
            storage.useEmulator("10.0.2.2", 9199);
            isEmulatorOn = true;
        }
    }

    /**
     * Clears the storage of all images.
     */
    public static void clearStorage() {
        storage.getReference().child("images").listAll().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (StorageReference prefixes : Objects.requireNonNull(task.getResult()).getPrefixes()) {
                    prefixes.listAll().addOnCompleteListener(task1 -> {
                        for (StorageReference item : Objects.requireNonNull(task1.getResult()).getItems()) {
                            item.delete();
                        }
                    });
                }
            }
        });

        storage.getReference().child("profilePictures").listAll().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (StorageReference item : Objects.requireNonNull(task.getResult()).getItems()) {
                    item.delete();
                }
            }
        });

    }

    /**
     * Uploads to the storage the profile picture of the user and returns the profile updated.
     *
     * @param profile     the profile of the user
     * @param bitmapImage the bitmap image of the profile picture
     * @return a completable future with the profile updated
     */
    public static CompletableFuture<Profile> uploadNewProfilePictureToStorage(Profile profile, Bitmap bitmapImage) {
        CompletableFuture<Profile> future = new CompletableFuture<>();
        StorageReference imageRef = storage.getReference().child("profilePictures/" + profile.getUid());
        //upload image to firebase storage
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmapImage.compress(Bitmap.CompressFormat.WEBP, 50 /*ignored for PNG*/, bos);
        byte[] bitmapdata = bos.toByteArray();
        ByteArrayInputStream bs = new ByteArrayInputStream(bitmapdata);

        // on failure, return profile with default profile picture path as profile picture
        imageRef.putStream(bs).addOnCompleteListener(taskSnapshot -> {
            if (taskSnapshot.isSuccessful()) {
                imageRef.getDownloadUrl().addOnCompleteListener(taskSnapshot1 -> {
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

    /**
     * Uploads to the storage an image and returns the url of the image.
     *
     * @param bitmapImage the bitmap image of the question
     * @return a completable future with the url of the image
     */
    public static CompletableFuture<String> uploadAndGetUrlFromImage(Bitmap bitmapImage) {
        String path = "images/" + Authenticator.getCurrentUser().getUid() + "/" + UUID.randomUUID().toString();

        StorageReference imageRef = storage.getReference().child(path);

        CompletableFuture<String> f = new CompletableFuture<>();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmapImage.compress(Bitmap.CompressFormat.WEBP, 70 /*ignored for PNG*/, bos);
        byte[] bitmapdata = bos.toByteArray();
        ByteArrayInputStream bs = new ByteArrayInputStream(bitmapdata);

        imageRef.putStream(bs).addOnCompleteListener(taskSnapshot -> {
            if (taskSnapshot.isSuccessful()) {
                imageRef.getDownloadUrl().addOnCompleteListener(taskSnapshot1 -> {
                    if (taskSnapshot1.isSuccessful()) {
                        f.complete(taskSnapshot1.getResult().toString());
                    } else {
                        f.completeExceptionally(new FirebaseException("Could not get download url"));
                    }
                });
            } else {
                f.completeExceptionally(new FirebaseException("Could not upload image"));
            }
        });

        return f;
    }

    /**
     * Returns the bitmap image from the url. This method is useful to
     * access images stored in the storage.
     *
     * @param src the url of the image
     * @return the bitmap image
     */
    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
