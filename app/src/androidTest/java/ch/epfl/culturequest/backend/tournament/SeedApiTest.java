package ch.epfl.culturequest.backend.tournament;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static ch.epfl.culturequest.backend.tournament.apis.TournamentManagerApi.getTournamentSharedPrefLocation;
import static ch.epfl.culturequest.backend.tournament.apis.TournamentManagerApi.handleTournaments;
import static ch.epfl.culturequest.database.Database.indicateTournamentNotGenerated;
import static ch.epfl.culturequest.database.Database.unlockTournamentGeneration;
import static ch.epfl.culturequest.database.Database.uploadSeedToDatabase;

import android.content.Context;
import android.content.SharedPreferences;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ch.epfl.culturequest.backend.tournament.apis.TournamentManagerApi;
import ch.epfl.culturequest.database.Database;

public class SeedApiTest {

    Context targetContext;

    @Before
    public void setUp() {

        targetContext = getApplicationContext();

        // clear the shared pref before starting the following tests
        clearSharedPreferences();

        // Set up the database to run on the local emulator of Firebase
        Database.setEmulatorOn();

        // clear the database before starting the following tests
        Database.clearDatabase().join();
    }

    @After
    public void tearDown() {
        // clear the shared pref after the tests
        clearSharedPreferences();

        // clear the database after the tests
        Database.clearDatabase().join();
    }

    @Test
    public void seedCorrectlyStoredWhenGenerated(){

        // Tournament has never been created and seed has never been generated in database => we generate it and upload it
        TournamentManagerApi.handleTournaments(targetContext).join();

        SharedPreferences tournamentSharedPref = getTournamentSharedPrefLocation();

        // from tournamentSharedPref, get the seed seed long variable
        Long seed = tournamentSharedPref.getLong("seed", 0);
        assertThat(seed, is(not(0)));
    }

    @Test
    public void seedCorrectlyStoredWhenFetched(){

        Long fakeSeed = 123456789L;

        // fake upload of seed to database
        uploadSeedToDatabase(fakeSeed).join();

        TournamentManagerApi.handleTournaments(targetContext).join();

        SharedPreferences tournamentSharedPref = getTournamentSharedPrefLocation();

        // from tournamentSharedPref, get the seed seed long variable
        Long seed = tournamentSharedPref.getLong("seed", 0);
        assertThat(seed, is(fakeSeed));
    }

    @Test
    public void newSeedCorrectlyGeneratedWhenTournamentOver() {

        SharedPreferences tournamentSharedPref = getTournamentSharedPrefLocation();

        // fake that the tournament end date is in the past
        Long pastDate = System.currentTimeMillis() - 1000000000; // the tournament is supposed to have started 11 days ago, so is now over
        tournamentSharedPref.edit().putLong("tournamentDate", pastDate).commit();

        // Case where nobody generated the new seed yet. So we have to generate it ourselves.
        // => Fetched seed = the one stored in the shared pref

        Long fakePreviousSeed = 123456789L;

        // store a fake seed in the shared preferences
        storeSeedInSharedPref(fakePreviousSeed);

        //upload the fake seed to the database
        uploadSeedToDatabase(fakePreviousSeed).join();

        // Main method
        handleTournaments(targetContext).join();

        // Get the new seed from the shared preferences
        Long newSeed = tournamentSharedPref.getLong("seed", 0);

        // check that it's different from previous seed
        assertThat(newSeed, is(not(fakePreviousSeed)));

    }

    @Test
    public void newSeedCorrectlyFetchedWhenTournamentOver(){

        SharedPreferences tournamentSharedPref = getTournamentSharedPrefLocation();

        // fake that the tournament end date is in the past
        Long pastDate = System.currentTimeMillis() - 1000000000; // the tournament is supposed to have started 11 days ago, so is now over
        tournamentSharedPref.edit().putLong("tournamentDate", pastDate).commit();


        // Case where someone already generated the new seed. So we just fetch it.

        Long fakePreviousSeed = 123456789L;
        Long newSeed = 987654321L;

        // store a fake seed in the shared preferences
        storeSeedInSharedPref(fakePreviousSeed);

        // fake an upload of a new seed to the database by another user
        uploadSeedToDatabase(newSeed).join();

        // Main method
        handleTournaments(targetContext).join();

        // Get the new seed from the shared preferences
        Long newStoredSeed = tournamentSharedPref.getLong("seed", 0);

        // check that it's different from previous seed
        assertThat(newStoredSeed, is(newSeed));

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

    private void storeSeedInSharedPref(Long seed) {
        SharedPreferences sharedPreferences = getTournamentSharedPrefLocation();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("seed", seed);
        editor.apply();
    }

}
