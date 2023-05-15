package ch.epfl.culturequest.notifications;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.culturequest.notifications.FireMessaging;

@RunWith(AndroidJUnit4.class)
public class FireMessagingTest {

    @Test
    public void getTokenIsSuccessful() {
        assertThat(FireMessaging.getDeviceToken().join(), is("test"));
    }
}
