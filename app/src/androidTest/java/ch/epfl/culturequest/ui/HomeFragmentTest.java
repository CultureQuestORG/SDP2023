package ch.epfl.culturequest.ui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

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

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.social.Post;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.ui.home.HomeFragment;

@RunWith(AndroidJUnit4.class)
public class HomeFragmentTest {

    private HomeFragment fragment;


    @Before
    public void setUp() throws InterruptedException {
        // Set up the database to run on the local emulator of Firebase
        Database.setEmulatorOn();

        // clear the database before starting the following tests
        Database.clearDatabase();

        // Initialize the database with some test profiles
        ArrayList<String> myFriendsIds = new ArrayList<>();
        myFriendsIds.add("friendID");
        Profile activeProfile = new Profile("currentUserUid", "currentUserName", "currentUserUsername", "currentUserEmail", "currentUserPhone", "currentUserProfilePicture", 400,new HashMap<>());
        Profile.setActiveProfile(activeProfile);
        Database.setProfile(activeProfile);
        Database.setProfile(new Profile("friendID", "testName2", "testUsername2", "testEmail2", "testPhone2", "testProfilePicture2", 300,new HashMap<>()));
        Database.addFollow("currentUserUid", "friendID");

        Database.uploadPost(new Post("postUid1",
                "friendID",
                "https://firebasestorage.googleapis.com/v0/b/culturequest.appspot.com/o/images%2F08064ffd-b463-4a99-9ee3-00446168e167?alt=media&token=9084b547-1058-4d16-8721-90adc10d867b",
                "David of Michelangelo",
                0,
                0,
                new ArrayList<>()));

        // Launch the fragment with the current user's uid for testing
        ActivityScenario<FragmentActivity> activityScenario = ActivityScenario.launch(FragmentActivity.class);
        activityScenario.onActivity(activity -> {
            fragment = new HomeFragment();
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(android.R.id.content, fragment);
            fragmentTransaction.commitNow();
        });

        Thread.sleep(5000);
    }

    @Test
    public void postIsVisible() throws InterruptedException {
        // the recycler view should contains 1 element (id is feed_container)
        Thread.sleep(2000);
        onView(withId(R.id.feed_container)).check(matches(hasChildCount(1)));
    }

    @After
    public void tearDown() {
        // clear the database after the tests
        Database.clearDatabase();
    }
}