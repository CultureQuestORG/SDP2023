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


public class LikeNotificationTest {

    // Tests that the like notification is correctly created
    @Test
    public void testLikeNotification() {
        Profile.setActiveProfile(new Profile("test", "Likee", "test", "test", "test", "test",0));
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

        android.app.Notification likeNotification = new LikeNotification("Liker").get(context);
        assertThat(likeNotification.extras.get(Notification.EXTRA_TITLE).toString(), is("Likee, you have a new like!"));
        assertThat(likeNotification.extras.get(Notification.EXTRA_TEXT).toString(), is("Liker liked your post!"));
        assertThat(likeNotification.priority, is(Notification.PRIORITY_DEFAULT));
        assertThat(likeNotification.getSmallIcon().getResId(), is(R.drawable.logo_compact));
        assertThat(likeNotification.getChannelId(), is(context.getString(R.string.likeNotifChannelID)));

        Profile.setActiveProfile(null);
    }

}
