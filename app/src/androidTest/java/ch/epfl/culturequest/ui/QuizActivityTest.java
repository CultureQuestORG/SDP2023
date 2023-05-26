package ch.epfl.culturequest.ui;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;


import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.action.ViewActions;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.authentication.Authenticator;
import ch.epfl.culturequest.backend.tournament.apis.SeedApi;
import ch.epfl.culturequest.backend.tournament.apis.TournamentManagerApi;
import ch.epfl.culturequest.backend.tournament.tournamentobjects.ArtQuiz;
import ch.epfl.culturequest.backend.tournament.tournamentobjects.QuizQuestion;
import ch.epfl.culturequest.backend.tournament.tournamentobjects.Tournament;
import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.social.Post;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.ui.quiz.QuizActivity;
import ch.epfl.culturequest.ui.quiz.QuizGameOverFragment;
import ch.epfl.culturequest.ui.quiz.QuizInterFragment;
import ch.epfl.culturequest.ui.quiz.QuizQuestionFragment;
import ch.epfl.culturequest.ui.quiz.QuizVictoryFragment;
import ch.epfl.culturequest.ui.quiz.QuizViewModel;
import ch.epfl.culturequest.ui.quiz.QuizWelcomeFragment;

public class QuizActivityTest {

    private final String email = "test@gmail.com";
    private final String password = "abcdefg";

    String tournamentId ;

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

        Post post = new Post("postID",
                "uid",
                "https://firebasestorage.googleapis.com/v0/b/culturequest.appspot.com/o/images%2FAcZdcTtQxkUtA4gLRj9rgaFoWVv1%2Fa6a2f12c-401b-4f02-a394-928e1e6bea74?alt=media&token=24102b4b-7a82-4692-bce2-36f770b065c3",
        "La Joconde",1234,0,new ArrayList<>());
        Database.setScoreQuiz("tournament1","art1","user1",1);
        Database.uploadPost(post).join();




