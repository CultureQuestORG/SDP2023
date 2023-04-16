package ch.epfl.culturequest.ui;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.junit.Assert.assertEquals;

import android.content.Intent;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.SettingsActivity;
import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.database.MockDatabase;
import ch.epfl.culturequest.social.Image;
import ch.epfl.culturequest.social.Post;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.ui.profile.ProfileFragment;
import ch.epfl.culturequest.utils.EspressoIdlingResource;

@RunWith(AndroidJUnit4.class)
public class ProfileFragmentTest {

    private ProfileFragment fragment;

    private Profile profile;
    private Post image;


    @Before
    public void setUp() {
        // add EspressoIdlingResource to the IdlingRegistry
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource);

        Database.init(new MockDatabase());

        image = new Post("abc", "123",
                "https://firebasestorage.googleapis.com/v0/b/culturequest.appspot.com/o/0000598561_OG.jpeg?alt=media&token=503f241d-cebf-4050-8897-4cbb7595e0b8",
                "Piece of Art", new Date(), 0, new ArrayList<>());

        Database.uploadPost(image);

        profile = new Profile("123", "Johnny Doe", "Xx_john_xX", "john.doe@gmail.com", "0707070707", "https://firebasestorage.googleapis.com/v0/b/culturequest.appspot.com/o/izi.png?alt=media&token=b62383d6-3831-4d22-9e82-0a02a9425289", List.of(image), new ArrayList<>(), 10);
        profile.setPosts(new ArrayList<>(Collections.singleton(image)));
        Profile.setActiveProfile(profile);
        Database.setProfile(profile);

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
        onView(withId(R.id.profileUsername)).check(matches(withText("Xx_john_xX")));
        profile.setUsername("johnny");
        onView(withId(R.id.profileUsername)).check(matches(withText("johnny")));

    }

    @Test
    public void settingButtonWorks() {

        onView(withId(R.id.settingsButton)).perform(click());
        onView(withId(R.id.log_out)).check(matches(isEnabled()));
    }
}