package ch.epfl.culturequest.backend.artprocessingtest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThrows;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;

import ch.epfl.culturequest.backend.artprocessing.apis.WikipediaDescriptionApi;
import ch.epfl.culturequest.backend.artprocessing.processingobjects.ArtRecognition;
import ch.epfl.culturequest.backend.artprocessing.processingobjects.BasicArtDescription;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.SocketPolicy;

public class WikipediaDescriptionApiTestWithMock {


    MockWebServer mockWebServer = new MockWebServer();

    WikipediaDescriptionApi wikipediaDescriptionApi = new WikipediaDescriptionApi();

    @Before
    public void setUp() throws Exception {
        mockWebServer.start(8080);
        wikipediaDescriptionApi.wikipediaBaseUrl = "http://localhost:8080/";
    }

    @Test
    public void exceptionWhenTimeout() {
        mockWebServer.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START));

        ArtRecognition artRecognition = new ArtRecognition("Mona Lisa", "Painting");

        // getArtDescription should return a future that is completed exceptionally
        CompletableFuture<BasicArtDescription> descriptionFuture = wikipediaDescriptionApi.getArtDescription(artRecognition);
        assertThrows(CompletionException.class, () -> descriptionFuture.join());
    }

    // test that summary, city, country, museum, year are null when the wikipedia api returns empty strings
    @Test
    public void nullFieldsWhenResponseEmpty() {
        mockWebServer.enqueue(new MockResponse().setBody("<html><body><p></p></body></html>").setResponseCode(200));
        ArtRecognition artRecognition = new ArtRecognition("Mona Lisa", "Painting");

        CompletableFuture<BasicArtDescription> descriptionFuture = wikipediaDescriptionApi.getArtDescription(artRecognition);
        BasicArtDescription description = descriptionFuture.join();
        assertThat(description.getSummary(), is(nullValue()));
        assertThat(description.getCity(), is(nullValue()));
        assertThat(description.getCountry(), is(nullValue()));
        assertThat(description.getMuseum(), is(nullValue()));
        assertThat(description.getYear(), is(nullValue()));
        assertThat(description.getArtist(), is(nullValue()));
    }

    // test that the future doesn't complete exceptionally when the wikipedia api returns a valid response after 5 seconds
    @Test
    public void noExceptionWhenValidResponseAfterTimeout() {
        mockWebServer.enqueue(new MockResponse().setBody("<html><body><p></p></body></html>").setResponseCode(200).setBodyDelay(5, TimeUnit.SECONDS));
        ArtRecognition artRecognition = new ArtRecognition("Mona Lisa", "Painting");
        CompletableFuture<BasicArtDescription> descriptionFuture = wikipediaDescriptionApi.getArtDescription(artRecognition);
        descriptionFuture.join();
    }

    // test that that the type is OTHER when the wikipedia api returns an unknown type
    @Test
    public void otherArtTypeWhenUnknownType() {
        mockWebServer.enqueue(new MockResponse().setBody("<html><body><p></p></body></html>").setResponseCode(200));
        ArtRecognition artRecognition = new ArtRecognition("Mona Lisa", "Unknown");
        CompletableFuture<BasicArtDescription> descriptionFuture = wikipediaDescriptionApi.getArtDescription(artRecognition);
        BasicArtDescription description = descriptionFuture.join();
        assertThat(description.getType(), is(BasicArtDescription.ArtType.OTHER));
    }

    @Test
    public void exceptionWhenBadHttpResponse() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(404));
        ArtRecognition artRecognition = new ArtRecognition("Mona Lisa", "Painting");
        CompletableFuture<BasicArtDescription> descriptionFuture = wikipediaDescriptionApi.getArtDescription(artRecognition);
        assertThrows(CompletionException.class, () -> descriptionFuture.join());
    }

    @Test
    public void nullCityWhenLocationHasNoCity() {
        mockWebServer.enqueue(new MockResponse().setBody("<html><body><p>Location</th><td class=\"infobox-data\"><a href=\"/wiki/Louvre\" title=\"Louvre\">Louvre</a></td></tr></tbody></table></p></body></html>").setResponseCode(200));
        ArtRecognition artRecognition = new ArtRecognition("Mona Lisa", "Painting");
        CompletableFuture<BasicArtDescription> descriptionFuture = wikipediaDescriptionApi.getArtDescription(artRecognition);
        BasicArtDescription description = descriptionFuture.join();
        assertThat(description.getCity(), is(nullValue()));
    }

    @Test
    public void nullCountryWhenLocationHasNoCountry(){
        mockWebServer.enqueue(new MockResponse().setBody("<html><body><p>Location</th><td class=\"infobox-data\"><a href=\"/wiki/Louvre\" title=\"Louvre\">Louvre</a>, Paris</td></tr></tbody></table></p></body></html>").setResponseCode(200));
        ArtRecognition artRecognition = new ArtRecognition("Mona Lisa", "Painting");
        CompletableFuture<BasicArtDescription> descriptionFuture = wikipediaDescriptionApi.getArtDescription(artRecognition);
        BasicArtDescription description = descriptionFuture.join();
        assertThat(description.getCountry(), is(nullValue()));
    }

    @Test
    public void getArtDescriptionCorrectlyUsesOpenAiApiWhenArchitecture(){
        mockWebServer.enqueue(new MockResponse().setBody("<html><body><p>Hello World</p></body></html>").setResponseCode(200));
        ArtRecognition artRecognition = new ArtRecognition("Arc de Triomphe", "Monument");

        MockOpenAiService mockOpenAiService = new MockOpenAiService("Mock API KEY");
        mockOpenAiService.setMockResponse("{\n" +
                "    \"designer\": \"Jean-François-Thérèse Chalgrin\",\n" +
                "    \"yearOfInauguration\": \"1836\",\n" +
                "    \"locationCity\": \"Paris\",\n" +
                "    \"locationCountry\": \"France\"\n" +
                "}");

        WikipediaDescriptionApi.service = mockOpenAiService;

        CompletableFuture<BasicArtDescription> descriptionFuture = new WikipediaDescriptionApi().getArtDescription(artRecognition);

        BasicArtDescription description = descriptionFuture.join();

        assert (description.getArtist().equals("Jean-François-Thérèse Chalgrin"));
        assert (description.getYear().equals("1836"));
        assert (description.getCity().equals("Paris"));
        assert (description.getCountry().equals("France"));


    }



    @After
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
    }


}
