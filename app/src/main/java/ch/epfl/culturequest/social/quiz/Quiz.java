package ch.epfl.culturequest.social.quiz;

import java.util.ArrayList;
import java.util.List;

public class Quiz {

    String artName;

    String tournament;
    ArrayList<Question> questions;

    public Quiz(String artName, ArrayList<Question> questions, String tournament) {
        this.artName = artName;
        this.questions = questions;
        this.tournament = tournament;
    }

    public Quiz() {
        this.artName = "";
        this.questions = null;
        this.tournament = "";
    }


    public String getArtName() {
        return artName;
    }

    public void setArtName(String artName) {
        this.artName = artName;
    }

    public ArrayList<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<Question> questions) {
        this.questions = questions;
    }



    public String getTournament() {
        return tournament;
    }

    public void setTournament(String tournament) {
        this.tournament = tournament;
    }


}
