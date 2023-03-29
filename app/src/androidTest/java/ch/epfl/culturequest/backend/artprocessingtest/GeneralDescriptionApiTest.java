package ch.epfl.culturequest.backend.artprocessingtest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.junit.Test;

import java.util.concurrent.CompletableFuture;

import ch.epfl.culturequest.backend.artprocessing.apis.GeneralDescriptionApi;
import ch.epfl.culturequest.backend.artprocessing.apis.WikipediaDescriptionApi;
import ch.epfl.culturequest.backend.artprocessing.processingobjects.ArtRecognition;
import ch.epfl.culturequest.backend.artprocessing.processingobjects.BasicArtDescription;

public class GeneralDescriptionApiTest {

    @Test
    public void getArtDescriptionCorrectlyUsesOpenAiApiWhenArchitecture(){

        ArtRecognition artRecognition = new ArtRecognition("Arc de Triomphe", "Monument");

        CompletableFuture<BasicArtDescription> descriptionFuture = new GeneralDescriptionApi().getArtDescription(artRecognition);

        BasicArtDescription description = descriptionFuture.join();

        // rewrite all the above with assertThat
        assertThat(description.getArtist(), is("Jean-François-Thérèse Chalgrin"));
        assertThat(description.getYear(), is("1836"));
        assertThat(description.getCity(), is("Paris"));
        assertThat(description.getCountry(), is("France"));
    }

    @Test
    public void getArtDescriptionReturnsCorrectDataWhenPainting(){
        ArtRecognition artRecognition = new ArtRecognition("Mona Lisa", "Painting");
        BasicArtDescription artDescription = new GeneralDescriptionApi().getArtDescription(artRecognition).join();

        String expectedSummaryMonaLisa = "The Mona Lisa is a half-length portrait painting by Italian artist Leonardo da Vinci. Considered an archetypal masterpiece of the Italian Renaissance, it has been described as \"the best known, the most visited, the most written about, the most sung about, the most parodied work of art in the world\". The painting's novel qualities include the subject's enigmatic expression, monumentality of the composition, the subtle modelling of forms, and the atmospheric illusionism.";

        // rewrite all the above assertions with assertThat
        assertThat(artDescription.getSummary(), is(expectedSummaryMonaLisa));
        assertThat(artDescription.getArtist(), is("Leonardo da Vinci"));
        assertThat(artDescription.getYear(), is("1517"));
        assertThat(artDescription.getCity(), is("Paris"));
        assertThat(artDescription.getCountry(), is(nullValue()));
        assertThat(artDescription.getMuseum(), is("Louvre"));
        assertThat(artDescription.getType(), is(BasicArtDescription.ArtType.PAINTING));
        assertThat(artDescription.getScore(), is(100));
    }

}
