package ch.epfl.culturequest.backend.artprocessingtest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import com.theokanning.openai.service.OpenAiService;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Map;

import ch.epfl.culturequest.BuildConfig;
import ch.epfl.culturequest.backend.artprocessing.apis.OpenAIDescriptionApi;
import ch.epfl.culturequest.backend.artprocessing.processingobjects.ArtRecognition;

public class OpenAIDescriptionAPITest {


    @Test
    public void getMissingDataArcDeTriompheReturnsCorrectData() {

        ArtRecognition arcDeTriompheRecognition = new ArtRecognition("Arc de Triomphe", "Monument");
        OpenAiService openAiService = new OpenAiService(BuildConfig.OPEN_AI_API_KEY);
        OpenAIDescriptionApi openAPI = new OpenAIDescriptionApi(openAiService);

        ArrayList<String> nullFields = new ArrayList<>();
        nullFields.add("artist");
        nullFields.add("year");
        nullFields.add("city");
        nullFields.add("country");

        Map<String, String> missingData = openAPI.getMissingData(arcDeTriompheRecognition, nullFields).join();
        Integer score = openAPI.getScore(arcDeTriompheRecognition).join();

        String retrievedArtist = missingData.get("artist");
        String retrievedYear = missingData.get("year");
        String retrievedCity = missingData.get("city");
        String retrievedCountry = missingData.get("country");

        assertThat(retrievedArtist, is("Jean-François-Thérèse Chalgrin"));
        assertThat(retrievedYear, is("1836"));
        assertThat(retrievedCity, is("Paris"));
        assertThat(retrievedCountry, is("France"));
        assertThat(score, is(80));
    }
}
