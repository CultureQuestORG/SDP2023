package ch.epfl.culturequest.notifications;

import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class PushNotificationTest {

    private static final PushNotification notification = new PushNotification("title", "text", "channelId");
    private static final PushNotification emptyNotification = new PushNotification();

    @Test
    public void testGetNotificationId() {
        assertThat(emptyNotification.getNotificationId(), is(""));
    }

    @Test
    public void testGetChannelId() {
        assertThat(notification.getChannelId(), is("channelId"));
    }

    @Test
    public void testGetTitle() {
        assertThat(notification.getTitle(), is("title"));
    }

    @Test
    public void testGetText() {
        assertThat(notification.getText(), is("text"));
    }

    @Test
    public void testGetTime() {
        assertThat(emptyNotification.getTime(), is(0L));
    }

    @Test
    public void testSetNotificationId() {
        notification.setNotificationId("notificationId");
        assertThat(notification.getNotificationId(), is("notificationId"));
    }

    @Test
    public void testSetChannelId() {
        emptyNotification.setChannelId("channelId");
        assertThat(emptyNotification.getChannelId(), is("channelId"));
    }

    @Test
    public void testSetTitle() {
        emptyNotification.setTitle("title");
        assertThat(emptyNotification.getTitle(), is("title"));
    }

    @Test
    public void testSetText() {
        emptyNotification.setText("text");
        assertThat(emptyNotification.getText(), is("text"));
    }

    @Test
    public void testSetTime() {
        notification.setTime(1);
        assertThat(notification.getTime(), is(1L));
    }
}
