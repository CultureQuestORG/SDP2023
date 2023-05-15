package ch.epfl.culturequest.social.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

import androidx.core.app.NotificationCompat;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.social.Profile;

/**
 * Class that represents notifications for a new offline scan
 */
public class ScanNotification extends AbstractNotification {
    public static final String CHANNEL_ID = "SCAN";

    /**
     * Constructor for the ScanNotification
     */
    public ScanNotification() {
        super(Profile.getActiveProfile().getUsername() + ", you have a new scan!",
                "We found a new offline scan result!",
                CHANNEL_ID);
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
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            return channel;
        }
        return null;
    }
}
