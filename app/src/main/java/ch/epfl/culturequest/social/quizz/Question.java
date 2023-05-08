package ch.epfl.culturequest.social.quizz;

import java.util.List;

public class Question {

    String question;
    List<String> possibilities;
    String answer;



    public Question(String question, List<String> possibilities, String answer) {
        assert possibilities.contains(answer);
        this.question = question;
        this.possibilities = possibilities;
        this.answer = answer;
    }

    public Question() {
        this.question = "";
        this.possibilities = null;
        this.answer = "";
    }

    public String getQuestion() {
        return question;
    }

    public List<String> getPossibilities() {
        return possibilities;
    }

    public String getAnswer() {
        return answer;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setPossibilities(List<String> possibilities) {
        this.possibilities = possibilities;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }


}
