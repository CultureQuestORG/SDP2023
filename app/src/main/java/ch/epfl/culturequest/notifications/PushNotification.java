package ch.epfl.culturequest.notifications;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import ch.epfl.culturequest.NavigationActivity;
import ch.epfl.culturequest.R;

/**
 * Super class that represents notifications stored in the database and sent by cloud messaging.
 */
public class PushNotification {
    private String notificationId;
    private String channelId;
    private String title;
    private String text;
    private long time;

    /**
     * Empty constructor used by Firebase
     */
    public PushNotification() {
        this.title = "";
        this.text = "";
        this.channelId = "";
        this.notificationId = "";
        this.time = 0;
    }

    /**
     * Constructor
     *
     * @param title     the title of the notification
     * @param text      the text of the notification
     * @param channelId the channel id of the notification
     */
    public PushNotification(String title, String text, String channelId) {
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
    public Notification buildNotification(Context context) {
        PendingIntent pendingIntent = selectPendingIntent(context, channelId);
        return new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.logo_compact)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .build();
    }

    /**
     * Returns the pending intent associated with the notification
     *
     * @param context   the context of the notification
     * @param channelId the channel id of the notification
     * @return the pending intent
     */
    public PendingIntent selectPendingIntent(Context context, String channelId) {
        Intent default_intent = new Intent(context, NavigationActivity.class);
        default_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent default_pending_intent = PendingIntent.getActivity(context, 0, default_intent, PendingIntent.FLAG_IMMUTABLE);

        switch (channelId) {
            case FollowNotification.CHANNEL_ID:
                return FollowNotification.getPendingIntent(context);
            case ScanNotification.CHANNEL_ID:
                return ScanNotification.getPendingIntent(context);
            case LikeNotification.CHANNEL_ID:
                return LikeNotification.getPendingIntent(context);
            case CompetitionNotification.CHANNEL_ID:
                return CompetitionNotification.getPendingIntent(context);
            case SightseeingNotification.CHANNEL_ID:
                return SightseeingNotification.getPendingIntent(context);
            default:
                return default_pending_intent;
        }
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

    @Override
    public boolean equals(Object o) {
        return o instanceof PushNotification && ((PushNotification) o).getNotificationId().equals(notificationId);
    }
}
