package ch.epfl.culturequest;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import ch.epfl.culturequest.backend.artprocessing.apis.ProcessingApi;
import ch.epfl.culturequest.backend.artprocessing.utils.ArtImageUpload;

public class ArtDescriptionDisplayActivity extends AppCompatActivity {

    private Bitmap scannedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_art_description_display);

        findViewById(R.id.back_button).setOnClickListener(view -> finish());

        // get the image from the extra
        scannedImage = getIntent().getParcelableExtra("bitmap");

        ImageView imageView = findViewById(R.id.artImage);
        imageView.setImageBitmap(scannedImage);

        processImageAndDisplayInformation();
    }

    private void processImageAndDisplayInformation() {
        ArtImageUpload artImageUpload = new ArtImageUpload();
        artImageUpload.uploadAndGetUrlFromImage(scannedImage).thenAccept(url -> {
            new ProcessingApi().getArtDescriptionFromUrl(url).thenAccept(artDescription -> {
                // do something with the artDescription object

                TextView artNameView = findViewById(R.id.artName);
                setTextOrFallback(artNameView, artDescription.getName(), "No name found");

                TextView artistNameView = findViewById(R.id.artistName);
                setTextOrFallback(artistNameView, artDescription.getArtist(), "No artist found");

                TextView artYearView = findViewById(R.id.artYear);
                setTextOrFallback(artYearView, artDescription.getYear(), "No year found");

                TextView artSummaryView = findViewById(R.id.artSummary);
                setTextOrFallback(artSummaryView, artDescription.getSummary(), "No description found");

            });
        });
    }
    private void setTextOrFallback(TextView textView, String text, String fallbackText) {
        textView.setText(text != null ? text : fallbackText);
    }

}