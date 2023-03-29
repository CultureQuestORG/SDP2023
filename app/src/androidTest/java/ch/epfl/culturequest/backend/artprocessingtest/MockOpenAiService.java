package ch.epfl.culturequest.backend.artprocessingtest;

import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;

import java.util.List;

public class MockOpenAiService extends OpenAiService {


    public MockOpenAiService(String apiKey) {
        super(apiKey);
    }

    private String mockResponse;

    @Override
    public ChatCompletionResult createChatCompletion(ChatCompletionRequest completionRequest) {
        // Return a mock JSON response here
        ChatCompletionResult result = new ChatCompletionResult();
        String jsonResponse = getMockResponse();
        ChatMessage responseMessage = new ChatMessage("ai", jsonResponse);
        ChatCompletionChoice mockChoice = new ChatCompletionChoice();
        mockChoice.setMessage(responseMessage);
        result.setChoices(List.of(mockChoice));
        return result;
    }

    public void setMockResponse(String mockResponse) {
        this.mockResponse = mockResponse;
    }

    public String getMockResponse() {
        return mockResponse;
    }


}
