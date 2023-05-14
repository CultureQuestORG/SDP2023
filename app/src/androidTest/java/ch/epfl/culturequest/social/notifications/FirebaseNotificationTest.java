package ch.epfl.culturequest.social.notifications;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.culturequest.social.Profile;

@RunWith(AndroidJUnit4.class)
public class FirebaseNotificationTest {

    @Test
    public void getTokenIsSuccessful() {
        assertThat(FirebaseNotification.getDeviceToken().join(), is("test"));
    }
}
