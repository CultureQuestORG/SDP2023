package ch.epfl.culturequest.ui;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;
import static ch.epfl.culturequest.utils.ProfileUtils.DEFAULT_PROFILE_PIC_PATH;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.authentication.Authenticator;
import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.social.Post;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.storage.FireStorage;
import ch.epfl.culturequest.ui.home.HomeFragment;
import ch.epfl.culturequest.ui.profile.ProfileFragment;
import ch.epfl.culturequest.utils.ProfileUtils;

@RunWith(AndroidJUnit4.class)
public class ProfileFragmentTest {

    private ProfileFragment fragment;
    private Profile profile;
    private final String email = "test@gmail.com";
    private final String password = "abcdefg";


    @Before
    public void setUp() throws InterruptedException, ExecutionException, TimeoutException {
        // Set up the database to run on the local emulator of Firebase
        Database.setEmulatorOn();

        // clear the database before starting the following tests
        Database.clearDatabase();

        //Set up the authentication to run on the local emulator of Firebase
        Authenticator.setEmulatorOn();
        FireStorage.setEmulatorOn();
        FireStorage.clearStorage();


        // Signs up a test user used in all the tests
        Authenticator.manualSignUp(email, password).join();

        // Manually signs in the user before the tests
        Authenticator.manualSignIn(email, password).join();

        // picpath is the url to the profile pic of the test user


        ProfileUtils.POSTS_ADDED = 0;

        String picpath = "https://firebasestorage.googleapis.com/v0/b/culturequest.appspot.com/o/profilePictures%2FcT93LtGk2dT9Jvg46pOpbBP69Kx1?alt=media&token=35ba6af5-104d-4218-bc26-3fb39f75ac15";
        Post post = new Post("abc", Authenticator.getCurrentUser().getUid(), picpath
                , "Piece of Art", 0, 0, new ArrayList<>());
        Database.uploadPost(post);

        profile = new Profile(Authenticator.getCurrentUser().getUid(), "Johnny Doe", "Xx_john_xX", "john.doe@gmail.com", "0707070707", DEFAULT_PROFILE_PIC_PATH, 35,new HashMap<>(), new ArrayList<>());
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

        Thread.sleep(5000);
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

    @Test
    public void checkingThatPostsUpdateCorrectly() throws InterruptedException {
        Post p = new Post("def", Authenticator.getCurrentUser().getUid(), DEFAULT_PROFILE_PIC_PATH
                , "Piece of Art number 2", 1, 0, new ArrayList<>());
        onView(withId(R.id.pictureGrid)).check(matches(hasChildCount(1)));

        Database.uploadPost(p);
        Thread.sleep(3000);
        ActivityScenario<FragmentActivity> activityScenario = ActivityScenario.launch(FragmentActivity.class);
        activityScenario.onActivity(activity -> {
            Fragment fragment = new HomeFragment();
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(android.R.id.content, fragment);
            fragmentTransaction.commitNow();
        });

        activityScenario = ActivityScenario.launch(FragmentActivity.class);
        activityScenario.onActivity(activity -> {
            fragment = new ProfileFragment();
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(android.R.id.content, fragment);
            fragmentTransaction.commitNow();
        });
    }

    @Test
    public void checkingThatPostsUpdateCorrectlyIfSigningOutInBetween() throws InterruptedException {
        Post p = new Post("def", Authenticator.getCurrentUser().getUid(), DEFAULT_PROFILE_PIC_PATH
                , "Piece of Art number 2", 1, 0, new ArrayList<>());
        onView(withId(R.id.pictureGrid)).check(matches(hasChildCount(1)));

        Database.uploadPost(p);
        Thread.sleep(3000);
        ActivityScenario<FragmentActivity> activityScenario = ActivityScenario.launch(FragmentActivity.class);
        activityScenario.onActivity(activity -> {
            Fragment fragment = new HomeFragment();
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(android.R.id.content, fragment);
            fragmentTransaction.commitNow();
        });

        Profile.setActiveProfile(null);

        activityScenario = ActivityScenario.launch(FragmentActivity.class);
        activityScenario.onActivity(activity -> {
            fragment = new ProfileFragment();
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(android.R.id.content, fragment);
            fragmentTransaction.commitNow();
        });
    }

    @Test
    public void deleteButtonWorks() throws InterruptedException {
        assertEquals(1, Objects.requireNonNull(Database.getPosts(Profile.getActiveProfile().getUid()).join()).size());
        //long click on the first picture should open an alert dialog
        onView(withId(R.id.pictureGrid)).perform(RecyclerViewActions.actionOnItemAtPosition(0, ViewActions.longClick()));
        // should open an alert dialog
        onView(withText("Are you sure you want to delete this post?")).check(matches(isDisplayed()));
        onView(withText("No")).perform(click());
        onView(withText("Are you sure you want to delete this post?")).check(doesNotExist());
        assertEquals(1, Objects.requireNonNull(Database.getPosts(Profile.getActiveProfile().getUid()).join()).size());
        Thread.sleep(2000);
        onView(withId(R.id.pictureGrid)).perform(RecyclerViewActions.actionOnItemAtPosition(0, ViewActions.longClick()));
        onView(withText("Are you sure you want to delete this post?")).check(matches(isDisplayed()));
        onView(withText("Yes")).perform(click());
        onView(withText("Are you sure you want to delete this post?")).check(doesNotExist());
        Thread.sleep(2000);
        assertEquals(0, Objects.requireNonNull(Database.getPosts(Profile.getActiveProfile().getUid()).join()).size());
        onView(withId(R.id.pictureGrid)).check(matches(hasChildCount(0)));
    }

    @Test
    public void scoreWorks() {
        // the score is 35 so the level should be 3
        onView(withId(R.id.level)).check(matches(withText(Integer.toString(3))));
        // the progress is "8/37 points"
        onView(withId(R.id.levelText)).check(matches(withText("8/37 points")));
    }

    @After
    public void tearDown() {
        // clear the database after the tests
        Database.clearDatabase();
    }
}