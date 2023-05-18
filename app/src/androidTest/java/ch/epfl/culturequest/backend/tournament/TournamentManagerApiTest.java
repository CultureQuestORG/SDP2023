package ch.epfl.culturequest.backend.tournament;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import static ch.epfl.culturequest.backend.tournament.apis.AppConcurrencyApi.getDeviceSynchronizationRef;
import static ch.epfl.culturequest.backend.tournament.apis.AppConcurrencyApi.indicateTournamentGenerated;
import static ch.epfl.culturequest.backend.tournament.apis.AppConcurrencyApi.indicateTournamentNotGenerated;
import static ch.epfl.culturequest.backend.tournament.apis.AppConcurrencyApi.isEqualAsync;
import static ch.epfl.culturequest.backend.tournament.apis.AppConcurrencyApi.isTournamentGenerationLocked;
import static ch.epfl.culturequest.backend.tournament.apis.AppConcurrencyApi.lockTournamentGeneration;
import static ch.epfl.culturequest.backend.tournament.apis.AppConcurrencyApi.setBoolAsync;
import static ch.epfl.culturequest.backend.tournament.apis.AppConcurrencyApi.unlockTournamentGeneration;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.After;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;

import ch.epfl.culturequest.backend.tournament.apis.TournamentManagerApi;
import ch.epfl.culturequest.backend.tournament.tournamentobjects.Tournament;

public class TournamentManagerApiTest {

    @After
    public void resetData() {
        clearSharedPreferences();
        unlockTournamentGeneration().join();
        indicateTournamentNotGenerated().join();
    }


    // Tournament date is correctly created and stored in shared pref in the first time
    @Test
    public void tournamentCorrectlyScheduledWhenFirstTime() {

        TournamentManagerApi.handleTournaments();

        SharedPreferences tournamentSharedPref = getTournamentSharedPrefLocation();

        // from tournamentSharedPref, get the tournament date tournamentDate long variable

        Long tournamentDate = tournamentSharedPref.getLong("tournamentDate", 0);

        // check that slot isn't empty
        assertThat(tournamentDate, is(not(0)));
    }

    // Correct handling when tournament is over
    @Test
    public void everythingCorrectlyHandledWhenTournamentIsOver(){

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
        TournamentManagerApi.handleTournaments();


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
    public void tournamentCorrectlyGeneratedWhenNoConcurrency(){


        SharedPreferences tournamentSharedPref = getTournamentSharedPrefLocation();

        // fake that the tournament end date is in the future, but that the tournament is supposed to have started
        Long fakeStartingDate = System.currentTimeMillis() - 100000000; // 100000000 is approx 1 day; the tournament is supposed to have started 1 day ago
        tournamentSharedPref.edit().putLong("tournamentDate", fakeStartingDate).commit();

        unlockTournamentGeneration().join();
        indicateTournamentNotGenerated().join();
        // clear weeklyTournament string in Tournament file shared pref
        tournamentSharedPref.edit().putString("weeklyTournament", null).commit();


        // Launch the main method
        TournamentManagerApi.handleTournaments();


        // Sleep 10 seconds
        try {
            Thread.sleep(1000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Check that the tournament has been generated

        Boolean tournamentGenerated = isEqualAsync(getTournamentGeneratedPath(), true).join();
        assertThat(tournamentGenerated, is(true));



    }




    private SharedPreferences getTournamentSharedPrefLocation(){

        Context context = getApplicationContext();

        SharedPreferences tournamentSharedPref = context.getSharedPreferences("tournament", Context.MODE_PRIVATE);

        return tournamentSharedPref;
    }

    private void clearSharedPreferences(){

        SharedPreferences tournamentSharedPref = getTournamentSharedPrefLocation();

        SharedPreferences.Editor editor = tournamentSharedPref.edit();

        editor.clear();

        editor.commit();
    }

    private DatabaseReference getTournamentGenerationLockedPath(){
        return getDeviceSynchronizationRef().child("generationLocked");
    }

    private DatabaseReference getTournamentGeneratedPath(){
        return getDeviceSynchronizationRef().child("generated");
    }





}
