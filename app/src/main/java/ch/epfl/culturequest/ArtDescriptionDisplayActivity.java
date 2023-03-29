package ch.epfl.culturequest;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.OutputStream;

import ch.epfl.culturequest.backend.LocalStorage;
import ch.epfl.culturequest.backend.artprocessing.apis.ProcessingApi;
import ch.epfl.culturequest.backend.artprocessing.utils.ArtImageUpload;

public class ArtDescriptionDisplayActivity extends AppCompatActivity {

    private Bitmap scannedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_art_description_display);

        findViewById(R.id.back_button).setOnClickListener(view -> finish());

        String imageUriExtra = getIntent().getStringExtra("imageUri");
        Uri imageUri = Uri.parse(imageUriExtra);

        // get bitmap from imageUri with the ContentResolver
        OutputStream os = new ByteArrayOutputStream();
        try {
            // get bitmap from imageUri with the ContentResolver
            Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
            scannedImage = bitmap;
            ImageView imageView = findViewById(R.id.artImage);
            imageView.setImageBitmap(scannedImage);
            processImageAndDisplayInformation();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            finish();
        }
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