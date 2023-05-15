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

public class FirebaseNotificationService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        sendNotification(remoteMessage);
    }

    @Override
    public void onNewToken(@NonNull String token) {
        String currentUserUid = Authenticator.getCurrentUser().getUid();
        Database.getDeviceTokens(currentUserUid).whenComplete((deviceTokens, throwable) -> {
            if (throwable != null || deviceTokens.contains(token)) {
                return;
            }
            deviceTokens.add(token);
            Database.setDeviceTokens(currentUserUid, deviceTokens);
        });
    }

    private void sendNotification(RemoteMessage remoteMessage) {
        PushNotification pushNotification = new PushNotification(remoteMessage.getData().get("title"),
                remoteMessage.getData().get("text"), remoteMessage.getData().get("channelId"));

        Notification notification = pushNotification.buildNotification(this);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(pushNotification.getNotificationId().hashCode(), notification);
    }
}