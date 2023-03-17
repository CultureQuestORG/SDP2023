package ch.epfl.culturequest.backend.artprocessingtest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

import ch.epfl.culturequest.backend.artprocessing.apis.RecognitionApi;
import ch.epfl.culturequest.backend.artprocessing.processingobjects.ArtRecognition;
import ch.epfl.culturequest.backend.artprocessing.utils.ArtImageUpload;

public class ArtImageUploadTest {

    final String davidImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/e/ec/%27David%27_by_Michelangelo_Fir_JBU005_denoised.jpg/800px-%27David%27_by_Michelangelo_Fir_JBU005_denoised.jpg";

    private Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Test
    public void imageUploadReturnsValidUrl() {

        Bitmap bitmap = getBitmapFromURL(davidImageUrl); // generates an example bitmap (it would normally come from the camera)

        CompletableFuture<String> f = new ArtImageUpload().uploadAndGetUrlFromImage(bitmap);
        String url = f.join();
        assert(url.startsWith("https"));
    }

}
