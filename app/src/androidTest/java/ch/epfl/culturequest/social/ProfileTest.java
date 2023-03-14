package ch.epfl.culturequest.social;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.core.Is.is;

import android.net.Uri;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ProfileTest {

    private Profile profile;
    private static FirebaseUser user;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private Uri defaultUri = Uri.parse("res/drawable/logo_compact.png");

    @Before
    public void setup() throws InterruptedException {
        mAuth
                .signInAnonymously()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        user = mAuth.getCurrentUser();
                        profile = new Profile(user,"joker", defaultUri);
                    }
                });
        Thread.sleep(1000);
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
    public void nameIsTheSame() {
        assertThat(profile.getName(), is(user.getDisplayName()));
    }

    @Test
    public void profileHasCorrectPhoneNum() {
        assertThat(profile.getPhoneNumber(), is(user.getPhoneNumber()));
    }

    @Test
    public void profilePicIsCorrect() {
        assertThat(profile.getProfilePicture(), is(defaultUri));
    }

    @Test
    public void updatingProfilePicWorks() {
        Uri newPic = Uri.parse("res/drawable/logo_plain.png");
        profile.updateProfilePicture(newPic);
        assertThat(profile.getProfilePicture(), is(newPic));
    }

    @AfterClass
    public static void destroyUser(){
        if(user != null){
            user.delete();
        }
    }
}
