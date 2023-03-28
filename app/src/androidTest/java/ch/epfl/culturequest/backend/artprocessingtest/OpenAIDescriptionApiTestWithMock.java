package ch.epfl.culturequest.backend.artprocessingtest;

import static org.junit.Assert.assertThrows;

import org.junit.Test;

import java.util.List;

import ch.epfl.culturequest.backend.artprocessing.apis.OpenAIDescriptionApi;
import ch.epfl.culturequest.backend.artprocessing.processingobjects.ArtRecognition;

public class OpenAIDescriptionApiTestWithMock {


    @Test
    public void getMissingDataWithStandardJsonReturnsCorrectData() {

        testWithGivenApiResponse("{\n" +
                "    \"designer\": \"The Rock\",\n" +
                "    \"yearOfInauguration\": \"2023\",\n" +
                "    \"locationCity\": \"Miami\",\n" +
                "    \"locationCountry\": \"United States\"\n" +
                "}");
    }

    @Test
    public void getMissingDataWithoutOnlyJsonParsesCorrectly(){
        testWithGivenApiResponse("Sure! Here's a JSON response for your prompt:\n" +
                "    {\n" +
                "    \"designer\": \"The Rock\",\n" +
                "    \"yearOfInauguration\": \"2023\",\n" +
                "    \"locationCity\": \"Miami\",\n" +
                "    \"locationCountry\": \"United States\"\n" +
                "}" +
                "Here's some more text that should be ignored ..."
        );
    }
    private void testWithGivenApiResponse(String mockText){
        MockOpenAiService mockOpenAiService = new MockOpenAiService("Mock API Key");
        mockOpenAiService.setMockResponse(mockText);
        OpenAIDescriptionApi openAPI = new OpenAIDescriptionApi(mockOpenAiService);
        List<String> missingData = openAPI.getMissingData(new ArtRecognition("Mock Art Name", "Mock additional information")).join();
        assert (missingData.get(0).equals("The Rock"));
        assert (missingData.get(1).equals("2023"));
        assert (missingData.get(2).equals("Miami"));
        assert (missingData.get(3).equals("United States"));

    }
    @Test
    public void getMissingDataWithInvalidJsonThrowsException(){

        MockOpenAiService mockOpenAiService = new MockOpenAiService("Mock API Key");
        mockOpenAiService.setMockResponse("This is not a JSON response");
        OpenAIDescriptionApi openAPI = new OpenAIDescriptionApi(mockOpenAiService);
        assertThrows(RuntimeException.class, () -> openAPI.getMissingData(new ArtRecognition("Mock Art Name", "Mock additional information")).join());

    }

    @Test
    public void getMissingDataWithNullFieldsReturnsNullElements(){
        MockOpenAiService mockOpenAiService = new MockOpenAiService("Mock API Key");

        // set the mock response to a JSON object with null values (not string null)
        mockOpenAiService.setMockResponse("{\n" +
                "    \"designer\": null,\n" +
                "    \"yearOfInauguration\": null,\n" +
                "    \"locationCity\": null,\n" +
                "    \"locationCountry\": null\n" +
                "}");


        OpenAIDescriptionApi openAPI = new OpenAIDescriptionApi(mockOpenAiService);
        // check that every element of the list is null
        List<String> missingData = openAPI.getMissingData(new ArtRecognition("Mock Art Name", "Mock additional information")).join();
        for (String element : missingData){
            assert (element == null);
        }
    }

    @Test
    public void getMissingDataWithoutAFieldReturnsNull(){

        MockOpenAiService mockOpenAiService = new MockOpenAiService("Mock API Key");
        mockOpenAiService.setMockResponse("{\n" +
                "    \"yearOfInauguration\": \"2023\",\n" +
                "    \"locationCity\": \"Miami\",\n" +
                "    \"locationCountry\": \"United States\"\n" +
                "}");

        OpenAIDescriptionApi openAPI = new OpenAIDescriptionApi(mockOpenAiService);
        List<String> missingData = openAPI.getMissingData(new ArtRecognition("Mock Art Name", "Mock additional information")).join();
        assert (missingData.get(0) == null);

    }

}
