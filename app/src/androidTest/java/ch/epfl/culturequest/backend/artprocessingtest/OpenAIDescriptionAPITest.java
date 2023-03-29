package ch.epfl.culturequest.backend.artprocessingtest;

import com.theokanning.openai.service.OpenAiService;

import org.junit.Test;

import java.util.ArrayList;

import ch.epfl.culturequest.BuildConfig;
import ch.epfl.culturequest.backend.artprocessing.apis.OpenAIDescriptionApi;
import ch.epfl.culturequest.backend.artprocessing.processingobjects.ArtRecognition;

public class OpenAIDescriptionAPITest {


    @Test
    public void getMissingDataArcDeTriompheReturnsCorrectData() {

        ArtRecognition arcDeTriompheRecognition = new ArtRecognition("Arc de Triomphe", "Monument");
        OpenAiService openAiService = new OpenAiService(BuildConfig.OPEN_AI_API_KEY);
        OpenAIDescriptionApi openAPI = new OpenAIDescriptionApi(openAiService);
        ArrayList<String> missingData = openAPI.getMissingData(arcDeTriompheRecognition).join();

        assert (missingData.get(0).equals("Jean-François-Thérèse Chalgrin"));
        assert (missingData.get(1).equals("1836"));
        assert (missingData.get(2).equals("Paris"));
        assert (missingData.get(3).equals("France"));
    }
}
