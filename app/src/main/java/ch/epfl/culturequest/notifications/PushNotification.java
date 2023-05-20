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
import androidx.navigation.NavDeepLinkBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import ch.epfl.culturequest.NavigationActivity;
import ch.epfl.culturequest.R;
import ch.epfl.culturequest.ui.profile.DisplayUserProfileActivity;

/**
 * Super class that represents notifications stored in the database and sent by cloud messaging.
 */
public class PushNotification {
    private String notificationId;
    private String channelId;
    private String title;
    private String text;
    private long time;
    private String senderId;

    /**
     * Empty constructor used by Firebase
     */
    public PushNotification() {
        this.title = "";
        this.text = "";
        this.channelId = "";
        this.notificationId = "";
        this.time = 0;
        this.senderId = "";
    }

    /**
     * Constructor
     *
     * @param title     the title of the notification
     * @param text      the text of the notification
     * @param channelId the channel id of the notification
     */
    public PushNotification(String title, String text, String channelId, String senderId) {
        this.title = title;
        this.text = text;
        this.channelId = channelId;
        this.notificationId = UUID.randomUUID().toString();
        this.time = System.currentTimeMillis();
        this.senderId = senderId;
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
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_ALL)
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
        Intent intent;
        switch (channelId) {
            case FollowNotification.CHANNEL_ID:
                intent = new Intent(context, DisplayUserProfileActivity.class);
                intent.putExtra("uid", senderId);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
            case LikeNotification.CHANNEL_ID:
                return new NavDeepLinkBuilder(context).setGraph(R.navigation.mobile_navigation)
                        .setDestination(R.id.navigation_profile).createPendingIntent();
            // case CompetitionNotification.CHANNEL_ID:
                // TODO: open the competition activity
            // case SightseeingNotification.CHANNEL_ID:
                //TODO: open the sightseeing activity
            default:
                intent = new Intent(context, NavigationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

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

    public String getSenderId() {
        return senderId;
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

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof PushNotification && ((PushNotification) o).getNotificationId().equals(notificationId);
    }
}
