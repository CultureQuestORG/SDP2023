package ch.epfl.culturequest.backend.tournament;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static ch.epfl.culturequest.database.Database.getDeviceSynchronizationRef;
import static ch.epfl.culturequest.database.Database.indicateTournamentGenerated;
import static ch.epfl.culturequest.database.Database.indicateTournamentNotGenerated;
import static ch.epfl.culturequest.database.Database.isEqualAsync;
import static ch.epfl.culturequest.database.Database.isTournamentGenerationLocked;
import static ch.epfl.culturequest.database.Database.lockTournamentGeneration;
import static ch.epfl.culturequest.database.Database.unlockTournamentGeneration;
import static ch.epfl.culturequest.database.Database.uploadSeedToDatabase;
import static ch.epfl.culturequest.database.Database.uploadTournamentToDatabase;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.database.DatabaseReference;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import ch.epfl.culturequest.backend.tournament.apis.TournamentManagerApi;
import ch.epfl.culturequest.backend.tournament.tournamentobjects.ArtQuiz;
import ch.epfl.culturequest.backend.tournament.tournamentobjects.QuizQuestion;
import ch.epfl.culturequest.backend.tournament.tournamentobjects.Tournament;
import ch.epfl.culturequest.database.Database;

public class TournamentManagerApiTest {

    Context targetContext;

    @Before
    public void setUp() {

        targetContext = getApplicationContext();

        // clear the shared pref before starting the following tests
        clearSharedPreferences();

        // Set up the database to run on the local emulator of Firebase
        Database.setEmulatorOn();

        // clear the database before starting the following tests
        Database.clearDatabase();


        unlockTournamentGeneration().join();
        indicateTournamentNotGenerated().join();
    }

    @After
    public void tearDown() {
        // clear the shared pref after the tests
        clearSharedPreferences();

        // clear the database after the tests
        Database.clearDatabase();
    }


    // Tournament date is correctly created and stored in shared pref in the first time
    @Test
    public void tournamentCorrectlyScheduledWhenFirstTime() {

        TournamentManagerApi.handleTournaments(targetContext).join();

        SharedPreferences tournamentSharedPref = getTournamentSharedPrefLocation();

        // from tournamentSharedPref, get the tournament date tournamentDate long variable
        Long tournamentDate = tournamentSharedPref.getLong("tournamentDate", 0);
        assertThat(tournamentDate, is(not(0)));

    }

    // Correct handling when tournament is over
    @Test
    public void everythingCorrectlyHandledWhenTournamentIsOver() {

        SharedPreferences tournamentSharedPref = getTournamentSharedPrefLocation();

        // fake that the tournament end date is in the past
        Long pastDate = System.currentTimeMillis() - 1000000000; // the tournament is supposed to have started 11 days ago, so is now over
        tournamentSharedPref.edit().putLong("tournamentDate", pastDate).commit();

        // put a random element in the tournament shared pref file to check that it is deleted
        tournamentSharedPref.edit().putString("randomElement", "random").commit();

        // fake that the tournament has been already generated
        indicateTournamentGenerated().join();

        // fake that the tournament generation is locked
        lockTournamentGeneration().join();

        // main function call (in the real app, this is called by onResume() handler of main activities)
        TournamentManagerApi.handleTournaments(targetContext).join();

        // check that the tournament date/schedule has been updated
        Long tournamentDate = tournamentSharedPref.getLong("tournamentDate", 0);
        assertThat(tournamentDate, is(not(pastDate)));

        // check that there is no more random element in the shared pref file => shared preferences correctly cleaned up
        String randomElement = tournamentSharedPref.getString("randomElement", "empty");
        assertThat(randomElement, is("empty"));

        // check that the tournament generation is unlocked

        Boolean generationNoLongerLocked = isEqualAsync(getTournamentGenerationLockedPath(), false).join();
        assertThat(generationNoLongerLocked, is(true));

        // check that the tournament is not generated

        Boolean tournamentNoLongerGenerated = isEqualAsync(getTournamentGeneratedPath(), false).join();
        assertThat(tournamentNoLongerGenerated, is(true));

    }

