package ch.epfl.culturequest.social.notifications;

import android.content.Context;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.social.Profile;

public final class FollowNotification implements Notification {
    private final String newFollower;

    public FollowNotification(String newFollower) {
        this.newFollower = newFollower;
    }

    @Override
    public void sendNotification(Context context) {
        android.app.Notification not = new NotificationCompat.Builder(context,  context.getString(R.string.followNotifChannelID))
                .setSmallIcon(R.drawable.logo_compact)
                .setContentTitle(Profile.getActiveProfile().getName() + ", you have a new follower!")
                .setContentText(newFollower + " is now following you!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT).build();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(not.hashCode(), not);
    }
}
