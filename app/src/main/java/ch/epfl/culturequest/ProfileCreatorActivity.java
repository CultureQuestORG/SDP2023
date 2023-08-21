package ch.epfl.culturequest;

import static ch.epfl.culturequest.utils.ProfileUtils.setProblemHintTextIfAny;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import ch.epfl.culturequest.authentication.Authenticator;
import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.notifications.FireMessaging;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.storage.FireStorage;
import ch.epfl.culturequest.storage.ImageFetcher;
import ch.epfl.culturequest.utils.AndroidUtils;
import ch.epfl.culturequest.utils.CropUtils;
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

    private TextView textView;
    private final Profile profile = new Profile(null, "");
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
        textView = findViewById(R.id.username);
        initialDrawable = profileView.getDrawable();
        Button button = findViewById(R.id.update_profile);
        EditText city = findViewById(R.id.city);

        textView.addTextChangedListener(onInputChange(button, (s) -> s.toString().length() > 0 && city.getText().toString().length() > 0));
        city.addTextChangedListener(onInputChange(button, (s) -> s.toString().length() > 0 && textView.getText().toString().length() > 0));
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
        //check if username is valid
        if (setProblemHintTextIfAny(textView)) return;
        String username = textView.getText().toString();
        setDefaultPicIfNoneSelected();

        profile.setUsername(username);
        profile.setUid(Authenticator.getCurrentUser().getUid());
        profile.setCity(((EditText) findViewById(R.id.city)).getText().toString());

        // Get first the device token, then store the profile in the database if it is not anonymous
        FireMessaging.getDeviceToken().whenComplete((token, ex) -> {
            if (ex == null && token != null) {
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
        // start the gallery activity to select a picture with result code TAKE_PICTURE
        startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), CropUtils.TAKE_PICTURE);
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
    private Void displayProfilePic(Uri result) {
        CircleImageView image = findViewById(R.id.profile_picture);
        ImageFetcher.fetchImage(this, result.toString(), image);
        profilePicUri = result.toString();
        try {
            profilePicBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), result);
        } catch (IOException e) {
            profilePicBitmap = FireStorage.getBitmapFromURL(ProfileUtils.DEFAULT_PROFILE_PIC_PATH);
        }
        return null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CropUtils.manageCropFlow(requestCode, resultCode, data, this, this::displayProfilePic, profileView, (v) -> {});
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

    private TextWatcher onInputChange(Button button, Predicate<Editable> isValid) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                button.setEnabled(isValid.test(s));
            }
        };
    }
}