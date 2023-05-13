package ch.epfl.culturequest.backend.artprocessing.apis;

import com.theokanning.openai.service.OpenAiService;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Function;

import ch.epfl.culturequest.BuildConfig;
import ch.epfl.culturequest.backend.artprocessing.processingobjects.ArtRecognition;
import ch.epfl.culturequest.backend.artprocessing.processingobjects.BasicArtDescription;
import ch.epfl.culturequest.backend.exceptions.OpenAiFailedException;
import ch.epfl.culturequest.database.Database;

/**
 * This class combines the results of the WikipediaDescriptionApi and the OpenAIDescriptionApi to get a complete description of the scanned art
 * If the art type is painting or sculpture, the WikipediaDescriptionApi is enough
 * If the art type is architecture or monument, the OpenAIDescriptionApi is used to get the missing data (artist, year, city, country)
 */

public class GeneralDescriptionApi {

    public static OpenAiService service = new OpenAiService(BuildConfig.OPEN_AI_API_KEY);

    enum RecoveryState {

        PartialRecovery,
        FullRecovery,
    }

    public CompletableFuture<BasicArtDescription> getArtDescription(ArtRecognition recognizedArt) {

        return new WikipediaDescriptionApi().getArtDescription(recognizedArt)
                .thenCompose(basicArtDescription -> recoverPotentialMissesAndGetScore(basicArtDescription, recognizedArt, RecoveryState.PartialRecovery))
                .handle((filledArtDescription, e) -> {
                    if (e != null && !openAiFullRecoveryFailed(e)){                    // if Wikipedia fails, we attempt a full recover with OpenAi
                        BasicArtDescription emptyArtDescription = new BasicArtDescription();
                        initializeDescriptionByRecognition(emptyArtDescription, recognizedArt); // initialize the description with the data we already have
                        return recoverPotentialMissesAndGetScore(emptyArtDescription, recognizedArt, RecoveryState.FullRecovery);
                    }

                    else if( e != null && openAiFullRecoveryFailed(e)){ // if both Wikipedia fails and OpenAi fails, we throw an exception leading to an error message
                        throw new CompletionException(new OpenAiFailedException("OpenAi failed the complete recovery of the missing data"));
                    }

                    return CompletableFuture.completedFuture(filledArtDescription);
                }).thenCompose(Function.identity());
    }

    private CompletableFuture<BasicArtDescription> recoverPotentialMissesAndGetScore(BasicArtDescription incompleteDescription, ArtRecognition recognizedArt, RecoveryState recoveryState) {

        OpenAIDescriptionApi openAIDescriptionApi = new OpenAIDescriptionApi(service);
        CompletableFuture<Integer> score = openAIDescriptionApi.getScore(recognizedArt);

        ArrayList<String> missingFields = getNullFields(incompleteDescription, recoveryState);

        if (missingFields.size() == 0) {
            return score
                    .thenApply(s -> {
                        incompleteDescription.setScore(s);
                        Database.setArtwork(incompleteDescription);
                        return incompleteDescription;
                    })
                    .exceptionally(e -> incompleteDescription);
        }


        CompletableFuture<Map<String, Object>> missingData = openAIDescriptionApi.getMissingData(recognizedArt, missingFields);

        return missingData.
                thenCombine(score, (data, s) -> {
                    // iterate over the data keys, and set the corresponding field in basicArtDescription
                    for (String key : data.keySet()) {
                        try {
                            Field field = BasicArtDescription.class.getDeclaredField(key);
                            field.setAccessible(true);

                            field.set(incompleteDescription, (String) data.get(key));
                        } catch (NoSuchFieldException | IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }

                    incompleteDescription.setScore(s);

                    return incompleteDescription;

                })
                .exceptionally(e -> {
                    if (recoveryState == RecoveryState.FullRecovery) {
                        throw new CompletionException(new OpenAiFailedException("OpenAI failed to fully recover the missing data"));
                    }
                    else {  // Partial recovery
                        return incompleteDescription;
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

            if (isIrrelevantFieldForRecovery(fieldName)) {
                continue;
            }

            if (recoveryState == RecoveryState.PartialRecovery) {
                try {
                    fieldValue = field.get(basicArtDescription);
                    // if fieldValue is null, add the field name to the list of missing data
                    if (fieldValue == null) {
                        missingFields.add(fieldName);
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
    private Boolean isIrrelevantFieldForRecovery(String fieldName){
        return fieldName == "score" || fieldName == "requiredOpenAi" || fieldName == "type" || fieldName == "name";
    }

    private void initializeDescriptionByRecognition(BasicArtDescription basicArtDescription, ArtRecognition recognizedArt) {
        basicArtDescription.setName(recognizedArt.getArtName());
        basicArtDescription.setType(WikipediaDescriptionApi.getArtType(recognizedArt));
    }

    private boolean openAiFullRecoveryFailed(Throwable exception){
        return exception.getCause() instanceof OpenAiFailedException && exception.getMessage().contains("OpenAI failed to fully recover the missing data");
    }

}
