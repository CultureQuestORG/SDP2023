package ch.epfl.culturequest.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import ch.epfl.culturequest.NavigationActivity;
import ch.epfl.culturequest.social.Profile;

public class SightseeingNotification extends PushNotification {
    public static final String CHANNEL_ID = "SIGHTSEEING";

    /**
     * Constructor for the SightseeingNotification
     *
     * @param friend the friend that invites to a new sightseeing
     */
    public SightseeingNotification(String friend) {
        super(friend + ", you have a new sightseeing event!",
                Profile.getActiveProfile().getUsername() + " invited you to a new sightseeing event!",
                CHANNEL_ID, Profile.getActiveProfile().getUid());
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
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            return channel;
        }
        return null;
    }
}
