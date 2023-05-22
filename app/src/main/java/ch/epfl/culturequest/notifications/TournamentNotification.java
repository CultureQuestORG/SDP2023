package ch.epfl.culturequest.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;

/**
 * Class that represents a notification for a new tournament. It will be created on each
 * phone of the users that are in the tournament by using a service, so as it is the phone of the
 * user that will create and send it to the user itself, without using CLoud Messaging,
 * the senderId is not needed.
 */
public class TournamentNotification extends PushNotification {
    public static final String CHANNEL_ID = "TOURNAMENT";

    /**
     * Constructor for the TournamentNotification
     */
    public TournamentNotification() {
        super("A new tournament has started!",
                "Click here to see your new tournament!",
                CHANNEL_ID, "");
    }

    /**
     * Returns the notification channel
     *
     * @return the notification channel
     */
    public static NotificationChannel getNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name = "TournamentNotification";
            String description = "TournamentNotification";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            return channel;
        }
        return null;
    }
}
