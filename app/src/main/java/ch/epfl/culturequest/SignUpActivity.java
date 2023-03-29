package ch.epfl.culturequest;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.mockito.internal.matchers.And;

import java.util.concurrent.CompletableFuture;

import ch.epfl.culturequest.authentication.Authenticator;
import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.utils.AndroidUtils;

public class SignUpActivity extends AppCompatActivity {
    private final Authenticator auth = new Authenticator(this, false);
    private final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // If the user is not logged in, display the sign in activity
        AndroidUtils.removeStatusBar(getWindow());
        if (currentUser == null) {
            setContentView(R.layout.activity_signin);
            findViewById(R.id.sign_in_button).setOnClickListener(k -> auth.signIn());
            return;
        }
        // If the user is logged in, check if he has a profile
        if (Profile.getActiveProfile() == null) {
            CompletableFuture<Profile> profile = Database.getProfile(currentUser.getUid());
            profile.handle((p, e) -> {
                if (e != null || p == null) {
                    // If the user does not have a profile, display the profile creator activity
                    AndroidUtils.redirectToActivity(this, ProfileCreatorActivity.class);
                } else {
                    // If the user has a profile, display the navigation activity
                    Profile.setActiveProfile(p);
                    AndroidUtils.redirectToActivity(this, NavigationActivity.class);
                }
                return null;
            }

            );
            return;
        }
        // If the user is logged in and has a profile, display the navigation activity
        AndroidUtils.redirectToActivity(this, NavigationActivity.class);

    }
}
