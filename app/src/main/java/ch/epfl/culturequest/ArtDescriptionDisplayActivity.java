package ch.epfl.culturequest;

import static ch.epfl.culturequest.social.RarityLevel.getRarityLevel;
import static ch.epfl.culturequest.utils.ProfileUtils.POSTS_ADDED;

import android.content.Intent;
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
import java.util.Objects;
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
    private Button shareButton;

    private String imageDownloadUrl;
    private boolean scan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_art_description_display);
        findViewById(R.id.back_button).setOnClickListener(view -> onBackPressed());
        postButton = findViewById(R.id.post_button);
        shareButton = findViewById(R.id.share_button);

        // Get serialized artDescription and images from intent
        String serializedArtDescription = getIntent().getStringExtra("artDescription");
        String imageUriExtra = getIntent().getStringExtra("imageUri");
        imageDownloadUrl = getIntent().getStringExtra("downloadUrl");

        // Check if the activity was started from the scanning activity
        scan = getIntent().getBooleanExtra("scanning", true);

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
                shareButton.setOnClickListener(v -> shareImage(imageUri, artDescription));
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
            shareButton.setVisibility(View.GONE);
        }
    }

    /**
     * If the user decides not to post the picture
     * we delete the pic from firebase storage.
     */
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        if (scan){
            FireStorage.deleteImage(imageDownloadUrl);
        }
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
        if (!Objects.equals(country, "none")) {
            countryBadge.setImageResource(ScanBadge.Country.fromString(country).getBadge());
            countryText.setText(country);
            countryBadge.setTag(ScanBadge.Country.fromString(country).name());
        } else {
            countryBadge.setVisibility(ImageView.GONE);
            countryText.setVisibility(TextView.GONE);
        }
    }

    private void setCityBadge(ImageView cityBadge, TextView cityText, String city) {
        if (!Objects.equals(city, "none")) {
            cityBadge.setImageResource(ScanBadge.City.fromString(city).getBadge());
            cityText.setText(city);
            cityBadge.setTag(ScanBadge.City.fromString(city).name());
        } else {
            cityBadge.setVisibility(ImageView.GONE);
            cityText.setVisibility(TextView.GONE);
        }
    }

    private void setMuseumBadge(ImageView museumBadge, TextView museumText, String museum) {
        if (!Objects.equals(museum, "none")) {
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
     * @param url     the image url to upload
     * @param artwork the artwork to add to the database
     */
    private void postImage(String url, BasicArtDescription artwork, List<String> badges) {
        String postId = UUID.randomUUID().toString();
        Profile activeProfile = Profile.getActiveProfile();
        String uid = activeProfile.getUid();
        Post newPost = new Post(postId, uid, url, artwork.getName(), new Date().getTime(), 0, new ArrayList<>());
        Profile.getActiveProfile().retrievePosts().thenCompose(posts -> {
            boolean alreadyPosted = posts.stream().anyMatch(post -> post.getArtworkName().equals(newPost.getArtworkName()));
            if (alreadyPosted) {
                showAlreadyPostedDialog(newPost);
            } else {
                Database.uploadPost(newPost).whenComplete((lambda, e) -> {
                    if (e == null) {
                        POSTS_ADDED++;
                        activeProfile.incrementScore(artwork.getScore());
                        activeProfile.addBadges(badges);
                        Intent intent = new Intent(this, NavigationActivity.class);
                        intent.putExtra("redirect", "profile");
                        startActivity(intent);
                    } else {
                        e.printStackTrace();
                    }
                }).exceptionally(l -> {
                    showErrorDialog("Couldn't post picture");
                    return null;
                });
            }
            return null;
        });
    }

    private void showAlreadyPostedDialog(Post post) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(post.getArtworkName() + " is already in your collection!");
        builder.setIcon(R.drawable.image_recognition_error);
        builder.setMessage("This post is already in your collection. You can still post it, but you will not get more points or badges!");
        builder.setCancelable(true);
        builder.setPositiveButton("Post", (dialog, which) -> {
            Database.uploadPost(post).handle((lambda, e) -> {
                if (e != null) {
                    e.printStackTrace();
                }
                POSTS_ADDED++;
                Intent intent = new Intent(this, NavigationActivity.class);
                intent.putExtra("redirect", "profile");
                startActivity(intent);
                return null;
            });
            dialog.cancel();
        });
        builder.setNegativeButton("Cancel", (dialog, id) -> {
            dialog.dismiss();
        });
        builder.create().show();
    }

    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this).setTitle("Error").setMessage(message).setCancelable(false).setPositiveButton("Cancel", (dialog, which) -> dialog.dismiss()).create().show();
    }

    private void shareImage(Uri uri, BasicArtDescription description) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/jpeg");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.putExtra(Intent.EXTRA_TEXT, "I just scanned " + description.getName() + " with \uD835\uDC02\uD835\uDC2E\uD835\uDC25\uD835\uDC2D\uD835\uDC2E\uD835\uDC2B\uD835\uDC1E\uD835\uDC10\uD835\uDC2E\uD835\uDC1E\uD835\uDC2C\uD835\uDC2D!\n\n" +
                "It's a " + getRarityLevel(description.getScore()).name().toLowerCase() + " artwork from " + description.getArtist() + ", displayed at "+ description.getMuseum() + ", " + description.getCity() + ".\n\n" +
                "Download the app here: https://play.google.com/store/apps/details?id=com.culturequest.culturequest");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "Share your scan using"));
    }



}