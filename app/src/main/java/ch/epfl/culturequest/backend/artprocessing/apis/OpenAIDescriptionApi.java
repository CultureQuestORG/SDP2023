package ch.epfl.culturequest.backend.artprocessing.apis;


import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import ch.epfl.culturequest.backend.artprocessing.processingobjects.ArtRecognition;


/**
 * This class is used to get a description's missing data from the OpenAI API
 * The missing data are: artist, year, city, country
 */
public class OpenAIDescriptionApi {
    private final String missingDataPrompt = "Given the input \"%s (%s)\", fill following fields: designer, yearOfInauguration, locationCity, locationCountry. Return your response as a JSON object.";

    private final String scorePrompt = "On a scale from 1 to 100 (ceil round to 10), evaluate the popularity of \"%s (%s)\". Fill the field \"artPopularity\", as JSON.";


    private OpenAiService service;

    public OpenAIDescriptionApi(OpenAiService service){
        this.service = service;
    }

    // make a function that returns a completable future of an array containing the artistName, yearOfCreation, locationCity, locationCountry, given the artRecognition object

    public CompletableFuture<ArrayList<String>> getMissingData(ArtRecognition recognizedArchitecture) {
        return getJsonApiResponse(recognizedArchitecture, missingDataPrompt).thenApply(this::parseMissingData);
    }

    public CompletableFuture<Integer> getScore(ArtRecognition recognizedArchitecture) {
        return getJsonApiResponse(recognizedArchitecture, scorePrompt).thenApply(this::parseScore);
    }

    private CompletableFuture<String> getJsonApiResponse(ArtRecognition recognizedArchitecture, String openAiPromptTemplate) {

        String prompt = String.format(openAiPromptTemplate, recognizedArchitecture.getArtName(), recognizedArchitecture.getAdditionalInfo());
        ChatMessage message = new ChatMessage("user", prompt);

        ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                .messages(List.of(message))
                .model("gpt-3.5-turbo")
                .n(1)
                .temperature(0.0)
                .build();

        return CompletableFuture.supplyAsync( () -> service.createChatCompletion(completionRequest))
                    .thenApply(result -> result.getChoices().get(0).getMessage().getContent());
    }

    private String getStringOrNull(JSONObject obj, String key) {
        String value = obj.optString(key, null);
        return "null".equals(value) ? null : value;
    }
    private ArrayList<String> parseMissingData(String jsonData){

        try {
            JSONObject obj = new JSONObject(extractJson(jsonData));

            ArrayList<String> data = new ArrayList<>();
            data.add(getStringOrNull(obj, "designer"));
            data.add(getStringOrNull(obj, "yearOfInauguration"));
            data.add(getStringOrNull(obj, "locationCity"));
            data.add(getStringOrNull(obj, "locationCountry"));

            return data;
        }

        catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private Integer parseScore (String jsonData) {
        try {
            JSONObject obj = new JSONObject(extractJson(jsonData));

            return obj.getInt("artPopularity");
        }
        catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    String extractJson(String s) {
        return s.substring(s.indexOf("{"), s.lastIndexOf("}") + 1);
    }
}