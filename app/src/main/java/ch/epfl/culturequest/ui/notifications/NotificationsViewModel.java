package ch.epfl.culturequest.ui.notifications;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.notifications.PushNotification;
import ch.epfl.culturequest.social.Profile;

public class NotificationsViewModel extends ViewModel {
    private final MutableLiveData<List<PushNotification>> notificationTexts;

    public NotificationsViewModel() {
        notificationTexts = new MutableLiveData<>();
        String currentUserUid = Profile.getActiveProfile().getUid();
        Database.getNotifications(currentUserUid).whenComplete((notifications, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
                return;
            }
            notificationTexts.setValue(notifications);
        });
    }

    public LiveData<List<PushNotification>> getNotificationTexts() {
        return notificationTexts;
    }
}
