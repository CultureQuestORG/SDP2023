package ch.epfl.culturequest.ui;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Button;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.TreeMap;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.social.PictureAdapter;
import ch.epfl.culturequest.social.Post;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.ui.profile.ProfileFragment;
import ch.epfl.culturequest.utils.EspressoIdlingResource;

@RunWith(AndroidJUnit4.class)
public class ProfileFragmentTest {

    private ProfileFragment fragment;

    private Profile profile;


    @Before
    public void setUp() throws InterruptedException {
        // Set up the database to run on the local emulator of Firebase
        Database.setEmulatorOn();

        // clear the database before starting the following tests
        Database.clearDatabase();

        Post post = new Post("abc", "123",
                "https://firebasestorage.googleapis.com/v0/b/culturequest.appspot.com/o/0000598561_OG.jpeg?alt=media&token=503f241d-cebf-4050-8897-4cbb7595e0b8",
                "Piece of Art", 0, 0, new ArrayList<>());

        Database.uploadPost(post);

        profile = new Profile("123", "Johnny Doe", "Xx_john_xX", "john.doe@gmail.com", "0707070707", "https://firebasestorage.googleapis.com/v0/b/culturequest.appspot.com/o/izi.png?alt=media&token=b62383d6-3831-4d22-9e82-0a02a9425289", 10);
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

        Thread.sleep(8000);
    }

    @After
    public void tearDown() {
        // clear the database after the tests
        Database.clearDatabase();
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
    public void deleteButtonWorks() throws InterruptedException {
        assertEquals(1, Objects.requireNonNull(Database.getPosts(Profile.getActiveProfile().getUid()).join()).size());

        //long click on the first picture should open an alert dialog
        onView(withId(R.id.pictureGrid)).perform(RecyclerViewActions.actionOnItemAtPosition(0, ViewActions.longClick()));
        // should open an alert dialog
        final AlertDialog dialog = PictureAdapter.getLastDialog();
        assertNotNull(dialog);
        if (dialog.isShowing()) {
            try {
                // click on the negative button on the ui thread
                runOnUiThread(() -> dialog.getButton(DialogInterface.BUTTON_NEGATIVE).performClick());
                assertFalse(dialog.isShowing());
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }


        onView(withId(R.id.pictureGrid)).perform(RecyclerViewActions.actionOnItemAtPosition(0, ViewActions.longClick()));

        final AlertDialog dialog2 = PictureAdapter.getLastDialog();
        assertNotNull(dialog2);
        if (dialog2.isShowing()) {
            try {
                runOnUiThread(() -> dialog2.getButton(DialogInterface.BUTTON_POSITIVE).performClick());
                assertTrue(Database.getPosts(Profile.getActiveProfile().getUid()).join().isEmpty());
                // check that the pictureGrid has no child
                onView(withId(R.id.pictureGrid)).check(matches(hasChildCount(0)));

            } catch (Throwable e) {
                e.printStackTrace();
            }
        }


    }
}