package ch.epfl.culturequest.ui;


import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.net.Uri;

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

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.database.MockDatabase;
import ch.epfl.culturequest.social.Image;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.ui.profile.ProfileFragment;
import ch.epfl.culturequest.utils.EspressoIdlingResource;

@RunWith(AndroidJUnit4.class)
public class ProfileFragmentTest {

    private ProfileFragment fragment;

    private Profile profile;
    private Image image;




    @Before
    public void setUp() {
        // add EspressoIdlingResource to the IdlingRegistry
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource);

        Database.init(new MockDatabase());
        Database db = new Database();
        image = new Image("Piece of Art","bla bla",  "https://firebasestorage.googleapis.com/v0/b/culturequest.appspot.com/o/0000598561_OG.jpeg?alt=media&token=503f241d-cebf-4050-8897-4cbb7595e0b8"
                ,123, "123");
        db.setImage(image);


        profile=new Profile("123", "Johnny Doe", "Xx_john_xX", "john.doe@gmail.com","0707070707", "https://firebasestorage.googleapis.com/v0/b/culturequest.appspot.com/o/izi.png?alt=media&token=b62383d6-3831-4d22-9e82-0a02a9425289", List.of(image), 10);
        db.setProfile(profile);


        ActivityScenario<FragmentActivity> activityScenario = ActivityScenario.launch(FragmentActivity.class);
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
    public void textViewDisplaysCorrectText() {


        onView(withId(R.id.profileName)).check(matches(withText("Johnny Doe")));
        profile.setName("John Doe");
        onView(withId(R.id.profileName)).check(matches(withText("John Doe")));








    }
}