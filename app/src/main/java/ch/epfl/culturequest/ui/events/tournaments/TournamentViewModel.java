package ch.epfl.culturequest.ui.events.tournaments;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.social.SightseeingEvent;
import ch.epfl.culturequest.tournament.quiz.Question;
import ch.epfl.culturequest.tournament.quiz.Quiz;

public class TournamentViewModel extends ViewModel {

//    private final MutableLiveData<Tournament> tournament;
    private final MutableLiveData<List<Quiz>> quizzes = new MutableLiveData<>();

    public TournamentViewModel(String tournamentId) {
        Quiz quiz = new Quiz("Joconde", List.of(new Question("Quel est le nom de cette oeuvre ?", List.of("La Joconde", "La Joconde", "La Joconde", "La Joconde"), 0)), tournamentId);
        quizzes.setValue(List.of(quiz));
    }

    public MutableLiveData<List<Quiz>> getQuizzes() {
        return quizzes;
    }

}
