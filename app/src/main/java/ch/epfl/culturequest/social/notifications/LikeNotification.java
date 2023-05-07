package ch.epfl.culturequest.social.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.social.Profile;

/**
 * Class that represents a notification for a new like
 */
public class LikeNotification implements Notification {
    private final String liker;

    private static final String CHANNEL_ID = "LIKE";

    /**
     * Constructor for the Like Notification
     * @param liker the username of the liker
     */
    public LikeNotification(String liker) {
        this.liker = liker;
    }

    @Override
    public android.app.Notification get(Context context) {
        return new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo_compact)
                .setContentTitle(Profile.getActiveProfile().getName() + ", you have a new like!")
                .setContentText(liker + " liked your post!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT).build();
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
