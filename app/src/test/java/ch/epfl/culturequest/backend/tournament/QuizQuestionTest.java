package ch.epfl.culturequest.backend.tournament;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import ch.epfl.culturequest.backend.tournament.tournamentobjects.QuizQuestion;

public class QuizQuestionTest{

    QuizQuestion QuizQuestion;

    @Before
    public void setUp(){
        ArrayList<String> answers = new ArrayList<>();
        answers.add("answer");
        answers.add("wrongAnswer1");
        answers.add("wrongAnswer2");
        answers.add("wrongAnswer3");
        QuizQuestion = new QuizQuestion("QuizQuestion", answers,0);
    }

    @Test
    public void setAndGetQuizQuestion(){
        QuizQuestion.setQuestionContent("newQuizQuestion");
        assertEquals("newQuizQuestion", QuizQuestion.getQuestionContent());
        QuizQuestion.setQuestionContent("QuizQuestion");
    }

    @Test
    public void setAndGetPossibleAnswers(){
        ArrayList<String> answers = new ArrayList<>();
        answers.add("answer");
        answers.add("WrongAnswer1");
        answers.add("WrongAnswer2");
        answers.add("WrongAnswer3");
        QuizQuestion.setPossibleAnswers(answers);
        assertEquals(answers, QuizQuestion.getPossibleAnswers());
    }

    @Test
    public void setAndGetCorrectAnswer(){
        QuizQuestion.setCorrectAnswerIndex(1);
        assertEquals(1, QuizQuestion.getCorrectAnswerIndex());
        QuizQuestion.setCorrectAnswerIndex(0);
    }

    @Test
    public void constructor(){
        ArrayList<String> answers = new ArrayList<>();
        answers.add("answer");
        answers.add("wrongAnswer1");
        answers.add("wrongAnswer2");
        answers.add("wrongAnswer3");
        QuizQuestion QuizQuestion = new QuizQuestion("QuizQuestion", answers,0);
        assertEquals("QuizQuestion", QuizQuestion.getQuestionContent());
        assertEquals(answers, QuizQuestion.getPossibleAnswers());
        assertEquals(0, QuizQuestion.getCorrectAnswerIndex());
    }

    @Test
    public void emptyConstructor() {
        QuizQuestion QuizQuestion = new QuizQuestion();
        assertEquals("", QuizQuestion.getQuestionContent());
        assertNull(QuizQuestion.getPossibleAnswers());
        assertEquals(0, QuizQuestion.getCorrectAnswerIndex());
    }

    @Test
    public void isCorrect(){
        assertTrue(QuizQuestion.isCorrect(0));
        assertFalse(QuizQuestion.isCorrect(1));
    }

}
