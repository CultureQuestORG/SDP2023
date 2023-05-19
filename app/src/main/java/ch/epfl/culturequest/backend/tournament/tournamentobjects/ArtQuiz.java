package ch.epfl.culturequest.backend.tournament.tournamentobjects;


import java.util.ArrayList;

// Represents a full quiz composed of several questions
public class ArtQuiz {

    ArrayList<QuizQuestion> questions;


    public ArtQuiz(){
        // For Serialization
    }

    public ArtQuiz(ArrayList<QuizQuestion> questions){
        this.questions = questions;
    }

    public ArrayList<QuizQuestion> getQuestions(){
        return questions;
    }

}