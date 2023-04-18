package ch.epfl.culturequest;

import static ch.epfl.culturequest.social.RarityLevel.getRarityLevel;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.OutputStream;

import ch.epfl.culturequest.backend.LocalStorage;
import ch.epfl.culturequest.backend.artprocessing.apis.ProcessingApi;
import ch.epfl.culturequest.backend.artprocessing.processingobjects.BasicArtDescription;
import ch.epfl.culturequest.backend.artprocessing.utils.ArtImageUpload;
import ch.epfl.culturequest.backend.artprocessing.utils.DescriptionSerializer;
import ch.epfl.culturequest.social.ScanBadge;

public class ArtDescriptionDisplayActivity extends AppCompatActivity {

    private Bitmap scannedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_art_description_display);

        findViewById(R.id.back_button).setOnClickListener(view -> finish());

        String serializedArtDescription = getIntent().getStringExtra("artDescription");
        String imageUriExtra = getIntent().getStringExtra("imageUri");
        Uri imageUri = Uri.parse(imageUriExtra);

        BasicArtDescription artDescription = DescriptionSerializer.deserialize(serializedArtDescription);

        // get bitmap from imageUri with the ContentResolver
        try {
            // get bitmap from imageUri with the ContentResolver
            Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
            scannedImage = bitmap;
            ImageView imageView = findViewById(R.id.artImage);
            imageView.setImageBitmap(scannedImage);
            displayArtInformation(artDescription);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            finish();
        }
    }
    private void displayArtInformation(BasicArtDescription artDescription){

        // Set Art Name
        TextView artNameView = findViewById(R.id.artName);
        setTextOrFallback(artNameView, artDescription.getName(), "No name found");

        // Set Artist
        TextView artistNameView = findViewById(R.id.artistName);
        setTextOrFallback(artistNameView, artDescription.getArtist(), "No artist found");

        // Set Year
        TextView artYearView = findViewById(R.id.artYear);
        setTextOrFallback(artYearView, artDescription.getYear(), "No year found");

        // Set Summary
        TextView artSummaryView = findViewById(R.id.artSummary);
        setTextOrFallback(artSummaryView, artDescription.getSummary(), "No description found");

        // Set Score
        TextView artScoreView = findViewById(R.id.artScore);
        artScoreView.setText(artDescription.getScore() != null ? "+" + artDescription.getScore().toString() + " pts" : "+50 pts");

        // Set Rarity Badge
        ImageView rarityBadge = findViewById(R.id.rarity);
        rarityBadge.setImageResource(getRarityLevel(artDescription.getScore()).getRarenessIcon());


        // Set Country Badge
        ImageView countryBadge = findViewById(R.id.countryBadge);
        TextView countryName = findViewById(R.id.countryName);

        if(artDescription.getCountry() != null ) {
            countryBadge.setImageResource(ScanBadge.Country.fromString(artDescription.getCountry()).getBadge());
            countryName.setText(artDescription.getCountry());
        } else {
            countryBadge.setVisibility(ImageView.GONE);
            countryName.setVisibility(TextView.GONE);
        }

        // Set City Badge
        ImageView cityBadge = findViewById(R.id.cityBadge);
        TextView cityName = findViewById(R.id.cityName);

        if(artDescription.getCity() != null ) {
            cityBadge.setImageResource(ScanBadge.City.fromString(artDescription.getCity()).getBadge());
            cityName.setText(artDescription.getCity());
        } else {
            cityBadge.setVisibility(ImageView.GONE);
            cityName.setVisibility(TextView.GONE);
        }

        // Set Museum Badge
        ImageView museumBadge = findViewById(R.id.museumBadge);
        TextView museumName = findViewById(R.id.museumName);

        if(artDescription.getMuseum() != null ) {
            museumBadge.setImageResource(ScanBadge.Museum.fromString(artDescription.getMuseum()).getBadge());
            museumName.setText(artDescription.getMuseum());
        } else {
            museumBadge.setVisibility(ImageView.GONE);
            museumName.setVisibility(TextView.GONE);
        }
    }

    private void setTextOrFallback(TextView textView, String text, String fallbackText) {
        textView.setText(text != null ? text : fallbackText);
    }

}