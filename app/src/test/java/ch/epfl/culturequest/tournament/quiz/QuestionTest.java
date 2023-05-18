package ch.epfl.culturequest.tournament.quiz;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class QuestionTest{

    Question question;

    @Before
    public void setUp(){
        ArrayList<String> answers = new ArrayList<>();
        answers.add("answer");
        answers.add("wrongAnswer1");
        answers.add("wrongAnswer2");
        answers.add("wrongAnswer3");
        question = new Question("question", answers,0);
    }

    @Test
    public void setAndGetQuestion(){
        question.setQuestion("newQuestion");
        assertEquals("newQuestion", question.getQuestion());
        question.setQuestion("question");
    }

    @Test
    public void setAndGetPossibilities(){
        ArrayList<String> answers = new ArrayList<>();
        answers.add("answer");
        answers.add("WrongAnswer1");
        answers.add("WrongAnswer2");
        answers.add("WrongAnswer3");
        question.setPossibilities(answers);
        assertEquals(answers, question.getPossibilities());
    }

    @Test
    public void setAndGetCorrectAnswer(){
        question.setAnswer(1);
        assertEquals(1, question.getAnswer());
        question.setAnswer(0);
    }

    @Test
    public void constructor(){
        ArrayList<String> answers = new ArrayList<>();
        answers.add("answer");
        answers.add("wrongAnswer1");
        answers.add("wrongAnswer2");
        answers.add("wrongAnswer3");
        Question question = new Question("question", answers,0);
        assertEquals("question", question.getQuestion());
        assertEquals(answers, question.getPossibilities());
        assertEquals(0, question.getAnswer());
    }

    @Test
    public void emptyConstructor() {
        Question question = new Question();
        assertEquals("", question.getQuestion());
        assertNull(question.getPossibilities());
        assertEquals(0, question.getAnswer());
    }

    @Test
    public void isCorrect(){
        assertTrue(question.isCorrect(0));
        assertFalse(question.isCorrect(1));
    }

}
