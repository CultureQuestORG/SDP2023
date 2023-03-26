package ch.epfl.culturequest;

import static ch.epfl.culturequest.utils.ProfileUtils.INCORRECT_USERNAME_FORMAT;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import ch.epfl.culturequest.authentication.Authenticator;
import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.databinding.ActivitySettingsBinding;
import ch.epfl.culturequest.social.Image;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.utils.ProfileUtils;

public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;
    private final String GALLERY_PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE;
    private ImageView profilePictureView;
    private String profilePicUri;

    private Profile activeProfile;

    private TextView username;


    private final ActivityResultLauncher<Intent> profilePictureSelector = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), this::displayProfilePic);
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            this.registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                    isGranted -> {
                        if (isGranted) openGallery();
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Button logoutButton = binding.logOut;
        logoutButton.setOnClickListener(v -> new Authenticator(this, false).signOut());

        activeProfile = Profile.getActiveProfile();

        username = binding.username;
        username.setText(activeProfile.getUsername());

        profilePictureView = binding.profilePicture;
        Picasso.get().load(activeProfile.getProfilePicture()).into(profilePictureView);


        profilePicUri = activeProfile.getProfilePicture();

        Button updateProfileButton = binding.updateProfile;
        updateProfileButton.setOnClickListener(this::UpdateProfile);
    }


    private void UpdateProfile(View v) {
        if (!ProfileUtils.isValid(activeProfile, username.getText().toString())) {
            username.setText("");
            username.setHint(INCORRECT_USERNAME_FORMAT);
            return;
        }


        if (profilePicUri.equals(activeProfile.getProfilePicture())) {
            Database.setProfile(activeProfile);
            finish();
            return;
        }

        FirebaseStorage storage = FirebaseStorage.getInstance();
        UploadTask task = storage.getReference().child("profilePictures").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).putFile(Uri.parse(profilePicUri));
        task.addOnSuccessListener(taskSnapshot -> {
                    storage.getReference().child("profilePictures").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getDownloadUrl().
                            addOnSuccessListener(uri -> {
                                activeProfile.setProfilePicture(uri.toString());
                                Database.setProfile(activeProfile);
                                finish();
                            });
                });


    }


    private void displayProfilePic(ActivityResult result) {
        if (result.getResultCode() != RESULT_OK)
            return;

        Intent data = result.getData();
        if (data == null)
            return;

        Uri selectedImage = data.getData();
        if (selectedImage == null)
            return;

        Picasso.get().load(selectedImage).into(profilePictureView);
        profilePicUri = selectedImage.toString();
    }


    public void selectProfilePicture(View view) {
        if (ContextCompat.checkSelfPermission(this, GALLERY_PERMISSION) == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else {
            requestPermissionLauncher.launch(GALLERY_PERMISSION);
        }
    }

    private void openGallery() {
        profilePictureSelector.launch(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI));
    }


}