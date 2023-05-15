package ch.epfl.culturequest;

import static ch.epfl.culturequest.utils.ProfileUtils.INCORRECT_USERNAME_FORMAT;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ch.epfl.culturequest.authentication.Authenticator;
import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.notifications.FireMessaging;
import ch.epfl.culturequest.storage.FireStorage;
import ch.epfl.culturequest.utils.AndroidUtils;
import ch.epfl.culturequest.utils.PermissionRequest;
import ch.epfl.culturequest.utils.ProfileUtils;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * This activity is used to allow the user to create a profile by
 * selecting a profile picture and a username
 */
public class ProfileCreatorActivity extends AppCompatActivity {
    private String profilePicUri;

    private Bitmap profilePicBitmap;
    private final Profile profile = new Profile(null, "");
    private final ActivityResultLauncher<Intent> profilePictureSelector = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), this::displayProfilePic);
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            this.registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                    isGranted -> {
                        if (isGranted) openGallery();
                    });

    private final PermissionRequest permissionRequest = new PermissionRequest(ProfileUtils.GALLERY_PERMISSION);
    private ImageView profileView;
    private Drawable initialDrawable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidUtils.removeStatusBar(getWindow());
        setContentView(R.layout.activity_profile_creation);
        //the following attributes are used to check whether the user actually selected a profile pic
        profileView = findViewById(R.id.profile_picture);
        initialDrawable = profileView.getDrawable();
    }

    @Override
    public void onBackPressed() {
        //do nothing!! We don't want the user to go back to the sign in page
    }

    /**
     * Called when clicking on the add profile pic icon. Basically asks for permissions
     * to read external storage, then opens the gallery for the user to select a profile pic
     *
     * @param view
     */
    public void selectProfilePicture(View view) {
        if (permissionRequest.hasPermission(this)) {
            openGallery();
        } else {
            permissionRequest.askPermission(requestPermissionLauncher);
        }
    }

    /**
     * Function called when user clicks on the buttont "Create my Account"
     * First checks if username is valid and if user has selected a profile pic,
     * then registers the Profile in the Database and redirects to the Navigation Intent
     * TODO need to store the profile in the Database
     *
     * @param view
     */
    public void createProfile(View view) {
        EditText textView = findViewById(R.id.username);
        String username = textView.getText().toString();

        //check if username is valid
        if (!ProfileUtils.isValid(profile, username)) {
            textView.setText("");
            textView.setHint(INCORRECT_USERNAME_FORMAT);
            return;
        }

        setDefaultPicIfNoneSelected();

        profile.setUsername(username);
        profile.setUid(Authenticator.getCurrentUser().getUid());

        // Get first the device token, then store the profile in the database if it is not anonymous
        FireMessaging.getDeviceToken().whenComplete((token, ex) -> {
            if (ex == null) {
                List<String> deviceTokens = new ArrayList<>();
                deviceTokens.add(token);
                profile.setDeviceTokens(deviceTokens);
            }

            //if user is anonymous, we don't want to store the profile in the database
            if (!Authenticator.getCurrentUser().isAnonymous()) {

                //if the profile picture is not the default one, we store it in the storage
                if (!profilePicUri.equals(ProfileUtils.DEFAULT_PROFILE_PIC_PATH))
                    FireStorage.uploadNewProfilePictureToStorage(profile, profilePicBitmap, true).whenComplete(
                            (profile, throwable) -> {
                                if (throwable != null) {
                                    throwable.printStackTrace();
                                } else {
                                    Database.setProfile(profile);
                                    Profile.setActiveProfile(profile);
                                }
                            }
                    );
                    // if the profile picture is the default one, we don't need to store it in the storage
                else {
                    Database.setProfile(profile);
                    Profile.setActiveProfile(profile);
                }

            } else {
                //if user is anonymous, we don't want to store the profile in the database
                Profile.setActiveProfile(profile);
            }

            Intent successfulProfileCreation = new Intent(this, NavigationActivity.class);
            startActivity(successfulProfileCreation);

        });
    }

    private void openGallery() {
        profilePictureSelector.launch(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI));
    }


    private void setDefaultPicIfNoneSelected() {
        if (profileView.getDrawable().equals(initialDrawable)) {
            profilePicUri = ProfileUtils.DEFAULT_PROFILE_PIC_PATH;
            profile.setProfilePicture(profilePicUri);
        }
    }


    /**
     * Displays the profile picture selected by the user
     *
     * @param result the result of the activity launched to select the profile picture
     */
    public void displayProfilePic(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
            Uri profilePicture = result.getData().getData();
            CircleImageView image = findViewById(R.id.profile_picture);
            Picasso.get().load(profilePicture).into(image);
            ((TextView) findViewById(R.id.profile_pic_text)).setText("");
            profilePicUri = profilePicture.toString();
            try {
                profilePicBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), profilePicture);
            } catch (IOException e) {
                profilePicBitmap = FireStorage.getBitmapFromURL(ProfileUtils.DEFAULT_PROFILE_PIC_PATH);
            }
        }
    }


    /**
     * Getter for the profile being created
     *
     * @return the profile being created
     */
    public Profile getProfile() {
        return profile;
    }

    public String getProfilePicUri() {
        return profilePicUri;
    }

}
