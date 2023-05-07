package ch.epfl.culturequest.social.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.social.Profile;

/**
 * Class that represents a notification for a new follower
 */
public final class FollowNotification implements Notification {

    private final String newFollower;
    private static final String CHANNEL_ID = "FOLLOW";

    /**
     * Constructor for the Follow Notification
     *
     * @param newFollower the username of the new follower
     */
    public FollowNotification(String newFollower) {
        this.newFollower = newFollower;
    }

    @Override
    public android.app.Notification get(Context context) {
        return new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo_compact)
                .setContentTitle(Profile.getActiveProfile().getName() + ", you have a new follower!")
                .setContentText(newFollower + " is now following you!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT).build();
    }

    /**
     * Returns the notification channel
     *
     * @return the notification channel
     */
    public static NotificationChannel getNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "FollowNotification";
            String description = "FollowNotification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            return channel;
        }
        return null;
    }
}
