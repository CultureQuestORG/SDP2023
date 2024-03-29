package ch.epfl.culturequest.backend.tournament.apis;

import static androidx.test.InstrumentationRegistry.getTargetContext;
import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static ch.epfl.culturequest.backend.tournament.apis.SeedApi.generateOrFetchSeedThenStore;
import static ch.epfl.culturequest.backend.tournament.apis.SeedApi.getCurrentSeed;
import static ch.epfl.culturequest.backend.tournament.apis.SeedApi.handleNewSeed;
import static ch.epfl.culturequest.backend.tournament.apis.SeedApi.seedAlreadyStoredInSharedPref;
import static ch.epfl.culturequest.backend.tournament.utils.RandomApi.generateWeeklyTournamentDate;
import static ch.epfl.culturequest.database.Database.fetchSeedIfAlreadyGenerated;
import static ch.epfl.culturequest.database.Database.handleFutureTimeout;
import static ch.epfl.culturequest.database.Database.indicateTournamentNotGenerated;
import static ch.epfl.culturequest.database.Database.isTournamentGenerationLocked;
import static ch.epfl.culturequest.database.Database.lockTournamentGeneration;
import static ch.epfl.culturequest.database.Database.unlockTournamentGeneration;
import static ch.epfl.culturequest.database.Database.uploadSeedToDatabase;
import static ch.epfl.culturequest.database.Database.uploadTournamentToDatabase;
import static ch.epfl.culturequest.database.Database.waitForTournamentGenerationAndFetchIt;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import com.google.gson.Gson;
import com.theokanning.openai.service.OpenAiService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.InputStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;
import ch.epfl.culturequest.BuildConfig;
import ch.epfl.culturequest.R;
import ch.epfl.culturequest.backend.tournament.tournamentobjects.ArtQuiz;
import ch.epfl.culturequest.backend.tournament.tournamentobjects.Tournament;
import ch.epfl.culturequest.backend.tournament.utils.RandomApi;

/**
    * Class in charge of the whole tournament backend management
    * The two accessible functions are: handleTournaments() and getTournamentFromSharedPref()
    * handleTournaments() should be called each time the main activity is created or resumed, it will handle the tournament scheduling and generation
    * getTournamentFromSharedPref() should be called each time the user wants to access the tournament after successfully stored or fetch, it will return the tournament stored in shared preferences
 */

@SuppressLint("NewApi")
public class TournamentManagerApi {

    public static OpenAiService service = new OpenAiService(BuildConfig.OPEN_AI_API_KEY, Duration.ofMinutes(2));
    private static Context currentContext;

    // Main function #1: To be called when most of activities are being resumed
    public static CompletableFuture<Void> handleTournaments(Context context) {

        currentContext = context;

        if (tournamentRemainingTime() == 0) { // A tournament has never been scheduled yet - first time the app is launched

            if(!seedAlreadyStoredInSharedPref()){
                return generateOrFetchSeedThenStore().thenAccept(x -> generateAndStoreTournamentDate());
            }
            generateAndStoreTournamentDate();

        } else if (tournamentRemainingTime() < 0) { // Tournament is over

            // Clear shared preferences, unlock all concurrency related variables, schedule the next tournament
            return setWeeklyTournamentOver(); // asynchronous call

        } else if (isTimeToGenerateTournament() && !tournamentAlreadyStoredInSharedPref()) {

            // generate or fetch tournament once and store it in Shared Preferences to access it easily later
            CompletableFuture<Void> future = generateOrFetchTournamentThenStore(); // asynchronous call
            handleFutureTimeout(future, 180); // timeout after 3 minutes

            return future;
        }
        return CompletableFuture.completedFuture(null);
    }
    // Main function #2: To be called to retrieve the tournament after it has been generated or fetched
    public static Tournament getTournamentFromSharedPref() {

        // Get shared preferences
        SharedPreferences sharedPreferences = getTournamentSharedPrefLocation();

        // Get the JSON string of the stored tournament
        String jsonTournament = sharedPreferences.getString("weeklyTournament", null);

        if (jsonTournament == null) {
            return null;
        }

        // Create a Gson instance
        Gson gson = new Gson();

        // Convert the JSON string back to a Tournament object
        Tournament tournament = gson.fromJson(jsonTournament, Tournament.class);

        return tournament;
    }

    private static CompletableFuture<Void> generateOrFetchTournamentThenStore() {

        CompletableFuture<Void> future = new CompletableFuture<>();

        // Generate or fetch tournament once and store it in Shared Preferences to access it easily later
        generateOrFetchTournament().thenAccept(tournament -> {
            if (tournament != null) {
                storeTournamentInSharedPref(tournament);
            }
            future.complete(null);
        });

        return future;
    }

