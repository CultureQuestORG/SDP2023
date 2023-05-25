package ch.epfl.culturequest.backend.tournament.apis;

import static ch.epfl.culturequest.backend.tournament.apis.TournamentManagerApi.getTournamentSharedPrefLocation;
import static ch.epfl.culturequest.database.Database.fetchSeedIfAlreadyGenerated;
import static ch.epfl.culturequest.database.Database.uploadSeedToDatabase;

import android.content.SharedPreferences;

import java.util.concurrent.CompletableFuture;

public class SeedApi {

    public static CompletableFuture<Void> generateOrFetchSeedThenStore(){

        return generateOrFetchSeed().thenAccept(seed -> {
            if (seed != null) {
                storeSeedInSharedPref(seed);
            }
        });

    }
    private static CompletableFuture<Long> generateOrFetchSeed(){

        return fetchSeedIfAlreadyGenerated().thenApply(seed -> {
            if (seed == null) {
                // not yet generated in database => we generate it and upload it
                return generateAndUploadSeed();
            }

            // already generated in database
            return seed;
        });
    }


    // Generate seed, upload it to database (asynchronously), and immediately return without waiting for the upload to finish
    private static Long generateAndUploadSeed() {

        Long generatedSeed = generateSeed();
        uploadSeedToDatabase(generatedSeed);
        return generatedSeed;
    }

    private static Long generateSeed(){

        return System.currentTimeMillis();
    }
    public static Long getCurrentSeed() {

        // Extract the seed from the SharedPreferences once fetched or generated
        SharedPreferences sharedPreferences = getTournamentSharedPrefLocation();

        // read the Long seed from the attribute "seed" in the SharedPreferences
        long seed = sharedPreferences.getLong("seed", 0L);

        return seed;
    }


    // To be called when the current tournament is over => We retrieve and store the new seed
    public static CompletableFuture<Void> handleNewSeed(Long currentSeed){

        CompletableFuture<Void> future = new CompletableFuture<>();

        // check if new sew seed was generated
        // if so, store the fetched one in shared pref
        // otherwise, generate a new one, upload it, and store in shared pref

        fetchSeedIfAlreadyGenerated().thenAccept(seed -> {

            // If seed in DB != current one, another user already generated it, so no need to generate it again
            if (seed != null && !seed.equals(currentSeed)) {
                storeSeedInSharedPref(seed);
                future.complete(null);
            }

            // if seed null or same as current one, nobody generated the new seed yet, so we should generate it and upload it
            else {
                Long newlyGeneratedSeed = generateAndUploadSeed();
                storeSeedInSharedPref(newlyGeneratedSeed);
                future.complete(null);
            }
        });

        return future;
    }

    public static boolean seedAlreadyStoredInSharedPref() {
        SharedPreferences sharedPreferences = getTournamentSharedPrefLocation();
        return sharedPreferences.getLong("seed", -1) != -1;
    }
    private static void storeSeedInSharedPref(Long seed) {
        SharedPreferences sharedPreferences = getTournamentSharedPrefLocation();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("seed", seed);
        editor.apply();
    }

}