    @Test
    public void tournamentCorrectlyGeneratedWhenNoConcurrency() {


        SharedPreferences tournamentSharedPref = getTournamentSharedPrefLocation();

        // fake that the tournament end date is in the future, but that the tournament is supposed to have started
        Long fakeStartingDate = System.currentTimeMillis() - 100000000; // 100000000 is approx 1 day; the tournament is supposed to have started 1 day ago
        tournamentSharedPref.edit().putLong("tournamentDate", fakeStartingDate).commit();

        unlockTournamentGeneration().join();
        indicateTournamentNotGenerated().join();

        // clear weeklyTournament string in Tournament file shared pref
        tournamentSharedPref.edit().putString("weeklyTournament", null).commit();


        // Launch the main method
        TournamentManagerApi.handleTournaments(targetContext);

        //// CHECKS ////

        // Check that the tournament has been generated

        // wait to be sure that the tournament has been indicated as generated in Firebase
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        Boolean tournamentGenerated = isEqualAsync(getTournamentGeneratedPath(), true).join();
//        assertThat(tournamentGenerated, is(true));

        // Check that the tournament generation is locked
        Boolean generationLocked = isTournamentGenerationLocked().join();
        assertThat(generationLocked, is(true));

        // Check that the weeklyTournament string in Tournament file shared pref is not null
//        String weeklyTournament = tournamentSharedPref.getString("weeklyTournament", null);
//        assertThat(weeklyTournament, is(not(nullValue())));

    }

    @Test
    public void tournamentCorrectlyFetchedWhenConcurrency() {

        SharedPreferences tournamentSharedPref = getTournamentSharedPrefLocation();

        // fake that the tournament end date is in the future, but that the tournament is supposed to have started
        Long fakeStartingDate = System.currentTimeMillis() - 100000000; // 100000000 is approx 1 day; the tournament is supposed to have started 1 day ago
        tournamentSharedPref.edit().putLong("tournamentDate", fakeStartingDate).commit();

        // fake that tournament generation is locked by another device and that the tournament is not generated yet
        lockTournamentGeneration().join();
        indicateTournamentNotGenerated().join();

        // clear weeklyTournament string in Tournament file shared pref
        tournamentSharedPref.edit().putString("weeklyTournament", null).commit();


        // fake a concurrent device successfully generating the tournament after 5 seconds
        // we thus schedule the future indication that the tournament is generated
        simulateConcurrentTournamentGeneration();

        // Launch the main method
        // In the given context, the tournament should be fetched from Firebase whenever it appears to be generated
        TournamentManagerApi.handleTournaments(targetContext).join();

        //// CHECKS ////

        // Check that the tournament has been generated by the concurrent device
        Boolean tournamentGenerated = isEqualAsync(getTournamentGeneratedPath(), true).join();
        assertThat(tournamentGenerated, is(true));

        // Check that tournament has been correctly stored in SharedPref after successful fetch from Firebase
        String weeklyTournament = tournamentSharedPref.getString("weeklyTournament", null);
        assertThat(weeklyTournament, is(not(nullValue())));
    }


    @Test
    public void tournamentCorrectlyRetrievedAndDeserializedFromSharedPref() {

        Tournament fakeTournament = getFakeTournament();

        // clear weeklyTournament string in Tournament file shared pref
        SharedPreferences tournamentSharedPref = getTournamentSharedPrefLocation();
        tournamentSharedPref.edit().putString("weeklyTournament", null).commit();


        // store the fakeTournamentInSharedPref
        TournamentManagerApi.storeTournamentInSharedPref(fakeTournament);

        // retrieve the tournament from shared pref
        Tournament retrievedTournament = TournamentManagerApi.getTournamentFromSharedPref();

        // verify that the tournament is the same as the fake one
        assertThat(areTournamentsEqual(retrievedTournament, fakeTournament), is(true));

    }


