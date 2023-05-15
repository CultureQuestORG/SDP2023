package ch.epfl.culturequest.social.quiz;

import java.util.List;

public class Quiz {

    String artName;

    String tournament;
    List<Question> questions;
    String uid;
    Integer score;

    Integer nextScore;

    Integer currentQuestion;

    boolean hasFailed;

    boolean hasFinished;




    public Quiz(String artName, List<Question> questions, String uid, Integer score, String tournament, Integer nextScore, Integer currentQuestion, boolean hasFailed, boolean hasFinished) {
        this.artName = artName;
        this.questions = questions;
        this.uid = uid;
        this.score = hasFailed ? 0 : score;
        this.tournament = tournament;
        this.nextScore = hasFinished ? score : nextScore;
        this.currentQuestion = currentQuestion;
        this.hasFailed = hasFailed;
        this.hasFinished = hasFinished;

    }

    public Quiz() {
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

    public Integer getNextScore() {
        return nextScore;
    }

    public void setNextScore(Integer nextScore) {
        this.nextScore = nextScore;
    }

    public Integer getCurrentQuestion() {
        return currentQuestion;
    }

    public void setCurrentQuestion(Integer currentQuestion) {
        this.currentQuestion = currentQuestion;
    }

    public boolean getHasFailed() {
        return hasFailed;
    }

    public void setHasFailed(boolean hasFailed) {
        this.hasFailed = hasFailed;
        if (hasFailed) {
            this.score = 0;
        }
    }

    public boolean getHasFinished() {
        return hasFinished;
    }

    public void setHasFinished(boolean hasFinished) {
        this.hasFinished = hasFinished;
        if (hasFinished) {
            this.nextScore = this.score;
        }
    }

    public void updateScore() {
        this.score = this.nextScore;
    }
}
