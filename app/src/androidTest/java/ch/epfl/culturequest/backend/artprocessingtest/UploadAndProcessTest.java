package ch.epfl.culturequest.backend.artprocessingtest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import android.graphics.Bitmap;

import org.junit.Test;

import ch.epfl.culturequest.backend.artprocessing.processingobjects.BasicArtDescription;
import ch.epfl.culturequest.backend.artprocessing.utils.UploadAndProcess;

public class UploadAndProcessTest {

    String imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/e/ec/'David'_by_Michelangelo_Fir_JBU005_denoised.jpg/1280px-'David'_by_Michelangelo_Fir_JBU005_denoised.jpg";

    @Test
    public void uploadAndProcessOutputsCorrectDescription(){

        Bitmap davidImageBitmap = new ArtImageUploadTest().getBitmapFromURL(imageUrl);

        BasicArtDescription artDescription = new UploadAndProcess().uploadAndProcess(davidImageBitmap).join();

        assertThat(artDescription.getName(), is("David of Michelangelo"));
        assertThat(artDescription.getType(), is(BasicArtDescription.ArtType.SCULPTURE));

    }

}
