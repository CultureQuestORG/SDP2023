package ch.epfl.culturequest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertNotNull;

import androidx.activity.ComponentActivity;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;

import ch.epfl.culturequest.authentication.Authenticator;
import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.utils.AndroidUtils;

@RunWith(AndroidJUnit4.class)
public class SignUpActivityTest {
    private ComponentActivity activity;
    private final String email = "test@gmail.com";
    private final String password = "abcdefg";

    static {
        BuildConfig.IS_TESTING.set(true);
    }
    @Before
    public void setup() {
        // Set up the database to run on the local emulator of Firebase
        Database.setEmulatorOn();

        // clear the database before starting the following tests
        Database.clearDatabase();

        //Set up the authentication to run on the local emulator of Firebase
        Authenticator.setEmulatorOn();

        // Signs up a test user used in all the tests
        Authenticator.manualSignUp(email, password).join();

        ActivityScenario<SignUpActivity> activityScenario = ActivityScenario.launch(SignUpActivity.class);
        activityScenario.onActivity(activity -> {
            this.activity = activity;
        });
    }

    @Test
    public void googleSignInButtonIsClickableForSignedOutUser() {
        // Sign out any Current User that is signed in to ensure that the Google Sign In button is clickable
        Authenticator.signOut(activity).join();
        onView(withId(R.id.sign_in_button)).check(matches(isEnabled()));
    }

    @Test
    public void signInTransitionsToNavActivityForSignInUserWithExistingProfile() throws InterruptedException {
        Authenticator.manualSignIn(email, password).join();
        assertNotNull(Authenticator.getCurrentUser());
        Profile profile = new Profile(Authenticator.getCurrentUser().getUid(), "test", "test", "test", "test", "test", 0,new HashMap<>(), new ArrayList<>());
        Database.setProfile(profile);
        Thread.sleep(2000);
        AndroidUtils.redirectToActivity(activity, SignUpActivity.class);
        Thread.sleep(2000);
        onView(withId(R.id.navigation_scan)).check(matches(isDisplayed()));
    }

    @After
    public void tearDown() {
        // clear the database after running the tests
        Database.clearDatabase();
    }
}
