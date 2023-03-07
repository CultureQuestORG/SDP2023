package ch.epfl.culturequest.social;

import android.net.Uri;

import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class Profile implements User {

    private final FirebaseUser user;
    private final String uid, firstName, lastName, email, phoneNumber;
    private Uri profilePicture;

    public Profile(FirebaseUser user, Uri profilePicture) {
        this.user = user;
        this.uid = user.getUid();
        String[] fullName = Objects.requireNonNull(user.getDisplayName()).split(" ");
        this.firstName = fullName[0];
        this.lastName = fullName[1];
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
