package ch.epfl.culturequest.authentication;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultLauncher;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import ch.epfl.culturequest.NavigationActivity;
import ch.epfl.culturequest.ProfileCreatorActivity;
import ch.epfl.culturequest.SignUpActivity;
import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.utils.AndroidUtils;

/**
 * A authenticator to sign in the app using google.
 * <p>
 * To launch from an activity simply:
 * Instantiate an attribute with new Authenticator(this) and call sign in and sign out methods
 */
public class Authenticator {

    private static final FirebaseAuth authInstance = FirebaseAuth.getInstance();
    private FirebaseUser user;
    private final ActivityResultLauncher<Intent> signInLauncher;
    private final ComponentActivity activity;
    private final boolean isAnonymous;
    private static boolean isEmulatorOn = false;

    /**
     * Authenticator for the login part of the app.
     *
     * @param activity    Activity from which we create an authenticator
     * @param isAnonymous This is used for testing. We login anonymously when testing so that
     *                    we dont need a user to physically login with google
     */
    public Authenticator(ComponentActivity activity, boolean isAnonymous) {
        this.activity = activity;
        this.user = getCurrentUser();
        this.isAnonymous = isAnonymous;
        this.signInLauncher = isAnonymous ? null : activity.registerForActivityResult(new FirebaseAuthUIActivityResultContract(), this::onSignInResult);
    }

    /**
     * Sets the emulator on for testing purposes
     */
    public static void setEmulatorOn() {
        if (!isEmulatorOn) {
            authInstance.useEmulator("10.0.2.2", 9099);
            isEmulatorOn = true;
        }
    }

    public static CompletableFuture<AtomicBoolean> deleteCurrentUser() {
        CompletableFuture<AtomicBoolean> future = new CompletableFuture<>();
        getCurrentUser().delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                future.complete(new AtomicBoolean(true));
            } else {
                future.complete(new AtomicBoolean(false));
            }
        });

        return future;
    }

    /**
     * Launches the sign in intent for a user to sign in using Google
     */
    public void signIn() {
        if (isAnonymous) {
            authInstance.signInWithEmailAndPassword("test@gmail.com", "abcdefg")
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            user = getCurrentUser();
                            AndroidUtils.redirectToActivity(activity, ProfileCreatorActivity.class);
                        }
                    });
        } else if (user == null) {
            signInLauncher.launch(signInIntent());
        } else {
            Database.getProfile(user.getUid()).handle((profile, throwable) -> {
                if (profile != null) {
                    Profile.setActiveProfile(profile);
                    AndroidUtils.redirectToActivity(activity, NavigationActivity.class);
                } else {
                    AndroidUtils.redirectToActivity(activity, ProfileCreatorActivity.class);
                }
                return null;
            }).exceptionally(throwable -> {
                throwable.printStackTrace();
                return null;
            });
        }
    }

    /**
     * Signs the user out of the session
     * If no user is signed in then signing out does nothing
     * Upon completion, must redirect the user to the sign in page
     */
    public void signOut() {
        if (user == null) {
            return;
        }
        if (isAnonymous) {
            authInstance.signOut();
            AndroidUtils.redirectToActivity(activity, SignUpActivity.class);
            return;
        }

        // first sign out the user
        authInstance.signOut();
        // then sign out of firebase so that the user is not automatically signed in
        AuthUI.getInstance().signOut(activity).addOnCompleteListener(task -> {
            AndroidUtils.redirectToActivity(activity, SignUpActivity.class);
        });

        Profile.setActiveProfile(null);

    }

    /**
     * Creates Sign in Intent for Google Authentication
     * We could add more authentication types but for now we only
     * use google
     *
     * @return the Intent for Google Authentication
     */
    private Intent signInIntent() {
        //leave as a list of providers in case we want to add some later on
        List<AuthUI.IdpConfig> providers = List.of(new AuthUI.IdpConfig.GoogleBuilder().build());

        return AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build();
    }

    /**
     * Method linked to the success of the sign in attempt
     * Here we deal with the cases when user successfully signs in or
     * when there is an error.
     *
     * @param result the result of the authentication process
     */
    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        if (result.getResultCode() == RESULT_OK) {
            user = getCurrentUser();
            assert user != null;
            Database.getProfile(user.getUid()).handle((profile, throwable) -> {
                if (profile != null) {
                    Profile.setActiveProfile(profile);
                    AndroidUtils.redirectToActivity(activity, NavigationActivity.class);
                } else {
                    Profile.setActiveProfile(new Profile("", null));
                    AndroidUtils.redirectToActivity(activity, ProfileCreatorActivity.class);
                }
                return null;
            }).exceptionally(throwable -> {
                throwable.printStackTrace();
                return null;
            });
        } else {
            AndroidUtils.redirectToActivity(activity, SignUpActivity.class);
        }
    }

    public FirebaseUser getUser() {
        return user;
    }

    public static FirebaseUser getCurrentUser() {
        return authInstance.getCurrentUser();
    }
}
