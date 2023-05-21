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
public class ScanNotificationTest {
    private final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

    @Before
    public void setup() {
        PushNotification.createNotificationChannels(context);
    }

    @Test
    public void ScanNotificationIsCorrectlyCreated() {
        Notification scanNotification = new ScanNotification().buildNotification(context);
        assertThat(scanNotification.extras.get(Notification.EXTRA_TITLE).toString(), is("You have a new scan!"));
        assertThat(scanNotification.extras.get(Notification.EXTRA_TEXT).toString(), is("We found a new offline scan result!"));
        assertThat(scanNotification.priority, is(Notification.PRIORITY_HIGH));
        assertThat(scanNotification.getSmallIcon().getResId(), is(R.drawable.logo_compact));
        assertThat(scanNotification.getChannelId(), is(ScanNotification.CHANNEL_ID));
        Intent intent = new Intent(context, NavigationActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        assertThat(scanNotification.contentIntent, is(PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)));
    }

}