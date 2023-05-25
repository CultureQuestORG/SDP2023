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
        artName = "";
        questions = null;
        scores = null;
    }

    public void setQuestions(ArrayList<QuizQuestion> questions) {
        this.questions = questions;
    }

    public void setScores(HashMap<String, Integer> scores) {
        this.scores = scores;
    }

    public void setArtName(String artName) {
        this.artName = artName;
    }

    public ArtQuiz(String artName, ArrayList<QuizQuestion> questions,HashMap<String, Integer> scores){
        this.questions = questions;
        this.artName = artName;
        this.scores = scores;

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