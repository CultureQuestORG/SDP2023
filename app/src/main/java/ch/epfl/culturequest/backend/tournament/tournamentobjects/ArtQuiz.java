package ch.epfl.culturequest.backend.tournament.tournamentobjects;


import java.util.ArrayList;
import java.util.HashMap;

// Represents a full quiz composed of several questions
public class ArtQuiz {

    ArrayList<QuizQuestion> questions;

    HashMap<String, Integer> scores = new HashMap<>();

    String artName;


    public ArtQuiz(){
        // For Serialization
    }

    public ArtQuiz(String artName,ArrayList<QuizQuestion> questions){
        this.questions = questions;
        this.artName = artName;
    }

    public ArrayList<QuizQuestion> getQuestions(){
        return questions;
    }

    public HashMap<String, Integer> getScores(){
        return scores;
    }

    public String getArtName(){
        return artName;
    }

}