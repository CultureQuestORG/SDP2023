package ch.epfl.culturequest.authentication;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Intent;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultLauncher;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import ch.epfl.culturequest.NavigationActivity;
import ch.epfl.culturequest.ProfileCreatorActivity;
import ch.epfl.culturequest.SignUpActivity;
import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.social.Profile;

/**
 * A authenticator to sign in the app using google.
 *
 * To launch from an activity simply:
 * Instantiate an attribute with new Authenticator(this) and call sign in and sign out methods
 */
public class Authenticator implements AuthService {

    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user;
    private final ActivityResultLauncher<Intent> signInLauncher;
    private final ComponentActivity activity;
    private final boolean isAnonymous;

    /**
     * Authenticator for the login part of the app.
     * @param activity Activity from which we create an authenticator
     * @param isAnonymous This is used for testing. We login anonymously when testing so that
     *                    we dont need a user to physically login with google
     */
    public Authenticator(ComponentActivity activity, boolean isAnonymous) {
        this.activity = activity;
        this.user = mAuth.getCurrentUser();
        this.isAnonymous = isAnonymous;
        this.signInLauncher = isAnonymous ? null : activity.registerForActivityResult(new FirebaseAuthUIActivityResultContract(), this::onSignInResult);
    }


    /**
     * Launches the sign in intent for a user to sign in using Google
     */
    @Override
    public void signIn() {
        if (isAnonymous) {
            FirebaseAuth
                    .getInstance()
                    .signInAnonymously()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            user = mAuth.getCurrentUser();
                            redirectTo(ProfileCreatorActivity.class);
                        }
                    });
        } else if (user == null) {
            signInLauncher.launch(signInIntent());
        } else {
            System.out.println("User is already signed in");
            Database.getProfile(user.getUid()).handle((profile, throwable) -> {
                System.out.println("Profile is " + profile);
                if (profile != null) {
                    Profile.setActiveProfile(profile);
                    redirectTo(NavigationActivity.class);
                } else {
                    redirectTo(ProfileCreatorActivity.class);
                }
                return null;
            }).exceptionally(throwable -> {
                System.out.println("Error while getting profile");
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
    @Override
    public void signOut() {
        if (user != null) {
            if(isAnonymous){
                mAuth.signOut();
                redirectTo(SignUpActivity.class);
            }
            else {AuthUI.getInstance()
                    .signOut(activity)
                    .addOnCompleteListener(task -> redirectTo(SignUpActivity.class));
                Profile.setActiveProfile(null);
            }
        }
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
        List<AuthUI.IdpConfig> providers = List.of(
                new AuthUI.IdpConfig.GoogleBuilder().build());

        return AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build();
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
            user = mAuth.getCurrentUser();
            assert user != null;
            Database.getProfile(user.getUid()).handle((profile, throwable) -> {
                if (profile != null) {
                    Profile.setActiveProfile(profile);
                    redirectTo(NavigationActivity.class);
                } else {
                    Profile.setActiveProfile(new Profile("", null));
                    redirectTo(ProfileCreatorActivity.class);
                }
                return null;
            }).exceptionally(throwable -> {
                throwable.printStackTrace();
                return null;
            });
        } else {
            redirectTo(SignUpActivity.class);
        }
    }

    private void redirectTo(Class<? extends Activity> cls) {
        activity.startActivity(new Intent(activity, cls));
    }

    public FirebaseUser getUser() {
        return user;
    }
}
