package ch.epfl.culturequest.social;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.core.Is.is;

import android.content.ContentResolver;
import android.net.Uri;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Objects;

import ch.epfl.culturequest.MainActivity;
import ch.epfl.culturequest.R;

@RunWith(AndroidJUnit4.class)
public class ProfileTest {

    private Profile profile;
    private FirebaseUser user;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    String path = "res/drawable/logo_compact.png";
    private Uri defaultUri = Uri.parse("res/drawable/logo_compact.png");

    @Before
    public void setup() throws InterruptedException {
        FirebaseAuth
                .getInstance()
                .signInAnonymously()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) user = mAuth.getCurrentUser();
                });

        Thread.sleep(5000);
        if (user != null) {
            profile = new Profile(user, "joker", defaultUri);
        } else System.exit(0);
    }

    @Test
    public void profileHasCorrectUID() {
        assertThat(profile.getUid(), is(user.getUid()));
    }

    @Test
    public void profileHasCorrectUsername() {
        assertThat(profile.getUsername(), is("joker"));
    }

    @Test
    public void profileHasCorrectEmail() {
        assertThat(profile.getEmail(), is(user.getEmail()));
    }

    @Test
    public void profileHasCorrectPhoneNum() {
        assertThat(profile.getPhoneNumber(), is(user.getPhoneNumber()));
    }

    @Test
    public void profileUserIsCurrentUser() {
        assertThat(profile.getUser(), is(FirebaseAuth.getInstance().getCurrentUser()));
    }

    @Test
    public void profilePicIsCorrect() {
        assertThat(profile.getProfilePictureUrl(), is(defaultUri));
    }

    @Test
    public void updatingProfilePicWorks(){
        Uri newPic = Uri.parse("res/drawable/logo_plain.png");
        profile.updateProfilePicture(newPic);
        assertThat(profile.getProfilePictureUrl(), is(newPic));
    }
}
