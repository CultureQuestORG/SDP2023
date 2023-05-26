package ch.epfl.culturequest.notifications;

import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static org.hamcrest.Matchers.is;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;

import ch.epfl.culturequest.NavigationActivity;
import ch.epfl.culturequest.R;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.ui.events.EventsActivity;

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
        PushNotification notification = new SightseeingNotification("John");
        Notification sightseeingNotification = notification.buildNotification(context);
        assertThat(sightseeingNotification.extras.get(Notification.EXTRA_TITLE).toString(), is("John, you have a new sightseeing event!"));
        assertThat(sightseeingNotification.extras.get(Notification.EXTRA_TEXT).toString(), is(profile.getUsername() + " invited you to a new sightseeing event!"));
        assertThat(sightseeingNotification.priority, is(Notification.PRIORITY_HIGH));
        assertThat(sightseeingNotification.getSmallIcon().getResId(), is(R.drawable.logo_compact));
        assertThat(sightseeingNotification.getChannelId(), is(SightseeingNotification.CHANNEL_ID));
        Intent intent = new Intent(context, EventsActivity.class).putExtra("redirect", "sightseeing");
        assertThat(sightseeingNotification.contentIntent, is(PendingIntent.getActivity(context, notification.getNotificationId().hashCode(), intent, PendingIntent.FLAG_MUTABLE)));
    }
}
