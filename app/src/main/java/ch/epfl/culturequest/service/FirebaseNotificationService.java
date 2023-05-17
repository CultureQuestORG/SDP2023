package ch.epfl.culturequest.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import ch.epfl.culturequest.authentication.Authenticator;
import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.notifications.PushNotification;
import ch.epfl.culturequest.social.Profile;

/**
 * Service that handles the reception of notifications
 */
public class FirebaseNotificationService extends FirebaseMessagingService {

    /**
     * Called when a notification is received either when the app is in the foreground or in the
     * background
     *
     * @param remoteMessage the notification message received
     */
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        sendNotification(remoteMessage);
    }

    /**
     * Called when a new token is generated. It is used to store new the token in the database.
     *
     * @param token the new token
     */
    @Override
    public void onNewToken(@NonNull String token) {
        if (Authenticator.getCurrentUser() == null) {
            return;
        }
        String currentUserUid = Authenticator.getCurrentUser().getUid();
        Database.getDeviceTokens(currentUserUid).whenComplete((deviceTokens, throwable) -> {
            if (throwable != null || deviceTokens.contains(token)) {
                return;
            }
            deviceTokens.add(token);

            Profile activeProfile = Profile.getActiveProfile();
            if (activeProfile != null) {
                activeProfile.setDeviceTokens(deviceTokens);
            }
            Database.setDeviceTokens(currentUserUid, deviceTokens);
        });
    }

    /**
     * Sends a notification to the user by making a notification appear on the user's device
     *
     * @param remoteMessage the notification message received
     */
    private void sendNotification(RemoteMessage remoteMessage) {
        PushNotification pushNotification = new PushNotification(remoteMessage.getData().get("title"),
                remoteMessage.getData().get("text"), remoteMessage.getData().get("channelId"));

        Notification notification = pushNotification.buildNotification(this);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(pushNotification.getNotificationId().hashCode(), notification);
    }
}