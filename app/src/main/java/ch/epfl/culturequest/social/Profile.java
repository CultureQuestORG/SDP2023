package ch.epfl.culturequest.social;

import android.net.Uri;
import android.support.annotation.NonNull;

public class Profile {

    private final User user;
    private final String uid, firstName, lastName, email, phoneNumber;
    private Uri profilePicture;

    public Profile(User user, Uri profilePicture) {
        this.user = user;
        this.uid = user.getUid();
        String[] fullName = user.getFullName().split(" ");
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
