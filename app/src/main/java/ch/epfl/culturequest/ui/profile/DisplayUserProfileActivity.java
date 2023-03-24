package ch.epfl.culturequest.ui.profile;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import ch.epfl.culturequest.databinding.FragmentProfileBinding;
import ch.epfl.culturequest.social.PictureAdapter;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.ui.SearchUserActivity;
import de.hdodenhof.circleimageview.CircleImageView;

public class DisplayUserProfileActivity extends AppCompatActivity {
    private FragmentProfileBinding binding;
    private PictureAdapter pictureAdapter;

    private Profile selectedProfile = SearchUserActivity.SELECTED_USER;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ProfileViewModelFactory factory = new ProfileViewModelFactory(selectedProfile);
        ProfileViewModel profileViewModel =
                new ViewModelProvider(this, factory).get(ProfileViewModel.class);
        binding = FragmentProfileBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        final TextView textView = binding.profileName;
        final CircleImageView profilePicture = binding.profilePicture;
        final RecyclerView pictureGrid = binding.pictureGrid;

        profileViewModel.getName().observe(this, textView::setText);
        profileViewModel.getProfilePictureUri().observe(this, uri -> Picasso.get().load(uri).into(profilePicture));
        profileViewModel.getPictures().observe(this, images -> {
            pictureAdapter = new PictureAdapter(images);
            pictureGrid.setAdapter(pictureAdapter);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
            pictureGrid.setLayoutManager(gridLayoutManager);
        });
        setContentView(root);
    }
}