        ArrayList<String> possibilities = new ArrayList<>();
        possibilities.add("answer");
        possibilities.add("wrongAnswer1");
        possibilities.add("wrongAnswer2");
        possibilities.add("wrongAnswer3");
        QuizQuestion question = new QuizQuestion("question", possibilities,0);
        ArrayList<QuizQuestion> questions = new ArrayList<>();
        questions.add(question);
        questions.add(question);
        questions.add(question);
        questions.add(question);
        questions.add(question);
        ArtQuiz quiz = new ArtQuiz("La Joconde", questions,new HashMap<>());
        HashMap<String, ArtQuiz> quizzes = new HashMap<>();
        quizzes.put("La Joconde", quiz);
        TournamentManagerApi.handleTournaments(ApplicationProvider.getApplicationContext());
        SeedApi.storeSeedInSharedPref(SeedApi.generateSeed());
        Tournament tournament = new Tournament(quizzes);
        tournamentId = tournament.getTournamentId();
        TournamentManagerApi.storeTournamentInSharedPref(tournament);
        //Database.addQuiz(quiz).join();

        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), QuizActivity.class);
        intent.putExtra("tournament", tournamentId);
        intent.putExtra("artName", "La Joconde");

        ActivityScenario<QuizActivity> scenario = ActivityScenario.launch(intent);
        scenario.onActivity(a -> {
            activity = a;
            quizViewModel=QuizViewModel.getQuiz(Profile.getActiveProfile().getUid(), tournamentId, "La Joconde");
        });

        Thread.sleep(5000);
    }


    @Test
    public void SuccessfulQuiz() throws InterruptedException {
    QuizQuestionFragment q=startQuiz();
    QuizInterFragment inter = (QuizInterFragment) answerQuestion(q);
    q=turnWheel(inter);
    inter =(QuizInterFragment) answerQuestion(q);
    q=turnWheel(inter);
    inter =(QuizInterFragment) answerQuestion(q);
    q=turnWheel(inter);
    inter =(QuizInterFragment) answerQuestion(q);
    q=turnWheel(inter);
    QuizVictoryFragment v =(QuizVictoryFragment) answerQuestion(q);
    checkFinalScreen(v);

    }

    @Test
    public void QuitQuiz() throws InterruptedException {
        QuizQuestionFragment q=startQuiz();
        QuizInterFragment inter = (QuizInterFragment) answerQuestion(q);
        q=turnWheel(inter);
        inter =(QuizInterFragment) answerQuestion(q);
        QuizVictoryFragment victory=quitQuiz(inter);
        checkFinalScreen(victory);
    }


    @Test
    public void FailedQuiz() throws InterruptedException {
        QuizQuestionFragment q=startQuiz();
        QuizGameOverFragment gameOver = (QuizGameOverFragment) answerWrongly(q);
        checkGameOverScreen();

    }

    private void checkGameOverScreen() throws InterruptedException  {
        Thread.sleep(2000);
        onView(withText("GAME OVER")).check(matches(isEnabled()));
        onView(withText("BACK TO THE TOURNAMENT")).check(matches(isEnabled()));
    }

    private void checkFinalScreen(QuizVictoryFragment victory) throws InterruptedException  {
        Thread.sleep(2000);
        onView(withText(String.format("You earned %d points", quizViewModel.getScore().getValue()))).check(matches(isEnabled()));
        onView(withText("BACK TO THE TOURNAMENT")).check(matches(isEnabled()));

    }

    private Fragment answerQuestion(QuizQuestionFragment q) throws InterruptedException {
        Thread.sleep(2000);
        onView(withId(R.id.answer1RadioButton)).check(matches(isEnabled()));
        onView(withId(R.id.answer2RadioButton)).check(matches(isEnabled()));
        onView(withId(R.id.answer3RadioButton)).check(matches(isEnabled()));
        onView(withId(R.id.answer4RadioButton)).check(matches(isEnabled()));
        Thread.sleep(2000);
        try {
            q.pickAnswer(0);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        Thread.sleep(2000);
        return q.valideAnswer();


    }

    private Fragment answerWrongly(QuizQuestionFragment q) throws InterruptedException {
        Thread.sleep(2000);
        onView(withId(R.id.answer1RadioButton)).check(matches(isEnabled()));
        onView(withId(R.id.answer2RadioButton)).check(matches(isEnabled()));
        onView(withId(R.id.answer3RadioButton)).check(matches(isEnabled()));
        onView(withId(R.id.answer4RadioButton)).check(matches(isEnabled()));

        Thread.sleep(2000);
        try{
            q.pickAnswer(1);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        Thread.sleep(2000);
        return q.valideAnswer();

    }

    private QuizQuestionFragment startQuiz() throws InterruptedException {
        Thread.sleep(2000);
        QuizWelcomeFragment fragment = activity.welcome();
        onView(withId(R.id.startButton)).check(matches(isEnabled()));
        onView(withText("La Joconde")).check(matches(isEnabled())); //check that the name of the art is displayed
        Thread.sleep(2000);
        return fragment.startQuiz();

    }

    private QuizVictoryFragment quitQuiz(QuizInterFragment inter) throws InterruptedException {
        Thread.sleep(2000);
        onView(withText(String.format("%d", quizViewModel.getScore().getValue()))).check(matches(isEnabled()));
        onView(withText("SPIN")).check(matches(isEnabled()));
        onView(withText("QUIT")).check(matches(isEnabled()));
        Thread.sleep(2000);
        return inter.quit();

    }

    private QuizQuestionFragment turnWheel(QuizInterFragment inter) throws InterruptedException {
        Thread.sleep(2000);
        onView(withText(String.format("%d", quizViewModel.getScore().getValue()))).check(matches(isEnabled()));
        onView(withText("SPIN")).check(matches(isEnabled()));
        onView(withText("QUIT")).check(matches(isEnabled()));
        Thread.sleep(2000);
        inter.spinWheel();
        Thread.sleep(6000);
        onView(withText("OK")).perform(ViewActions.click());
        Thread.sleep(2000);
        onView(withText(String.format("%d", quizViewModel.getNextScore().getValue()))).check(matches(isEnabled()));
        Thread.sleep(2000);
        return inter.nextQuestion();


    }

    @Test
    public void scoreDB(){
        Database.getScoreQuiz("tournament1","art1","user1").thenAccept(
                score -> assertEquals(1,score.intValue())
        );
    }


}
