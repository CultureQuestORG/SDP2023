package ch.epfl.culturequest.ui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.not;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.action.ViewActions;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.authentication.Authenticator;
import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.tournament.quiz.Question;
import ch.epfl.culturequest.tournament.quiz.Quiz;
import ch.epfl.culturequest.ui.quiz.QuizActivity;
import ch.epfl.culturequest.ui.quiz.QuizViewModel;

public class QuizActivityTest {

    private final String email = "test@gmail.com";
    private final String password = "abcdefg";

    QuizActivity activity;

    QuizViewModel quizViewModel;


    @Before
    public void setUp() throws InterruptedException {
        // Set up the database to run on the local emulator of Firebase
        Database.setEmulatorOn();

        // clear the database before starting the following tests
        Database.clearDatabase();

        //Set up the authentication to run on the local emulator of Firebase
        Authenticator.setEmulatorOn();

        // Signs up a test user used in all the tests
        Authenticator.manualSignUp(email, password).join();

        // Manually signs in the user before the tests
        Authenticator.manualSignIn(email, password).join();

        Profile.setActiveProfile(new Profile("testUser",""));

        ArrayList<String> possibilities = new ArrayList<>();
        possibilities.add("answer");
        possibilities.add("wrongAnswer1");
        possibilities.add("wrongAnswer2");
        possibilities.add("wrongAnswer3");
        Question question = new Question("question", possibilities,0);
        ArrayList<Question> questions = new ArrayList<>();
        questions.add(question);
        questions.add(question);
        questions.add(question);
        questions.add(question);
        questions.add(question);
        Quiz quiz = new Quiz("La Joconde", questions,"tournamentId");
        Database.addQuiz(quiz).join();

        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), QuizActivity.class);
        intent.putExtra("tournament", "tournamentId");
        intent.putExtra("artName", "La Joconde");

        ActivityScenario<QuizActivity> scenario = ActivityScenario.launch(intent);
        scenario.onActivity(a -> {
            activity = a;
            quizViewModel=QuizViewModel.getQuiz(Profile.getActiveProfile().getUid(), "tournamentId", "La Joconde");
        });

        Thread.sleep(20000);



    }


    @Test
    public void SuccessfulQuiz() throws InterruptedException {
    startQuiz();
    answerQuestion();
    turnWheel();
    answerQuestion();
    turnWheel();
    answerQuestion();
    turnWheel();
    answerQuestion();
    turnWheel();
    answerQuestion();
    checkFinalScreen();

    }

    @Test
    public void QuitQuiz() throws InterruptedException {
        startQuiz();
        answerQuestion();
        turnWheel();
        answerQuestion();
        quitQuiz();
        checkFinalScreen();
    }

    @Test
    public void FailedQuiz() throws InterruptedException {
        startQuiz();
        answerWrongly();
        checkGameOverScreen();

    }

    private void checkGameOverScreen() throws InterruptedException  {
        Thread.sleep(2000);
        onView(withText("GAME OVER")).check(matches(isDisplayed()));
        onView(withText("BACK TO THE TOURNAMENT")).check(matches(isDisplayed()));
    }

    private void checkFinalScreen() throws InterruptedException  {
        Thread.sleep(2000);
        onView(withText(String.format("You earned %d points", quizViewModel.getScore().getValue()))).check(matches(isDisplayed()));
        onView(withText("BACK TO THE TOURNAMENT")).perform(ViewActions.click());

    }

    private void answerQuestion() throws InterruptedException {
        Thread.sleep(2000);
        onView(withText("answer")).check(matches(isDisplayed()));
        onView(withText("wrongAnswer1")).check(matches(isDisplayed()));
        onView(withText("wrongAnswer2")).check(matches(isDisplayed()));
        onView(withText("wrongAnswer3")).check(matches(isDisplayed()));
        onView(withText("answer")).perform(ViewActions.click());
        Thread.sleep(2000);
        onView(withText("Next")).perform(ViewActions.click());
        Thread.sleep(2000);

    }

    private void answerWrongly() throws InterruptedException {
        Thread.sleep(2000);
        onView(withText("answer")).check(matches(isDisplayed()));
        onView(withText("wrongAnswer1")).check(matches(isDisplayed()));
        onView(withText("wrongAnswer2")).check(matches(isDisplayed()));
        onView(withText("wrongAnswer3")).check(matches(isDisplayed()));
        onView(withText("wrongAnswer1")).perform(ViewActions.click());
        Thread.sleep(2000);
        onView(withText("Next")).perform(ViewActions.click());
        Thread.sleep(2000);

    }

    private void startQuiz() throws InterruptedException {
        Thread.sleep(2000);
        onView(withText("Start")).check(matches(isDisplayed()));
        onView(withText("La Joconde")).check(matches(isDisplayed())); //check that the name of the art is displayed
        onView(withText("Start")).perform(ViewActions.click());
        Thread.sleep(2000);
    }

    private void quitQuiz() throws InterruptedException {
        Thread.sleep(2000);
        onView(withText(String.format("%d", quizViewModel.getScore().getValue()))).check(matches(isDisplayed()));
        onView(withText("SPIN")).check(matches(isDisplayed()));
        onView(withText("QUIT")).check(matches(isDisplayed()));
        Thread.sleep(2000);
        onView(withText("QUIT")).perform(ViewActions.click());
        Thread.sleep(2000);
    }

    private void turnWheel() throws InterruptedException {
        Thread.sleep(2000);
        onView(withText(String.format("%d", quizViewModel.getScore().getValue()))).check(matches(isDisplayed()));
        onView(withText("SPIN")).check(matches(isDisplayed()));
        onView(withText("QUIT")).check(matches(isDisplayed()));
        onView(withText("SPIN")).perform(ViewActions.click());
        Thread.sleep(2000);
        onView(withText("OK")).perform(ViewActions.click());
        Thread.sleep(2000);
        onView(withText(String.format("%d", quizViewModel.getNextScore().getValue()))).check(matches(isDisplayed()));
        onView(withText("NEXT")).perform(ViewActions.click());
        Thread.sleep(2000);

    }


}
