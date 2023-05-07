package ch.epfl.culturequest.social.notifications;

import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static org.hamcrest.Matchers.is;

import android.app.Notification;
import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.social.Profile;

@RunWith(AndroidJUnit4.class)
public class SightSeeingNotificationTest {
    private final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    private final Profile profile = new Profile("test", "test", "SightSeer", "test", "test", "test", 0, new HashMap<>());

    @Before
    public void setup() {
        NotificationInterface.createNotificationChannels(context);
        Profile.setActiveProfile(profile);
    }

    @Test
    public void testSightSeeingNotification() {
        Notification sightSeeingNotification = new SightSeeingNotification("John").get(context);
        assertThat(sightSeeingNotification.extras.get(Notification.EXTRA_TITLE).toString(), is(profile.getUsername() + ", you have a new sightseeing event!"));
        assertThat(sightSeeingNotification.extras.get(Notification.EXTRA_TEXT).toString(), is("John invited you to a new sightseeing event!"));
        assertThat(sightSeeingNotification.priority, is(Notification.PRIORITY_DEFAULT));
        assertThat(sightSeeingNotification.getSmallIcon().getResId(), is(R.drawable.logo_compact));
        assertThat(sightSeeingNotification.getChannelId(), is(SightSeeingNotification.CHANNEL_ID));
    }
}
