package ch.epfl.culturequest.social.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;

import ch.epfl.culturequest.social.Profile;

public class SightseeingNotification extends AbstractNotification {
    public static final String CHANNEL_ID = "SIGHTSEEING";

    /**
     * Constructor for the SightseeingNotification
     *
     * @param friend the friend that invites to a new sightseeing
     */
    public SightseeingNotification(String friend) {
        super(Profile.getActiveProfile().getUsername() + ", you have a new sightseeing event!",
                friend + " invited you to a new sightseeing event!",
                CHANNEL_ID);
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
