package ch.epfl.culturequest.notifications;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.social.Profile;

@RunWith(AndroidJUnit4.class)
public class FireMessagingTest {

    @Before
    public void setUp() {
        // Set up the database to run on the local emulator of Firebase
        Database.setEmulatorOn();

        // clear the database before starting the following tests
        Database.clearDatabase();
    }

    @Test
    public void getTokenReturnsTokenOfCorrectLength() {
        try {
            String token = FireMessaging.getDeviceToken().get(5, TimeUnit.SECONDS);
            assertThat(token.length(), is(163));
        }
        catch (ExecutionException | InterruptedException | TimeoutException e) {
            fail("Test failed because of an exception: " + e.getMessage());
        }
    }

    @Test
    public void sendNotificationReturnsTrueAndAddsNotificationToDatabase() {
        try {
            List<String> deviceTokens =  new ArrayList<>();
            deviceTokens.add(FireMessaging.getDeviceToken().get(5, TimeUnit.SECONDS));
            Profile profile = new Profile("test", "test", "test", "test", "test", "test", 0, new HashMap<>(), deviceTokens);
            Database.setProfile(profile).get(5, TimeUnit.SECONDS);
            Thread.sleep(2000);
            String uid = "test";
            PushNotification notification = new PushNotification("title", "text", "channelId");
            boolean result = FireMessaging.sendNotification(uid, notification).get(5, TimeUnit.SECONDS).get();
            assertThat(result, is(true));
            assertThat(Database.getNotifications(uid).get(5, TimeUnit.SECONDS).get(0), is(notification));
        }
        catch (ExecutionException | InterruptedException | TimeoutException e) {
            fail("Test failed because of an exception: " + e.getMessage());
        }
    }

    @Test
    public void sendNotificationToUnknownUserReturnsFalse() {
        try {
            String uid = "unknown";
            PushNotification notification = new PushNotification("title", "text", "channelId");
            boolean result = FireMessaging.sendNotification(uid, notification).get(5, TimeUnit.SECONDS).get();
            assertThat(result, is(false));
        }
        catch (ExecutionException | InterruptedException | TimeoutException e) {
            fail("Test failed because of an exception: " + e.getMessage());
        }
    }

    @After
    public void tearDown() {
        // clear the database after the tests
        Database.clearDatabase();
    }
}