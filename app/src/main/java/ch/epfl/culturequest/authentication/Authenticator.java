package ch.epfl.culturequest.authentication;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultLauncher;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.firebase.ui.auth.data.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import ch.epfl.culturequest.MainActivity;
import ch.epfl.culturequest.NavigationActivity;
import ch.epfl.culturequest.ProfileCreatorActivity;
import ch.epfl.culturequest.SignUpActivity;

/**
 * A authenticator to sign in the app using google.
 *
 * To launch from an activity simply:
 * Instantiate an attribute with new Authenticator(this) and call sign in and sign out methods
 */
public class Authenticator implements AuthService {

    private FirebaseUser user;
    private final ActivityResultLauncher<Intent> signInLauncher;
    private final ComponentActivity activity;

    public Authenticator(ComponentActivity activity) {
        this.activity = activity;
        this.user = FirebaseAuth.getInstance().getCurrentUser();
        this.signInLauncher = activity.registerForActivityResult(new FirebaseAuthUIActivityResultContract(), this::onSignInResult);
    }

    /**
     * Launches the sign in intent for a user to sign in using Google
     */
    @Override
    public void signIn() {
        if (user == null) {
            signInLauncher.launch(signInIntent());
        } else {
            redirectToHomePage();
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
            AuthUI.getInstance()
                    .signOut(activity)
                    .addOnCompleteListener(task -> {
                        ///TODO return to the homepage to sign in
                        redirectToSignInPage();
                    });
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
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            user = FirebaseAuth.getInstance().getCurrentUser();
            Intent profileCreation = new Intent(activity, ProfileCreatorActivity.class);
            activity.startActivity(profileCreation);
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.

            //in case of failure when signing in, we will just
            // redirect back to the main page so they can attempt to sign in again
            redirectToSignInPage();
        }
    }

    private void redirectToSignInPage() {
        //modify later
        Intent intent = new Intent(activity, SignUpActivity.class);
        activity.startActivity(intent);
    }

    private void redirectToHomePage() {
        Intent intent = new Intent(activity, NavigationActivity.class);
        activity.startActivity(intent);
    }

    public FirebaseUser getUser() {
        return user;
    }
}
