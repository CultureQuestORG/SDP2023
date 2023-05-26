package ch.epfl.culturequest.ui.events.tournaments;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import ch.epfl.culturequest.backend.tournament.tournamentobjects.ArtQuiz;
import ch.epfl.culturequest.backend.tournament.tournamentobjects.Tournament;
import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.social.Profile;

public class TournamentViewModel extends ViewModel {

//    private final MutableLiveData<Tournament> tournament;
    private final MutableLiveData<Map<String, ArtQuiz>> quizzes = new MutableLiveData<>();
    private final MutableLiveData<String> tournament = new MutableLiveData<>();

    private final MutableLiveData<List<Map.Entry<Profile, Integer>>> tournamentLeaderboard = new MutableLiveData<>();

    public TournamentViewModel(Tournament tournament) {
        quizzes.setValue(tournament.getArtQuizzes());
        this.tournament.setValue(tournament.getTournamentId());

        Database.getLeaderboard(tournament).whenComplete((leaderboard, throwable) -> {
            if (throwable != null) {
                System.out.printf("Error getting leaderboard: %s%n", throwable.getMessage());
                throwable.printStackTrace();
            } else {
                List<Map.Entry<Profile, Integer>> leaderboardList = new ArrayList<>(leaderboard.entrySet());
                leaderboardList.sort((o1, o2) -> o2.getValue() - o1.getValue());
                System.out.printf("Leaderboard: %s%n", leaderboardList);
                tournamentLeaderboard.postValue(leaderboardList);
            }
        });
    }

    public MutableLiveData<Map<String, ArtQuiz>> getQuizzes() {
        return quizzes;
    }

    public MutableLiveData<String> getTournament() {
        return tournament;
    }

    public MutableLiveData<List<Map.Entry<Profile, Integer>>> getTournamentLeaderboard() {
        return tournamentLeaderboard;
    }

}
