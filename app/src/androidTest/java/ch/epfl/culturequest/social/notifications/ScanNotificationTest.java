package ch.epfl.culturequest.social.notifications;

import static org.hamcrest.MatcherAssert.assertThat;
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
import ch.epfl.culturequest.social.Profile;


@RunWith(AndroidJUnit4.class)
public class ScanNotificationTest {
    private final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    private final Profile profile = new Profile("test", "test", "Scanner", "test", "test", "test", 0, new HashMap<>(), new ArrayList<>());

    @Before
    public void setup() {
        NotificationInterface.createNotificationChannels(context);
        Profile.setActiveProfile(profile);
    }

    @Test
    public void ScanNotificationIsCorrectlyCreated() {
        Notification scanNotification = new ScanNotification().get(context);
        assertThat(scanNotification.extras.get(Notification.EXTRA_TITLE).toString(), is(profile.getUsername() + ", you have a new scan!"));
        assertThat(scanNotification.extras.get(Notification.EXTRA_TEXT).toString(), is("We found a new offline scan result!"));
        assertThat(scanNotification.priority, is(Notification.PRIORITY_DEFAULT));
        assertThat(scanNotification.getSmallIcon().getResId(), is(R.drawable.logo_compact));
        assertThat(scanNotification.getChannelId(), is(ScanNotification.CHANNEL_ID));
    }

}