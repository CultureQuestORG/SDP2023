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
public class FollowNotificationTest {
    private final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    private final Profile profile = new Profile("test", "test", "Followee", "test", "test", "test", 0, new HashMap<>(), new ArrayList<>());

    @Before
    public void setup() {
        AbstractNotification.createNotificationChannels(context);
        Profile.setActiveProfile(profile);
    }

    @Test
    public void FollowNotificationIsCorrectlyCreated() {
        Notification followNotification = new FollowNotification("Follower").buildNotification(context);
        assertThat(followNotification.extras.get(Notification.EXTRA_TITLE).toString(), is(profile.getUsername() + ", you have a new follower!"));
        assertThat(followNotification.extras.get(Notification.EXTRA_TEXT).toString(), is("Follower is now following you!"));
        assertThat(followNotification.priority, is(Notification.PRIORITY_DEFAULT));
        assertThat(followNotification.getSmallIcon().getResId(), is(R.drawable.logo_compact));
        assertThat(followNotification.getChannelId(), is(FollowNotification.CHANNEL_ID));
    }

}