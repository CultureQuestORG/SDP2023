package ch.epfl.culturequest.social;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseUser;

public class User {
    private final FirebaseUser user;
    private final String uid, fullName, email, phoneNumber;

    public User(FirebaseUser user)  {
        if (user == null) throw new NullPointerException("User is null");
        else if (!user.isEmailVerified())
            throw new IllegalStateException("User email is not verified");
        this.user = user;
        this.uid = user.getUid();
        this.fullName = user.getDisplayName();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public String getUid() {
        return uid;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
