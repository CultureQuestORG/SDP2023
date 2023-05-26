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

import ch.epfl.culturequest.NavigationActivity;
import ch.epfl.culturequest.R;
import ch.epfl.culturequest.social.Profile;


@RunWith(AndroidJUnit4.class)
public class LikeNotificationTest {
    private final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    private final Profile profile = new Profile("test", "test", "Liker", "test", "test", "test", 0, new HashMap<>(), new ArrayList<>());

    @Before
    public void setup() {
        PushNotification.createNotificationChannels(context);
        Profile.setActiveProfile(profile);
    }

    @Test
    public void likeNotificationIsCorrectlyCreated() {
        PushNotification notification = new LikeNotification("Likee");
        Notification likeNotification = notification.buildNotification(context);
        assertThat(likeNotification.extras.get(Notification.EXTRA_TITLE).toString(), is("Likee, you have a new like!"));
        assertThat(likeNotification.extras.get(Notification.EXTRA_TEXT).toString(), is(profile.getUsername() + " liked your post!"));
        assertThat(likeNotification.priority, is(Notification.PRIORITY_HIGH));
        assertThat(likeNotification.getSmallIcon().getResId(), is(R.drawable.logo_compact));
        assertThat(likeNotification.getChannelId(), is(LikeNotification.CHANNEL_ID));
        Intent intent = new Intent(context, NavigationActivity.class)
                .putExtra("redirect", "profile");
        assertThat(likeNotification.contentIntent, is(PendingIntent.getActivity(context, notification.getNotificationId().hashCode(), intent, PendingIntent.FLAG_MUTABLE)));
    }

}
