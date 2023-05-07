package ch.epfl.culturequest.social.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

import androidx.core.app.NotificationCompat;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.social.Profile;

/**
 * Class that represents notifications for a new offline scan
 */
public class ScanNotification implements Notification {

    private static final String CHANNEL_ID = "SCAN";

    @Override
    public android.app.Notification get(Context context) {
        return new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo_compact)
                .setContentTitle(Profile.getActiveProfile().getName() + ", you have a new scan!")
                .setContentText("We found a new offline scan result!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT).build();
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
