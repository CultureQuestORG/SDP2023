package ch.epfl.culturequest.backend.artprocessing.apis;

import com.theokanning.openai.service.OpenAiService;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Function;

import ch.epfl.culturequest.BuildConfig;
import ch.epfl.culturequest.backend.artprocessing.processingobjects.ArtRecognition;
import ch.epfl.culturequest.backend.artprocessing.processingobjects.BasicArtDescription;
import ch.epfl.culturequest.backend.exceptions.OpenAiFailedException;
import ch.epfl.culturequest.backend.exceptions.RecognitionFailedException;
import ch.epfl.culturequest.database.Database;

/**
 * This class combines the results of the WikipediaDescriptionApi and the OpenAIDescriptionApi to get a complete description of the scanned art
 * If the art type is painting or sculpture, the WikipediaDescriptionApi is enough
 * If the art type is architecture or monument, the OpenAIDescriptionApi is used to get the missing data (artist, year, city, country)
 */

public class GeneralDescriptionApi {

    private WikipediaDescriptionApi wikipediaDescriptionApi;
    private OpenAiService service;

    private static final int DEFAULT_SCORE = 50;

    public GeneralDescriptionApi(WikipediaDescriptionApi wikipediaDescriptionApi, OpenAiService openAiService) {
        this.wikipediaDescriptionApi = wikipediaDescriptionApi;
        this.service = openAiService;
    }

    private BasicArtDescription savedWikipediaDescription;

    enum RecoveryState {
        PartialRecovery,
        FullRecovery,
    }

    public CompletableFuture<BasicArtDescription> getArtDescription(ArtRecognition recognizedArt) {

        return wikipediaDescriptionApi.getArtDescription(recognizedArt)
                .thenCompose(basicArtDescription -> {
                        savedWikipediaDescription = basicArtDescription; // We save it in case of subsequent Open AI failure
                        return recoverPotentialMissesAndGetScore(basicArtDescription, recognizedArt, RecoveryState.PartialRecovery, 1); // Wikipedia didn't fail so it either provided a full description or an incomplete one
                    })
                .handle((filledArtDescription, e) -> {
                    if (e != null) {  // Wikipedia or Open AI error

                       // If Open Ai failed and we are in a partial recovery state (since Wikipedia didn't fail), we attempt a second partial recovery with Open AI
                       if(openAiRecoverableFail(e) && savedWikipediaDescription != null){
                           return recoverPotentialMissesAndGetScore(savedWikipediaDescription, recognizedArt, RecoveryState.PartialRecovery, 2);
                       }

                       // if Wikipedia completely fails, we attempt a full recover with OpenAi
                       BasicArtDescription emptyArtDescription = new BasicArtDescription();
                       initializeDescriptionByRecognition(emptyArtDescription, recognizedArt); // initialize the description with the data we already have
                       return recoverPotentialMissesAndGetScore(emptyArtDescription, recognizedArt, RecoveryState.FullRecovery, 2);
                    }

                    Database.setArtwork(filledArtDescription);
                    return CompletableFuture.completedFuture(filledArtDescription); // No initial missing data or Open AI successfully provided the score and recovered the potential missing data
                }).thenCompose(Function.identity());
    }

