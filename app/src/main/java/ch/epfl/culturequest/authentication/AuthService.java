package ch.epfl.culturequest.authentication;

/**
 * Interface used for authenticating users
 */
public interface AuthService {
    /**
     * Launches the sign in intent for a user to sign in using Google
     */
    void signIn();

    /**
     * Signs the user out of the session
     * If no user is signed in then signing out does nothing
     * Upon completion, must redirect the user to the sign in page
     */
    void signOut();
}

