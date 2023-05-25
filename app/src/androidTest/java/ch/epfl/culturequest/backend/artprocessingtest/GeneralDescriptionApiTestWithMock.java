package ch.epfl.culturequest.backend.artprocessingtest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThrows;

import com.theokanning.openai.service.OpenAiService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.time.Duration;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import ch.epfl.culturequest.BuildConfig;
import ch.epfl.culturequest.backend.artprocessing.apis.GeneralDescriptionApi;
import ch.epfl.culturequest.backend.artprocessing.processingobjects.ArtRecognition;
import ch.epfl.culturequest.backend.artprocessing.processingobjects.BasicArtDescription;
import ch.epfl.culturequest.backend.artprocessingtest.mocks.FailingWikipediaDescriptionApi;
import ch.epfl.culturequest.backend.artprocessingtest.mocks.IncompleteWikipediaDescriptionApi;
import ch.epfl.culturequest.backend.exceptions.OpenAiFailedException;
import ch.epfl.culturequest.database.Database;

public class GeneralDescriptionApiTestWithMock {


    @Before
    public void setUp() {
        // Set up the database to run on the local emulator of Firebase
        Database.setEmulatorOn();

        // clear the database before starting the following tests
        Database.clearDatabase();
    }

    @After
    public void tearDown() {
        // clear the database after the tests
        Database.clearDatabase();
    }

    ArtRecognition arcDeTriompheRecognition = new ArtRecognition("Arc de Triomphe", "Monument");


    @Test
    public void successfulFullRecoveryIfWikipediaFailing() {

        FailingWikipediaDescriptionApi failingWikipediaDescriptionApi = new FailingWikipediaDescriptionApi("useless");
        OpenAiService openAiService = new OpenAiService(BuildConfig.OPEN_AI_API_KEY, Duration.ofSeconds(40));

        GeneralDescriptionApi generalDescriptionApi = new GeneralDescriptionApi(failingWikipediaDescriptionApi, openAiService);

        BasicArtDescription basicArtDescription = generalDescriptionApi.getArtDescription(arcDeTriompheRecognition).join();

        ArrayList<String> nullFields = getNullFields(basicArtDescription);

        // check that the only nullField is "museum" (which is obviously not relevant to the monument Arc de Triomphe here)
        assertThat(nullFields.size(), is(1));
        assertThat(nullFields.get(0), is("museum"));
    }

    @Test
    public void successfulPartialRecoveryIfWikipediaIncomplete() {

        IncompleteWikipediaDescriptionApi incompleteWikipediaDescriptionApi = new IncompleteWikipediaDescriptionApi("useless");

        ArrayList<String> fieldsToBeNull = new ArrayList<>();
        fieldsToBeNull.add("artist");
        fieldsToBeNull.add("year");
        fieldsToBeNull.add("city");
        fieldsToBeNull.add("country");

        incompleteWikipediaDescriptionApi.indicateFieldsToBeNull(fieldsToBeNull);

        OpenAiService openAiService = new OpenAiService(BuildConfig.OPEN_AI_API_KEY);

        GeneralDescriptionApi generalDescriptionApi = new GeneralDescriptionApi(incompleteWikipediaDescriptionApi, openAiService);

        BasicArtDescription basicArtDescription = generalDescriptionApi.getArtDescription(arcDeTriompheRecognition).join();

        ArrayList<String> nullFields = getNullFields(basicArtDescription);

        // check that the fields "artist" "year" "city" "country" are not in the nullFields list
        assertThat(nullFields.contains("artist"), is(false));
        assertThat(nullFields.contains("year"), is(false));
        assertThat(nullFields.contains("city"), is(false));
        assertThat(nullFields.contains("country"), is(false));

    }

    @Test
    public void scoreCorrectlyAssignedIfWikipediaSuccessfulAndComplete() {

        IncompleteWikipediaDescriptionApi incompleteWikipediaDescriptionApi = new IncompleteWikipediaDescriptionApi("useless");
        // We don't set any null field to the mock wikipedia api, so it will return a complete description

        OpenAiService openAiService = new OpenAiService(BuildConfig.OPEN_AI_API_KEY);
        GeneralDescriptionApi generalDescriptionApi = new GeneralDescriptionApi(incompleteWikipediaDescriptionApi, openAiService);

        BasicArtDescription basicArtDescription = generalDescriptionApi.getArtDescription(arcDeTriompheRecognition).join();

        assertThat(basicArtDescription.getScore() > 70, is(true));

    }


    @Test
    public void OpenAiExceptionIfBothWikipediaAndOpenAIFailing() {
        FailingWikipediaDescriptionApi failingWikipediaDescriptionApi = new FailingWikipediaDescriptionApi("useless");
        MockOpenAiService mockOpenAiService = new MockOpenAiService("Useless");
        mockOpenAiService.setChatCompletionThrowsException(true);

        GeneralDescriptionApi generalDescriptionApi = new GeneralDescriptionApi(failingWikipediaDescriptionApi, mockOpenAiService);

        CompletableFuture<BasicArtDescription> future =
                generalDescriptionApi.getArtDescription(arcDeTriompheRecognition);

        CompletionException exception = assertThrows(CompletionException.class, future::join);

        assertThat(exception.getCause() instanceof OpenAiFailedException, is(true));
        assertThat(exception.getCause().getMessage(), is("OpenAI failed to fully recover the missing data"));
    }


    private ArrayList<String> getNullFields(BasicArtDescription recoveredBasicArtDescription) {
        Field[] fields = BasicArtDescription.class.getDeclaredFields();
        ArrayList<String> nullFields = new ArrayList<>();

        for (Field field : fields) {
            field.setAccessible(true); // You need to do this to access private fields
            try {
                if (field.get(recoveredBasicArtDescription) == null || field.get(recoveredBasicArtDescription).equals("")) {
                    nullFields.add(field.getName());
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return nullFields;
    }

    //todo: verify that WikipediaDescriptionApi correctly sets the value to null when can't retrieve it
}