package ch.epfl.culturequest.ui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.view.View;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.ui.profile.DisplayUserProfileActivity;
import ch.epfl.culturequest.utils.ProfileUtils;

@RunWith(AndroidJUnit4.class)
public class DisplayUserProfileActivityTest {

    @Rule
    public ActivityScenarioRule<DisplayUserProfileActivity> testRule = new ActivityScenarioRule<>(DisplayUserProfileActivity.class);

    @BeforeClass
    public static void setUp() {
        // Set up the database to run on the local emulator of Firebase
        Database.setEmulatorOn();

        // clear the database before starting the following tests
        Database.clearDatabase();

        // Initialize the database with some test profiles
        Profile activeProfile = new Profile("currentUserUid", "currentUserName", "currentUserUsername", "currentUserEmail", "currentUserPhone", "currentUserProfilePicture", 400);
        Profile.setActiveProfile(activeProfile);

        Profile profile1 = new Profile("uid", "name", "username", "email", "phone", "photo", 3);
        ProfileUtils.setSelectedProfile(profile1);

        Database.setProfile(profile1);
        Database.setProfile(activeProfile);
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
