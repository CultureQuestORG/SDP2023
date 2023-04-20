package ch.epfl.culturequest.social.notifications;

import android.content.Context;

import androidx.core.app.NotificationCompat;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.social.Profile;

/**
 * Class that represents a notification for a new like
 */
public class LikeNotification implements Notification {
    private final String liker;

    /**
     * Constructor for the Like Notification
     * @param liker the username of the liker
     */
    public LikeNotification(String liker) {
        this.liker = liker;
    }

    @Override
    public android.app.Notification get(Context context) {
        return new NotificationCompat.Builder(context, context.getString(R.string.likeNotifChannelID))
                .setSmallIcon(R.drawable.logo_compact)
                .setContentTitle(Profile.getActiveProfile().getName() + ", you have a new like!")
                .setContentText(liker + " liked your post!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT).build();
    }
}
