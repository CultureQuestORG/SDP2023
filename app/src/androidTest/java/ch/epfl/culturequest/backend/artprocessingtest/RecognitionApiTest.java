package ch.epfl.culturequest.backend.artprocessingtest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

import java.util.concurrent.CompletableFuture;

import ch.epfl.culturequest.backend.artprocessing.processingobjects.ArtRecognition;
import ch.epfl.culturequest.backend.artprocessing.apis.RecognitionApi;

public class RecognitionApiTest {

    String imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/e/ec/'David'_by_Michelangelo_Fir_JBU005_denoised.jpg/1280px-'David'_by_Michelangelo_Fir_JBU005_denoised.jpg";

    RecognitionApi recognitionApi = new RecognitionApi("https://lens.google.com/uploadbyurl");

    @Test
    public void getArtNameReturnsCorrectTitle(){

        String expectedArtName = "David of Michelangelo";

        CompletableFuture<ArtRecognition> artRecognitionCompletableFuture = recognitionApi.getArtName(imageUrl);
        ArtRecognition artRecognition = artRecognitionCompletableFuture.join();

        assertThat(artRecognition.getArtName(), is(expectedArtName));
    }

    @Test
    public void getArtNameReturnsCorrectAdditionalInfo(){

        String expectedAdditionalInformation = "Sculpture";
        CompletableFuture<ArtRecognition> artRecognitionCompletableFuture = recognitionApi.getArtName(imageUrl);
        ArtRecognition artRecognition = artRecognitionCompletableFuture.join();

        assertThat(artRecognition.getAdditionalInfo(), is(expectedAdditionalInformation));

    }




}
