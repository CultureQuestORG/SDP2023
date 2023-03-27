package ch.epfl.culturequest;

import static androidx.test.espresso.Espresso.onView;
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

import ch.epfl.culturequest.social.Profile;

@RunWith(AndroidJUnit4.class)
public class ProfileCreatorActivityTest {

    private static FirebaseUser user;


    @Rule
    public GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(Manifest.permission.READ_EXTERNAL_STORAGE);

    private static Profile profile;

    private static ProfileCreatorActivity activity;

    @BeforeClass
    public static void setup() throws InterruptedException {
        FirebaseAuth.getInstance()
                .signInWithEmailAndPassword("test@gmail.com", "abcdefg")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        user = FirebaseAuth.getInstance().getCurrentUser();
                    }
                });
        Thread.sleep(2000);
    }

    @Before
    public void init(){
        ActivityScenario
                .launch(ProfileCreatorActivity.class)
                .onActivity(a -> {
                    profile = a.getProfile();
                    activity=a;

                });
        Intents.init();
    }

    @After
    public void release(){
        Intents.release();
    }

    @AfterClass
    public static void tearDown() {
        user.delete();
    }

    @Test
    public void correctUsernameTransitionsToNavActivity() {
        Instrumentation.ActivityMonitor activityMonitor = getInstrumentation()
                .addMonitor(NavigationActivity.class.getName(), null, false);

        onView(withId(R.id.username)).perform(typeText("abcd"), pressBack());
        onView(withId(R.id.create_profile)).perform(click());

        NavigationActivity secondActivity = (NavigationActivity) activityMonitor
                .waitForActivityWithTimeout(5000);
        assertNotNull(secondActivity);

        Intent expectedIntent = new Intent(getInstrumentation().getTargetContext(), NavigationActivity.class);
        assertEquals(expectedIntent.getComponent(), secondActivity.getIntent().getComponent());
        ActivityScenario.launch(NavigationActivity.class).onActivity(NavigationActivity::onBackPressed);

    }

    @Test
    public void wrongUserNameDoesntChangeIntent() {
        Instrumentation.ActivityMonitor activityMonitor = getInstrumentation()
                .addMonitor(NavigationActivity.class.getName(), null, false);
        onView(withId(R.id.username)).perform(typeText("  !+ "));
        onView(withId(R.id.create_profile)).perform(pressBack()).perform(click());
        NavigationActivity secondActivity = (NavigationActivity) activityMonitor
                .waitForActivityWithTimeout(2000);
        assertNull(secondActivity);
    }

    @Test
    public void wrongUserNameSetsHintText() throws InterruptedException {
        onView(withId(R.id.username)).perform(typeText("  !+ "));
        onView(withId(R.id.create_profile)).perform(pressBack()).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.username)).check(matches(withHint(INCORRECT_USERNAME_FORMAT)));
    }

    @Test
    public void pressingBackDoesntChangeIntent() {
        ActivityScenario.launch(ProfileCreatorActivity.class).onActivity(ProfileCreatorActivity::onBackPressed);
        Intents.intended(hasComponent(ProfileCreatorActivity.class.getName()));
    }

    @Test
    public void wrongUsernamesFailProfileCreation() {
        onView(withId(R.id.username)).perform(typeText(""));
        onView(withId(R.id.create_profile)).perform(click());
        onView(withId(R.id.username)).check(matches(withHint(INCORRECT_USERNAME_FORMAT)));

        onView(withId(R.id.username)).perform(typeText("lol"), pressBack());
        onView(withId(R.id.create_profile)).perform(click());
        onView(withId(R.id.username)).check(matches(withHint(INCORRECT_USERNAME_FORMAT)));

        onView(withId(R.id.username)).perform(typeText("abcdefghijklmnopqrstuvxyz"), pressBack());
        onView(withId(R.id.create_profile)).perform(click());
        onView(withId(R.id.username)).check(matches(withHint(INCORRECT_USERNAME_FORMAT)));

        onView(withId(R.id.username)).perform(typeText("john Doe"), pressBack());
        onView(withId(R.id.create_profile)).perform(click());
        onView(withId(R.id.username)).check(matches(withHint(INCORRECT_USERNAME_FORMAT)));
    }


    @AfterClass
    public static void destroy() {
        Intents.release();
    }

}
