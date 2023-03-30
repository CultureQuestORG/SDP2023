package ch.epfl.culturequest.utils;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.MediaStore;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;

import ch.epfl.culturequest.social.Profile;


/**
 * Class that contains methods that are used in activities where a profile is created or modified
 *
 */
public class ProfileUtils {

    public static String DEFAULT_PROFILE_PATH = "https://firebasestorage.googleapis.com/v0/b/culturequest.appspot.com/o/profilePictures%2Fbasic_profile_picture.png?alt=media&token=8e407bd6-ad5f-401a-9b2d-7852ccfb9d62";

    private static Profile SELECTED_PROFILE = null;
    public static String INCORRECT_USERNAME_FORMAT = "Incorrect Username Format";
    public static String USERNAME_REGEX = "^[a-zA-Z0-9_-]+$";

    public static final String GALLERY_PERMISSION =
            //Version code R is android 11.
            Build.VERSION.SDK_INT > Build.VERSION_CODES.R ?
                    Manifest.permission.READ_MEDIA_IMAGES :
                    Manifest.permission.READ_EXTERNAL_STORAGE;


    /**
     * Checks if the username is valid
     *
     * @param username the username to check
     * @return true if the username is valid, false otherwise
     */
    public static boolean usernameIsValid(String username) {
        int length = username.length();
        return !username.isEmpty()
                && length > 3
                && length < 20
                && username.matches(USERNAME_REGEX)
                && !username.contains(" ");
    }


    /**
     * Checks if the username is valid and sets it to the profile if it is
     *
     * @param profile the profile to set the username to
     * @param username the username to check
     * @return true if the username is valid, false otherwise
     */
    public static boolean isValid(Profile profile, String username) {
        if (usernameIsValid(username)) {
            profile.setUsername(username);
            return true;
        }
        return false;
    }

    public static void setSelectedProfile(Profile profile){
        SELECTED_PROFILE = profile;
    }

    public static Profile getSelectedProfile(){
        return SELECTED_PROFILE;
    }


}
