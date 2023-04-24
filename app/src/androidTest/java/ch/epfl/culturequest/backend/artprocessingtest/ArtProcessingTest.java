package ch.epfl.culturequest.backend.artprocessingtest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

import java.util.concurrent.CompletableFuture;

import ch.epfl.culturequest.backend.artprocessing.apis.ProcessingApi;
import ch.epfl.culturequest.backend.artprocessing.apis.WikipediaDescriptionApi;
import ch.epfl.culturequest.backend.artprocessing.processingobjects.ArtRecognition;
import ch.epfl.culturequest.backend.artprocessing.apis.RecognitionApi;
import ch.epfl.culturequest.backend.artprocessing.processingobjects.BasicArtDescription;

public class ArtProcessingTest {

    String imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/e/ec/'David'_by_Michelangelo_Fir_JBU005_denoised.jpg/1280px-'David'_by_Michelangelo_Fir_JBU005_denoised.jpg";

    @Test
    public void artProcessingOutputsCorrectDescription(){

        BasicArtDescription artDescription = ProcessingApi.getArtDescriptionFromUrl(imageUrl).join();

        assertThat(artDescription.getName(), is("David of Michelangelo"));
        assertThat(artDescription.getType(), is(BasicArtDescription.ArtType.SCULPTURE));

    }

}
