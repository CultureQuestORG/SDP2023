package ch.epfl.culturequest.social.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

import androidx.core.app.NotificationCompat;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.social.Profile;

public class CompetitionNotification implements NotificationInterface {
    public static final String CHANNEL_ID = "COMPETITION";

    @Override
    public Notification get(Context context) {
        return new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo_compact)
                .setContentTitle(Profile.getActiveProfile().getUsername() + ", you have a new competition!")
                .setContentText("Click here to see your new competition!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT).build();
    }

    /**
     * Returns the notification channel
     *
     * @return the notification channel
     */
    public static NotificationChannel getNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name = "CompetitionNotification";
            String description = "CompetitionNotification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            return channel;
        }
        return null;
    }

}
