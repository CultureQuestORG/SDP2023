package ch.epfl.culturequest.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;

import ch.epfl.culturequest.social.Profile;

public class CompetitionNotification extends PushNotification {
    public static final String CHANNEL_ID = "COMPETITION";

    public CompetitionNotification() {
        super(Profile.getActiveProfile().getUsername() + ", you have a new competition!",
                "Click here to see your new competition!",
                CHANNEL_ID);
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
