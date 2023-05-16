package ch.epfl.culturequest;

import static ch.epfl.culturequest.utils.AndroidUtils.hasConnection;
import static ch.epfl.culturequest.utils.AndroidUtils.showNoConnectionAlert;
import static ch.epfl.culturequest.utils.ProfileUtils.INCORRECT_USERNAME_FORMAT;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.squareup.picasso.Picasso;

import java.io.IOException;

import ch.epfl.culturequest.authentication.Authenticator;
import ch.epfl.culturequest.backend.tournament.apis.TournamentManagerApi;
import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.databinding.ActivitySettingsBinding;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.storage.FireStorage;
import ch.epfl.culturequest.utils.AndroidUtils;
import ch.epfl.culturequest.utils.CustomSnackbar;
import ch.epfl.culturequest.utils.EspressoIdlingResource;
import ch.epfl.culturequest.utils.ProfileUtils;


/**
 * Activity that allows the user to change his profile picture and username
 */
public class SettingsActivity extends AppCompatActivity {

    private ImageView profilePictureView;
    private String profilePicUri;
    private Bitmap profilePicBitmap;
    private Profile activeProfile;

    private TextView username;


    private final ActivityResultLauncher<Intent> profilePictureSelector = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::displayProfilePic);
    private final ActivityResultLauncher<String> requestPermissionLauncher = this.registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) openGallery();
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        AndroidUtils.removeStatusBar(getWindow());
        ch.epfl.culturequest.databinding.ActivitySettingsBinding binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //handle logout
        Button logoutButton = binding.logOut;
        logoutButton.setOnClickListener(v -> {
            Context context = v.getContext();
            if (hasConnection(context)) Authenticator.signOut(this);
            else {
                View rootView = v.getRootView();
                CustomSnackbar.showCustomSnackbar("Cannot log out. You are not connected to the internet", R.drawable.unknown_error, rootView);
            }
        });

        activeProfile = Profile.getActiveProfile();

        // if the user is not logged in, we can't display the settings so we finish the activity
        if (activeProfile == null) {
            finish();
            return;
        }

        username = binding.username;
        username.setText(activeProfile.getUsername());

        // load the profile picture
        profilePictureView = binding.profilePicture;
        Picasso.get().load(activeProfile.getProfilePicture()).into(profilePictureView);
        profilePicUri = activeProfile.getProfilePicture();

        // handle the update profile button
        Button updateProfileButton = binding.updateProfile;
        updateProfileButton.setOnClickListener(this::UpdateProfile);
    }


    private void UpdateProfile(View v) {
        EspressoIdlingResource.increment();

        // Check if the username is valid
        if (!ProfileUtils.isValid(activeProfile, username.getText().toString())) {
            username.setText("");
            username.setHint(INCORRECT_USERNAME_FORMAT);
            EspressoIdlingResource.decrement();
            return;
        }


        // if the profile picture has not been changed, we don't need to upload it again
        if (profilePicUri.equals(activeProfile.getProfilePicture())) {
            Database.setProfile(activeProfile);
            Profile.setActiveProfile(activeProfile);
            finish();
            EspressoIdlingResource.decrement();
            return;
        }

        if (!hasConnection(this)) {
            showNoConnectionAlert(this, "You have no internet connection. Your profile will be updated once you connect.");
        }
            FireStorage.uploadNewProfilePictureToStorage(activeProfile, profilePicBitmap,true).whenComplete(
                    (profile, throwable) -> {
                        if (throwable != null) {
                            throwable.printStackTrace();
                        } else {
                            Database.setProfile(profile);
                            Profile.setActiveProfile(profile);
                        }

                        finish();
                        EspressoIdlingResource.decrement();
                    }
            );

        }
        // Upload the new profile picture and update the profile



    /**
     * Displays the profile picture selected by the user
     *
     * @param result the result of the activity launched to select the profile picture
     */
    private void displayProfilePic(ActivityResult result) {
        if (result.getResultCode() != RESULT_OK) return;

        Intent data = result.getData();
        if (data == null) return;

        Uri selectedImage = data.getData();
        if (selectedImage == null) return;

        Picasso.get().load(selectedImage).into(profilePictureView);
        profilePicUri = selectedImage.toString();
        try {
            profilePicBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
        } catch (IOException e) {
            profilePicBitmap = FireStorage.getBitmapFromURL(ProfileUtils.DEFAULT_PROFILE_PIC_PATH);
        }
    }


    /**
     * Opens the gallery to select a profile picture
     *
     * @param view the view that was clicked
     */
    public void selectProfilePicture(View view) {
        if (ContextCompat.checkSelfPermission(this, ProfileUtils.GALLERY_PERMISSION) == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else {
            requestPermissionLauncher.launch(ProfileUtils.GALLERY_PERMISSION);
        }
    }

    private void openGallery() {
        profilePictureSelector.launch(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI));
    }

    @Override
    protected void onResume() {
        super.onResume();
        TournamentManagerApi.handleTournaments();
    }


}