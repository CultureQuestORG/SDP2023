package ch.epfl.culturequest.ui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.authentication.Authenticator;
import ch.epfl.culturequest.backend.map_collection.OTMLatLng;
import ch.epfl.culturequest.backend.map_collection.OTMLocation;
import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.social.SightseeingEvent;
import ch.epfl.culturequest.ui.events.EventsActivity;

@RunWith(AndroidJUnit4.class)
public class EventActivityTest {
    @Before
    public void setUp() throws InterruptedException {
        // Set up the database to run on the local emulator of Firebase
        Database.setEmulatorOn();

        // clear the database before starting the following tests
        Database.clearDatabase();

        // Initialize the database with some test profiles
        ArrayList<String> myFriendsIds = new ArrayList<>();
        myFriendsIds.add("friendID");

        Authenticator.manualSignUp("test@gmail.com", "abcdefg");
        Authenticator.manualSignIn("test@gmail.com", "abcdefg");

        Profile activeProfile = new Profile("currentUserUid", "currentUserName", "currentUserUsername", "currentUserEmail", "currentUserPhone", "currentUserProfilePicture", 400, new HashMap<>(), new ArrayList<>());
        Profile friendProfile = new Profile("friend", "friend", "friend", "friendEmail", "friendPhone", "friendProfilePicture", 400, new HashMap<>(), new ArrayList<>());
        Profile.setActiveProfile(activeProfile);
        Database.setProfile(activeProfile);
        Database.setSightseeingEvent(new SightseeingEvent(activeProfile, List.of(activeProfile), List.of(new OTMLocation("Museum", new OTMLatLng(10, 10), "Museum"))));
        Database.setSightseeingEvent(new SightseeingEvent(activeProfile, List.of(activeProfile, friendProfile), List.of(new OTMLocation("Museum 2", new OTMLatLng(10, 10), "Museum 2"))));

        ActivityScenario<EventsActivity> testRule = ActivityScenario.launch(EventsActivity.class);

        Thread.sleep(5000);
    }

    @Test
    public void sightseeingEventsFirstDisplayed() {
        onView(withId(R.id.events_recycler_view)).check(matches(isDisplayed()));
        onView(withText("Museum")).check(matches(isDisplayed()));
        onView(withText("Museum 2")).check(matches(isDisplayed()));
    }

    @Test
    public void sightseeingEventsRightDisplay() {
        onView(withId(R.id.sightseeing_button)).perform(click());
        onView(withId(R.id.events_recycler_view)).perform(RecyclerViewActions.scrollToPosition(0));

        onView(withText("Museum")).check(matches(isDisplayed()));
        onView(withText("currentUserUsername")).check(matches(isDisplayed()));
        onView(withText("Museum 2")).check(matches(isDisplayed()));
        onView(withText("currentUserUsername")).check(matches(isDisplayed()));
        onView(withText("friend")).check(matches(isDisplayed()));
    }


}