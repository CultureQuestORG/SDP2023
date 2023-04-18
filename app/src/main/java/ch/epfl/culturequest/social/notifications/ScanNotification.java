package ch.epfl.culturequest.social.notifications;

import android.content.Context;

import androidx.core.app.NotificationCompat;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.social.Profile;

/**
 * Class that represents a notification for a new offline scan
 */
public class ScanNotification implements Notification {

    @Override
    public android.app.Notification get(Context context) {
        return new NotificationCompat.Builder(context, context.getString(R.string.scanNotifChannelID))
                .setSmallIcon(R.drawable.logo_compact)
                .setContentTitle(Profile.getActiveProfile().getName() + ", you have a new scan!")
                .setContentText("We found a new offline scan result!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT).build();
    }
}
