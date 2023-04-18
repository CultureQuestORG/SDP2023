package ch.epfl.culturequest.social.notifications;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import android.app.Notification;
import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.social.Profile;


public class FollowNotificationTest {

    // Tests that the like notification is correctly created
    @Test
    public void testFollowNotification() {
        Profile.setActiveProfile(new Profile("test", "Followee", "test", "test", "test", "test", List.of(), new ArrayList<>(),0));
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

        android.app.Notification followNotification = new FollowNotification("Follower").get(context);
        assertThat(followNotification.extras.get(Notification.EXTRA_TITLE).toString(), is("Followee, you have a new follower!"));
        assertThat(followNotification.extras.get(Notification.EXTRA_TEXT).toString(), is("Follower is now following you!"));
        assertThat(followNotification.priority, is(Notification.PRIORITY_DEFAULT));
        assertThat(followNotification.getSmallIcon().getResId(), is(R.drawable.logo_compact));
        assertThat(followNotification.getChannelId(), is(context.getString(R.string.followNotifChannelID)));

        Profile.setActiveProfile(null);
    }

}