package ch.epfl.culturequest.backend.artprocessingtest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;

import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import ch.epfl.culturequest.backend.artprocessing.apis.ProcessingApi;
import ch.epfl.culturequest.backend.artprocessing.processingobjects.BasicArtDescription;

public class ArtProcessingTest {

    String imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/e/ec/'David'_by_Michelangelo_Fir_JBU005_denoised.jpg/1280px-'David'_by_Michelangelo_Fir_JBU005_denoised.jpg";

    @Test
    public void artProcessingOutputsCorrectDescription() {
        try {
            BasicArtDescription artDescription = ProcessingApi.getArtDescriptionFromUrl(imageUrl).get(40, TimeUnit.SECONDS);
            assertThat(artDescription.getName(), is("David of Michelangelo"));
            assertThat(artDescription.getType(), is(BasicArtDescription.ArtType.SCULPTURE));
        } catch (ExecutionException | InterruptedException |
                 TimeoutException e) {
            fail("Test failed because of an exception: " + e.getMessage());
        }
    }

}
