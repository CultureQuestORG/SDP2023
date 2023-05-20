package ch.epfl.culturequest.ui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;

import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.authentication.Authenticator;
import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.notifications.PushNotification;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.ui.notifications.NotificationsActivity;

@RunWith(AndroidJUnit4.class)
public class NotificationsActivityTest {
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
        Profile.setActiveProfile(activeProfile);
        Database.setProfile(activeProfile);

        PushNotification notif1 = new PushNotification("notif1", "notif1", "LIKE", "senderId1");
        Database.addNotification(activeProfile.getUid(), notif1);

        PushNotification notif2 = new PushNotification("notif2", "notif2", "FOLLOW", "senderId2");
        Database.addNotification(activeProfile.getUid(), notif2);

        PushNotification notif3 = new PushNotification("notif3", "notif3", "SCAN", "senderId3");
        Database.addNotification(activeProfile.getUid(), notif3);

        ActivityScenario<NotificationsActivity> testRule = ActivityScenario.launch(NotificationsActivity.class);

        Thread.sleep(5000);
    }

    @Test
    public void testNotificationsActivityShowsNotifications() throws InterruptedException {
        onView(withId(R.id.notifications_recycler_view)).check(matches(isDisplayed()));
        onView(withText("notif2")).check(matches(isEnabled()));
        onView(withText("notif1")).check(matches(isEnabled()));
    }

    @Test
    public void testNotificationsActivityDeleteWorks() throws InterruptedException {
        onView(withId(R.id.notifications_recycler_view)).check(matches(isDisplayed()));
        onView(withId(R.id.notifications_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, clickChildViewWithId(R.id.delete_button)));

        Thread.sleep(4000);

        try {
            assertThat(Database.getNotifications(Profile.getActiveProfile().getUid()).get().size(), is(2));
        } catch (ExecutionException e) {
            fail("Test failed because of an exception: " + e.getMessage());
        }

        onView(withText("notif2")).check(matches(isEnabled()));
        onView(withText("notif1")).check(matches(isEnabled()));
    }

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
}
