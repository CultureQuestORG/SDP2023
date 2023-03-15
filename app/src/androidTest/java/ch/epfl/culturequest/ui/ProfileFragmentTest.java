package ch.epfl.culturequest.ui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

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

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.database.MockDatabase;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.ui.profile.ProfileFragment;
import ch.epfl.culturequest.utils.EspressoIdlingResource;

@RunWith(AndroidJUnit4.class)
public class ProfileFragmentTest {

    private ActivityScenario<FragmentActivity> activityScenario;
    private ProfileFragment fragment;




    @Before
    public void setUp() {
        // add EspressoIdlingResource to the IdlingRegistry
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource);

        Database.init(new MockDatabase());
        Database db = new Database();
        db.setProfile(new Profile("123", "Johnny Doe", "Xx_john_xX", "john.doe@gmail.com","0707070707", "file://res/drawable/logo_compact.png"));


        activityScenario = ActivityScenario.launch(FragmentActivity.class);
        activityScenario.onActivity(activity -> {
            fragment = new ProfileFragment();
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(android.R.id.content, fragment);
            fragmentTransaction.commitNow();
        });
    }

    @After
    public void tearDown() {
        // remove EspressoIdlingResource from the IdlingRegistry
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource);
    }

    @Test
    public void textViewDisplaysCorrectText() throws InterruptedException {
            // Wait for the database to be accessed



            //Thread.sleep(10000);
            onView(withId(R.id.profileName)).check(matches(withText("Johnny Doe")));






    }
}