package ch.epfl.culturequest.ui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.android21buttons.fragmenttestrule.FragmentTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.social.Post;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.ui.home.HomeFragment;
import ch.epfl.culturequest.ui.leaderboard.LeaderboardFragment;
import ch.epfl.culturequest.utils.EspressoIdlingResource;

@RunWith(AndroidJUnit4.class)
public class HomeFragmentTest {

    private HomeFragment fragment;




    @Before
    public void setUp() {
        // Set up the database to run on the local emulator of Firebase
        Database.setEmulatorOn();

        // clear the database before starting the following tests
        Database.clearDatabase();


        // Initialize the database with some test profiles
        ArrayList<String> myFriendsIds = new ArrayList<>();
        myFriendsIds.add("friendID");
        Profile activeProfile =new Profile("currentUserUid", "currentUserName", "currentUserUsername", "currentUserEmail", "currentUserPhone", "currentUserProfilePicture",  new ArrayList<>(), myFriendsIds , 400);
        Profile.setActiveProfile(activeProfile);
        Database.setProfile(activeProfile);
        Database.setProfile(new Profile("friendID", "testName2", "testUsername2", "testEmail2", "testPhone2", "testProfilePicture2", new ArrayList<>(), new ArrayList<>(), 300));

        Database.uploadPost(new Post("postUid1",
                "friendID",
                "https://firebasestorage.googleapis.com/v0/b/culturequest.appspot.com/o/images%2F08064ffd-b463-4a99-9ee3-00446168e167?alt=media&token=9084b547-1058-4d16-8721-90adc10d867b",
                "David of Michelangelo",
                new Date(),
            0,
            List.of()));
        // Add EspressoIdlingResource to the IdlingRegistry to make sure tests wait for the fragment and database to be ready
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource);

        // Launch the fragment with the current user's uid for testing
        ActivityScenario<FragmentActivity> activityScenario = ActivityScenario.launch(FragmentActivity.class);
        activityScenario.onActivity(activity -> {
            fragment = new HomeFragment();
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(android.R.id.content, fragment);
            fragmentTransaction.commitNow();
        });
    }
    @Test
    public void postIsVisible() {
        // the recycler view should contains 1 element (id is feed_container)
        onView(withId(R.id.feed_container)).check(matches(hasChildCount(1)));
    }
}