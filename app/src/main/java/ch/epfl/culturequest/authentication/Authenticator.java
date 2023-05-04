package ch.epfl.culturequest.authentication;

import static android.app.Activity.RESULT_OK;
import static ch.epfl.culturequest.utils.AndroidUtils.isNetworkAvailable;
import static ch.epfl.culturequest.utils.AndroidUtils.showNoConnectionAlert;

import android.content.Intent;
import android.util.Log;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultLauncher;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
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
    private static ActivityResultLauncher<Intent> signInLauncher;
    private static boolean isEmulatorOn = false;

    /**
     * Sets the emulator on for testing purposes
     */
    public static void setEmulatorOn() {
        if (!isEmulatorOn) {
            authInstance.useEmulator("10.0.2.2", 9099);
            isEmulatorOn = true;
        }
    }

    /**
     * sets the Firebase Ui launcher for the sign in intent linked to the parent activity
     *
     * @param activity
     */
    public static void setSignInLauncher(ComponentActivity activity) {
        signInLauncher = activity.registerForActivityResult(new FirebaseAuthUIActivityResultContract(), result -> {
            if (result.getResultCode() == RESULT_OK && getCurrentUser() != null) {
                signIn(activity);
            } else {
                AndroidUtils.redirectToActivity(activity, SignUpActivity.class);
            }
        });
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
     * Automatically Sign Up and In the user to the app by using a tier party service with the Firebase UI
     */
    public static CompletableFuture<String> signIn(ComponentActivity activity) {
        CompletableFuture<String> future = new CompletableFuture<>();
        if (getCurrentUser() == null) {
            if (isNetworkAvailable()) {
                signInLauncher.launch(signInIntent());
                future.complete("User signed in after being signed out with the Firebase UI");
            } else showNoConnectionAlert(activity, "Please try to login again later");
        } else {
                Database.getProfile(getCurrentUser().getUid()).handle((profile, throwable) -> {
                    if (profile != null) {
                        Profile.setActiveProfile(profile);
                        AndroidUtils.redirectToActivity(activity, NavigationActivity.class);
                        future.complete("User signed in with an existing profile");
                    } else {
                        AndroidUtils.redirectToActivity(activity, ProfileCreatorActivity.class);
                        future.complete("User signed in with no existing profile");
                    }
                    return null;
                }).exceptionally(throwable -> {
                    // If an error occurs, sign out the user
                    signOut(activity);
                    future.completeExceptionally(throwable);
                    return null;
                });
        }
        return future;
    }

    /**
     * Signs up (creates) new user to the app manually by using the email and password
     */
    public static CompletableFuture<AtomicBoolean> manualSignUp(String email, String password) {
        CompletableFuture<AtomicBoolean> future = new CompletableFuture<>();
        authInstance.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        future.complete(new AtomicBoolean(true));
                    } else {
                        future.complete(new AtomicBoolean(false));
                    }
                });
        return future;
    }

    /**
     * Signs in the user to the app manually by using the email and password
     */
    public static CompletableFuture<AtomicBoolean> manualSignIn(String email, String password) {
        CompletableFuture<AtomicBoolean> future = new CompletableFuture<>();
        authInstance.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        future.complete(new AtomicBoolean(true));
                    } else {
                        future.complete(new AtomicBoolean(false));
                    }
                });
        return future;
    }

    /**
     * Signs out the user from the app
     * If no user is signed in then signing out does nothing
     * Upon completion, must redirect the user to the sign in page
     *
     * @return a future that completes when the user is signed out
     */
    public static CompletableFuture<AtomicBoolean> signOut(ComponentActivity activity) {
        CompletableFuture<AtomicBoolean> future = new CompletableFuture<>();

        if (getCurrentUser() == null) {
            future.complete(new AtomicBoolean(false));
        } else {
            // first sign out the user
            authInstance.signOut();
            // then sign out of firebase so that the user is not automatically signed in
            AuthUI.getInstance().signOut(activity).addOnCompleteListener(task -> {
                AndroidUtils.redirectToActivity(activity, SignUpActivity.class);
                Profile.setActiveProfile(null);
                future.complete(new AtomicBoolean(true));
            });
        }

        return future;
    }

    /**
     * Creates Sign in Intent for Google Authentication
     * We could add more authentication types but for now we only
     * use google
     *
     * @return the Intent for Google Authentication
     */
    private static Intent signInIntent() {
        //leave as a list of providers in case we want to add some later on
        List<AuthUI.IdpConfig> providers = List.of(new AuthUI.IdpConfig.GoogleBuilder().build());

        return AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build();
    }

    /**
     * @return the current user signed in
     */
    public static FirebaseUser getCurrentUser() {
        return authInstance.getCurrentUser();
    }
}
