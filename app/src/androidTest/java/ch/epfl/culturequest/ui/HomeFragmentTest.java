package ch.epfl.culturequest.ui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;
import static ch.epfl.culturequest.utils.ProfileUtils.DEFAULT_PROFILE_PIC_PATH;

import android.view.View;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.backend.artprocessing.processingobjects.BasicArtDescription;
import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.social.Post;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.ui.home.HomeFragment;

@RunWith(AndroidJUnit4.class)
public class HomeFragmentTest {

    private HomeFragment fragment;


    public ViewAction clickChildViewWithId(final int id) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return null;
            }

            @Override
            public String getDescription() {
                return "Click on a child view with specified id.";
            }

            @Override
            public void perform(UiController uiController, View view) {
                View v = view.findViewById(id);
                v.performClick();
            }
        };

    }


    @Before
    public void setUp() throws InterruptedException {
        // Set up the database to run on the local emulator of Firebase
        Database.setEmulatorOn();

        // clear the database before starting the following tests
        Database.clearDatabase();

        // Initialize the database with some test profiles
        ArrayList<String> myFriendsIds = new ArrayList<>();
        myFriendsIds.add("friendID");
        Profile activeProfile = new Profile("currentUserUid", "currentUserName", "currentUserUsername", "currentUserEmail", "currentUserPhone", "currentUserProfilePicture", 400, new HashMap<>(), new ArrayList<>());
        Profile.setActiveProfile(activeProfile);
        Database.setProfile(activeProfile);
        Database.setProfile(new Profile("friendID", "testName2", "testUsername2", "testEmail2", "testPhone2", "testProfilePicture2", 300, new HashMap<>(), new ArrayList<>()));
        Database.addFollow("currentUserUid", "friendID");

        Database.uploadPost(new Post("postUid1",
                "friendID",
                DEFAULT_PROFILE_PIC_PATH,
                "David of Michelangelo",
                0,
                0,
                new ArrayList<>()));

        Database.uploadPost(new Post("postUid1",
                "friendID",
                DEFAULT_PROFILE_PIC_PATH,
                "Mona Lisa",
                1,
                50,
                new ArrayList<>()));

        Database.setArtwork(new BasicArtDescription(
                "Mona Lisa",
                "Leonardo da Vinci",
                "La Joconde (en italien: La Gioconda [la dʒoˈkonda] ou Monna Lisa [ˈmɔnna ˈliːza]), ou Portrait de Mona Lisa, est un tableau de l'artiste Léonard de Vinci, réalisé entre 1503 et 1506 ou entre 1513 et 15161",
                BasicArtDescription.ArtType.PAINTING,
                "1503",
                "Paris",
                "France",
                "Musée du Louvre",
                100
        ));

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

    // Test that the like count is displayed correctly
    @Test
    public void likeCountIsVisible() throws InterruptedException {
        Thread.sleep(2000);
        onView(withId(R.id.like_count)).check(matches(withText("50 likes")));
    }

    @Test
    public void clickOnLikeWorks() throws InterruptedException {
        // click on the first post
        Thread.sleep(2000);
        onView(withId(R.id.feed_container)).perform(RecyclerViewActions.actionOnItemAtPosition(0, clickChildViewWithId(R.id.like_button)));

        Thread.sleep(2000);

        try {
            assertThat(Database.getPosts("friendID").get(5, java.util.concurrent.TimeUnit.SECONDS).get(0).isLikedBy("currentUserUid"), is(true));
        } catch (ExecutionException | TimeoutException e) {
            fail("Test failed because of an exception: " + e.getMessage());
        }

        Thread.sleep(2000);

        onView(withId(R.id.feed_container)).perform(RecyclerViewActions.actionOnItemAtPosition(0, clickChildViewWithId(R.id.like_button)));

        Thread.sleep(2000);

        try {
            assertThat(Database.getPosts("friendID").get(5, java.util.concurrent.TimeUnit.SECONDS).get(0).isLikedBy("currentUserUid"), is(false));
        } catch (ExecutionException | TimeoutException e) {
            fail("Test failed because of an exception: " + e.getMessage());
        }
    }

    @Test
    public void clickOnPostDisplaysInfos() throws InterruptedException {
        // click on the first post
        Thread.sleep(2000);
        onView(withId(R.id.post_recto)).check(matches(isDisplayed()));
        onView(withId(R.id.feed_container)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        Thread.sleep(1000);

        // check that the post is displayed
        onView(withId(R.id.post_verso)).check(matches(isDisplayed()));

        onView(withId(R.id.feed_container)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        Thread.sleep(1000);

        // check that the post is displayed
        onView(withId(R.id.post_recto)).check(matches(isDisplayed()));
    }

    @After
    public void tearDown() {
        // clear the database after the tests
        Database.clearDatabase();
    }
}