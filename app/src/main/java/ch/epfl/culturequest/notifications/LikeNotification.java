package ch.epfl.culturequest.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.navigation.NavDeepLinkBuilder;

import ch.epfl.culturequest.NavigationActivity;
import ch.epfl.culturequest.R;
import ch.epfl.culturequest.notifications.PushNotification;
import ch.epfl.culturequest.social.Profile;

/**
 * Class that represents a notification for a new like
 */
public class LikeNotification extends PushNotification {
    public static final String CHANNEL_ID = "LIKE";

    /**
     * Constructor for the LikeNotification
     *
     * @param likee the username of the likee
     */
    public LikeNotification(String likee) {
        super(likee + ", you have a new like!",
                Profile.getActiveProfile().getUsername() + " liked your post!",
                CHANNEL_ID);
    }

    /**
     * Returns the notification channel
     *
     * @return the notification channel
     */
    public static NotificationChannel getNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "LikeNotification";
            String description = "LikeNotification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            return channel;
        }
        return null;
    }

    /**
     * Returns the pending intent for the notification
     *
     * @param context the context of the notification
     * @return the pending intent for the notification
     */
    public static PendingIntent getPendingIntent(Context context) {
        PendingIntent pendingIntent = new NavDeepLinkBuilder(context).setGraph(R.navigation.mobile_navigation).setDestination(R.id.navigation_profile).createPendingIntent();
        return pendingIntent;
    }
}
