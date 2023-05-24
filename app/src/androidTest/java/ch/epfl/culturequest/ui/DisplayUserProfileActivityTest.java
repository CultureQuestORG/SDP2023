package ch.epfl.culturequest.ui;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;
import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.authentication.Authenticator;
import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.ui.profile.DisplayUserProfileActivity;

@RunWith(AndroidJUnit4.class)
public class DisplayUserProfileActivityTest {
    private final String email = "test@gmail.com";
    private final String password = "abcdefg";

    public static ActivityScenario<DisplayUserProfileActivity> scenario;

    @Before
    public void setUp() throws InterruptedException {

        // Set up the database to run on the local emulator of Firebase
        Database.setEmulatorOn();
        // clear the database before starting the following tests
        Database.clearDatabase();
        // Initialize the database with some test profiles

        //Set up the authentication to run on the local emulator of Firebase
        Authenticator.setEmulatorOn();

        // Signs up a test user used in all the tests
        Authenticator.manualSignUp(email, password).join();

        // Manually signs in the user before the tests
        Authenticator.manualSignIn(email, password).join();

        Profile activeProfile = new Profile("currentUserUid", "currentUserName", "currentUserUsername", "currentUserEmail", "currentUserPhone", "currentUserProfilePicture", 400, new HashMap<>(), new ArrayList<>());
        Profile.setActiveProfile(activeProfile);

        Profile profile1 = new Profile("fakeuid", "name", "username", "email", "phone", "photo", 3, new HashMap<>(), new ArrayList<>());

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
    public void textViewDisplaysUnfollowButton() {
        onView(withId(R.id.profileFollowText)).check(matches(withText("Follow")));
        onView(withId(R.id.profileFollowButton)).perform(click());
        onView(withId(R.id.profileFollowText)).check(matches(withText("Unfollow")));
    }

    @Test
    public void clickOnHomeButtonRedirectsToHome() {
        onView(withId(R.id.home_icon)).perform(click());
        onView(withId(R.id.homeFragment)).check(matches(isEnabled()));
    }

    @Test
    public void clickOnBackButtonWithoutIntentExtraRedirectsToLastActivity() {
        onView(withId(R.id.back_button)).perform(click());
    }

    @Test
    public void clickOnBackButtonWithIntentExtraRedirectsToHome() {
        Intent intent = new Intent(getApplicationContext(), DisplayUserProfileActivity.class);
        intent.putExtra("uid", "fakeuid");
        intent.putExtra("redirect", "home");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        scenario = ActivityScenario.launch(intent);
        onView(withId(R.id.back_button)).perform(click());
        onView(withId(R.id.homeFragment)).check(matches(isEnabled()));
    }

    @Test
    public void clickOnFollowButtonFollowsUser() throws InterruptedException {
        onView(withId(R.id.profileFollowButton)).perform(click());
        Thread.sleep(4000);
        onView(withId(R.id.profileFollowText)).check(matches(withText("Unfollow")));
    }

    @After
    public void tearDown() {
        Database.clearDatabase();
    }
}
