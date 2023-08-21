package ch.epfl.culturequest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressBack;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static ch.epfl.culturequest.utils.ProfileUtils.DEFAULT_PROFILE_PIC_PATH;

import android.Manifest;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.culturequest.authentication.Authenticator;
import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.storage.FireStorage;
import ch.epfl.culturequest.ui.settings.UserSettingsActivity;

@RunWith(AndroidJUnit4.class)
public class UserSettingsActivityTest {
    @Rule
    public GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(Manifest.permission.READ_EXTERNAL_STORAGE);
//    @Rule
//    public GrantPermissionRule grantPermissionRule2 = GrantPermissionRule.grant(Manifest.permission.READ_MEDIA_IMAGES);

    private static UserSettingsActivity activity;
    private final String email = "test@gmail.com";
    private final String password = "abcdefg";

    @Before
    public void setup() throws InterruptedException {
        // Set up the database to run on the local emulator of Firebase
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

        // Manually signs in the user before the tests
        Authenticator.manualSignIn(email, password).join();

        Profile.setActiveProfile(new Profile("userName", DEFAULT_PROFILE_PIC_PATH));

        ActivityScenario.launch(UserSettingsActivity.class).onActivity(a -> activity = a);
        Intents.init();
    }

    @Test
    public void SettingsActivity() {
        onView(withId(R.id.username)).check(matches(withHint("Username")));
        onView(withId(R.id.username)).check(matches(withText("userName")));
        onView(withId(R.id.update_profile)).check(matches(withText("Update profile")));
        onView(withId(R.id.log_out)).check(matches(withText("Log out")));
    }


    @Test
    public void UpdateProfileButtonSendsBackToProfileFragmentWithCorrectUsername() {
        onView(withId(R.id.username)).perform(clearText()).perform(typeText("newUserName"));
        // remove the keyboard
        onView(withId(R.id.username)).perform(pressBack());

        onView(withId(R.id.update_profile)).perform(click());


        assertTrue(activity.isFinishing());


        assertEquals("newUserName", Profile.getActiveProfile().getUsername());
    }

//    @Test
//    public void profilePictureButtonSendsPickerIntent() {
//        onView(withId(R.id.profile_picture)).perform(click());
//
//        intended(hasAction(Intent.ACTION_PICK));
//    }

//    @Test
//    public void afterPictureChosenGoToCrop() {
//        Intent intent = new Intent();
//        intent.setData(Uri.parse("content://media/external/images/media/1"));
//        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(RESULT_OK, intent);
//        intending(hasAction(Intent.ACTION_PICK)).respondWith(result);
//
//        onView(withId(R.id.profile_picture)).perform(click());
//
//        intended(hasComponent(UCropActivity.class.getName()));
//    }

    @After
    public void tearDown() {
        Intents.release();

        // clear the database after each test
        Database.clearDatabase();

        // Clear the storage after the tests
        FireStorage.clearStorage();
    }


}

