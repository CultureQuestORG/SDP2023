package ch.epfl.culturequest.social;

import com.google.firebase.auth.FirebaseAuthProvider;
import com.google.firebase.auth.FirebaseUser;

public interface User {
    String getDisplayName();
    String getFirstName();
    String getLastName();
    String getUid();
    String getEmail();
    String getPhoneNumber();
}