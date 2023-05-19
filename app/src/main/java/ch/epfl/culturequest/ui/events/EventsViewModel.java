package ch.epfl.culturequest.ui.events;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.social.SightseeingEvent;

public class EventsViewModel extends ViewModel {

    private final MutableLiveData<List<SightseeingEvent>> sightseeingEvents;
    private final MutableLiveData<List<String>> tournamentsEvents;

    public EventsViewModel() {
        sightseeingEvents = new MutableLiveData<>();
        tournamentsEvents = new MutableLiveData<>();
        refreshSightseeingEvents();
        refreshTournamentsEvents();
    }

    public MutableLiveData<List<SightseeingEvent>> getSightseeingEvents() {
        return sightseeingEvents;
    }

    public void refreshSightseeingEvents() {
        Database.getSightseeingEvents(Profile.getActiveProfile().getUid()).whenComplete((events, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
                return;
            }
            System.out.println(events);
            sightseeingEvents.setValue(events);
        });
    }

    public MutableLiveData<List<String>> getTournamentsEvents() {
        return tournamentsEvents;
    }

    public void refreshTournamentsEvents() {
//        Database.getTournamentsEvents(Profile.getActiveProfile().getUid()).whenComplete((events, throwable) -> {
//            if (throwable != null) {
//                throwable.printStackTrace();
//                return;
//            }
//            tournamentsEvents.setValue(events);
//        });
        tournamentsEvents.setValue(List.of("Tournament 1", "Tournament 2", "Tournament 3"));
    }

}
