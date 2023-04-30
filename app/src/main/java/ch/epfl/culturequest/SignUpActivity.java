package ch.epfl.culturequest;

import static ch.epfl.culturequest.utils.ProfileUtils.DEFAULT_PROFILE_PATH;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import ch.epfl.culturequest.authentication.Authenticator;
import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.social.Post;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.utils.AndroidUtils;
import ch.epfl.culturequest.utils.CheckInternetConnection;

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
            Activity t = this;
            CheckInternetConnection connection = new CheckInternetConnection() {
                @Override
                public void onSuccess() {
                    profile.handle((p, e) -> {
                        if (e != null || p == null) {
                            // If the user does not have a profile, display the profile creator activity
                            AndroidUtils.redirectToActivity(t, ProfileCreatorActivity.class);
                        } else {
                            // If the user has a profile, display the navigation activity
                            Profile.setActiveProfile(p);
                            AndroidUtils.redirectToActivity(t, NavigationActivity.class);
                        }
                        return null;
                    });
                }

                @Override
                public void onFailure() {
                    String uid = currentUser.getUid(), name = currentUser.getDisplayName(), email = currentUser.getEmail(), phoneNum = currentUser.getPhoneNumber();
                    Profile p = new Profile(uid, name, "", email, phoneNum, DEFAULT_PROFILE_PATH, -1);
                    Profile.setActiveProfile(p);
                }
            };
        }
        // If the user is logged in and has a profile, display the navigation activity
        AndroidUtils.redirectToActivity(this, NavigationActivity.class);
    }
}
