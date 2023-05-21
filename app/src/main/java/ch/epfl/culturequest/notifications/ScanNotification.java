package ch.epfl.culturequest.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;

/**
 * Class that represents notifications for a new offline scan. It will be created on each
 * where there is a new scan available by using a service, so as it is the phone of the
 * user that will create and send it to the user itself, without using CLoud Messaging,
 * the senderId is not needed.
 */
public class ScanNotification extends PushNotification {
    public static final String CHANNEL_ID = "SCAN";

    /**
     * Constructor for the ScanNotification
     */
    public ScanNotification() {
        super("You have a new scan!",
                "We found a new offline scan result!",
                CHANNEL_ID, "");
    }

    /**
     * Returns the notification channel
     *
     * @return the notification channel
     */
    public static NotificationChannel getNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name = "ScanNotification";
            String description = "ScanNotification";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            return channel;
        }
        return null;
    }
}
