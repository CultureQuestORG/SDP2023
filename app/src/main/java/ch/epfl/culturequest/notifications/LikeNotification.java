package ch.epfl.culturequest.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import ch.epfl.culturequest.social.Profile;

/**
 * Class that represents a notification for a new like
 */
public class LikeNotification extends PushNotification {
    public static final String CHANNEL_ID = "LIKE";

    /**
     * Constructor for the LikeNotification
     *
     * @param liker the username of the liker
     */
    public LikeNotification(String liker) {
        super(Profile.getActiveProfile().getUsername() + ", you have a new like!",
                liker + " liked your post!",
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
}
