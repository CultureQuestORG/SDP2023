package ch.epfl.culturequest.backend.tournament.tournamentobjects;


// represents a single question in a quiz, with four possible answers and a correct answer

import java.util.ArrayList;

public class QuizQuestion {

    private ArrayList<String> possibleAnswers;

    private String questionContent;
    private int correctAnswerIndex;

    public QuizQuestion(String questionContent, ArrayList<String> possibleAnswers, int correctAnswerIndex){
        this.possibleAnswers = possibleAnswers;
        this.correctAnswerIndex = correctAnswerIndex;
        this.questionContent = questionContent;
    }

    public QuizQuestion(){
        // For Serialization
    }

    public ArrayList<String> getPossibleAnswers(){
        return possibleAnswers;
    }

    public int getCorrectAnswerIndex(){
        return correctAnswerIndex;
    }

    public String getCorrectAnswer(){
        return possibleAnswers.get(correctAnswerIndex);
    }

    public String getQuestionContent(){
        return questionContent;
    }

}
