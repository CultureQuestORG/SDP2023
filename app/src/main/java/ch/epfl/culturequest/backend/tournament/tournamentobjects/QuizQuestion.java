package ch.epfl.culturequest.backend.tournament.tournamentobjects;


// represents a single question in a quiz, with four possible answers and a correct answer

import java.util.ArrayList;

public class QuizQuestion {

    private ArrayList<String> possibleAnswers;

    private String questionContent;
    private int correctAnswerIndex;

    public QuizQuestion(String questionContent, ArrayList<String> possibleAnswers, int correctAnswerIndex){
        assert correctAnswerIndex < possibleAnswers.size();
        assert correctAnswerIndex >= 0;
        this.possibleAnswers = possibleAnswers;
        this.correctAnswerIndex = correctAnswerIndex;
        this.questionContent = questionContent;
    }

    public void setPossibleAnswers(ArrayList<String> possibleAnswers) {
        this.possibleAnswers = possibleAnswers;
    }

    public void setQuestionContent(String questionContent) {
        this.questionContent = questionContent;
    }

    public void setCorrectAnswerIndex(int correctAnswerIndex) {
        assert correctAnswerIndex < possibleAnswers.size();
        assert correctAnswerIndex >= 0;
        this.correctAnswerIndex = correctAnswerIndex;
    }

    public QuizQuestion(){
        // For Serialization
        this.possibleAnswers = null;
        this.correctAnswerIndex = 0;
        this.questionContent = "";
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

    public boolean isCorrect(int selectedAnswer) {
        return selectedAnswer == correctAnswerIndex;
    }





}
