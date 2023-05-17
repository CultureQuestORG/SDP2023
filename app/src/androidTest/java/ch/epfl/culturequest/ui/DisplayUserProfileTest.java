package ch.epfl.culturequest.ui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.ui.profile.DisplayUserProfileActivity;
import ch.epfl.culturequest.utils.EspressoIdlingResource;
import ch.epfl.culturequest.utils.ProfileUtils;

@RunWith(AndroidJUnit4.class)
public class DisplayUserProfileTest {

   @Before
    public void setUp() {
        // Set up the database to run on the local emulator of Firebase
        Database.setEmulatorOn();

        // clear the database before starting the following tests
        Database.clearDatabase();

        Database.setProfile(new Profile("testUid1", "testName1", "alice", "currentUserEmail", "currentUserPhone", "currentUserProfilePicture", 0,new HashMap<>(), new ArrayList<>()));
    }

    @After
    public void tearDown() {
        // clear the database after finishing the tests
        Database.clearDatabase();
    }

    @Test
    public void backButtonIsVisible() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), DisplayUserProfileActivity.class);
        intent.putExtra("uid", "testUid1");
        ActivityScenario.launch(intent);
        onView(withId(R.id.back_button)).check(matches(isClickable()));
    }

    @Test
    public void homeButtonIsVisible() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), DisplayUserProfileActivity.class);
        intent.putExtra("uid", "testUid1");
        ActivityScenario.launch(intent);
        onView(withId(R.id.home_icon)).check(matches(isClickable()));
    }
}
