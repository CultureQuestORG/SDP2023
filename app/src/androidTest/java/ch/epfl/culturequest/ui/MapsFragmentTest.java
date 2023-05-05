package ch.epfl.culturequest.ui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static ch.epfl.culturequest.utils.ProfileUtils.DEFAULT_PROFILE_PATH;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.authentication.Authenticator;
import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.ui.map.MapsFragment;

@RunWith(AndroidJUnit4.class)
public class MapsFragmentTest {
    @Before
    public void setUp() throws InterruptedException {

        Profile profile = new Profile("test", "Johnny Doe", "Xx_john_xX", "john.doe@gmail.com", "0707070707", DEFAULT_PROFILE_PATH, 35, new HashMap<>());
        Profile.setActiveProfile(profile);
        Database.setProfile(profile);

        ActivityScenario<FragmentActivity> activityScenario = ActivityScenario.launch(FragmentActivity.class);
        activityScenario.onActivity(activity -> {
            MapsFragment fragment = new MapsFragment();
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(android.R.id.content, fragment);
            fragmentTransaction.commitNow();
        });

        Thread.sleep(5000);
    }

    @Test
    public void mapsFragmentIsVisible() {
        // Check that the maps fragment is visible
        onView(withId(R.id.map)).check(matches(isDisplayed()));
    }

}