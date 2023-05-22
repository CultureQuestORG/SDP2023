package ch.epfl.culturequest.tournament.quiz;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class QuizTest {

    Quiz quiz;

    @Before
    public void setUp() {
        quiz = new Quiz("artName", null, "tournament");
    }


    @Test
    public void setAndGetArtName() {
        quiz.setArtName("newArtName");
        assertEquals("newArtName", quiz.getArtName());
        quiz.setArtName("artName");
    }

    @Test
    public void setAndGetQuestions() {
        ArrayList<String> answers = new ArrayList<>();
        answers.add("answer");
        answers.add("wrongAnswer1");
        answers.add("wrongAnswer2");
        answers.add("wrongAnswer3");
        Question question = new Question("question", answers,0);
        ArrayList<Question> questions = new ArrayList<>();
        questions.add(question);
        quiz.setQuestions(questions);
        assertEquals(questions, quiz.getQuestions());
    }

    @Test
    public void setAndGetTournament() {
        quiz.setTournament("newTournament");
        assertEquals("newTournament", quiz.getTournament());
        quiz.setTournament("tournament");
    }

    @Test
    public void constructor() {
        Quiz quiz = new Quiz("artName", null, "tournament");
        assertEquals("artName", quiz.getArtName());
        assertEquals("tournament", quiz.getTournament());
    }

    @Test
    public void emptyConstructor() {
        Quiz quiz = new Quiz();
        assertEquals("", quiz.getArtName());
        assertEquals("", quiz.getTournament());
        assertNull(quiz.getQuestions());
    }





}
