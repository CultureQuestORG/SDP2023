package ch.epfl.culturequest.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import ch.epfl.culturequest.NavigationActivity;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.ui.profile.DisplayUserProfileActivity;

/**
 * Class that represents a notification for a new follower
 */
public final class FollowNotification extends PushNotification {
    public static final String CHANNEL_ID = "FOLLOW";

    /**
     * Constructor for the FollowNotification
     *
     * @param newFollowee the username of the new follower
     */
    public FollowNotification(String newFollowee) {
        super(newFollowee + ", you have a new follower!",
                Profile.getActiveProfile().getUsername() + " is now following you!",
                CHANNEL_ID);
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

    /**
     * Returns the pending intent for the notification
     *
     * @param context the context of the notification
     * @return the pending intent for the notification
     */
    public static PendingIntent getPendingIntent(Context context) {
        Intent intent = new Intent(context, DisplayUserProfileActivity.class);
        intent.putExtra("uid", Profile.getActiveProfile().getUid());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return  PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
    }
}
