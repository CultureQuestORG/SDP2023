package artprocessingtest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

import java.util.concurrent.CompletableFuture;

import artprocessing.apis.WikipediaDescriptionApi;
import artprocessing.processingobjects.ArtDescription;
import artprocessing.processingobjects.ArtRecognition;
import artprocessing.apis.DescriptionApi;
import artprocessing.apis.RecognitionApi;
import artprocessing.processingobjects.BasicArtDescription;

public class ArtProcessingTest {

    String imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/e/ec/'David'_by_Michelangelo_Fir_JBU005_denoised.jpg/1280px-'David'_by_Michelangelo_Fir_JBU005_denoised.jpg";

    @Test
    public void artProcessingOutputsCorrectDescription(){

        CompletableFuture<ArtRecognition> artRecognitionCompletableFuture = new RecognitionApi().getArtName(imageUrl);
        CompletableFuture<BasicArtDescription> artDescriptionCompletableFuture = artRecognitionCompletableFuture.thenCompose(a -> {
            WikipediaDescriptionApi wikipediaDescriptionApi = new WikipediaDescriptionApi();
            return wikipediaDescriptionApi.getArtDescription(a);
        });
        BasicArtDescription artDescription = artDescriptionCompletableFuture.join();

        assertThat(artDescription.getName(), is("David of Michelangelo"));
        assertThat(artDescription.getType(), is(BasicArtDescription.ArtType.SCULPTURE));

    }

}
