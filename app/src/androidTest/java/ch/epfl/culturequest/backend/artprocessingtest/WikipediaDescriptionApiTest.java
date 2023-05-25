package ch.epfl.culturequest.backend.artprocessingtest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsStringIgnoringCase;
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

public class WikipediaDescriptionApiTest {

    WikipediaDescriptionApi wikipediaDescriptionApi = new WikipediaDescriptionApi("https://en.wikipedia.org/wiki/Special:Search?search=");

    BasicArtDescription descriptionMonaLisa;
    BasicArtDescription descriptionDavidOfMichelangelo;

    BasicArtDescription descriptionArcDeTriomphe;

    BasicArtDescription getBasicArtDescription(String artName, String additionalInfo){
        ArtRecognition artRecognition = new ArtRecognition(artName, additionalInfo);
        CompletableFuture<BasicArtDescription> descriptionFuture = wikipediaDescriptionApi.getArtDescription(artRecognition);
        return descriptionFuture.join();
    }

    @Before
    public void setUp() {
        descriptionMonaLisa = getBasicArtDescription("Mona Lisa", "Painting");
        descriptionDavidOfMichelangelo = getBasicArtDescription("David of Michelangelo", "Sculpture");
        descriptionArcDeTriomphe = getBasicArtDescription("Arc de Triomphe", "Monument");
    }

    @Test
    public void descriptionApiReturnsCorrectSummary() {
        String expectedSummaryMonaLisa = "The Mona Lisa is a half-length portrait painting by Italian artist Leonardo da Vinci. Considered an archetypal masterpiece of the Italian Renaissance, it has been described as \"the best known, the most visited, the most written about, the most sung about, the most parodied work of art in the world\". The painting's novel qualities include the subject's enigmatic expression, monumentality of the composition, the subtle modelling of forms, and the atmospheric illusionism.";
        assertThat(descriptionMonaLisa.getSummary(), is(expectedSummaryMonaLisa));
        String realSummaryDavidOfMichelangelo = descriptionDavidOfMichelangelo.getSummary();

        assertThat(realSummaryDavidOfMichelangelo,containsStringIgnoringCase("David"));
        assertThat(realSummaryDavidOfMichelangelo,containsStringIgnoringCase("Michelangelo"));
        assertThat(realSummaryDavidOfMichelangelo,containsStringIgnoringCase("sculpture"));
        assertThat(realSummaryDavidOfMichelangelo,containsStringIgnoringCase("1504"));
        assertThat(realSummaryDavidOfMichelangelo,containsStringIgnoringCase("Florence"));
        assertThat(realSummaryDavidOfMichelangelo,containsStringIgnoringCase("Renaissance"));
        assertThat(realSummaryDavidOfMichelangelo,containsStringIgnoringCase("5.17"));

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

    @Test
    public void descriptionApiReturnsCorrectArtist(){
        assertThat(descriptionMonaLisa.getArtist(), is("Leonardo da Vinci"));
        assertThat(descriptionDavidOfMichelangelo.getArtist(), is("Michelangelo"));
        assertThat(descriptionArcDeTriomphe.getArtist(), is(nullValue()));
    }

}
