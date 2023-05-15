package ch.epfl.culturequest.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import ch.epfl.culturequest.NavigationActivity;
import ch.epfl.culturequest.social.Profile;

public class CompetitionNotification extends PushNotification {
    public static final String CHANNEL_ID = "COMPETITION";

    /**
     * Constructor for the CompetitionNotification
     */
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

    /**
     * Returns the pending intent for the notification
     *
     * @param context the context of the notification
     * @return the pending intent for the notification
     */
    public static PendingIntent getPendingIntent(Context context) {
        Intent intent = new Intent(context, NavigationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return  PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
    }

}
