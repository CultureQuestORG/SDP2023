package ch.epfl.culturequest.social.notifications;

import android.content.Context;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.social.Profile;

public class LikeNotification implements Notification {
    private final String liker;
    private final String postURL;

    public LikeNotification(String liker, String postURL) {
        this.liker = liker;
        this.postURL = postURL;
    }

    @Override
    public void sendNotification(Context context) {
        Database.getImage(postURL).thenAccept(bitmap -> {
            android.app.Notification not = new NotificationCompat.Builder(context, context.getString(R.string.likeNotifChannelID))
                    .setSmallIcon(R.drawable.logo_compact)
                    .setContentTitle(Profile.getActiveProfile().getName() + ", you have a new like!")
                    .setContentText(liker + " liked your post!")
                   /* TODO: Try to use this to add the nice image
                    .setLargeIcon(b)
                    .setStyle(new NotificationCompat.BigPictureStyle()
                            .bigPicture(myBitmap)
                            .bigLargeIcon(null))*/
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT).build();
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(not.hashCode(), not);
        });
    }
}
