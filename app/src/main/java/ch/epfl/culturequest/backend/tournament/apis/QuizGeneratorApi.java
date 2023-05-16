package ch.epfl.culturequest.backend.tournament.apis;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import ch.epfl.culturequest.backend.artprocessing.processingobjects.ArtRecognition;
import ch.epfl.culturequest.backend.exceptions.OpenAiFailedException;
import ch.epfl.culturequest.backend.tournament.tournamentobjects.ArtQuiz;
import ch.epfl.culturequest.backend.tournament.tournamentobjects.QuizQuestion;

public class QuizGeneratorApi {

    private OpenAiService service;
    private String quizGenerationPrompt = "Given the art: \"%s\", write a quiz of 5 questions including 3 wrong and 1 correct possible answer in each. The true answer is indicated by its index. Return in JSON.";

    public QuizGeneratorApi(OpenAiService service){
        this.service = service;
    }


    public CompletableFuture<ArtQuiz> generateArtQuiz(String artName){


        return getJsonApiResponse(artName, quizGenerationPrompt).thenApply(this::parseQuiz);
    }

    private CompletableFuture<String> getJsonApiResponse(String artName, String openAiPromptTemplate) {

        String prompt = String.format(openAiPromptTemplate, artName);
        ChatMessage message = new ChatMessage("user", prompt);

        ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                .messages(List.of(message))
                .model("gpt-3.5-turbo")
                .n(1)
                .maxTokens(1000)
                .temperature(0.0)
                .build();

        return CompletableFuture.supplyAsync( () -> service.createChatCompletion(completionRequest))
                .thenApply(result -> result.getChoices().get(0).getMessage().getContent())
                .exceptionally(e -> {
                    throw new CompletionException(new OpenAiFailedException("OpenAI failed to respond"));
                });
    }

    private ArtQuiz parseQuiz(String quizJson){

        ArrayList<QuizQuestion> quizQuestions = new ArrayList<>();

        try {


            JSONObject jsonObject = new JSONObject(quizJson);
            JSONArray quizArray = jsonObject.getJSONArray("quiz");

            for (int i = 0; i < quizArray.length(); i++) {
                JSONObject questionObject = quizArray.getJSONObject(i);
                QuizQuestion quizQuestion = parseQuestion(questionObject);

                quizQuestions.add(quizQuestion);
            }
        }

        catch (Exception e){
            throw new CompletionException(new OpenAiFailedException("Quiz parsing failed"));
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

        int correctAnswerIndex = questionObject.getInt("answer");

        QuizQuestion quizQuestion = new QuizQuestion(question, options, correctAnswerIndex);

        return quizQuestion;
    }
}