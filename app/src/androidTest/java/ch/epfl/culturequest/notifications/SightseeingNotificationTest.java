package ch.epfl.culturequest.notifications;

import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static org.hamcrest.Matchers.is;

import android.app.Notification;
import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.notifications.PushNotification;
import ch.epfl.culturequest.notifications.SightseeingNotification;
import ch.epfl.culturequest.social.Profile;

@RunWith(AndroidJUnit4.class)
public class SightseeingNotificationTest {
    private final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    private final Profile profile = new Profile("test", "test", "SightSeer", "test", "test", "test", 0, new HashMap<>(), new ArrayList<>());

    @Before
    public void setup() {
        PushNotification.createNotificationChannels(context);
        Profile.setActiveProfile(profile);
    }

    @Test
    public void testSightSeeingNotification() {
        Notification sightseeingNotification = new SightseeingNotification("John").buildNotification(context);
        assertThat(sightseeingNotification.extras.get(Notification.EXTRA_TITLE).toString(), is("John, you have a new sightseeing event!"));
        assertThat(sightseeingNotification.extras.get(Notification.EXTRA_TEXT).toString(), is(profile.getUsername() + " invited you to a new sightseeing event!"));
        assertThat(sightseeingNotification.priority, is(Notification.PRIORITY_DEFAULT));
        assertThat(sightseeingNotification.getSmallIcon().getResId(), is(R.drawable.logo_compact));
        assertThat(sightseeingNotification.getChannelId(), is(SightseeingNotification.CHANNEL_ID));
    }
}
