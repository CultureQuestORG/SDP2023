package ch.epfl.culturequest.ui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import ch.epfl.culturequest.BuildConfig;
import ch.epfl.culturequest.R;
import ch.epfl.culturequest.authentication.Authenticator;
import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.ui.leaderboard.LeaderboardFragment;

@RunWith(AndroidJUnit4.class)
public class LeaderboardFragmentTest {
    private LeaderboardFragment fragment;
    private final String email = "test@gmail.com";
    private final String password = "abcdefg";

    static {
        BuildConfig.IS_TESTING.set(true);
    }

    @Before
    public void setUp() throws InterruptedException {
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

        // Initialize the database with some test profiles
        Profile activeProfile = new Profile(Authenticator.getCurrentUser().getUid(), "currentUserName", "currentUserUsername", "currentUserEmail", "currentUserPhone", "currentUserProfilePicture", 400,new HashMap<>(), new ArrayList<>());
        Database.setProfile(activeProfile);

        Database.setProfile(new Profile("testUid2", "testName2", "testUsername2", "testEmail2", "testPhone2", "testProfilePicture2", 300,new HashMap<>(), new ArrayList<>()));
        Database.setProfile(new Profile("testUid3", "testName3", "testUsername3", "testEmail3", "testPhone3", "testProfilePicture3", 200,new HashMap<>(), new ArrayList<>()));
        Database.setProfile(new Profile("testUid4", "testName4", "testUsername4", "testEmail4", "testPhone4", "testProfilePicture4", 100,new HashMap<>(), new ArrayList<>()));

        Database.addFollow(Authenticator.getCurrentUser().getUid(), "testUid2");
        Database.addFollow(Authenticator.getCurrentUser().getUid(), "testUid3");

        // Launch the fragment with the current user's uid for testing
        ActivityScenario<FragmentActivity> activityScenario = ActivityScenario.launch(FragmentActivity.class);
        activityScenario.onActivity(activity -> {
            fragment = new LeaderboardFragment();
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(android.R.id.content, fragment);
            fragmentTransaction.commitNow();
        });

        Thread.sleep(5000);
    }

    @Test
    public void databaseContains4Profiles() {
        int numberOfProfiles = 0;
        try {
            numberOfProfiles = Database.getNumberOfProfiles().get(5, java.util.concurrent.TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            fail("Test failed because of an exception: " + e.getMessage());
        }
        assertTrue(numberOfProfiles >= 4);
    }

    @Test
    public void currentUserScoreDisplayedIs400() {
        onView(withId(R.id.current_user_score)).check(matches(withText("400")));
    }

    @Test
    public void currentUserUsernameDisplayedIsCurrentUserUsername() {
        onView(withId(R.id.current_username)).check(matches(withText("currentUserUsername")));
    }

    /*@Test
    public void globalRankingWorks() throws InterruptedException {
        onView(withId(R.id.globalLeaderboardButton)).perform(click());
        Thread.sleep(5000);
        //R.id.friends_recycler_view should be visible
        onView(withId(R.id.global_recycler_view)).check(matches(isEnabled()));
        //R.id.friends_recycler_view should have exactly 4 children
        onView(withId(R.id.global_recycler_view)).check(matches(hasChildCount(4)));

        //R.id.friends_recycler_view should not be visible
        onView(withId(R.id.friends_recycler_view)).check(matches(not(isDisplayed())));

        // should be first among my friends
        onView(withId(R.id.current_user_rank)).check(matches(withText("1")));
    }*/

    @Test
    public void friendlyRankingWorks() throws InterruptedException {
        onView(withId(R.id.friendsLeaderboardButton)).perform(click());
        Thread.sleep(5000);
        //R.id.friends_recycler_view should be visible
        onView(withId(R.id.friends_recycler_view)).check(matches(isEnabled()));
        //R.id.friends_recycler_view should have exactly 3 children
        onView(withId(R.id.friends_recycler_view)).check(matches(hasChildCount(3)));

        //R.id.recycler_view should not be visible
        onView(withId(R.id.global_recycler_view)).check(matches(not(isDisplayed())));

        // should be first among my friends
        onView(withId(R.id.current_user_rank)).check(matches(withText("1")));
    }

    @After
    public void tearDown() {
        // clear the database after finishing the tests
        Database.clearDatabase();
    }
}