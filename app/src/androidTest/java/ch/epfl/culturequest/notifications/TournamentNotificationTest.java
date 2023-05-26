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

import ch.epfl.culturequest.NavigationActivity;
import ch.epfl.culturequest.R;
import ch.epfl.culturequest.ui.events.EventsActivity;

@RunWith(AndroidJUnit4.class)
public class TournamentNotificationTest {
    private final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

    @Before
    public void setup() {
        PushNotification.createNotificationChannels(context);
    }

    @Test
    public void testTournamentNotification() {
        PushNotification notification = new TournamentNotification();
        Notification tournamentNotification = notification.buildNotification(context);
        assertThat(tournamentNotification.extras.get(Notification.EXTRA_TITLE).toString(), is("A new tournament has started!"));
        assertThat(tournamentNotification.extras.get(Notification.EXTRA_TEXT).toString(), is("Click here to see your new tournament!"));
        assertThat(tournamentNotification.priority, is(Notification.PRIORITY_HIGH));
        assertThat(tournamentNotification.getSmallIcon().getResId(), is(R.drawable.logo_compact));
        assertThat(tournamentNotification.getChannelId(), is(TournamentNotification.CHANNEL_ID));
        Intent intent = new Intent(context, EventsActivity.class);
        assertThat(tournamentNotification.contentIntent, is(PendingIntent.getActivity(context, notification.getNotificationId().hashCode(), intent, PendingIntent.FLAG_MUTABLE)));
    }

}
