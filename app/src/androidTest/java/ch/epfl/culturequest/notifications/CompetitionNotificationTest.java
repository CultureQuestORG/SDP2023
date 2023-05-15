package ch.epfl.culturequest.notifications;

import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
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
public class CompetitionNotificationTest {
    private final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    private final Profile profile = new Profile("test", "test", "Competitor", "test", "test", "test", 0, new HashMap<>(), new ArrayList<>());

    @Before
    public void setup() {
        PushNotification.createNotificationChannels(context);
        Profile.setActiveProfile(profile);
    }

    @Test
    public void testCompetitionNotification() {
        Notification competitionNotification = new CompetitionNotification().buildNotification(context);
        assertThat(competitionNotification.extras.get(Notification.EXTRA_TITLE).toString(), is(profile.getUsername() + ", you have a new competition!"));
        assertThat(competitionNotification.extras.get(Notification.EXTRA_TEXT).toString(), is("Click here to see your new competition!"));
        assertThat(competitionNotification.priority, is(Notification.PRIORITY_DEFAULT));
        assertThat(competitionNotification.getSmallIcon().getResId(), is(R.drawable.logo_compact));
        assertThat(competitionNotification.getChannelId(), is(CompetitionNotification.CHANNEL_ID));
    }

}
