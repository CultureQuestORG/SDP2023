package ch.epfl.culturequest;

import static ch.epfl.culturequest.social.RarityLevel.getRarityLevel;
import static ch.epfl.culturequest.utils.ProfileUtils.POSTS_ADDED;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import ch.epfl.culturequest.backend.artprocessing.processingobjects.BasicArtDescription;
import ch.epfl.culturequest.backend.artprocessing.utils.DescriptionSerializer;
import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.social.Post;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.social.ScanBadge;
import ch.epfl.culturequest.storage.FireStorage;

public class ArtDescriptionDisplayActivity extends AppCompatActivity {

    private Bitmap scannedImage;

    private static final int POPUP_DELAY = 5000;

    private Button postButton;

    private String imageDownloadUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_art_description_display);
        findViewById(R.id.back_button).setOnClickListener(view -> onBackPressed());
        postButton = findViewById(R.id.post_button);

        // Get serialized artDescription and images from intent
        String serializedArtDescription = getIntent().getStringExtra("artDescription");
        String imageUriExtra = getIntent().getStringExtra("imageUri");
        imageDownloadUrl = getIntent().getStringExtra("downloadUrl");

        // Check if the activity was started from the scanning activity
        boolean scan = getIntent().getBooleanExtra("scanning", true);

        if(scan) {
            Uri imageUri = Uri.parse(imageUriExtra);
            BasicArtDescription artDescription = DescriptionSerializer.deserialize(serializedArtDescription);
            // Get SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("openAI_popup_pref", MODE_PRIVATE);
            boolean doNotShowAgain = sharedPreferences.getBoolean("do_not_show_again", false);
            // Check if artDescription.openAIRequired is true and doNotShowAgain is false
            if (artDescription.isOpenAiRequired() && !doNotShowAgain) {
                showOpenAIPopup();
            }
            // get bitmap from imageUri with the ContentResolver
            try {
                // get bitmap from imageUri with the ContentResolver
                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                scannedImage = bitmap;
                ((ImageView) findViewById(R.id.artImage)).setImageBitmap(bitmap);
                displayArtInformation(artDescription);
                postButton.setOnClickListener(v -> postImage(imageDownloadUrl, artDescription, List.of(artDescription.getCountry(), artDescription.getCity(), artDescription.getMuseum())));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                finish();
            }
        } else {
            // Deserialize artDescription
            BasicArtDescription artDescription = DescriptionSerializer.deserialize(serializedArtDescription);

            // Get SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("openAI_popup_pref", MODE_PRIVATE);
            boolean doNotShowAgain = sharedPreferences.getBoolean("do_not_show_again", false);

            // Check if artDescription.openAIRequired is true and doNotShowAgain is false
            if (artDescription.isOpenAiRequired() && !doNotShowAgain) {
                showOpenAIPopup();
            }

            // Display art information on the page
            displayArtInformation(artDescription);

            // Display image on the page from the server
            Picasso.get()
                    .load(imageDownloadUrl)
                    .placeholder(android.R.drawable.progress_horizontal)
                    .into((ImageView) findViewById(R.id.artImage));

            // Remove post button as the image was not scanned
            postButton.setVisibility(View.GONE);
        }
    }

    /**
     * If the user decides not to post the picture
     * we delete the pic from firebase storage.
     */
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        FireStorage.deleteImage(imageDownloadUrl);
    }

    private void displayArtInformation(BasicArtDescription artDescription) {

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

    private void showOpenAIPopup() {
        View popupView = LayoutInflater.from(this).inflate(R.layout.popup_openai_message, null);
        final PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        // Show the popup in the center of the screen after the activity has been fully initialized
        findViewById(android.R.id.content).post(() -> {
            if (!isFinishing()) {
                popupWindow.showAtLocation(findViewById(android.R.id.content), Gravity.CENTER, 0, 0);
            }
        });

        // Set the "Do not show it again" button click listener
        Button btnDoNotShowAgain = popupView.findViewById(R.id.btn_do_not_show_again);
        btnDoNotShowAgain.setOnClickListener(view -> {

            SharedPreferences sharedPreferences = getSharedPreferences("openAI_popup_pref", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("do_not_show_again", true);
            editor.apply();

            popupWindow.dismiss();
        });

        // Set a delay of 3 seconds before making the popup invisible
        new Handler().postDelayed(() -> {
            if (popupWindow.isShowing()) {
                popupWindow.dismiss();
            }
        }, POPUP_DELAY);
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
     * Uploads an image to the database when the user presses on the post button. it will post
     * the image in the storage at the address: images/uid/postId
     *
     * @param url  the image url to upload
     * @param artwork the artwork to add to the database
     */
    private void postImage(String url, BasicArtDescription artwork, List<String> badges) {
        String postId = UUID.randomUUID().toString();
        Profile activeProfile = Profile.getActiveProfile();
        String uid = activeProfile.getUid();
        Database.uploadPost(new Post(postId, uid, url, artwork.getName(), new Date().getTime(), 0, new ArrayList<>())).whenComplete((lambda, e) -> {
            if (e == null) {
                POSTS_ADDED++;
                activeProfile.incrementScore(artwork.getScore());
                activeProfile.addBadges(badges);
                finish();
            } else {
                e.printStackTrace();
            }
        }).exceptionally(l -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Error").setMessage("Couldn't post picture").setCancelable(false).setPositiveButton("Cancel", (dialog, which) -> dialog.dismiss());
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            return null;
        });
    }
}