package ch.epfl.culturequest.backend.tournament.apis;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.epfl.culturequest.backend.artprocessing.processingobjects.ArtRecognition;
import ch.epfl.culturequest.backend.exceptions.OpenAiFailedException;
import ch.epfl.culturequest.backend.tournament.tournamentobjects.ArtQuiz;
import ch.epfl.culturequest.backend.tournament.tournamentobjects.QuizQuestion;

public class QuizGeneratorApi {

    private OpenAiService service;

    /*
    private final String quizGenerationPrompt = "Given the art \"David (Michelangelo)\", generate a quiz of 5 difficult questions in JSON. ";
    private String quizGenerationPromptX = "Given the art: \"%s\", write a quiz of 5 questions including 3 wrong and 1 correct possible answer in each. The true answer is indicated by its index. Return in JSON.";
    private final String testPrompt3 = "Given the art: \"Mona Lisa\", write a quiz of 5 questions with 4 options in each, with the correct answer indicated by its index. Return your response in JSON.";
    private final String testPrompt4 = "Given the art: \"Mona Lisa\", write a quiz (in JSON) of 5 questions with 4 options in each, with the correct answer indicated by its index.";
    private final String testPrompt5 = "Given the art: \"Mona Lisa\", write a quiz (in JSON) of 5 questions with 4 options in each, with the correct answer indicated by its index. Directly return the array including all the questions without beginning with any title key.";
    */

    private final static String quizGenerationPrompt ="Given the art \"David (Michelangelo)\", generate a quiz of 5 difficult questions in JSON, with 4 options in each, 3 wrong and 1 correct answer. As the \"options\" list four questions, the answer should always be among them!!";


    public QuizGeneratorApi(OpenAiService service){
        this.service = service;
    }


    public CompletableFuture<ArtQuiz> generateArtQuiz(String artName){

        CompletableFuture<String> jsonApiResponse = getJsonApiResponse(artName, quizGenerationPrompt);
        return jsonApiResponse.thenApply(this::parseQuiz);
    }

    private CompletableFuture<String> getJsonApiResponse(String artName, String quizPromptTemplate) {

        String prompt = String.format(quizPromptTemplate, artName);
        ChatMessage message = new ChatMessage("user", prompt);

        ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                .messages(List.of(message))
                .model("gpt-3.5-turbo")
                .maxTokens(1000)
                .n(1)
                .temperature(0.0)
                .build();

        return CompletableFuture
                .supplyAsync(
                        () -> service.createChatCompletion(completionRequest))
                .thenApply(
                        result -> result.getChoices().get(0).getMessage().getContent()
                )
                .exceptionally(e -> {
                    throw new CompletionException(new OpenAiFailedException("OpenAI failed to respond"));
                });
    }

    private ArtQuiz parseQuiz(String quizJson){

        ArrayList<QuizQuestion> quizQuestions = new ArrayList<>();

        try {

            JSONArray quizArray = parseJsonArrayFromString(quizJson);

            for (int i = 0; i < quizArray.length(); i++) {
                JSONObject questionObject = quizArray.getJSONObject(i);
                QuizQuestion quizQuestion = parseQuestion(questionObject);

                quizQuestions.add(quizQuestion);
            }
        }

        catch (Exception e){
            throw new CompletionException(new OpenAiFailedException("Quiz parsing failed - Questions not parsed correctly"));
        }

        return new ArtQuiz(quizQuestions);
    }

    private QuizQuestion parseQuestion(JSONObject questionObject) throws JSONException{

        String question = questionObject.getString("question");

        JSONArray optionsArray = questionObject.getJSONArray("options");
        ArrayList<String> options = new ArrayList<>();
        for (int j = 0; j < optionsArray.length(); j++) {
            options.add(optionsArray.getString(j));
        }

        ArrayList<String> randomizedOptions = randomizeOptions(options);
        int correctAnswerIndex = getCorrectAnswerIndex(randomizedOptions, questionObject.getString("answer"));

        QuizQuestion quizQuestion = new QuizQuestion(question, randomizedOptions, correctAnswerIndex);

        return quizQuestion;
    }

    private JSONArray parseJsonArrayFromString(String jsonResponse) throws JSONException {

        Pattern pattern = Pattern.compile("\\[\\s*\\{.*\\s*\\]", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(jsonResponse);

        if(matcher.find()){
            String jsonArrayString = matcher.group(0);
            return new JSONArray(jsonArrayString);
        }

        throw new CompletionException(new OpenAiFailedException("Quiz parsing failed - JSON array not found"));
    }

    private ArrayList<String> randomizeOptions(ArrayList<String> options){

        // change the order of the options randomly
        ArrayList<String> randomizedOptions = new ArrayList<>();
        while (options.size() > 0){
            int randomIndex = (int) (Math.random() * options.size());
            randomizedOptions.add(options.get(randomIndex));
            options.remove(randomIndex);
        }

        return randomizedOptions;
    }

    private int getCorrectAnswerIndex(ArrayList<String> options, String answer){

        for (int i = 0; i < options.size(); i++) {
            if (options.get(i).equals(answer)){
                return i;
            }
        }

        return 0; // If the correct answer is not found, we return 0 as the correct answer index (so that we're not penalized too much by ChatGPT stupidity ...)

    }
}