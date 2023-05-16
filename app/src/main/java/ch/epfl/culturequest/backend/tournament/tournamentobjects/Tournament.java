package ch.epfl.culturequest.backend.tournament.tournamentobjects;

import java.util.HashMap;
import java.util.Map;
import ch.epfl.culturequest.backend.tournament.utils.RandomApi;

public class Tournament {


    private String tournamentId;

    private Map<String, ArtQuiz> artQuizzes = new HashMap<String, ArtQuiz>();

    public Tournament(Map<String, ArtQuiz> artQuizzes){

        tournamentId = RandomApi.getWeeklyTournamentPseudoRandomUUID();
    }

    public String getTournamentId(){
        return tournamentId;
    }

    public Map<String, ArtQuiz> getArtQuizzes(){
        return artQuizzes;
    }

}
