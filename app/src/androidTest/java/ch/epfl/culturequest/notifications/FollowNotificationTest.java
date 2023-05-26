package ch.epfl.culturequest.notifications;

import static org.hamcrest.MatcherAssert.assertThat;
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

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.ui.profile.DisplayUserProfileActivity;


@RunWith(AndroidJUnit4.class)
public class FollowNotificationTest {
    private final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    private final Profile profile = new Profile("test", "test", "Follower", "test", "test", "test", 0, new HashMap<>(), new ArrayList<>());

    @Before
    public void setup() {
        PushNotification.createNotificationChannels(context);
        Profile.setActiveProfile(profile);
    }

    @Test
    public void FollowNotificationIsCorrectlyCreated() {
        PushNotification notification = new FollowNotification("Followee");
        Notification followNotification = notification.buildNotification(context);
        assertThat(followNotification.extras.get(Notification.EXTRA_TITLE).toString(), is("Followee, you have a new follower!"));
        assertThat(followNotification.extras.get(Notification.EXTRA_TEXT).toString(), is(profile.getUsername() + " is now following you!"));
        assertThat(followNotification.priority, is(Notification.PRIORITY_HIGH));
        assertThat(followNotification.getSmallIcon().getResId(), is(R.drawable.logo_compact));
        assertThat(followNotification.getChannelId(), is(FollowNotification.CHANNEL_ID));
        Intent intent = new Intent(context, DisplayUserProfileActivity.class)
                .putExtra("uid", profile.getUid()).putExtra("redirect", "home");
        assertThat(followNotification.contentIntent, is(PendingIntent.getActivity(context, notification.getNotificationId().hashCode(), intent, PendingIntent.FLAG_MUTABLE)));
    }

}