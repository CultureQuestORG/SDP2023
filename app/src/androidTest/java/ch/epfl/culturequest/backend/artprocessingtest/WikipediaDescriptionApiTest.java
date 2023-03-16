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

public class WikipediaDescriptionApiTest {

    WikipediaDescriptionApi wikipediaDescriptionApi = new WikipediaDescriptionApi();

    BasicArtDescription descriptionMonaLisa;
    BasicArtDescription descriptionDavidOfMichelangelo;

    BasicArtDescription descriptionArcDeTriomphe;

    MockWebServer mockWebServer = new MockWebServer();

    BasicArtDescription getBasicArtDescription(String artName, String additionalInfo){
        ArtRecognition artRecognition = new ArtRecognition(artName, additionalInfo);
        CompletableFuture<BasicArtDescription> descriptionFuture = wikipediaDescriptionApi.getArtDescription(artRecognition);
        return descriptionFuture.join();
    }

    @Before
    public void setUp() throws IOException {

        mockWebServer.start(8080);

        descriptionMonaLisa = getBasicArtDescription("Mona Lisa", "Painting");
        descriptionDavidOfMichelangelo = getBasicArtDescription("David of Michelangelo", "Sculpture");
        descriptionArcDeTriomphe = getBasicArtDescription("Arc de Triomphe", "Monument");
    }

    private void initUrl(Boolean useMockWebserver){
        if(useMockWebserver){
            wikipediaDescriptionApi.wikipediaBaseUrl = "http://localhost:8080/";
        } else {
            wikipediaDescriptionApi.wikipediaBaseUrl = "https://en.wikipedia.org/wiki/Special:Search?search=";
        }
    }

    @Test
    public void descriptionApiReturnsCorrectSummary() {
        String expectedSummaryMonaLisa = "The Mona Lisa is a half-length portrait painting by Italian artist Leonardo da Vinci. Considered an archetypal masterpiece of the Italian Renaissance, it has been described as \"the best known, the most visited, the most written about, the most sung about, the most parodied work of art in the world\". The painting's novel qualities include the subject's enigmatic expression, monumentality of the composition, the subtle modelling of forms, and the atmospheric illusionism.";
        String expectedSummaryDavidOfMichelangelo = "David is a masterpiece of Renaissance sculpture, created in marble between 1501 and 1504 by the Italian artist Michelangelo. David is a 5.17-metre marble statue of the Biblical figure David, a favoured subject in the art of Florence. David was originally commissioned as one of a series of statues of prophets to be positioned along the roofline of the east end of Florence Cathedral, but was instead placed in a public square, outside the Palazzo Vecchio, the seat of civic government in Florence, in the Piazza della Signoria, where it was unveiled on 8 September 1504.";
        assertThat(descriptionMonaLisa.getSummary(), is(expectedSummaryMonaLisa));
        assertThat(descriptionDavidOfMichelangelo.getSummary(), is(expectedSummaryDavidOfMichelangelo));
        assertThat(descriptionArcDeTriomphe.getSummary(), is("The Arc de Triomphe de l'Étoile is one of the most famous monuments in Paris, France, standing at the western end of the Champs-Élysées at the centre of Place Charles de Gaulle, formerly named Place de l'Étoile—the étoile or \"star\" of the juncture formed by its twelve radiating avenues. The location of the arc and the plaza is shared between three arrondissements, 16th, 17th, and 8th. The Arc de Triomphe honours those who fought and died for France in the French Revolutionary and Napoleonic Wars, with the names of all French victories and generals inscribed on its inner and outer surfaces.") );
    }

    @Test
    public void descriptionApiReturnsCorrectName() {
        assertThat(descriptionMonaLisa.getName(), is("Mona Lisa"));
        assertThat(descriptionDavidOfMichelangelo.getName(), is("David of Michelangelo"));
        assertThat(descriptionArcDeTriomphe.getName(), is("Arc de Triomphe"));
    }

    @Test
    public void descriptionApiReturnsCorrectType() {
        assertThat(descriptionMonaLisa.getType(), is(BasicArtDescription.ArtType.PAINTING));
        assertThat(descriptionDavidOfMichelangelo.getType(), is(BasicArtDescription.ArtType.SCULPTURE));
        assertThat(descriptionArcDeTriomphe.getType(), is(BasicArtDescription.ArtType.ARCHITECTURE));
    }

    @Test
    public void descriptionApiReturnsCorrectCity() {
        assertThat(descriptionMonaLisa.getCity(), is("Paris"));
        assertThat(descriptionDavidOfMichelangelo.getCity(), is("Florence"));
        assertThat(descriptionArcDeTriomphe.getCity(), is(nullValue()));
    }

