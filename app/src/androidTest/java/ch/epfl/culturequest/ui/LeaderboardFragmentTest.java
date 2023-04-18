package ch.epfl.culturequest.ui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.hasMinimumChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.ui.leaderboard.LeaderboardFragment;
import ch.epfl.culturequest.utils.EspressoIdlingResource;

@RunWith(AndroidJUnit4.class)
public class LeaderboardFragmentTest {
    private LeaderboardFragment fragment;
    FirebaseDatabase firebaseDatabase;

    @Before
    public void setUp() {
        // Set up the database to run on the local emulator of Firebase
        Database.setEmulatorOn();

        // clear the database before starting the following tests
        Database.clearDatabase();

        // Initialize the database with some test profiles
        ArrayList<String> myFriendsIds = new ArrayList<>();
        myFriendsIds.add("testUid2");
        myFriendsIds.add("testUid3");
        Profile activeProfile =new Profile("currentUserUid", "currentUserName", "currentUserUsername", "currentUserEmail", "currentUserPhone", "currentUserProfilePicture",  new ArrayList<>(), myFriendsIds , 400);
        Profile.setActiveProfile(activeProfile);
        Database.setProfile(activeProfile);
        Database.setProfile(new Profile("testUid2", "testName2", "testUsername2", "testEmail2", "testPhone2", "testProfilePicture2", new ArrayList<>(), new ArrayList<>(), 300));
        Database.setProfile(new Profile("testUid3", "testName3", "testUsername3", "testEmail3", "testPhone3", "testProfilePicture3", new ArrayList<>(), new ArrayList<>(), 200));
        Database.setProfile(new Profile("testUid4", "testName4", "testUsername4", "testEmail4", "testPhone4", "testProfilePicture4", new ArrayList<>(), new ArrayList<>(), 100));

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

    @Test
    public void friendlyRankingWorks(){
        onView(withId(R.id.friendsLeaderboardButton)).perform(click());
        //R.id.friends_recycler_view should be visible
        onView(withId(R.id.friends_recycler_view)).check(matches(isEnabled()));
        //R.id.friends_recycler_view should have exactly 3 children
        onView(withId(R.id.friends_recycler_view)).check(matches(hasChildCount(3)));

        //R.id.recycler_view should not be visible
        onView(withId(R.id.recycler_view)).check(matches(not(isDisplayed())));


        // should be first among my friends
        onView(withId(R.id.current_user_rank)).check(matches(withText("1")));

    }

    @After
    public void tearDown() {
        // remove EspressoIdlingResource from the IdlingRegistry
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource);

        // clear the database after finishing the tests
        Database.clearDatabase();
    }
}