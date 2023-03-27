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
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import static ch.epfl.culturequest.utils.ProfileUtils.DEFAULT_PROFILE_PATH;
import static ch.epfl.culturequest.utils.ProfileUtils.INCORRECT_USERNAME_FORMAT;

import android.Manifest;
import android.app.Instrumentation;
import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.database.MockDatabase;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.ui.profile.ProfileFragment;

@RunWith(AndroidJUnit4.class)
public class SettingsActivityTest {

    private static FirebaseUser user;


    @Rule
    public GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(Manifest.permission.READ_EXTERNAL_STORAGE);


    private static SettingsActivity activity;

    @BeforeClass
    public static void setup() throws InterruptedException {
        Database.init(new MockDatabase());
        FirebaseAuth.getInstance()
                .signInAnonymously()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        user = FirebaseAuth.getInstance().getCurrentUser();
                        Profile.setActiveProfile(new Profile( "userName", DEFAULT_PROFILE_PATH));
                    }
                });
        Thread.sleep(2000);

    }

    @Before
    public void init() {
        ActivityScenario
                .launch(SettingsActivity.class)
                .onActivity(a -> activity = a);
        Intents.init();
    }

    @After
    public  void release(){
        Intents.release();
    }

    @AfterClass
    public static void tearDown() {
        user.delete();
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


}

