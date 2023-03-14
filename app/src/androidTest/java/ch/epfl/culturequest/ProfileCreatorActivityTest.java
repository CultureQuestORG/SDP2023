package ch.epfl.culturequest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressBack;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import android.Manifest;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;

import android.app.Instrumentation.ActivityResult;
import android.widget.ImageView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.function.Function;

import ch.epfl.culturequest.social.Profile;

@RunWith(AndroidJUnit4.class)
public class ProfileCreatorActivityTest {

    private static FirebaseUser user;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();


    @Rule
    public GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(Manifest.permission.READ_EXTERNAL_STORAGE);

    private Profile profile;
    private ImageView profilePic;

    @Before
    public void setup() throws InterruptedException {
        mAuth
                .signInAnonymously()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        user = mAuth.getCurrentUser();
                    }
                });
        Thread.sleep(2000);
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ProfileCreatorActivity.class);
        ActivityScenario
                .launch(ProfileCreatorActivity.class)
                .onActivity(activity -> {
                    profile = activity.getProfile();
                    profilePic = activity.findViewById(R.id.profile_picture);
                });
        Intents.init();
    }

    @Test
    public void correctUsernameTransitionsToNavActivity() {
        Instrumentation.ActivityMonitor activityMonitor = getInstrumentation()
                .addMonitor(NavigationActivity.class.getName(), null, false);

        onView(withId(R.id.username)).perform(typeText("lucamouchel"), pressBack());
        onView(withId(R.id.create_profile)).perform(click());

        NavigationActivity secondActivity = (NavigationActivity) activityMonitor
                .waitForActivityWithTimeout(5000);
        assertNotNull(secondActivity);

        Intent expectedIntent = new Intent(getInstrumentation().getTargetContext(), NavigationActivity.class);
        assertEquals(expectedIntent.getComponent(), secondActivity.getIntent().getComponent());
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
        onView(withId(R.id.username)).check(matches(withHint(ProfileCreatorActivity.INCORRECT_USERNAME_FORMAT)));
    }

    @Test
    public void pressingBackDoesntChangeIntent() {
        ActivityScenario.launch(ProfileCreatorActivity.class).onActivity(ProfileCreatorActivity::onBackPressed);
        Intents.intended(hasComponent(ProfileCreatorActivity.class.getName()));
    }

    @Test
    public void notSelectingPicGivesDefaultProfilePicAndCorrectUsername() throws InterruptedException {
        onView(withId(R.id.username)).perform(typeText("JohnDoe"));
        onView(withId(R.id.create_profile)).perform(pressBack()).perform(click());
        Thread.sleep(2000);
        assertEquals(profile.getUsername(), "JohnDoe");
        assertEquals(profile.getProfilePicture(), Uri.parse(ProfileCreatorActivity.DEFAULT_PROFILE_PATH));
    }

    @Test
    public void selectingImageDisplaysImage() throws InterruptedException {
        ActivityResult result = new ActivityResult(Activity.RESULT_OK, null);
        intending(hasAction(Intent.ACTION_PICK)).respondWith(result);
        onView(withId(R.id.profile_picture)).perform(click());
        intended(hasAction(Intent.ACTION_PICK));
        assertNotNull(result);
    }

    @Test
    public void wrongUsernamesFailProfileCreation(){
        writeUsernameAndClickCreate("");
        onView(withId(R.id.username)).check(matches(withHint(ProfileCreatorActivity.INCORRECT_USERNAME_FORMAT)));

        writeUsernameAndClickCreate("lol");
        onView(withId(R.id.username)).check(matches(withHint(ProfileCreatorActivity.INCORRECT_USERNAME_FORMAT)));

        writeUsernameAndClickCreate("abcdefghijklmnopqrstuvxyz");
        onView(withId(R.id.username)).check(matches(withHint(ProfileCreatorActivity.INCORRECT_USERNAME_FORMAT)));

        writeUsernameAndClickCreate("john doe");
        onView(withId(R.id.username)).check(matches(withHint(ProfileCreatorActivity.INCORRECT_USERNAME_FORMAT)));
    }
    private void writeUsernameAndClickCreate(String toWrite){
        onView(withId(R.id.username)).perform(typeText(toWrite), pressBack());
        onView(withId(R.id.create_profile)).perform(click());
    }

    @After
    public void release(){
        Intents.release();
    }


    @AfterClass
    public static void destroy() {
        user.delete();
    }

}
