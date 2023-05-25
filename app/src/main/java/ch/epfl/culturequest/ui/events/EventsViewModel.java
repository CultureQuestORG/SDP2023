package ch.epfl.culturequest.ui.events;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import ch.epfl.culturequest.backend.tournament.apis.TournamentManagerApi;
import ch.epfl.culturequest.backend.tournament.tournamentobjects.Tournament;
import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.social.SightseeingEvent;

public class EventsViewModel extends ViewModel {

    private final MutableLiveData<List<SightseeingEvent>> sightseeingEvents;
    private final MutableLiveData<List<Tournament>> tournamentsEvents;

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

    public MutableLiveData<List<Tournament>> getTournamentsEvents() {
        return tournamentsEvents;
    }

    public void refreshTournamentsEvents() {
        Tournament tournament = TournamentManagerApi.getTournamentFromSharedPref();

        if(tournament == null) {
            return;
        }

        tournamentsEvents.setValue(List.of(tournament));
    }

}
