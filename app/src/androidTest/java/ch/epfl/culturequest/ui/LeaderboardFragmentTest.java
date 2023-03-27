package ch.epfl.culturequest.ui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.is;


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

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.database.FireDatabase;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.ui.leaderboard.LeaderboardFragment;
import ch.epfl.culturequest.utils.EspressoIdlingResource;

@RunWith(AndroidJUnit4.class)
public class LeaderboardFragmentTest {
    private LeaderboardFragment fragment;
    private Database database;
    private FirebaseDatabase firebaseDatabase;


    @Before
    public void setUp() {
        // set up the database
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.useEmulator("10.0.2.2", 9000);
        Database.init(new FireDatabase(firebaseDatabase));
        database = new Database();

        // Initialize the database with some profiles
        database.setProfile(new Profile("testUid", "testName", "testUsername", "testEmail","testPhone", "testProfilePicture", null, 400));
        database.setProfile(new Profile("testUid2", "testName2", "testUsername2", "testEmail2","testPhone2", "testProfilePicture2", null, 300));
        database.setProfile(new Profile("testUid3", "testName3", "testUsername3", "testEmail3","testPhone3", "testProfilePicture3", null, 200));
        database.setProfile(new Profile("testUid4", "testName4", "testUsername4", "testEmail4","testPhone4", "testProfilePicture4", null, 100));

        // add EspressoIdlingResource to the IdlingRegistry
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource);

        ActivityScenario<FragmentActivity> activityScenario = ActivityScenario.launch(FragmentActivity.class);
        activityScenario.onActivity(activity -> {
            fragment = LeaderboardFragment.newInstance("testUid");
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(android.R.id.content, fragment);
            fragmentTransaction.commitNow();
        });
    }

//    @Test
//    public void getRankIsCorrect() {
//        assertThat(database.getRank("testUid").join(), is(1));
//    }

    @After
    public void tearDown() {
        // remove EspressoIdlingResource from the IdlingRegistry
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource);
        // clear the database
        // firebaseDatabase.getReference().setValue(null);
    }

}