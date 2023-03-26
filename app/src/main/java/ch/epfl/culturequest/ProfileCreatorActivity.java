package ch.epfl.culturequest;

import static ch.epfl.culturequest.utils.ProfileUtils.INCORRECT_USERNAME_FORMAT;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.utils.ProfileUtils;
import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileCreatorActivity extends AppCompatActivity {



    private String profilePicUri;


    private final Profile profile = new Profile(null, "");
    private final ActivityResultLauncher<Intent> profilePictureSelector = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), this::displayProfilePic);
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            this.registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                    isGranted -> {
                        if (isGranted) openGallery();
                    });
    private ImageView profileView;
    private Drawable initialDrawable;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        if (ContextCompat.checkSelfPermission(this, ProfileUtils.GALLERY_PERMISSION) == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else {
            requestPermissionLauncher.launch(ProfileUtils.GALLERY_PERMISSION);
        }
    }

    /**
     * Function called when user clicks on the buttont "Create my Account"
     * First checks if username is valid and if user has selected a profile pic,
     * then registers the Profile in the Database and redirects to the Navigation Intent
     *
     * @param view
     */
    public void createProfile(View view) {
        EditText textView = findViewById(R.id.username);
        String username = textView.getText().toString();

        if (!ProfileUtils.isValid(profile,username)) {
            textView.setText("");
            textView.setHint(INCORRECT_USERNAME_FORMAT);
            return;
        }

        setDefaultPicIfNoneSelected();

        if (!Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).isAnonymous()) {
            profile.setUsername(username);

            if ((profilePicUri.equals(ProfileUtils.DEFAULT_PROFILE_PATH)))
                storeProfileInDatabase(ProfileUtils.DEFAULT_PROFILE_PATH);
             else
                storeImageAndProfileInDatabase();
        } else{
            profile.setUsername(username);
            profile.setProfilePicture(profilePicUri);
            Profile.setActiveProfile(profile);
        }

        Intent successfulProfileCreation = new Intent(this, NavigationActivity.class);
        startActivity(successfulProfileCreation);
    }



    private void storeProfileInDatabase(String path) {
        profile.setProfilePicture(path);
        Profile.setActiveProfile(profile);
        Database.setProfile(profile);
    }

    private void storeImageAndProfileInDatabase() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        UploadTask task = storage.getReference().child("profilePictures").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).putFile(Uri.parse(profilePicUri));
        task.addOnFailureListener(e -> storeProfileInDatabase(ProfileUtils.DEFAULT_PROFILE_PATH))
                .addOnSuccessListener(taskSnapshot -> storage.getReference().child("profilePictures").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getDownloadUrl().addOnFailureListener(e -> storeProfileInDatabase(ProfileUtils.DEFAULT_PROFILE_PATH)).addOnSuccessListener(uri -> storeProfileInDatabase(uri.toString())));
    }


    private void openGallery() {
        profilePictureSelector.launch(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI));
    }



    private void setDefaultPicIfNoneSelected() {
        if (profileView.getDrawable().equals(initialDrawable)) {
            profilePicUri = ProfileUtils.DEFAULT_PROFILE_PATH;
        }
    }


    private void displayProfilePic(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
            Uri profilePicture = result.getData().getData();
            CircleImageView image = findViewById(R.id.profile_picture);
            Picasso.get().load(profilePicture).into(image);
            ((TextView) findViewById(R.id.profile_pic_text)).setText("");
            profilePicUri = profilePicture.toString();

        }
    }







    public Profile getProfile() {
        return profile;
    }

    public String getProfilePicUri() {
        return profilePicUri;
    }

}
