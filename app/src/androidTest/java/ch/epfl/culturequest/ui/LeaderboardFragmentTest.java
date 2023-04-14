package ch.epfl.culturequest.ui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.database.FirebaseDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.database.FireDatabase;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.ui.leaderboard.LeaderboardFragment;
import ch.epfl.culturequest.utils.EspressoIdlingResource;

@RunWith(AndroidJUnit4.class)
public class LeaderboardFragmentTest {
    private LeaderboardFragment fragment;

    @Before
    public void setUp() {
        // Set up the database to run on the local emulator of Firebase
        Database.setEmulatorOn();

        // clear the database before starting the following tests
        Database.clearDatabase();


        // Initialize the database with some test profiles
        Database.setProfile(new Profile("currentUserUid", "currentUserName", "currentUserUsername", "currentUserEmail", "currentUserPhone", "currentUserProfilePicture", List.of(), 400));
        Database.setProfile(new Profile("testUid2", "testName2", "testUsername2", "testEmail2", "testPhone2", "testProfilePicture2", List.of(), 300));
        Database.setProfile(new Profile("testUid3", "testName3", "testUsername3", "testEmail3", "testPhone3", "testProfilePicture3", List.of(), 200));
        Database.setProfile(new Profile("testUid4", "testName4", "testUsername4", "testEmail4", "testPhone4", "testProfilePicture4", List.of(), 100));

        // Add EspressoIdlingResource to the IdlingRegistry to make sure tests wait for the fragment and database to be ready
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource);

        // Launch the fragment with the current user's uid for testing
        ActivityScenario<FragmentActivity> activityScenario = ActivityScenario.launch(FragmentActivity.class);
        activityScenario.onActivity(activity -> {
            fragment = LeaderboardFragment.newInstance("currentUserUid");
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(android.R.id.content, fragment);
            fragmentTransaction.commitNow();
        });
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

   /* @Test
    public void currentUserRankDisplayedIs1() {
        onView(withId(R.id.current_user_rank)).check(matches(withText("1")));
    }*/

    @After
    public void tearDown() {
        // remove EspressoIdlingResource from the IdlingRegistry
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource);
        // clear the database after finishing the tests
        Database.clearDatabase();

    }
}