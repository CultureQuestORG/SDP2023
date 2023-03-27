package ch.epfl.culturequest.ui;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasMinimumChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasToString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import android.app.Instrumentation;
import android.content.Intent;

import androidx.test.espresso.IdlingRegistry;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.List;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.database.FireDatabase;
import ch.epfl.culturequest.database.MockDatabase;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.ui.profile.DisplayUserProfileActivity;
import ch.epfl.culturequest.utils.EspressoIdlingResource;

@RunWith(AndroidJUnit4.class)
public class SearchUserActivityTest {
    @Rule
    public ActivityScenarioRule<SearchUserActivity> testRule = new ActivityScenarioRule<>(SearchUserActivity.class);
    FirebaseDatabase firebaseDatabase;

    @Before
    public void setUp() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.useEmulator("10.0.2.2", 9000);
        Database.init(new FireDatabase(firebaseDatabase));

        // clear the database before starting the following tests
        firebaseDatabase.getReference().setValue(null);
        // Set up the database to run on the local emulator of Firebase

        // Initialize the database with some test profiles
        Database.setProfile(new Profile("testUid1", "testName1", "alice", "currentUserEmail", "currentUserPhone", "currentUserProfilePicture", List.of(), 0));
        Database.setProfile(new Profile("testUid2", "testName2", "allen", "testEmail2", "testPhone2", "testProfilePicture2", List.of(), 0));
        Database.setProfile(new Profile("testUid3", "testName3", "bob", "testEmail3", "testPhone3", "testProfilePicture3", List.of(), 0));
        Database.setProfile(new Profile("testUid4", "testName4", "john", "testEmail4", "testPhone4", "testProfilePicture4", List.of(), 0));
        // Add EspressoIdlingResource to the IdlingRegistry to make sure tests wait for the fragment and database to be ready
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource);
    }

    @Test
    public void test() {

    }

    @Test
    public void typingUsernameAutomaticallyShowsUsers() {
        onView(withId(R.id.search_user)).perform(typeText("alice"));
        onData(hasToString(containsString("alice")))
                .inAdapterView(withId(R.id.list_view))
                .atPosition(0)
                .check(matches(withText("alice")));
    }

    @Test
    public void typingAutomaticallyDisplaysUsers() {
        onView(withId(R.id.search_user)).perform(typeText("a"));
        onView(withId(R.id.list_view))
                .check(matches((hasMinimumChildCount(1))));
    }

    @Test
    public void emptyQueryDisplaysNothing() {
        onView(withId(R.id.search_user)).perform(typeText(""));
        onView(withId(R.id.list_view))
                .check(matches(Matchers.not(hasMinimumChildCount(1))));
    }

    @Test
    public void clickingOnUserOpensProfilePage() {
        Instrumentation.ActivityMonitor activityMonitor = getInstrumentation()
                .addMonitor(DisplayUserProfileActivity.class.getName(), null, false);

        onView(withId(R.id.search_user)).perform(typeText("allen"));
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
    public void teardown(){
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource);
        firebaseDatabase.getReference().setValue(null);
    }

}
