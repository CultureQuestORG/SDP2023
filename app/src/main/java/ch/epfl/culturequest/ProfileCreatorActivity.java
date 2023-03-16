package ch.epfl.culturequest;

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
import com.squareup.picasso.Picasso;

import java.util.Objects;

import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.social.Profile;
import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileCreatorActivity extends AppCompatActivity {
    public static String INCORRECT_USERNAME_FORMAT = "Incorrect Username Format";
    public static String USERNAME_REGEX = "^[a-zA-Z0-9_-]+$";
    public static String DEFAULT_PROFILE_PATH = "res/drawable/profile_icon_selector.xml";

    private final String GALLERY_PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE;
    private final Profile profile = new Profile(null, null);
    private final ActivityResultLauncher<Intent> profilePictureSelector = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), this::displayProfilePic);
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            this.registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                    isGranted -> {
                        if (isGranted) openGallery();
                    });
    private ImageView profileView;
    private Drawable initialDrawable;
    private Database db = new Database();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_creation);
        //the following attributes are used to check whether the user actually selected a profile pic
        profileView = findViewById(R.id.profile_picture);
        initialDrawable = profileView.getDrawable();
    }

    @Override
    public void onBackPressed(){
        //do nothing!! We don't want the user to go back to the sign in page
    }

    /**
     * Called when clicking on the add profile pic icon. Basically asks for permissions
     * to read external storage, then opens the gallery for the user to select a profile pic
     * @param view
     */
    public void selectProfilePicture(View view) {
        if (ContextCompat.checkSelfPermission(this, GALLERY_PERMISSION) == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else {
            requestPermissionLauncher.launch(GALLERY_PERMISSION);
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
        if (usernameIsValid(username)) {
            profile.setUsername(username);
            //checks if user actually selected profile pic,
            //if they dont, we set a default profile pic
            if (profileView.getDrawable().equals(initialDrawable)) {
                profile.setProfilePicture(DEFAULT_PROFILE_PATH);
            }

            if (!Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).isAnonymous()){
                db.setProfile(profile);
            }
            Intent successfulProfileCreation = new Intent(this, NavigationActivity.class);
            startActivity(successfulProfileCreation);
        } else {
            textView.setText("");
            textView.setHint(INCORRECT_USERNAME_FORMAT);
        }
    }

    private void openGallery() {
        profilePictureSelector.launch(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI));
    }

    private void displayProfilePic(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
            Uri profilePicture = result.getData().getData();
            CircleImageView image = findViewById(R.id.profile_picture);
            Picasso.get().load(profilePicture).into(image);
            ((TextView)findViewById(R.id.profile_pic_text)).setText("");
            profile.setProfilePicture(profilePicture.getPath());
        }
    }

    private boolean usernameIsValid(String username) {
        int length = username.length();
        return !username.isEmpty()
                && length > 3
                && length < 20
                && username.matches(USERNAME_REGEX)
                && !username.contains(" ");
    }

    //used for testing purposes


    public Profile getProfile() {
        return profile;
    }

}
