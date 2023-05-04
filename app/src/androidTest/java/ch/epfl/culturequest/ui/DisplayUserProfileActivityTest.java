package ch.epfl.culturequest.ui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.ui.profile.DisplayUserProfileActivity;
import ch.epfl.culturequest.utils.ProfileUtils;

@RunWith(AndroidJUnit4.class)
public class DisplayUserProfileActivityTest {


    public static ActivityScenario<DisplayUserProfileActivity> scenario;
    @Before
    public void setUp() throws InterruptedException {

        // Set up the database to run on the local emulator of Firebase
        Database.setEmulatorOn();
        // clear the database before starting the following tests
        Database.clearDatabase();
        // Initialize the database with some test profiles
        Profile activeProfile = new Profile("currentUserUid", "currentUserName", "currentUserUsername", "currentUserEmail", "currentUserPhone", "currentUserProfilePicture", 400,new HashMap<>());
        Profile.setActiveProfile(activeProfile);

        Profile profile1 = new Profile("fakeuid", "name", "username", "email", "phone", "photo", 3,new HashMap<>());

        Database.setProfile(profile1);
        Database.setProfile(activeProfile);

        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), DisplayUserProfileActivity.class);
        intent.putExtra("uid", "fakeuid");
        scenario = ActivityScenario.launch(intent);

    }

    @Test
    public void textViewDisplaysPlace() {
        onView(withId(R.id.profilePlace)).check(matches(withText("Lausanne")));
    }

    @Test
    public void settingsNotDisplayed() {
        onView(withId(R.id.settingsButton)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.forViewVisibility(View.INVISIBLE))));
    }

    @Test
    public void textViewDisplaysFollowButton() {
        onView(withId(R.id.profileFollowText)).check(matches(withText("Follow")));
        onView(withId(R.id.profileFollowButton)).check(matches(isDisplayed()));
    }

    @Test
    public void textViewDisplaysUnfollowButton() throws InterruptedException {
        onView(withId(R.id.profileFollowText)).check(matches(withText("Follow")));
        onView(withId(R.id.profileFollowButton)).perform(click());
        onView(withId(R.id.profileFollowText)).check(matches(withText("Unfollow")));
    }

    @After
    public void tearDown() {
        Database.clearDatabase();
    }
}
