package ch.epfl.culturequest.social;

import android.net.Uri;

import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

/**
 * Creates a profile for users
 */
public class Profile implements User {

    private final User user;
    private final String uid, fullName,firstName, lastName, email, phoneNumber;
    private Uri profilePicture;

    public Profile(User user, Uri profilePicture) {
        this.user = user;
        this.uid = user.getUid();
        String[] fullName = Objects.requireNonNull(user.getDisplayName()).split(" ");
        this.fullName = user.getDisplayName();
        this.firstName = fullName[0];
        this.lastName = fullName[1];
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.profilePicture = profilePicture;
    }


    public User getUser() {
        return user;
    }

    public String getUid() {
        return uid;
    }

    @Override
    public String getDisplayName() {
        return fullName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
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