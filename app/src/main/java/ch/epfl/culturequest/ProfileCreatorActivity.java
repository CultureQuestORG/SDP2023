package ch.epfl.culturequest;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.social.Profile;


public class ProfileCreatorActivity extends ComponentActivity {
    private Profile profile;
    private final ActivityResultLauncher<Intent> profilePictureSelector = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), this::setProfilePicture);



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_creation);
        profile = new Profile(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()), null);
        ((TextView) findViewById(R.id.email_text)).setText(profile.getEmail());
        ((TextView) findViewById(R.id.first_name_text)).setText(profile.getFirstName());
        ((TextView) findViewById(R.id.last_name_text)).setText(profile.getLastName());
    }


    public void createProfile(View view) {
        Database db = new Database();
        //need albert here
        //db.set(profile.getUid(), profile);
        System.out.println("created!!");
    }

    public void selectProfilePicture(View view) {
        //Weird: doesnt request permission
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        profilePictureSelector.launch(intent);
    }

    private void setProfilePicture(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
            Uri profilePicture = result.getData().getData();
            Picasso.get().load(profilePicture).into((ImageView) findViewById(R.id.profile_picture));
            profile.updateProfilePicture(profilePicture);
        }
    }
}