    private CompletableFuture<BasicArtDescription> recoverPotentialMissesAndGetScore(BasicArtDescription incompleteDescription, ArtRecognition recognizedArt, RecoveryState recoveryState, int tryCount) {

        OpenAIDescriptionApi openAIDescriptionApi = new OpenAIDescriptionApi(service);
        CompletableFuture<Integer> score = openAIDescriptionApi.getScore(recognizedArt);

        ArrayList<String> nullFields = getNullFields(incompleteDescription, recoveryState);

        if (nullFields.size() == 0) { // No missing data, we just have to provide the score. Even it fails here, it doesn't really matter. We just set the score to the default value and still return the object.
            return score
                    .thenApply(s -> {
                        incompleteDescription.setScore(s);
                        return incompleteDescription;
                    })
                    .exceptionally(e -> {
                        incompleteDescription.setScore(DEFAULT_SCORE);
                        Database.setArtwork(incompleteDescription);
                        return incompleteDescription;
                    });
        }


        CompletableFuture<Map<String, String>> missingData = openAIDescriptionApi.getMissingData(recognizedArt, nullFields);

        return missingData.
                thenCombine(score, (data, s) -> {

                    // fill the incomplete basicArtDescription with the missing data & score

                    for (String key : data.keySet()) {
                        try {
                            Field field = BasicArtDescription.class.getDeclaredField(key);
                            field.setAccessible(true);

                            field.set(incompleteDescription, data.get(key));
                        } catch (NoSuchFieldException | IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }

                    // If Open AI successfully helped for the recovery, we indicate than Open AI was used
                    incompleteDescription.setRequiredOpenAi(true);

                    incompleteDescription.setScore(s);

                    if(incompleteDescription.getMuseum().isEmpty() || incompleteDescription.getMuseum().equals("") || incompleteDescription.getMuseum() == null){
                        incompleteDescription.setMuseum("none");
                    }

                    if(incompleteDescription.getCity() == null || incompleteDescription.getCity().isEmpty() || incompleteDescription.getCity().equals("")){
                        incompleteDescription.setCity("none");
                    }

                    if(incompleteDescription.getCountry() == null || incompleteDescription.getCountry().isEmpty() || incompleteDescription.getCountry().equals("")){
                        incompleteDescription.setCountry("none");
                    }

                    Database.setArtwork(incompleteDescription);
                    return incompleteDescription;

                })
                .exceptionally(e -> {
                    if (tryCount == 2) {

                        // if both Wikipedia fails and OpenAi fails or Open AI failed two times in a row, we throw an exception leading to a displayed error message in the app
                        throw new CompletionException(new OpenAiFailedException("Open AI critical fail")); // forward the exception to Processing API, it won't get handled by the handle method of getArtDescription method
                    }
                    else {  // Partial recovery

                        throw new CompletionException(new RecognitionFailedException("Open AI recoverable fail")); // forward the exception to handle of getArtDescription method
                    }
                });
    }

    // know which fields are null, the ones Wikipedia didn't find (ambiguity, page doesn't exist, failure, etc.)
    // triggered in partial recovery case
    private ArrayList<String> getNullFields(BasicArtDescription basicArtDescription, RecoveryState recoveryState) {

        ArrayList<String> missingFields = new ArrayList<>();

        // Get all declared fields (including private ones)
        Field[] fields = BasicArtDescription.class.getDeclaredFields();

        // Iterate over the fields
        for (Field field : fields) {

            // Make the field accessible (to access private fields)
            field.setAccessible(true);

            // Get the name and value of the field
            String fieldName = field.getName();
            Object fieldValue;

            if (isIrrelevantFieldForRecovery(fieldName, basicArtDescription.getType())) {
                continue;
            }

            if (recoveryState == RecoveryState.PartialRecovery) {
                try {
                    fieldValue = field.get(basicArtDescription);
                    // if fieldValue is null, add the field name to the list of missing data
                    if (fieldValue == null || fieldValue.equals("")) {
                        missingFields.add(fieldName);
                    }
                    else{
                        if (fieldName == "summary" && fieldValue.toString().length() < 450) {
                            basicArtDescription.setSummary("");
                            missingFields.add(fieldName);
                        }
                    }

                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            // recoveryState == RecoveryState.FullRecovery
            else {
                missingFields.add(fieldName);
            }
        }

        return missingFields;
    }

    // Fields that are not relevant to the OpenAI prompt for missing data
    private Boolean isIrrelevantFieldForRecovery(String fieldName, BasicArtDescription.ArtType artType){

        // if art type is of type architecture, the museum is irrelevant
        if (artType == BasicArtDescription.ArtType.ARCHITECTURE && Objects.equals(fieldName, "museum")) {
            return true;
        }

        return fieldName == "score" || fieldName == "requiredOpenAi" || fieldName == "type" || fieldName == "name";
    }

    private void initializeDescriptionByRecognition(BasicArtDescription basicArtDescription, ArtRecognition recognizedArt) {
        basicArtDescription.setName(recognizedArt.getArtName());
        basicArtDescription.setType(WikipediaDescriptionApi.getArtType(recognizedArt));
    }

    private boolean openAiRecoverableFail(Throwable exception){
        return exception.getCause() instanceof OpenAiFailedException && exception.getMessage().contains("Open AI recoverable fail");
    }

}
