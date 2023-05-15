package ch.epfl.culturequest.social.notifications;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import ch.epfl.culturequest.R;

/**
 * Interface that represents notifications
 */
public abstract class AbstractNotification {
    private String notificationId;
    private String channelId;
    private String title;
    private String text;
    private long time;

    public AbstractNotification() {
        this.title = "";
        this.text = "";
        this.channelId = "";
        this.notificationId = "";
        this.time = 0;
    }

    public AbstractNotification(String title, String text, String channelId) {
        this.title = title;
        this.text = text;
        this.channelId = channelId;
        this.notificationId = UUID.randomUUID().toString();
        this.time = System.currentTimeMillis();
    }

    /**
     * Creates the notification channels. This method can be called multiple times, it will only
     * create the channels if they don't already exist. It should be called as soon as possible.
     *
     * @param context the context of the notification
     */
    public static void createNotificationChannels(Context context) {
        // Create the NotificationChannels, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // retrieve a list of all notifications channels
            List<NotificationChannel> channels = new ArrayList<>();
            channels.add(FollowNotification.getNotificationChannel());
            channels.add(ScanNotification.getNotificationChannel());
            channels.add(LikeNotification.getNotificationChannel());
            channels.add(CompetitionNotification.getNotificationChannel());
            channels.add(SightseeingNotification.getNotificationChannel());

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(context, NotificationManager.class);
            Objects.requireNonNull(notificationManager).createNotificationChannels(channels);
        }
    }

    /**
     * Returns the notification ready to be sent
     *
     * @param context the context of the notification
     * @return the notification
     */
    public Notification buildNotification(Context context){
        return new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.logo_compact)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT).build();
    }

    public String getNotificationId() {
        return notificationId;
    }

    public String getChannelId() {
        return channelId;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public long getTime() {
        return time;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
