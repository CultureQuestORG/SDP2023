package ch.epfl.culturequest.backend.artprocessing.apis;

import android.util.Pair;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import ch.epfl.culturequest.backend.artprocessing.processingobjects.ArtRecognition;
import ch.epfl.culturequest.backend.artprocessing.processingobjects.BasicArtDescription;
import ch.epfl.culturequest.backend.exceptions.OpenAiFailedException;

public class OpenAIDescriptionApi {

    private final String missingDataPrompt = "Given the input \"%s (%s)\", fill following fields: %s. Return your response as a JSON object.";
    private final String scorePrompt = "On a scale from 1 to 100 (ceil round to 10), evaluate the popularity of \"%s (%s)\". Fill the field \"artPopularity\", as JSON.";

    private ArrayList<String> missingDataNames;
    private OpenAiService service;


    enum ResponseDataType {
        MISSING_DATA,
        SCORE
    }

    enum FieldType {
        STRING,
        INTEGER
    }

    public OpenAIDescriptionApi(OpenAiService service) {
        this.service = service;
    }

    // make a function that returns a completable future of an array containing the artistName, yearOfCreation, locationCity, locationCountry, given the artRecognition object

    public CompletableFuture<Map<String, Object>> getMissingData(ArtRecognition recognizedArchitecture, ArrayList<String> missingDataNames) {
        BasicArtDescription.ArtType artType = WikipediaDescriptionApi.getArtType(recognizedArchitecture);
        this.missingDataNames = getPromptReadyMissingFieldsList(missingDataNames, artType);
        return getJsonApiResponse(recognizedArchitecture, ResponseDataType.MISSING_DATA).thenApply(this::parseApiResponse);
    }

    public CompletableFuture<Integer> getScore(ArtRecognition recognizedArchitecture) {
        return getJsonApiResponse(recognizedArchitecture, ResponseDataType.SCORE).
                thenApply( jsonResponse -> (Integer) parseApiResponse(jsonResponse).get("score"));
    }

    private CompletableFuture<String> getJsonApiResponse(ArtRecognition recognizedArchitecture, ResponseDataType dataType) {

        String prompt;

        switch (dataType) {

            case MISSING_DATA:
                String promptReadyMissingData = String.join(", ", missingDataNames);
                prompt = String.format(missingDataPrompt, recognizedArchitecture.getArtName(), recognizedArchitecture.getAdditionalInfo(), promptReadyMissingData);
                break;

            case SCORE:
                prompt = String.format(scorePrompt, recognizedArchitecture.getArtName(), recognizedArchitecture.getAdditionalInfo());
                break;

            default:
                throw new IllegalArgumentException("Invalid response data type");
        }

        ChatMessage message = new ChatMessage("user", prompt);

        ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                .messages(List.of(message))
                .model("gpt-3.5-turbo")
                .n(1)
                .temperature(0.0)
                .build();

        return CompletableFuture.supplyAsync(() -> service.createChatCompletion(completionRequest))
                .thenApply(result -> result.getChoices().get(0).getMessage().getContent())
                .exceptionally(e -> {
                    throw new CompletionException(new OpenAiFailedException("OpenAI failed to respond"));
                });
    }

    private Map<String, Object> parseApiResponse(String jsonData) {

        Map<String, Object> parsedData = new HashMap<>();

        try {
            JSONObject jsonObject = new JSONObject(extractJson(jsonData));

            // iterate over the keys in the json object and add them to the dictionary
            jsonObject.keys() // returns an iterator
                    .forEachRemaining(key -> {
                        Pair<String, FieldType> normalizedField = normalizeFieldAndGetType(key);
                        String normalizedKey = normalizedField.first;
                        FieldType fieldType = normalizedField.second;

                        switch (fieldType) {
                            case STRING:
                                parsedData.put(normalizedKey, jsonObject.optString(key, null));
                                break;
                            case INTEGER:
                                parsedData.put(normalizedKey, jsonObject.optInt(key, 50));
                                break;
                        }
                    });
        } catch (Exception ex) {
            throw new CompletionException(new OpenAiFailedException("OpenAI failed to provide JSON data"));
        }

        return parsedData;
    }

    String extractJson(String s) {
        return s.substring(s.indexOf("{"), s.lastIndexOf("}") + 1);
    }


    // Depending on the art type, we might ask different field names referring to the same thing (e.g. designer vs artist) so need normalization
    private Pair<String, FieldType> normalizeFieldAndGetType(String jsonKey) {

        switch(jsonKey) {
            case "designer" :
            case "artistName":
                return new Pair<>("artist", FieldType.STRING);

            case "yearOfCreation":
            case "yearOfInauguration":
                return new Pair<>("year", FieldType.INTEGER);

            case "locationCity":
                return new Pair<>("city", FieldType.STRING);

            case "locationCountry":
                return new Pair<>("country", FieldType.STRING);

            case "description":
                return new Pair<>("summary", FieldType.STRING);
            case "currentMuseum":
                return new Pair<>("museum", FieldType.STRING);
            case "artPopularity":
                return new Pair<>("score", FieldType.INTEGER);

            default:
                throw new CompletionException(new OpenAiFailedException("Unexpected missing data field name"));
        }
    }

    private ArrayList<String> getPromptReadyMissingFieldsList(ArrayList<String> missingFields, BasicArtDescription.ArtType artType){

        ArrayList<String> promptReadyMissingFields = new ArrayList<>();

        for(String missingField : missingFields){
            promptReadyMissingFields.add(getOptimalPromptFieldName(missingField, artType));
        }

        return promptReadyMissingFields;
    }

    private String getOptimalPromptFieldName(String missingFieldName, BasicArtDescription.ArtType artType){

        String promptFieldName = "";

        switch (missingFieldName) {
            case "artist":

                if(isPaintingOrSculpture(artType)){
                    promptFieldName = "artistName";
                }
                else {
                    promptFieldName = "designer";
                }
                break;

            case "year":
                if(isPaintingOrSculpture(artType)){
                    promptFieldName = "yearOfCreation";
                }
                else {
                    promptFieldName = "yearOfInauguration";
                }
                break;

            case "city":
                promptFieldName = "locationCity";
                break;

            case "country":
                promptFieldName = "locationCountry";
                break;

            case "summary":
                promptFieldName = "description (4 to 6 sentences)";
                break;

            default:
                promptFieldName = "";
        }

        return promptFieldName;
    }

    private Boolean isPaintingOrSculpture(BasicArtDescription.ArtType artType){
        return artType == BasicArtDescription.ArtType.PAINTING || artType == BasicArtDescription.ArtType.SCULPTURE;
    }

}