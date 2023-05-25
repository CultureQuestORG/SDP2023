package ch.epfl.culturequest;


import static androidx.core.content.ContextCompat.startActivity;
import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.Manifest;
import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
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
import ch.epfl.culturequest.ui.profile.DisplayUserProfileActivity;

@RunWith(AndroidJUnit4.class)
public class NavigationActivityTest {

    private final String email = "test@gmail.com";
    private final String password = "abcdefg";

    public static ActivityScenario<DisplayUserProfileActivity> scenario;

    @Rule
    public GrantPermissionRule permissionCamera = GrantPermissionRule.grant(Manifest.permission.CAMERA);
    @Rule
    public GrantPermissionRule permissionMaps = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);

    @Before
    public void setUp() {
        // Set up the database to run on the local emulator of Firebase
        Database.setEmulatorOn();

        // clear the database before starting the following tests
        Database.clearDatabase();

        //Set up the authentication to run on the local emulator of Firebase
        Authenticator.setEmulatorOn();

        // Signs up a test user used in all the tests
        Authenticator.manualSignUp(email, password).join();

        // Manually signs in the user before the tests
        Authenticator.manualSignIn(email, password).join();

        // Set activeProfile to null in order to test the robustness of the app
        Profile.setActiveProfile(null);

        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), NavigationActivity.class);
        scenario = ActivityScenario.launch(intent);
    }

    @Test
    public void clickOnHomeMenuItemDisplaysHomeFragment() throws InterruptedException {
        onView(withId(R.id.navigation_home))
                .perform(click());
        Thread.sleep(2000);
        // check home fragment is displayed
        onView(withId(R.id.homeFragment)).check(matches(isEnabled()));
    }

    @Test
    public void clickOnLeaderboardMenuItemDisplaysLeaderboardFragment() throws InterruptedException {
        onView(withId(R.id.navigation_leaderboard))
                .perform(click());
        Thread.sleep(2000);
        // check leaderboard fragment is displayed
        onView(withId(R.id.leaderboardFragment)).check(matches(isEnabled()));
    }

    @Test
    public void clickOnMapMenuItemDisplaysMapFragment() throws InterruptedException {
        onView(withId(R.id.navigation_map))
                .perform(click());
        Thread.sleep(2000);
        // check map fragment is displayed
        onView(withId(R.id.map_fragment)).check(matches(isEnabled()));
    }

    @Test
    public void clickOnProfileMenuItemDisplaysProfileFragment() throws InterruptedException {
        onView(withId(R.id.navigation_profile))
                .perform(click());
        Thread.sleep(2000);
        // check profile fragment is displayed
        onView(withId(R.id.profileFragment)).check(matches(isEnabled()));
    }

    @Test
    public void clickOnScanMenuItemDisplaysScanFragment() throws InterruptedException {
        onView(withId(R.id.navigation_scan))
                .perform(click());
        Thread.sleep(2000);
        // check scan fragment is displayed
        onView(withId(R.id.scanFragment)).check(matches(isEnabled()));
    }

    @Test
    public void navigationActivityWithProfileExtraRedirectsToProfileFragment() {
        Intent intent = new Intent(getApplicationContext(), NavigationActivity.class);
        intent.putExtra("redirect", "profile");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        scenario = ActivityScenario.launch(intent);
        onView(withId(R.id.profileFragment)).check(matches(isEnabled()));
    }

    @Test
    public void navigationActivityWithHomeExtraRedirectsToHomeFragment() {
        Intent intent = new Intent(getApplicationContext(), NavigationActivity.class);
        intent.putExtra("redirect", "home");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        scenario = ActivityScenario.launch(intent);
        onView(withId(R.id.homeFragment)).check(matches(isEnabled()));
    }

    @Test
    public void clickOnSeveralMenuItemTriggersTheListener() throws InterruptedException {
        onView(withId(R.id.navigation_home)).perform(click());
        onView(withId(R.id.navigation_leaderboard)).perform(click());
        onView(withId(R.id.navigation_map)).perform(click());
        onView(withId(R.id.navigation_profile)).perform(click());
        onView(withId(R.id.navigation_scan)).perform(click());
        onView(withId(R.id.navigation_home)).perform(click());
        Thread.sleep(2000);
        // check scan fragment is displayed
        onView(withId(R.id.homeFragment)).check(matches(isEnabled()));
    }

    @After
    public void tearDown() {
        // clear the database after the tests
        Database.clearDatabase();
    }
}