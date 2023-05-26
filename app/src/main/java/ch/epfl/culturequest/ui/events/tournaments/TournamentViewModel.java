package ch.epfl.culturequest.ui.events.tournaments;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Map;

import ch.epfl.culturequest.backend.tournament.tournamentobjects.ArtQuiz;
import ch.epfl.culturequest.backend.tournament.tournamentobjects.Tournament;

public class TournamentViewModel extends ViewModel {

//    private final MutableLiveData<Tournament> tournament;
    private final MutableLiveData<Map<String, ArtQuiz>> quizzes = new MutableLiveData<>();
    private final MutableLiveData<String> tournament = new MutableLiveData<>();

    public TournamentViewModel(Tournament tournament) {
        quizzes.setValue(tournament.getArtQuizzes());
        this.tournament.setValue(tournament.getTournamentId());
    }

    public MutableLiveData<Map<String, ArtQuiz>> getQuizzes() {
        return quizzes;
    }

    public MutableLiveData<String> getTournament() {
        return tournament;
    }

}
