package ch.epfl.culturequest;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import ch.epfl.culturequest.backend.artprocessing.apis.ProcessingApi;
import ch.epfl.culturequest.backend.artprocessing.apis.RecognitionApi;
import ch.epfl.culturequest.backend.artprocessing.processingobjects.ArtRecognition;
import ch.epfl.culturequest.backend.artprocessing.utils.ArtImageUpload;

public class ArtProcessingDemoActivity extends AppCompatActivity {

    private Bitmap scannedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_art_processing_demo);
        // get the image from the extra
        scannedImage = getIntent().getParcelableExtra("bitmap");
        processImage();
    }


    private void processImage() {
        ArtImageUpload artImageUpload = new ArtImageUpload();
        artImageUpload.uploadAndGetUrlFromImage(scannedImage).thenAccept(url -> {
            new ProcessingApi().getArtDescriptionFromUrl(url).thenAccept(artDescription -> {
                // do something with the artDescription object
                ImageView imageView = findViewById(R.id.imageView);
                imageView.setImageBitmap(scannedImage);

                TextView artNameView = findViewById(R.id.artName);
                artNameView.setText(artDescription.getName());

                TextView artSummaryView = findViewById(R.id.artSummary);
                artSummaryView.setText(artDescription.getSummary());

            });
        });
    }

}