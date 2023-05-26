package ch.epfl.culturequest.backend.tournament;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.runners.model.MultipleFailureException.assertEmpty;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import ch.epfl.culturequest.backend.tournament.tournamentobjects.ArtQuiz;
import ch.epfl.culturequest.backend.tournament.tournamentobjects.QuizQuestion;
import ch.epfl.culturequest.database.Database;

public class QuizTest {

    ArtQuiz ArtQuiz;

    @Before
    public void setUp(){
        ArtQuiz = new ArtQuiz("artName", null,new HashMap<>());
    }


    @Test
    public void setAndGetArtName() {
        ArtQuiz.setArtName("newArtName");
        assertEquals("newArtName", ArtQuiz.getArtName());
        ArtQuiz.setArtName("artName");
    }

    @Test
    public void setAndGetQuestions() {
        ArrayList<String> answers = new ArrayList<>();
        answers.add("answer");
        answers.add("wrongAnswer1");
        answers.add("wrongAnswer2");
        answers.add("wrongAnswer3");
        QuizQuestion question = new QuizQuestion("question", answers,0);
        ArrayList<QuizQuestion> questions = new ArrayList<>();
        questions.add(question);
        ArtQuiz.setQuestions(questions);
        assertEquals(questions, ArtQuiz.getQuestions());
    }

    @Test
    public void constructor() {
        HashMap<String, Integer> scores = new HashMap<>();
        ArtQuiz ArtQuiz = new ArtQuiz("artName", null, scores);
        assertEquals("artName", ArtQuiz.getArtName());
        assertEquals(scores, ArtQuiz.getScores());
    }

    @Test
    public void emptyConstructor() {
        ArtQuiz ArtQuiz = new ArtQuiz();
        assertEquals("", ArtQuiz.getArtName());
        assertTrue(ArtQuiz.getScores().isEmpty());
        assertNull(ArtQuiz.getQuestions());
    }

    @Test
    public void setAndGetScores() {
        ArtQuiz.setScores(null);
        assertNull(ArtQuiz.getScores());
    }


}
