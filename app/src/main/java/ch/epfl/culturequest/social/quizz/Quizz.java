package ch.epfl.culturequest.social.quizz;

import java.util.List;

public class Quizz {

    String artName;

    String tournament;
    List<Question> questions;
    String uid;
    Integer score;




    public Quizz(String artName, List<Question> questions, String uid, Integer score, String tournament) {
        this.artName = artName;
        this.questions = questions;
        this.uid = uid;
        this.score = score;
        this.tournament = tournament;
    }

    public Quizz() {
        this.artName = "";
        this.questions = null;
        this.uid = "";
        this.score = 0;
        this.tournament = "";
    }


    public String getArtName() {
        return artName;
    }

    public void setArtName(String artName) {
        this.artName = artName;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getTournament() {
        return tournament;
    }

    public void setTournament(String tournament) {
        this.tournament = tournament;
    }
}
