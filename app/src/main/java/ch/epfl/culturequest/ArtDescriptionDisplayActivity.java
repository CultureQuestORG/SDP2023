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
        artScoreView.setText(artDescription.getScore() != null ? "+" + artDescription.getScore().toString() + " pts" : "+30 pts");

        // Set Rarity Badge
        ImageView rarityBadge = findViewById(R.id.rarity);
        setRarityBadge(rarityBadge, artDescription.getScore());

        // Set Country Badge
        ImageView countryBadge = findViewById(R.id.countryBadge);
        TextView countryName = findViewById(R.id.countryName);

        setCountryBadge(countryBadge, countryName, artDescription.getCountry());

        // Set City Badge
        ImageView cityBadge = findViewById(R.id.cityBadge);
        TextView cityName = findViewById(R.id.cityName);

        setCityBadge(cityBadge, cityName, artDescription.getCity());

        // Set Museum Badge
        ImageView museumBadge = findViewById(R.id.museumBadge);
        TextView museumName = findViewById(R.id.museumName);

        setMuseumBadge(museumBadge, museumName, artDescription.getMuseum());
    }

    private void setTextOrFallback(TextView textView, String text, String fallbackText) {
        textView.setText(text != null ? text : fallbackText);
    }

    private void setRarityBadge(ImageView rarityBadge, Integer score) {
        if (score != null) {
            rarityBadge.setImageResource(getRarityLevel(score).getRarenessIcon());
            rarityBadge.setTag(getRarityLevel(score).name());
        } else {
            rarityBadge.setImageResource(getRarityLevel(30).getRarenessIcon());
            rarityBadge.setTag(getRarityLevel(30).name());
        }
    }

    private void setCountryBadge(ImageView countryBadge, TextView countryText, String country) {
        if (country != null) {
            countryBadge.setImageResource(ScanBadge.Country.fromString(country).getBadge());
            countryText.setText(country);
            countryBadge.setTag(ScanBadge.Country.fromString(country).name());
        } else {
            countryBadge.setVisibility(ImageView.GONE);
            countryText.setVisibility(TextView.GONE);
        }
    }

    private void setCityBadge(ImageView cityBadge, TextView cityText, String city) {
        if (city != null) {
            cityBadge.setImageResource(ScanBadge.City.fromString(city).getBadge());
            cityText.setText(city);
            cityBadge.setTag(ScanBadge.City.fromString(city).name());
        } else {
            cityBadge.setVisibility(ImageView.GONE);
            cityText.setVisibility(TextView.GONE);
        }
    }

    private void setMuseumBadge(ImageView museumBadge, TextView museumText, String museum) {
        if (museum != null) {
            museumBadge.setImageResource(ScanBadge.Museum.fromString(museum).getBadge());
            museumText.setText(museum);
            museumBadge.setTag(ScanBadge.Museum.fromString(museum).name());
        } else {
            museumBadge.setVisibility(ImageView.GONE);
            museumText.setVisibility(TextView.GONE);
        }
    }
}