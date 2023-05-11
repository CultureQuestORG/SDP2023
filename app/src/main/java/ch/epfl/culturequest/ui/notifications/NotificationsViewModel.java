package ch.epfl.culturequest.ui.notifications;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class NotificationsViewModel extends ViewModel {
    private final MutableLiveData<List<String>> notificationTexts;

    public NotificationsViewModel() {
        notificationTexts = new MutableLiveData<>();
        notificationTexts.setValue(List.of("Notification 1", "Notification 2", "Notification 3"));
    }

    public LiveData<List<String>> getNotificationTexts() {
        return notificationTexts;
    }
}
