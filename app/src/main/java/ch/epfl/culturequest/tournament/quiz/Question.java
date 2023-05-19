package ch.epfl.culturequest.tournament.quiz;

import java.util.ArrayList;
import java.util.List;

public class Question {

    String question;
    List<String> possibilities;
    int answer;



    public Question(String question, List<String> possibilities, int answer) {
        assert answer < possibilities.size();
        assert answer >= 0;
        this.question = question;
        this.possibilities = possibilities;
        this.answer = answer;
    }

    public Question() {
        this.question = "";
        this.possibilities = null;
        this.answer = 0;
    }

    public String getQuestion() {
        return question;
    }

    public List<String> getPossibilities() {
        return possibilities;
    }

    public int getAnswer() {
        return answer;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setPossibilities(ArrayList<String> possibilities) {
        this.possibilities = possibilities;
    }

    public void setAnswer(int answer) {
        assert answer < possibilities.size();
        assert answer >= 0;
        this.answer = answer;
    }


    public boolean isCorrect(int selectedAnswer) {
        return selectedAnswer == answer;
    }
}
