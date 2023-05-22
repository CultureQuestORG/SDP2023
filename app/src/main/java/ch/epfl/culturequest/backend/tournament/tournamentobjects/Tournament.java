package ch.epfl.culturequest.backend.tournament.tournamentobjects;

import java.util.HashMap;
import java.util.Map;
import ch.epfl.culturequest.backend.tournament.utils.RandomApi;

public class Tournament {


    private String tournamentId;

    private Map<String, ArtQuiz> artQuizzes = new HashMap<String, ArtQuiz>();

    public Tournament(){
        // For Serialization
    }
    public Tournament(Map<String, ArtQuiz> artQuizzes){
        tournamentId = RandomApi.getWeeklyTournamentPseudoRandomUUID();
        this.artQuizzes = artQuizzes;
    }

    public String getTournamentId(){
        return tournamentId;
    }

    public Map<String, ArtQuiz> getArtQuizzes(){
        return artQuizzes;
    }

}
