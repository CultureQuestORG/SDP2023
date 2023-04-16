package ch.epfl.culturequest.ui;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasMinimumChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasToString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.app.Instrumentation;
import android.content.Intent;

import androidx.test.espresso.IdlingRegistry;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.ui.profile.DisplayUserProfileActivity;
import ch.epfl.culturequest.utils.EspressoIdlingResource;

@RunWith(AndroidJUnit4.class)
public class SearchUserActivityTest {
    @Rule
    public ActivityScenarioRule<SearchUserActivity> testRule = new ActivityScenarioRule<>(SearchUserActivity.class);

    @Before
    public void setUp() {
        // Set up the database to run on the local emulator of Firebase
        Database.setEmulatorOn();

        // clear the database before starting the following tests
        Database.clearDatabase();

        Database.setProfile(new Profile("testUid1", "testName1", "alice", "currentUserEmail", "currentUserPhone", "currentUserProfilePicture", new ArrayList<>(), new ArrayList<>(), 0));
        Database.setProfile(new Profile("testUid2", "testName2", "allen", "testEmail2", "testPhone2", "testProfilePicture2", new ArrayList<>(), new ArrayList<>(), 0));
        Database.setProfile(new Profile("testUid3", "testName3", "bob", "testEmail3", "testPhone3", "testProfilePicture3", new ArrayList<>(), new ArrayList<>(), 0));
        Database.setProfile(new Profile("testUid4", "testName4", "john", "testEmail4", "testPhone4", "testProfilePicture4", new ArrayList<>(), new ArrayList<>(), 0));

        // Add EspressoIdlingResource to the IdlingRegistry to make sure tests wait for the fragment and database to be ready
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource);
    }


    @Test
    public void typingUsernameAutomaticallyShowsUsers() throws InterruptedException {
        onView(withId(R.id.search_user)).perform(typeText("alice"));
        Thread.sleep(1000);
        onData(hasToString(containsString("alice"))).inAdapterView(withId(R.id.list_view));
    }

    @Test
    public void emptyQueryDisplaysNothing() {
        onView(withId(R.id.search_user)).perform(typeText(""));
        onView(withId(R.id.list_view))
                .check(matches(Matchers.not(hasMinimumChildCount(1))));
    }

    @Test
    public void pressback() {
        onView(withId(R.id.back_icon1)).perform(click());
    }

    @Test
    public void clickingOnUserOpensProfilePage() throws InterruptedException {
        Instrumentation.ActivityMonitor activityMonitor = getInstrumentation()
                .addMonitor(DisplayUserProfileActivity.class.getName(), null, false);
        onView(withId(R.id.search_user)).perform(typeText("allen"));
        Thread.sleep(1000);
        onData(hasToString(containsString("allen")))
                .inAdapterView(withId(R.id.list_view))
                .atPosition(0).perform(click());


        DisplayUserProfileActivity secondActivity = (DisplayUserProfileActivity) activityMonitor
                .waitForActivityWithTimeout(5000);
        assertNotNull(secondActivity);

        Intent expectedIntent = new Intent(getInstrumentation().getTargetContext(), DisplayUserProfileActivity.class);
        assertEquals(expectedIntent.getComponent(), secondActivity.getIntent().getComponent());
    }


    @After
    public void teardown() {
        //Database.deleteProfile("testUid1");
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource);

        // clear the database after finishing the tests
        Database.clearDatabase();
    }
}
