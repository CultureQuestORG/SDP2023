package ch.epfl.culturequest.ui.notifications;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.stream.Collectors;

import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.notifications.PushNotification;
import ch.epfl.culturequest.social.Profile;

public class NotificationsViewModel extends ViewModel {
    private final MutableLiveData<List<PushNotification>> notificationTexts;

    public NotificationsViewModel() {
        notificationTexts = new MutableLiveData<>();
        Database.getNotifications(Profile.getActiveProfile().getUid()).whenComplete((notifications, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
                return;
            }
            notificationTexts.setValue(notifications);
        });

        notificationTexts.observeForever(notifications -> {
            if (notifications == null) {
                return;
            }

            List<PushNotification> oldNotifications = notificationTexts.getValue()
                    .stream()
                    .filter(notification -> notification.getTime() < System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 7)
                    .collect(Collectors.toList());

            if(oldNotifications.isEmpty()) {
                return;
            }

            oldNotifications.forEach(notification -> Database.deleteNotification(Profile.getActiveProfile().getUid(), notification));
            notificationTexts.setValue(notifications.stream().filter(notification -> !oldNotifications.contains(notification)).collect(Collectors.toList()));
        });
    }

    public LiveData<List<PushNotification>> getNotificationTexts() {
        return notificationTexts;
    }
}
