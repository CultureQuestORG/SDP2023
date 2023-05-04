package ch.epfl.culturequest.social.notifications;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import android.app.Notification;
import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.social.Profile;


public class ScanNotificationTest {

    // Tests that the like notification is correctly created
    @Test
    public void testScanNotification() {
        Profile.setActiveProfile(new Profile("test", "Scanner", "test", "test", "test", "test",0,new HashMap<>()));
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

        android.app.Notification scanNotif = new ScanNotification().get(context);
        assertThat(scanNotif.extras.get(Notification.EXTRA_TITLE).toString(), is("Scanner, you have a new scan!"));
        assertThat(scanNotif.extras.get(Notification.EXTRA_TEXT).toString(), is("We found a new offline scan result!"));
        assertThat(scanNotif.priority, is(Notification.PRIORITY_DEFAULT));
        assertThat(scanNotif.getSmallIcon().getResId(), is(R.drawable.logo_compact));
        assertThat(scanNotif.getChannelId(), is(context.getString(R.string.scanNotifChannelID)));

        Profile.setActiveProfile(null);
    }

}