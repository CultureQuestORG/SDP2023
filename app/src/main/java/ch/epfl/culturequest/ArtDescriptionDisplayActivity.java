package ch.epfl.culturequest;

import static ch.epfl.culturequest.social.RarityLevel.getRarityLevel;
import static ch.epfl.culturequest.utils.ProfileUtils.postsAdded;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import ch.epfl.culturequest.backend.artprocessing.processingobjects.BasicArtDescription;
import ch.epfl.culturequest.backend.artprocessing.utils.DescriptionSerializer;
import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.social.Post;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.social.ScanBadge;
import ch.epfl.culturequest.storage.FireStorage;
import ch.epfl.culturequest.ui.commons.LoadingAnimation;

public class ArtDescriptionDisplayActivity extends AppCompatActivity {

    private Bitmap scannedImage;

    private Button postButton;
    private LoadingAnimation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_art_description_display);
        animation = findViewById(R.id.uploadingAnimation);
        findViewById(R.id.back_button).setOnClickListener(view -> finish());
        postButton = findViewById(R.id.post_button);
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
            postButton.setOnClickListener(v -> {
                uploadImage(imageUri, artDescription);
            });

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

    /**
     * Uploads an image to the database when the user presses on the post button. While the images/ folder in the folder is not emptied, we create a folder test
     * with subfolders for each user and in it the post ids of the images. This way it is easy to categorise images per user.
     *
     * @param uri     the uri to upload
     * @param artwork the artwork to add to the database
     */
    private void uploadImage(Uri uri, BasicArtDescription artwork) {
        String postId = UUID.randomUUID().toString();
        Profile activeProfile = Profile.getActiveProfile();
        String uid = activeProfile.getUid();
        animation.startLoading();
        Bitmap bitmap = FireStorage.getBitmapFromURI(uri, getContentResolver());
        FireStorage.uploadAndGetUrlFromImage(Objects.requireNonNull(bitmap)).whenComplete((url, e) -> {
            if (e == null) {
                Post post = new Post(postId, uid, url, artwork.getName(), new Date().getTime(), 0, new ArrayList<>());
                Database.uploadPost(post).thenAccept((done) -> {
                    if (done.get()) {
                        postsAdded++;
                        activeProfile.incrementScore(artwork.getScore());
                        animation.stopLoading();
                        finish();
                    }
                });
            } else {
                e.printStackTrace();
            }
        }).exceptionally(l -> {
            animation.stopLoading();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Error")
                    .setMessage("Couldn't post picture")
                    .setCancelable(false)
                    .setPositiveButton("Cancel", (dialog, which) -> {
                        dialog.dismiss();
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            return null;
        });
    }
}