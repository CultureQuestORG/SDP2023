package ch.epfl.culturequest.social;

import android.net.Uri;

import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

/**
 * Creates a profile for users
 */
public class Profile {


    private String uid, name, username, email, phoneNumber;
    private String profilePicture;


    public Profile(FirebaseUser user, String username, String profilePicture) {
        this.username = username;
        this.uid = user.getUid();
        this.name = user.getDisplayName();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.profilePicture = profilePicture;
    }


    /**
     * Creates a profile with no information
     * This is useful for firebase who needs a default constructor
     * and sets the values using setters
     */
    public Profile() {
        this.uid = "";
        this.name = "";
        this.username = "";
        this.email = "";
        this.phoneNumber = "";
        this.profilePicture = null;
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

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }



    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }




}