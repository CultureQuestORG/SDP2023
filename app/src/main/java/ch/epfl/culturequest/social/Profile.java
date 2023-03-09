package ch.epfl.culturequest.social;

import android.net.Uri;

import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

/**
 * Creates a profile for users
 */
public class Profile {

    private final FirebaseUser user;
    private final String uid, name, username, email, phoneNumber;
    private Uri profilePicture;

    /**
     * Creates a Profile for a user.
     *
     * All Preconditions are checked in other classes. e.g., We create a profile
     * only when the user has correctly signed in and has correctly set they're username.
     * So user will normally never be null, and there is no need for sanitization of username as
     * it's already been dealt with
     *
     * @param user current user signed in
     * @param username username of user
     * @param profilePicture Profile picture. Can be set to null
     */
    public Profile(FirebaseUser user, String username, Uri profilePicture) {
        this.user = Objects.requireNonNull(user);
        this.username = username;
        this.uid = user.getUid();
        this.name = user.getDisplayName();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.profilePicture = profilePicture;
    }

    public FirebaseUser getUser() {
        return user;
    }

    public String getUid() {
        return uid;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Uri getProfilePictureUrl() {
        return profilePicture;
    }

    public void updateProfilePicture(Uri profilePicture) {
        this.profilePicture = profilePicture;
    }

}