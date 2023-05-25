package ch.epfl.culturequest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.isNotClickable;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertNotNull;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.ComponentActivity;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import ch.epfl.culturequest.authentication.Authenticator;
import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.storage.FireStorage;
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
    public void setup() throws InterruptedException {
        Database.setEmulatorOn();

        // clear the database before starting the following tests
        Database.clearDatabase();

        // Set up the online storage to run on the local emulator of Firebase
        FireStorage.setEmulatorOn();

        // Clear the storage after the tests
        FireStorage.clearStorage();

        //Set up the authentication to run on the local emulator of Firebase
        Authenticator.setEmulatorOn();

        // Signs up a test user used in all the tests
        Authenticator.manualSignUp(email, password).join();
        Thread.sleep(3000);
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

    ///////////////////////Manual sign in tests///////////////////////
    @Test
    public void signUpSetsIssuesWhenIncorrectPW() throws InterruptedException {
        Authenticator.signOut(activity).join();

        onView(withId(R.id.editTextTextEmailAddress)).perform(replaceText("test@gmail.com")).perform(closeSoftKeyboard());

        onView(withId(R.id.editTextTextPassword)).perform(replaceText("abcd"));
        onView(withId(R.id.sign_up_manually)).perform(click());
        onView(withId(R.id.issues)).check(matches(withText("Password must contain at least 1 digit")));


        onView(withId(R.id.editTextTextPassword)).perform(replaceText("abcd1"));
        onView(withId(R.id.sign_up_manually)).perform(click());
        onView(withId(R.id.issues)).check(matches(withText("Password must contain at least 1 special character")));

        onView(withId(R.id.editTextTextPassword)).perform(replaceText("abcd1!"));
        onView(withId(R.id.sign_up_manually)).perform(click());
        onView(withId(R.id.issues)).check(matches(withText("Password should be minimum 8 characters")));
    }

    @Test
    public void signingUpEmailIsSuccessful() {
        onView(withId(R.id.editTextTextEmailAddress)).perform(replaceText(UUID.randomUUID().toString().substring(0,7) + "@gmail.com"));
        onView(withId(R.id.editTextTextPassword)).perform(replaceText("abcdefg1!"));
        onView(withId(R.id.sign_up_manually)).check(matches(isEnabled())).perform(click());
    }

    @Test
    public void tryingToSignUpWithSameEmailSetsIssue(){
        onView(withId(R.id.editTextTextEmailAddress)).perform(replaceText("test@gmail.com"));
        onView(withId(R.id.editTextTextPassword)).perform(replaceText("abcdefg1!"));
        onView(withId(R.id.sign_up_manually)).perform(click());
        onView(withId(R.id.issues)).check(matches(withText("This email is already used")));
    }

    @Test
    public void signingInWithCorrectEmailWorks() {
        Authenticator.manualSignUp("testx@gmail.com", "password1!").join();
        onView(withId(R.id.editTextTextEmailAddress)).perform(replaceText("testx@gmail.com"));
        onView(withId(R.id.editTextTextPassword)).perform(replaceText("password1!"));
        onView(withId(R.id.sign_in_manually)).perform(click());
    }

    @Test
    public void signingInWithIncorrectCredentialsSetsIssue() {
        Authenticator.manualSignUp("testx@gmail.com", "password1!").join();
        onView(withId(R.id.editTextTextEmailAddress)).perform(replaceText("testx@gmail.com"));
        onView(withId(R.id.editTextTextPassword)).perform(replaceText("wrongpassword1!"));
        onView(withId(R.id.sign_in_manually)).perform(click());
        onView(withId(R.id.issues)).check(matches(withText("Wrong sign in credentials")));
    }

    @Test
    public void settingRightCredentialsAndUndoingPreventsLogin() {
        onView(withId(R.id.editTextTextEmailAddress)).perform(replaceText("testx@gmail.com"));
        onView(withId(R.id.editTextTextPassword)).perform(replaceText("wrongpassword1!"));
        onView(withId(R.id.sign_in_manually)).check(matches(isClickable()));
        onView(withId(R.id.editTextTextPassword)).perform(replaceText("shfuwrew"));
        onView(withId(R.id.sign_in_manually)).check(matches(isNotClickable()));
    }



    @After
    public void tearDown() {
        // clear the database after running the tests
        Database.clearDatabase();
    }
}
