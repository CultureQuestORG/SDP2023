package ch.epfl.culturequest.social.notifications;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Interface that represents notifications
 */
public interface NotificationInterface {

    /**
     * Creates the notification channels. This method can be called multiple times, it will only
     * create the channels if they don't already exist. It should be called as soon as possible.
     *
     * @param context the context of the notification
     */
    static void createNotificationChannels(Context context) {
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
    Notification get(Context context);
}