    private SharedPreferences getTournamentSharedPrefLocation() {

        Context context = getApplicationContext();

        SharedPreferences tournamentSharedPref = context.getSharedPreferences("tournament", Context.MODE_PRIVATE);

        return tournamentSharedPref;
    }

    private void clearSharedPreferences() {

        SharedPreferences tournamentSharedPref = getTournamentSharedPrefLocation();

        SharedPreferences.Editor editor = tournamentSharedPref.edit();

        editor.clear();

        editor.commit();
    }

    private DatabaseReference getTournamentGenerationLockedPath() {
        return getDeviceSynchronizationRef().child("generationLocked");
    }

    private DatabaseReference getTournamentGeneratedPath() {
        return getDeviceSynchronizationRef().child("generated");
    }

    private CompletableFuture<Void> simulateConcurrentTournamentGeneration() {

        CompletableFuture<Void> fakeConcurrentGeneration = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Tournament fakeTournament = getFakeTournament();

            uploadTournamentToDatabase(fakeTournament).join();

            indicateTournamentGenerated().join();
            fakeConcurrentGeneration.complete(null);
        });

        return fakeConcurrentGeneration;
    }

    private Tournament getFakeTournament() {

        ArrayList<String> fakeOptions = new ArrayList<String>() {{
            add("fakeOption1");
            add("fakeOption2");
            add("fakeOption3");
            add("fakeOption4");
        }};

        QuizQuestion fakeQuestion = new QuizQuestion("fakeQuestion", fakeOptions, 0);
        ArrayList<QuizQuestion> fakeQuestions = new ArrayList<QuizQuestion>() {{
            add(fakeQuestion);
            add(fakeQuestion);
            add(fakeQuestion);
        }};
        ArtQuiz fakQuiz = new ArtQuiz(fakeQuestions);
        HashMap<String, ArtQuiz> fakeQuizDict = new HashMap<String, ArtQuiz>() {{
            put("fakeArt", fakQuiz);
        }};

        Tournament fakeTournament = new Tournament(fakeQuizDict);

        return fakeTournament;
    }

    private boolean areTournamentsEqual(Tournament tournament1, Tournament tournament2) {
        // Compare tournament IDs
        if (!tournament1.getTournamentId().equals(tournament2.getTournamentId())) {
            return false;
        }

        // Compare artQuizzes
        Map<String, ArtQuiz> artQuizzes1 = tournament1.getArtQuizzes();
        Map<String, ArtQuiz> artQuizzes2 = tournament2.getArtQuizzes();

        // Compare the number of art quizzes
        if (artQuizzes1.size() != artQuizzes2.size()) {
            return false;
        }

        // Compare each art quiz
        for (Map.Entry<String, ArtQuiz> entry : artQuizzes1.entrySet()) {
            String quizId = entry.getKey();
            ArtQuiz quiz1 = entry.getValue();
            ArtQuiz quiz2 = artQuizzes2.get(quizId);

            // If quiz ID is not found in the second tournament, they are not equal
            if (quiz2 == null) {
                return false;
            }

            // Compare quiz questions
            ArrayList<QuizQuestion> questions1 = quiz1.getQuestions();
            ArrayList<QuizQuestion> questions2 = quiz2.getQuestions();

            // Compare the number of quiz questions
            if (questions1.size() != questions2.size()) {
                return false;
            }

            // Compare each quiz question
            for (int i = 0; i < questions1.size(); i++) {
                QuizQuestion question1 = questions1.get(i);
                QuizQuestion question2 = questions2.get(i);

                // Compare question content
                if (!question1.getQuestionContent().equals(question2.getQuestionContent())) {
                    return false;
                }

                // Compare possible answers
                if (!question1.getPossibleAnswers().equals(question2.getPossibleAnswers())) {
                    return false;
                }

                // Compare correct answer index
                if (question1.getCorrectAnswerIndex() != question2.getCorrectAnswerIndex()) {
                    return false;
                }
            }
        }

        // The tournaments are equal
        return true;
    }
}
