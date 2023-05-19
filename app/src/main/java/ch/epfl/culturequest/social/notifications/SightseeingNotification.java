package ch.epfl.culturequest.social.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

import androidx.core.app.NotificationCompat;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.social.Profile;

public class SightseeingNotification implements NotificationInterface {
    private final String friend;
    public static final String CHANNEL_ID = "SIGHTSEEING";

    /**
     * Constructor for the SightseeingNotification
     *
     * @param friend the friend that invites to a new sightseeing
     */
    public SightseeingNotification(String friend) {
        this.friend = friend;
    }

    @Override
    public Notification get(Context context) {
        return new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo_compact)
                .setContentTitle(Profile.getActiveProfile().getUsername() + ", you have a new sightseeing event!")
                .setContentText(friend + " invited you to a new sightseeing event!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT).build();
    }

    /**
     * Returns the notification channel
     *
     * @return the notification channel
     */
    public static NotificationChannel getNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name = "SightseeingNotification";
            String description = "SightseeingNotification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            return channel;
        }
        return null;
    }
}