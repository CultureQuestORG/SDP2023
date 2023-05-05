package ch.epfl.culturequest.authentication;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import androidx.activity.ComponentActivity;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.SignUpActivity;
import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.social.Profile;

@RunWith(AndroidJUnit4.class)
public class AuthenticatorTest {
    private ComponentActivity activity;
    private final String email = "test@gmail.com";
    private final String password = "abcdefg";

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

        // Manually signs in the user before the tests in order to test the automatic redirection
        Authenticator.manualSignIn(email, password).join();

        ActivityScenario<SignUpActivity> activityScenario = ActivityScenario.launch(SignUpActivity.class);
        activityScenario.onActivity(activity -> this.activity = activity);
    }

    @Test
    public void SignInWithNoExistingProfileRedirectsToProfileCreatorActivity() {
        try {
            String signInState = Authenticator.signIn(activity).get(5, TimeUnit.SECONDS);
            assertEquals(signInState, "User signed in with no existing profile");
            onView(withId(R.id.create_profile)).check(matches(isDisplayed()));
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            fail("Test failed because of an exception: " + e.getMessage());
        }
    }

    @Test
    public void SignInWithExistingProfileRedirectsToNavigationActivity() {
        Profile profile = new Profile(Authenticator.getCurrentUser().getUid(), "test", "test", "test", "test", "test", 0,new HashMap<>());

        try {
            Database.setProfile(profile);
            Thread.sleep(2000);
            String signInState = Authenticator.signIn(activity).get(5, TimeUnit.SECONDS);
            assertEquals(signInState, "User signed in with an existing profile");
            onView(withId(R.id.navigation_scan)).check(matches(isDisplayed()));
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            fail("Test failed because of an exception: " + e.getMessage());
        }

        Database.clearDatabase();
    }

    @Test
    public void SignInAfterBeingSignedOutLaunchesTheFirebaseSignInUI() {
        try {
            assertTrue(Authenticator.signOut(activity).get(5, TimeUnit.SECONDS).get());
            assertNull(Authenticator.getCurrentUser());
            Thread.sleep(3000);
            onView(withId(R.id.sign_in_button)).check(matches(isDisplayed()));
            String signInState = Authenticator.signIn(activity).get(5, TimeUnit.SECONDS);
            assertEquals(signInState, "User signed in after being signed out with the Firebase UI");
            // Signs in the user again for the other tests
            assertTrue(Authenticator.manualSignIn(email, password).get(5, TimeUnit.SECONDS).get());
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            fail("Test failed because of an exception: " + e.getMessage());
        }
    }

    @Test
    public void SignOutWithUserSingedInRedirectsToSignInActivity() {
        try {
            assertTrue(Authenticator.signOut(activity).get(5, TimeUnit.SECONDS).get());
            assertNull(Authenticator.getCurrentUser());
            Thread.sleep(3000);
            onView(withId(R.id.sign_in_button)).check(matches(isDisplayed()));
            // Signs in the user again for the other tests
            assertTrue(Authenticator.manualSignIn(email, password).get(5, TimeUnit.SECONDS).get());
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            fail("Test failed because of an exception: " + e.getMessage());
        }
    }

    @Test
    public void signOutWithNoUserSignedInDoesNothing() {
        try {
            assertTrue(Authenticator.signOut(activity).get(5, TimeUnit.SECONDS).get());
            assertFalse(Authenticator.signOut(activity).get(5, TimeUnit.SECONDS).get());

            // Signs in the user again for the other tests
            assertTrue(Authenticator.manualSignIn(email, password).get(5, TimeUnit.SECONDS).get());
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            fail("Test failed because of an exception: " + e.getMessage());
        }
    }

    @Test
    public void deleteCurrentUserDeletesTheCurrentUser() {
        try {
            assertNotNull(Authenticator.getCurrentUser());
            assertTrue(Authenticator.deleteCurrentUser().get(5, TimeUnit.SECONDS).get());
            assertNull(Authenticator.getCurrentUser());

            // Signs up the user again for the other tests
            assertTrue(Authenticator.manualSignUp(email, password).get(5, TimeUnit.SECONDS).get());
            // Signs in the user again for the other tests
            assertTrue(Authenticator.manualSignIn(email, password).get(5, TimeUnit.SECONDS).get());
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            fail("Test failed because of an exception: " + e.getMessage());
        }
    }


    @After
    public void tearDown() {
        // clear the database after running the tests
        Database.clearDatabase();
    }
}
