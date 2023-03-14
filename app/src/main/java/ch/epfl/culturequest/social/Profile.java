package ch.epfl.culturequest.social;

import android.net.Uri;

import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

/**
 * Creates a profile for users
 */
public class Profile {

    private String username;
    private Uri profilePicture;
    private FirebaseUser user;

    /**
     * Creates a Profile for a user.
     *
     * All Preconditions are checked in other classes. e.g., We create a profile
     * only when the user has correctly signed in and has correctly set they're username.
     * So user will normally never be null, and there is no need for sanitization of username as
     * it's already been dealt with
     *  @param username username of user
     * @param profilePicture Profile picture. Can be set to null
     */
    public Profile(@NotNull FirebaseUser user, String username, Uri profilePicture) {
        this.user = user;
        this.username = username;
        this.profilePicture = profilePicture;
    }

    public String getUid() {
        return user.getUid();
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return user.getDisplayName();
    }

    public String getEmail() {
        return user.getEmail();
    }

    public String getPhoneNumber() {
        return user.getPhoneNumber();
    }

    public Uri getProfilePicture() {
        return profilePicture;
    }

    public void updateUsername(String username){
        this.username = username;
    }

    public void updateProfilePicture(Uri profilePicture) {
        this.profilePicture = profilePicture;
    }

}