    // If the tournament generation is locked, another device is currently generating the tournament so we should wait for it to be generated and fetch it from the database
    public static CompletableFuture<Tournament> fetchTournamentWhenGenerated(String tournamentId) {

        AtomicReference<Tournament> fetchedTournament = new AtomicReference<>();
        CompletableFuture<Tournament> future = new CompletableFuture<>();

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.schedule(() -> {
            if (fetchedTournament.get() == null) {
                // If the tournament hasn't been generated 2 minutes after the generation lock, it's likely that the device that was generating it crashed so we should unlock the generation and try to generate it again
                // we make the future complete exceptionally so that the caller (generateOrFetchTournament) can handle it and try to generate the tournament again if needed
                future.completeExceptionally(new TimeoutException("Failed to fetch the tournament from the database after 2 minutes"));
            }
        }, 2, TimeUnit.MINUTES);

        return waitForTournamentGenerationAndFetchIt(fetchedTournament, future, tournamentId);
    }

    private static void generateAndStoreTournamentDate() {

        // To be called only if tournamentRemainingTime <= 0 (i.e. tournament is over or has not been generated yet)
        if (tournamentRemainingTime() > 0) {
            return;
        }

        Calendar tournamentDate = generateWeeklyTournamentDate();

        // Store calendar.getTime() in shared preferences
        SharedPreferences sharedPref = getTournamentSharedPrefLocation();
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong("tournamentDate", tournamentDate.getTime().getTime());
        editor.apply();
    }

    private static Boolean isTimeToGenerateTournament() {

        long tournamentDate = getTournamentSharedPrefLocation().getLong("tournamentDate", 0);

        if (tournamentDate == 0) {
            return false;
        }

        Calendar calendar = Calendar.getInstance();
        long currentTime = calendar.getTime().getTime();

        return currentTime > tournamentDate;
    }

    // Remaining time from now until the tournament ends (tournament date + 1 week)
    // If tournament end date is in the past, return value is negative
    private static int tournamentRemainingTime() {

        // a tournament should last 1 week
        int tournamentDuration = 7 * 24 * 60 * 60 * 1000;

        long tournamentDate = getTournamentSharedPrefLocation().getLong("tournamentDate", 0);

        if (tournamentDate == 0) {
            return 0;
        }

        Calendar calendar = Calendar.getInstance();
        long currentTime = calendar.getTime().getTime();

        long remainingTime = tournamentDate + tournamentDuration - currentTime;

        return (int) remainingTime;

    }

    // If the tournament has not been generated yet, generate it and upload it to the database
    // If the tournament has already been generated, fetch it from the database and return it
    private static CompletableFuture<Tournament> generateOrFetchTournament() {

        return isTournamentGenerationLocked().thenCompose(generationLocked -> {
            if (generationLocked) {
                return fetchTournamentWhenGeneratedAndHandleGenerationTimeout();
            }
            else {
                // lock the tournament generation to prevent other users from generating it at the same time
                lockTournamentGeneration();

                //// Generate the tournament ////
                ArrayList<String> artNames = randomlyChooseArtNames();

                Map<String, CompletableFuture<ArtQuiz>> artQuizFutures = generateTournamentQuizzesGivenArtNames(artNames);

                CompletableFuture<ArtQuiz>[] futuresArray = artQuizFutures.values().toArray(new CompletableFuture[0]);

                // wait for all the art quizzes to be generated then create the tournament and upload it to the database
                return CompletableFuture.allOf(futuresArray)
                        .thenApply(x -> createAndUploadTournamentFromQuizzes(artQuizFutures));
            }
        });
    }

    // This method is responsible for fetching the tournament from database (call to fetchTournamentWhenGenerated) and handling the case where the tournament takes too long to be generated, in which case we unlock the generation and try to generate it again
    private static CompletableFuture<Tournament> fetchTournamentWhenGeneratedAndHandleGenerationTimeout(){

        String weeklyTournamentId = RandomApi.getWeeklyTournamentPseudoRandomUUID();

        return fetchTournamentWhenGenerated(weeklyTournamentId)
                .handle((result, ex) -> {
                    if (ex != null && ex.getCause() instanceof TimeoutException) {
                        if (ex.getCause() instanceof TimeoutException) {

                            // If the device that firstly took the generation lead at least 2 minutes after the lock didn't generate the tournament yet, we consider that this device is finito, so we unlock the generation, take the lead and generate the tournament
                            return unlockTournamentGeneration().thenCompose(unlocked -> {
                                if (unlocked) {
                                    return generateOrFetchTournament();
                                } else {
                                    throw new RuntimeException("Failed to unlock tournament generation", ex);
                                }
                            });
                        } else {
                            throw new CompletionException(ex);
                        }
                    } else {
                        return CompletableFuture.completedFuture(result);
                    }
                }).thenCompose(Function.identity());
    }