    @Test
    public void descriptionApiReturnsCorrectCountry() {
        assertThat(descriptionMonaLisa.getCountry(), is(nullValue()));
        assertThat(descriptionDavidOfMichelangelo.getCountry(), is("Italy"));
        assertThat(descriptionArcDeTriomphe.getCountry(), is(nullValue()));
    }

    @Test
    public void descriptionApiReturnsCorrectYear() {
        assertThat(descriptionMonaLisa.getYear(), is("1517"));
        assertThat(descriptionDavidOfMichelangelo.getYear(), is("1504"));
        assertThat(descriptionArcDeTriomphe.getYear(), is(nullValue()));
    }

    @Test
    public void descriptionApiReturnsCorrectMuseum() {
        assertThat(descriptionMonaLisa.getMuseum(), is("Louvre"));
        assertThat(descriptionDavidOfMichelangelo.getMuseum(), is("Galleria dell'Accademia"));
        assertThat(descriptionArcDeTriomphe.getMuseum(), is(nullValue()));
    }

    // test that the BasicArtDescription future is completed exceptionally when the wikipedia api returns an error
    @Test
    public void exceptionWhenBadHttpResponse() {
        initUrl(true);
        mockWebServer.enqueue(new MockResponse().setResponseCode(404));
        ArtRecognition artRecognition = new ArtRecognition("Mona Lisa", "Painting");

        // getArtDescription should return a future that is completed exceptionally
        CompletableFuture<BasicArtDescription> descriptionFuture = wikipediaDescriptionApi.getArtDescription(artRecognition);
        assertThrows(CompletionException.class, () -> descriptionFuture.join());
    }

    // test that the BasicArtDescription future is completed exceptionally when the wikipedia api times out
    @Test
    public void exceptionWhenTimeout() {
        initUrl(true);
        mockWebServer.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START));

        ArtRecognition artRecognition = new ArtRecognition("Mona Lisa", "Painting");

        // getArtDescription should return a future that is completed exceptionally
        CompletableFuture<BasicArtDescription> descriptionFuture = wikipediaDescriptionApi.getArtDescription(artRecognition);
        assertThrows(CompletionException.class, () -> descriptionFuture.join());
    }

    // test that summary, city, country, museum, year are null when the wikipedia api returns empty strings
    @Test
    public void nullFieldsWhenResponseEmpty() {
        initUrl(true);
        mockWebServer.enqueue(new MockResponse().setBody("<html><body><p></p></body></html>").setResponseCode(200));
        ArtRecognition artRecognition = new ArtRecognition("Mona Lisa", "Painting");

        // getArtDescription should return a future that is completed exceptionally
        CompletableFuture<BasicArtDescription> descriptionFuture = wikipediaDescriptionApi.getArtDescription(artRecognition);
        BasicArtDescription description = descriptionFuture.join();
        assertThat(description.getSummary(), is(nullValue()));
        assertThat(description.getCity(), is(nullValue()));
        assertThat(description.getCountry(), is(nullValue()));
        assertThat(description.getMuseum(), is(nullValue()));
        assertThat(description.getYear(), is(nullValue()));
    }

    // test that the future doesn't complete exceptionally when the wikipedia api returns a valid response after 5 seconds
    @Test
    public void noExceptionWhenValidResponseAfterTimeout() {
        initUrl(true);
        mockWebServer.enqueue(new MockResponse().setBody("<html><body><p></p></body></html>").setResponseCode(200).setBodyDelay(5, TimeUnit.SECONDS));
        ArtRecognition artRecognition = new ArtRecognition("Mona Lisa", "Painting");

        // getArtDescription should return a future that is completed exceptionally
        CompletableFuture<BasicArtDescription> descriptionFuture = wikipediaDescriptionApi.getArtDescription(artRecognition);
        descriptionFuture.join();
    }

    // test that that the type is OTHER when the wikipedia api returns an unknown type
    @Test
    public void otherArtTypeWhenUnknownType() {
        initUrl(true);
        mockWebServer.enqueue(new MockResponse().setBody("<html><body><p></p></body></html>").setResponseCode(200));
        ArtRecognition artRecognition = new ArtRecognition("Mona Lisa", "Unknown");

        // getArtDescription should return a future that is completed exceptionally
        CompletableFuture<BasicArtDescription> descriptionFuture = wikipediaDescriptionApi.getArtDescription(artRecognition);
        BasicArtDescription description = descriptionFuture.join();
        assertThat(description.getType(), is(BasicArtDescription.ArtType.OTHER));
    }


    @After
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

}
