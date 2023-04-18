package ch.epfl.culturequest.social.notifications;

import android.content.Context;

import androidx.core.app.NotificationCompat;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.social.Profile;

/**
 * Class that represents a notification for a new follower
 */
public final class FollowNotification implements Notification {

    private final String newFollower;

    /**
     * Constructor for the Follow Notification
     * @param newFollower the username of the new follower
     */
    public FollowNotification(String newFollower) {
        this.newFollower = newFollower;
    }

    @Override
    public android.app.Notification get(Context context) {
        return new NotificationCompat.Builder(context,  context.getString(R.string.followNotifChannelID))
                .setSmallIcon(R.drawable.logo_compact)
                .setContentTitle(Profile.getActiveProfile().getName() + ", you have a new follower!")
                .setContentText(newFollower + " is now following you!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT).build();
    }
}