    private static Map<String, CompletableFuture<ArtQuiz>> generateTournamentQuizzesGivenArtNames(ArrayList<String> artNames) {

        Map<String, CompletableFuture<ArtQuiz>> artQuizFutures = new HashMap<>();

        Supplier<ArtQuiz> fallBack = () -> null;

        for (String artName : artNames) {
            Supplier<CompletableFuture<ArtQuiz>> quizGenerator = () -> new QuizGeneratorApi(service).generateArtQuiz(artName);
            CompletableFuture<ArtQuiz> artQuizFuture = RetryFuture.ExecWithRetryOrFallback(quizGenerator, fallBack, 2);
            artQuizFutures.put(artName, artQuizFuture);
        }

        return artQuizFutures;
    }

    private static Tournament createAndUploadTournamentFromQuizzes(Map<String, CompletableFuture<ArtQuiz>> completedQuizzesMappedByArtName){

        Map<String, ArtQuiz> artQuizzes = new HashMap<>();
        for (Map.Entry<String, CompletableFuture<ArtQuiz>> entry : completedQuizzesMappedByArtName.entrySet()) {
            try {

                ArtQuiz quiz = entry.getValue().get();

                // A null quiz means that the quiz generation failed after 2 retries, the tournament would fail to be generated so we should abort and unlock
                if (quiz == null) {
                    // unlock the tournament generation so that another user can try to generate it
                    unlockTournamentGeneration();
                    return null;
                }

                artQuizzes.put(entry.getKey(), quiz);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        Tournament tournament = new Tournament(artQuizzes);
        uploadTournamentToDatabase(tournament);

        return tournament;
    }

    private static ArrayList<String> randomlyChooseArtNames() {
        Random random = RandomApi.getRandom();

        ArrayList<String> artNames = new ArrayList<>();
        try {
            Resources resources = currentContext.getResources();
            InputStream inputStream = resources.openRawResource(R.raw.famous_arts);
            // InputStream to JSONArray
            String json;
            try (Scanner s = new Scanner(inputStream)) {
                json = s.useDelimiter("\\A").hasNext() ? s.next() : "";
            }
            JSONObject jsonObj = new JSONObject(json);
            JSONArray jsonArray = jsonObj.getJSONArray("artworks");

            // Randomly choose three artworks from the JSON array
            int numArtworks = jsonArray.length();
            int numArtNamesToChoose = 3;
            Set<Integer> chosenIndices = new HashSet<>();

            while (chosenIndices.size() < numArtNamesToChoose) {
                int randomIndex = random.nextInt(numArtworks);
                if (!chosenIndices.contains(randomIndex)) {
                    JSONObject artworkObject = jsonArray.getJSONObject(randomIndex);
                    String artName = artworkObject.getString("artName");
                    artNames.add(artName);
                    chosenIndices.add(randomIndex);
                }}
        } catch (JSONException e) {
            e.printStackTrace();}
        return artNames;
    }
    public static void storeTournamentInSharedPref(Tournament tournament) {

        SharedPreferences sharedPreferences = getTournamentSharedPrefLocation();
        Gson gson = new Gson();
        String jsonTournament = gson.toJson(tournament);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("weeklyTournament", jsonTournament);
        editor.apply();
    }

    private static CompletableFuture<Void> setWeeklyTournamentOver() {

        // We remember the current seed before cleaning the shared pref
        Long currentSeed = getCurrentSeed();

        clearTournamentSharedPref();

        CompletableFuture<Void> newSeedHandled = handleNewSeed(currentSeed).thenAccept(x -> {
            generateAndStoreTournamentDate();
        });

        CompletableFuture<Boolean> unlockTournamentGenerationFuture = unlockTournamentGeneration();
        CompletableFuture<Boolean> indicateTournamentNotGeneratedFuture = indicateTournamentNotGenerated();

        return CompletableFuture.allOf(unlockTournamentGenerationFuture, indicateTournamentNotGeneratedFuture, newSeedHandled);
    }

    public static SharedPreferences getTournamentSharedPrefLocation() {

        return currentContext.getSharedPreferences("tournament", Context.MODE_PRIVATE);
    }

    private static void clearTournamentSharedPref() {
        SharedPreferences sharedPreferences = getTournamentSharedPrefLocation();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
    private static boolean tournamentAlreadyStoredInSharedPref() {
        SharedPreferences sharedPreferences = getTournamentSharedPrefLocation();
        return sharedPreferences.getString("weeklyTournament", null) != null;
    }

}