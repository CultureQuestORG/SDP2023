package ch.epfl.culturequest.ui.events.tournaments;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.Map;

import ch.epfl.culturequest.backend.tournament.tournamentobjects.ArtQuiz;
import ch.epfl.culturequest.backend.tournament.tournamentobjects.Tournament;
import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.social.SightseeingEvent;
import ch.epfl.culturequest.tournament.quiz.Question;
import ch.epfl.culturequest.tournament.quiz.Quiz;

public class TournamentViewModel extends ViewModel {

//    private final MutableLiveData<Tournament> tournament;
    private final MutableLiveData<Map<String, ArtQuiz>> quizzes = new MutableLiveData<>();

    public TournamentViewModel(Tournament tournament) {
        quizzes.setValue(tournament.getArtQuizzes());
    }

    public MutableLiveData<Map<String, ArtQuiz>> getQuizzes() {
        return quizzes;
    }

}
